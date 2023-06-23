/*
 * ====================================================================
 * /comparator/SessionComparator.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.comparator;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Comparator;

import de.lehmannet.om.ISession;

public class SessionComparator implements Comparator<ISession>, Serializable {

    private final boolean reverse;

    public SessionComparator() {
        this.reverse = false;
    }

    public SessionComparator(boolean reverse) {

        this.reverse = reverse;

    }

    @Override
    public int compare(ISession o1, ISession o2) {

        ISession s1;
        ISession s2;
        if (reverse) {
            s1 = o1;
            s2 = o2;
        } else {
            s2 = o1;
            s1 = o2;
        }

        OffsetDateTime s1Begin = s1.getBegin();
        OffsetDateTime s2Begin = s2.getBegin();

        if (s1Begin.isBefore(s2Begin)) {
            return -1;
        } else if (s1Begin.isAfter(s2Begin)) {
            return 1;
        }

        return 0;

    }

}
