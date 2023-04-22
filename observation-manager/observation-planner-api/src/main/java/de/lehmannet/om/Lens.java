/* ====================================================================
 * /Lens.java
 *
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.lehmannet.om.mapper.LensMapper;
import de.lehmannet.om.util.SchemaException;

/**
 * A Lens describes a lens used to extend or reduce a focal length. Implementations of Lens can be Barlow lenses or
 * Shapley lenses depending on the given factor. The model name and the factor are mandatory fields which have to be
 * set.
 *
 * @author doergn@users.sourceforge.net
 *
 * @since 1.7
 */
public class Lens extends SchemaElement implements ILens {

    // ------------------
    // Instance Variables ------------------------------------------------
    // ------------------

    // Model name of the lens (usually given by vendor)
    private String model = "";

    // Vendor name of lens (TeleVue, Meade, Vixen....)
    private String vendor = null;

    // Focal length factor of the lens.
    // Factors > 1 represent barlow lenses
    // Factors < 1 represent shapley lenses
    // Factors <= 0 are not allowed
    private float factor = 1.0f;

    // Flag indicating whether Lens is still available
    private boolean available = true;

    // ------------
    // Constructors ------------------------------------------------------
    // ------------

    /**
     * Constructs a new instance of a Lens from an given XML Schema Node.<br>
     * Normally this constructor is only used by de.lehmannet.om.util.SchemaLoader
     *
     * @param lens
     *            The XML Schema element that represents this lens
     *
     * @throws IllegalArgumentException
     *             if parameter is <code>null</code>,
     * @throws SchemaException
     *             if the given Node does not match the XML Schema specifications
     */
    public Lens(Node lens) throws SchemaException, IllegalArgumentException {

        if (lens == null) {
            throw new IllegalArgumentException("Parameter lens node cannot be NULL. ");
        }

        // Cast to element as we need some methods from it
        Element lensElement = (Element) lens;

        this.setID(LensMapper.getMandatoryID(lensElement));
        this.setModel(LensMapper.getMandatoryModel(lensElement));
        this.setFactor(LensMapper.getMandatoryFactor(lensElement));
        this.setAvailability(LensMapper.getOptionalAvailability(lensElement));
        this.setVendor(LensMapper.getOptionalVendor(lensElement));

    }

    /**
     * Constructs a new instance of a Lens.<br>
     *
     * @param model
     *            The lens model name
     * @param factor
     *            The focal length factor of the lens<br>
     *            Factors > 1 represent barlow lenses<br>
     *            Factors < 1 represent shapley lenses<br>
     *            Factors <= 0 are not allowed
     *
     * @throws IllegalArgumentException
     *             if model is <code>null</code> or factor is Float.NaN or <= 0
     */
    public Lens(String model, float factor) throws IllegalArgumentException {

        this.setModel(model);
        this.setFactor(factor);

    }

    // -------------
    // SchemaElement -----------------------------------------------------
    // -------------

    /**
     * Returns a display name for this element.<br>
     * The method differs from the toString() method as toString() shows more technical information about the element.
     * Also the formating of toString() can spread over several lines.<br>
     * This method returns a string (in one line) that can be used as displayname in e.g. a UI dropdown box.
     *
     * @return Returns a String with a one line display name
     *
     * @see java.lang.Object.toString();
     */
    @Override
    public String getDisplayName() {

        String dn = this.getModel() + " " + this.getFactor();
        if ((this.vendor != null) && !("".equals(this.vendor.trim()))) {
            dn = this.getVendor() + " " + dn;
        }

        return dn;

    }

    // ------
    // Object ------------------------------------------------------------
    // ------

    /**
     * Overwrittes toString() method from java.lang.Object.<br>
     * Returns all fields of the class lens (unset fields will be ignored). The result string will look like this:<br>
     * Example:<br>
     * <code>
     * Lens Model: Powermate<br>
     * Vendor: TeleVue
     * Focal length factor: 5.0
     * </code>
     *
     * @return A string representing the lens
     *
     * @see java.lang.Object
     */
    @Override
    public String toString() {

        StringBuilder buffer = new StringBuilder();

        buffer.append("Lens Model: ");
        buffer.append(this.getModel());

        if (vendor != null) {
            buffer.append("\n");
            buffer.append("Vendor: ");
            buffer.append(this.getVendor());
        }

        buffer.append("\n");
        buffer.append("Focal length factor: ");
        buffer.append(this.getFactor());

        return buffer.toString();

    }

    // ----------
    // IEquipment --------------------------------------------------------
    // ----------

    /**
     * Returns <code>true</code> if this element is still available for use-<br>
     *
     * @return a boolean with the availability of the element
     */
    @Override
    public boolean isAvailable() {

        return this.available;

    }

    /**
     * Sets the availability of this element.<br>
     *
     * @param available
     *            A boolean value indicating whether this element is still available for usage
     */
    @Override
    public void setAvailability(boolean available) {

        this.available = available;

    }

    // -----
    // ILens -------------------------------------------------------------
    // -----

