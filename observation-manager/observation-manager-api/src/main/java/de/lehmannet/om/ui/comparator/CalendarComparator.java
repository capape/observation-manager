/*
 * ====================================================================
 * /comparator/CalendarComparator.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.comparator;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Comparator;

public class CalendarComparator implements Comparator<Calendar>, Serializable {

    @Override
    public int compare(Calendar o1, Calendar o2) {

        if (o1.before(o2)) {
            return -1;
        } else if (o1.after(o2)) {
            return 1;
        }

        return 0;

    }

}
