/*
 * ====================================================================
 * /extension/deepSky/catalog/MessierCatalog.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.deepSky.catalog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.lehmannet.om.Angle;
import de.lehmannet.om.EquPosition;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.extension.deepSky.DeepSkyTarget;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetDN;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetDS;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetGC;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetGN;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetGX;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetNA;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetOC;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetPN;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetQS;
import de.lehmannet.om.ui.catalog.IListableCatalog;
import de.lehmannet.om.ui.navigation.tableModel.AbstractSchemaTableModel;
import de.lehmannet.om.ui.panel.AbstractSearchPanel;
import de.lehmannet.om.ui.panel.GenericListableCatalogSearchPanel;
import de.lehmannet.om.util.FloatUtil;

public class MessierCatalog implements IListableCatalog {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessierCatalog.class);

    private static final String CATALOG_NAME = "Messier";

    private static final String CATALOG_ABB = "M";

    private static final String DATASOURCE_ORIGIN = "ObservationManager - Messier Catalog 1.0";

    // Key = Messier Number
    // Value = Target
    private final Map<String, ITarget> map = new LinkedHashMap<>();

    private AbstractSchemaTableModel tableModel = null;

    public MessierCatalog(File file) {

        // Load targets into memory.
        // In case or problems aboard
        if (!this.loadTargets(file)) {
            return;
        }

        this.tableModel = new DeepSkyTableModel(this);

    }

    @Override
    public AbstractSchemaTableModel getTableModel() {

        return this.tableModel;

    }

    @Override
    public String[] getCatalogIndex() {

        return (String[]) this.map.keySet().toArray(new String[] {});

    }

    @Override
    public String getAbbreviation() {

        return MessierCatalog.CATALOG_ABB;

    }

    @Override
    public String getName() {

        return MessierCatalog.CATALOG_NAME;

    }

    @Override
    public ITarget getTarget(String messierNumber) {

        return (DeepSkyTarget) this.map.get(messierNumber);

    }

    @Override
    public ITarget[] getTargets() {

        return (ITarget[]) this.map.values().toArray(new ITarget[0]);

    }

    @Override
    public AbstractSearchPanel getSearchPanel() {

        return new GenericListableCatalogSearchPanel(this);

    }

    private boolean loadTargets(File file) {

        // Read file
        Reader reader = null;

        try {
            // Must read UTF-8 as we run into problems on some OS
            reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);

        } catch (FileNotFoundException fnfe) {
            LOGGER.error("File not found: {}", file, fnfe);
            return false;
        }

        // Read line(s)
        String line = null;
        String messierNumber = null;
        DeepSkyTarget target = null;
        StringTokenizer tokenizer = null;
        try (BufferedReader bufferedReader = new BufferedReader(reader);) {
            while ((line = bufferedReader.readLine()) != null) {
                if ((line.startsWith("#")) || ("".equals(line.trim()))) { // Comment or empty line
                    // line = bufferedReader.readLine();
                    continue;
                }
                messierNumber = line.substring(0, line.indexOf(','));

                target = null;

                tokenizer = new StringTokenizer(line, ",");

                tokenizer.nextToken(); // Skip first token (Messier number)
                String ngc = tokenizer.nextToken();
                String constellation = tokenizer.nextToken();
                String ra = tokenizer.nextToken();
                String dec = tokenizer.nextToken();
                String mag = tokenizer.nextToken();
                String size = tokenizer.nextToken();
                String type = tokenizer.nextToken();

                if ("DN".equals(type)) {
                    target = new DeepSkyTargetDN(messierNumber, MessierCatalog.DATASOURCE_ORIGIN);
                } else if ("NA".equals(type)) {
                    target = new DeepSkyTargetNA(messierNumber, MessierCatalog.DATASOURCE_ORIGIN);
                } else if ("DS".equals(type)) {
                    target = new DeepSkyTargetDS(messierNumber, MessierCatalog.DATASOURCE_ORIGIN);

                    String separation = tokenizer.nextToken();
                    if ((separation != null) && !("".equals(separation.trim()))) {
                        try {
                            double s = Double.parseDouble(separation);
                            ((DeepSkyTargetDS) target).setSeparation(new Angle(s, Angle.ARCSECOND));
                        } catch (NumberFormatException nfe) {
                            LOGGER.error("Malformed entry: {} - Separation is: {} ", messierNumber, separation, nfe);
                        }
                    }

                    String positionAngle = tokenizer.nextToken();
                    if ((positionAngle != null) && !("".equals(positionAngle.trim()))) {
                        try {
                            int pa = Integer.parseInt(positionAngle);
                            ((DeepSkyTargetDS) target).setPositionAngle(pa);
                        } catch (NumberFormatException nfe) {
                            LOGGER.error("Malformed entry:{}  - Position Angle is: {} ", messierNumber, positionAngle);
                        }
                    }

                    String companionStar = tokenizer.nextToken();
                    if ((companionStar != null) && !("".equals(companionStar.trim()))) {
                        try {
                            double cs = Double.parseDouble(companionStar);
                            ((DeepSkyTargetDS) target).setCompanionMag(cs);
                        } catch (NumberFormatException nfe) {
                            LOGGER.error(
                                    "Malformed entry: " + messierNumber + " - Companion star mag is: " + companionStar);
                        }
                    }

                } else if ("GC".equals(type)) {
                    target = new DeepSkyTargetGC(messierNumber, MessierCatalog.DATASOURCE_ORIGIN);

                    String brightestStar = tokenizer.nextToken();
                    if ((brightestStar != null) && !("".equals(brightestStar.trim()))) {
                        try {
                            double bs = Double.parseDouble(brightestStar);
                            ((DeepSkyTargetGC) target).setMagnitude(bs);
                        } catch (NumberFormatException nfe) {
                            LOGGER.error(
                                    "Malformed entry: " + messierNumber + " - Brightest stars is: " + brightestStar);
                        }
                    }

                    String concentration = tokenizer.nextToken();
                    if ((concentration != null) && !("".equals(concentration.trim()))) {
                        ((DeepSkyTargetGC) target).setConcentration(concentration);
                    }
                } else if ("GN".equals(type)) {
                    target = new DeepSkyTargetGN(messierNumber, MessierCatalog.DATASOURCE_ORIGIN);

                    String nebulaType = tokenizer.nextToken();
                    if ((nebulaType != null) && !("".equals(nebulaType.trim()))) {
                        ((DeepSkyTargetGN) target).setNebulaType(nebulaType);
                    }

                    String positionAngle = tokenizer.nextToken();
                    if ((positionAngle != null) && !("".equals(positionAngle.trim()))) {
                        try {
                            int pa = Integer.parseInt(positionAngle);
                            ((DeepSkyTargetGN) target).setPositionAngle(pa);
                        } catch (NumberFormatException nfe) {
                            LOGGER.error("Malformed entry: {}  - Position Angle is: {} ", messierNumber, positionAngle);
                        }
                    }
                } else if ("GX".equals(type)) {
                    target = new DeepSkyTargetGX(messierNumber, MessierCatalog.DATASOURCE_ORIGIN);

                    String hubbleType = tokenizer.nextToken();
                    if ((hubbleType != null) && !("".equals(hubbleType.trim()))) {
                        ((DeepSkyTargetGX) target).setHubbleType(hubbleType);
                    }

                    String positionAngle = tokenizer.nextToken();
                    if ((positionAngle != null) && !("".equals(positionAngle.trim()))) {
                        try {
                            int pa = Integer.parseInt(positionAngle);
                            ((DeepSkyTargetGX) target).setPositionAngle(pa);
                        } catch (NumberFormatException nfe) {
                            LOGGER.error("Malformed entry:{}  - Position Angle is: {} ", messierNumber, positionAngle);
                        }
                    }
                } else if ("OC".equals(type)) {
                    target = new DeepSkyTargetOC(messierNumber, MessierCatalog.DATASOURCE_ORIGIN);

                    String brightestStar = tokenizer.nextToken();
                    if ((brightestStar != null) && !("".equals(brightestStar.trim()))) {
                        try {
                            double bs = Double.parseDouble(brightestStar);
                            ((DeepSkyTargetOC) target).setBrightestStar(bs);
                        } catch (NumberFormatException nfe) {
                            LOGGER.error("Malformed entry: {} - Brightest star is: ", messierNumber, brightestStar);
                        }
                    }

                    String amount = tokenizer.nextToken();
                    if ((amount != null) && !("".equals(amount.trim()))) {
                        try {
                            int a = Integer.parseInt(amount);
                            ((DeepSkyTargetOC) target).setAmountOfStars(a);
                        } catch (NumberFormatException nfe) {
                            LOGGER.error("Malformed entry: {} - Amount of stars is: ", messierNumber, amount);
                        }
                    }

                    String trumpler = tokenizer.nextToken();
                    if ((trumpler != null) && !("".equals(trumpler.trim()))) {
                        ((DeepSkyTargetOC) target).setClusterClassification(trumpler);
                    }
                } else if ("PN".equals(type)) {
                    target = new DeepSkyTargetPN(messierNumber, MessierCatalog.DATASOURCE_ORIGIN);

                    String centralStar = tokenizer.nextToken();
                    if ((centralStar != null) && !("".equals(centralStar.trim()))) {
                        try {
                            double cs = Double.parseDouble(centralStar);
                            ((DeepSkyTargetPN) target).setCentralStarMagnitude(cs);
                        } catch (NumberFormatException nfe) {
                            LOGGER.error("Malformed entry: {}- Central star is: ", messierNumber, centralStar);
                        }
                    }
                } else if ("QS".equals(type)) {
                    target = new DeepSkyTargetQS(messierNumber, MessierCatalog.DATASOURCE_ORIGIN);
                }

                List<String> aliasNames = new ArrayList<>();
                while (tokenizer.hasMoreTokens()) {
                    aliasNames.add(tokenizer.nextToken());
                }

                if (target != null) {
                    target.setConstellation(constellation);
                    target.setPosition(new EquPosition(ra, dec));

                    if (size.indexOf('x') != -1) {
                        double large = Double.parseDouble(size.substring(0, size.indexOf('x')));
                        double small = Double.parseDouble(size.substring(size.indexOf('x') + 1));
                        if (small > large) {
                            double x = small;
                            small = large;
                            large = x;
                        }
                        target.setLargeDiameter(new Angle(large, Angle.ARCMINUTE));
                        target.setSmallDiameter(new Angle(small, Angle.ARCMINUTE));
                    } else {
                        target.setLargeDiameter(new Angle(Double.parseDouble(size), Angle.ARCMINUTE));
                        target.setSmallDiameter(new Angle(Double.parseDouble(size), Angle.ARCMINUTE));
                    }

                    target.setVisibleMagnitude(FloatUtil.parseFloat(mag));
                    target.addAliasName(ngc);
                    for (Object aliasName : aliasNames) {
                        target.addAliasName((String) aliasName);
                    }

                    this.map.put(messierNumber, target);
                }

            }

        } catch (IOException ioe) {
            LOGGER.error("Error reading file {} ", file, ioe);
            return false;
        }

        return true;

    }

}