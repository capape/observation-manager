/* ====================================================================
 * /dialog/TargetSelectorPopup.java
 *
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;

import de.lehmannet.om.ITarget;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.comparator.TargetComparator;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.util.ConstraintsBuilder;

//NO LONGER NEEDED - REPLACED BY SCHEMAELEMENTSELECTORPOPUP
class TargetSelectorPopup extends JDialog implements ActionListener {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private JButton ok = null;
    private JButton cancel = null;

    private TargetSelectionModel tableModel = null;

    public TargetSelectorPopup(ObservationManager om, ObservationManagerModel model, String title, String targetType,
            List<ITarget> preSelectedTargets) {

        super(om, true);

        this.setTitle(title);
        this.setSize(400, 210);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);

        this.tableModel = new TargetSelectionModel(model.getTargets(), targetType, preSelectedTargets);

        this.initDialog();

        this.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();
        if (source instanceof JButton) {
            JButton sourceButton = (JButton) source;
            if (sourceButton.equals(this.ok)) {
                this.dispose();
            } else if (sourceButton.equals(this.cancel)) {
                this.dispose();
                this.tableModel = null; // Set TableModel = null to indicate canceled UI
            }
        } /*
           * else if( source instanceof JCheckBox ) { // Update TableModel int row =
           * Integer.parseInt(((JCheckBox)source).getActionCommand()); // We send the row in the action Command
           * this.tableModel.setSelection(row, ((JCheckBox)source).isSelected()); }
           */

    }

    public List<ITarget> getSelectedTargets() {

        if (this.tableModel == null) {
            return null;
        }

        return this.tableModel.getAllSelectedTargets();

    }

    private void initDialog() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        this.getContentPane().setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 2, 1, 90, 90);
        constraints.fill = GridBagConstraints.BOTH;
        JTable table = new JTable(this.tableModel);
        table.setEnabled(true);
        table.setEditingColumn(1);
        table.setRowSelectionAllowed(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setDoubleBuffered(true);

        table.setToolTipText(AbstractDialog.bundle.getString("popup.targetSelector.table.tooltip"));
        JScrollPane scrollPane = new JScrollPane(table);
        gridbag.setConstraints(scrollPane, constraints);
        this.getContentPane().add(scrollPane);

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 1, 1, 5, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.ok = new JButton(AbstractDialog.bundle.getString("dialog.button.ok"));
        this.ok.addActionListener(this);
        gridbag.setConstraints(this.ok, constraints);
        this.getContentPane().add(this.ok);

        ConstraintsBuilder.buildConstraints(constraints, 1, 1, 1, 1, 5, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.cancel = new JButton(AbstractDialog.bundle.getString("dialog.button.cancel"));
        this.cancel.addActionListener(this);
        gridbag.setConstraints(this.cancel, constraints);
        this.getContentPane().add(this.cancel);

    }

}

class TargetSelectionModel extends AbstractTableModel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Map<ITarget, Boolean> targetMap = null;

    public TargetSelectionModel(ITarget[] targets, String targetFilter, List<ITarget> preSelectedTargets) {

        this.targetMap = new TreeMap<ITarget, Boolean>(new TargetComparator());
        for (ITarget target : targets) {
            if (target.getXSIType().equals(targetFilter)) {
                if (preSelectedTargets.contains(target)) {
                    targetMap.put(target, Boolean.TRUE);
                } else {
                    targetMap.put(target, Boolean.FALSE);
                }
            }

        }

    }

    @Override
    public int getColumnCount() {

        return 2;

    }

    @Override
    public int getRowCount() {

        if (targetMap == null) {
            return 5;
        }

        return targetMap.size();

    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {

        return columnIndex == 1;

    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        ITarget keyTarget = (ITarget) this.targetMap.keySet().toArray(new Object[0])[rowIndex];

        if (keyTarget == null) {
            return "";
        }

        switch (columnIndex) {
        case 0: {
            return keyTarget.getDisplayName(); // Returns a String
        }
        case 1: {
            return this.targetMap.get(keyTarget); // Returns a Boolean
        }
        default:
            return "";
        }

    }

    @Override
    public void setValueAt(Object o, int row, int column) {

        if (column == 1) {
            if (o instanceof Boolean) {
                this.setSelection(row, (Boolean) o);
            }
        }

    }

    @Override
    public Class<?> getColumnClass(int c) {

        switch (c) {
        case 0: {
            return String.class;
        }
        case 1: {
            return Boolean.class;
        }
        }

        return String.class;

    }

    private void setSelection(int row, boolean selection) {

        ITarget keyTarget = (ITarget) this.targetMap.keySet().toArray(new Object[0])[row];
        this.targetMap.remove(keyTarget);
        this.targetMap.put(keyTarget, selection);

    }

    public List<ITarget> getAllSelectedTargets() {

        List<ITarget> result = new ArrayList<>();

        Iterator<ITarget> keyIterator = this.targetMap.keySet().iterator();
        ITarget current = null;
        Boolean currentValue = null;
        while (keyIterator.hasNext()) {
            current = keyIterator.next();
            currentValue = this.targetMap.get(current);
            if (currentValue) {
                result.add(current);
            }
        }

        return result;

    }

}
