/*
 * ====================================================================
 * /dialog/ObservationDialog.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.dialog;

import de.lehmannet.om.IObservation;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ObservationManagerContext;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.cache.UIDataCache;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.panel.ObservationDialogPanel;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import javax.swing.JButton;

public class ObservationDialog extends AbstractDialog {

    private static final long serialVersionUID = -4963447134614319943L;

    private JButton next = new JButton("Next");
    private boolean createAdditionalObservation = false;

    public ObservationDialog(
            ObservationManagerContext context,
            ObservationManager om,
            ObservationManagerModel model,
            IObservation observation,
            UIDataCache uiCache) {

        this(context, om, model, observation, null, uiCache);
    }

    public ObservationDialog(
            ObservationManagerContext context,
            ObservationManager om,
            ObservationManagerModel model,
            IObservation observation,
            ISchemaElement se,
            UIDataCache uiCache) {

        super(om, model, om.getUiHelper(), new ObservationDialogPanel(context, om, model, observation, se, uiCache));

        if (observation == null) {
            this.setTitle(AbstractDialog.bundle.getString("dialog.observation.title"));
        } else {
            this.setTitle(AbstractDialog.bundle.getString("dialog.observation.titleEdit") + " "
                    + observation.getDisplayName());
        }

        // Add additional next button
        GridBagLayout gridbag = (GridBagLayout) this.getContentPane().getLayout();

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.fill = GridBagConstraints.BOTH;
        this.getContentPane().setLayout(gridbag);

        // Set next button as second button
        ConstraintsBuilder.buildConstraints(constraints, 2, 1, 1, 1, 100, 5);
        this.next = new JButton(AbstractDialog.bundle.getString("dialog.button.next"));
        this.next.addActionListener(this);
        gridbag.setConstraints(this.next, constraints);
        this.getContentPane().add(this.next);

        // Move cancel button to third position
        this.getContentPane().remove(this.cancel);
        ConstraintsBuilder.buildConstraints(constraints, 3, 1, 1, 1, 100, 5);
        gridbag.setConstraints(this.cancel, constraints);
        this.getContentPane().add(this.cancel);

        this.setSize(serialVersionUID, om.getSize().width - 200, 620);
        this.setLocationRelativeTo(om);

        this.setVisible(true);
    }

    public IObservation getObservation() {

        if (this.schemaElement != null) {
            return (IObservation) this.schemaElement;
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
                this.actionPerformed(new ActionEvent(this.positive, ActionEvent.ACTION_PERFORMED, "Next pressed"));
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
