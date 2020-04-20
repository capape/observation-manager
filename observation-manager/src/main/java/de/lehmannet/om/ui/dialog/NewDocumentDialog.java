package de.lehmannet.om.ui.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;

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
import de.lehmannet.om.ui.image.ImageResolver;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.util.SchemaElementConstants;

// That's one nasty dialog with nasty helper classes
// Better way then forwarding parent object references would be to use events, but anyway...it works :-)
public class NewDocumentDialog extends JDialog implements ActionListener {

    private static final String DESCRIPTION = "-";
    private static final String DEFAULT_COLLAPSED_ICON_TEXT = "+";
    private static final String OBSERVATION_EXPANDED_ICON = "observation_e.png";
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    // Result constants
    public static final int CANCEL = -1;
    private static final int OK_BLANK = 0;
    public static final int OK_COPY = 1;

    // Constants used as ActionCommands
    private static final String AC_BLANK = "blank";
    private static final String AC_NEW = "new";
    private static final String AC_OK = "ok";
    private static final String AC_CANCEL = "cancel";

    private final PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
            .getBundle("ObservationManager", Locale.getDefault());

    private ObservationManager om = null;
    private JTree tree = null;

    private Boolean blank = null;

    // Result arrays
    private IImager[] imagers = null;
    private IEyepiece[] eyepieces = null;
    private IFilter[] filters = null;
    private ILens[] lenses = null;
    private IObservation[] observations = null;
    private IObserver[] observers = null;
    private IScope[] scopes = null;
    private ISession[] sessions = null;
    private ISite[] sites = null;
    private ITarget[] targets = null;
    private final ImageResolver imageResolver;

    public NewDocumentDialog(JFrame om, ImageResolver resolver) {

        super((ObservationManager) om, true);

        this.om = (ObservationManager) om;

        this.imageResolver = resolver;

        this.setTitle(this.bundle.getString("dialog.newDoc.title"));
        this.setSize(480, 480);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(om);

        this.initTree();

        this.initDialog();

        this.pack();
        this.setVisible(true);
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (NewDocumentDialog.AC_BLANK.equals(e.getActionCommand())) {
            this.tree.setEnabled(false);
            this.blank = Boolean.TRUE;
        } else if (NewDocumentDialog.AC_NEW.equals(e.getActionCommand())) {
            this.tree.setEnabled(true);
            this.blank = Boolean.FALSE;
        } else if (NewDocumentDialog.AC_OK.equals(e.getActionCommand())) {
            boolean success = this.prepareResult();
            if (success) {
                this.dispose();
            }
        } else {
            this.blank = null; // Indicates cancel

            // Close window
            this.dispose();
        }

    }

    public int getResult() {

        if (this.blank == null) {
            return NewDocumentDialog.CANCEL; // Cancel was pressed
        } else {
            if (this.blank) {
                return NewDocumentDialog.OK_BLANK; // OK was pressed and create blank document was selected
            } else {
                return NewDocumentDialog.OK_COPY; // OK was pressed and create new document was selected
            }
        }

    }

    public ISchemaElement[] getSchemaElements(SchemaElementConstants schemaElementCode) {

        // Always return NULL if cancel was pressed or creation of blank document was
        // requested
        if ((this.getResult() == NewDocumentDialog.CANCEL) || (this.getResult() == NewDocumentDialog.OK_BLANK)) {
            return null;
        }

        switch (schemaElementCode) {
        case IMAGER: {
            return this.imagers;
        }
        case EYEPIECE: {
            return this.eyepieces;
        }
        case FILTER: {
            return this.filters;
        }
        case LENS: {
            return this.lenses;
        }
        case OBSERVATION: {
            return this.observations;
        }
        case OBSERVER: {
            return this.observers;
        }
        case SCOPE: {
            return this.scopes;
        }
        case SESSION: {
            return this.sessions;
        }
        case SITE: {
            return this.sites;
        }
        case TARGET: {
            return this.targets;
        }
        default:
            return null;
        }

    }

    JTree getTree() {

        return this.tree;

    }

