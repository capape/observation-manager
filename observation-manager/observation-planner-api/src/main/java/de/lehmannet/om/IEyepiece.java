/* ====================================================================
 * /IEyepiece.java
 *
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om;

import org.w3c.dom.Element;

/**
 * An IEyepiece describes a optical eyepiece. The model name and the focalLength are mandatory fields which have to be
 * set.
 *
 * @author doergn@users.sourceforge.net
 *
 * @since 1.0
 */
public interface IEyepiece extends ISchemaElement, IEquipment {

    // ---------
    // Constants ---------------------------------------------------------
    // ---------

    /**
     * Constant for XML representation: eyepiece element name
     */
    String XML_ELEMENT_EYEPIECE = "eyepiece";

    /**
     * Constant for XML representation: model element name
     *
     * Example:<br>
     * &lt;eyepiece&gt; <br>
     * <i>More stuff goes here</i> &lt;model&gt;<code>Model name goes here</code>&lt;/model&gt; <i>More stuff goes
     * here</i> &lt;/eyepiece&gt;
     */
    String XML_ELEMENT_MODEL = "model";

    /**
     * Constant for XML representation: vendor element name
     *
     * Example:<br>
     * &lt;eyepiece&gt; <br>
     * <i>More stuff goes here</i> &lt;vendor&gt;<code>Vendor name goes here</code>&lt;/vendor&gt; <i>More stuff goes
     * here</i> &lt;/eyepiece&gt;
     */
    String XML_ELEMENT_VENDOR = "vendor";

    /**
     * Constant for XML representation: focalLength element name
     *
     * Example:<br>
     * &lt;eyepiece&gt; <br>
     * <i>More stuff goes here</i> &lt;focalLength&gt;<code>Focal length goes here</code>&lt;/focalLength&gt; <i>More
     * stuff goes here</i> &lt;/eyepiece&gt;
     */
    String XML_ELEMENT_FOCALLENGTH = "focalLength";

    /**
     * Constant for XML representation: maximal focalLength element name
     *
     * Example:<br>
     * &lt;eyepiece&gt; <br>
     * <i>More stuff goes here</i>
     * &lt;maxFocalLength&gt;<code>Maximal Focal length goes here</code>&lt;/maxFocalLength&gt; <i>More stuff goes
     * here</i> &lt;/eyepiece&gt;
     */
    String XML_ELEMENT_MAXFOCALLENGTH = "maxFocalLength";

    /**
     * Constant for XML representation: apparent field of view element name
     *
     * Example:<br>
     * &lt;eyepiece&gt; <br>
     * <i>More stuff goes here</i> &lt;apparentFOV&gt;<code>apparent field of view goes here</code>&lt;/apparentFOV&gt;
     * <i>More stuff goes here</i> &lt;/eyepiece&gt;
     */
    String XML_ELEMENT_APPARENTFOV = "apparentFOV";

    // --------------
    // Public methods ----------------------------------------------------
    // --------------

    /**
     * Adds this Eyepiece to a given parent XML DOM Element. The Eyepiece element will be set as a child element of the
     * passed element.
     *
     * @param parent
     *            The parent element for this Eyepiece
     *
     * @see org.w3c.dom.Element
     */
    void addToXmlElement(Element element);

    /**
     * Adds the eyepiece link to an given XML DOM Element The eyepiece element itself will be attached to given elements
     * ownerDocument if the passed boolean was <code>true</code>. If the ownerDocument has no eyepiece container, it
     * will be created (in case the passed boolean was <code>true</code>).<br>
     * Example:<br>
     * &lt;parameterElement&gt;<br>
     * <b>&lt;eyepieceLink&gt;123&lt;/eyepieceLink&gt;</b><br>
     * &lt;/parameterElement&gt;<br>
     * <i>More stuff of the xml document goes here</i><br>
     * <b>&lt;eyepieceContainer&gt;</b><br>
     * <b>&lt;eyepiece id="123"&gt;</b><br>
     * <i>eyepiece description goes here</i><br>
     * <b>&lt;/eyepiece&gt;</b><br>
     * <b>&lt;/eyepieceContainer&gt;</b><br>
     * <br>
     *
     * @param element
     *            The element under which the the eyepiece link is created
     * @param addElementToContainer
     *            if <code>true</code> it's ensured that the linked element exists in the corresponding container
     *            element. Please note, passing <code>true</code> slowes down XML serialization.
     *
     * @return Returns the Element given as parameter with a additional eyepiece link, and the eyepiece element under
     *         the eyepiece container of the ownerDocument Might return <code>null</code> if element was
     *         <code>null</code>.
     *
     * @see org.w3c.dom.Element
     *
     * @since 2.0
     */
    Element addAsLinkToXmlElement(Element element, boolean addElementToContainer);

