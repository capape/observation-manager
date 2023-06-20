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

    private static final Logger LOGGER = LoggerFactory.getLogger(CatalogManagerImpl.class);

    @Override
    public ProjectCatalog[] getProjects() {

        return this.projectLoader.getProjects();

    }

    public void loadProjectFiles() {

        while (this.projectLoader == null) {
            try {
                if (this.extensionLoader.getCatalogLoader().isLoading()) {
                    this.projectLoader = new ProjectLoader(CatalogManagerImpl.this.model,
                            this.extensionLoader.getCatalogLoader(), CatalogManagerImpl.this.installDir,
                            CatalogManagerImpl.this.uiHelper);
                } else {
                    this.wait(300);
                }
            } catch (final InterruptedException ie) {
                LOGGER.error("Interrupted while waiting for Catalog Loader to finish", ie);
            }
        }
    }
}