    /**
     * Adds this Lens to a given parent XML DOM Element. The Lens element will be set as a child element of the passed
     * element.
     *
     * @param parent
     *            The parent element for this Lens
     *
     * @see org.w3c.dom.Element
     */
    @Override
    public void addToXmlElement(Element element) {

        if (element == null) {
            return;
        }

        Document ownerDoc = element.getOwnerDocument();

        // Check if this element doesn't exist so far
        NodeList nodeList = element.getElementsByTagName(ILens.XML_ELEMENT_LENS);
        if (nodeList.getLength() > 0) {
            Node currentNode = null;
            NamedNodeMap attributes = null;
            for (int i = 0; i < nodeList.getLength(); i++) { // iterate over all found nodes
                currentNode = nodeList.item(i);
                attributes = currentNode.getAttributes();
                Node idAttribute = attributes.getNamedItem(ISchemaElement.XML_ELEMENT_ATTRIBUTE_ID);
                if ((idAttribute != null) // if ID attribute is set and equals this objects ID, return existing element
                        && (idAttribute.getNodeValue().trim().equals(this.getID().trim()))) {
                    return;
                }
            }
        }

        // Create the new lens element
        Element e_Lens = ownerDoc.createElement(XML_ELEMENT_LENS);
        e_Lens.setAttribute(XML_ELEMENT_ATTRIBUTE_ID, this.getID());

        element.appendChild(e_Lens);

        // ----- Set Comments (do this at the very beginning to possibly increase speed
        // during read)
        if (!this.isAvailable()) {
            Comment comment = ownerDoc.createComment(IEquipment.XML_COMMENT_ELEMENT_NOLONGERAVAILABLE);
            e_Lens.appendChild(comment);
        }

        Element e_Model = ownerDoc.createElement(XML_ELEMENT_MODEL);
        Node n_ModelText = ownerDoc.createCDATASection(this.model);
        e_Model.appendChild(n_ModelText);
        e_Lens.appendChild(e_Model);

        if (vendor != null) {
            Element e_Vendor = ownerDoc.createElement(XML_ELEMENT_VENDOR);
            Node n_VendorText = ownerDoc.createCDATASection(this.vendor);
            e_Vendor.appendChild(n_VendorText);
            e_Lens.appendChild(e_Vendor);
        }

        Element e_factor = ownerDoc.createElement(XML_ELEMENT_FACTOR);
        Node n_FocalLengthText = ownerDoc.createTextNode(Float.toString(this.factor));
        e_factor.appendChild(n_FocalLengthText);
        e_Lens.appendChild(e_factor);

    }

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
     *
     * @return Returns the Element given as parameter with a additional lens link, and the lens element under the lens
     *         container of the ownerDocument Might return <code>null</code> if element was <code>null</code>.
     *
     * @see org.w3c.dom.Element
     *
     * @since 2.0
     */
    @Override
    public Element addAsLinkToXmlElement(Element element, boolean addElementToContainer) {

        if (element == null) {
            return null;
        }

        Document ownerDoc = element.getOwnerDocument();

        // Create the link element
        Element e_Link = ownerDoc.createElement(XML_ELEMENT_LENS);
        Node n_LinkText = ownerDoc.createTextNode(this.getID());
        e_Link.appendChild(n_LinkText);

        element.appendChild(e_Link);

        if (addElementToContainer) {
            // Get or create the container element
            Element e_Lenses = null;
            NodeList nodeList = ownerDoc.getElementsByTagName(RootElement.XML_LENS_CONTAINER);
            if (nodeList.getLength() == 0) { // we're the first element. Create container element
                e_Lenses = ownerDoc.createElement(RootElement.XML_LENS_CONTAINER);
                ownerDoc.getDocumentElement().appendChild(e_Lenses);
            } else {
                e_Lenses = (Element) nodeList.item(0); // there should be only one container element
            }

            this.addToXmlElement(e_Lenses);
        }

        return element;

    }

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
     *
     * @return Returns the Element given as parameter with a additional lens link Might return <code>null</code> if
     *         element was <code>null</code>.
     *
     * @see org.w3c.dom.Element
     */
    @Override
    public Element addAsLinkToXmlElement(Element element) {

        return this.addAsLinkToXmlElement(element, false);

    }

    /**
     * Returns the focal length factor of this lens.<br>
     * Factors > 1 represent barlow lenses<br>
     * Factors < 1 represent shapley lenses<br>
     * Factors <= 0 are not allowed<br>
     *
     * @return Returns the focal length factor of the lens.
     */
    @Override
    public float getFactor() {

        return this.factor;

    }

    /**
     * Returns the model name of the lens.<br>
     *
     * @return Returns a String representing the lens model name.<br>
     */
    @Override
    public String getModel() {

        return model;

    }

    /**
     * Returns the vendor name of the lens.<br>
     *
     * @return Returns a String representing the lens vendor name.<br>
     *         If <code>null</code> is returned the vendor name was never set.
     */
    @Override
    public String getVendor() {

        return vendor;

    }

    /**
     * Sets the focal length factor of the lens.<br>
     *
     * @param factor
     *            The new focal length factor to be set.
     *
     * @throws IllegalArgumentException
     *             if factor was <code>Float.NaN</code> or <= 0
     */
    @Override
    public void setFactor(float factor) throws IllegalArgumentException {

        if (Float.isNaN(factor)) {
            throw new IllegalArgumentException("Focal length factor cannot be Float.NaN. ");
        }

        if (factor <= 0) {
            throw new IllegalArgumentException("Focal length factor must be > 0 ");
        }

        this.factor = factor;

    }

    /**
     * Sets the model name for the lens.<br>
     *
     * @param modelname
     *            The new model name to be set.
     *
     * @throws IllegalArgumentException
     *             if modelname was <code>null</code>
     */
    @Override
    public void setModel(String modelname) throws IllegalArgumentException {

        if (modelname == null) {
            throw new IllegalArgumentException("Lens modelname cannot be null. ");
        }

        this.model = modelname;

    }

    /**
     * Sets the vendor name of the lens.<br>
     *
     * @param vendorname
     *            The new vendor name to be set.
     */
    @Override
    public void setVendor(String vendorname) {

        if ((vendorname != null) && ("".equals(vendorname.trim()))) {
            this.vendor = null;
            return;
        }

        this.vendor = vendorname;

    }

}