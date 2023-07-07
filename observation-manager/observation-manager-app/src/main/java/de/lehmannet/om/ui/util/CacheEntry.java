package de.lehmannet.om.ui.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collectors;

import de.lehmannet.om.IEyepiece;
import de.lehmannet.om.IFilter;
import de.lehmannet.om.IImager;
import de.lehmannet.om.ILens;
import de.lehmannet.om.IObservation;
import de.lehmannet.om.IObserver;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.IScope;
import de.lehmannet.om.ISession;
import de.lehmannet.om.ISite;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.ITargetContaining;

class CacheEntry {

    private String xmlPath = null;
    private Set<SchemaElementCacheEntry<IObservation>> observation = new HashSet<>();
    private Set<SchemaElementCacheEntry<IEyepiece>> eyepiece = new HashSet<>();
    private Set<SchemaElementCacheEntry<IImager>> imager = new HashSet<>();
    private Set<SchemaElementCacheEntry<IFilter>> filter = new HashSet<>();
    private Set<SchemaElementCacheEntry<IObserver>> coObserver = new HashSet<>();
    private Set<SchemaElementCacheEntry<IObserver>> observer = new HashSet<>();
    private Set<SchemaElementCacheEntry<IScope>> scope = new HashSet<>();
    private Set<SchemaElementCacheEntry<ISession>> session = new HashSet<>();
    private Set<SchemaElementCacheEntry<ISite>> site = new HashSet<>();
    private Set<SchemaElementCacheEntry<ITarget>> target = new HashSet<>();
    private Set<SchemaElementCacheEntry<ILens>> lens = new HashSet<>();
    // Contains only ITargetContaining targets
    // The refered elements are the contained targets
    // We need this to update the contained targets observation references in case
    // the ITargetContaining target is changed
    // E.g. MultipleStar referes to components A,C,B
    // --> MultipleStar and TargetStars A,B and C refer to an Observation
    // --> MultipleStar gets edited and a new TargetStar gets added/removed
    // Esp. in case of removal of a contained star, we need to know the old list of
    // contained stars, so that we can remove the observation reference e.g. from
    // TargetStar C
    private List<SchemaElementCacheEntry<ITarget>> targetContaining = new ArrayList<>();

    public CacheEntry() {

    }

    public CacheEntry(String xmlPath, IObservation[] observation, IEyepiece[] eyepiece, IFilter[] filter,
            IImager[] imager, IObserver[] observer, IScope[] scope, ISession[] session, ISite[] site, ITarget[] target,
            ILens[] lens) {

        this.xmlPath = xmlPath;

        addObservationsToCache(observation);
        addEyepiecesToCache(eyepiece);
        addFiltersToCache(filter);
        addImagerToCache(imager);
        addObserverToCache(observer);
        addScopesToCache(scope);
        addSessionsToCache(session);
        addSitesToCache(site);
        addTargetsToCache(target);
        addLensesToCache(lens);
        assignObservationsToCacheElements();

    }

    private void assignObservationsToCacheElements() {
        // Assign observations to SchemaElementCacheEntries
        // and vice versa (schemaElements to observation SchemaElementCacheEntry)
        for (SchemaElementCacheEntry<IObservation> iObservation : this.observation) {
            addAllObservationElements(iObservation);
        }
    }

    private void addTargetsToCache(ITarget[] target) {

        this.target.clear();
        this.targetContaining.clear();

        for (int i = 0; i < target.length; i++) {
            this.target.add(new SchemaElementCacheEntry<ITarget>(target[i]));
            if (target[i] instanceof ITargetContaining) { // Fill targetContaining Array (via ArrayList)
                SchemaElementCacheEntry<ITarget> entry = addReferencedElements(target, target[i]);
                this.targetContaining.add(entry);
            }
        }

    }

    private SchemaElementCacheEntry<ITarget> addReferencedElements(ITarget[] target, ITarget current) {
        // We add the SchemaElementCacheEntry to the Array
        // with ITargetContaining as schemaElement and the component targets as
        // refered elements

        ITargetContaining iTargetContaining = (ITargetContaining) current;
        List<ITarget> componentTargets = iTargetContaining.getComponentTargets(target);
        List<ISchemaElement> data = new ArrayList<>();
        data.addAll(componentTargets);

        SchemaElementCacheEntry<ITarget> entry = new SchemaElementCacheEntry<ITarget>(current);
        entry.addReferencedElements(data);
        return entry;
    }

    private void addSitesToCache(ISite[] site) {
        this.site.clear();
        for (int i = 0; i < site.length; i++) {
            this.site.add(new SchemaElementCacheEntry<ISite>(site[i]));
        }
    }

    private void addSessionsToCache(ISession[] session) {
        this.session.clear();
        for (int i = 0; i < session.length; i++) {
            this.session.add(new SchemaElementCacheEntry<ISession>(session[i]));
        }
    }

    private void addScopesToCache(IScope[] scope) {
        this.scope.clear();
        for (int i = 0; i < scope.length; i++) {
            this.scope.add(new SchemaElementCacheEntry<IScope>(scope[i]));
        }
    }

    private void addObserverToCache(IObserver[] observer) {

        this.observer.clear();
        this.coObserver.clear();
        for (int i = 0; i < observer.length; i++) {
            this.observer.add(new SchemaElementCacheEntry<IObserver>(observer[i]));
            this.coObserver.add(new SchemaElementCacheEntry<IObserver>(observer[i]));
        }

    }

    private void addImagerToCache(IImager[] imager) {
        this.imager.clear();
        for (int i = 0; i < imager.length; i++) {
            this.imager.add(new SchemaElementCacheEntry<IImager>(imager[i]));
        }
    }

    private void addFiltersToCache(IFilter[] filter) {
        this.filter.clear();
        for (int i = 0; i < filter.length; i++) {
            this.filter.add(new SchemaElementCacheEntry<IFilter>(filter[i]));
        }
    }

    private void addEyepiecesToCache(IEyepiece[] eyepiece) {
        this.eyepiece.clear();
        for (int i = 0; i < eyepiece.length; i++) {
            this.eyepiece.add(new SchemaElementCacheEntry<IEyepiece>(eyepiece[i]));
        }
    }

    private void addObservationsToCache(IObservation[] observation) {

        this.observation.clear();
        for (int i = 0; i < observation.length; i++) {
            this.observation.add(new SchemaElementCacheEntry<IObservation>(observation[i]));
        }
    }

    private void addLensesToCache(ILens[] lens) {

        this.lens.clear();
        for (int i = 0; i < lens.length; i++) {
            this.lens.add(new SchemaElementCacheEntry<ILens>(lens[i]));
        }
    }

    public String getXmlPath() {

        return this.xmlPath;

    }

