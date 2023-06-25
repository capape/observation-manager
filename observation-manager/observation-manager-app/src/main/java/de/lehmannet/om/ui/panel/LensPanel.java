/*
 * ====================================================================
 * /panel/LensPanel.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import de.lehmannet.om.ICloneable;
import de.lehmannet.om.ILens;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.Lens;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.OMLabel;
import de.lehmannet.om.util.FloatUtil;

public class LensPanel extends AbstractPanel {

    private static final long serialVersionUID = -1170117637615757746L;

    private ILens lens = null;

    private JTextField vendor = null;
    private JTextField model = null;
    private JTextField factor = null;

    public LensPanel(ILens lens, boolean editable) {

        super(editable);

        this.lens = lens;

        this.createPanel();

        if (lens != null) {
            this.loadSchemaElement();
        }

    }

    @Override
    public ISchemaElement getSchemaElement() {

        return ICloneable.copyOrNull(this.lens);

    }

    @Override
    public ISchemaElement updateSchemaElement() {

        if (this.lens == null) {
            return null;
        }

        // Check mandatory fields
        String modelName = this.getModelName();
        if (modelName == null) {
            return null;
        }
        this.lens.setModel(modelName);

        float factor = this.getFactor();
        if (Float.isNaN(factor)) {
            return null;
        }
        if (factor <= 0) {
            this.createWarning(AbstractPanel.bundle.getString("panel.lens.warning.factorGreater"));
            return null;
        }
        this.lens.setFactor(factor);

        // Add optional fields
        this.lens.setVendor(this.vendor.getText());

        return ICloneable.copyOrNull(this.lens);

    }

    @Override
    public ISchemaElement createSchemaElement() {

        // Check mandatory fields
        String modelName = this.getModelName();
        if (modelName == null) {
            return null;
        }

        float factor = this.getFactor();
        if (Float.isNaN(factor)) {
            return null;
        }
        if (factor < 0) {
            this.createWarning(AbstractPanel.bundle.getString("panel.lens.warning.factorGreater"));
            return null;
        }

        // Create lens
        this.lens = new Lens(modelName, factor);

        // Add optional attributes
        String vendor = this.vendor.getText();
        if ((vendor != null) && !("".equals(vendor))) {
            this.lens.setVendor(vendor);
        }

        return ICloneable.copyOrNull(this.lens);

    }

    private String getModelName() {

        String modelName = this.model.getText();
        if ((modelName == null) || ("".equals(modelName))) {
            this.createWarning(AbstractPanel.bundle.getString("panel.lens.warning.noModelName"));
            return null;
        }

        return modelName;

    }

    private float getFactor() {

        String factor = this.factor.getText();
        if ((factor == null) || ("".equals(factor))) {
            this.createWarning(AbstractPanel.bundle.getString("panel.lens.warning.noFactor"));
            return Float.NaN;
        }
        float f = 0.0f;
        try {
            f = FloatUtil.parseFloat(factor);
        } catch (NumberFormatException nfe) {
            this.createWarning(AbstractPanel.bundle.getString("panel.lens.warning.FactorNumeric"));
            return Float.NaN;
        }

        return f;

    }

    private void loadSchemaElement() {

        this.vendor.setText(this.lens.getVendor());
        this.vendor.setEditable(this.isEditable());

        this.model.setText(this.lens.getModel());
        this.model.setEditable(this.isEditable());

        this.factor.setText(String.valueOf(this.lens.getFactor()));
        this.factor.setEditable(this.isEditable());

    }

    private void createPanel() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 1, 1, 5, 1);
        OMLabel LmodelName = new OMLabel(AbstractPanel.bundle.getString("panel.lens.label.model"), true);
        LmodelName.setToolTipText(AbstractPanel.bundle.getString("panel.lens.tooltip.model"));
        gridbag.setConstraints(LmodelName, constraints);
        this.add(LmodelName);
        ConstraintsBuilder.buildConstraints(constraints, 1, 0, 1, 1, 45, 1);
        this.model = new JTextField();
        this.model.setToolTipText(AbstractPanel.bundle.getString("panel.lens.tooltip.model"));
        gridbag.setConstraints(this.model, constraints);
        this.add(this.model);

        ConstraintsBuilder.buildConstraints(constraints, 2, 0, 1, 1, 5, 1);
        OMLabel LvendorName = new OMLabel(AbstractPanel.bundle.getString("panel.lens.label.vendor"),
                SwingConstants.RIGHT, false);
        LvendorName.setToolTipText(AbstractPanel.bundle.getString("panel.lens.tooltip.vendor"));
        gridbag.setConstraints(LvendorName, constraints);
        this.add(LvendorName);
        ConstraintsBuilder.buildConstraints(constraints, 3, 0, 1, 1, 45, 1);
        this.vendor = new JTextField();
        this.vendor.setToolTipText(AbstractPanel.bundle.getString("panel.lens.tooltip.vendor"));
        gridbag.setConstraints(this.vendor, constraints);
        this.add(this.vendor);

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 1, 1, 5, 1);
        OMLabel Lfactor = null;
        if (this.isEditable()) {
            Lfactor = new OMLabel(AbstractPanel.bundle.getString("panel.lens.label.factor") + "*", false);
        } else {
            Lfactor = new OMLabel(AbstractPanel.bundle.getString("panel.lens.label.factor"), false);
        }
        Lfactor.setToolTipText(AbstractPanel.bundle.getString("panel.lens.tooltip.factor"));
        gridbag.setConstraints(Lfactor, constraints);
        this.add(Lfactor);
        ConstraintsBuilder.buildConstraints(constraints, 1, 1, 1, 1, 45, 1);
        this.factor = new JTextField(3);
        this.factor.setToolTipText(AbstractPanel.bundle.getString("panel.lens.tooltip.factor"));
        gridbag.setConstraints(this.factor, constraints);
        this.add(this.factor);

        int rowCounter = 2;
        if (this.isEditable()) {
            ConstraintsBuilder.buildConstraints(constraints, 0, rowCounter++, 4, 1, 45, 1);
            OMLabel Lhint = new OMLabel("* " + AbstractPanel.bundle.getString("panel.lens.label.hint"), true);
            gridbag.setConstraints(Lhint, constraints);
            this.add(Lhint);
        }

        ConstraintsBuilder.buildConstraints(constraints, 0, rowCounter++, 4, 1, 45, 92);
        constraints.fill = GridBagConstraints.BOTH;
        JLabel Lfill = new JLabel("");
        gridbag.setConstraints(Lfill, constraints);
        this.add(Lfill);

    }

}
