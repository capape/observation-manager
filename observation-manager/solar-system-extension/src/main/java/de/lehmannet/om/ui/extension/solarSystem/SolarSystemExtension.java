package de.lehmannet.om.ui.extension.solarSystem;

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
import de.lehmannet.om.extension.solarSystem.SolarSystemTargetComet;
import de.lehmannet.om.extension.solarSystem.SolarSystemTargetMinorPlanet;
import de.lehmannet.om.extension.solarSystem.SolarSystemTargetMoon;
import de.lehmannet.om.extension.solarSystem.SolarSystemTargetPlanet;
import de.lehmannet.om.extension.solarSystem.SolarSystemTargetSun;
import de.lehmannet.om.ui.catalog.ICatalog;
import de.lehmannet.om.ui.catalog.IListableCatalog;
import de.lehmannet.om.ui.dialog.IImagerDialog;
import de.lehmannet.om.ui.dialog.ITargetDialog;
import de.lehmannet.om.ui.extension.AbstractExtension;
import de.lehmannet.om.ui.extension.IExtensionContext;
import de.lehmannet.om.ui.extension.PopupMenuExtension;
import de.lehmannet.om.ui.extension.solarSystem.catalog.SolarSystemCatalog;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.ui.preferences.PreferencesPanel;
import de.lehmannet.om.util.SchemaElementConstants;

public class SolarSystemExtension extends AbstractExtension {

    private static final String NAME = "Solar System";
    private static final float VERSION = 0.9f;
    private static URL UPDATE_URL = null;
    static {
        try {
            SolarSystemExtension.UPDATE_URL = new URL(
                    "http://observation.sourceforge.net/extension/solarSystem/update");
        } catch (MalformedURLException m_url) {
            // Do nothing
        }
    }

    private PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle.getBundle(
            "de.lehmannet.om.ui.extension.solarSystem.oalSolarSystemTargetDisplayNames", Locale.getDefault());
    private IExtensionContext extensionContext;
    private final Set<String> supportedTargetXSITypes = new HashSet<>();
    private final Set<String> getSupportedFindingXSITypes = new HashSet<>();
    private final Set<String> allSupportedXSITypes = new HashSet<>();

    public SolarSystemExtension() {

        this.OAL_EXTENSION_FILE = "./openastronomylog21/extensions/ext_SolarSystem.xsd";

        this.initAllSupportedXSITypes();
        this.initFindingPanels();
        this.initTargetPanels();
        this.initTargetDialogs();

    }

    @Override
    public String getName() {

        return SolarSystemExtension.NAME;

    }

    @Override
    public float getVersion() {

        return SolarSystemExtension.VERSION;

    }

    @Override
    public URL getUpdateInformationURL() {

        return SolarSystemExtension.UPDATE_URL;

    }

    @Override
    public JMenu getMenu() {

        // No menu for SolarSystem
        return null;

    }

    @Override
    public void reloadLanguage() {

        this.bundle = (PropertyResourceBundle) ResourceBundle.getBundle(
                "de.lehmannet.om.ui.extension.solarSystem.oalSolarSystemTargetDisplayNames", Locale.getDefault());

    }

    @Override
    public ICatalog[] getCatalogs(File catalogDir) {

        IListableCatalog solarSystem = new SolarSystemCatalog();
        return new ICatalog[] { solarSystem };

    }

    @Override
    public PreferencesPanel getPreferencesPanel() {

        // No Preferences for SolarSystem
        return null;

    }

    @Override
    public Set<String> getAllSupportedXSITypes() {

        return Collections.unmodifiableSet(this.allSupportedXSITypes);
    }

    public void initAllSupportedXSITypes() {

        // Return all XSI types which are supported by this extension

        this.initSupportedTargetXSITypes();
        this.initSupportedFindingXSITypes();
        this.allSupportedXSITypes.addAll(this.getSupportedFindingXSITypes);
        this.allSupportedXSITypes.addAll(this.supportedTargetXSITypes);

    }

    @Override
    public Set<String> getSupportedXSITypes(SchemaElementConstants schemaElementConstant) {

        if (SchemaElementConstants.TARGET == schemaElementConstant) {
            return Collections.unmodifiableSet(this.supportedTargetXSITypes);
        } else if (SchemaElementConstants.FINDING == schemaElementConstant) {
            return Collections.unmodifiableSet(this.getSupportedFindingXSITypes);
        } else {
            return Collections.emptySet();
        }

    }

