/* ====================================================================
 * /extension/deepSky/catalog/AbstractNGCICCatalog
 *
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.lehmannet.om.Angle;
import de.lehmannet.om.Constellation;
import de.lehmannet.om.EquPosition;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.SurfaceBrightness;
import de.lehmannet.om.TargetStar;
import de.lehmannet.om.extension.deepSky.DeepSkyTarget;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetAS;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetDN;
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

public abstract class AbstractNGCICCatalog implements IListableCatalog {

    // Key = NGC/IC Number
    // Value = ITarget
    private final Map<String, ITarget> map = new LinkedHashMap<>();

    private AbstractSchemaTableModel tableModel = null;

    AbstractNGCICCatalog(File catalogFile) {

        // Load targets into memory.
        // In case or problems aboard
        if (!this.loadTargets(catalogFile)) {
            return;
        }

        // Do this after the targets are loaded
        this.tableModel = new DeepSkyTableModel(this);

    }

    @Override
    public AbstractSchemaTableModel getTableModel() {

        return this.tableModel;

    }

    @Override
    public AbstractSearchPanel getSearchPanel() {

        return new GenericListableCatalogSearchPanel(this);

    }

    @Override
    public ITarget getTarget(String objectName) {

        return (ITarget) this.map.get(objectName);

    }

    @Override
    public ITarget[] getTargets() {

        return (ITarget[]) this.map.values().toArray(new ITarget[] {});

    }

    @Override
    public String[] getCatalogIndex() {

        return (String[]) this.map.keySet().toArray(new String[] {});

    }

    @Override
    public abstract String getAbbreviation();

    @Override
    public abstract String getName();

    private boolean loadTargets(File file) {

        // Check catalog file
        Reader reader = null;
        BufferedReader bufferedReader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(file));
            bufferedReader = new BufferedReader(reader);
        } catch (FileNotFoundException fnfe) {
            System.err.println("File not found: " + file);
            return false;
        }

        // Line Data
        String catalogPrefix = null;
        String catalogNumber = null;
        String extension = null;
        String components = null;
        String constellation = null;
        Constellation con = null;
        String ra = null;
        String dec = null;
        EquPosition position = null;
        String vMag = null;
        String surfaceBrightness = null;
        String diameterLage = null;
        String diameterSmall = null;
        String positionAngle = null;
        String type = null;
        String pgcNumber = null;
        List<String> aliasNames = new ArrayList<>();

        // Get each line and create target
        String line = null;
        String[] tokens = null;
        ITarget target = null;
        int index = 0;

        try {
            line = bufferedReader.readLine();
        } catch (IOException ioe) {
            System.err.println("Error reading first line from: " + file + "\n Catalog cannot be loaded.");
            return false;
        }

        while (line != null) {

            // --------------- Line parsing start

            if ("".equals(line.trim())) { // Empty line.
                continue;
            }

            // Split line into all 34 elements
            tokens = line.split(";", 34);

            // Get NGC or IC prefix
            catalogPrefix = tokens[index++];
            if ("N".equals(catalogPrefix)) {
                catalogPrefix = "NGC";
            } else {
                catalogPrefix = "IC";
            }

            // Build catalogNumber (like NGC123)
            catalogNumber = catalogPrefix + tokens[index++];

            // Catalog Number extension like A,B (if any)
            extension = tokens[index++];
            if (!"".equals(extension)) {
                catalogNumber = catalogNumber + extension;
            }

            // Catalog Number extension like A,B (if any)
            components = tokens[index++];
            if (!"".equals(components)) {
                catalogNumber = catalogNumber + "-" + components;
            }

            // We're not interested in the next 3 entries
            // - Dreyer object (seen/listed by Dreyer?)
            // - Status of identification
            // - Precision flag
            index = index + 3;

            // Constellation
            constellation = tokens[index++];
            con = Constellation.getConstellationByAbbOrName(constellation);

            // Right ascension
            ra = tokens[index++] + EquPosition.RA_HOUR + tokens[index++] + EquPosition.RA_MIN + tokens[index++]
                    + EquPosition.RA_SEC;
            ra = ra.replace(',', '.'); // Second value can contain , a decimal separator

            // Declination
            dec = tokens[index++] + tokens[index++] + EquPosition.DEC_DEG + tokens[index++] + EquPosition.DEC_MIN
                    + tokens[index++] + EquPosition.DEC_SEC;

            position = new EquPosition(ra, dec);

            // Photographic (blue) magnitude (Bmag) is not of interest for us
            index++;

            // Visual magnitude
            vMag = tokens[index++];
            vMag = vMag.replace(',', '.');

            // Surface Brightness
            surfaceBrightness = tokens[index++];
            surfaceBrightness = surfaceBrightness.replace(',', '.');

            // Diameter
            diameterLage = tokens[index++];
            diameterLage = diameterLage.replace(',', '.');
            diameterSmall = tokens[index++];
            diameterSmall = diameterSmall.replace(',', '.');

            // Position angle
            positionAngle = tokens[index++];

            // Type (Galaxy, star, ...)
            type = tokens[index++];

            // PGC number + Aliasnames
            pgcNumber = tokens[index++];
            if (!"".equals(pgcNumber)) {
                aliasNames.add("PGC " + pgcNumber);
            }

            // Add all remaining aliasNames
            for (; index < tokens.length; index++) {
                if (!"".equals(tokens[index])) {
                    aliasNames.add(tokens[index]);
                } else {
                    break; // After the first empty token, there won't be additional tokens
                }
            }

            // --------------- Line parsing done

            // --------------- Create ITarget object depending on type

            // Stars
            if (type.startsWith("*")) {
                if ("*".equals(type)) { // Single star

                    target = new TargetStar(catalogNumber, this.getName());
                    if (!"".equals(vMag)) {
                        ((TargetStar) target).setMagnitudeApparent(FloatUtil.parseFloat(vMag));
                    }

                } else if ("*2".equals(type)) { // Double star

                    target = new DeepSkyTargetDS(catalogNumber, this.getName());
                    if (!"".equals(positionAngle)) {
                        ((DeepSkyTargetDS) target).setPositionAngle(Integer.parseInt(positionAngle));
                    }

                } else { // Asterism (Multiple star system cannot be created
                         // due to insufficient information from catalog)
                         // therefore everything after double star is an asterism

                    target = new DeepSkyTargetAS(catalogNumber, this.getName());
                    if (!"".equals(positionAngle)) {
                        ((DeepSkyTargetAS) target).setPositionAngle(Integer.parseInt(positionAngle));
                    }

                }
            } else if (type.startsWith("OCL") // Open Cluster (check with startsWith as sometimes it's OCL+EN (NGC 361)
                                              // )
                    || type.startsWith("I1") || type.startsWith("I2") || type.startsWith("I3") || type.startsWith("II1")
                    || type.startsWith("II2") || type.startsWith("II3") || type.startsWith("III1")
                    || type.startsWith("III2") || type.startsWith("III3") || type.startsWith("IV1")
                    || type.startsWith("IV2") || type.startsWith("IV3")) {

                target = new DeepSkyTargetOC(catalogNumber, this.getName());
                if (type.startsWith("I1") // Truempler classification
                        || type.startsWith("I2") || type.startsWith("I3") || type.startsWith("II1")
                        || type.startsWith("II2") || type.startsWith("II3") || type.startsWith("III1")
                        || type.startsWith("III2") || type.startsWith("III3") || type.startsWith("IV1")
                        || type.startsWith("IV2") || type.startsWith("IV3") || type.startsWith("OCL")) {
                    ((DeepSkyTargetOC) target).setClusterClassification(type);
                }

            } else if ("GCL".equals(type) // Globular Cluster
                    || "I".equals(type) || "II".equals(type) || "III".equals(type) || "IV".equals(type)
                    || "V".equals(type) || "VI".equals(type) || "VII".equals(type) || "VIII".equals(type)
                    || "IX".equals(type) || "X".equals(type) || "XI".equals(type) || "XII".equals(type)) {

                target = new DeepSkyTargetGC(catalogNumber, this.getName());
                if ("I".equals(type) // Shapley Sawyer Globular Cluster Concentration
                        || "II".equals(type) || "III".equals(type) || "IV".equals(type) || "V".equals(type)
                        || "VI".equals(type) || "VII".equals(type) || "VIII".equals(type) || "IX".equals(type)
                        || "X".equals(type) || "XI".equals(type) || "XII".equals(type)) {
                    ((DeepSkyTargetGC) target).setConcentration(type);
                }

            } else if ("DN".equals(type)) { // Dark nebulae

                target = new DeepSkyTargetDN(catalogNumber, this.getName());
                if (!"".equals(positionAngle)) {
                    ((DeepSkyTargetDN) target).setPositionAngle(Integer.parseInt(positionAngle));
                }

            } else if ("EM".equals(type) // Emission & reflection nebulae / Supernova remnant
                    || "EN".equals(type) || "RN".equals(type) || "SNR".equals(type) // Make sure to check SNR before
                                                                                    // galaxies!
            ) {

                target = new DeepSkyTargetGN(catalogNumber, this.getName());
                if (!"".equals(positionAngle)) {
                    ((DeepSkyTargetGN) target).setPositionAngle(Integer.parseInt(positionAngle));
                }
                if ("EM".equals(type) || "EN".equals(type)) {
                    ((DeepSkyTargetGN) target).setNebulaType("Emission nebula");
                }
                if ("RN".equals(type)) {
                    ((DeepSkyTargetGN) target).setNebulaType("Reflection nebula");
                }
                if ("SNR".equals(type)) {
                    ((DeepSkyTargetGN) target).setNebulaType(" 	Supernova remnant");
                }

            } else if ("NF".equals(type)) { // Not Found / Unknown type

                target = new DeepSkyTargetNA(catalogNumber, this.getName());

            } else if ("PN".equals(type)) { // Planetary nebluae

                target = new DeepSkyTargetPN(catalogNumber, this.getName());

            } else if ("GxyP".equals(type)) { // Planetary nebluae

                target = new DeepSkyTargetNA(catalogNumber, this.getName());
                target.setNotes("Part of galaxy (e.g. bright HII region)");

            } else if ((type.startsWith("C")) // All kinds of Galaxies
                    || (type.startsWith("D")) // As we check with startWith()...
                    || (type.startsWith("E")) // make sure we call this as last option
                    || (type.startsWith("I")) || (type.startsWith("P")) || (type.startsWith("R"))
                    || (type.startsWith("PRG")) || (type.startsWith("S")) || (type.startsWith("c"))
                    || (type.startsWith("d")) || (type.startsWith("5C")) || (type.startsWith("4S"))
                    || (type.startsWith("3S"))) {

                target = new DeepSkyTargetGX(catalogNumber, this.getName());
                if (!"".equals(positionAngle)) {
                    ((DeepSkyTargetGX) target).setPositionAngle(Integer.parseInt(positionAngle));
                }
                ((DeepSkyTargetGX) target).setHubbleType(type);

            } else { // Should never get here!

                target = new DeepSkyTargetNA(catalogNumber, this.getName());

            }

            // --------------- Set all common target values

            if (target instanceof DeepSkyTarget) { // e.g. TargetStar is not a DeepSkyTarget

                // Set all DeepSkyTarget parameters

                // Visual magnitude
                if (!"".equals(vMag)) {
                    ((DeepSkyTarget) target).setVisibleMagnitude(Float.parseFloat(vMag));
                }

                // Surface brightness
                if (!"".equals(surfaceBrightness)) {
                    SurfaceBrightness sb = new SurfaceBrightness(Float.parseFloat(surfaceBrightness),
                            SurfaceBrightness.MAGS_SQR_ARC_MIN);
                    ((DeepSkyTarget) target).setSurfaceBrightness(sb);
                }

                // Large Diameter
                if (!"".equals(diameterLage)) {
                    ((DeepSkyTarget) target)
                            .setLargeDiameter(new Angle(Float.parseFloat(diameterLage), Angle.ARCMINUTE));
                }

                // Small Diameter
                if (!"".equals(diameterSmall)) {
                    ((DeepSkyTarget) target)
                            .setSmallDiameter(new Angle(Float.parseFloat(diameterSmall), Angle.ARCMINUTE));
                }

            }

            // Alias names
            target.setAliasNames((String[]) aliasNames.toArray(new String[] {}));

            // Constellation
            target.setConstellation(con);

            // Position
            target.setPosition(position);

            // Add target to map and increment counter
            this.map.put(catalogNumber, target);

            // --------------- Read next line
            try {
                line = bufferedReader.readLine();
            } catch (IOException ioe) {
                System.err.println("Error reading line from: " + file
                        + "\n Catalog cannot be loaded. Last successful line was: " + catalogNumber);
                return false;
            }

            // --------------- Reset values for next round
            index = 0;
            catalogPrefix = null;
            catalogNumber = null;
            extension = null;
            components = null;
            constellation = null;
            con = null;
            ra = null;
            dec = null;
            position = null;
            vMag = null;
            surfaceBrightness = null;
            diameterLage = null;
            diameterSmall = null;
            positionAngle = null;
            type = null;
            pgcNumber = null;
            aliasNames.clear();

        }

        return true;

    }

}
