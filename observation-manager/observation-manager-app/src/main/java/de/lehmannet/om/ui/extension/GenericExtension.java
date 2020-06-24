package de.lehmannet.om.ui.extension;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JMenu;

import org.w3c.dom.Element;

import de.lehmannet.om.GenericFinding;
import de.lehmannet.om.GenericTarget;
import de.lehmannet.om.IFinding;
import de.lehmannet.om.IImager;
import de.lehmannet.om.IObservation;
import de.lehmannet.om.ISession;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.TargetStar;
import de.lehmannet.om.ui.catalog.ICatalog;
import de.lehmannet.om.ui.dialog.IImagerDialog;
import de.lehmannet.om.ui.dialog.ITargetDialog;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.ui.preferences.PreferencesPanel;
import de.lehmannet.om.ui.util.LocaleToolsFactory;
import de.lehmannet.om.util.SchemaElementConstants;

// Build-In extension for ObservationManager
public class GenericExtension implements IExtension {

    public static final String NAME = "Built-In extension";
    private static final String VERSION = "0.9.0";

    private ResourceBundle bundle = LocaleToolsFactory.appInstance().getBundle("genericTargetDisplayNames",
            Locale.getDefault());

    private final Map<String, String> findingPanels = new HashMap<>();
    private final Map<String, String> targetPanels = new HashMap<>();
    private final Map<String, String> targetDialogs = new HashMap<>();
    private IExtensionContext extensionContext;

    private final Set<String> supportedTargetXSITypes = new HashSet<>();
    private final Set<String> supportedFinfingXSITypes = new HashSet<>();
    private final Set<String> allSupportedXSITypes = new HashSet<>();

    public GenericExtension() {

        this.initAllSupportedXSITypes();

        this.initFindingPanels();
        this.initTargetDialogs();
        this.initTargetPanels();

    }

    @Override
    public boolean addOALExtensionElement(Element docElement) {

        // We don't have something to add to the schema
        return true;

    }

    @Override
    public String getName() {

        return GenericExtension.NAME;

    }

    @Override
    public String getVersion() {

        return GenericExtension.VERSION;

    }

    @Override
    public void reloadLanguage() {

        this.bundle = LocaleToolsFactory.appInstance().getBundle("ObservationManager", Locale.getDefault());

    }

    @Override
    public JMenu getMenu() {

        return null;

    }

    @Override
    public PreferencesPanel getPreferencesPanel() {

        return null;

    }

    @Override
    public ICatalog[] getCatalogs(File catalogDir) {

        return null;

    }

    private void initAllSupportedXSITypes() {

        this.initSupportedTargetXSITypes();
        this.initSupportedFindingXSITypes();

        allSupportedXSITypes.addAll(this.supportedFinfingXSITypes);
        allSupportedXSITypes.addAll(this.supportedTargetXSITypes);

    }

    private void initSupportedTargetXSITypes() {

        supportedTargetXSITypes.add(GenericTarget.XML_XSI_TYPE_VALUE);
        supportedTargetXSITypes.add(TargetStar.XML_XSI_TYPE_VALUE);

    }

    private void initSupportedFindingXSITypes() {

        supportedFinfingXSITypes.add(GenericFinding.XML_XSI_TYPE_VALUE);
    }

    @Override
    public Set<String> getAllSupportedXSITypes() {

        return Collections.unmodifiableSet(this.allSupportedXSITypes);

    }

    @Override
    public Set<String> getSupportedXSITypes(SchemaElementConstants schemaElementConstant) {

        if (SchemaElementConstants.TARGET == schemaElementConstant) {
            return Collections.unmodifiableSet(this.supportedTargetXSITypes);

        } else if (SchemaElementConstants.FINDING == schemaElementConstant) {
            return Collections.unmodifiableSet(this.supportedFinfingXSITypes);
        } else {
            return Collections.emptySet();
        }

    }

    @Override
    public String getPanelForXSIType(String xsiType, SchemaElementConstants schemaElementConstants) {

        if (SchemaElementConstants.FINDING == schemaElementConstants) {
            return (String) this.findingPanels.get(xsiType);
        } else if (SchemaElementConstants.TARGET == schemaElementConstants) {
            return (String) this.targetPanels.get(xsiType);
        }

        return null;

    }

    @Override
    public String getDialogForXSIType(String xsiType, SchemaElementConstants schemaElementConstants) {

        if (SchemaElementConstants.TARGET == schemaElementConstants) {
            return (String) this.targetDialogs.get(xsiType);
        }

        return null;

    }

    @Override
    public boolean isCreationAllowed(String xsiType) {

        return true;

    }

    @Override
    public String getDisplayNameForXSIType(String xsiType) {

        try {
            return this.bundle.getString(xsiType);
        } catch (MissingResourceException mre) { // XSIType not found
            return null;
        }

    }

    private void initFindingPanels() {

        this.findingPanels.put(GenericTarget.XML_XSI_TYPE_VALUE, "de.lehmannet.om.ui.panel.GenericFindingPanel");
        this.findingPanels.put(GenericFinding.XML_XSI_TYPE_VALUE, "de.lehmannet.om.ui.panel.GenericFindingPanel");
        this.findingPanels.put(TargetStar.XML_XSI_TYPE_VALUE, "de.lehmannet.om.ui.panel.GenericFindingPanel");

    }

    private void initTargetPanels() {

        this.targetPanels.put(TargetStar.XML_XSI_TYPE_VALUE, "de.lehmannet.om.ui.panel.TargetStarPanel");
        this.targetPanels.put(GenericTarget.XML_XSI_TYPE_VALUE, "de.lehmannet.om.ui.panel.GenericTargetPanel");

    }

    private void initTargetDialogs() {

        this.targetDialogs.put(GenericTarget.XML_XSI_TYPE_VALUE, "de.lehmannet.om.ui.dialog.GenericTargetDialog");
        this.targetDialogs.put(TargetStar.XML_XSI_TYPE_VALUE, "de.lehmannet.om.ui.dialog.TargetStarDialog");

    }

    @Override
    public PopupMenuExtension getPopupMenu() {

        return null;

    }

    // No specific updates available. GenericExtension is part of ObservationManager
    // itself
    @Override
    public URL getUpdateInformationURL() {

        return null;

    }

    @Override
    public AbstractPanel getFindingPanelForXSIType(String xsiType, IFinding finding, ISession session, ITarget target,
            boolean editable) {
        return FindingPanelFactory.newInstance(this.extensionContext, xsiType, finding, session, editable);
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
    public ITargetDialog getTargetDialogForXSIType(String xsiType, JFrame parent, ITarget target,
            IObservation observation, boolean editable) {
        return TargetDialogFactory.newInstance(this.extensionContext, xsiType, parent, target, editable);
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

}
