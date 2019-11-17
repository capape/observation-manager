/* ====================================================================
 * /extension/deepSky/panel/DeepSkyTargetNAPanel.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.deepSky.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;

import de.lehmannet.om.IObserver;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetNA;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.ui.util.ConstraintsBuilder;

public class DeepSkyTargetNAPanel extends AbstractPanel {

    private static final long serialVersionUID = 6133631918037750600L;

    private ObservationManager observationManager = null;
    private DeepSkyTargetNA target = null;

    private DeepSkyTargetContainer deepSkyTargetContainer = null;

    public DeepSkyTargetNAPanel(ObservationManager om, ITarget target, Boolean editable)
            throws IllegalArgumentException {

        super(editable);

        if ((target != null) && !(target instanceof DeepSkyTargetNA)) {
            throw new IllegalArgumentException(
                    "Passed ITarget must derive from de.lehmannet.om.extension.deepSky.DeepSkyTargetNA\n");
        }

        this.target = (DeepSkyTargetNA) target;
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

        ITarget t = this.deepSkyTargetContainer.updateTarget(this.target);
        if (t == null) {
            return null;
        } else {
            this.target = (DeepSkyTargetNA) t;
        }

        return this.target;

    }

    @Override
    public ISchemaElement createSchemaElement() {

        String name = this.deepSkyTargetContainer.getName();
        String datasource = this.deepSkyTargetContainer.getDatasource();
        IObserver observer = this.deepSkyTargetContainer.getObserver();

        // Make sure only datasource or observer is set
        if (this.deepSkyTargetContainer.checkOrigin(datasource, observer)) {
            return null;
        }

        if (observer != null) {
            this.target = new DeepSkyTargetNA(name, observer);
        } else {
            this.target = new DeepSkyTargetNA(name, datasource);
        }

        // Set all other fields
        ITarget t = (ITarget) this.updateSchemaElement();
        if (t == null) {
            return null;
        } else {
            this.target = (DeepSkyTargetNA) t;
        }

        return this.target;

    }

    private void createPanel() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 4, 1, 45, 1);
        this.deepSkyTargetContainer = new DeepSkyTargetContainer(this.observationManager, this.target,
                super.isEditable());
        gridbag.setConstraints(this.deepSkyTargetContainer, constraints);
        this.add(this.deepSkyTargetContainer);

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 4, 1, 45, 99);
        constraints.fill = GridBagConstraints.BOTH;
        JLabel Lfill = new JLabel("");
        gridbag.setConstraints(Lfill, constraints);
        this.add(Lfill);

    }

}
