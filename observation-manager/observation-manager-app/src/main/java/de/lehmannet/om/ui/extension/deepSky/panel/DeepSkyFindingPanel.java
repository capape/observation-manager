/* ====================================================================
 * /extension/deepSky/panel/DeepSkyFindingPanel.java
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

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import de.lehmannet.om.Angle;
import de.lehmannet.om.IFinding;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ISession;
import de.lehmannet.om.extension.deepSky.DeepSkyFinding;
import de.lehmannet.om.ui.container.AngleContainer;
import de.lehmannet.om.ui.container.FindingContainer;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.ui.panel.IFindingPanel;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.OMLabel;
import de.lehmannet.om.ui.util.TristateCheckbox;

public class DeepSkyFindingPanel extends AbstractPanel implements IFindingPanel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    final PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
            .getBundle("de.lehmannet.om.ui.extension.deepSky.DeepSky", Locale.getDefault());

    private static final String XSI_TYPE = "oal:findingsDeepSkyType";

    ObservationManager om = null;

    DeepSkyFinding finding = null;
    private JComboBox<String> rating = null;

    FindingContainer findingContainer = null;

    private AngleContainer smallDiameter = null;
    private AngleContainer largeDiameter = null;
    private JTextField ratingField = null;
    private TristateCheckbox stellar = null;
    private TristateCheckbox resolved = null;
    private TristateCheckbox mottled = null;
    private TristateCheckbox extended = null;
    private ISession session = null;

    DeepSkyFindingPanel(ObservationManager om, IFinding paramFinding, ISession s, Boolean editable)
            throws IllegalArgumentException {

        super(editable);

        DeepSkyFinding result = this.assureIsDeepSkyFinding(paramFinding);
        this.finding = result;
        this.session = s;
        this.om = om;

        this.createPanel();

        if (this.finding != null) {
            this.finding = result;
            this.loadSchemaElement();
        }

    }

    private DeepSkyFinding assureIsDeepSkyFinding(IFinding paramFinding) {
        if ((paramFinding != null) && !(paramFinding instanceof DeepSkyFinding)) {
            // throw new IllegalArgumentException("Passed IFinding must derive from
            // de.lehmannet.om.extension.deepSky.DeepSkyFinding\n");

            // Create DeepSkyFinding from given IFinding (must be a IFinding instance due to
            // parameter definition)
            // Rating will be set to 99 (unknown)
            DeepSkyFinding dsf = new DeepSkyFinding(paramFinding.getDescription(), 99);
            dsf.setLanguage(paramFinding.getLanguage());

            return dsf; // Update reference
        }
        return (DeepSkyFinding) paramFinding;
    }

    // ------
    // JPanel -----------------------------------------------------------------
    // ------

    @Override
    public String getName() {

        return this.bundle.getString("panel.finding.name");

    }

    // -------------
    // IFindingPanel -----------------------------------------------------------
    // -------------

    @Override
    public String getXSIType() {

        return DeepSkyFindingPanel.XSI_TYPE;

    }

    // -------------
    // AbstractPanel ----------------------------------------------------------
    // -------------

    @Override
    public ISchemaElement getSchemaElement() {

        return this.finding;

    }

    @Override
    public ISchemaElement updateSchemaElement() {

        if (this.finding == null) {
            return null;
        }

        int rating = this.getRating();
        if (rating == -1) {
            this.createWarning(this.bundle.getString("panel.finding.warning.setRating"));
            return null;
        }
        if (rating == (this.rating.getItemCount() - 1)) { // 99 = unknown rating
            rating = 99;
        }
        this.finding.setRating(rating);

        // Set optional elements
        Angle small = null;
        try {
            small = this.smallDiameter.getAngle();
        } catch (NumberFormatException nfe) {
            this.createWarning(this.bundle.getString("panel.warning.smallDiameterNoNumber"));
            return null;
        }
        if (small != null) {
            if (small.getValue() < 0) {
                this.createWarning(this.bundle.getString("panel.finding.warning.smallDiameterPositive"));
                return null;
            }
        }
        this.finding.setSmallDiameter(small);

        Angle large = null;
        try {
            large = this.largeDiameter.getAngle();
        } catch (NumberFormatException nfe) {
            this.createWarning(this.bundle.getString("panel.warning.largeDiameterNoNumber"));
            return null;
        }
        if (large != null) {
            if (large.getValue() < 0) {
                this.createWarning(this.bundle.getString("panel.finding.warning.largeDiameterPositive"));
                return null;
            }
        }
        this.finding.setLargeDiameter(large);

        if (this.findingContainer.getLanguage() != null) {
            this.finding.setLanguage(this.findingContainer.getLanguage());
        }

        this.finding.setDescription(this.findingContainer.getDescription());

        if (this.stellar.isNASelected()) {
            if (this.stellar.isFalseSelected()) {
                this.finding.setStellar(Boolean.FALSE);
            } else {
                this.finding.setStellar(Boolean.TRUE);
            }
        } else {
            this.finding.setStellar(null);
        }

        if (this.resolved.isNASelected()) {
            if (this.resolved.isFalseSelected()) {
                this.finding.setResolved(Boolean.FALSE);
            } else {
                this.finding.setResolved(Boolean.TRUE);
            }
        } else {
            this.finding.setResolved(null);
        }

        if (this.mottled.isNASelected()) {
            if (this.mottled.isFalseSelected()) {
                this.finding.setMottled(Boolean.FALSE);
            } else {
                this.finding.setMottled(Boolean.TRUE);
            }
        } else {
            this.finding.setMottled(null);
        }

        if (this.extended.isNASelected()) {
            if (this.extended.isFalseSelected()) {
                this.finding.setExtended(Boolean.FALSE);
            } else {
                this.finding.setExtended(Boolean.TRUE);
            }
        } else {
            this.finding.setExtended(null);
        }

        return this.finding;

    }

    @Override
    public ISchemaElement createSchemaElement() {

        int rating = this.getRating();
        if (rating == -1) {
            this.createWarning(this.bundle.getString("panel.finding.warning.setRating"));
            return null;
        }
        if (rating == (this.rating.getItemCount() - 1)) { // 99 = unknown rating
            rating = 99;
        }

        this.finding = new DeepSkyFinding(this.findingContainer.getDescription(), rating);

        // Set all other fields
        IFinding f = (IFinding) this.updateSchemaElement();
        if (f == null) {
            return null;
        } else {
            this.finding = (DeepSkyFinding) f;
        }

        return this.finding;

    }

    int getRating() {

        // Nothing selected
        if (this.rating.getSelectedIndex() == 0) {
            return -1;
        }

        return this.rating.getSelectedIndex();

    }

    private void createPanel() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        this.setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 1, 1, 4, 1);
        OMLabel LvisualRating = new OMLabel(this.bundle.getString("panel.finding.label.rating"), true);
        LvisualRating.setToolTipText(this.bundle.getString("panel.finding.tooltip.rating"));
        gridbag.setConstraints(LvisualRating, constraints);
        this.add(LvisualRating);
        ConstraintsBuilder.buildConstraints(constraints, 1, 0, 3, 1, 10, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.rating = this.getVisualRatingBox();
        this.ratingField = new JTextField();
        if (this.isEditable()) {
            this.rating.setToolTipText(this.bundle.getString("panel.finding.tooltip.rating"));
            gridbag.setConstraints(this.rating, constraints);
            this.add(this.rating);
        } else {
            this.ratingField.setToolTipText(this.bundle.getString("panel.finding.tooltip.rating"));
            gridbag.setConstraints(this.ratingField, constraints);
            this.add(this.ratingField);
        }

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 1, 1, 4, 1);
        OMLabel LsmallDiameter = new OMLabel(this.bundle.getString("panel.finding.label.smallDiameter"), false);
        LsmallDiameter.setToolTipText(this.bundle.getString("panel.finding.tooltip.smallDiameter"));
        gridbag.setConstraints(LsmallDiameter, constraints);
        this.add(LsmallDiameter);
        ConstraintsBuilder.buildConstraints(constraints, 1, 1, 1, 1, 15, 1);
        this.smallDiameter = new AngleContainer(Angle.ARCSECOND, this.isEditable());
        this.smallDiameter.setToolTipText(this.bundle.getString("panel.finding.tooltip.smallDiameter"));
        this.smallDiameter.setUnits(new String[] { Angle.ARCMINUTE, Angle.ARCSECOND });
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(this.smallDiameter, constraints);
        this.add(this.smallDiameter);

        ConstraintsBuilder.buildConstraints(constraints, 2, 1, 1, 1, 4, 1);
        OMLabel LlargeDiameter = new OMLabel(this.bundle.getString("panel.finding.label.largeDiameter"),
                SwingConstants.RIGHT, false);
        LlargeDiameter.setToolTipText(this.bundle.getString("panel.finding.tooltip.largeDiameter"));
        gridbag.setConstraints(LlargeDiameter, constraints);
        this.add(LlargeDiameter);
        ConstraintsBuilder.buildConstraints(constraints, 3, 1, 1, 1, 5, 1);
        this.largeDiameter = new AngleContainer(Angle.ARCSECOND, this.isEditable());
        this.largeDiameter.setToolTipText(this.bundle.getString("panel.finding.tooltip.largeDiameter"));
        this.largeDiameter.setUnits(new String[] { Angle.ARCMINUTE, Angle.ARCSECOND });
        // constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(this.largeDiameter, constraints);
        this.add(this.largeDiameter);

        ConstraintsBuilder.buildConstraints(constraints, 0, 2, 1, 1, 4, 1);
        OMLabel Lstellar = new OMLabel(this.bundle.getString("panel.finding.label.stellar"), false);
        Lstellar.setToolTipText(this.bundle.getString("panel.finding.tooltip.stellar"));
        gridbag.setConstraints(Lstellar, constraints);
        this.add(Lstellar);
        ConstraintsBuilder.buildConstraints(constraints, 1, 2, 3, 1, 10, 1);
        this.stellar = new TristateCheckbox();
        this.stellar.setToolTipText(this.bundle.getString("panel.finding.tooltip.stellar"));
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(this.stellar, constraints);
        this.add(this.stellar);

        ConstraintsBuilder.buildConstraints(constraints, 0, 3, 1, 1, 4, 1);
        OMLabel Lresolved = new OMLabel(this.bundle.getString("panel.finding.label.resolved"), false);
        Lresolved.setToolTipText(this.bundle.getString("panel.finding.tooltip.resolved"));
        gridbag.setConstraints(Lresolved, constraints);
        this.add(Lresolved);
        ConstraintsBuilder.buildConstraints(constraints, 1, 3, 3, 1, 10, 1);
        this.resolved = new TristateCheckbox();
        this.resolved.setToolTipText(this.bundle.getString("panel.finding.tooltip.resolved"));
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(this.resolved, constraints);
        this.add(this.resolved);

        ConstraintsBuilder.buildConstraints(constraints, 0, 4, 1, 1, 4, 1);
        OMLabel Lmottled = new OMLabel(this.bundle.getString("panel.finding.label.mottled"), false);
        Lmottled.setToolTipText(this.bundle.getString("panel.finding.tooltip.mottled"));
        gridbag.setConstraints(Lmottled, constraints);
        this.add(Lmottled);
        ConstraintsBuilder.buildConstraints(constraints, 1, 4, 3, 1, 10, 1);
        this.mottled = new TristateCheckbox();
        this.mottled.setToolTipText(this.bundle.getString("panel.finding.tooltip.mottled"));
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(this.mottled, constraints);
        this.add(this.mottled);

        ConstraintsBuilder.buildConstraints(constraints, 0, 5, 1, 1, 4, 1);
        OMLabel Lextended = new OMLabel(this.bundle.getString("panel.finding.label.extended"), false);
        Lextended.setToolTipText(this.bundle.getString("panel.finding.tooltip.extended"));
        gridbag.setConstraints(Lextended, constraints);
        this.add(Lextended);
        ConstraintsBuilder.buildConstraints(constraints, 1, 5, 3, 1, 10, 1);
        this.extended = new TristateCheckbox();
        this.extended.setToolTipText(this.bundle.getString("panel.finding.tooltip.extended"));
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(this.extended, constraints);
        this.add(this.extended);

        ConstraintsBuilder.buildConstraints(constraints, 0, 6, 4, 1, 14, 87);
        constraints.fill = GridBagConstraints.BOTH;
        this.findingContainer = new FindingContainer(this.om, this.finding, this.session, this.isEditable());
        gridbag.setConstraints(this.findingContainer, constraints);
        this.add(this.findingContainer);

        ConstraintsBuilder.buildConstraints(constraints, 0, 6, 4, 1, 14, 87);
        constraints.fill = GridBagConstraints.BOTH;
        JLabel Lfill = new JLabel("");
        gridbag.setConstraints(Lfill, constraints);
        this.add(Lfill);

    }

    void loadSchemaElement() {

        this.smallDiameter.setAngle(this.finding.getSmallDiameter());
        this.smallDiameter.setEditable(this.isEditable());

        this.largeDiameter.setAngle(this.finding.getLargeDiameter());
        this.largeDiameter.setEditable(this.isEditable());

        // Since 0.91:
        // Rating for e.g. DoubleStars only contains 3 values
        // Make sure that rating that exceed the current rating get mapped to unknown
        if (this.finding.getRating() > (this.rating.getItemCount() - 1)) {
            this.finding.setRating(99);
        }

        if (this.isEditable()) {
            if (this.finding.getRating() == 99) {
                this.rating.setSelectedIndex(this.rating.getItemCount() - 1);
            } else {
                this.rating.setSelectedIndex(this.finding.getRating());
            }

            this.rating.setEditable(this.isEditable());
        } else {
            if (this.finding.getRating() == 99) { // Get last item
                this.ratingField.setText("" + this.rating.getItemAt(this.rating.getItemCount() - 1));
            } else {
                this.ratingField.setText("" + this.rating.getItemAt(this.finding.getRating()));
            }
            this.ratingField.setEditable(this.isEditable());
        }

        try {
            if (this.finding.getStellar()) {
                this.stellar.setTrueSelected();
            } else {
                this.stellar.setFalseSelected();
            }
        } catch (IllegalStateException ise) {
            // Value was never set
            this.stellar.setNASelected();
        }

        try {
            if (this.finding.getResolved()) {
                this.resolved.setTrueSelected();
            } else {
                this.resolved.setFalseSelected();
            }
        } catch (IllegalStateException ise) {
            // Value was never set
            this.resolved.setNASelected();
        }

        try {
            if (this.finding.getMottled()) {
                this.mottled.setTrueSelected();
            } else {
                this.mottled.setFalseSelected();
            }
        } catch (IllegalStateException ise) {
            // Value was never set
            this.mottled.setNASelected();
        }

        try {
            if (this.finding.getExtended()) {
                this.extended.setTrueSelected();
            } else {
                this.extended.setFalseSelected();
            }
        } catch (IllegalStateException ise) {
            // Value was never set
            this.extended.setNASelected();
        }

        if (!this.isEditable()) {
            this.stellar.setEditable(false);
            this.resolved.setEditable(false);
            this.mottled.setEditable(false);
            this.extended.setEditable(false);
        }

    }

    JComboBox<String> getVisualRatingBox() {

        JComboBox<String> box = new JComboBox<>();
        box.addItem("----");
        box.addItem(this.bundle.getString("panel.finding.dropdown.rating.1"));
        box.addItem(this.bundle.getString("panel.finding.dropdown.rating.2"));
        box.addItem(this.bundle.getString("panel.finding.dropdown.rating.3"));
        box.addItem(this.bundle.getString("panel.finding.dropdown.rating.4"));
        box.addItem(this.bundle.getString("panel.finding.dropdown.rating.5"));
        box.addItem(this.bundle.getString("panel.finding.dropdown.rating.6"));
        box.addItem(this.bundle.getString("panel.finding.dropdown.rating.7"));
        box.addItem(this.bundle.getString("panel.finding.dropdown.rating.99"));

        return box;

    }

}
