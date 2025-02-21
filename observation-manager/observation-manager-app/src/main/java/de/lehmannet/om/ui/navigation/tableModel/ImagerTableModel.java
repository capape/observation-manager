/*
 * ====================================================================
 * /navigation/tableModel/ImagerTableModel.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.navigation.tableModel;

import de.lehmannet.om.IImager;

public class ImagerTableModel extends AbstractSchemaTableModel {

    private static final String MODEL_ID = "Imager";

    public ImagerTableModel(IImager[] imagers) {

        this.elements = imagers;
    }

    @Override
    public int getColumnCount() {

        return 3;
    }

    @Override
    public String getID() {

        return ImagerTableModel.MODEL_ID;
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

        IImager imager = (IImager) this.elements[rowIndex];

        switch (columnIndex) {
            case 0: {
                value = imager.getVendor();
                break;
            }
            case 1: {
                value = imager.getModel();
                break;
            }
            case 2: {
                if ((imager.getRemarks() != null) && (imager.getRemarks().length() > 15)) {
                    value = imager.getRemarks().substring(0, 15);
                } else {
                    value = imager.getRemarks();
                }
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
                name = AbstractSchemaTableModel.bundle.getString("table.header.imager.vendor");
                break;
            }
            case 1: {
                name = AbstractSchemaTableModel.bundle.getString("table.header.imager.model");
                break;
            }
            case 2: {
                name = AbstractSchemaTableModel.bundle.getString("table.header.imager.remarks");
                break;
            }
        }

        return name;
    }
}
