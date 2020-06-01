/* ====================================================================
 * /util/XMLFileLoader.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import de.lehmannet.om.ITargetContaining;
import de.lehmannet.om.OALException;
import de.lehmannet.om.RootElement;
import de.lehmannet.om.ui.comparator.EyepieceComparator;
import de.lehmannet.om.ui.comparator.FilterComparator;
import de.lehmannet.om.ui.comparator.ImagerComparator;
import de.lehmannet.om.ui.comparator.LensComparator;
import de.lehmannet.om.ui.comparator.ObservationComparator;
import de.lehmannet.om.ui.comparator.ObserverComparator;
import de.lehmannet.om.ui.comparator.ScopeComparator;
import de.lehmannet.om.ui.comparator.SessionComparator;
import de.lehmannet.om.ui.comparator.SiteComparator;
import de.lehmannet.om.ui.comparator.TargetComparator;
import de.lehmannet.om.util.SchemaException;
import de.lehmannet.om.util.SchemaLoader;

// All get{SchemaElement} are extremly slow. Implement faster caching!
public class XMLFileLoaderImpl implements XMLFileLoader {

    // Maps, used to store File - XML Object relations
    // - Key = Path to XML File
    // - Value = Schema objects of xmlFile
    private final List<CacheEntry> cache = new ArrayList<>();

    // Path to XML Schemas used to validate XML files
    private File schemaPath;

    // The schemaLoader to use
    private final SchemaLoader loader = new SchemaLoader();
    private static final Logger LOGGER = LoggerFactory.getLogger(XMLFileLoaderImpl.class);

    public static final XMLFileLoader newInstance(String pathFile) {

        final File file = new File(pathFile);
        if (!file.exists()) {

            LOGGER.error("Comast schema path not found:{} \n.", pathFile);
        }
        return new XMLFileLoaderImpl(file);
    };

    private XMLFileLoaderImpl(File file) {

        this.schemaPath = file;
    }

    public void clear() {

        this.cache.clear();

    }

    public boolean isEmpty() {

        RootElement root = this.getRootElement();

        return root == null;

    }

    public boolean save(String path) {

        return this.saveAs(null, path);

    }

    public boolean saveAs(String oldPath, String newPath) {

        RootElement root = this.getRootElement();

        try {
            Objects.requireNonNull(root).serializeAsXml(new File(newPath));
            // this.loadObservations(newPath); // Fill cache .... Not good! Strange
            // behaviour. After save, first try to do
            // chnaged (e.g. stellar etc) is not taken. Second try works...) Better solution
            // below!
            ((CacheEntry) this.cache.iterator().next()).setXMLPath(newPath); // Works only with one XML!!!
        } catch (SchemaException se) {
            System.err.println("Unable to write file: " + newPath + "\n" + se);
            return false;
        }

        return true;

    }

    public Document getDocument() {

        RootElement root = this.getRootElement();

        try {
            if (root != null) {
                return root.getDocument();
            } else {
                System.err.println("Unable to retrieve DOM Document\n");
            }
        } catch (SchemaException se) {
            System.err.println("Unable to retrieve DOM Document\n" + se);
        }

        return null;

    }

    /*
     * public Document getDocumentForObservation(IObservation observation) {
     * 
     * RootElement root = new RootElement();
     * 
     * // @todo This only works for ONE file opened... if( observation == null ) {
     * // Nothing to save return null; }
     * 
     * this.addObservationAndDependentToRoot(observation, root);
     * 
     * try { if( root != null ) { return root.getDocument(); } else {
     * System.err.println("Unable to retrieve DOM Document\n"); } }
     * catch(SchemaException se) {
     * System.err.println("Unable to retrieve DOM Document\n" + se); }
     * 
     * return null;
     * 
     * }
     */

    public String getXMLFileForSchemaElement(ISchemaElement schemaElement) {

        // @todo This only works for ONE file opened...
        if (this.cache.isEmpty()) {
            // Nothing to save
            return null;
        }

        // @todo This only works for ONE file opened...
        return this.cache.listIterator().next().getXmlPath();

    }

    public String getXMLPathForSchemaElement(ISchemaElement schemaElement) {

        return new File(this.getXMLFileForSchemaElement(schemaElement)).getParent();

    }

    public Document getDocumentForSchemaElement(ISchemaElement schemaElement) {

        // @todo This only works for ONE file opened...

        if (schemaElement == null) {
            // Nothing to save
            return null;
        }

        RootElement root = new RootElement();

        // Get all observations for SchemaElement
        if (schemaElement instanceof IObservation) { // Only add one observation to Document
            this.addObservationAndDependentToRoot((IObservation) schemaElement, root);
        } else { // Add several observations to Document
            IObservation[] observations = this.getObservations(schemaElement);

            if ((observations == null) || (observations.length == 0)) {
                return null; // No observations for this schemaElement
            }

            this.addObservationsAndDependentToRoot(Arrays.asList(observations), root);
        }

        // Return document if it was be created
        try {
            return root.getDocument();
        } catch (SchemaException se) {
            System.err.println("Unable to retrieve DOM Document for " + schemaElement + "\n" + se);
        }

        return null;

    }

    public void addSchemaElement(ISchemaElement element) {

        if (element == null) { // Nothing to add
            return;
        }

        CacheEntry entry = null;
        if (this.cache.isEmpty()) {
            entry = new CacheEntry();
            this.cache.add(entry);
        } else { // @todo This only works for ONE file opened...
            entry = (CacheEntry) this.cache.iterator().next();
        }

        if (element instanceof IObserver) {
            entry.addObserver((IObserver) element);
        } else if (element instanceof IEyepiece) {
            entry.addEyepiece((IEyepiece) element);
        } else if (element instanceof IImager) {
            entry.addImager((IImager) element);
        } else if (element instanceof IFilter) {
            entry.addFilter((IFilter) element);
        } else if (element instanceof IObservation) {
            entry.addObservation((IObservation) element);
        } else if (element instanceof IScope) {
            entry.addScope((IScope) element);
        } else if (element instanceof ISite) {
            entry.addSite((ISite) element);
        } else if (element instanceof ISession) {
            entry.addSession((ISession) element);
        } else if (element instanceof ITarget) {
            entry.addTarget((ITarget) element);
        } else if (element instanceof ILens) {
            entry.addLens((ILens) element);
        } else {
            System.out.print("Unknown element: " + element);
        }

    }

    public void addSchemaElement(ISchemaElement element, boolean dependend) {

        if (element == null) { // Nothing to add
            return;
        }

        CacheEntry entry = null;
        if (cache.isEmpty()) {
            entry = new CacheEntry();
            cache.add(entry);
        } else { // @todo This only works for ONE file opened...
            entry = (CacheEntry) cache.iterator().next();
        }

        if (element instanceof IObserver) {
            entry.addObserver((IObserver) element);
        } else if (element instanceof IEyepiece) {
            entry.addEyepiece((IEyepiece) element);
        } else if (element instanceof IImager) {
            entry.addImager((IImager) element);
        } else if (element instanceof IFilter) {
            entry.addFilter((IFilter) element);
        } else if (element instanceof IObservation) {

            entry.addObservation((IObservation) element);

            IObservation observation = (IObservation) element;
            if (observation.getObserver() != null) {
                entry.addObserver(observation.getObserver());
            }
            if (observation.getEyepiece() != null) {
                entry.addEyepiece(observation.getEyepiece());
            }
            if (observation.getImager() != null) {
                entry.addImager(observation.getImager());
            }
            if (observation.getFilter() != null) {
                entry.addFilter(observation.getFilter());
            }
            if (observation.getScope() != null) {
                entry.addScope(observation.getScope());
            }
            if (observation.getSite() != null) {
                entry.addSite(observation.getSite());
            }
            if (observation.getSession() != null) {
                entry.addSession(observation.getSession());

                ISession session = observation.getSession();
                if (session.getCoObservers() != null) {
                    for (Object o : session.getCoObservers()) {
                        entry.addObserver((IObserver) o);
                    }
                }
                if (session.getSite() != null) {
                    entry.addSite(session.getSite());
                }
            }
            if (observation.getTarget() != null) {
                entry.addTarget(observation.getTarget());
            }
            if (observation.getLens() != null) {
                entry.addLens(observation.getLens());
            }
        } else if (element instanceof IScope) {
            entry.addScope((IScope) element);
        } else if (element instanceof ISite) {
            entry.addSite((ISite) element);
        } else if (element instanceof ISession) {
            entry.addSession((ISession) element);

            ISession session = (ISession) element;
            if (session.getCoObservers() != null) {
                for (Object o : session.getCoObservers()) {
                    entry.addObserver((IObserver) o);
                }
            }
            if (session.getSite() != null) {
                entry.addSite(session.getSite());
            }
        } else if (element instanceof ITarget) {
            entry.addTarget((ITarget) element);
        } else if (element instanceof ILens) {
            entry.addLens((ILens) element);
        } else {
            System.out.print("Unknown element: " + element);
        }

    }

    public List<ISchemaElement> removeSchemaElement(ISchemaElement element) {

        List<ISchemaElement> resultList = new ArrayList<>();

        CacheEntry entry = null;
        if (this.cache.isEmpty()) {
            return resultList;
        } else { // @todo This only works for ONE file opened...
            entry = (CacheEntry) this.cache.iterator().next();
        }

        if (element instanceof IObserver) {
            resultList = entry.removeObserver((IObserver) element);
        } else if (element instanceof IEyepiece) {
            resultList = entry.removeEyepiece((IEyepiece) element);
        } else if (element instanceof IImager) {
            resultList = entry.removeImager((IImager) element);
        } else if (element instanceof IFilter) {
            resultList = entry.removeFilter((IFilter) element);
        } else if (element instanceof IObservation) {
            resultList = entry.removeObservation((IObservation) element);
        } else if (element instanceof IScope) {
            resultList = entry.removeScope((IScope) element);
        } else if (element instanceof ISite) {
            resultList = entry.removeSite((ISite) element);
        } else if (element instanceof ISession) {
            resultList = entry.removeSession((ISession) element);
        } else if (element instanceof ITarget) {
            resultList = entry.removeTarget((ITarget) element);
        } else if (element instanceof ILens) {
            resultList = entry.removeLens((ILens) element);
        } else {
            System.out.print("Unknown element for deletion: " + element);
            return null; // Return null to indicate error
        }

        return resultList;

    }

    public void updateSchemaElement(ISchemaElement element) {

        if (element instanceof IObservation) {
            IObservation observation = (IObservation) element;

            CacheEntry entry = null;
            if (this.cache.isEmpty()) {
                return;
            } else { // @todo This only works for ONE file opened...
                entry = (CacheEntry) this.cache.iterator().next();
            }

            entry.updateObservation(observation);

        } else if (element instanceof ISession) {
            // CoObservers might have changed, which has an effect on the
            // observation cache (coObserver) entries
            ISession session = (ISession) element;

            CacheEntry entry = null;
            if (this.cache.isEmpty()) {
                return;
            } else { // @todo This only works for ONE file opened...
                entry = (CacheEntry) this.cache.iterator().next();
            }

            IObservation[] observations = this.getObservations(element);
            if ((observations != null) // If there are no observations for this session no need for an update
                    && (observations.length > 0)) {
                entry.updateSession(observations, session);
            }

        } else if ((element instanceof ITarget) && (element instanceof ITargetContaining)) {
            // Refered targets might have changed
            ITarget target = (ITarget) element;

            CacheEntry entry = null;
            if (this.cache.isEmpty()) {
                return;
            } else { // @todo This only works for ONE file opened...
                entry = (CacheEntry) this.cache.iterator().next();
            }

            entry.updateTarget(target);

        }

    }

    public String[] getAllOpenedFiles() {

        String[] files = new String[this.cache.size()];
        ListIterator<CacheEntry> iterator = this.cache.listIterator();
        int x = 0;
        String path = null;
        while (iterator.hasNext()) {
            path = iterator.next().getXmlPath();
            if (path != null) {
                files[x++] = path;
            }
        }

        // Only dummy entry in cache?
        if ((files.length == 1) && (files[0] == null)) {
            return null;
        }

        return files;

    }

    public IObserver[] getObservers() {

        IObserver[] result = null;
        if (this.cache.size() == 1) { // Only one file open
            result = this.cache.get(0).getObservers();
        } else {
            ListIterator<CacheEntry> iterator = this.cache.listIterator();
            CacheEntry current = null;
            IObserver[] currentArray = null;
            IObserver[][] o = new IObserver[this.cache.size()][];
            int i = 0;
            int resultSize = 0;
            while (iterator.hasNext()) {
                current = iterator.next();
                currentArray = current.getObservers();
                if ((currentArray != null) && (currentArray.length > 0)) {
                    o[i++] = currentArray;
                    resultSize = resultSize + currentArray.length;
                }
            }

            result = new IObserver[resultSize];
            if (resultSize == 0) {
                return result;
            }

            int j = 0;
            for (IObserver[] iObservers : o) {
                currentArray = iObservers;
                for (IObserver iObserver : currentArray) {
                    result[j++] = iObserver;
                }
            }
        }

        Arrays.sort(result, new ObserverComparator());

        return result;

    }

    public IEyepiece[] getEyepieces() {

        IEyepiece[] result = null;
        if (this.cache.size() == 1) { // Only one file open
            result = this.cache.get(0).getEyepieces();
        } else {
            ListIterator<CacheEntry> iterator = this.cache.listIterator();
            CacheEntry current = null;
            IEyepiece[] currentArray = null;
            IEyepiece[][] o = new IEyepiece[this.cache.size()][];
            int i = 0;
            int resultSize = 0;
            while (iterator.hasNext()) {
                current = iterator.next();
                currentArray = current.getEyepieces();
                if ((currentArray != null) && (currentArray.length > 0)) {
                    o[i++] = currentArray;
                    resultSize = resultSize + currentArray.length;
                }
            }

            result = new IEyepiece[resultSize];
            if (resultSize == 0) {
                return result;
            }

            int j = 0;
            for (IEyepiece[] iEyepieces : o) {
                currentArray = iEyepieces;
                for (IEyepiece iEyepiece : currentArray) {
                    result[j++] = iEyepiece;
                }
            }
        }

        Arrays.sort(result, new EyepieceComparator());

        return result;

    }

    public IImager[] getImagers() {

        IImager[] result = null;
        if (this.cache.size() == 1) { // Only one file open
            result = this.cache.get(0).getImagers();
        } else {
            ListIterator<CacheEntry> iterator = this.cache.listIterator();
            CacheEntry current = null;
            IImager[] currentArray = null;
            IImager[][] o = new IImager[this.cache.size()][];
            int i = 0;
            int resultSize = 0;
            while (iterator.hasNext()) {
                current = iterator.next();
                currentArray = current.getImagers();
                if ((currentArray != null) && (currentArray.length > 0)) {
                    o[i++] = currentArray;
                    resultSize = resultSize + currentArray.length;
                }
            }

            result = new IImager[resultSize];
            if (resultSize == 0) {
                return result;
            }

            int j = 0;
            for (IImager[] iImagers : o) {
                currentArray = iImagers;
                for (IImager iImager : currentArray) {
                    result[j++] = iImager;
                }
            }
        }

        Arrays.sort(result, new ImagerComparator());

        return result;

    }

    public IFilter[] getFilters() {

        IFilter[] result = null;
        if (this.cache.size() == 1) { // Only one file open
            result = this.cache.get(0).getFilters();
        } else {
            ListIterator<CacheEntry> iterator = this.cache.listIterator();
            CacheEntry current = null;
            IFilter[] currentArray = null;
            IFilter[][] o = new IFilter[this.cache.size()][];
            int i = 0;
            int resultSize = 0;
            while (iterator.hasNext()) {
                current = iterator.next();
                currentArray = current.getFilters();
                if ((currentArray != null) && (currentArray.length > 0)) {
                    o[i++] = currentArray;
                    resultSize = resultSize + currentArray.length;
                }
            }

            result = new IFilter[resultSize];
            if (resultSize == 0) {
                return result;
            }

            int j = 0;
            for (IFilter[] iFilters : o) {
                currentArray = iFilters;
                for (IFilter iFilter : currentArray) {
                    result[j++] = iFilter;
                }
            }
        }

        Arrays.sort(result, new FilterComparator());

        return result;

    }

    public IObservation[] getObservations() {

        IObservation[] result = null;
        if (this.cache.size() == 1) { // Only one file open
            result = this.cache.get(0).getObservations();
        } else { // Several files open
            ListIterator<CacheEntry> iterator = this.cache.listIterator();
            CacheEntry current = null;
            IObservation[] currentArray = null;
            IObservation[][] o = new IObservation[this.cache.size()][];
            int i = 0;
            int resultSize = 0;
            while (iterator.hasNext()) {
                current = iterator.next();
                currentArray = current.getObservations();
                if ((currentArray != null) && (currentArray.length > 0)) {
                    o[i++] = currentArray;
                    resultSize = resultSize + currentArray.length;
                }
            }

            result = new IObservation[resultSize];
            if (resultSize == 0) {
                return result;
            }

            int j = 0;
            for (IObservation[] iObservations : o) {
                currentArray = iObservations;
                for (IObservation iObservation : currentArray) {
                    result[j++] = iObservation;
                }
            }
        }

        Arrays.sort(result, new ObservationComparator());

        return result;

    }

    public IObservation[] getObservations(ISchemaElement element) {

        if (element == null) { // No element given
            return null;
        } else if (element instanceof IObservation) { // Return all observations
            return this.getObservations();
        }

        IObservation[] result = null;
        if (this.cache.size() == 1) { // Only one file open
            List<ISchemaElement> list = this.cache.get(0).getReferedElements(element);
            if (list == null) {
                return null;
            }
            result = (IObservation[]) (list.toArray(new IObservation[] {}));
        } else { // Several files open
            ListIterator<CacheEntry> iterator = this.cache.listIterator();
            CacheEntry current = null;
            IObservation[] currentArray = null;
            IObservation[][] o = new IObservation[this.cache.size()][];
            int i = 0;
            int resultSize = 0;
            while (iterator.hasNext()) {
                current = iterator.next();
                List<ISchemaElement> list = current.getReferedElements(element);
                if (list == null) {
                    return null;
                }
                currentArray = (IObservation[]) list.toArray(new IObservation[] {});
                if (currentArray.length > 0) {
                    o[i++] = currentArray;
                    resultSize = resultSize + currentArray.length;
                }
            }

            result = new IObservation[resultSize];
            if (resultSize == 0) {
                return result;
            }

            int j = 0;
            for (IObservation[] iObservations : o) {
                currentArray = iObservations;
                for (IObservation iObservation : currentArray) {
                    result[j++] = iObservation;
                }
            }
        }

        // Sort observations
        Arrays.sort(result, new ObservationComparator());

        return result;

    }

    public IObservation[] getCoObserverObservations(IObserver observer) {

        if (observer == null) { // No element given
            return null;
        }

        IObservation[] result = null;
        if (this.cache.size() == 1) { // Only one file open
            List<ISchemaElement> list = this.cache.get(0).getReferencedObservationsForCoObserver(observer);
            if (list == null) {
                return null;
            }
            result = (IObservation[]) (list.toArray(new IObservation[] {}));
        } else { // Several files open
            ListIterator<CacheEntry> iterator = this.cache.listIterator();
            CacheEntry current = null;
            IObservation[] currentArray = null;
            IObservation[][] o = new IObservation[this.cache.size()][];
            int i = 0;
            int resultSize = 0;
            while (iterator.hasNext()) {
                current = iterator.next();
                List<ISchemaElement> list = current.getReferencedObservationsForCoObserver(observer);
                if (list == null) {
                    return null;
                }
                currentArray = (IObservation[]) (list.toArray(new IObservation[] {}));
                if (currentArray.length > 0) {
                    o[i++] = currentArray;
                    resultSize = resultSize + currentArray.length;
                }
            }

            result = new IObservation[resultSize];
            if (resultSize == 0) {
                return result;
            }

            int j = 0;
            for (IObservation[] iObservations : o) {
                currentArray = iObservations;
                for (IObservation iObservation : currentArray) {
                    result[j++] = iObservation;
                }
            }
        }

        // Sort observations
        Arrays.sort(result, new ObservationComparator());

        return result;

    }

    public IScope[] getScopes() {

        IScope[] result = null;
        if (this.cache.size() == 1) { // Only one file open
            result = this.cache.get(0).getScopes();
        } else {
            ListIterator<CacheEntry> iterator = this.cache.listIterator();
            CacheEntry current = null;
            IScope[] currentArray = null;
            IScope[][] o = new IScope[this.cache.size()][];
            int i = 0;
            int resultSize = 0;
            while (iterator.hasNext()) {
                current = iterator.next();
                currentArray = current.getScopes();
                if ((currentArray != null) && (currentArray.length > 0)) {
                    o[i++] = currentArray;
                    resultSize = resultSize + currentArray.length;
                }
            }

            result = new IScope[resultSize];
            if (resultSize == 0) {
                return result;
            }

            int j = 0;
            for (IScope[] iScopes : o) {
                currentArray = iScopes;
                for (IScope iScope : currentArray) {
                    result[j++] = iScope;
                }
            }
        }

        Arrays.sort(result, new ScopeComparator());

        return result;

    }

    public ISession[] getSessions() {

        ISession[] result = null;
        if (this.cache.size() == 1) { // Only one file open
            result = this.cache.get(0).getSessions();
        } else {
            ListIterator<CacheEntry> iterator = this.cache.listIterator();
            CacheEntry current = null;
            ISession[] currentArray = null;
            ISession[][] o = new ISession[this.cache.size()][];
            int i = 0;
            int resultSize = 0;
            while (iterator.hasNext()) {
                current = iterator.next();
                currentArray = current.getSessions();
                if ((currentArray != null) && (currentArray.length > 0)) {
                    o[i++] = currentArray;
                    resultSize = resultSize + currentArray.length;
                }
            }

            result = new ISession[resultSize];
            if (resultSize == 0) {
                return result;
            }

            int j = 0;
            for (ISession[] iSessions : o) {
                currentArray = iSessions;
                for (ISession iSession : currentArray) {
                    result[j++] = iSession;
                }
            }
        }

        Arrays.sort(result, new SessionComparator());

        return result;

    }

    public ISite[] getSites() {

        ISite[] result = null;
        if (this.cache.size() == 1) { // Only one file open
            result = this.cache.get(0).getSites();
        } else {
            ListIterator<CacheEntry> iterator = this.cache.listIterator();
            CacheEntry current = null;
            ISite[] currentArray = null;
            ISite[][] o = new ISite[this.cache.size()][];
            int i = 0;
            int resultSize = 0;
            while (iterator.hasNext()) {
                current = iterator.next();
                currentArray = current.getSites();
                if ((currentArray != null) && (currentArray.length > 0)) {
                    o[i++] = currentArray;
                    resultSize = resultSize + currentArray.length;
                }
            }

            result = new ISite[resultSize];
            if (resultSize == 0) {
                return result;
            }

            int j = 0;
            for (ISite[] iSites : o) {
                currentArray = iSites;
                for (ISite iSite : currentArray) {
                    result[j++] = iSite;
                }
            }
        }

        Arrays.sort(result, new SiteComparator());

        return result;

    }

    public ITarget[] getTargets() {

        ITarget[] result = null;
        if (this.cache.size() == 1) { // Only one file open
            result = this.cache.get(0).getTargets();
        } else {
            ListIterator<CacheEntry> iterator = this.cache.listIterator();
            CacheEntry current = null;
            ITarget[] currentArray = null;
            ITarget[][] o = new ITarget[this.cache.size()][];
            int i = 0;
            int resultSize = 0;
            while (iterator.hasNext()) {
                current = iterator.next();
                currentArray = current.getTargets();
                if ((currentArray != null) && (currentArray.length > 0)) {
                    o[i++] = currentArray;
                    resultSize = resultSize + currentArray.length;
                }
            }

            result = new ITarget[resultSize];
            if (resultSize == 0) {
                return result;
            }

            int j = 0;
            for (ITarget[] iTargets : o) {
                currentArray = iTargets;
                for (ITarget iTarget : currentArray) {
                    result[j++] = iTarget;
                }
            }
        }

        Arrays.sort(result, new TargetComparator());

        return result;

    }

    public ILens[] getLenses() {

        ILens[] result = null;
        if (this.cache.size() == 1) { // Only one file open
            result = this.cache.get(0).getLenses();
        } else {
            ListIterator<CacheEntry> iterator = this.cache.listIterator();
            CacheEntry current = null;
            ILens[] currentArray = null;
            ILens[][] o = new ILens[this.cache.size()][];
            int i = 0;
            int resultSize = 0;
            while (iterator.hasNext()) {
                current = iterator.next();
                currentArray = current.getLenses();
                if ((currentArray != null) && (currentArray.length > 0)) {
                    o[i++] = currentArray;
                    resultSize = resultSize + currentArray.length;
                }
            }

            result = new ILens[resultSize];
            if (resultSize == 0) {
                return result;
            }

            int j = 0;
            for (ILens[] lens : o) {
                currentArray = lens;
                for (ILens iLens : currentArray) {
                    result[j++] = iLens;
                }
            }
        }

        Arrays.sort(result, new LensComparator());

        return result;

    }

    public boolean loadObservations(String xmlPath) {

        try {
            loader.load(new File(xmlPath), this.schemaPath);

            IObservation[] obs = loader.getObservations();
            IEyepiece[] eye = loader.getEyepieces();
            IFilter[] fil = loader.getFilters();
            IImager[] imager = loader.getImagers();
            IObserver[] observers = loader.getObservers();
            IScope[] scopes = loader.getScopes();
            ISession[] sessions = loader.getSessions();
            ISite[] sites = loader.getSites();
            ITarget[] targets = loader.getTargets();
            ILens[] lenses = loader.getLenses();

            
            // Delete all old stuff
            this.cache.clear();

            this.cache.add(new CacheEntry(xmlPath, obs, eye, fil, imager, observers, scopes, sessions, sites, targets,
                    lenses));

        } catch (OALException oal) {
            System.err.print("Cannot load: " + xmlPath + "\nNested Exception is: " + oal.getMessage());
            return false;
        }

        return true;

    }

    private RootElement getRootElement() {

        RootElement root = new RootElement();

        // @todo This only works for ONE file opened...
        if (this.cache.isEmpty()) {
            // Nothing to save
            return null;
        }

        CacheEntry entry = (CacheEntry) this.cache.listIterator().next();

        try {
            IScope[] sa = entry.getScopes();
            if ((sa != null) && (sa.length > 0)) {
                root.addScopes(Arrays.asList(sa));
            }

            ISite[] sia = entry.getSites();
            if ((sia != null) && (sia.length > 0)) {
                root.addSites(Arrays.asList(sia));
            }

            IObserver[] oa = entry.getObservers();
            if ((oa != null) && (oa.length > 0)) {
                root.addObservers(Arrays.asList(oa));
            }

            IEyepiece[] ea = entry.getEyepieces();
            if ((ea != null) && (ea.length > 0)) {
                root.addEyepieces(Arrays.asList(ea));
            }

            IFilter[] fil = entry.getFilters();
            if ((fil != null) && (fil.length > 0)) {
                root.addFilters(Arrays.asList(fil));
            }

            IImager[] ia = entry.getImagers();
            if ((ia != null) && (ia.length > 0)) {
                root.addImagers(Arrays.asList(ia));
            }

            ITarget[] ta = entry.getTargets();
            if ((ta != null) && (ta.length > 0)) {
                root.addTargets(Arrays.asList(ta));
            }

            ISession[] sea = entry.getSessions();
            if ((sea != null) && (sea.length > 0)) {
                root.addSessions(Arrays.asList(sea));
            }

            IObservation[] obsera = entry.getObservations();
            if ((obsera != null) && (obsera.length > 0)) {
                root.addObservations(Arrays.asList(obsera));
            }

            ILens[] len = entry.getLenses();
            if ((len != null) && (len.length > 0)) {
                root.addLenses(Arrays.asList(len));
            }
        } catch (SchemaException se) {
            System.err.println("Unable to add elements\n" + se);
        }

        return root;

    }

    private void addObservationsAndDependentToRoot(List<IObservation> observations, RootElement root) {

        for (IObservation observation : observations) {
            this.addObservationAndDependentToRoot(observation, root);
        }

    }

    private void addObservationAndDependentToRoot(IObservation observation, RootElement root) {

        try {

            IScope scope = observation.getScope();
            if (scope != null) {
                root.addScope(scope);
            }

            ISite site = observation.getSite();
            if (site != null) {
                root.addSite(site);
            }

            IObserver observer = observation.getObserver();
            if (observer != null) {
                root.addObserver(observer);
            }

            IEyepiece eyepiece = observation.getEyepiece();
            if (eyepiece != null) {
                root.addEyepiece(eyepiece);
            }

            IFilter filter = observation.getFilter();
            if (filter != null) {
                root.addFilter(filter);
            }

            IImager imager = observation.getImager();
            if (imager != null) {
                root.addImager(imager);
            }

            ITarget target = observation.getTarget();
            if (target != null) {
                root.addTarget(target);
            }

            ISession session = observation.getSession();
            if (session != null) {
                root.addSession(session);
            }

            ILens lens = observation.getLens();
            if (lens != null) {
                root.addLens(lens);
            }

            root.addObservation(observation);

        } catch (SchemaException se) {
            System.err.println("Unable to add element\n" + se);
        }

    }

}

