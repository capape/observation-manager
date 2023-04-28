/* ====================================================================
 * /RootElement.java
 *
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import de.lehmannet.om.util.SchemaException;

/**
 * The RootElement element is the root element of a schema element. All other schema elements are grouped below
 * RootElement.<br>
 * The object itself contains no astronomical data but provides XML namespaces, and schema element containers. A schema
 * element container groups multiple schema elements of one and the same type.<br>
 * E.g.<br>
 * <observers><br>
 * <observer><br>
 * <name>Foo</name><br>
 * <i>More observer stuff goes here</i><br>
 * </observer><br>
 * <observer><br>
 * <name>Foo</name><br>
 * <i>More observer stuff goes here</i><br>
 * </observer><br>
 * </observers><br>
 * In this example <observers> is the container element of multiple <observer> elements.<br>
 * Also the RootElement object contains the serializeToSchema() method, that will create a schema valid XML file.
 *
 * @author doergn@users.sourceforge.net
 *
 * @since 1.0
 */
public class RootElement {

    private static final Logger LOG = LoggerFactory.getLogger(RootElement.class);

    // ---------
    // Constants ---------------------------------------------------------
    // ---------

    // XML Namespace of schema Key
    private static final String XML_NS_KEY = "xmlns:oal";

    // XML Namespace of schema
    private static final String XML_NS = "http://groups.google.com/group/openastronomylog";

    // XML SI Schema Key
    private static final String XML_SI_KEY = "xmlns:xsi";

    // XML SI Schema
    private static final String XML_SI = "http://www.w3.org/2001/XMLSchema-instance";

    // XML Schema Location Key
    private static final String XML_SCHEMA_LOCATION_KEY = "xsi:schemaLocation";

    // XML Schema Location
    private static final String XML_SCHEMA_LOCATION = "http://groups.google.com/group/openastronomylog oal21.xsd";

    // XML Schema Version Key
    private static final String XML_SCHEMA_VERSION_KEY = "version";

    // XML Schema Location
    private static final String XML_SCHEMA_VERSION = "2.1";

    // Schema container for <observation> objects
    private static final String XML_OBSERVATION_CONTAINER = "oal:observations";

    // Schema container for <session> objects
    public static final String XML_SESSION_CONTAINER = "sessions";

    // Schema container for <target> objects
    public static final String XML_TARGET_CONTAINER = "targets";

    // Schema container for <observer> objects
    public static final String XML_OBSERVER_CONTAINER = "observers";

    // Schema container for <site> objects
    public static final String XML_SITE_CONTAINER = "sites";

    // Schema container for <scope> objects
    public static final String XML_SCOPE_CONTAINER = "scopes";

    // Schema container for <eyepiece> objects
    public static final String XML_EYEPIECE_CONTAINER = "eyepieces";

    // Schema container for <imager> objects
    public static final String XML_IMAGER_CONTAINER = "imagers";

    // Schema container for <filter> objects
    public static final String XML_FILTER_CONTAINER = "filters";

    // Schema container for <lens> objects
    public static final String XML_LENS_CONTAINER = "lenses";

    // ------------------
    // Instance Variables ------------------------------------------------
    // ------------------

    // All obervation objects belonging to this RootElement group
    private final List<IObservation> observationList = new ArrayList<>();

    // All observer objects belonging to this RootElement group
    private final List<IObserver> observerList = new ArrayList<>();

    // All site objects belonging to this RootElement group
    private final List<ISite> siteList = new ArrayList<>();

    // All scope objects belonging to this RootElement group
    private final List<IScope> scopeList = new ArrayList<>();

    // All eyepiece objects belonging to this RootElement group
    private final List<IEyepiece> eyepieceList = new ArrayList<>();

    // All imager objects belonging to this RootElement group
    private final List<IImager> imagerList = new ArrayList<>();

    // All session objects belonging to this RootElement group
    private final List<ISession> sessionList = new ArrayList<>();

    // All target objects belonging to this RootElement group
    private final List<ITarget> targetList = new ArrayList<>();

    // All filter objects belonging to this RootElement group
    private final List<IFilter> filterList = new ArrayList<>();

    // All lens objects belonging to this RootElement group
    private final List<ILens> lensList = new ArrayList<>();

