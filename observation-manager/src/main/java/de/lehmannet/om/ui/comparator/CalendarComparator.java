/* ====================================================================
 * /comparator/CalendarComparator.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.comparator;

import java.util.Calendar;
import java.util.Comparator;

public class CalendarComparator implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {

        if ((o1 instanceof Calendar) && (o2 instanceof Calendar)) {
            Calendar c1 = (Calendar) o1;
            Calendar c2 = (Calendar) o2;

            if (c1.before(c2)) {
                return -1;
            } else if (c1.after(c2)) {
                return 1;
            }

        }

        return 0;

    }

}
