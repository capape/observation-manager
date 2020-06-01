/* ====================================================================
 * /extension/deepSky/panel/DeepSkyTargetDNPanel.java
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

import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import de.lehmannet.om.IObserver;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetDN;
import de.lehmannet.om.model.ObservationManagerModel;

import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.IConfiguration;
import de.lehmannet.om.ui.util.OMLabel;
import de.lehmannet.om.ui.util.UserInterfaceHelper;

public class DeepSkyTargetDNPanel extends AbstractPanel {

    private static final long serialVersionUID = -87037689673091977L;

    private final PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
            .getBundle("de.lehmannet.om.ui.extension.deepSky.DeepSky", Locale.getDefault());

    private DeepSkyTargetDN target = null;

    private DeepSkyTargetContainer deepSkyTargetContainer = null;
    private JTextField opacity = null;
    private JTextField positionAngle = null;
    private final ObservationManagerModel model;
    private final UserInterfaceHelper uiHelper;
    private final IConfiguration configuration;

    public DeepSkyTargetDNPanel(IConfiguration configuration, UserInterfaceHelper uiHelper, ObservationManagerModel model, ITarget target, Boolean editable)
            throws IllegalArgumentException {

        super(editable);

        if ((target != null) && !(target instanceof DeepSkyTargetDN)) {
            throw new IllegalArgumentException(
                    "Passed ITarget must derive from de.lehmannet.om.extension.deepSky.DeepSkyTargetDN\n");
        }

        this.target = (DeepSkyTargetDN) target;
        this.model = model;
        this.configuration = configuration;
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
            this.target = (DeepSkyTargetDN) t;
        }

        // Optional parameters
        String opacity = this.opacity.getText().trim();
        if (!"".equals(opacity)) {
            int o = Integer.parseInt(opacity);
            if ((o < 1) || (o > 6)) {
                this.createWarning(this.bundle.getString("panel.dn.warning.opacity.invalid"));
                return null;
            }
            this.target.setOpacity(o);
        }

        String pA = this.positionAngle.getText();
        if (pA != null) {
            int p = -1;

            if (!"".equals(pA.trim())) {
                try {
                    p = Integer.parseInt(pA);

                    if ((p < 0) || (p > 359)) {
                        this.createWarning(this.bundle.getString("panel.dn.warning.posAngle.invalid"));
                        return null;
                    }
                } catch (NumberFormatException nfe) {
                    this.createWarning(this.bundle.getString("panel.dn.warning.posAngle.invalid"));
                    return null;
                }
            }

            this.target.setPositionAngle(p);
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
            this.target = new DeepSkyTargetDN(name, observer);
        } else {
            this.target = new DeepSkyTargetDN(name, datasource);
        }

        // Set all other fields
        ITarget t = (ITarget) this.updateSchemaElement();
        if (t == null) {
            return null;
        } else {
            this.target = (DeepSkyTargetDN) t;
        }

        return this.target;

    }

    private void loadSchemaElement() {

        if (this.target.getOpacity() != -1) {
            this.opacity.setText("" + this.target.getOpacity());
        }
        this.opacity.setEditable(this.isEditable());

        if (this.target.getPositionAngle() != -1) {
            this.positionAngle.setText("" + this.target.getPositionAngle());
        }
        this.positionAngle.setEditable(this.isEditable());

    }

    private void createPanel() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 4, 6, 100, 1);
        this.deepSkyTargetContainer = new DeepSkyTargetContainer( this.uiHelper, this.model, this.target,
                this.isEditable());
        gridbag.setConstraints(this.deepSkyTargetContainer, constraints);
        this.add(this.deepSkyTargetContainer);

        ConstraintsBuilder.buildConstraints(constraints, 0, 6, 4, 1, 100, 1);
        JSeparator seperator1 = new JSeparator(SwingConstants.HORIZONTAL);
        gridbag.setConstraints(seperator1, constraints);
        this.add(seperator1);

        ConstraintsBuilder.buildConstraints(constraints, 0, 7, 1, 1, 5, 1);
        OMLabel Lopacity = new OMLabel(this.bundle.getString("panel.dn.label.opacity"), false);
        Lopacity.setToolTipText(this.bundle.getString("panel.dn.tooltip.opacity"));
        gridbag.setConstraints(Lopacity, constraints);
        this.add(Lopacity);
        ConstraintsBuilder.buildConstraints(constraints, 1, 7, 1, 1, 45, 1);
        this.opacity = new JTextField();
        this.opacity.setToolTipText(this.bundle.getString("panel.dn.tooltip.opacity"));
        this.opacity.setEditable(this.isEditable());
        gridbag.setConstraints(this.opacity, constraints);
        this.add(this.opacity);

        ConstraintsBuilder.buildConstraints(constraints, 2, 7, 1, 1, 5, 1);
        OMLabel LpositionAngle = new OMLabel(this.bundle.getString("panel.dn.label.posAngle"), SwingConstants.RIGHT,
                false);
        LpositionAngle.setToolTipText(this.bundle.getString("panel.dn.tooltip.posAngle"));
        gridbag.setConstraints(LpositionAngle, constraints);
        this.add(LpositionAngle);
        ConstraintsBuilder.buildConstraints(constraints, 3, 7, 1, 1, 45, 1);
        this.positionAngle = new JTextField(3);
        this.positionAngle.setToolTipText(this.bundle.getString("panel.dn.tooltip.posAngle"));
        gridbag.setConstraints(this.positionAngle, constraints);
        this.add(this.positionAngle);

        ConstraintsBuilder.buildConstraints(constraints, 0, 8, 4, 1, 100, 95);
        constraints.fill = GridBagConstraints.BOTH;
        JLabel Lfill = new JLabel("");
        gridbag.setConstraints(Lfill, constraints);
        this.add(Lfill);

    }

}
