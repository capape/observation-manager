/*
 * ====================================================================
 * /extension/solarSystem/panel/SolarSystemTargetSunPanel.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.solarSystem.panel;

import de.lehmannet.om.IObservation;
import de.lehmannet.om.IObserver;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.extension.solarSystem.SolarSystemTargetSun;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.container.TargetContainer;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.IConfiguration;
import de.lehmannet.om.util.Ephemerides;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JLabel;

public class SolarSystemTargetSunPanel extends AbstractPanel {

    private static final long serialVersionUID = -1272347298165613077L;
    // private final ResourceBundle bundle =
    // LocaleToolsFactory.extensionInstance().getBundle("de.lehmannet.om.ui.extension.solarSystem.SolarSystem",
    // Locale.getDefault());

    private IConfiguration configuration = null;
    private SolarSystemTargetSun target = null;
    private IObservation observation = null;

    private TargetContainer targetContainer = null;
    private final ObservationManagerModel model;

    public SolarSystemTargetSunPanel(
            IConfiguration om, ObservationManagerModel model, ITarget target, IObservation o, Boolean editable)
            throws IllegalArgumentException {

        super(editable);

        if ((target != null) && !(target instanceof SolarSystemTargetSun)) {
            throw new IllegalArgumentException(
                    "Passed ITarget must derive from de.lehmannet.om.extension.solarSystem.SolarSystemTargetSun\n");
        }

        this.target = (SolarSystemTargetSun) target;
        this.observation = o;
        this.configuration = om;
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
            this.target = (SolarSystemTargetSun) t;
        }

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
            this.target = new SolarSystemTargetSun(name, observer);
        } else {
            this.target = new SolarSystemTargetSun(name, datasource);
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
        this.targetContainer =
                new TargetContainer(this.configuration, this.model, this.target, this.isEditable(), true);
        if ((!this.isEditable()) && (this.observation != null)) {
            this.targetContainer.setPosition(
                    Ephemerides.getSunPosition(this.observation.getBegin().toZonedDateTime()));
        }
        gridbag.setConstraints(this.targetContainer, constraints);
        this.add(this.targetContainer);

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 4, 1, 45, 99);
        constraints.fill = GridBagConstraints.BOTH;
        JLabel Lfill = new JLabel("");
        gridbag.setConstraints(Lfill, constraints);
        this.add(Lfill);
    }
}
