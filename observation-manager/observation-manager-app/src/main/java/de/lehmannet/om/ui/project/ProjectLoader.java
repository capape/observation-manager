/* ====================================================================
 * /project/CatalogLoader.java
 *
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.lehmannet.om.GenericTarget;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.catalog.CatalogLoader;
import de.lehmannet.om.ui.navigation.observation.utils.InstallDir;
import de.lehmannet.om.ui.panel.AbstractSearchPanel;
import de.lehmannet.om.ui.util.UserInterfaceHelper;

public class ProjectLoader {

    final PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle.getBundle("ObservationManager",
            Locale.getDefault());

    private static final String PROJECTS_DIR = "projects";

    private final ObservationManagerModel model;
    private final CatalogLoader catalogLoader;
    private final InstallDir installDir;
    private final UserInterfaceHelper uiHelper;

    private final List<ProjectCatalog> projectList = new ArrayList<>();

    // Used to load projects in parallel
    private final ThreadGroup loadProjects = new ThreadGroup("Load all projects");

    public ProjectLoader(ObservationManagerModel model, CatalogLoader catalogLoader, InstallDir installDir,
            UserInterfaceHelper uiHelper) {

        this.model = model;
        this.catalogLoader = catalogLoader;
        this.installDir = installDir;
        this.uiHelper = uiHelper;

        this.loadProjects();

    }

    public ProjectCatalog[] getProjects() {

        this.waitForProjectsLoaders();

        return (ProjectCatalog[]) projectList.toArray(new ProjectCatalog[] {});

    }

    private void waitForProjectsLoaders() {

        // Must make sure all project loader threads have finished their work
        if (this.loadProjects.activeCount() > 0) {

            this.uiHelper.createWaitPopUp(bundle.getString("catalogLoader.info.waitOnLoaders"), this.loadProjects);

        }

    }

    private void loadProjects() {

        File path = new File(this.installDir.getPathForFolder(ProjectLoader.PROJECTS_DIR));
        if (!path.exists()) {
            return;
        }

        // Get all project files
        String[] projects = path.list((dir, name) -> {

            File file = new File(dir.getAbsolutePath() + File.separator + name);
            return file.getName().endsWith(".omp") && !"CVS".equals(file.getName()); // For developers ;-)

        });

        // No project files found
        if ((projects == null) || (projects.length == 0)) {
            return;
        }

        // Load all targets, created by observer
        List<ITarget> userTargets = this.loadUserTargets();

        // Create a thread for all projects, where the projects will be loaded in.
        // As projects are loaded during startup and loading of projects can take some
        // time,
        // this should increase startup times
        // It must be ensured that catalogs are loaded completely before the projects,
        // as projects
        // refer to catalogs
        List<Thread> projectThreads = new ArrayList<>();
        File projectFile = null;
        for (String project : projects) {
            projectFile = new File(path.getAbsolutePath() + File.separator + project);
            ProjectLoaderRunnable runnable = new ProjectLoaderRunnable(this.catalogLoader, this.projectList,
                    userTargets, projectFile);
            Thread thread = new Thread(this.loadProjects, runnable, "Load project " + project);
            projectThreads.add(thread);
        }

        // Start loading all projects
        for (Object projectThread : projectThreads) {
            ((Thread) projectThread).start();
        }

    }

    private List<ITarget> loadUserTargets() {

        List<ITarget> userTargets = new ArrayList<>();

        ITarget[] targets = this.model.getTargets();
        for (ITarget target : targets) {
            if (target.getObserver() != null) {
                userTargets.add(target);
            }
        }

        return userTargets;

    }

}

class ProjectLoaderRunnable implements Runnable {

    private List<ProjectCatalog> projectList = null;
    private File projectFile = null;
    private CatalogLoader catalogLoader = null;
    private List<ITarget> userTargets = null;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectLoaderRunnable.class);

    public ProjectLoaderRunnable(CatalogLoader catalogLoader, List<ProjectCatalog> projectList,
            List<ITarget> userTargets, File projectFile) {

        this.catalogLoader = catalogLoader;
        this.projectList = projectList;
        this.projectFile = projectFile;
        this.userTargets = userTargets;

    }

    @Override
    public void run() {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Project loading start: {} {}", this.projectFile.getName(), System.currentTimeMillis());
        }

        ProjectCatalog pc = this.loadProjectCatalog(this.projectFile);

        // Project is loaded, so add it to map
        if (pc != null) {
            synchronized (this.projectList) { // Make sure access to map is synchronized
                this.projectList.add(pc);
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Project loading done: {} {}", this.projectFile.getName(), System.currentTimeMillis());
        }

    }

    private ITarget searchForTarget(String line) {

        final String USER_KEY = "USER";

        // Extract catalogName and targetName
        String catalogName = null;
        if (line.contains(",")) { // Check if catalog name was given at all
            catalogName = line.substring(0, line.indexOf(","));
        }
        String targetName = line.substring(line.indexOf(",") + 1);

        // Check whether line was formated correctly
        if ("".equals(targetName.trim())) {
            return null;
        }

        // Handle user/observer objects (objects not from a catalog)
        if ((catalogName != null) && (USER_KEY.equals(catalogName.toUpperCase()))) {
            String ut_name = null;
            String t_name = this.formatName(targetName);

            if (!this.userTargets.isEmpty()) { // There are userTargets at all
                ListIterator<ITarget> iterator = this.userTargets.listIterator();
                ITarget current = null;
                while (iterator.hasNext()) {
                    current = iterator.next();

                    ut_name = this.formatName(current.getName());
                    if (ut_name.equals(t_name)) { // Target names match
                        return current;
                    } else { // Try whether alias names match
                        if ((current.getAliasNames() != null) && (current.getAliasNames().length > 0)) {
                            String[] aNames = current.getAliasNames();
                            for (String aName : aNames) {
                                ut_name = this.formatName(aName);
                                if (ut_name.equals(t_name)) { // Alias name matches
                                    return current;
                                }
                            }
                            return null; // No need to access catalogs directly or via search as this is a user target
                        } else {
                            return null; // No need to access catalogs directly or via search as this is a user target
                        }
                    }
                }
                return null; // No need to access catalogs directly or via search as this is a user target
            } else {
                return null; // No need to access catalogs directly or via search as this is a user target
            }
        }

        // Access target directly (works only if we've a catalog name)
        ISchemaElement target = null;
        if (catalogName != null) {
            target = catalogLoader.getTarget(catalogName, this.formatName(targetName));
        }

        if (target != null) { // Found target via the direct access
            return (ITarget) target;
        }

        // Target cannot be accessed...try to search for it (in all catalogs)

        // Get all catalog names
        String[] catalogNames = catalogLoader.getCatalogNames();
        for (String name : catalogNames) {
            // Search via search panel as searching might be optimized
            // and it'll include alias names
            AbstractSearchPanel searchPanel = catalogLoader.getCatalog(name).getSearchPanel();
            if (searchPanel != null) {
                searchPanel.search(targetName);
                target = searchPanel.getSearchResult();
            }

            if (target != null) {
                return (ITarget) target; // We found something!
            }
        }

        return null; // Given targetName couldn't be found in any catalog

    }

    private String formatName(String name) {

        name = name.trim();
        name = name.replaceAll(" ", "");
        name = name.toUpperCase();

        return name;

    }

    private ProjectCatalog loadProjectCatalog(File projectFile) {

        final String PROJECT_NAME_KEY = "ProjectName";

        if (!projectFile.exists()) {
            return null;
        }

        // Try to read file line by line
        List<ITarget> targets = new ArrayList<>();
        ITarget target = null;
        String name = null;
        try {
            FileInputStream fis = new FileInputStream(projectFile);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(isr);

            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) { // Skip comment lines
                    continue;
                }
                if (line.startsWith(PROJECT_NAME_KEY)) { // We've found the project name
                    name = line.substring(line.indexOf("=") + 1);
                    continue;
                }

                // Search for target in all "real" catalogs
                target = this.searchForTarget(line);

                if (target == null) {
                    // Throw exception as target does not exist (neither in catalogs, nor in XML
                    // file)
                    // throw new TargetNotFoundException(projectFilepath, line);

                    // Create dummy target (instead of throwing an exception)
                    String projectName = (name == null) ? projectFile.getName() : name;
                    String targetName = line.substring(line.indexOf(",") + 1);
                    targets.add(new GenericTarget(targetName, projectName));
                } else {
                    targets.add(target);
                }

            }
        } catch (IOException ioe) {
            System.err.println("Cannot load project file: " + projectFile + "\n" + ioe);
        }

        if (targets.isEmpty()) { // Should never happen as exception should be thrown
            return null;
        }

        if (name == null) { // There was no explicit name set in the project file
            name = projectFile.getName(); // Use filename as project name
            name = name.substring(0, name.indexOf(".")); // Crop off file extension
        }

        // Create catalog

        return new ProjectCatalog(name, (ITarget[]) targets.toArray(new ITarget[] {}));

    }

}
