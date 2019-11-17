/* ====================================================================
 * /box/SessionBox.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.box;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ISession;

public class SessionBox extends AbstractBox {

    @Override
    public void addItem(ISchemaElement element) {

        if (element == null) {
            return;
        }

        ISession session = (ISession) element;
        String key = this.getKey(session);

        super.addItem(key, session);

    }

    @Override
    protected String getKey(ISchemaElement element) {

        // The displayname of Session does not show time, so we build own string...

        ISession session = (ISession) element;
        Calendar begin = session.getBegin();
        Calendar end = session.getEnd();

        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault());

        df.setCalendar(begin);
        String result = df.format(begin.getTime()) + " - ";
        df.setCalendar(end);
        result = result + df.format(end.getTime());

        return result;

    }

}
