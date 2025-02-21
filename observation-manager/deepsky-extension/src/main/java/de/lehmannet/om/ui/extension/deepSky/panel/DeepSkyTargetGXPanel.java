/*
 * ====================================================================
 * /extension/deepSky/panel/DeepSkyTargetGXPanel.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.deepSky.panel;

import de.lehmannet.om.IObserver;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetGX;
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

public class DeepSkyTargetGXPanel extends AbstractPanel {

    private static final long serialVersionUID = -3778934999845455902L;

    private final ResourceBundle bundle =
            ResourceBundle.getBundle("de.lehmannet.om.ui.extension.deepSky.DeepSky", Locale.getDefault());

    private DeepSkyTargetGX target = null;

    private DeepSkyTargetContainer deepSkyTargetContainer = null;
    private JTextField hubbleType = null;
    private JTextField positionAngle = null;
    private final ObservationManagerModel model;
    private final UserInterfaceHelper uiHelper;

    public DeepSkyTargetGXPanel(UserInterfaceHelper om, ObservationManagerModel model, ITarget target, Boolean editable)
            throws IllegalArgumentException {

        super(editable);

        if ((target != null) && !(target instanceof DeepSkyTargetGX)) {
            throw new IllegalArgumentException(
                    "Passed ITarget must derive from de.lehmannet.om.extension.deepSky.DeepSkyTargetGX\n");
        }

        this.target = (DeepSkyTargetGX) target;
        this.uiHelper = om;
        this.model = model;

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
            this.target = (DeepSkyTargetGX) t;
        }

        // Optional parameters
        String hT = this.hubbleType.getText().trim();
        if (!"".equals(hT)) {
            this.target.setHubbleType(hT);
        }

        String pA = this.positionAngle.getText();
        if (pA != null) {
            int p = -1;

            if (!"".equals(pA.trim())) {
                try {
                    p = Integer.parseInt(pA);

                    if ((p < 0) || (p > 359)) {
                        this.createWarning(this.bundle.getString("panel.gx.warning.posAngle.invalid"));
                        return null;
                    }
                } catch (NumberFormatException nfe) {
                    this.createWarning(this.bundle.getString("panel.gx.warning.posAngle.invalid"));
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
            this.target = new DeepSkyTargetGX(name, observer);
        } else {
            this.target = new DeepSkyTargetGX(name, datasource);
        }

        // Set all other fields
        ITarget t = (ITarget) this.updateSchemaElement();
        if (t == null) {
            return null;
        } else {
            this.target = (DeepSkyTargetGX) t;
        }

        return this.target;
    }

    private void loadSchemaElement() {

        if (this.target.getHubbleType() != null) {
            this.hubbleType.setText("" + this.target.getHubbleType());
        }
        this.hubbleType.setEditable(this.isEditable());

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
        this.deepSkyTargetContainer =
                new DeepSkyTargetContainer(this.uiHelper, this.model, this.target, this.isEditable());
        gridbag.setConstraints(this.deepSkyTargetContainer, constraints);
        this.add(this.deepSkyTargetContainer);

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 4, 1, 100, 1);
        JSeparator seperator1 = new JSeparator(SwingConstants.HORIZONTAL);
        gridbag.setConstraints(seperator1, constraints);
        this.add(seperator1);

        ConstraintsBuilder.buildConstraints(constraints, 0, 2, 1, 1, 5, 1);
        OMLabel LhubbleType = new OMLabel(this.bundle.getString("panel.gx.label.type"), false);
        LhubbleType.setToolTipText(this.bundle.getString("panel.gx.tooltip.type"));
        gridbag.setConstraints(LhubbleType, constraints);
        this.add(LhubbleType);
        ConstraintsBuilder.buildConstraints(constraints, 1, 2, 1, 1, 45, 1);
        this.hubbleType = new JTextField();
        this.hubbleType.setToolTipText(this.bundle.getString("panel.gx.tooltip.type"));
        this.hubbleType.setEditable(this.isEditable());
        gridbag.setConstraints(this.hubbleType, constraints);
        this.add(this.hubbleType);

        ConstraintsBuilder.buildConstraints(constraints, 2, 2, 1, 1, 5, 1);
        OMLabel LpositionAngle =
                new OMLabel(this.bundle.getString("panel.gx.label.posAngle"), SwingConstants.RIGHT, false);
        LpositionAngle.setToolTipText(this.bundle.getString("panel.gx.tooltip.posAngle"));
        gridbag.setConstraints(LpositionAngle, constraints);
        this.add(LpositionAngle);
        ConstraintsBuilder.buildConstraints(constraints, 3, 2, 1, 1, 45, 1);
        this.positionAngle = new JTextField(3);
        this.positionAngle.setToolTipText(this.bundle.getString("panel.gx.tooltip.posAngle"));
        gridbag.setConstraints(this.positionAngle, constraints);
        this.add(this.positionAngle);

        ConstraintsBuilder.buildConstraints(constraints, 0, 3, 4, 1, 45, 95);
        constraints.fill = GridBagConstraints.BOTH;
        JLabel Lfill = new JLabel("");
        gridbag.setConstraints(Lfill, constraints);
        this.add(Lfill);
    }
}
