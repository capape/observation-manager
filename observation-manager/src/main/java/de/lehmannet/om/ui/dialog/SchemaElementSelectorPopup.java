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

    public SchemaElementSelectorPopup(ObservationManager om, String title, String xsiType, List<? extends ISchemaElement> preSelectedElements,
            boolean multipleSelection, SchemaElementConstants schemaElement) throws IllegalArgumentException, NoSuchElementException { // See
                                                                                                                    // SchemaElementConstants

        super(om);

        this.setTitle(title);
        this.setSize(SchemaElementSelectorPopup.serialVersionUID, 660, 250);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);

        ISchemaElement[] elements = null;
        switch (schemaElement) {
        case IMAGER: {
            elements = om.getXmlCache().getImagers();
            break;
        }
        case EYEPIECE: {
            elements = om.getXmlCache().getEyepieces();
            break;
        }
        case FILTER: {
            elements = om.getXmlCache().getFilters();
            break;
        }
        case LENS: {
            elements = om.getXmlCache().getLenses();
            break;
        }
        case OBSERVATION: {
            elements = om.getXmlCache().getObservations();
            break;
        }
        case OBSERVER: {
            elements = om.getXmlCache().getObservers();
            break;
        }
        case SCOPE: {
            elements = om.getXmlCache().getScopes();
            break;
        }
        case SESSION: {
            elements = om.getXmlCache().getSessions();
            break;
        }
        case SITE: {
            elements = om.getXmlCache().getSites();
            break;
        }
        case TARGET: {
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
        }

    }

    public List<ISchemaElement> getAllSelectedElements() {

        if (this.tableModel == null) {
            return null;
        }

        return this.tableModel.getAllSelectedElements();

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