    private boolean prepareResult() {

        TreeModel model = this.tree.getModel();

        Object current = model.getRoot();
        Object node = null;
        Object leaf = null;
        for (int i = 0; i < model.getChildCount(current); i++) { // Iterate of all schema element type nodes
            node = this.tree.getModel().getChild(current, i);
            if (node instanceof DefaultMutableTreeNode) {
                if (((DefaultMutableTreeNode) node).getUserObject() instanceof CheckBoxNode) { // Node is selected?
                    Object uo = ((DefaultMutableTreeNode) node).getUserObject();
                    if (((CheckBoxNode) uo).isSelected()) {
                        List<ISchemaElement> resultList = new ArrayList<>();
                        for (int j = 0; j < model.getChildCount(node); j++) { // Iterate over all children
                            leaf = model.getChild(node, j);
                            if (leaf instanceof DefaultMutableTreeNode) {
                                Object luo = ((DefaultMutableTreeNode) leaf).getUserObject();
                                if (luo instanceof SchemaElementLeaf) {
                                    SchemaElementLeaf sel = (SchemaElementLeaf) luo;
                                    if (sel.isSelected()) { // Child is selected?
                                        ISchemaElement se = sel.getSchemaElement();
                                        resultList.add(se);
                                    }
                                }
                            }
                        }
                        // Now fill the arrays
                        if (!resultList.isEmpty()) {
                            if (resultList.get(0) instanceof IImager) {
                                this.imagers = (IImager[]) resultList.toArray(new IImager[] {});
                            } else if (resultList.get(0) instanceof IEyepiece) {
                                this.eyepieces = (IEyepiece[]) resultList.toArray(new IEyepiece[] {});
                            } else if (resultList.get(0) instanceof IFilter) {
                                this.filters = (IFilter[]) resultList.toArray(new IFilter[] {});
                            } else if (resultList.get(0) instanceof ILens) {
                                this.lenses = (ILens[]) resultList.toArray(new ILens[] {});
                            } else if (resultList.get(0) instanceof IObservation) {
                                this.observations = (IObservation[]) resultList.toArray(new IObservation[] {});
                            } else if (resultList.get(0) instanceof IObserver) {
                                this.observers = (IObserver[]) resultList.toArray(new IObserver[] {});
                            } else if (resultList.get(0) instanceof IScope) {
                                this.scopes = (IScope[]) resultList.toArray(new IScope[] {});
                            } else if (resultList.get(0) instanceof ISession) {
                                this.sessions = (ISession[]) resultList.toArray(new ISession[] {});
                            } else if (resultList.get(0) instanceof ISite) {
                                this.sites = (ISite[]) resultList.toArray(new ISite[] {});
                            } else if (resultList.get(0) instanceof ITarget) {
                                this.targets = (ITarget[]) resultList.toArray(new ITarget[] {});
                            }
                        }
                    }
                }
            }
        }

        // Check dependencies
        boolean solvedDependencyProblem = false;

        if ((this.observations != null) && (this.observations.length > 0)) { // Check dependencies of selected
                                                                             // observations
            for (IObservation observation : this.observations) {
                // --- Eyepiece
                IEyepiece eyepiece = observation.getEyepiece();
                if (eyepiece != null) {
                    if ((this.eyepieces != null) && (this.eyepieces.length > 0)) {
                        boolean found = false;
                        for (IEyepiece iEyepiece : this.eyepieces) {
                            if (iEyepiece.equals(eyepiece)) { // Element found
                                found = true;
                                break;
                            }
                        }
                        if (!found) { // Element was not found, add it to array
                            this.eyepieces = (IEyepiece[]) this.resizeArray(this.eyepieces, this.eyepieces.length + 1);
                            this.eyepieces[this.eyepieces.length - 1] = eyepiece;
                            solvedDependencyProblem = true;
                        }
                    } else {
                        this.eyepieces = new IEyepiece[] { eyepiece };
                        solvedDependencyProblem = true;
                    }
                }
                // --- Filter
                IFilter filter = observation.getFilter();
                if (filter != null) {
                    if ((this.filters != null) && (this.filters.length > 0)) {
                        boolean found = false;
                        for (IFilter iFilter : this.filters) {
                            if (iFilter.equals(filter)) { // Element found
                                found = true;
                                break;
                            }
                        }
                        if (!found) { // Element was not found, add it to array
                            this.filters = (IFilter[]) this.resizeArray(this.filters, this.filters.length + 1);
                            this.filters[this.filters.length - 1] = filter;
                            solvedDependencyProblem = true;
                        }
                    } else {
                        this.filters = new IFilter[] { filter };
                        solvedDependencyProblem = true;
                    }
                }
                // --- Imager
                IImager imager = observation.getImager();
                if (imager != null) {
                    if ((this.imagers != null) && (this.imagers.length > 0)) {
                        boolean found = false;
                        for (IImager iImager : this.imagers) {
                            if (iImager.equals(imager)) { // Element found
                                found = true;
                                break;
                            }
                        }
                        if (!found) { // Element was not found, add it to array
                            this.imagers = (IImager[]) this.resizeArray(this.imagers, this.imagers.length + 1);
                            this.imagers[this.imagers.length - 1] = imager;
                            solvedDependencyProblem = true;
                        }
                    } else {
                        this.imagers = new IImager[] { imager };
                        solvedDependencyProblem = true;
                    }
                }
                // --- Lens
                ILens lens = observation.getLens();
                if (lens != null) {
                    if ((this.lenses != null) && (this.lenses.length > 0)) {
                        boolean found = false;
                        for (ILens iLens : this.lenses) {
                            if (iLens.equals(lens)) { // Element found
                                found = true;
                                break;
                            }
                        }
                        if (!found) { // Element was not found, add it to array
                            this.lenses = (ILens[]) this.resizeArray(this.lenses, this.lenses.length + 1);
                            this.lenses[this.lenses.length - 1] = lens;
                            solvedDependencyProblem = true;
                        }
                    } else {
                        this.lenses = new ILens[] { lens };
                        solvedDependencyProblem = true;
                    }
                }
                // --- Observer
                IObserver observer = observation.getObserver();
                if (observer != null) {
                    if ((this.observers != null) && (this.observers.length > 0)) {
                        boolean found = false;
                        for (IObserver iObserver : this.observers) {
                            if (iObserver.equals(observer)) { // Element found
                                found = true;
                                break;
                            }
                        }
                        if (!found) { // Element was not found, add it to array
                            this.observers = (IObserver[]) this.resizeArray(this.observers, this.observers.length + 1);
                            this.observers[this.observers.length - 1] = observer;
                            solvedDependencyProblem = true;
                        }
                    } else {
                        this.observers = new IObserver[] { observer };
                        solvedDependencyProblem = true;
                    }
                }
                // --- Scope
                IScope scope = observation.getScope();
                if (scope != null) {
                    if ((this.scopes != null) && (this.scopes.length > 0)) {
                        boolean found = false;
                        for (IScope iScope : this.scopes) {
                            if (iScope.equals(scope)) { // Element found
                                found = true;
                                break;
                            }
                        }
                        if (!found) { // Element was not found, add it to array
                            this.scopes = (IScope[]) this.resizeArray(this.scopes, this.scopes.length + 1);
                            this.scopes[this.scopes.length - 1] = scope;
                            solvedDependencyProblem = true;
                        }
                    } else {
                        this.scopes = new IScope[] { scope };
                        solvedDependencyProblem = true;
                    }
                }
                ISession session = observation.getSession();
                if (session != null) {
                    if ((this.sessions != null) && (this.sessions.length > 0)) {
                        boolean found = false;
                        for (ISession iSession : this.sessions) {
                            if (iSession.equals(session)) { // Element found
                                found = true;
                                break;
                            }
                        }
                        if (!found) { // Element was not found, add it to array
                            this.sessions = (ISession[]) this.resizeArray(this.sessions, this.sessions.length + 1);
                            this.sessions[this.sessions.length - 1] = session;
                            solvedDependencyProblem = true;
                        }
                    } else {
                        this.sessions = new ISession[] { session };
                        solvedDependencyProblem = true;
                    }
                }
                ISite site = observation.getSite();
                if (site != null) {
                    if ((this.sites != null) && (this.sites.length > 0)) {
                        boolean found = false;
                        for (ISite iSite : this.sites) {
                            if (iSite.equals(site)) { // Element found
                                found = true;
                                break;
                            }
                        }
                        if (!found) { // Element was not found, add it to array
                            this.sites = (ISite[]) this.resizeArray(this.sites, this.sites.length + 1);
                            this.sites[this.sites.length - 1] = site;
                            solvedDependencyProblem = true;
                        }
                    } else {
                        this.sites = new ISite[] { site };
                        solvedDependencyProblem = true;
                    }
                }
                ITarget target = observation.getTarget();
                if (target != null) {
                    if ((this.targets != null) && (this.targets.length > 0)) {
                        boolean found = false;
                        for (ITarget iTarget : this.targets) {
                            if (iTarget.equals(target)) { // Element found
                                found = true;
                                break;
                            }
                        }
                        if (!found) { // Element was not found, add it to array
                            this.targets = (ITarget[]) this.resizeArray(this.targets, this.targets.length + 1);
                            this.targets[this.targets.length - 1] = target;
                            solvedDependencyProblem = true;
                        }
                    } else {
                        this.targets = new ITarget[] { target };
                        solvedDependencyProblem = true;
                    }
                }
            }
        }

        if ((this.sessions != null) && (this.sessions.length > 0)) { // Check dependencies of selected sessions
            for (ISession session : this.sessions) {
                // -- Site
                ISite site = session.getSite();
                if (site != null) {
                    if ((this.sites != null) && (this.sites.length > 0)) {
                        boolean found = false;
                        for (ISite iSite : this.sites) {
                            if (iSite.equals(site)) { // Element found
                                found = true;
                                break;
                            }
                        }
                        if (!found) { // Element was not found, add it to array
                            this.sites = (ISite[]) this.resizeArray(this.sites, this.sites.length + 1);
                            this.sites[this.sites.length - 1] = site;
                            solvedDependencyProblem = true;
                        }
                    } else {
                        this.sites = new ISite[] { site };
                        solvedDependencyProblem = true;
                    }
                }
                // --- Observer
                List<IObserver> coObservers = session.getCoObservers();
                Iterator<IObserver> iterator = coObservers.iterator();
                IObserver observer = null;
                while (iterator.hasNext()) {
                    observer = iterator.next();
                    if (observer != null) {
                        if ((this.observers != null) && (this.observers.length > 0)) {
                            boolean found = false;
                            for (IObserver iObserver : this.observers) {
                                if (iObserver.equals(observer)) { // Element found
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) { // Element was not found, add it to array
                                this.observers = (IObserver[]) this.resizeArray(this.observers,
                                        this.observers.length + 1);
                                this.observers[this.observers.length - 1] = observer;
                                solvedDependencyProblem = true;
                            }
                        } else {
                            this.observers = new IObserver[] { observer };
                            solvedDependencyProblem = true;
                        }
                    }
                }
            }
        }

        if ((this.targets != null) && (this.targets.length > 0)) { // Check dependencies of selected targets
            for (ITarget target : this.targets) {
                // --- Observer
                IObserver observer = target.getObserver();
                if (observer != null) {
                    if ((this.observers != null) && (this.observers.length > 0)) {
                        boolean found = false;
                        for (IObserver iObserver : this.observers) {
                            if (iObserver.equals(observer)) { // Element found
                                found = true;
                                break;
                            }
                        }
                        if (!found) { // Element was not found, add it to array
                            this.observers = (IObserver[]) this.resizeArray(this.observers, this.observers.length + 1);
                            this.observers[this.observers.length - 1] = observer;
                            solvedDependencyProblem = true;
                        }
                    } else {
                        this.observers = new IObserver[] { observer };
                        solvedDependencyProblem = true;
                    }
                }
            }
        }

        if (solvedDependencyProblem) {
            this.om.createInfo(this.bundle.getString("dialog.newDoc.info.solvedDependencyProblem"));
        }

        return true;

    }

    private void initDialog() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        this.getContentPane().setLayout(gridbag);

        ButtonGroup buttonGroup = new ButtonGroup();

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 2, 1, 25, 2);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        JRadioButton newBlankDocument = new JRadioButton(this.bundle.getString("dialog.newDoc.button.blank"));
        newBlankDocument.setToolTipText(this.bundle.getString("dialog.newDoc.tooltip.blank"));
        newBlankDocument.setActionCommand(NewDocumentDialog.AC_BLANK);
        newBlankDocument.addActionListener(this);
        newBlankDocument.setSelected(true);
        this.blank = Boolean.TRUE;
        buttonGroup.add(newBlankDocument);
        gridbag.setConstraints(newBlankDocument, constraints);
        this.getContentPane().add(newBlankDocument);