// One cache entry per file
class CacheEntry {

    private String xmlPath = null;
    private SchemaElementCacheEntry<ISchemaElement>[] observation = new SchemaElementCacheEntry[0];
    private SchemaElementCacheEntry<ISchemaElement>[] eyepiece = new SchemaElementCacheEntry[0];
    private SchemaElementCacheEntry<ISchemaElement>[] imager = new SchemaElementCacheEntry[0];
    private SchemaElementCacheEntry<ISchemaElement>[] filter = new SchemaElementCacheEntry[0];
    private SchemaElementCacheEntry<ISchemaElement>[] coObserver = new SchemaElementCacheEntry[0];
    private SchemaElementCacheEntry<ISchemaElement>[] observer = new SchemaElementCacheEntry[0];
    private SchemaElementCacheEntry<ISchemaElement>[] scope = new SchemaElementCacheEntry[0];
    private SchemaElementCacheEntry<ISchemaElement>[] session = new SchemaElementCacheEntry[0];
    private SchemaElementCacheEntry<ISchemaElement>[] site = new SchemaElementCacheEntry[0];
    private SchemaElementCacheEntry<ISchemaElement>[] target = new SchemaElementCacheEntry[0];
    private SchemaElementCacheEntry<ISchemaElement>[] lens = new SchemaElementCacheEntry[0];
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
    private SchemaElementCacheEntry[] targetContaining = new SchemaElementCacheEntry[0];

