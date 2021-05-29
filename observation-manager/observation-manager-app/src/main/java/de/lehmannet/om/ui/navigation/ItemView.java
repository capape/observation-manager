/* ====================================================================
 * /navigation/ItemView.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.navigation;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.lehmannet.om.IEyepiece;
import de.lehmannet.om.IFilter;
import de.lehmannet.om.IFinding;
import de.lehmannet.om.IImager;
import de.lehmannet.om.ILens;
import de.lehmannet.om.IObservation;
import de.lehmannet.om.IObserver;
import de.lehmannet.om.IScope;
import de.lehmannet.om.ISession;
import de.lehmannet.om.ISite;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.cache.UIDataCache;
import de.lehmannet.om.ui.image.ImageResolver;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.ui.panel.EyepiecePanel;
import de.lehmannet.om.ui.panel.FilterPanel;
import de.lehmannet.om.ui.panel.IFindingPanel;
import de.lehmannet.om.ui.panel.IImagerPanel;
import de.lehmannet.om.ui.panel.LensPanel;
import de.lehmannet.om.ui.panel.ObservationItemPanel;
import de.lehmannet.om.ui.panel.ObserverPanel;
import de.lehmannet.om.ui.panel.ScopePanel;
import de.lehmannet.om.ui.panel.SessionPanel;
import de.lehmannet.om.ui.panel.SitePanel;
import de.lehmannet.om.ui.util.LocaleToolsFactory;
import de.lehmannet.om.util.SchemaElementConstants;

public class ItemView extends JPanel implements ChangeListener {

    /**
     *
     */
    private static final long serialVersionUID = -3588994516307727926L;
    private static final TabIDComponent FINDING_TIC = new TabIDComponent((byte) 1);
    private static final TabIDComponent TARGET_TIC = new TabIDComponent((byte) 2);
    private static final TabIDComponent SITE_TIC = new TabIDComponent((byte) 3);
    private static final TabIDComponent SESSION_TIC = new TabIDComponent((byte) 4);
    private static final TabIDComponent SCOPE_TIC = new TabIDComponent((byte) 5);
    private static final TabIDComponent OBSERVER_TIC = new TabIDComponent((byte) 6);
    private static final TabIDComponent EYEPIECE_TIC = new TabIDComponent((byte) 7);
    private static final TabIDComponent FILTER_TIC = new TabIDComponent((byte) 8);
    private static final TabIDComponent IMAGER_TIC = new TabIDComponent((byte) 9);
    private static final TabIDComponent LENS_TIC = new TabIDComponent((byte) 10);

    private final ResourceBundle bundle = LocaleToolsFactory.appInstance().getBundle("ObservationManager",
            Locale.getDefault());

    private ObservationManager main = null;

    private JTabbedPane tabbedPane = null;

    private ObservationItemPanel overviewPanel = null;
    private AbstractPanel targetPanel = null;
    private AbstractPanel findingPanel = null;
    private SitePanel sitePanel = null;
    private SessionPanel sessionPanel = null;
    private ScopePanel scopePanel = null;
    private ObserverPanel observerPanel = null;
    private EyepiecePanel eyepiecePanel = null;
    private FilterPanel filterPanel = null;
    private AbstractPanel imagerPanel = null;
    private LensPanel lensPanel = null;

    private Component lastSelectedTab = null;

    // Contains the current shown observation, or null (if other SchemaElement type
    // is shown)
    private IObservation currentObseration = null;

    private final ImageResolver imageResolver;
    private final ObservationManagerModel model;
    private final UIDataCache uiCache;

    public ItemView(ObservationManager main, ObservationManagerModel model, ImageResolver resolver,
            UIDataCache uiCache) {

        this.main = main;
        this.model = model;
        this.imageResolver = resolver;
        this.uiCache = uiCache;
        this.tabbedPane = new JTabbedPane();
        this.tabbedPane.addChangeListener(this);

        this.setLayout(new BorderLayout());
        this.add(this.tabbedPane);

    }

    @Override
    public void stateChanged(ChangeEvent e) {

        // Move everything in an own thread an invoke later
        // This should fix strange ArrayIndexOutOfBoundsExceptions thrown at some VMs
        SwingUtilities.invokeLater(() -> {
            Component tabIndexComponent = ItemView.this.tabbedPane.getSelectedComponent();
            // Is selected component a dummy?
            if (!(tabIndexComponent instanceof TabIDComponent)) { // No: Set selection
                return;
            }
            // Yes: Load the corresponding component
            byte tabID = ((TabIDComponent) tabIndexComponent).getTabID();
            switch (tabID) {
            case 1: {
                IFinding f = (IFinding) ItemView.this.currentObseration.getResults().get(0);
                ISession se = ItemView.this.currentObseration.getSession();
                ITarget t = ItemView.this.currentObseration.getTarget();
                ItemView.this.loadFindingPanel(f, se, t, ItemView.this.tabbedPane.getSelectedIndex());
                return;
            }
            case 2: {
                ITarget t = ItemView.this.currentObseration.getTarget();
                ItemView.this.loadTargetPanel(t, ItemView.this.currentObseration,
                        ItemView.this.tabbedPane.getSelectedIndex());
                return;
            }
            case 3: {
                ISite s = ItemView.this.currentObseration.getSite();
                ItemView.this.loadSitePanel(s, ItemView.this.tabbedPane.getSelectedIndex());
                return;
            }
            case 4: {
                ISession s = ItemView.this.currentObseration.getSession();
                ItemView.this.loadSessionPanel(s, ItemView.this.tabbedPane.getSelectedIndex());
                return;
            }
            case 5: {
                IScope s = ItemView.this.currentObseration.getScope();
                ItemView.this.loadScopePanel(s, ItemView.this.tabbedPane.getSelectedIndex());
                return;
            }
            case 6: {
                IObserver o = ItemView.this.currentObseration.getObserver();
                ItemView.this.loadObserverPanel(o, ItemView.this.tabbedPane.getSelectedIndex());
                return;
            }
            case 7: {
                IEyepiece eye = ItemView.this.currentObseration.getEyepiece();
                ItemView.this.loadEyepiecePanel(eye, ItemView.this.tabbedPane.getSelectedIndex());
                return;
            }
            case 8: {
                IFilter f = ItemView.this.currentObseration.getFilter();
                ItemView.this.loadFilterPanel(f, ItemView.this.tabbedPane.getSelectedIndex());
                return;
            }
            case 9: {
                IImager i = ItemView.this.currentObseration.getImager();
                ItemView.this.loadImagerPanel(i, ItemView.this.tabbedPane.getSelectedIndex());
                return;
            }
            case 10: {
                ILens l = ItemView.this.currentObseration.getLens();
                ItemView.this.loadLensPanel(l, ItemView.this.tabbedPane.getSelectedIndex());
                return;
            }
            default: {
                ItemView.this.tabbedPane.setSelectedIndex(0);
            }
            }
        });

    }

    public void clear() {

        this.tabbedPane.removeAll();
        this.lastSelectedTab = null;
        this.currentObseration = null;

    }

    public void showObservation(final IObservation observation) {

        // Keep the last selected tab in mind...if there're more than one ( = we showed
        // an observation)
        if (this.tabbedPane.getTabCount() > 1) {
            this.lastSelectedTab = this.tabbedPane.getSelectedComponent();
        } else {
            this.lastSelectedTab = null;
        }

        // Keep in mind this observation
        this.currentObseration = observation;
        this.tabbedPane.removeAll();

        this.addObservationTab(observation);

        int tabIndex = 0;
        int findingIndex = 0;
        int targetIndex = 0;
        int siteIndex = 0;
        int sessionIndex = 0;
        int scopeIndex = 0;
        int observerIndex = 0;
        int eyepieceIndex = 0;
        int filterIndex = 0;
        int imagerIndex = 0;
        int lensIndex = 0;

        IFinding f = null;
        if (!observation.getResults().isEmpty()) {
            f = observation.getResults().get(0);
            if (f != null) {
                tabIndex++;
                findingIndex = tabIndex;
                this.addFindingTab(f, observation.getSession(), observation.getTarget(), findingIndex); // @todo: Only
                                                                                                        // works
                                                                                                        // with one
                                                                                                        // finding
            }
        }

        ITarget t = observation.getTarget();
        if (t != null) {
            tabIndex++;
            targetIndex = tabIndex;
            this.addTargetTab(t, observation, targetIndex);
        }

        ISite s = observation.getSite();
        if (s != null) {
            tabIndex++;
            siteIndex = tabIndex;
            this.addSiteTab(s, siteIndex);
        }

        ISession se = observation.getSession();
        if (se != null) {
            tabIndex++;
            sessionIndex = tabIndex;
            this.addSessionTab(se, sessionIndex);
        }

        IScope sc = observation.getScope();
        if (sc != null) {
            tabIndex++;
            scopeIndex = tabIndex;
            this.addScopeTab(sc, scopeIndex);
        }

        IObserver obs = observation.getObserver();
        if (obs != null) {
            tabIndex++;
            observerIndex = tabIndex;
            this.addObserverTab(obs, observerIndex);
        }

        IEyepiece eye = observation.getEyepiece();
        if (eye != null) {
            tabIndex++;
            eyepieceIndex = tabIndex;
            this.addEyepieceTab(eye, eyepieceIndex);
        }

        IFilter fil = observation.getFilter();
        if (fil != null) {
            tabIndex++;
            filterIndex = tabIndex;
            this.addFilterTab(fil, filterIndex);
        }

        IImager img = observation.getImager();
        if (img != null) {
            tabIndex++;
            imagerIndex = tabIndex;
            this.addImagerTab(img, imagerIndex);
        }

        ILens len = observation.getLens();
        if (len != null) {
            tabIndex++;
            lensIndex = tabIndex;
            this.addLensTab(len, lensIndex);
        }

        if (this.lastSelectedTab == null) { // Nothing selected...might be inital call
            return;
        }

        // Select the last selected tab again
        if (this.lastSelectedTab instanceof ObservationItemPanel) {
            this.tabbedPane.setSelectedComponent(this.overviewPanel);
        } else if (this.lastSelectedTab instanceof IFindingPanel) {
            if (f != null) {
                this.loadFindingPanel(f, se, t, findingIndex);
                this.tabbedPane.setSelectedComponent(this.findingPanel);
            }
        } else if (this.lastSelectedTab instanceof SitePanel) {
            if (s != null) {
                this.loadSitePanel(s, siteIndex);
                this.tabbedPane.setSelectedComponent(this.sitePanel);
            }
        } else if (this.lastSelectedTab instanceof SessionPanel) {
            if (se != null) {
                this.loadSessionPanel(se, sessionIndex);
                this.tabbedPane.setSelectedComponent(this.sessionPanel);
            }
        } else if (this.lastSelectedTab instanceof ScopePanel) {
            if (sc != null) {
                this.loadScopePanel(sc, scopeIndex);
                this.tabbedPane.setSelectedComponent(this.scopePanel);
            }
        } else if (this.lastSelectedTab instanceof ObserverPanel) {
            if (obs != null) {
                this.loadObserverPanel(obs, observerIndex);
                this.tabbedPane.setSelectedComponent(this.observerPanel);
            }
        } else if (this.lastSelectedTab instanceof EyepiecePanel) {
            if (eye != null) {
                this.loadEyepiecePanel(eye, eyepieceIndex);
                this.tabbedPane.setSelectedComponent(this.eyepiecePanel);
            }
        } else if (this.lastSelectedTab instanceof FilterPanel) {
            if (fil != null) {
                this.loadFilterPanel(fil, filterIndex);
                this.tabbedPane.setSelectedComponent(this.filterPanel);
            }
        } else if (this.lastSelectedTab instanceof IImagerPanel) {
            if (img != null) {
                this.loadImagerPanel(img, imagerIndex);
                this.tabbedPane.setSelectedComponent(this.imagerPanel);
            }
        } else if (this.lastSelectedTab instanceof LensPanel) {
            if (len != null) {
                this.loadLensPanel(len, lensIndex);
                this.tabbedPane.setSelectedComponent(this.lensPanel);
            }
        } else {
            if (t != null) {
                this.loadTargetPanel(t, observation, targetIndex);
                this.tabbedPane.setSelectedComponent(this.targetPanel);
            }
        }

    }

    public void showObserver(IObserver observer) {

        this.clear();
        this.addObserverTab(observer, -1);

    }

    public void showTarget(ITarget target, IObservation o) {

        this.clear();
        this.addTargetTab(target, o, -1);

    }

    public void showFinding(IFinding finding) {

        this.clear();
        this.addFindingTab(finding, null, null, -1);

    }

    public void showSite(ISite site) {

        this.clear();
        this.addSiteTab(site, -1);

    }

    public void showSession(ISession session) {

        this.clear();
        this.addSessionTab(session, -1);

    }

    public void showScope(IScope scope) {

        this.clear();
        this.addScopeTab(scope, -1);

    }

    public void showEyepiece(IEyepiece eyepiece) {

        this.clear();
        this.addEyepieceTab(eyepiece, -1);

    }

    public void showFilter(IFilter filter) {

        this.clear();
        this.addFilterTab(filter, -1);

    }

    public void showImager(IImager imager) {

        this.clear();
        this.addImagerTab(imager, -1);

    }

    public void showLens(ILens lens) {

        this.clear();
        this.addLensTab(lens, -1);

    }

    public void reloadLanguage() {

        AbstractPanel.reloadLanguage();

    }

    private void addObservationTab(IObservation observation) {

        this.overviewPanel = new ObservationItemPanel(this.main, this.model, observation);
        this.tabbedPane.addTab(this.bundle.getString("observation"),
                new ImageIcon(this.imageResolver.getImageURL("observation_l.png").orElse(null)), this.overviewPanel);

    }

    private void addFindingTab(IFinding finding, ISession session, ITarget t, int index) {

        if (this.tabbedPane.getTabCount() == 0) { // Show only this tab
            this.loadFindingPanel(finding, session, t, index);
        } else { // Show complete observation (load panel later on user request)

            // This if..else statement contains a lot of vodoo and black magic
            // Problem was that during startup (using the load last XML file feature)
            // this exception was thrown occasionally:
            // Exception in thread "AWT-EventQueue-0" ERR <some Date/Time>
            // java.lang.ArrayIndexOutOfBoundsException: 1
            // at javax.swing.plaf.basic.BasicTabbedPaneUI.paintTabArea(Unknown Source)
            // at javax.swing.plaf.basic.BasicTabbedPaneUI.paint(Unknown Source)
            // at javax.swing.plaf.metal.MetalTabbedPaneUI.paint(Unknown Source)
            // at javax.swing.plaf.metal.MetalTabbedPaneUI.update(Unknown Source)
            // at javax.swing.JComponent.paintComponent(Unknown Source)
            // at javax.swing.JComponent.paint(Unknown Source)
            // at javax.swing.JComponent.paintChildren(Unknown Source)
            // at javax.swing.JComponent.paint(Unknown Source)
            // at javax.swing.JComponent.paintToOffscreen(Unknown Source)
            // at javax.swing.BufferStrategyPaintManager.paint(Unknown Source)
            // at javax.swing.RepaintManager.paint(Unknown Source)
            // at javax.swing.JComponent._paintImmediately(Unknown Source)
            // at javax.swing.JComponent.paintImmediately(Unknown Source)
            // at javax.swing.RepaintManager.paintDirtyRegions(Unknown Source)
            // at javax.swing.RepaintManager.paintDirtyRegions(Unknown Source)
            // at javax.swing.RepaintManager.seqPaintDirtyRegions(Unknown Source)
            // at javax.swing.SystemEventQueueUtilities$ComponentWorkRequest.run(Unknown
            // Source)
            // at java.awt.event.InvocationEvent.dispatch(Unknown Source)
            // at java.awt.EventQueue.dispatchEvent(Unknown Source)
            // at java.awt.EventDispatchThread.pumpOneEventForFilters(Unknown Source)
            // at java.awt.EventDispatchThread.pumpEventsForFilter(Unknown Source)
            // at java.awt.EventDispatchThread.pumpEventsForHierarchy(Unknown Source)
            // at java.awt.EventDispatchThread.pumpEvents(Unknown Source)
            // at java.awt.EventDispatchThread.pumpEvents(Unknown Source)
            // at java.awt.EventDispatchThread.run(Unknown Source)
            // After some investigation I found out that the showObservation() method in
            // here has some hiccups
            // when calling the addFindingTab method to add the finding as second tab (after
            // the observationItem tab)
            // (Position of this finding tab is 1...I think that correlated to the array
            // index in the exception above)
            // As a solution we use the invokeAndWait method now (only when adding this
            // tab), and only when the calling
            // thread is not the Swing EventDispatcher thread (which should only happen
            // during startup, that the calling
            // thread is not the EventDispatcher))
            // As the invokeAndWait method cannot be called from the EventDispatcher thread,
            // we use the if statement to
            // decide whether we call it directly or via an own runnable.
            // Also important to know is that we need to use invokeAndWait and NOT
            // invokeLater method, as otherwise the
            // sorted order of the tabs gets lost, and the finding tab is added somewhere at
            // the end (instead of being the
            // second tab

            if (SwingUtilities.isEventDispatchThread()) {
                this.tabbedPane.addTab(this.bundle.getString("finding"),
                        new ImageIcon(ItemView.this.imageResolver.getImageURL("finding_l.png").orElse(null)),
                        ItemView.FINDING_TIC);
            } else {
                try {
                    SwingUtilities.invokeAndWait(
                            () -> ItemView.this.tabbedPane.addTab(ItemView.this.bundle.getString("finding"),
                                    new ImageIcon(
                                            ItemView.this.imageResolver.getImageURL("finding_l.png").orElse(null)),
                                    ItemView.FINDING_TIC));
                } catch (Exception e) {
                    System.err.println("Error during add tab. Most probably you can ignore this.\n" + e);
                }
            }
        }

    }

    private void loadFindingPanel(IFinding finding, ISession session, ITarget target, int index) {

        this.findingPanel = this.main.getExtensionLoader().getSchemaUILoader().getFindingPanel(finding.getXSIType(),
                finding, session, target, false);
        if (this.findingPanel != null) {
            if (index <= 0) {
                this.tabbedPane.addTab(this.bundle.getString("finding"),
                        new ImageIcon(this.imageResolver.getImageURL("finding_l.png").orElse(null)), this.findingPanel);
            } else {
                // index = this.tabbedPane.getSelectedIndex();
                this.tabbedPane.setComponentAt(index, this.findingPanel);
            }
        }

    }

    private void addTargetTab(ITarget target, IObservation o, int index) {

        if (this.tabbedPane.getTabCount() == 0) { // Show only this tab
            this.loadTargetPanel(target, o, index);
        } else { // Show complete observation (load panel later on user request)
            if (SwingUtilities.isEventDispatchThread()) {
                this.tabbedPane.addTab(this.bundle.getString("target"),
                        new ImageIcon(this.imageResolver.getImageURL("target_l.png").orElse(null)),
                        ItemView.TARGET_TIC);
            } else {
                try {
                    SwingUtilities.invokeAndWait(
                            () -> ItemView.this.tabbedPane.addTab(ItemView.this.bundle.getString("target"),
                                    new ImageIcon(ItemView.this.imageResolver.getImageURL("target_l.png").orElse(null)),
                                    ItemView.TARGET_TIC));
                } catch (Exception e) {
                    System.err.println("Error during add tab. Most probably you can ignore this.\n" + e);
                }
            }
        }

    }

    private void loadTargetPanel(ITarget target, IObservation o, int index) {

        this.targetPanel = this.main.getExtensionLoader().getSchemaUILoader().getTargetPanel(target.getXSIType(),
                target, o, false);
        if (this.targetPanel != null) {
            if (index <= 0) {
                this.tabbedPane.addTab(this.bundle.getString("target"),
                        new ImageIcon(this.imageResolver.getImageURL("target_l.png").orElse(null)), this.targetPanel);
            } else {
                // index = this.tabbedPane.getSelectedIndex();
                this.tabbedPane.setComponentAt(index, this.targetPanel);
            }
        }

    }

    private void addSiteTab(ISite site, int index) {

        if (this.tabbedPane.getTabCount() == 0) { // Show only this tab
            this.loadSitePanel(site, index);
        } else { // Show complete observation (load panel later on user request)
            if (SwingUtilities.isEventDispatchThread()) {
                this.tabbedPane.addTab(this.bundle.getString("site"),
                        new ImageIcon(this.imageResolver.getImageURL("site_l.png").orElse(null)), ItemView.SITE_TIC);
            } else {
                try {
                    SwingUtilities
                            .invokeAndWait(() -> ItemView.this.tabbedPane.addTab(ItemView.this.bundle.getString("site"),
                                    new ImageIcon(ItemView.this.imageResolver.getImageURL("site_l.png").orElse(null)),
                                    ItemView.SITE_TIC));
                } catch (Exception e) {
                    System.err.println("Error during add tab. Most probably you can ignore this.\n" + e);
                }
            }
        }

    }

    private void loadSitePanel(ISite site, int index) {

        this.sitePanel = new SitePanel(site, false);
        if (index <= 0) {
            this.tabbedPane.addTab(this.bundle.getString("site"),
                    new ImageIcon(this.imageResolver.getImageURL("site_l.png").orElse(null)), this.sitePanel);
        } else {
            // index = this.tabbedPane.getSelectedIndex();
            this.tabbedPane.setComponentAt(index, this.sitePanel);
        }

    }

    private void addSessionTab(ISession session, int index) {

        if (this.tabbedPane.getTabCount() == 0) { // Show only this tab
            this.loadSessionPanel(session, index);
        } else { // Show complete observation (load panel later on user request)
            if (SwingUtilities.isEventDispatchThread()) {
                this.tabbedPane.addTab(this.bundle.getString("session"),
                        new ImageIcon(this.imageResolver.getImageURL("session_l.png").orElse(null)),
                        ItemView.SESSION_TIC);
            } else {
                try {
                    SwingUtilities.invokeAndWait(
                            () -> ItemView.this.tabbedPane.addTab(ItemView.this.bundle.getString("session"),
                                    new ImageIcon(
                                            ItemView.this.imageResolver.getImageURL("session_l.png").orElse(null)),
                                    ItemView.SESSION_TIC));
                } catch (Exception e) {
                    System.err.println("Error during add tab. Most probably you can ignore this.\n" + e);
                }
            }
        }

    }

    private void loadSessionPanel(ISession session, int index) {

        this.sessionPanel = new SessionPanel(this.main, this.model, session, false, this.uiCache);
        if (index <= 0) {
            this.tabbedPane.addTab(this.bundle.getString("session"),
                    new ImageIcon(this.imageResolver.getImageURL("session_l.png").orElse(null)), this.sessionPanel);
        } else {
            // index = this.tabbedPane.getSelectedIndex();
            this.tabbedPane.setComponentAt(index, this.sessionPanel);
        }

    }

    private void addScopeTab(IScope scope, int index) {

        if (this.tabbedPane.getTabCount() == 0) { // Show only this tab
            this.loadScopePanel(scope, index);
        } else { // Show complete observation (load panel later on user request)
            if (SwingUtilities.isEventDispatchThread()) {
                this.tabbedPane.addTab(this.bundle.getString("scope"),
                        new ImageIcon(this.imageResolver.getImageURL("scope_l.png").orElse(null)), ItemView.SCOPE_TIC);
            } else {
                try {
                    SwingUtilities.invokeAndWait(
                            () -> ItemView.this.tabbedPane.addTab(ItemView.this.bundle.getString("scope"),
                                    new ImageIcon(ItemView.this.imageResolver.getImageURL("scope_l.png").orElse(null)),
                                    ItemView.SCOPE_TIC));
                } catch (Exception e) {
                    System.err.println("Error during add tab. Most probably you can ignore this.\n" + e);
                }
            }
        }
    }

    private void loadScopePanel(IScope scope, int index) {

        this.scopePanel = new ScopePanel(scope, false);
        if (index <= 0) {
            this.tabbedPane.addTab(this.bundle.getString("scope"),
                    new ImageIcon(this.imageResolver.getImageURL("scope_l.png").orElse(null)), this.scopePanel);
        } else {
            // index = this.tabbedPane.getSelectedIndex();
            this.tabbedPane.setComponentAt(index, this.scopePanel);
        }

    }

    private void addObserverTab(IObserver observer, int index) {

        if (this.tabbedPane.getTabCount() == 0) { // Show only this tab
            this.loadObserverPanel(observer, index);
        } else { // Show complete observation (load panel later on user request)
            if (SwingUtilities.isEventDispatchThread()) {
                this.tabbedPane.addTab(this.bundle.getString("observer"),
                        new ImageIcon(this.imageResolver.getImageURL("observer_l.png").orElse(null)),
                        ItemView.OBSERVER_TIC);
            } else {
                try {
                    SwingUtilities.invokeAndWait(
                            () -> ItemView.this.tabbedPane.addTab(ItemView.this.bundle.getString("observer"),
                                    new ImageIcon(
                                            ItemView.this.imageResolver.getImageURL("observer_l.png").orElse(null)),
                                    ItemView.OBSERVER_TIC));
                } catch (Exception e) {
                    System.err.println("Error during add tab. Most probably you can ignore this.\n" + e);
                }
            }
        }

    }

    private void loadObserverPanel(IObserver observer, int index) {

        this.observerPanel = new ObserverPanel(observer, false);
        if (index <= 0) {
            this.tabbedPane.addTab(this.bundle.getString("observer"),
                    new ImageIcon(this.imageResolver.getImageURL("observer_l.png").orElse(null)), this.observerPanel);
        } else {
            // index = this.tabbedPane.getSelectedIndex();
            this.tabbedPane.setComponentAt(index, this.observerPanel);
        }

    }

    private void addEyepieceTab(IEyepiece eyepiece, int index) {

        if (this.tabbedPane.getTabCount() == 0) { // Show only this tab
            this.loadEyepiecePanel(eyepiece, index);
        } else { // Show complete observation (load panel later on user request)
            if (SwingUtilities.isEventDispatchThread()) {
                this.tabbedPane.addTab(this.bundle.getString("eyepiece"),
                        new ImageIcon(this.imageResolver.getImageURL("eyepiece_l.png").orElse(null)),
                        ItemView.EYEPIECE_TIC);
            } else {
                try {
                    SwingUtilities.invokeAndWait(
                            () -> ItemView.this.tabbedPane.addTab(ItemView.this.bundle.getString("eyepiece"),
                                    new ImageIcon(
                                            ItemView.this.imageResolver.getImageURL("eyepiece_l.png").orElse(null)),
                                    ItemView.EYEPIECE_TIC));
                } catch (Exception e) {
                    System.err.println("Error during add tab. Most probably you can ignore this.\n" + e);
                }
            }
        }

    }

    private void loadEyepiecePanel(IEyepiece eyepiece, int index) {

        this.eyepiecePanel = new EyepiecePanel(eyepiece);

        if (index <= 0) {
            this.tabbedPane.addTab(this.bundle.getString("eyepiece"),
                    new ImageIcon(this.imageResolver.getImageURL("eyepiece_l.png").orElse(null)), this.eyepiecePanel);
        } else {
            // index = this.tabbedPane.getSelectedIndex();
            this.tabbedPane.setComponentAt(index, this.eyepiecePanel);
        }

    }

    private void addFilterTab(IFilter filter, int index) {

        if (this.tabbedPane.getTabCount() == 0) { // Show only this tab
            this.loadFilterPanel(filter, index);
        } else { // Show complete observation (load panel later on user request)
            if (SwingUtilities.isEventDispatchThread()) {
                this.tabbedPane.addTab(this.bundle.getString("filter"),
                        new ImageIcon(this.imageResolver.getImageURL("filter_l.png").orElse(null)),
                        ItemView.FILTER_TIC);
            } else {
                try {
                    SwingUtilities.invokeAndWait(
                            () -> ItemView.this.tabbedPane.addTab(ItemView.this.bundle.getString("filter"),
                                    new ImageIcon(ItemView.this.imageResolver.getImageURL("filter_l.png").orElse(null)),
                                    ItemView.FILTER_TIC));
                } catch (Exception e) {
                    System.err.println("Error during add tab. Most probably you can ignore this.\n" + e);
                }
            }
        }

    }

    private void loadFilterPanel(IFilter filter, int index) {

        this.filterPanel = new FilterPanel(filter, false);

        if (index <= 0) {
            this.tabbedPane.addTab(this.bundle.getString("filter"),
                    new ImageIcon(this.imageResolver.getImageURL("filter_l.png").orElse(null)), this.filterPanel);
        } else {
            // index = this.tabbedPane.getSelectedIndex();
            this.tabbedPane.setComponentAt(index, this.filterPanel);
        }

    }

    private void addImagerTab(IImager imager, int index) {

        if (this.tabbedPane.getTabCount() == 0) { // Show only this tab
            this.loadImagerPanel(imager, index);
        } else { // Show complete observation (load panel later on user request)
            if (SwingUtilities.isEventDispatchThread()) {
                this.tabbedPane.addTab(this.bundle.getString("imager"),
                        new ImageIcon(this.imageResolver.getImageURL("imager_l.png").orElse(null)),
                        ItemView.IMAGER_TIC);
            } else {
                try {
                    SwingUtilities.invokeAndWait(
                            () -> ItemView.this.tabbedPane.addTab(ItemView.this.bundle.getString("imager"),
                                    new ImageIcon(ItemView.this.imageResolver.getImageURL("imager_l.png").orElse(null)),
                                    ItemView.IMAGER_TIC));
                } catch (Exception e) {
                    System.err.println("Error during add tab. Most probably you can ignore this.\n" + e);
                }
            }
        }

    }

    private void loadImagerPanel(IImager imager, int index) {

        this.imagerPanel = this.main.getExtensionLoader().getSchemaUILoader().getSchemaElementPanel(imager.getXSIType(),
                SchemaElementConstants.IMAGER, imager, false);
        if (this.imagerPanel != null) {
            if (index <= 0) {
                this.tabbedPane.addTab(this.bundle.getString("imager"),
                        new ImageIcon(this.imageResolver.getImageURL("imager_l.png").orElse(null)), this.imagerPanel);
            } else {
                // index = this.tabbedPane.getSelectedIndex();
                this.tabbedPane.setComponentAt(index, this.imagerPanel);
            }
        }

    }

    private void addLensTab(ILens lens, int index) {

        if (this.tabbedPane.getTabCount() == 0) { // Show only this tab
            this.loadLensPanel(lens, index);
        } else { // Show complete observation (load panel later on user request)
            String tabTitle = this.bundle.getString("lens");
            if (lens.getFactor() > 1) {
                tabTitle = this.bundle.getString("lens.barlow");
            } else if (lens.getFactor() < 1) {
                tabTitle = this.bundle.getString("lens.sharpley");
            }
            if (SwingUtilities.isEventDispatchThread()) {
                this.tabbedPane.addTab(tabTitle,
                        new ImageIcon(this.imageResolver.getImageURL("lens_l.png").orElse(null)), ItemView.LENS_TIC);
            } else {
                try {
                    SwingUtilities.invokeAndWait(() -> {
                        String tabTitle1 = ItemView.this.bundle.getString("lens");
                        ItemView.this.tabbedPane.addTab(tabTitle1,
                                new ImageIcon(ItemView.this.imageResolver.getImageURL("lens_l.png").orElse(null)),
                                ItemView.LENS_TIC);
                    });
                } catch (Exception e) {
                    System.err.println("Error during add tab. Most probably you can ignore this.\n" + e);
                }
            }

        }

    }

    private void loadLensPanel(ILens lens, int index) {

        this.lensPanel = new LensPanel(lens, false);
        if (index <= 0) {
            String tabTitle = this.bundle.getString("lens");
            if (lens.getFactor() > 1) {
                tabTitle = this.bundle.getString("lens.barlow");
            } else if (lens.getFactor() < 1) {
                tabTitle = this.bundle.getString("lens.sharpley");
            }
            this.tabbedPane.addTab(tabTitle, new ImageIcon(this.imageResolver.getImageURL("lens_l.png").orElse(null)),
                    this.lensPanel);
        } else {
            // index = this.tabbedPane.getSelectedIndex();
            this.tabbedPane.setComponentAt(index, this.lensPanel);
        }

    }

}

// Dummy class as placeholder for real panel (as minimal as possible...must
// extend Component to add to TabbedPane)
class TabIDComponent extends Component {

    /**
     *
     */
    private static final long serialVersionUID = 350103099685046530L;
    private byte tabID = 0;

    public TabIDComponent(byte tabID) {

        this.tabID = tabID;

    }

    public byte getTabID() {

        return this.tabID;

    }

}