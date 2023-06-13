/*
 * ====================================================================
 * /Eyepiece.java
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

import de.lehmannet.om.mapper.EyePieceMapper;
import de.lehmannet.om.util.SchemaException;

/**
 * Eyepiece implements the class de.lehmannet.om.IEyepiece. An Eyepiece describes a optical eyepiece. The model name and
 * the focalLength are mandatory fields which have to be set.
 *
 * @author doergn@users.sourceforge.net
 * @since 1.0
 */
public class Eyepiece extends SchemaElement implements IEyepiece {

    // ------------------
    // Instance Variables ------------------------------------------------
    // ------------------

    // Model name of the eyepiece (usually given by vendor)
    private String model = "";

    // Vendor name of eyepiece (TeleVue, Meade, Vixen....)
    private String vendor = null;

    // Focal length of the eyepiece.
    private float focalLength = Float.NaN;

    // Maximal focal length of the eyepiece. (in case of zoom eyepiece)
    private float maxFocalLength = Float.NaN;

    // Apparent field of view. Angles values cannot be 0 or negative.
    private Angle apparentFOV = null;

    // Flag indicating whether Eyepiece is still available
    private boolean available = true;

    // ------------
    // Constructors ------------------------------------------------------
    // ------------

    /**
     * Constructs a new instance of an Eyepiece from an given XML Schema Node.<br>
     * Normally this constructor is only used by de.lehmannet.om.util.SchemaLoader
     *
     * @param eyepiece
     *            The XML Schema element that represents this eyepiece
     * @throws IllegalArgumentException
     *             if parameter is <code>null</code>,
     * @throws SchemaException
     *             if the given Node does not match the XML Schema specifications
     */
    public Eyepiece(Node eyepiece) throws SchemaException, IllegalArgumentException {

        if (eyepiece == null) {
            throw new IllegalArgumentException("Parameter eyepiece node cannot be NULL. ");
        }

        // Cast to element as we need some methods from it
        Element eyepieceElement = (Element) eyepiece;

        // Getting data
        // First mandatory stuff and down below optional data
        this.setID(EyePieceMapper.getMandatoryID(eyepieceElement));
        this.setModel(EyePieceMapper.getMandatoryModel(eyepieceElement));
        this.setFocalLength(EyePieceMapper.getMandatoryFocalLength(eyepieceElement));
        this.setAvailability(EyePieceMapper.getOptionalAvailability(eyepieceElement));
        this.setVendor(EyePieceMapper.getOptionalVendor(eyepieceElement));
        this.setMaxFocalLength(EyePieceMapper.getOptionalMaximunFocusLength(eyepieceElement));
        this.setApparentFOV(EyePieceMapper.getApparentFOV(eyepieceElement));

    }