    public CacheEntry() {

    }

    public CacheEntry(String xmlPath, IObservation[] observation, IEyepiece[] eyepiece, IFilter[] filter,
            IImager[] imager, IObserver[] observer, IScope[] scope, ISession[] session, ISite[] site, ITarget[] target,
            ILens[] lens) {

        this.xmlPath = xmlPath;
        this.observation = new SchemaElementCacheEntry[observation.length];
        for (int i = 0; i < observation.length; i++) {
            this.observation[i] = new SchemaElementCacheEntry(observation[i]);
        }

        this.eyepiece = new SchemaElementCacheEntry[eyepiece.length];
        for (int i = 0; i < eyepiece.length; i++) {
            this.eyepiece[i] = new SchemaElementCacheEntry(eyepiece[i]);
        }

        this.filter = new SchemaElementCacheEntry[filter.length];
        for (int i = 0; i < filter.length; i++) {
            this.filter[i] = new SchemaElementCacheEntry(filter[i]);
        }

        this.imager = new SchemaElementCacheEntry[imager.length];
        for (int i = 0; i < imager.length; i++) {
            this.imager[i] = new SchemaElementCacheEntry(imager[i]);
        }

        this.observer = new SchemaElementCacheEntry[observer.length];
        this.coObserver = new SchemaElementCacheEntry[observer.length];
        for (int i = 0; i < observer.length; i++) {
            this.observer[i] = new SchemaElementCacheEntry(observer[i]);
            this.coObserver[i] = new SchemaElementCacheEntry(observer[i]);
        }

        this.scope = new SchemaElementCacheEntry[scope.length];
        for (int i = 0; i < scope.length; i++) {
            this.scope[i] = new SchemaElementCacheEntry(scope[i]);
        }

        this.session = new SchemaElementCacheEntry[session.length];
        for (int i = 0; i < session.length; i++) {
            this.session[i] = new SchemaElementCacheEntry(session[i]);
        }

        this.site = new SchemaElementCacheEntry[site.length];
        for (int i = 0; i < site.length; i++) {
            this.site[i] = new SchemaElementCacheEntry(site[i]);
        }

        this.target = new SchemaElementCacheEntry[target.length];
        List<SchemaElementCacheEntry> helper = new ArrayList<>();
        for (int i = 0; i < target.length; i++) {
            this.target[i] = new SchemaElementCacheEntry(target[i]);
            if (target[i] instanceof ITargetContaining) { // Fill targetContaining Array (via ArrayList)
                // We add the SchemaElementCacheEntry to the Array
                // with ITargetContaining as schemaElement and the component targets as
                // refered elements
                SchemaElementCacheEntry entry = new SchemaElementCacheEntry(target[i]);
                List<ISchemaElement> data = new ArrayList<>();
                data.addAll(((ITargetContaining) target[i]).getComponentTargets(target));
                entry.addReferencedElements(data);
                helper.add(entry);
            }
        }
        this.targetContaining = (SchemaElementCacheEntry[]) helper.toArray(new SchemaElementCacheEntry[] {});

        this.lens = new SchemaElementCacheEntry[lens.length];
        for (int i = 0; i < lens.length; i++) {
            this.lens[i] = new SchemaElementCacheEntry(lens[i]);
        }

        // Assign observations to SchemaElementCacheEntries
        // and vice versa (schemaElements to observation SchemaElementCacheEntry)
        for (SchemaElementCacheEntry iObservation : this.observation) {
            addAllObservationElements(iObservation);
        }

    }

