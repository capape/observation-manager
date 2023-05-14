/*
 * ====================================================================
 * /container/AngleContainer.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.container;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import de.lehmannet.om.Angle;
import de.lehmannet.om.EquPosition;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.LocaleToolsFactory;

public class AngleContainer extends Container {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final String UNIT_KEY_PREFIX = "Angle.Unit.";
    private final ResourceBundle bundle = LocaleToolsFactory.appInstance().getBundle("ObservationManager",
            Locale.getDefault());

    private double angleValue = Double.NaN;
    private String angleUnit = Angle.DEGREE;

    private boolean editable = false;

    // Stores all possible units, or NULL if units cannot be choosen
    private String[] units = null;

    // Show arc value fields instead of decimal field
    private boolean useArc = false;

    private JLabel unit = null;
    private final AngleUnitBox unitBox = new AngleUnitBox();
    private final JTextField decValue = new JTextField();
    private final JTextField arcDegreeValue = new JTextField();
    private final JTextField arcMinValue = new JTextField();
    private final JTextField arcSecValue = new JTextField();

    private final Map<String, String> unitI18Nmap = new HashMap<>();

    public AngleContainer(Angle angle, boolean editable) {

        this.setAngle(angle);

        this.editable = editable;

        this.createContainer();

    }

    public AngleContainer(String angleUnit, boolean editable) {

        if (Angle.isValidUnit(angleUnit)) {
            this.angleUnit = angleUnit;

            this.editable = editable;
            this.createContainer();

        } else {
            throw new IllegalArgumentException("Invalid angle unit. Use de.lehmannet.om.Angle constants.\n");
        }

    }

    public Angle getAngle() throws NumberFormatException {

        if (!this.useArc) {
            if ((this.decValue.getText() != null) && !("".equals(this.decValue.getText()))) {
                try {
                    this.angleValue = Double.parseDouble(this.decValue.getText());
                } catch (NumberFormatException nfe) {
                    throw new NumberFormatException("Given DEC value " + this.decValue.getText() + " cannot be parsed");
                }

                if ((this.editable) && (this.units != null)) {
                    this.angleUnit = this.getUnitFromI18NString((String) this.unitBox.getSelectedItem());
                }

                return new Angle(this.angleValue, this.angleUnit);
            }
        } else {
            if ((this.arcDegreeValue.getText() != null) && !("".equals(this.arcDegreeValue.getText().trim()))) {
                int deg;
                try {
                    deg = Integer.parseInt(this.arcDegreeValue.getText());
                } catch (NumberFormatException nfe) {
                    throw new NumberFormatException(
                            "Given arc degree value " + this.arcDegreeValue.getText() + " cannot be parsed");
                }

                int min = 0;
                if ((this.arcMinValue.getText() != null) && !("".equals(this.arcMinValue.getText().trim()))) {
                    try {
                        min = Integer.parseInt(this.arcMinValue.getText());
                    } catch (NumberFormatException nfe) {
                        throw new NumberFormatException(
                                "Given arc min value " + this.arcMinValue.getText() + " cannot be parsed");
                    }
                }

                int sec = 0;
                if ((this.arcSecValue.getText() != null) && !("".equals(this.arcSecValue.getText().trim()))) {
                    try {
                        String secString = this.arcSecValue.getText();
                        secString = secString.replace('.', ' ');
                        secString = secString.replace(',', ' ');
                        sec = Integer.parseInt(secString);
                    } catch (NumberFormatException nfe) {
                        throw new NumberFormatException(
                                "Given arc sec value " + this.arcSecValue.getText() + " cannot be parsed");
                    }
                }

                // Use EquPosition for transformation (with dummy value for RA)
                String arcString = EquPosition.getDecString(deg, min, sec);
                EquPosition eq = new EquPosition(
                        "0" + EquPosition.RA_HOUR + "0" + EquPosition.RA_MIN + "0" + EquPosition.RA_SEC, arcString);

                Angle decAngle = eq.getDecAngle();
                this.angleValue = decAngle.getValue();
                this.angleUnit = decAngle.getUnit(); // Should always set the value to DEGREE

                return decAngle;

            }
        }

        return null;

    }

    public void setAngle(Angle angle) {

        if (angle != null) {
            this.angleUnit = angle.getUnit();
            this.angleValue = angle.getValue();

            this.decValue.setText(this.getValue());
            this.unit.setText(this.getI18NUnit(this.angleUnit));
            this.unitBox.addItem(this.getI18NUnit(this.angleUnit));
        }

    }

    public void setToolTipText(String text) {

        this.decValue.setToolTipText(text);
        this.unit.setToolTipText(text);

    }

    public void setEditable(boolean editable) {

        this.editable = editable;

        this.decValue.setEditable(this.editable);

    }

    public void setUnits(String[] units) {

        // Only allowed if we're in editmode
        if (!this.editable) {
            return;
        }

        // If null is given we deactivate this function again
        if ((units == null) || (units.length == 0)) {
            this.units = null;
            return;
        }

        // Check if values in given Array are OK
        int[] checkedOK = new int[units.length];
        java.util.Arrays.fill(checkedOK, -1);
        int x = 0;
        for (int i = 0; i < units.length; i++) {
            if (Angle.isValidUnit(units[i])) {
                checkedOK[x++] = i;
            }
        }

        // Fill internal unit list
        this.units = new String[x];
        x = 0;
        for (int value : checkedOK) {
            if (value != -1) {
                this.units[x++] = units[value];
            }
        }

        // Recreate container, as this method is not called after constructor
        this.removeAll();
        this.createContainer();

    }

    public void setArcDecTransformation(boolean enabled) {

        // Only allowed if we're in editmode
        if (!this.editable) {
            return;
        }

        // Do this before the useArc state is changed
        this.setArcValues(); // Implicitly changes the this.angleValue field

        this.useArc = enabled;

        // Recreate container, as this method is not called after constructor
        this.removeAll();
        this.createContainer();

    }

    private void createContainer() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        this.setLayout(gridbag);

        if ((this.editable) && (this.useArc)) {
            ConstraintsBuilder.buildConstraints(constraints, 0, 0, 1, 1, 28, 100);
            constraints.fill = GridBagConstraints.HORIZONTAL;
            this.arcDegreeValue.setEditable(this.editable);
            gridbag.setConstraints(this.arcDegreeValue, constraints);
            this.add(this.arcDegreeValue);
            ConstraintsBuilder.buildConstraints(constraints, 1, 0, 1, 1, 5, 100);
            constraints.fill = GridBagConstraints.HORIZONTAL;
            JLabel arcDegreeLabel = new JLabel(EquPosition.DEC_DEG);
            gridbag.setConstraints(arcDegreeLabel, constraints);
            this.add(arcDegreeLabel);

            ConstraintsBuilder.buildConstraints(constraints, 2, 0, 1, 1, 28, 100);
            this.arcMinValue.setEditable(this.editable);
            gridbag.setConstraints(this.arcMinValue, constraints);
            this.add(this.arcMinValue);
            ConstraintsBuilder.buildConstraints(constraints, 3, 0, 1, 1, 5, 100);
            constraints.fill = GridBagConstraints.HORIZONTAL;
            JLabel arcMinLabel = new JLabel(EquPosition.DEC_MIN);
            gridbag.setConstraints(arcMinLabel, constraints);
            this.add(arcMinLabel);

            ConstraintsBuilder.buildConstraints(constraints, 4, 0, 1, 1, 28, 100);
            this.arcSecValue.setEditable(this.editable);
            gridbag.setConstraints(this.arcSecValue, constraints);
            this.add(this.arcSecValue);
            ConstraintsBuilder.buildConstraints(constraints, 5, 0, 1, 1, 5, 100);
            constraints.fill = GridBagConstraints.HORIZONTAL;
            JLabel arcSecLabel = new JLabel(EquPosition.DEC_SEC);
            gridbag.setConstraints(arcSecLabel, constraints);
            this.add(arcSecLabel);

        } else {
            ConstraintsBuilder.buildConstraints(constraints, 0, 0, 6, 1, 90, 100);
            constraints.fill = GridBagConstraints.HORIZONTAL;
            if (!Double.isNaN(this.angleValue)) {
                this.decValue.setText(this.getValue());
            }
            this.decValue.setEditable(this.editable);
            gridbag.setConstraints(this.decValue, constraints);
            this.add(this.decValue);

            ConstraintsBuilder.buildConstraints(constraints, 6, 0, 1, 1, 10, 100);
            constraints.fill = GridBagConstraints.HORIZONTAL;
            // Check if we're in editmode and a list of allowed units was given
            if ((this.editable) && (this.units != null) && (!this.useArc)) {
                for (String s : this.units) {
                    this.unitBox.addItem(this.getI18NUnit(s));
                }

                gridbag.setConstraints(this.unitBox, constraints);
                this.add(this.unitBox);
            } else {
                this.unit = new JLabel(this.getI18NUnit(this.angleUnit));
                gridbag.setConstraints(this.unit, constraints);
                this.add(this.unit);
            }
        }

    }

    private String getValue() {

        // Output format
        DecimalFormat df = new DecimalFormat("0.00");
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(dfs);

        return df.format(this.angleValue);

    }

    private void setArcValues() {

        Angle angle = this.getAngle();

        if (angle == null) {
            return;
        }

        // Use EquPosition for transformation (set dummy angle for RA)
        EquPosition ep = new EquPosition(new Angle(0, Angle.DEGREE), angle);
        String arcString = ep.getDec();

        String deg = arcString.substring(0, arcString.indexOf(EquPosition.DEC_DEG));
        String min = arcString.substring(arcString.indexOf(EquPosition.DEC_DEG) + 1,
                arcString.indexOf(EquPosition.DEC_MIN));
        String sec = arcString.substring(arcString.indexOf(EquPosition.DEC_MIN) + 1,
                arcString.indexOf(EquPosition.DEC_SEC));

        this.arcDegreeValue.setText(deg);
        this.arcMinValue.setText(min);
        this.arcSecValue.setText(sec);

    }

    private String getI18NUnit(String unit) {

        String result = this.bundle.getString(AngleContainer.UNIT_KEY_PREFIX + unit);

        unitI18Nmap.put(result, unit);

        return result;

    }

    private String getUnitFromI18NString(String I18N) {

        return (String) this.unitI18Nmap.get(I18N);

    }

    // ---------------------
    // Private inner classes --------------------------------------------------
    // ---------------------

    private static class AngleUnitBox extends JComboBox {

        /**
         *
         */
        private static final long serialVersionUID = 7549558268570152814L;
        private final List<String> list = new ArrayList<>();

        // ---------
        // JComboBox ----------------------------------------------------------
        // ---------

        void addItem(String angleUnit) {

            // Item already exists
            if (this.list.contains(angleUnit)) {
                this.setSelectedItem(angleUnit);
                return;
            }

            this.list.add(angleUnit);
            super.addItem(angleUnit);
            this.setSelectedItem(angleUnit);

        }

    }

}
