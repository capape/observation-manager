/*
 * ====================================================================
 * /ISite.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om;

import org.w3c.dom.Element;

/**
 * An ISite describes an observation site where an observation took place.<br>
 * A site can be identified by its latitude and longitude values, but as for processing reasons its implementation
 * should have more mandatory fields, such as elevation and timezone.
 *
 * @author doergn@users.sourceforge.net
 * @since 1.0
 */
public interface ISite extends ISchemaElement, ICloneable {

    // ---------
    // Constants ---------------------------------------------------------
    // ---------

    /**
     * Constant for XML representation: Site element name.<br>
     * Example:<br>
     * &lt;site&gt;<i>More stuff goes here</i>&lt;/site&gt;
     */
    String XML_ELEMENT_SITE = "site";

    /**
     * Constant for XML representation: Site name element name.<br>
     * Example:<br>
     * &lt;site&gt; <br>
     * <i>More stuff goes here</i> &lt;name&gt;<code>Site name goes here</code>&lt;/name&gt; <i>More stuff goes here</i>
     * &lt;/site&gt;
     */
    String XML_ELEMENT_NAME = "name";

    /**
     * Constant for XML representation: Site longitude element name.<br>
     * Example:<br>
     * &lt;site&gt; <br>
     * <i>More stuff goes here</i> &lt;longitude&gt;<code>Site longitude goes here</code>&lt;/longitude&gt; <i>More
     * stuff goes here</i> &lt;/site&gt;
     */
    String XML_ELEMENT_LONGITUDE = "longitude";

    /**
     * Constant for XML representation: Site latitude element name.<br>
     * Example:<br>
     * &lt;site&gt; <br>
     * <i>More stuff goes here</i> &lt;latitude&gt;<code>Site latitude goes here</code>&lt;/latitude&gt; <i>More stuff
     * goes here</i> &lt;/site&gt;
     */
    String XML_ELEMENT_LATITUDE = "latitude";

    /**
     * Constant for XML representation: Site elevation element name.<br>
     * Example:<br>
     * &lt;site&gt; <br>
     * <i>More stuff goes here</i> &lt;elevation&gt;<code>Site elevation goes here</code>&lt;/elevation&gt; <i>More
     * stuff goes here</i> &lt;/site&gt;
     */
    String XML_ELEMENT_ELEVATION = "elevation";

    /**
     * Constant for XML representation: Site timezone element name.<br>
     * Example:<br>
     * &lt;site&gt; <br>
     * <i>More stuff goes here</i> &lt;timezone&gt;<code>Sites timezone goes here</code>&lt;/timezone&gt; <i>More stuff
     * goes here</i> &lt;/site&gt;
     */
    String XML_ELEMENT_TIMEZONE = "timezone";

    /**
     * Constant for XML representation: Site IAU code element name.<br>
     * Example:<br>
     * &lt;site&gt; <br>
     * <i>More stuff goes here</i> &lt;code&gt;<code>Sites IAU code goes here</code>&lt;/code&gt; <i>More stuff goes
     * here</i> &lt;/site&gt;
     */
    String XML_ELEMENT_IAUCODE = "code";

    // --------------
    // Public Methods ----------------------------------------------------
    // --------------

    /**
     * Adds this Site to a given parent XML DOM Element. The Site element will be set as a child element of the passed
     * element.
     *
     * @param parent
     *            The parent element for this Site
     * @see org.w3c.dom.Element
     */
    void addToXmlElement(Element element);

    /**
     * Adds the site link to an given XML DOM Element The site element itself will be attached to given elements
     * ownerDocument if the passed boolean value is <code>true</code>. Also if the ownerDocument has no site container,
     * it will be created (if the passed boolean value is <code>true</code> <br>
     * Example:<br>
     * &lt;parameterElement&gt;<br>
     * <b>&lt;siteLink&gt;123&lt;/siteLink&gt;</b><br>
     * &lt;/parameterElement&gt;<br>
     * <i>More stuff of the xml document goes here</i><br>
     * <b>&lt;siteContainer&gt;</b><br>
     * <b>&lt;site id="123"&gt;</b><br>
     * <i>site description goes here</i><br>
     * <b>&lt;/site&gt;</b><br>
     * <b>&lt;/siteContainer&gt;</b><br>
     * <br>
     *
     * @param parent
     *            The element under which the the site link is created
     * @param addElementToContainer
     *            if <code>true</code> it's ensured that the linked element exists in the corresponding container
     *            element. Please note, passing <code>true</code> slowes down XML serialization.
     * @return Returns the Element given as parameter with a additional site link, and the site element under the site
     *         container of the ownerDocument Might return <code>null</code> if element was <code>null</code>.
     * @see org.w3c.dom.Element
     * @since 2.0
     */
    org.w3c.dom.Element addAsLinkToXmlElement(org.w3c.dom.Element parent, boolean addElementToContainer);

