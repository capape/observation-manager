package de.lehmannet.om.ui.statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import de.lehmannet.om.ICloneable;
import de.lehmannet.om.IObservation;
import de.lehmannet.om.ITarget;

public class TargetObservations {

    private ITarget target = null;
    private List<IObservation> observations = null;

    public TargetObservations(ITarget target) {

        this.target = target;

    }

    public ITarget getTarget() {

        return ICloneable.copyOrNull(this.target);

    }

    public List<IObservation> getObservations() {

        if (this.observations == null) {
            return Collections.emptyList();
        }

        return this.observations.stream().map(x -> {
            return (IObservation) x;
        }).collect(Collectors.toList());

    }

    public boolean addObservation(IObservation observation) {

        boolean first = false;
        if (this.observations == null) {
            this.observations = new ArrayList<>(3);
            first = true;
        }
        this.observations.add(observation);

        return first;

    }

}
