/*
 * ====================================================================
 * /comparator/LensComparator.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.comparator;

import de.lehmannet.om.ILens;
import java.util.Comparator;

public class LensComparator implements Comparator<ILens> {

    @Override
    public int compare(ILens o1, ILens o2) {

        if ((o1 instanceof ILens) && (o2 instanceof ILens)) {
            ILens l1 = (ILens) o1;
            ILens l2 = (ILens) o2;

            float l1a = l1.getFactor();
            float l2a = l2.getFactor();

            return Math.round((float) Math.ceil(l1a - l2a));
        }

        return 0;
    }
}