        JRadioButton newDocument = new JRadioButton(this.bundle.getString("dialog.newDoc.button.new"));
        newDocument.setToolTipText(this.bundle.getString("dialog.newDoc.tooltip.new"));
        newDocument.setActionCommand(NewDocumentDialog.AC_NEW);
        buttonGroup.add(newDocument);
        newDocument.addActionListener(this);
        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 2, 1, 25, 2);
        gridbag.setConstraints(newDocument, constraints);
        this.getContentPane().add(newDocument);

        this.tree.setEnabled(false); // Disable as we've preselected new blank document
        this.tree.setToolTipText(this.bundle.getString("dialog.newDoc.tooltip.tree"));
        JScrollPane scrollPanel = new JScrollPane(this.tree);
        scrollPanel.setBorder(BorderFactory.createTitledBorder(this.bundle.getString("dialog.newDoc.border.tree")));
        ConstraintsBuilder.buildConstraints(constraints, 0, 2, 2, 1, 50, 88);
        constraints.fill = GridBagConstraints.BOTH;
        gridbag.setConstraints(scrollPanel, constraints);
        this.getContentPane().add(scrollPanel);

        JButton ok = new JButton(this.bundle.getString("dialog.button.ok"));
        ok.setActionCommand(NewDocumentDialog.AC_OK);
        ok.addActionListener(this);
        ConstraintsBuilder.buildConstraints(constraints, 0, 3, 1, 1, 25, 4);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(ok, constraints);
        this.getContentPane().add(ok);

        JButton cancel = new JButton(this.bundle.getString("dialog.button.cancel"));
        cancel.setActionCommand(NewDocumentDialog.AC_CANCEL);
        cancel.addActionListener(this);
        ConstraintsBuilder.buildConstraints(constraints, 1, 3, 1, 1, 25, 4);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(cancel, constraints);
        this.getContentPane().add(cancel);

    }

    private void initTree() {

       
                
        Icon expanded = null;
        Icon collapsed = null;

        // The root node
        CheckBoxNode root = new CheckBoxNode(this, this.bundle.getString("treeRoot"), false, null, null, null);

        // Create all schema element nodes
        expanded = new ImageIcon(this.imageResolver.getImageURL(OBSERVATION_EXPANDED_ICON).orElse(null),DESCRIPTION);
        collapsed = new ImageIcon(this.imageResolver.getImageURL("observation_c.png").orElse(null),DEFAULT_COLLAPSED_ICON_TEXT);
        CheckBoxNode observations = new CheckBoxNode(this, this.bundle.getString("observations"), false,
                this.om.getXmlCache().getObservations(), expanded, collapsed);
        root.add(observations);

        expanded = new ImageIcon(this.imageResolver.getImageURL("target_e.png").orElse(null),DESCRIPTION);
        collapsed = new ImageIcon(this.imageResolver.getImageURL("target_c.png").orElse(null),DEFAULT_COLLAPSED_ICON_TEXT);
        CheckBoxNode targets = new CheckBoxNode(this, this.bundle.getString("targets"), false,
                this.om.getXmlCache().getTargets(), expanded, collapsed);
        root.add(targets);

        expanded = new ImageIcon(this.imageResolver.getImageURL("scope_e.png").orElse(null),DESCRIPTION);
        collapsed = new ImageIcon(this.imageResolver.getImageURL("scope_c.png").orElse(null),DEFAULT_COLLAPSED_ICON_TEXT);
        CheckBoxNode scopes = new CheckBoxNode(this, this.bundle.getString("scopes"), true,
                this.om.getXmlCache().getScopes(), expanded, collapsed);
        root.add(scopes);

        expanded = new ImageIcon(this.imageResolver.getImageURL("imager_e.png").orElse(null),DESCRIPTION);
        collapsed = new ImageIcon(this.imageResolver.getImageURL("imager_c.png").orElse(null),DEFAULT_COLLAPSED_ICON_TEXT);
        CheckBoxNode imagers = new CheckBoxNode(this, this.bundle.getString("imagers"), true,
                this.om.getXmlCache().getImagers(), expanded, collapsed);
        root.add(imagers);

        expanded = new ImageIcon(this.imageResolver.getImageURL("filter_e.png").orElse(null),DESCRIPTION);
        collapsed = new ImageIcon(this.imageResolver.getImageURL("filter_c.png").orElse(null),DEFAULT_COLLAPSED_ICON_TEXT);
        CheckBoxNode filters = new CheckBoxNode(this, this.bundle.getString("filters"), true,
                this.om.getXmlCache().getFilters(), expanded, collapsed);
        root.add(filters);

        expanded = new ImageIcon(this.imageResolver.getImageURL("eyepiece_e.png").orElse(null),DESCRIPTION);
        collapsed = new ImageIcon(this.imageResolver.getImageURL("eyepiece_c.png").orElse(null),DEFAULT_COLLAPSED_ICON_TEXT);
        CheckBoxNode eyepieces = new CheckBoxNode(this, this.bundle.getString("eyepieces"), true,
                this.om.getXmlCache().getEyepieces(), expanded, collapsed);
        root.add(eyepieces);

        expanded = new ImageIcon(this.imageResolver.getImageURL("lens_e.png").orElse(null),DESCRIPTION);
        collapsed = new ImageIcon(this.imageResolver.getImageURL("lens_c.png").orElse(null),DEFAULT_COLLAPSED_ICON_TEXT);
        CheckBoxNode lenses = new CheckBoxNode(this, this.bundle.getString("lenses"), true,
                this.om.getXmlCache().getLenses(), expanded, collapsed);
        root.add(lenses);

        expanded = new ImageIcon(this.imageResolver.getImageURL("site_e.png").orElse(null),DESCRIPTION);
        collapsed = new ImageIcon(this.imageResolver.getImageURL("site_c.png").orElse(null),DESCRIPTION);
        CheckBoxNode sites = new CheckBoxNode(this, this.bundle.getString("sites"), true,
                this.om.getXmlCache().getSites(), expanded, collapsed);
        root.add(sites);

        expanded = new ImageIcon(this.imageResolver.getImageURL("session_e.png").orElse(null),DESCRIPTION);
        collapsed = new ImageIcon(this.imageResolver.getImageURL("session_c.png").orElse(null),DEFAULT_COLLAPSED_ICON_TEXT);
        CheckBoxNode sessions = new CheckBoxNode(this, this.bundle.getString("sessions"), false,
                this.om.getXmlCache().getSessions(), expanded, collapsed);
        root.add(sessions);

        expanded = new ImageIcon(this.imageResolver.getImageURL("observer_e.png").orElse(null),DESCRIPTION);
        collapsed = new ImageIcon(this.imageResolver.getImageURL("observer_c.png").orElse(null),DEFAULT_COLLAPSED_ICON_TEXT);
        CheckBoxNode observers = new CheckBoxNode(this, this.bundle.getString("observers"), true,
                this.om.getXmlCache().getObservers(), expanded, collapsed);
        root.add(observers);

        this.tree = new JTree(root);

        CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();
        tree.setCellRenderer(renderer);

        tree.setCellEditor(new CheckBoxNodeEditor(tree, renderer));
        tree.setEditable(true);

    }

    private ISchemaElement[] resizeArray(ISchemaElement[] oldArray, int newSize) {

        Class elementType = oldArray.getClass().getComponentType();
        ISchemaElement[] newArray = (ISchemaElement[]) java.lang.reflect.Array.newInstance(elementType, newSize);

        System.arraycopy(oldArray, 0, newArray, 0, oldArray.length);

        return newArray;

    }

}

