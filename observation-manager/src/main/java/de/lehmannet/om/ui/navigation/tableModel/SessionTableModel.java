/* ====================================================================
 * /navigation/tableModel/SessionTableModel.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.navigation.tableModel;

import java.util.Calendar;
import java.util.Iterator;

import de.lehmannet.om.IObserver;
import de.lehmannet.om.ISession;
import de.lehmannet.om.Site;

public class SessionTableModel extends AbstractSchemaTableModel {

    private static final String MODEL_ID = "Session";

    public SessionTableModel(ISession[] session) {

        super.elements = session;

    }

    @Override
    public int getColumnCount() {

        return 7;

    }

    @Override
    public String getID() {

        return SessionTableModel.MODEL_ID;

    }

    @Override
    public int getRowCount() {

        if (super.elements == null) {
            return 5;
        }
        return super.elements.length;

    }

    @Override
    public Class getColumnClass(int columnIndex) {

        Class c = null;

        switch (columnIndex) {
        case 0: {
            c = Calendar.class;
            break;
        }
        case 1: {
            c = Calendar.class;
            break;
        }
        case 2: {
            c = Site.class;
            break;
        }
        case 3: {
            c = String.class;
            break;
        }
        case 4: {
            c = String.class;
            break;
        }
        case 5: {
            c = String.class;
            break;
        }
        case 6: {
            c = String.class;
            break;
        }
        }

        return c;

    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        Object value = null;

        if (super.elements == null) {
            return value;
        }

        ISession session = (ISession) super.elements[rowIndex];
        switch (columnIndex) {
        case 0: {
            value = session.getBegin();
            break;
        }
        case 1: {
            value = session.getEnd();
            break;
        }
        case 2: {
            value = session.getSite();
            break;
        }
        case 3: {
            value = session.getWeather();
            break;
        }
        case 4: {
            Iterator i = session.getCoObservers().iterator();
            if (!session.getCoObservers().isEmpty()) {
                value = ""; // Otherwise the coObservers are prefixed with NULL
            }
            while (i.hasNext()) {
                value = value + ((IObserver) i.next()).getDisplayName();
                if (i.hasNext()) {
                    value = value + "; ";
                }
            }
            break;
        }
        case 5: {
            value = session.getEquipment();
            break;
        }
        case 6: {
            if ((session.getComments() != null) && (session.getComments().length() > 15)) {
                value = session.getComments().substring(0, 15);
            } else {
                value = session.getComments();
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
            name = AbstractSchemaTableModel.bundle.getString("table.header.session.begin");
            break;
        }
        case 1: {
            name = AbstractSchemaTableModel.bundle.getString("table.header.session.end");
            break;
        }
        case 2: {
            name = AbstractSchemaTableModel.bundle.getString("table.header.session.site");
            break;
        }
        case 3: {
            name = AbstractSchemaTableModel.bundle.getString("table.header.session.weather");
            break;
        }
        case 4: {
            name = AbstractSchemaTableModel.bundle.getString("table.header.session.coobserver");
            break;
        }
        case 5: {
            name = AbstractSchemaTableModel.bundle.getString("table.header.session.equipment");
            break;
        }
        case 6: {
            name = AbstractSchemaTableModel.bundle.getString("table.header.session.comments");
            break;
        }
        }

        return name;

    }

}
