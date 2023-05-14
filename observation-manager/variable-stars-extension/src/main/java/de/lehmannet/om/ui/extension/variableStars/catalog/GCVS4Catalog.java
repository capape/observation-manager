/*
 * ====================================================================
 * /extension/variableStars/catalog/GCVS4Catalog
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.variableStars.catalog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.lehmannet.om.Constellation;
import de.lehmannet.om.EquPosition;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.extension.variableStars.TargetVariableStar;
import de.lehmannet.om.ui.catalog.ICatalog;

import de.lehmannet.om.ui.panel.AbstractSearchPanel;
import de.lehmannet.om.ui.util.IConfiguration;
import de.lehmannet.om.util.FloatUtil;

public class GCVS4Catalog implements ICatalog {

    private final Logger LOGGER = LoggerFactory.getLogger(GCVS4Catalog.class);

    private static final String CATALOG_NAME = "General Catalogue of Variable Stars - Volumes I-III, 4th Edition - (GCVS4)";

    private static final String CATALOG_ABB = "GCVS";

    private static final String DATASOURCE_ORIGIN = "Sternberg Astronomical Institute, Moscow, Russia (http://www.sai.msu.su/groups/cluster/gcvs/gcvs/) - Edition: 4";

    private static final int CATALOG_LINE_SIZE = 162; // Size of lines in catalog file (in bytes)

    // Data index
    private static final int[] LINES_ALL = new int[] { 3, 40370 };

    // Catalog index
    private static final int[] LINES_ANDROMEDA = new int[] { 3, 461 };
    private static final int[] LINES_ANTLIA = new int[] { 461, 545 };
    private static final int[] LINES_APUS = new int[] { 545, 866 };
    private static final int[] LINES_AQUARIUS = new int[] { 866, 1171 };
    private static final int[] LINES_AQUILA = new int[] { 1171, 2879 };
    private static final int[] LINES_ARA = new int[] { 2879, 3762 };
    private static final int[] LINES_ARIES = new int[] { 3762, 3845 };
    private static final int[] LINES_AURIGA = new int[] { 3845, 4406 };
    private static final int[] LINES_BOOTES = new int[] { 4406, 4627 };
    private static final int[] LINES_CAELUM = new int[] { 4627, 4649 };
    private static final int[] LINES_CAMELOPARDALIS = new int[] { 4649, 4934 };
    private static final int[] LINES_CANCER = new int[] { 4934, 5169 };
    private static final int[] LINES_CANES_VENATICI = new int[] { 5169, 5323 };
    private static final int[] LINES_CANES_MAIOR = new int[] { 5323, 5692 };
    private static final int[] LINES_CANES_MINOR = new int[] { 5692, 5821 };
    private static final int[] LINES_CAPRICORNUS = new int[] { 5821, 5940 };
    private static final int[] LINES_CARINA = new int[] { 5940, 6545 };
    private static final int[] LINES_CASSIOPEIA = new int[] { 6545, 7560 };
    private static final int[] LINES_CENTAURUS = new int[] { 7560, 8633 };
    private static final int[] LINES_CEPHEUS = new int[] { 8633, 9371 };
    private static final int[] LINES_CETUS = new int[] { 9371, 9559 };
    private static final int[] LINES_CHAMAELEON = new int[] { 9559, 9725 };
    private static final int[] LINES_CIRCINUS = new int[] { 9725, 9859 };
    private static final int[] LINES_COLUMBA = new int[] { 9859, 9934 };
    private static final int[] LINES_COMA_BERENICES = new int[] { 9934, 10214 };
    private static final int[] LINES_CORONA_AUSTRALIS = new int[] { 10214, 10945 };
    private static final int[] LINES_CORONA_BOREALIS = new int[] { 10945, 11026 };
    private static final int[] LINES_CORVUS = new int[] { 11026, 11070 };
    private static final int[] LINES_CRATER = new int[] { 11070, 11126 };
    private static final int[] LINES_CRUX = new int[] { 11126, 11284 };
    private static final int[] LINES_CYGNUS = new int[] { 11284, 13757 };
    private static final int[] LINES_DELPHINUS = new int[] { 13757, 14063 };
    private static final int[] LINES_DORADO = new int[] { 14063, 14147 };
    private static final int[] LINES_DRACO = new int[] { 14147, 14434 };
    private static final int[] LINES_EQUULEUS = new int[] { 14434, 14464 };
    private static final int[] LINES_ERIDANUS = new int[] { 14464, 14716 };
    private static final int[] LINES_FORNAX = new int[] { 14716, 14790 };
    private static final int[] LINES_GEMINI = new int[] { 14790, 15172 };
    private static final int[] LINES_GRUS = new int[] { 15172, 15316 };
    private static final int[] LINES_HERCULES = new int[] { 15316, 16442 };
    private static final int[] LINES_HOROLOGIUM = new int[] { 16442, 16502 };
    private static final int[] LINES_HYDRA = new int[] { 16502, 16929 };
    private static final int[] LINES_HYDRUS = new int[] { 16929, 17055 };
    private static final int[] LINES_INDUS = new int[] { 17055, 17168 };
    private static final int[] LINES_LACERTA = new int[] { 17168, 17621 };
    private static final int[] LINES_LEO = new int[] { 17621, 17835 };
    private static final int[] LINES_LEO_MINOR = new int[] { 17835, 17887 };
    private static final int[] LINES_LEPUS = new int[] { 17887, 17952 };
    private static final int[] LINES_LIBRA = new int[] { 17952, 18214 };
    private static final int[] LINES_LUPUS = new int[] { 18214, 18520 };
    private static final int[] LINES_LYNX = new int[] { 18520, 18673 };
    private static final int[] LINES_LYRA = new int[] { 18673, 19312 };
    private static final int[] LINES_MENSA = new int[] { 19312, 19383 };
    private static final int[] LINES_MICROSCOPUS = new int[] { 19383, 19512 };
    private static final int[] LINES_MONOCERUS = new int[] { 19512, 20384 };
    private static final int[] LINES_MUSCA = new int[] { 20384, 20672 };
    private static final int[] LINES_NORMA = new int[] { 20672, 21062 };
    private static final int[] LINES_OCTANS = new int[] { 21062, 21210 };
    private static final int[] LINES_OPHIUCHUS = new int[] { 21210, 23829 };
    private static final int[] LINES_ORION = new int[] { 23829, 25638 };
    private static final int[] LINES_PAVO = new int[] { 25638, 26042 };
    private static final int[] LINES_PEGASUS = new int[] { 26042, 26459 };
    private static final int[] LINES_PERSEUS = new int[] { 26459, 27192 };
    private static final int[] LINES_PHOENIX = new int[] { 27192, 27320 };
    private static final int[] LINES_PICTOR = new int[] { 27320, 27385 };
    private static final int[] LINES_PISCES_AUSTRINUS = new int[] { 27548, 27600 };
    private static final int[] LINES_PISCES = new int[] { 27385, 27548 };
    private static final int[] LINES_PUPPIS = new int[] { 27600, 28202 };
    private static final int[] LINES_PYXIS = new int[] { 28202, 28334 };
    private static final int[] LINES_RETICULUM = new int[] { 28334, 28377 };
    private static final int[] LINES_SAGITTA = new int[] { 28377, 28743 };
    private static final int[] LINES_SAGITTARIUS = new int[] { 28743, 34304 };
    private static final int[] LINES_SCORPIUS = new int[] { 34304, 35591 };
    private static final int[] LINES_SCULPTOR = new int[] { 35591, 35703 };
    private static final int[] LINES_SCUTUM = new int[] { 35703, 36195 };
    private static final int[] LINES_SERPENS = new int[] { 36195, 36604 };
    private static final int[] LINES_SEXTANS = new int[] { 36604, 36657 };
    private static final int[] LINES_TAURUS = new int[] { 36657, 37905 };
    private static final int[] LINES_TELESCOPIUM = new int[] { 37905, 38258 };
    private static final int[] LINES_TRIANGULUM_AUSTRALIS = new int[] { 38331, 38624 };
    private static final int[] LINES_TRIANGULUM = new int[] { 38258, 38331 };
    private static final int[] LINES_TUCANA = new int[] { 38624, 38778 };
    private static final int[] LINES_URSA_MAIOR = new int[] { 38778, 39075 };
    private static final int[] LINES_URSA_MINOR = new int[] { 39075, 39118 };
    private static final int[] LINES_VELA = new int[] { 39118, 39523 };
    private static final int[] LINES_VIRGO = new int[] { 39523, 39862 };
    private static final int[] LINES_VOLANS = new int[] { 39862, 39914 };
    private static final int[] LINES_VULPECULA = new int[] { 39914, 40370 };

    private File catalogFile = null;

    private final IConfiguration configuration;

    public GCVS4Catalog(File catalogDir, IConfiguration configuration) {

        this.configuration = configuration;
        // Get catalog File
        this.catalogFile = new File(
                catalogDir.getAbsolutePath() + File.separator + "variableStars" + File.separator + "gcvs4.dat");

        // Check if file exists
        if (!this.catalogFile.exists()) {
            return;
        }

    }

    @Override
    public String getName() {

        return GCVS4Catalog.CATALOG_NAME;

    }

    @Override
    public String getAbbreviation() {

        return GCVS4Catalog.CATALOG_ABB;

    }

    @Override
    public AbstractSearchPanel getSearchPanel() {

        return new GCVS4SearchPanel(this, configuration);

    }

    @Override
    public ITarget getTarget(String objectName) {

        ITarget target = null;

        if ((objectName == null) || ("".equals(objectName.trim()))) {
            return null;
        }

        // "Format" objectName
        String objectNameLC = objectName.toLowerCase();
        objectNameLC = this.removeWhiteSpaces(objectNameLC);

        if (objectNameLC.startsWith(GCVS4Catalog.CATALOG_ABB.toLowerCase())
                || (((byte) objectNameLC.charAt(0) >= 48) && ((byte) objectNameLC.charAt(0) < 58)) // Starts with number
        ) {
            if (objectNameLC.startsWith(GCVS4Catalog.CATALOG_ABB.toLowerCase())) {
                objectNameLC = objectNameLC.replaceAll(GCVS4Catalog.CATALOG_ABB.toLowerCase(), "");
                objectNameLC = objectNameLC.trim();
            }
            int line = this.binarySearch(GCVS4Catalog.LINES_ALL, objectNameLC, true);
            target = this.createTarget(line);
        } else {
            int line = this.searchByName(objectName); // Don't change case
            if (line != -1) {
                target = this.createTarget(line);
            }
        }

        return target;

    }

    private int searchByName(String objectName) {

        int[] lines = null;

        // Try to get constellation
        objectName = objectName.trim();
        int cStart = objectName.lastIndexOf(' ');
        if (cStart == -1) { // There is no space in given object name. This pattern does not match the GCVS4
                            // catalog
            return -1; // We won't find anything in our catalog
        }
        String constName = objectName.substring(cStart);
        String firstPart = objectName.substring(0, cStart);
        Constellation constellation = Constellation.getConstellationByAbbOrName(constName);

        if (constellation == null) { // Without constellation we cannot find the object
            return -1;
        }

        // Make sure abbreviation is used
        objectName = firstPart + " " + constellation.getAbbreviation();

        // Try to determin the constellation borders/lines
        if (constellation.equals(Constellation.ANDROMEDA)) {
            lines = GCVS4Catalog.LINES_ANDROMEDA;
        } else if (constellation.equals(Constellation.ANTLIA)) {
            lines = GCVS4Catalog.LINES_ANTLIA;
        } else if (constellation.equals(Constellation.APUS)) {
            lines = GCVS4Catalog.LINES_APUS;
        } else if (constellation.equals(Constellation.AQUARIUS)) {
            lines = GCVS4Catalog.LINES_AQUARIUS;
        } else if (constellation.equals(Constellation.AQUILA)) {
            lines = GCVS4Catalog.LINES_AQUILA;
        } else if (constellation.equals(Constellation.ARA)) {
            lines = GCVS4Catalog.LINES_ARA;
        } else if (constellation.equals(Constellation.ARIES)) {
            lines = GCVS4Catalog.LINES_ARIES;
        } else if (constellation.equals(Constellation.AURIGA)) {
            lines = GCVS4Catalog.LINES_AURIGA;
        } else if (constellation.equals(Constellation.BOOTES)) {
            lines = GCVS4Catalog.LINES_BOOTES;
        } else if (constellation.equals(Constellation.CAELUM)) {
            lines = GCVS4Catalog.LINES_CAELUM;
        } else if (constellation.equals(Constellation.CAMELOPARDALIS)) {
            lines = GCVS4Catalog.LINES_CAMELOPARDALIS;
        } else if (constellation.equals(Constellation.CANCER)) {
            lines = GCVS4Catalog.LINES_CANCER;
        } else if (constellation.equals(Constellation.CANES_VENATICI)) {
            lines = GCVS4Catalog.LINES_CANES_VENATICI;
        } else if (constellation.equals(Constellation.CANIS_MAIOR)) {
            lines = GCVS4Catalog.LINES_CANES_MAIOR;
        } else if (constellation.equals(Constellation.CANIS_MINOR)) {
            lines = GCVS4Catalog.LINES_CANES_MINOR;
        } else if (constellation.equals(Constellation.CAPRICORNUS)) {
            lines = GCVS4Catalog.LINES_CAPRICORNUS;
        } else if (constellation.equals(Constellation.CARINA)) {
            lines = GCVS4Catalog.LINES_CARINA;
        } else if (constellation.equals(Constellation.CASSIOPEIA)) {
            lines = GCVS4Catalog.LINES_CASSIOPEIA;
        } else if (constellation.equals(Constellation.CENTAURUS)) {
            lines = GCVS4Catalog.LINES_CENTAURUS;
        } else if (constellation.equals(Constellation.CEPHEUS)) {
            lines = GCVS4Catalog.LINES_CEPHEUS;
        } else if (constellation.equals(Constellation.CETUS)) {
            lines = GCVS4Catalog.LINES_CETUS;
        } else if (constellation.equals(Constellation.CHAMAELEON)) {
            lines = GCVS4Catalog.LINES_CHAMAELEON;
        } else if (constellation.equals(Constellation.CIRCINUS)) {
            lines = GCVS4Catalog.LINES_CIRCINUS;
        } else if (constellation.equals(Constellation.COLUMBA)) {
            lines = GCVS4Catalog.LINES_COLUMBA;
        } else if (constellation.equals(Constellation.COMA_BERENICES)) {
            lines = GCVS4Catalog.LINES_COMA_BERENICES;
        } else if (constellation.equals(Constellation.CORONA_AUSTRALIS)) {
            lines = GCVS4Catalog.LINES_CORONA_AUSTRALIS;
        } else if (constellation.equals(Constellation.CORONA_BOREALIS)) {
            lines = GCVS4Catalog.LINES_CORONA_BOREALIS;
        } else if (constellation.equals(Constellation.CORVUS)) {
            lines = GCVS4Catalog.LINES_CORVUS;
        } else if (constellation.equals(Constellation.CRATER)) {
            lines = GCVS4Catalog.LINES_CRATER;
        } else if (constellation.equals(Constellation.CRUX)) {
            lines = GCVS4Catalog.LINES_CRUX;
        } else if (constellation.equals(Constellation.CYGNUS)) {
            lines = GCVS4Catalog.LINES_CYGNUS;
        } else if (constellation.equals(Constellation.DELPHINUS)) {
            lines = GCVS4Catalog.LINES_DELPHINUS;
        } else if (constellation.equals(Constellation.DORADO)) {
            lines = GCVS4Catalog.LINES_DORADO;
        } else if (constellation.equals(Constellation.DRACO)) {
            lines = GCVS4Catalog.LINES_DRACO;
        } else if (constellation.equals(Constellation.EQUULEUS)) {
            lines = GCVS4Catalog.LINES_EQUULEUS;
        } else if (constellation.equals(Constellation.ERIDANUS)) {
            lines = GCVS4Catalog.LINES_ERIDANUS;
        } else if (constellation.equals(Constellation.FORNAX)) {
            lines = GCVS4Catalog.LINES_FORNAX;
        } else if (constellation.equals(Constellation.GEMINI)) {
            lines = GCVS4Catalog.LINES_GEMINI;
        } else if (constellation.equals(Constellation.GRUS)) {
            lines = GCVS4Catalog.LINES_GRUS;
        } else if (constellation.equals(Constellation.HERCULES)) {
            lines = GCVS4Catalog.LINES_HERCULES;
        } else if (constellation.equals(Constellation.HOROLOGIUM)) {
            lines = GCVS4Catalog.LINES_HOROLOGIUM;
        } else if (constellation.equals(Constellation.HYDRA)) {
            lines = GCVS4Catalog.LINES_HYDRA;
        } else if (constellation.equals(Constellation.HYDRUS)) {
            lines = GCVS4Catalog.LINES_HYDRUS;
        } else if (constellation.equals(Constellation.INDUS)) {
            lines = GCVS4Catalog.LINES_INDUS;
        } else if (constellation.equals(Constellation.LACERTA)) {
            lines = GCVS4Catalog.LINES_LACERTA;
        } else if (constellation.equals(Constellation.LEO)) {
            lines = GCVS4Catalog.LINES_LEO;
        } else if (constellation.equals(Constellation.LEO_MINOR)) {
            lines = GCVS4Catalog.LINES_LEO_MINOR;
        } else if (constellation.equals(Constellation.LEPUS)) {
            lines = GCVS4Catalog.LINES_LEPUS;
        } else if (constellation.equals(Constellation.LIBRA)) {
            lines = GCVS4Catalog.LINES_LIBRA;
        } else if (constellation.equals(Constellation.LUPUS)) {
            lines = GCVS4Catalog.LINES_LUPUS;
        } else if (constellation.equals(Constellation.LYNX)) {
            lines = GCVS4Catalog.LINES_LYNX;
        } else if (constellation.equals(Constellation.LYRA)) {
            lines = GCVS4Catalog.LINES_LYRA;
        } else if (constellation.equals(Constellation.MENSA)) {
            lines = GCVS4Catalog.LINES_MENSA;
        } else if (constellation.equals(Constellation.MICROSCOPUS)) {
            lines = GCVS4Catalog.LINES_MICROSCOPUS;
        } else if (constellation.equals(Constellation.MONOCERUS)) {
            lines = GCVS4Catalog.LINES_MONOCERUS;
        } else if (constellation.equals(Constellation.MUSCA)) {
            lines = GCVS4Catalog.LINES_MUSCA;
        } else if (constellation.equals(Constellation.NORMA)) {
            lines = GCVS4Catalog.LINES_NORMA;
        } else if (constellation.equals(Constellation.OCTANS)) {
            lines = GCVS4Catalog.LINES_OCTANS;
        } else if (constellation.equals(Constellation.OPHIUCHUS)) {
            lines = GCVS4Catalog.LINES_OPHIUCHUS;
        } else if (constellation.equals(Constellation.ORION)) {
            lines = GCVS4Catalog.LINES_ORION;
        } else if (constellation.equals(Constellation.PAVO)) {
            lines = GCVS4Catalog.LINES_PAVO;
        } else if (constellation.equals(Constellation.PEGASUS)) {
            lines = GCVS4Catalog.LINES_PEGASUS;
        } else if (constellation.equals(Constellation.PERSEUS)) {
            lines = GCVS4Catalog.LINES_PERSEUS;
        } else if (constellation.equals(Constellation.PHOENIX)) {
            lines = GCVS4Catalog.LINES_PHOENIX;
        } else if (constellation.equals(Constellation.PICTOR)) {
            lines = GCVS4Catalog.LINES_PICTOR;
        } else if (constellation.equals(Constellation.PISCES_AUSTRINUS)) {
            lines = GCVS4Catalog.LINES_PISCES_AUSTRINUS;
        } else if (constellation.equals(Constellation.PISCES)) {
            lines = GCVS4Catalog.LINES_PISCES;
        } else if (constellation.equals(Constellation.PUPPIS)) {
            lines = GCVS4Catalog.LINES_PUPPIS;
        } else if (constellation.equals(Constellation.PYXIS)) {
            lines = GCVS4Catalog.LINES_PYXIS;
        } else if (constellation.equals(Constellation.RETICULUM)) {
            lines = GCVS4Catalog.LINES_RETICULUM;
        } else if (constellation.equals(Constellation.SAGITTA)) {
            lines = GCVS4Catalog.LINES_SAGITTA;
        } else if (constellation.equals(Constellation.SAGITTARIUS)) {
            lines = GCVS4Catalog.LINES_SAGITTARIUS;
        } else if (constellation.equals(Constellation.SCORPIUS)) {
            lines = GCVS4Catalog.LINES_SCORPIUS;
        } else if (constellation.equals(Constellation.SCULPTOR)) {
            lines = GCVS4Catalog.LINES_SCULPTOR;
        } else if (constellation.equals(Constellation.SCUTUM)) {
            lines = GCVS4Catalog.LINES_SCUTUM;
        } else if (constellation.equals(Constellation.SERPENS)) {
            lines = GCVS4Catalog.LINES_SERPENS;
        } else if (constellation.equals(Constellation.SEXTANS)) {
            lines = GCVS4Catalog.LINES_SEXTANS;
        } else if (constellation.equals(Constellation.TAURUS)) {
            lines = GCVS4Catalog.LINES_TAURUS;
        } else if (constellation.equals(Constellation.TELESCOPIUM)) {
            lines = GCVS4Catalog.LINES_TELESCOPIUM;
        } else if (constellation.equals(Constellation.TRIANGULUM_AUSTRALIS)) {
            lines = GCVS4Catalog.LINES_TRIANGULUM_AUSTRALIS;
        } else if (constellation.equals(Constellation.TRIANGULUM)) {
            lines = GCVS4Catalog.LINES_TRIANGULUM;
        } else if (constellation.equals(Constellation.TUCANA)) {
            lines = GCVS4Catalog.LINES_TUCANA;
        } else if (constellation.equals(Constellation.URSA_MAIOR)) {
            lines = GCVS4Catalog.LINES_URSA_MAIOR;
        } else if (constellation.equals(Constellation.URSA_MINOR)) {
            lines = GCVS4Catalog.LINES_URSA_MINOR;
        } else if (constellation.equals(Constellation.VELA)) {
            lines = GCVS4Catalog.LINES_VELA;
        } else if (constellation.equals(Constellation.VIRGO)) {
            lines = GCVS4Catalog.LINES_VIRGO;
        } else if (constellation.equals(Constellation.VOLANS)) {
            lines = GCVS4Catalog.LINES_VOLANS;
        } else if (constellation.equals(Constellation.VULPECULA)) {
            lines = GCVS4Catalog.LINES_VULPECULA;
        }

        // Nothing found
        if (lines == null) {
            return -1;
        }

        int line = this.binarySearch(lines, objectName, false);

        // In case the binary search didn't succeed, search again sequential,
        // as some entries in catalog (like P Cyg) are strangly sorted and
        // will not be found with binary search
        if (line == -1) {
            line = this.seqentialSearch(lines, objectName);
        }

        return line;

    }

    private int seqentialSearch(int[] startEnd, String objectName) {

        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(this.catalogFile, "r");

            byte[] buffer = new byte[9];
            int off = 8;
            int len = 9;

            int low = startEnd[0] - 1;
            int currentLine = startEnd[1]; // Start from highest position
            int pointerPos = 0;
            boolean found = false;
            do {
                pointerPos = GCVS4Catalog.CATALOG_LINE_SIZE * currentLine; // Calculate read/write pointer position in
                                                                           // bytes
                pointerPos = pointerPos + off; // Move to position in line, where ID or name is placed
                raf.seek(pointerPos); // Goto position
                raf.read(buffer, 0, len); // Read name or ID

                String name = new String(buffer);
                // name = name.toLowerCase();
                name = this.removeWhiteSpaces(name);

                if ("".equals(name)) { // Read empty line
                    currentLine--; // Go one line "up"
                    continue;
                }

                int compResult = this.compareStrings(objectName, name);
                if (compResult == 0) {
                    found = true;
                    break; // Found object
                }

                currentLine--; // Go one line "up"
            } while (currentLine >= low);

            try {
                raf.close();
            } catch (IOException ioe) {
                LOGGER.error("Unable to close data stream.{} (SeqS)", this.catalogFile, ioe);
            }

            if (found) {
                return currentLine;
            } else {
                return -1;
            }

        } catch (FileNotFoundException fnfe) {
            LOGGER.error("Cannot find catalog file:{} (SeqS)", this.catalogFile, fnfe);
        } catch (IOException ioe) {
            LOGGER.error("Error while accessing catalog file:{} (SeqS)", this.catalogFile, ioe);
        }

        // Close file in case an exception was thrown
        try {
            if (raf != null) {
                raf.close();
            }
        } catch (IOException ioe) {
            LOGGER.error("Unable to close data stream.{} (SeqS)", this.catalogFile, ioe);
        }

        return -1;

    }

    private int binarySearch(int[] startEnd, String objectName, boolean catalogNumber) {

        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(this.catalogFile, "r");

            byte[] buffer = null;
            int off = -1;
            int len = -1;
            if (catalogNumber) {
                buffer = new byte[6];
                off = 0;
                len = 6;
            } else {
                buffer = new byte[9];
                off = 8;
                len = 9;
            }

            boolean found = false;
            boolean done = false;
            int low = startEnd[0] - 1;
            int high = startEnd[1];
            int middle = 0;
            int pointerPos = 0;
            do {
                middle = low + ((high - low) / 2); // Calculate middle of block
                pointerPos = GCVS4Catalog.CATALOG_LINE_SIZE * middle; // Calculate read/write pointer position in bytes
                pointerPos = pointerPos + off; // Move to position in line, where ID or name is placed
                raf.seek(pointerPos); // Goto position
                raf.read(buffer, 0, len); // Read name or ID

                String name = new String(buffer);
                // name = name.toLowerCase();
                name = this.removeWhiteSpaces(name);

                int compResult = this.compareStrings(objectName, name);
                if (compResult < 0) {
                    if (high == middle) {
                        done = true;
                    }
                    high = middle;
                } else if (compResult > 0) {
                    if (low == middle) {
                        done = true;
                    }
                    low = middle;
                } else {
                    found = true;
                }

            } while (!found && !done);

            try {
                raf.close();
            } catch (IOException ioe) {
                LOGGER.error("Unable to close data stream. {}", this.catalogFile, ioe);
            }

            if (done) {
                return -1; // Not found
            }

            return middle;

        } catch (FileNotFoundException fnfe) {
            LOGGER.error("Cannot find catalog file: {}", this.catalogFile, fnfe);
        } catch (IOException ioe) {
            LOGGER.error("Error while accessing catalog file: {}", this.catalogFile, ioe);
        }

        // Close file
        try {
            if (raf != null) {
                raf.close();
            }
        } catch (IOException ioe) {
            LOGGER.error("Unable to close data stream. {}", this.catalogFile, ioe);
        }

        return -1;

    }

    private ITarget createTarget(int lineNumber) {

        TargetVariableStar target = null;

        if (lineNumber == -1) {
            return null;
        }

        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(this.catalogFile, "r");
            raf.seek(GCVS4Catalog.CATALOG_LINE_SIZE * lineNumber); // Goto line
            String line = raf.readLine(); // Read line

            StringTokenizer tokenizer = new StringTokenizer(line, "|");
            String gcvsNumber = tokenizer.nextToken();
            gcvsNumber = gcvsNumber.trim();
            String designation = tokenizer.nextToken();
            designation = designation.replace('*', ' ');
            designation = this.removeWhiteSpaces(designation);

            String constellationString = designation.substring(designation.length() - 3);
            Constellation constellation = Constellation.getConstellationByAbbOrName(constellationString);

            // Position
            String position = tokenizer.nextToken();
            EquPosition equPosition = null;
            if ((position != null) && !("".equals(position.trim()))) {
                String raHour = position.substring(0, 2);
                String raMin = position.substring(2, 4);
                String raSec = position.substring(4, 8);
                String decDec = position.substring(8, 11);
                String decMin = position.substring(11, 13);
                String decSec = position.substring(13);
                String ra = raHour + EquPosition.RA_HOUR + raMin + EquPosition.RA_MIN + raSec + EquPosition.RA_SEC;
                String dec = decDec + EquPosition.DEC_DEG + decMin + EquPosition.DEC_MIN + decSec + EquPosition.DEC_SEC;
                equPosition = new EquPosition(ra, dec);
            }

            String type = tokenizer.nextToken();
            String maxMag = tokenizer.nextToken();
            String minMag = tokenizer.nextToken();
            minMag = minMag.replace(':', ' ');
            minMag = minMag.replace('<', ' ');
            minMag = minMag.replace('>', ' ');
            minMag = minMag.replace('(', ' ');
            minMag = minMag.replace(')', ' ');

            String v = tokenizer.nextToken();
            String epoch = tokenizer.nextToken();
            String year = tokenizer.nextToken();
            String period = tokenizer.nextToken();
            String Mm = tokenizer.nextToken();
            String spectrum = tokenizer.nextToken();
            String references = tokenizer.nextToken();
            String otherDesignations = tokenizer.nextToken();
            otherDesignations = otherDesignations.replaceAll("N {11}", " "); // Star does not exist
            otherDesignations = otherDesignations.replace('=', ' ');
            otherDesignations = this.removeWhiteSpaces(otherDesignations);

            // Star is listed as with different designation. Read new designation and parse
            // line again
            String additionalDesignation = null;
            if ((equPosition == null) && !("".equals(otherDesignations)) && !(otherDesignations.startsWith("HIP")
                    || (((byte) otherDesignations.charAt(0) >= 48) && ((byte) otherDesignations.charAt(0) < 58)) // Starts
                                                                                                                 // with
                                                                                                                 // number
            )) {
                additionalDesignation = designation; // Save old designation

                int newLine = this.searchByName(otherDesignations);

                raf.seek(GCVS4Catalog.CATALOG_LINE_SIZE * newLine); // Goto line
                line = raf.readLine(); // Read line

                tokenizer = new StringTokenizer(line, "|");
                gcvsNumber = tokenizer.nextToken();

                designation = tokenizer.nextToken();
                designation = designation.replace('*', ' ');
                designation = this.removeWhiteSpaces(designation);

                constellationString = designation.substring(designation.length() - 3);
                constellation = Constellation.getConstellationByAbbOrName(constellationString);

                // Position
                position = tokenizer.nextToken();
                equPosition = null;
                if ((position != null) && !("".equals(position.trim()))) {
                    String raHour = position.substring(0, 2);
                    String raMin = position.substring(2, 4);
                    String raSec = position.substring(4, 8);
                    String decDec = position.substring(9, 11);
                    String decMin = position.substring(11, 13);
                    String decSec = position.substring(13);
                    String ra = raHour + EquPosition.RA_HOUR + raMin + EquPosition.RA_MIN + raSec + EquPosition.RA_SEC;
                    String dec = decDec + EquPosition.DEC_DEG + decMin + EquPosition.DEC_MIN + decSec
                            + EquPosition.DEC_SEC;
                    equPosition = new EquPosition(ra, dec);
                }

                type = tokenizer.nextToken();
                maxMag = tokenizer.nextToken();
                minMag = tokenizer.nextToken();
                minMag = minMag.replace(':', ' ');
                minMag = minMag.replace('<', ' ');
                minMag = minMag.replace('>', ' ');
                minMag = minMag.replace('(', ' ');
                minMag = minMag.replace(')', ' ');

                v = tokenizer.nextToken();
                epoch = tokenizer.nextToken();
                year = tokenizer.nextToken();
                period = tokenizer.nextToken();
                Mm = tokenizer.nextToken();
                spectrum = tokenizer.nextToken();
                references = tokenizer.nextToken();
                otherDesignations = tokenizer.nextToken();
                otherDesignations = otherDesignations.replaceAll("N {11}", " "); // Star does not exist
                otherDesignations = otherDesignations.replace('=', ' ');
                otherDesignations = this.removeWhiteSpaces(otherDesignations);
            }

            target = new TargetVariableStar(designation, GCVS4Catalog.CATALOG_NAME);
            target.setPosition(equPosition);
            if (!"".equals(minMag.trim())) {
                target.setMagnitudeApparent(Float.parseFloat(minMag));
            }
            if ((maxMag != null) && !("".equals(maxMag.trim()))) {
                target.setMaxMagnitudeApparent(Float.parseFloat(maxMag));
            }

            if ((period != null) && !("".equals(period.trim()))) {
                period = period.replace('(', ' ');
                period = period.replace(')', ' ');
                period = period.replace(':', ' ');
                float p = FloatUtil.parseFloat(period);
                target.setPeriod(p);
            }

            target.setType(type);
            target.setConstellation(constellation);
            target.setStellarClassification(spectrum);
            if (additionalDesignation != null) {
                target.setAliasNames(new String[] { (GCVS4Catalog.CATALOG_ABB + gcvsNumber), otherDesignations,
                        additionalDesignation });
            } else {
                target.setAliasNames(new String[] { (GCVS4Catalog.CATALOG_ABB + gcvsNumber), otherDesignations });
            }

        } catch (FileNotFoundException fnfe) {
            LOGGER.error("Cannot find catalog file: {}", this.catalogFile, fnfe);
        } catch (IOException ioe) {
            LOGGER.error("Error while accessing catalog file: {}", this.catalogFile, ioe);
        }

        // Close file
        try {
            if (raf != null) {
                raf.close();
            }
        } catch (IOException ioe) {
            LOGGER.error("Unable to close data stream. {}", this.catalogFile, ioe);
        }

        return target;

    }

    private int compareStrings(String a, String b) {

        int result = a.compareTo(b);

        if (result == 0) { // We found what we searched for...
            return result;
        }

        b = b.substring(0, 2); // Only take first to chars

        // Designation of var stars starts with R until ZZ and then starts again from A
        // until QZ
        if ((b.compareTo("R") >= 0) || (a.compareTo("R") >= 0)) {

            // Exception from above rule
            if ((b.startsWith("V0")) || (b.startsWith("V1")) || (b.startsWith("V2")) || (b.startsWith("V3"))
                    || (b.startsWith("V4")) || (b.startsWith("V5")) || (b.startsWith("V6")) || (b.startsWith("V7"))
                    || (b.startsWith("V8")) || (b.startsWith("V9")) || (a.startsWith("V0")) || (a.startsWith("V1"))
                    || (a.startsWith("V2")) || (a.startsWith("V3")) || (a.startsWith("V4")) || (a.startsWith("V5"))
                    || (a.startsWith("V6")) || (a.startsWith("V7")) || (a.startsWith("V8")) || (a.startsWith("V9"))) {
                return result; // Don't change anything if we're operation on Vxxxx names
            }

            // Exception from above rule
            if ((b.compareTo("R") >= 0) && (a.compareTo("R") >= 0)) {
                return result; // Don't change anything
            }

            // Change result
            result = result * -1;
        }

        return result;

    }

    private String removeWhiteSpaces(String name) {

        name = name.replaceAll(" {5}", " ");
        name = name.replaceAll(" {4}", " ");
        name = name.replaceAll(" {3}", " ");
        name = name.replaceAll(" {2}", " ");
        name = name.trim();

        return name;

    }

}