class CheckBoxNodeRenderer extends DefaultTreeCellRenderer {

    private final Color selectionForeground;
    private final Color selectionBackground;
    private final Color textForeground;
    private final Color textBackground;
    private final Color selectionBorderColor;
    private Font selectedTreeFont = null;
    private Font unselectedTreeFont = null;

    public CheckBoxNodeRenderer() {

        this.selectedTreeFont = UIManager.getFont("Tree.font");
        this.selectedTreeFont = this.selectedTreeFont.deriveFont(Font.BOLD);
        this.unselectedTreeFont = UIManager.getFont("Tree.font");
        this.unselectedTreeFont = this.unselectedTreeFont.deriveFont(Font.PLAIN);

        selectionBorderColor = UIManager.getColor("Tree.selectionBorderColor");
        selectionForeground = UIManager.getColor("Tree.selectionForeground");
        selectionBackground = UIManager.getColor("Tree.selectionBackground");
        textForeground = UIManager.getColor("Tree.textForeground");
        textBackground = UIManager.getColor("Tree.textBackground");

    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
            boolean leaf, int row, boolean hasFocus) {

        Component returnValue = null;
        if (leaf) {
            if ((value instanceof DefaultMutableTreeNode)) {
                Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
                if (userObject instanceof SchemaElementLeaf) {
                    SchemaElementLeaf sel = (SchemaElementLeaf) userObject;

                    if (selected) {
                        sel.setForeground(selectionForeground);
                        sel.setBackground(selectionBackground);
                    } else {
                        sel.setForeground(textForeground);
                        sel.setBackground(textBackground);
                    }
                    if (sel.isSelected()) {
                        sel.setFont(this.selectedTreeFont);
                    } else {
                        sel.setFont(this.unselectedTreeFont);
                    }
                    Boolean booleanValue = (Boolean) UIManager.get("Tree.drawsFocusBorderAroundIcon");
                    sel.setFocusPainted((booleanValue != null) && (booleanValue));

                    returnValue = sel;
                }
            }
        } else {
            // Get folder icons
            Icon icon = null;
            CheckBoxNode cbn = null;
            if ((value instanceof DefaultMutableTreeNode)) {
                DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) value;
                Object userObject = dmtn.getUserObject();
                if (userObject instanceof CheckBoxNode) {
                    cbn = (CheckBoxNode) userObject;
                    if (expanded) {
                        icon = ((CheckBoxNode) userObject).getExpandedIcon();
                    } else {
                        icon = ((CheckBoxNode) userObject).getCollapsedIcon();
                    }
                }
            }

            DefaultTreeCellRenderer nonLeafRenderer = new DefaultTreeCellRenderer();
            returnValue = nonLeafRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, false, row,
                    hasFocus);

