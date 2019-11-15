package de.lehmannet.om.ui.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;

import de.lehmannet.om.GenericTarget;
import de.lehmannet.om.IObserver;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.ui.container.TargetContainer;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.util.ConstraintsBuilder;

public class GenericTargetPanel extends AbstractPanel {

    private static final long serialVersionUID = 9175024145109241504L;

    private ObservationManager observationManager = null;
    private GenericTarget target = null;

    private TargetContainer targetContainer = null;

    public GenericTargetPanel(ObservationManager om, ITarget target, Boolean editable) throws IllegalArgumentException {

        super(editable);

        if ((target != null) && !(target instanceof GenericTarget)) {
            throw new IllegalArgumentException("Passed ITarget must derive from de.lehmannet.om.GenericTarget\n");
        }

        this.target = (GenericTarget) target;
        this.observationManager = om;

        this.createPanel();

    }

    @Override
    public ISchemaElement createSchemaElement() {

        String name = this.targetContainer.getName();
        String datasource = this.targetContainer.getDatasource();
        IObserver observer = this.targetContainer.getObserver();

        // Make sure only datasource or observer is set
        if (!this.targetContainer.checkOrigin(datasource, observer)) {
            return null;
        }

        if (observer != null) {
            this.target = new GenericTarget(name, observer);
        } else {
            this.target = new GenericTarget(name, datasource);
        }

        // Tell target container about newly created target
        this.targetContainer.setTarget(this.target);

        // Set all other fields
        this.updateSchemaElement();

        return this.target;

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
            this.target = (GenericTarget) t;
        }

        return this.target;

    }

    private void createPanel() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        this.setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 4, 1, 45, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.targetContainer = new TargetContainer(this.observationManager, this.target, super.isEditable(), false);
        gridbag.setConstraints(this.targetContainer, constraints);
        this.add(this.targetContainer);

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 4, 1, 45, 99);
        constraints.fill = GridBagConstraints.BOTH;
        JLabel Lfill = new JLabel("");
        gridbag.setConstraints(Lfill, constraints);
        this.add(Lfill);

    }

}
