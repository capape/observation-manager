/*
 * ====================================================================
 * /navigation/tableModel/ScopeTableModel.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.navigation.tableModel;

import de.lehmannet.om.Angle;
import de.lehmannet.om.IScope;

public class ScopeTableModel extends AbstractSchemaTableModel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final String MODEL_ID = "Scope";

    public ScopeTableModel(IScope[] scopes) {

        this.elements = scopes;

    }

    @Override
    public int getColumnCount() {

        return 8;

    }

    @Override
    public String getID() {

        return ScopeTableModel.MODEL_ID;

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
            case 0:
            case 4:
            case 1: {
                c = String.class;
                break;
            }
            case 2:
            case 6:
            case 5:
            case 3: {
                c = Float.class;
                break;
            }
            case 7: {
                c = Angle.class;
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

        IScope scope = (IScope) this.elements[rowIndex];

        switch (columnIndex) {
            case 0: {
                value = scope.getVendor();
                break;
            }
            case 1: {
                value = scope.getModel();
                break;
            }
            case 2: {
                value = scope.getAperture();
                break;
            }
            case 3: {
                value = scope.getFocalLength();
                break;
            }
            case 4: {
                value = scope.getType();
                break;
            }
            case 5: {
                value = scope.getLightGrasp();
                break;
            }
            case 6: {
                value = scope.getMagnification();
                break;
            }
            case 7: {
                Angle angle = scope.getTrueFieldOfView();
                if (angle != null) {
                    angle.toDegree();
                    value = angle;
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
                name = AbstractSchemaTableModel.bundle.getString("table.header.scope.vendor");
                break;
            }
            case 1: {
                name = AbstractSchemaTableModel.bundle.getString("table.header.scope.model");
                break;
            }
            case 2: {
                name = AbstractSchemaTableModel.bundle.getString("table.header.scope.aperture");
                break;
            }
            case 3: {
                name = AbstractSchemaTableModel.bundle.getString("table.header.scope.focalLength");
                break;
            }
            case 4: {
                name = AbstractSchemaTableModel.bundle.getString("table.header.scope.type");
                break;
            }
            case 5: {
                name = AbstractSchemaTableModel.bundle.getString("table.header.scope.lightGrasp");
                break;
            }
            case 6: {
                name = AbstractSchemaTableModel.bundle.getString("table.header.scope.magnification");
                break;
            }
            case 7: {
                name = AbstractSchemaTableModel.bundle.getString("table.header.scope.trueFoV");
                break;
            }
        }

        return name;

    }

}
