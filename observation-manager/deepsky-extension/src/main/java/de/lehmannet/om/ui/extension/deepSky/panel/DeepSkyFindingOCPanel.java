/*
 * ====================================================================
 * /extension/deepSky/panel/DeepSkyFindingOCPanel.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.deepSky.panel;

import de.lehmannet.om.IFinding;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ISession;
import de.lehmannet.om.extension.deepSky.DeepSkyFinding;
import de.lehmannet.om.extension.deepSky.DeepSkyFindingOC;
import de.lehmannet.om.ui.extension.deepSky.dialog.DeepSkyFindingOCTraitDialog;
import de.lehmannet.om.ui.navigation.observation.utils.InstallDir;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.IConfiguration;
import de.lehmannet.om.ui.util.OMLabel;
import de.lehmannet.om.ui.util.TristateCheckbox;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;

public class DeepSkyFindingOCPanel extends DeepSkyFindingPanel implements ActionListener {

    private static final long serialVersionUID = -304700875998209917L;

    private TristateCheckbox unusualShape = null;
    private TristateCheckbox partlyUnresolved = null;
    private TristateCheckbox colorContrasts = null;
    private Character character = null;
    private JButton characterButton = null;

    private final String imagePath;
    private final InstallDir installDir;

    public DeepSkyFindingOCPanel(
            IConfiguration configuration, InstallDir installDir, IFinding paramFinding, ISession s, Boolean editable)
            throws IllegalArgumentException {

        super(configuration, paramFinding, s, editable);

        this.installDir = installDir;
        if (paramFinding != null) {
            if (!(paramFinding instanceof DeepSkyFindingOC)) {
                // DeepSkyFindingPanel might already have converted the Finding into a
                // DeepSkyFinding
                paramFinding = this.finding;

                try { // Might be old (< 1.5) DeepSkyFinding
                    if (paramFinding != null) {
                        this.finding = new DeepSkyFindingOC(
                                paramFinding.getDescription(), ((DeepSkyFinding) paramFinding).getRating());
                    } else { // Finding was something else. So recycle description and use 99 as default
                        // rating
                        this.finding = new DeepSkyFindingOC("", 99);
                    }
                } catch (ClassCastException cce) {
                    throw new IllegalArgumentException(
                            "Passed IFinding must derive from de.lehmannet.om.extension.deepSky.DeepSkyFindingOC\n");
                }
            } else {
                this.finding = (DeepSkyFindingOC) paramFinding;
            }
        }
        this.imagePath = this.installDir.getPathForFolder("images") + "deepSky" + File.separatorChar + "clusterTypes"
                + File.separatorChar;

        this.createOCPanel();

        if (this.finding != null) {
            this.loadSchemaElementOC();
        }
    }

    // ------
    // JPanel -----------------------------------------------------------------
    // ------

    @Override
    public String getName() {

        return this.bundle.getString("panel.findingOC.name");
    }

    // -------------
    // IFindingPanel -----------------------------------------------------------
    // -------------

    @Override
    public String getXSIType() {

        String XSI_TYPE = "oal:findingsDeepSkyOCType";
        return XSI_TYPE;
    }

    // --------------
    // ActionListener ---------------------------------------------------------
    // --------------

    @Override
    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();
        if (source instanceof JButton) {
            if (source.equals(this.characterButton)) {
                DeepSkyFindingOCTraitDialog traitDialog =
                        new DeepSkyFindingOCTraitDialog(null, this.imagePath, this.character);
                this.character = traitDialog.getCharacter();
                if (this.character != null) {
                    Image image = Toolkit.getDefaultToolkit()
                            .getImage(this.imagePath + Character.toLowerCase(this.character) + ".png");
                    image = image.getScaledInstance(90, 90, Image.SCALE_FAST);
                    this.characterButton.setIcon(new ImageIcon(image));
                    this.characterButton.setText("   " + this.character + "   ");
                    this.characterButton.setToolTipText(this.bundle.getString("dialog.oc.trait.explanation."
                            + this.character.toString().toLowerCase()));
                } else {
                    this.characterButton.setText(this.bundle.getString("panel.findingOC.button.character"));
                    this.characterButton.setIcon(null);
                }
            }
        }
    }

    // -------------
    // AbstractPanel ----------------------------------------------------------
    // -------------

    @Override
    public ISchemaElement updateSchemaElement() {

        if (this.finding == null) {
            return null;
        }

        DeepSkyFindingOC finding = (DeepSkyFindingOC) super.updateSchemaElement();

        // Something went wrong here. Maybe given values are malformed
        if (finding == null) {
            return null;
        }

        // Set optional OC elements
        finding.setCharacter(this.character);

        if (this.unusualShape.isNASelected()) {
            if (this.unusualShape.isFalseSelected()) {
                finding.setUnusualShape(Boolean.FALSE);
            } else {
                finding.setUnusualShape(Boolean.TRUE);
            }
        } else {
            finding.setUnusualShape(null);
        }

        if (this.partlyUnresolved.isNASelected()) {
            if (this.partlyUnresolved.isFalseSelected()) {
                finding.setPartlyUnresolved(Boolean.FALSE);
            } else {
                finding.setPartlyUnresolved(Boolean.TRUE);
            }
        } else {
            finding.setPartlyUnresolved(null);
        }

        if (this.colorContrasts.isNASelected()) {
            if (this.colorContrasts.isFalseSelected()) {
                finding.setColorContrasts(Boolean.FALSE);
            } else {
                finding.setColorContrasts(Boolean.TRUE);
            }
        } else {
            finding.setColorContrasts(null);
        }

        this.finding = finding;

        return this.finding;
    }

    @Override
    public ISchemaElement createSchemaElement() {

        int rating = this.getRating();
        if (rating == -1) {
            this.createWarning(this.bundle.getString("panel.finding.warning.setRating"));
            return null;
        }

        this.finding = new DeepSkyFindingOC(this.findingContainer.getDescription(), rating);

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
    private void createOCPanel() {

        // this.createPanel(); <--- will be called via our constructor -> super ->
        // createPanel()

        GridBagLayout gridbag = (GridBagLayout) this.getLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        // this.setLayout(gridbag); <-- already set by super class

        ConstraintsBuilder.buildConstraints(constraints, 0, 7, 1, 1, 5, 10);
        OMLabel LunusualShape = new OMLabel(this.bundle.getString("panel.findingOC.label.unusualShape"), false);
        LunusualShape.setToolTipText(this.bundle.getString("panel.findingOC.tooltip.unusualShape"));
        gridbag.setConstraints(LunusualShape, constraints);
        this.add(LunusualShape);
        ConstraintsBuilder.buildConstraints(constraints, 1, 7, 2, 1, 45, 10);
        this.unusualShape = new TristateCheckbox();
        this.unusualShape.setToolTipText(this.bundle.getString("panel.findingOC.tooltip.unusualShape"));
        constraints.fill = GridBagConstraints.BOTH;
        gridbag.setConstraints(this.unusualShape, constraints);
        this.add(this.unusualShape);

        ConstraintsBuilder.buildConstraints(constraints, 0, 8, 1, 1, 5, 10);
        OMLabel LpartlyUnresolved = new OMLabel(this.bundle.getString("panel.findingOC.label.partlyUnresolved"), false);
        LpartlyUnresolved.setToolTipText(this.bundle.getString("panel.findingOC.tooltip.partlyUnresolved"));
        gridbag.setConstraints(LpartlyUnresolved, constraints);
        this.add(LpartlyUnresolved);
        ConstraintsBuilder.buildConstraints(constraints, 1, 8, 2, 1, 45, 10);
        this.partlyUnresolved = new TristateCheckbox();
        this.partlyUnresolved.setToolTipText(this.bundle.getString("panel.findingOC.tooltip.partlyUnresolved"));
        constraints.fill = GridBagConstraints.BOTH;
        gridbag.setConstraints(this.partlyUnresolved, constraints);
        this.add(this.partlyUnresolved);

        ConstraintsBuilder.buildConstraints(constraints, 0, 9, 1, 1, 5, 10);
        OMLabel LcolorContrasts = new OMLabel(this.bundle.getString("panel.findingOC.label.colorContrasts"), false);
        LcolorContrasts.setToolTipText(this.bundle.getString("panel.findingOC.tooltip.colorContrasts"));
        gridbag.setConstraints(LcolorContrasts, constraints);
        this.add(LcolorContrasts);
        ConstraintsBuilder.buildConstraints(constraints, 1, 9, 2, 1, 45, 10);
        this.colorContrasts = new TristateCheckbox();
        this.colorContrasts.setToolTipText(this.bundle.getString("panel.findingOC.tooltip.colorContrasts"));
        constraints.fill = GridBagConstraints.BOTH;
        gridbag.setConstraints(this.colorContrasts, constraints);
        this.add(this.colorContrasts);

        ConstraintsBuilder.buildConstraints(constraints, 3, 7, 1, 1, 5, 10);
        OMLabel Lcharacter = new OMLabel(this.bundle.getString("panel.findingOC.label.character"), false);
        Lcharacter.setToolTipText(this.bundle.getString("panel.findingOC.tooltip.character"));
        constraints.anchor = GridBagConstraints.SOUTHWEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(Lcharacter, constraints);
        this.add(Lcharacter);
        ConstraintsBuilder.buildConstraints(constraints, 3, 8, 1, 2, 45, 10);
        this.characterButton = new JButton();
        this.characterButton.addActionListener(this);
        if ((this.finding != null) && (((DeepSkyFindingOC) this.finding).getCharacter() != null)) {
            Character c = ((DeepSkyFindingOC) this.finding).getCharacter();
            Image image = Toolkit.getDefaultToolkit().getImage(this.imagePath + Character.toLowerCase(c) + ".png");
            image = image.getScaledInstance(90, 90, Image.SCALE_FAST);
            ImageIcon icon = new ImageIcon(image);
            this.characterButton.setIcon(icon);
            this.characterButton.setDisabledIcon(icon);
            this.characterButton.setText("   " + ((DeepSkyFindingOC) this.finding).getCharacter() + "   ");
            this.characterButton.setToolTipText(this.bundle.getString("dialog.oc.trait.explanation."
                    + ((DeepSkyFindingOC) this.finding)
                            .getCharacter()
                            .toString()
                            .toLowerCase()));
        } else {
            this.characterButton.setText(this.bundle.getString("panel.findingOC.button.character"));
            this.characterButton.setToolTipText(this.bundle.getString("panel.findingOC.tooltip.character"));
        }
        constraints.fill = GridBagConstraints.BOTH;
        gridbag.setConstraints(this.characterButton, constraints);
        this.add(this.characterButton);

        ConstraintsBuilder.buildConstraints(constraints, 0, 10, 4, 1, 50, 60);
        constraints.fill = GridBagConstraints.BOTH;
        JLabel Lfill = new JLabel("");
        gridbag.setConstraints(Lfill, constraints);
        this.add(Lfill);
    }

    private void loadSchemaElementOC() {

        this.loadSchemaElement();

        DeepSkyFindingOC findingOC = (DeepSkyFindingOC) this.finding;

        this.character = findingOC.getCharacter();
        this.characterButton.setEnabled(this.isEditable());

        try {
            if (findingOC.getUnusualShape()) {
                this.unusualShape.setTrueSelected();
            } else {
                this.unusualShape.setFalseSelected();
            }
        } catch (IllegalStateException ise) {
            // Value was never set
            this.unusualShape.setNASelected();
        }
        this.unusualShape.setEditable(this.isEditable());

        try {
            if (findingOC.getColorContrasts()) {
                this.colorContrasts.setTrueSelected();
            } else {
                this.colorContrasts.setFalseSelected();
            }
        } catch (IllegalStateException ise) {
            // Value was never set
            this.colorContrasts.setNASelected();
        }
        this.colorContrasts.setEditable(this.isEditable());

        try {
            if (findingOC.getPartlyUnresolved()) {
                this.partlyUnresolved.setTrueSelected();
            } else {
                this.partlyUnresolved.setFalseSelected();
            }
        } catch (IllegalStateException ise) {
            // Value was never set
            this.partlyUnresolved.setNASelected();
        }
        this.partlyUnresolved.setEditable(this.isEditable());
    }

    // -------------------
    // DeepSkyFindingPanel ------------------------------------------------------
    // -------------------

    // --------------------------------------------------------------------------

    @Override
    protected JComboBox<String> getVisualRatingBox() {

        JComboBox<String> box = new JComboBox<>();
        box.addItem("----");
        box.addItem(this.bundle.getString("panel.ocfinding.dropdown.rating.1"));
        box.addItem(this.bundle.getString("panel.ocfinding.dropdown.rating.2"));
        box.addItem(this.bundle.getString("panel.ocfinding.dropdown.rating.3"));
        box.addItem(this.bundle.getString("panel.ocfinding.dropdown.rating.4"));
        box.addItem(this.bundle.getString("panel.ocfinding.dropdown.rating.5"));
        box.addItem(this.bundle.getString("panel.ocfinding.dropdown.rating.6"));
        box.addItem(this.bundle.getString("panel.ocfinding.dropdown.rating.7"));
        box.addItem(this.bundle.getString("panel.finding.dropdown.rating.99"));

        return box;
    }
}
