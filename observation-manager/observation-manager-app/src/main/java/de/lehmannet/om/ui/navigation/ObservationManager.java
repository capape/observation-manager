/* ====================================================================
 * /navigation/ObservationManager.java
 *
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.navigation;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.lehmannet.om.IEyepiece;
import de.lehmannet.om.IFilter;
import de.lehmannet.om.IImager;
import de.lehmannet.om.ILens;
import de.lehmannet.om.IObservation;
import de.lehmannet.om.IObserver;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.IScope;
import de.lehmannet.om.ISession;
import de.lehmannet.om.ISite;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.cache.UIDataCache;
import de.lehmannet.om.ui.dialog.AbstractDialog;
import de.lehmannet.om.ui.dialog.EyepieceDialog;
import de.lehmannet.om.ui.dialog.FilterDialog;
import de.lehmannet.om.ui.dialog.LensDialog;
import de.lehmannet.om.ui.dialog.OMDialog;
import de.lehmannet.om.ui.dialog.ObservationDialog;
import de.lehmannet.om.ui.dialog.ObserverDialog;
import de.lehmannet.om.ui.dialog.ProgressDialog;
import de.lehmannet.om.ui.dialog.ScopeDialog;
import de.lehmannet.om.ui.dialog.SessionDialog;
import de.lehmannet.om.ui.dialog.SiteDialog;
import de.lehmannet.om.ui.dialog.TableElementsDialog;
import de.lehmannet.om.ui.extension.ExtensionLoader;
import de.lehmannet.om.ui.i18n.TextManager;
import de.lehmannet.om.ui.image.ImageResolver;
import de.lehmannet.om.ui.navigation.observation.utils.InstallDir;
import de.lehmannet.om.ui.navigation.observation.utils.SystemInfo;
import de.lehmannet.om.ui.project.CatalogManager;
import de.lehmannet.om.ui.project.CatalogManagerImpl;
import de.lehmannet.om.ui.project.ProjectCatalog;
import de.lehmannet.om.ui.theme.ThemeManager;
import de.lehmannet.om.ui.theme.ThemeManagerImpl;
import de.lehmannet.om.ui.util.ConfigKey;
import de.lehmannet.om.ui.util.IConfiguration;
import de.lehmannet.om.ui.util.LoggerConfig;
// import de.lehmannet.om.ui.util.SplashScreen;
import de.lehmannet.om.ui.util.UserInterfaceHelper;
import de.lehmannet.om.ui.util.UserInterfaceHelperImpl;
import de.lehmannet.om.ui.util.Worker;
import de.lehmannet.om.util.DateManager;
import de.lehmannet.om.util.FloatUtil;
import de.lehmannet.om.util.SchemaElementConstants;

public class ObservationManager extends JFrame implements IObservationManagerJFrame {

    private static final long serialVersionUID = -9092637724048070172L;

    private final Logger LOGGER = LoggerFactory.getLogger(ObservationManager.class);

    // Working directory
    public static final String WORKING_DIR = ".observationManager";

    // ---------
    // Variables --------------------------------------------------------------
    // ---------
    private JSplitPane hSplitPane;
    private JSplitPane vSplitPane;

    private JMenuBar menuBar;

    private TableView table;
    private ItemView item;
    private TreeView tree;

    private boolean changed = false; // Indicates if changed where made after
                                     // load.

    private Boolean nightVisionOnStartup;
    // private Thread splash;

    private final InstallDir installDir;

    private final IConfiguration configuration;
    private final ObservationManagerModel model;

    final ExtensionLoader extLoader;

    private final ObservationManagerMenuFile menuFile;
    private final ObservationManagerMenuData menuData;
    private final ObservationManagerMenuExtras menuExtras;
    private final ObservationManagerMenuHelp menuHelp;
    private final ObservationManagerMenuExtensions menuExtensions;

    private final ImageResolver imageResolver;
    private final ThemeManager themeManager;
    private final TextManager textManager;
    private final TextManager versionTextManager;
    private final DateManager dateManager;

    private final ObservationManagerHtmlHelper htmlHelper;
    private final UserInterfaceHelper uiHelper;
    private final UIDataCache uiCache;

    public final InstallDir getInstallDir() {
        return this.installDir;
    }

    public final ObservationManagerHtmlHelper getHtmlHelper() {
        return this.htmlHelper;
    }

    public final UserInterfaceHelper getUiHelper() {
        return this.uiHelper;
    }

    public DateManager getDateManager() {
        return this.dateManager;
    }

    private final CatalogManager catalogManager;

    private ObservationManager(Builder builder) {

        this.installDir = builder.installDir;
        this.configuration = builder.configuration;
        this.imageResolver = builder.imageResolver;
        this.textManager = builder.textManager;
        this.versionTextManager = builder.versionTextManager;
        this.themeManager = new ThemeManagerImpl(this.configuration, this.textManager, this);
        this.model = builder.model;
        this.uiHelper = new UserInterfaceHelperImpl(this, textManager);
        this.nightVisionOnStartup = builder.nightVision;
        this.uiCache = builder.uiCache;
        this.dateManager = builder.dateManager;

        LOGGER.info("Observation Manager {} starting up...", this.getVersion());
        LOGGER.info("Java:\t {} {}  ", System.getProperty("java.vendor"), System.getProperty("java.version"));
        LOGGER.info("OS:\t {} ({}) {}", System.getProperty("os.name"), System.getProperty("os.arch"),
                System.getProperty("os.version"));

        LOGGER.debug("Launching extension loader ...");
        this.extLoader = new ExtensionLoader(this, this.model, this.installDir); // --> DEP VARIABLE STARS --> DIALOG

        LOGGER.debug("Start: {}", new Date());
        LOGGER.debug(SystemInfo.printMemoryUsage());

        LoggerConfig.initLogs();

        // Set title
        this.setTitle();

        LOGGER.debug("Generating menus...");

        this.catalogManager = new CatalogManagerImpl(this.model, this.installDir, this.extLoader, uiHelper);
        this.htmlHelper = new ObservationManagerHtmlHelper(uiHelper, this.textManager, this.configuration,
                this.installDir, this.model);
        this.menuFile = new ObservationManagerMenuFile(this.configuration, this.model, this.htmlHelper,
                this.imageResolver, this.textManager, uiHelper, this.installDir, this);
        this.menuData = new ObservationManagerMenuData(this.model, this.imageResolver, this.textManager, this,
                this.uiCache);
        this.menuExtras = new ObservationManagerMenuExtras(this.configuration, this.imageResolver, this.themeManager,
                this.textManager, uiHelper, this.model, this.installDir, this);
        this.menuHelp = new ObservationManagerMenuHelp(this.configuration, this.textManager, this);
        this.menuExtensions = new ObservationManagerMenuExtensions(this.configuration, this.extLoader,
                this.imageResolver, this.textManager, uiHelper, this);

        // Set icon
        this.setIconImage(new ImageIcon(this.installDir.getPathForFile("om_logo.png")).getImage());

        // Init menu and disable it during startup
        this.initMenuBar();
        this.enableMenus(false);

        // Set nightvision theme
        if (nightVisionOnStartup) {
            this.menuExtras.enableNightVisionTheme(true);
        }

        LOGGER.debug("Configure main view...");

        this.item = this.initItemView();
        this.table = this.initTableView();
        this.tree = this.initTreeView();

        this.initMain();

        // ****************************************************************
        // Only required for Auto. update, which is currently not supported
        //
        // Check on restart Update and perform required steps (of necessary)
        // this.performRestartUpdate();
        // ****************************************************************

        LOGGER.debug("Loading config files...");
        this.loadConfigFiles();

        LOGGER.debug("Refreshing view...");
        this.table.showObservations(null, null);
        this.tree.updateTree();

        LOGGER.debug("Checking for updates...");
        this.checkForUpdatesOnLoad();

        // If we should show the hints on startup, do so now...
        LOGGER.debug("Creating hints dialog...");
        if (this.configuration.getBooleanConfig(ConfigKey.CONFIG_HELP_HINTS_STARTUP, true)) {
            this.menuExtras.showDidYouKnow();
        }

        // Add shortcut key listener
        this.addShortcuts();

        // We're up an running, so enable menus now
        this.enableMenus(true);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Up and running: {} ", new Date());
            LOGGER.debug(SystemInfo.printMemoryUsage());
        }

    }

    private void loadConfigFiles() {
        // Load XML File on startup (if desired)
        final ObservationManagerFileLoader fileLoader = new ObservationManagerFileLoader(configuration, model);

        final Worker configLoader = new Worker() {
            Pair<String, Boolean> result;

            @Override
            public void run() {

                result = fileLoader.loadConfig().orElse(Pair.of("Not loaded", true));
                LOGGER.debug("Config: {},{}", result.getLeft(), result.getRight());

            }

            @Override
            public String getReturnMessage() {

                if (result.getRight()) {
                    LOGGER.debug("Configuration loaded ");
                    return "";
                }

                String msgError = ObservationManager.this.textManager.getString("error.loadXML");
                LOGGER.error("Configuration error: {} ", msgError);
                return msgError + " " + result.getLeft();

            }

            @Override
            public byte getReturnType() {

                if (result.getRight()) {
                    return Worker.RETURN_TYPE_OK;
                } else {
                    return Worker.RETURN_TYPE_ERROR;
                }

            }

        };
        final boolean load = this.configuration.getBooleanConfig(ConfigKey.CONFIG_OPENONSTARTUP);
        LOGGER.debug("Loading config at startup: {}", load);
        if (load) {
            new ProgressDialog(this, this.textManager.getString("progress.wait.title"),
                    this.textManager.getString("progress.wait.xml.load.info"), configLoader);
        }
    }

    private void checkForUpdatesOnLoad() {
        // Check for updates
        if (this.configuration.getBooleanConfig(ConfigKey.CONFIG_UPDATECHECK_STARTUP, false)) {
            this.menuExtras.checkUpdates();
        }
    }

    @Override
    protected void processWindowEvent(final WindowEvent e) {

        if (e.getID() == WindowEvent.WINDOW_CLOSING) {

            if (this.menuFile.exit(this.changed)) {
                this.processWindowEvent(e);
                this.dispose();
            }

        }

    }

    public void reloadLanguage() {

        // Load new bundle
        final String isoKey = this.configuration.getConfig(ConfigKey.CONFIG_UILANGUAGE,
                Locale.getDefault().getLanguage());
        this.textManager.useLanguage(isoKey);

        final Locale aLocale = new Locale.Builder().setLanguage(isoKey).build();
        Locale.setDefault(aLocale);

        // Reload title
        this.setTitle();

        // Remove old UI components
        this.hSplitPane.removeAll();
        this.vSplitPane.removeAll();
        this.getContentPane().removeAll();

        // Tell the extensions about the switch
        this.extLoader.reloadLanguage();

        // (Re-)init UI components (would be better to do this with
        // eventing...maybe in a later version :) )
        AbstractDialog.reloadLanguage();
        this.initMenuBar();
        this.item.reloadLanguage();
        this.item = this.initItemView();
        this.table.reloadLanguage();
        this.table = this.initTableView();
        this.tree = this.initTreeView();

        // Rebuild UI
        this.initMain();

        // Reload items
        this.table.showObservations(null, null);
        this.tree.updateTree();

    }

    public void deleteSchemaElement(final ISchemaElement element) {

        if (element == null) {
            return;
        }

        // Confirmation pop-up
        final JOptionPane pane = new JOptionPane(this.textManager.getString("info.delete.question"),
                JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
        final JDialog dialog = pane.createDialog(this, this.textManager.getString("info.delete.title"));
        dialog.setVisible(true);
        final Object selectedValue = pane.getValue();
        if ((selectedValue instanceof Integer)) {
            if ((Integer) selectedValue == JOptionPane.NO_OPTION) {
                return; // don't delete
            }
        }

        final List<ISchemaElement> result = this.model.remove(element);
        if (result == null) { // Deletion failed
            if (element instanceof ITarget) {
                this.createWarning(this.textManager.getString("error.deleteTargetFromCatalog"));
                return;
            }
            LOGGER.error("Error during deletion of element: {}", element);
            return;
        }

        if (result.isEmpty()) { // Deletion successful
            this.update(element);
        } else { // Deletion failed due to dependencies
            new TableElementsDialog(this, result);
        }

    }

    public ExtensionLoader getExtensionLoader() {

        return this.extLoader;

    }

    public ISchemaElement getSelectedTableElement() {

        return this.table.getSelectedElement();

    }

    // parentElement can be null (in that case all available observations will
    // be shown)
    public void updateRight(final ISchemaElement element, final ISchemaElement parentElement) {

        if (element != null) {
            // calling showObservations on table is sufficient, as this
            // internally calls showObservation on the itemView.

            if (element instanceof IObservation) {
                // this.item.showObservation((IObservation)element);
                this.table.showObservations((IObservation) element, parentElement);
            } else if (element instanceof ITarget) {
                // this.item.showTarget((ITarget)element);
                this.table.showTargets((ITarget) element);
            } else if (element instanceof IScope) {
                // this.item.showScope((IScope)element);
                this.table.showScopes((IScope) element);
            } else if (element instanceof IEyepiece) {
                // this.item.showEyepiece((IEyepiece)element);
                this.table.showEyepieces((IEyepiece) element);
            } else if (element instanceof IImager) {
                // this.item.showImager((IImager)element);
                this.table.showImagers((IImager) element);
            } else if (element instanceof IFilter) {
                // this.item.showImager((IFilter)element);
                this.table.showFilters((IFilter) element);
            } else if (element instanceof ISite) {
                // this.item.showSite((ISite)element);
                this.table.showSites((ISite) element);
            } else if (element instanceof ISession) {
                // this.item.showSession((ISession)element);
                this.table.showSessions((ISession) element);
            } else if (element instanceof IObserver) {
                // this.item.showObserver((IObserver)element);
                this.table.showObservers((IObserver) element);
            } else if (element instanceof ILens) {
                // this.item.showLens((ILens)element);
                this.table.showLenses((ILens) element);
            }
        }

    }

    public void exit() {
        this.menuFile.exit(this.changed);
    }

    public void updateLeft() {

        this.tree.updateTree();

    }

    public void updateUI(final ISchemaElement element) {

        // Update UI
        this.tree.setSelection(element, null); // This is enough...the rest
                                               // (table, item) will be updated
                                               // subsequently

    }

    public void update(final ISchemaElement element) {

        // Update cache
        this.model.update(element);

        // Update tree (clears old data and refreshes it completely)
        this.updateLeft();

        // Update UI
        this.updateUI(element);

    }

    public void refreshUI() {
        ISchemaElement element = this.model.getSelectedElement();
        this.updateLeft();
        this.updateUI(element);
    }

    public void setChanged(final boolean changed) {

        this.model.setChanged(true);

        if ((changed) // From unchanged to changed
                && (!this.changed)) {
            this.setTitle(this.getTitle() + " *");
        } else if (!changed) {
            this.setTitle(); // From changed to unchanged
        }
        this.changed = changed;

    }

    public boolean isChanged() {
        return this.model.hasChanged();
    }

    public ItemView getItemView() {

        return this.item;

    }

    public TableView getTableView() {

        return this.table;

    }

    public TreeView getTreeView() {

        return this.tree;

    }

    public JSplitPane getHorizontalSplitPane() {

        return this.hSplitPane;

    }

    public JSplitPane getVerticalSplitPane() {

        return this.vSplitPane;

    }

    public void createWarning(final String message) {

        JOptionPane.showMessageDialog(this, message, this.textManager.getString("title.warning"),
                JOptionPane.WARNING_MESSAGE);

    }

    public void createInfo(final String message) {

        JOptionPane.showMessageDialog(this, message, this.textManager.getString("title.info"),
                JOptionPane.INFORMATION_MESSAGE);

    }

    public IConfiguration getConfiguration() {

        return this.configuration;

    }

    public ProjectCatalog[] getProjects() {

        return catalogManager.getProjects();
    }

    public void resetWindowSizes() {

        this.configuration.deleteKeysStartingWith(OMDialog.DIALOG_SIZE_KEY);

    }

    private void setTitle() {

        // final Class<? extends Toolkit> toolkit = Toolkit.getDefaultToolkit().getClass();
        String title = String.format("Observation Manager - %s ", this.getVersion());
        // if (toolkit.getName().equals("sun.awt.X11.XToolkit")) { // Sets title
        // // correct in
        // // Linux/Gnome3
        // // desktop
        // try {
        // final Field awtAppClassName = toolkit.getDeclaredField("awtAppClassName");
        // awtAppClassName.setAccessible(true);
        // awtAppClassName.set(null, title);
        // } catch (final Exception e) {
        // // Cannot do much here
        // }
        // }

        this.setTitle(title);

    }

    private void initMenuBar() {

        this.menuBar = new JMenuBar();
        this.menuBar.add(this.menuFile.getMenu());
        this.menuBar.add(this.menuData.getMenu());
        this.menuBar.add(this.menuExtras.getMenu());
        this.menuBar.add(this.menuExtensions.getMenu());
        this.menuBar.add(this.menuHelp.getMenu());
        this.setJMenuBar(this.menuBar);
    }

    private void initMain() {

        this.setLocationAndSize();

        this.hSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        this.hSplitPane.setTopComponent(this.table);
        this.hSplitPane.setBottomComponent(this.item);
        this.hSplitPane.setContinuousLayout(true);
        this.getContentPane().add(hSplitPane);

        this.vSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        this.vSplitPane.setLeftComponent(this.tree);
        this.vSplitPane.setRightComponent(this.hSplitPane);
        this.vSplitPane.setContinuousLayout(true);
        this.getContentPane().add(vSplitPane);

        this.hSplitPane.setVisible(true);
        this.vSplitPane.setVisible(true);

        this.setDividerLocation();

        this.setVisible(true);

    }

    private void setLocationAndSize() {

        // Get the size of the screen
        final Dimension maxSize = Toolkit.getDefaultToolkit().getScreenSize();

        // Get last size
        final String stringSize = this.configuration.getConfig(ConfigKey.CONFIG_MAINWINDOW_SIZE,
                maxSize.width + "x" + maxSize.height);
        int width = Integer.parseInt(stringSize.substring(0, stringSize.indexOf('x')));
        int height = Integer.parseInt(stringSize.substring(stringSize.indexOf('x') + 1));
        if (width > maxSize.width) {
            width = maxSize.width;
        }
        if (height > maxSize.height) {
            height = maxSize.height;
        }
        final Dimension size = new Dimension(width, height);
        this.setSize(size);

        // Location
        final String stringLocation = this.configuration.getConfig(ConfigKey.CONFIG_MAINWINDOW_POS);
        int x = 0;
        int y = 0;
        if (stringLocation != null && !"".equals(stringLocation.trim())) {
            x = Integer.parseInt(stringLocation.substring(0, stringLocation.indexOf(',')));
            y = Integer.parseInt(stringLocation.substring(stringLocation.indexOf(',') + 1));
            // Check if position is in current screen size
            if (x > maxSize.width) {
                x = 0;
            }
            if (y > maxSize.height) {
                y = 0;
            }
        }
        this.setLocation(x, y);

        // Check if we're maximized the last time, and if so, maximized again
        final boolean maximized = Boolean.parseBoolean(
                this.configuration.getConfig(ConfigKey.CONFIG_MAINWINDOW_MAXIMIZED, Boolean.toString(false)));
        if (maximized) {
            this.setExtendedState(Frame.MAXIMIZED_BOTH);
        }

    }

    private void setDividerLocation() {

        // Set dividers
        String sVertical = this.configuration.getConfig(ConfigKey.CONFIG_MAINWINDOW_DIVIDER_VERTICAL);
        String sHorizontal = this.configuration.getConfig(ConfigKey.CONFIG_MAINWINDOW_DIVIDER_HORIZONTAL);

        float vertical = 0;
        float horizontal = 0;
        if ((sHorizontal != null) && (sVertical != null)) {
            try {
                vertical = FloatUtil.parseFloat(sVertical);
                horizontal = FloatUtil.parseFloat(sHorizontal);
            } catch (final NumberFormatException nfe) { // In case of errors set
                // default values
                sVertical = null;
                sHorizontal = null;
            }
        }

        if ((sVertical == null) || ("".equals(sVertical.trim()))) {
            this.vSplitPane.setDividerLocation(this.getWidth() / 5);
        } else {
            this.vSplitPane.setDividerLocation((int) (this.getWidth() / vertical));
        }

        if ((sVertical == null) || ("".equals(sVertical.trim()))) {
            this.hSplitPane.setDividerLocation((int) (this.getHeight() / 2.7));
        } else {
            this.hSplitPane.setDividerLocation((int) (this.getHeight() / horizontal));
        }

    }

    private TableView initTableView() {

        final TableView table = new TableView(this, this.model, this.textManager, this.uiCache);
        table.setVisible(true);

        return table;

    }

    private ItemView initItemView() {

        final ItemView item = new ItemView(this, this.model, this.imageResolver, this.uiCache);
        item.setVisible(true);

        return item;

    }

    private TreeView initTreeView() {

        final TreeView tree = new TreeView(this, this.model, this.textManager, this.imageResolver, this.uiCache);
        tree.setMinimumSize(new Dimension(this.getWidth() / 8, this.getHeight()));
        tree.setVisible(true);

        return tree;

    }

    private void enableMenus(final boolean enabled) {

        for (int i = 0; i < this.menuBar.getMenuCount(); i++) {
            this.menuBar.getMenu(i).setEnabled(enabled);
        }

    }

    private void addShortcuts() {

        final int menuKeyModifier = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        // New Observation
        this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_N, menuKeyModifier), "NEW_OBSERVATION");
        this.getRootPane().getActionMap().put("NEW_OBSERVATION", new AbstractAction() {

            private static final long serialVersionUID = 54338866832362257L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                ObservationManager.this.menuData.createNewObservation();
            }

        });

        // (Print) Show HTML export
        this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_P, menuKeyModifier), "PRINT");
        this.getRootPane().getActionMap().put("PRINT", new AbstractAction() {

            private static final long serialVersionUID = -5051798279720676416L;

            @Override
            public void actionPerformed(final ActionEvent e) {

                ObservationManager.this.menuFile.createHTML();

            }
        });

        // Help
        this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0),
                "HELP");
        this.getRootPane().getActionMap().put("HELP", new AbstractAction() {

            private static final long serialVersionUID = 2672501453219731894L;

            @Override
            public void actionPerformed(final ActionEvent e) {

                ObservationManager.this.menuExtras.showDidYouKnow();

            }
        });

        // Edit Observation
        this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_E, menuKeyModifier), "EDIT_OBSERVATION");
        this.getRootPane().getActionMap().put("EDIT_OBSERVATION", new AbstractAction() {

            private static final long serialVersionUID = 7853484982323650329L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                final ISchemaElement element = ObservationManager.this.getSelectedTableElement();
                if (element instanceof IObservation) {
                    // Edit current/selected observation
                    new ObservationDialog(ObservationManager.this, ObservationManager.this.model,
                            ObservationManager.this.textManager, (IObservation) element,
                            ObservationManager.this.uiCache);
                } else if (element instanceof ITarget) {
                    final ITarget target = (ITarget) element;
                    ObservationManager.this.getExtensionLoader().getSchemaUILoader()
                            .getTargetDialog(target.getXSIType(), target, null);
                } else if (element instanceof IScope) {
                    new ScopeDialog(ObservationManager.this, (IScope) element);
                } else if (element instanceof IEyepiece) {
                    new EyepieceDialog(ObservationManager.this, (IEyepiece) element);
                } else if (element instanceof IImager) {
                    final IImager imager = (IImager) element;
                    ObservationManager.this.getExtensionLoader().getSchemaUILoader()
                            .getSchemaElementDialog(imager.getXSIType(), SchemaElementConstants.IMAGER, imager, true);
                } else if (element instanceof ISite) {
                    new SiteDialog(ObservationManager.this, (ISite) element);
                } else if (element instanceof IFilter) {
                    new FilterDialog(ObservationManager.this, (IFilter) element);
                } else if (element instanceof ISession) {
                    new SessionDialog(ObservationManager.this, ObservationManager.this.model, (ISession) element,
                            ObservationManager.this.uiCache);
                } else if (element instanceof IObserver) {
                    new ObserverDialog(ObservationManager.this, (IObserver) element);
                } else if (element instanceof ILens) {
                    new LensDialog(ObservationManager.this, (ILens) element);
                }

            }

        });

        // Save file
        this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_S, menuKeyModifier), "SAVE_FILE");
        this.getRootPane().getActionMap().put("SAVE_FILE", new AbstractAction() {

            private static final long serialVersionUID = -4045748682943270961L;

            @Override
            public void actionPerformed(final ActionEvent e) {

                ObservationManager.this.menuFile.saveFile();
            }

        });

        // Open file
        this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_O, menuKeyModifier), "OPEN_FILE");
        this.getRootPane().getActionMap().put("OPEN_FILE", new AbstractAction() {

            private static final long serialVersionUID = -8299917980145286282L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                ObservationManager.this.menuFile.openFile(ObservationManager.this.changed);
            }

        });

    }

    public ImageResolver getImageResolver() {
        return imageResolver;
    }

    @Override
    public void createProgressDialog(Worker worker, String title, String loadingMessage) {
        new ProgressDialog(this, title, loadingMessage, worker);

    }

    public boolean isNightVisionEnabled() {
        return this.themeManager.isNightVision();
    }

    public static class Builder {
        private String locale;
        private boolean nightVision;
        private InstallDir installDir;
        private IConfiguration configuration;
        private ImageResolver imageResolver;
        private TextManager textManager;
        private TextManager versionTextManager;
        private ObservationManagerModel model;
        private UIDataCache uiCache;
        private DateManager dateManager;

        public Builder(ObservationManagerModel model) {
            this.model = model;
        }

        public Builder dateManager(DateManager dateManager) {
            this.dateManager = dateManager;
            return this;
        }

        public Builder locale(String locale) {
            this.locale = locale;
            return this;
        }

        public Builder nightVision(boolean nightVision) {
            this.nightVision = nightVision;
            return this;
        }

        public Builder installDir(InstallDir installDir) {
            this.installDir = installDir;
            return this;
        }

        public Builder configuration(IConfiguration configuration) {
            this.configuration = configuration;
            return this;
        }

        public Builder imageResolver(ImageResolver value) {
            this.imageResolver = value;
            return this;
        }

        public Builder textManager(TextManager value) {
            this.textManager = value;
            return this;
        }

        public Builder versionTextManager(TextManager value) {
            this.versionTextManager = value;
            return this;
        }

        public Builder uiCache(UIDataCache value) {
            this.uiCache = value;
            return this;
        }

        public ObservationManager build() {

            return new ObservationManager(this);
        }

    }

    /**
     * created to prepare next refactor step for AbstractDialog
     */
    @Deprecated
    public ObservationManagerModel getModel() {
        return this.model;

    }

    public String getVersion() {
        return this.versionTextManager.getString("observation.manager.version");
    }
}