    public List<ISchemaElement> getReferedElements(ISchemaElement element) {

        if (element instanceof IObservation) {
            for (SchemaElementCacheEntry<IObservation> schemaElementCacheEntry : this.observation) {
                if (schemaElementCacheEntry.getSchemaElement().equals(element)) {
                    return schemaElementCacheEntry.getReferencedElements();
                }
            }
        } else if (element instanceof IEyepiece) {
            for (SchemaElementCacheEntry<IEyepiece> schemaElementCacheEntry : this.eyepiece) {
                if (schemaElementCacheEntry.getSchemaElement().equals(element)) {
                    return schemaElementCacheEntry.getReferencedElements();
                }
            }
        } else if (element instanceof IImager) {
            for (SchemaElementCacheEntry<IImager> schemaElementCacheEntry : this.imager) {
                if (schemaElementCacheEntry.getSchemaElement().equals(element)) {
                    return schemaElementCacheEntry.getReferencedElements();
                }
            }
        } else if (element instanceof IFilter) {
            for (SchemaElementCacheEntry<IFilter> schemaElementCacheEntry : this.filter) {
                if (schemaElementCacheEntry.getSchemaElement().equals(element)) {
                    return schemaElementCacheEntry.getReferencedElements();
                }
            }
        } else if (element instanceof IObserver) {
            for (SchemaElementCacheEntry<IObserver> schemaElementCacheEntry : this.observer) {
                if (schemaElementCacheEntry.getSchemaElement().equals(element)) {
                    return schemaElementCacheEntry.getReferencedElements();
                }
            }
        } else if (element instanceof IScope) {
            for (SchemaElementCacheEntry<IScope> schemaElementCacheEntry : this.scope) {
                if (schemaElementCacheEntry.getSchemaElement().equals(element)) {
                    return schemaElementCacheEntry.getReferencedElements();
                }
            }
        } else if (element instanceof ISession) {
            for (SchemaElementCacheEntry<ISession> schemaElementCacheEntry : this.session) {
                if (schemaElementCacheEntry.getSchemaElement().equals(element)) {
                    return schemaElementCacheEntry.getReferencedElements();
                }
            }
        } else if (element instanceof ISite) {
            for (SchemaElementCacheEntry<ISite> schemaElementCacheEntry : this.site) {
                if (schemaElementCacheEntry.getSchemaElement().equals(element)) {
                    return schemaElementCacheEntry.getReferencedElements();
                }
            }
        } else if (element instanceof ITarget) {
            for (SchemaElementCacheEntry<ITarget> schemaElementCacheEntry : this.target) {
                if (schemaElementCacheEntry.getSchemaElement().equals(element)) {
                    return schemaElementCacheEntry.getReferencedElements();
                }
            }
        } else if (element instanceof ILens) {
            for (SchemaElementCacheEntry<ILens> len : this.lens) {
                if (len.getSchemaElement().equals(element)) {
                    return len.getReferencedElements();
                }
            }
        }

        return null;

    }

    public List<ISchemaElement> getReferencedObservationsForCoObserver(IObserver coObserver) {

        for (SchemaElementCacheEntry<IObserver> schemaElementCacheEntry : this.coObserver) {
            if (schemaElementCacheEntry.getSchemaElement().equals(coObserver)) {
                List<ISchemaElement> re = new ArrayList<ISchemaElement>(
                        schemaElementCacheEntry.getReferencedElements());
                re.removeIf(o -> o instanceof ISession);
                return re;
            }
        }

        return null;

    }

    public IObservation[] getObservations() {

        List<IObservation> set = this.observation.stream().map(entry -> entry.getSchemaElement())
                .collect(Collectors.toList());
        return set.toArray(new IObservation[set.size()]);

    }

    public IEyepiece[] getEyepieces() {
        List<IEyepiece> set = this.eyepiece.stream().map(entry -> entry.getSchemaElement())
                .collect(Collectors.toList());
        return set.toArray(new IEyepiece[set.size()]);

    }

    public IImager[] getImagers() {

        List<IImager> set = this.imager.stream().map(entry -> entry.getSchemaElement()).collect(Collectors.toList());
        return set.toArray(new IImager[set.size()]);

    }

    public IFilter[] getFilters() {

        List<IFilter> set = this.filter.stream().map(entry -> entry.getSchemaElement()).collect(Collectors.toList());
        return set.toArray(new IFilter[set.size()]);
    }

    public IObserver[] getObservers() {

        List<IObserver> set = this.observer.stream().map(entry -> entry.getSchemaElement())
                .collect(Collectors.toList());
        return set.toArray(new IObserver[set.size()]);

    }

    public IObserver[] getCoObservers() {

        List<IObserver> set = this.coObserver.stream().map(entry -> entry.getSchemaElement())
                .collect(Collectors.toList());
        return set.toArray(new IObserver[set.size()]);

    }

    public IScope[] getScopes() {

        List<IScope> set = this.scope.stream().map(entry -> entry.getSchemaElement()).collect(Collectors.toList());
        return set.toArray(new IScope[set.size()]);

    }

    public ISession[] getSessions() {

        List<ISession> set = this.session.stream().map(entry -> entry.getSchemaElement()).collect(Collectors.toList());
        return set.toArray(new ISession[set.size()]);

    }

    public ISite[] getSites() {

        List<ISite> set = this.site.stream().map(entry -> entry.getSchemaElement()).collect(Collectors.toList());
        return set.toArray(new ISite[set.size()]);

    }

    public ITarget[] getTargets() {

        List<ITarget> set = this.target.stream().map(entry -> entry.getSchemaElement()).collect(Collectors.toList());
        return set.toArray(new ITarget[set.size()]);

    }

    public ILens[] getLenses() {

        List<ILens> set = this.lens.stream().map(entry -> entry.getSchemaElement()).collect(Collectors.toList());
        return set.toArray(new ILens[set.size()]);

    }

    public void addEyepiece(IEyepiece eyepiece) {

        SchemaElementCacheEntry<IEyepiece> entry = new SchemaElementCacheEntry<IEyepiece>(eyepiece);
        this.eyepiece.add(entry);
    }

    public void addImager(IImager imager) {

        SchemaElementCacheEntry<IImager> entry = new SchemaElementCacheEntry<IImager>(imager);
        this.imager.add(entry);

    }

    public void addFilter(IFilter filter) {

        SchemaElementCacheEntry<IFilter> entry = new SchemaElementCacheEntry<IFilter>(filter);
        this.filter.add(entry);

    }

    public void addObservation(IObservation observation) {

        SchemaElementCacheEntry<IObservation> entry = new SchemaElementCacheEntry<IObservation>(observation);
        this.observation.add(entry);
        // Add observation to other schemaElements
        this.addAllObservationElements(entry);

    }

    public void addObserver(IObserver observer) {

        SchemaElementCacheEntry<IObserver> entry = new SchemaElementCacheEntry<IObserver>(observer);
        this.observer.add(entry);

        SchemaElementCacheEntry<IObserver> entry2 = new SchemaElementCacheEntry<IObserver>(observer);
        this.coObserver.add(entry2);

    }

    public void addScope(IScope scope) {

        SchemaElementCacheEntry<IScope> entry = new SchemaElementCacheEntry<IScope>(scope);
        this.scope.add(entry);

    }

    public void addSession(ISession session) {
        SchemaElementCacheEntry<ISession> entry = new SchemaElementCacheEntry<ISession>(session);
        this.session.add(entry);

    }

