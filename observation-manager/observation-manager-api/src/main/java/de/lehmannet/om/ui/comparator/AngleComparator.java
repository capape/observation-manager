/*
 * ====================================================================
 * /comparator/AngleComparator.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.comparator;

import de.lehmannet.om.Angle;
import java.util.Comparator;

public class AngleComparator implements Comparator<Angle> {

    @Override
    public int compare(Angle o1, Angle o2) {

        if ((o1 instanceof Angle) && (o2 instanceof Angle)) {
            Angle ao1 = (Angle) o1;
            Angle ao2 = (Angle) o2;

            // Create new copies of given Angles, as otherwise we would change the
            // original objects in the next steps
            Angle a1 = new Angle(ao1.getValue(), ao1.getUnit());
            Angle a2 = new Angle(ao2.getValue(), ao2.getUnit());

            if (a1.toArcSec() < a2.toArcSec()) {
                return -1;
            } else if (a1.toArcSec() > a2.toArcSec()) {
                return 1;
            }
        }

        return 0;
    }
}
