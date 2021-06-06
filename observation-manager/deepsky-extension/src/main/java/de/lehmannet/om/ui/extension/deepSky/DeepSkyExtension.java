package de.lehmannet.om.ui.extension.deepSky;

import java.io.File;
import java.net.MalformedURLException;
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import de.lehmannet.om.IFinding;
import de.lehmannet.om.IImager;
import de.lehmannet.om.IObservation;
import de.lehmannet.om.ISession;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.extension.deepSky.DeepSkyFinding;
import de.lehmannet.om.extension.deepSky.DeepSkyFindingDS;
import de.lehmannet.om.extension.deepSky.DeepSkyFindingOC;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetAS;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetCG;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetDN;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetDS;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetGC;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetGN;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetGX;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetMS;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetNA;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetOC;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetPN;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetQS;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetSC;
import de.lehmannet.om.ui.catalog.ICatalog;
import de.lehmannet.om.ui.catalog.IListableCatalog;
import de.lehmannet.om.ui.dialog.IImagerDialog;
import de.lehmannet.om.ui.dialog.ITargetDialog;
import de.lehmannet.om.SchemaOalTypeInfo;
import de.lehmannet.om.ui.extension.IExtension;
import de.lehmannet.om.ui.extension.IExtensionContext;
import de.lehmannet.om.ui.extension.PopupMenuExtension;
import de.lehmannet.om.ui.extension.deepSky.catalog.CaldwellCatalog;
import de.lehmannet.om.ui.extension.deepSky.catalog.DeepSkyTableModel;
import de.lehmannet.om.ui.extension.deepSky.catalog.ICCatalog;
import de.lehmannet.om.ui.extension.deepSky.catalog.MessierCatalog;
import de.lehmannet.om.ui.extension.deepSky.catalog.NGCCatalog;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.ui.preferences.PreferencesPanel;
import de.lehmannet.om.util.SchemaElementConstants;

public class DeepSkyExtension implements IExtension {

    private static final String NAME = "DeepSky";
    private static final String VERSION = "0.9.2";
    private static URL UPDATE_URL = null;
    static {
        try {
            DeepSkyExtension.UPDATE_URL = new URL("http://observation.sourceforge.net/extension/deepSky/update");
        } catch (MalformedURLException m_url) {
            // Do nothing
        }
    }

    private final Map<String, String> findingPanels = new HashMap<>();
    private final Map<String, String> targetPanels = new HashMap<>();
    private final Map<String, String> targetDialogs = new HashMap<>();

    private final String OAL_EXTENSION_FILE = "./openastronomylog21/extensions/ext_DeepSky.xsd";

    private ResourceBundle bundle;

    private IExtensionContext extensionContext;

    private final Set<String> supportedTargetXSITypes = new HashSet<>();
    private final Set<String> supportedFinfingXSITypes = new HashSet<>();
    private final Set<String> allSupportedXSITypes = new HashSet<>();
    private final Set<SchemaOalTypeInfo> extensionOalTypes = new HashSet<>();

    public DeepSkyExtension() {

        this.initLanguage();
        // this.OAL_EXTENSION_FILE = "./extensions/ext_DeepSky.xsd";
        this.initAllSupportedXSITypes();
        // Return all XSI types which are supported by this extension

        this.initFindingPanels();
        this.initTargetPanels();
        this.initTargetDialogs();

        this.initExtensionTypes();

    }

