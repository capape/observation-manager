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

/**
 * Model for observation manager.
 * 
 * @autor capapegil
 */
public interface ObservationManagerModel {

    
    boolean hasChanged();

	void setChanged(boolean b);

    void setTitle(String title);

    String getTittle();

    @Deprecated 
    XMLFileLoader getXmlCache();


    void clear();

    boolean isEmpty();

    public void add(ISchemaElement element);
    
    public void add(ISchemaElement element, boolean dependend) ;
    
    public List<ISchemaElement> remove(ISchemaElement element);
    
    public void update(ISchemaElement element);
    
    public IObserver[] getObservers();

    public IEyepiece[] getEyepieces();

    public IImager[] getImagers() ;

    public IFilter[] getFilters();

    public IObservation[] getObservations();
    
    public IObservation[] getObservations(ISchemaElement element);

    public IObservation[] getCoObserverObservations(IObserver observer);

    public IScope[] getScopes() ;

    public ISession[] getSessions();

    public ISite[] getSites() ;

    public ITarget[] getTargets();

    public ILens[] getLenses();    
    
    boolean loadObservations(String file);
}