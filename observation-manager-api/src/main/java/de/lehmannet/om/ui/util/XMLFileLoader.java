

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

    String getXMLFileForSchemaElement(ISchemaElement schemaElement);    

    String getXMLPathForSchemaElement(ISchemaElement schemaElement);

    Document getDocumentForSchemaElement(ISchemaElement schemaElement);

    void addSchemaElement(ISchemaElement element);
    void addSchemaElement(ISchemaElement element, boolean dependend) ;
    List<ISchemaElement> removeSchemaElement(ISchemaElement element);
    void updateSchemaElement(ISchemaElement element);
    String[] getAllOpenedFiles();
    IObserver[] getObservers();

    IEyepiece[] getEyepieces();

    IImager[] getImagers() ;

    IFilter[] getFilters();

    IObservation[] getObservations();
    IObservation[] getObservations(ISchemaElement element);

    IObservation[] getCoObserverObservations(IObserver observer);

    IScope[] getScopes() ;

    ISession[] getSessions();

    ISite[] getSites() ;

    ITarget[] getTargets();

    ILens[] getLenses();

    boolean loadObservations(String xmlPath);

   
}



