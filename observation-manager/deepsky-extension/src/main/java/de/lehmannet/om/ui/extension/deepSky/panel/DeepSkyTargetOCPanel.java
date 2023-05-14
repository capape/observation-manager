/*
 * ====================================================================
 * /extension/deepSky/panel/DeepSkyTargetOCPanel.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.deepSky.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import de.lehmannet.om.IObserver;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetOC;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.OMLabel;
import de.lehmannet.om.ui.util.UserInterfaceHelper;

public class DeepSkyTargetOCPanel extends AbstractPanel {

    private static final long serialVersionUID = 419950163961421970L;

    private final ResourceBundle bundle = ResourceBundle.getBundle("de.lehmannet.om.ui.extension.deepSky.DeepSky",
            Locale.getDefault());

    private DeepSkyTargetOC target = null;

    private DeepSkyTargetContainer deepSkyTargetContainer = null;
    private JTextField brightestStar = null;
    private JTextField stars = null;
    private JTextField clusterClassification = null;
    private final ObservationManagerModel model;
    private final UserInterfaceHelper uiHelper;

    public DeepSkyTargetOCPanel(UserInterfaceHelper uiHelper, ObservationManagerModel model, ITarget target,
            Boolean editable) throws IllegalArgumentException {

        super(editable);

        if ((target != null) && !(target instanceof DeepSkyTargetOC)) {
            throw new IllegalArgumentException(
                    "Passed ITarget must derive from de.lehmannet.om.extension.deepSky.DeepSkyTargetDN\n");
        }

        this.target = (DeepSkyTargetOC) target;
        this.model = model;
        this.uiHelper = uiHelper;
        this.createPanel();

        if (this.target != null) {
            this.loadSchemaElement();
        }

    }

    @Override
    public ISchemaElement getSchemaElement() {

        return this.target;

    }

    @Override
    public ISchemaElement updateSchemaElement() {

        if (this.target == null) {
            return null;
        }

        ITarget t = this.deepSkyTargetContainer.updateTarget(this.target);
        if (t == null) {
            return null;
        } else {
            this.target = (DeepSkyTargetOC) t;
        }

        // Optional parameters
        String amountStars = this.stars.getText().trim();
        if (!"".equals(amountStars)) {
            int aS = Integer.parseInt(amountStars);
            if (aS < 1) {
                this.createWarning(this.bundle.getString("panel.oc.warning.amount.positive"));
            }
            this.target.setAmountOfStars(aS);
        }

        String brightestStar = this.brightestStar.getText().trim();
        if (!"".equals(brightestStar)) {
            double bS = Double.parseDouble(brightestStar);
            this.target.setBrightestStar(bS);
        }

        String clusterClassification = this.clusterClassification.getText().trim();
        if (!"".equals(clusterClassification)) {
            this.target.setClusterClassification(clusterClassification);
        }

        return this.target;

    }

    @Override
    public ISchemaElement createSchemaElement() {

        String name = this.deepSkyTargetContainer.getName();
        String datasource = this.deepSkyTargetContainer.getDatasource();
        IObserver observer = this.deepSkyTargetContainer.getObserver();

        // Make sure only datasource or observer is set
        if (this.deepSkyTargetContainer.checkOrigin(datasource, observer)) {
            return null;
        }

        if (observer != null) {
            this.target = new DeepSkyTargetOC(name, observer);
        } else {
            this.target = new DeepSkyTargetOC(name, datasource);
        }

        // Set all other fields
        ITarget t = (ITarget) this.updateSchemaElement();
        if (t == null) {
            return null;
        } else {
            this.target = (DeepSkyTargetOC) t;
        }

        return this.target;

    }

    private void loadSchemaElement() {

        if (this.target.getAmountOfStars() != -1) {
            this.stars.setText("" + this.target.getAmountOfStars());
        }
        this.stars.setEditable(this.isEditable());

        if (!Double.isNaN(this.target.getBrightestStar())) {
            this.brightestStar.setText("" + this.target.getBrightestStar());
        }
        this.brightestStar.setEditable(this.isEditable());

        if (this.target.getClusterClassification() != null) {
            this.clusterClassification.setText(this.target.getClusterClassification());
        }
        this.clusterClassification.setEditable(this.isEditable());

    }

    private void createPanel() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 4, 1, 45, 1);
        this.deepSkyTargetContainer = new DeepSkyTargetContainer(this.uiHelper, this.model, this.target,
                this.isEditable());
        gridbag.setConstraints(this.deepSkyTargetContainer, constraints);
        this.add(this.deepSkyTargetContainer);

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 4, 1, 100, 1);
        JSeparator seperator1 = new JSeparator(SwingConstants.HORIZONTAL);
        gridbag.setConstraints(seperator1, constraints);
        this.add(seperator1);

        ConstraintsBuilder.buildConstraints(constraints, 0, 2, 1, 1, 5, 1);
        OMLabel Lamount = new OMLabel(this.bundle.getString("panel.oc.label.amount"), false);
        Lamount.setToolTipText(this.bundle.getString("panel.oc.tooltip.amount"));
        gridbag.setConstraints(Lamount, constraints);
        this.add(Lamount);
        ConstraintsBuilder.buildConstraints(constraints, 1, 2, 1, 1, 45, 1);
        this.stars = new JTextField();
        this.stars.setToolTipText(this.bundle.getString("panel.oc.tooltip.amount"));
        this.stars.setEditable(this.isEditable());
        gridbag.setConstraints(this.stars, constraints);
        this.add(this.stars);

        ConstraintsBuilder.buildConstraints(constraints, 2, 2, 1, 1, 5, 1);
        OMLabel Lbrightest = new OMLabel(this.bundle.getString("panel.oc.label.brightestStar"), SwingConstants.RIGHT,
                false);
        Lbrightest.setToolTipText(this.bundle.getString("panel.oc.tooltip.brightestStar"));
        gridbag.setConstraints(Lbrightest, constraints);
        this.add(Lbrightest);
        ConstraintsBuilder.buildConstraints(constraints, 3, 2, 1, 1, 45, 1);
        this.brightestStar = new JTextField();
        this.brightestStar.setToolTipText(this.bundle.getString("panel.oc.tooltip.brightestStar"));
        this.brightestStar.setEditable(this.isEditable());
        gridbag.setConstraints(this.brightestStar, constraints);
        this.add(this.brightestStar);

        ConstraintsBuilder.buildConstraints(constraints, 0, 3, 1, 1, 5, 1);
        OMLabel Lclass = new OMLabel(this.bundle.getString("panel.oc.label.classification"), false);
        Lclass.setToolTipText(this.bundle.getString("panel.oc.tooltip.classification"));
        gridbag.setConstraints(Lclass, constraints);
        this.add(Lclass);
        ConstraintsBuilder.buildConstraints(constraints, 1, 3, 1, 1, 45, 1);
        this.clusterClassification = new JTextField();
        this.clusterClassification.setToolTipText(this.bundle.getString("panel.oc.tooltip.classification"));
        this.clusterClassification.setEditable(this.isEditable());
        gridbag.setConstraints(this.clusterClassification, constraints);
        this.add(this.clusterClassification);

        ConstraintsBuilder.buildConstraints(constraints, 0, 4, 4, 1, 45, 93);
        constraints.fill = GridBagConstraints.BOTH;
        JLabel Lfill = new JLabel("");
        gridbag.setConstraints(Lfill, constraints);
        this.add(Lfill);

    }

}
