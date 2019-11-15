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
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JTextField;

import de.lehmannet.om.EquPosition;
import de.lehmannet.om.ui.util.ConstraintsBuilder;

public class EquPositionContainer extends Container {

    final PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle.getBundle("ObservationManager",
            Locale.getDefault());

    private EquPosition equPosition = null;

    private boolean editable = false;

    private JTextField raHourValue = new JTextField();
    private JLabel raHourLabel = null;
    private JTextField raMinValue = new JTextField();;
    private JLabel raMinLabel = null;
    private JTextField raSecValue = new JTextField();;
    private JLabel raSecLabel = null;

    private JTextField decDegreeValue = new JTextField();;
    private JLabel decDegreeLabel = null;
    private JTextField decMinValue = new JTextField();;
    private JLabel decMinLabel = null;
    private JTextField decSecValue = new JTextField();;
    private JLabel decSecLabel = null;

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
        super.setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 7, 1, 100, 1);
        JLabel Lposition = new JLabel(this.bundle.getString("equPosition.label.position"));
        Lposition.setToolTipText(this.bundle.getString("equPosition.label.coordinates"));
        Lposition.setFont(new Font("sansserif", Font.ITALIC + Font.BOLD, 12));
        gridbag.setConstraints(Lposition, constraints);
        super.add(Lposition);

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 1, 1, 10, 1);
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.EAST;
        JLabel LRA = new JLabel(this.bundle.getString("equPosition.label.ra"));
        LRA.setToolTipText(this.bundle.getString("equPosition.tooltip.ra"));
        gridbag.setConstraints(LRA, constraints);
        super.add(LRA);

        ConstraintsBuilder.buildConstraints(constraints, 1, 1, 1, 1, 25, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        gridbag.setConstraints(this.raHourValue, constraints);
        this.raHourValue.setEditable(this.editable);
        this.raHourValue.setToolTipText(this.bundle.getString("equPosition.tooltip.ra"));
        super.add(this.raHourValue);
        ConstraintsBuilder.buildConstraints(constraints, 2, 1, 1, 1, 5, 1);
        this.raHourLabel = new JLabel(EquPosition.RA_HOUR);
        this.raHourLabel.setToolTipText(this.bundle.getString("equPosition.tooltip.ra"));
        gridbag.setConstraints(this.raHourLabel, constraints);
        super.add(this.raHourLabel);

        ConstraintsBuilder.buildConstraints(constraints, 3, 1, 1, 1, 25, 1);
        this.raMinValue.setEditable(this.editable);
        this.raMinValue.setToolTipText(this.bundle.getString("equPosition.tooltip.ra"));
        gridbag.setConstraints(this.raMinValue, constraints);
        super.add(this.raMinValue);
        ConstraintsBuilder.buildConstraints(constraints, 4, 1, 1, 1, 5, 1);
        this.raMinLabel = new JLabel(EquPosition.RA_MIN);
        this.raMinLabel.setToolTipText(this.bundle.getString("equPosition.tooltip.ra"));
        gridbag.setConstraints(this.raMinLabel, constraints);
        super.add(this.raMinLabel);

        ConstraintsBuilder.buildConstraints(constraints, 5, 1, 1, 1, 25, 1);
        this.raSecValue.setEditable(this.editable);
        this.raSecValue.setToolTipText(this.bundle.getString("equPosition.tooltip.ra"));
        gridbag.setConstraints(this.raSecValue, constraints);
        super.add(this.raSecValue);
        ConstraintsBuilder.buildConstraints(constraints, 6, 1, 1, 1, 5, 1);
        this.raSecLabel = new JLabel(EquPosition.RA_SEC);
        this.raSecLabel.setToolTipText(this.bundle.getString("equPosition.tooltip.ra"));
        gridbag.setConstraints(this.raSecLabel, constraints);
        super.add(this.raSecLabel);

        ConstraintsBuilder.buildConstraints(constraints, 0, 2, 1, 1, 10, 1);
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.EAST;
        JLabel LDEC = new JLabel(this.bundle.getString("equPosition.label.dec"));
        LDEC.setToolTipText(this.bundle.getString("equPosition.tooltip.dec"));
        gridbag.setConstraints(LDEC, constraints);
        super.add(LDEC);

        ConstraintsBuilder.buildConstraints(constraints, 1, 2, 1, 1, 25, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        this.decDegreeValue.setEditable(this.editable);
        this.decDegreeValue.setToolTipText(this.bundle.getString("equPosition.tooltip.dec"));
        gridbag.setConstraints(this.decDegreeValue, constraints);
        super.add(this.decDegreeValue);
        ConstraintsBuilder.buildConstraints(constraints, 2, 2, 1, 1, 5, 1);
        this.decDegreeLabel = new JLabel(EquPosition.DEC_DEG);
        this.decDegreeLabel.setToolTipText(this.bundle.getString("equPosition.tooltip.dec"));
        gridbag.setConstraints(this.decDegreeLabel, constraints);
        super.add(this.decDegreeLabel);

        ConstraintsBuilder.buildConstraints(constraints, 3, 2, 1, 1, 25, 1);
        this.decMinValue.setEditable(this.editable);
        this.decMinValue.setToolTipText(this.bundle.getString("equPosition.tooltip.dec"));
        gridbag.setConstraints(this.decMinValue, constraints);
        super.add(this.decMinValue);
        ConstraintsBuilder.buildConstraints(constraints, 4, 2, 1, 1, 5, 1);
        this.decMinLabel = new JLabel(EquPosition.DEC_MIN);
        this.decMinLabel.setToolTipText(this.bundle.getString("equPosition.tooltip.dec"));
        gridbag.setConstraints(this.decMinLabel, constraints);
        super.add(this.decMinLabel);

        ConstraintsBuilder.buildConstraints(constraints, 5, 2, 1, 1, 25, 1);
        this.decSecValue.setEditable(this.editable);
        this.decSecValue.setToolTipText(this.bundle.getString("equPosition.tooltip.dec"));
        gridbag.setConstraints(this.decSecValue, constraints);
        super.add(this.decSecValue);
        ConstraintsBuilder.buildConstraints(constraints, 6, 2, 1, 1, 5, 1);
        this.decSecLabel = new JLabel(EquPosition.DEC_SEC);
        this.decSecLabel.setToolTipText(this.bundle.getString("equPosition.tooltip.dec"));
        gridbag.setConstraints(this.decSecLabel, constraints);
        super.add(this.decSecLabel);

    }

}
