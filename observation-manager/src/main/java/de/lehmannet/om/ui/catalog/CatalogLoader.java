/* ====================================================================
 * /catalog/CatalogLoader.java
 *
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.catalog;

import java.awt.BorderLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.JProgressBar;

import de.lehmannet.om.ITarget;
import de.lehmannet.om.ui.dialog.OMDialog;
import de.lehmannet.om.ui.extension.IExtension;
import de.lehmannet.om.ui.navigation.ObservationManager;

public class CatalogLoader {

    private final PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
            .getBundle("ObservationManager", Locale.getDefault());

    private static final String CATALOG_DIR = "catalog";

    private ObservationManager observationManager = null;
    private List<IExtension> extensions = null;

    // Key: Catalog name (String)
    // Value: Catalog (ICatalog)
    private final Map<String, ICatalog> catalogMap = new HashMap<>();

    // Key: Extension name (String)
    // Value: Extension version (Float)
    private final Map<String, Float> knownExtensions = new HashMap<>();

    // Used to load catalogs in parallel
    private final ThreadGroup loadCatalogs = new ThreadGroup("Load all catalogs");

    public CatalogLoader(ObservationManager om, List<IExtension> extensions) {

        this.observationManager = om;
        this.extensions = extensions;

        this.loadCatalogues();

    }

    public String[] getCatalogNames() {

        this.waitForCatalogLoaders();

        return (String[]) this.catalogMap.keySet().toArray(new String[] {});

    }

    public String[] getListableCatalogNames() {

        this.waitForCatalogLoaders();

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

        this.waitForCatalogLoaders();

        ICatalog catalog = this.getCatalog(catalogName);

        if (catalog != null) {
            return catalog.getTarget(objectID);
        }

        return null;

    }

    public ICatalog getCatalog(String catalogName) {

        this.waitForCatalogLoaders();

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

    private void loadCatalogues() {

        File catalogDir = new File(
                this.observationManager.getInstallDir().getPathForFolder(CatalogLoader.CATALOG_DIR));
        if (!catalogDir.exists()) {
            boolean makeCatDir = catalogDir.mkdir();
            if (!makeCatDir) {
                System.err.println("Catalog directory not found: " + catalogDir);
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
                float knownVersion = (Float) this.knownExtensions.get(current.getName());
                float version = current.getVersion();
                if (knownVersion >= version) {
                    continue; // Extension in that version is already known to us
                }
            }

            // Create a thread for all catalogs, where the catalogs will be loaded in.
            // As catalogs are loaded during startup and loading of catalogs can take some
            // time,
            // this should increase startup times
            CatalogLoaderRunnable runnable = new CatalogLoaderRunnable(current, this.catalogMap, catalogDir,
                    this.observationManager.isDebug());
            Thread thread = new Thread(this.loadCatalogs, runnable, "Load catalog " + current.getName());
            catalogs.add(thread);

            // Add current extension to list of known extesions
            this.knownExtensions.put(current.getName(), current.getVersion());
        }

        // Start loading all catalogs
        for (Object catalog : catalogs) {
            ((Thread) catalog).start();
        }

    }

}

class CatalogLoaderRunnable implements Runnable {

    private IExtension extension = null;
    private Map<String,ICatalog> resultMap = null;
    private File catalogDir = null;
    private boolean debug = false;

    public CatalogLoaderRunnable(IExtension extension, Map<String,ICatalog> resultMap, File catalogDir, boolean debug) {

        this.extension = extension;
        this.resultMap = resultMap;
        this.catalogDir = catalogDir;
        this.debug = debug;

    }

    @Override
    public void run() {

        if (debug) {
            System.out.println("Catalog loading start: " + this.extension.getName() + " " + System.currentTimeMillis());
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

        if (debug) {
            System.out.println("Catalog loading done: " + this.extension.getName() + " " + System.currentTimeMillis());
        }

    }

}

class WaitPopup extends OMDialog {

    private static final long serialVersionUID = 4130578764471183037L;

    private ThreadGroup threadGroup = null;

    public WaitPopup(ThreadGroup threadGroup, ObservationManager om) {

        super(om);
        super.setLocationRelativeTo(om);
        PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle.getBundle("ObservationManager",
                Locale.getDefault());
        super.setTitle(bundle.getString("catalogLoader.info.waitOnLoaders"));

        this.threadGroup = threadGroup;

        super.getContentPane().setLayout(new BorderLayout());

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setIndeterminate(true);

        super.getContentPane().add(progressBar, BorderLayout.CENTER);

        this.setSize(WaitPopup.serialVersionUID, 250, 60);
        // this.pack();

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
                System.err.println("Interrupted while waiting for ThreadGroup.\n" + ie);
            } catch (IllegalMonitorStateException imse) {
                System.err.println("Ingnoring \n " + imse);
            }
        }
        this.dispose();

    }

}