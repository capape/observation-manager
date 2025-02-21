package de.lehmannet.om.model;

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
import de.lehmannet.om.ui.util.IConfiguration;
import java.io.File;
import java.util.List;
import java.util.Optional;
import org.w3c.dom.Document;

/**
 * Model for observation manager.
 *
 * @autor capapegil
 */
public interface ObservationManagerModel {

    Document getDocument(Document doc);

    Document getDocumentForElement(ISchemaElement schemaElement);

    void setSelectedElement(ISchemaElement selected);

    ISchemaElement getSelectedElement();

    boolean hasChanged();

    void setChanged(boolean b);

    void setTitle(String title);

    String getTittle();

    Optional<String> getRootName();

    IConfiguration getConfiguration();

    void clear();

    boolean isEmpty();

    void add(ISchemaElement element);

    void add(ISchemaElement element, boolean dependend);

    List<ISchemaElement> remove(ISchemaElement element);

    void update(ISchemaElement element);

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

    boolean loadObservations(String file);

    List<File> getFilesFromPath(List<String> imagePath);

    String getXMLFileForSchemaElement(ISchemaElement schemaElement);

    String getXMLPathForSchemaElement(ISchemaElement schemaElement);

    File getExportFile(final String filename, final String extension);

    String[] getAllOpenedFiles();

    boolean save(String name);

    boolean saveAs(String oldPath, String newPath);
}
