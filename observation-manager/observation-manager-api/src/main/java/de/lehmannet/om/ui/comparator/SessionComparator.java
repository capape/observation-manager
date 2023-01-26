/* ====================================================================
 * /comparator/SessionComparator.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.comparator;

import java.time.OffsetDateTime;
import java.util.Comparator;

import de.lehmannet.om.ISession;

public class SessionComparator implements Comparator<ISession> {

    private boolean reverse = false;

    public SessionComparator() {
    }

    public SessionComparator(boolean reverse) {

        this.reverse = reverse;

    }

    @Override
    public int compare(ISession o1, ISession o2) {

        if ((o1 instanceof ISession) && (o2 instanceof ISession)) {
            ISession s1 = null;
            ISession s2 = null;
            if (reverse) {
                s1 = (ISession) o1;
                s2 = (ISession) o2;
            } else {
                s2 = (ISession) o1;
                s1 = (ISession) o2;
            }

            OffsetDateTime s1Begin = s1.getBegin();
            OffsetDateTime s2Begin = s2.getBegin();

            if (s1Begin.isBefore(s2Begin)) {
                return -1;
            } else if (s1Begin.isAfter(s2Begin)) {
                return 1;
            }

        }

        return 0;

    }

}
