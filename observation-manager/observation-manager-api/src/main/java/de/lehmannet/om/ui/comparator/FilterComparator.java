/*
 * ====================================================================
 * /comparator/FilterComparator.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.comparator;

import java.io.Serializable;
import java.util.Comparator;

import de.lehmannet.om.IFilter;

public class FilterComparator implements Comparator<IFilter>, Serializable {

    @Override
    public int compare(IFilter o1, IFilter o2) {

        String i1m = o1.getModel().trim();
        String i2m = o2.getModel().trim();

        return i1m.compareToIgnoreCase(i2m);

    }

}
