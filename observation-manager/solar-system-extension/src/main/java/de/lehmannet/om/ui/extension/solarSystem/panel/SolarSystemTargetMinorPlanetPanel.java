/*
 * ====================================================================
 * /extension/solarSystem/panel/SolarSystemTargetMinorPlanetPanel.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.solarSystem.panel;

import static de.lehmannet.om.ICloneable.copyOrNull;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;

import de.lehmannet.om.IObserver;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.extension.solarSystem.SolarSystemTargetMinorPlanet;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.container.TargetContainer;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.IConfiguration;

public class SolarSystemTargetMinorPlanetPanel extends AbstractPanel {

    private static final long serialVersionUID = -5621507862602343177L;

    private SolarSystemTargetMinorPlanet target = null;

    private TargetContainer targetContainer = null;
    private final ObservationManagerModel model;
    private final IConfiguration configuration;

    public SolarSystemTargetMinorPlanetPanel(IConfiguration configuration, ObservationManagerModel model,
            ITarget target, Boolean editable) throws IllegalArgumentException {

        super(editable);

        if (target != null && !(target instanceof SolarSystemTargetMinorPlanet)) {
            throw new IllegalArgumentException(
                    "Passed ITarget must derive from de.lehmannet.om.extension.solarSystem.SolarSystemTargetMinorPlanet\n");
        }

        this.target = copyOrNull((SolarSystemTargetMinorPlanet) target);
        this.configuration = configuration;
        this.model = model;
        this.createPanel();

    }

    @Override
    public ISchemaElement getSchemaElement() {

        return copyOrNull(this.target);

    }

    @Override
    public ISchemaElement updateSchemaElement() {

        if (this.target == null) {
            return null;
        }

        this.targetContainer.setTarget(this.target);

        ITarget t = this.targetContainer.updateTarget();
        if (t == null) {
            return null;
        } else {
            this.target = (SolarSystemTargetMinorPlanet) t;
        }

        return this.target.copy();

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
            this.target = new SolarSystemTargetMinorPlanet(name, observer);
        } else {
            this.target = new SolarSystemTargetMinorPlanet(name, datasource);
        }

        // Set all other fields
        this.updateSchemaElement();

        return this.target.copy();

    }

    private void createPanel() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        this.setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 4, 1, 45, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.targetContainer = new TargetContainer(this.configuration, this.model, this.target, this.isEditable(),
                true);
        gridbag.setConstraints(this.targetContainer, constraints);
        this.add(this.targetContainer);

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 4, 1, 45, 99);
        constraints.fill = GridBagConstraints.BOTH;
        JLabel lFill = new JLabel("");
        gridbag.setConstraints(lFill, constraints);
        this.add(lFill);

    }

}
