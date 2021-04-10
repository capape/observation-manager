/* ====================================================================
 * /navigation/tableModel/LensTableModel.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.navigation.tableModel;

import java.util.Locale;
import java.util.ResourceBundle;

import de.lehmannet.om.ILens;
import de.lehmannet.om.ui.util.LocaleToolsFactory;

public class LensTableModel extends AbstractSchemaTableModel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final String MODEL_ID = "Lens";

    private final ResourceBundle bundle = LocaleToolsFactory.appInstance().getBundle("ObservationManager",
            Locale.getDefault());

    public LensTableModel(ILens[] lenses) {

        this.elements = lenses;

    }

    @Override
    public int getColumnCount() {

        return 3;

    }

    @Override
    public String getID() {

        return LensTableModel.MODEL_ID;

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
        case 1: {
            c = String.class;
            break;
        }
        case 2: {
            c = Float.class;
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

        ILens lens = (ILens) this.elements[rowIndex];

        switch (columnIndex) {
        case 0: {
            value = lens.getVendor();
            if (value == null) {
                value = "";
            }
            break;
        }
        case 1: {
            value = lens.getModel();
            break;
        }
        case 2: {
            value = lens.getFactor();
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
            name = this.bundle.getString("table.header.lens.vendor");
            break;
        }
        case 1: {
            name = this.bundle.getString("table.header.lens.model");
            break;
        }
        case 2: {
            name = this.bundle.getString("table.header.lens.factor");
            break;
        }
        }

        return name;

    }

}
