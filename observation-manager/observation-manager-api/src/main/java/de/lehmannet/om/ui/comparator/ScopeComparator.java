/*
 * ====================================================================
 * /comparator/ScopeComparator.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.comparator;

import java.io.Serializable;
import java.util.Comparator;

import de.lehmannet.om.IScope;

public class ScopeComparator implements Comparator<IScope>, Serializable {

    @Override
    public int compare(IScope o1, IScope o2) {

        // TODO review this comparator
        float s1a = o1.getAperture();
        float s2a = o2.getAperture();

        return Math.round((float) Math.ceil(s1a - s2a));

    }

}
