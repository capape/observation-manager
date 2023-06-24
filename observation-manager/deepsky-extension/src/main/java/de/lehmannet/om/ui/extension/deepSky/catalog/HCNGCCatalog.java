/*
 * ====================================================================
 * /extension/deepSky/catalog/HCNGCCatalog
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.deepSky.catalog;

import static de.lehmannet.om.util.Sanitizer.toLogMessage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.lehmannet.om.Angle;
import de.lehmannet.om.EquPosition;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.TargetStar;
import de.lehmannet.om.extension.deepSky.DeepSkyTarget;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetAS;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetDS;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetGC;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetGN;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetGX;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetNA;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetOC;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetPN;
import de.lehmannet.om.ui.catalog.IListableCatalog;
import de.lehmannet.om.ui.navigation.tableModel.AbstractSchemaTableModel;
import de.lehmannet.om.ui.panel.AbstractSearchPanel;
import de.lehmannet.om.ui.panel.GenericListableCatalogSearchPanel;
import de.lehmannet.om.util.FloatUtil;

public class HCNGCCatalog implements IListableCatalog {

    private static final Logger LOGGER = LoggerFactory.getLogger(HCNGCCatalog.class);

    private static final String CATALOG_NAME = "The Historically Corrected New General Catalogue (HCNGC) Ver 1.11 ";

    private static final String DATASOURCE_ORIGIN = "The NGC/IC Project LLC (http://www.ngcic.org) - Ver 1.11";

    private static final String CATALOG_ABB = "HCNGC";

    // Key = NGC Number
    // Value = ITarget
    private final Map<String, ITarget> map = new LinkedHashMap<>();

    private AbstractSchemaTableModel tableModel = null;

    public HCNGCCatalog(File file) {

        // Load targets into memory.
        // In case or problems aboard
        if (!this.loadTargets(file)) {
            return;
        }

        this.tableModel = new DeepSkyTableModel(this);

    }

    @Override
    public String getName() {

        return HCNGCCatalog.CATALOG_NAME;

    }

    @Override
    public String getAbbreviation() {

        return HCNGCCatalog.CATALOG_ABB;

    }

    @Override
    public ITarget getTarget(String hcngcNumber) {

        return (ITarget) this.map.get(hcngcNumber);

    }

    @Override
    public ITarget[] getTargets() {

        return (ITarget[]) this.map.values().toArray(new ITarget[] {});

    }

    @Override
    public AbstractSearchPanel getSearchPanel() {

        return new GenericListableCatalogSearchPanel(this);

    }

    private boolean loadTargets(File file) {

        // Check catalog file
        Reader reader = null;
        try {
            // Must read UTF-16 as we run into problems on some OS
            reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);

        } catch (FileNotFoundException fnfe) {
            LOGGER.error("File not found: {}. {}", toLogMessage(file.getName()), toLogMessage(fnfe.toString()));
            return false;
        }

        // Get each line and create target
        String line = null;
        String hcngcNumber = null;
        StringTokenizer tokenizer = null;
        ITarget target = null;
        try (BufferedReader bufferedReader = new BufferedReader(reader);) {
            int counter = 1;
            while ((line = bufferedReader.readLine()) != null) {
                if (!(line.startsWith("" + counter)) || ("".equals(line.trim()))) { // Comment, header or empty line.
                                                                                    // Real data starts with a counter
                                                                                    // at 1.
                    continue;
                }

                // Get HCNGC Number
                hcngcNumber = "HCNGC" + line.substring(0, line.indexOf('|'));

                // Parse line...and create Target
                target = null;

                tokenizer = new StringTokenizer(line, "|");

                tokenizer.nextToken(); // Skip token (HCNGC number)
                tokenizer.nextToken(); // LineCounter (ignore this for now)
                String gcNo = tokenizer.nextToken();
                tokenizer.nextToken(); // Skip token (John Herschel (JH) designation)
                tokenizer.nextToken(); // Skip token (William Herschel (WH) designation)
                String ra = tokenizer.nextToken();
                String dec = tokenizer.nextToken();
                String constellation = tokenizer.nextToken();
                tokenizer.nextToken(); // Skip token (Original NGC Summary Description)
                tokenizer.nextToken(); // Skip token (Discoverer)
                tokenizer.nextToken(); // Skip token (Year)
                tokenizer.nextToken(); // Skip token (Type)
                tokenizer.nextToken(); // Skip token (inch)
                String type = tokenizer.nextToken();
                String classification = tokenizer.nextToken();
                String size = tokenizer.nextToken();
                String positionAngle = tokenizer.nextToken();
                String vMag = tokenizer.nextToken();
                tokenizer.nextToken(); // Skip token (bMag (blue Magnification)
                tokenizer.nextToken(); // vsourceFaceBrightness
                String ngcNo = tokenizer.nextToken();
                String icNo = tokenizer.nextToken();

                String aliasNames = hcngcNumber.replaceAll("HCNGC", "NGC") + ",";
                // Add NGC number which is equal to HCNGC number.
                // The above ngcNo is only given in case the NGC Catalog has two entries for the
                // same
                // object (NGC20 == NGC6) so for compatibility reasons add the NGC Number
                aliasNames = aliasNames + tokenizer.nextToken();

                // Skip the rest from here...

                // Cut off decimal points at positionAngle (if given)
                if (positionAngle.lastIndexOf(".") != -1) {
                    positionAngle = positionAngle.substring(0, positionAngle.lastIndexOf("."));
                }
                // Some objects have two values...skip positionAngle entry for those
                if (positionAngle.contains("/")) {
                    positionAngle = null;
                }
                if ("E".equals(positionAngle)) { // PA for HCNGC1089 is E...whatever that means
                    positionAngle = null;
                }

                if (("NF".equals(type)) // NF Not Found NGC 412
                        || ("GxyCld".equals(type)) // Bright cloud/knot in a galaxy NGC 5447
                        || ("***".equals(type)) // *** Triple Star NGC 4397 (Cannot use DeepSkyMS as catalog doesn't
                                                // contain component star list
                ) {
                    // For the above named types we don't have a representation in the OAL model,
                    // therefore
                    // we map them to the general type DeepSkyTargetNA
                    target = new DeepSkyTargetNA(hcngcNumber, HCNGCCatalog.DATASOURCE_ORIGIN);

                } else if ("**".equals(type)) { // ** Double Star NGC 8
                    target = new DeepSkyTargetDS(hcngcNumber, HCNGCCatalog.DATASOURCE_ORIGIN);

                    if ((positionAngle != null) && !("".equals(positionAngle.trim()))) {
                        try {
                            int pa = Integer.parseInt(positionAngle);
                            ((DeepSkyTargetDS) target).setPositionAngle(pa);
                        } catch (NumberFormatException nfe) {
                            LOGGER.error("Malformed entry: {}  - Position Angle is: {} ", toLogMessage(hcngcNumber), toLogMessage(positionAngle));
                        }
                    }

                } else if ("Ast".equals(type)) { // Ast Asterism NGC 305
                    target = new DeepSkyTargetAS(hcngcNumber, HCNGCCatalog.DATASOURCE_ORIGIN);

                    if ((positionAngle != null) && !("".equals(positionAngle.trim()))) {
                        try {
                            int pa = Integer.parseInt(positionAngle);
                            ((DeepSkyTargetAS) target).setPositionAngle(pa);
                        } catch (NumberFormatException nfe) {
                            LOGGER.error("Malformed entry: {}  - Position Angle is: {} ", toLogMessage(hcngcNumber), toLogMessage(positionAngle));
                        }
                    }

                } else if ("*".equals(type)) { // Single Star NGC 3797
                    target = new TargetStar(hcngcNumber, HCNGCCatalog.DATASOURCE_ORIGIN);

                    if ((classification != null) && !("".equals(classification.trim()))) {
                        ((TargetStar) target).setStellarClassification(classification);
                    }

                    if ((vMag != null) && !("".equals(vMag.trim()))) {
                        ((TargetStar) target).setMagnitudeApparent(FloatUtil.parseFloat(vMag));
                    }

                } else if ("Gxy".equals(type)) { // Galaxy NGC 3320
                    target = new DeepSkyTargetGX(hcngcNumber, HCNGCCatalog.DATASOURCE_ORIGIN);

                    if ((positionAngle != null) && !("".equals(positionAngle.trim()))) {
                        try {
                            int pa = Integer.parseInt(positionAngle);
                            ((DeepSkyTargetGX) target).setPositionAngle(pa);
                        } catch (NumberFormatException nfe) {
                            LOGGER.error("Malformed entry: {}  - Position Angle is: {} ",  toLogMessage(hcngcNumber), toLogMessage(positionAngle));
                        }
                    }

                    if ((classification != null) && !("".equals(classification.trim()))) {
                        ((DeepSkyTargetGX) target).setHubbleType(classification);
                    }

                } else if ("GC".equals(type)) {
                    target = new DeepSkyTargetGC(hcngcNumber, HCNGCCatalog.DATASOURCE_ORIGIN);

                } else if (("Neb".equals(type)) || ("Neb?".equals(type)) // e.g. HCNGC1990
                        || ("SNR".equals(type)) || ("HIIRgn".equals(type)) || ("OC+Neb".equals(type)) // NGC 256
                ) {
                    target = new DeepSkyTargetGN(hcngcNumber, HCNGCCatalog.DATASOURCE_ORIGIN);

                    if ("Neb".equals(type)) {
                        if ((classification != null) && !("".equals(classification.trim()))) {
                            ((DeepSkyTargetGN) target).setNebulaType(classification);
                        }
                    } else if ("SNR".equals(type)) {
                        if ((classification != null) && !("".equals(classification.trim()))) {
                            ((DeepSkyTargetGN) target).setNebulaType(type + " " + classification);
                        }
                    }

                    if ((positionAngle != null) && !("".equals(positionAngle.trim()))) {
                        try {
                            int pa = Integer.parseInt(positionAngle);
                            ((DeepSkyTargetGN) target).setPositionAngle(pa);
                        } catch (NumberFormatException nfe) {
                            LOGGER.error("Malformed entry: {}  - Position Angle is: {} ",  toLogMessage(hcngcNumber), toLogMessage(positionAngle));
                        }
                    }

                } else if (("OC".equals(type)) || ("MWSC".equals(type))) {

                    target = new DeepSkyTargetOC(hcngcNumber, HCNGCCatalog.DATASOURCE_ORIGIN);

                } else if ("PN".equals(type)) {
                    target = new DeepSkyTargetPN(hcngcNumber, HCNGCCatalog.DATASOURCE_ORIGIN);
                }

                // Make sure aliasNames are ; seperated list and no space is between catalogue
                // name and index
                // Also add NGC, GC and IC as aliasNames
                aliasNames = aliasNames.trim();
                if ((ngcNo != null) && !("".equals(ngcNo.trim()))) {
                    if (!aliasNames.trim().equals("")) {
                        aliasNames = aliasNames + ",";
                    }
                    aliasNames = aliasNames + "NGC" + ngcNo;
                }
                if ((gcNo != null) && !("".equals(gcNo.trim()))) {
                    if (!aliasNames.trim().equals("")) {
                        aliasNames = aliasNames + ",";
                    }
                    aliasNames = aliasNames + "GC" + gcNo;
                }
                if ((icNo != null) && !("".equals(icNo.trim()))) {
                    if (!aliasNames.trim().equals("")) {
                        aliasNames = aliasNames + ",";
                    }
                    aliasNames = aliasNames + "IC" + icNo;
                }
                aliasNames = aliasNames.replaceAll(" ", "");
                aliasNames = aliasNames.toUpperCase();

                // Hack! Cut off decimal point at seconds
                ra = ra.substring(0, ra.lastIndexOf("."));
                ra = ra + "s";

                // Only for HCNGC6439...
                if ("HCNGC6439".equals(hcngcNumber)) {
                    dec = dec.substring(0, dec.lastIndexOf("."));
                    dec = dec + "\"";
                }

                dec = dec.replaceAll("\'\'", "\""); // Sometimes " is given as '' (HCNGC224)
                if (!dec.endsWith("\"")) { // Sometimes " is missing (HCNGC339)
                    dec = dec + "\"";
                }
                dec = dec.replace('.', '\''); // Sometimes ' is given as . (HCNGC467)

                if (target != null) {
                    target.setConstellation(constellation);

                    target.setPosition(new EquPosition(ra, dec));

                    if (target instanceof DeepSkyTarget) { // In case of single star target is not an DeepSkyTarget
                                                           // instance
                        size = size.replaceAll("\'", "");
                        size = size.replaceAll("\"", "");
                        size = size.replaceAll("\u00b0", "");
                        size = size.replaceAll("O", "0"); // Sometimes 0 is given as 'O' (HCNGC7308)
                        size = size.toUpperCase(); // Sometimes the x is lower case :-(
                        if (size.indexOf('&') != -1) { // HCNGC6991 has two size entries divided by &
                            size = size.substring(0, size.indexOf('&') - 1);
                        }
                        if (!"".equals(size.trim())) {
                            if (size.indexOf('X') != -1) { // No (valid) entry (e.g. HCNGC1554)
                                String s_large = size.substring(0, size.indexOf('X'));
                                String s_small = size.substring(size.indexOf('X') + 1);
                                if (!("".equals(s_large.trim()) && ("".equals(s_small.trim())))) { // In case of e.g.
                                                                                                   // HCNGC501 size is
                                                                                                   // empty
                                    double large = Double.parseDouble(s_large);
                                    double small = Double.parseDouble(s_small);
                                    if (small > large) {
                                        double x = small;
                                        small = large;
                                        large = x;
                                    }
                                    ((DeepSkyTarget) target).setLargeDiameter(new Angle(large, Angle.ARCMINUTE));
                                    ((DeepSkyTarget) target).setSmallDiameter(new Angle(small, Angle.ARCMINUTE));
                                }
                            }
                        }

                        if ((vMag != null) && !("".equals(vMag.trim()))) {
                            ((DeepSkyTarget) target).setVisibleMagnitude(FloatUtil.parseFloat(vMag));
                        }
                    }

                    String[] an = aliasNames.split(",");
                    target.setAliasNames(an);

                    // Add target to map and increment counter
                    this.map.put(hcngcNumber, target);
                }
                counter++;

            }
        } catch (IOException ioe) {
            LOGGER.error("Error reading file {}. {} ", toLogMessage(file.getName()), toLogMessage(ioe.toString()));
            return false;
        }

        return true;

    }

    @Override
    public String[] getCatalogIndex() {

        return (String[]) this.map.keySet().toArray(new String[] {});

    }

    @Override
    public AbstractSchemaTableModel getTableModel() {

        return this.tableModel;

    }

}