    public String getXmlPath() {

        return this.xmlPath;

    }

    public List<ISchemaElement> getReferedElements(ISchemaElement element) {

        if (element instanceof IObservation) {
            for (SchemaElementCacheEntry schemaElementCacheEntry : this.observation) {
                if (schemaElementCacheEntry.getSchemaElement().equals(element)) {
                    return schemaElementCacheEntry.getReferencedElements();
                }
            }
        } else if (element instanceof IEyepiece) {
            for (SchemaElementCacheEntry schemaElementCacheEntry : this.eyepiece) {
                if (schemaElementCacheEntry.getSchemaElement().equals(element)) {
                    return schemaElementCacheEntry.getReferencedElements();
                }
            }
        } else if (element instanceof IImager) {
            for (SchemaElementCacheEntry schemaElementCacheEntry : this.imager) {
                if (schemaElementCacheEntry.getSchemaElement().equals(element)) {
                    return schemaElementCacheEntry.getReferencedElements();
                }
            }
        } else if (element instanceof IFilter) {
            for (SchemaElementCacheEntry schemaElementCacheEntry : this.filter) {
                if (schemaElementCacheEntry.getSchemaElement().equals(element)) {
                    return schemaElementCacheEntry.getReferencedElements();
                }
            }
        } else if (element instanceof IObserver) {
            for (SchemaElementCacheEntry schemaElementCacheEntry : this.observer) {
                if (schemaElementCacheEntry.getSchemaElement().equals(element)) {
                    return schemaElementCacheEntry.getReferencedElements();
                }
            }
        } else if (element instanceof IScope) {
            for (SchemaElementCacheEntry schemaElementCacheEntry : this.scope) {
                if (schemaElementCacheEntry.getSchemaElement().equals(element)) {
                    return schemaElementCacheEntry.getReferencedElements();
                }
            }
        } else if (element instanceof ISession) {
            for (SchemaElementCacheEntry schemaElementCacheEntry : this.session) {
                if (schemaElementCacheEntry.getSchemaElement().equals(element)) {
                    return schemaElementCacheEntry.getReferencedElements();
                }
            }
        } else if (element instanceof ISite) {
            for (SchemaElementCacheEntry schemaElementCacheEntry : this.site) {
                if (schemaElementCacheEntry.getSchemaElement().equals(element)) {
                    return schemaElementCacheEntry.getReferencedElements();
                }
            }
        } else if (element instanceof ITarget) {
            for (SchemaElementCacheEntry schemaElementCacheEntry : this.target) {
                if (schemaElementCacheEntry.getSchemaElement().equals(element)) {
                    return schemaElementCacheEntry.getReferencedElements();
                }
            }
        } else if (element instanceof ILens) {
            for (SchemaElementCacheEntry len : this.lens) {
                if (len.getSchemaElement().equals(element)) {
                    return len.getReferencedElements();
                }
            }
        }

        return null;

    }

