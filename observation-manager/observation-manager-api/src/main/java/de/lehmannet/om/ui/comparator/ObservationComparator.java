/*
 * ====================================================================
 * /comparator/ObservationComparator.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.comparator;

import de.lehmannet.om.IObservation;
import de.lehmannet.om.IObserver;
import de.lehmannet.om.ITarget;
import java.time.OffsetDateTime;
import java.util.Comparator;

public class ObservationComparator implements Comparator<IObservation> {

    private boolean reverse = false;

    public ObservationComparator() {}

    public ObservationComparator(boolean reverse) {

        this.reverse = reverse;
    }

    @Override
    public int compare(IObservation o1, IObservation o2) {

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

            OffsetDateTime ob1B = ob1.getBegin();
            OffsetDateTime ob2B = ob2.getBegin();

            if (ob1B.isBefore(ob2B)) {
                return -1;
            } else if (ob1B.isAfter(ob2B)) {
                return 1;
            } else if (ob1B.equals(ob2B)) { // Same start date. Try to check end date
                OffsetDateTime ob1E = ob1.getEnd();
                OffsetDateTime ob2E = ob2.getEnd();
                if (ob1E != null) {
                    if (ob2E != null) {
                        if (ob1E.isBefore(ob2E)) {
                            return -1;
                        } else if (ob1E.isAfter(ob2E)) {
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
