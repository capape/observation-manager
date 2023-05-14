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

    /**
     * Clear internal data cache
     */
    void clear();

    /**
     * @return true if no xml file is loaded
     */
    boolean isEmpty();

    /**
     * Save the current document
     *
     * @param path
     *            Path to save xml data
     * @return true if file is saved, false in other case
     */
    boolean save(String path);

    /**
     * Save the current document in new path
     *
     * @param path
     *            oldPath to save xml data
     * @param path
     *            new path to save xml data
     * @return true if file is saved, false in other case
     */
    boolean saveAs(String oldPath, String newPath);

    /**
     * @return internal xml document or null if not exists
     */
    Document getDocument();

    String getXMLFileForSchemaElement(ISchemaElement schemaElement);

    String getXMLPathForSchemaElement(ISchemaElement schemaElement);

    Document getDocumentForSchemaElement(ISchemaElement schemaElement);

    void addSchemaElement(ISchemaElement element);

    void addSchemaElement(ISchemaElement element, boolean dependend);

    List<ISchemaElement> removeSchemaElement(ISchemaElement element);

    void updateSchemaElement(ISchemaElement element);

    String[] getAllOpenedFiles();

    IObserver[] getObservers();

    IEyepiece[] getEyepieces();

    IImager[] getImagers();

    IFilter[] getFilters();

    IObservation[] getObservations();

    IObservation[] getObservations(ISchemaElement element);

    IObservation[] getCoObserverObservations(IObserver observer);

    IScope[] getScopes();

    ISession[] getSessions();

    ISite[] getSites();

    ITarget[] getTargets();

    ILens[] getLenses();

    boolean loadObservations(String xmlPath);

}
