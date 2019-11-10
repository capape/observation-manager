/* ====================================================================
 * /panel/ScopePanel.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import de.lehmannet.om.Angle;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.IScope;
import de.lehmannet.om.Scope;
import de.lehmannet.om.ui.container.AngleContainer;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.OMLabel;
import de.lehmannet.om.util.FloatUtil;
import de.lehmannet.om.util.OpticsUtil;

public class ScopePanel extends AbstractPanel implements ActionListener {

    private static final long serialVersionUID = 3260776777967694833L;

    private IScope scope = null;

    private JTextField aperture = null;
    private JTextField focalLength = null;
    private JTextField magnification = null;
    private JTextField model = null;
    private JTextField exitPupil = null;
    private AngleContainer trueFOV = null;
    private JTextField type = null;
    private JTextField vendor = null;
    private OMLabel Lerected = null;
    private OMLabel Ltruesided = null;
    private JTextField lightGrasp = null;
    private JCheckBox orientation = null;
    private JCheckBox orientationErect = null;
    private JCheckBox orientationTruesided = null;

    public ScopePanel(IScope scope, boolean editable) {

        super(editable);

        this.scope = scope;

        this.createPanel();

        if (scope != null) {
            this.loadSchemaElement();
        }

    }

    private void loadSchemaElement() {

        this.type.setText(this.scope.getType());
        this.type.setEditable(super.isEditable());

        this.vendor.setText(this.scope.getVendor());
        this.vendor.setEditable(super.isEditable());

        this.model.setText(this.scope.getModel());
        this.model.setEditable(super.isEditable());

        float fl = this.scope.getFocalLength();
        if (!Float.isNaN(fl)) {
            this.focalLength.setText("" + fl);
        }
        this.focalLength.setEditable(super.isEditable());

        this.aperture.setText(String.valueOf(this.scope.getAperture()));
        this.aperture.setEditable(super.isEditable());

        float mg = this.scope.getMagnification();
        if (!Float.isNaN(mg)) {
            this.magnification.setText("" + mg);

            if (!super.isEditable()) { // Exit pupil is only shown in display mode
                float ep = OpticsUtil.getExitPupil(this.scope);
                if (!Float.isNaN(ep)) {
                    // Output format
                    DecimalFormat df = new DecimalFormat("0.00");
                    DecimalFormatSymbols dfs = new DecimalFormatSymbols();
                    dfs.setDecimalSeparator('.');
                    df.setDecimalFormatSymbols(dfs);

                    this.exitPupil.setText(df.format(ep));
                    this.exitPupil.setEditable(super.isEditable());
                }
            }
        }
        this.magnification.setEditable(super.isEditable());

        float lg = this.scope.getLightGrasp();
        if (!Float.isNaN(lg)) {
            this.lightGrasp.setText("" + lg);
        }
        this.lightGrasp.setEditable(super.isEditable());

        this.trueFOV.setAngle(this.scope.getTrueFieldOfView());
        this.trueFOV.setEditable(super.isEditable());

        boolean erectedSet = false;
        try {
            boolean erected = this.scope.isOrientationErected();
            this.orientationErect.setSelected(erected);
            this.orientationErect.setVisible(true);
            this.Lerected.setVisible(true);
            this.orientation.setSelected(true);
            erectedSet = true;
        } catch (IllegalStateException ise) {
            // Do not show checkbox, as erection field was never set
            this.orientationErect.setVisible(false);
            this.Lerected.setVisible(false);
        }

        try {
            boolean trueSided = this.scope.isOrientationTruesided();
            this.orientationTruesided.setSelected(trueSided);
            this.orientationTruesided.setVisible(true);
            this.Ltruesided.setVisible(true);
            this.orientation.setSelected(true);
        } catch (IllegalStateException ise) {
            // Do not show checkbox, as true sided field was never set
            this.orientationTruesided.setVisible(false);
            this.Ltruesided.setVisible(false);
            if (!erectedSet) {
                this.orientation.setSelected(false);
            }
        }

        this.orientation.setEnabled(super.isEditable());
        this.orientationTruesided.setEnabled(super.isEditable());
        this.orientationErect.setEnabled(super.isEditable());

    }

    @Override
    public ISchemaElement getSchemaElement() {

        return this.scope;

    }

    @Override
    public ISchemaElement updateSchemaElement() {

        if (this.scope == null) {
            return null;
        }

        // Get mandatory fields
        String modelName = this.getModelName();
        if (modelName == null) {
            return null;
        }
        this.scope.setModel(modelName);

        float aperture = this.getAperture();
        if (Float.isNaN(aperture)) {
            return null;
        }
        this.scope.setAperture(aperture);

        IScope result = this.getScope(true, aperture, modelName);
        if (result == null) {
            return null;
        }
        this.scope = result;

        // Set optional values

        String lightGrasp = this.lightGrasp.getText();
        if ((lightGrasp != null) && !("".equals(lightGrasp))) {
            try {
                float lg = FloatUtil.parseFloat(lightGrasp);
                if ((lg < 0.0) || (lg > 1.0)) {
                    super.createWarning(AbstractPanel.bundle.getString("panel.scope.warning.lightGraspInvalid"));
                    return null;
                }
                this.scope.setLightGrasp(lg);
            } catch (NumberFormatException nfe) {
                super.createWarning(AbstractPanel.bundle.getString("panel.scope.warning.lightGraspNumeric"));
                return null;
            }
        }

        Angle trueFieldOfView = null;
        try {
            trueFieldOfView = this.trueFOV.getAngle();
        } catch (NumberFormatException nfe) {
            super.createWarning(AbstractPanel.bundle.getString("panel.warning.trueFOVNoNumber"));
            return null;
        }
        if (trueFieldOfView != null) {
            double tfoV = trueFieldOfView.getValue();
            if (tfoV < 0.0) {
                super.createWarning(AbstractPanel.bundle.getString("panel.scope.warning.trueFoVpositive"));
                return null;
            }
            if (!Float.isNaN(this.scope.getFocalLength())) {
                super.createWarning(AbstractPanel.bundle.getString("panel.scope.warning.trueFoVNoMagnification"));
                return null;
            }
            this.scope.setTrueFieldOfView(trueFieldOfView);
        }

        String vendor = this.vendor.getText();
        if ((vendor != null) && !("".equals(vendor))) {
            this.scope.setVendor(vendor);
        }

        String type = this.type.getText();
        if ((type != null) && !("".equals(type))) {
            this.scope.setType(type);
        }

        if (this.orientation.isSelected()) {
            this.scope.setOrientation(this.orientationErect.isSelected(), this.orientationTruesided.isSelected());
        }

        return this.scope;

    }

    @Override
    public ISchemaElement createSchemaElement() {

        // Get mandatory fields
        String modelName = this.getModelName();
        if (modelName == null) {
            return null;
        }

        float aperture = this.getAperture();
        if (Float.isNaN(aperture)) {
            return null;
        }

        IScope result = this.getScope(false, aperture, modelName);
        if (result == null) {
            return null;
        }
        this.scope = result;

        // Set optional values

        String lightGrasp = this.lightGrasp.getText();
        if ((lightGrasp != null) && !("".equals(lightGrasp))) {
            try {
                float lg = FloatUtil.parseFloat(lightGrasp);
                if ((lg < 0.0) || (lg > 1.0)) {
                    super.createWarning(AbstractPanel.bundle.getString("panel.scope.warning.lightGraspInvalid"));
                    return null;
                }
                this.scope.setLightGrasp(lg);
            } catch (NumberFormatException nfe) {
                super.createWarning(AbstractPanel.bundle.getString("panel.scope.warning.lightGraspNumeric"));
                return null;
            }
        }

        Angle trueFieldOfView = null;
        try {
            trueFieldOfView = this.trueFOV.getAngle();
        } catch (NumberFormatException nfe) {
            super.createWarning(AbstractPanel.bundle.getString("panel.warning.trueFOVNoNumber"));
            return null;
        }
        if (trueFieldOfView != null) {
            double tfoV = trueFieldOfView.getValue();
            if (tfoV < 0.0) {
                super.createWarning(AbstractPanel.bundle.getString("panel.scope.warning.trueFoVpositive"));
                return null;
            }
            if (!Float.isNaN(this.scope.getFocalLength())) {
                super.createWarning(AbstractPanel.bundle.getString("panel.scope.warning.trueFoVNoMagnification"));
                return null;
            }
            this.scope.setTrueFieldOfView(trueFieldOfView);
        }

        String vendor = this.vendor.getText();
        if ((vendor != null) && !("".equals(vendor))) {
            this.scope.setVendor(vendor);
        }

        String type = this.type.getText();
        if ((type != null) && !("".equals(type))) {
            this.scope.setType(type);
        }

        if (this.orientation.isSelected()) {
            this.scope.setOrientation(this.orientationErect.isSelected(), this.orientationTruesided.isSelected());
        }

        return this.scope;

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();
        if (source instanceof JCheckBox) {
            JCheckBox sourceCB = (JCheckBox) source;
            if (sourceCB.equals(this.orientation)) {
                if (this.orientation.isSelected()) {
                    this.Lerected.setVisible(true);
                    this.Ltruesided.setVisible(true);
                    this.orientationErect.setVisible(true);
                    this.orientationTruesided.setVisible(true);
                } else {
                    this.Lerected.setVisible(false);
                    this.Ltruesided.setVisible(false);
                    this.orientationErect.setVisible(false);
                    this.orientationTruesided.setVisible(false);
                }
            }
        }

    }

    private IScope getScope(boolean update, float aperture, String modelName) {

        // Create scope with focalLength or magnification
        String focalLength = this.focalLength.getText();
        String magnification = this.magnification.getText();

        IScope scope = null;

        if ((focalLength == null) || ("".equals(focalLength.trim()))) {
            // Magnification has to be set
            if ((magnification != null) && !("".equals(magnification.trim()))) {
                float ma = 0.0f;
                try {
                    ma = FloatUtil.parseFloat(magnification);
                } catch (NumberFormatException nfe) {
                    super.createWarning(AbstractPanel.bundle.getString("panel.scope.warning.magnificationNumeric"));
                    return null;
                }
                if (ma < 0) {
                    super.createWarning(AbstractPanel.bundle.getString("panel.scope.warning.magnificationPositive"));
                    return null;
                }
                if (update) {
                    scope = this.scope;
                    scope.setMagnification(ma);
                } else {
                    scope = new Scope(aperture, ma, modelName);
                }
            } else {
                super.createWarning(AbstractPanel.bundle.getString("panel.scope.warning.magnificationOrAperture"));
                return null;
            }
        } else if ((magnification == null) || ("".equals(magnification.trim()))) {
            float fl = 0.0f;
            try {
                fl = FloatUtil.parseFloat(focalLength);
            } catch (NumberFormatException nfe) {
                super.createWarning(AbstractPanel.bundle.getString("panel.scope.warning.focalLnegthNumeric"));
                return null;
            }
            if (fl < 0) {
                super.createWarning(AbstractPanel.bundle.getString("panel.scope.warning.focalLnegthPositive"));
                return null;
            }
            if (update) {
                scope = this.scope;
                scope.setFocalLength(fl);
            } else {
                scope = new Scope(modelName, aperture, fl);
            }
        } else {
            super.createWarning(AbstractPanel.bundle.getString("panel.scope.warning.magnificationOrApertureNotBoth"));
            return null;
        }

        return scope;

    }

    private float getAperture() {

        String aperture = this.aperture.getText();
        if ((aperture == null) || ("".equals(aperture))) {
            super.createWarning(AbstractPanel.bundle.getString("panel.scope.warning.noAperture"));
            return Float.NaN;
        }
        float ap = 0.0f;
        try {
            ap = FloatUtil.parseFloat(aperture);
            if (ap <= 0.0) {
                super.createWarning(AbstractPanel.bundle.getString("panel.scope.warning.aperturePositive"));
                return Float.NaN;
            }
        } catch (NumberFormatException nfe) {
            super.createWarning(AbstractPanel.bundle.getString("panel.scope.warning.apertureNumeric"));
            return Float.NaN;
        }

        return ap;

    }

    private String getModelName() {

        String modelName = this.model.getText();
        if ((modelName == null) || ("".equals(modelName))) {
            this.createWarning(AbstractPanel.bundle.getString("panel.scope.warning.noModelName"));
            return null;
        }

        return modelName;

    }

    private void createPanel() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 1, 1, 5, 1);
        OMLabel LmodelName = new OMLabel(AbstractPanel.bundle.getString("panel.scope.label.modelName"), true);
        LmodelName.setToolTipText(AbstractPanel.bundle.getString("panel.scope.tooltip.modelName"));
        gridbag.setConstraints(LmodelName, constraints);
        this.add(LmodelName);
        ConstraintsBuilder.buildConstraints(constraints, 1, 0, 1, 1, 45, 1);
        this.model = new JTextField(25);
        this.model.setToolTipText(AbstractPanel.bundle.getString("panel.scope.tooltip.modelName"));
        gridbag.setConstraints(this.model, constraints);
        this.add(this.model);

        ConstraintsBuilder.buildConstraints(constraints, 2, 0, 1, 1, 5, 1);
        OMLabel LvendorName = new OMLabel(AbstractPanel.bundle.getString("panel.scope.label.vendor"), SwingConstants.RIGHT,
                false);
        LvendorName.setToolTipText(AbstractPanel.bundle.getString("panel.scope.tooltip.vendor"));
        gridbag.setConstraints(LvendorName, constraints);
        this.add(LvendorName);
        ConstraintsBuilder.buildConstraints(constraints, 3, 0, 1, 1, 45, 1);
        this.vendor = new JTextField(25);
        this.vendor.setToolTipText(AbstractPanel.bundle.getString("panel.scope.tooltip.vendor"));
        gridbag.setConstraints(this.vendor, constraints);
        this.add(this.vendor);

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 1, 1, 5, 1);
        OMLabel Laperture = new OMLabel(AbstractPanel.bundle.getString("panel.scope.label.aperture"), true);
        Laperture.setToolTipText(AbstractPanel.bundle.getString("panel.scope.tooltip.aperture"));
        gridbag.setConstraints(Laperture, constraints);
        this.add(Laperture);
        ConstraintsBuilder.buildConstraints(constraints, 1, 1, 1, 1, 45, 1);
        this.aperture = new JTextField(6);
        this.aperture.setToolTipText(AbstractPanel.bundle.getString("panel.scope.tooltip.aperture"));
        gridbag.setConstraints(this.aperture, constraints);
        this.add(this.aperture);

        ConstraintsBuilder.buildConstraints(constraints, 2, 1, 1, 1, 5, 1);
        OMLabel LfocalLength = null;
        if (super.isEditable()) {
            LfocalLength = new OMLabel(AbstractPanel.bundle.getString("panel.scope.label.focalLength") + "*",
                    SwingConstants.RIGHT, true);
        } else {
            LfocalLength = new OMLabel(AbstractPanel.bundle.getString("panel.scope.label.focalLength"), SwingConstants.RIGHT,
                    true);
        }
        LfocalLength.setToolTipText(AbstractPanel.bundle.getString("panel.scope.tooltip.focalLength"));
        gridbag.setConstraints(LfocalLength, constraints);
        // Only show focal length in creation mode, or focal length is set
        if ((super.isEditable()) // Edit mode -> Show focal length
                || !(Float.isNaN(this.scope.getFocalLength())) // Display mode -> Only show when set
        ) {
            this.add(LfocalLength);
        }

        ConstraintsBuilder.buildConstraints(constraints, 3, 1, 1, 1, 45, 1);
        this.focalLength = new JTextField(6);
        this.focalLength.setToolTipText(AbstractPanel.bundle.getString("panel.scope.tooltip.focalLength"));
        gridbag.setConstraints(this.focalLength, constraints);
        // Only show focal length in creation mode, or focal length is set
        if ((super.isEditable()) // Edit mode -> Show focal length
                || !(Float.isNaN(this.scope.getFocalLength())) // Display mode -> Only show when set
        ) {
            this.add(this.focalLength);
        }

        ConstraintsBuilder.buildConstraints(constraints, 0, 2, 1, 1, 5, 1);
        OMLabel Ltype = new OMLabel(AbstractPanel.bundle.getString("panel.scope.label.type"), false);
        Ltype.setToolTipText(AbstractPanel.bundle.getString("panel.scope.tooltip.type"));
        gridbag.setConstraints(Ltype, constraints);
        this.add(Ltype);
        ConstraintsBuilder.buildConstraints(constraints, 1, 2, 1, 1, 45, 1);
        this.type = new JTextField(25);
        this.type.setToolTipText(AbstractPanel.bundle.getString("panel.scope.tooltip.type"));
        gridbag.setConstraints(this.type, constraints);
        this.add(this.type);

        ConstraintsBuilder.buildConstraints(constraints, 2, 2, 1, 1, 5, 1);
        OMLabel Lmagnification = null;
        if (super.isEditable()) {
            Lmagnification = new OMLabel(AbstractPanel.bundle.getString("panel.scope.label.magnification") + "*",
                    SwingConstants.RIGHT, true);
        } else {
            Lmagnification = new OMLabel(AbstractPanel.bundle.getString("panel.scope.label.magnification"),
                    SwingConstants.RIGHT, true);
        }
        Lmagnification.setToolTipText(AbstractPanel.bundle.getString("panel.scope.tooltip.magnification"));
        gridbag.setConstraints(Lmagnification, constraints);
        // Only show magnification in creation mode, or magnification is set
        if ((super.isEditable()) // Edit mode -> Show magnification
                || !(Float.isNaN(this.scope.getMagnification())) // Display mode -> Only show when set
        ) {
            this.add(Lmagnification);
        }
        ConstraintsBuilder.buildConstraints(constraints, 3, 2, 1, 1, 45, 1);
        this.magnification = new JTextField(6);
        this.magnification.setToolTipText(AbstractPanel.bundle.getString("panel.scope.tooltip.magnification"));
        gridbag.setConstraints(this.magnification, constraints);
        // Only show magnification in creation mode, or magnification is set
        if ((super.isEditable()) // Edit mode -> Show magnification
                || !(Float.isNaN(this.scope.getMagnification())) // Display mode -> Only show when set
        ) {
            this.add(this.magnification);
        }

        ConstraintsBuilder.buildConstraints(constraints, 0, 3, 1, 1, 5, 1);
        OMLabel LlightGrasp = new OMLabel(AbstractPanel.bundle.getString("panel.scope.label.lightGrasp"), false);
        LlightGrasp.setToolTipText(AbstractPanel.bundle.getString("panel.scope.tooltip.lightGrasp"));
        gridbag.setConstraints(LlightGrasp, constraints);
        this.add(LlightGrasp);
        ConstraintsBuilder.buildConstraints(constraints, 1, 3, 1, 1, 45, 1);
        this.lightGrasp = new JTextField(25);
        this.lightGrasp.setToolTipText(AbstractPanel.bundle.getString("panel.scope.tooltip.lightGrasp"));
        gridbag.setConstraints(this.lightGrasp, constraints);
        this.add(this.lightGrasp);

        ConstraintsBuilder.buildConstraints(constraints, 2, 3, 1, 1, 5, 1);
        OMLabel LtfoV = new OMLabel(AbstractPanel.bundle.getString("panel.scope.label.trueFoV"), SwingConstants.RIGHT, false);
        gridbag.setConstraints(LtfoV, constraints);
        LtfoV.setToolTipText(AbstractPanel.bundle.getString("panel.scope.tooltip.trueFoV"));
        // Only show trueFoV in creation mode, or magnification is set
        if ((super.isEditable()) // Edit mode -> Show magnification
                || !(Float.isNaN(this.scope.getMagnification())) // Display mode -> Only show when set
        ) {
            this.add(LtfoV);
        }
        ConstraintsBuilder.buildConstraints(constraints, 3, 3, 1, 1, 45, 1);
        this.trueFOV = new AngleContainer(Angle.DEGREE, super.isEditable());
        this.trueFOV.setToolTipText(AbstractPanel.bundle.getString("panel.scope.tooltip.trueFoV"));
        gridbag.setConstraints(this.trueFOV, constraints);
        // Only show trueFoV in creation mode, or magnification is set
        if ((super.isEditable()) // Edit mode -> Show magnification
                || !(Float.isNaN(this.scope.getMagnification())) // Display mode -> Only show when set
        ) {
            this.add(this.trueFOV);
        }

        int rowCounter = 4;

        if (!super.isEditable()) { // Only show in display mode
            if ((this.scope != null) && (!Float.isNaN(this.scope.getMagnification()))) {
                ConstraintsBuilder.buildConstraints(constraints, 0, rowCounter, 1, 1, 5, 1);
                JLabel LexitPupil = new JLabel(AbstractPanel.bundle.getString("panel.scope.label.exitPupil"));
                gridbag.setConstraints(LexitPupil, constraints);
                LexitPupil.setToolTipText(AbstractPanel.bundle.getString("panel.scope.tooltip.exitPupil"));
                this.add(LexitPupil);
                ConstraintsBuilder.buildConstraints(constraints, 1, rowCounter++, 1, 1, 45, 1);
                this.exitPupil = new JTextField(6);
                this.exitPupil.setToolTipText(AbstractPanel.bundle.getString("panel.scope.tooltip.exitPupil"));
                gridbag.setConstraints(this.exitPupil, constraints);
                this.add(this.exitPupil);
            }
        }

        ConstraintsBuilder.buildConstraints(constraints, 0, rowCounter, 1, 1, 5, 1);
        OMLabel Lorientation = new OMLabel(AbstractPanel.bundle.getString("panel.scope.label.orientation"), false);
        Lorientation.setToolTipText(AbstractPanel.bundle.getString("panel.scope.tooltip.orientation"));
        gridbag.setConstraints(Lorientation, constraints);
        this.add(Lorientation);
        ConstraintsBuilder.buildConstraints(constraints, 1, rowCounter++, 1, 1, 45, 1);
        this.orientation = new JCheckBox();
        this.orientation.setSelected(false);
        this.orientation.setToolTipText(AbstractPanel.bundle.getString("panel.scope.tooltip.orientation"));
        this.orientation.addActionListener(this);
        gridbag.setConstraints(this.orientation, constraints);
        this.add(this.orientation);

        ConstraintsBuilder.buildConstraints(constraints, 1, rowCounter, 1, 1, 5, 1);
        this.Lerected = new OMLabel(AbstractPanel.bundle.getString("panel.scope.label.erected"), true);
        this.Lerected.setVisible(false);
        gridbag.setConstraints(this.Lerected, constraints);
        this.add(this.Lerected);
        ConstraintsBuilder.buildConstraints(constraints, 2, rowCounter++, 1, 1, GridBagConstraints.REMAINDER, 1);
        this.orientationErect = new JCheckBox();
        this.orientationErect.setVisible(false);
        gridbag.setConstraints(this.orientationErect, constraints);
        this.add(this.orientationErect);

        ConstraintsBuilder.buildConstraints(constraints, 1, rowCounter, 1, 1, 5, 1);
        this.Ltruesided = new OMLabel(AbstractPanel.bundle.getString("panel.scope.label.truesided"), true);
        this.Ltruesided.setVisible(false);
        gridbag.setConstraints(this.Ltruesided, constraints);
        this.add(this.Ltruesided);
        ConstraintsBuilder.buildConstraints(constraints, 2, rowCounter++, 1, 1, GridBagConstraints.REMAINDER, 1);
        this.orientationTruesided = new JCheckBox();
        this.orientationTruesided.setVisible(false);
        gridbag.setConstraints(this.orientationTruesided, constraints);
        this.add(this.orientationTruesided);

        // Add hint on how to use focal length and magnification, when we're in edit
        // mode
        if (super.isEditable()) {
            ConstraintsBuilder.buildConstraints(constraints, 0, rowCounter++, 4, 1, 45, 80);
            OMLabel focalLengthMagnificationHint = new OMLabel(
                    AbstractPanel.bundle.getString("panel.scope.label.focalLengthMagnificationHint"), true);
            gridbag.setConstraints(focalLengthMagnificationHint, constraints);
            this.add(focalLengthMagnificationHint);
        }

        ConstraintsBuilder.buildConstraints(constraints, 0, rowCounter++, 4, 1, 45, 80);
        constraints.fill = GridBagConstraints.BOTH;
        JLabel Lfill = new JLabel("");
        gridbag.setConstraints(Lfill, constraints);
        this.add(Lfill);

    }

}
