/* ====================================================================
 * /box/SessionBox.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.box;

import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ISession;
import de.lehmannet.om.util.DateManager;

public class SessionBox extends AbstractBox {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private final DateManager dateManager;

    public SessionBox(DateManager dateManager) {
        super();
        this.dateManager = dateManager;

    }

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

        return dateManager.offsetDateTimeToString(session.getBegin()) + " - "
                + dateManager.offsetDateTimeToString(session.getEnd());

    }

}