    public void addSite(ISite site) {

        SchemaElementCacheEntry<ISite> entry = new SchemaElementCacheEntry<ISite>(site);
        this.site.add(entry);

    }

    public void addTarget(ITarget target) {

        SchemaElementCacheEntry<ITarget> entry = new SchemaElementCacheEntry<ITarget>(target);
        boolean added = this.target.add(entry);

        if (added) { // Add target to targetContaining
            addToTargetContaining(target);
        }

    }

    private void addToTargetContaining(ITarget target) {
        if (target instanceof ITargetContaining) {
            SchemaElementCacheEntry<ITarget> entry = addReferencedElements(this.getTargets(), target);
            this.targetContaining.add(entry);
        }
    }

    public void addLens(ILens lens) {

        SchemaElementCacheEntry<ILens> entry = new SchemaElementCacheEntry<ILens>(lens);
        this.lens.add(entry);

    }

    public void updateObservation(IObservation observation) {

        // First get SchemaElementCacheEntry for the given observation
        SchemaElementCacheEntry<IObservation> cacheEntry = null;
        for (SchemaElementCacheEntry<IObservation> entry : this.observation) {
            // For the compare use the ID here, as the observation given and the observation
            // in the cache might (most probably will) be different
            if (observation.getID().equals(entry.getSchemaElement().getID())) {
                cacheEntry = entry;
                break;
            }
        }

        // This looks strange, but this might happen in case an observation is removed.
        // After the remove, an updateObservation is called, which cannot find the
        // observation any longer
        if (cacheEntry == null) {
            return;
        }

        // Iterate over each refered schemaElement from the cacheEntry
        List<ISchemaElement> referedElements = cacheEntry.getReferencedElements();
        ListIterator<ISchemaElement> iterator = referedElements.listIterator();
        ISchemaElement current = null;

        // Do remove and add via external lists when the while loop has finished.
        // Otherwise we screw up the iterator
        List<ISchemaElement> removeList = new ArrayList<>();
        List<ISchemaElement> addList = new ArrayList<>();
        while (iterator.hasNext()) {
            current = (ISchemaElement) iterator.next();

            // With the below remove and add approach, we don't check for changes
            // we simply do always an update

            // Remove old entry (always)
            removeList.add(current);

            // Check which type the current schemaElement has and add new entry
            if (current instanceof IEyepiece) {
                addList.add(observation.getEyepiece());

                // Iterate of schemaElement array and update observation reference
                for (SchemaElementCacheEntry<IEyepiece> schemaElementCacheEntry : this.eyepiece) {
                    if (schemaElementCacheEntry.getSchemaElement().equals(current)) { // Remove old cache reference
                        schemaElementCacheEntry.removeReferencedElement(observation);
                    }
                    if (schemaElementCacheEntry.getSchemaElement().equals(observation.getEyepiece())) { // Add new cache
                        // reference
                        schemaElementCacheEntry.addReferencedElement(observation);
                    }
                }

            } else if (current instanceof IImager) {
                addList.add(observation.getImager());

                // Iterate of schemaElement array and update observation reference
                for (SchemaElementCacheEntry<IImager> schemaElementCacheEntry : this.imager) {
                    if (schemaElementCacheEntry.getSchemaElement().equals(current)) { // Remove old cache reference
                        schemaElementCacheEntry.removeReferencedElement(observation);
                    }
                    if (schemaElementCacheEntry.getSchemaElement().equals(observation.getImager())) { // Add new cache
                                                                                                      // reference
                        schemaElementCacheEntry.addReferencedElement(observation);
                    }
                }

            } else if (current instanceof IFilter) {
                addList.add(observation.getFilter());

                // Iterate of schemaElement array and update observation reference
                for (SchemaElementCacheEntry<IFilter> schemaElementCacheEntry : this.filter) {
                    if (schemaElementCacheEntry.getSchemaElement().equals(current)) { // Remove old cache reference
                        schemaElementCacheEntry.removeReferencedElement(observation);
                    }
                    if (schemaElementCacheEntry.getSchemaElement().equals(observation.getFilter())) { // Add new cache
                                                                                                      // reference
                        schemaElementCacheEntry.addReferencedElement(observation);
                    }
                }

            } else if (current instanceof IScope) {
                addList.add(observation.getScope());

                // Iterate of schemaElement array and update observation reference
                for (SchemaElementCacheEntry<IScope> schemaElementCacheEntry : this.scope) {
                    if (schemaElementCacheEntry.getSchemaElement().equals(current)) { // Remove old cache reference
                        schemaElementCacheEntry.removeReferencedElement(observation);
                    }
                    if (schemaElementCacheEntry.getSchemaElement().equals(observation.getScope())) { // Add new cache
                                                                                                     // reference
                        schemaElementCacheEntry.addReferencedElement(observation);
                    }
                }

            } else if (current instanceof ISession) {
                addList.add(observation.getSession());

                // Iterate of schemaElement array and update observation reference
                for (SchemaElementCacheEntry<ISession> elementCacheEntry : this.session) {
                    if (elementCacheEntry.getSchemaElement().equals(current)) { // Remove old cache reference

                        // Remove session reference
                        elementCacheEntry.removeReferencedElement(observation);

                        // Remove old coObserver references
                        if ((((ISession) current).getCoObservers() != null)
                                && !(((ISession) current).getCoObservers().isEmpty())) {
                            List<IObserver> coObservers = ((ISession) current).getCoObservers();
                            ListIterator<IObserver> listIterator = coObservers.listIterator();
                            IObserver currentObserver = null;
                            while (listIterator.hasNext()) { // Iterate over all coObservers
                                currentObserver = listIterator.next();
                                for (SchemaElementCacheEntry<IObserver> schemaElementCacheEntry : this.coObserver) { // Iterate
                                    // over all
                                    // coObservers
                                    if (schemaElementCacheEntry.getSchemaElement().equals(currentObserver)) {
                                        // Add Observation to coObservers refered elements
                                        schemaElementCacheEntry.removeReferencedElement(observation);

                                        break; // Break for loop
                                    }
                                }
                            }
                        }

                    }
                    if (elementCacheEntry.getSchemaElement().equals(observation.getSession())) { // Add new cache
                        // reference

                        // Add session reference
                        elementCacheEntry.addReferencedElement(observation);

                        // Add new coObserver references
                        if ((observation.getSession().getCoObservers() != null)
                                && !(observation.getSession().getCoObservers().isEmpty())) {
                            List<IObserver> coObservers = observation.getSession().getCoObservers();
                            ListIterator<IObserver> listIterator = coObservers.listIterator();
                            IObserver currentObserver = null;
                            while (listIterator.hasNext()) { // Iterate over all coObservers
                                currentObserver = listIterator.next();
                                for (SchemaElementCacheEntry<IObserver> schemaElementCacheEntry : this.coObserver) { // Iterate
                                    // over all
                                    // coObservers
                                    if (schemaElementCacheEntry.getSchemaElement().equals(currentObserver)) {
                                        // Add Observation to coObservers refered elements
                                        schemaElementCacheEntry.addReferencedElement(observation);
                                        // Also include a reference to the session, as we need this in case
                                        // the session will get updated to know, which coObserver was
                                        // refering to that session in the past (for removing coObservers)
                                        schemaElementCacheEntry.addReferencedElement(observation.getSession());

                                        break; // Break for loop
                                    }
                                }
                            }
                        }

                    }
                }

            } else if (current instanceof IObserver) {
                addList.add(observation.getObserver());

                // Iterate of schemaElement array and update observation reference
                for (SchemaElementCacheEntry<IObserver> schemaElementCacheEntry : this.observer) {
                    if (schemaElementCacheEntry.getSchemaElement().equals(current)) { // Remove old cache reference
                        schemaElementCacheEntry.removeReferencedElement(observation);
                    }
                    if (schemaElementCacheEntry.getSchemaElement().equals(observation.getObserver())) { // Add new cache
                        // reference
                        schemaElementCacheEntry.addReferencedElement(observation);
                    }
                }

            } else if (current instanceof ISite) {
                addList.add(observation.getSite());

                // Iterate of schemaElement array and update observation reference
                for (SchemaElementCacheEntry<ISite> schemaElementCacheEntry : this.site) {
                    if (schemaElementCacheEntry.getSchemaElement().equals(current)) { // Remove old cache reference
                        schemaElementCacheEntry.removeReferencedElement(observation);
                    }
                    if (schemaElementCacheEntry.getSchemaElement().equals(observation.getSite())) { // Add new cache
                                                                                                    // reference
                        schemaElementCacheEntry.addReferencedElement(observation);
                    }
                }

            } else if (current instanceof ITarget) {
                addList.add(observation.getTarget());

                // Iterate of schemaElement array and update observation reference
                for (SchemaElementCacheEntry<ITarget> elementCacheEntry : this.target) {
                    if (elementCacheEntry.getSchemaElement().equals(current)) { // Remove old cache reference
                        elementCacheEntry.removeReferencedElement(observation);

                        // If the current target is a TargetContaining target, we need to remove also
                        // the observation references from the containing targets
                        if (elementCacheEntry.getSchemaElement() instanceof ITargetContaining) {
                            List<ITarget> containingTargets = ((ITargetContaining) elementCacheEntry.getSchemaElement())
                                    .getComponentTargets(this.getTargets());
                            ListIterator<ITarget> listIterator = containingTargets.listIterator();
                            ITarget ct = null;
                            while (listIterator.hasNext()) {
                                ct = listIterator.next();
                                for (SchemaElementCacheEntry<ITarget> schemaElementCacheEntry : this.target) {
                                    if (schemaElementCacheEntry.getSchemaElement().equals(ct)) { // Found a depending
                                                                                                 // target
                                        schemaElementCacheEntry.removeReferencedElement(observation);
                                    }
                                }
                            }
                        }
                    }
                    if (elementCacheEntry.getSchemaElement().equals(observation.getTarget())) { // Add new cache
                                                                                                // reference
                        elementCacheEntry.addReferencedElement(observation);

                        // If the current target is a TargetContaining target, we need to add also
                        // the observation references from the containing targets
                        if (elementCacheEntry.getSchemaElement() instanceof ITargetContaining) {
                            List<ITarget> containingTargets = ((ITargetContaining) elementCacheEntry.getSchemaElement())
                                    .getComponentTargets(this.getTargets());
                            ListIterator<ITarget> listIterator = containingTargets.listIterator();
                            ITarget ct = null;
                            while (listIterator.hasNext()) {
                                ct = listIterator.next();
                                for (SchemaElementCacheEntry<ITarget> schemaElementCacheEntry : this.target) {
                                    if (schemaElementCacheEntry.getSchemaElement().equals(ct)) { // Found a depending
                                                                                                 // target
                                        schemaElementCacheEntry.addReferencedElement(observation);
                                    }
                                }
                            }
                        }
                    }
                }

            } else if (current instanceof ILens) {
                addList.add(observation.getLens());

                // Iterate of schemaElement array and update observation reference
                for (SchemaElementCacheEntry<ILens> len : this.lens) {
                    if (len.getSchemaElement().equals(current)) { // Remove old cache reference
                        len.removeReferencedElement(observation);
                    }
                    if (len.getSchemaElement().equals(observation.getLens())) { // Add new cache reference
                        len.addReferencedElement(observation);
                    }
                }

            }
        }

        // Now we can safely remove and add the elements from the cacheEntry
        for (Object value : removeList) {
            cacheEntry.removeReferencedElement((ISchemaElement) value);
        }

        // Add must be after remove, otherwise not changed elements will be removed...
        for (Object o : addList) {
            cacheEntry.addReferencedElement((ISchemaElement) o);
        }

        // Now we have to make sure new selected elements are added as well
        // E.g. an observation didn't have an filter, yet. But now it has one. This
        // won't be covered
        // above as we only work on the cache entry (who doesn't know anything about the
        // "new" element reference...
        // (Elements that existed before and are now removed from the observation, will
        // be covered above!)
        if (!addList.contains(observation.getEyepiece())) {
            // Update observation cache entry to element
            cacheEntry.addReferencedElement(observation.getEyepiece());

            // Update element cache entry to observation
            for (SchemaElementCacheEntry<IEyepiece> schemaElementCacheEntry : this.eyepiece) {
                if (schemaElementCacheEntry.getSchemaElement().equals(observation.getEyepiece())) { // Add new cache
                                                                                                    // reference
                    schemaElementCacheEntry.addReferencedElement(observation);
                }
            }
        }
        if (!addList.contains(observation.getImager())) {
            // Update observation cache entry to element
            cacheEntry.addReferencedElement(observation.getImager());

            // Update element cache entry to observation
            for (SchemaElementCacheEntry<IImager> schemaElementCacheEntry : this.imager) {
                if (schemaElementCacheEntry.getSchemaElement().equals(observation.getImager())) { // Add new cache
                                                                                                  // reference
                    schemaElementCacheEntry.addReferencedElement(observation);
                }
            }
        }
        if (!addList.contains(observation.getFilter())) {
            // Update observation cache entry to element
            cacheEntry.addReferencedElement(observation.getFilter());

            // Update element cache entry to observation
            for (SchemaElementCacheEntry<IFilter> schemaElementCacheEntry : this.filter) {
                if (schemaElementCacheEntry.getSchemaElement().equals(observation.getFilter())) { // Add new cache
                                                                                                  // reference
                    schemaElementCacheEntry.addReferencedElement(observation);
                }
            }
        }
        if (!addList.contains(observation.getObserver())) {
            // Update observation cache entry to element
            cacheEntry.addReferencedElement(observation.getObserver());

            // Update element cache entry to observation
            for (SchemaElementCacheEntry<IObserver> schemaElementCacheEntry : this.observer) {
                if (schemaElementCacheEntry.getSchemaElement().equals(observation.getObserver())) { // Add new cache
                                                                                                    // reference
                    schemaElementCacheEntry.addReferencedElement(observation);
                }
            }
        }
        if (!addList.contains(observation.getScope())) {
            // Update observation cache entry to element
            cacheEntry.addReferencedElement(observation.getScope());

            // Update element cache entry to observation
            for (SchemaElementCacheEntry<IScope> schemaElementCacheEntry : this.scope) {
                if (schemaElementCacheEntry.getSchemaElement().equals(observation.getScope())) { // Add new cache
                                                                                                 // reference
                    schemaElementCacheEntry.addReferencedElement(observation);
                }
            }
        }
        if (!addList.contains(observation.getSession())) {
            // Update observation cache entry to element
            cacheEntry.addReferencedElement(observation.getSession());

            // Update element cache entry to observation
            for (SchemaElementCacheEntry<ISession> elementCacheEntry : this.session) {
                if (elementCacheEntry.getSchemaElement().equals(observation.getSession())) { // Add new cache reference
                    elementCacheEntry.addReferencedElement(observation);

                    // Add new coObserver references
                    if ((observation.getSession().getCoObservers() != null)
                            && !(observation.getSession().getCoObservers().isEmpty())) {
                        List<IObserver> coObservers = observation.getSession().getCoObservers();
                        ListIterator<IObserver> listIterator = coObservers.listIterator();
                        IObserver currentObserver = null;
                        while (listIterator.hasNext()) { // Iterate over all coObservers
                            currentObserver = listIterator.next();
                            for (SchemaElementCacheEntry<IObserver> schemaElementCacheEntry : this.coObserver) { // Iterate
                                                                                                                 // over
                                // all coObservers
                                if (schemaElementCacheEntry.getSchemaElement().equals(currentObserver)) {
                                    // Add Observation to coObservers refered elements
                                    schemaElementCacheEntry.addReferencedElement(observation);

                                    break; // Break for loop
                                }
                            }
                        }
                    }

                }
            }
        }
        if (!addList.contains(observation.getSite())) {
            // Update observation cache entry to element
            cacheEntry.addReferencedElement(observation.getSite());

            // Update element cache entry to observation
            for (SchemaElementCacheEntry<ISite> schemaElementCacheEntry : this.site) {
                if (schemaElementCacheEntry.getSchemaElement().equals(observation.getSite())) { // Add new cache
                                                                                                // reference
                    schemaElementCacheEntry.addReferencedElement(observation);
                }
            }
        }
        if (!addList.contains(observation.getTarget())) {
            // Update observation cache entry to element
            cacheEntry.addReferencedElement(observation.getTarget());

            // Update element cache entry to observation
            for (SchemaElementCacheEntry<ITarget> schemaElementCacheEntry : this.target) {
                if (schemaElementCacheEntry.getSchemaElement().equals(observation.getTarget())) { // Add new cache
                                                                                                  // reference
                    schemaElementCacheEntry.addReferencedElement(observation);
                }
            }
        }
        if (!addList.contains(observation.getLens())) {
            // Update observation cache entry to element
            cacheEntry.addReferencedElement(observation.getLens());

            // Update element cache entry to observation
            for (SchemaElementCacheEntry<ILens> len : this.lens) {
                if (len.getSchemaElement().equals(observation.getLens())) { // Add new cache reference
                    len.addReferencedElement(observation);
                }
            }
        }

    }

