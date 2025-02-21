/*
 * ====================================================================
 * /extension/deepSky/panel/DeepSkyTargetGCPanel.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.deepSky.panel;

import de.lehmannet.om.IObserver;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetGC;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.OMLabel;
import de.lehmannet.om.ui.util.UserInterfaceHelper;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class DeepSkyTargetGCPanel extends AbstractPanel {

    private static final long serialVersionUID = -2094397730845639164L;

    private final ResourceBundle bundle =
            ResourceBundle.getBundle("de.lehmannet.om.ui.extension.deepSky.DeepSky", Locale.getDefault());

    private DeepSkyTargetGC target = null;

    private DeepSkyTargetContainer deepSkyTargetContainer = null;
    private JTextField magnitude = null;
    private JTextField concentration = null;
    private final ObservationManagerModel model;
    private final UserInterfaceHelper uiHelper;

    public DeepSkyTargetGCPanel(
            UserInterfaceHelper uiHelper, ObservationManagerModel model, ITarget target, Boolean editable)
            throws IllegalArgumentException {

        super(editable);

        if ((target != null) && !(target instanceof DeepSkyTargetGC)) {
            throw new IllegalArgumentException(
                    "Passed ITarget must derive from de.lehmannet.om.extension.deepSky.DeepSkyTargetGC\n");
        }

        this.target = (DeepSkyTargetGC) target;
        this.model = model;
        this.uiHelper = uiHelper;

        this.createPanel();

        if (this.target != null) {
            this.loadSchemaElement();
        }
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
            this.target = (DeepSkyTargetGC) t;
        }

        // Optional parameters
        String magnitude = this.magnitude.getText().trim();
        if (!"".equals(magnitude)) {
            double mag = Double.parseDouble(magnitude);
            this.target.setMagnitude(mag);
        }

        String concentration = this.concentration.getText().trim();
        if (!"".equals(concentration)) {
            this.target.setConcentration(concentration);
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
            this.target = new DeepSkyTargetGC(name, observer);
        } else {
            this.target = new DeepSkyTargetGC(name, datasource);
        }

        // Set all other fields
        ITarget t = (ITarget) this.updateSchemaElement();
        if (t == null) {
            return null;
        } else {
            this.target = (DeepSkyTargetGC) t;
        }

        return this.target;
    }

    private void loadSchemaElement() {

        // @todo: Doubles values should be float
        // Optional values can be cleared

        if (!Double.isNaN(this.target.getMagnitude())) {
            this.magnitude.setText("" + this.target.getMagnitude());
        }
        this.magnitude.setEditable(this.isEditable());

        if (this.target.getConcentration() != null) {
            this.concentration.setText(this.target.getConcentration());
        }
        this.concentration.setEditable(this.isEditable());
    }

    private void createPanel() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        // constraints.fill = GridBagConstraints.HORIZONTAL;
        this.setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 8, 1, 100, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.deepSkyTargetContainer =
                new DeepSkyTargetContainer(this.uiHelper, this.model, this.target, this.isEditable());
        gridbag.setConstraints(this.deepSkyTargetContainer, constraints);
        this.add(this.deepSkyTargetContainer);

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 8, 1, 100, 1);
        JSeparator seperator1 = new JSeparator(SwingConstants.HORIZONTAL);
        gridbag.setConstraints(seperator1, constraints);
        this.add(seperator1);

        ConstraintsBuilder.buildConstraints(constraints, 0, 2, 1, 1, 5, 1);
        constraints.fill = GridBagConstraints.NONE;
        OMLabel Lconcentration = new OMLabel(this.bundle.getString("panel.gc.label.concentration"), false);
        Lconcentration.setToolTipText(this.bundle.getString("panel.gc.tooltip.concentration"));
        gridbag.setConstraints(Lconcentration, constraints);
        this.add(Lconcentration);
        ConstraintsBuilder.buildConstraints(constraints, 1, 2, 1, 1, 45, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.concentration = new JTextField();
        this.concentration.setToolTipText(this.bundle.getString("panel.gc.tooltip.concentration"));
        this.concentration.setEditable(this.isEditable());
        gridbag.setConstraints(this.concentration, constraints);
        this.add(this.concentration);

        ConstraintsBuilder.buildConstraints(constraints, 2, 2, 1, 1, 5, 1);
        constraints.fill = GridBagConstraints.NONE;
        OMLabel Lmagnitude =
                new OMLabel(this.bundle.getString("panel.gc.label.magnitude"), SwingConstants.RIGHT, false);
        Lmagnitude.setToolTipText(this.bundle.getString("panel.gc.tooltip.magnitude"));
        gridbag.setConstraints(Lmagnitude, constraints);
        this.add(Lmagnitude);
        ConstraintsBuilder.buildConstraints(constraints, 3, 2, 1, 1, 45, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.magnitude = new JTextField();
        this.magnitude.setToolTipText(this.bundle.getString("panel.gc.tooltip.magnitude"));
        this.magnitude.setEditable(this.isEditable());
        gridbag.setConstraints(this.magnitude, constraints);
        this.add(this.magnitude);

        ConstraintsBuilder.buildConstraints(constraints, 0, 3, 4, 1, 100, 95);
        constraints.fill = GridBagConstraints.BOTH;
        JLabel Lfill = new JLabel("");
        gridbag.setConstraints(Lfill, constraints);
        this.add(Lfill);
    }
}
