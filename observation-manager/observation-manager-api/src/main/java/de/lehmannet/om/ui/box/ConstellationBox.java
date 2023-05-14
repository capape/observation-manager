/*
 * ====================================================================
 * /box/ConstellationBox
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.box;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;

import de.lehmannet.om.Constellation;

public class ConstellationBox extends JComboBox<String> {

    /**
     *
     */
    private static final long serialVersionUID = -90389064765087999L;

    private static final String EMPTY_ENTRY = "----";

    public ConstellationBox(boolean useI18Nnames) {

        this.addEmptyItem();

        for (Constellation c : Constellation.values()) {
            this.addItem(c.name());
        }

        if (useI18Nnames) {
            this.setRenderer(new ConstellationRenderer());
        }

    }

    public Constellation getSelectedConstellation() {

        String cons = (String) this.getSelectedItem();
        if (ConstellationBox.EMPTY_ENTRY.equals(cons)) {
            return null;
        } else {
            return Constellation.valueOf(cons);
        }

    }

    public void setSelectedConstellation(Constellation constellation) {

        if (constellation == null) {
            this.selectEmptyItem();
            return;
        }

        this.setSelectedItem(constellation.name());

    }

    private void selectEmptyItem() {

        this.setSelectedItem(ConstellationBox.EMPTY_ENTRY);

    }

    private void addEmptyItem() {

        this.addItem(ConstellationBox.EMPTY_ENTRY);
        this.setSelectedItem(ConstellationBox.EMPTY_ENTRY);

    }

}

class ConstellationRenderer extends DefaultListCellRenderer {

    /**
     *
     */
    private static final long serialVersionUID = 2469467850811602651L;

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
            boolean cellHasFocus) {

        Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof Constellation) {
            Constellation constellation = (Constellation) value;
            this.setText(constellation.getDisplayName() + " (" + constellation.getAbbreviation() + ")");
        }

        return c;

    }

}
