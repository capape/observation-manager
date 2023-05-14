/*
 * ====================================================================
 * /Constellation.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Constellation enum represents all 88 IAU constellations. The constellation name (and abbreviation) used in this class
 * is in latin.
 *
 * @author doergn@users.sourceforge.net capapecd cagil
 * @since 2.0
 */
public enum Constellation {

    // Each constellation is created once in a static manner
    // @formatter:off
    ANDROMEDA("And", "Andromeda"),
    ANTLIA("Ant", "Antlia"),
    APUS("Aps", "Apus"),
    AQUARIUS("Aqr", "Aquarius"),
    AQUILA("Aql", "Aquila"),
    ARA("Ara", "Ara"),
    ARIES("Ari", "Aries"),
    AURIGA("Aur", "Auriga"),
    BOOTES("Boo", "Bootes"),
    CAELUM("Cae", "Caelum"),
    CAMELOPARDALIS("Cam", "Camelopardalis"),
    CANCER("Cnc", "Cancer"),
    CANES_VENATICI("CVn", "Canes Venatici"),
    CANIS_MAIOR("CMa", "Canis Maior"),
    CANIS_MINOR("CMi", "Canis Minor"),
    CAPRICORNUS("Cap", "Capricornus"),
    CARINA("Car", "Carina"),
    CASSIOPEIA("Cas", "Cassiopeia"),
    CENTAURUS("Cen", "Centaurus"),
    CEPHEUS("Cep", "Cepheus"),
    CETUS("Cet", "Cetus"),
    CHAMAELEON("Cha", "Chamaeleon"),
    CIRCINUS("Cir", "Circinus"),
    COLUMBA("Col", "Columba"),
    COMA_BERENICES("Com", "Coma Berenices"),
    CORONA_AUSTRALIS("CrA", "Corona Australis"),
    CORONA_BOREALIS("CrB", "Corona Borealis"),
    CORVUS("Crv", "Corvus"),
    CRATER("Crt", "Crater"),
    CRUX("Cru", "Crux"),
    CYGNUS("Cyg", "Cygnus"),
    DELPHINUS("Del", "Delphinus"),
    DORADO("Dor", "Dorado"),
    DRACO("Dra", "Draco"),
    EQUULEUS("Equ", "Equuleus"),
    ERIDANUS("Eri", "Eridanus"),
    FORNAX("For", "Fornax"),
    GEMINI("Gem", "Gemini"),
    GRUS("Gru", "Grus"),
    HERCULES("Her", "Hercules"),
    HOROLOGIUM("Hor", "Horologium"),
    HYDRA("Hya", "Hydra"),
    HYDRUS("Hyi", "Hydrus"),
    INDUS("Ind", "Indus"),
    LACERTA("Lac", "Lacerta"),
    LEO("Leo", "Leo"),
    LEO_MINOR("LMi", "Leo Minor"),
    LEPUS("Lep", "Lepus"),
    LIBRA("Lib", "Libra"),
    LUPUS("Lup", "Lupus"),
    LYNX("Lyn", "Lynx"),
    LYRA("Lyr", "Lyra"),
    MENSA("Men", "Mensa"),
    MICROSCOPUS("Mic", "Microscopus"),
    MONOCERUS("Mon", "Monocerus"),
    MUSCA("Mus", "Musca"),
    NORMA("Nor", "Norma"),
    OCTANS("Oct", "Octans"),
    OPHIUCHUS("Oph", "Ophiuchus"),
    ORION("Ori", "Orion"),
    PAVO("Pav", "Pavo"),
    PEGASUS("Peg", "Pegasus"),
    PERSEUS("Per", "Perseus"),
    PHOENIX("Phe", "Phoenix"),
    PICTOR("Pic", "Pictor"),
    PISCES_AUSTRINUS("PsA", "Pisces Austrinus"),
    PISCES("Psc", "Pisces"),
    PUPPIS("Pup", "Puppis"),
    PYXIS("Pyx", "Pyxis"),
    RETICULUM("Ret", "Reticulum"),
    SAGITTA("Sge", "Sagitta"),
    SAGITTARIUS("Sgr", "Sagittarius"),
    SCORPIUS("Sco", "Scorpius"),
    SCULPTOR("Scl", "Sculptor"),
    SCUTUM("Sct", "Scutum"),
    SERPENS("Ser", "Serpens"),
    SEXTANS("Sex", "Sextans"),
    TAURUS("Tau", "Taurus"),
    TELESCOPIUM("Tel", "Telescopium"),
    TRIANGULUM_AUSTRALIS("TrA", "Triangulum Australis"),
    TRIANGULUM("Tri", "Triangulum"),
    TUCANA("Tuc", "Tucana"),
    URSA_MAIOR("UMa", "Ursa Maior"),
    URSA_MINOR("UMi", "Ursa Minor"),
    VELA("Vel", "Vela"),
    VIRGO("Vir", "Virgo"),
    VOLANS("Vol", "Volans"),
    VULPECULA("Vul", "Vulpecula");
    // @formatter:on