    private void initSupportedTargetXSITypes() {

        this.supportedTargetXSITypes.add(SolarSystemTargetComet.XML_XSI_TYPE_VALUE);
        this.supportedTargetXSITypes.add(SolarSystemTargetMinorPlanet.XML_XSI_TYPE_VALUE);
        this.supportedTargetXSITypes.add(SolarSystemTargetMoon.XML_XSI_TYPE_VALUE);
        this.supportedTargetXSITypes.add(SolarSystemTargetSun.XML_XSI_TYPE_VALUE);
        this.supportedTargetXSITypes.add(SolarSystemTargetPlanet.XML_XSI_TYPE_VALUE);
    }

    private void initSupportedFindingXSITypes() {

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

        // Don't allow instance creation for moon, sun and planets
        return (!xsiType.equals("oal:MoonTargetType")) && (!xsiType.equals("oal:SunTargetType"))
                && (!xsiType.equals("oal:PlanetTargetType"));

    }

    private void initFindingPanels() {

        Map<String, String> findingPanels = new HashMap<>();

        findingPanels.put(SolarSystemTargetComet.XML_XSI_TYPE_VALUE, "de.lehmannet.om.ui.panel.GenericFindingPanel");
        findingPanels.put(SolarSystemTargetMinorPlanet.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.panel.GenericFindingPanel");
        findingPanels.put(SolarSystemTargetMoon.XML_XSI_TYPE_VALUE, "de.lehmannet.om.ui.panel.GenericFindingPanel");
        findingPanels.put(SolarSystemTargetSun.XML_XSI_TYPE_VALUE, "de.lehmannet.om.ui.panel.GenericFindingPanel");
        findingPanels.put(SolarSystemTargetPlanet.XML_XSI_TYPE_VALUE, "de.lehmannet.om.ui.panel.GenericFindingPanel");

        this.getPanels().put(SchemaElementConstants.FINDING, findingPanels);

    }

    private void initTargetPanels() {

        Map<String, String> targetPanels = new HashMap<>();

        targetPanels.put(SolarSystemTargetComet.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.solarSystem.panel.SolarSystemTargetCometPanel");
        targetPanels.put(SolarSystemTargetMinorPlanet.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.solarSystem.panel.SolarSystemTargetMinorPlanetPanel");
        targetPanels.put(SolarSystemTargetMoon.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.solarSystem.panel.SolarSystemTargetMoonPanel");
        targetPanels.put(SolarSystemTargetSun.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.solarSystem.panel.SolarSystemTargetSunPanel");
        targetPanels.put(SolarSystemTargetPlanet.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.solarSystem.panel.SolarSystemTargetPlanetPanel");

        this.getPanels().put(SchemaElementConstants.TARGET, targetPanels);

    }

    private void initTargetDialogs() {

        Map<String, String> targetDialogs = new HashMap<>();

        targetDialogs.put(SolarSystemTargetComet.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.solarSystem.dialog.SolarSystemTargetCometDialog");
        targetDialogs.put(SolarSystemTargetMinorPlanet.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.solarSystem.dialog.SolarSystemTargetMinorPlanetDialog");
        targetDialogs.put(SolarSystemTargetMoon.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.solarSystem.dialog.SolarSystemTargetMoonDialog");
        targetDialogs.put(SolarSystemTargetSun.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.solarSystem.dialog.SolarSystemTargetSunDialog");
        targetDialogs.put(SolarSystemTargetPlanet.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.solarSystem.dialog.SolarSystemTargetPlanetDialog");

        this.getDialogs().put(SchemaElementConstants.TARGET, targetDialogs);

    }

    @Override
    public PopupMenuExtension getPopupMenu() {

        return null;

    }

    @Override
    public void setContext(IExtensionContext context) {
        this.extensionContext = context;

    }

    @Override
    public AbstractPanel getFindingPanelForXSIType(String xsiType, IFinding finding, ISession session, ITarget target,
            boolean editable) {
        return FindingPanelFactory.newInstance(this.extensionContext, xsiType, finding, session, editable);
    }

    @Override
    public AbstractPanel getTargetPanelForXSIType(String xsiType, ITarget target, IObservation observation,
            boolean editable) {

        return TargetPanelFactory.newInstance(this.extensionContext, xsiType, target, observation, editable);
    }

    @Override
    public ITargetDialog getTargetDialogForXSIType(String xsiType, JFrame parent, ITarget target,
            IObservation observation, boolean editable) {
        return TargetDialogFactory.newInstance(this.extensionContext, xsiType, parent, target, observation, editable);
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
