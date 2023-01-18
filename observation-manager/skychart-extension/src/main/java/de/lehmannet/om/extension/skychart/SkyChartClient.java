/* ====================================================================
 * /extension/skychart/SkyChartClient.java
 *
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.extension.skychart;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import de.lehmannet.om.Angle;
import de.lehmannet.om.EquPosition;
import de.lehmannet.om.IFinding;
import de.lehmannet.om.IImager;
import de.lehmannet.om.IObservation;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ISession;
import de.lehmannet.om.ISite;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.ui.catalog.ICatalog;
import de.lehmannet.om.ui.dialog.IImagerDialog;
import de.lehmannet.om.ui.dialog.ITargetDialog;
import de.lehmannet.om.SchemaOalTypeInfo;
import de.lehmannet.om.ui.extension.IExtension;
import de.lehmannet.om.ui.extension.IExtensionContext;
import de.lehmannet.om.ui.extension.PopupMenuExtension;
import de.lehmannet.om.ui.navigation.IObservationManagerJFrame;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.ui.preferences.PreferencesPanel;
import de.lehmannet.om.ui.util.Worker;
import de.lehmannet.om.util.SchemaElementConstants;

public class SkyChartClient implements IExtension, ActionListener {

    private static final String NAME = "Starchart Remote Control";
    private static final String VERSION = "0.9.2";
    private static URL UPDATE_URL = null;
    static {
        try {
            SkyChartClient.UPDATE_URL = new URL("http://observation.sourceforge.net/extension/skychart/update");
        } catch (MalformedURLException m_url) {
            // Do nothing
        }
    }

    private ResourceBundle bundle;

    private static final Logger LOGGER = LoggerFactory.getLogger(SkyChartClient.class);
    private IObservationManagerJFrame om = null;
    private JMenu mainMenu = null;
    private JMenuItem mainMoveTo = null;

    private JMenu popupMenu = null;
    private JMenuItem popupMoveTo = null;
    private IExtensionContext context;
    private final Set<SchemaOalTypeInfo> extensionTypes = new HashSet<>();

    public SkyChartClient(IExtensionContext context) {

        this.context = context;
        this.initLanguage();
        this.initMenus();

    }

    @Override
    public Set<SchemaOalTypeInfo> getExtensionTypes() {

        return Collections.unmodifiableSet(this.extensionTypes);
    }

    private void initLanguage() {
        try {
            this.bundle = ResourceBundle.getBundle("de.lehmannet.om.extension.skychart.Skychart", Locale.getDefault());
        } catch (MissingResourceException mre) {

            this.bundle = ResourceBundle.getBundle("de.lehmannet.om.extension.skychart.Skychart", Locale.ENGLISH);
        }
    }

    // --------------
    // ActionListener ---------------------------------------------------------
    // --------------

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() instanceof JMenuItem) { // Should always be the case
            JMenuItem source = (JMenuItem) e.getSource();
            if ((source.equals(this.mainMoveTo)) || (source.equals(this.popupMoveTo))) {
                ISchemaElement se = this.context.getModel().getSelectedElement();
                if (se instanceof IObservation) {
                    this.moveSkychart((IObservation) se);
                } else if (se instanceof ITarget) {
                    this.moveSkychart((ITarget) se);
                } else {
                    this.context.getUserInterfaceHelper()
                            .showWarning(this.bundle.getString("skychart.move.wrongSchemaElementType"));
                }
            }
        }

    }

    private void moveSkychart(ITarget target) {

        StarchartSocket socket = this.createSocket();
        if (socket == null) {
            return;
        }

        boolean result = this.moveSkychartToTarget(socket, target);

        if (result) {
            // ---- Refresh chart
            String command = this.createREFRESHCommand();
            Boolean response = this.sendData(socket, command);
            if (response == null) {
                return; // Something went wrong. User is already informed, so
                        // cancel here.
            } else if (response) {
                // Inform user
                this.context.getUserInterfaceHelper()
                        .showInfo(this.bundle.getString("skychart.move.ok") + target.getDisplayName());
            } else {
                // Inform user
                this.context.getUserInterfaceHelper().showInfo(this.bundle.getString("skychart.move.failed"));
            }
        } else {
            // Inform user
            this.context.getUserInterfaceHelper().showInfo(this.bundle.getString("skychart.move.failed"));
        }

        // Close socket
        try {
            socket.close();
        } catch (IOException ioe) {
            // Can't do much here.
            System.err.println("Unable to close socket to Skychart application.\n" + ioe);
        }

    }

    private void moveSkychart(IObservation observation) {

        StarchartSocket socket = this.createSocket();
        if (socket == null) {
            return;
        }

        // Begin with data sending
        String[] commands = null;
        Boolean response = null;

        // ---- Set observation site
        /*
         * if( observation.getSite() != null ) { commands = new String[1]; commands[0] =
         * this.createSiteCommand(observation.getSite()); response = this.sendData(socket, commands[0]); if( response ==
         * null ) { return; // Something went wrong. User is already informed, so cancel here. } }
         */

        // ---- Set observation date
        if (observation.getBegin() != null) {
            commands = new String[2];
            commands[0] = this.createDateCommand(observation.getBegin().toZonedDateTime());
            commands[1] = this.createREFRESHCommand();
            for (String command : commands) {
                response = this.sendData(socket, command);
                if (response == null) {
                    return; // Something went wrong. User is already informed,
                    // so cancel here.
                }
            }
        }

        // ---- Set target site
        boolean result = this.moveSkychartToTarget(socket, observation.getTarget());

        if (result) {
            // ---- Refresh chart
            commands = new String[1];
            commands[0] = this.createREFRESHCommand();
            response = this.sendData(socket, commands[0]);
            if (response == null) {
                return; // Something went wrong. User is already informed, so
                        // cancel here.
            } else if (response) {
                // Inform user
                this.context.getUserInterfaceHelper()
                        .showInfo(this.bundle.getString("skychart.move.ok") + observation.getTarget().getDisplayName());
            } else {
                // Inform user
                this.context.getUserInterfaceHelper().showInfo(this.bundle.getString("skychart.move.failed"));
            }
        } else {
            // Inform user
            this.context.getUserInterfaceHelper().showInfo(this.bundle.getString("skychart.move.failed"));
        }

        // Close socket
        try {
            socket.close();
        } catch (IOException ioe) {
            // Can't do much here.
            System.err.println("Unable to close socket to Skychart application.\n" + ioe);
        }

    }

    private boolean moveSkychartToTarget(StarchartSocket socket, ITarget target) {

        // Begin with data sending
        String[] commands = null;
        Boolean response = null;

        // ---- Set Target (via Skychart search)
        commands = this.createSEARCHCommands(target);
        String responseString = null;
        for (String s : commands) {
            responseString = this.sendDataWithServerResponse(socket, s);
            if (responseString == null) { // Communication error
                System.err.println("Set Target by position...Failed with error");
                return false;
            } else {

                // This is somehow new from Skychart.
                // In older versions it returned the moved to coordinated. Now
                // it just responds OK! or not Found.
                if (responseString.contains("OK!")) { // Direct hit! :-)
                    return true;
                } else if (responseString.indexOf("Not found!") != 1) {
                    continue;
                } else if ("".equals(responseString.replace('.', ' ').trim())) { // Only
                    // ....
                    // as
                    // response
                    // can
                    // also
                    // be
                    // considered
                    // as
                    // OK...whatever
                    // (test
                    // with
                    // moon
                    // as
                    // target)
                    return true;
                }

                StringTokenizer tokenizer = new StringTokenizer(responseString, "\t");
                int j = 0;
                String current = null;
                String ra = null;
                String dec = null;
                String objectName = null;
                while (tokenizer.hasMoreTokens()) {
                    current = tokenizer.nextToken();
                    if (j == 2) {
                        ra = current;
                        dec = tokenizer.nextToken();
                        char d = 176;
                        dec = dec.replaceAll("" + d, EquPosition.DEC_DEG);
                        if (dec.contains(".")) {
                            dec = dec.substring(0, dec.lastIndexOf("."));
                        } else {
                            if (dec.lastIndexOf("s") != -1) {
                                dec = dec.substring(0, dec.lastIndexOf("s"));
                            }
                        }
                        dec = dec + "\"";
                        tokenizer.nextToken(); // This is the type returned by
                        // Skycharts
                        objectName = tokenizer.nextToken();
                        break;
                    }
                    j++;
                }

                // Check whether found object and target object have same name
                if (objectName != null) {

                    objectName = objectName.replaceAll(" ", "");
                    objectName = objectName.trim().toUpperCase();
                    if (objectName.equals(target.getName().toUpperCase())) {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Found " + target.getName() + " by name");
                        }
                        return true; // Names match. We found our object
                    }
                    String[] aliasNames = target.getAliasNames();
                    if ((aliasNames != null) && (aliasNames.length > 0)) {
                        for (String aliasName : aliasNames) {
                            if (objectName.equals(aliasName.toUpperCase())) {
                                if (LOGGER.isDebugEnabled()) {
                                    System.out.println("Found " + target.getName() + " by aliasname " + aliasName);
                                }
                                return true; // Names match. We found our object
                            }
                        }
                    }
                }

                // Check whether found object and target are max. 0.5 degree
                // away from each other
                // which we consider as a good hit.
                if (ra != null) {
                    EquPosition ep = null;
                    try {
                        ep = new EquPosition(ra, dec);
                    } catch (IllegalArgumentException iae) { // RA, DEC string
                        // my be
                        // malformed
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("RA or DEC string is malformed: RA: " + ra + "\tDEC: " + dec);
                        }
                        continue;
                    }
                    EquPosition targetEp = target.getPosition();

                    if (targetEp == null) {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Cannot find " + target.getName() + " as target position is NULL");
                        }
                        continue;
                    }

                    // Check whether RA differs only max 0.5 degree
                    Angle epRa = ep.getRaAngle();
                    double epRaDegree = epRa.toDegree();
                    Angle targetEpRa = targetEp.getRaAngle();
                    double targetEpRaDegree = targetEpRa.toDegree();

                    double raDiff = Math.abs(epRaDegree - targetEpRaDegree);
                    if (raDiff > 0.5) {
                        if (LOGGER.isDebugEnabled()) {
                            System.out
                                    .println("Found wrong " + target.getName() + " as RA differs more than 0.5 degree");
                        }
                        continue; // This is most propably not the object we'Re
                        // searching
                    }

                    // Check whether DEC differs only max 0.5 degree
                    Angle epDec = ep.getDecAngle();
                    double epDecDegree = epDec.toDegree();
                    Angle targetEpDec = targetEp.getDecAngle();
                    double targetEpDecDegree = targetEpDec.toDegree();

                    double decDiff = Math.abs(epDecDegree - targetEpDecDegree);
                    if (decDiff > 0.5) {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Found wrong " + target.getName() + " as DEC differs more than 0.5 degree");
                        }
                        continue; // This is most propably not the object we're
                        // searching
                    }

                    // If we come here the found object is max 0.5 degree (in RA
                    // or DEC) away from our target, so we stop searching
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Found " + target.getName() + " as RA and DEC do not differ more than 0.5 degree");
                    }
                    return true;
                }
            }
        }

        // ---- Set target (via position)
        commands = this.createEquPositionCommands(target.getPosition());
        if ((commands != null) && (commands.length > 0)) {
            for (String command : commands) {
                response = this.sendData(socket, command);
                if (response == null) { // Communication error
                    System.err.println("Set Target by position...Failed with error");
                    return false;
                } else {
                    return true;
                }
            }
        }

        return true;

    }

    private Boolean sendData(StarchartSocket socket, String command) {

        // Create input and output writers/readers
        try {
            String response = socket.send(command);

            if ((response != null) && ((response.startsWith(StarchartSocket.SERVER_RESPONSE_OK))
                    || (response.endsWith(StarchartSocket.SERVER_RESPONSE_OK)))) {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }

        } catch (IOException ioe) {
            om.createWarning(this.bundle.getString("skychart.communication.failed"));
            System.err.println("Unable to send data to Skychart application.\n" + ioe);
            return null; // Indicate something went wrong
        }

    }

    private String sendDataWithServerResponse(StarchartSocket socket, String command) {

        // Create input and output writers/readers
        try {

            return socket.send(command);

        } catch (IOException ioe) {
            om.createWarning(this.bundle.getString("skychart.communication.failed"));
            System.err.println("Unable to send data to Skychart application.\n" + ioe);
            return null; // Indicate something went wrong
        }

    }

    private StarchartSocket createSocket() {

        // Get IP
        String ip = this.context.getConfiguration().getConfig(SkyChartConfigKey.CONFIG_SERVER_IP_KEY);
        if ((ip == null) || ("".equals(ip.trim()))) {
            ip = SkyChartPreferences.SERVER_DEFAULT_IP;
        }

        // Get Port
        String s_port = this.context.getConfiguration().getConfig(SkyChartConfigKey.CONFIG_SERVER_PORT_KEY);
        int port = SkyChartPreferences.SERVER_DEFAULT_PORT;
        if ((s_port != null) && !("".equals(s_port.trim()))) {
            port = Integer.parseInt(s_port);
        }

        // Create Socket
        StarchartSocket socket = null;
        try {
            socket = new StarchartSocket(ip, port);
        } catch (UnknownHostException uhe) {
            om.createWarning(this.bundle.getString("skychart.communication.failed.host"));
            System.err.println("Unable to reach Skychart application. Host unknown.\n" + uhe);
        } catch (ConnectException ce) { // SkyChart is most probably not open.
                                        // So try to start it
            // Try to get application path
            // final String applicationPath =
            final String applicationPath = this.context.getConfiguration()
                    .getConfig(SkyChartConfigKey.CONFIG_APPLICATION_PATH);
            if (applicationPath == null || "".equals(applicationPath.trim())) { // No
                                                                                // application
                                                                                // path
                                                                                // specified
                om.createWarning(this.bundle.getString("skychart.application.start.nopath"));
                System.err.println(
                        "Unable to reach Skychart application and unable to start it as no application path is provided.\n"
                                + ce);
            } else { // Try to start it
                LOGGER.info("Unable to reach Skychart application. Try to launch it.");

                Worker startApplication = new Worker() {

                    private final Process p = null;

                    @Override
                    public void run() {
                        try {
                            Runtime rt = Runtime.getRuntime();
                            rt.exec(applicationPath + " --unique");
                            Thread.sleep(10 * 1000); // Wait max. 10 sec for
                                                     // startup

                        } catch (IOException ioe) {
                            om.createWarning(SkyChartClient.this.bundle.getString("skychart.application.start.failed"));
                            System.err
                                    .println("Unable to start Skychart application (" + applicationPath + ").\n" + ioe);
                        } catch (IllegalMonitorStateException isme) {
                            // Ignore. This comes from the p.wait() call, when
                            // skychart is already launched
                            System.err.println("Ingnoring \n " + isme);
                        } catch (Exception e) {
                            om.createWarning(SkyChartClient.this.bundle.getString("skychart.application.start.failed"));
                            System.err.println("Failed to start Skychart application (" + applicationPath + ").\n" + e);
                        }
                    }

                    @Override
                    public String getReturnMessage() {

                        return "";

                    }

                    @Override
                    public byte getReturnType() {

                        if (p != null) {
                            p.exitValue();
                        }
                        return Worker.RETURN_TYPE_ERROR;

                    }

                };

                this.context.getUserInterfaceHelper().createProgressDialog(
                        this.bundle.getString("skychart.application.start.title"),
                        this.bundle.getString("skychart.application.start.loading"), startApplication);

                try {
                    socket = new StarchartSocket(ip, port);
                } catch (IOException ioe) {
                    om.createWarning(this.bundle.getString("skychart.communication.failed"));
                    System.err.println("Unable to reach Skychart application.\n" + ioe);
                }

                return socket;

            }
        } catch (IOException ioe) {
            om.createWarning(this.bundle.getString("skychart.communication.failed"));
            System.err.println("Unable to reach Skychart application.\n" + ioe);
        }

        return socket;

    }

    // See Server Commands:
    // http://www.ap-i.net/skychart/en/documentation/server_commands
    private String[] createEquPositionCommands(EquPosition position) {

        if (position == null) {
            return null;
        }

        String[] result = new String[3];

        // I've no idea why we need this. But when Searching for an object
        // failed for CdC
        // it somehow locks the position. So we've to free it first by manually
        // move the chart a bit
        // test this with open cluster "STOCK 2" (where it finds an object STOCK
        // upon search command. Afterwards
        // the SETRA/SETDEC don't move the chart
        result[0] = "MOVEEAST";

        // Build RA string
        String ra = position.getRa();
        result[1] = "SETRA RA:" + ra;

        // Build DEC string
        String dec = position.getDec();
        char d = 176;
        dec = dec.replace(d, 'd');
        dec = dec.replace('\'', 'm');
        dec = dec.replace('\"', 's');
        result[2] = "SETDEC DEC:" + dec;

        return result;

    }

    // See Server Commands:
    // http://www.ap-i.net/skychart/en/documentation/server_commands
    private String[] createSEARCHCommands(ITarget target) {

        String[] commands;
        if ((target.getAliasNames() != null) && (target.getAliasNames().length > 0)) {
            commands = new String[target.getAliasNames().length + 2];
        } else {
            commands = new String[2];
        }

        commands[0] = "SEARCH \"" + target.getName().trim() + "\"";
        commands[1] = "SEARCH \"" + target.getDisplayName().trim() + "\"";

        // Search for alias names
        String[] aliasNames = target.getAliasNames();
        if ((aliasNames != null) && (aliasNames.length > 0)) {
            for (int i = 0; i < aliasNames.length; i++) {
                commands[i + 2] = "SEARCH \"" + aliasNames[i].trim() + "\"";
            }

        }

        return commands;

    }

    // See Server Commands:
    // http://www.ap-i.net/skychart/en/documentation/server_commands
    private String createSiteCommand(ISite site) {

        String alt = "ALT:" + site.getElevation() + "m";
        String obs = "OBS:" + site.getName();

        // Use EquPosition for transformation (set dummy angle for RA)
        EquPosition ep = new EquPosition(new Angle(0, Angle.DEGREE), site.getLatitude());
        String latArc = ep.getDec();
        char d = 176;
        latArc = latArc.replace(d, 'd');
        latArc = latArc.replace('\'', 'm');
        latArc = latArc.replace('\"', 's');
        String lat = "LAT:" + latArc;

        // Use EquPosition for transformation (set dummy angle for RA)
        ep = new EquPosition(new Angle(0, Angle.DEGREE), site.getLongitude());
        String lonArc = ep.getDec();
        lonArc = lonArc.replace(d, 'd');
        lonArc = lonArc.replace('\'', 'm');
        lonArc = lonArc.replace('\"', 's');
        String lon = "LON:" + lonArc;

        return "SETOBS " + lat + lon + alt + obs;

    }

    // See Server Commands:
    // http://www.ap-i.net/skychart/en/documentation/server_commands
    private String createREFRESHCommand() {

        return "REDRAW";

    }

    private String createDateCommand(ZonedDateTime date) {

        String dateString = date.toString();

        return "SETDATE " + dateString.substring(0, dateString.lastIndexOf("T") + 9);

    }

    private void initMenus() {

        // Main Menu (in Menubar)

        this.mainMenu = new JMenu(this.bundle.getString("skychart.menu.title"));

        this.mainMoveTo = new JMenuItem(this.bundle.getString("skychart.menu.moveSkychart"));
        this.mainMoveTo.setMnemonic('s');
        this.mainMoveTo.addActionListener(this);
        this.mainMenu.add(this.mainMoveTo);

        // Popup Menu

        this.popupMenu = new JMenu(this.bundle.getString("skychart.menu.title"));

        this.popupMoveTo = new JMenuItem(this.bundle.getString("skychart.menu.moveSkychart"));
        this.popupMoveTo.setMnemonic('s');
        this.popupMoveTo.addActionListener(this);
        this.popupMenu.add(this.popupMoveTo);

    }

    // ----------
    // IExtension -------------------------------------------------------------
    // ----------

    @Override
    public JMenu getMenu() {

        return this.mainMenu;

    }

    @Override
    public PopupMenuExtension getPopupMenu() {

        return new PopupMenuExtension(
                new SchemaElementConstants[] { SchemaElementConstants.OBSERVATION, SchemaElementConstants.TARGET },
                this.popupMenu);

    }

    @Override
    public String getName() {

        return SkyChartClient.NAME;

    }

    @Override
    public URL getUpdateInformationURL() {

        return SkyChartClient.UPDATE_URL;

    }

    @Override
    public PreferencesPanel getPreferencesPanel() {

        return new SkyChartPreferences(this.context.getConfiguration());

    }

    @Override
    public String getVersion() {

        return SkyChartClient.VERSION;

    }

    @Override
    public void reloadLanguage() {

        this.initLanguage();
        this.initMenus();

    }

    // --------------
    // Not applicable ---------------------------------------------------------
    // --------------

    @Override
    public Set<String> getAllSupportedXSITypes() {

        return Collections.emptySet();

    }

    @Override
    public ICatalog[] getCatalogs(File catalogDir) {

        return null;

    }

    @Override
    public String getDisplayNameForXSIType(String xsiType) {

        return null;

    }

    @Override
    public Set<String> getSupportedXSITypes(SchemaElementConstants schemaElementConstant) {

        return Collections.emptySet();

    }

    @Override
    public boolean isCreationAllowed(String xsiType) {

        return false;

    }

    @Override
    public boolean addOALExtensionElement(Element docElement) {

        return true;

    }

    @Override
    public String getPanelForXSIType(String xsiType, SchemaElementConstants schemaElementConstant) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDialogForXSIType(String xsiType, SchemaElementConstants schemaElementConstant) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AbstractPanel getFindingPanelForXSIType(String xsiType, IFinding finding, ISession session, ITarget target,
            boolean editable) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AbstractPanel getTargetPanelForXSIType(String xsiType, ITarget target, IObservation observation,
            boolean editable) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ITargetDialog getTargetDialogForXSIType(String xsiType, JFrame parent, ITarget target,
            IObservation observation, boolean editable) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean supports(String xsiType) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public IImagerDialog getImagerDialogForXSIType(String xsiType, JFrame parent, IImager imager, boolean editable) {
        // TODO Auto-generated method stub
        return null;
    }

}
