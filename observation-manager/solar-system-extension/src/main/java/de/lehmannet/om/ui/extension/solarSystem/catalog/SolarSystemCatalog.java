/*
 * ====================================================================
 * /extension/solarSystem/catalog/SolarSystemCatalog.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.solarSystem.catalog;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import de.lehmannet.om.ITarget;
import de.lehmannet.om.extension.solarSystem.SolarSystemTarget;
import de.lehmannet.om.extension.solarSystem.SolarSystemTargetMoon;
import de.lehmannet.om.extension.solarSystem.SolarSystemTargetPlanet;
import de.lehmannet.om.extension.solarSystem.SolarSystemTargetSun;
import de.lehmannet.om.ui.catalog.IListableCatalog;
import de.lehmannet.om.ui.navigation.tableModel.AbstractSchemaTableModel;
import de.lehmannet.om.ui.panel.AbstractSearchPanel;

public class SolarSystemCatalog implements IListableCatalog {

    private static final String CATALOG_NAME = "Solar System";

    private static final String CATALOG_ABB = "";

    private static final String DATASOURCE_ORIGIN = "ObservationManager - SolarSystem Catalog 1.0";

    // Key IDs for major solar system bodies
    /*
     * public static final String KEY_SUN = "SUN"; public static final String KEY_MERCURY = "MERCURY"; public static
     * final String KEY_VENUS = "VENUS"; public static final String KEY_EARTH = "EARTH"; public static final String
     * KEY_MOON = "MOON"; public static final String KEY_MARS = "MARS"; public static final String KEY_JUPITER =
     * "JUPITER"; public static final String KEY_SATURN = "SATURN"; public static final String KEY_URANUS = "URANUS";
     * public static final String KEY_NEPTUNE = "NEPTUNE";
     */

    // Key = Name
    // Value = Target
    private final Map<String, ITarget> map = new LinkedHashMap<>();

    private AbstractSchemaTableModel tableModel = null;
    private Locale lastKnownDefaultLocale = Locale.getDefault();

    public SolarSystemCatalog() {

        this.fillCatalog();

        this.tableModel = new SolarSystemTableModel(this);

    }

    @Override
    public String getName() {

        return SolarSystemCatalog.CATALOG_NAME;

    }

    @Override
    public String getAbbreviation() {

        return SolarSystemCatalog.CATALOG_ABB;

    }

    @Override
    public ITarget getTarget(String catalogNumber) {

        if (!this.map.containsKey(catalogNumber)) {
            return null;
        }

        return (ITarget) this.map.get(catalogNumber);

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
    public AbstractSchemaTableModel getTableModel() {

        // Check if locale has changed. And if so, reload catalog with now I18N names
        if (!Locale.getDefault().equals(this.lastKnownDefaultLocale)) {
            this.reloadLanguage();
        }

        return this.tableModel;

    }

    @Override
    public AbstractSearchPanel getSearchPanel() {

        return null; // We don't offer a search panel in this catalog, as object names are I18N
                     // relevant

    }

    private void reloadLanguage() {

        this.tableModel = new SolarSystemTableModel(this);
        this.lastKnownDefaultLocale = Locale.getDefault();

    }

    private void fillCatalog() {

        SolarSystemTargetSun sun = new SolarSystemTargetSun(SolarSystemTarget.KEY_SUN,
                SolarSystemCatalog.DATASOURCE_ORIGIN);
        SolarSystemTargetPlanet mecury = new SolarSystemTargetPlanet(SolarSystemTarget.KEY_MERCURY,
                SolarSystemCatalog.DATASOURCE_ORIGIN);
        SolarSystemTargetPlanet venus = new SolarSystemTargetPlanet(SolarSystemTarget.KEY_VENUS,
                SolarSystemCatalog.DATASOURCE_ORIGIN);
        SolarSystemTargetMoon moon = new SolarSystemTargetMoon(SolarSystemTarget.KEY_MOON,
                SolarSystemCatalog.DATASOURCE_ORIGIN);
        SolarSystemTargetPlanet mars = new SolarSystemTargetPlanet(SolarSystemTarget.KEY_MARS,
                SolarSystemCatalog.DATASOURCE_ORIGIN);
        SolarSystemTargetPlanet jupiter = new SolarSystemTargetPlanet(SolarSystemTarget.KEY_JUPITER,
                SolarSystemCatalog.DATASOURCE_ORIGIN);
        SolarSystemTargetPlanet saturn = new SolarSystemTargetPlanet(SolarSystemTarget.KEY_SATURN,
                SolarSystemCatalog.DATASOURCE_ORIGIN);
        SolarSystemTargetPlanet uranus = new SolarSystemTargetPlanet(SolarSystemTarget.KEY_URANUS,
                SolarSystemCatalog.DATASOURCE_ORIGIN);
        SolarSystemTargetPlanet neptune = new SolarSystemTargetPlanet(SolarSystemTarget.KEY_NEPTUNE,
                SolarSystemCatalog.DATASOURCE_ORIGIN);

        this.map.put(sun.getName(), sun);
        this.map.put(mecury.getName(), mecury);
        this.map.put(venus.getName(), venus);
        this.map.put(moon.getName(), moon);
        this.map.put(mars.getName(), mars);
        this.map.put(jupiter.getName(), jupiter);
        this.map.put(saturn.getName(), saturn);
        this.map.put(uranus.getName(), uranus);
        this.map.put(neptune.getName(), neptune);

    }

}

class SolarSystemTableModel extends AbstractSchemaTableModel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final String MODEL_ID = "SolSys";

    public SolarSystemTableModel(SolarSystemCatalog catalog) {

        String[] index = catalog.getCatalogIndex();
        ITarget[] targets = new ITarget[index.length];
        for (int i = 0; i < index.length; i++) {
            targets[i] = catalog.getTarget(index[i]);
        }

        this.elements = targets;

    }

    @Override
    public String getID() {

        return SolarSystemTableModel.MODEL_ID;

    }

    @Override
    public int getColumnCount() {

        return 1;

    }

    @Override
    public int getRowCount() {

        if (this.elements == null) {
            return 5;
        }
        return this.elements.length;

    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        String value = "";

        if (this.elements == null) {
            return value;
        }

        ITarget target = (ITarget) this.elements[rowIndex];

        if (target != null) {
            if (columnIndex == 0) {
                value = target.getDisplayName();
            }
        }

        return value;

    }

    @Override
    public String getColumnName(int column) {

        String name = "";

        if (column == 0) {
            ResourceBundle bundle = ResourceBundle.getBundle("de.lehmannet.om.ui.extension.solarSystem.SolarSystem",
                    Locale.getDefault());
            name = bundle.getString("catalog.table.columnHeader.name");
        }

        return name;

    }

    @Override
    public int getColumnSize(int columnIndex) {

        if (columnIndex == 0) {
            return 100;
        }

        return super.getColumnSize(columnIndex);

    }

}
