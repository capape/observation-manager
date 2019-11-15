/* ====================================================================
 * /Constellation.java
 *
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Constellation class represents all 88 IAU constellations. The constellation
 * name (and abbreviation) used in this class is in latin.
 *
 * @author doergn@users.sourceforge.net
 * @since 2.0
 */
public class Constellation {

    // ---------
    // Constants --------------------------------------------------------------
    // ---------

    // Each constellation is created once in a static manner

    public static final Constellation ANDROMEDA = new Constellation("And", "Andromeda");
    public static final Constellation ANTLIA = new Constellation("Ant", "Antlia");
    public static final Constellation APUS = new Constellation("Aps", "Apus");
    public static final Constellation AQUARIUS = new Constellation("Aqr", "Aquarius");
    public static final Constellation AQUILA = new Constellation("Aql", "Aquila");
    public static final Constellation ARA = new Constellation("Ara", "Ara");
    public static final Constellation ARIES = new Constellation("Ari", "Aries");
    public static final Constellation AURIGA = new Constellation("Aur", "Auriga");
    public static final Constellation BOOTES = new Constellation("Boo", "Bootes");
    public static final Constellation CAELUM = new Constellation("Cae", "Caelum");
    public static final Constellation CAMELOPARDALIS = new Constellation("Cam", "Camelopardalis");
    public static final Constellation CANCER = new Constellation("Cnc", "Cancer");
    public static final Constellation CANES_VENATICI = new Constellation("CVn", "Canes Venatici");
    public static final Constellation CANIS_MAIOR = new Constellation("CMa", "Canis Maior");
    public static final Constellation CANIS_MINOR = new Constellation("CMi", "Canis Minor");
    public static final Constellation CAPRICORNUS = new Constellation("Cap", "Capricornus");
    public static final Constellation CARINA = new Constellation("Car", "Carina");
    public static final Constellation CASSIOPEIA = new Constellation("Cas", "Cassiopeia");
    public static final Constellation CENTAURUS = new Constellation("Cen", "Centaurus");
    public static final Constellation CEPHEUS = new Constellation("Cep", "Cepheus");
    public static final Constellation CETUS = new Constellation("Cet", "Cetus");
    public static final Constellation CHAMAELEON = new Constellation("Cha", "Chamaeleon");
    public static final Constellation CIRCINUS = new Constellation("Cir", "Circinus");
    public static final Constellation COLUMBA = new Constellation("Col", "Columba");
    public static final Constellation COMA_BERENICES = new Constellation("Com", "Coma Berenices");
    public static final Constellation CORONA_AUSTRALIS = new Constellation("CrA", "Corona Australis");
    public static final Constellation CORONA_BOREALIS = new Constellation("CrB", "Corona Borealis");
    public static final Constellation CORVUS = new Constellation("Crv", "Corvus");
    public static final Constellation CRATER = new Constellation("Crt", "Crater");
    public static final Constellation CRUX = new Constellation("Cru", "Crux");
    public static final Constellation CYGNUS = new Constellation("Cyg", "Cygnus");
    public static final Constellation DELPHINUS = new Constellation("Del", "Delphinus");
    public static final Constellation DORADO = new Constellation("Dor", "Dorado");
    public static final Constellation DRACO = new Constellation("Dra", "Draco");
    public static final Constellation EQUULEUS = new Constellation("Equ", "Equuleus");
    public static final Constellation ERIDANUS = new Constellation("Eri", "Eridanus");
    public static final Constellation FORNAX = new Constellation("For", "Fornax");
    public static final Constellation GEMINI = new Constellation("Gem", "Gemini");
    public static final Constellation GRUS = new Constellation("Gru", "Grus");
    public static final Constellation HERCULES = new Constellation("Her", "Hercules");
    public static final Constellation HOROLOGIUM = new Constellation("Hor", "Horologium");
    public static final Constellation HYDRA = new Constellation("Hya", "Hydra");
    public static final Constellation HYDRUS = new Constellation("Hyi", "Hydrus");
    public static final Constellation INDUS = new Constellation("Ind", "Indus");
    public static final Constellation LACERTA = new Constellation("Lac", "Lacerta");
    public static final Constellation LEO = new Constellation("Leo", "Leo");
    public static final Constellation LEO_MINOR = new Constellation("LMi", "Leo Minor");
    public static final Constellation LEPUS = new Constellation("Lep", "Lepus");
    public static final Constellation LIBRA = new Constellation("Lib", "Libra");
    public static final Constellation LUPUS = new Constellation("Lup", "Lupus");
    public static final Constellation LYNX = new Constellation("Lyn", "Lynx");
    public static final Constellation LYRA = new Constellation("Lyr", "Lyra");
    public static final Constellation MENSA = new Constellation("Men", "Mensa");
    public static final Constellation MICROSCOPUS = new Constellation("Mic", "Microscopus");
    public static final Constellation MONOCERUS = new Constellation("Mon", "Monocerus");
    public static final Constellation MUSCA = new Constellation("Mus", "Musca");
    public static final Constellation NORMA = new Constellation("Nor", "Norma");
    public static final Constellation OCTANS = new Constellation("Oct", "Octans");
    public static final Constellation OPHIUCHUS = new Constellation("Oph", "Ophiuchus");
    public static final Constellation ORION = new Constellation("Ori", "Orion");
    public static final Constellation PAVO = new Constellation("Pav", "Pavo");
    public static final Constellation PEGASUS = new Constellation("Peg", "Pegasus");
    public static final Constellation PERSEUS = new Constellation("Per", "Perseus");
    public static final Constellation PHOENIX = new Constellation("Phe", "Phoenix");
    public static final Constellation PICTOR = new Constellation("Pic", "Pictor");
    public static final Constellation PISCES_AUSTRINUS = new Constellation("PsA", "Pisces Austrinus");
    public static final Constellation PISCES = new Constellation("Psc", "Pisces");
    public static final Constellation PUPPIS = new Constellation("Pup", "Puppis");
    public static final Constellation PYXIS = new Constellation("Pyx", "Pyxis");
    public static final Constellation RETICULUM = new Constellation("Ret", "Reticulum");
    public static final Constellation SAGITTA = new Constellation("Sge", "Sagitta");
    public static final Constellation SAGITTARIUS = new Constellation("Sgr", "Sagittarius");
    public static final Constellation SCORPIUS = new Constellation("Sco", "Scorpius");
    public static final Constellation SCULPTOR = new Constellation("Scl", "Sculptor");
    public static final Constellation SCUTUM = new Constellation("Sct", "Scutum");
    public static final Constellation SERPENS = new Constellation("Ser", "Serpens");
    public static final Constellation SEXTANS = new Constellation("Sex", "Sextans");
    public static final Constellation TAURUS = new Constellation("Tau", "Taurus");
    public static final Constellation TELESCOPIUM = new Constellation("Tel", "Telescopium");
    public static final Constellation TRIANGULUM_AUSTRALIS = new Constellation("TrA", "Triangulum Australis");
    public static final Constellation TRIANGULUM = new Constellation("Tri", "Triangulum");
    public static final Constellation TUCANA = new Constellation("Tuc", "Tucana");
    public static final Constellation URSA_MAIOR = new Constellation("UMa", "Ursa Maior");
    public static final Constellation URSA_MINOR = new Constellation("UMi", "Ursa Minor");
    public static final Constellation VELA = new Constellation("Vel", "Vela");
    public static final Constellation VIRGO = new Constellation("Vir", "Virgo");
    public static final Constellation VOLANS = new Constellation("Vol", "Volans");
    public static final Constellation VULPECULA = new Constellation("Vul", "Vulpecula");

