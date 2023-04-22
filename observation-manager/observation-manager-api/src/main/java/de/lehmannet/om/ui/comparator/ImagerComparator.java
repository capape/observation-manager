/* ====================================================================
 * /comparator/ImagerComparator.java
 *
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.comparator;

import java.util.Comparator;

import de.lehmannet.om.IImager;

public class ImagerComparator implements Comparator<IImager> {

    @Override
    public int compare(IImager o1, IImager o2) {

        if ((o1 instanceof IImager) && (o2 instanceof IImager)) {
            IImager i1 = (IImager) o1;
            IImager i2 = (IImager) o2;

            String i1m = i1.getModel().trim();
            String i2m = i2.getModel().trim();

            return i1m.compareToIgnoreCase(i2m);

        }

        return 0;

    }

}
