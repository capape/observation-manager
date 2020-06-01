package de.lehmannet.om.ui.extension.solarSystem;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
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

    public SolarSystemExtension() {

        this.OAL_EXTENSION_FILE = "./openastronomylog21/extensions/ext_SolarSystem.xsd";

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

        // Return all XSI types which are supported by this extension
        Set<String> result = new HashSet<>();
        result.addAll(this.getSupportedFindingXSITypes());
        result.addAll(this.getSupportedTargetXSITypes());

        return result;

    }

    @Override
    public Set<String> getSupportedXSITypes(SchemaElementConstants schemaElementConstant) {

        Set<String> result = null;
        if (SchemaElementConstants.TARGET == schemaElementConstant) {
            result = this.getSupportedTargetXSITypes();
        } else if (SchemaElementConstants.FINDING == schemaElementConstant) {
            result = this.getSupportedFindingXSITypes();
        }

        return result;

    }

    private Set<String> getSupportedTargetXSITypes() {

        Set<String> result = new HashSet<>();
        result.add(SolarSystemTargetComet.XML_XSI_TYPE_VALUE);
        result.add(SolarSystemTargetMinorPlanet.XML_XSI_TYPE_VALUE);
        result.add(SolarSystemTargetMoon.XML_XSI_TYPE_VALUE);
        result.add(SolarSystemTargetSun.XML_XSI_TYPE_VALUE);
        result.add(SolarSystemTargetPlanet.XML_XSI_TYPE_VALUE);

        return result;

    }

    private Set<String> getSupportedFindingXSITypes() {

        return new HashSet<>();

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

        this.panels.put(SchemaElementConstants.FINDING, findingPanels);

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

        this.panels.put(SchemaElementConstants.TARGET, targetPanels);

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

        this.dialogs.put(SchemaElementConstants.TARGET, targetDialogs);

    }

    @Override
    public PopupMenuExtension getPopupMenu() {

        return null;

    }

    @Override
    public AbstractPanel getFindingPanelForXSIType(String xsiType, IFinding finding, ISession session,
            boolean editable) {
        // TODO Auto-generated method stub
        return null;
    }

   
    @Override
    public void setContext(IExtensionContext context) {
        this.extensionContext = context;

    }

    @Override
    public AbstractPanel getTargetPanelForXSIType(String xsiType, ITarget target, boolean editable) {
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

}