    private PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle.getBundle("Constellations",
            Locale.getDefault());

    // ------------------
    // Instance Variables -----------------------------------------------------
    // ------------------

    // Constellation abbreviation in latin
    private String abbreviation = null;

    // Constellation name in latin
    private String name = null;

    // ------------
    // Constructors -----------------------------------------------------------
    // ------------

    // ------------------------------------------------------------------------
    /*
     * Private constructor. Use one of the public predefinied constants or use
     * getInstance(String) method to create constellation
     */
    private Constellation(String abb, String name) {

        this.abbreviation = abb;
        this.name = name;

    }

    // ---------------------
    // Public static methods --------------------------------------------------
    // ---------------------

    // ------------------------------------------------------------------------
    /**
     * Returns an instance of a Constellation from a given String.<br>
     * If a specific Constellation is requested, please use the public static
     * constellations provided.
     *
     * @param name The name or abbreviation of the requested constellation in latin
     * @return A Constellation object if the passed name matches a latin
     *         constellation name or abbreviation. Might return <code>null<code> if
     *         no matching constellation could be found.
     */
    public static Constellation getInstance(String name) {

        if ((name == null) || ("".equals(name.trim()))) {
            return null;
        }

        // Format name
        name = name.trim();
        name = name.toLowerCase();

        // Try to determin the constellation
        if ((name.equals("andromeda")) || (name.equals("and"))) {
            return Constellation.ANDROMEDA;
        } else if ((name.equals("antlia")) || (name.equals("ant"))) {
            return Constellation.ANTLIA;
        } else if ((name.equals("apus")) || (name.equals("aps"))) {
            return Constellation.APUS;
        } else if ((name.equals("aquarius")) || (name.equals("aqr"))) {
            return Constellation.AQUARIUS;
        } else if ((name.equals("aquila")) || (name.equals("aql"))) {
            return Constellation.AQUILA;
        } else if (name.equals("ara")) {
            return Constellation.ARA;
        } else if ((name.equals("aries")) || (name.equals("ari"))) {
            return Constellation.ARIES;
        } else if ((name.equals("auriga")) || (name.equals("aur"))) {
            return Constellation.AURIGA;
        } else if ((name.equals("bootes")) || (name.equals("boo"))) {
            return Constellation.BOOTES;
        } else if ((name.equals("caelum")) || (name.equals("cae"))) {
            return Constellation.CAELUM;
        } else if ((name.equals("camelopardalis")) || (name.equals("cam"))) {
            return Constellation.CAMELOPARDALIS;
        } else if ((name.equals("cancer")) || (name.equals("cnc"))) {
            return Constellation.CANCER;
        } else if ((name.equals("canes venatici")) || (name.equals("cvn"))) {
            return Constellation.CANES_VENATICI;
        } else if ((name.equals("canis maior")) || (name.equals("cma"))) {
            return Constellation.CANIS_MAIOR;
        } else if ((name.equals("canis minor")) || (name.equals("cmi"))) {
            return Constellation.CANIS_MINOR;
        } else if ((name.equals("capricornus")) || (name.equals("cap"))) {
            return Constellation.CAPRICORNUS;
        } else if ((name.equals("carina")) || (name.equals("car"))) {
            return Constellation.CARINA;
        } else if ((name.equals("cassiopeia")) || (name.equals("cas"))) {
            return Constellation.CASSIOPEIA;
        } else if ((name.equals("centaurus")) || (name.equals("cen"))) {
            return Constellation.CENTAURUS;
        } else if ((name.equals("cepheus")) || (name.equals("cep"))) {
            return Constellation.CEPHEUS;
        } else if ((name.equals("cetus")) || (name.equals("cet"))) {
            return Constellation.CETUS;
        } else if ((name.equals("chamaeleon")) || (name.equals("cha"))) {
            return Constellation.CHAMAELEON;
        } else if ((name.equals("circinus")) || (name.equals("cir"))) {
            return Constellation.CIRCINUS;
        } else if ((name.equals("columba")) || (name.equals("col"))) {
            return Constellation.COLUMBA;
        } else if ((name.equals("coma berenices")) || (name.equals("com"))) {
            return Constellation.COMA_BERENICES;
        } else if ((name.equals("corona australis")) || (name.equals("cra"))) {
            return Constellation.CORONA_AUSTRALIS;
        } else if ((name.equals("corona borealis")) || (name.equals("crb"))) {
            return Constellation.CORONA_BOREALIS;
        } else if ((name.equals("corvus")) || (name.equals("crv"))) {
            return Constellation.CORVUS;
        } else if ((name.equals("crater")) || (name.equals("crt"))) {
            return Constellation.CRATER;
        } else if ((name.equals("crux")) || (name.equals("cru"))) {
            return Constellation.CRUX;
        } else if ((name.equals("cygnus")) || (name.equals("cyg"))) {
            return Constellation.CYGNUS;
        } else if ((name.equals("delphinus")) || (name.equals("del"))) {
            return Constellation.DELPHINUS;
        } else if ((name.equals("dorado")) || (name.equals("dor"))) {
            return Constellation.DORADO;
        } else if ((name.equals("draco")) || (name.equals("dra"))) {
            return Constellation.DRACO;
        } else if ((name.equals("equuleus")) || (name.equals("equ"))) {
            return Constellation.EQUULEUS;
        } else if ((name.equals("eridanus")) || (name.equals("eri"))) {
            return Constellation.ERIDANUS;
        } else if ((name.equals("fornax")) || (name.equals("for"))) {
            return Constellation.FORNAX;
        } else if ((name.equals("gemini")) || (name.equals("gem"))) {
            return Constellation.GEMINI;
        } else if ((name.equals("grus")) || (name.equals("gru"))) {
            return Constellation.GRUS;
        } else if ((name.equals("hercules")) || (name.equals("her"))) {
            return Constellation.HERCULES;
        } else if ((name.equals("horologium")) || (name.equals("hor"))) {
            return Constellation.HOROLOGIUM;
        } else if ((name.equals("hydra")) || (name.equals("hya"))) {
            return Constellation.HYDRA;
        } else if ((name.equals("hydrus")) || (name.equals("hyi"))) {
            return Constellation.HYDRUS;
        } else if ((name.equals("indus")) || (name.equals("ind"))) {
            return Constellation.INDUS;
        } else if ((name.equals("lacerta")) || (name.equals("lac"))) {
            return Constellation.LACERTA;
        } else if (name.equals("leo")) {
            return Constellation.LEO;
        } else if ((name.equals("leo minor")) || (name.equals("lmi"))) {
            return Constellation.LEO_MINOR;
        } else if ((name.equals("lepus")) || (name.equals("lep"))) {
            return Constellation.LEPUS;
        } else if ((name.equals("libra")) || (name.equals("lib"))) {
            return Constellation.LIBRA;
        } else if ((name.equals("lupus")) || (name.equals("lup"))) {
            return Constellation.LUPUS;
        } else if ((name.equals("lynx")) || (name.equals("lyn"))) {
            return Constellation.LYNX;
        } else if ((name.equals("lyra")) || (name.equals("lyr"))) {
            return Constellation.LYRA;
        } else if ((name.equals("mensa")) || (name.equals("men"))) {
            return Constellation.MENSA;
        } else if ((name.equals("microscopus")) || (name.equals("mic"))) {
            return Constellation.MICROSCOPUS;
        } else if ((name.equals("monocerus")) || (name.equals("mon"))) {
            return Constellation.MONOCERUS;
        } else if ((name.equals("musca")) || (name.equals("mus"))) {
            return Constellation.MUSCA;
        } else if ((name.equals("norma")) || (name.equals("nor"))) {
            return Constellation.NORMA;
        } else if ((name.equals("octans")) || (name.equals("oct"))) {
            return Constellation.OCTANS;
        } else if ((name.equals("ophiuchus")) || (name.equals("oph"))) {
            return Constellation.OPHIUCHUS;
        } else if ((name.equals("orion")) || (name.equals("ori"))) {
            return Constellation.ORION;
        } else if ((name.equals("pavo")) || (name.equals("pav"))) {
            return Constellation.PAVO;
        } else if ((name.equals("pegasus")) || (name.equals("peg"))) {
            return Constellation.PEGASUS;
        } else if ((name.equals("perseus")) || (name.equals("per"))) {
            return Constellation.PERSEUS;
        } else if ((name.equals("phoenix")) || (name.equals("phe"))) {
            return Constellation.PHOENIX;
        } else if ((name.equals("pictor")) || (name.equals("pic"))) {
            return Constellation.PICTOR;
        } else if ((name.equals("pisces austrinus")) || (name.equals("psa"))) {
            return Constellation.PISCES_AUSTRINUS;
        } else if ((name.equals("pisces")) || (name.equals("psc"))) {
            return Constellation.PISCES;
        } else if ((name.equals("puppis")) || (name.equals("pup"))) {
            return Constellation.PUPPIS;
        } else if ((name.equals("pyxis")) || (name.equals("pyx"))) {
            return Constellation.PYXIS;
        } else if ((name.equals("reticulum")) || (name.equals("ret"))) {
            return Constellation.RETICULUM;
        } else if ((name.equals("sagitta")) || (name.equals("sge"))) {
            return Constellation.SAGITTA;
        } else if ((name.equals("sagittarius")) || (name.equals("sgr"))) {
            return Constellation.SAGITTARIUS;
        } else if ((name.equals("scorpius")) || (name.equals("sco"))) {
            return Constellation.SCORPIUS;
        } else if ((name.equals("sculptor")) || (name.equals("scl"))) {
            return Constellation.SCULPTOR;
        } else if ((name.equals("scutum")) || (name.equals("sct"))) {
            return Constellation.SCUTUM;
        } else if ((name.equals("serpens")) || (name.equals("ser"))) {
            return Constellation.SERPENS;
        } else if ((name.equals("sextans")) || (name.equals("sex"))) {
            return Constellation.SEXTANS;
        } else if ((name.equals("taurus")) || (name.equals("tau"))) {
            return Constellation.TAURUS;
        } else if ((name.equals("telescopium")) || (name.equals("tel"))) {
            return Constellation.TELESCOPIUM;
        } else if ((name.equals("triangulum australis")) || (name.equals("tra"))) {
            return Constellation.TRIANGULUM_AUSTRALIS;
        } else if ((name.equals("triangulum")) || (name.equals("tri"))) {
            return Constellation.TRIANGULUM;
        } else if ((name.equals("tucana")) || (name.equals("tuc"))) {
            return Constellation.TUCANA;
        } else if ((name.equals("ursa maior")) || (name.equals("uma"))) {
            return Constellation.URSA_MAIOR;
        } else if ((name.equals("ursa minor")) || (name.equals("umi"))) {
            return Constellation.URSA_MINOR;
        } else if ((name.equals("vela")) ) {
            return Constellation.VELA;
        } else if ((name.equals("virgo")) || (name.equals("vir"))) {
            return Constellation.VIRGO;
        } else if ((name.equals("volans")) || (name.equals("vol"))) {
            return Constellation.VOLANS;
        } else if ((name.equals("vulpecula")) || (name.equals("vul"))) {
            return Constellation.VULPECULA;
        }

        return null;

    }

