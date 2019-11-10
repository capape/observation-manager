/* ====================================================================
 * /dialog/ObservationDialog.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import de.lehmannet.om.IObservation;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.panel.ObservationDialogPanel;
import de.lehmannet.om.ui.util.ConstraintsBuilder;

public class ObservationDialog extends AbstractDialog implements ActionListener {

    private static final long serialVersionUID = -4963447134614319943L;

    private JButton next = new JButton("Next");
    private boolean createAdditionalObservation = false;

    public ObservationDialog(ObservationManager om, IObservation observation) {

        this(om, observation, null);

    }

    public ObservationDialog(ObservationManager om, IObservation observation, ISchemaElement se) {

        super(om, new ObservationDialogPanel(om, observation, se));

        if (observation == null) {
            super.setTitle(AbstractDialog.bundle.getString("dialog.observation.title"));
        } else {
            super.setTitle(AbstractDialog.bundle.getString("dialog.observation.titleEdit") + " "
                    + observation.getDisplayName());
        }

        // Add additional next button
        GridBagLayout gridbag = (GridBagLayout) super.getContentPane().getLayout();

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.fill = GridBagConstraints.BOTH;
        super.getContentPane().setLayout(gridbag);

        // Set next button as second button
        ConstraintsBuilder.buildConstraints(constraints, 2, 1, 1, 1, 100, 5);
        this.next = new JButton(AbstractDialog.bundle.getString("dialog.button.next"));
        this.next.addActionListener(this);
        gridbag.setConstraints(this.next, constraints);
        super.getContentPane().add(this.next);

        // Move cancel button to third position
        super.getContentPane().remove(super.cancel);
        ConstraintsBuilder.buildConstraints(constraints, 3, 1, 1, 1, 100, 5);
        gridbag.setConstraints(super.cancel, constraints);
        super.getContentPane().add(super.cancel);

        super.setSize(ObservationDialog.serialVersionUID, om.getSize().width - 200, 620);
        super.setLocationRelativeTo(om);

        super.setVisible(true);

    }

    public IObservation getObservation() {

        if (super.schemaElement != null) {
            return (IObservation) super.schemaElement;
        }

        return null;

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();
        if (source instanceof JButton) {
            if (source.equals(this.next)) {
                // Flag dialog so that after closure it'll open again for next observation
                this.createAdditionalObservation = true;
                // Trigger OK event so that current observation gets created
                super.actionPerformed(new ActionEvent(super.positive, ActionEvent.ACTION_PERFORMED, "Next pressed"));
                return;
            }
        }

        // Call action handler from super class for cancel and OK buttons
        super.actionPerformed(e);

    }

    public boolean isCreateAdditionalObservation() {

        return this.createAdditionalObservation;

    }

}