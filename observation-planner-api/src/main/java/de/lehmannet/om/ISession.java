/* ====================================================================
 * /ISession.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om;

import java.util.List;

import org.w3c.dom.Element;

/**
 * An ISession can be used to link several observations together. Typically a session would describe an observation
 * night, where several observations took place. Therefore an ISession requires two mandatory fields: a start date and
 * an end date. All observations of the session should have a start date that is inbetween the sessions start and end
 * date.
 * 
 * @author doergn@users.sourceforge.net
 * @since 1.0
 */
public interface ISession extends ISchemaElement {

    // ---------
    // Constants ---------------------------------------------------------
    // ---------

    /**
     * Constant for XML representation: Sessions element name.<br>
     * Example:<br>
     * &lt;session&gt;<i>More stuff goes here</i>&lt;/session&gt;
     */
    String XML_ELEMENT_SESSION = "session";

    /**
     * Constant for XML representation: Sessions start date element name.<br>
     * Example:<br>
     * &lt;session&gt; <br>
     * <i>More stuff goes here</i> &lt;begin&gt;<code>Sessions start date goes here</code>&lt;/begin&gt; <i>More stuff
     * goes here</i> &lt;/session&gt;
     */
    String XML_ELEMENT_BEGIN = "begin";

    /**
     * Constant for XML representation: Sessions end date element name.<br>
     * Example:<br>
     * &lt;session&gt; <br>
     * <i>More stuff goes here</i> &lt;end&gt;<code>Sessions end date goes here</code>&lt;/end&gt; <i>More stuff goes
     * here</i> &lt;/session&gt;
     */
    String XML_ELEMENT_END = "end";

    /**
     * Constant for XML representation: Sessions site element name.<br>
     * Example:<br>
     * &lt;session&gt; <br>
     * <i>More stuff goes here</i> &lt;site&gt;<code>Sessions site goes here</code>&lt;/site&gt; <i>More stuff goes
     * here</i> &lt;/session&gt;
     */
    String XML_ELEMENT_SITE = "site";

    /**
     * Constant for XML representation: Sessions coObserver element name.<br>
     * Example:<br>
     * &lt;session&gt; <br>
     * <i>More stuff goes here</i> &lt;coObserver&gt;<code>Sessions coObserver goes here</code>&lt;/coObserver&gt;
     * <i>More stuff goes here</i> &lt;/session&gt;
     */
    String XML_ELEMENT_COOBSERVER = "coObserver";

    /**
     * Constant for XML representation: Sessions weather element name.<br>
     * Example:<br>
     * &lt;session&gt; <br>
     * <i>More stuff goes here</i> &lt;weather&gt;<code>Sessions weather goes here</code>&lt;/weather&gt; <i>More stuff
     * goes here</i> &lt;/session&gt;
     */
    String XML_ELEMENT_WEATHER = "weather";

    /**
     * Constant for XML representation: Sessions equipment element name.<br>
     * Example:<br>
     * &lt;session&gt; <br>
     * <i>More stuff goes here</i> &lt;equipment&gt;<code>Sessions equipment goes here</code>&lt;/equipment&gt; <i>More
     * stuff goes here</i> &lt;/session&gt;
     */
    String XML_ELEMENT_EQUIPMENT = "equipment";

    /**
     * Constant for XML representation: Sessions comments element name.<br>
     * Example:<br>
     * &lt;session&gt; <br>
     * <i>More stuff goes here</i> &lt;comments&gt;<code>Sessions comments go here</code>&lt;/comments&gt; <i>More stuff
     * goes here</i> &lt;/session&gt;
     */
    String XML_ELEMENT_COMMENTS = "comments";

    /**
     * Constant for XML representation: language attribute<br>
     * Since COMAST 1.5 it is possible to add a language description to a session element. This language description
     * give the language in which all session related entrys were made.<br>
     * The value is given as ISO String. (E.g. de=German, fr=Frensh, ...)<br>
     * Example:<br>
     * &lt;session id=&quot;someID&quot; lang=&quot;someISOString&quot;&gt;<br>
     * <br>
     * <i>More stuff goes here</i> &lt;/session&gt;
     * 
     * @since 1.5
     */
    String XML_ELEMENT_ATTRIBUTE_LANGUAGE = "lang";

    /**
     * Constant for XML representation: Image made during the session.<br>
     * Example:<br>
     * &lt;session&gt; <br>
     * <i>More stuff goes here</i> &lt;image&gt;<code>Relative path to image</code>&lt;/image&gt; <i>More stuff goes
     * here</i> &lt;/session&gt;
     */
    String XML_ELEMENT_IMAGE = "image";

