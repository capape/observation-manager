/* ====================================================================
 * /Scope.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.apache.commons.lang3.tuple.Pair;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.lehmannet.om.mapper.ScopeMapper;
import de.lehmannet.om.util.FloatUtil;
import de.lehmannet.om.util.SchemaException;

/**
 * A Scope describes an optical instrument which can be used for astronomical
 * observations.<br>
 * A Scope must have an aperture and a model name, as well as either a
 * magnification <b>or</b> a focalLength. (magnification should be set if the
 * optical instrument does not allow to change eyepieces).<br>
 * 
 * @author doergn@users.sourceforge.net
 * @since 1.0
 */
public class Scope extends SchemaElement implements IScope {

    // ---------
    // Constants ---------------------------------------------------------
    // ---------

    // XSML schema instance value. Enables class/schema loaders to identify this
    // class. As this class handled both types scopeType and
    // fixedMagnificationOpticsType
    // we declare both here
    private static final String XML_XSI_TYPE_VALUE = "oal:scopeType";

    // XSML schema instance value. Enables class/schema loaders to identify this
    // class. As this class handled both types scopeType and
    // fixedMagnificationOpticsType
    // we declare both here
    private static final String XML_XSI_FIXEDTYPE_VALUE = "oal:fixedMagnificationOpticsType";

    // ------------------
    // Instance Variables ------------------------------------------------
    // ------------------

    // Model of the scope
    private String model = "";

    // Type of scope. E.g. Newton, Schmidt,...
    // Or use DSL coded abbreviation:
    // A: Naked eye C: Cassegrain
    // B: Binoculars S: Schmidt-Cassegrain
    // N: Newton K: Kutter (Schiefspiegler)
    // R: Refractor M: Maksutov
    private String type = null;

    // Vendor of scope
    private String vendor = null;

    // Aperture (only positiv values)
    private float aperture = Float.NaN;

    // Focal length of scope. Used if scope has variable eyepieces.
    // Can be used instead of magnification.
    private float focalLength = Float.NaN;

    // Magnification of scope. Used if scope has no variable eyepieces.
    // Can be used instead of focalLength.
    private float magnification = Float.NaN;

    // Used to determin aberration of scope.
    // Valid values between 0.0 and 1.0 (0% - 100%)
    private float lightGrasp = Float.NaN;

    // 1 if the orientation is erected (true)
    // 0 if the orientation is not erected (false)
    // -1 the value was not set
    private int orientation_Erect = -1;

    // 1 if the orientation is erected (true)
    // 0 if the orientation is not erected (false)
    // -1 the value was not set
    private int orientation_Truesided = -1;

    // If magnification is set, the true field of view can be set additionally
    // Angle cannot be negative
    private Angle trueFieldOfView = null;

    // Flag indicating whether Scope is still available
    private boolean available = true;

    // ------------
    // Constructors ------------------------------------------------------
    // ------------

    /**
     * Constructs a new instance of a Scope from a given XML Schmea Node. Normally
     * this constructor is only used by de.lehmannet.om.util.SchemaLoader
     *
     * @param scope The XML Schema Node that represents this Scope Object
     * @throws IllegalArgumentException if the parameter is <NULL>
     * @throws SchemaException          if the given Node does not match the XML
     *                                  Schema secifications
     */
    public Scope(Node scope) throws SchemaException, IllegalArgumentException {

        if (scope == null) {
            throw new IllegalArgumentException("Parameter scope node cannot be NULL. ");
        }

        // Cast to element as we need some methods from it
        Element scopeElement = (Element) scope;

        this.setID(ScopeMapper.getMandatoryID(scopeElement));
        this.setAvailability(ScopeMapper.getOptionalAvailability(scopeElement));
        this.setFocalLengthNoCheckingMagnification(ScopeMapper.getOptionalFocalLength(scopeElement));
        
        this.setMagnificationNoCheckingFocalLength(ScopeMapper.getOptionalMagnification(scopeElement));
        if (!Float.isNaN(this.getMagnification())) {
            this.setTrueFieldOfView(ScopeMapper.getOptionalTrueViewOfField(scopeElement));
        } 

        this.setModel(ScopeMapper.getMandatoryModel(scopeElement));
        this.setAperture(ScopeMapper.getMandatoryAperture(scopeElement));
        this.setType(ScopeMapper.getOptionalType(scopeElement));
        this.setVendor(ScopeMapper.getOptionalVendor(scopeElement));

        this.setLightGrasp(ScopeMapper.getOptionalLightGrasp(scopeElement));

        Pair<Boolean, Boolean> pair = ScopeMapper.getOptionalOrientation(scopeElement);
        this.setOrientation(pair.getLeft(), pair.getRight());
    }

