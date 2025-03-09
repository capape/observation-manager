/*
 * ====================================================================
 * /catalog/CatalogLoader.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.catalog;

import de.lehmannet.om.ITarget;
import de.lehmannet.om.ObservationManagerContext;
import de.lehmannet.om.ui.extension.IExtension;
import de.lehmannet.om.ui.i18n.TextManager;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.navigation.observation.utils.InstallDir;
import de.lehmannet.om.ui.util.UserInterfaceHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.semver4j.Semver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CatalogLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(CatalogLoader.class);

    

    private static final String CATALOG_DIR = "catalog";

    // Key: Catalog name (String)
    // Value: Catalog (ICatalog)
    private final Map<String, ICatalog> catalogMap = new HashMap<>();

    // Key: Extension name (String)
    // Value: Extension version (Semver string)
    private final Map<String, String> knownExtensions = new HashMap<>();

    private final UserInterfaceHelper uiHelper;
    private final List<IExtension> extensions;
    private final TextManager textManager;
    private final InstallDir installDir;

    public CatalogLoader(ObservationManagerContext context, UserInterfaceHelper uiHelper, List<IExtension> extensions) {

        
        this.uiHelper = uiHelper;
        this.installDir = context.getInstallDir();
        this.textManager = context.getTextManager();
        this.extensions = extensions;

        this.loadCatalogues();
    }

    public String[] getCatalogNames() {

        return (String[]) this.catalogMap.keySet().toArray(new String[] {});
    }

    public String[] getListableCatalogNames() {

        Iterator<String> iterator = this.catalogMap.keySet().iterator();
        ICatalog cat = null;
        String currentKey = null;
        List<String> result = new ArrayList<>();
        while (iterator.hasNext()) {
            currentKey = iterator.next();
            cat = this.catalogMap.get(currentKey);
            if (cat instanceof IListableCatalog) {
                result.add(currentKey);
            }
        }

        return (String[]) result.toArray(new String[] {});
    }

    public ITarget getTarget(String catalogName, String objectID) {

        ICatalog catalog = this.getCatalog(catalogName);

        if (catalog != null) {
            return catalog.getTarget(objectID);
        }

        return null;
    }

    public ICatalog getCatalog(String catalogName) {

        if (!this.catalogMap.containsKey(catalogName)) {
            if (catalogName != null) { // Search for abbreviation
                Iterator<ICatalog> catIterator = this.catalogMap.values().iterator();
                ICatalog current = null;
                while (catIterator.hasNext()) {
                    current = catIterator.next();
                    if (catalogName.equals(current.getAbbreviation())) {
                        return current;
                    }
                }
            } else {
                return null;
            }
        }

        return (ICatalog) this.catalogMap.get(catalogName);
    }

    private void loadCatalogues() {

        File catalogDir = new File(this.installDir.getPathForFolder(CATALOG_DIR));
        if (!catalogDir.exists()) {
            boolean makeCatDir = catalogDir.mkdir();
            if (!makeCatDir) {
                LOGGER.error("Catalog directory not found: {}", catalogDir);
                this.uiHelper.showWarning(this.textManager.getString("catalogLoader.warning.noCatalogDir"));
                return;
            }
        }

        Iterator<IExtension> iterator = this.extensions.iterator();
        IExtension current = null;
        while (iterator.hasNext()) {
            current = (IExtension) iterator.next();

            // Check if extension is already known, or must be updated
            if (this.knownExtensions.containsKey(current.getName())) {
                // Is extension already known (we're in an update run)
                // Extension is known...check if version is equal

                Semver knownVersion = Semver.parse(this.knownExtensions.get(current.getName()));
                Semver version = Semver.parse(current.getVersion());
                if (knownVersion.isGreaterThan(version)) {
                    continue; // Extension in that version is already known to us
                }
            }

            this.catalogMap.putAll(loadCatalogsAsMap(current, catalogDir));

            // Add current extension to list of known extesions
            this.knownExtensions.put(current.getName(), current.getVersion());
        }
    }

    public boolean isFromCatalog(String name) {

        for (String catalog : getListableCatalogNames()) {

            if (getTarget(catalog, name) != null) {
                return true;
            }
        }
        return false;
    }

    private Map<String, ICatalog> loadCatalogsAsMap(IExtension extension, File catalogDir) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Catalog loading start: {}", extension.getName());
        }

        // On huge catalogs, this may take some time:
        ICatalog[] currentCatalogs = extension.getCatalogs(catalogDir);

        var catalogs = new ConcurrentHashMap<String, ICatalog>();

        // All catalogs are loaded, so add them to map
        if (currentCatalogs != null) {
            for (ICatalog currentCatalog : currentCatalogs) {
                catalogs.put(currentCatalog.getName(), currentCatalog);
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Catalog loading done: {}", extension.getName());
        }
        return catalogs;
    }
}
