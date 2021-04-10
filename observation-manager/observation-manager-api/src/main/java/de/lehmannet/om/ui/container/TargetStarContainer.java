/* ====================================================================
 * /container/TargetStarContainer.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.container;

import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import de.lehmannet.om.IObserver;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.TargetStar;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.IConfiguration;
import de.lehmannet.om.ui.util.LocaleToolsFactory;
import de.lehmannet.om.util.FloatUtil;

public class TargetStarContainer extends Container {

    private final ResourceBundle bundle = LocaleToolsFactory.appInstance().getBundle("ObservationManager",
            Locale.getDefault());

    private boolean editable = false;

    private TargetStar starTarget = null;

    private TargetContainer targetContainer = null;

    // Make this accessible for e.g. VariableStarContainer which will change the
    // label text
    public JLabel labelMagnitudeApp = null;

    // Stars apparent magnitude (make this accessible for e.g. VariableStarContainer
    // which will change the label text)git
    public JTextField magnitudeApparent = null;

    // Stellar classification like O,B,A,F,G,K,M
    private JTextField stellarClassification = null;

    private final ObservationManagerModel model;
    private final IConfiguration configuration;

    public TargetStarContainer(IConfiguration configuration, ObservationManagerModel model, ITarget target,
            boolean editable) throws IllegalArgumentException {

        this.editable = editable;
        this.model = model;

        if ((target != null) && !(target instanceof TargetStar)) {
            throw new IllegalArgumentException("Passed ITarget must derive from de.lehmannet.om.TargetStar\n");
        }

        this.starTarget = (TargetStar) target;
        this.configuration = configuration;

        this.createPanel();

        if (this.starTarget != null) {
            this.loadSchemaElement();
        }

    }

    public ISchemaElement createSchemaElement() {

        String name = this.targetContainer.getName();
        String datasource = this.targetContainer.getDatasource();
        IObserver observer = this.targetContainer.getObserver();

        // Make sure only datasource or observer is set
        if (!this.targetContainer.checkOrigin(datasource, observer)) {
            return null;
        }

        if (observer != null) {
            this.starTarget = new TargetStar(name, observer);
        } else {
            this.starTarget = new TargetStar(name, datasource);
        }

        // Tell targetContainer about new created target
        this.targetContainer.setTarget(this.starTarget);

        // Set all other fields
        ITarget t = this.updateTarget();
        if (t == null) {
            return null;
        } else {
            this.starTarget = (TargetStar) t;
        }

        return this.starTarget;

    }

    public ISchemaElement getSchemaElement() {

        return this.starTarget;

    }

    public ITarget updateTarget() {

        if (this.starTarget == null) {
            return null;
        }

        // Generic Target stuff
        ITarget t = this.targetContainer.updateTarget();
        if (t == null) {
            return null;
        } else {
            this.starTarget = (TargetStar) t;
        }

        String magnitudeApp = this.magnitudeApparent.getText().trim();
        if (!"".equals(magnitudeApp)) {
            try {
                float mag = FloatUtil.parseFloat(magnitudeApp);
                this.starTarget.setMagnitudeApparent(mag);
            } catch (NumberFormatException nfe) {
                this.createWarning(this.bundle.getString("panel.targetStar.warning.magnitudeApparent"));
                return null;
            }
        } else {
            this.starTarget.setMagnitudeApparent(Float.NaN);
        }

        String classification = this.stellarClassification.getText().trim();
        if (!"".equals(classification)) {
            this.starTarget.setStellarClassification(classification);
        } else {
            this.starTarget.setStellarClassification("");
        }

        return this.starTarget;

    }

    private void loadSchemaElement() {

        // Container data will be loaded while construction of container in
        // createPanel method

        if (!Float.isNaN(this.starTarget.getMagnitudeApparent())) {
            this.magnitudeApparent.setText("" + this.starTarget.getMagnitudeApparent());
        }
        this.magnitudeApparent.setEditable(this.editable);

        if (this.starTarget.getStellarClassification() != null) {
            this.stellarClassification.setText(this.starTarget.getStellarClassification());
        }
        this.stellarClassification.setEditable(this.editable);

    }

    public boolean checkOrigin(String datasource, IObserver observer) {

        return targetContainer.checkOrigin(datasource, observer);

    }

    public String getDatasource() {

        return targetContainer.getDatasource();

    }

    public IObserver getObserver() {

        return targetContainer.getObserver();

    }

    @Override
    public String getName() {

        return targetContainer.getName();

    }

    public void setTarget(TargetStar target) {

        this.starTarget = target;
        this.targetContainer.setTarget(target);

    }

    private void createPanel() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        this.setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 4, 1, 45, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.targetContainer = new TargetContainer(this.configuration, this.model, this.starTarget, this.editable,
                false);
        gridbag.setConstraints(this.targetContainer, constraints);
        this.add(this.targetContainer);

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 4, 1, 100, 1);
        JSeparator seperator1 = new JSeparator(SwingConstants.HORIZONTAL);
        gridbag.setConstraints(seperator1, constraints);
        this.add(seperator1);

        ConstraintsBuilder.buildConstraints(constraints, 0, 2, 1, 1, 1, 1);
        constraints.fill = GridBagConstraints.NONE;
        JLabel LstellarClassification = new JLabel(
                this.bundle.getString("panel.targetStar.label.stellarClassification"));
        LstellarClassification.setToolTipText(this.bundle.getString("panel.targetStar.tooltip.stellarClassification"));
        gridbag.setConstraints(LstellarClassification, constraints);
        LstellarClassification.setFont(new Font("sansserif", Font.ITALIC + Font.BOLD, 12));
        this.add(LstellarClassification);
        ConstraintsBuilder.buildConstraints(constraints, 1, 2, 1, 1, 5, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.stellarClassification = new JTextField();
        this.stellarClassification
                .setToolTipText(this.bundle.getString("panel.targetStar.tooltip.stellarClassification"));
        this.stellarClassification.setEditable(this.editable);
        gridbag.setConstraints(this.stellarClassification, constraints);
        this.add(this.stellarClassification);

        // Dummy for filling space
        ConstraintsBuilder.buildConstraints(constraints, 2, 2, 2, 1, 70, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        JLabel Ldummy = new JLabel("");
        gridbag.setConstraints(Ldummy, constraints);
        this.add(Ldummy);

        ConstraintsBuilder.buildConstraints(constraints, 0, 3, 1, 1, 1, 1);
        constraints.fill = GridBagConstraints.NONE;
        this.labelMagnitudeApp = new JLabel(this.bundle.getString("panel.targetStar.label.magnitudeApparent"));
        this.labelMagnitudeApp.setToolTipText(this.bundle.getString("panel.targetStar.tooltip.magnitudeApparent"));
        gridbag.setConstraints(this.labelMagnitudeApp, constraints);
        this.labelMagnitudeApp.setFont(new Font("sansserif", Font.ITALIC + Font.BOLD, 12));
        this.add(this.labelMagnitudeApp);
        ConstraintsBuilder.buildConstraints(constraints, 1, 3, 1, 1, 5, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.magnitudeApparent = new JTextField();
        this.magnitudeApparent.setToolTipText(this.bundle.getString("panel.targetStar.tooltip.magnitudeApparent"));
        this.magnitudeApparent.setEditable(this.editable);
        gridbag.setConstraints(this.magnitudeApparent, constraints);
        this.add(this.magnitudeApparent);

        // Dummy for filling space
        ConstraintsBuilder.buildConstraints(constraints, 2, 3, 2, 1, 70, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        JLabel Ldummy2 = new JLabel("");
        gridbag.setConstraints(Ldummy2, constraints);
        this.add(Ldummy2);

        ConstraintsBuilder.buildConstraints(constraints, 0, 4, 4, 1, 5, 93);
        constraints.fill = GridBagConstraints.BOTH;
        JLabel Lfill = new JLabel("");
        gridbag.setConstraints(Lfill, constraints);
        this.add(Lfill);

    }

    private void createWarning(String message) {

        JOptionPane.showMessageDialog(this, message, this.bundle.getString("target.warning.title"),
                JOptionPane.WARNING_MESSAGE);

    }

}
