/*
 * ====================================================================
 * /extension/variableStars/panel/VariableStarFindingPanel.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.variableStars.panel;

import static de.lehmannet.om.ICloneable.copyOrNull;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.swing.JCheckBox;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.apache.commons.lang3.StringUtils;

import de.lehmannet.om.IFinding;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ISession;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.extension.variableStars.FindingVariableStar;
import de.lehmannet.om.ui.container.FindingContainer;
import de.lehmannet.om.ui.extension.variableStars.VariableStarsConfigKey;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.ui.panel.IFindingPanel;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.IConfiguration;
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

    private final ResourceBundle bundle = ResourceBundle
            .getBundle("de.lehmannet.om.ui.extension.variableStars.VariableStar", Locale.getDefault());

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

    private final IConfiguration configuration;

    public VariableStarFindingPanel(IConfiguration configuration, FindingVariableStar finding, ISession s, ITarget t,
            Boolean editable) throws IllegalArgumentException {

        super(editable);
        this.configuration = configuration;

        this.finding = copyOrNull(finding);
        this.session = copyOrNull(s);
        this.target = copyOrNull(t);

        this.createPanel();

        if (this.finding != null) {
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

        return copyOrNull(this.finding);

    }

    @Override
    public ISchemaElement updateSchemaElement() {

        if (this.finding == null) {
            return null;
        }

        // Set mandatory fields

        String mag = this.magnitude.getText();
        if (StringUtils.isBlank(mag)) {
            this.createWarning(this.bundle.getString("panel.variableStarFinding.warning.setMagnitude"));
            return null;
        }

        try {
            float magnitudeFloat = FloatUtil.parseFloat(mag);
            this.finding.setMagnitude(magnitudeFloat);
        } catch (NumberFormatException nfe) {
            this.createWarning(this.bundle.getString("panel.variableStarFinding.warning.magnitudeNotANumber"));
            return null;
        }

        String chartD = this.chartDate.getText();
        if (StringUtils.isBlank(chartD)) {
            this.createWarning(this.bundle.getString("panel.variableStarFinding.warning.setChartDate"));
            return null;
        }
        this.finding.setChartDate(chartD);

        List<String> compStars = this.getComparismStars();
        if (compStars == null) {
            this.createWarning(this.bundle.getString("panel.variableStarFinding.warning.setCompStars"));
            return null;
        }
        this.finding.setComparismStars(compStars);

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

        return this.finding.copy();

    }

    @Override
    public ISchemaElement createSchemaElement() {

        // Get all required field for finding creation

        String mag = this.magnitude.getText();
        float magnitudeFloat = Float.NaN;
        if (!StringUtils.isBlank(mag)) {
            try {
                magnitudeFloat = FloatUtil.parseFloat(mag);
            } catch (NumberFormatException nfe) {
                this.createWarning(this.bundle.getString("panel.variableStarFinding.warning.magnitudeNotANumber"));
                return null;
            }
        } else {
            this.createWarning(this.bundle.getString("panel.variableStarFinding.warning.setMagnitude"));
            return null;
        }

        String chartD = this.chartDate.getText();
        if (StringUtils.isBlank(chartD)) {
            this.createWarning(this.bundle.getString("panel.variableStarFinding.warning.setChartDate"));
            return null;
        }

        List<String> compStars = this.getComparismStars();
        if (compStars == null) {
            this.createWarning(this.bundle.getString("panel.variableStarFinding.warning.setCompStars"));
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

        return this.finding.copy();

    }

    private void createPanel() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        this.setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 1, 1, 1, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        OMLabel lChartDate = new OMLabel(this.bundle.getString("panel.variableStarFinding.label.chartDate"),
                SwingConstants.LEFT, true);
        lChartDate.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.chartDate"));
        gridbag.setConstraints(lChartDate, constraints);
        this.add(lChartDate);
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
        OMLabel lNonAAVSOchart = new OMLabel(this.bundle.getString("panel.variableStarFinding.label.nonAAVSOchart"),
                SwingConstants.LEFT, false);
        lNonAAVSOchart.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.nonAAVSOchart"));
        gridbag.setConstraints(lNonAAVSOchart, constraints);
        this.add(lNonAAVSOchart);

        ConstraintsBuilder.buildConstraints(constraints, 4, 0, 1, 1, 1, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.EAST;
        OMLabel lMagnitude = new OMLabel(this.bundle.getString("panel.variableStarFinding.label.magnitude"),
                SwingConstants.RIGHT, true);
        lMagnitude.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.magnitude"));
        gridbag.setConstraints(lMagnitude, constraints);
        this.add(lMagnitude);
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
        OMLabel lMagnitudeFainterThan = new OMLabel(
                this.bundle.getString("panel.variableStarFinding.label.magnitudeFainterThan"), SwingConstants.LEFT,
                false);
        lMagnitudeFainterThan
                .setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.magnitudeFainterThan"));
        gridbag.setConstraints(lMagnitudeFainterThan, constraints);
        this.add(lMagnitudeFainterThan);

        ConstraintsBuilder.buildConstraints(constraints, 8, 0, 1, 1, 1, 1);
        constraints.anchor = GridBagConstraints.EAST;
        this.magnitudeUncertain = new JCheckBox();
        this.magnitudeUncertain.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.magUncertain"));
        gridbag.setConstraints(this.magnitudeUncertain, constraints);
        this.add(this.magnitudeUncertain);

        ConstraintsBuilder.buildConstraints(constraints, 9, 0, 1, 1, 3, 1);
        constraints.anchor = GridBagConstraints.WEST;
        OMLabel lMagnitudeUncertain = new OMLabel(this.bundle.getString("panel.variableStarFinding.label.magUncertain"),
                SwingConstants.LEFT, false);
        lMagnitudeUncertain.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.magUncertain"));
        gridbag.setConstraints(lMagnitudeUncertain, constraints);
        this.add(lMagnitudeUncertain);

        ConstraintsBuilder.buildConstraints(constraints, 4, 1, 1, 1, 1, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.EAST;
        OMLabel lCompStars = new OMLabel(this.bundle.getString("panel.variableStarFinding.label.compStars"),
                SwingConstants.RIGHT, true);
        lCompStars.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.compStars"));
        gridbag.setConstraints(lCompStars, constraints);
        this.add(lCompStars);
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
        OMLabel lComparismSequenceProblem = new OMLabel(
                this.bundle.getString("panel.variableStarFinding.label.comparismSeqProblem"), SwingConstants.LEFT,
                false);
        lComparismSequenceProblem
                .setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.comparismSeqProblem"));
        gridbag.setConstraints(lComparismSequenceProblem, constraints);
        this.add(lComparismSequenceProblem);

        ConstraintsBuilder.buildConstraints(constraints, 0, 2, 1, 1, 1, 1);
        constraints.anchor = GridBagConstraints.EAST;
        this.brightSky = new JCheckBox();
        this.brightSky.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.brightSky"));
        gridbag.setConstraints(this.brightSky, constraints);
        this.add(this.brightSky);

        ConstraintsBuilder.buildConstraints(constraints, 1, 2, 1, 1, 3, 1);
        constraints.anchor = GridBagConstraints.WEST;
        OMLabel lBrightSky = new OMLabel(this.bundle.getString("panel.variableStarFinding.label.brightSky"),
                SwingConstants.LEFT, false);
        lBrightSky.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.brightSky"));
        gridbag.setConstraints(lBrightSky, constraints);
        this.add(lBrightSky);

        ConstraintsBuilder.buildConstraints(constraints, 2, 2, 1, 1, 1, 1);
        constraints.anchor = GridBagConstraints.EAST;
        this.clouds = new JCheckBox();
        this.clouds.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.clouds"));
        gridbag.setConstraints(this.clouds, constraints);
        this.add(this.clouds);

        ConstraintsBuilder.buildConstraints(constraints, 3, 2, 1, 1, 3, 1);
        constraints.anchor = GridBagConstraints.WEST;
        OMLabel lClouds = new OMLabel(this.bundle.getString("panel.variableStarFinding.label.clouds"),
                SwingConstants.LEFT, false);
        lClouds.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.clouds"));
        gridbag.setConstraints(lClouds, constraints);
        this.add(lClouds);

        ConstraintsBuilder.buildConstraints(constraints, 4, 2, 1, 1, 1, 1);
        constraints.anchor = GridBagConstraints.EAST;
        this.poorSeeing = new JCheckBox();
        this.poorSeeing.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.poorSeeing"));
        gridbag.setConstraints(this.poorSeeing, constraints);
        this.add(this.poorSeeing);

        ConstraintsBuilder.buildConstraints(constraints, 5, 2, 1, 1, 3, 1);
        constraints.anchor = GridBagConstraints.WEST;
        OMLabel lPoorSeeing = new OMLabel(this.bundle.getString("panel.variableStarFinding.label.poorSeeing"),
                SwingConstants.LEFT, false);
        lPoorSeeing.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.poorSeeing"));
        gridbag.setConstraints(lPoorSeeing, constraints);
        this.add(lPoorSeeing);

        ConstraintsBuilder.buildConstraints(constraints, 6, 2, 1, 1, 1, 1);
        constraints.anchor = GridBagConstraints.EAST;
        this.nearHorizion = new JCheckBox();
        this.nearHorizion.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.nearHorizon"));
        gridbag.setConstraints(this.nearHorizion, constraints);
        this.add(this.nearHorizion);

        ConstraintsBuilder.buildConstraints(constraints, 7, 2, 1, 1, 3, 1);
        constraints.anchor = GridBagConstraints.WEST;
        OMLabel lNearHorizion = new OMLabel(this.bundle.getString("panel.variableStarFinding.label.nearHorizon"),
                SwingConstants.LEFT, false);
        lNearHorizion.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.nearHorizon"));
        gridbag.setConstraints(lNearHorizion, constraints);
        this.add(lNearHorizion);

        ConstraintsBuilder.buildConstraints(constraints, 0, 3, 1, 1, 1, 1);
        constraints.anchor = GridBagConstraints.EAST;
        this.unusualActivity = new JCheckBox();
        this.unusualActivity.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.unusualActivity"));
        gridbag.setConstraints(this.unusualActivity, constraints);
        this.add(this.unusualActivity);

        ConstraintsBuilder.buildConstraints(constraints, 1, 3, 1, 1, 3, 1);
        constraints.anchor = GridBagConstraints.WEST;
        OMLabel lUnusualActivity = new OMLabel(this.bundle.getString("panel.variableStarFinding.label.unusualActivity"),
                SwingConstants.LEFT, false);
        lUnusualActivity.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.unusualActivity"));
        gridbag.setConstraints(lUnusualActivity, constraints);
        this.add(lUnusualActivity);

        ConstraintsBuilder.buildConstraints(constraints, 2, 3, 1, 1, 1, 1);
        constraints.anchor = GridBagConstraints.EAST;
        this.outburst = new JCheckBox();
        this.outburst.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.outburst"));
        gridbag.setConstraints(this.outburst, constraints);
        this.add(this.outburst);

        ConstraintsBuilder.buildConstraints(constraints, 3, 3, 1, 1, 3, 1);
        constraints.anchor = GridBagConstraints.WEST;
        OMLabel lOutburst = new OMLabel(this.bundle.getString("panel.variableStarFinding.label.outburst"),
                SwingConstants.LEFT, false);
        lOutburst.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.outburst"));
        gridbag.setConstraints(lOutburst, constraints);
        this.add(lOutburst);

        ConstraintsBuilder.buildConstraints(constraints, 4, 3, 1, 1, 1, 1);
        constraints.anchor = GridBagConstraints.EAST;
        this.starIdentificationUncertain = new JCheckBox();
        this.starIdentificationUncertain
                .setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.starIdentificationUncertain"));
        gridbag.setConstraints(this.starIdentificationUncertain, constraints);
        this.add(this.starIdentificationUncertain);

        ConstraintsBuilder.buildConstraints(constraints, 5, 3, 1, 1, 3, 1);
        constraints.anchor = GridBagConstraints.WEST;
        OMLabel lStarIdentificationUncertain = new OMLabel(
                this.bundle.getString("panel.variableStarFinding.label.starIdentificationUncertain"),
                SwingConstants.LEFT, false);
        lStarIdentificationUncertain
                .setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.starIdentificationUncertain"));
        gridbag.setConstraints(lStarIdentificationUncertain, constraints);
        this.add(lStarIdentificationUncertain);

        ConstraintsBuilder.buildConstraints(constraints, 6, 3, 1, 1, 1, 1);
        constraints.anchor = GridBagConstraints.EAST;
        this.faintStar = new JCheckBox();
        this.faintStar.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.faintStar"));
        gridbag.setConstraints(this.faintStar, constraints);
        this.add(this.faintStar);

        ConstraintsBuilder.buildConstraints(constraints, 7, 3, 1, 1, 3, 1);
        constraints.anchor = GridBagConstraints.WEST;
        OMLabel lFaintStar = new OMLabel(this.bundle.getString("panel.variableStarFinding.label.faintStar"),
                SwingConstants.LEFT, false);
        lFaintStar.setToolTipText(this.bundle.getString("panel.variableStarFinding.tooltip.faintStar"));
        gridbag.setConstraints(lFaintStar, constraints);
        this.add(lFaintStar);

        ConstraintsBuilder.buildConstraints(constraints, 0, 6, 10, 1, 18, 1);
        constraints.fill = GridBagConstraints.BOTH;
        JSeparator seperator = new JSeparator();
        gridbag.setConstraints(seperator, constraints);
        this.add(seperator);

        ConstraintsBuilder.buildConstraints(constraints, 0, 7, 10, 1, 18, 87);
        constraints.fill = GridBagConstraints.BOTH;
        this.findingContainer = new FindingContainer(this.configuration, this.finding, this.session, this.isEditable());
        gridbag.setConstraints(this.findingContainer, constraints);
        this.add(this.findingContainer);

        /*
         * ConstraintsBuilder.buildConstraints(constraints, 0, 6, 6, 1, 18, 13);
         * constraints.fill =
         * GridBagConstraints.BOTH; JLabel Lfill = new JLabel("");
         * gridbag.setConstraints(Lfill, constraints);
         * this.add(Lfill);
         */

    }

    private void loadSchemaElement() {

        this.magnitude.setText("" + this.finding.getMagnitude());
        this.magnitude.setEditable(this.isEditable());

        this.chartDate.setText(this.finding.getChartDate());
        this.chartDate.setEditable(this.isEditable());

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
        this.comparismStars.setEditable(this.isEditable());

        this.magnitudeFainterThan.setSelected(this.finding.isMagnitudeFainterThan());
        this.magnitudeFainterThan.setEnabled(this.isEditable());

        this.brightSky.setSelected(this.finding.isBrightSky());
        this.brightSky.setEnabled(this.isEditable());

        this.clouds.setSelected(this.finding.isClouds());
        this.clouds.setEnabled(this.isEditable());

        this.poorSeeing.setSelected(this.finding.isPoorSeeing());
        this.poorSeeing.setEnabled(this.isEditable());

        this.nearHorizion.setSelected(this.finding.isNearHorizion());
        this.nearHorizion.setEnabled(this.isEditable());

        this.unusualActivity.setSelected(this.finding.isUnusualActivity());
        this.unusualActivity.setEnabled(this.isEditable());

        this.outburst.setSelected(this.finding.isOutburst());
        this.outburst.setEnabled(this.isEditable());

        this.nonAAVSOchart.setSelected(this.finding.isNonAAVSOchart());
        this.nonAAVSOchart.setEnabled(this.isEditable());

        this.comparismSequenceProblem.setSelected(this.finding.isComparismSequenceProblem());
        this.comparismSequenceProblem.setEnabled(this.isEditable());

        this.magnitudeUncertain.setSelected(this.finding.isMagnitudeUncertain());
        this.magnitudeUncertain.setEnabled(this.isEditable());

        this.starIdentificationUncertain.setSelected(this.finding.isStarIdentificationUncertain());
        this.starIdentificationUncertain.setEnabled(this.isEditable());

        this.faintStar.setSelected(this.finding.isFaintStar());
        this.faintStar.setEnabled(this.isEditable());

    }

    private List<String> getComparismStars() {

        String compStarText = this.comparismStars.getText();
        if (StringUtils.isBlank(compStarText)) {
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

        if (this.isEditable() && this.finding != null
                && Boolean.parseBoolean(this.configuration.getConfig(VariableStarsConfigKey.CONFIG_CACHE_ENABLED))) {

            this.configuration.setConfig(VariableStarFindingPanel.CONFIG_LAST_CHARTDATE, this.finding.getChartDate());
            // Use JTextField here so that we don't need to build up string again
            this.configuration.setConfig(VariableStarFindingPanel.CONFIG_LAST_COMPARISM_STARS,
                    this.comparismStars.getText());
            if (this.nonAAVSOchart.isSelected()) {
                this.configuration.setConfig(VariableStarFindingPanel.CONFIG_LAST_NONAAVSOCHART,
                        Boolean.toString(this.finding.isNonAAVSOchart()));
            } else {
                this.configuration.setConfig(VariableStarFindingPanel.CONFIG_LAST_NONAAVSOCHART, null);
            }
            if (this.target != null) {
                this.configuration.setConfig(VariableStarFindingPanel.CONFIG_LAST_STAR, this.target.getName());
            }
        }

    }

    private void loadFromCache() {

        String targetName = "";
        if (this.target != null) {
            targetName = this.target.getName().toLowerCase(Locale.getDefault());
        }

        if (this.isEditable()
                && Boolean.parseBoolean(this.configuration.getConfig(VariableStarsConfigKey.CONFIG_CACHE_ENABLED))
                && targetName.equals(this.configuration.getConfig(VariableStarFindingPanel.CONFIG_LAST_STAR, "")
                        .toLowerCase(Locale.getDefault()))) {

            String lastChartDate = this.configuration.getConfig(VariableStarFindingPanel.CONFIG_LAST_CHARTDATE);
            if (!StringUtils.isBlank(lastChartDate)) {
                this.chartDate.setText(lastChartDate);
            }

            String lastCompStars = this.configuration.getConfig(VariableStarFindingPanel.CONFIG_LAST_COMPARISM_STARS);
            if (!StringUtils.isBlank(lastCompStars)) {
                this.comparismStars.setText(lastCompStars);
            }

            String lastNonAAVSOChart = this.configuration.getConfig(VariableStarFindingPanel.CONFIG_LAST_NONAAVSOCHART);
            if (!StringUtils.isBlank(lastNonAAVSOChart)) {
                this.nonAAVSOchart.setSelected(Boolean.parseBoolean(lastNonAAVSOChart));
            }
        }

    }

}
