/* ====================================================================
 * /ILens.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om;

import org.w3c.dom.Element;

/**
 * An ILens describes a lens used to extend or reduce a focal length. Implementations of ILens can be Barlow lenses or
 * Shapley lenses depending on the given factor. The model name and the factor are mandatory fields which have to be
 * set.
 * 
 * @author doergn@users.sourceforge.net
 * @since 1.7
 */
public interface ILens extends ISchemaElement, IEquipment {

    // ---------
    // Constants ---------------------------------------------------------
    // ---------

    /**
     * Constant for XML representation: lens element name
     */
    String XML_ELEMENT_LENS = "lens";

    /**
     * Constant for XML representation: model element name
     * 
     * Example:<br>
     * &lt;lens&gt; <br>
     * <i>More stuff goes here</i> &lt;model&gt;<code>Model name goes here</code>&lt;/model&gt; <i>More stuff goes
     * here</i> &lt;/lens&gt;
     */
    String XML_ELEMENT_MODEL = "model";

    /**
     * Constant for XML representation: vendor element name
     *
     * Example:<br>
     * &lt;lens&gt; <br>
     * <i>More stuff goes here</i> &lt;vendor&gt;<code>Vendor name goes here</code>&lt;/vendor&gt; <i>More stuff goes
     * here</i> &lt;/lens&gt;
     */
    String XML_ELEMENT_VENDOR = "vendor";

    /**
     * Constant for XML representation: factor element name
     *
     * Example:<br>
     * &lt;lens&gt; <br>
     * <i>More stuff goes here</i> &lt;factor&gt;<code>lens focal length factor goes here</code>&lt;/factor&gt; <i>More
     * stuff goes here</i> &lt;/lens&gt;
     */
    String XML_ELEMENT_FACTOR = "factor";

    // --------------
    // Public methods ----------------------------------------------------
    // --------------

    /**
     * Adds this Lens to a given parent XML DOM Element. The Lens element will be set as a child element of the passed
     * element.
     * 
     * @param parent
     *            The parent element for this Lens
     * @see org.w3c.dom.Element
     */
    void addToXmlElement(Element element);

    /**
     * Adds the lens link to an given XML DOM Element The lens element itself will be attached to given elements
     * ownerDocument if the passed boolean was <code>true</code>. If the ownerDocument has no lens container, it will be
     * created (in case the passed boolean was <code>true</code>).<br>
     * Example:<br>
     * &lt;parameterElement&gt;<br>
     * <b>&lt;lensLink&gt;123&lt;/lensLink&gt;</b><br>
     * &lt;/parameterElement&gt;<br>
     * <i>More stuff of the xml document goes here</i><br>
     * <b>&lt;lensContainer&gt;</b><br>
     * <b>&lt;lens id="123"&gt;</b><br>
     * <i>lens description goes here</i><br>
     * <b>&lt;/lens&gt;</b><br>
     * <b>&lt;/lensContainer&gt;</b><br>
     * <br>
     * 
     * @param element
     *            The element under which the the lens link is created
     * @param addElementToContainer
     *            if <code>true</code> it's ensured that the linked element exists in the corresponding container
     *            element. Please note, passing <code>true</code> slowes down XML serialization.
     * @return Returns the Element given as parameter with a additional lens link, and the lens element under the lens
     *         container of the ownerDocument Might return <code>null</code> if element was <code>null</code>.
     * @see org.w3c.dom.Element
     * @since 2.0
     */
    Element addAsLinkToXmlElement(Element element, boolean addElementToContainer);

    /**
     * Adds the lens link to an given XML DOM Element The lens element itself will <b>NOT</b> be attached to given
     * elements ownerDocument. Calling this method is equal to calling <code>addAsLinkToXmlElement</code> with
     * parameters <code>element, false</code><br>
     * Example:<br>
     * &lt;parameterElement&gt;<br>
     * <b>&lt;lensLink&gt;123&lt;/lensLink&gt;</b><br>
     * &lt;/parameterElement&gt;<br>
     * <br>
     * 
     * @param element
     *            The element under which the the lens link is created
     * @return Returns the Element given as parameter with a additional lens link Might return <code>null</code> if
     *         element was <code>null</code>.
     * @see org.w3c.dom.Element
     */
    Element addAsLinkToXmlElement(Element element);

    /**
     * Returns the focal length factor of this lens.<br>
     * Factors > 1 represent barlow lenses<br>
     * Factors < 1 represent shapley lenses<br>
     * Factors <= 0 are not allowed<br>
     * 
     * @return Returns the focal length factor of the lens.
     */
    float getFactor();

    /**
     * Returns the model name of the lens.<br>
     * 
     * @return Returns a String representing the lenses model name.<br>
     */
    String getModel();

    /**
     * Returns the vendor name of the lens.<br>
     * 
     * @return Returns a String representing the lenses vendor name.<br>
     *         If <code>null</code> is returned the vendor name was never set.
     */
    String getVendor();

    /**
     * Sets the focal length factor of the lens.<br>
     * Value must be > 0.<br>
     * Factors > 1 represent barlow lenses<br>
     * Factors < 1 represent shapley lenses<br>
     * Factors <= 0 are not allowed<br>
     * 
     * @param factor
     *            The new focal length factor to be set.
     */
    void setFactor(float factor);

    /**
     * Sets the model name for the lens.<br>
     * 
     * @param modelname
     *            The new model name to be set.
     */
    void setModel(String modelname);

    /**
     * Sets the vendor name of the lens.<br>
     * 
     * @param vendorname
     *            The new vendor name to be set.
     */
    void setVendor(String vendorname);

}
