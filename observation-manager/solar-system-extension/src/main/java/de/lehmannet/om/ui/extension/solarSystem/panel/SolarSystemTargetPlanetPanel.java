/*
 * ====================================================================
 * /extension/solarSystem/panel/SolarSystemTargetPlanetPanel.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.solarSystem.panel;

import de.lehmannet.om.IObservation;
import de.lehmannet.om.IObserver;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.extension.solarSystem.SolarSystemTarget;
import de.lehmannet.om.extension.solarSystem.SolarSystemTargetPlanet;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.container.TargetContainer;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.IConfiguration;
import de.lehmannet.om.util.Ephemerides;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JLabel;

public class SolarSystemTargetPlanetPanel extends AbstractPanel {

    private static final long serialVersionUID = 2999793701926234578L;

    // private final ResourceBundle bundle =
    // LocaleToolsFactory.extensionInstance().getBundle("de.lehmannet.om.ui.extension.solarSystem.SolarSystem",
    // Locale.getDefault());

    private IConfiguration configuration = null;
    private SolarSystemTargetPlanet target = null;

    private TargetContainer targetContainer = null;
    private IObservation observation = null;

    private final ObservationManagerModel model;

    public SolarSystemTargetPlanetPanel(
            IConfiguration configuration,
            ObservationManagerModel model,
            ITarget target,
            IObservation o,
            Boolean editable)
            throws IllegalArgumentException {

        super(editable);

        if ((target != null) && !(target instanceof SolarSystemTargetPlanet)) {
            throw new IllegalArgumentException(
                    "Passed ITarget must derive from de.lehmannet.om.extension.solarSystem.SolarSystemTargetPlanet\n");
        }

        this.target = (SolarSystemTargetPlanet) target;
        this.observation = o;
        this.configuration = configuration;
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
            this.target = (SolarSystemTargetPlanet) t;
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
            this.target = new SolarSystemTargetPlanet(name, observer);
        } else {
            this.target = new SolarSystemTargetPlanet(name, datasource);
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
            this.targetContainer.setPosition(Ephemerides.getPosition(
                    this.mapPlanetKeysForEphemerides(this.target.getName()),
                    this.observation.getBegin().toZonedDateTime()));
        }
        gridbag.setConstraints(this.targetContainer, constraints);
        this.add(this.targetContainer);

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 4, 1, 45, 99);
        constraints.fill = GridBagConstraints.BOTH;
        JLabel Lfill = new JLabel("");
        gridbag.setConstraints(Lfill, constraints);
        this.add(Lfill);
    }

    private int mapPlanetKeysForEphemerides(String planet) {

        if (SolarSystemTarget.KEY_SUN.equals(planet)) {
            return Ephemerides.SUN;
        } else if (SolarSystemTarget.KEY_MERCURY.equals(planet)) {
            return Ephemerides.MERCURY;
        } else if (SolarSystemTarget.KEY_VENUS.equals(planet)) {
            return Ephemerides.VENUS;
        } else if (SolarSystemTarget.KEY_MOON.equals(planet)) {
            return Ephemerides.MOON;
        } else if (SolarSystemTarget.KEY_MARS.equals(planet)) {
            return Ephemerides.MARS;
        } else if (SolarSystemTarget.KEY_JUPITER.equals(planet)) {
            return Ephemerides.JUPITER;
        } else if (SolarSystemTarget.KEY_SATURN.equals(planet)) {
            return Ephemerides.SATURN;
        } else if (SolarSystemTarget.KEY_URANUS.equals(planet)) {
            return Ephemerides.URANUS;
        } else if (SolarSystemTarget.KEY_NEPTUNE.equals(planet)) {
            return Ephemerides.NEPTUNE;
        }

        return -1;
    }
}
