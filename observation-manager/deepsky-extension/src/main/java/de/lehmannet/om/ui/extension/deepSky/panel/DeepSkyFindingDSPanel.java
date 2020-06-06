/* ====================================================================
 * /extension/deepSky/panel/DeepSkyFindingDSPanel.java
 *
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.deepSky.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Locale;
import java.util.Objects;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import de.lehmannet.om.IFinding;
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
    private static final String EMPTY_ITEM = "----";

    private JComponent colorMain = null; // Will be JComboBox or JTextField
    private JComponent colorCompanion = null; // Will be JComboBox or JTextField
    private final TristateCheckbox equalBrightness = new TristateCheckbox();
    private final TristateCheckbox niceSurrounding = new TristateCheckbox();

    public DeepSkyFindingDSPanel(IConfiguration configuration, IFinding paramFinding, ISession s, Boolean editable)
            throws IllegalArgumentException {

        super(configuration, paramFinding, s, editable);

        if (paramFinding != null) {
            if (!(paramFinding instanceof DeepSkyFindingDS)) {
                // DeepSkyFindingPanel might already have converted the Finding into a
                // DeepSkyFinding
                paramFinding = super.finding;

                try { // Might be old (< 1.5) DeepSkyFinding
                    if (paramFinding != null) {
                        this.finding = new DeepSkyFindingDS(paramFinding.getDescription(),
                                ((DeepSkyFinding) paramFinding).getRating());
                    } else { // Finding was something else. So recycle description and use 0 as default
                             // rating
                        this.finding = new DeepSkyFindingDS("", 0);
                    }
                } catch (ClassCastException cce) {
                    throw new IllegalArgumentException(
                            "Passed IFinding must derive from de.lehmannet.om.extension.deepSky.DeepSkyFindingDS\n");
                }
            } else {
                this.finding = (DeepSkyFindingDS) paramFinding;
            }
        }

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

        return this.finding;

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

        return this.finding;

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
        OMLabel LcolorMain = new OMLabel(this.bundle.getString("panel.findingDS.label.colormain"), false);
        LcolorMain.setToolTipText(this.bundle.getString("panel.findingDS.tooltip.colormain"));
        gridbag.setConstraints(LcolorMain, constraints);
        this.add(LcolorMain);
        ConstraintsBuilder.buildConstraints(constraints, 1, 7, 1, 1, 45, 22);
        if (this.isEditable()) {
            this.colorMain = this.getColorBox();
            ((JComboBox) this.colorMain).setEditable(this.isEditable());
            // ((JComboBox)this.colorMain).addItemListener(this);
        } else {
            this.colorMain = new JTextField();
            ((JTextField) this.colorMain).setEditable(this.isEditable());
        }
        this.colorMain.setToolTipText(this.bundle.getString("panel.findingDS.tooltip.colormain"));
        gridbag.setConstraints(this.colorMain, constraints);
        this.add(this.colorMain);

        ConstraintsBuilder.buildConstraints(constraints, 2, 7, 1, 1, 5, 22);
        OMLabel LcolorCompanion = new OMLabel(this.bundle.getString("panel.findingDS.label.colorcompanion"),
                SwingConstants.RIGHT, false);
        LcolorCompanion.setToolTipText(this.bundle.getString("panel.findingDS.tooltip.colorcompanion"));
        gridbag.setConstraints(LcolorCompanion, constraints);
        this.add(LcolorCompanion);
        ConstraintsBuilder.buildConstraints(constraints, 3, 7, 1, 1, 45, 22);
        if (this.isEditable()) {
            this.colorCompanion = this.getColorBox();
            ((JComboBox) this.colorCompanion).setEditable(this.isEditable());
            // ((JComboBox)this.colorCompanion).addItemListener(this);
        } else {
            this.colorCompanion = new JTextField();
            ((JTextField) this.colorCompanion).setEditable(this.isEditable());
        }
        this.colorCompanion.setToolTipText(this.bundle.getString("panel.findingDS.tooltip.colorcompanion"));
        gridbag.setConstraints(this.colorCompanion, constraints);
        this.add(this.colorCompanion);

        ConstraintsBuilder.buildConstraints(constraints, 0, 8, 1, 1, 5, 22);
        OMLabel LequalBrightness = new OMLabel(this.bundle.getString("panel.findingDS.label.equalbrightness"), false);
        LequalBrightness.setToolTipText(this.bundle.getString("panel.findingDS.tooltip.equalbrightness"));
        gridbag.setConstraints(LequalBrightness, constraints);
        this.add(LequalBrightness);
        ConstraintsBuilder.buildConstraints(constraints, 1, 8, 3, 1, 45, 22);
        this.equalBrightness.setToolTipText(this.bundle.getString("panel.findingDS.tooltip.equalbrightness"));
        gridbag.setConstraints(this.equalBrightness, constraints);
        this.add(this.equalBrightness);

        ConstraintsBuilder.buildConstraints(constraints, 0, 9, 1, 1, 5, 22);
        OMLabel LniceSurrounding = new OMLabel(this.bundle.getString("panel.findingDS.label.nicesurrounding"), false);
        LniceSurrounding.setToolTipText(this.bundle.getString("panel.findingDS.tooltip.nicesurrounding"));
        gridbag.setConstraints(LniceSurrounding, constraints);
        this.add(LniceSurrounding);
        ConstraintsBuilder.buildConstraints(constraints, 1, 9, 3, 1, 45, 22);
        this.niceSurrounding.setToolTipText(this.bundle.getString("panel.findingDS.tooltip.nicesurrounding"));
        gridbag.setConstraints(this.niceSurrounding, constraints);
        this.add(this.niceSurrounding);

        ConstraintsBuilder.buildConstraints(constraints, 0, 10, 4, 1, 14, 87);
        constraints.fill = GridBagConstraints.BOTH;
        JLabel Lfill = new JLabel("");
        gridbag.setConstraints(Lfill, constraints);
        this.add(Lfill);

    }

    private void loadSchemaElementDS() {

        this.loadSchemaElement();

        DeepSkyFindingDS findingDS = (DeepSkyFindingDS) this.finding;

        if (findingDS.getColorMain() != null) {
            if (this.isEditable()) {
                ((JComboBox) this.colorMain).setSelectedItem(new BoxItem(findingDS.getColorMain().toLowerCase()));
                ((JComboBox) this.colorMain).setEditable(this.isEditable());
            } else {
                ((JTextField) this.colorMain).setText(this.bundle
                        .getString("panel.findingDS.dropdown.color." + findingDS.getColorMain().toLowerCase()));
                ((JTextField) this.colorMain).setEditable(this.isEditable());
            }
        }

        if (findingDS.getColorCompanion() != null) {
            if (this.isEditable()) {
                ((JComboBox) this.colorCompanion)
                        .setSelectedItem(new BoxItem(findingDS.getColorCompanion().toLowerCase()));
                ((JComboBox) this.colorCompanion).setEditable(this.isEditable());
            } else {
                ((JTextField) this.colorCompanion).setText(this.bundle
                        .getString("panel.findingDS.dropdown.color." + findingDS.getColorCompanion().toLowerCase()));
                ((JTextField) this.colorCompanion).setEditable(this.isEditable());
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

    private JComboBox getColorBox() {

        JComboBox box = new JComboBox();
        box.addItem(DeepSkyFindingDSPanel.EMPTY_ITEM);
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
            Object o = ((JComboBox) this.colorMain).getSelectedItem();
            if (o instanceof String) { // EMPTY-Item
                return null;
            } else {
                BoxItem bi = (BoxItem) o;
                return Objects.requireNonNull(bi).getColor();
            }
        } else {
            cm = ((JTextField) this.colorMain).getText();
            if ("".equals(cm.trim())) {
                return null;
            }
        }

        return cm;

    }

    private String getColorCompanion() {

        String cm = null;
        if (this.isEditable()) {
            Object o = ((JComboBox) this.colorCompanion).getSelectedItem();
            if (o instanceof String) { // EMPTY-Item
                return null;
            } else {
                BoxItem bi = (BoxItem) o;
                return Objects.requireNonNull(bi).getColor();
            }
        } else {
            cm = ((JTextField) this.colorCompanion).getText();
            if ("".equals(cm.trim())) {
                return null;
            }
        }

        return cm;

    }

    // -------------------
    // DeepSkyFindingPanel ------------------------------------------------------
    // -------------------

    // --------------------------------------------------------------------------

    @Override
    protected JComboBox<String> getVisualRatingBox() {

        JComboBox<String> box = new JComboBox<>();
        box.addItem("----");
        box.addItem(this.bundle.getString("panel.dsfinding.dropdown.rating.1"));
        box.addItem(this.bundle.getString("panel.dsfinding.dropdown.rating.2"));
        box.addItem(this.bundle.getString("panel.dsfinding.dropdown.rating.3"));
        box.addItem(this.bundle.getString("panel.finding.dropdown.rating.99"));

        return box;

    }

}

class BoxItem {

    private String color = null;

    public BoxItem(String color) {

        this.color = color;

    }

    @Override
    public String toString() {

        PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
                .getBundle("de.lehmannet.om.ui.extension.deepSky.DeepSky", Locale.getDefault());

        return bundle.getString("panel.findingDS.dropdown.color." + color.toLowerCase());

    }

    @Override
    public boolean equals(Object o) {

        if (o instanceof BoxItem) {
            return o.equals(color);
        }

        return false;

    }

    public String getColor() {

        return this.color;

    }

}