    public void updateSession(IObservation[] observations, ISession session) {

        List<ISchemaElement> oldReferedElements = null;
        ListIterator<ISchemaElement> oldReferedElementsIterator = null;
        ISchemaElement currentSE = null;
        List<ISchemaElement> removeList = new ArrayList<>();
        for (SchemaElementCacheEntry<IObserver> schemaElementCacheEntry : this.coObserver) { // Iterator over all
                                                                                             // coObservers

            // -------- First remove old coObservers

            oldReferedElements = schemaElementCacheEntry.getReferencedElements(); // Iterate over all referenced
                                                                                  // sessions
            oldReferedElementsIterator = oldReferedElements.listIterator();
            while (oldReferedElementsIterator.hasNext()) {
                currentSE = oldReferedElementsIterator.next();
                if (currentSE instanceof ISession) {
                    if (session.getID().equals(currentSE.getID())) { // Is referenced session the searched session?
                        if (!session.getCoObservers().contains(schemaElementCacheEntry.getSchemaElement())) { // coObserver
                            // no longer
                            // part of
                            // session
                            // Remove reference to session
                            removeList.add(session);

                            // Remove reference to observations from session
                            removeList.addAll(Arrays.asList(observations));
                        }
                    }
                }
            }
            schemaElementCacheEntry.removeReferencedElements(removeList);
            removeList.clear();

            // -------- Now add new added coObservers

            // Loop must take place here, as it could be that coObservers are added who
            // didn't had any
            // references yet. (Observer that was never a coObserver before)
            if ((session.getCoObservers().contains(schemaElementCacheEntry.getSchemaElement())) // We're in the current
                    // session's
                    // list
                    && !(schemaElementCacheEntry.getReferencedElements().contains(session)) // but we are not in the
                                                                                            // cached
            // list of the
            // observer
            ) { // => we must be added
                // Add observations
                schemaElementCacheEntry.addReferencedElements(Arrays.asList(observations));

                // Add session
                schemaElementCacheEntry.addReferencedElement(session);
            }

        }

        for (IObservation observation : observations) {
            updateObservation(observation);
        }

    }