            // Set folder icon
            DefaultTreeCellRenderer dtcr = null;
            if ((icon != null) && (returnValue instanceof DefaultTreeCellRenderer)) {
                dtcr = (DefaultTreeCellRenderer) returnValue;
                if ((cbn.isSelected()) && (cbn.size() > 0)) {
                    dtcr.setFont(this.selectedTreeFont);
                } else {
                    dtcr.setFont(this.unselectedTreeFont);
                }
                dtcr.setIcon(icon);
                dtcr.setDisabledIcon(icon);
                dtcr.addMouseListener(new CheckBoxNodeRendererMouseListener(cbn));
            }
        }

        return returnValue;

    }

}

class CheckBoxNodeRendererMouseListener implements MouseListener {

    private CheckBoxNode node = null;

    public CheckBoxNodeRendererMouseListener(CheckBoxNode checkBoxNode) {

        this.node = checkBoxNode;

    }

    @Override
    public void mouseClicked(MouseEvent e) {

        if (node.isSelected()) {
            this.node.setSelected(false);
        } else {
            this.node.setSelected(true);
        }

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

}

class CheckBoxNodeEditor extends DefaultTreeCellEditor {

    public CheckBoxNodeEditor(JTree tree, DefaultTreeCellRenderer renderer) {

        super(tree, renderer);

    }

