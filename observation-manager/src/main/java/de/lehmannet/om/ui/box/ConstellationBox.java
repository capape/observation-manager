/* ====================================================================
 * /box/ConstellationBox
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.box;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;

import de.lehmannet.om.Constellation;

public class ConstellationBox extends JComboBox {

    private static final String EMPTY_ENTRY = "----";

    private static final Constellation cache[] = new Constellation[] { Constellation.ANDROMEDA, Constellation.ANTLIA,
            Constellation.APUS, Constellation.AQUARIUS, Constellation.AQUILA, Constellation.ARA, Constellation.ARIES,
            Constellation.AURIGA, Constellation.BOOTES, Constellation.CAELUM, Constellation.CAMELOPARDALIS,
            Constellation.CANCER, Constellation.CANES_VENATICI, Constellation.CANIS_MAIOR, Constellation.CANIS_MINOR,
            Constellation.CAPRICORNUS, Constellation.CARINA, Constellation.CASSIOPEIA, Constellation.CENTAURUS,
            Constellation.CEPHEUS, Constellation.CETUS, Constellation.CHAMAELEON, Constellation.CIRCINUS,
            Constellation.COLUMBA, Constellation.COMA_BERENICES, Constellation.CORONA_AUSTRALIS,
            Constellation.CORONA_BOREALIS, Constellation.CORVUS, Constellation.CRATER, Constellation.CRUX,
            Constellation.CYGNUS, Constellation.DELPHINUS, Constellation.DORADO, Constellation.DRACO,
            Constellation.EQUULEUS, Constellation.ERIDANUS, Constellation.FORNAX, Constellation.GEMINI,
            Constellation.GRUS, Constellation.HERCULES, Constellation.HOROLOGIUM, Constellation.HYDRA,
            Constellation.HYDRUS, Constellation.INDUS, Constellation.LACERTA, Constellation.LEO,
            Constellation.LEO_MINOR, Constellation.LEPUS, Constellation.LIBRA, Constellation.LUPUS, Constellation.LYNX,
            Constellation.LYRA, Constellation.MENSA, Constellation.MICROSCOPUS, Constellation.MONOCERUS,
            Constellation.MUSCA, Constellation.NORMA, Constellation.OCTANS, Constellation.OPHIUCHUS,
            Constellation.ORION, Constellation.PAVO, Constellation.PEGASUS, Constellation.PERSEUS,
            Constellation.PHOENIX, Constellation.PICTOR, Constellation.PISCES_AUSTRINUS, Constellation.PISCES,
            Constellation.PUPPIS, Constellation.PYXIS, Constellation.RETICULUM, Constellation.SAGITTA,
            Constellation.SAGITTARIUS, Constellation.SCORPIUS, Constellation.SCULPTOR, Constellation.SCUTUM,
            Constellation.SERPENS, Constellation.SEXTANS, Constellation.TAURUS, Constellation.TELESCOPIUM,
            Constellation.TRIANGULUM_AUSTRALIS, Constellation.TRIANGULUM, Constellation.TUCANA,
            Constellation.URSA_MAIOR, Constellation.URSA_MINOR, Constellation.VELA, Constellation.VIRGO,
            Constellation.VOLANS, Constellation.VULPECULA };

    public ConstellationBox(boolean useI18Nnames) {

        this.addEmptyItem();

        for (int i = 0; i < ConstellationBox.cache.length; i++) {
            super.addItem(ConstellationBox.cache[i]);
        }

        if (useI18Nnames) {
            this.setRenderer(new ConstellationRenderer());
        }

    }

    public Constellation getSelectedConstellation() {

        Object cons = super.getSelectedItem();
        if (ConstellationBox.EMPTY_ENTRY.equals(cons)) {
            return null;
        } else {
            return (Constellation) cons;
        }

    }

    /*
     * public String getSelectedConstellationAbbreviation() {
     * 
     * Object cons = super.getSelectedItem(); if(
     * ConstellationBox.EMPTY_ENTRY.equals(cons) ) { return null; } else { return
     * ((Constellation)cons).getAbbreviation(); }
     * 
     * }
     */

    public void setSelectedConstellation(Constellation constellation) {

        if (constellation == null) {
            this.selectEmptyItem();
            return;
        }

        for (int i = 0; i < ConstellationBox.cache.length; i++) {
            if (ConstellationBox.cache[i].equals(constellation)) {
                super.setSelectedItem(ConstellationBox.cache[i]);
                return;
            }
        }

        // Abbreviation not found
        this.selectEmptyItem();

    }

    /*
     * public static String getAbbreviation(String name) {
     * 
     * if( (name == null) || ("".equals(name.trim())) ) { return null; }
     * 
     * for(int i=0; i < ConstellationBox.cache.length; i++) { if(
     * ConstellationBox.cache[i].getName().toLowerCase().trim().equals(name.
     * toLowerCase().trim()) ) { return ConstellationBox.cache[i].getAbbreviation();
     * } }
     * 
     * return null;
     * 
     * }
     */

    /*
     * public static String getConstellationName(String abbreviation) {
     * 
     * if( (abbreviation == null) || ("".equals(abbreviation.trim())) ) { return
     * null; }
     * 
     * for(int i=0; i < ConstellationBox.cache.length; i++) { if(
     * ConstellationBox.cache[i].getAbbreviation().equals(abbreviation) ) { return
     * ConstellationBox.cache[i].getName(); } }
     * 
     * return null;
     * 
     * }
     */

    public void selectEmptyItem() {

        super.setSelectedItem(ConstellationBox.EMPTY_ENTRY);

    }

    public void addEmptyItem() {

        super.addItem(ConstellationBox.EMPTY_ENTRY);
        super.setSelectedItem(ConstellationBox.EMPTY_ENTRY);

    }

}

class ConstellationRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
            boolean cellHasFocus) {

        Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof Constellation) {
            Constellation constellation = (Constellation) value;
            super.setText(constellation.getDisplayName() + " (" + constellation.getAbbreviation() + ")");
        }

        return c;

    }

}
