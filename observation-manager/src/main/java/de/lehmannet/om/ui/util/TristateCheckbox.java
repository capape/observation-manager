/* ====================================================================
 * /util/TristateCheckbox.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.util;

import java.awt.GridLayout;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class TristateCheckbox extends JPanel {

    private final PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
            .getBundle("ObservationManager", Locale.getDefault());

    private JRadioButton trueValue = new JRadioButton();
    private JRadioButton falseValue = new JRadioButton();
    private JRadioButton naValue = new JRadioButton();

    private ButtonGroup group = new ButtonGroup();

    public TristateCheckbox() {

        super(new GridLayout(2, 0));

        this.group.add(this.naValue);
        this.group.add(this.trueValue);
        this.group.add(this.falseValue);

        this.naValue.setSelected(true);

        super.add(new JLabel(this.bundle.getString("checkbox.label.false")));
        super.add(new JLabel(this.bundle.getString("checkbox.label.na")));
        super.add(new JLabel(this.bundle.getString("checkbox.label.true")));

        super.add(this.falseValue);
        super.add(this.naValue);
        super.add(this.trueValue);

        super.setVisible(true);

    }

    public void setEditable(boolean editable) {

        this.trueValue.setEnabled(editable);
        this.falseValue.setEnabled(editable);
        this.naValue.setEnabled(editable);

    }

    public boolean isFalseSelected() {

        return this.falseValue.isSelected();

    }

    public boolean isNASelected() {

        return this.naValue.isSelected();

    }

    public boolean isTrueSelected() {

        return this.trueValue.isSelected();

    }

    public void setTrueSelected() {

        this.trueValue.setSelected(true);

    }

    public void setFalseSelected() {

        this.falseValue.setSelected(true);

    }

    public void setNASelected() {

        this.naValue.setSelected(true);

    }

}
