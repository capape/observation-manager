/*
 * ====================================================================
 * /IObservation.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om;

import java.time.OffsetDateTime;
import java.util.List;
import org.w3c.dom.Element;

/**
 * An IObservation describes an astronomical oberservation of exactly one celestial object (target).<br>
 * The observation must have one start date to be correct, but does not have to have an end date (as the end date might
 * be lost in older observations).<br>
 * Inside the XML Schema the Observation is the central entry point for accessing all other kinds of data. (See:
 * <a href="http://groups.google.com/group/openastronomylog"> XML Schema Doc</a>) Therefore the IObservation provides
 * access to almost all other XML Schema elements.
 *
 * @author doergn@users.sourceforge.net
 * @since 1.0
 */
public interface IObservation extends ISchemaElement {

    // ---------
    // Constants ---------------------------------------------------------
    // ---------

    /**
     * Constant for XML representation: IObservation element name.<br>
     * Example:<br>
     * &lt;observation&gt;<i>More stuff goes here</i>&lt;/observation&gt;
     */
    String XML_ELEMENT_OBSERVATION = "observation";

    /**
     * Constant for XML representation: RootElement begin date element name.<br>
     * Example:<br>
     * &lt;observation&gt; <br>
     * <i>More stuff goes here</i> &lt;begin&gt;<code>Start date and time of observation</code>&lt;/begin&gt; <i>More
     * stuff goes here</i> &lt;/observation&gt;
     */
    String XML_ELEMENT_BEGIN = "begin";

    /**
     * Constant for XML representation: RootElement accessories (filters, ...).<br>
     * Example:<br>
     * &lt;observation&gt; <br>
     * <i>More stuff goes here</i> &lt;accessories&gt;<code>Some accessories</code>&lt;/accessories&gt; <i>More stuff
     * goes here</i> &lt;/observation&gt;
     */
    String XML_ELEMENT_ACCESSORIES = "accessories";

    /**
     * Constant for XML representation: RootElement end date element name.<br>
     * Example:<br>
     * &lt;observation&gt; <br>
     * <i>More stuff goes here</i> &lt;end&gt;<code>End date and time of observation</code>&lt;/end&gt; <i>More stuff
     * goes here</i> &lt;/observation&gt;
     */
    String XML_ELEMENT_END = "end";

    /**
     * Constant for XML representation: RootElement faintest star element name.<br>
     * Example:<br>
     * &lt;observation&gt; <br>
     * <i>More stuff goes here</i> &lt;faintestStar&gt;<code>Magnitude of faintest star</code>&lt;/faintestStar&gt;
     * <i>More stuff goes here</i> &lt;/observation&gt;
     */
    String XML_ELEMENT_FAINTESTSTAR = "faintestStar";

    /**
     * Constant for XML representation: RootElement sky quality meter element name.<br>
     * Example:<br>
     * &lt;observation&gt; <br>
     * <i>More stuff goes here</i> &lt;sqm&gt;<code>Sky quality in magnitudes per square arcsecond</code>&lt;/sqm&gt;
     * <i>More stuff goes here</i> &lt;/observation&gt;
     *
     * @deprecated use XML_ELEMENT_MAGPERSQUAREARCSEC
     */
    @Deprecated
    String XML_ELEMENT_SKYQUALITY = "sqm";

    /**
     * Constant for XML representation: RootElement sky quality element name.<br>
     * Example:<br>
     * &lt;observation&gt; <br>
     * <i>More stuff goes here</i> &lt;sky-quality&gt;<code>Sky quality</code>&lt;/sky-quality&gt; <i>More stuff goes
     * here</i> &lt;/observation&gt;
     *
     * @since 2.0
     */
    String XML_ELEMENT_SKYQUALITY_NEW = "sky-quality";

    /**
     * Constant for XML representation: RootElement seeing.<br>
     * Allowed values 1,2,3,4,5 .<br>
     * Example:<br>
     * &lt;observation&gt; <br>
     * <i>More stuff goes here</i> &lt;seeingType&gt;<code>Seeing during observation</code>&lt;/seeingType&gt; <i>More
     * stuff goes here</i> &lt;/observation&gt;
     */
    String XML_ELEMENT_SEEING = "seeing";

    /**
     * Constant for XML representation: RootElement magnification.<br>
     * Example:<br>
     * &lt;observation&gt; <br>
     * <i>More stuff goes here</i>
     * &lt;magnification&gt;<code>Magnification used for observation</code>&lt;/magnification&gt; <i>More stuff goes
     * here</i> &lt;/observation&gt;
     */
    String XML_ELEMENT_MAGNIFICATION = "magnification";

