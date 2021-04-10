/* ====================================================================
 * /navigation/TreeView.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.navigation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import de.lehmannet.om.IEquipment;
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
import de.lehmannet.om.ui.i18n.TextManager;
import de.lehmannet.om.ui.image.ImageResolver;
import de.lehmannet.om.util.SchemaElementConstants;

public class TreeView extends JPanel implements TreeSelectionListener {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private JTree tree = null;

    private JScrollPane scrollTree = null;

    // Make default visible as PopupMenuHandler and SchemaElementTreeCellRender
    // will check with these objects
    DefaultMutableTreeNode root = null;
    DefaultMutableTreeNode eyepiece = null;
    DefaultMutableTreeNode imager = null;
    DefaultMutableTreeNode filter = null;
    DefaultMutableTreeNode scope = null;
    DefaultMutableTreeNode site = null;
    DefaultMutableTreeNode target = null;
    DefaultMutableTreeNode observer = null;
    DefaultMutableTreeNode session = null;
    DefaultMutableTreeNode observation = null;
    DefaultMutableTreeNode lens = null;

    // Used for faster access in setSelection
    // Key=ISchemaElement - Value: SchemaElementMutableTreeNode
    private final Map<ISchemaElement, SchemaElementMutableTreeNode> nodes = new HashMap<>();

    private final ImageResolver imageResolver;
    private final ObservationManagerModel model;
    private final ObservationManager observationManager;
    private final TextManager textManager;

    public TreeView(ObservationManager om, ObservationManagerModel omModel, TextManager textManager,
            ImageResolver resolver) {

        this.observationManager = om;
        this.model = omModel;
        this.textManager = textManager;
        this.imageResolver = resolver;

        this.root = new DefaultMutableTreeNode(this.textManager.getString("treeRoot"));
        this.observation = new DefaultMutableTreeNode(this.textManager.getString("tree.observations"));
        this.target = new DefaultMutableTreeNode(this.textManager.getString("targets"));
        this.scope = new DefaultMutableTreeNode(this.textManager.getString("scopes"));
        this.imager = new DefaultMutableTreeNode(this.textManager.getString("imagers"));
        this.filter = new DefaultMutableTreeNode(this.textManager.getString("filters"));
        this.eyepiece = new DefaultMutableTreeNode(this.textManager.getString("eyepieces"));
        this.lens = new DefaultMutableTreeNode(this.textManager.getString("lenses"));
        this.site = new DefaultMutableTreeNode(this.textManager.getString("sites"));
        this.session = new DefaultMutableTreeNode(this.textManager.getString("sessions"));
        this.observer = new DefaultMutableTreeNode(this.textManager.getString("observers"));

        this.root.add(this.observation);
        this.root.add(this.target);
        this.root.add(this.scope);
        this.root.add(this.imager);
        this.root.add(this.eyepiece);
        this.root.add(this.lens);
        this.root.add(this.filter);
        this.root.add(this.site);
        this.root.add(this.session);
        this.root.add(this.observer);

        this.initTree();

        this.tree = new JTree(this.root);
        this.tree.setCellRenderer(new SchemaElementTreeCellRenderer(this, this.observationManager, this.imageResolver));
        this.tree.addTreeSelectionListener(this);
        this.tree.setExpandsSelectedPaths(true);

        MouseListener ml = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    int x = e.getX();
                    int y = e.getY();

                    // Convert coordinates
                    MouseEvent c = SwingUtilities.convertMouseEvent(TreeView.this.tree, e,
                            TreeView.this.observationManager);
                    Point p = new Point(c.getX(), c.getY());

                    int selRow = tree.getRowForLocation(x, y);
                    TreePath selPath = TreeView.this.tree.getPathForLocation(e.getX(), e.getY());
                    TreeView.this.tree.setSelectionPath(selPath);
                    if (selRow != -1) {
                        if (e.getClickCount() == 1) {
                            Object node = Objects.requireNonNull(selPath).getLastPathComponent();

                            if (node instanceof SchemaElementMutableTreeNode) {
                                ISchemaElement element = ((SchemaElementMutableTreeNode) node).getSchemaElement();
                                new PopupMenuHandler(TreeView.this.observationManager, TreeView.this.model,
                                        TreeView.this.textManager, element, p.x, p.y,
                                        (byte) (PopupMenuHandler.EDIT + PopupMenuHandler.CREATE_HTML
                                                + PopupMenuHandler.CREATE_XML + PopupMenuHandler.DELETE
                                                + PopupMenuHandler.CREATE_NEW_OBSERVATION
                                                + PopupMenuHandler.EXTENSIONS),
                                        SchemaElementConstants.NONE,
                                        TreeView.this.observationManager.getExtensionLoader().getPopupMenus());
                            } else if (node instanceof DefaultMutableTreeNode) {
                                SchemaElementConstants type = SchemaElementConstants.NONE;
                                Object o = selPath.getLastPathComponent();

                                if (o.equals(TreeView.this.observationManager.getTreeView().eyepiece)) {
                                    type = SchemaElementConstants.EYEPIECE;
                                } else if (o.equals(TreeView.this.observationManager.getTreeView().scope)) {
                                    type = SchemaElementConstants.SCOPE;
                                } else if (o.equals(TreeView.this.observationManager.getTreeView().observation)) {
                                    type = SchemaElementConstants.OBSERVATION;
                                } else if (o.equals(TreeView.this.observationManager.getTreeView().imager)) {
                                    type = SchemaElementConstants.IMAGER;
                                } else if (o.equals(TreeView.this.observationManager.getTreeView().site)) {
                                    type = SchemaElementConstants.SITE;
                                } else if (o.equals(TreeView.this.observationManager.getTreeView().session)) {
                                    type = SchemaElementConstants.SESSION;
                                } else if (o.equals(TreeView.this.observationManager.getTreeView().observer)) {
                                    type = SchemaElementConstants.OBSERVER;
                                } else if (o.equals(TreeView.this.observationManager.getTreeView().target)) {
                                    type = SchemaElementConstants.TARGET;
                                } else if (o.equals(TreeView.this.observationManager.getTreeView().filter)) {
                                    type = SchemaElementConstants.FILTER;
                                } else if (o.equals(TreeView.this.observationManager.getTreeView().lens)) {
                                    type = SchemaElementConstants.LENS;
                                }

                                new PopupMenuHandler(TreeView.this.observationManager, TreeView.this.model,
                                        TreeView.this.textManager, null, p.x, p.y, PopupMenuHandler.CREATE, type, null);
                            }
                        }
                    }
                }
            }
        };
        this.tree.addMouseListener(ml);

        this.setLayout(new BorderLayout());
        this.scrollTree = new JScrollPane(this.tree);
        this.add(this.scrollTree);

    }

    // ---------------------
    // TreeSelectionListener --------------------------------------------------
    // ---------------------

    @Override
    public void valueChanged(TreeSelectionEvent e) {

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        ISchemaElement element = null;
        if ((node != null) && (!(node instanceof SchemaElementMutableTreeNode))) {
            TreePath tp = new TreePath(node.getPath());
            if (node.getParent() == null) {
                return;
            }

            TreePath ptp = new TreePath(node.getParent());
            if (this.tree.isExpanded(ptp)) {
                this.tree.collapsePath(tp);

                // Allow collapse of parent component..commenting this in, would prevent this
                /*
                 * if( this.lastSelected != null ) { this.tree.setSelectionPath(new
                 * TreePath(this.lastSelected.getPath())); }
                 */
            } else {
                this.tree.expandPath(tp);
                node = node.getFirstLeaf();
                this.tree.setSelectionPath(new TreePath(node.getPath()));
            }

        }

        if ((node instanceof SchemaElementMutableTreeNode)) {
            element = ((SchemaElementMutableTreeNode) node).getSchemaElement();

            // Check if parent node is also a schemaElementMutableTreeNode
            // If so, we pass the parent element to OM, which will force to load only the
            // observations belonging to the parent element
            // If not, observation will be null, which is force a load of all observations
            // Note: For all non IObservation elements, the updateRight() method argument
            // parentElement is ignored.
            ISchemaElement parentElement = null;
            if (node.getParent() instanceof SchemaElementMutableTreeNode) {
                parentElement = ((SchemaElementMutableTreeNode) node.getParent()).getSchemaElement();
            }

            this.observationManager.updateRight(element, parentElement);

            SchemaElementMutableTreeNode lastSelected = (SchemaElementMutableTreeNode) node;
        }

    }

    // --------------
    // Public Methods ---------------------------------------------------------
    // --------------

    public void updateTree() {

        String rootName = this.model.getRootName().orElse(this.textManager.getString("treeRoot"));
        this.root.setUserObject(rootName);

        this.observation.removeAllChildren();
        this.target.removeAllChildren();
        this.scope.removeAllChildren();
        this.eyepiece.removeAllChildren();
        this.imager.removeAllChildren();
        this.filter.removeAllChildren();
        this.site.removeAllChildren();
        this.session.removeAllChildren();
        this.observer.removeAllChildren();
        this.lens.removeAllChildren();
        this.initTree();

        // This might cause NullPointerExceptions in BasicTreeUI.paintRow
        // this.tree.updateUI();
        // Try this
        EventQueue.invokeLater(() -> tree.updateUI());

    }

    // ParentElement can be null
    // In that case show the observation from the all observations node
    // Note: Passing the parent element, makes only sense if element is an
    // IObservation
    public void setSelection(ISchemaElement element, ISchemaElement parentElement) {

        TreePath tp = null;
        if (parentElement != null) { // Select observation underneath the parentElement
            if (this.nodes.containsKey(parentElement)) {
                SchemaElementMutableTreeNode current = (SchemaElementMutableTreeNode) this.nodes.get(parentElement);
                ISchemaElement currentSE = null;
                for (int i = 0; i < current.getChildCount(); i++) {
                    currentSE = ((SchemaElementMutableTreeNode) current.getChildAt(i)).getSchemaElement();
                    if (element.equals(currentSE)) {
                        TreePath newPath = new TreePath(
                                ((SchemaElementMutableTreeNode) current.getChildAt(i)).getPath());
                        if (!this.tree.getSelectionPath().equals(newPath)) { // Only set new Path when neccessary
                            this.tree.setSelectionPath(newPath);
                            this.scrollTree(newPath);
                            return;
                        }
                    }
                }
                tp = new TreePath(current.getPath());
                this.tree.setSelectionPath(tp);
            }
        } else { // Select schemaElement
            if (this.nodes.containsKey(element)) {
                SchemaElementMutableTreeNode current = (SchemaElementMutableTreeNode) this.nodes.get(element);
                if (current.getSchemaElement().equals(element)) {
                    tp = new TreePath(current.getPath());
                    this.tree.setSelectionPath(tp);
                }
            }
        }

        this.scrollTree(tp);

    }

    // ---------------
    // Private Methods --------------------------------------------------------
    // ---------------

    private void scrollTree(TreePath tp) {

        // -------- Try to scroll to row

        JViewport viewport = this.scrollTree.getViewport();

        // This rectangle is relative to the table where the
        // northwest corner of cell (0,0) is always (0,0).
        Rectangle rect = this.tree.getRowBounds(this.tree.getRowForPath(tp));
        if (rect == null) { // Nothing to scroll to
            return;
        }

        // The location of the view relative to the table
        Rectangle viewRect = viewport.getViewRect();

        // Translate the cell location so that it is relative
        // to the view, assuming the northwest corner of the
        // view is (0,0).
        rect.setLocation(rect.x - viewRect.x, rect.y - viewRect.y);

        // Calculate location of rect if it were at the center of view
        int centerX = (viewRect.width - rect.width) / 2;
        int centerY = (viewRect.height - rect.height) / 2;

        // Fake the location of the cell so that scrollRectToVisible
        // will move the cell to the center
        if (rect.x < centerX) {
            centerX = -centerX;
        }
        if (rect.y < centerY) {
            centerY = -centerY;
        }
        rect.translate(centerX, centerY);

        // Scroll the area into view.
        viewport.scrollRectToVisible(rect);

    }

    private void initTree() {

        // Observation Node
        ISchemaElement[] elements = this.model.getObservations();
        this.addSchemaElements(elements, this.observation);
        if (elements.length > 0) {
            this.observation
                    .setUserObject(this.textManager.getString("tree.observations") + " (" + elements.length + ")");
        } else {
            this.observation.setUserObject(this.textManager.getString("tree.observations"));
        }

        // TargetContainer Node
        elements = this.model.getTargets();
        this.addSchemaElements(elements, this.target);
        if (elements.length > 0) {
            this.target.setUserObject(this.textManager.getString("targets") + " (" + elements.length + ")");
        } else {
            this.target.setUserObject(this.textManager.getString("targets"));
        }

        // ScopePanel Node
        elements = this.model.getScopes();
        this.addSchemaElements(elements, this.scope);
        if (elements.length > 0) {
            this.scope.setUserObject(this.textManager.getString("scopes") + " (" + elements.length + ")");
        } else {
            this.scope.setUserObject(this.textManager.getString("scopes"));
        }

        // ImagerPanel Node
        elements = this.model.getImagers();
        this.addSchemaElements(elements, this.imager);
        if (elements.length > 0) {
            this.imager.setUserObject(this.textManager.getString("imagers") + " (" + elements.length + ")");
        } else {
            this.imager.setUserObject(this.textManager.getString("imagers"));
        }

        // EyepiecePanel Node
        elements = this.model.getEyepieces();
        this.addSchemaElements(elements, this.eyepiece);
        if (elements.length > 0) {
            this.eyepiece.setUserObject(this.textManager.getString("eyepieces") + " (" + elements.length + ")");
        } else {
            this.eyepiece.setUserObject(this.textManager.getString("eyepieces"));
        }

        // FilterPanel Node
        elements = this.model.getFilters();
        this.addSchemaElements(elements, this.filter);
        if (elements.length > 0) {
            this.filter.setUserObject(this.textManager.getString("filters") + " (" + elements.length + ")");
        } else {
            this.filter.setUserObject(this.textManager.getString("filters"));
        }

        // SitePanel Node
        elements = this.model.getSites();
        this.addSchemaElements(elements, this.site);
        if (elements.length > 0) {
            this.site.setUserObject(this.textManager.getString("sites") + " (" + elements.length + ")");
        } else {
            this.site.setUserObject(this.textManager.getString("sites"));
        }

        // SessionPanel Node
        elements = this.model.getSessions();
        this.addSchemaElements(elements, this.session);
        if (elements.length > 0) {
            this.session.setUserObject(this.textManager.getString("sessions") + " (" + elements.length + ")");
        } else {
            this.session.setUserObject(this.textManager.getString("sessions"));
        }

        // ObserverPanel Node
        elements = this.model.getObservers();
        this.addSchemaElements(elements, this.observer);
        if (elements.length > 0) {
            this.observer.setUserObject(this.textManager.getString("observers") + " (" + elements.length + ")");
        } else {
            this.observer.setUserObject(this.textManager.getString("observers"));
        }

        // LensPanel Node
        elements = this.model.getLenses();
        this.addSchemaElements(elements, this.lens);
        if (elements.length > 0) {
            this.lens.setUserObject(this.textManager.getString("lenses") + " (" + elements.length + ")");
        } else {
            this.lens.setUserObject(this.textManager.getString("lenses"));
        }

    }

    private void addSchemaElements(ISchemaElement[] elements, DefaultMutableTreeNode node) {

        SchemaElementMutableTreeNode current = null;
        for (ISchemaElement element : elements) {
            current = new SchemaElementMutableTreeNode(element);

            // Only add observations for all non-IObservation elements
            if (!(element instanceof IObservation)) {
                // Get all observations for corresponding schema element
                IObservation[] observations = this.model.getObservations(element);
                if (observations != null) {
                    // If the element is an IObserver, we also need to access the observations where
                    // this observer
                    // is the coObserver
                    // Also we attach the coObserver Observations to the other observations, as the
                    // both will
                    // be listed under the observer node (in different font/color)
                    if (element instanceof IObserver) {
                        IObservation[] coObserver = this.model.getCoObserverObservations((IObserver) element);
                        if (coObserver != null) {

                            // Add coObserver observations to other observations (and remove doublicates via
                            // HashSet)
                            List<IObservation> obs = new ArrayList<>(Arrays.asList(observations));
                            int coObsLength = coObserver.length;
                            for (IObservation iObservation : coObserver) {
                                if (!obs.contains(iObservation)) { // New observation
                                    obs.add(iObservation);
                                } else { // Doublicate
                                    coObsLength--; // One coObserver observation that won't be counted
                                }
                            }
                            observations = (IObservation[]) obs.toArray(new IObservation[] {});

                            current.setSize(observations.length - coObsLength, coObsLength);

                        } else {
                            current.setSize(observations.length, -1);
                        }
                    } else {
                        current.setSize(observations.length, -1);
                    }

                    // Add all observations to the parent node
                    for (IObservation iObservation : observations) {
                        current.add(new SchemaElementMutableTreeNode(iObservation, -1));
                    }
                }
            }

            this.nodes.put(element, current); // For faster access
            node.add(current); // Add element to treenode
        }

    }

}

