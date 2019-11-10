package de.lehmannet.om.ui.extension.solarSystem;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.JMenu;

import de.lehmannet.om.extension.solarSystem.SolarSystemTargetComet;
import de.lehmannet.om.extension.solarSystem.SolarSystemTargetMinorPlanet;
import de.lehmannet.om.extension.solarSystem.SolarSystemTargetMoon;
import de.lehmannet.om.extension.solarSystem.SolarSystemTargetPlanet;
import de.lehmannet.om.extension.solarSystem.SolarSystemTargetSun;
import de.lehmannet.om.ui.catalog.ICatalog;
import de.lehmannet.om.ui.catalog.IListableCatalog;
import de.lehmannet.om.ui.extension.AbstractExtension;
import de.lehmannet.om.ui.extension.PopupMenuExtension;
import de.lehmannet.om.ui.extension.solarSystem.catalog.SolarSystemCatalog;
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

    public SolarSystemExtension() {

        super.OAL_EXTENSION_FILE = "./openastronomylog21/extensions/ext_SolarSystem.xsd";

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
    public Set getAllSupportedXSITypes() {

        // Return all XSI types which are supported by this extension
        HashSet result = new HashSet();
        result.addAll(this.getSupportedFindingXSITypes());
        result.addAll(this.getSupportedTargetXSITypes());

        return result;

    }

    @Override
    public Set getSupportedXSITypes(int schemaElementConstant) {

        Set result = null;
        if (SchemaElementConstants.TARGET == schemaElementConstant) {
            result = this.getSupportedTargetXSITypes();
        } else if (SchemaElementConstants.FINDING == schemaElementConstant) {
            result = this.getSupportedFindingXSITypes();
        }

        return result;

    }

    private Set getSupportedTargetXSITypes() {

        HashSet result = new HashSet();
        result.add(SolarSystemTargetComet.XML_XSI_TYPE_VALUE);
        result.add(SolarSystemTargetMinorPlanet.XML_XSI_TYPE_VALUE);
        result.add(SolarSystemTargetMoon.XML_XSI_TYPE_VALUE);
        result.add(SolarSystemTargetSun.XML_XSI_TYPE_VALUE);
        result.add(SolarSystemTargetPlanet.XML_XSI_TYPE_VALUE);

        return result;

    }

    private Set getSupportedFindingXSITypes() {

        HashSet result = new HashSet();

        return result;

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
        if ((xsiType.equals("oal:MoonTargetType")) || (xsiType.equals("oal:SunTargetType"))
                || (xsiType.equals("oal:PlanetTargetType"))) {
            return false;
        } else {
            return true;
        }

    }

    private void initFindingPanels() {

        HashMap findingPanels = new HashMap();

        findingPanels.put(SolarSystemTargetComet.XML_XSI_TYPE_VALUE, "de.lehmannet.om.ui.panel.GenericFindingPanel");
        findingPanels.put(SolarSystemTargetMinorPlanet.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.panel.GenericFindingPanel");
        findingPanels.put(SolarSystemTargetMoon.XML_XSI_TYPE_VALUE, "de.lehmannet.om.ui.panel.GenericFindingPanel");
        findingPanels.put(SolarSystemTargetSun.XML_XSI_TYPE_VALUE, "de.lehmannet.om.ui.panel.GenericFindingPanel");
        findingPanels.put(SolarSystemTargetPlanet.XML_XSI_TYPE_VALUE, "de.lehmannet.om.ui.panel.GenericFindingPanel");

        super.panels.put(new Integer(SchemaElementConstants.FINDING), findingPanels);

    }

    private void initTargetPanels() {

        HashMap targetPanels = new HashMap();

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

        super.panels.put(new Integer(SchemaElementConstants.TARGET), targetPanels);

    }

    private void initTargetDialogs() {

        HashMap targetDialogs = new HashMap();

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

        super.dialogs.put(new Integer(SchemaElementConstants.TARGET), targetDialogs);

    }

    @Override
    public PopupMenuExtension getPopupMenu() {

        return null;

    }

}