    public void updateTarget(ITarget target) {

        // Check if component list has changed
        List<ITarget> newComponents;
        if (target instanceof ITargetContaining) {
            newComponents = ((ITargetContaining) target).getComponentTargets(this.getTargets());
        } else {
            newComponents = Collections.emptyList();
        }
        List<ISchemaElement> oldComponents = new ArrayList<>();
        for (SchemaElementCacheEntry<ITarget> elementCacheEntry : this.targetContaining) {
            if (elementCacheEntry.getSchemaElement().equals(target)) {
                oldComponents = new ArrayList<>(elementCacheEntry.getReferencedElements());

                // Now we need to ensure that our cache stays up2date
                // CAUTION! FROM NOW ON this.targetContaining[] shall be no longer accessed in
                // this method
                // Always use newComponents and oldComponents lists!!!

                // Remove all old references (to component targets)
                elementCacheEntry.clearAllReferences();
                // Add new referenced (to component targets)
                List<ISchemaElement> data = new ArrayList<>();
                if (target instanceof ITargetContaining) {
                    data.addAll(((ITargetContaining) target).getComponentTargets(this.getTargets()));
                }
                elementCacheEntry.addReferencedElements(data);

                break;
            }
        }

        if ((oldComponents.containsAll(newComponents)) && (newComponents.containsAll(oldComponents))) {
            return; // Nothing changed in regards to components
        }

        // Something changed in the components lists...
        List<ISchemaElement> removedComponent = new ArrayList<>(oldComponents);
        List<ISchemaElement> addedComponent = new ArrayList<>();
        ListIterator<ITarget> newIterator = newComponents.listIterator();
        ITarget current = null;
        while (newIterator.hasNext()) {
            current = newIterator.next();
            if (!removedComponent.contains(current)) { // Old list didn't know this Target, so this must be new
                addedComponent.add(current);
            } else {
                // OldComponents knows this Target, so remove it from the removedList (which is
                // a copy of the oldList)
                // At the end of the while loop, this will leave only the removed Targets in the
                // removedList
                removedComponent.remove(current);
            }
        }

        // Add references to added components
        ListIterator<ISchemaElement> addedIterator = addedComponent.listIterator();
        ITarget addedTarget = null;
        while (addedIterator.hasNext()) {
            addedTarget = (ITarget) addedIterator.next();
            for (SchemaElementCacheEntry<ITarget> schemaElementCacheEntry : this.target) {
                if (addedTarget.equals(schemaElementCacheEntry.getSchemaElement())) {
                    // Now we need to get all observations for this (ITargetContaining) target
                    // in order to add them also to the addedTarget
                    List<ISchemaElement> observation = this.getReferedElements(target);
                    schemaElementCacheEntry.addReferencedElements(observation);
                    break; // Continue with while loop
                }
            }
        }

        // Remove references from removed added components
        ListIterator<ISchemaElement> removedterator = removedComponent.listIterator();
        ITarget removedTarget = null;
        while (removedterator.hasNext()) {
            removedTarget = (ITarget) removedterator.next();
            for (SchemaElementCacheEntry<ITarget> schemaElementCacheEntry : this.target) {
                if (removedTarget.equals(schemaElementCacheEntry.getSchemaElement())) {
                    // Now we need to get all observations for this (ITargetContaining) target
                    // in order to remove them also from the removedTarget
                    List<ISchemaElement> observation = this.getReferedElements(target);
                    schemaElementCacheEntry.removeReferencedElements(observation);
                    break; // Continue with while loop
                }
            }
        }

    }

