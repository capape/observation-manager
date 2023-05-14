/*
 * ====================================================================
 * /comparator/EyepieceComparator.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.comparator;

import java.util.Comparator;

import de.lehmannet.om.IEyepiece;

public class EyepieceComparator implements Comparator<IEyepiece> {

    @Override
    public int compare(IEyepiece o1, IEyepiece o2) {

        if ((o1 instanceof IEyepiece) && (o2 instanceof IEyepiece)) {
            IEyepiece e1 = (IEyepiece) o1;
            IEyepiece e2 = (IEyepiece) o2;

            float e1a = e1.getFocalLength();
            float e2a = e2.getFocalLength();

            return Math.round((float) Math.ceil(e1a - e2a));

        }

        return 0;

    }

}
