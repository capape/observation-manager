/* ====================================================================
 * /Observation.java
 *
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.lehmannet.om.mapper.ObservationMapper;
import de.lehmannet.om.util.DateConverter;
import de.lehmannet.om.util.SchemaException;

/**
 * An IObservation describes an astronomical oberservation of exactly one
 * celestial object (target).<br>
 * The observation must have one start date to be correct, but does not have to
 * have an end date (as the end date might be lost in older observations).<br>
 * Inside the XML Schema the Observation is the central entry point for
 * accessing all other kinds of data. (See:
 * <a href="http://observation.sourceforge.net/schema/doc/uml/root.html"> XML
 * Schema Doc</a>) Therefore the IObservation provides access to almost all
 * other XML Schema elements.
 *
 * @author doergn@users.sourceforge.net
 * @since 1.0
 */
public class Observation extends SchemaElement implements IObservation {

    // ------------------
    // Instance Variables ------------------------------------------------
    // ------------------

    // Start date of observation
    private Calendar begin = null;

    // End date of observation
    private Calendar end = null;

    // Faintest start that could be seen with the naked eye (in magnitude)
    private float faintestStar = Float.NaN;

    // Sky quality meter
    private SurfaceBrightness sqmValue = null;

    // Magnification used for this observation
    private float magnification = Float.NaN;

    // Seeing conditions (1: best, 5: worst, -1 value wasn't set by user)
    private int seeing = -1;

    // Relative paths to an image (list of String)
    private List<String> images = new LinkedList<>();

    // Imager used for this observation
    private IImager imager = null;

    // The session this observation belongs to
    private ISession session = null;

    // Accessories used during this observation
    private String accessories = null;

    // The target of the observation
    private ITarget target = null;

    // The observer who made the observation
    private IObserver observer = null;

    // The site where the observation took place
    private ISite site = null;

    // The scope that was used during the observation
    private IScope scope = null;

    // The eyepiece that was used for this observation
    private IEyepiece eyepiece = null;

    // The filter that was used for this observation
    private IFilter filter = null;

    // The lens that was used for this observation
    private ILens lens = null;

    // The results (IFinding) of the observation as List
    private List<IFinding> results = new LinkedList<>();

    // ------------
    // Constructors ------------------------------------------------------
    // ------------

    /**
     * Constructs a new Observation instance from a given XML Schema Node. Normally
     * this constructor is only used by de.lehmannet.om.util.SchemaLoader<br>
     * Please mind: As a Observation can have multiple subelements (e.g.
     * <target>,<session>,<observer>...) that link to their implementing elements
     * somewhere else in the xml. Because of that this constructor requires several
     * arrays of these subelements to check whether their implementing element
     * exists. If a Observation Node has no subelement of a specific type (cause its
     * optional by XML Schema specification), the according array parameter can be
     * empty or <code>null</code>.
     *
     * @param observation the XML Schema Node that represents this Observation
     *                    object
     * @param targets     Array of ITarget that might be linked from this
     *                    observation
     * @param observers   Array of IObserver that might be linked from this
     *                    observation
     * @param sites       Array of ISite that might be linked from this observation
     * @param scopes      Array of IScope that might be linked from this observation
     *                    (as Site is optional by XML Schema, this parameter can be
     *                    <code>NULL</code> if passed Observation Node has no <site>
     *                    subelement)
     * @param sessions    Array of ISession that might be linked from this
     *                    observation (as Session is optional by XML Schema, this
     *                    parameter can be <code>NULL</code> if passed Observation
     *                    Node has no <session> subelement)
     * @param eyepieces   Array of IEyepiece that might be linked from this
     *                    observation (as Eyepiece is optional by XML Schema, this
     *                    parameter can be <code>NULL</code> if passed Observation
     *                    Node has no <eyepiece> subelement)
     * @param filters     Array of IFilter that might be linked from this
     *                    observation (as Filter is optional by XML Schema, this
     *                    parameter can be <code>NULL</code> if passed Observation
     *                    Node has no <filter> subelement)
     * @param imagers     Array of IImagers that might be linked from this
     *                    observation (as Imager is optional by XML Schema, this
     *                    parameter can be <code>NULL</code> if passed Observation
     *                    Node has no <imager> subelement)
     * @param lenses      Array of ILens that might be linked from this observation
     *                    (as Lens is optional by XML Schema, this parameter can be
     *                    <code>NULL</code> if passed Observation Node has no <lens>
     *                    subelement)
     * @throws IllegalArgumentException if parameter observation is
     *                                  <code>null</code> or Observation Node has
     *                                  subelements of which no according array was
     *                                  passed (or the passed array was empty).
     * @throws SchemaException          if the given Node does not match the XML
     *                                  Schema specifications
     */
    public Observation(Node observation, ITarget[] targets, IObserver[] observers, ISite[] sites, IScope[] scopes,
            ISession[] sessions, IEyepiece[] eyepieces, IFilter[] filters, IImager[] imagers, ILens... lenses)
            throws SchemaException, IllegalArgumentException {

        if (observation == null) {
            throw new IllegalArgumentException("Parameter observation node cannot be NULL. ");
        }

        // Cast to element as we need some methods from it
        Element observationElement = (Element) observation;

        // Helper classes
        
       

        // Getting data
        // First mandatory stuff and down below optional data

        this.setID(ObservationMapper.getMandatoryID(observationElement));
        this.setBegin(ObservationMapper.getMandatoryBeginDate(observationElement));
        this.setTarget(ObservationMapper.getMandatoryTarget(targets, observationElement));
        this.setObserver(ObservationMapper.getMandatoryObserver(observers, observationElement));
        this.setEnd(ObservationMapper.getOptionalEndDate(observationElement));
        this.setSite(ObservationMapper.getOptionalSite(sites, observationElement));
        this.setScope(ObservationMapper.getOptionalScope(scopes, observationElement));

        this.setImages(ObservationMapper.getOptionalImages(observationElement));

        this.setFaintestStar(ObservationMapper.getOptionalFaintestStar(observationElement));

        this.setSkyQuality(ObservationMapper.getSkyQualityMeter(observationElement));
        this.setSkyQuality(ObservationMapper.getNewSkyQualityMeter(observationElement));

        this.setMagnification(ObservationMapper.getOptionalMagnification(observationElement));
        this.setAccessories(ObservationMapper.getOptionalAccesories(observationElement));
        this.setSeeing(ObservationMapper.getOptionalSeeing(observationElement));
        this.setSession(ObservationMapper.getOptionalSession(sessions, observationElement));
        this.setEyepiece(ObservationMapper.getOptionalEyepiece(eyepieces, observationElement));
        this.setLens(ObservationMapper.getOptionalLens(observationElement, lenses));
        this.setFilter(ObservationMapper.getOptionalFilter(filters, observationElement));

        this.setImager(ObservationMapper.getOptionalImager(imagers, observationElement));

    }

