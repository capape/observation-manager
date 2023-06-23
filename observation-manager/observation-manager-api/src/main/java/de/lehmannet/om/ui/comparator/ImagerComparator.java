/*
 * ====================================================================
 * /comparator/ImagerComparator.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.comparator;

import java.io.Serializable;
import java.util.Comparator;

import de.lehmannet.om.IImager;

public class ImagerComparator implements Comparator<IImager>, Serializable {

    @Override
    public int compare(IImager o1, IImager o2) {

        String i1m = o1.getModel().trim();
        String i2m = o2.getModel().trim();

        return i1m.compareToIgnoreCase(i2m);

    }

}