    // --------------
    // Public methods ----------------------------------------------------
    // --------------

    public Collection<IObservation> getObservations() {

        return this.observationList;

    }

    public Collection<IEyepiece> getEyepieceList() {

        return this.eyepieceList;

    }

    public Collection<IImager> getImagerList() {

        return this.imagerList;

    }

    public Collection<IObserver> getObserverList() {

        return this.observerList;

    }

    public Collection<IScope> getScopeList() {

        return this.scopeList;

    }

    public Collection<ISession> getSessionList() {

        return this.sessionList;

    }

    public Collection<ISite> getSiteList() {

        return this.siteList;

    }

    public Collection<ITarget> getTargetList() {

        return this.targetList;

    }

    public Collection<IFilter> getFilterList() {

        return this.filterList;

    }

    public Collection<ILens> getLensList() {

        return this.lensList;

    }

    public void addObservation(IObservation observation) throws SchemaException {

        if (observation != null) {
            observationList.add(observation);
        } else {
            throw new SchemaException("Observation cannot be null. ");
        }

    }

    public void addObservations(Collection<IObservation> observations) throws SchemaException {

        if (observations != null) {
            observationList.addAll(observations);
        } else {
            throw new SchemaException("Observations cannot be null. ");
        }

    }

    public void addEyepiece(IEyepiece eyepiece) throws SchemaException {

        if (eyepiece != null) {
            this.eyepieceList.add(eyepiece);
        } else {
            throw new SchemaException("Eyepiece cannot be null. ");
        }

    }

    public void addEyepieces(Collection<IEyepiece> eyepieces) throws SchemaException {

        if (eyepieces != null) {
            this.eyepieceList.addAll(eyepieces);
        } else {
            throw new SchemaException("Eyepieces cannot be null. ");
        }

    }

    public void addImager(IImager imager) throws SchemaException {

        if (imager != null) {
            this.imagerList.add(imager);
        } else {
            throw new SchemaException("Imager cannot be null. ");
        }

    }

    public void addImagers(Collection<IImager> imagers) throws SchemaException {

        if (imagers != null) {
            this.imagerList.addAll(imagers);
        } else {
            throw new SchemaException("Imagers cannot be null. ");
        }

    }

    public void addSite(ISite site) throws SchemaException {

        if (site != null) {
            this.siteList.add(site);
        } else {
            throw new SchemaException("Site cannot be null. ");
        }

    }

    public void addSites(Collection<ISite> sites) throws SchemaException {

        if (sites != null) {
            this.siteList.addAll(sites);
        } else {
            throw new SchemaException("Sites cannot be null. ");
        }

    }

    public void addScope(IScope scope) throws SchemaException {

        if (scope != null) {
            this.scopeList.add(scope);
        } else {
            throw new SchemaException("Scope cannot be null. ");
        }

    }

    public void addScopes(Collection<IScope> scopes) throws SchemaException {

        if (scopes != null) {
            this.scopeList.addAll(scopes);
        } else {
            throw new SchemaException("Scopes cannot be null. ");
        }

    }

    public void addSession(ISession session) throws SchemaException {

        if (session != null) {
            this.sessionList.add(session);
        } else {
            throw new SchemaException("Session cannot be null. ");
        }

    }

    public void addSessions(Collection<ISession> sessions) throws SchemaException {

        if (sessions != null) {
            this.sessionList.addAll(sessions);
        } else {
            throw new SchemaException("Sessions cannot be null. ");
        }

    }

    public void addObserver(IObserver observer) throws SchemaException {

        if (observer != null) {
            this.observerList.add(observer);
        } else {
            throw new SchemaException("Observer cannot be null. ");
        }

    }

    public void addObservers(Collection<IObserver> observers) throws SchemaException {

        if (observers != null) {
            this.observerList.addAll(observers);
        } else {
            throw new SchemaException("Observers cannot be null. ");
        }

    }

    public void addTarget(ITarget target) throws SchemaException {

        if (target != null) {
            this.targetList.add(target);
        } else {
            throw new SchemaException("Target cannot be null. ");
        }

    }

    public void addTargets(Collection<ITarget> targets) throws SchemaException {

        if (targets != null) {
            this.targetList.addAll(targets);
        } else {
            throw new SchemaException("Targets cannot be null. ");
        }

    }