    private void initExtensionTypes() {

        this.extensionOalTypes.add(new SchemaOalTypeInfo.Builder()
                .targetClassName("de.lehmannet.om.extension.deepSky.DeepSkyTargetDN").targetType("oal:deepSkyDN")
                .findingClassName("de.lehmannet.om.extension.deepSky.DeepSkyFinding")
                .findingType("oal:findingsDeepSkyType").build());

        this.extensionOalTypes.add(new SchemaOalTypeInfo.Builder()
                .targetClassName("de.lehmannet.om.extension.deepSky.DeepSkyTargetDS").targetType("oal:deepSkyDS")
                .findingClassName("de.lehmannet.om.extension.deepSky.DeepSkyFindingDS")
                .findingType("oal:findingsDeepSkyDSType").build());

        this.extensionOalTypes.add(new SchemaOalTypeInfo.Builder()
                .targetClassName("de.lehmannet.om.extension.deepSky.DeepSkyTargetGC").targetType("oal:deepSkyGC")
                .findingClassName("de.lehmannet.om.extension.deepSky.DeepSkyFinding")
                .findingType("oal:findingsDeepSkyType").build());

        this.extensionOalTypes.add(new SchemaOalTypeInfo.Builder()
                .targetClassName("de.lehmannet.om.extension.deepSky.DeepSkyTargetGN").targetType("oal:deepSkyGN")
                .findingClassName("de.lehmannet.om.extension.deepSky.DeepSkyFinding")
                .findingType("oal:findingsDeepSkyType").build());

        this.extensionOalTypes.add(new SchemaOalTypeInfo.Builder()
                .targetClassName("de.lehmannet.om.extension.deepSky.DeepSkyTargetGX").targetType("oal:deepSkyGX")
                .findingClassName("de.lehmannet.om.extension.deepSky.DeepSkyFinding")
                .findingType("oal:findingsDeepSkyType").build());

        this.extensionOalTypes.add(new SchemaOalTypeInfo.Builder()
                .targetClassName("de.lehmannet.om.extension.deepSky.DeepSkyTargetOC").targetType("oal:deepSkyOC")
                .findingClassName("de.lehmannet.om.extension.deepSky.DeepSkyFindingOC")
                .findingType("oal:findingsDeepSkyOCType").build());

        this.extensionOalTypes.add(new SchemaOalTypeInfo.Builder()
                .targetClassName("de.lehmannet.om.extension.deepSky.DeepSkyTargetPN").targetType("oal:deepSkyPN")
                .findingClassName("de.lehmannet.om.extension.deepSky.DeepSkyFinding")
                .findingType("oal:findingsDeepSkyType").build());

        this.extensionOalTypes.add(new SchemaOalTypeInfo.Builder()
                .targetClassName("de.lehmannet.om.extension.deepSky.DeepSkyTargetQS").targetType("oal:deepSkyQS")
                .findingClassName("de.lehmannet.om.extension.deepSky.DeepSkyFinding")
                .findingType("oal:findingsDeepSkyType").build());

        this.extensionOalTypes.add(new SchemaOalTypeInfo.Builder()
                .targetClassName("de.lehmannet.om.extension.deepSky.DeepSkyTargetNA").targetType("oal:deepSkyNA")
                .findingClassName("de.lehmannet.om.GenericFinding").findingType("oal:findingsType").build());

        this.extensionOalTypes.add(new SchemaOalTypeInfo.Builder()
                .targetClassName("de.lehmannet.om.extension.deepSky.DeepSkyTargetAS").targetType("oal:deepSkyAS")
                .findingClassName("de.lehmannet.om.extension.deepSky.DeepSkyFinding")
                .findingType("oal:findingsDeepSkyType").build());

        this.extensionOalTypes.add(new SchemaOalTypeInfo.Builder()
                .targetClassName("de.lehmannet.om.extension.deepSky.DeepSkyTargetSC").targetType("oal:deepSkySC")
                .findingClassName("de.lehmannet.om.extension.deepSky.DeepSkyFinding")
                .findingType("oal:findingsDeepSkyType").build());

        this.extensionOalTypes.add(new SchemaOalTypeInfo.Builder()
                .targetClassName("de.lehmannet.om.extension.deepSky.DeepSkyTargetMS").targetType("oal:deepSkyMS")
                .findingClassName("de.lehmannet.om.GenericFinding").findingType("oal:findingsType").build());

        this.extensionOalTypes.add(new SchemaOalTypeInfo.Builder()
                .targetClassName("de.lehmannet.om.extension.deepSky.DeepSkyTargetCG").targetType("oal:deepSkyCG")
                .findingClassName("de.lehmannet.om.extension.deepSky.DeepSkyFinding")
                .findingType("oal:findingsDeepSkyType").build());

    }

    @Override
    public Set<SchemaOalTypeInfo> getExtensionTypes() {

        return Collections.unmodifiableSet(this.extensionOalTypes);
    }

    private void initLanguage() {
        try {
            this.bundle = ResourceBundle.getBundle("de.lehmannet.om.ui.extension.deepSky.oalDeepSkyTargetDisplayNames",
                    Locale.getDefault());
        } catch (MissingResourceException mre) {

            this.bundle = ResourceBundle.getBundle("de.lehmannet.om.ui.extension.deepSky.oalDeepSkyTargetDisplayNames",
                    Locale.ENGLISH);
        }
    }

    @Override
    public String getName() {

        return DeepSkyExtension.NAME;

    }

    @Override
    public String getVersion() {

        return DeepSkyExtension.VERSION;

    }

    @Override
    public URL getUpdateInformationURL() {

        return DeepSkyExtension.UPDATE_URL;

    }

    @Override
    public void reloadLanguage() {

        this.initLanguage();
        DeepSkyTableModel.reloadLanguage();

    }

    @Override
    public JMenu getMenu() {

        // No menu for DeepSky
        return null;

    }

