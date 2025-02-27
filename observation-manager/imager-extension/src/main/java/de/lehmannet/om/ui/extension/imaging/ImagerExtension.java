package de.lehmannet.om.ui.extension.imaging;

import de.lehmannet.om.IFinding;
import de.lehmannet.om.IImager;
import de.lehmannet.om.IObservation;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ISession;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.SchemaOalTypeInfo;
import de.lehmannet.om.extension.imaging.CCDImager;
import de.lehmannet.om.ui.catalog.ICatalog;
import de.lehmannet.om.ui.dialog.IDialog;
import de.lehmannet.om.ui.dialog.ITargetDialog;
import de.lehmannet.om.ui.extension.AbstractExtension;
import de.lehmannet.om.ui.extension.IExtensionContext;
import de.lehmannet.om.ui.extension.PopupMenuExtension;
import de.lehmannet.om.ui.extension.imaging.dialog.CCDImagerDialog;
import de.lehmannet.om.ui.extension.imaging.panel.CCDImagerPanel;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.ui.panel.IPanel;
import de.lehmannet.om.ui.preferences.PreferencesPanel;
import de.lehmannet.om.util.SchemaElementConstants;
import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.JMenu;

public class ImagerExtension extends AbstractExtension {

    private static final String NAME = "Imager";

    private ResourceBundle bundle;
    private final IExtensionContext context;

    private final Set<String> supportedXSITypes = new HashSet<>();
    private final Set<String> allSupportedXSITypes = new HashSet<>();
    private final Set<SchemaOalTypeInfo> extensionTypes = new HashSet<>();

    public ImagerExtension(IExtensionContext context) {

        this.context = context;
        this.initLanguage();
        this.OAL_EXTENSION_FILE = "./openastronomylog21/extensions/ext_Imaging.xsd";

        this.initAllSupportedXSITypes();

        this.initPanels();
        this.initDialogs();

        this.initExtensionTypes();
    }

    private void initExtensionTypes() {

        this.extensionTypes.add(new SchemaOalTypeInfo.Builder()
                .targetClassName("de.lehmannet.om.extension.imaging.CCDImager")
                .targetType(CCDImager.XML_ATTRIBUTE_CCDIMAGER)
                .build());
    }

    private void initLanguage() {
        try {
            this.bundle = ResourceBundle.getBundle(
                    "de.lehmannet.om.ui.extension.imaging.oalImagingDisplayNames", Locale.getDefault());
        } catch (MissingResourceException mre) {

            this.bundle = ResourceBundle.getBundle(
                    "de.lehmannet.om.ui.extension.imaging.oalImagingDisplayNames", Locale.ENGLISH);
        }
    }

    @Override
    public Set<SchemaOalTypeInfo> getExtensionTypes() {
        return Collections.unmodifiableSet(this.extensionTypes);
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

        this.initLanguage();
    }

    @Override
    public String getName() {

        return ImagerExtension.NAME;
    }

    @Override
    public Optional<URL> getUpdateInformationURL() {

        return Optional.empty();
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
    public AbstractPanel getFindingPanelForXSIType(
            String xsiType, IFinding finding, ISession session, ITarget target, boolean editable) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AbstractPanel getTargetPanelForXSIType(
            String xsiType, ITarget target, IObservation observation, boolean editable) {
        // TODO Auto-generated method stub
        return null; // CCDImagerPanel(IImager imager, Boolean editable);
    }

    @Override
    public ITargetDialog getTargetDialogForXSIType(
            String xsiType, JFrame parent, ITarget target, IObservation observation, boolean editable) {
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
    public IDialog getGenericDialogForXSIType(String xsiType, JFrame parent, ISchemaElement element, boolean editable) {

        return new CCDImagerDialog(
                parent, this.context.getUserInterfaceHelper(), this.context.getModel(), (IImager) element, editable);
    }

    @Override
    public IPanel getGenericPanelForXSIType(String xsiType, ISchemaElement element, boolean editable) {
        // TODO Auto-generated method stub
        return new CCDImagerPanel((IImager) element, editable);
    }
}
