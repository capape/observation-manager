/*
 * ====================================================================
 * /comparator/AngleComparator.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.comparator;

import java.io.Serializable;
import java.util.Comparator;

import de.lehmannet.om.Angle;

public class AngleComparator implements Comparator<Angle>, Serializable {

    @Override
    public int compare(Angle o1, Angle o2) {

        // Create new copies of given Angles, as otherwise we would change the
        // original objects in the next steps
        Angle a1 = new Angle(o1.getValue(), o1.getUnit());
        Angle a2 = new Angle(o2.getValue(), o2.getUnit());

        return Double.compare(a1.toArcSec(), a2.toArcSec());

    }

}