    // --------------
    // Public Methods ----------------------------------------------------
    // --------------

/**
     * Adds this Session to a given parent XML DOM Element. The Session element will be set as a child element of the
     * passed element.
     * 
     * @return Returns the element given as parameter with this Session as child element.<br>
     *         Might return <code>null</code> if parent was <code>null</code>.
     * @see org.w3c.dom.Element
     */
    Element addToXmlElement(Element element);

/**
     * Adds the session link to an given XML DOM Element The session element itself will be attached to given elements
     * ownerDocument if the passed boolean is <cod>true</code>. If the ownerDocument has no session container, it will
     * be created (in case the passed boolean is <cod>true</code>).<br>
     * Example:<br>
     * &lt;parameterElement&gt;<br>
     * <b>&lt;sessionLink&gt;123&lt;/sessionLink&gt;</b><br>
     * &lt;/parameterElement&gt;<br>
     * <i>More stuff of the xml document goes here</i><br>
     * <b>&lt;sessionContainer&gt;</b><br>
     * <b>&lt;session id="123"&gt;</b><br>
     * <i>session description goes here</i><br>
     * <b>&lt;/session&gt;</b><br>
     * <b>&lt;/sessionContainer&gt;</b><br>
     * <br>
     * 
     * @param addElementToContainer
     *            if <code>true</code> it's ensured that the linked element exists in the corresponding container
     *            element. Please note, passing <code>true</code> slowes down XML serialization.
     * @return Returns the Element given as parameter with a additional Session link, and the session element under the
     *         session container of the ownerDocument Might return <code>null</code> if element was <code>null</code>.
     * @see org.w3c.dom.Element
     * @since 2.0
     */
    org.w3c.dom.Element addAsLinkToXmlElement(org.w3c.dom.Element parent, boolean addElementToContainer);

/**
     * Adds the session link to an given XML DOM Element The session element itself will <b>NOT</b> be attached to given
     * elements ownerDocument. Calling this method is equal to calling <code>addAsLinkToXmlElement</code> with
     * parameters <code>element, false</code><br>
     * Example:<br>
     * &lt;parameterElement&gt;<br>
     * <b>&lt;sessionLink&gt;123&lt;/sessionLink&gt;</b><br>
     * &lt;/parameterElement&gt;<br>
     * <br>
     * 
     * @param element
     *            The element under which the the session link is created
     * @return Returns the Element given as parameter with a additional session link Might return <code>null</code> if
     *         element was <code>null</code>.
     * @see org.w3c.dom.Element
     */
    org.w3c.dom.Element addAsLinkToXmlElement(Element element);

/**
     * Returns the start date of the session.<br>
     * 
     * @return Returns the start date of the session
     */
    java.util.Calendar getBegin();

/**
     * Returns a List of coobservers who joined this session.<br>
     * Might return <code>null</code> if no coobservers were added to this session.
     * 
     * @return Returns a List of coobserver or <code>null</code> if coobservers were never added.
     */
    java.util.List<IObserver> getCoObservers();

/**
     * Returns a comment about this session.<br>
     * Might return <code>null</code> if no comment was set to this session.
     * 
     * @return Returns a comment about this session or <code>null</code> if no comment was set at all.
     */
    String getComments();

/**
     * Returns the end date of the session.<br>
     * 
     * @return Returns the end date of the session
     */
    java.util.Calendar getEnd();

/**
     * Returns the site of the session.<br>
     * 
     * @return Returns the site of the session
     */
    ISite getSite();

/**
     * Returns a string describing equipment which was used during this session.<br>
     * Typically one should add non optical equipment here like "Radio and a warm bottle of Tea."<br>
     * Might return <code>null</code> if no equipment was set to this session.
     * 
     * @return Returns string describing some equipment which was used during the session or <code>null</code> if no
     *         additional equipment was used at all.
     */
    String getEquipment();

/**
     * Returns a describtion of the weather conditions during the session.<br>
     * Might return <code>null</code> if no weather conditions were addedt to this session.
     * 
     * @return Returns a describtion of the weather conditions during the session or <code>null</code> if no weather
     *         conditions were added at all.
     */
    String getWeather();

/**
     * Returns the language in which this session is described as ISO language string. E.g. de=German, fr=French,
     * ...<br>
     * Might return <code>null</code> if no language was set for this session.
     * 
     * @return Returns a ISO language code that represents the sessions describtion language or <code>null</code> if no
     *         language was set at all.
     * @since 1.5
     */
    String getLanguage();

/**
     * Sets the start date of the session.<br>
     * 
     * @param begin
     *            The new start date of the session.
     * @throws IllegalArgumentException
     *             if new start date is <code>null</code>
     */
    void setBegin(java.util.Calendar begin) throws IllegalArgumentException;

/**
     * Sets the end date of the session.<br>
     * 
     * @param end
     *            The new end date of the session.
     * @throws IllegalArgumentException
     *             if new end date is <code>null</code>
     */
    void setEnd(java.util.Calendar end) throws IllegalArgumentException;

/**
     * Sets a site (location) where the session took place.<br>
     * A session can only took place at one site.
     * 
     * @param site
     *            The site where the session took place.
     * @throws IllegalArgumentException
     *             if site is <code>null</code>
     */
    void setSite(ISite site) throws IllegalArgumentException;

/**
     * Sets a comment to the session.<br>
     * The old comment will be overwritten.
     * 
     * @param comments
     *            A new comment for the session
     */
    void setComments(String comments);

/**
     * Sets a equipment description to the session.<br>
     * Typically non optical equipment will should be stored here, e.g. "Red LED light and bottle of hot tea."<br>
     * The old equipment will be overwritten.
     * 
     * @param equipment
     *            The new equipment of the session
     */
    void setEquipment(String equipment);

/**
     * Sets a new List of coobservers to this session.<br>
     * The old List of coobservers will be overwritten. If you want to add one ore more coobservers to the existing list
     * use addCoObservers(java.util.List) or addCoObserver(IObserver) instead.
     * 
     * @param coObservers
     *            The new List of coobservers of the session
     */
    void setCoObservers(List coObservers);

/**
     * Adds a List of coobservers to this session.<br>
     * The old List of coobservers will be extended by the new List of coobservers.
     * 
     * @param coObservers
     *            A List of coobservers which will be added to the existing List of coobservers which is stored in the
     *            session
     * @return <b>true</b> if the list could be added to the existing list, <b>false</b> if the operation fails, because
     *         e.g. one of the lists elements does not implement the IObserver interface. If <b>false</b> is returned
     *         the existing list is not changed at all.
     */
    boolean addCoObservers(java.util.List coObservers);

/**
     * Adds a single coobserver to this session.<br>
     * 
     * @param coObserver
     *            A new coobserver who will be addded to the List of coobservers
     */
    void addCoObserver(IObserver coObserver);

/**
     * Sets the weather conditions of the session.<br>
     * The weather conditions string should explain in some short sentences, how the weather conditions were like during
     * the session. E.g. "Small clouds at the first hour but then totally clear and cool, at about 4\u00b0C."
     * 
     * @param weather
     *            A string describing the weather conditions during the session
     */
    void setWeather(String weather);

/**
     * Sets the language in which this session is described. String must be given as ISO language string. E.g.
     * de=German, fr=French, ...<br>
     * 
     * @param language
     *            ISO language string
     * @since 1.5
     */
    void setLanguage(String language);

/**
     * Sets a List of images (path as String) for this session.<br>
     * The old list of images will be overwritten. If you want to add one ore more images to the existing ones use
     * addImages(java.util.List) or addImage(String).<br>
     * If the new list of images was successfully attached to this session, the method will return <b>true</b>. If one
     * of the elements in the list isn't a java.lang.String object <b>false</b> is returned.<br>
     * If the new list is <code>null</code>, an IllegalArgumentException is thrown.
     * 
     * @param imagesList
     *            The new (String) list of images for this session
     * @see de.lehmannet.om.ISession#addImages(java.util.List images)
     * @see de.lehmannet.om.ISession#addImage(String image)
     * @throws IllegalArgumentException
     *             if new image list is <code>null</code>
     */
    void setImages(List imagesList) throws IllegalArgumentException;

/**
     * Adds a new image (path) to this session.<br>
     * 
     * @param imagePath
     *            A new image for this session
     */
    void addImage(String imagePath);

/**
     * Adds a List of image paths (String) to this session.<br>
     * The new list of images will be added to the existing list of images belonging to this session. If you want to
     * replace the old images list use setImages(java.util.List).<br>
     * If the new list of images was successfully added to the old images list, the method will return <b>true</b>. If
     * the list is empty or <code>null</code>, the old result list will remain unchanged.
     * 
     * @param images
     *            A list (containing Strings) with additional images (path) for this session
     * @return <b>true</b> if the given list was successfully added to this session images list. <b>false</b> if the new
     *         list could not be added and the old list remains unchanged.
     */
    boolean addImages(List images);

/**
     * Returns a list of images (relativ path to images), taken at this session. Might return <code>null</code> if
     * images were set.
     * 
     * @return List of images or <code>null</code> if no images were set.
     */
    List getImages();

}
