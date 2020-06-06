package de.lehmannet.om.ui.extension.imaging;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JMenu;

import de.lehmannet.om.IFinding;
import de.lehmannet.om.IImager;
import de.lehmannet.om.IObservation;
import de.lehmannet.om.ISession;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.extension.imaging.CCDImager;
import de.lehmannet.om.ui.catalog.ICatalog;
import de.lehmannet.om.ui.dialog.IImagerDialog;
import de.lehmannet.om.ui.dialog.ITargetDialog;
import de.lehmannet.om.ui.extension.AbstractExtension;
import de.lehmannet.om.ui.extension.IExtensionContext;
import de.lehmannet.om.ui.extension.PopupMenuExtension;
import de.lehmannet.om.ui.extension.imaging.dialog.CCDImagerDialog;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.ui.preferences.PreferencesPanel;
import de.lehmannet.om.util.SchemaElementConstants;

public class ImagerExtension extends AbstractExtension {

    private static final String NAME = "Imager";
    private static final float VERSION = 0.9f;
    private static URL UPDATE_URL = null;
    static {
        try {
            ImagerExtension.UPDATE_URL = new URL("http://observation.sourceforge.net/extension/imaging/update");
        } catch (MalformedURLException m_url) {
            // Do nothing
        }
    }

    private PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
            .getBundle("de.lehmannet.om.ui.extension.imaging.oalImagingDisplayNames", Locale.getDefault());
    private IExtensionContext extensionContext;

    private final Set<String> supportedXSITypes = new HashSet<>();
    private final Set<String> allSupportedXSITypes = new HashSet<>();

    public ImagerExtension() {

        this.OAL_EXTENSION_FILE = "./openastronomylog21/extensions/ext_Imaging.xsd";

        this.initAllSupportedXSITypes();

        this.initPanels();
        this.initDialogs();

    }

    @Override
    public ICatalog[] getCatalogs(File catalogDir) {

        return null;

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
    public JMenu getMenu() {

        return null;

    }

    @Override
    public void reloadLanguage() {

        this.bundle = (PropertyResourceBundle) ResourceBundle
                .getBundle("de.lehmannet.om.ui.extension.imaging.oalImagingDisplayNames", Locale.getDefault());

    }

    @Override
    public String getName() {

        return ImagerExtension.NAME;

    }

    @Override
    public URL getUpdateInformationURL() {

        return ImagerExtension.UPDATE_URL;

    }

    @Override
    public PreferencesPanel getPreferencesPanel() {

        return null;

    }

    private void initSupportedXSITypes() {
        this.supportedXSITypes.add(CCDImager.XML_ATTRIBUTE_CCDIMAGER);
    }

    @Override
    public Set<String> getSupportedXSITypes(SchemaElementConstants schemaElementConstants) {

        if (SchemaElementConstants.IMAGER == schemaElementConstants) {
            return Collections.unmodifiableSet(this.supportedXSITypes);
        }

        return Collections.emptySet();

    }

    @Override
    public Set<String> getAllSupportedXSITypes() {

        return Collections.unmodifiableSet(this.allSupportedXSITypes);

    }

    public void initAllSupportedXSITypes() {

        this.initSupportedXSITypes();

        this.allSupportedXSITypes.addAll(this.supportedXSITypes);

    }

    @Override
    public float getVersion() {

        return ImagerExtension.VERSION;

    }

    @Override
    public boolean isCreationAllowed(String xsiType) {

        return true;

    }

    private void initPanels() {

        Map<String, String> panels = new HashMap<>();

        panels.put(CCDImager.XML_ATTRIBUTE_CCDIMAGER, "de.lehmannet.om.ui.extension.imaging.panel.CCDImagerPanel");

        this.getPanels().put(SchemaElementConstants.IMAGER, panels);

    }

    private void initDialogs() {

        Map<String, String> dialogs = new HashMap<>();

        dialogs.put(CCDImager.XML_ATTRIBUTE_CCDIMAGER, "de.lehmannet.om.ui.extension.imaging.dialog.CCDImagerDialog");

        this.getPanels().put(SchemaElementConstants.IMAGER, dialogs);

    }

    @Override
    public PopupMenuExtension getPopupMenu() {

        return null;

    }

    @Override
    public AbstractPanel getFindingPanelForXSIType(String xsiType, IFinding finding, ISession session, ITarget target,
            boolean editable) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setContext(IExtensionContext context) {
        this.extensionContext = context;
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

        if (xsiType == null) {
            return false;
        }
        return this.allSupportedXSITypes.contains(xsiType);
    }

    @Override
    public IImagerDialog getImagerDialogForXSIType(String xsiType, JFrame parent, IImager imager, boolean editable) {

        return new CCDImagerDialog(parent, this.extensionContext.getUserInterfaceHelper(),
                this.extensionContext.getModel(), imager);
    }

}
