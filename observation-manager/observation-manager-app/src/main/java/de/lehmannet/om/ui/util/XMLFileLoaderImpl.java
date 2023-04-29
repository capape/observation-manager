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

        Backup backup = Backup.create(newPath);

        try {
            File xmlFile = new File(newPath);
            Objects.requireNonNull(root).serializeAsXmlFormatted(xmlFile);
            // this.loadObservations(newPath); // Fill cache .... Not good! Strange
            // behaviour. After save, first try to do
            // chnaged (e.g. stellar etc) is not taken. Second try works...) Better solution
            // below!
            ((CacheEntry) this.cache.iterator().next()).setXMLPath(newPath); // Works only with one XML!!!
            backup.delete();
        } catch (SchemaException se) {
            LOGGER.error("Unable to write file: {}. You have a previous backup of your data in {}", newPath, se,
                    backup.getPath());
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
                LOGGER.error("Unable to retrieve DOM Document");
            }
        } catch (SchemaException se) {
            LOGGER.error("Unable to retrieve DOM Document", se);
        }

        return null;

    }

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
            LOGGER.error("Unable to retrieve DOM Document for {}.", schemaElement, se);
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
            LOGGER.warn("Unknown element: {} ", element);
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
            LOGGER.warn("Unknown element: {}", element);
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
            LOGGER.error("Unknown element for deletion: {}", element);
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
