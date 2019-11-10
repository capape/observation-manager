/* ====================================================================
 * /extension/skychart/SkyChartClient.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.skychart;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.w3c.dom.Element;

import de.lehmannet.om.Angle;
import de.lehmannet.om.EquPosition;
import de.lehmannet.om.IObservation;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ISite;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.ui.catalog.ICatalog;
import de.lehmannet.om.ui.dialog.ProgressDialog;
import de.lehmannet.om.ui.extension.IExtension;
import de.lehmannet.om.ui.extension.PopupMenuExtension;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.preferences.PreferencesPanel;
import de.lehmannet.om.ui.util.Worker;
import de.lehmannet.om.util.DateConverter;
import de.lehmannet.om.util.SchemaElementConstants;

public class SkyChartClient implements IExtension, ActionListener {

    private static final String NAME = "Starchart Remote Control";
    private static final float VERSION = 0.91f;
    private static URL UPDATE_URL = null;
    static {
        try {
            SkyChartClient.UPDATE_URL = new URL("http://observation.sourceforge.net/extension/skychart/update");
        } catch (MalformedURLException m_url) {
            // Do nothing
        }
    }

    private PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
            .getBundle("de.lehmannet.om.ui.extension.skychart.Skychart", Locale.getDefault());

    private ObservationManager om = null;
    private JMenu mainMenu = null;
    private JMenuItem mainMoveTo = null;

    private JMenu popupMenu = null;
    private JMenuItem popupMoveTo = null;

    public SkyChartClient(ObservationManager om) {

        this.om = om;

        this.initMenus();

    }

    // --------------
    // ActionListener ---------------------------------------------------------
    // --------------

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() instanceof JMenuItem) { // Should always be the case
            JMenuItem source = (JMenuItem) e.getSource();
            if ((source.equals(this.mainMoveTo)) || (source.equals(this.popupMoveTo))) {
                ISchemaElement se = this.om.getSelectedTableElement();
                if (se instanceof IObservation) {
                    this.moveSkychart((IObservation) se);
                } else if (se instanceof ITarget) {
                    this.moveSkychart((ITarget) se);
                } else {
                    this.om.createWarning(this.bundle.getString("skychart.move.wrongSchemaElementType"));
                }
            }
        }

    }

    public void moveSkychart(ITarget target) {

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
            } else if (response.booleanValue()) {
                // Inform user
                this.om.createInfo(this.bundle.getString("skychart.move.ok") + target.getDisplayName());
            } else if (!response.booleanValue()) {
                // Inform user
                this.om.createInfo(this.bundle.getString("skychart.move.failed"));
            }
        } else {
            // Inform user
            this.om.createInfo(this.bundle.getString("skychart.move.failed"));
        }

        // Close socket
        try {
            socket.close();
        } catch (IOException ioe) {
            // Can't do much here.
            System.err.println("Unable to close socket to Skychart application.\n" + ioe);
        }

    }

    public void moveSkychart(IObservation observation) {

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
         * this.createSiteCommand(observation.getSite()); response =
         * this.sendData(socket, commands[0]); if( response == null ) { return; //
         * Something went wrong. User is already informed, so cancel here. } }
         */

        // ---- Set observation date
        if (observation.getBegin() != null) {
            commands = new String[2];
            commands[0] = this.createDateCommand(observation.getBegin());
            commands[1] = this.createREFRESHCommand();
            for (int i = 0; i < commands.length; i++) {
                response = this.sendData(socket, commands[i]);
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
            } else if (response.booleanValue()) {
                // Inform user
                this.om.createInfo(
                        this.bundle.getString("skychart.move.ok") + observation.getTarget().getDisplayName());
            } else if (!response.booleanValue()) {
                // Inform user
                this.om.createInfo(this.bundle.getString("skychart.move.failed"));
            }
        } else {
            // Inform user
            this.om.createInfo(this.bundle.getString("skychart.move.failed"));
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
        for (int i = 0; i < commands.length; i++) {
            responseString = this.sendDataWithServerResponse(socket, commands[i]);
            if (responseString == null) { // Communication error
                System.err.println("Set Target by position...Failed with error");
                return false;
            } else {

                // This is somehow new from Skychart.
                // In older versions it returned the moved to coordinated. Now
                // it just responds OK! or not Found.
                if (responseString.indexOf("OK!") != -1) { // Direct hit! :-)
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
                        if (dec.indexOf(".") != -1) {
                            dec = dec.substring(0, dec.lastIndexOf("."));
                        } else {
                            if (dec.lastIndexOf("s") != -1) {
                                dec = dec.substring(0, dec.lastIndexOf("s"));
                            }
                        }
                        dec = dec + "\"";
                        tokenizer.nextToken(); // This is the type returned by
                                               // Skycharts
                        objectName = new String(tokenizer.nextToken());
                        break;
                    }
                    j++;
                }

                // Check whether found object and target object have same name
                if (objectName != null) {

                    objectName = objectName.replaceAll(" ", "");
                    objectName = objectName.trim().toUpperCase();
                    if (objectName.equals(target.getName().toUpperCase())) {
                        if (this.om.isDebug()) {
                            System.out.println("Found " + target.getName() + " by name");
                        }
                        return true; // Names match. We found our object
                    }
                    String[] aliasNames = target.getAliasNames();
                    if ((aliasNames != null) && (aliasNames.length > 0)) {
                        for (int x = 0; x < aliasNames.length; x++) {
                            if (objectName.equals(aliasNames[x].toUpperCase())) {
                                if (this.om.isDebug()) {
                                    System.out.println("Found " + target.getName() + " by aliasname " + aliasNames[x]);
                                }
                                return true; // Names match. We found our object
                            }
                        }
                    }
                }

                // Check whether found object and target are max. 0.5 degree
                // away from each other
                // which we consider as a good hit.
                if ((ra != null) && (dec != null)) {
                    EquPosition ep = null;
                    try {
                        ep = new EquPosition(ra, dec);
                    } catch (IllegalArgumentException iae) { // RA, DEC string
                                                             // my be
                                                             // malformed
                        if (this.om.isDebug()) {
                            System.out.println("RA or DEC string is malformed: RA: " + ra + "\tDEC: " + dec);
                        }
                        continue;
                    }
                    EquPosition targetEp = target.getPosition();

                    if (targetEp == null) {
                        if (this.om.isDebug()) {
                            System.out.println("Cannot find " + target.getName() + " as target position is NULL");
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
                        if (this.om.isDebug()) {
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
                        if (this.om.isDebug()) {
                            System.out.println(
                                    "Found wrong " + target.getName() + " as DEC differs more than 0.5 degree");
                        }
                        continue; // This is most propably not the object we're
                                  // searching
                    }

                    // If we come here the found object is max 0.5 degree (in RA
                    // or DEC) away from our target, so we stop searching
                    if (this.om.isDebug()) {
                        System.out.println(
                                "Found " + target.getName() + " as RA and DEC do not differ more than 0.5 degree");
                    }
                    return true;
                }
            }
        }

        // ---- Set target (via position)
        commands = this.createEquPositionCommands(target.getPosition());
        if ((commands != null) && (commands.length > 0)) {
            for (int i = 0; i < commands.length; i++) {
                response = this.sendData(socket, commands[i]);
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
                return new Boolean(true);
            } else {
                return new Boolean(false);
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
            String response = socket.send(command);

            return response;

        } catch (IOException ioe) {
            om.createWarning(this.bundle.getString("skychart.communication.failed"));
            System.err.println("Unable to send data to Skychart application.\n" + ioe);
            return null; // Indicate something went wrong
        }

    }

    private StarchartSocket createSocket() {

        // Get IP
        String ip = this.om.getConfiguration().getConfig(SkyChartPreferences.CONFIG_SERVER_IP_KEY);
        if ((ip == null) || ("".equals(ip.trim()))) {
            ip = SkyChartPreferences.SERVER_DEFAULT_IP;
        }

        // Get Port
        String s_port = this.om.getConfiguration().getConfig(SkyChartPreferences.CONFIG_SERVER_PORT_KEY);
        int port = SkyChartPreferences.SERVER_DEFAULT_PORT;
        if ((s_port != null) && !("".equals(s_port.trim()))) {
            port = Integer.parseInt(s_port);
        }

        // Create Socket
        StarchartSocket socket = null;
        try {
            socket = new StarchartSocket(ip, port, this.om.isDebug());
        } catch (UnknownHostException uhe) {
            om.createWarning(this.bundle.getString("skychart.communication.failed.host"));
            System.err.println("Unable to reach Skychart application. Host unknown.\n" + uhe);
        } catch (ConnectException ce) { // SkyChart is most probably not open.
                                        // So try to start it
            // Try to get application path
            // final String applicationPath =
            final String applicationPath = this.om.getConfiguration()
                    .getConfig(SkyChartPreferences.CONFIG_APPLICATION_PATH);
            if (applicationPath == null || "".equals(applicationPath.trim())) { // No
                                                                                // application
                                                                                // path
                                                                                // specified
                om.createWarning(this.bundle.getString("skychart.application.start.nopath"));
                System.err.println(
                        "Unable to reach Skychart application and unable to start it as no application path is provided.\n"
                                + ce);
            } else { // Try to start it
                System.out.println("Unable to reach Skychart application. Try to launch it.");

                Worker startApplication = new Worker() {

                    private String message = "";
                    Process p = null;

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
                        } catch (Exception e) {
                            om.createWarning(SkyChartClient.this.bundle.getString("skychart.application.start.failed"));
                            System.err.println("Failed to start Skychart application (" + applicationPath + ").\n" + e);
                        }
                    }

                    @Override
                    public String getReturnMessage() {

                        return message;

                    }

                    @Override
                    public byte getReturnType() {

                        return (p.exitValue() == 0) ? Worker.RETURN_TYPE_OK : Worker.RETURN_TYPE_ERROR;

                    }

                };

                new ProgressDialog(this.om, this.bundle.getString("skychart.application.start.title"),
                        this.bundle.getString("skychart.application.start.loading"), startApplication);

                try {
                    socket = new StarchartSocket(ip, port, this.om.isDebug());
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

        commands[0] = "SEARCH " + target.getName();
        commands[1] = "SEARCH " + target.getDisplayName();

        // Search for alias names
        String[] aliasNames = target.getAliasNames();
        if ((aliasNames != null) && (aliasNames.length > 0)) {
            for (int i = 0; i < aliasNames.length; i++) {
                commands[i + 2] = "SEARCH " + aliasNames[i];
            }

        }

        // Sort so that a possible NGC name is always first
        for (int i = 0; i < commands.length; i++) {
            if (commands[i].startsWith("SEARCH NGC")) {
                String x = commands[0];
                commands[0] = commands[i];
                commands[i] = x;
                break;
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

        String result = "SETOBS " + lat + lon + alt + obs;

        return result;

    }

    // See Server Commands:
    // http://www.ap-i.net/skychart/en/documentation/server_commands
    private String createREFRESHCommand() {

        return "REDRAW";

    }

    private String createDateCommand(Calendar date) {

        String dateString = DateConverter.toISO8601(date);

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

        PopupMenuExtension pme = new PopupMenuExtension(
                new int[] { SchemaElementConstants.OBSERVATION, SchemaElementConstants.TARGET }, this.popupMenu);

        return pme;

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

        return new SkyChartPreferences(this.om.getConfiguration());

    }

    @Override
    public float getVersion() {

        return SkyChartClient.VERSION;

    }

    @Override
    public void reloadLanguage() {

        this.bundle = (PropertyResourceBundle) ResourceBundle
                .getBundle("de.lehmannet.om.ui.extension.skychart.Skychart", Locale.getDefault());
        this.initMenus();

    }

    // --------------
    // Not applicable ---------------------------------------------------------
    // --------------

    @Override
    public Set getAllSupportedXSITypes() {

        return null;

    }

    @Override
    public ICatalog[] getCatalogs(File catalogDir) {

        return null;

    }

    @Override
    public String getDialogForXSIType(String xsiType, int schemaElementConstant) {

        return null;

    }

    @Override
    public String getDisplayNameForXSIType(String xsiType) {

        return null;

    }

    @Override
    public String getPanelForXSIType(String xsiType, int schemaElementConstant) {

        return null;

    }

    @Override
    public Set getSupportedXSITypes(int schemaElementConstant) {

        return null;

    }

    @Override
    public boolean isCreationAllowed(String xsiType) {

        return false;

    }

    @Override
    public boolean addOALExtensionElement(Element docElement) {

        return true;

    }

}

class StarchartSocket extends Socket {

    public static final String SERVER_RESPONSE_OK = "OK!";
    public static final String SERVER_RESPONSE_FAILED = "Failed!";
    public static final String SERVER_RESPONSE_NOTFOUND = "Not found!";

    private boolean verbose = true;

    private PrintWriter out = null;
    private BufferedReader in = null;

    public StarchartSocket(String ip, int port, boolean verbose) throws IOException {

        super(ip, port);

        this.verbose = verbose;

        this.out = new PrintWriter(super.getOutputStream(), true);
        ;
        this.in = new BufferedReader(new InputStreamReader(super.getInputStream()));

        String response = this.in.readLine();
        if (this.verbose) {
            System.out.println("Socket creation response from Skychart: " + response);
        }

    }

    public String send(String command) throws IOException {

        // Add CR+LF (Byte 10 and 13) to end of command as PrintWriter.println()
        // uses system
        // line separator which is 13+10 on windows and e.g. only 10 on Linux.
        // Skycharts expects 13+10 so we've to make sure the CR+LF comes as
        // expected to Skychart
        byte[] b = command.getBytes();
        byte[] lfB = new byte[b.length + 2];
        System.arraycopy(b, 0, lfB, 0, b.length);
        lfB[lfB.length - 2] = 13;
        lfB[lfB.length - 1] = 10;
        command = new String(lfB);
        String byteString = "";
        if (this.verbose) {
            System.out.println("Skychart command is: " + command);
            System.out.println("Skychart command as byte array: ");
            for (int i = 0; i < lfB.length; i++) {
                byteString = byteString + " " + lfB[i];
            }
            System.out.println(byteString);
        }

        // Send the data
        this.out.print(command);
        this.out.flush();

        // Get the response
        String r = "";
        String response = "";

        // Check the response and wait on OK or Failure message from Skychart
        int index = 0;
        do {
            r = this.in.readLine();
            if (this.verbose) {
                System.out.println("Skychart response: " + r);
            }
            response = response + r;
            index++;
        } while (((r.indexOf(StarchartSocket.SERVER_RESPONSE_OK) == -1)
                && (r.indexOf(StarchartSocket.SERVER_RESPONSE_NOTFOUND) == -1)
                && (r.indexOf(StarchartSocket.SERVER_RESPONSE_FAILED) == -1)) && (index <= 3) // Wait for 3 responses
                                                                                              // for a OK or Failure
                                                                                              // from
                                                                                              // Skychart
        );

        return response;

    }

    @Override
    public void close() throws IOException {

        try {
            if (out != null) {
                this.out.close();
            }
            if (in != null) {
                this.in.close();
            }
        } catch (IllegalStateException ise) {
            // Readers and writers cannot be closed...can't do anything here
        }

        super.close();

    }

}