    public List<ISchemaElement> removeEyepiece(IEyepiece eyepiece) {

        List<ISchemaElement> dependencies = this.observation.stream().filter(o -> {
            IEyepiece element = o.getSchemaElement().getEyepiece();
            return element != null && element.equals(eyepiece);
        }).map(a -> a.getSchemaElement()).collect(Collectors.toList());

        SchemaElementCacheEntry<IEyepiece> observationEntry = new SchemaElementCacheEntry<IEyepiece>(eyepiece);
        boolean foundElement = this.eyepiece.remove(observationEntry);

        if (!foundElement) { // Observation was not found, stop here and return empty list
            return Collections.emptyList();
        }

        return dependencies;

    }

    public List<ISchemaElement> removeImager(IImager imager) {

        List<ISchemaElement> dependencies = this.observation.stream().filter(o -> {
            IImager element = o.getSchemaElement().getImager();
            return element != null && element.equals(imager);
        }).map(a -> a.getSchemaElement()).collect(Collectors.toList());

        SchemaElementCacheEntry<IImager> entry = new SchemaElementCacheEntry<IImager>(imager);
        boolean foundElement = this.imager.remove(entry);

        if (!foundElement) {
            return Collections.emptyList();
        }

        return dependencies;

    }

    public List<ISchemaElement> removeFilter(IFilter filter) {

        List<ISchemaElement> dependencies = this.observation.stream().filter(o -> {
            IFilter element = o.getSchemaElement().getFilter();
            return element != null && element.equals(filter);
        }).map(a -> a.getSchemaElement()).collect(Collectors.toList());

        SchemaElementCacheEntry<IFilter> entry = new SchemaElementCacheEntry<IFilter>(filter);
        boolean foundElement = this.filter.remove(entry);

        if (!foundElement) {
            return Collections.emptyList();
        }

        return dependencies;

    }

    public List<ISchemaElement> removeObservation(IObservation observation) {

        SchemaElementCacheEntry<IObservation> observationEntry = new SchemaElementCacheEntry<IObservation>(observation);
        boolean foundElement = this.observation.remove(observationEntry);

        if (!foundElement) { // Observation was not found, stop here and return empty list
            return Collections.emptyList();
        }

        // Remove observation from in all SchemaElement arrays

        // --------------------------------------------------------------------
        IEyepiece e = observation.getEyepiece();
        if (e != null) {
            for (SchemaElementCacheEntry<IEyepiece> schemaElementCacheEntry : this.eyepiece) {
                if (e.equals(schemaElementCacheEntry.getSchemaElement())) {
                    schemaElementCacheEntry.removeReferencedElement(observation);
                    break;
                }
            }
        }

        // --------------------------------------------------------------------
        IImager imager = observation.getImager();
        if (imager != null) {
            for (SchemaElementCacheEntry<IImager> schemaElementCacheEntry : this.imager) {
                if (imager.equals(schemaElementCacheEntry.getSchemaElement())) {
                    schemaElementCacheEntry.removeReferencedElement(observation);
                    break;
                }
            }
        }

        // --------------------------------------------------------------------
        IFilter filter = observation.getFilter();
        if (filter != null) {
            for (SchemaElementCacheEntry<IFilter> schemaElementCacheEntry : this.filter) {
                if (filter.equals(schemaElementCacheEntry.getSchemaElement())) {
                    schemaElementCacheEntry.removeReferencedElement(observation);
                    break;
                }
            }
        }

        // --------------------------------------------------------------------
        IObserver observer = observation.getObserver();
        if (observer != null) {
            for (SchemaElementCacheEntry<IObserver> schemaElementCacheEntry : this.observer) {
                if (observer.equals(schemaElementCacheEntry.getSchemaElement())) {
                    schemaElementCacheEntry.removeReferencedElement(observation);
                    break;
                }
            }
        }

        // --------------------------------------------------------------------
        IScope scope = observation.getScope();
        if (scope != null) {
            for (SchemaElementCacheEntry<IScope> schemaElementCacheEntry : this.scope) {
                if (scope.equals(schemaElementCacheEntry.getSchemaElement())) {
                    schemaElementCacheEntry.removeReferencedElement(observation);
                    break;
                }
            }
        }

        // --------------------------------------------------------------------
        ISession session = observation.getSession();
        if (session != null) {
            for (SchemaElementCacheEntry<ISession> entrySession : this.session) {
                // Remove coObservers (if available)
                if ((session.getCoObservers() != null) && !(session.getCoObservers().isEmpty())) {
                    List<IObserver> coObservers = session.getCoObservers();
                    ListIterator<IObserver> iterator = coObservers.listIterator();
                    IObserver current = null;
                    while (iterator.hasNext()) { // Iterate over all coObservers
                        current = iterator.next();
                        for (SchemaElementCacheEntry<IObserver> coobsever : this.coObserver) { // Iterate over all
                                                                                               // coObservers
                            if (coobsever.getSchemaElement().equals(current)) {
                                // Remove Observation from coObservers refered elements
                                coobsever.removeReferencedElement(observation);

                                break; // Break for loop
                            }
                        }
                    }
                }

                if (session.equals(entrySession.getSchemaElement())) {
                    entrySession.removeReferencedElement(observation);
                    break;
                }
            }
        }

        // --------------------------------------------------------------------
        ISite site = observation.getSite();
        if (site != null) {
            for (SchemaElementCacheEntry<ISite> schemaElementCacheEntry : this.site) {
                if (site.equals(schemaElementCacheEntry.getSchemaElement())) {
                    schemaElementCacheEntry.removeReferencedElement(observation);
                    break;
                }
            }
        }

        // --------------------------------------------------------------------
        ITarget target = observation.getTarget();
        if (target != null) {
            for (SchemaElementCacheEntry<ITarget> elementCacheEntry : this.target) {
                if (target.equals(elementCacheEntry.getSchemaElement())) {
                    elementCacheEntry.removeReferencedElement(observation);

                    // If the current target is a TargetContaining target, we need to remove also
                    // the observation references from the containing targets
                    if (elementCacheEntry.getSchemaElement() instanceof ITargetContaining) {
                        List<ITarget> containingTargets = ((ITargetContaining) elementCacheEntry.getSchemaElement())
                                .getComponentTargets(this.getTargets());
                        ListIterator<ITarget> listIterator = containingTargets.listIterator();
                        ITarget ct = null;
                        while (listIterator.hasNext()) {
                            ct = listIterator.next();
                            for (SchemaElementCacheEntry<ITarget> schemaElementCacheEntry : this.target) {
                                if (schemaElementCacheEntry.getSchemaElement().equals(ct)) { // Found a depending target
                                    schemaElementCacheEntry.removeReferencedElement(observation);
                                }
                            }
                        }
                    }

                    break;
                }
            }
        }

        // --------------------------------------------------------------------
        ILens lens = observation.getLens();
        if (lens != null) {
            for (SchemaElementCacheEntry<ILens> len : this.lens) {
                if (lens.equals(len.getSchemaElement())) {
                    len.removeReferencedElement(observation);
                    break;
                }
            }
        }

        // An observation doesn't have any dependencies, so always return empty list
        return new ArrayList<ISchemaElement>();

    }

