/*
 * ====================================================================
 * /comparator/LensComparator.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.comparator;

import java.io.Serializable;
import java.util.Comparator;

import de.lehmannet.om.ILens;

public class LensComparator implements Comparator<ILens>, Serializable {

    @Override
    public int compare(ILens o1, ILens o2) {

        // TODO:review this comparator

        float l1a = o1.getFactor();
        float l2a = o2.getFactor();

        return Math.round((float) Math.ceil(l1a - l2a));

    }

}
