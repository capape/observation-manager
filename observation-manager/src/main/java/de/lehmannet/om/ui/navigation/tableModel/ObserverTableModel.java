/* ====================================================================
 * /navigation/tableModel/ObserverTableModel.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.navigation.tableModel;

import java.util.Iterator;

import de.lehmannet.om.IObserver;

public class ObserverTableModel extends AbstractSchemaTableModel {

    private static final String MODEL_ID = "Observer";

    public ObserverTableModel(IObserver[] observer) {

        super.elements = observer;

    }

    @Override
    public int getColumnCount() {

        return 3;

    }

    @Override
    public String getID() {

        return ObserverTableModel.MODEL_ID;

    }

    @Override
    public int getRowCount() {

        if (super.elements == null) {
            return 5;
        }
        return super.elements.length;

    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        String value = "";

        if (super.elements == null) {
            return value;
        }

        IObserver observer = (IObserver) super.elements[rowIndex];

        switch (columnIndex) {
        case 0: {
            value = observer.getSurname();
            break;
        }
        case 1: {
            value = observer.getName();
            break;
        }
        case 2: {
            Iterator i = observer.getContacts().iterator();
            while (i.hasNext()) {
                value = value + i.next();
                if (i.hasNext()) {
                    value = value + "; ";
                }
            }
            break;
        }
        /*
         * case 3 : { value = observer.getgetDSLCode(); break; }
         */
        }

        return value;

    }

    @Override
    public String getColumnName(int column) {

        String name = "";

        switch (column) {
        case 0: {
            name = AbstractSchemaTableModel.bundle.getString("table.header.observer.surname");
            break;
        }
        case 1: {
            name = AbstractSchemaTableModel.bundle.getString("table.header.observer.name");
            break;
        }
        case 2: {
            name = AbstractSchemaTableModel.bundle.getString("table.header.observer.contact");
            break;
        }
        /*
         * case 3 : { name =
         * AbstractSchemaTableModel.bundle.getString("table.header.observer.dsl");
         * break; }
         */
        }

        return name;

    }

}