    /**
     * Constructs a new instance of a Scope.
     * 
     * @param model       The scopes model name
     * @param aperture    The aperture of the scope
     * @param focalLength The focal length of the scope
     * @throws IllegalArgumentException if one of the parameters has an illegal
     *                                  value (see setModel(String),
     *                                  setAperture(float) and setFocalLength(float)
     *                                  for allowed values
     */
    public Scope(String model, float aperture, float focalLength) throws IllegalArgumentException {

        this.setModel(model);

        this.setAperture(aperture);

        this.setFocalLength(focalLength);

    }

    /**
     * Constructs a new instance of a Scope.
     * 
     * @param aperture      The scopes aperture
     * @param magnification The magnification of the scope
     * @param model         The model name of the scope
     * @throws IllegalArgumentException if one of the parameters has an illegal
     *                                  value (see setModel(String),
     *                                  setAperture(float) and
     *                                  setMagnification(float) for allowed values
     */
    public Scope(float aperture, float magnification, String model) throws IllegalArgumentException {

        this.setModel(model);

        this.setAperture(aperture);

        this.setMagnification(magnification);

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
     * @see java.lang.Object.toString();
     */
    @Override
    public String getDisplayName() {

        String dn = this.getModel();
        if ((this.vendor != null) && !("".equals(this.vendor.trim()))) {
            dn = this.getVendor() + " " + dn;
        }

        if (!Float.isNaN(this.aperture)) {
            // Output format
            DecimalFormat df = new DecimalFormat("0");
            DecimalFormatSymbols dfs = new DecimalFormatSymbols();
            dfs.setDecimalSeparator('.');
            df.setDecimalFormatSymbols(dfs);

            dn = dn + " " + df.format(this.aperture) + "mm";
        }

        return dn;

    }

    // ------------------------
    // IExtendableSchemaElement ------------------------------------------
    // ------------------------

    /*
     * @Override public String[] getXSIType() {
     * 
     * return new String[] { Scope.XML_XSI_TYPE_VALUE, Scope.XML_XSI_FIXEDTYPE_VALUE
     * };
     * 
     * }
     */

    // ------
    // Object ------------------------------------------------------------
    // ------

    /**
     * Overwrittes toString() method from java.lang.Object.<br>
     * Returns the field values of this Scope.
     * 
     * @return This Scopes field values
     * @see java.lang.Object
     */
    @Override
    public String toString() {

        StringBuilder buffer = new StringBuilder();
        buffer.append("Scope: Model name=");
        buffer.append(model);

        buffer.append(" Aperture=");
        buffer.append(aperture);

        if (!Float.isNaN(focalLength)) {
            buffer.append(" focal length=");
            buffer.append(focalLength);
        }

        if (!Float.isNaN(magnification)) {
            buffer.append(" magnification=");
            buffer.append(magnification);
            if (this.trueFieldOfView != null) {
                buffer.append(" TrueFieldOfView=");
                buffer.append(this.trueFieldOfView);
            }
        }

        if (type != null) {
            buffer.append(" type=");
            buffer.append(type);
        }

        if (vendor != null) {
            buffer.append(" vendor=");
            buffer.append(vendor);
        }

        if (!Float.isNaN(lightGrasp)) {
            buffer.append(" light grasp=");
            buffer.append(lightGrasp);
        }

        if ((this.orientation_Erect != -1) && (this.orientation_Truesided != -1)) {
            buffer.append(" Orientation=");
            if (orientation_Erect == 1) {
                buffer.append(" (Erected");
            } else if (orientation_Erect == 0) {
                buffer.append(" (Not Erected");
            }
            if (orientation_Truesided == 1) {
                buffer.append(" Truesided)");
            } else if (orientation_Truesided == 0) {
                buffer.append(" Not Truesided)");
            }

        }

        return buffer.toString();

    }

    /*
     * @Override public boolean equals(Object obj) {
     * 
     * if( obj == null || !(obj instanceof IScope) ) { return false; }
     * 
     * IScope scope = (IScope)obj;
     * 
     * String modelName = scope.getModel(); if( modelName == null ) { return false;
     * } if( !model.toLowerCase(Locale.getDefault()).equals(modelName.toLowerCase(Locale.getDefault())) ) { return false;
     * }
     * 
     * float objAperture = scope.getAperture(); if( objAperture != aperture ) {
     * return false; }
     * 
     * if( !Float.isNaN(magnification) ) { float objMagnification =
     * scope.getMagnification(); if( magnification == objMagnification ) { return
     * true; } else { return false; }
     * 
     * }
     * 
     * if( !Float.isNaN(focalLength) ) { float objFocalLength =
     * scope.getFocalLength(); if( focalLength == objFocalLength ) { return true; }
     * else { return false; } }
     * 
     * // We should never get here return false;
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
     * @param available A boolean value indicating whether this element is still
     *                  available for usage
     */
    @Override
    public void setAvailability(boolean available) {

        this.available = available;

    }

    // ------
    // IScope ------------------------------------------------------------
    // ------

    /**
     * Adds this Scope to a given parent XML DOM Element. The Scope element will be
     * set as a child element of the passed element.
     * 
     * @param parent The parent element for this Scope
     * @see org.w3c.dom.Element
     */
    @Override
    public void addToXmlElement(Element element) {

        if (element == null) {
            return;
        }

        Document ownerDoc = element.getOwnerDocument();

        // Check if this element doesn't exist so far
        boolean existsElement = this.existsNodeElement(element);
        if (!existsElement) {
            return;
        }

        // Create the new scope element
        Element e_Scope = ownerDoc.createElement(XML_ELEMENT_SCOPE);
        e_Scope.setAttribute(XML_ELEMENT_ATTRIBUTE_ID, this.getID());

        element.appendChild(e_Scope);

        // ----- Set Comments (do this at the very beginning to possibly increase speed
        // during read)
        if (!this.isAvailable()) {
            Comment comment = ownerDoc.createComment(IEquipment.XML_COMMENT_ELEMENT_NOLONGERAVAILABLE);
            e_Scope.appendChild(comment);
        }

        Element e_Model = ownerDoc.createElement(XML_ELEMENT_MODEL);
        Node n_ModelText = ownerDoc.createCDATASection(this.model);
        e_Model.appendChild(n_ModelText);
        e_Scope.appendChild(e_Model);

        if (type != null) {
            addTypeNode(ownerDoc, e_Scope);
        }

        if (vendor != null) {
            addVendorNode(ownerDoc, e_Scope);
        }

        addApertureNode(ownerDoc, e_Scope);

        addOrientationNode(ownerDoc, e_Scope);

        addFocalLengthNode(ownerDoc, e_Scope);

        addMagnificationNode(ownerDoc, e_Scope);

    }

    private boolean existsNodeElement(Element element) {
        boolean existsElement = true;
        NodeList nodeList = element.getElementsByTagName(IScope.XML_ELEMENT_SCOPE);
        if (nodeList.getLength() > 0) {
            Node currentNode = null;
            NamedNodeMap attributes = null;
            for (int i = 0; i < nodeList.getLength(); i++) { // iterate over all found nodes
                currentNode = nodeList.item(i);
                attributes = currentNode.getAttributes();
                Node idAttribute = attributes.getNamedItem(ISchemaElement.XML_ELEMENT_ATTRIBUTE_ID);
                if ((idAttribute != null) // if ID attribute is set and equals this objects ID, return existing element
                        && (idAttribute.getNodeValue().trim().equals(this.getID().trim()))) {
                    existsElement = false;
                    ;
                }
            }
        }
        return existsElement;
    }

    private void addTypeNode(Document ownerDoc, Element e_Scope) {
        Element e_Type = ownerDoc.createElement(XML_ELEMENT_TYPE);
        Node n_TypeText = ownerDoc.createCDATASection(this.type);
        e_Type.appendChild(n_TypeText);
        e_Scope.appendChild(e_Type);
    }

    private void addVendorNode(Document ownerDoc, Element e_Scope) {
        Element e_Vendor = ownerDoc.createElement(XML_ELEMENT_VENDOR);
        Node n_VendorText = ownerDoc.createCDATASection(this.vendor);
        e_Vendor.appendChild(n_VendorText);
        e_Scope.appendChild(e_Vendor);
    }

    private void addApertureNode(Document ownerDoc, Element e_Scope) {
        Element e_Aperture = ownerDoc.createElement(XML_ELEMENT_APERTURE);
        Node n_ApertureText = ownerDoc.createTextNode(Float.toString(this.aperture));
        e_Aperture.appendChild(n_ApertureText);
        e_Scope.appendChild(e_Aperture);

        if (!Float.isNaN(lightGrasp)) {
            Element e_LightGrasp = ownerDoc.createElement(XML_ELEMENT_LIGHTGRASP);
            Node n_LightGraspText = ownerDoc.createTextNode(Float.toString(this.lightGrasp));
            e_LightGrasp.appendChild(n_LightGraspText);
            e_Scope.appendChild(e_LightGrasp);
        }
    }

    private void addOrientationNode(Document ownerDoc, Element e_Scope) {
        if ((this.orientation_Erect != -1) && (this.orientation_Truesided != -1)) {
            Element e_Orientation = ownerDoc.createElement(IScope.XML_ELEMENT_ORENTATION);
            String erect = (this.orientation_Erect == 1) ? "true" : "false";
            String truesided = (this.orientation_Truesided == 1) ? "true" : "false";
            e_Orientation.setAttribute(IScope.XML_ELEMENT_ORENTATION_ATTRIBUTE_ERECT, erect);
            e_Orientation.setAttribute(IScope.XML_ELEMENT_ORENTATION_ATTRIBUTE_TRUESIDED, truesided);
            e_Scope.appendChild(e_Orientation);
        }
    }

    private void addFocalLengthNode(Document ownerDoc, Element e_Scope) {
        if (!Float.isNaN(focalLength)) {
            Element e_FocalLength = ownerDoc.createElement(XML_ELEMENT_FOCALLENGTH);
            Node n_FocalLengthText = ownerDoc.createTextNode(Float.toString(this.focalLength));
            e_FocalLength.appendChild(n_FocalLengthText);
            e_Scope.appendChild(e_FocalLength);
            // Set XSI:Type
            e_Scope.setAttribute(IScope.XML_XSI_TYPE, Scope.XML_XSI_TYPE_VALUE);
        }
    }

    private void addMagnificationNode(Document ownerDoc, Element e_Scope) {
        if (!Float.isNaN(magnification)) {
            Element e_Magnification = ownerDoc.createElement(XML_ELEMENT_MAGNIFICATION);
            Node n_MagnificationText = ownerDoc.createTextNode(Float.toString(this.magnification));
            e_Magnification.appendChild(n_MagnificationText);
            e_Scope.appendChild(e_Magnification);
            if (this.trueFieldOfView != null) {
                Element e_TrueFieldOfView = ownerDoc.createElement(IScope.XML_ELEMENT_TRUEFIELD);
                e_TrueFieldOfView = this.trueFieldOfView.setToXmlElement(e_TrueFieldOfView);
                e_Scope.appendChild(e_TrueFieldOfView);
            }
            // Set XSI:Type
            e_Scope.setAttribute(IScope.XML_XSI_TYPE, Scope.XML_XSI_FIXEDTYPE_VALUE);
        }
    }

    /**
     * Adds the scope link to an given XML DOM Element The scope element itself will
     * be attached to given elements ownerDocument if the passed boolean was
     * <code>true</code>. If the ownerDocument has no scope container, it will be
     * created (in case the passed boolean was <code>true</code>).<br>
     * Example:<br>
     * &lt;parameterElement&gt;<br>
     * <b>&lt;scopeLink&gt;123&lt;/scopeLink&gt;</b><br>
     * &lt;/parameterElement&gt;<br>
     * <i>More stuff of the xml document goes here</i><br>
     * <b>&lt;scopeContainer&gt;</b><br>
     * <b>&lt;scope id="123"&gt;</b><br>
     * <i>scope description goes here</i><br>
     * <b>&lt;/scope&gt;</b><br>
     * <b>&lt;/scopeContainer&gt;</b><br>
     * <br>
     * 
     * @param element               The element under which the the scope link is
     *                              created
     * @param addElementToContainer if <code>true</code> it's ensured that the
     *                              linked element exists in the corresponding
     *                              container element. Please note, passing
     *                              <code>true</code> slowes down XML serialization.
     * @return Returns the Element given as parameter with a additional scope link,
     *         and the scope element under the scope container of the ownerDocument
     *         Might return <code>null</code> if element was <code>null</code>.
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
        Element e_Link = ownerDoc.createElement(XML_ELEMENT_SCOPE);
        Node n_LinkText = ownerDoc.createTextNode(this.getID());
        e_Link.appendChild(n_LinkText);

        element.appendChild(e_Link);

        if (addElementToContainer) {
            // Get or create the container element
            Element e_Scopes = null;
            NodeList nodeList = ownerDoc.getElementsByTagName(RootElement.XML_SCOPE_CONTAINER);
            if (nodeList.getLength() == 0) { // we're the first element. Create container element
                e_Scopes = ownerDoc.createElement(RootElement.XML_SCOPE_CONTAINER);
                ownerDoc.getDocumentElement().appendChild(e_Scopes);
            } else {
                e_Scopes = (Element) nodeList.item(0); // there should be only one container element
            }

            this.addToXmlElement(e_Scopes);
        }

        return element;

    }

    /**
     * Adds the scope link to an given XML DOM Element The scope element itself will
     * <b>NOT</b> be attached to given elements ownerDocument. Calling this method
     * is equal to calling <code>addAsLinkToXmlElement</code> with parameters
     * <code>element, false</code><br>
     * Example:<br>
     * &lt;parameterElement&gt;<br>
     * <b>&lt;scopeLink&gt;123&lt;/observerLink&gt;</b><br>
     * &lt;/parameterElement&gt;<br>
     * <br>
     * 
     * @param element The element under which the the scope link is created
     * @return Returns the Element given as parameter with a additional scope link
     *         Might return <code>null</code> if element was <code>null</code>.
     * @see org.w3c.dom.Element
     */
    @Override
    public Element addAsLinkToXmlElement(Element element) {

        return this.addAsLinkToXmlElement(element, false);

    }

    /**
     * Returns the aperture of the scope.<br>
     * The aperture can be any positive float value.
     * 
     * @return Returns the aperture of the scope
     */
    @Override
    public float getAperture() {

        return aperture;

    }

    /**
     * Returns the focal length of the scope.<br>
     * This value might return <code>Float.NaN</code> in case the focal length is
     * not set for this scope. In that case the magnification must return a value.
     * 
     * @return Returns the focal length of the scope, or Float.NaN if the value does
     *         not suit this scope
     */
    @Override
    public float getFocalLength() {

        return focalLength;

    }

    /**
     * Returns the light grasp value of the scope.<br>
     * Allowed values are between 0.0 and 1.0 (including 0.0 and 1.0)<br>
     * This value might return <code>Float.NaN</code> in case the light grasp was
     * never set.
     * 
     * @return Returns the light grasp value of the scope, or Float.NaN if the value
     *         was never set.
     */
    @Override
    public float getLightGrasp() {

        return lightGrasp;

    }

    /**
     * Returns the true field of view, if set.<br>
     * Might return <code>NULL</code> as the field is optional only if magnification
     * is set.
     * 
     * @return Returns the true field of view of the scope, or <code>NULL</code> if
     *         the value was never set.
     */
    @Override
    public Angle getTrueFieldOfView() {

        return this.trueFieldOfView;

    }

    /**
     * Returns the scopes picture vertical orientation.
     * 
     * @return <b>true</b> if the scopes picture is errected
     * @throws IllegalStateException if orientation was not set by the user, so the
     *                               class cannot return <b>true</b> or <b>false</b>
     */
    @Override
    public boolean isOrientationErected() throws IllegalStateException {

        if (this.orientation_Erect == -1) {
            throw new IllegalStateException("Orientation was never set. ");
        }

        return this.orientation_Erect == 1;

    }

    /**
     * Returns the scopes picture horizontal orientation.
     * 
     * @return <b>true</b> if the scopes picture is truesided
     * @throws IllegalStateException if orientation was not set by the user, so the
     *                               class cannot return <b>true</b> or <b>false</b>
     */
    @Override
    public boolean isOrientationTruesided() throws IllegalStateException {

        if (this.orientation_Truesided == -1) {
            throw new IllegalStateException("Orientation was never set. ");
        }

        return this.orientation_Truesided == 1;

    }

    /**
     * Returns the magnification of the scope.<br>
     * This value might return <code>Float.NaN</code> in case the magnification is
     * not set for this scope. In that case the focal length must return a value.
     * 
     * @return Returns the magnification of the scope, or Float.NaN if the value
     *         does not suit this scope
     */
    @Override
    public float getMagnification() {

        return magnification;

    }

    /**
     * Returns the scopes model name.<br>
     * 
     * @return Returns the model name of the scope
     */
    @Override
    public String getModel() {

        return model;

    }

    /**
     * Returns the scope type.<br>
     * E.g. Newton, Binocular, Reflector...<br>
     * If abbreviation is returned, here's the mapping: A: Naked eye<br>
     * C: Cassegrain<br>
     * B: Binoculars<br>
     * S: Schmidt-Cassegrain<br>
     * N: Newton<br>
     * K: Kutter (Schiefspiegler)<br>
     * R: Refractor<br>
     * M: Maksutov<br>
     * <br>
     * 
     * This method might return <code>null</code> if the type was never set.
     * 
     * @return Returns the scope type, or <code>null</code> if the type was never
     *         set.
     */
    @Override
    public String getType() {

        if (type != null) {
            switch (type) {
                case "A":
                    return "Naked eye";
                case "B":
                    return "Binoculars";
                case "N":
                    return "Newton";
                case "R":
                    return "Refractor";
                case "C":
                    return "Cassegrain";
                case "S":
                    return "Schmidt-Cassegrain";
                case "K":
                    return "Kutter";
                case "M":
                    return "Maksutov";
                default:
                    return type;

            }
        }

        return type;

    }

    /**
     * Returns the scope's vendor name.<br>
     * E.g. Celestron, TeleVue, Meade, Vixen...<br>
     * This method might return <code>null</code> if the vendor was never set.
     * 
     * @return Returns the scope's vendor name, or <code>null</code> if the type was
     *         never set.
     */
    @Override
    public String getVendor() {

        return vendor;

    }

    /**
     * Sets the aperture of the scope.<br>
     * The aperture must be a positive float value.
     * 
     * @param aperture The new aperture of the scope
     * @throws IllegalArgumentException if the given aperture is smaller or equal
     *                                  0.0 or if the aperture is Float.NaN
     */
    @Override
    public void setAperture(float aperture) throws IllegalArgumentException {

        if (Float.isNaN(aperture)) {
            throw new IllegalArgumentException("Aperture cannot be Float.NaN. ");
        }

        if (aperture <= 0.0) {
            throw new IllegalArgumentException("Aperture cannot be <= 0.0 ");
        }

        this.aperture = aperture;

    }

    /**
     * Sets the focal length of the scope.<br>
     * Throws IllegalArgumentException if the given parameter is Float.NaN and
     * magnification is not set. This mechanism should prevent that both values
     * contain an invalid value at the same time. Either focal length <b>or</b>
     * magnification must be set. Also if the new focal Length is smaller or equal
     * 0.0 a IllegalArgumentException is thrown.
     * 
     * @param focalLength The new focal length of the scope
     * @throws IllegalArgumentException if the given focal length is smaller or
     *                                  equal 0.0 or if the new focal length is
     *                                  Float.NaN while magnification is also
     *                                  Float.NaN. Or magnification is already set.
     */
    @Override
    public void setFocalLength(float focalLength) throws IllegalArgumentException {

        if ((Float.isNaN(focalLength)) && (Float.isNaN(magnification))) {
            throw new IllegalArgumentException(
                    "Focal length cannot be Float.NaN while magnification is also Float.NaN. ");
        }

        if (focalLength <= 0.0) {
            throw new IllegalArgumentException("Focal length cannot be <= 0.0 ");
        }

        this.magnification = Float.NaN;
        this.trueFieldOfView = null;
        this.focalLength = focalLength;

    }

    private void setFocalLengthNoCheckingMagnification(float focalLength) throws IllegalArgumentException {

        if (!Float.isNaN(focalLength)) {

            if (focalLength <= 0.0) {
                throw new IllegalArgumentException("Focal length cannot be <= 0.0 ");
            }

            this.magnification = Float.NaN;
            this.trueFieldOfView = null;
            this.focalLength = focalLength;
        }

    }

    /**
     * Sets the light grasp value of the scope.<br>
     * The light grasp must be a positive float value between 0.0 and 1.0 (including
     * 0.0 and 1.0).
     * 
     * @param lightGrasp The new light grasp value of the scope
     */
    @Override
    public void setLightGrasp(float lightGrasp) {

        if ((lightGrasp < 0.0) || (lightGrasp > 1.0)) {
            return;
        }

        this.lightGrasp = lightGrasp;

    }

    /**
     * Sets the magnification of the scope.<br>
     * Throws IllegalArgumentException if the given parameter is Float.NaN and focal
     * length is not set. This mechanism should prevent that both values contain an
     * invalid value at the same time. Either focal length <b>or</b> magnification
     * must be set.
     * 
     * @param magnification The new magnification value of the scope
     * @throws IllegalArgumentException if the given focal length is Float.NaN while
     *                                  focal length is not set. Or focal length is
     *                                  already set.
     */
    @Override
    public void setMagnification(float magnification) throws IllegalArgumentException {

        if ((Float.isNaN(magnification)) && (Float.isNaN(focalLength))) {
            throw new IllegalArgumentException(
                    "Magnification cannot be Float.NaN while focal length is also Float.NaN. ");
        }

        this.focalLength = Float.NaN;
        this.magnification = magnification;

    }

    private void setMagnificationNoCheckingFocalLength(float magnification) throws IllegalArgumentException {

        this.focalLength = Float.NaN;
        this.magnification = magnification;

    }


    /**
     * Sets the true field of view, if magnification is given.<br>
     * 
     * @param tfov The true field of view of the scope
     * @throws IllegalArgumentException if focal length is set.
     */
    @Override
    public void setTrueFieldOfView(Angle tfov) throws IllegalArgumentException {

        if (!Float.isNaN(this.focalLength)) {
            throw new IllegalArgumentException(
                    "Focal length is set. True field of view can only be set, if magnification is set. ");
        }

        this.trueFieldOfView = tfov;

    }

    /**
     * Sets the model name of the scope.<br>
     * The model name cannot be <code>null</code>.
     * 
     * @param model The new model name of the scope
     * @throws IllegalArgumentException if the given model name is <code>null</code>
     */
    @Override
    public void setModel(String model) throws IllegalArgumentException {

        if (model == null) {
            throw new IllegalArgumentException("Model name cannot be null. ");
        }

        this.model = model;

    }

    /**
     * Sets the type of the scope.<br>
     * E.g. Newton, Reflector, Binocular... The type is optional but should be given
     * if known!<br>
     * When applicable, the following coding (according to the DSL) should be
     * used:<br>
     * A: Naked eye C: Cassegrain<br>
     * B: Binoculars S: Schmidt-Cassegrain<br>
     * N: Newton K: Kutter (Schiefspiegler)<br>
     * R: Refractor M: Maksutov <br>
     * <br>
     * 
     * @param type The new type of the scope
     */
    @Override
    public void setType(String type) {

        if ((type != null) && ("".equals(type.trim()))) {
            this.type = null;
            return;
        }

        this.type = type;

    }

    /**
     * Sets the vendor name of the scope.<br>
     * 
     * @param vendor The new vendor name of the scope
     */
    @Override
    public void setVendor(String vendor) {

        if ((vendor != null) && ("".equals(vendor.trim()))) {
            this.vendor = null;
            return;
        }

        this.vendor = vendor;

    }

    /**
     * Sets the orientation of the scope.<br>
     * 
     * @param errected  <code>true</code> if the scope produces errected pictures
     * @param truesided <code>true</code> if the scope produces truesided pictures
     */
    @Override
    public void setOrientation(boolean errected, boolean truesided) {

        if (errected) {
            this.orientation_Erect = 1;
        } else {
            this.orientation_Erect = 0;
        }

        if (truesided) {
            this.orientation_Truesided = 1;
        } else {
            this.orientation_Truesided = 0;
        }

    }

}
