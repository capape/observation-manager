/* ====================================================================
 * /statistics/CatalogTargets.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.statistics;

import de.lehmannet.om.IObservation;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.ui.catalog.IListableCatalog;

class CatalogTargets {

    private IListableCatalog catalog = null;
    private TargetObservations[] targets = null;
    private int observations = 0;

    public CatalogTargets(IListableCatalog catalog, ITarget[] t) {

        this.catalog = catalog;
        this.targets = new TargetObservations[t.length];
        for (int i = 0; i < t.length; i++) {
            this.targets[i] = new TargetObservations(t[i]);
        }

    }

    public void checkTarget(IObservation observation) {

        ITarget observedTarget = observation.getTarget();

        for (TargetObservations target : this.targets) {
            if (this.areEqual(target.getTarget(), observedTarget)) {
                // Add observation
                // If this is the first add, increase counter.
                if (target.addObservation(observation)) {
                    this.observations++;
                }
            }
        }

    }

    public int numberOfObservations() {

        return observations;

    }

    public IListableCatalog getCatalog() {

        return this.catalog;

    }

    public TargetObservations[] getTargetObservations() {

        return this.targets;

    }

    private boolean areEqual(ITarget catalogTarget, ITarget observedTarget) { // Don't use equal() from ITarget, as it
                                                                              // doesn't check aliasnames

        // All names of the catalog target are now filled into an array. Mind that names
        // are not formated!
        String catalogTargetName = catalogTarget.getName();
        String[] catalogTargetAliasNames = catalogTarget.getAliasNames();
        String[] allCatalogTargetNames = null;
        if ((catalogTargetAliasNames != null) && (catalogTargetAliasNames.length > 0)) {
            allCatalogTargetNames = new String[catalogTargetAliasNames.length + 1];
            System.arraycopy(catalogTargetAliasNames, 0, allCatalogTargetNames, 0, catalogTargetAliasNames.length);
        } else {
            allCatalogTargetNames = new String[1];
        }
        allCatalogTargetNames[allCatalogTargetNames.length - 1] = catalogTargetName;

        // All names of the observed target are now filled into an array. Mind that
        // names are not formated!
        String observedTargetName = observedTarget.getName();
        String[] observedTargetAliasNames = observedTarget.getAliasNames();
        String[] allObservedTargetNames = null;
        if ((observedTargetAliasNames != null) && (observedTargetAliasNames.length > 0)) {
            allObservedTargetNames = new String[observedTargetAliasNames.length + 1];
            System.arraycopy(observedTargetAliasNames, 0, allObservedTargetNames, 0, observedTargetAliasNames.length);
        } else {
            allObservedTargetNames = new String[1];
        }
        allObservedTargetNames[allObservedTargetNames.length - 1] = observedTargetName;

        // Now compare both arrays and try to find a machting name. Start at the end of
        // both arrays, as the target name is stored at the end.
        // Hopefully in most cases the names do already match.
        // Format name here should increase performance, as we only format the required
        // amount of strings.
        for (int x = allCatalogTargetNames.length - 1; x >= 0; x--) {
            for (int y = allObservedTargetNames.length - 1; y >= 0; y--) {
                if (allCatalogTargetNames[x].equals(allObservedTargetNames[y])) { // Try without formatting names
                                                                                  // (formatName call takes most of CPU
                                                                                  // time in whole calculation)
                    return true;
                }
                if (this.formatName(allCatalogTargetNames[x]).equals(this.formatName(allObservedTargetNames[y]))) { // Try
                                                                                                                    // with
                                                                                                                    // formated
                                                                                                                    // names
                    return true;
                }
            }
        }

        return false;

    }

    private String formatName(String name) {

        // name = name.trim();
        name = name.toUpperCase();
        name = name.replaceAll(" ", "");

        return name;

    }

}
