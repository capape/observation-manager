/*
 * ====================================================================
 * /IImager.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om;

import org.w3c.dom.Element;

/**
 * An IImager describes a camera. The model name is a mandatory field which has to be set.
 *
 * @author doergn@users.sourceforge.net
 * @since 1.3
 */
public interface IImager extends ISchemaElement, IExtendableSchemaElement, IEquipment {

    // ---------
    // Constants ---------------------------------------------------------
    // ---------

    /**
     * Constant for XML Schema Instance type.<br>
     * This is more or less the same as in IExtendableSchemaElement.java but as extending IExtendableSchemaElement would
     * cause us to implement <code>String getXSIType()</code> (which we can't do here) we define this constant again in
     * IImager.
     */
    String XML_XSI_TYPE = "xsi:type";

    /**
     * Constant for XML representation: imager element name
     */
    String XML_ELEMENT_IMAGER = "imager";

    String XML_ELEMENT_IMAGER_SKY_SAFARI = "ccdCameraType";

    /**
     * Constant for XML representation: model element name
     * Example:<br>
     * &lt;imager&gt; <br>
     * <i>More stuff goes here</i> &lt;model&gt;<code>Model name goes here</code>&lt;/model&gt; <i>More stuff goes
     * here</i> &lt;/imager&gt;
     */
    String XML_ELEMENT_MODEL = "model";

    // public static final String XML_ELEMENT_TYPE = "type"; Removed by OAL 2.0

    /**
     * Constant for XML representation: vendor element name
     * Example:<br>
     * &lt;imager&gt; <br>
     * <i>More stuff goes here</i> &lt;vendor&gt;<code>Vendor name goes here</code>&lt;/vendor&gt; <i>More stuff goes
     * here</i> &lt;/imager&gt;
     */
    String XML_ELEMENT_VENDOR = "vendor";

    /**
     * Constant for XML representation: remarks element name
     * Example:<br>
     * &lt;imager&gt; <br>
     * <i>More stuff goes here</i> &lt;remarks&gt;<code>Vendor name goes here</code>&lt;/remarks&gt; <i>More stuff goes
     * here</i> &lt;/imager&gt;
     */
    String XML_ELEMENT_REMARKS = "remarks";

    // public static final String CCD = "ccd";

    // public static final String FILM = "film";

    // --------------
    // Public methods ----------------------------------------------------
    // --------------

    /**
     * Adds this IImager to a given parent XML DOM Element. The IImager element will be set as a child element of the
     * passed element.
     *
     * @param parent
     *            The parent element for this IImager
     * @see org.w3c.dom.Element
     */
    void addToXmlElement(Element element);

    /**
     * Adds the imager link to an given XML DOM Element The IImager element itself will be attached to given elements
     * ownerDocument if the passed boolean was <code>true</code>. If the ownerDocument has no IImager container, it will
     * be created (in case the passed boolean was <code>true</code>).<br>
     * Example:<br>
     * &lt;parameterElement&gt;<br>
     * <b>&lt;imagerLink&gt;123&lt;/imagerLink&gt;</b><br>
     * &lt;/parameterElement&gt;<br>
     * <i>More stuff of the xml document goes here</i><br>
     * <b>&lt;imagerContainer&gt;</b><br>
     * <b>&lt;imager id="123"&gt;</b><br>
     * <i>imager description goes here</i><br>
     * <b>&lt;/imager&gt;</b><br>
     * <b>&lt;/imagerContainer&gt;</b><br>
     * <br>
     *
     * @param parent
     *            The element under which the the imager link is created
     * @param addElementToContainer
     *            if <code>true</code> it's ensured that the linked element exists in the corresponding container
     *            element. Please note, passing <code>true</code> slowes down XML serialization.
     * @return Returns the Element given as parameter with a additional imager link, and the imager element under the
     *         imager container of the ownerDocument Might return <code>null</code> if element was <code>null</code>.
     * @see org.w3c.dom.Element
     * @since 2.0
     */
    Element addAsLinkToXmlElement(Element parent, boolean addElementToContainer);

    /**
     * Adds the imager link to an given XML DOM Element The imager element itself will <b>NOT</b> be attached to given
     * elements ownerDocument. Calling this method is equal to calling <code>addAsLinkToXmlElement</code> with
     * parameters <code>element, false</code><br>
     * Example:<br>
     * &lt;parameterElement&gt;<br>
     * <b>&lt;imagerLink&gt;123&lt;/imagerLink&gt;</b><br>
     * &lt;/parameterElement&gt;<br>
     * <br>
     *
     * @param element
     *            The element under which the the imager link is created
     * @return Returns the Element given as parameter with a additional imager link Might return <code>null</code> if
     *         element was <code>null</code>.
     * @see org.w3c.dom.Element
     */
    Element addAsLinkToXmlElement(Element element);

    /**
     * Returns the model name of this imager.<br>
     *
     * @return Returns the model name of this imager.<br>
     */
    String getModel();

    /**
     * Returns the vendor name of this imager.<br>
     *
     * @return Returns the vendor name of this imager or <code>NULL</code> if vendor name was not set.<br>
     */
    String getVendor();

    // public String getType();

    /**
     * Returns the remarks to this imager.<br>
     *
     * @return Returns the remarks to this imager or <code>NULL</code> if no remarks were set.<br>
     */
    String getRemarks();

    /**
     * Sets the model name for the imager.<br>
     *
     * @param modelname
     *            The new model name to be set.
     * @throws IllegalArgumentException
     *             if new modelname is <code>null</code> or empty string
     */
    void setModel(String modelname) throws IllegalArgumentException;

    /**
     * Sets the vendor name for the imager.<br>
     *
     * @param vendor
     *            The new vendor name to be set.
     */
    void setVendor(String vendor);

    // public void setType(String type);

    /**
     * Sets the remarks for this imager.<br>
     *
     * @param remarks
     *            The new remarks.
     */
    void setRemarks(String remarks);

}