    public void addFilter(IFilter filter) throws SchemaException {

        if (filter != null) {
            this.filterList.add(filter);
        } else {
            throw new SchemaException("Filter cannot be null. ");
        }

    }

    public void addFilters(Collection<IFilter> filters) throws SchemaException {

        if (filters != null) {
            this.filterList.addAll(filters);
        } else {
            throw new SchemaException("Filters cannot be null. ");
        }

    }

    public void addLens(ILens lens) throws SchemaException {

        if (lens != null) {
            this.lensList.add(lens);
        } else {
            throw new SchemaException("Lens cannot be null. ");
        }

    }

    public void addLenses(Collection<ILens> lenses) throws SchemaException {

        if (lenses != null) {
            this.lensList.addAll(lenses);
        } else {
            throw new SchemaException("Lenses cannot be null. ");
        }

    }

    public void serializeAsXml(File xmlFile) throws SchemaException {

        if (xmlFile == null) {
            throw new SchemaException("File cannot be null. ");
        }

        Document newSchema = this.getDocument();

        DOMImplementationRegistry registry;
        try {
            registry = DOMImplementationRegistry.newInstance();

            DOMImplementationLS domImplLS = (DOMImplementationLS) registry.getDOMImplementation("LS");

            LSSerializer writer = domImplLS.createLSSerializer();

            writer.writeToURI(newSchema, xmlFile.toURI().toURL().toString());
        } catch (IOException ex) {
            LOG.error("saveConfiguration - Could not save the configuration to the file {} ", xmlFile.getAbsolutePath(),
                    ex);
        } catch (ClassNotFoundException e) {
            LOG.error("saveConfiguration - Could not save the configuration to the file {} ", xmlFile.getAbsolutePath(),
                    e);
            throw new SchemaException(e.toString(), e);
        } catch (InstantiationException e) {
            throw new SchemaException(e.toString(), e);
        } catch (IllegalAccessException e) {
            throw new SchemaException(e.toString(), e);
        } catch (ClassCastException e) {
            throw new SchemaException(e.toString(), e);
        }

    }

    public void serializeAsXmlFormatted(File xmlFile) throws SchemaException {

        if (xmlFile == null) {
            throw new SchemaException("File cannot be null. ");
        }
        TransformerFactory transformerFactory = TransformerFactory.newInstance();

        DOMSource source = new DOMSource(this.getDocument());

        try {
            FileWriter writer = new FileWriter(xmlFile);
            StreamResult result = new StreamResult(writer);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(source, result);
        } catch (TransformerConfigurationException e) {
            LOG.error("Cannot configure xml format", e);

        } catch (TransformerException e) {
            LOG.error("Error transorming to xml file", e);
            throw new SchemaException(e.getLocalizedMessage(), e);
        } catch (IOException e) {

            LOG.error("Error writing to xml file", e);
            throw new RuntimeException(e);
        } catch (Throwable e) {

            LOG.error("Error writing to xml file", e);
            throw new RuntimeException(e);
        }
    }

    public Document getDocument() throws SchemaException {

        Document newSchema = null;
        try {
            newSchema = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException pce) {
            throw new SchemaException("Unable to create new XML document. ", pce);
        }

        Element root = newSchema.createElement(RootElement.XML_OBSERVATION_CONTAINER);
        root.setAttribute(XML_NS_KEY, XML_NS);
        root.setAttribute(XML_SI_KEY, XML_SI);
        root.setAttribute(XML_SCHEMA_LOCATION_KEY, XML_SCHEMA_LOCATION);
        root.setAttribute(XML_SCHEMA_VERSION_KEY, XML_SCHEMA_VERSION);

        newSchema.appendChild(root);

        // Add not linked elements
        // Don't change this as otherwise E&T cannot load the schema :-)
        root = this.addObserverToXmlElement(root);
        root = this.addSiteToXmlElement(root);
        root = this.addSessionToXmlElement(root);
        root = this.addTargetToXmlElement(root);
        root = this.addScopeToXmlElement(root);
        root = this.addEyepieceToXmlElement(root);
        root = this.addLensToXmlElement(root);
        root = this.addFilterToXmlElement(root);
        root = this.addImagerToXmlElement(root);

        // This will persist all elements that are used by the observations
        // so almost all elements, except the not linked elements
        Iterator<IObservation> iterator = observationList.iterator();
        IObservation current = null;
        while (iterator.hasNext()) {
            current = iterator.next();
            current.addToXmlElement(root);
        }

        return newSchema;

    }