    /**
     * Constant for XML representation: Image made during observation.<br>
     * Example:<br>
     * &lt;observation&gt; <br>
     * <i>More stuff goes here</i> &lt;image&gt;<code>Relative path to image</code>&lt;/image&gt; <i>More stuff goes
     * here</i> &lt;/observation&gt;
     */
    String XML_ELEMENT_IMAGE = "image";

    // --------------
    // Public Methods ----------------------------------------------------
    // --------------

    /**
     * Adds the IObservation implementation to an given parent XML DOM Element. The observation Element will be set as a
     * child element of the passed element.<br>
     * Example:<br>
     * &lt;parentElement&gt;<br>
     * &lt;observation&gt;<br>
     * <i>More stuff goes here</i><br>
     * &lt;/observation&gt;<br>
     * &lt;/parentElement&gt;
     *
     * @param parent
     *            The parent element for the IObservation implementation
     * @see org.w3c.dom.Element
     */
    void addToXmlElement(Element parent);

    /**
     * Returns the start date of the observation.<br>
     *
     * @return The start date of the observation
     */
    OffsetDateTime getBegin();

    /**
     * Returns the end date of the observation.<br>
     * Might return <code>null</code> if no end date was given.
     *
     * @return The end date of the observation or <code>null</code> if no end date was given
     */
    OffsetDateTime getEnd();

    /**
     * Returns the accessories used for this observation.<br>
     * Might return <code>null</code> if no accessories have been used.
     *
     * @return Accessories used for this observation or <code>null</code> if no accessories were used
     */
    String getAccessories();

    /**
     * Returns the eyepiece with which the observation was made.<br>
     * Might return <code>null</code> if no eyepiece was used at all.
     *
     * @return The eyepiece used for the observation or <code>null</code> if no eyepiece was used.
     */
    IEyepiece getEyepiece();

    /**
     * Returns the lens with which the observation was made.<br>
     * Might return <code>null</code> if no lens was used at all.
     *
     * @return The lens used for the observation or <code>null</code> if no lens was used.
     */
    ILens getLens();

    /**
     * Returns the sky quality meter value Might return <code>null</code> if no value was set at all.
     *
     * @return The sky quality value, or <code>null</code> if no value was set.
     */
    SurfaceBrightness getSkyQuality();

    /**
     * Returns the filter which was used for this observation<br>
     * Might return <code>null</code> if no filter was used at all.
     *
     * @return The filter used for the observation or <code>null</code> if no filter was used.
     * @since 1.5
     */
    IFilter getFilter();

    /**
     * Returns the magnitude of the faintest star that could be seen during observation time with the unaided eye. Might
     * return <code>Float.NaN</code> if no value was set at all.
     *
     * @return The magnitude of the faintest star as float value, or <code>Float.NaN</code> if no value was set.
     */
    float getFaintestStar();

    /**
     * Returns the magnification used for this observation. Might return <code>Float.NaN</code> if no value was set at
     * all.
     *
     * @return The magnification or <code>Float.NaN</code> if no value was set.
     */
    float getMagnification();

    /**
     * Returns a list of images (relativ path to images), taken at this observation. Might return <code>null</code> if
     * images were set.
     *
     * @return List of images or <code>null</code> if no images were set.
     */
    List<String> getImages();

    /**
     * Returns the seeing during this observation.<br>
     * Values can reach from 1 to 5, where 1 is best seeing and 5 the worst seeing.<br>
     * Might return <code>-1</code> if no value was set at all.
     *
     * @return A int between 1-5 representing the seeing, or <code>-1</code> if no value was set for seeing.
     */
    int getSeeing();

    /**
     * Returns the observer who made the observation.<br>
     *
     * @return The observer who made this observation.
     */
    IObserver getObserver();

    /**
     * Returns a List with one or more results of the observation.<br>
     * Every observation has at least one result.
     *
     * @return A List containing the results of the observation.
     */
    List<IFinding> getResults();

    /**
     * Returns the scope that was used for the observation.<br>
     * Might return <code>null</code> if the observation was not made with any scope.
     *
     * @return The scope which was used for the observation, or <code>null</code> if no scope was used at all.
     */
    IScope getScope();

    /**
     * Returns the session this observation belongs to.<br>
     * Might return <code>null</code> if the observation is not part of any observation session.
     *
     * @return The session this observation belongs to, or <code>null</code> if the observation does not belong to any
     *         session.
     */
    ISession getSession();

    /**
     * Returns the site where the observation took place.<br>
     *
     * @return The site of the observation.
     */
    ISite getSite();

    /**
     * Returns the target which was observed.<br>
     *
     * @return The target which was observed.
     */
    ITarget getTarget();