    /**
     * Constructs a new instance of an Observation.<br>
     * Simplest constructor as all parameters are mandatory.
     *
     * @param begin    Start date of observation
     * @param target   The target of the observation
     * @param observer The observer who made the observation
     * @param result   The result of this observation
     * @throws IllegalArgumentException if one of the parameters is
     *                                  <code>null</code>
     */
    public Observation(Calendar begin, ITarget target, IObserver observer, IFinding result)
            throws IllegalArgumentException {

        if (begin == null) {
            throw new IllegalArgumentException("Begin date cannot be null. ");
        }
        this.begin = begin;

        if (target == null) {
            throw new IllegalArgumentException("Target cannot be null. ");
        }
        this.target = target;

        if (observer == null) {
            throw new IllegalArgumentException("Observer cannot be null. ");
        }
        this.observer = observer;

        if (result == null) {
            throw new IllegalArgumentException("Result cannot be null. ");
        }
        this.addResult(result);

    }

    /**
     * Constructs a new instance of an Observation.<br>
     *
     * @param begin    Start date of observation
     * @param target   The target of the observation
     * @param observer The observer who made the observation
     * @param results  The results of this observation as List
     * @throws IllegalArgumentException if one of the parameters is
     *                                  <code>null</code> or the result list is
     *                                  empty
     */
    public Observation(Calendar begin, ITarget target, IObserver observer, List<IFinding> results)
            throws IllegalArgumentException {

        if (begin == null) {
            throw new IllegalArgumentException("Begin date cannot be null. ");
        }
        this.begin = begin;

        if (target == null) {
            throw new IllegalArgumentException("Target cannot be null. ");
        }
        this.target = target;

        if (observer == null) {
            throw new IllegalArgumentException("Observer cannot be null. ");
        }
        this.observer = observer;

        if ((results == null) || (results.isEmpty())) {
            throw new IllegalArgumentException("Result list cannot be null or empty. ");
        }
        this.addResults(results);

    }

    /**
     * Constructs a new instance of an Observation.<br>
     *
     * @param begin    Start date of observation
     * @param end      End date of observation
     * @param target   The target of the observation
     * @param observer The observer who made the observation
     * @param results  The results of this observation as List
     * @throws IllegalArgumentException if one of the parameters, except end date,
     *                                  is <code>null</code>, or the result list is
     *                                  empty
     */
    public Observation(Calendar begin, Calendar end, ITarget target, IObserver observer, List<IFinding> results)
            throws IllegalArgumentException {

        this(begin, target, observer, results);

        this.end = end;

    }

    /**
     * Constructs a new instance of an Observation.<br>
     *
     * @param begin    Start date of observation
     * @param end      End date of observation
     * @param target   The target of the observation
     * @param observer The observer who made the observation
     * @param result   The result of this observation
     * @throws IllegalArgumentException if one of the parameters, except end date,
     *                                  is <code>null</code>
     */
    public Observation(Calendar begin, Calendar end, ITarget target, IObserver observer, IFinding result)
            throws IllegalArgumentException {

        this(begin, target, observer, result);

        this.end = end;

    }

