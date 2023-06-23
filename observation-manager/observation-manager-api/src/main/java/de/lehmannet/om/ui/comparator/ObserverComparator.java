/*
 * ====================================================================
 * /comparator/ObserverComparator.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.comparator;

import java.io.Serializable;
import java.util.Comparator;

import de.lehmannet.om.IObserver;

public class ObserverComparator implements Comparator<IObserver>, Serializable {

    @Override
    public int compare(IObserver o1, IObserver o2) {

        String n1 = o1.getName().trim() + o1.getSurname().trim();
        String n2 = o2.getName().trim() + o2.getSurname().trim();

        return n1.compareToIgnoreCase(n2);

    }

}