    public List<ISchemaElement> removeObserver(IObserver observer) {

        List<IObservation> dependenciesObservation = this.observation.stream().filter(o -> {
            IObserver element = o.getSchemaElement().getObserver();
            return element != null && element.equals(observer);
        }).map(a -> a.getSchemaElement()).collect(Collectors.toList());

        List<ISession> dependenciesSession = this.session.stream().filter(o -> {
            List<IObserver> element = o.getSchemaElement().getCoObservers();
            return element != null && element.contains(observer);
        }).map(a -> a.getSchemaElement()).collect(Collectors.toList());

        SchemaElementCacheEntry<IObserver> entry = new SchemaElementCacheEntry<IObserver>(observer);
        boolean foundElement = this.observer.remove(entry) || this.coObserver.remove(entry);

        if (!foundElement) {
            return Collections.emptyList();
        }

        List<ISchemaElement> dependencies = new ArrayList<>();
        dependencies.addAll(dependenciesObservation);
        dependencies.addAll(dependenciesSession);

        return dependencies;

    }

    public List<ISchemaElement> removeScope(IScope scope) {

        List<ISchemaElement> dependencies = this.observation.stream().filter(o -> {
            IScope element = o.getSchemaElement().getScope();
            return element != null && element.equals(scope);
        }).map(a -> a.getSchemaElement()).collect(Collectors.toList());

        SchemaElementCacheEntry<IScope> entry = new SchemaElementCacheEntry<IScope>(scope);
        boolean foundElement = this.scope.remove(entry);

        if (!foundElement) {
            return Collections.emptyList();
        }

        return dependencies;

    }

    public List<ISchemaElement> removeSession(ISession session) {

        List<ISchemaElement> dependencies = this.observation.stream().filter(o -> {
            ISession element = o.getSchemaElement().getSession();
            return element != null && element.equals(session);
        }).map(a -> a.getSchemaElement()).collect(Collectors.toList());

        SchemaElementCacheEntry<ISession> entry = new SchemaElementCacheEntry<ISession>(session);
        boolean foundElement = this.session.remove(entry);

        if (!foundElement) {
            return Collections.emptyList();
        }

        return dependencies;

    }

    public List<ISchemaElement> removeSite(ISite site) {

        List<IObservation> dependenciesObservation = this.observation.stream().filter(o -> {
            ISite element = o.getSchemaElement().getSite();
            return element != null && element.equals(site);
        }).map(a -> a.getSchemaElement()).collect(Collectors.toList());

        List<ISession> dependenciesSession = this.session.stream().filter(o -> {
            ISite element = o.getSchemaElement().getSite();
            return element != null && element.equals(site);
        }).map(a -> a.getSchemaElement()).collect(Collectors.toList());

        SchemaElementCacheEntry<ISite> entry = new SchemaElementCacheEntry<ISite>(site);
        boolean foundElement = this.site.remove(entry);

        if (!foundElement) {
            return Collections.emptyList();
        }

        List<ISchemaElement> dependencies = new ArrayList<>();
        dependencies.addAll(dependenciesObservation);
        dependencies.addAll(dependenciesSession);

        return dependencies;

    }

    public List<ISchemaElement> removeTarget(ITarget target) {

        List<ISchemaElement> dependenciesTarget = this.observation.stream().filter(o -> {
            ITarget element = o.getSchemaElement().getTarget();
            return element != null && element.equals(target);
        }).map(a -> a.getSchemaElement()).collect(Collectors.toList());

        List<ISchemaElement> dependenciesTargetContaining = this.observation.stream().filter(o -> {
            ITarget element = o.getSchemaElement().getTarget();
            return element != null && !element.equals(target);
        }).map(a -> a.getSchemaElement()).filter(a -> a instanceof ITargetContaining)
                .filter(a -> ((ITargetContaining) a).getComponentTargets(this.getTargets()).contains(target))
                .collect(Collectors.toList());

        SchemaElementCacheEntry<ITarget> entry = new SchemaElementCacheEntry<ITarget>(target);
        boolean foundElement = this.target.remove(entry);

        List<ISchemaElement> dependencies = new ArrayList<>();
        dependencies.addAll(dependenciesTarget);
        dependencies.addAll(dependenciesTargetContaining);

        if (foundElement && dependencies.isEmpty()) { // Object has no dependencies

            // Delete entry in targetContained array
            // As the ITargetContaining target must have no more observation references
            // while deletion,
            // it's components must have lost its referenced observations as well. (handled
            // in updateObservation or
            // deleteObservation)
            // So we only need to do some houseKeeping here and keep the cache Array clean

            for (SchemaElementCacheEntry<ITarget> entryTarget : this.targetContaining) {
                if (target.equals(entry.getSchemaElement())) {
                    this.targetContaining.remove(entryTarget);
                    break;
                }
            }
        }

        return dependencies;
    }

    public List<ISchemaElement> removeLens(ILens lens) {

        List<ISchemaElement> dependencies = this.observation.stream().filter(o -> {
            ILens element = o.getSchemaElement().getLens();
            return element != null && element.equals(lens);
        }).map(a -> a.getSchemaElement()).collect(Collectors.toList());

        SchemaElementCacheEntry<ILens> entry = new SchemaElementCacheEntry<ILens>(lens);
        boolean foundElement = this.lens.remove(entry);

        if (!foundElement) {
            return Collections.emptyList();
        }

        return dependencies;

    }