    private ResourceBundle bundle = ResourceBundle.getBundle("Constellations", Locale.getDefault());

    // Constellation abbreviation in latin
    private String abbreviation;

    // Constellation name in latin
    private String name;

    private Constellation(String abb, String name) {
        this.abbreviation = abb;
        this.name = name;
    }

    public static Constellation getConstellationByName(String data) {

        if (data == null) {
            throw new IllegalArgumentException("Invalid constellation");
        }
        final String toSearch = data.trim().toUpperCase(Locale.getDefault());

        for (Constellation item : Constellation.values()) {
            if (item.getName().equalsIgnoreCase(toSearch)) {
                return item;
            }
        }

        throw new IllegalArgumentException("Invalid constellation");

    }

    public static Constellation getConstellationByAbb(String data) {

        if (data == null) {
            throw new IllegalArgumentException("Invalid constellation");
        }
        final String toSearch = data.trim().toUpperCase(Locale.getDefault());

        for (Constellation item : Constellation.values()) {
            if (item.getAbbreviation().equalsIgnoreCase(toSearch)) {
                return item;
            }
        }

        throw new IllegalArgumentException("Invalid constellation");

    }

    public static Constellation getConstellationByAbbOrName(String data) {

        if (data == null) {
            throw new IllegalArgumentException("Invalid constellation");
        }
        final String toSearch = data.trim().toUpperCase(Locale.getDefault());

        for (Constellation item : Constellation.values()) {
            if (item.getAbbreviation().equalsIgnoreCase(toSearch) || item.getName().equalsIgnoreCase(toSearch)) {
                return item;
            }
        }

        throw new IllegalArgumentException("Invalid constellation: " + data);

    }

    /**
     * Returns a string representing this constellation as string.<br>
     *
     * @return This constellation as java.lang.String
     */
    @Override
    public String toString() {

        return String.format("%s (%s)", this.getName(),
 this.getAbbreviation());

    }

    /**
     * Returns a I18N representation name for that Constellation (if available)
     */
    public String getDisplayName() {

        String result = this.getName(); // Use non I18N name as default

        try {
            if (!this.bundle.getLocale().equals(Locale.getDefault())) { // Check whether language has changed
                this.bundle = ResourceBundle.getBundle("Constellations", Locale.getDefault());
            }
            result = this.bundle.getString(this.getAbbreviation());
        } catch (MissingResourceException mre1) { // Try with name as key
            try {
                result = this.bundle.getString(this.getName());
            } catch (MissingResourceException mre2) {
                // OK, seems there's really no I18N for this...
            }
        }

        return result; // In worst case the non I18N name is returned

    }

    /**
     * Returns the name of this constellation.<br>
     *
     * @return The name of this constellation
     */
    public String getName() {

        return this.name;

    }

    /**
     * Returns the abbreviation of this constellation.<br>
     *
     * @return The abbreviation of this constellation
     */
    public String getAbbreviation() {

        return this.abbreviation;

    }

}
