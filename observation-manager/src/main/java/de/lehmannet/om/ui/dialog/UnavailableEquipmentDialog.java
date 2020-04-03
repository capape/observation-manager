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
import java.util.EventObject;
import java.util.Iterator;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;

import de.lehmannet.om.IEquipment;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.util.ConstraintsBuilder;

public class UnavailableEquipmentDialog extends OMDialog implements ActionListener {

    private static final long serialVersionUID = 7709563738807903171L;

    // Constants used as ActionCommands
    private static final String AC_OK = "ok";
    private static final String AC_CANCEL = "cancel";

    private final PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
            .getBundle("ObservationManager", Locale.getDefault());

    private ObservationManager om = null;
    private JTree tree = null;

    private boolean changedElements = false; // Indicates whether user did change some elements

    public UnavailableEquipmentDialog(ObservationManager om) {

        super(om);

        this.om = om;

        super.setTitle(this.bundle.getString("dialog.unavailableEquipment.title"));
        super.setSize(UnavailableEquipmentDialog.serialVersionUID, 380, 360);
        super.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        super.setLocationRelativeTo(om);

        this.initTree();

        this.initDialog();

        this.setVisible(true);
        // this.pack();

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (UnavailableEquipmentDialog.AC_OK.equals(e.getActionCommand())) {
            this.markEquipmentUnavailable();
            this.om.updateLeft();
        }

        // Close window
        super.dispose();

    }

    public JTree getTree() {

        return this.tree;

    }

    public boolean changedElements() {

        return this.changedElements;

    }

    private void markEquipmentUnavailable() {

        TreeModel model = this.tree.getModel();

        Object current = model.getRoot();
        Object node = null;
        Object leaf = null;
        for (int i = 0; i < model.getChildCount(current); i++) { // Iterate of all schema element type nodes
            node = this.tree.getModel().getChild(current, i);

            if (node instanceof DefaultMutableTreeNode) {
                if (((DefaultMutableTreeNode) node).getUserObject() instanceof CheckBoxEquipmentNode) { // Node is
                                                                                                        // selected?
                    for (int j = 0; j < model.getChildCount(node); j++) { // Iterate over all children
                        leaf = model.getChild(node, j);
                        if (leaf instanceof DefaultMutableTreeNode) {
                            Object luo = ((DefaultMutableTreeNode) leaf).getUserObject();
                            if (luo instanceof EquipmentLeaf) {
                                EquipmentLeaf sel = (EquipmentLeaf) luo;
                                ISchemaElement se = sel.getSchemaElement();
                                if (!sel.isSelected()) { // Child is not selected?
                                    if (se instanceof IEquipment) {
                                        ((IEquipment) se).setAvailability(false);
                                        this.changedElements = true;
                                    }
                                } else { // Might be reactivating of equipment
                                    if (se instanceof IEquipment) {
                                        ((IEquipment) se).setAvailability(true);
                                        this.changedElements = true;
                                    }
                                }
                            }
                        }
                    }
                    // }
                }
            }
        }

    }

    private void initDialog() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        super.getContentPane().setLayout(gridbag);

        this.tree.setEnabled(true);
        this.tree.setToolTipText(this.bundle.getString("dialog.unavailableEquipment.tooltip.tree"));
        JScrollPane scrollPanel = new JScrollPane(this.tree);
        scrollPanel.setBorder(
                BorderFactory.createTitledBorder(this.bundle.getString("dialog.unavailableEquipment.border.tree")));
        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 2, 1, 50, 92);
        constraints.fill = GridBagConstraints.BOTH;
        gridbag.setConstraints(scrollPanel, constraints);
        super.getContentPane().add(scrollPanel);

        JButton ok = new JButton(this.bundle.getString("dialog.button.ok"));
        ok.setActionCommand(UnavailableEquipmentDialog.AC_OK);
        ok.addActionListener(this);
        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 1, 1, 25, 4);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(ok, constraints);
        super.getContentPane().add(ok);

        JButton cancel = new JButton(this.bundle.getString("dialog.button.cancel"));
        cancel.setActionCommand(UnavailableEquipmentDialog.AC_CANCEL);
        cancel.addActionListener(this);
        ConstraintsBuilder.buildConstraints(constraints, 1, 1, 1, 1, 25, 4);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(cancel, constraints);
        super.getContentPane().add(cancel);

    }

    private void initTree() {

        String imagesDir = this.om.getInstallDir().getPathForFolder( "images");
        Icon expanded = null;
        Icon collapsed = null;

        // The root node
        CheckBoxEquipmentNode root = new CheckBoxEquipmentNode(this, this.bundle.getString("treeRoot"), null, null,
                null);

        // Create all schema element nodes
        expanded = new ImageIcon(imagesDir + "scope_e.png");
        collapsed = new ImageIcon(imagesDir + "scope_c.png");
        CheckBoxEquipmentNode scopes = new CheckBoxEquipmentNode(this, this.bundle.getString("scopes"),
                this.om.getXmlCache().getScopes(), expanded, collapsed);
        root.add(scopes);

        expanded = new ImageIcon(imagesDir + "imager_e.png");
        collapsed = new ImageIcon(imagesDir + "imager_c.png");
        CheckBoxEquipmentNode imagers = new CheckBoxEquipmentNode(this, this.bundle.getString("imagers"),
                this.om.getXmlCache().getImagers(), expanded, collapsed);
        root.add(imagers);

        expanded = new ImageIcon(imagesDir + "filter_e.png");
        collapsed = new ImageIcon(imagesDir + "filter_c.png");
        CheckBoxEquipmentNode filters = new CheckBoxEquipmentNode(this, this.bundle.getString("filters"),
                this.om.getXmlCache().getFilters(), expanded, collapsed);
        root.add(filters);

        expanded = new ImageIcon(imagesDir + "eyepiece_e.png");
        collapsed = new ImageIcon(imagesDir + "eyepiece_c.png");
        CheckBoxEquipmentNode eyepieces = new CheckBoxEquipmentNode(this, this.bundle.getString("eyepieces"),
                this.om.getXmlCache().getEyepieces(), expanded, collapsed);
        root.add(eyepieces);

        expanded = new ImageIcon(imagesDir + "lens_e.png");
        collapsed = new ImageIcon(imagesDir + "lens_c.png");
        CheckBoxEquipmentNode lenses = new CheckBoxEquipmentNode(this, this.bundle.getString("lenses"),
                this.om.getXmlCache().getLenses(), expanded, collapsed);
        root.add(lenses);

        this.tree = new JTree(root);

        CheckBoxNodeEquipmentRenderer renderer = new CheckBoxNodeEquipmentRenderer();
        tree.setCellRenderer(renderer);

        tree.setCellEditor(new CheckBoxNodeEquipmentEditor(tree, renderer));
        tree.setEditable(true);

    }

    /*
     * private ISchemaElement[] resizeArray(ISchemaElement[] oldArray, int newSize) {
     * 
     * Class elementType = oldArray.getClass().getComponentType(); ISchemaElement[] newArray =
     * (ISchemaElement[])java.lang.reflect.Array.newInstance(elementType, newSize);
     * 
     * System.arraycopy(oldArray, 0, newArray, 0, oldArray.length);
     * 
     * return newArray;
     * 
     * }
     */

}