    /**
     * Constructs a new instance of an Observation.<br>
     *
     * @param begin         Start date of observation
     * @param end           End date of observation
     * @param faintestStar  Faintest star visible with the naked eye (in magnitude)
     * @param sq            Sky quality meter value
     * @param seeing        The seeing during observation (1=best, 5=worst)
     * @param magnification Magnification used at the observation
     * @param target        The target of the observation
     * @param observer      The observer who made the observation
     * @param site          The place where the observation took place
     * @param scope         The scope used for this observation
     * @param eyepiece      The eyepiece used for this observation
     * @param filter        The filter used for this observation
     * @param imager        The imager used for this observation
     * @param lens          The lens used for this observation
     * @param session       The session this observation belongs to
     * @param result        The result of this observation
     * @throws IllegalArgumentException if one of the follwing parameters, is
     *                                  <code>null</code>: begin, target, observer,
     *                                  site, result or seeing is < 1 or > 5
     */
    public Observation(Calendar begin, Calendar end, float faintestStar, SurfaceBrightness sq, int seeing,
            float magnification, ITarget target, IObserver observer, ISite site, IScope scope, String accessories,
            IEyepiece eyepiece, IFilter filter, IImager imager, ILens lens, ISession session, IFinding result)
            throws IllegalArgumentException {

        this(begin, end, target, observer, result);

        this.setFaintestStar(faintestStar);
        this.setMagnification(magnification);
        this.setSite(site);
        this.setScope(scope);
        this.setEyepiece(eyepiece);
        this.setFilter(filter);
        this.setSession(session);
        this.setSeeing(seeing);
        this.setAccessories(accessories);
        this.setImager(imager);
        this.setLens(lens);
        this.setSkyQuality(sq);

    }

    /**
     * Constructs a new instance of an Observation.<br>
     *
     * @param begin         Start date of observation
     * @param end           End date of observation
     * @param faintestStar  Faintest star visible with the naked eye (in magnitude)
     * @param sq            Sky quality meter value
     * @param seeing        The seeing during observation (1=best, 5=worst)
     * @param magnification Magnification used at the observation
     * @param target        The target of the observation
     * @param observer      The observer who made the observation
     * @param site          The place where the observation took place
     * @param scope         The scope used for this observation
     * @param eyepiece      The eyepiece used for this observation
     * @param filter        The filter used for this observation
     * @param imager        The imager used for this observation
     * @param lens          The lens used for this observation
     * @param session       The session this observation belongs to
     * @param results       The results of this observation as List
     * @throws IllegalArgumentException if one of the follwing parameters, is
     *                                  <code>null</code>: begin, target, observer,
     *                                  site, results or result list is empty. Also
     *                                  if seeing is < 1 or > 5
     */
    public Observation(Calendar begin, Calendar end, float faintestStar, SurfaceBrightness sq, int seeing,
            float magnification, ITarget target, IObserver observer, ISite site, IScope scope, String accessories,
            IEyepiece eyepiece, IFilter filter, IImager imager, ILens lens, ISession session, List<IFinding> results)
            throws IllegalArgumentException {

        this(begin, end, target, observer, results);

        this.setFaintestStar(faintestStar);
        this.setMagnification(magnification);
        this.setSite(site);
        this.setScope(scope);
        this.setEyepiece(eyepiece);
        this.setFilter(filter);
        this.setSession(session);
        this.setSeeing(seeing);
        this.setAccessories(accessories);
        this.setImager(imager);
        this.setLens(lens);
        this.setSkyQuality(sq);

    }

    // -------------
    // SchemaElement -----------------------------------------------------
    // -------------

    /**
     * Returns a display name for this element.<br>
     * The method differs from the toString() method as toString() shows more
     * technical information about the element. Also the formating of toString() can
     * spread over several lines.<br>
     * This method returns a string (in one line) that can be used as displayname in
     * e.g. a UI dropdown box.
     *
     * @return Returns a String with a one line display name
     *
     */
    @Override
    public String getDisplayName() {

        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault());

        format.setCalendar(this.begin);

