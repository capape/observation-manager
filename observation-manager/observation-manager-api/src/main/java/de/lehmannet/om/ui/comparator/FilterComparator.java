/*
 * ====================================================================
 * /comparator/FilterComparator.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.comparator;

import java.util.Comparator;

import de.lehmannet.om.IFilter;

public class FilterComparator implements Comparator<IFilter> {

    @Override
    public int compare(IFilter o1, IFilter o2) {

        if ((o1 instanceof IFilter) && (o2 instanceof IFilter)) {
            IFilter i1 = (IFilter) o1;
            IFilter i2 = (IFilter) o2;

            String i1m = i1.getModel().trim();
            String i2m = i2.getModel().trim();

            return i1m.compareToIgnoreCase(i2m);

        }

        return 0;

    }

}
