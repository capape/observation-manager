/*
 * ====================================================================
 * /navigation/tableModel/ObserverTableModel.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.navigation.tableModel;

import java.util.Iterator;

import de.lehmannet.om.ICloneable;
import de.lehmannet.om.IObserver;
import de.lehmannet.om.ISchemaElement;

public class ObserverTableModel extends AbstractSchemaTableModel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final String MODEL_ID = "Observer";

    public ObserverTableModel(IObserver[] observers) {

        this.elements = ICloneable.copyToList(observers).toArray(new ISchemaElement[observers.length]);

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

        if (this.elements == null) {
            return 5;
        }
        return this.elements.length;

    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        StringBuilder value = new StringBuilder();

        if (this.elements == null) {
            return value.toString();
        }

        IObserver observer = (IObserver) this.elements[rowIndex];

        switch (columnIndex) {
            case 0: {
                value = new StringBuilder(observer.getSurname());
                break;
            }
            case 1: {
                value = new StringBuilder(observer.getName());
                break;
            }
            case 2: {
                Iterator<String> i = observer.getContacts().iterator();
                while (i.hasNext()) {
                    value.append(i.next());
                    if (i.hasNext()) {
                        value.append("; ");
                    }
                }
                break;
            }
            /*
             * case 3 : { value = observer.getgetDSLCode(); break; }
             */
        }

        return value.toString();

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
             * case 3 : { name = AbstractSchemaTableModel.bundle.getString("table.header.observer.dsl"); break; }
             */
        }

        return name;

    }

}