        return format.format(this.begin.getTime()) + " - " + this.target.getDisplayName();

    }

    // ------
    // Object ------------------------------------------------------------
    // ------

    /**
     * Overwrittes toString() method from java.lang.Object.<br>
     * Returns the field values of this Observation.
     *
     * @return This Observation field values
     * @see java.lang.Object
     */
    @Override
    public String toString() {

        StringBuilder buffer = new StringBuilder();
        buffer.append("Observation: Begin Date=");
        buffer.append(DateConverter.toISO8601(begin));

        if (end != null) {
            buffer.append(" End date=");
            buffer.append(DateConverter.toISO8601(end));
        }

        buffer.append(" Target=");
        buffer.append(target);

        buffer.append(" Observer=");
        buffer.append(observer);

        buffer.append(" Results=");
        ListIterator<IFinding> iterator = results.listIterator();
        while (iterator.hasNext()) {
            buffer.append(iterator.next());
            if (iterator.hasNext()) {
                buffer.append(" --- ");
            }
        }

        if ((this.images != null) && (this.images.size() > 0)) {
            buffer.append(" Images=");
            ListIterator<String> imageIterator = images.listIterator();
            while (imageIterator.hasNext()) {
                buffer.append(imageIterator.next());
                if (imageIterator.hasNext()) {
                    buffer.append(", ");
                }
            }
        }

        if (!Float.isNaN(faintestStar)) {
            buffer.append(" Faintest star=");
            buffer.append(faintestStar);
        }

        if (sqmValue != null) {
            buffer.append(" Sky quality meter value=");
            buffer.append(sqmValue);
        }

        if (!Float.isNaN(magnification)) {
            buffer.append(" Magnification=");
            buffer.append(magnification);
        }

        if (seeing != -1) {
            buffer.append(" Seeing=");
            buffer.append(seeing);
        }

        if (accessories != null) {
            buffer.append(" Accessories=");
            buffer.append(accessories);
        }

        if (session != null) {
            buffer.append(" Session=");
            buffer.append(session);
        }

        if (site != null) {
            buffer.append(" Site=");
            buffer.append(site);
        }

        if (scope != null) {
            buffer.append(" Scope=");
            buffer.append(scope);
        }

        if (eyepiece != null) {
            buffer.append(" Eyepiece=");
            buffer.append(eyepiece);
        }

        if (filter != null) {
            buffer.append(" Filter=");
            buffer.append(filter);
        }

        if (imager != null) {
            buffer.append(" Imager=");
            buffer.append(imager);
        }

        if (lens != null) {
            buffer.append(" Lens=");
            buffer.append(lens);
        }

        return buffer.toString();

    }

    /*
     * public boolean equals(Object obj) {
     *
     * if( obj == null || !(obj instanceof IObservation) ) { return false; }
     *
     * IObservation observation = (IObservation)obj;
     *
     * if( !observation.getBegin().equals(begin) ) { return false; }
     *
     * if( !observation.getTarget().equals(target) ) { return false; }
     *
     * if( !observation.getObserver().equals(observer) ) { return false; }
     *
     * // Sort result list from given object List objectResults =
     * sortResultList(observation.getResults());
     *
     * // dublicate this RootElement results, that the original // result list stays
     * unchanged, while we sort and compare the results List resultList = new
     * LinkedList(results); // Sort internal result list resultList =
     * sortResultList(resultList);
     *
     * // Calls AbstractList.equals(Object) as both list should be sorted if(
     * !resultList.equals(objectResults) ) { return false; }
     *
     * return true;
     *
     * }
     */

    // ------------
    // IObservation ------------------------------------------------------
    // ------------

    /**
     * Adds this Observation to a given parent XML DOM Element. The Observation
     * element will be set as a child element of the passed element.
     *
     * @param parent The parent element for this Observation
     * @see org.w3c.dom.Element
     */
    @Override
    public void addToXmlElement(Element parent) {

        if (parent == null) {
            return;
        }

        Document ownerDoc = parent.getOwnerDocument();

        Element e_Observation = ownerDoc.createElement(IObservation.XML_ELEMENT_OBSERVATION);

        // Create the link attribute
        e_Observation.setAttribute(ISchemaElement.XML_ELEMENT_ATTRIBUTE_ID, this.getID());

        // Don't change the sequence, as otherwise E&T cannot load the schema :-)
        e_Observation = observer.addAsLinkToXmlElement(e_Observation, IObserver.XML_ELEMENT_OBSERVER);

        if (site != null) {
            e_Observation = site.addAsLinkToXmlElement(e_Observation);
        }

        if (session != null) {
            e_Observation = session.addAsLinkToXmlElement(e_Observation);
        }

        e_Observation = target.addAsLinkToXmlElement(e_Observation, null);

        addBegin(ownerDoc, e_Observation);
        addEnd(ownerDoc, e_Observation);
        addFaintestStar(ownerDoc, e_Observation);

        addSqm(ownerDoc, e_Observation);

        addSeeing(ownerDoc, e_Observation);

        if (scope != null) {
            e_Observation = scope.addAsLinkToXmlElement(e_Observation);
        }

        addAccesories(ownerDoc, e_Observation);

        if (eyepiece != null) {
            e_Observation = eyepiece.addAsLinkToXmlElement(e_Observation);
        }

        if (lens != null) {
            e_Observation = lens.addAsLinkToXmlElement(e_Observation);
        }

        if (filter != null) {
            e_Observation = filter.addAsLinkToXmlElement(e_Observation);
        }

        addMagnification(ownerDoc, e_Observation);

        if (imager != null) {
            e_Observation = imager.addAsLinkToXmlElement(e_Observation);
        }

        ListIterator<IFinding> iterator = this.results.listIterator();
        IFinding result = null;
        while (iterator.hasNext()) {
            result = iterator.next();
            e_Observation = result.addToXmlElement(e_Observation);
        }

        addImages(ownerDoc, e_Observation);

        // Add element here so that XML sequence fits forward references
        parent.appendChild(e_Observation);

    }

    private void addImages(Document ownerDoc, Element e_Observation) {
        if ((this.images != null) && (!this.images.isEmpty())) {
            ListIterator<String> imagesIterator = this.images.listIterator();
            Element e_currentImage = null;
            Node n_ImageText = null;
            String path = null;
            while (imagesIterator.hasNext()) {
                e_currentImage = ownerDoc.createElement(XML_ELEMENT_IMAGE);
                // Always write image path with / separators. While loading from XML, convert
                // back to \ if necessary
                path = (String) imagesIterator.next();
                path = path.replace('\\', '/');
                n_ImageText = ownerDoc.createCDATASection(path);
                e_currentImage.appendChild(n_ImageText);
                e_Observation.appendChild(e_currentImage);
            }
        }
    }

    private void addMagnification(Document ownerDoc, Element e_Observation) {
        if (!Float.isNaN(this.magnification)) {
            Element e_Magnification = ownerDoc.createElement(XML_ELEMENT_MAGNIFICATION);
            Node n_MagnificationText = ownerDoc.createTextNode(String.valueOf(this.magnification));
            e_Magnification.appendChild(n_MagnificationText);
            e_Observation.appendChild(e_Magnification);
        }
    }

    private void addAccesories(Document ownerDoc, Element e_Observation) {
        if (this.accessories != null) {
            Element e_Accessories = ownerDoc.createElement(XML_ELEMENT_ACCESSORIES);
            Node n_AccessoriesText = ownerDoc.createCDATASection(String.valueOf(this.accessories));
            e_Accessories.appendChild(n_AccessoriesText);
            e_Observation.appendChild(e_Accessories);
        }
    }

    private void addSeeing(Document ownerDoc, Element e_Observation) {
        if (seeing != -1) {
            Element e_Seeing = ownerDoc.createElement(XML_ELEMENT_SEEING);
            Node n_SeeingText = ownerDoc.createTextNode(String.valueOf(seeing));
            e_Seeing.appendChild(n_SeeingText);
            e_Observation.appendChild(e_Seeing);
        }
    }

    private void addSqm(Document ownerDoc, Element e_Observation) {
        if (sqmValue != null) {
            Element e_SQMValue = ownerDoc.createElement(XML_ELEMENT_SKYQUALITY_NEW);
            e_SQMValue = sqmValue.setToXmlElement(e_SQMValue);
            e_Observation.appendChild(e_SQMValue);
        }
    }

    private void addFaintestStar(Document ownerDoc, Element e_Observation) {
        if (!Float.isNaN(faintestStar)) {
            Element e_FaintestStar = ownerDoc.createElement(XML_ELEMENT_FAINTESTSTAR);
            Node n_FaintestStarText = ownerDoc.createTextNode(String.valueOf(faintestStar));
            e_FaintestStar.appendChild(n_FaintestStarText);
            e_Observation.appendChild(e_FaintestStar);
        }
    }

    private void addEnd(Document ownerDoc, Element e_Observation) {
        if (end != null) {
            Element e_End = ownerDoc.createElement(XML_ELEMENT_END);
            Node n_EndText = ownerDoc.createTextNode(DateConverter.toISO8601(end));
            e_End.appendChild(n_EndText);
            e_Observation.appendChild(e_End);
        }
    }

    private void addBegin(Document ownerDoc, Element e_Observation) {
        Element e_Begin = ownerDoc.createElement(XML_ELEMENT_BEGIN);
        Node n_BeginText = ownerDoc.createTextNode(DateConverter.toISO8601(begin));
        e_Begin.appendChild(n_BeginText);
        e_Observation.appendChild(e_Begin);
    }

    /**
     * Returns the start date of the observation.<br>
     *
     * @return The start date of the observation
     */
    @Override
    public Calendar getBegin() {

        return (Calendar) begin.clone();

    }

    /**
     * Returns the accessories used for this observation.<br>
     * Might return <code>null</code> if no accessories have been used.
     *
     * @return Accessories used for this observation or <code>null</code> if no
     *         accessories were used
     */
    @Override
    public String getAccessories() {

        return this.accessories;

    }

    /**
     * Returns the end date of the observation.<br>
     * Might return <code>null</code> if no end date was given.
     *
     * @return The end date of the observation or <code>null</code> if no end date
     *         was given
     */
    @Override
    public Calendar getEnd() {

        if (end != null) {
            return (Calendar) end.clone();
        }

        return null;

    }

    /**
     * Returns the eyepiece with which the observation was made.<br>
     * Might return <code>null</code> if no eyepiece was used at all.
     *
     * @return The eyepiece used for the observation or <code>null</code> if no
     *         eyepiece was used.
     */
    @Override
    public IEyepiece getEyepiece() {

        return eyepiece;

    }

    /**
     * Returns the lens with which the observation was made.<br>
     * Might return <code>null</code> if no lens was used at all.
     *
     * @return The lens used for the observation or <code>null</code> if no lens was
     *         used.
     */
    @Override
    public ILens getLens() {

        return lens;

    }

    /**
     * Returns the filter which was used for this observation<br>
     * Might return <code>null</code> if no filter was used at all.
     *
     * @return The filter used for the observation or <code>null</code> if no filter
     *         was used.
     * @since 1.5
     */
    @Override
    public IFilter getFilter() {

        return this.filter;

    }

    /**
     * Returns the imager that was used for this observation.<br>
     *
     * @return The imager used at this observation.
     */
    @Override
    public IImager getImager() {

        return this.imager;

    }

    /**
     * Returns the magnification used for this observation. Might return
     * <code>Float.NaN</code> if no value was set at all.
     *
     * @return The magnification or <code>Float.NaN</code> if no value was set.
     */
    @Override
    public float getMagnification() {

        return magnification;

    }

    /**
     * Returns the magnitude of the faintest star that could be seen during
     * observation time with the unaided eye. Might return <code>Float.NaN</code> if
     * no value was set at all.
     *
     * @return The magnitude of the faintest star as float value, or
     *         <code>Float.NaN</code> if no value was set.
     */
    @Override
    public float getFaintestStar() {

        return faintestStar;

    }

    /**
     * Returns the sky quality meter value Might return <code>null</code> if no
     * value was set at all.
     *
     * @return The sky quality value, or <code>null</code> if no value was set.
     */
    @Override
    public SurfaceBrightness getSkyQuality() {

        return sqmValue;

    }

    /**
     * Returns a list of images (relativ path to images), taken at this observation.
     * Might return <code>null</code> if images were set.
     *
     * @return List of images or <code>null</code> if no images were set.
     */
    @Override
    public List<String> getImages() {

        if ((this.images == null) || (this.images.isEmpty())) {
            return null;
        }

        return this.images;

    }

    /**
     * Returns the seeing during this observation.<br>
     * Values can reach from 1 to 5, where 1 is best seeing and 5 the worst
     * seeing.<br>
     * Might return <code>-1</code> if no value was set at all.
     *
     * @return A int between 1-5 representing the seeing, or <code>-1</code> if no
     *         value was set for seeing.
     */
    @Override
    public int getSeeing() {

        return seeing;

    }

    /**
     * Returns the observer who made the observation.<br>
     *
     * @return The observer who made this observation.
     */
    @Override
    public IObserver getObserver() {

        return observer;

    }

    /**
     * Sets a single IFinding as result for this observation.<br>
     * The old list of results will be overwritten. If you want to add one ore more
     * results to the existing ones use addResults(java.util.List) or
     * addResult(IFinding).<br>
     * If the passed IFinding was successfully attached to this observation, the
     * method will return <b>true</b>. <br>
     * If the passed IFinding is <code>null</code>, an IllegalArgumentException is
     * thrown.
     *
     * @param results A new result for this observation
     * @see de.lehmannet.om.IObservation#addResults(java.util.List results)
     * @see de.lehmannet.om.IObservation#addResult(IFinding result)
     * @see de.lehmannet.om.IObservation#setResults(List results)
     * @see de.lehmannet.om.IFinding
     * @throws IllegalArgumentException if the new result is <code>null</code>
     */
    @Override
    public void setResult(IFinding result) throws IllegalArgumentException {

        if (results == null) {
            throw new IllegalArgumentException("Result cannot be null. ");
        }

        this.results = new LinkedList<IFinding>();
        this.results.add(result);

    }

    /**
     * Sets a List of results for this observation.<br>
     * The old list of results will be overwritten. If you want to add one ore more
     * results to the existing ones use addResults(java.util.List) or
     * addResult(IFinding).<br>
     * If the new list of results was successfully attached to this observation, the
     * method will return <b>true</b>. If one of the elements in the list does not
     * implement the IFinding interface <b>false</b> is returned.<br>
     * If the new list is empty or <code>null</code>, an IllegalArgumentException is
     * thrown.
     *
     * @param results The new list of results for this observation
     * @return <b>true</b> if the given list was successfully attached to this
     *         observation. <b>false</b> if one of the new result elements in the
     *         list did not implement the the IFinding interface.
     * @see de.lehmannet.om.IObservation#addResults(java.util.List results)
     * @see de.lehmannet.om.IObservation#addResult(IFinding result)
     * @see de.lehmannet.om.IFinding
     * @throws IllegalArgumentException if new results list is <code>null</code> or
     *                                  empty
     */
    @Override
    public boolean setResults(List<IFinding> results) {

        if ((results == null) || (results.isEmpty())) {
            throw new IllegalArgumentException("Result list cannot be null or empty. ");
        }

        this.results = results;
        return true;

    }

    /**
     * Sets the accessories used during the observation.<br>
     * If there was already an accessories list attached to this observation, the
     * old one will be replaced with the new one.
     *
     * @param accessories The accessories of the observation
     */
    @Override
    public void setAccessories(String accessories) {

        if ((accessories != null) && ("".equals(accessories.trim()))) {
            this.accessories = null;
            return;
        }

        this.accessories = accessories;

    }

    /**
     * Sets a List of images (path as String) for this observation.<br>
     * The old list of images will be overwritten. If you want to add one ore more
     * images to the existing ones use addImages(java.util.List) or
     * addImage(String).<br>
     * If the new list of images was successfully attached to this observation, the
     * method will return <b>true</b>. If one of the elements in the list isn't a
     * java.lang.String object <b>false</b> is returned.<br>
     * If the new list is <code>null</code>, an IllegalArgumentException is thrown.
     *
     * @param imagesList The new (String) list of images for this observation
     * @see de.lehmannet.om.IObservation#addImages(java.util.List images)
     * @see de.lehmannet.om.IObservation#addImage(String image)
     * @throws IllegalArgumentException if new image list is <code>null</code>
     */
    @Override
    public void setImages(List<String> imagesList) throws IllegalArgumentException {

        if (imagesList == null) {
            throw new IllegalArgumentException("Images list cannot be null. ");
        }

        if (imagesList.isEmpty()) {
            this.images.clear();
            return;
        }

        this.images = imagesList;

    }

    /**
     * Sets an imager used for this observation.<br>
     *
     * @param imager The imager used for this observation
     */
    @Override
    public void setImager(IImager imager) {

        this.imager = imager;

    }

    /**
     * Adds a List of results for this observation.<br>
     * The new list of results will be added to the existing list of results
     * belonging to this observation. If you want to replace the old result list use
     * setResults(java.util.List).<br>
     * If the new list of results was successfully added to the old result list, the
     * method will return <b>true</b>. If the list is empty or <code>null</code>,
     * the old result list will remain unchanged.
     *
     * @param results A list with more results for this observation
     * @return <b>true</b> if the given list was successfully added to this
     *         observations result list. <b>false</b> if the new list could not be
     *         added and the old list remains unchanged.
     * @see de.lehmannet.om.IObservation#setResults(java.util.List results)
     */
    @Override
    public boolean addResults(List<IFinding> results) {

        if ((results == null) || (results.isEmpty())) {
            return false;
        }

        this.results.addAll(results);
        return true;

    }

    /**
     * Adds a new result to this observation.<br>
     *
     * @param result A new result for this observation
     */
    @Override
    public void addResult(IFinding result) {

        if (result == null) {
            return;
        }

        this.results.add(result);

    }

    /**
     * Adds a List of image paths (String) to this observation.<br>
     * The new list of images will be added to the existing list of images belonging
     * to this observation. If you want to replace the old images list use
     * setImages(java.util.List).<br>
     * If the new list of images was successfully added to the old images list, the
     * method will return <b>true</b>. If the list is empty or <code>null</code>,
     * the old result list will remain unchanged.
     *
     * @param images A list (containing Strings) with additional images (path) for
     *               this observation
     * @return <b>true</b> if the given list was successfully added to this
     *         observations images list. <b>false</b> if the new list could not be
     *         added and the old list remains unchanged.
     * @see de.lehmannet.om.IObservation#setResults(java.util.List images)
     */
    @Override
    public boolean addImages(List<String> images) {

        if ((images == null) || (images.isEmpty())) {
            return false;
        }

        this.images.addAll(images);
        return true;

    }

    /**
     * Adds a new image (path) to this observation.<br>
     *
     * @param imagePath A new image for this observation
     */
    @Override
    public void addImage(String imagePath) {

        if (imagePath == null) {
            return;
        }

        this.images.add(imagePath);

    }

    /**
     * Returns a List with one or more results of the observation.<br>
     * Every observation has at least one result.
     *
     * @return A List containing the results of the observation.
     */
    @Override
    public List<IFinding> getResults() {

        return this.results;

    }

    /**
     * Returns the scope that was used for the observation.<br>
     * Might return <code>null</code> if the observation was not made with any
     * scope.
     *
     * @return The scope which was used for the observation, or <code>null</code> if
     *         no scope was used at all.
     */
    @Override
    public IScope getScope() {

        return scope;

    }

    /**
     * Returns the session this observation belongs to.<br>
     * Might return <code>null</code> if the observation is not part of any
     * observation session.
     *
     * @return The session this observation belongs to, or <code>null</code> if the
     *         observation does not belong to any session.
     */
    @Override
    public ISession getSession() {

        return session;

    }

    /**
     * Returns the site where the observation took place.<br>
     *
     * @return The site of the observation, or <code>null</code> if the observation
     *         has no site
     */
    @Override
    public ISite getSite() {

        return site;

    }

    /**
     * Returns the target which was observed.<br>
     *
     * @return The target which was observed.
     */
    @Override
    public ITarget getTarget() {

        return target;

    }

    /**
     * Sets the start date of the observation.<br>
     * The start date is a mandatory field, as the end date is not.
     *
     * @param begin The start date of the observation
     * @throws IllegalArgumentException if new begin date is <code>null</code>
     */
    @Override
    public void setBegin(Calendar begin) throws IllegalArgumentException {

        if (begin == null) {
            throw new IllegalArgumentException("Begin date cannot be null. ");
        }

        this.begin = (Calendar) begin.clone();

    }

    /**
     * Sets the end date of the observation.<br>
     * The end date is an optional field, as for example old observations might not
     * have an precise end date.
     *
     * @param end The end date of the observation
     */
    @Override
    public void setEnd(Calendar end) {

        if (end == null) {
            this.end = null;
            return;
        }

        this.end = (Calendar) end.clone();

    }

    /**
     * Sets the eyepiece of the observation.<br>
     * If there was already an eyepiece attached to this observation, the old one
     * will be replaced with the new one.
     *
     * @param eyepiece The eyepiece of the observation
     */
    @Override
    public void setEyepiece(IEyepiece eyepiece) {

        this.eyepiece = eyepiece;

    }

    /**
     * Sets the lens of the observation.<br>
     * If there was already an lens attached to this observation, the old one will
     * be replaced with the new one.
     *
     * @param lens The lens of the observation
     */
    @Override
    public void setLens(ILens lens) {

        this.lens = lens;

    }

    /**
     * Sets the filter used during this observation.<br>
     * If there was already an filter attached to this observation, the old one will
     * be replaced with the new one.
     *
     * @param filter The filter used during this observation
     * @since 1.5
     */
    @Override
    public void setFilter(IFilter filter) {

        this.filter = filter;

    }

    /**
     * Sets the magnitude value of the faintest star which could be seen with the
     * unaided eye during the observation.<br>
     * If there was already a value set for this observation, the old one will be
     * replaced with the new one.
     *
     * @param faintestStar The faintestStar of the observation in magnitude
     */
    @Override
    public void setFaintestStar(float faintestStar) {

        this.faintestStar = faintestStar;

    }

    /**
     * Sets the magnification used at the observation.<br>
     * If there was already a value set for this observation, the old one will be
     * replaced with the new one. In case a zoom eyepiece was used for the
     * observation, this value conatais th actual focalLength used. (And also the
     * lens focal length factor if used)<br>
     * Example:<br>
     * scope had a focal length of 1114mm<br>
     * zoomeyepiece used a focal length of 10mm<br>
     * lens (barlow) used had a focal length factor of 2x<br>
     * Magnification set here must be:<br>
     * 1114*2/10 = 222,8
     *
     * @param magnification The magnification used at the observation
     */
    @Override
    public void setMagnification(float magnification) {

        this.magnification = magnification;

    }

    /**
     * Sets the sky quality meter value that was determined during the
     * observation.<br>
     * If there was already a value set for this observation, the old one will be
     * replaced with the new one.
     *
     * @param sq The sky quality meter value
     */
    @Override
    public void setSkyQuality(SurfaceBrightness sq) {

        this.sqmValue = sq;

    }

    /**
     * Sets the seeing during this observation.<br>
     * Values can reach from 1 to 5, where 1 is best seeing and 5 the worst
     * seeing.<br>
     *
     * @param seeing A int between 1-5 representing the seeing
     */
    @Override
    public void setSeeing(int seeing) throws IllegalArgumentException {

        if (seeing == -1) { // Unset value
            this.seeing = -1;
            return;
        }

        if ((seeing < 1) || (seeing > 5)) {
            throw new IllegalArgumentException("Seeing must be 1,2,3,4 or 5, but was: " + seeing
                    + "\nIf you wanna clear the entry, please pass -1 as parameter.");
        }

        this.seeing = seeing;

    }

    /**
     * Sets the observer of the observation.<br>
     * If there was already an observer attached to this observation, the old one
     * will be replaced with the new one.
     *
     * @param observer The observer of the observation
     * @throws IllegalArgumentException if new observer is <code>null</code>
     */
    @Override
    public void setObserver(IObserver observer) throws IllegalArgumentException {

        if (observer == null) {
            throw new IllegalArgumentException("Observer cannot be null. ");
        }

        this.observer = observer;

    }

    /**
     * Sets the scope of the observation.<br>
     * If there was already a scope attached to this observation, the old one will
     * be replaced with the new one.
     *
     * @param scope The scope of the observation
     */
    @Override
    public void setScope(IScope scope) {

        this.scope = scope;

    }

    /**
     * Sets the session which this observation belongs to.<br>
     * This observations start date must be between the sessions start and end
     * date.<br>
     * If there was already a session attached to this observation, the old one will
     * be replaced with the new one.
     *
     * @param session The session this observation belongs to
     * @throws IllegalArgumentException if this observations start date is not
     *                                  between the session start and end date
     */
    @Override
    public void setSession(ISession session) throws IllegalArgumentException {

        if (session == null) { // Clear session
            this.session = null;
            return;
        }

        Calendar sessionStart = session.getBegin();
        Calendar sessionEnd = session.getEnd();

        // Check if start date of observation is equal or later then session start
        if (sessionStart.before(begin)) {

            // Check if also end date is correct (if set)
            if (end != null) {
                if (end.before(sessionEnd)) {
                    this.session = session;
                } else {
                    throw new IllegalArgumentException("Session end date if before observations end date. ");
                }
            }

            this.session = session;

        } else {
            throw new IllegalArgumentException("Session start date if after observations start date. ");
        }

    }

    /**
     * Sets the site where this observation took place.<br>
     * If there was already a site attached to this observation, the old one will be
     * replaced with the new one.
     *
     * @param site The site this observation took place
     */
    @Override
    public void setSite(ISite site) {

        this.site = site;

    }

    /**
     * Sets the target of this observation.<br>
     * If there was already a target attached to this observation, the old one will
     * be replaced with the new one.
     *
     * @param target The target of this observation
     * @throws IllegalArgumentException if new Target is <code>null</code>
     */
    @Override
    public void setTarget(ITarget target) throws IllegalArgumentException {

        if (target == null) {
            throw new IllegalArgumentException("Target cannot be null. ");
        }

        this.target = target;

    }

    

}
