/* ====================================================================
 * /util/TristateCheckbox.java
 *
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.util;

import java.awt.GridLayout;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class TristateCheckbox extends JPanel {

    private final JRadioButton trueValue = new JRadioButton();
    private final JRadioButton falseValue = new JRadioButton();
    private final JRadioButton naValue = new JRadioButton();

    public TristateCheckbox() {

        super(new GridLayout(2, 0));

        ButtonGroup group = new ButtonGroup();
        group.add(this.naValue);
        group.add(this.trueValue);
        group.add(this.falseValue);

        this.naValue.setSelected(true);

        ResourceBundle bundle = LocaleToolsFactory.appInstance().getBundle("ObservationManager", Locale.getDefault());
        this.add(new JLabel(bundle.getString("checkbox.label.false")));
        this.add(new JLabel(bundle.getString("checkbox.label.na")));
        this.add(new JLabel(bundle.getString("checkbox.label.true")));

        this.add(this.falseValue);
        this.add(this.naValue);
        this.add(this.trueValue);

        this.setVisible(true);

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

        return !this.naValue.isSelected();

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
