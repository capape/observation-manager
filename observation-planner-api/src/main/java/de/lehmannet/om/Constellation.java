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
 * Constellation class represents all 88 IAU constellations. The constellation name (and abbreviation) used in this
 * class is in latin.
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
     * Private constructor. Use one of the public predefinied constants or use getInstance(String) method to create
     * constellation
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
     * If a specific Constellation is requested, please use the public static constellations provided.
     *
     * @param paramName
     *            The name or abbreviation of the requested constellation in latin
     * @return A Constellation object if the passed name matches a latin constellation name or abbreviation. Might
     *         return <code>null<code> if no matching constellation could be found.
     */
    public static Constellation getInstance(String paramName) {

        if (paramName == null || "".equals(paramName.trim())) {
            return null;
        }

        // Format name
        String name = paramName.trim();
        name = name.toLowerCase();

        // Try to determin the constellation
        switch (name) {
        case "andromeda":
        case "and":
            return Constellation.ANDROMEDA;
        case "antlia":
        case "ant":
            return Constellation.ANTLIA;
        case "apus":
        case "aps":
            return Constellation.APUS;
        case "aquarius":
        case "aqr":
            return Constellation.AQUARIUS;
        case "aquila":
        case "aql":
            return Constellation.AQUILA;
        case "ara":
            return Constellation.ARA;
        case "aries":
        case "ari":
            return Constellation.ARIES;
        case "auriga":
        case "aur":
            return Constellation.AURIGA;
        case "bootes":
        case "boo":
            return Constellation.BOOTES;
        case "caelum":
        case "cae":
            return Constellation.CAELUM;
        case "camelopardalis":
        case "cam":
            return Constellation.CAMELOPARDALIS;
        case "cancer":
        case "cnc":
            return Constellation.CANCER;
        case "canes venatici":
        case "cvn":
            return Constellation.CANES_VENATICI;
        case "canis maior":
        case "cma":
            return Constellation.CANIS_MAIOR;
        case "canis minor":
        case "cmi":
            return Constellation.CANIS_MINOR;
        case "capricornus":
        case "cap":
            return Constellation.CAPRICORNUS;
        case "carina":
        case "car":
            return Constellation.CARINA;
        case "cassiopeia":
        case "cas":
            return Constellation.CASSIOPEIA;
        case "centaurus":
        case "cen":
            return Constellation.CENTAURUS;
        case "cepheus":
        case "cep":
            return Constellation.CEPHEUS;
        case "cetus":
        case "cet":
            return Constellation.CETUS;
        case "chamaeleon":
        case "cha":
            return Constellation.CHAMAELEON;
        case "circinus":
        case "cir":
            return Constellation.CIRCINUS;
        case "columba":
        case "col":
            return Constellation.COLUMBA;
        case "coma berenices":
        case "com":
            return Constellation.COMA_BERENICES;
        case "corona australis":
        case "cra":
            return Constellation.CORONA_AUSTRALIS;
        case "corona borealis":
        case "crb":
            return Constellation.CORONA_BOREALIS;
        case "corvus":
        case "crv":
            return Constellation.CORVUS;
        case "crater":
        case "crt":
            return Constellation.CRATER;
        case "crux":
        case "cru":
            return Constellation.CRUX;
        case "cygnus":
        case "cyg":
            return Constellation.CYGNUS;
        case "delphinus":
        case "del":
            return Constellation.DELPHINUS;
        case "dorado":
        case "dor":
            return Constellation.DORADO;
        case "draco":
        case "dra":
            return Constellation.DRACO;
        case "equuleus":
        case "equ":
            return Constellation.EQUULEUS;
        case "eridanus":
        case "eri":
            return Constellation.ERIDANUS;
        case "fornax":
        case "for":
            return Constellation.FORNAX;
        case "gemini":
        case "gem":
            return Constellation.GEMINI;
        case "grus":
        case "gru":
            return Constellation.GRUS;
        case "hercules":
        case "her":
            return Constellation.HERCULES;
        case "horologium":
        case "hor":
            return Constellation.HOROLOGIUM;
        case "hydra":
        case "hya":
            return Constellation.HYDRA;
        case "hydrus":
        case "hyi":
            return Constellation.HYDRUS;
        case "indus":
        case "ind":
            return Constellation.INDUS;
        case "lacerta":
        case "lac":
            return Constellation.LACERTA;
        case "leo":
            return Constellation.LEO;
        case "leo minor":
        case "lmi":
            return Constellation.LEO_MINOR;
        case "lepus":
        case "lep":
            return Constellation.LEPUS;
        case "libra":
        case "lib":
            return Constellation.LIBRA;
        case "lupus":
        case "lup":
            return Constellation.LUPUS;
        case "lynx":
        case "lyn":
            return Constellation.LYNX;
        case "lyra":
        case "lyr":
            return Constellation.LYRA;
        case "mensa":
        case "men":
            return Constellation.MENSA;
        case "microscopus":
        case "mic":
            return Constellation.MICROSCOPUS;
        case "monocerus":
        case "mon":
            return Constellation.MONOCERUS;
        case "musca":
        case "mus":
            return Constellation.MUSCA;
        case "norma":
        case "nor":
            return Constellation.NORMA;
        case "octans":
        case "oct":
            return Constellation.OCTANS;
        case "ophiuchus":
        case "oph":
            return Constellation.OPHIUCHUS;
        case "orion":
        case "ori":
            return Constellation.ORION;
        case "pavo":
        case "pav":
            return Constellation.PAVO;
        case "pegasus":
        case "peg":
            return Constellation.PEGASUS;
        case "perseus":
        case "per":
            return Constellation.PERSEUS;
        case "phoenix":
        case "phe":
            return Constellation.PHOENIX;
        case "pictor":
        case "pic":
            return Constellation.PICTOR;
        case "pisces austrinus":
        case "psa":
            return Constellation.PISCES_AUSTRINUS;
        case "pisces":
        case "psc":
            return Constellation.PISCES;
        case "puppis":
        case "pup":
            return Constellation.PUPPIS;
        case "pyxis":
        case "pyx":
            return Constellation.PYXIS;
        case "reticulum":
        case "ret":
            return Constellation.RETICULUM;
        case "sagitta":
        case "sge":
            return Constellation.SAGITTA;
        case "sagittarius":
        case "sgr":
            return Constellation.SAGITTARIUS;
        case "scorpius":
        case "sco":
            return Constellation.SCORPIUS;
        case "sculptor":
        case "scl":
            return Constellation.SCULPTOR;
        case "scutum":
        case "sct":
            return Constellation.SCUTUM;
        case "serpens":
        case "ser":
            return Constellation.SERPENS;
        case "sextans":
        case "sex":
            return Constellation.SEXTANS;
        case "taurus":
        case "tau":
            return Constellation.TAURUS;
        case "telescopium":
        case "tel":
            return Constellation.TELESCOPIUM;
        case "triangulum australis":
        case "tra":
            return Constellation.TRIANGULUM_AUSTRALIS;
        case "triangulum":
        case "tri":
            return Constellation.TRIANGULUM;
        case "tucana":
        case "tuc":
            return Constellation.TUCANA;
        case "ursa maior":
        case "uma":
            return Constellation.URSA_MAIOR;
        case "ursa minor":
        case "umi":
            return Constellation.URSA_MINOR;
        case "vela":
            return Constellation.VELA;
        case "virgo":
        case "vir":
            return Constellation.VIRGO;
        case "volans":
        case "vol":
            return Constellation.VOLANS;
        case "vulpecula":
        case "vul":
            return Constellation.VULPECULA;
        default:
            return null;
        }

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
     * @param o
     *            An object to compare against this constellation
     * @return <code>true</code> if the passed object is an instance of de.lehmannet.om.Constellation and the passed
     *         constellation and this instance have the same abbreviation.
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