    private Element addEyepieceToXmlElement(Element root) {

        if (root == null) {
            return null;
        }

        Document ownerDoc = root.getOwnerDocument();

        // Get or create the container element
        Element e_Eyepiece = null;
        NodeList nodeList = ownerDoc.getElementsByTagName(RootElement.XML_EYEPIECE_CONTAINER);
        if (nodeList.getLength() == 0) { // we're the first element. Create container element
            e_Eyepiece = ownerDoc.createElement(RootElement.XML_EYEPIECE_CONTAINER);
            ownerDoc.getDocumentElement().appendChild(e_Eyepiece);
        } else {
            e_Eyepiece = (Element) nodeList.item(0); // there should be only one container element
        }

        Iterator<IEyepiece> iterator = this.eyepieceList.iterator();
        IEyepiece current = null;
        while (iterator.hasNext()) {
            current = iterator.next();
            current.addToXmlElement(e_Eyepiece);
        }

        return root;

    }

    private Element addImagerToXmlElement(Element root) {

        if (root == null) {
            return null;
        }

        Document ownerDoc = root.getOwnerDocument();

        // Get or create the container element
        Element e_Imager = null;
        NodeList nodeList = ownerDoc.getElementsByTagName(RootElement.XML_IMAGER_CONTAINER);
        if (nodeList.getLength() == 0) { // we're the first element. Create container element
            e_Imager = ownerDoc.createElement(RootElement.XML_IMAGER_CONTAINER);
            ownerDoc.getDocumentElement().appendChild(e_Imager);
        } else {
            e_Imager = (Element) nodeList.item(0); // there should be only one container element
        }

        Iterator<IImager> iterator = this.imagerList.iterator();
        IImager current = null;
        while (iterator.hasNext()) {
            current = iterator.next();
            current.addToXmlElement(e_Imager);
        }

        return root;

    }

    private Element addSiteToXmlElement(Element root) {

        if (root == null) {
            return null;
        }

        Document ownerDoc = root.getOwnerDocument();

        // Get or create the container element
        Element e_Site = null;
        NodeList nodeList = ownerDoc.getElementsByTagName(RootElement.XML_SITE_CONTAINER);
        if (nodeList.getLength() == 0) { // we're the first element. Create container element
            e_Site = ownerDoc.createElement(RootElement.XML_SITE_CONTAINER);
            ownerDoc.getDocumentElement().appendChild(e_Site);
        } else {
            e_Site = (Element) nodeList.item(0); // there should be only one container element
        }

        Iterator<ISite> iterator = this.siteList.iterator();
        ISite current = null;
        while (iterator.hasNext()) {
            current = iterator.next();
            current.addToXmlElement(e_Site);
        }

        return root;

    }

    private Element addObserverToXmlElement(Element root) {

        if (root == null) {
            return null;
        }

        Document ownerDoc = root.getOwnerDocument();

        // Get or create the container element
        Element e_Observer = null;
        NodeList nodeList = ownerDoc.getElementsByTagName(RootElement.XML_OBSERVER_CONTAINER);
        if (nodeList.getLength() == 0) { // we're the first element. Create container element
            e_Observer = ownerDoc.createElement(RootElement.XML_OBSERVER_CONTAINER);
            ownerDoc.getDocumentElement().appendChild(e_Observer);
        } else {
            e_Observer = (Element) nodeList.item(0); // there should be only one container element
        }

        Iterator<IObserver> iterator = this.observerList.iterator();
        IObserver current = null;
        while (iterator.hasNext()) {
            current = iterator.next();
            current.addToXmlElement(e_Observer);
        }

        return root;

    }