    /**
     * Adds the eyepiece link to an given XML DOM Element The eyepiece element itself will <b>NOT</b> be attached to
     * given elements ownerDocument. Calling this method is equal to calling <code>addAsLinkToXmlElement</code> with
     * parameters <code>element, false</code><br>
     * Example:<br>
     * &lt;parameterElement&gt;<br>
     * <b>&lt;eyepieceLink&gt;123&lt;/eyepieceLink&gt;</b><br>
     * &lt;/parameterElement&gt;<br>
     * <br>
     *
     * @param element
     *            The element under which the the eyepiece link is created
     *
     * @return Returns the Element given as parameter with a additional eyepiece link Might return <code>null</code> if
     *         element was <code>null</code>.
     *
     * @see org.w3c.dom.Element
     */
    Element addAsLinkToXmlElement(Element element);

    /**
     * Returns the apparent field of view of this eyepiece.
     *
     * @return Returns the apparent field of view of this eyepiece. The Angles value cannot be negative or 0.<br>
     *         If <code>null</code> is returned the apparent field of view value was never set.
     *
     * @see de.lehmannet.om.Angle
     */
    de.lehmannet.om.Angle getApparentFOV();

    /**
     * Returns the focal length of this eyepiece. The focal length of the telescope divided by the focal length of the
     * eyepiece equals the amplification.
     *
     * @return Returns the focal length of the eyepiece.
     */
    float getFocalLength();

    /**
     * Returns the maximal focal length of this eyepiece in case this eyepiece is a zoom eyepiece. Might return
     * <code>Float.NaN</code> in case this eyepiece is not a zoom eyepiece.
     *
     * @return Returns the maximal focal length of the eyepiece.
     *
     * @since 1.7
     */
    float getMaxFocalLength();

    /**
     * Returns the model name of the eyepiece.<br>
     *
     * @return Returns a String representing the eyepieces model name.<br>
     *         If <code>null</code> is returned the model name was never set.
     */
    String getModel();

    /**
     * Returns the vendor name of the eyepiece.<br>
     *
     * @return Returns a String representing the eyepieces vendor name.<br>
     *         If <code>null</code> is returned the vendor name was never set.
     */
    String getVendor();

    /**
     * Returns <code>true</code> if this eyepiece is a zoom eyepiece.<br>
     * Basically this method just checks if the maxFocalLength field is set.
     *
     * @return <code>true</code> if this eyepiece is a zoom eyepiece
     *
     * @since 1.7
     */
    boolean isZoomEyepiece();

    /**
     * Sets the apparent field of view of this eyepiece.<br>
     * The field of view Angle cannot be negative or 0.
     *
     * @param apparentFOV
     *            The new apparent field of view to be set.
     */
    void setApparentFOV(Angle apparentFOV);

    /**
     * Sets the focal length of the eyepiece.<br>
     *
     * @param focalLength
     *            The new focal length to be set.
     */
    void setFocalLength(float focalLength);

    /**
     * Sets the maximal focal length of the zoom eyepiece.<br>
     * If Float.NaN is passed, this eyepiece will no longer be treated as a zoom eyepiece.
     *
     * @param maxFocalLength
     *            The new maximal focal length to be set.
     *
     * @since 1.7
     */
    void setMaxFocalLength(float maxFocalLength);

    /**
     * Sets the model name for the eyepiece.<br>
     *
     * @param modelname
     *            The new model name to be set.
     */
    void setModel(String modelname);

    /**
     * Sets the vendor name of the eyepiece.<br>
     *
     * @param vendorname
     *            The new vendor name to be set.
     */
    void setVendor(String vendorname);

}