    /**
     * Returns the imager that was used for this observation.<br>
     *
     * @return The imager used at this observation.
     */
    IImager getImager();

    /**
     * Sets a single IFinding as result for this observation.<br>
     * The old list of results will be overwritten. If you want to add one ore more results to the existing ones use
     * addResults(java.util.List) or addResult(IFinding).<br>
     * If the passed IFinding was successfully attached to this observation, the method will return <b>true</b>. <br>
     * If the passed IFinding is <code>null</code>, an IllegalArgumentException is thrown.
     *
     * @param results
     *            A new result for this observation
     * @see de.lehmannet.om.IObservation#addResults(java.util.List results)
     * @see de.lehmannet.om.IObservation#addResult(IFinding result)
     * @see de.lehmannet.om.IObservation#setResults(List results)
     * @see de.lehmannet.om.IFinding
     * @throws IllegalArgumentException
     *             if the new result is <code>null</code>
     */
    void setResult(IFinding result) throws IllegalArgumentException;

    /**
     * Sets a List of results for this observation.<br>
     * The old list of results will be overwritten. If you want to add one ore more results to the existing ones use
     * addResults(java.util.List) or addResult(IFinding).<br>
     * If the new list of results was successfully attached to this observation, the method will return <b>true</b>. If
     * one of the elements in the list does not implement the IFinding interface <b>false</b> is returned.<br>
     * If the new list is empty or <code>null</code>, an IllegalArgumentException is thrown.
     *
     * @param results
     *            The new list of results for this observation
     * @return <b>true</b> if the given list was successfully attached to this observation. <b>false</b> if one of the
     *         new result elements in the list did not implement the the IFinding interface.
     * @see de.lehmannet.om.IObservation#addResults(java.util.List results)
     * @see de.lehmannet.om.IObservation#addResult(IFinding result)
     * @see de.lehmannet.om.IFinding
     * @throws IllegalArgumentException
     *             if new results list is <code>null</code> or empty
     */
    boolean setResults(List<IFinding> results) throws IllegalArgumentException;

    /**
     * Adds a List of results for this observation.<br>
     * The new list of results will be added to the existing list of results belonging to this observation. If you want
     * to replace the old result list use setResults(java.util.List).<br>
     * If the new list of results was successfully added to the old result list, the method will return <b>true</b>. If
     * the list is empty or <code>null</code>, the old result list will remain unchanged.
     *
     * @param results
     *            A list with more results for this observation
     * @return <b>true</b> if the given list was successfully added to this observations result list. <b>false</b> if
     *         the new list could not be added and the old list remains unchanged.
     * @see de.lehmannet.om.IObservation#setResults(java.util.List results)
     */
    boolean addResults(List<IFinding> results);

    /**
     * Adds a new result to this observation.<br>
     *
     * @param result
     *            A new result for this observation
     */
    void addResult(IFinding result);

    /**
     * Adds a List of image paths (String) to this observation.<br>
     * The new list of images will be added to the existing list of images belonging to this observation. If you want to
     * replace the old images list use setImages(java.util.List).<br>
     * If the new list of images was successfully added to the old images list, the method will return <b>true</b>. If
     * the list is empty or <code>null</code>, the old result list will remain unchanged.
     *
     * @param images
     *            A list (containing Strings) with additional images (path) for this observation
     * @return <b>true</b> if the given list was successfully added to this observations images list. <b>false</b> if
     *         the new list could not be added and the old list remains unchanged.
     * @see de.lehmannet.om.IObservation#setResults(java.util.List images)
     */
    boolean addImages(List<String> images);

    /**
     * Adds a new image (path) to this observation.<br>
     *
     * @param imagePath
     *            A new image for this observation
     */
    void addImage(String imagePath);

    /**
     * Sets a List of images (path as String) for this observation.<br>
     * The old list of images will be overwritten. If you want to add one ore more images to the existing ones use
     * addImages(java.util.List) or addImage(String).<br>
     * If the new list of images was successfully attached to this observation, the method will return <b>true</b>. If
     * one of the elements in the list isn't a java.lang.String object <b>false</b> is returned.<br>
     * If the new list is empty or <code>null</code>, an IllegalArgumentException is thrown.
     *
     * @param imagesList
     *            The new (String) list of images for this observation
     * @see de.lehmannet.om.IObservation#addImages(java.util.List images)
     * @see de.lehmannet.om.IObservation#addImage(String image)
     * @throws IllegalArgumentException
     *             if new image list is <code>null</code> or empty
     */
    void setImages(List<String> imagesList) throws IllegalArgumentException;

    /**
     * Sets an imager used for this observation.<br>
     *
     * @param imager
     *            The imager used for this observation
     */
    void setImager(IImager imager);

