/*
 * ====================================================================
 * /comparator/SiteComparator.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.comparator;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Locale;

import de.lehmannet.om.ISite;

public class SiteComparator implements Comparator<ISite>, Serializable {

    @Override
    public int compare(ISite o1, ISite o2) {

        String s1Name = o1.getName().toLowerCase(Locale.getDefault()).trim();
        return s1Name.compareToIgnoreCase(o2.getName().toLowerCase(Locale.getDefault()).trim());

    }

}
