package de.lehmannet.om.model;

import java.util.List;

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
import de.lehmannet.om.ui.util.XMLFileLoader;

public class ObservationManagerModelImpl implements ObservationManagerModel {

    private boolean changed = false;

    private static final String CHANGED_SUFFIX = " *";
    private String title = "";
    private String titleWhenChanges = CHANGED_SUFFIX;

    private final XMLFileLoader xmlCache;

    public ObservationManagerModelImpl(XMLFileLoader cache) {
        this.xmlCache = cache;

    }

    @Override
    public XMLFileLoader getXmlCache() {
        return this.xmlCache;
    }

    @Override
    public boolean hasChanged() {
        return changed;
    }

    @Override
    public void setChanged(boolean b) {
        this.changed = true;

    }

    @Override
    public void setTitle(String title) {
        if (!this.title.equals(title)) {
            this.changed = true;
            this.title = title;
            this.titleWhenChanges = this.title + CHANGED_SUFFIX;

        }

    }

    @Override
    public String getTittle() {
        if (changed) {
            return titleWhenChanges;
        } else {
            return title;
        }
    }

    @Override
    public void clear() {
       this.xmlCache.clear();
       this.setChanged(true);
    }

    @Override
    public boolean isEmpty() {
        
        return this.xmlCache.isEmpty();
    }

    @Override
    public void add(ISchemaElement element) {
       this.xmlCache.addSchemaElement(element);
       this.setChanged(true);
    }

    @Override
    public void add(ISchemaElement element, boolean dependend) {
        this.xmlCache.addSchemaElement(element,dependend);
        this.setChanged(true);
    }

    @Override
    public List<ISchemaElement> remove(ISchemaElement element) {
       
        final List<ISchemaElement> result = this.xmlCache.removeSchemaElement(element);
        this.setChanged(true);
        return result;
    }

    @Override
    public void update(ISchemaElement element) {
        this.xmlCache.updateSchemaElement(element);
        this.setChanged(true);

    }

    @Override
    public IObserver[] getObservers() {
        return this.xmlCache.getObservers();
    }

    @Override
    public IEyepiece[] getEyepieces() {
        return this.xmlCache.getEyepieces();
    }

    @Override
    public IImager[] getImagers() {
        return this.xmlCache.getImagers();
    }

    @Override
    public IFilter[] getFilters() {
        
        return this.xmlCache.getFilters();
    }

    @Override
    public IObservation[] getObservations() {
        return this.xmlCache.getObservations();
    }

    @Override
    public IObservation[] getObservations(ISchemaElement element) {
        
        return this.xmlCache.getObservations(element);
    }

    @Override
    public IObservation[] getCoObserverObservations(IObserver observer) {
        return this.xmlCache.getCoObserverObservations(observer);
    }

    @Override
    public IScope[] getScopes() {
        
        return this.xmlCache.getScopes();
    }

    @Override
    public ISession[] getSessions() {
        return this.xmlCache.getSessions();
    }

    @Override
    public ISite[] getSites() {
        return this.xmlCache.getSites();
    }

    @Override
    public ITarget[] getTargets() {
        return this.xmlCache.getTargets();
    }

    @Override
    public ILens[] getLenses() {
        return this.xmlCache.getLenses();
    }

 
    @Override
    public boolean loadObservations(final String filePath) {
        this.clear();
        return this.xmlCache.loadObservations(filePath);
    }
}