    public void setXMLPath(String path) {

        this.xmlPath = path;

    }

    private void addAllObservationElements(SchemaElementCacheEntry<IObservation> observationCacheEntry) {

        IObservation observation = observationCacheEntry.getSchemaElement();
        if (observation == null) {
            return;
        }

        // --------------------------------------------------------------

        IEyepiece e = observation.getEyepiece();
        if (e != null) {
            for (SchemaElementCacheEntry<IEyepiece> schemaElementCacheEntry : this.eyepiece) {
                if (schemaElementCacheEntry.getSchemaElement().equals(e)) {
                    // Add Observation to schemaElements refered elements
                    schemaElementCacheEntry.addReferencedElement(observation);
                    // Add schemaElement to observationCache entry
                    observationCacheEntry.addReferencedElement(e);
                    break;
                }
            }
        }

        // --------------------------------------------------------------

        IFilter f = observation.getFilter();
        if (f != null) {
            for (SchemaElementCacheEntry<IFilter> schemaElementCacheEntry : this.filter) {
                if (schemaElementCacheEntry.getSchemaElement().equals(f)) {
                    // Add Observation to schemaElements refered elements
                    schemaElementCacheEntry.addReferencedElement(observation);
                    // Add schemaElement to observationCache entry
                    observationCacheEntry.addReferencedElement(f);
                    break;
                }
            }
        }

        // --------------------------------------------------------------

        IImager im = observation.getImager();
        if (im != null) {
            for (SchemaElementCacheEntry<IImager> schemaElementCacheEntry : this.imager) {
                if (schemaElementCacheEntry.getSchemaElement().equals(im)) {
                    // Add Observation to schemaElements refered elements
                    schemaElementCacheEntry.addReferencedElement(observation);
                    // Add schemaElement to observationCache entry
                    observationCacheEntry.addReferencedElement(im);
                    break;
                }
            }
        }

        // --------------------------------------------------------------

        IObserver o = observation.getObserver();
        if (o != null) {
            for (SchemaElementCacheEntry<IObserver> schemaElementCacheEntry : this.observer) {
                if (schemaElementCacheEntry.getSchemaElement().equals(o)) {
                    // Add Observation to schemaElements refered elements
                    schemaElementCacheEntry.addReferencedElement(observation);
                    // Add schemaElement to observationCache entry
                    observationCacheEntry.addReferencedElement(o);
                    break;
                }
            }
        }

        // --------------------------------------------------------------

        IScope sc = observation.getScope();
        if (sc != null) {
            for (SchemaElementCacheEntry<IScope> schemaElementCacheEntry : this.scope) {
                if (schemaElementCacheEntry.getSchemaElement().equals(sc)) {
                    // Add Observation to schemaElements refered elements
                    schemaElementCacheEntry.addReferencedElement(observation);
                    // Add schemaElement to observationCache entry
                    observationCacheEntry.addReferencedElement(sc);
                    break;
                }
            }
        }

        // --------------------------------------------------------------

        ISession s = observation.getSession();
        if (s != null) {
            for (SchemaElementCacheEntry<ISession> elementCacheEntry : this.session) {

                if (elementCacheEntry.getSchemaElement().equals(s)) {

                    // Add coObservers (if available)
                    if ((s.getCoObservers() != null) && !(s.getCoObservers().isEmpty())) {
                        List<IObserver> coObservers = s.getCoObservers();
                        ListIterator<IObserver> iterator = coObservers.listIterator();
                        IObserver current = null;
                        while (iterator.hasNext()) { // Iterate over all coObservers
                            current = iterator.next();
                            for (SchemaElementCacheEntry<IObserver> schemaElementCacheEntry : this.coObserver) { // Iterate
                                                                                                                 // over
                                // all coObservers
                                if (schemaElementCacheEntry.getSchemaElement().equals(current)) {
                                    // Add Observation to coObservers refered elements
                                    schemaElementCacheEntry.addReferencedElement(observation);

                                    // Session might be referenced already from another observation
                                    // belonging to the same session
                                    // TODO: BUG uncomment this entry if
                                    /*
                                     * if (!schemaElementCacheEntry.contains(observation.getSession())) { // Also
                                     * include a reference to the session, as we need this in case // the session will
                                     * get updated to know, which coObserver was // refering to that session in the past
                                     * (for removing coObservers)
                                     * schemaElementCacheEntry.addReferencedElement(observation.getSession()); }
                                     */

                                    // Do not add the coObserver to the observationCacheEntry
                                    // So the observations will have not direct dependency on the coObserver
                                    break; // Break for loop
                                }
                            }
                        }
                    }

                    // Add Observation to schemaElements refered elements
                    elementCacheEntry.addReferencedElement(observation);
                    // Add schemaElement to observationCache entry
                    observationCacheEntry.addReferencedElement(s);
                    break;
                }
            }
        }

        // --------------------------------------------------------------

        ISite si = observation.getSite();
        if (si != null) {
            for (SchemaElementCacheEntry<ISite> schemaElementCacheEntry : this.site) {
                if (schemaElementCacheEntry.getSchemaElement().equals(si)) {
                    // Add Observation to schemaElements refered elements
                    schemaElementCacheEntry.addReferencedElement(observation);
                    // Add schemaElement to observationCache entry
                    observationCacheEntry.addReferencedElement(si);
                    break;
                }
            }
        }

        // --------------------------------------------------------------

        ITarget t = observation.getTarget();
        if (t != null) {
            for (SchemaElementCacheEntry<ITarget> elementCacheEntry : this.target) {
                if (elementCacheEntry.getSchemaElement().equals(t)) {
                    // Add Observation to schemaElements refered elements
                    elementCacheEntry.addReferencedElement(observation);

                    if (t instanceof ITargetContaining) { // This Target refers to additional other targets
                        List<ITarget> containedTargets = ((ITargetContaining) t).getComponentTargets(this.getTargets());
                        ListIterator<ITarget> iterator = containedTargets.listIterator();
                        ITarget ct = null;
                        // Go over all dependent targets and add a reference to this observation
                        // ! The observation itself won't get a reference to the additional Target !
                        while (iterator.hasNext()) {
                            ct = iterator.next();
                            for (SchemaElementCacheEntry<ITarget> schemaElementCacheEntry : this.target) {
                                if (schemaElementCacheEntry.getSchemaElement().equals(ct)) {
                                    schemaElementCacheEntry.addReferencedElement(observation);
                                }
                            }
                        }
                    }

                    // Add schemaElement to observationCache entry
                    observationCacheEntry.addReferencedElement(t);
                    break;
                }
            }
        }

        // --------------------------------------------------------------

        ILens l = observation.getLens();
        if (l != null) {
            for (SchemaElementCacheEntry<ILens> len : this.lens) {
                if (len.getSchemaElement().equals(l)) {
                    // Add Observation to schemaElements refered elements
                    len.addReferencedElement(observation);
                    // Add schemaElement to observationCache entry
                    observationCacheEntry.addReferencedElement(l);
                    break;
                }
            }
        }

    }

}
