/* ====================================================================
 * /navigation/tableModel/SessionTableModel.java
 *
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.navigation.tableModel;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;

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
            c = OffsetDateTime.class;
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
    public Object getValueAt(int rowIndex, int columnIndex) {

        if (this.elements == null) {
            return null;
        }

        ISession session = (ISession) this.elements[rowIndex];
        switch (columnIndex) {
        case 0:
            return session.getBegin();
        case 1:
            return session.getEnd();
        case 2:
            return session.getSite();
        case 3:
            return StringUtils.trimToEmpty(session.getWeather());
        case 4:
            Iterator<IObserver> i = session.getCoObservers().iterator();
            final StringBuilder value;
            if (!session.getCoObservers().isEmpty()) {
                value = new StringBuilder(); // Otherwise the coObservers are prefixed with NULL
            } else {
                value = new StringBuilder("");
            }
            while (i.hasNext()) {
                value.append(i.next().getDisplayName());
                if (i.hasNext()) {
                    value.append("; ");
                }
            }
            return value.toString();
        case 5:
            return StringUtils.trimToEmpty(session.getEquipment());
        case 6:
            if ((session.getComments() != null) && (session.getComments().length() > 15)) {
                return session.getComments().substring(0, 15);
            } else {
                return StringUtils.trimToEmpty(session.getComments());
            }
        default:
            return StringUtils.EMPTY;
        }

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