class CheckBoxNodeEquipmentRenderer extends DefaultTreeCellRenderer {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final Color selectionForeground;
    private final Color selectionBackground;
    private final Color textForeground;
    private final Color textBackground;
    private final Color selectionBorderColor;
    private Font selectedTreeFont = null;
    private Font unselectedTreeFont = null;

    public CheckBoxNodeEquipmentRenderer() {

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
                if (userObject instanceof EquipmentLeaf) {
                    EquipmentLeaf sel = (EquipmentLeaf) userObject;

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
            CheckBoxEquipmentNode cbn = null;
            if ((value instanceof DefaultMutableTreeNode)) {
                DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) value;
                Object userObject = dmtn.getUserObject();
                if (userObject instanceof CheckBoxEquipmentNode) {
                    cbn = (CheckBoxEquipmentNode) userObject;
                    if (expanded) {
                        icon = ((CheckBoxEquipmentNode) userObject).getExpandedIcon();
                    } else {
                        icon = ((CheckBoxEquipmentNode) userObject).getCollapsedIcon();
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
                dtcr.addMouseListener(new CheckBoxNodeEquipmentRendererMouseListener(cbn));
            }
        }

        return returnValue;

    }

}

class CheckBoxNodeEquipmentRendererMouseListener implements MouseListener {

    private CheckBoxEquipmentNode node = null;

    public CheckBoxNodeEquipmentRendererMouseListener(CheckBoxEquipmentNode checkBoxNode) {

        this.node = checkBoxNode;

    }

    @Override
    public void mouseClicked(MouseEvent e) {

        // Changed selection
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

class CheckBoxNodeEquipmentEditor extends DefaultTreeCellEditor {

    public CheckBoxNodeEquipmentEditor(JTree tree, DefaultTreeCellRenderer renderer) {

        super(tree, renderer);

    }

    @Override
    public boolean isCellEditable(EventObject event) {

        return true;

    }

    @Override
    public Component getTreeCellEditorComponent(final JTree tree, Object value, boolean selected, boolean expanded,
            boolean leaf, int row) {

        return super.renderer.getTreeCellRendererComponent(tree, value, true, expanded, leaf, row, true);

    }

}

class CheckBoxEquipmentNode extends Vector {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String text = null;
    private boolean selected = false;

    private Icon expandedIcon = null;
    private Icon collapsedIcon = null;

    private UnavailableEquipmentDialog dialog = null;

    private int selectedChildren = 0;

    public CheckBoxEquipmentNode(UnavailableEquipmentDialog dialog, String text, ISchemaElement[] elements,
            Icon expanded, Icon collapsed) {

        this.dialog = dialog;
        this.text = text;
        this.expandedIcon = expanded;
        this.collapsedIcon = collapsed;

        int noAvailable = 0;

        if (elements != null) {
            for (ISchemaElement element : elements) {
                super.add(new EquipmentLeaf(this.dialog, this, element));
                if (((IEquipment) element).isAvailable()) {
                    noAvailable++;
                }
            }
            if (elements.length > 0) {
                this.selectedChildren = noAvailable;
                if (noAvailable > 0) {
                    this.selected = true;
                }
            }
        }

    }

    public boolean isSelected() {

        return selected;

    }

    public void setSelected(boolean newValue) {

        selected = newValue;
        Iterator iterator = super.iterator();
        while (iterator.hasNext()) {
            ((EquipmentLeaf) iterator.next()).setSelected(newValue);
        }

        if (newValue) {
            this.selectedChildren = super.size();
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

class EquipmentLeaf extends JCheckBox implements ActionListener {

    /**
     *
     */
    private static final long serialVersionUID = -8497892001962915732L;
    private ISchemaElement se = null;
    private UnavailableEquipmentDialog dialog = null;
    private CheckBoxEquipmentNode parentNode = null;

    public EquipmentLeaf(UnavailableEquipmentDialog dialog, CheckBoxEquipmentNode node, ISchemaElement se) {

        this.dialog = dialog;
        this.parentNode = node;
        this.se = se;
        super.setSelected(((IEquipment) se).isAvailable());

        super.addActionListener(this);

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

        if (super.isSelected()) {
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