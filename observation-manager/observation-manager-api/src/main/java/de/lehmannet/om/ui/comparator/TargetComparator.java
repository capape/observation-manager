/*
 * ====================================================================
 * /comparator/TargetComparator.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.comparator;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Locale;

import de.lehmannet.om.ITarget;

public class TargetComparator implements Comparator<ITarget>, Serializable {

    @Override
    public int compare(ITarget o1, ITarget o2) {

        String t1Name = o1.getName().toLowerCase(Locale.getDefault()).trim();

        // Are names equal?
        if (t1Name.equals(o2.getName().toLowerCase(Locale.getDefault()).trim())) {
            return 0;
        }

        // Maybe we find t1 name in t2 alias names?
        String[] an = o2.getAliasNames();
        if (an != null && an.length > 0) {
            for (String s : an) {
                if (t1Name.equals(s.toLowerCase(Locale.getDefault()).trim())) {
                    return 0;
                }
            }
        }

        String t2Name = o2.getName().toLowerCase(Locale.getDefault()).trim();

        // No equal name found....sort using native String method
        return t1Name.compareTo(t2Name);

    }

}