    private Element addTargetToXmlElement(Element root) {

        if (root == null) {
            return null;
        }

        Document ownerDoc = root.getOwnerDocument();

        // Get or create the container element
        Element e_Target = null;
        NodeList nodeList = ownerDoc.getElementsByTagName(RootElement.XML_TARGET_CONTAINER);
        if (nodeList.getLength() == 0) { // we're the first element. Create container element
            e_Target = ownerDoc.createElement(RootElement.XML_TARGET_CONTAINER);
            ownerDoc.getDocumentElement().appendChild(e_Target);
        } else {
            e_Target = (Element) nodeList.item(0); // there should be only one container element
        }

        Iterator<ITarget> iterator = this.targetList.iterator();
        ITarget current = null;
        while (iterator.hasNext()) {
            current = iterator.next();
            current.addToXmlElement(e_Target);
        }

        return root;

    }

    private Element addFilterToXmlElement(Element root) {

        if (root == null) {
            return null;
        }

        Document ownerDoc = root.getOwnerDocument();

        // Get or create the container element
        Element e_Filter = null;
        NodeList nodeList = ownerDoc.getElementsByTagName(RootElement.XML_FILTER_CONTAINER);
        if (nodeList.getLength() == 0) { // we're the first element. Create container element
            e_Filter = ownerDoc.createElement(RootElement.XML_FILTER_CONTAINER);
            ownerDoc.getDocumentElement().appendChild(e_Filter);
        } else {
            e_Filter = (Element) nodeList.item(0); // there should be only one container element
        }

        Iterator<IFilter> iterator = this.filterList.iterator();
        IFilter current = null;
        while (iterator.hasNext()) {
            current = iterator.next();
            current.addToXmlElement(e_Filter);
        }

        return root;

    }

    private Element addLensToXmlElement(Element root) {

        if (root == null) {
            return null;
        }

        Document ownerDoc = root.getOwnerDocument();

        // Get or create the container element
        Element e_Lens = null;
        NodeList nodeList = ownerDoc.getElementsByTagName(RootElement.XML_LENS_CONTAINER);
        if (nodeList.getLength() == 0) { // we're the first element. Create container element
            e_Lens = ownerDoc.createElement(RootElement.XML_LENS_CONTAINER);
            ownerDoc.getDocumentElement().appendChild(e_Lens);
        } else {
            e_Lens = (Element) nodeList.item(0); // there should be only one container element
        }

        Iterator<ILens> iterator = this.lensList.iterator();
        ILens current = null;
        while (iterator.hasNext()) {
            current = iterator.next();
            current.addToXmlElement(e_Lens);
        }

        return root;

    }

    private Element addSessionToXmlElement(Element root) {

        if (root == null) {
            return null;
        }

        Document ownerDoc = root.getOwnerDocument();

        // Get or create the container element
        Element e_Session = null;
        NodeList nodeList = ownerDoc.getElementsByTagName(RootElement.XML_SESSION_CONTAINER);
        if (nodeList.getLength() == 0) { // we're the first element. Create container element
            e_Session = ownerDoc.createElement(RootElement.XML_SESSION_CONTAINER);
            ownerDoc.getDocumentElement().appendChild(e_Session);
        } else {
            e_Session = (Element) nodeList.item(0); // there should be only one container element
        }

        Iterator<ISession> iterator = this.sessionList.iterator();
        ISession current = null;
        while (iterator.hasNext()) {
            current = iterator.next();
            current.addToXmlElement(e_Session);
        }

        return root;

    }

    private Element addScopeToXmlElement(Element root) {

        if (root == null) {
            return null;
        }

        Document ownerDoc = root.getOwnerDocument();

        // Get or create the container element
        Element e_Scope = null;
        NodeList nodeList = ownerDoc.getElementsByTagName(RootElement.XML_SCOPE_CONTAINER);
        if (nodeList.getLength() == 0) { // we're the first element. Create container element
            e_Scope = ownerDoc.createElement(RootElement.XML_SCOPE_CONTAINER);
            ownerDoc.getDocumentElement().appendChild(e_Scope);
        } else {
            e_Scope = (Element) nodeList.item(0); // there should be only one container element
        }

        Iterator<IScope> iterator = this.scopeList.iterator();
        IScope current = null;
        while (iterator.hasNext()) {
            current = iterator.next();
            current.addToXmlElement(e_Scope);
        }

        return root;

    }
}
