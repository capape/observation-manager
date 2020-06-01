/* ====================================================================
 * /extension/solarSystem/panel/SolarSystemTargetCometPanel.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.solarSystem.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;

import de.lehmannet.om.IObserver;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.extension.solarSystem.SolarSystemTargetComet;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.container.TargetContainer;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.ui.util.ConstraintsBuilder;

public class SolarSystemTargetCometPanel extends AbstractPanel {

    private static final long serialVersionUID = -4640850780189528128L;

    // private final PropertyResourceBundle bundle =
    // (PropertyResourceBundle)ResourceBundle.getBundle("de.lehmannet.om.ui.extension.solarSystem.SolarSystem",
    // Locale.getDefault());

    private ObservationManager observationManager = null;
    private SolarSystemTargetComet target = null;

    private TargetContainer targetContainer = null;
    private ObservationManagerModel model;

    public SolarSystemTargetCometPanel(ObservationManager om, ObservationManagerModel model, ITarget target, Boolean editable)
            throws IllegalArgumentException {

        super(editable);

        if ((target != null) && !(target instanceof SolarSystemTargetComet)) {
            throw new IllegalArgumentException(
                    "Passed ITarget must derive from de.lehmannet.om.extension.solarSystem.SolarSystemTargetComet\n");
        }

        this.target = (SolarSystemTargetComet) target;
        this.observationManager = om;
        this.model = model;
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

        this.targetContainer.setTarget(this.target);

        ITarget t = this.targetContainer.updateTarget();
        if (t == null) {
            return null;
        } else {
            this.target = (SolarSystemTargetComet) t;
        }

        this.updateUI();

        return this.target;

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
            this.target = new SolarSystemTargetComet(name, observer);
        } else {
            this.target = new SolarSystemTargetComet(name, datasource);
        }

        // Set all other fields
        this.updateSchemaElement();

        return this.target;

    }

    private void createPanel() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        this.setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 4, 1, 45, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.targetContainer = new TargetContainer(this.observationManager.getConfiguration(), this.model, this.target, this.isEditable(), true);
        gridbag.setConstraints(this.targetContainer, constraints);
        this.add(this.targetContainer);

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 4, 1, 45, 99);
        constraints.fill = GridBagConstraints.BOTH;
        JLabel Lfill = new JLabel("");
        gridbag.setConstraints(Lfill, constraints);
        this.add(Lfill);

    }

}
