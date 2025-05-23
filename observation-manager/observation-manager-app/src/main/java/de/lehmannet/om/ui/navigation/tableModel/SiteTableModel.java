/*
 * ====================================================================
 * /navigation/tableModel/SiteTableModel.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.navigation.tableModel;

import de.lehmannet.om.Angle;
import de.lehmannet.om.ISite;
import de.lehmannet.om.Site;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class SiteTableModel extends AbstractSchemaTableModel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final String MODEL_ID = "Site";

    public SiteTableModel(ISite[] sites) {

        this.elements = sites;
    }

    @Override
    public int getColumnCount() {

        return 6;
    }

    @Override
    public String getID() {

        return SiteTableModel.MODEL_ID;
    }

    @Override
    public int getRowCount() {

        if (this.elements == null) {
            return 5;
        }
        return this.elements.length;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {

        Class<?> c = null;

        switch (columnIndex) {
            case 0: {
                c = Site.class;
                break;
            }
            case 1:
            case 2: {
                c = Angle.class;
                break;
            }
            case 3: {
                c = Float.class;
                break;
            }
            case 4: {
                c = String.class;
                break;
            }
            case 5: {
                c = Integer.class;
                break;
            }
        }

        return c;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        Object value = null;

        if (this.elements == null) {
            return null;
        }

        ISite site = (ISite) this.elements[rowIndex];

        switch (columnIndex) {
            case 0: {
                value = site;
                break;
            }
            case 1: {
                value = site.getLatitude();
                break;
            }
            case 2: {
                value = site.getLongitude();
                break;
            }
            case 3: {
                value = site.getElevation();
                break;
            }
            case 4: {
                value = site.getIAUCode();
                break;
            }
            case 5: {
                value = site.getTimezone();
                break;
            }
        }

        return value;
    }

    @Override
    public String getColumnName(int column) {

        String name = "";

        switch (column) {
            case 0: {
                name = AbstractSchemaTableModel.bundle.getString("table.header.site.name");
                break;
            }
            case 1: {
                name = AbstractSchemaTableModel.bundle.getString("table.header.site.latitude");
                break;
            }
            case 2: {
                name = AbstractSchemaTableModel.bundle.getString("table.header.site.longitude");
                break;
            }
            case 3: {
                name = AbstractSchemaTableModel.bundle.getString("table.header.site.elevation");
                break;
            }
            case 4: {
                name = AbstractSchemaTableModel.bundle.getString("table.header.site.iauNo");
                break;
            }
            case 5: {
                name = AbstractSchemaTableModel.bundle.getString("table.header.site.timezone");
                break;
            }
        }

        return name;
    }

    private String getValue(double value) {

        // Output format
        DecimalFormat df = new DecimalFormat("0.00");
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(dfs);

        return df.format(value);
    }
}
