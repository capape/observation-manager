/* ====================================================================
 * /util/XMLFileLoader.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.util;

import java.util.List;

import org.w3c.dom.Document;

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

public interface XMLFileLoader {

    void clear();

    boolean isEmpty();

    boolean save(String path);

    boolean saveAs(String oldPath, String newPath);

    Document getDocument();

    /*
     * public Document getDocumentForObservation(IObservation observation) {
     * 
     * RootElement root = new RootElement();
     * 
     * // @todo This only works for ONE file opened... if( observation == null ) { // Nothing to save return null; }
     * 
     * this.addObservationAndDependentToRoot(observation, root);
     * 
     * try { if( root != null ) { return root.getDocument(); } else {
     * System.err.println("Unable to retrieve DOM Document\n"); } } catch(SchemaException se) {
     * System.err.println("Unable to retrieve DOM Document\n" + se); }
     * 
     * return null;
     * 
     * }
     */

    public String getXMLFileForSchemaElement(ISchemaElement schemaElement);

    public String getXMLPathForSchemaElement(ISchemaElement schemaElement);

    public Document getDocumentForSchemaElement(ISchemaElement schemaElement);

    public void addSchemaElement(ISchemaElement element);
    public void addSchemaElement(ISchemaElement element, boolean dependend) ;
    public List<ISchemaElement> removeSchemaElement(ISchemaElement element);
    public void updateSchemaElement(ISchemaElement element);
    public String[] getAllOpenedFiles();
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

    public boolean loadObservations(String xmlPath);

   
}



