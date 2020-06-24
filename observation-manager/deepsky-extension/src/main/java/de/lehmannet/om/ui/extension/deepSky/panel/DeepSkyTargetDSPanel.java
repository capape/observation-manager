/* ====================================================================
 * /extension/deepSky/panel/DeepSkyTargetDSPanel.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.deepSky.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import de.lehmannet.om.Angle;
import de.lehmannet.om.IObserver;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetDS;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.container.AngleContainer;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.IConfiguration;
import de.lehmannet.om.ui.util.OMLabel;
import de.lehmannet.om.ui.util.UserInterfaceHelper;

public class DeepSkyTargetDSPanel extends AbstractPanel {

    private static final long serialVersionUID = 1499872623503724981L;

    private final ResourceBundle bundle = ResourceBundle.getBundle("de.lehmannet.om.ui.extension.deepSky.DeepSky",
            Locale.getDefault());

    private DeepSkyTargetDS target = null;

    private DeepSkyTargetContainer deepSkyTargetContainer = null;
    private JTextField magComp = null;
    private JTextField positionAngle = null;
    private AngleContainer separation = null;
    private final ObservationManagerModel model;
    private final UserInterfaceHelper uiHelper;

    public DeepSkyTargetDSPanel(UserInterfaceHelper uiHelper, ObservationManagerModel model, ITarget target,
            Boolean editable) throws IllegalArgumentException {

        super(editable);

        if ((target != null) && !(target instanceof DeepSkyTargetDS)) {
            throw new IllegalArgumentException(
                    "Passed ITarget must derive from de.lehmannet.om.extension.deepSky.DeepSkyTargetDS\n");
        }

        this.target = (DeepSkyTargetDS) target;
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
            this.target = (DeepSkyTargetDS) t;
        }

        // Optional parameters
        String magCompanion = this.magComp.getText().trim();
        if (!"".equals(magCompanion)) {
            double mc = Double.parseDouble(magCompanion);
            this.target.setCompanionMag(mc);
        }

        String pA = this.positionAngle.getText();
        if (pA != null) {
            int p = -1;

            if (!"".equals(pA.trim())) {
                try {
                    p = Integer.parseInt(pA);

                    if ((p < 0) || (p > 359)) {
                        this.createWarning(this.bundle.getString("panel.ds.warning.posAngle.invalid"));
                        return null;
                    }
                } catch (NumberFormatException nfe) {
                    this.createWarning(this.bundle.getString("panel.ds.warning.posAngle.invalid"));
                    return null;
                }
            }

            this.target.setPositionAngle(p);
        }

        Angle sep = null;
        try {
            sep = this.separation.getAngle();
        } catch (NumberFormatException nfe) {
            this.createWarning(this.bundle.getString("panel.warning.separationNoNumber"));
            return null;
        }
        // if( sep != null ) {
        this.target.setSeparation(sep);
        // }

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
            this.target = new DeepSkyTargetDS(name, observer);
        } else {
            this.target = new DeepSkyTargetDS(name, datasource);
        }

        // Set all other fields
        ITarget t = (ITarget) this.updateSchemaElement();
        if (t == null) {
            return null;
        } else {
            this.target = (DeepSkyTargetDS) t;
        }

        return this.target;

    }

    private void loadSchemaElement() {

        if (!Double.isNaN(this.target.getCompanionMag())) {
            this.magComp.setText("" + this.target.getCompanionMag());
        }
        this.magComp.setEditable(this.isEditable());

        if (this.target.getPositionAngle() != -1) {
            this.positionAngle.setText("" + this.target.getPositionAngle());
        }
        this.positionAngle.setEditable(this.isEditable());

        if (this.target.getSeparation() != null) {
            this.separation.setAngle(this.target.getSeparation());
        }
        this.separation.setEditable(this.isEditable());

    }

    private void createPanel() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 4, 1, 45, 1);
        this.deepSkyTargetContainer = new DeepSkyTargetContainer(this.uiHelper, this.model, this.target,
                this.isEditable());
        gridbag.setConstraints(this.deepSkyTargetContainer, constraints);
        this.add(this.deepSkyTargetContainer);

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 4, 1, 100, 1);
        JSeparator seperator1 = new JSeparator(SwingConstants.HORIZONTAL);
        gridbag.setConstraints(seperator1, constraints);
        this.add(seperator1);

        ConstraintsBuilder.buildConstraints(constraints, 0, 2, 1, 1, 5, 1);
        OMLabel LmagComp = new OMLabel(this.bundle.getString("panel.ds.label.magnitude"), false);
        LmagComp.setToolTipText(this.bundle.getString("panel.ds.tooltip.magnitude"));
        gridbag.setConstraints(LmagComp, constraints);
        this.add(LmagComp);
        ConstraintsBuilder.buildConstraints(constraints, 1, 2, 1, 1, 45, 1);
        this.magComp = new JTextField();
        this.magComp.setToolTipText(this.bundle.getString("panel.ds.tooltip.magnitude"));
        this.magComp.setEditable(this.isEditable());
        gridbag.setConstraints(this.magComp, constraints);
        this.add(this.magComp);

        ConstraintsBuilder.buildConstraints(constraints, 0, 3, 1, 1, 5, 1);
        OMLabel LpositionAngle = new OMLabel(this.bundle.getString("panel.ds.label.posAngle"), false);
        LpositionAngle.setToolTipText(this.bundle.getString("panel.ds.tooltip.posAngle"));
        gridbag.setConstraints(LpositionAngle, constraints);
        this.add(LpositionAngle);
        ConstraintsBuilder.buildConstraints(constraints, 1, 3, 1, 1, 45, 1);
        this.positionAngle = new JTextField(3);
        this.positionAngle.setToolTipText(this.bundle.getString("panel.ds.tooltip.posAngle"));
        gridbag.setConstraints(this.positionAngle, constraints);
        this.add(this.positionAngle);

        ConstraintsBuilder.buildConstraints(constraints, 2, 3, 1, 1, 5, 1);
        OMLabel LseparationAngle = new OMLabel(this.bundle.getString("panel.ds.label.separation"), SwingConstants.RIGHT,
                false);
        LseparationAngle.setToolTipText(this.bundle.getString("panel.ds.tooltip.separation"));
        gridbag.setConstraints(LseparationAngle, constraints);
        this.add(LseparationAngle);
        ConstraintsBuilder.buildConstraints(constraints, 3, 3, 1, 1, 45, 1);
        this.separation = new AngleContainer(Angle.ARCSECOND, this.isEditable());
        this.separation.setUnits(new String[] { Angle.ARCMINUTE, Angle.ARCSECOND });
        this.separation.setToolTipText(this.bundle.getString("panel.ds.tooltip.separation"));
        gridbag.setConstraints(this.separation, constraints);
        this.add(this.separation);

        ConstraintsBuilder.buildConstraints(constraints, 0, 4, 4, 1, 45, 93);
        constraints.fill = GridBagConstraints.BOTH;
        JLabel Lfill = new JLabel("");
        gridbag.setConstraints(Lfill, constraints);
        this.add(Lfill);

    }

}
