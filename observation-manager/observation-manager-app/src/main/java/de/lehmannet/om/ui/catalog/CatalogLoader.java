/*
 * ====================================================================
 * /catalog/CatalogLoader.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.catalog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.lehmannet.om.ITarget;
import de.lehmannet.om.ui.extension.IExtension;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.update.Version;
import de.lehmannet.om.ui.util.LocaleToolsFactory;

public class CatalogLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(CatalogLoader.class);

    private final ResourceBundle bundle;

    private static final String CATALOG_DIR = "catalog";

    private ObservationManager observationManager = null;
    private List<IExtension> extensions = null;

    // Key: Catalog name (String)
    // Value: Catalog (ICatalog)
    private final Map<String, ICatalog> catalogMap = new HashMap<>();

    // Key: Extension name (String)
    // Value: Extension version (Float)
    private final Map<String, Version> knownExtensions = new HashMap<>();

    // Used to load catalogs in parallel
    private final ThreadGroup loadCatalogs = new ThreadGroup("Load all catalogs");

    public CatalogLoader(ObservationManager om, List<IExtension> extensions) {

        this.bundle = LocaleToolsFactory.appInstance().getBundle("ObservationManager", Locale.getDefault());
        this.observationManager = om;
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

    /*
    public void update() {

        this.waitForCatalogLoaders();

        this.loadCatalogues();

    }



    public boolean isLoading() {

        return this.loadCatalogs.activeCount() > 0;

    }

    private void waitForCatalogLoaders() {

        // Must make sure all catalog loader threads have finished their work
        if (this.isLoading()) {
            new WaitPopup(this.loadCatalogs, this.observationManager);
        }

    }
    */

    private void loadCatalogues() {

        File catalogDir = new File(this.observationManager.getInstallDir().getPathForFolder(CatalogLoader.CATALOG_DIR));
        if (!catalogDir.exists()) {
            boolean makeCatDir = catalogDir.mkdir();
            if (!makeCatDir) {
                LOGGER.error("Catalog directory not found: {}", catalogDir);
                this.observationManager.createWarning(this.bundle.getString("catalogLoader.warning.noCatalogDir"));
                return;
            }
        }

        Iterator<IExtension> iterator = this.extensions.iterator();
        IExtension current = null;
        List<Thread> catalogs = new ArrayList<>();
        while (iterator.hasNext()) {
            current = (IExtension) iterator.next();

            // Check if extension is already known, or must be updated
            if (this.knownExtensions.containsKey(current.getName())) { // Is extension already known (we're in an update
                                                                       // run)
                // Extension is known...check if version is equal

                Version knownVersion = this.knownExtensions.get(current.getName());
                Version version = Version.createVersion(current.getVersion());
                if (knownVersion.compareTo(version) >= 0) {
                    continue; // Extension in that version is already known to us
                }
            }

            this.catalogMap.putAll(loadCatalogsAsMap(current, catalogDir));

            // Add current extension to list of known extesions
            this.knownExtensions.put(current.getName(), Version.createVersion(current.getVersion()));
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

/*
class CatalogLoaderRunnable implements Runnable {

    private IExtension extension = null;
    private Map<String, ICatalog> resultMap = null;
    private File catalogDir = null;

    private static final Logger LOGGER = LoggerFactory.getLogger(CatalogLoaderRunnable.class);

    public CatalogLoaderRunnable(IExtension extension, Map<String, ICatalog> resultMap, File catalogDir) {

        this.extension = extension;
        this.resultMap = resultMap;
        this.catalogDir = catalogDir;

    }

    @Override
    public void run() {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Catalog loading start: {}", this.extension.getName());
        }

        // On huge catalogs, this may take some time:
        ICatalog[] currentCatalogs = extension.getCatalogs(this.catalogDir);

        // All catalogs are loaded, so add them to map
        if (currentCatalogs != null) {
            synchronized (this.resultMap) { // Make sure access to map is synchronized
                for (ICatalog currentCatalog : currentCatalogs) {
                    this.resultMap.put(currentCatalog.getName(), currentCatalog);
                }
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Catalog loading done: {}", this.extension.getName());
        }

    }

}
*/
/*

class WaitPopup extends OMDialog {

    private static final Logger LOGGER = LoggerFactory.getLogger(WaitPopup.class);

    private static final long serialVersionUID = 4130578764471183037L;

    private ThreadGroup threadGroup = null;

    public WaitPopup(ThreadGroup threadGroup, ObservationManager om) {

        super(om);
        this.setLocationRelativeTo(om);
        ResourceBundle bundle = LocaleToolsFactory.appInstance().getBundle("ObservationManager", Locale.getDefault());
        this.setTitle(bundle.getString("catalogLoader.info.waitOnLoaders"));

        this.threadGroup = threadGroup;

        this.getContentPane().setLayout(new BorderLayout());

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setIndeterminate(true);

        this.getContentPane().add(progressBar, BorderLayout.CENTER);

        this.setSize(WaitPopup.serialVersionUID, 250, 60);

        Runnable wait = WaitPopup.this::waitForCatalogLoaders;

        Thread waitThread = new Thread(wait, "ProjectLoader: WaitPopup");
        waitThread.start();

        this.setVisible(true);

    }

    private void waitForCatalogLoaders() {

        while (this.threadGroup.activeCount() > 0) {
            try {
                this.threadGroup.wait(300);
            } catch (InterruptedException ie) {
                LOGGER.error("Interrupted while waiting for ThreadGroup.", ie);
            } catch (IllegalMonitorStateException imse) {
                LOGGER.error("Ingnoring", imse);
            }
        }
        this.dispose();

    }
*/