class SchemaElementMutableTreeNode extends DefaultMutableTreeNode {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private ISchemaElement element = null;

    public SchemaElementMutableTreeNode(ISchemaElement element) {

        this(element, -1);

    }

    public SchemaElementMutableTreeNode(ISchemaElement element, int size) {

        this.element = element;
        this.setSize(size, -1);

    }

    public ISchemaElement getSchemaElement() {

        return this.element;

    }

    public void setSize(int size, int secondValue) {

        if (size >= 0) {
            if (secondValue > 0) {
                this.setUserObject("<html>" + element.getDisplayName() + " (" + size + " / <font color=\"#afafaf\">"
                        + secondValue + "</font>)</html>");
            } else {
                this.setUserObject(element.getDisplayName() + " (" + size + ")");
            }
        } else {
            this.setUserObject(element.getDisplayName());
        }

    }

}

class SchemaElementTreeCellRenderer extends DefaultTreeCellRenderer {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private TreeView treeView = null;

    private ObservationManager om = null;
    private final ImageResolver imageResolver;

    public SchemaElementTreeCellRenderer(TreeView treeView, ObservationManager om, ImageResolver resolver) {

        this.treeView = treeView;
        this.om = om;
        this.imageResolver = resolver;

    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
            int row, boolean hasFocus) {

        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        Icon icon = null;

        // Set default font
        this.setFont(new Font("Arial", Font.PLAIN, 12));
        // Set default (text) color
        this.setForeground(Color.DARK_GRAY);
        if (this.om.isNightVisionEnabled()) {
            this.setBackgroundNonSelectionColor(new Color(255, 175, 175));
        } else {
            this.setBackgroundNonSelectionColor(Color.WHITE);
        }

        // Check leafs

        if (value instanceof SchemaElementMutableTreeNode) {

            SchemaElementMutableTreeNode schemaNode = (SchemaElementMutableTreeNode) value;
            ISchemaElement se = schemaNode.getSchemaElement();
            if (se instanceof IEyepiece) {

                // Change color is equipment is no longer available
                if (((IEquipment) se).isAvailable()) {
                    this.setForeground(Color.DARK_GRAY);
                    this.setFont(new Font("Arial", Font.PLAIN, 12));
                } else { // Unavailable
                    this.setForeground(Color.GRAY);
                    this.setFont(new Font("Arial", Font.PLAIN, 12));
                }

                icon = new ImageIcon(this.imageResolver.getImageURL("eyepiece_l.png").orElse(null), "");
                this.setToolTipText("");
                this.setIcon(icon);

                return this;
            } else if (se instanceof ISession) {
                icon = new ImageIcon(this.imageResolver.getImageURL("session_l.png").orElse(null), "");
                this.setToolTipText("");
                this.setIcon(icon);

                return this;
            } else if (se instanceof ISite) {
                icon = new ImageIcon(this.imageResolver.getImageURL("site_l.png").orElse(null), "");
                this.setToolTipText("");
                this.setIcon(icon);

                return this;
            } else if (se instanceof IScope) {

                // Change color is equipment is no longer available
                if (((IEquipment) se).isAvailable()) {
                    this.setForeground(Color.DARK_GRAY);
                    this.setFont(new Font("Arial", Font.PLAIN, 12));
                } else { // Unavailable
                    this.setForeground(Color.GRAY);
                    this.setFont(new Font("Arial", Font.PLAIN, 12));
                }

                icon = new ImageIcon(this.imageResolver.getImageURL("scope_l.png").orElse(null), "");
                this.setToolTipText("");
                this.setIcon(icon);

                return this;
            } else if (se instanceof ITarget) {
                icon = new ImageIcon(this.imageResolver.getImageURL("target_l.png").orElse(null), "");
                this.setToolTipText("");
                this.setIcon(icon);

                return this;
            } else if (se instanceof IObserver) {
                icon = new ImageIcon(this.imageResolver.getImageURL("observer_l.png").orElse(null), "");
                this.setToolTipText("");
                this.setIcon(icon);

                return this;
            } else if (se instanceof IObservation) {
                // In case the observation belongs to another schema element, use light gray
                if (schemaNode.getParent() instanceof SchemaElementMutableTreeNode) {

                    // Check if parent element is an IObserver
                    // If so, check if parentObserver is the main observer of this observation
                    // If no, he's one of the coObservers and we change the font a little
                    ISchemaElement parentSE = ((SchemaElementMutableTreeNode) schemaNode.getParent())
                            .getSchemaElement();
                    if (parentSE instanceof IObserver) {
                        if (parentSE.equals(((IObservation) se).getObserver())) { // "Main" Observer
                            this.setForeground(Color.GRAY);
                            this.setFont(new Font("Arial", Font.ITALIC, 12));
                        } else { // CoObserver
                            this.setForeground(new Color(175, 175, 175)); // Matches <font color=\"#afafaf\"> in
                                                                          // MutableTreeNode
                            this.setFont(new Font("Arial", Font.ITALIC, 12));
                        }
                    } else { // Not an IObserver
                        this.setForeground(Color.GRAY);
                        this.setFont(new Font("Arial", Font.ITALIC, 12));
                    }
                }

                icon = new ImageIcon(this.imageResolver.getImageURL("observation_l.png").orElse(null), "");
                this.setToolTipText("");
                this.setIcon(icon);

                return this;
            } else if (se instanceof IImager) {

                // Change color is equipment is no longer available
                if (((IEquipment) se).isAvailable()) {
                    this.setForeground(Color.DARK_GRAY);
                    this.setFont(new Font("Arial", Font.PLAIN, 12));
                } else { // Unavailable
                    this.setForeground(Color.GRAY);
                    this.setFont(new Font("Arial", Font.PLAIN, 12));
                }

                icon = new ImageIcon(this.imageResolver.getImageURL("imager_l.png").orElse(null), "");
                this.setToolTipText("");
                this.setIcon(icon);

                return this;
            } else if (se instanceof IFilter) {

                // Change color is equipment is no longer available
                if (((IEquipment) se).isAvailable()) {
                    this.setForeground(Color.DARK_GRAY);
                    this.setFont(new Font("Arial", Font.PLAIN, 12));
                } else { // Unavailable
                    this.setForeground(Color.GRAY);
                    this.setFont(new Font("Arial", Font.PLAIN, 12));
                }

                icon = new ImageIcon(this.imageResolver.getImageURL("filter_l.png").orElse(null), "");
                this.setToolTipText("");
                this.setIcon(icon);

                return this;
            } else if (se instanceof ILens) {

                // Change color is equipment is no longer available
                if (((IEquipment) se).isAvailable()) {
                    this.setForeground(Color.DARK_GRAY);
                    this.setFont(new Font("Arial", Font.PLAIN, 12));
                } else { // Unavailable
                    this.setForeground(Color.GRAY);
                    this.setFont(new Font("Arial", Font.PLAIN, 12));
                }

                icon = new ImageIcon(this.imageResolver.getImageURL("lens_l.png").orElse(null), "");
                this.setToolTipText("");
                this.setIcon(icon);

                return this;
            }

        }

        // Check nodes

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        if (this.treeView.eyepiece.equals(node)) {

            if (expanded) {
                icon = new ImageIcon(this.imageResolver.getImageURL("eyepiece_e.png").orElse(null), "");
            } else {
                icon = new ImageIcon(this.imageResolver.getImageURL("eyepiece_c.png").orElse(null), "");
            }

        } else if (this.treeView.session.equals(node)) {

            if (expanded) {
                icon = new ImageIcon(this.imageResolver.getImageURL("session_e.png").orElse(null), "");
            } else {
                icon = new ImageIcon(this.imageResolver.getImageURL("session_c.png").orElse(null), "");
            }

        } else if (this.treeView.site.equals(node)) {

            if (expanded) {
                icon = new ImageIcon(this.imageResolver.getImageURL("site_e.png").orElse(null), "");
            } else {
                icon = new ImageIcon(this.imageResolver.getImageURL("site_c.png").orElse(null), "");
            }

        } else if (this.treeView.scope.equals(node)) {

            if (expanded) {
                icon = new ImageIcon(this.imageResolver.getImageURL("scope_e.png").orElse(null), "");
            } else {
                icon = new ImageIcon(this.imageResolver.getImageURL("scope_c.png").orElse(null), "");
            }

        } else if (this.treeView.target.equals(node)) {

            if (expanded) {
                icon = new ImageIcon(this.imageResolver.getImageURL("target_e.png").orElse(null), "");
            } else {
                icon = new ImageIcon(this.imageResolver.getImageURL("target_c.png").orElse(null), "");
            }

        } else if (this.treeView.observer.equals(node)) {

            if (expanded) {
                icon = new ImageIcon(this.imageResolver.getImageURL("observer_e.png").orElse(null), "");
            } else {
                icon = new ImageIcon(this.imageResolver.getImageURL("observer_c.png").orElse(null), "");
            }

        } else if (this.treeView.observation.equals(node)) {

            this.setFont(new Font("Arial", Font.BOLD, 12));
            if (expanded) {
                icon = new ImageIcon(this.imageResolver.getImageURL("observation_e.png").orElse(null), "");
            } else {
                icon = new ImageIcon(this.imageResolver.getImageURL("observation_c.png").orElse(null), "");
            }

        } else if (this.treeView.imager.equals(node)) {

            if (expanded) {
                icon = new ImageIcon(this.imageResolver.getImageURL("imager_e.png").orElse(null), "");
            } else {
                icon = new ImageIcon(this.imageResolver.getImageURL("imager_c.png").orElse(null), "");
            }
        } else if (this.treeView.filter.equals(node)) {

            if (expanded) {
                icon = new ImageIcon(this.imageResolver.getImageURL("filter_e.png").orElse(null), "");
            } else {
                icon = new ImageIcon(this.imageResolver.getImageURL("filter_c.png").orElse(null), "");
            }
        } else if (this.treeView.lens.equals(node)) {

            if (expanded) {
                icon = new ImageIcon(this.imageResolver.getImageURL("lens_e.png").orElse(null), "");
            } else {
                icon = new ImageIcon(this.imageResolver.getImageURL("lens_c.png").orElse(null), "");
            }

        } else if (this.treeView.root.equals(node)) {

            icon = new ImageIcon(this.imageResolver.getImageURL("root.png").orElse(null), "");

        }

        // Check if node is empty
        if (node.children().equals(DefaultMutableTreeNode.EMPTY_ENUMERATION)) {
            icon = new ImageIcon(this.imageResolver.getImageURL("empty.png").orElse(null), "");
        }

        this.setIcon(icon);

        return this;

    }

}
