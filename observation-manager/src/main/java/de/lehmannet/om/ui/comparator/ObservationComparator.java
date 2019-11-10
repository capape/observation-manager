/* ====================================================================
 * /comparator/ObservationComparator.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.comparator;

import java.util.Calendar;
import java.util.Comparator;

import de.lehmannet.om.IObservation;
import de.lehmannet.om.IObserver;
import de.lehmannet.om.ITarget;

public class ObservationComparator implements Comparator {

    private boolean reverse = false;

    public ObservationComparator() {

    }

    public ObservationComparator(boolean reverse) {

        this.reverse = reverse;

    }

    @Override
    public int compare(Object o1, Object o2) {

        if ((o1 instanceof IObservation) && (o2 instanceof IObservation)) {
            IObservation ob1 = null;
            IObservation ob2 = null;
            if (reverse) {
                ob1 = (IObservation) o1;
                ob2 = (IObservation) o2;
            } else {
                ob2 = (IObservation) o1;
                ob1 = (IObservation) o2;
            }

            Calendar ob1B = ob1.getBegin();
            Calendar ob2B = ob2.getBegin();

            if (ob1B.before(ob2B)) {
                return -1;
            } else if (ob1B.after(ob2B)) {
                return 1;
            } else if (ob1B.equals(ob2B)) { // Same start date. Try to check end date
                Calendar ob1E = ob1.getEnd();
                Calendar ob2E = ob2.getEnd();
                if (ob1E != null) {
                    if (ob2E != null) {
                        if (ob1E.before(ob2E)) {
                            return -1;
                        } else if (ob1E.after(ob2E)) {
                            return 1;
                        }
                    } else { // Observation 1 has end date, while Observation 2 has none
                        return 1;
                    }
                } else if (ob2E != null) { // Observation 2 has a end date, while Observation 1 has none.
                    return -1;
                }

                // End dates are NULL. So try to use TargetComparator
                ITarget ob1T = ob1.getTarget();
                ITarget ob2T = ob2.getTarget();
                TargetComparator targetComparator = new TargetComparator();
                int retValue = targetComparator.compare(ob1T, ob2T);
                if (retValue == 0) { // Target also equal...compare observer
                    IObserver ob1O = ob1.getObserver();
                    IObserver ob2O = ob2.getObserver();
                    ObserverComparator observerComparator = new ObserverComparator();
                    return observerComparator.compare(ob1O, ob2O);
                }
                return retValue;
            }

        }

        return 0;

    }

}
