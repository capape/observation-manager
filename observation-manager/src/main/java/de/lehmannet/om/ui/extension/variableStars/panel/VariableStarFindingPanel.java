/* ====================================================================
 * /extension/variableStars/panel/VariableStarFindingPanel.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.variableStars.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.swing.JCheckBox;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import de.lehmannet.om.IFinding;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ISession;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.extension.variableStars.FindingVariableStar;
import de.lehmannet.om.ui.container.FindingContainer;
import de.lehmannet.om.ui.extension.variableStars.VariableStarsPreferences;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.ui.panel.IFindingPanel;
import de.lehmannet.om.ui.util.Configuration;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.OMLabel;
import de.lehmannet.om.util.FloatUtil;

public class VariableStarFindingPanel extends AbstractPanel implements IFindingPanel {

    private static final long serialVersionUID = 4348851988579524093L;

    // Config keys
    private static final String CONFIG_LAST_CHARTDATE = "om.extension.variableStar.finding.lastChartDate";
    private static final String CONFIG_LAST_COMPARISM_STARS = "om.extension.variableStar.finding.lastComparismStars";
    private static final String CONFIG_LAST_NONAAVSOCHART = "om.extension.variableStar.finding.lastNonAAVSOChart";
    private static final String CONFIG_LAST_STAR = "om.extension.variableStar.finding.lastStar";

    private final static String DELIMITER_COMPARISM_STAR = ",";

    private final PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
            .getBundle("de.lehmannet.om.ui.extension.variableStars.VariableStar", Locale.getDefault());

    private ObservationManager om = null;

    private FindingVariableStar finding = null;
    private ISession session = null;
    private ITarget target = null;

    private FindingContainer findingContainer = null;

    private JCheckBox magnitudeFainterThan = null;
    private JTextField magnitude = null;
    private JTextField chartDate = null;
    private JTextField comparismStars = null;
    private JCheckBox brightSky = null;
    private JCheckBox clouds = null;
    private JCheckBox poorSeeing = null;
    private JCheckBox nearHorizion = null;
    private JCheckBox unusualActivity = null;
    private JCheckBox outburst = null;
    private JCheckBox nonAAVSOchart = null;
    private JCheckBox comparismSequenceProblem = null;
    private JCheckBox magnitudeUncertain = null;
    private JCheckBox starIdentificationUncertain = null;
    private JCheckBox faintStar = null;

    public VariableStarFindingPanel(ObservationManager om, IFinding finding, ISession s, ITarget t, Boolean editable)
            throws IllegalArgumentException {

        super(editable);

        if ((finding != null) && !(finding instanceof FindingVariableStar)) {
            // throw new IllegalArgumentException("Passed IFinding must derive from
            // de.lehmannet.om.extension.variableStars.FindingVariableStar\n");

            // Create minimalistic FindingVariableStar from given IFinding (must be a
            // IFinding instance due to parameter definition)
            List<String> dummy = new ArrayList<>();
            dummy.add("-999.99");
            FindingVariableStar fvs = new FindingVariableStar(-999.99f, dummy, "---");
            fvs.setDescription(finding.getDescription());
            fvs.setLanguage(finding.getLanguage());

            finding = fvs; // Update reference
        }
        this.finding = (FindingVariableStar) finding;
        this.om = om;
        this.session = s;
        this.target = t;

        this.createPanel();

        if (this.finding != null) {
            this.finding = (FindingVariableStar) finding;
            this.loadSchemaElement();
        } else {
            this.loadFromCache();
        }

    }

    // ------
    // JPanel -----------------------------------------------------------------
    // ------

    @Override
    public String getName() {

        return this.bundle.getString("panel.variableStarFinding.name");

    }

    // -------------
    // IFindingPanel -----------------------------------------------------------
    // -------------

    @Override
    public String getXSIType() {

        return FindingVariableStar.XML_XSI_TYPE_VALUE;

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

        // Set mandatory fields

        String mag = this.magnitude.getText();
        if ((mag != null) && !("".equals(mag.trim()))) {
            try {
                float magnitudeFloat = FloatUtil.parseFloat(mag);
                this.finding.setMagnitude(magnitudeFloat);
            } catch (NumberFormatException nfe) {
                super.createWarning(this.bundle.getString("panel.variableStarFinding.warning.magnitudeNotANumber"));
                return null;
            }
        } else {
            super.createWarning(this.bundle.getString("panel.variableStarFinding.warning.setMagnitude"));
            return null;
        }

        String chartD = this.chartDate.getText();
        if ((chartD != null) && !("".equals(chartD.trim()))) {
            this.finding.setChartDate(chartD);
        } else {
            super.createWarning(this.bundle.getString("panel.variableStarFinding.warning.setChartDate"));
            return null;
        }

        List<String> compStars = this.getComparismStars();
        if (compStars != null) {
            this.finding.setComparismStars(compStars);
        } else {
            super.createWarning(this.bundle.getString("panel.variableStarFinding.warning.setCompStars"));
            return null;
        }

        // Set optional elements
        if (this.findingContainer.getLanguage() != null) {
            this.finding.setLanguage(this.findingContainer.getLanguage());
        }

        this.finding.setDescription(this.findingContainer.getDescription());

        if (this.magnitudeFainterThan.isSelected()) {
            this.finding.setMagnitudeFainterThan(true);
        }

        if (this.brightSky.isSelected()) {
            this.finding.setBrightSky(true);
        }

        if (this.clouds.isSelected()) {
            this.finding.setClouds(true);
        }

        if (this.poorSeeing.isSelected()) {
            this.finding.setPoorSeeing(true);
        }

        if (this.nearHorizion.isSelected()) {
            this.finding.setNearHorizion(true);
        }

        if (this.unusualActivity.isSelected()) {
            this.finding.setUnusualActivity(true);
        }

        if (this.outburst.isSelected()) {
            this.finding.setOutburst(true);
        }

        if (this.nonAAVSOchart.isSelected()) {
            this.finding.setNonAAVSOchart(true);
        }

        if (this.comparismSequenceProblem.isSelected()) {
            this.finding.setComparismSequenceProblem(true);
        }

        if (this.magnitudeUncertain.isSelected()) {
            this.finding.setMagnitudeUncertain(true);
        }

        if (this.starIdentificationUncertain.isSelected()) {
            this.finding.setStarIdentificationUncertain(true);
        }

        if (this.faintStar.isSelected()) {
            this.finding.setFaintStar(true);
        }

        this.writeToCache();

        return this.finding;

    }

    @Override
    public ISchemaElement createSchemaElement() {

        // Get all required field for finding creation

        String mag = this.magnitude.getText();
        float magnitudeFloat = Float.NaN;
        if ((mag != null) && !("".equals(mag.trim()))) {
            try {
                magnitudeFloat = FloatUtil.parseFloat(mag);
            } catch (NumberFormatException nfe) {
                super.createWarning(this.bundle.getString("panel.variableStarFinding.warning.magnitudeNotANumber"));
                return null;
            }
        } else {
            super.createWarning(this.bundle.getString("panel.variableStarFinding.warning.setMagnitude"));
            return null;
        }

        String chartD = this.chartDate.getText();
        if ((chartD == null) || ("".equals(chartD.trim()))) {
            super.createWarning(this.bundle.getString("panel.variableStarFinding.warning.setChartDate"));
            return null;
        }

        List<String> compStars = this.getComparismStars();
        if (compStars == null) {
            super.createWarning(this.bundle.getString("panel.variableStarFinding.warning.setCompStars"));
            return null;
        }

        // Create finding
        this.finding = new FindingVariableStar(magnitudeFloat, compStars, chartD);

        // Set all other fields
        IFinding f = (IFinding) this.updateSchemaElement();
        if (f == null) {
            return null;
        } else {
            this.finding = (FindingVariableStar) f;
        }

        return this.finding;

    }

    private void createPanel() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        this.setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 1, 1, 1, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        OMLabel LchartDate = new OMLabel(this.bundle.getString("panel.variableStarFinding.label.chartDate"),
                SwingConstants.LEFT, true);
        LchartDate.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.chartDate"));
        gridbag.setConstraints(LchartDate, constraints);
        this.add(LchartDate);
        ConstraintsBuilder.buildConstraints(constraints, 1, 0, 1, 1, 5, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        this.chartDate = new JTextField();
        this.chartDate.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.chartDate"));
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(this.chartDate, constraints);
        this.add(this.chartDate);

        ConstraintsBuilder.buildConstraints(constraints, 2, 0, 1, 1, 1, 1);
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.EAST;
        this.nonAAVSOchart = new JCheckBox();
        this.nonAAVSOchart.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.nonAAVSOchart"));
        gridbag.setConstraints(this.nonAAVSOchart, constraints);
        this.add(this.nonAAVSOchart);

        ConstraintsBuilder.buildConstraints(constraints, 3, 0, 1, 1, 3, 1);
        constraints.anchor = GridBagConstraints.WEST;
        OMLabel LnonAAVSOchart = new OMLabel(this.bundle.getString("panel.variableStarFinding.label.nonAAVSOchart"),
                SwingConstants.LEFT, false);
        LnonAAVSOchart.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.nonAAVSOchart"));
        gridbag.setConstraints(LnonAAVSOchart, constraints);
        this.add(LnonAAVSOchart);

        ConstraintsBuilder.buildConstraints(constraints, 4, 0, 1, 1, 1, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.EAST;
        OMLabel Lmagnitude = new OMLabel(this.bundle.getString("panel.variableStarFinding.label.magnitude"),
                SwingConstants.RIGHT, true);
        Lmagnitude.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.magnitude"));
        gridbag.setConstraints(Lmagnitude, constraints);
        this.add(Lmagnitude);
        ConstraintsBuilder.buildConstraints(constraints, 5, 0, 1, 1, 5, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        this.magnitude = new JTextField();
        this.magnitude.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.magnitude"));
        gridbag.setConstraints(this.magnitude, constraints);
        this.add(this.magnitude);

        ConstraintsBuilder.buildConstraints(constraints, 6, 0, 1, 1, 3, 1);
        constraints.anchor = GridBagConstraints.EAST;
        constraints.fill = GridBagConstraints.NONE;
        this.magnitudeFainterThan = new JCheckBox();
        this.magnitudeFainterThan
                .setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.magnitudeFainterThan"));
        gridbag.setConstraints(this.magnitudeFainterThan, constraints);
        this.add(this.magnitudeFainterThan);

        ConstraintsBuilder.buildConstraints(constraints, 7, 0, 1, 1, 3, 1);
        constraints.anchor = GridBagConstraints.WEST;
        OMLabel LmagnitudeFainterThan = new OMLabel(
                this.bundle.getString("panel.variableStarFinding.label.magnitudeFainterThan"), SwingConstants.LEFT,
                false);
        LmagnitudeFainterThan
                .setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.magnitudeFainterThan"));
        gridbag.setConstraints(LmagnitudeFainterThan, constraints);
        this.add(LmagnitudeFainterThan);

        ConstraintsBuilder.buildConstraints(constraints, 8, 0, 1, 1, 1, 1);
        constraints.anchor = GridBagConstraints.EAST;
        this.magnitudeUncertain = new JCheckBox();
        this.magnitudeUncertain.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.magUncertain"));
        gridbag.setConstraints(this.magnitudeUncertain, constraints);
        this.add(this.magnitudeUncertain);

        ConstraintsBuilder.buildConstraints(constraints, 9, 0, 1, 1, 3, 1);
        constraints.anchor = GridBagConstraints.WEST;
        OMLabel LmagnitudeUncertain = new OMLabel(this.bundle.getString("panel.variableStarFinding.label.magUncertain"),
                SwingConstants.LEFT, false);
        LmagnitudeUncertain.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.magUncertain"));
        gridbag.setConstraints(LmagnitudeUncertain, constraints);
        this.add(LmagnitudeUncertain);

        ConstraintsBuilder.buildConstraints(constraints, 4, 1, 1, 1, 1, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.EAST;
        OMLabel LcompStars = new OMLabel(this.bundle.getString("panel.variableStarFinding.label.compStars"),
                SwingConstants.RIGHT, true);
        LcompStars.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.compStars"));
        gridbag.setConstraints(LcompStars, constraints);
        this.add(LcompStars);
        ConstraintsBuilder.buildConstraints(constraints, 5, 1, 1, 1, 5, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        this.comparismStars = new JTextField();
        this.comparismStars.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.compStars"));
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(this.comparismStars, constraints);
        this.add(this.comparismStars);

        ConstraintsBuilder.buildConstraints(constraints, 6, 1, 1, 1, 1, 1);
        constraints.anchor = GridBagConstraints.EAST;
        this.comparismSequenceProblem = new JCheckBox();
        this.comparismSequenceProblem
                .setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.comparismSeqProblem"));
        constraints.fill = GridBagConstraints.NONE;
        gridbag.setConstraints(this.comparismSequenceProblem, constraints);
        this.add(this.comparismSequenceProblem);

        ConstraintsBuilder.buildConstraints(constraints, 7, 1, 1, 1, 3, 1);
        constraints.anchor = GridBagConstraints.WEST;
        OMLabel LcomparismSequenceProblem = new OMLabel(
                this.bundle.getString("panel.variableStarFinding.label.comparismSeqProblem"), SwingConstants.LEFT,
                false);
        LcomparismSequenceProblem
                .setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.comparismSeqProblem"));
        gridbag.setConstraints(LcomparismSequenceProblem, constraints);
        this.add(LcomparismSequenceProblem);

        ConstraintsBuilder.buildConstraints(constraints, 0, 2, 1, 1, 1, 1);
        constraints.anchor = GridBagConstraints.EAST;
        this.brightSky = new JCheckBox();
        this.brightSky.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.brightSky"));
        gridbag.setConstraints(this.brightSky, constraints);
        this.add(this.brightSky);

        ConstraintsBuilder.buildConstraints(constraints, 1, 2, 1, 1, 3, 1);
        constraints.anchor = GridBagConstraints.WEST;
        OMLabel LbrightSky = new OMLabel(this.bundle.getString("panel.variableStarFinding.label.brightSky"),
                SwingConstants.LEFT, false);
        LbrightSky.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.brightSky"));
        gridbag.setConstraints(LbrightSky, constraints);
        this.add(LbrightSky);

        ConstraintsBuilder.buildConstraints(constraints, 2, 2, 1, 1, 1, 1);
        constraints.anchor = GridBagConstraints.EAST;
        this.clouds = new JCheckBox();
        this.clouds.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.clouds"));
        gridbag.setConstraints(this.clouds, constraints);
        this.add(this.clouds);

        ConstraintsBuilder.buildConstraints(constraints, 3, 2, 1, 1, 3, 1);
        constraints.anchor = GridBagConstraints.WEST;
        OMLabel Lclouds = new OMLabel(this.bundle.getString("panel.variableStarFinding.label.clouds"),
                SwingConstants.LEFT, false);
        Lclouds.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.clouds"));
        gridbag.setConstraints(Lclouds, constraints);
        this.add(Lclouds);

        ConstraintsBuilder.buildConstraints(constraints, 4, 2, 1, 1, 1, 1);
        constraints.anchor = GridBagConstraints.EAST;
        this.poorSeeing = new JCheckBox();
        this.poorSeeing.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.poorSeeing"));
        gridbag.setConstraints(this.poorSeeing, constraints);
        this.add(this.poorSeeing);

        ConstraintsBuilder.buildConstraints(constraints, 5, 2, 1, 1, 3, 1);
        constraints.anchor = GridBagConstraints.WEST;
        OMLabel LpoorSeeing = new OMLabel(this.bundle.getString("panel.variableStarFinding.label.poorSeeing"),
                SwingConstants.LEFT, false);
        LpoorSeeing.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.poorSeeing"));
        gridbag.setConstraints(LpoorSeeing, constraints);
        this.add(LpoorSeeing);

        ConstraintsBuilder.buildConstraints(constraints, 6, 2, 1, 1, 1, 1);
        constraints.anchor = GridBagConstraints.EAST;
        this.nearHorizion = new JCheckBox();
        this.nearHorizion.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.nearHorizon"));
        gridbag.setConstraints(this.nearHorizion, constraints);
        this.add(this.nearHorizion);

        ConstraintsBuilder.buildConstraints(constraints, 7, 2, 1, 1, 3, 1);
        constraints.anchor = GridBagConstraints.WEST;
        OMLabel LnearHorizion = new OMLabel(this.bundle.getString("panel.variableStarFinding.label.nearHorizon"),
                SwingConstants.LEFT, false);
        LnearHorizion.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.nearHorizon"));
        gridbag.setConstraints(LnearHorizion, constraints);
        this.add(LnearHorizion);

        ConstraintsBuilder.buildConstraints(constraints, 0, 3, 1, 1, 1, 1);
        constraints.anchor = GridBagConstraints.EAST;
        this.unusualActivity = new JCheckBox();
        this.unusualActivity.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.unusualActivity"));
        gridbag.setConstraints(this.unusualActivity, constraints);
        this.add(this.unusualActivity);

        ConstraintsBuilder.buildConstraints(constraints, 1, 3, 1, 1, 3, 1);
        constraints.anchor = GridBagConstraints.WEST;
        OMLabel LunusualActivity = new OMLabel(this.bundle.getString("panel.variableStarFinding.label.unusualActivity"),
                SwingConstants.LEFT, false);
        LunusualActivity.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.unusualActivity"));
        gridbag.setConstraints(LunusualActivity, constraints);
        this.add(LunusualActivity);

        ConstraintsBuilder.buildConstraints(constraints, 2, 3, 1, 1, 1, 1);
        constraints.anchor = GridBagConstraints.EAST;
        this.outburst = new JCheckBox();
        this.outburst.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.outburst"));
        gridbag.setConstraints(this.outburst, constraints);
        this.add(this.outburst);

        ConstraintsBuilder.buildConstraints(constraints, 3, 3, 1, 1, 3, 1);
        constraints.anchor = GridBagConstraints.WEST;
        OMLabel Loutburst = new OMLabel(this.bundle.getString("panel.variableStarFinding.label.outburst"),
                SwingConstants.LEFT, false);
        Loutburst.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.outburst"));
        gridbag.setConstraints(Loutburst, constraints);
        this.add(Loutburst);

        ConstraintsBuilder.buildConstraints(constraints, 4, 3, 1, 1, 1, 1);
        constraints.anchor = GridBagConstraints.EAST;
        this.starIdentificationUncertain = new JCheckBox();
        this.starIdentificationUncertain
                .setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.starIdentificationUncertain"));
        gridbag.setConstraints(this.starIdentificationUncertain, constraints);
        this.add(this.starIdentificationUncertain);

        ConstraintsBuilder.buildConstraints(constraints, 5, 3, 1, 1, 3, 1);
        constraints.anchor = GridBagConstraints.WEST;
        OMLabel LstarIdentificationUncertain = new OMLabel(
                this.bundle.getString("panel.variableStarFinding.label.starIdentificationUncertain"),
                SwingConstants.LEFT, false);
        LstarIdentificationUncertain
                .setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.starIdentificationUncertain"));
        gridbag.setConstraints(LstarIdentificationUncertain, constraints);
        this.add(LstarIdentificationUncertain);

        ConstraintsBuilder.buildConstraints(constraints, 6, 3, 1, 1, 1, 1);
        constraints.anchor = GridBagConstraints.EAST;
        this.faintStar = new JCheckBox();
        this.faintStar.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.faintStar"));
        gridbag.setConstraints(this.faintStar, constraints);
        this.add(this.faintStar);

        ConstraintsBuilder.buildConstraints(constraints, 7, 3, 1, 1, 3, 1);
        constraints.anchor = GridBagConstraints.WEST;
        OMLabel LfaintStar = new OMLabel(this.bundle.getString("panel.variableStarFinding.label.faintStar"),
                SwingConstants.LEFT, false);
        LfaintStar.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.faintStar"));
        gridbag.setConstraints(LfaintStar, constraints);
        this.add(LfaintStar);

        ConstraintsBuilder.buildConstraints(constraints, 0, 6, 10, 1, 18, 1);
        constraints.fill = GridBagConstraints.BOTH;
        JSeparator seperator = new JSeparator();
        gridbag.setConstraints(seperator, constraints);
        this.add(seperator);

        ConstraintsBuilder.buildConstraints(constraints, 0, 7, 10, 1, 18, 87);
        constraints.fill = GridBagConstraints.BOTH;
        this.findingContainer = new FindingContainer(this.om, this.finding, this.session, super.isEditable());
        gridbag.setConstraints(this.findingContainer, constraints);
        this.add(this.findingContainer);

        /*
         * ConstraintsBuilder.buildConstraints(constraints, 0, 6, 6, 1, 18, 13); constraints.fill =
         * GridBagConstraints.BOTH; JLabel Lfill = new JLabel(""); gridbag.setConstraints(Lfill, constraints);
         * this.add(Lfill);
         */

    }

    private void loadSchemaElement() {

        this.magnitude.setText("" + this.finding.getMagnitude());
        this.magnitude.setEditable(super.isEditable());

        this.chartDate.setText(this.finding.getChartDate());
        this.chartDate.setEditable(super.isEditable());

        List<String> compStars = this.finding.getComparismStars();
        ListIterator<String> iterator = compStars.listIterator();
        StringBuilder compStarText = new StringBuilder();
        while (iterator.hasNext()) {
            compStarText.append(iterator.next());
            if (iterator.hasNext()) {
                compStarText.append(", ");
            }
        }
        this.comparismStars.setText(compStarText.toString());
        this.comparismStars.setEditable(super.isEditable());

        this.magnitudeFainterThan.setSelected(this.finding.isMagnitudeFainterThan());
        this.magnitudeFainterThan.setEnabled(super.isEditable());

        this.brightSky.setSelected(this.finding.isBrightSky());
        this.brightSky.setEnabled(super.isEditable());

        this.clouds.setSelected(this.finding.isClouds());
        this.clouds.setEnabled(super.isEditable());

        this.poorSeeing.setSelected(this.finding.isPoorSeeing());
        this.poorSeeing.setEnabled(super.isEditable());

        this.nearHorizion.setSelected(this.finding.isNearHorizion());
        this.nearHorizion.setEnabled(super.isEditable());

        this.unusualActivity.setSelected(this.finding.isUnusualActivity());
        this.unusualActivity.setEnabled(super.isEditable());

        this.outburst.setSelected(this.finding.isOutburst());
        this.outburst.setEnabled(super.isEditable());

        this.nonAAVSOchart.setSelected(this.finding.isNonAAVSOchart());
        this.nonAAVSOchart.setEnabled(super.isEditable());

        this.comparismSequenceProblem.setSelected(this.finding.isComparismSequenceProblem());
        this.comparismSequenceProblem.setEnabled(super.isEditable());

        this.magnitudeUncertain.setSelected(this.finding.isMagnitudeUncertain());
        this.magnitudeUncertain.setEnabled(super.isEditable());

        this.starIdentificationUncertain.setSelected(this.finding.isStarIdentificationUncertain());
        this.starIdentificationUncertain.setEnabled(super.isEditable());

        this.faintStar.setSelected(this.finding.isFaintStar());
        this.faintStar.setEnabled(super.isEditable());

    }

    private List<String> getComparismStars() {

        String compStarText = this.comparismStars.getText();
        if ((compStarText == null) || ("".equals(compStarText.trim()))) {
            return null;
        }

        List<String> result = new ArrayList<>(4);
        StringTokenizer tokenizer = new StringTokenizer(compStarText,
                VariableStarFindingPanel.DELIMITER_COMPARISM_STAR);
        while (tokenizer.hasMoreTokens()) {
            result.add(tokenizer.nextToken().trim());
        }

        return result;

    }

    private void writeToCache() {

        if ((super.isEditable()) && (this.finding != null) && (Boolean
                .parseBoolean(this.om.getConfiguration().getConfig(VariableStarsPreferences.CONFIG_CACHE_ENABLED)))) {
            Configuration config = this.om.getConfiguration();
            config.setConfig(VariableStarFindingPanel.CONFIG_LAST_CHARTDATE, this.finding.getChartDate());
            config.setConfig(VariableStarFindingPanel.CONFIG_LAST_COMPARISM_STARS, this.comparismStars.getText()); // Use
                                                                                                                   // JTextField
                                                                                                                   // here
                                                                                                                   // so
                                                                                                                   // that
                                                                                                                   // we
                                                                                                                   // don't
                                                                                                                   // need
                                                                                                                   // to
                                                                                                                   // build
                                                                                                                   // up
                                                                                                                   // string
                                                                                                                   // again
            if (this.nonAAVSOchart.isSelected()) {
                config.setConfig(VariableStarFindingPanel.CONFIG_LAST_NONAAVSOCHART,
                        Boolean.toString(this.finding.isNonAAVSOchart()));
            } else {
                config.setConfig(VariableStarFindingPanel.CONFIG_LAST_NONAAVSOCHART, null);
            }
            if (this.target != null) {
                config.setConfig(VariableStarFindingPanel.CONFIG_LAST_STAR, this.target.getName());
            }
        }

    }

    private void loadFromCache() {

        String targetName = "";
        if (this.target != null) {
            targetName = this.target.getName().toLowerCase();
        }

        if ((super.isEditable())
                && (Boolean.parseBoolean(
                        this.om.getConfiguration().getConfig(VariableStarsPreferences.CONFIG_CACHE_ENABLED)))
                && (targetName.equals(this.om.getConfiguration()
                        .getConfig(VariableStarFindingPanel.CONFIG_LAST_STAR, "").toLowerCase()))) {
            Configuration config = this.om.getConfiguration();

            String lastChartDate = config.getConfig(VariableStarFindingPanel.CONFIG_LAST_CHARTDATE);
            if ((lastChartDate != null) && !("".equals(lastChartDate.trim()))) {
                this.chartDate.setText(lastChartDate);
            }

            String lastCompStars = config.getConfig(VariableStarFindingPanel.CONFIG_LAST_COMPARISM_STARS);
            if ((lastCompStars != null) && !("".equals(lastCompStars.trim()))) {
                this.comparismStars.setText(lastCompStars);
            }

            String lastNonAAVSOChart = config.getConfig(VariableStarFindingPanel.CONFIG_LAST_NONAAVSOCHART);
            if ((lastNonAAVSOChart != null) && !("".equals(lastNonAAVSOChart.trim()))) {
                this.nonAAVSOchart.setSelected(Boolean.parseBoolean(lastNonAAVSOChart));
            }
        }

    }

}