    @Override
    public boolean isCellEditable(EventObject event) {

        return true;

    }

    @Override
    public Component getTreeCellEditorComponent(final JTree tree, Object value, boolean selected, boolean expanded,
            boolean leaf, int row) {

        return this.renderer.getTreeCellRendererComponent(tree, value, true, expanded, leaf, row, true);

    }

}

class CheckBoxNode extends Vector {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String text = null;
    private boolean selected = false;

    private Icon expandedIcon = null;
    private Icon collapsedIcon = null;

    private NewDocumentDialog dialog = null;

    private int selectedChildren = 0;

    public CheckBoxNode(NewDocumentDialog dialog, String text, boolean selected, ISchemaElement[] elements,
            Icon expanded, Icon collapsed) {

        this.dialog = dialog;
        this.text = text;
        this.selected = selected;
        this.expandedIcon = expanded;
        this.collapsedIcon = collapsed;

        if (elements != null) {
            for (ISchemaElement element : elements) {
                this.add(new SchemaElementLeaf(this.dialog, this, element, selected));
            }
            if (selected) {
                this.selectedChildren = elements.length;
            }
        }

    }

    public boolean isSelected() {

        return selected;

    }

    public void setSelected(boolean newValue) {

        selected = newValue;
        Iterator iterator = this.iterator();
        while (iterator.hasNext()) {
            ((SchemaElementLeaf)iterator.next()).setSelected(newValue);
        }

        if (newValue) {
            this.selectedChildren = this.size();
        } else {
            this.selectedChildren = 0;
        }

        // Update tree, if we've a reference to it
        final JTree tree = this.dialog.getTree();
        if (tree != null) {
            EventQueue.invokeLater(tree::updateUI);
        }

    }