    @Override
    public ICatalog[] getCatalogs(File catalogDir) {

        IListableCatalog messier = new MessierCatalog(
                new File(catalogDir.getAbsoluteFile() + File.separator + "deepSky/messier"));
        IListableCatalog ngc = new NGCCatalog(
                new File(catalogDir.getAbsoluteFile() + File.separator + "deepSky/NGC2009"));
        IListableCatalog ic = new ICCatalog(new File(catalogDir.getAbsoluteFile() + File.separator + "deepSky/IC2009"));
        // @since 0.81: Replaced with NGC/IC2009 by Wolfgang Steinicke
        // IListableCatalog hcngc = new HCNGCCatalog(new
        // File(catalogDir.getAbsoluteFile() + File.separator + "deepSky/HCNGC"));
        IListableCatalog caldwell = new CaldwellCatalog(
                new File(catalogDir.getAbsoluteFile() + File.separator + "deepSky/caldwell"));

        return new ICatalog[] { messier, ngc, ic, caldwell };

    }

    @Override
    public PreferencesPanel getPreferencesPanel() {

        // No preferences from DeepSky
        return null;

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

    private void initSupportedTargetXSITypes() {

        supportedTargetXSITypes.add(DeepSkyTargetDN.XML_XSI_TYPE_VALUE);
        supportedTargetXSITypes.add(DeepSkyTargetDS.XML_XSI_TYPE_VALUE);
        supportedTargetXSITypes.add(DeepSkyTargetGC.XML_XSI_TYPE_VALUE);
        supportedTargetXSITypes.add(DeepSkyTargetGN.XML_XSI_TYPE_VALUE);
        supportedTargetXSITypes.add(DeepSkyTargetGX.XML_XSI_TYPE_VALUE);
        supportedTargetXSITypes.add(DeepSkyTargetOC.XML_XSI_TYPE_VALUE);
        supportedTargetXSITypes.add(DeepSkyTargetPN.XML_XSI_TYPE_VALUE);
        supportedTargetXSITypes.add(DeepSkyTargetQS.XML_XSI_TYPE_VALUE);
        supportedTargetXSITypes.add(DeepSkyTargetNA.XML_XSI_TYPE_VALUE);
        supportedTargetXSITypes.add(DeepSkyTargetAS.XML_XSI_TYPE_VALUE);
        supportedTargetXSITypes.add(DeepSkyTargetSC.XML_XSI_TYPE_VALUE);
        supportedTargetXSITypes.add(DeepSkyTargetMS.XML_XSI_TYPE_VALUE);
        supportedTargetXSITypes.add(DeepSkyTargetCG.XML_XSI_TYPE_VALUE);

    }

    private void initSupportedFindingXSITypes() {

        supportedFinfingXSITypes.add(DeepSkyFinding.XML_XSI_TYPE_VALUE);
        supportedFinfingXSITypes.add(DeepSkyFindingOC.XML_XSI_TYPE_VALUE);
        supportedFinfingXSITypes.add(DeepSkyFindingDS.XML_XSI_TYPE_VALUE);

    }

    @Override
    public String getDisplayNameForXSIType(String xsiType) {

        try {
            return this.bundle.getString(xsiType);
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

        this.findingPanels.put(DeepSkyTargetDN.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyFindingPanel");
        this.findingPanels.put(DeepSkyTargetNA.XML_XSI_TYPE_VALUE, "de.lehmannet.om.ui.panel.GenericFindingPanel");
        this.findingPanels.put(DeepSkyTargetDS.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyFindingDSPanel");
        this.findingPanels.put(DeepSkyTargetGC.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyFindingPanel");
        this.findingPanels.put(DeepSkyTargetGN.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyFindingPanel");
        this.findingPanels.put(DeepSkyTargetGX.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyFindingPanel");
        this.findingPanels.put(DeepSkyTargetOC.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyFindingOCPanel");
        this.findingPanels.put(DeepSkyTargetPN.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyFindingPanel");
        this.findingPanels.put(DeepSkyTargetQS.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyFindingPanel");
        this.findingPanels.put(DeepSkyTargetAS.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyFindingPanel");
        this.findingPanels.put(DeepSkyTargetSC.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyFindingPanel");
        this.findingPanels.put(DeepSkyTargetMS.XML_XSI_TYPE_VALUE, "de.lehmannet.om.ui.panel.GenericFindingPanel");
        this.findingPanels.put(DeepSkyTargetCG.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyFindingPanel");
        this.findingPanels.put(DeepSkyFinding.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyFindingPanel");
        this.findingPanels.put(DeepSkyFindingOC.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyFindingOCPanel");
        this.findingPanels.put(DeepSkyFindingDS.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyFindingDSPanel");

    }

    private void initTargetPanels() {

        this.targetPanels.put(DeepSkyTargetDN.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetDNPanel");
        this.targetPanels.put(DeepSkyTargetDS.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetDSPanel");
        this.targetPanels.put(DeepSkyTargetGC.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetGCPanel");
        this.targetPanels.put(DeepSkyTargetGN.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetGNPanel");
        this.targetPanels.put(DeepSkyTargetGX.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetGXPanel");
        this.targetPanels.put(DeepSkyTargetOC.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetOCPanel");
        this.targetPanels.put(DeepSkyTargetPN.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetPNPanel");
        this.targetPanels.put(DeepSkyTargetQS.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetQSPanel");
        this.targetPanels.put(DeepSkyTargetNA.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetNAPanel");
        this.targetPanels.put(DeepSkyTargetAS.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetASPanel");
        this.targetPanels.put(DeepSkyTargetSC.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetSCPanel");
        this.targetPanels.put(DeepSkyTargetMS.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetMSPanel");
        this.targetPanels.put(DeepSkyTargetCG.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetCGPanel");

    }

    private void initTargetDialogs() {

        this.targetDialogs.put(DeepSkyTargetDN.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.deepSky.dialog.DeepSkyTargetDNDialog");
        this.targetDialogs.put(DeepSkyTargetDS.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.deepSky.dialog.DeepSkyTargetDSDialog");
        this.targetDialogs.put(DeepSkyTargetGC.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.deepSky.dialog.DeepSkyTargetGCDialog");
        this.targetDialogs.put(DeepSkyTargetGN.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.deepSky.dialog.DeepSkyTargetGNDialog");
        this.targetDialogs.put(DeepSkyTargetGX.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.deepSky.dialog.DeepSkyTargetGXDialog");
        this.targetDialogs.put(DeepSkyTargetOC.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.deepSky.dialog.DeepSkyTargetOCDialog");
        this.targetDialogs.put(DeepSkyTargetPN.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.deepSky.dialog.DeepSkyTargetPNDialog");
        this.targetDialogs.put(DeepSkyTargetQS.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.deepSky.dialog.DeepSkyTargetQSDialog");
        this.targetDialogs.put(DeepSkyTargetNA.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.deepSky.dialog.DeepSkyTargetNADialog");
        this.targetDialogs.put(DeepSkyTargetAS.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.deepSky.dialog.DeepSkyTargetASDialog");
        this.targetDialogs.put(DeepSkyTargetSC.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.deepSky.dialog.DeepSkyTargetSCDialog");
        this.targetDialogs.put(DeepSkyTargetMS.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.deepSky.dialog.DeepSkyTargetMSDialog");
        this.targetDialogs.put(DeepSkyTargetCG.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.deepSky.dialog.DeepSkyTargetCGDialog");

    }

    @Override
    public Set<String> getAllSupportedXSITypes() {

        return Collections.unmodifiableSet(this.allSupportedXSITypes);

    }

    public void initAllSupportedXSITypes() {

        this.initSupportedTargetXSITypes();
        this.initSupportedFindingXSITypes();

        allSupportedXSITypes.addAll(this.supportedFinfingXSITypes);
        allSupportedXSITypes.addAll(this.supportedTargetXSITypes);

    }

    @Override
    public boolean addOALExtensionElement(Element docElement) {

        // Check if include is already in place
        NodeList list = docElement.getElementsByTagName("xsd:include");
        NamedNodeMap attributes = null;
        for (int i = 0; i < list.getLength(); i++) {
            attributes = list.item(i).getAttributes();
            if (this.OAL_EXTENSION_FILE.equals(attributes.getNamedItem("schemaLocation").getNodeValue())) {
                return true;
            }
        }

        Document doc = docElement.getOwnerDocument();

        Element e = doc.createElement("xsd:include");
        e.setAttribute("schemaLocation", this.OAL_EXTENSION_FILE);

        docElement.appendChild(e);

        return true;

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + OAL_EXTENSION_FILE.hashCode();
        result = prime * result + findingPanels.hashCode();
        result = prime * result + targetDialogs.hashCode();
        result = prime * result + targetPanels.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DeepSkyExtension other = (DeepSkyExtension) obj;
        if (!findingPanels.equals(other.findingPanels))
            return false;
        if (!targetDialogs.equals(other.targetDialogs))
            return false;
        return targetPanels.equals(other.targetPanels);
    }

    @Override
    public PopupMenuExtension getPopupMenu() {

        return null;

    }

    @Override
    public AbstractPanel getFindingPanelForXSIType(String xsiType, IFinding finding, ISession session, ITarget target,
            boolean editable) {
        return FindingPanelFactory.newInstance(this.extensionContext, xsiType, finding, session, editable);
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
    public void setContext(IExtensionContext context) {
        this.extensionContext = context;

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
