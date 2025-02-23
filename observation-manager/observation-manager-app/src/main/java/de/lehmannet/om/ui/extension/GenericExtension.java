package de.lehmannet.om.ui.extension;

import de.lehmannet.om.GenericFinding;
import de.lehmannet.om.GenericTarget;
import de.lehmannet.om.IFinding;
import de.lehmannet.om.IObservation;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ISession;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.SchemaOalTypeInfo;
import de.lehmannet.om.TargetStar;
import de.lehmannet.om.ui.catalog.ICatalog;
import de.lehmannet.om.ui.dialog.IDialog;
import de.lehmannet.om.ui.dialog.ITargetDialog;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.ui.panel.IPanel;
import de.lehmannet.om.ui.preferences.PreferencesPanel;
import de.lehmannet.om.ui.util.LocaleToolsFactory;
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
import org.w3c.dom.Element;

// Build-In extension for ObservationManager
public class GenericExtension extends AbstractExtension {

    public static final String NAME = "Built-In extension";

    private ResourceBundle bundle =
            LocaleToolsFactory.appInstance().getBundle("genericTargetDisplayNames", Locale.getDefault());

    private final Map<String, String> findingPanels = new HashMap<>();
    private final Map<String, String> targetPanels = new HashMap<>();
    private final Map<String, String> targetDialogs = new HashMap<>();
    private final IExtensionContext context;

    private final Set<String> supportedTargetXSITypes = new HashSet<>();
    private final Set<String> supportedFinfingXSITypes = new HashSet<>();
    private final Set<String> allSupportedXSITypes = new HashSet<>();
    private final Set<SchemaOalTypeInfo> extensionOalTypes = new HashSet<>();

    public GenericExtension(IExtensionContext context) {

        this.context = context;

        this.initAllSupportedXSITypes();

        this.initFindingPanels();
        this.initTargetDialogs();
        this.initTargetPanels();

        this.initExtensionTypes();
    }

    private void initExtensionTypes() {

        this.extensionOalTypes.add(new SchemaOalTypeInfo.Builder()
                .targetClassName("de.lehmannet.om.GenericTarget")
                .targetType(GenericTarget.XML_XSI_TYPE_VALUE)
                .findingClassName("de.lehmannet.om.GenericFinding")
                .findingType(GenericFinding.XML_XSI_TYPE_VALUE)
                .build());

        this.extensionOalTypes.add(new SchemaOalTypeInfo.Builder()
                .targetClassName("de.lehmannet.om.TargetStar")
                .targetType(TargetStar.XML_XSI_TYPE_VALUE)
                .findingClassName("de.lehmannet.om.GenericFinding")
                .findingType(GenericFinding.XML_XSI_TYPE_VALUE)
                .build());
    }

    @Override
    public Set<SchemaOalTypeInfo> getExtensionTypes() {

        return Collections.unmodifiableSet(this.extensionOalTypes);
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
    public Optional<URL> getUpdateInformationURL() {

        return Optional.empty();
    }

    @Override
    public AbstractPanel getFindingPanelForXSIType(
            String xsiType, IFinding finding, ISession session, ITarget target, boolean editable) {
        return FindingPanelFactory.newInstance(this.context, xsiType, finding, session, editable);
    }

    @Override
    public AbstractPanel getTargetPanelForXSIType(
            String xsiType, ITarget target, IObservation observation, boolean editable) {
        return TargetPanelFactory.newInstance(this.context, xsiType, target, editable);
    }

    @Override
    public ITargetDialog getTargetDialogForXSIType(
            String xsiType, JFrame parent, ITarget target, IObservation observation, boolean editable) {
        return TargetDialogFactory.newInstance(this.context, xsiType, parent, target, editable);
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getGenericDialogForXSIType'");
    }

    @Override
    public IPanel getGenericPanelForXSIType(String xsiType, ISchemaElement element, boolean editable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getGenericPanelForXSIType'");
    }
}