    public List<ISchemaElement> getReferencedObservationsForCoObserver(IObserver coObserver) {

        for (SchemaElementCacheEntry schemaElementCacheEntry : this.coObserver) {
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

        IObservation[] result = new IObservation[this.observation.length];
        for (int i = 0; i < this.observation.length; i++) {
            result[i] = (IObservation) this.observation[i].getSchemaElement();
        }

        return result;

    }

    public IEyepiece[] getEyepieces() {

        IEyepiece[] result = new IEyepiece[this.eyepiece.length];
        for (int i = 0; i < this.eyepiece.length; i++) {
            result[i] = (IEyepiece) this.eyepiece[i].getSchemaElement();
        }

        return result;

    }

    public IImager[] getImagers() {

        IImager[] result = new IImager[this.imager.length];
        for (int i = 0; i < this.imager.length; i++) {
            result[i] = (IImager) this.imager[i].getSchemaElement();
        }

        return result;

    }

    public IFilter[] getFilters() {

        IFilter[] result = new IFilter[this.filter.length];
        for (int i = 0; i < this.filter.length; i++) {
            result[i] = (IFilter) this.filter[i].getSchemaElement();
        }

        return result;
    }

    public IObserver[] getObservers() {

        IObserver[] result = new IObserver[this.observer.length];
        for (int i = 0; i < this.observer.length; i++) {
            result[i] = (IObserver) this.observer[i].getSchemaElement();
        }

        return result;

    }

    public IObserver[] getCoObservers() {

        IObserver[] result = new IObserver[this.observer.length];
        for (int i = 0; i < this.observer.length; i++) {
            result[i] = (IObserver) this.observer[i].getSchemaElement();
        }

        return result;

    }

    public IScope[] getScopes() {

        IScope[] result = new IScope[this.scope.length];
        for (int i = 0; i < this.scope.length; i++) {
            result[i] = (IScope) this.scope[i].getSchemaElement();
        }

        return result;

    }

    public ISession[] getSessions() {

        ISession[] result = new ISession[this.session.length];
        for (int i = 0; i < this.session.length; i++) {
            result[i] = (ISession) this.session[i].getSchemaElement();
        }

        return result;

    }

    public ISite[] getSites() {

        ISite[] result = new ISite[this.site.length];
        for (int i = 0; i < this.site.length; i++) {
            result[i] = (ISite) this.site[i].getSchemaElement();
        }

        return result;

    }

    public ITarget[] getTargets() {

        ITarget[] result = new ITarget[this.target.length];
        for (int i = 0; i < this.target.length; i++) {
            result[i] = (ITarget) this.target[i].getSchemaElement();
        }

        return result;

    }

    public ILens[] getLenses() {

        ILens[] result = new ILens[this.lens.length];
        for (int i = 0; i < this.lens.length; i++) {
            result[i] = (ILens) this.lens[i].getSchemaElement();
        }

        return result;

    }

    public void addEyepiece(IEyepiece eyepiece) {

        if (this.doublicateCheck(this.eyepiece, eyepiece)) {
            SchemaElementCacheEntry[] newArray = new SchemaElementCacheEntry[this.eyepiece.length + 1];
            System.arraycopy(this.eyepiece, 0, newArray, 0, this.eyepiece.length);
            newArray[newArray.length - 1] = new SchemaElementCacheEntry(eyepiece);
            this.eyepiece = newArray;
        }

    }

    public void addImager(IImager imager) {

        if (this.doublicateCheck(this.imager, imager)) {
            SchemaElementCacheEntry[] newArray = new SchemaElementCacheEntry[this.imager.length + 1];
            System.arraycopy(this.imager, 0, newArray, 0, this.imager.length);
            newArray[newArray.length - 1] = new SchemaElementCacheEntry(imager);
            this.imager = newArray;
        }

    }

    public void addFilter(IFilter filter) {

        if (this.doublicateCheck(this.filter, filter)) {
            SchemaElementCacheEntry[] newArray = new SchemaElementCacheEntry[this.filter.length + 1];
            System.arraycopy(this.filter, 0, newArray, 0, this.filter.length);
            newArray[newArray.length - 1] = new SchemaElementCacheEntry(filter);
            this.filter = newArray;
        }

    }

    public void addObservation(IObservation observation) {

        // Check for doublicates
        for (SchemaElementCacheEntry schemaElementCacheEntry : this.observation) {
            if (schemaElementCacheEntry.getSchemaElement().equals(observation)) {
                return; // Doublicate found
            }
        }

        // Create and add observation SchemaElementCacheEntry
        SchemaElementCacheEntry[] newArray = new SchemaElementCacheEntry[this.observation.length + 1];
        System.arraycopy(this.observation, 0, newArray, 0, this.observation.length);
        newArray[newArray.length - 1] = new SchemaElementCacheEntry(observation);
        this.observation = newArray;

        // Add observation to other schemaElements
        this.addAllObservationElements(newArray[newArray.length - 1]);

    }

    public void addObserver(IObserver observer) {

        if (this.doublicateCheck(this.observer, observer)) {
            SchemaElementCacheEntry[] newArray = new SchemaElementCacheEntry[this.observer.length + 1];
            System.arraycopy(this.observer, 0, newArray, 0, this.observer.length);
            newArray[newArray.length - 1] = new SchemaElementCacheEntry(observer);
            this.observer = newArray;

            // Also update coObserver array
            SchemaElementCacheEntry[] newCoObsArray = new SchemaElementCacheEntry[this.coObserver.length + 1];
            System.arraycopy(this.coObserver, 0, newCoObsArray, 0, this.coObserver.length);
            newCoObsArray[newCoObsArray.length - 1] = new SchemaElementCacheEntry(observer);
            this.coObserver = newCoObsArray;
        }

    }

    public void addScope(IScope scope) {

        if (this.doublicateCheck(this.scope, scope)) {
            SchemaElementCacheEntry[] newArray = new SchemaElementCacheEntry[this.scope.length + 1];
            System.arraycopy(this.scope, 0, newArray, 0, this.scope.length);
            newArray[newArray.length - 1] = new SchemaElementCacheEntry(scope);
            this.scope = newArray;
        }

    }

    public void addSession(ISession session) {

        if (this.doublicateCheck(this.session, session)) {
            SchemaElementCacheEntry[] newArray = new SchemaElementCacheEntry[this.session.length + 1];
            System.arraycopy(this.session, 0, newArray, 0, this.session.length);
            newArray[newArray.length - 1] = new SchemaElementCacheEntry(session);
            this.session = newArray;
        }

    }

    public void addSite(ISite site) {

        if (this.doublicateCheck(this.site, site)) {
            SchemaElementCacheEntry[] newArray = new SchemaElementCacheEntry[this.site.length + 1];
            System.arraycopy(this.site, 0, newArray, 0, this.site.length);
            newArray[newArray.length - 1] = new SchemaElementCacheEntry(site);
            this.site = newArray;
        }

    }

    public void addTarget(ITarget target) {

        if (this.doublicateCheck(this.target, target)) {
            SchemaElementCacheEntry[] newArray = new SchemaElementCacheEntry[this.target.length + 1];
            System.arraycopy(this.target, 0, newArray, 0, this.target.length);
            newArray[newArray.length - 1] = new SchemaElementCacheEntry(target);
            this.target = newArray;

            // Add target to targetContaining
            if (target instanceof ITargetContaining) {
                SchemaElementCacheEntry[] newContainingArray = new SchemaElementCacheEntry[this.targetContaining.length
                        + 1];
                System.arraycopy(this.targetContaining, 0, newContainingArray, 0, this.targetContaining.length);
                newContainingArray[newContainingArray.length - 1] = new SchemaElementCacheEntry(target);
                List<ISchemaElement> data = new ArrayList<>();
                data.addAll(((ITargetContaining) target).getComponentTargets(this.getTargets()));
                newContainingArray[newContainingArray.length - 1].addReferencedElements(data);
                this.targetContaining = newContainingArray;
            }

        }

    }

    public void addLens(ILens lens) {

        if (this.doublicateCheck(this.lens, lens)) {
            SchemaElementCacheEntry[] newArray = new SchemaElementCacheEntry[this.lens.length + 1];
            System.arraycopy(this.lens, 0, newArray, 0, this.lens.length);
            newArray[newArray.length - 1] = new SchemaElementCacheEntry(lens);
            this.lens = newArray;
        }

    }

    public void updateObservation(IObservation observation) {

        // First get SchemaElementCacheEntry for the given observation
        SchemaElementCacheEntry cacheEntry = null;
        for (SchemaElementCacheEntry entry : this.observation) {
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
                for (SchemaElementCacheEntry schemaElementCacheEntry : this.eyepiece) {
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
                for (SchemaElementCacheEntry schemaElementCacheEntry : this.imager) {
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
                for (SchemaElementCacheEntry schemaElementCacheEntry : this.filter) {
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
                for (SchemaElementCacheEntry schemaElementCacheEntry : this.scope) {
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
                for (SchemaElementCacheEntry elementCacheEntry : this.session) {
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
                                for (SchemaElementCacheEntry schemaElementCacheEntry : this.coObserver) { // Iterate
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
                                for (SchemaElementCacheEntry schemaElementCacheEntry : this.coObserver) { // Iterate
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
                for (SchemaElementCacheEntry schemaElementCacheEntry : this.observer) {
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
                for (SchemaElementCacheEntry schemaElementCacheEntry : this.site) {
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
                for (SchemaElementCacheEntry elementCacheEntry : this.target) {
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
                                for (SchemaElementCacheEntry schemaElementCacheEntry : this.target) {
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
                                for (SchemaElementCacheEntry schemaElementCacheEntry : this.target) {
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
                for (SchemaElementCacheEntry len : this.lens) {
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
            for (SchemaElementCacheEntry schemaElementCacheEntry : this.eyepiece) {
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
            for (SchemaElementCacheEntry schemaElementCacheEntry : this.imager) {
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
            for (SchemaElementCacheEntry schemaElementCacheEntry : this.filter) {
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
            for (SchemaElementCacheEntry schemaElementCacheEntry : this.observer) {
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
            for (SchemaElementCacheEntry schemaElementCacheEntry : this.scope) {
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
            for (SchemaElementCacheEntry elementCacheEntry : this.session) {
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
                            for (SchemaElementCacheEntry schemaElementCacheEntry : this.coObserver) { // Iterate over
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
            for (SchemaElementCacheEntry schemaElementCacheEntry : this.site) {
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
            for (SchemaElementCacheEntry schemaElementCacheEntry : this.target) {
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
            for (SchemaElementCacheEntry len : this.lens) {
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
        for (SchemaElementCacheEntry schemaElementCacheEntry : this.coObserver) { // Iterator over all coObservers

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

    }

    public void updateTarget(ITarget target) {

        // Check if component list has changed
        List<ITarget> newComponents = ((ITargetContaining) target).getComponentTargets(this.getTargets());
        List<ISchemaElement> oldComponents = new ArrayList<>();
        for (SchemaElementCacheEntry elementCacheEntry : this.targetContaining) {
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
                data.addAll(((ITargetContaining) target).getComponentTargets(this.getTargets()));
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
            for (SchemaElementCacheEntry schemaElementCacheEntry : this.target) {
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
            for (SchemaElementCacheEntry schemaElementCacheEntry : this.target) {
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

        List<ISchemaElement> dependencyList = new ArrayList<>();

        SchemaElementCacheEntry[] newArray = new SchemaElementCacheEntry[this.eyepiece.length - 1];
        boolean foundDependency = false;
        boolean foundElement = false;
        for (int i = 0; i < this.eyepiece.length; i++) {
            if (this.eyepiece[i].getSchemaElement().equals(eyepiece)) {
                foundElement = true;
                // Check dependencies
                IEyepiece e = null;
                for (SchemaElementCacheEntry schemaElementCacheEntry : this.observation) {
                    e = ((IObservation) schemaElementCacheEntry.getSchemaElement()).getEyepiece();
                    if ((e != null) && (e.equals(eyepiece))) {
                        foundDependency = true;
                        dependencyList.add(schemaElementCacheEntry.getSchemaElement());
                    }
                }
            } else {
                if ((foundElement) && (!foundDependency)) {
                    newArray[i - 1] = this.eyepiece[i];
                } else {
                    if (i < newArray.length) { // Avoid ArrayIndexOutOfBoundsException if current element is the last
                                               // and still
                                               // not found
                        newArray[i] = this.eyepiece[i];
                    }
                }
            }
        }
        if ((foundElement) && (!foundDependency)) { // Object has no dependencies
            this.eyepiece = newArray;
        }

        return dependencyList;

    }

    public List<ISchemaElement> removeImager(IImager imager) {

        List<ISchemaElement> dependencyList = new ArrayList<>();

        SchemaElementCacheEntry[] newArray = new SchemaElementCacheEntry[this.imager.length - 1];
        boolean foundDependency = false;
        boolean foundElement = false;
        for (int i = 0; i < this.imager.length; i++) {
            if (this.imager[i].getSchemaElement().equals(imager)) {
                foundElement = true;
                // Check dependencies
                IImager e = null;
                for (SchemaElementCacheEntry schemaElementCacheEntry : this.observation) {
                    e = ((IObservation) schemaElementCacheEntry.getSchemaElement()).getImager();
                    if ((e != null) && (e.equals(imager))) {
                        foundDependency = true;
                        dependencyList.add(schemaElementCacheEntry.getSchemaElement());
                    }
                }
            } else {
                if ((foundElement) && (!foundDependency)) {
                    newArray[i - 1] = this.imager[i];
                } else {
                    if (i < newArray.length) { // Avoid ArrayIndexOutOfBoundsException if current element is the last
                                               // and still
                                               // not found
                        newArray[i] = this.imager[i];
                    }
                }
            }
        }
        if ((foundElement) && (!foundDependency)) { // Object has no dependencies
            this.imager = newArray;
        }

        return dependencyList;

    }

    public List<ISchemaElement> removeFilter(IFilter filter) {

        List<ISchemaElement> dependencyList = new ArrayList<>();

        SchemaElementCacheEntry[] newArray = new SchemaElementCacheEntry[this.filter.length - 1];
        boolean foundDependency = false;
        boolean foundElement = false;
        for (int i = 0; i < this.filter.length; i++) {
            if (this.filter[i].getSchemaElement().equals(filter)) {
                foundElement = true;
                // Check dependencies
                IFilter e = null;
                for (SchemaElementCacheEntry schemaElementCacheEntry : this.observation) {
                    e = ((IObservation) schemaElementCacheEntry.getSchemaElement()).getFilter();
                    if ((e != null) && (e.equals(filter))) {
                        foundDependency = true;
                        dependencyList.add(schemaElementCacheEntry.getSchemaElement());
                    }
                }
            } else {
                if ((foundElement) && (!foundDependency)) {
                    newArray[i - 1] = this.filter[i];
                } else {
                    if (i < newArray.length) { // Avoid ArrayIndexOutOfBoundsException if current element is the last
                                               // and still
                                               // not found
                        newArray[i] = this.filter[i];
                    }
                }
            }
        }
        if ((foundElement) && (!foundDependency)) { // Object has no dependencies
            this.filter = newArray;
        }

        return dependencyList;

    }

    public List<ISchemaElement> removeObservation(IObservation observation) {

        SchemaElementCacheEntry[] newArray = new SchemaElementCacheEntry[this.observation.length - 1];
        boolean foundElement = false;
        for (int i = 0; i < this.observation.length; i++) {
            if (this.observation[i].getSchemaElement().equals(observation)) {
                foundElement = true;
            } else {
                if (foundElement) {
                    newArray[i - 1] = this.observation[i];
                } else {
                    if (i < newArray.length) { // Avoid ArrayIndexOutOfBoundsException if current element is the last
                                               // and still
                                               // not found
                        newArray[i] = this.observation[i];
                    }
                }
            }
        }
        if (foundElement) { // Observation was found
            this.observation = newArray;
        } else { // Observation was not found, stop here and return empty list
            return new ArrayList<ISchemaElement>();
        }

        // Remove observation from in all SchemaElement arrays

        // --------------------------------------------------------------------
        IEyepiece e = observation.getEyepiece();
        if (e != null) {
            for (SchemaElementCacheEntry schemaElementCacheEntry : this.eyepiece) {
                if (e.equals(schemaElementCacheEntry.getSchemaElement())) {
                    schemaElementCacheEntry.removeReferencedElement(observation);
                    break;
                }
            }
        }

        // --------------------------------------------------------------------
        IImager imager = observation.getImager();
        if (imager != null) {
            for (SchemaElementCacheEntry schemaElementCacheEntry : this.imager) {
                if (imager.equals(schemaElementCacheEntry.getSchemaElement())) {
                    schemaElementCacheEntry.removeReferencedElement(observation);
                    break;
                }
            }
        }

        // --------------------------------------------------------------------
        IFilter filter = observation.getFilter();
        if (filter != null) {
            for (SchemaElementCacheEntry schemaElementCacheEntry : this.filter) {
                if (filter.equals(schemaElementCacheEntry.getSchemaElement())) {
                    schemaElementCacheEntry.removeReferencedElement(observation);
                    break;
                }
            }
        }

        // --------------------------------------------------------------------
        IObserver observer = observation.getObserver();
        if (observer != null) {
            for (SchemaElementCacheEntry schemaElementCacheEntry : this.observer) {
                if (observer.equals(schemaElementCacheEntry.getSchemaElement())) {
                    schemaElementCacheEntry.removeReferencedElement(observation);
                    break;
                }
            }
        }

        // --------------------------------------------------------------------
        IScope scope = observation.getScope();
        if (scope != null) {
            for (SchemaElementCacheEntry schemaElementCacheEntry : this.scope) {
                if (scope.equals(schemaElementCacheEntry.getSchemaElement())) {
                    schemaElementCacheEntry.removeReferencedElement(observation);
                    break;
                }
            }
        }

        // --------------------------------------------------------------------
        ISession session = observation.getSession();
        if (session != null) {
            for (int i = 0; i < this.session.length; i++) {
                // Remove coObservers (if available)
                if ((session.getCoObservers() != null) && !(session.getCoObservers().isEmpty())) {
                    List<IObserver> coObservers = session.getCoObservers();
                    ListIterator<IObserver> iterator = coObservers.listIterator();
                    IObserver current = null;
                    while (iterator.hasNext()) { // Iterate over all coObservers
                        current = iterator.next();
                        for (int x = 0; i < this.coObserver.length; x++) { // Iterate over all coObservers
                            if (this.coObserver[x].getSchemaElement().equals(current)) {
                                // Remove Observation from coObservers refered elements
                                this.coObserver[x].removeReferencedElement(observation);

                                break; // Break for loop
                            }
                        }
                    }
                }

                if (session.equals(this.session[i].getSchemaElement())) {
                    this.session[i].removeReferencedElement(observation);
                    break;
                }
            }
        }

        // --------------------------------------------------------------------
        ISite site = observation.getSite();
        if (site != null) {
            for (SchemaElementCacheEntry schemaElementCacheEntry : this.site) {
                if (site.equals(schemaElementCacheEntry.getSchemaElement())) {
                    schemaElementCacheEntry.removeReferencedElement(observation);
                    break;
                }
            }
        }

        // --------------------------------------------------------------------
        ITarget target = observation.getTarget();
        if (target != null) {
            for (SchemaElementCacheEntry elementCacheEntry : this.target) {
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
                            for (SchemaElementCacheEntry schemaElementCacheEntry : this.target) {
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
            for (SchemaElementCacheEntry len : this.lens) {
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

        List<ISchemaElement> dependencyList = new ArrayList<>();

        SchemaElementCacheEntry[] newArray = new SchemaElementCacheEntry[this.observer.length - 1];
        SchemaElementCacheEntry[] newCoObserverArray = new SchemaElementCacheEntry[this.coObserver.length - 1];
        boolean foundDependency = false;
        boolean foundElement = false;
        for (int i = 0; i < this.observer.length; i++) {
            if (this.observer[i].getSchemaElement().equals(observer)) {
                foundElement = true;
                // Check dependencies in Observation elements
                IObserver e = null;
                for (SchemaElementCacheEntry elementCacheEntry : this.observation) {
                    e = ((IObservation) elementCacheEntry.getSchemaElement()).getObserver();
                    if ((e != null) && (e.equals(observer))) {
                        foundDependency = true;
                        dependencyList.add(elementCacheEntry.getSchemaElement());
                    }
                }
                // Check dependencies in session elements
                List<IObserver> coObs = null;
                for (SchemaElementCacheEntry schemaElementCacheEntry : this.session) {
                    coObs = ((ISession) schemaElementCacheEntry.getSchemaElement()).getCoObservers();
                    if ((coObs != null) && !(coObs.isEmpty())) {
                        for (IObserver coOb : coObs) {
                            if (observer.equals(coOb)) {
                                foundDependency = true;
                                dependencyList.add(schemaElementCacheEntry.getSchemaElement());
                                break;
                            }
                        }
                    }
                }
            } else {
                if ((foundElement) && (!foundDependency)) {
                    newArray[i - 1] = this.observer[i];
                    newCoObserverArray[i - 1] = this.coObserver[i];
                } else {
                    if (i < newArray.length) { // Avoid ArrayIndexOutOfBoundsException if current element is the last
                                               // and still
                                               // not found
                        newArray[i] = this.observer[i];
                        newCoObserverArray[i] = this.coObserver[i];
                    }
                }
            }
        }
        if ((foundElement) && (!foundDependency)) { // Object has no dependencies
            this.observer = newArray;
            this.coObserver = newCoObserverArray;
        }

        return dependencyList;

    }

    public List<ISchemaElement> removeScope(IScope scope) {

        List<ISchemaElement> dependencyList = new ArrayList<>();

        SchemaElementCacheEntry[] newArray = new SchemaElementCacheEntry[this.scope.length - 1];
        boolean foundDependency = false;
        boolean foundElement = false;
        for (int i = 0; i < this.scope.length; i++) {
            if (this.scope[i].getSchemaElement().equals(scope)) {
                foundElement = true;
                // Check dependencies
                IScope e = null;
                for (SchemaElementCacheEntry schemaElementCacheEntry : this.observation) {
                    e = ((IObservation) schemaElementCacheEntry.getSchemaElement()).getScope();
                    if ((e != null) && (e.equals(scope))) {
                        foundDependency = true;
                        dependencyList.add(schemaElementCacheEntry.getSchemaElement());
                    }
                }
            } else {
                if ((foundElement) && (!foundDependency)) {
                    newArray[i - 1] = this.scope[i];
                } else {
                    if (i < newArray.length) { // Avoid ArrayIndexOutOfBoundsException if current element is the last
                                               // and still
                                               // not found
                        newArray[i] = this.scope[i];
                    }
                }
            }
        }
        if ((foundElement) && (!foundDependency)) { // Object has no dependencies
            this.scope = newArray;
        }

        return dependencyList;

    }

    public List<ISchemaElement> removeSession(ISession session) {

        List<ISchemaElement> dependencyList = new ArrayList<>();

        SchemaElementCacheEntry[] newArray = new SchemaElementCacheEntry[this.session.length - 1];
        boolean foundDependency = false;
        boolean foundElement = false;
        for (int i = 0; i < this.session.length; i++) {
            if (this.session[i].getSchemaElement().equals(session)) {
                foundElement = true;
                // Check dependencies
                ISession e = null;
                for (SchemaElementCacheEntry schemaElementCacheEntry : this.observation) {
                    e = ((IObservation) schemaElementCacheEntry.getSchemaElement()).getSession();
                    if ((e != null) && (e.equals(session))) {
                        foundDependency = true;
                        dependencyList.add(schemaElementCacheEntry.getSchemaElement());
                    }
                }
            } else {
                if ((foundElement) && (!foundDependency)) {
                    newArray[i - 1] = this.session[i];
                } else {
                    if (i < newArray.length) { // Avoid ArrayIndexOutOfBoundsException if current element is the last
                                               // and still
                                               // not found
                        newArray[i] = this.session[i];
                    }
                }
            }
        }
        if ((foundElement) && (!foundDependency)) { // Object has no dependencies
            this.session = newArray;
        }

        return dependencyList;

    }

    public List<ISchemaElement> removeSite(ISite site) {

        List<ISchemaElement> dependencyList = new ArrayList<>();

        SchemaElementCacheEntry[] newArray = new SchemaElementCacheEntry[this.site.length - 1];
        boolean foundDependency = false;
        boolean foundElement = false;
        for (int i = 0; i < this.site.length; i++) {
            if (this.site[i].getSchemaElement().equals(site)) {
                foundElement = true;
                // Check dependencies in Observation elements
                ISite e = null;
                for (SchemaElementCacheEntry elementCacheEntry : this.observation) {
                    e = ((IObservation) elementCacheEntry.getSchemaElement()).getSite();
                    if ((e != null) && (e.equals(site))) {
                        foundDependency = true;
                        dependencyList.add(elementCacheEntry.getSchemaElement());
                    }
                }
                // Check dependencies in session elements
                for (SchemaElementCacheEntry schemaElementCacheEntry : this.session) {
                    e = ((ISession) schemaElementCacheEntry.getSchemaElement()).getSite();
                    if ((e != null) && (e.equals(site))) {
                        foundDependency = true;
                        dependencyList.add(schemaElementCacheEntry.getSchemaElement());
                    }
                }
            } else {
                if ((foundElement) && (!foundDependency)) {
                    newArray[i - 1] = this.site[i];
                } else {
                    if (i < newArray.length) { // Avoid ArrayIndexOutOfBoundsException if current element is the last
                                               // and still
                                               // not found
                        newArray[i] = this.site[i];
                    }
                }
            }
        }
        if ((foundElement) && (!foundDependency)) { // Object has no dependencies
            this.site = newArray;
        }

        return dependencyList;

    }

    public List<ISchemaElement> removeTarget(ITarget target) {

        List<ISchemaElement> dependencyList = new ArrayList<>();

        /*
         * IObserver creator = ((ITarget)element).getObserver(); // Delete only targets
         * created/edited by Observers if( creator == null ) { return null; // Return
         * null to indicate error }
         */// Allow deletion of catalog elements

        SchemaElementCacheEntry[] newArray = new SchemaElementCacheEntry[this.target.length - 1];
        boolean foundDependency = false;
        boolean foundElement = false;
        for (int i = 0; i < this.target.length; i++) {
            if (this.target[i].getSchemaElement().equals(target)) {
                foundElement = true;
                // Check dependencies
                ITarget e = null;
                for (SchemaElementCacheEntry schemaElementCacheEntry : this.observation) {
                    e = ((IObservation) schemaElementCacheEntry.getSchemaElement()).getTarget();
                    if ((e != null) && (e.equalsID(target))) {
                        foundDependency = true;
                        dependencyList.add(schemaElementCacheEntry.getSchemaElement());
                    }
                }
            } else {
                // The current target cache isn't the target that should be deleted, however
                // the current target cache contains a target that refers to other targets.
                // -> We need to check those refered Targets too...
                if (this.target[i].getSchemaElement() instanceof ITargetContaining) {
                    List<ITarget> containingTargets = ((ITargetContaining) this.target[i].getSchemaElement())
                            .getComponentTargets(this.getTargets());
                    ListIterator<ITarget> listIterator = containingTargets.listIterator();
                    ITarget ct = null;
                    while (listIterator.hasNext()) {
                        ct = listIterator.next();
                        if (ct.equals(target)) {
                            foundDependency = true;
                            dependencyList.add(this.target[i].getSchemaElement());
                        }
                    }
                }

                if ((foundElement) && (!foundDependency)) {
                    newArray[i - 1] = this.target[i];
                } else {
                    if (i < newArray.length) { // Avoid ArrayIndexOutOfBoundsException if current element is the last
                                               // and still
                                               // not found
                        newArray[i] = this.target[i];
                    }
                }
            }
        }

        if ((foundElement) && (!foundDependency)) { // Object has no dependencies
            // Actual deletion
            this.target = newArray;

            // Delete entry in targetContained array
            // As the ITargetContaining target must have no more observation references
            // while deletion,
            // it's components must have lost its referenced observations as well. (handled
            // in updateObservation or
            // deleteObservation)
            // So we only need to do some houseKeeping here and keep the cache Array clean
            List<SchemaElementCacheEntry> tcList = new ArrayList<>(Arrays.asList(this.targetContaining));
            for (SchemaElementCacheEntry schemaElementCacheEntry : this.targetContaining) {
                if (target.equals(schemaElementCacheEntry.getSchemaElement())) {
                    tcList.remove(schemaElementCacheEntry);
                    break;
                }
            }
            this.targetContaining = (SchemaElementCacheEntry[]) tcList.toArray(new SchemaElementCacheEntry[] {});

        }

        return dependencyList;

    }

    public List<ISchemaElement> removeLens(ILens lens) {

        List<ISchemaElement> dependencyList = new ArrayList<>();

        SchemaElementCacheEntry[] newArray = new SchemaElementCacheEntry[this.lens.length - 1];
        boolean foundDependency = false;
        boolean foundElement = false;
        for (int i = 0; i < this.lens.length; i++) {
            if (this.lens[i].getSchemaElement().equals(lens)) {
                foundElement = true;
                // Check dependencies
                ILens e = null;
                for (SchemaElementCacheEntry schemaElementCacheEntry : this.observation) {
                    e = ((IObservation) schemaElementCacheEntry.getSchemaElement()).getLens();
                    if ((e != null) && (e.equals(lens))) {
                        foundDependency = true;
                        dependencyList.add(schemaElementCacheEntry.getSchemaElement());
                    }
                }
            } else {
                if ((foundElement) && (!foundDependency)) {
                    newArray[i - 1] = this.lens[i];
                } else {
                    if (i < newArray.length) { // Avoid ArrayIndexOutOfBoundsException if current element is the last
                                               // and still
                                               // not found
                        newArray[i] = this.lens[i];
                    }
                }
            }
        }
        if ((foundElement) && (!foundDependency)) { // Object has no dependencies
            this.lens = newArray;
        }

        return dependencyList;

    }

    public void setXMLPath(String path) {

        this.xmlPath = path;

    }

    private boolean doublicateCheck(SchemaElementCacheEntry[] array, ISchemaElement e) {

        if ((array == null) || (e == null)) {
            return true;
        }

        for (SchemaElementCacheEntry schemaElementCacheEntry : array) {
            if (schemaElementCacheEntry.getSchemaElement().equals(e)) {
                return false;
            }
        }

        return true;

    }

    private void addAllObservationElements(SchemaElementCacheEntry observationCacheEntry) {

        IObservation observation = (IObservation) observationCacheEntry.getSchemaElement();
        if (observation == null) {
            return;
        }

        // --------------------------------------------------------------

        IEyepiece e = observation.getEyepiece();
        if (e != null) {
            for (SchemaElementCacheEntry schemaElementCacheEntry : this.eyepiece) {
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
            for (SchemaElementCacheEntry schemaElementCacheEntry : this.filter) {
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
            for (SchemaElementCacheEntry schemaElementCacheEntry : this.imager) {
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
            for (SchemaElementCacheEntry schemaElementCacheEntry : this.observer) {
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
            for (SchemaElementCacheEntry schemaElementCacheEntry : this.scope) {
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
            for (SchemaElementCacheEntry elementCacheEntry : this.session) {

                if (elementCacheEntry.getSchemaElement().equals(s)) {

                    // Add coObservers (if available)
                    if ((s.getCoObservers() != null) && !(s.getCoObservers().isEmpty())) {
                        List<IObserver> coObservers = s.getCoObservers();
                        ListIterator<IObserver> iterator = coObservers.listIterator();
                        IObserver current = null;
                        while (iterator.hasNext()) { // Iterate over all coObservers
                            current = iterator.next();
                            for (SchemaElementCacheEntry schemaElementCacheEntry : this.coObserver) { // Iterate over
                                                                                                      // all coObservers
                                if (schemaElementCacheEntry.getSchemaElement().equals(current)) {
                                    // Add Observation to coObservers refered elements
                                    schemaElementCacheEntry.addReferencedElement(observation);

                                    // Session might be referenced already from another observation
                                    // belonging to the same session
                                    if (!schemaElementCacheEntry.contains(observation.getSession())) {
                                        // Also include a reference to the session, as we need this in case
                                        // the session will get updated to know, which coObserver was
                                        // refering to that session in the past (for removing coObservers)
                                        schemaElementCacheEntry.addReferencedElement(observation.getSession());
                                    }

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
            for (SchemaElementCacheEntry schemaElementCacheEntry : this.site) {
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
            for (SchemaElementCacheEntry elementCacheEntry : this.target) {
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
                            for (SchemaElementCacheEntry schemaElementCacheEntry : this.target) {
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
            for (SchemaElementCacheEntry len : this.lens) {
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

// Stores an ISchemaElement and list of refering elements
// The refering elements will be the observations, beloning to the corresponding
// schemaElement
// Or in case the element is an IObservation, then the refering elements are all
// elements
// beloning to the IObservation (at that point in time)
// Storing the refered IObservation elements makes sense in the
// updateSchemaElement method, as
// the passed IObservation is already changed. Therefore we keep the last know
// references of an
// IObservation here. Think of it like a double linked list.
class SchemaElementCacheEntry<T extends ISchemaElement> {

    private T element = null;
    private final List<T> referenceList = new ArrayList<>();

    public SchemaElementCacheEntry(T element) {

        this.element = element;
    }

    public List<T> getReferencedElements() {

        /*
         * ISchemaElement[] result = (ISchemaElement[])this.referenceList.toArray(new
         * ISchemaElement[] {}); Arrays.sort(result, new ObservationComparator());
         * 
         * return result;
         */
        // this.referenceList.

        return this.referenceList;

    }

    public T getSchemaElement() {

        return this.element;

    }

    public void addReferencedElement(T se) {

        if (se == null) {
            return;
        }

        this.referenceList.add(se);

    }

    public void addReferencedElements(Collection<T> collection) {

        if (collection == null) {
            return;
        }

        this.referenceList.addAll(collection);

    }

    public void removeReferencedElement(T se) {

        this.referenceList.remove(se);

    }

    public void removeReferencedElements(Collection<T> collection) {

        this.referenceList.removeAll(collection);

    }

    public int getNumberOfReferences() {

        return this.referenceList.size();

    }

    public boolean contains(T se) {

        return this.referenceList.contains(se);

    }

    public void clearAllReferences() {

        this.referenceList.clear();

    }

}