    // --------------
    // Public methods ---------------------------------------------------------
    // --------------

    // ------------------------------------------------------------------------
    /**
     * Returns a string representing this constellation as string.<br>
     *
     * @return This constellation as java.lang.String
     */
    @Override
    public String toString() {

        return this.getName() + " (" + this.getAbbreviation() + ")";

    }

    // ------------------------------------------------------------------------
    /**
     * Returns a I18N representation name for that Constellation (if available)
     */
    public String getDisplayName() {

        String result = this.getName(); // Use non I18N name as default

        try {
            if (!this.bundle.getLocale().equals(Locale.getDefault())) { // Check whether language has changed
                this.bundle = (PropertyResourceBundle) ResourceBundle.getBundle("Constellations", Locale.getDefault());
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

    // ------------------------------------------------------------------------
    /**
     * Checks whether this constellation and the given object are equal.<br>
     *
     * @param o An object to compare against this constellation
     * @return <code>true</code> if the passed object is an instance of
     *         de.lehmannet.om.Constellation and the passed constellation and this
     *         instance have the same abbreviation.
     */
    @Override
    public boolean equals(Object o) {

        if (o instanceof Constellation) {
            return this.abbreviation.toLowerCase().trim()
                    .equals(((Constellation) o).getAbbreviation().toLowerCase().trim());
        }

        return false;

    }

    // ------------------------------------------------------------------------
    /**
     * Returns a hash code for this constellation.<br>
     *
     * @return A hash code for this constellation
     */
    @Override
    public int hashCode() {

        return this.abbreviation.hashCode();

    }

    // ------------------------------------------------------------------------
    /**
     * Returns the name of this constellation.<br>
     *
     * @return The name of this constellation
     */
    public String getName() {

        return this.name;

    }

    // ------------------------------------------------------------------------
    /**
     * Returns the abbreviation of this constellation.<br>
     *
     * @return The abbreviation of this constellation
     */
    public String getAbbreviation() {

        return this.abbreviation;

    }

}
