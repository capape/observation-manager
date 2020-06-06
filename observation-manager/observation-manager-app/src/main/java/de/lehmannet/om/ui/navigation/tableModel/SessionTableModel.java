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

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final String MODEL_ID = "Session";

    public SessionTableModel(ISession[] session) {

        this.elements = session;

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
            c = Calendar.class;
            break;
        }
        case 2: {
            c = Site.class;
            break;
        }
        case 3:
        case 6:
        case 5:
        case 4: {
            c = String.class;
            break;
        }
        }

        return c;

    }

    @Override
    public String getValueAt(int rowIndex, int columnIndex) {

        if (this.elements == null) {
            return null;
        }

        StringBuilder value = new StringBuilder();
        ISession session = (ISession) this.elements[rowIndex];
        switch (columnIndex) {
        case 0: {
            value.append(session.getBegin().toString());
            break;
        }
        case 1: {
            value.append(session.getEnd().toString());
            break;
        }
        case 2: {
            value.append(session.getSite());
            break;
        }
        case 3: {
            value.append(session.getWeather());
            break;
        }
        case 4: {
            Iterator<IObserver> i = session.getCoObservers().iterator();
            if (!session.getCoObservers().isEmpty()) {
                value = new StringBuilder(); // Otherwise the coObservers are prefixed with NULL
            } else {
                value = new StringBuilder("NULL");
            }
            while (i.hasNext()) {
                value.append(i.next().getDisplayName());
                if (i.hasNext()) {
                    value.append("; ");
                }
            }
            break;
        }
        case 5: {
            value = new StringBuilder(session.getEquipment());
            break;
        }
        case 6: {
            if ((session.getComments() != null) && (session.getComments().length() > 15)) {
                value = new StringBuilder(session.getComments().substring(0, 15));
            } else {
                value = new StringBuilder(session.getComments());
            }
            break;
        }
        }

        return value.toString();

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
