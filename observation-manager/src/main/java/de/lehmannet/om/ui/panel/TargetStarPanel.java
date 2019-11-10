/* ====================================================================
 * /panel/TargetStarPanel.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;

import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.TargetStar;
import de.lehmannet.om.ui.container.TargetStarContainer;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.util.ConstraintsBuilder;

public class TargetStarPanel extends AbstractPanel {

    private static final long serialVersionUID = -6410013808077608852L;

    private ObservationManager observationManager = null;
    private TargetStar target = null;

    private TargetStarContainer targetContainer = null;

    public TargetStarPanel(ObservationManager om, ITarget target, Boolean editable) throws IllegalArgumentException {

        super(editable);

        if ((target != null) && !(target instanceof TargetStar)) {
            throw new IllegalArgumentException("Passed ITarget must derive from de.lehmannet.om.TargetStar\n");
        }

        this.target = (TargetStar) target;
        this.observationManager = om;

        this.createPanel();

    }

    @Override
    public ISchemaElement getSchemaElement() {

        return this.target;

    }

    @Override
    public ISchemaElement updateSchemaElement() {

        if (this.target == null) {
            return null;
        }

        ITarget t = this.targetContainer.updateTarget();
        if (t == null) {
            return null;
        } else {
            this.target = (TargetStar) t;
        }

        return this.target;

    }

    @Override
    public ISchemaElement createSchemaElement() {

        return this.targetContainer.createSchemaElement();

    }

    private void createPanel() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        this.setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 4, 1, 45, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.targetContainer = new TargetStarContainer(this.observationManager, this.target, super.isEditable());
        gridbag.setConstraints(this.targetContainer, constraints);
        this.add(this.targetContainer);

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 4, 1, 45, 99);
        constraints.fill = GridBagConstraints.BOTH;
        JLabel Lfill = new JLabel("");
        gridbag.setConstraints(Lfill, constraints);
        this.add(Lfill);

    }

}
