/* ====================================================================
 * /dialog/SchemaElementSelectorPopup.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.NoSuchElementException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;

import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.navigation.tableModel.ExtendedSchemaTableModel;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.util.SchemaElementConstants;

public class SchemaElementSelectorPopup extends OMDialog implements ActionListener {

    private static final long serialVersionUID = -8232319636708320688L;

    private JButton ok = null;
    private JButton cancel = null;

    private ExtendedSchemaTableModel tableModel = null;
    private boolean multipleSelection = false;

    public SchemaElementSelectorPopup(ObservationManager om, String title, String xsiType, List preSelectedElements,
            boolean multipleSelection, int schemaElement) throws IllegalArgumentException, NoSuchElementException { // See
                                                                                                                    // SchemaElementConstants

        super(om);

        super.setTitle(title);
        super.setSize(SchemaElementSelectorPopup.serialVersionUID, 660, 250);
        super.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        super.setLocationRelativeTo(null);

        this.multipleSelection = multipleSelection;

        ISchemaElement[] elements = null;
        switch (schemaElement) {
        case SchemaElementConstants.IMAGER: {
            elements = om.getXmlCache().getImagers();
            break;
        }
        case SchemaElementConstants.EYEPIECE: {
            elements = om.getXmlCache().getEyepieces();
            break;
        }
        case SchemaElementConstants.FILTER: {
            elements = om.getXmlCache().getFilters();
            break;
        }
        case SchemaElementConstants.LENS: {
            elements = om.getXmlCache().getLenses();
            break;
        }
        case SchemaElementConstants.OBSERVATION: {
            elements = om.getXmlCache().getObservations();
            break;
        }
        case SchemaElementConstants.OBSERVER: {
            elements = om.getXmlCache().getObservers();
            break;
        }
        case SchemaElementConstants.SCOPE: {
            elements = om.getXmlCache().getScopes();
            break;
        }
        case SchemaElementConstants.SESSION: {
            elements = om.getXmlCache().getSessions();
            break;
        }
        case SchemaElementConstants.SITE: {
            elements = om.getXmlCache().getSites();
            break;
        }
        case SchemaElementConstants.TARGET: {
            elements = om.getXmlCache().getTargets();
            break;
        }
        default: {
            throw new IllegalArgumentException(
                    "Passed schemaElement ID was wrong. Use SchemaElementConstants for retriving ID."); // SchemaElementID
                                                                                                        // was wrong
        }
        }

        if (elements == null) {
            throw new NoSuchElementException("No element of type: " + schemaElement + " found in XML cache");
        }

        this.tableModel = new ExtendedSchemaTableModel(elements, schemaElement, xsiType, multipleSelection,
                preSelectedElements);

        this.initDialog();

        super.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();
        if (source instanceof JButton) {
            JButton sourceButton = (JButton) source;
            if (sourceButton.equals(this.ok)) {
                super.dispose();
            } else if (sourceButton.equals(this.cancel)) {
                super.dispose();
                this.tableModel = null; // Set TableModel = null to indicate canceled UI
            }
        }

    }

    public List getAllSelectedElements() {

        if (this.tableModel == null) {
            return null;
        }

        return this.tableModel.getAllSelectedElements();

    }

    private void initDialog() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        super.getContentPane().setLayout(gridbag);

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
        super.getContentPane().add(scrollPane);

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 1, 1, 5, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.ok = new JButton(AbstractDialog.bundle.getString("dialog.button.ok"));
        this.ok.addActionListener(this);
        gridbag.setConstraints(this.ok, constraints);
        super.getContentPane().add(this.ok);

        ConstraintsBuilder.buildConstraints(constraints, 1, 1, 1, 1, 5, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.cancel = new JButton(AbstractDialog.bundle.getString("dialog.button.cancel"));
        this.cancel.addActionListener(this);
        gridbag.setConstraints(this.cancel, constraints);
        super.getContentPane().add(this.cancel);

    }

}