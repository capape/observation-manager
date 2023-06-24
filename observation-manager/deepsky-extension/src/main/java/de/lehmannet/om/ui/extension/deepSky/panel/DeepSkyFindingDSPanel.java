/*
 * ====================================================================
 * /extension/deepSky/panel/DeepSkyFindingDSPanel.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.deepSky.panel;

import static de.lehmannet.om.ICloneable.copyOrNull;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.apache.commons.lang3.StringUtils;

import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ISession;
import de.lehmannet.om.extension.deepSky.DeepSkyFinding;
import de.lehmannet.om.extension.deepSky.DeepSkyFindingDS;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.IConfiguration;
import de.lehmannet.om.ui.util.OMLabel;
import de.lehmannet.om.ui.util.TristateCheckbox;

public class DeepSkyFindingDSPanel extends DeepSkyFindingPanel {

    private static final long serialVersionUID = 7078168276916621618L;

    private static final String XSI_TYPE = "oal:findingsDeepSkyDSType";

    private JComponent colorMain = null; // Will be JComboBox or JTextField
    private JComponent colorCompanion = null; // Will be JComboBox or JTextField
    private final TristateCheckbox equalBrightness = new TristateCheckbox();
    private final TristateCheckbox niceSurrounding = new TristateCheckbox();

    public DeepSkyFindingDSPanel(IConfiguration configuration, DeepSkyFindingDS paramFinding, ISession s,
            Boolean editable) throws IllegalArgumentException {

        super(configuration, paramFinding, s, editable);

        this.finding = copyOrNull(paramFinding);

        this.createDSPanel();

        if (this.finding != null) {
            this.loadSchemaElementDS();
        }

    }

    // ------
    // JPanel -----------------------------------------------------------------
    // ------

    @Override
    public String getName() {

        return this.bundle.getString("panel.findingDS.name");

    }

    // -------------
    // IFindingPanel -----------------------------------------------------------
    // -------------

    @Override
    public String getXSIType() {

        return DeepSkyFindingDSPanel.XSI_TYPE;

    }

    // -------------
    // AbstractPanel ----------------------------------------------------------
    // -------------

    @Override
    public ISchemaElement updateSchemaElement() {

        if (this.finding == null) {
            return null;
        }

        DeepSkyFindingDS findingDS = (DeepSkyFindingDS) super.updateSchemaElement();

        // Something went wrong here. Maybe given values are malformed
        if (findingDS == null) {
            return null;
        }

        // Set optional DS elements
        String cm = this.getColorMain();
        findingDS.setMainStarColor(cm);

        String cs = this.getColorCompanion();
        findingDS.setCompanionStarColor(cs);

        if (this.equalBrightness.isNASelected()) {
            if (this.equalBrightness.isFalseSelected()) {
                findingDS.setEqualBrightness(Boolean.FALSE);
            } else {
                findingDS.setEqualBrightness(Boolean.TRUE);
            }
        } else {
            findingDS.setEqualBrightness(null);
        }

        if (this.niceSurrounding.isNASelected()) {
            if (this.niceSurrounding.isFalseSelected()) {
                findingDS.setNiceSurrounding(Boolean.FALSE);
            } else {
                findingDS.setNiceSurrounding(Boolean.TRUE);
            }
        } else {
            findingDS.setNiceSurrounding(null);
        }

        this.finding = findingDS;

        return this.finding.copy();

    }

    @Override
    public ISchemaElement createSchemaElement() {

        int rating = this.getRating();
        if (rating == -1) {
            this.createWarning(this.bundle.getString("panel.finding.warning.setRating"));
            return null;
        }

        this.finding = new DeepSkyFindingDS(this.findingContainer.getDescription(), rating);

        // Something went wrong. Maybe entered values were malformed...

        // Set all other fields
        this.finding = (DeepSkyFinding) this.updateSchemaElement();

        // Something went wrong. Maybe entered values were malformed...
        if (this.finding == null) {
            return null;
        }

        return this.finding.copy();

    }

    // Do not call this createPanel! Otherwise contructor of super call will call
    // this (if called by our constructor)
    private void createDSPanel() {

        // this.createPanel(); <--- will be called via our constructor -> super ->
        // createPanel()

        GridBagLayout gridbag = (GridBagLayout) this.getLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        // this.setLayout(gridbag); <-- already set by super class

        ConstraintsBuilder.buildConstraints(constraints, 0, 7, 1, 1, 5, 22);
        OMLabel lColorMain = new OMLabel(this.bundle.getString("panel.findingDS.label.colormain"), false);
        lColorMain.setToolTipText(this.bundle.getString("panel.findingDS.tooltip.colormain"));
        gridbag.setConstraints(lColorMain, constraints);
        this.add(lColorMain);
        ConstraintsBuilder.buildConstraints(constraints, 1, 7, 1, 1, 45, 22);
        if (this.isEditable()) {
            JComboBox<BoxItem> boxItemBox = this.getColorBox();
            boxItemBox.setEditable(this.isEditable());
            this.colorMain = boxItemBox;
        } else {
            JTextField textField = new JTextField();
            textField.setEditable(this.isEditable());
            this.colorMain = textField;
        }
        this.colorMain.setToolTipText(this.bundle.getString("panel.findingDS.tooltip.colormain"));
        gridbag.setConstraints(this.colorMain, constraints);
        this.add(this.colorMain);

        ConstraintsBuilder.buildConstraints(constraints, 2, 7, 1, 1, 5, 22);
        OMLabel lColorCompanion = new OMLabel(this.bundle.getString("panel.findingDS.label.colorcompanion"),
                SwingConstants.RIGHT, false);
        lColorCompanion.setToolTipText(this.bundle.getString("panel.findingDS.tooltip.colorcompanion"));
        gridbag.setConstraints(lColorCompanion, constraints);
        this.add(lColorCompanion);
        ConstraintsBuilder.buildConstraints(constraints, 3, 7, 1, 1, 45, 22);

        if (this.isEditable()) {
            JComboBox<BoxItem> boxItemBox = this.getColorBox();
            boxItemBox.setEditable(this.isEditable());
            this.colorCompanion = boxItemBox;
        } else {
            JTextField textField = new JTextField();
            textField.setEditable(this.isEditable());
            this.colorCompanion = textField;
        }

        this.colorCompanion.setToolTipText(this.bundle.getString("panel.findingDS.tooltip.colorcompanion"));
        gridbag.setConstraints(this.colorCompanion, constraints);
        this.add(this.colorCompanion);

        ConstraintsBuilder.buildConstraints(constraints, 0, 8, 1, 1, 5, 22);
        OMLabel lEqualBrightness = new OMLabel(this.bundle.getString("panel.findingDS.label.equalbrightness"), false);
        lEqualBrightness.setToolTipText(this.bundle.getString("panel.findingDS.tooltip.equalbrightness"));
        gridbag.setConstraints(lEqualBrightness, constraints);
        this.add(lEqualBrightness);
        ConstraintsBuilder.buildConstraints(constraints, 1, 8, 3, 1, 45, 22);
        this.equalBrightness.setToolTipText(this.bundle.getString("panel.findingDS.tooltip.equalbrightness"));
        gridbag.setConstraints(this.equalBrightness, constraints);
        this.add(this.equalBrightness);

        ConstraintsBuilder.buildConstraints(constraints, 0, 9, 1, 1, 5, 22);
        OMLabel lNiceSurrounding = new OMLabel(this.bundle.getString("panel.findingDS.label.nicesurrounding"), false);
        lNiceSurrounding.setToolTipText(this.bundle.getString("panel.findingDS.tooltip.nicesurrounding"));
        gridbag.setConstraints(lNiceSurrounding, constraints);
        this.add(lNiceSurrounding);
        ConstraintsBuilder.buildConstraints(constraints, 1, 9, 3, 1, 45, 22);
        this.niceSurrounding.setToolTipText(this.bundle.getString("panel.findingDS.tooltip.nicesurrounding"));
        gridbag.setConstraints(this.niceSurrounding, constraints);
        this.add(this.niceSurrounding);

        ConstraintsBuilder.buildConstraints(constraints, 0, 10, 4, 1, 14, 87);
        constraints.fill = GridBagConstraints.BOTH;
        JLabel lFill = new JLabel("");
        gridbag.setConstraints(lFill, constraints);
        this.add(lFill);

    }

    private void loadSchemaElementDS() {

        this.loadSchemaElement();

        DeepSkyFindingDS findingDS = (DeepSkyFindingDS) this.finding;

        if (findingDS.getColorMain() != null) {

            if (this.isEditable()) {
                JComboBox<BoxItem> boxItemBox = getJComboBoxForMainColor();
                boxItemBox.setSelectedItem(new BoxItem(findingDS.getColorMain().toLowerCase(Locale.getDefault())));
                boxItemBox.setEditable(this.isEditable());
            } else {
                JTextField textField = (JTextField) this.colorMain;
                textField.setText(this.bundle.getString(
                        "panel.findingDS.dropdown.color." + findingDS.getColorMain().toLowerCase(Locale.getDefault())));
                textField.setEditable(this.isEditable());
            }

        }

        if (findingDS.getColorCompanion() != null) {

            if (this.isEditable()) {
                JComboBox<BoxItem> boxItemBox = getJComboBoxForColorCompanion();
                boxItemBox.setSelectedItem(new BoxItem(findingDS.getColorCompanion().toLowerCase(Locale.getDefault())));
                boxItemBox.setEditable(this.isEditable());
            } else {
                JTextField textField = (JTextField) this.colorCompanion;
                textField.setText(this.bundle.getString("panel.findingDS.dropdown.color."
                        + findingDS.getColorCompanion().toLowerCase(Locale.getDefault())));
                textField.setEditable(this.isEditable());
            }

        }

        try {
            if (findingDS.getEqualBrightness()) {
                this.equalBrightness.setTrueSelected();
            } else {
                this.equalBrightness.setFalseSelected();
            }
        } catch (IllegalStateException ise) {
            // Value was never set
            this.equalBrightness.setNASelected();
        }
        this.equalBrightness.setEditable(this.isEditable());

        try {
            if (findingDS.getNiceSurrounding()) {
                this.niceSurrounding.setTrueSelected();
            } else {
                this.niceSurrounding.setFalseSelected();
            }
        } catch (IllegalStateException ise) {
            // Value was never set
            this.niceSurrounding.setNASelected();
        }
        this.niceSurrounding.setEditable(this.isEditable());

    }

    private JComboBox<BoxItem> getColorBox() {

        JComboBox<BoxItem> box = new JComboBox<>();
        box.addItem(BoxItem.EMPTY);
        box.addItem(new BoxItem(DeepSkyFindingDS.COLOR_WHITE));
        box.addItem(new BoxItem(DeepSkyFindingDS.COLOR_RED));
        box.addItem(new BoxItem(DeepSkyFindingDS.COLOR_ORANGE));
        box.addItem(new BoxItem(DeepSkyFindingDS.COLOR_YELLOW));
        box.addItem(new BoxItem(DeepSkyFindingDS.COLOR_GREEN));
        box.addItem(new BoxItem(DeepSkyFindingDS.COLOR_BLUE));

        return box;

    }

    private String getColorMain() {

        String cm = null;
        if (this.isEditable()) {
            JComboBox<BoxItem> boxItemBox = getJComboBoxForMainColor();
            BoxItem bi = (BoxItem) boxItemBox.getSelectedItem();
            return bi.getValue();
        } else {
            JTextField textField = (JTextField) this.colorMain;
            cm = textField.getText();
            if (StringUtils.isBlank(cm)) {
                return null;
            }
            return cm;
        }

    }

    private JComboBox<BoxItem> getJComboBoxForMainColor() {
        return toJComboBox(this.colorMain);
    }

    private JComboBox<BoxItem> toJComboBox(JComponent component) {
        return (JComboBox<BoxItem>) component;
    }

    private JComboBox<BoxItem> getJComboBoxForColorCompanion() {
        return toJComboBox(this.colorCompanion);
    }

    private String getColorCompanion() {

        String cm = null;
        if (this.isEditable()) {
            JComboBox<BoxItem> boxItemBox = getJComboBoxForColorCompanion();
            BoxItem bi = (BoxItem) boxItemBox.getSelectedItem();
            return bi.getValue();
        } else {
            JTextField textField = (JTextField) this.colorCompanion;
            cm = textField.getText();
            if (StringUtils.isBlank(cm)) {
                return null;
            }
            return cm;
        }
    }

    // -------------------
    // DeepSkyFindingPanel ------------------------------------------------------
    // -------------------

    // --------------------------------------------------------------------------

    @Override
    protected JComboBox<String> getVisualRatingBox() {

        JComboBox<String> box = new JComboBox<>();
        box.addItem(BoxItem.EMPTY.toString());
        box.addItem(this.bundle.getString("panel.dsfinding.dropdown.rating.1"));
        box.addItem(this.bundle.getString("panel.dsfinding.dropdown.rating.2"));
        box.addItem(this.bundle.getString("panel.dsfinding.dropdown.rating.3"));
        box.addItem(this.bundle.getString("panel.finding.dropdown.rating.99"));

        return box;

    }

}

class BoxItem {

    private static final String EMPTY_VALUE = "----";

    public static final BoxItem EMPTY = new BoxItem(EMPTY_VALUE);
    private final String value;

    public BoxItem(String value) {

        this.value = value;

    }

    @Override
    public String toString() {

        if (EMPTY_VALUE.equals(value)) {
            return value;
        }

        ResourceBundle bundle = ResourceBundle.getBundle("de.lehmannet.om.ui.extension.deepSky.DeepSky",
                Locale.getDefault());

        return bundle.getString("panel.findingDS.dropdown.color." + value.toLowerCase(Locale.getDefault()));

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BoxItem other = (BoxItem) obj;
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        return true;
    }

    public String getValue() {

        if (EMPTY_VALUE.equals(value)) {
            return null;
        }

        return this.value;

    }

}
