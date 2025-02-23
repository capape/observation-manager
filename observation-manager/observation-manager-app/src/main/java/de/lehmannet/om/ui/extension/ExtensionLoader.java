/*
 * ====================================================================
 * /extension/ExtensionLoader.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension;

import de.lehmannet.om.extension.skychart.SkyChartClient;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.catalog.CatalogLoader;
import de.lehmannet.om.ui.extension.deepSky.DeepSkyExtension;
import de.lehmannet.om.ui.extension.imaging.ImagerExtension;
import de.lehmannet.om.ui.extension.solarSystem.SolarSystemExtension;
import de.lehmannet.om.ui.extension.variableStars.VariableStarsExtension;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.navigation.observation.utils.InstallDir;
import de.lehmannet.om.ui.preferences.PreferencesPanel;
import de.lehmannet.om.util.ConfigLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;
import javax.swing.JMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtensionLoader {

    // ---------
    // Constants ---------------------------------------------------------
    // ---------
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtensionLoader.class);

    // ------------------
    // Instance Variables ------------------------------------------------
    // ------------------

    private final List<IExtension> extensions = new LinkedList<>();

    private CatalogLoader catalogLoader = null;

    private SchemaUILoader schemaUILoader = null;

    private PopupMenuExtension[] cachedPopupMenus = null;

    private JMenu[] cachedMenus = null;

    private final InstallDir installDir;

    @Deprecated
    private final ObservationManager om;

    private final ObservationManagerModel model;

    private final IExtensionContext context;
    private final ExternalExtensionLoader loader;
    // ------------
    // Constructors ------------------------------------------------------
    // ------------

    public ExtensionLoader(ObservationManager om, ObservationManagerModel model, InstallDir installDir) {

        this.installDir = installDir;
        this.om = om;
        this.model = model;
        this.context = new ExtensionContext.Builder()
                .configuration(this.om.getConfiguration())
                .installDir(this.installDir)
                .uiHelper(this.om.getUiHelper())
                .model(this.model)
                .build();

        // TODO inject
        this.loader = new ExternalExtensionLoader(context);

        this.loadExtensions();

        this.catalogLoader = new CatalogLoader(om, this.extensions);
        this.schemaUILoader = new SchemaUILoader(om, this.extensions);
    }

    // --------------
    // Public Methods ----------------------------------------------------
    // --------------

    public void addExternalExtension(ZipFile fileExtension) {

        IExtension extension = loader.addExtension(fileExtension);

        final List<String> extensionNames =
                this.extensions.stream().map(IExtension::getName).collect(Collectors.toList());
        if (extensionNames.contains(extension.getName())) {
            LOGGER.info("There is already an extension called: {}", extension.getName());

        } else {
            LOGGER.info("Successfully loaded extension: {} ", extension.getName());
            this.extensions.add(extension);
            // Clear Menu and PopupMenu Caches
            this.cachedMenus = null;
            this.cachedPopupMenus = null;
        }
    }

    public List<IExtension> getExtensions() {

        // Create new list to force user to call our addExtension methods
        // when installing a new extension
        List<IExtension> result = new ArrayList<>();
        Iterator<IExtension> iterator = this.extensions.iterator();
        while (iterator.hasNext()) {
            IExtension current = iterator.next();
            // Do not add generic extension
            if (!GenericExtension.NAME.equals(current.getName())) {
                result.add(current);
            }
        }

        return result;
    }

    public void reloadLanguage() {

        for (IExtension extension : this.extensions) {
            extension.reloadLanguage();
        }

        this.cachedMenus = null;
        this.cachedPopupMenus = null;
    }

    public CatalogLoader getCatalogLoader() {

        return this.catalogLoader;
    }

    public SchemaUILoader getSchemaUILoader() {

        return this.schemaUILoader;
    }

    public JMenu[] getMenus() {

        if (this.cachedMenus == null) {

            List<JMenu> result = new ArrayList<>();

            for (IExtension current : this.extensions) {
                if (current.getMenu() != null) {
                    result.add(current.getMenu());
                }
            }

            this.cachedMenus = result.toArray(new JMenu[] {});
        }

        return this.cachedMenus;
    }

    public PopupMenuExtension[] getPopupMenus() {

        if (this.cachedPopupMenus == null) {

            List<PopupMenuExtension> result = new ArrayList<>();

            for (IExtension current : this.extensions) {
                if (current.getPopupMenu() != null) {
                    result.add(current.getPopupMenu());
                }
            }

            this.cachedPopupMenus = result.toArray(new PopupMenuExtension[] {});
        }

        return this.cachedPopupMenus;
    }

    public PreferencesPanel[] getPreferencesTabs() {

        List<PreferencesPanel> result = new ArrayList<>();

        for (IExtension current : this.extensions) {
            if (current.getPreferencesPanel() != null) {
                result.add(current.getPreferencesPanel());
            }
        }

        return result.toArray(new PreferencesPanel[] {});
    }

    // ---------------
    // Private methods ---------------------------------------------------
    // ---------------

    private void loadExtensions() {

        this.addInternalExtension(new GenericExtension(context));
        this.addInternalExtension(new SkyChartClient(context));
        this.addInternalExtension(new DeepSkyExtension(context));
        this.addInternalExtension(new ImagerExtension(context));
        this.addInternalExtension(new SolarSystemExtension(context));
        this.addInternalExtension(new VariableStarsExtension(context));
    }

    private void addInternalExtension(IExtension extension) {

        this.loadExtensionTypes(extension);
    }

    private void loadExtensionTypes(IExtension extension) {
        try {
            extension.getExtensionTypes().stream().forEach(type -> ConfigLoader.loadInternalExtension(type));
            this.extensions.add(extension);
            this.logSupported(extension);

        } catch (Throwable e) {
            LOGGER.error("Cannot load types for {}", extension.getName());
        }
    }

    private void logSupported(IExtension extension) {
        if (LOGGER.isDebugEnabled()) {
            extension
                    .getAllSupportedXSITypes()
                    .forEach(type -> LOGGER.debug("Extension: {} supports type: {}", extension.getName(), type));
        }
    }
}