    /**
     * Constructs a new instance of an Eyepiece.<br>
     *
     * @param model
     *            The eyepieces model name
     * @param focalLength
     *            The focal length of the eyepiece
     * @throws IllegalArgumentException
     *             if model is <code>null</code> or focalLength is Float.NaN
     */
    public Eyepiece(String model, float focalLength) throws IllegalArgumentException {

        this.setModel(model);
        this.setFocalLength(focalLength);

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
     * @see java.lang.Object.toString();
     */
    @Override
    public String getDisplayName() {

        String dn = this.getModel() + " " + this.getFocalLength();
        if (this.isZoomEyepiece()) {
            dn = dn + "-" + this.getMaxFocalLength();
        }
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
     * Returns all fields of the class Eyepiece (unset field will be ignored). The result string will look like
     * this:<br>
     * Example:<br>
     * <code>
     * Eyepiece Model: Ultra Wide Angle 8.8mm<br>
     * Vendor: Meade
     * Focal length: 8.8
     * Apparent field of view: 84 DEG
     * </code>
     *
     * @return A string representing the eyepiece
     * @see java.lang.Object
     */
    @Override
    public String toString() {

        StringBuilder buffer = new StringBuilder();

        buffer.append("Eyepiece Model: ");
        buffer.append(this.getModel());

        if (vendor != null) {
            buffer.append("Vendor: ");
            buffer.append(this.getVendor());
        }

        buffer.append("Focal length: ");
        buffer.append(this.getFocalLength());

        if (apparentFOV != null) {
            buffer.append("Apparent field of view: ");
            buffer.append(this.getApparentFOV());
        }

        if (!Float.isNaN(maxFocalLength)) {
            buffer.append("Maximal focal length: ");
            buffer.append(this.getMaxFocalLength());
        }

        return buffer.toString();

    }

    /*
     * public boolean equals(Object obj) {
     *
     * if( obj == null || !(obj instanceof IEyepiece) ) { return false; }
     *
     * IEyepiece eyepiece = (IEyepiece)obj;
     *
     * if( !(model.equals(eyepiece.getModel())) ) { return false; }
     *
     * if( !(focalLength == eyepiece.getFocalLength()) ) { return false; }
     *
     * if( vendor != null ) { if( !vendor.equals(eyepiece.getVendor()) ) { return false; } } else if(
     * eyepiece.getVendor() != null ) { return false; }
     *
     * if( apparentFOV != null ) { if( !apparentFOV.equals(eyepiece.getApparentFOV()) ) { return false; } } else if(
     * eyepiece.getApparentFOV() != null ) { return false; }
     *
     * return true;
     *
     * }
     */

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

    // ---------
    // IEyepiece ---------------------------------------------------------
    // ---------

    /**
     * Adds this Eyepiece to a given parent XML DOM Element. The Eyepiece element will be set as a child element of the
     * passed element.
     *
     * @param parent
     *            The parent element for this Eyepiece
     * @see org.w3c.dom.Element
     */
    @Override
    public void addToXmlElement(Element element) {

        if (element == null) {
            return;
        }

        Document ownerDoc = element.getOwnerDocument();

        // Check if this element doesn't exist so far
        NodeList nodeList = element.getElementsByTagName(IEyepiece.XML_ELEMENT_EYEPIECE);
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

        // Create the new eyepiece element
        Element e_Eyepiece = ownerDoc.createElement(XML_ELEMENT_EYEPIECE);
        e_Eyepiece.setAttribute(XML_ELEMENT_ATTRIBUTE_ID, this.getID());

        element.appendChild(e_Eyepiece);

        // ----- Set Comments (do this at the very beginning to possibly increase speed
        // during read)
        if (!this.isAvailable()) {
            Comment comment = ownerDoc.createComment(IEquipment.XML_COMMENT_ELEMENT_NOLONGERAVAILABLE);
            e_Eyepiece.appendChild(comment);
        }

        Element e_Model = ownerDoc.createElement(XML_ELEMENT_MODEL);
        Node n_ModelText = ownerDoc.createCDATASection(this.model);
        e_Model.appendChild(n_ModelText);
        e_Eyepiece.appendChild(e_Model);

        if (vendor != null) {
            Element e_Vendor = ownerDoc.createElement(XML_ELEMENT_VENDOR);
            Node n_VendorText = ownerDoc.createCDATASection(this.vendor);
            e_Vendor.appendChild(n_VendorText);
            e_Eyepiece.appendChild(e_Vendor);
        }

        Element e_FocalLength = ownerDoc.createElement(XML_ELEMENT_FOCALLENGTH);
        Node n_FocalLengthText = ownerDoc.createTextNode(Float.toString(this.focalLength));
        e_FocalLength.appendChild(n_FocalLengthText);
        e_Eyepiece.appendChild(e_FocalLength);

        if (!Float.isNaN(this.maxFocalLength)) {
            Element e_MaxFocalLength = ownerDoc.createElement(XML_ELEMENT_MAXFOCALLENGTH);
            Node n_MaxFocalLengthText = ownerDoc.createTextNode(Float.toString(this.maxFocalLength));
            e_MaxFocalLength.appendChild(n_MaxFocalLengthText);
            e_Eyepiece.appendChild(e_MaxFocalLength);
        }

        if (apparentFOV != null) {
            Element e_ApparentFOV = ownerDoc.createElement(XML_ELEMENT_APPARENTFOV);
            e_ApparentFOV = apparentFOV.setToXmlElement(e_ApparentFOV);
            e_Eyepiece.appendChild(e_ApparentFOV);
        }

    }

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
     * @return Returns the Element given as parameter with a additional eyepiece link, and the eyepiece element under
     *         the eyepiece container of the ownerDocument Might return <code>null</code> if element was
     *         <code>null</code>.
     * @see org.w3c.dom.Element
     * @since 2.0
     */
    @Override
    public Element addAsLinkToXmlElement(Element element, boolean addElementToContainer) {

        if (element == null) {
            return null;
        }

        Document ownerDoc = element.getOwnerDocument();

        // Create the link element
        Element e_Link = ownerDoc.createElement(XML_ELEMENT_EYEPIECE);
        Node n_LinkText = ownerDoc.createTextNode(this.getID());
        e_Link.appendChild(n_LinkText);

        element.appendChild(e_Link);

        if (addElementToContainer) {
            // Get or create the container element
            Element e_Eyepieces = null;
            NodeList nodeList = ownerDoc.getElementsByTagName(RootElement.XML_EYEPIECE_CONTAINER);
            if (nodeList.getLength() == 0) { // we're the first element. Create container element
                e_Eyepieces = ownerDoc.createElement(RootElement.XML_EYEPIECE_CONTAINER);
                ownerDoc.getDocumentElement().appendChild(e_Eyepieces);
            } else {
                e_Eyepieces = (Element) nodeList.item(0); // there should be only one container element
            }

            this.addToXmlElement(e_Eyepieces);
        }

        return element;

    }

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
     * @return Returns the Element given as parameter with a additional eyepiece link Might return <code>null</code> if
     *         element was <code>null</code>.
     * @see org.w3c.dom.Element
     */
    @Override
    public Element addAsLinkToXmlElement(Element element) {

        return this.addAsLinkToXmlElement(element, false);

    }

    /**
     * Returns the apparent field of view of this eyepiece.
     *
     * @return Returns the apparent field of view of this eyepiece. The Angles value cannot be negative or 0.<br>
     *         If <code>null</code> is returned the apparent field of view value was never set.
     * @see de.lehmannet.om.Angle
     */
    @Override
    public Angle getApparentFOV() {

        return Angle.of(apparentFOV);

    }

    /**
     * Returns the focal length of this eyepiece. The focal length of the telescope divided by the focal length of the
     * eyepiece equals the amplification.<br>
     * In case this eyepiece is a zoomEyepiece, this focal length is the minimum focal length. To retrieve the maximim
     * focal length, please use getMaxFocalLength()
     *
     * @see getMaxFocalLength()
     * @return Returns the focal length of the eyepiece.
     */
    @Override
    public float getFocalLength() {

        return focalLength;

    }

    /**
     * Returns the maximal focal length of this eyepiece in case this eyepiece is a zoom eyepiece. Might return
     * <code>Float.NaN</code> in case this eyepiece is not a zoom eyepiece.
     *
     * @return Returns the maximal focal length of the eyepiece.
     * @since 1.7
     */
    @Override
    public float getMaxFocalLength() {

        return maxFocalLength;

    }

    /**
     * Returns the model name of the eyepiece.<br>
     *
     * @return Returns a String representing the eyepieces model name.<br>
     */
    @Override
    public String getModel() {

        return model;

    }

    /**
     * Returns the vendor name of the eyepiece.<br>
     *
     * @return Returns a String representing the eyepieces vendor name.<br>
     *         If <code>null</code> is returned the vendor name was never set.
     */
    @Override
    public String getVendor() {

        return vendor;

    }

    /**
     * Returns <code>true</code> if this eyepiece is a zoom eyepiece.<br>
     * Basically this method just checks if the maxFocalLength field is set.
     *
     * @return <code>true</code> if this eyepiece is a zoom eyepiece
     * @since 1.7
     */
    @Override
    public boolean isZoomEyepiece() {

        return !Float.isNaN(this.maxFocalLength);

    }

    /**
     * Sets the apparent field of view of this eyepiece.<br>
     * The field of view Angle cannot be negative or 0.
     *
     * @param apparentFOV
     *            The new apparent field of view to be set.
     */
    @Override
    public void setApparentFOV(Angle apparentFOV) {

        if ((apparentFOV == null) || (apparentFOV.getValue() <= 0.0)) {
            return;
        }

        this.apparentFOV = Angle.of(apparentFOV);

    }

    /**
     * Sets the focal length of the eyepiece.<br>
     *
     * @param focalLength
     *            The new focal length to be set.
     * @throws IllegalArgumentException
     *             if focalLength was <code>Float.NaN</code>
     */
    @Override
    public void setFocalLength(float focalLength) throws IllegalArgumentException {

        if (Float.isNaN(focalLength)) {
            throw new IllegalArgumentException("Focal length cannot be Float.NaN. ");
        }

        this.focalLength = focalLength;

    }

    /**
     * Sets the maximal focal length of the zoom eyepiece.<br>
     * If Float.NaN is passed, this eyepiece will no longer be treated as a zoom eyepiece.
     *
     * @param maxFocalLength
     *            The new maximal focal length to be set.
     * @since 1.7
     */
    @Override
    public void setMaxFocalLength(float maxFocalLength) {

        this.maxFocalLength = maxFocalLength;

    }

    /**
     * Sets the model name for the eyepiece.<br>
     *
     * @param modelname
     *            The new model name to be set.
     * @throws IllegalArgumentException
     *             if modelname was <code>null</code>
     */
    @Override
    public void setModel(String modelname) throws IllegalArgumentException {

        if (modelname == null) {
            throw new IllegalArgumentException("Modelname cannot be null. ");
        }

        this.model = modelname;

    }

    /**
     * Sets the vendor name of the eyepiece.<br>
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