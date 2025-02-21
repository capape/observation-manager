/*
 * ====================================================================
 * /box/SessionBox.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.box;

import de.lehmannet.om.ISession;
import de.lehmannet.om.util.DateManager;

public class SessionBox extends OMComboBox<ISession> {

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
    protected String getKey(ISession session) {

        return dateManager.offsetDateTimeToStringWithHour(session.getBegin()) + " - "
                + dateManager.offsetDateTimeToStringWithHour(session.getEnd());
    }
}
