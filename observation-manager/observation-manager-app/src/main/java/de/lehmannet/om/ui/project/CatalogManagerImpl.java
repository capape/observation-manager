package de.lehmannet.om.ui.project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.extension.ExtensionLoader;
import de.lehmannet.om.ui.navigation.observation.utils.InstallDir;
import de.lehmannet.om.ui.util.UserInterfaceHelper;

public class CatalogManagerImpl implements CatalogManager {

    private final ObservationManagerModel model;
    private final InstallDir installDir;
    private final UserInterfaceHelper uiHelper;
    private final ExtensionLoader extensionLoader;
    private ProjectLoader projectLoader;

    public CatalogManagerImpl(final ObservationManagerModel model, InstallDir installDir,
            ExtensionLoader extensionLoader, UserInterfaceHelper uiHelper) {
        this.model = model;
        this.installDir = installDir;
        this.uiHelper = uiHelper;
        this.extensionLoader = extensionLoader;
        this.loadProjectFiles();
    }

    private Thread waitForCatalogLoaderThread;

    private static final Logger LOGGER = LoggerFactory.getLogger(CatalogManagerImpl.class);

    @Override
    public ProjectCatalog[] getProjects() {

        // Wait for ProjectLoader to finish
        if (this.waitForCatalogLoaderThread.isAlive()) {
            try {
                this.waitForCatalogLoaderThread.join();
            } catch (final InterruptedException ie) {
                System.err.println(
                        "Got interrupted while waiting for catalog loader...List of projects will be empty. Please try again.");
                return null;
            }
        }

        return this.projectLoader.getProjects();

    }

    public void loadProjectFiles() {

        // Create an own thread that waits for the catalog loader
        // to finish. Only if all catalogs are loaded the project loader
        // might start in the background

        class WaitForCatalogLoader implements Runnable {

            private CatalogManagerImpl catalogManager;

            WaitForCatalogLoader(final CatalogManagerImpl catManager) {
                this.catalogManager = catManager;
            }

            @Override
            public void run() {

                while (this.catalogManager.projectLoader == null) {
                    try {
                        if (!this.catalogManager.extensionLoader.getCatalogLoader().isLoading()) {

                            LOGGER.debug("Catalog loading done. Start project loading in background...");
                            // Initialite ProjectLoader and start loading projects
                            this.catalogManager.projectLoader = new ProjectLoader(CatalogManagerImpl.this.model,
                                    this.catalogManager.extensionLoader.getCatalogLoader(),
                                    CatalogManagerImpl.this.installDir, CatalogManagerImpl.this.uiHelper);
                        } else {
                            this.wait(300);
                        }
                    } catch (final InterruptedException ie) {
                        System.err.println("Interrupted while waiting for Catalog Loader to finish.\n" + ie);
                    } catch (final IllegalMonitorStateException imse) {
                        // Ignore this
                    }
                }

            }

        }

        this.waitForCatalogLoaderThread = new Thread(new WaitForCatalogLoader(this),
                "Waiting for Catalog Loader to finish");
        waitForCatalogLoaderThread.start();

    }
}