/* ====================================================================
 * /extension/deepSky/panel/DeepSkyTargetGNPanel.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.deepSky.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import de.lehmannet.om.IObserver;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetGN;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.OMLabel;

public class DeepSkyTargetGNPanel extends AbstractPanel {

    private static final long serialVersionUID = -8930605972714341358L;

    private final PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
            .getBundle("de.lehmannet.om.ui.extension.deepSky.DeepSky", Locale.getDefault());

    private ObservationManager observationManager = null;
    private DeepSkyTargetGN target = null;

    private DeepSkyTargetContainer deepSkyTargetContainer = null;
    private JTextField nebulaType = null;
    private JTextField positionAngle = null;

    public DeepSkyTargetGNPanel(ObservationManager om, ITarget target, Boolean editable)
            throws IllegalArgumentException {

        super(editable);

        if ((target != null) && !(target instanceof DeepSkyTargetGN)) {
            throw new IllegalArgumentException(
                    "Passed ITarget must derive from de.lehmannet.om.extension.deepSky.DeepSkyTargetGN\n");
        }

        this.target = (DeepSkyTargetGN) target;
        this.observationManager = om;

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
            this.target = (DeepSkyTargetGN) t;
        }

        // Optional parameters
        String nT = this.nebulaType.getText().trim();
        if (!"".equals(nT)) {
            this.target.setNebulaType(nT);
        }

        String pA = this.positionAngle.getText();
        if (pA != null) {
            int p = -1;

            if (!"".equals(pA.trim())) {
                try {
                    p = Integer.parseInt(pA);

                    if ((p < 0) || (p > 359)) {
                        this.createWarning(this.bundle.getString("panel.gn.warning.posAngle.invalid"));
                        return null;
                    }
                } catch (NumberFormatException nfe) {
                    this.createWarning(this.bundle.getString("panel.gn.warning.posAngle.invalid"));
                    return null;
                }
            }

            this.target.setPositionAngle(p);
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
            this.target = new DeepSkyTargetGN(name, observer);
        } else {
            this.target = new DeepSkyTargetGN(name, datasource);
        }

        // Set all other fields
        ITarget t = (ITarget) this.updateSchemaElement();
        if (t == null) {
            return null;
        } else {
            this.target = (DeepSkyTargetGN) t;
        }

        return this.target;

    }

    private void loadSchemaElement() {

        if (this.target.getNebulaType() != null) {
            this.nebulaType.setText("" + this.target.getNebulaType());
        }
        this.nebulaType.setEditable(this.isEditable());

        if (this.target.getPositionAngle() != -1) {
            this.positionAngle.setText("" + this.target.getPositionAngle());
        }
        this.positionAngle.setEditable(this.isEditable());

    }

    private void createPanel() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 4, 1, 45, 1);
        this.deepSkyTargetContainer = new DeepSkyTargetContainer(this.observationManager, this.target,
                this.isEditable());
        gridbag.setConstraints(this.deepSkyTargetContainer, constraints);
        this.add(this.deepSkyTargetContainer);

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 4, 1, 100, 1);
        JSeparator seperator1 = new JSeparator(SwingConstants.HORIZONTAL);
        gridbag.setConstraints(seperator1, constraints);
        this.add(seperator1);

        ConstraintsBuilder.buildConstraints(constraints, 0, 2, 1, 1, 5, 1);
        OMLabel LnebuleaType = new OMLabel(this.bundle.getString("panel.gn.label.type"), false);
        LnebuleaType.setToolTipText(this.bundle.getString("panel.gn.tooltip.type"));
        gridbag.setConstraints(LnebuleaType, constraints);
        this.add(LnebuleaType);
        ConstraintsBuilder.buildConstraints(constraints, 1, 2, 1, 1, 45, 1);
        this.nebulaType = new JTextField();
        this.nebulaType.setToolTipText(this.bundle.getString("panel.gn.tooltip.type"));
        this.nebulaType.setEditable(this.isEditable());
        gridbag.setConstraints(this.nebulaType, constraints);
        this.add(this.nebulaType);

        ConstraintsBuilder.buildConstraints(constraints, 2, 2, 1, 1, 5, 1);
        OMLabel LpositionAngle = new OMLabel(this.bundle.getString("panel.gn.label.posAngle"), SwingConstants.RIGHT,
                false);
        LpositionAngle.setToolTipText(this.bundle.getString("panel.gn.tooltip.posAngle"));
        gridbag.setConstraints(LpositionAngle, constraints);
        this.add(LpositionAngle);
        ConstraintsBuilder.buildConstraints(constraints, 3, 2, 1, 1, 45, 1);
        this.positionAngle = new JTextField(3);
        this.positionAngle.setToolTipText(this.bundle.getString("panel.gn.tooltip.posAngle"));
        gridbag.setConstraints(this.positionAngle, constraints);
        this.add(this.positionAngle);

        ConstraintsBuilder.buildConstraints(constraints, 0, 3, 4, 1, 45, 95);
        constraints.fill = GridBagConstraints.BOTH;
        JLabel Lfill = new JLabel("");
        gridbag.setConstraints(Lfill, constraints);
        this.add(Lfill);

    }

}
