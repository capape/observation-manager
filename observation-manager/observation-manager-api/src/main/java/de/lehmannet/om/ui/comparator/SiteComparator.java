/*
 * ====================================================================
 * /comparator/SiteComparator.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.comparator;

import java.util.Comparator;

import de.lehmannet.om.ISite;

public class SiteComparator implements Comparator<ISite> {

    @Override
    public int compare(ISite o1, ISite o2) {

        if ((o1 instanceof ISite) && (o2 instanceof ISite)) {
            ISite s1 = (ISite) o1;
            ISite s2 = (ISite) o2;

            String s1Name = s1.getName().toLowerCase().trim();

            return s1Name.compareToIgnoreCase(s2.getName().trim());

        }

        return 0;

    }

}
