/* ====================================================================
 * /container/EquPositionContainer.java
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
import javax.swing.JTextField;

import de.lehmannet.om.EquPosition;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.LocaleToolsFactory;

class EquPositionContainer extends Container {

    /**
     *
     */
    private static final long serialVersionUID = 4434903051968591296L;

    private final ResourceBundle bundle = LocaleToolsFactory.appInstance().getBundle("ObservationManager",
            Locale.getDefault());

    private EquPosition equPosition = null;

    private boolean editable = false;

    private final JTextField raHourValue = new JTextField();
    private final JTextField raMinValue = new JTextField();
    private final JTextField raSecValue = new JTextField();

    private final JTextField decDegreeValue = new JTextField();
    private final JTextField decMinValue = new JTextField();
    private final JTextField decSecValue = new JTextField();

    public EquPositionContainer(EquPosition position, boolean editable) {

        this.editable = editable;
        this.equPosition = position;

        this.createContainer();
        if (this.equPosition != null) {
            this.loadEquPosition();
        }

    }

    public EquPosition getEquPosition() {

        int raH = -1;
        int raM = -1;
        double raS = -1d;
        try {
            raH = Integer.parseInt(this.raHourValue.getText());
            raM = Integer.parseInt(this.raMinValue.getText());
            raS = Double.parseDouble(this.raSecValue.getText());
        } catch (NumberFormatException nfe) {
            return null;
        }

        String ra = EquPosition.getRaString(raH, raM, raS);

        int decD = -1;
        int decM = -1;
        double decS = -1;
        try {
            decD = Integer.parseInt(this.decDegreeValue.getText());
            decM = Integer.parseInt(this.decMinValue.getText());
            decS = Double.parseDouble(this.decSecValue.getText());
        } catch (NumberFormatException nfe) {
            return null;
        }

        String dec = EquPosition.getDecString(decD, decM, decS);

        this.equPosition = new EquPosition(ra, dec);

        return this.equPosition;

    }

    public void setEquPosition(EquPosition equPosition) {

        if (equPosition != null) {
            this.equPosition = equPosition;
            this.loadEquPosition();
        }

    }

    public void setEditable(boolean editable) {

        this.editable = editable;

        this.raHourValue.setEditable(this.editable);
        this.raMinValue.setEditable(this.editable);
        this.raSecValue.setEditable(this.editable);

        this.decDegreeValue.setEditable(this.editable);
        this.decMinValue.setEditable(this.editable);
        this.decSecValue.setEditable(this.editable);

    }

    private void loadEquPosition() {

        String ra = this.equPosition.getRa();
        String raHour = ra.substring(0, ra.indexOf(EquPosition.RA_HOUR));
        String raMin = ra.substring(ra.indexOf(EquPosition.RA_HOUR) + 1, ra.indexOf(EquPosition.RA_MIN));
        String raSec = ra.substring(ra.indexOf(EquPosition.RA_MIN) + 1, ra.indexOf(EquPosition.RA_SEC));

        String dec = this.equPosition.getDec();
        String decDeg = dec.substring(0, dec.indexOf(EquPosition.DEC_DEG));
        String decMin = dec.substring(dec.indexOf(EquPosition.DEC_DEG) + 1, dec.indexOf(EquPosition.DEC_MIN));
        String decSec = dec.substring(dec.indexOf(EquPosition.DEC_MIN) + 1, dec.indexOf(EquPosition.DEC_SEC));

        this.raHourValue.setText(raHour);
        this.raMinValue.setText(raMin);
        this.raSecValue.setText(raSec);

        this.decDegreeValue.setText(decDeg);
        this.decMinValue.setText(decMin);
        this.decSecValue.setText(decSec);

    }

    private void createContainer() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 7, 1, 100, 1);
        JLabel Lposition = new JLabel(this.bundle.getString("equPosition.label.position"));
        Lposition.setToolTipText(this.bundle.getString("equPosition.label.coordinates"));
        Lposition.setFont(new Font("sansserif", Font.ITALIC + Font.BOLD, 12));
        gridbag.setConstraints(Lposition, constraints);
        this.add(Lposition);

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 1, 1, 10, 1);
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.EAST;
        JLabel LRA = new JLabel(this.bundle.getString("equPosition.label.ra"));
        LRA.setToolTipText(this.bundle.getString("equPosition.tooltip.ra"));
        gridbag.setConstraints(LRA, constraints);
        this.add(LRA);

        ConstraintsBuilder.buildConstraints(constraints, 1, 1, 1, 1, 25, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        gridbag.setConstraints(this.raHourValue, constraints);
        this.raHourValue.setEditable(this.editable);
        this.raHourValue.setToolTipText(this.bundle.getString("equPosition.tooltip.ra"));
        this.add(this.raHourValue);
        ConstraintsBuilder.buildConstraints(constraints, 2, 1, 1, 1, 5, 1);
        JLabel raHourLabel = new JLabel(EquPosition.RA_HOUR);
        raHourLabel.setToolTipText(this.bundle.getString("equPosition.tooltip.ra"));
        gridbag.setConstraints(raHourLabel, constraints);
        this.add(raHourLabel);

        ConstraintsBuilder.buildConstraints(constraints, 3, 1, 1, 1, 25, 1);
        this.raMinValue.setEditable(this.editable);
        this.raMinValue.setToolTipText(this.bundle.getString("equPosition.tooltip.ra"));
        gridbag.setConstraints(this.raMinValue, constraints);
        this.add(this.raMinValue);
        ConstraintsBuilder.buildConstraints(constraints, 4, 1, 1, 1, 5, 1);
        JLabel raMinLabel = new JLabel(EquPosition.RA_MIN);
        raMinLabel.setToolTipText(this.bundle.getString("equPosition.tooltip.ra"));
        gridbag.setConstraints(raMinLabel, constraints);
        this.add(raMinLabel);

        ConstraintsBuilder.buildConstraints(constraints, 5, 1, 1, 1, 25, 1);
        this.raSecValue.setEditable(this.editable);
        this.raSecValue.setToolTipText(this.bundle.getString("equPosition.tooltip.ra"));
        gridbag.setConstraints(this.raSecValue, constraints);
        this.add(this.raSecValue);
        ConstraintsBuilder.buildConstraints(constraints, 6, 1, 1, 1, 5, 1);
        JLabel raSecLabel = new JLabel(EquPosition.RA_SEC);
        raSecLabel.setToolTipText(this.bundle.getString("equPosition.tooltip.ra"));
        gridbag.setConstraints(raSecLabel, constraints);
        this.add(raSecLabel);

        ConstraintsBuilder.buildConstraints(constraints, 0, 2, 1, 1, 10, 1);
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.EAST;
        JLabel LDEC = new JLabel(this.bundle.getString("equPosition.label.dec"));
        LDEC.setToolTipText(this.bundle.getString("equPosition.tooltip.dec"));
        gridbag.setConstraints(LDEC, constraints);
        this.add(LDEC);

        ConstraintsBuilder.buildConstraints(constraints, 1, 2, 1, 1, 25, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        this.decDegreeValue.setEditable(this.editable);
        this.decDegreeValue.setToolTipText(this.bundle.getString("equPosition.tooltip.dec"));
        gridbag.setConstraints(this.decDegreeValue, constraints);
        this.add(this.decDegreeValue);
        ConstraintsBuilder.buildConstraints(constraints, 2, 2, 1, 1, 5, 1);
        JLabel decDegreeLabel = new JLabel(EquPosition.DEC_DEG);
        decDegreeLabel.setToolTipText(this.bundle.getString("equPosition.tooltip.dec"));
        gridbag.setConstraints(decDegreeLabel, constraints);
        this.add(decDegreeLabel);

        ConstraintsBuilder.buildConstraints(constraints, 3, 2, 1, 1, 25, 1);
        this.decMinValue.setEditable(this.editable);
        this.decMinValue.setToolTipText(this.bundle.getString("equPosition.tooltip.dec"));
        gridbag.setConstraints(this.decMinValue, constraints);
        this.add(this.decMinValue);
        ConstraintsBuilder.buildConstraints(constraints, 4, 2, 1, 1, 5, 1);
        JLabel decMinLabel = new JLabel(EquPosition.DEC_MIN);
        decMinLabel.setToolTipText(this.bundle.getString("equPosition.tooltip.dec"));
        gridbag.setConstraints(decMinLabel, constraints);
        this.add(decMinLabel);

        ConstraintsBuilder.buildConstraints(constraints, 5, 2, 1, 1, 25, 1);
        this.decSecValue.setEditable(this.editable);
        this.decSecValue.setToolTipText(this.bundle.getString("equPosition.tooltip.dec"));
        gridbag.setConstraints(this.decSecValue, constraints);
        this.add(this.decSecValue);
        ConstraintsBuilder.buildConstraints(constraints, 6, 2, 1, 1, 5, 1);
        JLabel decSecLabel = new JLabel(EquPosition.DEC_SEC);
        decSecLabel.setToolTipText(this.bundle.getString("equPosition.tooltip.dec"));
        gridbag.setConstraints(decSecLabel, constraints);
        this.add(decSecLabel);

    }

}