    /**
     * Sets the start date of the observation.<br>
     * The start date is a mandatory field, as the end date is not.
     *
     * @param begin
     *            The start date of the observation
     * @throws IllegalArgumentException
     *             if new begin date is <code>null</code>
     */
    void setBegin(OffsetDateTime begin) throws IllegalArgumentException;

    /**
     * Sets the end date of the observation.<br>
     * The end date is an optional field, as for example old observations might not have an precise end date.
     *
     * @param end
     *            The end date of the observation
     */
    void setEnd(OffsetDateTime end);

    /**
     * Sets the eyepiece of the observation.<br>
     * If there was already an eyepiece attached to this observation, the old one will be replaced with the new one.
     *
     * @param eyepiece
     *            The eyepiece of the observation
     */
    void setEyepiece(IEyepiece eyepiece);

    /**
     * Sets the lens of the observation.<br>
     * If there was already an lens attached to this observation, the old one will be replaced with the new one.
     *
     * @param lens
     *            The lens of the observation
     */
    void setLens(ILens lens);

    /**
     * Sets the filter used during this observation.<br>
     * If there was already an filter attached to this observation, the old one will be replaced with the new one.
     *
     * @param filter
     *            The filter used during this observation
     * @since 1.5
     */
    void setFilter(IFilter filter);

    /**
     * Sets the accessories used during the observation.<br>
     * If there was already an accessories list attached to this observation, the old one will be replaced with the new
     * one.
     *
     * @param accessories
     *            The accessories of the observation
     */
    void setAccessories(String accessories);

    /**
     * Sets the magnitude value of the faintest star which could be seen with the unaided eye during the
     * observation.<br>
     * If there was already a value set for this observation, the old one will be replaced with the new one.
     *
     * @param faintestStar
     *            The faintestStar of the observation in magnitude
     */
    void setFaintestStar(float faintestStar);

    /**
     * Sets the sky quality meter value that was determined during the observation.<br>
     * If there was already a value set for this observation, the old one will be replaced with the new one.
     *
     * @param sq
     *            The sky quality meter value
     */
    void setSkyQuality(SurfaceBrightness sq);

    /**
     * Sets the magnification used at the observation.<br>
     * If there was already a value set for this observation, the old one will be replaced with the new one. In case a
     * zoom eyepiece was used for the observation, this value conatais th actual focalLength used. (And also the lens
     * focal length factor if used)<br>
     * Example:<br>
     * scope had a focal length of 1114mm<br>
     * zoomeyepiece used a focal length of 10mm<br>
     * lens (barlow) used had a focal length factor of 2x<br>
     * Magnification set here must be:<br>
     * 1114*2/10 = 222,8
     *
     * @param magnification
     *            The magnification used at the observation
     */
    void setMagnification(float magnification);

    /**
     * Sets the seeing during this observation.<br>
     * Values can reach from 1 to 5, where 1 is best seeing and 5 the worst seeing.<br>
     *
     * @param seeing
     *            A int between 1-5 representing the seeing
     */
    void setSeeing(int seeing) throws IllegalArgumentException;

    /**
     * Sets the observer of the observation.<br>
     * If there was already an observer attached to this observation, the old one will be replaced with the new one.
     *
     * @param observer
     *            The observer of the observation
     * @throws IllegalArgumentException
     *             if new observer is <code>null</code>
     */
    void setObserver(IObserver observer) throws IllegalArgumentException;

    /**
     * Sets the scope of the observation.<br>
     * If there was already a scope attached to this observation, the old one will be replaced with the new one.
     *
     * @param scope
     *            The scope of the observation
     */
    void setScope(IScope scope);

    /**
     * Sets the session which this observation belongs to.<br>
     * This observations start date must be between the sessions start and end date.<br>
     * If there was already a session attached to this observation, the old one will be replaced with the new one.
     *
     * @param session
     *            The session this observation belongs to
     * @throws IllegalArgumentException
     *             if this observations start date is not between the session start and end date
     */
    void setSession(ISession session) throws IllegalArgumentException;

    /**
     * Sets the site where this observation took place.<br>
     * If there was already a site attached to this observation, the old one will be replaced with the new one.
     *
     * @param site
     *            The site this observation took place
     * @throws IllegalArgumentException
     *             if new site is <code>null</code>
     */
    void setSite(ISite site) throws IllegalArgumentException;

    /**
     * Sets the target of this observation.<br>
     * If there was already a target attached to this observation, the old one will be replaced with the new one.
     *
     * @param target
     *            The target of this observation
     * @throws IllegalArgumentException
     *             if new target is <code>null</code>
     */
    void setTarget(ITarget target) throws IllegalArgumentException;
}
