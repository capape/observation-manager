/* ====================================================================
 * /extension/variableStars/ExtensionLoader.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.variableStars;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.lehmannet.om.IFinding;
import de.lehmannet.om.IImager;
import de.lehmannet.om.IObservation;
import de.lehmannet.om.IObserver;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ISession;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.extension.variableStars.FindingVariableStar;
import de.lehmannet.om.extension.variableStars.TargetVariableStar;
import de.lehmannet.om.extension.variableStars.export.AAVSOVisualSerializer;
import de.lehmannet.om.ui.catalog.ICatalog;
import de.lehmannet.om.ui.dialog.IImagerDialog;
import de.lehmannet.om.ui.dialog.ITargetDialog;
import de.lehmannet.om.ui.dialog.SchemaElementSelectorPopup;
import de.lehmannet.om.ui.extension.AbstractExtension;
import de.lehmannet.om.ui.extension.IExtensionContext;
import de.lehmannet.om.ui.extension.PopupMenuExtension;
import de.lehmannet.om.ui.extension.variableStars.catalog.GCVS4Catalog;
import de.lehmannet.om.ui.extension.variableStars.dialog.VariableStarChartDialog;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.ui.preferences.PreferencesPanel;
import de.lehmannet.om.util.SchemaElementConstants;

public class VariableStarsExtension extends AbstractExtension implements ActionListener {

    private static final String NAME = "Variable Stars";
    private static final String VERSION = "0.9.3";
    private static URL UPDATE_URL = null;
    static {
        try {
            VariableStarsExtension.UPDATE_URL = new URL(
                    "http://observation.sourceforge.net/extension/variableStars/update");
        } catch (MalformedURLException m_url) {
            // Do nothing
        }
    }

    private PropertyResourceBundle typeBundle = (PropertyResourceBundle) ResourceBundle.getBundle(
            "de.lehmannet.om.ui.extension.variableStars.oalVariableStarTargetDisplayNames", Locale.getDefault());
    private PropertyResourceBundle uiBundle = (PropertyResourceBundle) ResourceBundle
            .getBundle("de.lehmannet.om.ui.extension.variableStars.VariableStar", Locale.getDefault());

    private JMenuItem exportAAVSO = null;
    private JMenuItem showChart = null;

    private static final Logger LOGGER = LoggerFactory.getLogger(VariableStarsExtension.class);

    private IExtensionContext extensionContext;

    private final Set<String> supportedTargetXSITypes = new HashSet<>();
    private final Set<String> supportedFinfingXSITypes = new HashSet<>();
    private final Set<String> allSupportedXSITypes = new HashSet<>();

    public VariableStarsExtension() {

        this.OAL_EXTENSION_FILE = "./openastronomylog21/extensions/ext_VariableStars.xsd";

        this.initAllSupportedXSITypes();

        this.initFindingPanels();
        this.initTargetPanels();
        this.initTargetDialogs();

    }

    @Override
    public String getName() {

        return VariableStarsExtension.NAME;

    }

    @Override
    public URL getUpdateInformationURL() {

        return VariableStarsExtension.UPDATE_URL;

    }

    @Override
    public String getVersion() {

        return VariableStarsExtension.VERSION;

    }

    @Override
    public void reloadLanguage() {

        this.typeBundle = (PropertyResourceBundle) ResourceBundle.getBundle(
                "de.lehmannet.om.ui.extension.variableStars.oalVariableStarTargetDisplayNames", Locale.getDefault());
        this.uiBundle = (PropertyResourceBundle) ResourceBundle
                .getBundle("de.lehmannet.om.ui.extension.variableStars.VariableStar", Locale.getDefault());

    }

    @Override
    public JMenu getMenu() {

        JMenu menu = new JMenu(this.uiBundle.getString("menu.main"));

        this.exportAAVSO = new JMenuItem(this.uiBundle.getString("menu.aavsoExport"));
        exportAAVSO.setMnemonic('e');
        exportAAVSO.addActionListener(this);
        menu.add(exportAAVSO);

        this.showChart = new JMenuItem(this.uiBundle.getString("menu.showChart"));
        showChart.setMnemonic('c');
        showChart.addActionListener(this);
        menu.add(showChart);

        return menu;

    }

    @Override
    public PreferencesPanel getPreferencesPanel() {

        return new VariableStarsPreferences(this.extensionContext.getConfiguration());

    }

    @Override
    public ICatalog[] getCatalogs(File catalogDir) {

        ICatalog gcvs = new GCVS4Catalog(catalogDir.getAbsoluteFile(), this.extensionContext.getConfiguration());

        return new ICatalog[] { gcvs };

    }

    private void initSupportedTargetXSITypes() {

        this.supportedTargetXSITypes.add(TargetVariableStar.XML_XSI_TYPE_VALUE);

    }

    private void initSupportedFindingXSITypes() {

        this.supportedFinfingXSITypes.add(FindingVariableStar.XML_XSI_TYPE_VALUE);
    }

    private void initAllSupportedXSITypes() {

        this.initSupportedTargetXSITypes();
        this.initSupportedFindingXSITypes();

        allSupportedXSITypes.addAll(this.supportedFinfingXSITypes);
        allSupportedXSITypes.addAll(this.supportedTargetXSITypes);

    }

    @Override
    public Set<String> getSupportedXSITypes(SchemaElementConstants schemaElementConstant) {

        if (SchemaElementConstants.TARGET == schemaElementConstant) {
            return Collections.unmodifiableSet(this.supportedTargetXSITypes);
        } else if (SchemaElementConstants.FINDING == schemaElementConstant) {
            return Collections.unmodifiableSet(this.supportedFinfingXSITypes);
        }

        return Collections.emptySet();

    }

    @Override
    public String getDisplayNameForXSIType(String xsiType) {

        try {
            return this.typeBundle.getString(xsiType);
        } catch (MissingResourceException mre) { // XSIType not found
            return null;
        }

    }

    @Override
    public boolean isCreationAllowed(String xsiType) {

        // All elements are allowed for creation of new instances
        return true;

    }

    private void initFindingPanels() {

        Map<String, String> findingPanels = new HashMap<>();

        findingPanels.put(FindingVariableStar.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.variableStars.panel.VariableStarFindingPanel");
        findingPanels.put(TargetVariableStar.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.variableStars.panel.VariableStarFindingPanel");

        this.getPanels().put(SchemaElementConstants.FINDING, findingPanels);

    }

    private void initTargetPanels() {

        Map<String, String> targetPanels = new HashMap<>();

        targetPanels.put(TargetVariableStar.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.variableStars.panel.VariableStarTargetPanel");

        this.getPanels().put(SchemaElementConstants.TARGET, targetPanels);

    }

    private void initTargetDialogs() {

        Map<String, String> targetDialogs = new HashMap<>();

        targetDialogs.put(TargetVariableStar.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.variableStars.dialog.VariableStarTargetDialog");

        this.getDialogs().put(SchemaElementConstants.TARGET, targetDialogs);

    }

    @Override
    public PopupMenuExtension getPopupMenu() {

        return null;

    }

    @Override
    public AbstractPanel getFindingPanelForXSIType(String xsiType, IFinding finding, ISession session, ITarget target,
            boolean editable) {
        return FindingPanelFactory.newInstance(this.extensionContext, xsiType, finding, session, target, editable);
    }

    @Override
    public ITargetDialog getTargetDialogForXSIType(String xsiType, JFrame parent, ITarget target,
            IObservation observation, boolean editable) {
        return TargetDialogFactory.newInstance(this.extensionContext, xsiType, parent, target, editable);
    }

    @Override
    public void setContext(IExtensionContext context) {
        this.extensionContext = context;

    }

    @Override
    public AbstractPanel getTargetPanelForXSIType(String xsiType, ITarget target, IObservation observation,
            boolean editable) {
        return TargetPanelFactory.newInstance(this.extensionContext, xsiType, target, editable);
    }

    @Override
    public boolean supports(String xsiType) {
        if (xsiType == null) {
            return false;
        }
        return this.allSupportedXSITypes.contains(xsiType);
    }

    @Override
    public IImagerDialog getImagerDialogForXSIType(String xsiType, JFrame parent, IImager imager, boolean editable) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() instanceof JMenuItem) { // Should always be the case
            JMenuItem source = (JMenuItem) e.getSource();
            Container om = source.getParent();

            if (source.equals(this.exportAAVSO)) {

                // Get preselected observations
                IObservation[] allObservations = this.extensionContext.getModel().getObservations();
                if (allObservations.length == 0) {
                    this.extensionContext.getUserInterfaceHelper()
                            .showInfo(this.uiBundle.getString("info.noObservationsFound"));
                    return;
                }
                List<IObservation> preselectedObservations = new ArrayList<>();
                for (IObservation allObservation : allObservations) {
                    // Only the variable star observations are of interest
                    if (TargetVariableStar.XML_XSI_TYPE_VALUE.equals(allObservation.getTarget().getXSIType())) {
                        // @todo: This works only with one result!
                        if (!((FindingVariableStar) allObservation.getResults().get(0)).isAlreadyExportedToAAVSO()) {
                            preselectedObservations.add(allObservation);
                        }
                    }
                }

                // Create popup for variable star observations
                // FIX ME: parent frame instead of null.
                SchemaElementSelectorPopup popup = new SchemaElementSelectorPopup(null,
                        this.extensionContext.getModel(),
                        this.uiBundle.getString("popup.exportAAVSO.selectObservations"),
                        TargetVariableStar.XML_XSI_TYPE_VALUE, preselectedObservations, true,
                        SchemaElementConstants.OBSERVATION);
                List<ISchemaElement> variableStarObservations = popup.getAllSelectedElements();
                if ((variableStarObservations == null) || (variableStarObservations.isEmpty())) {
                    return;
                }

                List<IObservation> results = variableStarObservations.stream().map(x -> (IObservation) x)
                        .collect(Collectors.toList());

                AAVSOVisualSerializer aavsoExport = new AAVSOVisualSerializer(
                        "Observation Manager - VariableStars Extension" + VERSION, results);

                // Create export file path
                String[] files = this.extensionContext.getModel().getAllOpenedFiles();
                if ((files == null) || (files.length == 0)) { // There is data (otherwise we wouldn't have come here),
                                                              // but data's not saved
                    this.extensionContext.getUserInterfaceHelper()
                            .showInfo(this.uiBundle.getString("error.noXMLFileOpen"));
                    return;
                }

                Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
                om.setCursor(hourglassCursor);

                // @todo This works only with ONE file opened
                File xmlFile = new File(files[0]);
                String exportFileName = xmlFile.getName();
                exportFileName = exportFileName.substring(0, exportFileName.indexOf('.'));
                exportFileName = xmlFile.getParent() + File.separatorChar + exportFileName + "_aavso.txt";
                File aavsoFile = new File(exportFileName);
                int i = 2;
                while (aavsoFile.exists()) { // Check if file exists...
                    exportFileName = exportFileName.substring(0, exportFileName.lastIndexOf("_aavso"));
                    exportFileName = exportFileName + "_aavso(" + i + ").txt";
                    i++;
                    aavsoFile = new File(exportFileName);
                }

                // Do the actual export
                int exportCounter = 0;
                try {
                    exportCounter = aavsoExport.serialize(new BufferedOutputStream(new FileOutputStream(aavsoFile)));
                } catch (FileNotFoundException fnfe) {
                    this.extensionContext.getUserInterfaceHelper()
                            .showInfo(this.uiBundle.getString("error.aavsoExportFileNotFound"));
                    System.err.println(fnfe);

                    Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
                    om.setCursor(defaultCursor);
                    return;
                } catch (Exception ex) {
                    this.extensionContext.getUserInterfaceHelper()
                            .showInfo(this.uiBundle.getString("error.aavsoExportNotOK"));
                    System.err.println(ex);

                    Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
                    om.setCursor(defaultCursor);
                    return;
                }

                // Set the om status to changed, as findings have been exported (which changes
                // their status)
                this.extensionContext.getModel().setChanged(true);

                Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
                om.setCursor(defaultCursor);

                if (exportCounter != variableStarObservations.size()) { // Not all observations were exported
                    this.extensionContext.getUserInterfaceHelper()
                            .showInfo(exportCounter + " " + this.uiBundle.getString("info.aavsoExport") + "\n"
                                    + aavsoFile + "\n" + this.uiBundle.getString("info.aavsoExportCheckLog"));
                } else { // All observations exported
                    this.extensionContext.getUserInterfaceHelper().showInfo(
                            exportCounter + " " + this.uiBundle.getString("info.aavsoExport") + "\n" + aavsoFile);
                }

                if (exportCounter == 0) { // Nothing exported, so delete file (file only contains aavso header)
                    if (!aavsoFile.delete()) {
                        LOGGER.warn("File no deleted");
                    }
                }

            } else if (source.equals(this.showChart)) {

                // Create popup with variableStars
                VariableStarSelectorPopup popup = null;
                IObservation[] observations = null;
                boolean quitLoop = false;
                do {
                    try {
                        // FIXME: null->om
                        popup = new VariableStarSelectorPopup(null, this.extensionContext.getUserInterfaceHelper(),
                                this.extensionContext.getModel());
                    } catch (IllegalArgumentException iae) { // No variable star observation found
                        return;
                    }
                    if (popup.getAllSelectedObservations() != null) {
                        if (popup.getAllSelectedObservations().length > 0) {
                            observations = popup.getAllSelectedObservations();

                            if ((observations != null) // No observations for star
                                    && (observations.length <= 0)) {
                                this.extensionContext.getUserInterfaceHelper().showWarning(
                                        this.uiBundle.getString("popup.selectVariableStar.warning.noObservations"));
                            } else {
                                quitLoop = true;
                            }
                        } else { // No Star selected
                            this.extensionContext.getUserInterfaceHelper().showWarning(
                                    this.uiBundle.getString("popup.selectVariableStar.warning.noStarSelected"));
                        }
                    } else {
                        return; // User pressed cancel
                    }
                } while (!quitLoop); // Exit loop by pressing cancel

                // Show color selection
                ColorSelectionDialog colorDialog = new ColorSelectionDialog(null,
                        this.extensionContext.getConfiguration(), observations);
                Map<IObserver, Color> colorMap = colorDialog.getColorMap();

                // Show chart
                if (colorMap != null) {
                    new VariableStarChartDialog(null, this.extensionContext.getUserInterfaceHelper(),
                            this.extensionContext.getConfiguration(), Objects.requireNonNull(observations), colorMap);
                }
            }
        }

    }

}
