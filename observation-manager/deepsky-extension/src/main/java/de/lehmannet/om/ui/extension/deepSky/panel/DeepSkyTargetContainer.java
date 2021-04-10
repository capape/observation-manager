/* ====================================================================
 * /extension/deepSky/panel/DeepSkyTargetContainer.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.deepSky.panel;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import de.lehmannet.om.Angle;
import de.lehmannet.om.Constellation;
import de.lehmannet.om.EquPosition;
import de.lehmannet.om.IObserver;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.SurfaceBrightness;
import de.lehmannet.om.extension.deepSky.DeepSkyTarget;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.container.AngleContainer;
import de.lehmannet.om.ui.container.SurfaceBrightnessContainer;
import de.lehmannet.om.ui.container.TargetContainer;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.OMLabel;
import de.lehmannet.om.ui.util.UserInterfaceHelper;
import de.lehmannet.om.util.FloatUtil;

class DeepSkyTargetContainer extends Container {

    private static final long serialVersionUID = 7287706985477081449L;

    private final ResourceBundle bundle = ResourceBundle.getBundle("de.lehmannet.om.ui.extension.deepSky.DeepSky",
            Locale.getDefault());

    private DeepSkyTarget target = null;
    private boolean editable = false;

    private TargetContainer targetContainer = null;
    private AngleContainer smallDiameter = null;
    private AngleContainer largeDiameter = null;
    private JTextField visibleMagnitude = null;
    private SurfaceBrightnessContainer surfaceBrightness = null;

    private final ObservationManagerModel model;
    // private final IConfiguration configuration;
    private final UserInterfaceHelper uiHelper;

    public DeepSkyTargetContainer(UserInterfaceHelper uiHelper, ObservationManagerModel model, ITarget target,
            boolean editable) {

        if (target != null) {
            if (!(target instanceof DeepSkyTarget)) {
                throw new IllegalArgumentException(
                        "Passed ITarget must derive from de.lehmannet.om.extension.deepSky.DeepSkyTarget\n");
            }
            this.target = (DeepSkyTarget) target;
        }

        this.model = model;
        this.editable = editable;
        this.uiHelper = uiHelper;
        // this.configuration = configuration;

        this.createContainer();

        if (this.target != null) {
            this.loadElement();
        }

    }

    // ---------------------------------
    // Methods passed to TargetContainer --------------------------------------
    // ---------------------------------

    public String getDatasource() {

        return this.targetContainer.getDatasource();

    }

    public IObserver getObserver() {

        return this.targetContainer.getObserver();

    }

    public Constellation getConstellation() {

        return this.targetContainer.getConstellation();

    }

    public EquPosition getPosition() {

        return this.targetContainer.getPosition();

    }

    @Override
    public String getName() {

        return this.targetContainer.getName();

    }

    public String[] getAliasNames() {

        return this.targetContainer.getAliasNames();

    }

    public boolean checkOrigin(String datasource, IObserver observer) {

        return !this.targetContainer.checkOrigin(datasource, observer);

    }

    // --------------
    // Public Methods ---------------------------------------------------------
    // --------------

    public ITarget updateTarget(ITarget target) {

        this.targetContainer.setTarget(target);

        ITarget t = this.targetContainer.updateTarget();
        if (t == null) {
            return null;
        }

        DeepSkyTarget dsTarget = (DeepSkyTarget) t;

        Angle smallDiameter = null;
        try {
            smallDiameter = this.getSmallDiameter();
        } catch (NumberFormatException nfe) {
            this.uiHelper.showWarning(this.bundle.getString("panel.warning.smallDiameterNoNumber"));
            return null;
        }
        // if( smallDiameter != null ) {
        dsTarget.setSmallDiameter(smallDiameter);
        // }

        Angle largeDiameter = null;
        try {
            largeDiameter = this.getLargeDiameter();
        } catch (NumberFormatException nfe) {
            this.uiHelper.showWarning(this.bundle.getString("panel.warning.largeDiameterNoNumber"));
            return null;
        }
        // if( largeDiameter != null ) {
        dsTarget.setLargeDiameter(largeDiameter);
        // }

        float visibleMagnitude = this.getVisibleMagnitude();
        if (!Float.isNaN(visibleMagnitude)) {
            dsTarget.setVisibleMagnitude(visibleMagnitude);
        }

        SurfaceBrightness surfaceBrightness = null;
        try {
            surfaceBrightness = this.getSurfaceBrightness();
        } catch (NumberFormatException nfe) {
            this.uiHelper.showWarning(this.bundle.getString("panel.observation.warning.noNumberSB"));
            return null;
        }
        if (surfaceBrightness != null) {
            dsTarget.setSurfaceBrightness(surfaceBrightness);
        }

        return dsTarget;

    }

    private Angle getSmallDiameter() throws NumberFormatException {

        return this.smallDiameter.getAngle();

    }

    private Angle getLargeDiameter() throws NumberFormatException {

        return this.largeDiameter.getAngle();

    }

    private float getVisibleMagnitude() {

        if ("".equals(this.visibleMagnitude.getText().trim())) {
            return Float.NaN;
        }

        return FloatUtil.parseFloat(this.visibleMagnitude.getText());

    }

    private SurfaceBrightness getSurfaceBrightness() throws NumberFormatException {

        return this.surfaceBrightness.getSurfaceBrightness();

    }

    private void loadElement() {

        if (!Float.isNaN(this.target.getVisibleMagnitude())) {
            this.visibleMagnitude.setText("" + this.target.getVisibleMagnitude());
        }
        this.visibleMagnitude.setEditable(this.editable);

        if (this.target.getSurfaceBrightness() != null) {
            this.surfaceBrightness.setSurfaceBrightness(this.target.getSurfaceBrightness());
        }
        this.surfaceBrightness.setEditable(this.editable);

        if (this.target.getSmallDiameter() != null) {
            this.smallDiameter.setAngle(this.target.getSmallDiameter());
        }
        this.smallDiameter.setEditable(this.editable);

        if (this.target.getLargeDiameter() != null) {
            this.largeDiameter.setAngle(this.target.getLargeDiameter());
        }
        this.largeDiameter.setEditable(this.editable);

    }

    private void createContainer() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        this.setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 8, 7, 100, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.targetContainer = new TargetContainer(this.model.getConfiguration(), this.model, this.target,
                this.editable, false);
        gridbag.setConstraints(this.targetContainer, constraints);
        this.add(this.targetContainer);

        ConstraintsBuilder.buildConstraints(constraints, 0, 8, 8, 1, 100, 1);
        JSeparator seperator1 = new JSeparator(SwingConstants.HORIZONTAL);
        gridbag.setConstraints(seperator1, constraints);
        this.add(seperator1);

        ConstraintsBuilder.buildConstraints(constraints, 0, 9, 1, 1, 5, 1);
        constraints.fill = GridBagConstraints.NONE;
        OMLabel LvisibleMagnitude = new OMLabel(this.bundle.getString("container.target.label.magnitude"), false);
        LvisibleMagnitude.setToolTipText(this.bundle.getString("container.target.tooltip.magnitude"));
        gridbag.setConstraints(LvisibleMagnitude, constraints);
        this.add(LvisibleMagnitude);
        ConstraintsBuilder.buildConstraints(constraints, 1, 9, 1, 1, 45, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.visibleMagnitude = new JTextField();
        this.visibleMagnitude.setToolTipText(this.bundle.getString("container.target.tooltip.magnitude"));
        this.visibleMagnitude.setEditable(this.editable);
        gridbag.setConstraints(this.visibleMagnitude, constraints);
        this.add(this.visibleMagnitude);

        ConstraintsBuilder.buildConstraints(constraints, 2, 9, 1, 1, 5, 1);
        constraints.fill = GridBagConstraints.NONE;
        OMLabel LsurfaceBrightness = new OMLabel(this.bundle.getString("container.target.label.surfaceBrightness"),
                SwingConstants.RIGHT, false);
        LsurfaceBrightness.setToolTipText(this.bundle.getString("container.target.tooltip.surfaceBrightness"));
        gridbag.setConstraints(LsurfaceBrightness, constraints);
        this.add(LsurfaceBrightness);
        ConstraintsBuilder.buildConstraints(constraints, 3, 9, 5, 1, 45, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.surfaceBrightness = new SurfaceBrightnessContainer(this.editable,
                new String[] { SurfaceBrightness.MAGS_SQR_ARC_MIN, SurfaceBrightness.MAGS_SQR_ARC_SEC });
        this.surfaceBrightness.setToolTipText(this.bundle.getString("container.target.tooltip.surfaceBrightness"));
        gridbag.setConstraints(this.surfaceBrightness, constraints);
        this.add(this.surfaceBrightness);

        ConstraintsBuilder.buildConstraints(constraints, 0, 10, 1, 1, 5, 1);
        constraints.fill = GridBagConstraints.NONE;
        OMLabel LsmallDiameter = new OMLabel(this.bundle.getString("container.target.label.smallDiameter"), false);
        LsmallDiameter.setToolTipText(this.bundle.getString("container.target.tooltip.smallDiameter"));
        gridbag.setConstraints(LsmallDiameter, constraints);
        this.add(LsmallDiameter);
        ConstraintsBuilder.buildConstraints(constraints, 1, 10, 1, 1, 45, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.smallDiameter = new AngleContainer(Angle.ARCSECOND, this.editable);
        this.smallDiameter.setUnits(new String[] { Angle.ARCMINUTE, Angle.ARCSECOND });
        this.smallDiameter.setToolTipText(this.bundle.getString("container.target.tooltip.smallDiameter"));
        gridbag.setConstraints(this.smallDiameter, constraints);
        this.add(this.smallDiameter);

        ConstraintsBuilder.buildConstraints(constraints, 2, 10, 1, 1, 5, 1);
        constraints.fill = GridBagConstraints.NONE;
        OMLabel LlargeDiameter = new OMLabel(this.bundle.getString("container.target.label.largeDiameter"),
                SwingConstants.RIGHT, false);
        LlargeDiameter.setToolTipText(this.bundle.getString("container.target.tooltip.largeDiameter"));
        gridbag.setConstraints(LlargeDiameter, constraints);
        this.add(LlargeDiameter);
        ConstraintsBuilder.buildConstraints(constraints, 3, 10, 1, 1, 45, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.largeDiameter = new AngleContainer(Angle.ARCSECOND, this.editable);
        this.largeDiameter.setUnits(new String[] { Angle.ARCMINUTE, Angle.ARCSECOND });
        this.largeDiameter.setToolTipText(this.bundle.getString("container.target.tooltip.largeDiameter"));
        gridbag.setConstraints(this.largeDiameter, constraints);
        this.add(this.largeDiameter);

    }

}
