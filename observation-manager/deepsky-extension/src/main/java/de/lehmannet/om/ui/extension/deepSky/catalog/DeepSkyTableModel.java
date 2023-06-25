/*
 * ====================================================================
 * /extension/deepSky/catalog/DeepSkyTableModel.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.deepSky.catalog;

import java.util.Locale;
import java.util.ResourceBundle;

import de.lehmannet.om.EquPosition;
import de.lehmannet.om.ICloneable;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.ui.catalog.IListableCatalog;
import de.lehmannet.om.ui.navigation.tableModel.AbstractSchemaTableModel;

public class DeepSkyTableModel extends AbstractSchemaTableModel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final String MODEL_ID = "DeepSky";

    private static ResourceBundle bundle = ResourceBundle.getBundle("de.lehmannet.om.ui.extension.deepSky.DeepSky",
            Locale.getDefault());

    public DeepSkyTableModel(IListableCatalog catalog) {

        String[] index = catalog.getCatalogIndex();
        ITarget[] targets = new ITarget[index.length];
        for (int i = 0; i < index.length; i++) {
            targets[i] = catalog.getTarget(index[i]);
        }

        this.elements = ICloneable.copyToList(targets)
                .toArray(new ISchemaElement[targets == null ? 0 : targets.length]);

    }

    @Override
    public String getID() {

        return DeepSkyTableModel.MODEL_ID;

    }

    @Override
    public int getColumnCount() {

        return 4;

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

        switch (columnIndex) {
            case 0: {
                value = target.getName();
                break;
            }
            case 1: {
                String[] aliasNames = target.getAliasNames();
                if ((aliasNames == null) || aliasNames.length == 0) {
                    value = "";
                    break;
                }
                StringBuilder alias = new StringBuilder();
                for (int i = 0; i < aliasNames.length; i++) {
                    alias.append(aliasNames[i]);
                    if (i < aliasNames.length - 1) {
                        alias.append(", ");
                    }
                }
                value = alias.toString();
                break;
            }
            case 2: {
                EquPosition pos = target.getPosition();
                if (pos != null) {
                    value = pos.getRa();
                }
                break;
            }
            case 3: {
                EquPosition pos = target.getPosition();
                if (pos != null) {
                    value = pos.getDec();
                }
                break;
            }
            default:
                break;
        }

        return value;

    }

    @Override
    public String getColumnName(int column) {

        String name;

        switch (column) {
            case 0: {
                name = DeepSkyTableModel.bundle.getString("catalog.table.columnHeader.name");
                break;
            }
            case 1: {
                name = DeepSkyTableModel.bundle.getString("catalog.table.columnHeader.aliasNames");
                break;
            }
            case 2: {
                name = DeepSkyTableModel.bundle.getString("catalog.table.columnHeader.ra");
                break;
            }
            case 3: {
                name = DeepSkyTableModel.bundle.getString("catalog.table.columnHeader.dec");
                break;
            }
            default: {
                name = "";
                break;
            }
        }

        return name;

    }

    @Override
    public int getColumnSize(int columnIndex) {

        switch (columnIndex) {
            case 0:
                return 95;

            case 1:
                return 300;

            case 2:
            case 3:
                return 100;
            default:
                return super.getColumnSize(columnIndex);
        }

    }

    public static void reloadLanguage() {

        DeepSkyTableModel.bundle = ResourceBundle.getBundle("de.lehmannet.om.ui.extension.deepSky.DeepSky",
                Locale.getDefault());

    }

}
