/* ====================================================================
 * /panel/EyepiecePanel.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import de.lehmannet.om.Angle;
import de.lehmannet.om.Eyepiece;
import de.lehmannet.om.IEyepiece;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ui.container.AngleContainer;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.OMLabel;
import de.lehmannet.om.util.FloatUtil;

public class EyepiecePanel extends AbstractPanel implements ItemListener {

    private static final long serialVersionUID = 2296288306891582119L;

    private IEyepiece eyepiece = null;

    private JTextField vendor = null;
    private JTextField model = null;
    private JLabel LfocalLengthName = null;
    private JTextField focalLength = null;
    private JCheckBox zoomEyepiece = null;
    private JLabel LmaxFocalLengthName = null;
    private JTextField maxFocalLength = null;
    private AngleContainer apparentFOV = null;

    public EyepiecePanel(IEyepiece eyepiece, boolean editable) {

        super(editable);

        this.eyepiece = eyepiece;

        this.createPanel();

        if (eyepiece != null) {
            this.loadSchemaElement();
        }

    }

    // Constructor only to be used in show mode
    public EyepiecePanel(IEyepiece eyepiece) {

        this(eyepiece, false);

    }

    @Override
    public void itemStateChanged(ItemEvent e) {

        Object source = e.getItemSelectable();

        if (source == this.zoomEyepiece) {
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                // Hide max focal length label and textbox
                this.maxFocalLength.setVisible(false);
                this.LmaxFocalLengthName.setVisible(false);
                // Change focal length label text
                this.LfocalLengthName.setText(AbstractPanel.bundle.getString("panel.eyepiece.label.focalLength"));
                this.LfocalLengthName
                        .setToolTipText(AbstractPanel.bundle.getString("panel.eyepiece.tooltip.focalLength"));
            } else if (e.getStateChange() == ItemEvent.SELECTED) {
                // Show max focal length label and textbox
                this.maxFocalLength.setVisible(true);
                this.LmaxFocalLengthName.setVisible(true);
                // Change focal length label text
                this.LfocalLengthName.setText(AbstractPanel.bundle.getString("panel.eyepiece.label.minFocalLength"));
                this.LfocalLengthName
                        .setToolTipText(AbstractPanel.bundle.getString("panel.eyepiece.tooltip.minFocalLength"));
            }
            this.updateUI(); // Refresh UI
        }

    }

    @Override
    public ISchemaElement getSchemaElement() {

        return this.eyepiece;

    }

    @Override
    public ISchemaElement updateSchemaElement() {

        if (this.eyepiece == null) {
            return null;
        }

        // Check mandatory fields
        String modelName = this.getModelName();
        if (modelName == null) {
            return null;
        }
        this.eyepiece.setModel(modelName);

        float focalLength = this.getFocalLength();
        if (Float.isNaN(focalLength)) {
            return null;
        }
        if (focalLength < 0) {
            this.createWarning(AbstractPanel.bundle.getString("panel.eyepiece.warning.focalLengthGreater"));
            return null;
        }
        this.eyepiece.setFocalLength(focalLength);

        // Add optional fields
        this.eyepiece.setVendor(this.vendor.getText());

        Angle afov = null;
        try {
            afov = this.apparentFOV.getAngle();
        } catch (NumberFormatException nfe) {
            this.createWarning(AbstractPanel.bundle.getString("panel.warning.apparentFOVNoNumber"));
            return null;
        }
        if ((afov != null) && (afov.getValue() < 0)) {
            this.createWarning(AbstractPanel.bundle.getString("panel.eyepiece.warning.apparentFoVGreater"));
            return null;
        }
        this.eyepiece.setApparentFOV(afov);

        float maxFL = Float.NaN;
        if (this.zoomEyepiece.isSelected()) {
            maxFL = this.getMaxFocalLength();
            if (Float.isNaN(maxFL)) {
                return null;
            }
            if (maxFL < 0) {
                this.createWarning(AbstractPanel.bundle.getString("panel.eyepiece.warning.maxFocalLengthGreater"));
                return null;
            }
            if (maxFL < focalLength) {
                this.createWarning(
                        AbstractPanel.bundle.getString("panel.eyepiece.warning.maxFocalLengthSmallerFocalLength"));
                return null;
            }
        }
        this.eyepiece.setMaxFocalLength(maxFL);

        return this.eyepiece;

    }

    @Override
    public ISchemaElement createSchemaElement() {

        // Check mandatory fields
        String modelName = this.getModelName();
        if (modelName == null) {
            return null;
        }

        float focalLength = this.getFocalLength();
        if (Float.isNaN(focalLength)) {
            return null;
        }
        if (focalLength < 0) {
            this.createWarning(AbstractPanel.bundle.getString("panel.eyepiece.warning.focalLengthGreater"));
            return null;
        }

        // Create eyepiece
        this.eyepiece = new Eyepiece(modelName, focalLength);

        // Add optional attributes
        Angle afov = null;
        try {
            afov = this.apparentFOV.getAngle();
        } catch (NumberFormatException nfe) {
            this.createWarning(AbstractPanel.bundle.getString("panel.warning.apparentFOVNoNumber"));
            return null;
        }
        if (afov != null) {
            if (afov.getValue() < 0) {
                this.createWarning(AbstractPanel.bundle.getString("panel.eyepiece.warning.apparentFoVGreater"));
                return null;
            }
            eyepiece.setApparentFOV(afov);
        }

        String vendor = this.vendor.getText();
        if ((vendor != null) && !("".equals(vendor))) {
            eyepiece.setVendor(vendor);
        }

        float maxFL = Float.NaN;
        if (this.zoomEyepiece.isSelected()) {
            maxFL = this.getMaxFocalLength();
            if (Float.isNaN(maxFL)) {
                return null;
            }
            if (maxFL < 0) {
                this.createWarning(AbstractPanel.bundle.getString("panel.eyepiece.warning.maxFocalLengthGreater"));
                return null;
            }
            if (maxFL < focalLength) {
                this.createWarning(
                        AbstractPanel.bundle.getString("panel.eyepiece.warning.maxFocalLengthSmallerFocalLength"));
                return null;
            }
        }
        this.eyepiece.setMaxFocalLength(maxFL);

        return this.eyepiece;

    }

    private String getModelName() {

        String modelName = this.model.getText();
        if ((modelName == null) || ("".equals(modelName))) {
            this.createWarning(AbstractPanel.bundle.getString("panel.eyepiece.warning.noModelName"));
            return null;
        }

        return modelName;

    }

    private float getFocalLength() {

        String focalLength = this.focalLength.getText();
        if ((focalLength == null) || ("".equals(focalLength))) {
            this.createWarning(AbstractPanel.bundle.getString("panel.eyepiece.warning.noFocalLength"));
            return Float.NaN;
        }
        float fl = 0.0f;
        try {
            fl = FloatUtil.parseFloat(focalLength);
        } catch (NumberFormatException nfe) {
            this.createWarning(AbstractPanel.bundle.getString("panel.eyepiece.warning.FocalLengthNumeric"));
            return Float.NaN;
        }

        return fl;

    }

    private float getMaxFocalLength() {

        String maxFocalLength = this.maxFocalLength.getText();
        if ((maxFocalLength == null) || ("".equals(maxFocalLength))) {
            this.createWarning(AbstractPanel.bundle.getString("panel.eyepiece.warning.noMaxFocalLength"));
            return Float.NaN;
        }
        float fl = 0.0f;
        try {
            fl = FloatUtil.parseFloat(maxFocalLength);
        } catch (NumberFormatException nfe) {
            this.createWarning(AbstractPanel.bundle.getString("panel.eyepiece.warning.MaxFocalLengthNumeric"));
            return Float.NaN;
        }

        return fl;

    }

    private void loadSchemaElement() {

        this.vendor.setText(this.eyepiece.getVendor());
        this.vendor.setEditable(this.isEditable());

        this.model.setText(this.eyepiece.getModel());
        this.model.setEditable(this.isEditable());

        this.focalLength.setText(String.valueOf(this.eyepiece.getFocalLength()));
        this.focalLength.setEditable(this.isEditable());

        if (!Float.isNaN(this.eyepiece.getMaxFocalLength())) { // Is this a zoom eyepiece
            this.maxFocalLength.setText("" + this.eyepiece.getMaxFocalLength());
            this.maxFocalLength.setEditable(this.isEditable());
            this.zoomEyepiece.setSelected(true);
        }

        if (this.eyepiece.getApparentFOV() != null) {
            Angle afov = new Angle(this.eyepiece.getApparentFOV().toDegree(), Angle.DEGREE);
            this.apparentFOV.setAngle(afov);
        }
        this.apparentFOV.setEditable(this.isEditable());

    }

    private void createPanel() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 1, 1, 5, 1);
        OMLabel LmodelName = new OMLabel(AbstractPanel.bundle.getString("panel.eyepiece.label.model"), true);
        LmodelName.setToolTipText(AbstractPanel.bundle.getString("panel.eyepiece.tooltip.model"));
        gridbag.setConstraints(LmodelName, constraints);
        this.add(LmodelName);
        ConstraintsBuilder.buildConstraints(constraints, 1, 0, 1, 1, 45, 1);
        this.model = new JTextField();
        this.model.setToolTipText(AbstractPanel.bundle.getString("panel.eyepiece.tooltip.model"));
        gridbag.setConstraints(this.model, constraints);
        this.add(this.model);

        ConstraintsBuilder.buildConstraints(constraints, 2, 0, 1, 1, 5, 1);
        OMLabel LvendorName = new OMLabel(AbstractPanel.bundle.getString("panel.eyepiece.label.vendor"),
                SwingConstants.RIGHT, false);
        LvendorName.setToolTipText(AbstractPanel.bundle.getString("panel.eyepiece.tooltip.vendor"));
        gridbag.setConstraints(LvendorName, constraints);
        this.add(LvendorName);
        ConstraintsBuilder.buildConstraints(constraints, 3, 0, 1, 1, 45, 1);
        this.vendor = new JTextField();
        this.vendor.setToolTipText(AbstractPanel.bundle.getString("panel.eyepiece.tooltip.vendor"));
        gridbag.setConstraints(this.vendor, constraints);
        this.add(this.vendor);

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 4, 1, 50, 1);
        this.zoomEyepiece = new JCheckBox(AbstractPanel.bundle.getString("panel.eyepiece.label.zoomEyepiece"), false);
        this.zoomEyepiece.addItemListener(this);
        if (this.isEditable()) {
            this.zoomEyepiece.setEnabled(true);
        } else {
            this.zoomEyepiece.setEnabled(false);
        }
        gridbag.setConstraints(this.zoomEyepiece, constraints);
        this.add(this.zoomEyepiece);

        ConstraintsBuilder.buildConstraints(constraints, 0, 2, 1, 1, 5, 1);
        LfocalLengthName = new OMLabel(AbstractPanel.bundle.getString("panel.eyepiece.label.focalLength"), true);
        LfocalLengthName.setToolTipText(AbstractPanel.bundle.getString("panel.eyepiece.tooltip.focalLength"));
        gridbag.setConstraints(LfocalLengthName, constraints);
        this.add(LfocalLengthName);
        ConstraintsBuilder.buildConstraints(constraints, 1, 2, 1, 1, 45, 1);
        this.focalLength = new JTextField();
        this.focalLength.setToolTipText(AbstractPanel.bundle.getString("panel.eyepiece.tooltip.focalLength"));
        gridbag.setConstraints(this.focalLength, constraints);
        this.add(this.focalLength);

        ConstraintsBuilder.buildConstraints(constraints, 2, 2, 1, 1, 5, 1);
        this.LmaxFocalLengthName = new OMLabel(AbstractPanel.bundle.getString("panel.eyepiece.label.maxFocalLength"),
                SwingConstants.RIGHT, true);
        LmaxFocalLengthName.setToolTipText(AbstractPanel.bundle.getString("panel.eyepiece.tooltip.maxFocalLength"));
        gridbag.setConstraints(LmaxFocalLengthName, constraints);
        this.add(LmaxFocalLengthName);
        this.LmaxFocalLengthName.setVisible(false); // Show when zoomEyepiece checkbox gets selected
        ConstraintsBuilder.buildConstraints(constraints, 3, 2, 1, 1, 45, 1);
        this.maxFocalLength = new JTextField();
        this.maxFocalLength.setToolTipText(AbstractPanel.bundle.getString("panel.eyepiece.tooltip.maxFocalLength"));
        gridbag.setConstraints(this.maxFocalLength, constraints);
        this.add(this.maxFocalLength);
        this.maxFocalLength.setVisible(false); // Show when zoomEyepiece checkbox gets selected

        ConstraintsBuilder.buildConstraints(constraints, 0, 4, 1, 1, 5, 1);
        JLabel LapparentFOVName = new OMLabel(AbstractPanel.bundle.getString("panel.eyepiece.label.appearendFoV"),
                false);
        LapparentFOVName.setToolTipText(AbstractPanel.bundle.getString("panel.eyepiece.tooltip.apparentFoV"));
        gridbag.setConstraints(LapparentFOVName, constraints);
        this.add(LapparentFOVName);
        ConstraintsBuilder.buildConstraints(constraints, 1, 4, 1, 1, 45, 1);
        this.apparentFOV = new AngleContainer(Angle.DEGREE, this.isEditable());
        this.apparentFOV.setToolTipText(AbstractPanel.bundle.getString("panel.eyepiece.tooltip.apparentFoV"));
        gridbag.setConstraints(this.apparentFOV, constraints);
        this.add(this.apparentFOV);

        ConstraintsBuilder.buildConstraints(constraints, 0, 5, 4, 1, 45, 92);
        constraints.fill = GridBagConstraints.BOTH;
        JLabel Lfill = new JLabel("");
        gridbag.setConstraints(Lfill, constraints);
        this.add(Lfill);

    }

}
