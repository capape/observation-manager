/* ====================================================================
 * /extension/deepSky/catalog/CaldwellCatalog.java
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
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

public class CaldwellCatalog implements IListableCatalog {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaldwellCatalog.class);

    private static final String CATALOG_NAME = "Caldwell";

    private static final String CATALOG_ABB = "C";

    private static final String DATASOURCE_ORIGIN = "ObservationManager - Caldwell Catalog 1.0";

    // Key = Caldwell Number
    // Value = ITarget
    private final LinkedHashMap<String, ITarget> map = new LinkedHashMap<>();

    private AbstractSchemaTableModel tableModel = null;

    public CaldwellCatalog(File file) {

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
    public String getName() {

        return CaldwellCatalog.CATALOG_NAME;

    }

    @Override
    public String getAbbreviation() {

        return CaldwellCatalog.CATALOG_ABB;

    }

    @Override
    public ITarget getTarget(String caldwellNumber) {

        return (DeepSkyTarget) this.map.get(caldwellNumber);

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

        Reader reader = null;

        try {
            // Must read UTF-8 as we run into problems on some OS
            reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);

        } catch (FileNotFoundException fnfe) {
            LOGGER.error("File not found: {} ", file, fnfe);
            return false;
        }

        try (BufferedReader bufferedReader = new BufferedReader(reader);) {
            String line = null;
            String caldwellNumber = null;
            DeepSkyTarget target = null;
            StringTokenizer tokenizer = null;
            while ((line = bufferedReader.readLine()) != null) {
                if ((line.startsWith("#")) || ("".equals(line.trim()))) { // Comment or empty line
                    // line = bufferedReader.readLine();
                    continue;
                }
                caldwellNumber = line.substring(0, line.indexOf(';'));

                target = null;

                tokenizer = new StringTokenizer(line, ";");

                tokenizer.nextToken(); // Skip first token (Caldwell number)
                String ngc = tokenizer.nextToken();
                String constellation = tokenizer.nextToken();
                String ra = tokenizer.nextToken();
                String dec = tokenizer.nextToken();
                String mag = tokenizer.nextToken();
                String size = tokenizer.nextToken();
                String type = tokenizer.nextToken();

                if ("DN".equals(type)) {
                    target = new DeepSkyTargetDN(caldwellNumber, CaldwellCatalog.DATASOURCE_ORIGIN);
                } else if ("NA".equals(type)) {
                    target = new DeepSkyTargetNA(caldwellNumber, CaldwellCatalog.DATASOURCE_ORIGIN);
                } else if ("DS".equals(type)) {
                    target = new DeepSkyTargetDS(caldwellNumber, CaldwellCatalog.DATASOURCE_ORIGIN);
                } else if ("GC".equals(type)) {
                    target = new DeepSkyTargetGC(caldwellNumber, CaldwellCatalog.DATASOURCE_ORIGIN);
                } else if ("GN".equals(type)) {
                    target = new DeepSkyTargetGN(caldwellNumber, CaldwellCatalog.DATASOURCE_ORIGIN);

                    String nebulaType = tokenizer.nextToken();
                    if ((nebulaType != null) && !("".equals(nebulaType.trim()))) {
                        ((DeepSkyTargetGN) target).setNebulaType(nebulaType);
                    }
                } else if ("GX".equals(type)) {
                    target = new DeepSkyTargetGX(caldwellNumber, CaldwellCatalog.DATASOURCE_ORIGIN);
                } else if ("OC".equals(type)) {
                    target = new DeepSkyTargetOC(caldwellNumber, CaldwellCatalog.DATASOURCE_ORIGIN);
                } else if ("PN".equals(type)) {
                    target = new DeepSkyTargetPN(caldwellNumber, CaldwellCatalog.DATASOURCE_ORIGIN);
                } else if ("QS".equals(type)) {
                    target = new DeepSkyTargetQS(caldwellNumber, CaldwellCatalog.DATASOURCE_ORIGIN);
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

                    if ((mag != null) && !("".equals(mag.trim()))) {
                        target.setVisibleMagnitude(FloatUtil.parseFloat(mag));
                    }

                    if (!"".equals(ngc.trim())) {
                        target.addAliasName(ngc);
                    }

                    Iterator<String> iterator = aliasNames.iterator();
                    String nextEntry = null;
                    while (iterator.hasNext()) {
                        nextEntry = iterator.next();
                        if (!"".equals(nextEntry.trim())) {
                            target.addAliasName(nextEntry);
                        }
                    }

                    this.map.put(caldwellNumber, target);
                }
            }

        } catch (IOException ioe) {
            LOGGER.error("Error reading file {} ", file, ioe);
            return false;
        }

        return true;

    }

}