    public String getText() {

        return text;

    }

    public void setText(String newValue) {

        text = newValue;

    }

    @Override
    public String toString() {

        return this.text;

    }

    public Icon getExpandedIcon() {

        return this.expandedIcon;

    }

    public Icon getCollapsedIcon() {

        return this.collapsedIcon;

    }

    public void childChangedToSelected() {

        this.selectedChildren++;

        this.selected = this.selectedChildren != 0;

    }

    public void childChangedToUnselected() {

        this.selectedChildren--;

        this.selected = this.selectedChildren != 0;

    }

}

class SchemaElementLeaf extends JCheckBox implements ActionListener {

    private ISchemaElement se = null;
    private NewDocumentDialog dialog = null;
    private CheckBoxNode parentNode = null;

    public SchemaElementLeaf(NewDocumentDialog dialog, CheckBoxNode node, ISchemaElement se, boolean selected) {

        this.dialog = dialog;
        this.parentNode = node;
        this.se = se;
        this.setSelected(selected);

        this.addActionListener(this);

    }

    @Override
    public String toString() {

        if (se == null) {
            return "";
        }

        return se.getDisplayName();

    }

    @Override
    public String getText() {

        return this.toString();

    }

    public ISchemaElement getSchemaElement() {

        return this.se;

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() != this) {
            return;
        }

        if (this.isSelected()) {
            this.parentNode.childChangedToSelected();
        } else {
            this.parentNode.childChangedToUnselected();
        }

        // Update tree, if we've a reference to it
        final JTree tree = this.dialog.getTree();
        if (tree != null) {
            EventQueue.invokeLater(tree::updateUI);
        }

    }

}