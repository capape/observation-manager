package de.lehmannet.om.ui.statistics;

import java.util.ArrayList;
import java.util.List;

import de.lehmannet.om.IObservation;
import de.lehmannet.om.ITarget;

public class TargetObservations {

    private ITarget target = null;
    private ArrayList observations = null;

    public TargetObservations(ITarget target) {

        this.target = target;

    }

    public ITarget getTarget() {

        return this.target;

    }

    public List getObservations() {

        return this.observations;

    }

    public boolean addObservation(IObservation observation) {

        boolean first = false;
        if (this.observations == null) {
            this.observations = new ArrayList(3);
            first = true;
        }
        this.observations.add(observation);

        return first;

    }

}
