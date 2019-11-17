/* ====================================================================
 * /container/SurfaceBrightnessContainer.java
 * 
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

import de.lehmannet.om.SurfaceBrightness;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.util.FloatUtil;

public class SurfaceBrightnessContainer extends Container {

    private final PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
            .getBundle("ObservationManager", Locale.getDefault());

    private static final String BUNDLE_UNIT_PREFIX = "SurfaceBrightness.unit.";

    /**
     * Prefix for calculated values
     */
    private static final String CALCULATION_INDICATOR = "~";

    private float value = Float.NaN;
    private String unit = SurfaceBrightness.MAGS_SQR_ARC_SEC;

    private boolean editable = false;

    // Stores all possible units (Key: Constant; Value: DisplayName)
    private final Map units = new HashMap();

    private JLabel Lunit = new JLabel();
    private final JComboBox unitBox = new JComboBox();
    private final JTextField valueField = new JTextField();

    public SurfaceBrightnessContainer(SurfaceBrightness sb, boolean editable, String[] sbUnits) {

        this.editable = editable;
        this.setUnits(sbUnits);
        this.setSurfaceBrightness(sb);
        this.createContainer();

    }

    public SurfaceBrightnessContainer(boolean editable, String[] sbUnits) {

        this.editable = editable;
        this.setUnits(sbUnits);
        this.createContainer();

    }

    public SurfaceBrightness getSurfaceBrightness() throws NumberFormatException {

        if ((this.valueField.getText() != null) && !("".equals(this.valueField.getText()))) {
            try {
                String valueText = this.valueField.getText();
                if (this.isCalculatedValue()) {
                    valueText = valueText.substring(1);
                }
                this.value = FloatUtil.parseFloat(valueText);
            } catch (NumberFormatException nfe) {
                throw new NumberFormatException(
                        "Given surface brightness " + this.valueField.getText() + " cannot be parsed.");
            }

            if (this.editable) {
                String unitLabel = (String) this.unitBox.getSelectedItem();
                this.unit = this.getUnitFromLabel(unitLabel);
            }

            return new SurfaceBrightness(this.value, this.unit);
        }

        return null;

    }

    public void setSurfaceBrightness(SurfaceBrightness sb) {

        if (sb != null) {
            this.unit = sb.getUnit();
            this.value = sb.getValue();

            this.valueField.setText(this.getValue());
            this.Lunit.setText(this.getUnitLabel(this.unit));
            this.unitBox.setSelectedItem(this.getUnitLabel(this.unit));
        } else {
            this.value = Float.NaN;
        }

    }

    public void setSurfaceBrightness(SurfaceBrightness sb, boolean calculatedValue) {

        this.setSurfaceBrightness(sb);
        if (calculatedValue) {
            this.valueField.setText(SurfaceBrightnessContainer.CALCULATION_INDICATOR + this.getValue());
            this.valueField.setToolTipText(this.bundle.getString("info.fst_BSB.calculated"));
        }

    }

    private boolean isCalculatedValue() {

        if (this.valueField.getText() != null) {
            return this.valueField.getText().startsWith(SurfaceBrightnessContainer.CALCULATION_INDICATOR);
        }

        return false;

    }

    public void setToolTipText(String text) {

        this.valueField.setToolTipText(text);
        if (!this.editable) {
            this.Lunit.setToolTipText(text);
        }

    }

    public void setEditable(boolean editable) {

        this.editable = editable;

        this.valueField.setEditable(this.editable);

    }

    private void setUnits(String[] units) {

        // Check if values in given Array are OK
        int[] checkedOK = new int[units.length];
        java.util.Arrays.fill(checkedOK, -1);
        int x = 0;
        for (int i = 0; i < units.length; i++) {
            if (SurfaceBrightness.isValidUnit(units[i])) {
                checkedOK[x++] = i;
            }
        }

        // Fill internal unit set
        x = 0;
        for (int item : checkedOK) {
            if (item != -1) {
                if (!this.units.containsKey(units[item])) { // Check whether unit already exists
                    this.units.put(units[item], this.getUnitLabel(units[item]));
                    this.unitBox.addItem(this.getUnitLabel(units[item]));
                    this.unitBox.setSelectedItem(this.getUnitLabel(units[item]));
                }
            }
        }

    }

    private void createContainer() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        super.setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 1, 1, 80, 100);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.valueField.setEditable(this.editable);
        gridbag.setConstraints(this.valueField, constraints);
        super.add(this.valueField);

        ConstraintsBuilder.buildConstraints(constraints, 1, 0, 1, 1, 20, 100);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        if (this.editable) {
            /*
             * Iterator iterator = this.units.values().iterator(); // Fill unit box with labels while(
             * iterator.hasNext() ) { this.unitBox.addItem(iterator.next()); }
             */
            gridbag.setConstraints(this.unitBox, constraints);
            super.add(this.unitBox);
        } else {
            this.Lunit = new JLabel(this.getUnitLabel(this.unit));
            gridbag.setConstraints(this.Lunit, constraints);
            super.add(this.Lunit);
        }

    }

    private String getValue() {

        // Output format
        DecimalFormat df = new DecimalFormat("0.00");
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(dfs);

        return df.format(this.value);

    }

    private String getUnitLabel(String unit) {

        return this.bundle.getString(SurfaceBrightnessContainer.BUNDLE_UNIT_PREFIX + unit);

    }

    private String getUnitFromLabel(String label) {

        if (!this.units.containsValue(label)) {
            return null;
        }

        Iterator iterator = this.units.keySet().iterator();
        String currentKey = null;
        while (iterator.hasNext()) {
            currentKey = (String) iterator.next();
            if (this.units.get(currentKey).equals(label)) {
                return currentKey;
            }
        }

        return null;

    }

}
