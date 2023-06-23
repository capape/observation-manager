/*
 * ====================================================================
 * /comparator/EyepieceComparator.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.comparator;

import java.io.Serializable;
import java.util.Comparator;

import de.lehmannet.om.IEyepiece;

public class EyepieceComparator implements Comparator<IEyepiece>, Serializable {

    @Override
    public int compare(IEyepiece o1, IEyepiece o2) {

        // TODO: review this comparator
        float e1a = o1.getFocalLength();
        float e2a = o2.getFocalLength();

        return Math.round((float) Math.ceil(e1a - e2a));

    }

}
