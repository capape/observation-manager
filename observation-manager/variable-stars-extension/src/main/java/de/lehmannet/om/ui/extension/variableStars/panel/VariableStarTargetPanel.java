/*
 * ====================================================================
 * /extension/variableStars/panel/VariableStarTargetPanel.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.variableStars.panel;

import static de.lehmannet.om.ICloneable.copyOrNull;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import de.lehmannet.om.IObserver;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.extension.variableStars.TargetVariableStar;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.container.TargetStarContainer;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.IConfiguration;
import de.lehmannet.om.ui.util.OMLabel;
import de.lehmannet.om.util.FloatUtil;

public class VariableStarTargetPanel extends AbstractPanel {

    private static final long serialVersionUID = -7456755701627150427L;

    private final ResourceBundle bundle = ResourceBundle
            .getBundle("de.lehmannet.om.ui.extension.variableStars.VariableStar", Locale.getDefault());

    private TargetVariableStar target = null;

    private TargetStarContainer targetContainer = null;

    private JTextField type = null;
    private JTextField maxApparentMag = null;
    private JTextField period = null;
    private final ObservationManagerModel model;
    private final IConfiguration configuration;

    public VariableStarTargetPanel(IConfiguration configuration, ObservationManagerModel model,
            TargetVariableStar target, Boolean editable) throws IllegalArgumentException {

        super(editable);

        this.target = copyOrNull(target);
        this.configuration = configuration;
        this.model = model;

        this.createPanel();

        if (this.target != null) {
            this.loadSchemaElement();
        }

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

        ITarget t = this.targetContainer.updateTarget();
        if (t == null) {
            return null;
        } else {
            this.target = (TargetVariableStar) t;
        }

        // Optional parameters

        String type = this.type.getText().trim();
        if (!"".equals(type)) {
            this.target.setType(type);
        }

        String magnitude = this.maxApparentMag.getText().trim();
        if (!"".equals(magnitude)) {
            float mag = FloatUtil.parseFloat(magnitude);
            if (mag > this.target.getMagnitudeApparent()) { // Greater value means less brightness
                this.createWarning(this.bundle.getString("panel.variableStarTarget.warning.maxSmallerMin"));
                return null;
            }
            this.target.setMaxMagnitudeApparent(mag);
        }

        String periodString = this.period.getText().trim();
        if (!"".equals(periodString)) {
            float p = FloatUtil.parseFloat(periodString);
            this.target.setPeriod(p);
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
            this.target = new TargetVariableStar(name, observer);
        } else {
            this.target = new TargetVariableStar(name, datasource);
        }
        this.targetContainer.setTarget(this.target); // Otherwise updateSchemaElement will fail

        // Set all other fields
        ITarget t = (ITarget) this.updateSchemaElement();
        if (t == null) {
            return null;
        } else {
            this.target = (TargetVariableStar) t;
        }

        return this.target.copy();

    }

    private void loadSchemaElement() {

        // Container data will be loaded while construction of container in
        // createPanel method

        String type = this.target.getType();
        if (type != null) {
            this.type.setText(type);
        } else {
            this.type.setText("");
        }
        this.type.setEditable(this.isEditable());

        float maxAppMag = this.target.getMaxApparentMag();
        if (!Float.isNaN(maxAppMag)) {
            this.maxApparentMag.setText("" + maxAppMag);
        } else {
            this.maxApparentMag.setText("");
        }
        this.maxApparentMag.setEditable(this.isEditable());

        float period = this.target.getPeriod();
        if (!Float.isNaN(period)) {
            this.period.setText("" + period);
        } else {
            this.period.setText("");
        }
        this.period.setEditable(this.isEditable());

    }

    private void createPanel() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 5, 1, 45, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.targetContainer = new TargetStarContainer(this.configuration, this.model, this.target, this.isEditable());
        gridbag.setConstraints(this.targetContainer, constraints);
        this.add(this.targetContainer);
        // Change labels in container
        this.targetContainer.labelMagnitudeApp
                .setText(this.bundle.getString("panel.variableStarTarget.label.minApparentMag"));
        this.targetContainer.labelMagnitudeApp
                .setToolTipText(this.bundle.getString("panel.variableStarTarget.tooltip.minApparentMag"));
        this.targetContainer.magnitudeApparent
                .setToolTipText(this.bundle.getString("panel.variableStarTarget.tooltip.minApparentMag"));

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 5, 1, 100, 1);
        JSeparator seperator1 = new JSeparator(SwingConstants.HORIZONTAL);
        gridbag.setConstraints(seperator1, constraints);
        this.add(seperator1);

        ConstraintsBuilder.buildConstraints(constraints, 0, 2, 1, 1, 1, 1);
        OMLabel lMaxAppMag = new OMLabel(this.bundle.getString("panel.variableStarTarget.label.maxApparentMag"), false);
        lMaxAppMag.setToolTipText(this.bundle.getString("panel.variableStarTarget.tooltip.maxApparentMag"));
        gridbag.setConstraints(lMaxAppMag, constraints);
        this.add(lMaxAppMag);
        ConstraintsBuilder.buildConstraints(constraints, 1, 2, 1, 1, 15, 1);
        this.maxApparentMag = new JTextField();
        this.maxApparentMag.setToolTipText(this.bundle.getString("panel.variableStarTarget.tooltip.maxApparentMag"));
        gridbag.setConstraints(this.maxApparentMag, constraints);
        this.add(this.maxApparentMag);

        // Dummy for filling space
        ConstraintsBuilder.buildConstraints(constraints, 2, 2, 3, 1, 50, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        JLabel lDummy = new JLabel("");
        gridbag.setConstraints(lDummy, constraints);
        this.add(lDummy);

        ConstraintsBuilder.buildConstraints(constraints, 0, 3, 1, 1, 1, 1);
        OMLabel lType = new OMLabel(this.bundle.getString("panel.variableStarTarget.label.type"), false);
        lType.setToolTipText(this.bundle.getString("panel.variableStarTarget.tooltip.type"));
        gridbag.setConstraints(lType, constraints);
        this.add(lType);
        ConstraintsBuilder.buildConstraints(constraints, 1, 3, 1, 1, 15, 1);
        this.type = new JTextField();
        this.type.setToolTipText(this.bundle.getString("panel.variableStarTarget.tooltip.type"));
        gridbag.setConstraints(this.type, constraints);
        this.add(this.type);

        ConstraintsBuilder.buildConstraints(constraints, 2, 3, 1, 1, 1, 1);
        OMLabel lPeriod = new OMLabel(this.bundle.getString("panel.variableStarTarget.label.period"),
                SwingConstants.RIGHT, false);
        lPeriod.setToolTipText(this.bundle.getString("panel.variableStarTarget.tooltip.period"));
        gridbag.setConstraints(lPeriod, constraints);
        this.add(lPeriod);
        ConstraintsBuilder.buildConstraints(constraints, 3, 3, 1, 1, 1, 1);
        this.period = new JTextField(5);
        this.period.setToolTipText(this.bundle.getString("panel.variableStarTarget.tooltip.period"));
        gridbag.setConstraints(this.period, constraints);
        this.add(this.period);
        ConstraintsBuilder.buildConstraints(constraints, 4, 3, 1, 1, 1, 1);
        OMLabel lDays = new OMLabel(this.bundle.getString("panel.variableStarTarget.label.period.days"), false);
        lDays.setToolTipText(this.bundle.getString("panel.variableStarTarget.tooltip.period"));
        gridbag.setConstraints(lDays, constraints);
        this.add(lDays);

        ConstraintsBuilder.buildConstraints(constraints, 0, 4, 5, 1, 5, 90);
        constraints.fill = GridBagConstraints.BOTH;
        JLabel lFill = new JLabel("");
        gridbag.setConstraints(lFill, constraints);
        this.add(lFill);

    }

}