    /**
     * Adds the site link to an given XML DOM Element The site element itself will <b>NOT</b> be attached to given
     * elements ownerDocument. Calling this method is equal to calling <code>addAsLinkToXmlElement</code> with
     * parameters <code>element, false</code><br>
     * Example:<br>
     * &lt;parameterElement&gt;<br>
     * <b>&lt;siteLink&gt;123&lt;/siteLink&gt;</b><br>
     * &lt;/parameterElement&gt;<br>
     * <br>
     *
     * @param element
     *            The element under which the the site link is created
     * @return Returns the Element given as parameter with a additional site link Might return <code>null</code> if
     *         element was <code>null</code>.
     * @see org.w3c.dom.Element
     */
    org.w3c.dom.Element addAsLinkToXmlElement(Element element);

    /**
     * Returns the latitude of the site.<br>
     * The latitude is a positiv angle if its north of the equator, and negative if south of the equator.
     *
     * @return Returns an Angle with the geographical latitude
     */
    de.lehmannet.om.Angle getLatitude();

    /**
     * Returns the longitude of the site.<br>
     * The longitude is a positiv angle if its east of Greenwich, and negative if west of Greenwich.
     *
     * @return Returns an Angle with the geographical longitude
     */
    de.lehmannet.om.Angle getLongitude();

    /**
     * Returns the name of the site.<br>
     * The name may be any string describing the site as precise as it can be.
     *
     * @return Returns the name of the site
     */
    String getName();

    /**
     * Returns the IAU station code of the site.<br>
     * This method may return <code>null</code> as the site may not have an IAU (International Astronomical Union)
     * station code.
     *
     * @return Returns the IAU code of the site, or <code>null</code> if no code exists, or was never set.
     */
    String getIAUCode();

    /**
     * Returns the timezone of the site.<br>
     * The timezone is given as positiv or negative value, depending on the sites timezone difference to the GMT in
     * minutes.
     *
     * @return Returns timzone offset (in comparism to GMT) in minutes
     */
    int getTimezone();

    /**
     * Returns the elevation of the site.<br>
     * The elevation is given in meters above/under sea level.
     *
     * @return Returns the sites elevation in meters above or under sea level, or <code>NULL</code> if value was never
     *         set
     */
    float getElevation();

    /**
     * Sets the elevation of the site.<br>
     * The elevation should be given in meters above/under sea level.
     *
     * @param elevation
     *            The new elevation for this site
     */
    void setElevation(float elevation);

    /**
     * Sets the latitude of the site.<br>
     * The latitude must be a positiv angle if its north of the equator, and negative if south of the equator.
     *
     * @param latitude
     *            The new latitude for this site
     * @throws IllegalArgumentException
     *             if latitude is <code>null</code>
     */
    void setLatitude(de.lehmannet.om.Angle latitude) throws IllegalArgumentException;

    /**
     * Sets the longitude of the site.<br>
     * The longitude must be a positiv angle if its east of Greenwich, and negative if west of Greenwich.
     *
     * @param longitude
     *            The new longitude for this site
     * @throws IllegalArgumentException
     *             if longitude is <code>null</code>
     */
    void setLongitude(de.lehmannet.om.Angle longitude) throws IllegalArgumentException;

    /**
     * Sets the name of the site.<br>
     * The name should be any string describing the site as precise as it can be.
     *
     * @param name
     *            The new name for this site
     * @throws IllegalArgumentException
     *             if name is <code>null</code>
     */
    void setName(String name) throws IllegalArgumentException;

    /**
     * Sets the IAU code of the site.<br>
     *
     * @param IAUCode
     *            The new IAU code for this site
     */
    void setIAUCode(String IAUCode);

    /**
     * Sets the timezone of the site.<br>
     * The timezone must be given as positiv or negative value, depending on the sites timezone difference to the GMT in
     * minutes.
     *
     * @param timezone
     *            The new timezone for this site in minutes
     * @throws IllegalArgumentException
     *             if new timezone is greater than 720 (12*60) or lower than -720 (12*60)
     */
    void setTimezone(int timezone) throws IllegalArgumentException;

}
