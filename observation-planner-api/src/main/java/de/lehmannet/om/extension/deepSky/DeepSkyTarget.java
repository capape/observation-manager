/* ====================================================================
 * extension/deepSky/DeepSkyTarget.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.extension.deepSky;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.lehmannet.om.Angle;
import de.lehmannet.om.IObserver;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.RootElement;
import de.lehmannet.om.SurfaceBrightness;
import de.lehmannet.om.Target;
import de.lehmannet.om.util.FloatUtil;
import de.lehmannet.om.util.SchemaException;

/**
 * DeepSkyTarget extends the de.lehmannet.om.Target class. Its specialised for DeepSky targets. A DeepSky target can be
 * an astronomical object outside our solar system.<br>
 * Mostly all fields of the class are optional, only the name (which is derived from class Target) is madatory.
 * 
 * @author doergn@users.sourceforge.net
 * @since 1.0
 */
public abstract class DeepSkyTarget extends Target {

    // ---------
    // Constants ---------------------------------------------------------
    // ---------

    // Constant for XML representation: small diameter element name
    private static final String XML_ELEMENT_SMALLDIAMETER = "smallDiameter";

    // Constant for XML representation: large diameter element name
    private static final String XML_ELEMENT_LARGEDIAMETER = "largeDiameter";

    // Constant for XML representation: visible magnitude element name
    private static final String XML_ELEMENT_VISIBLEMAGNITUDE = "visMag";

    // Constant for XML representation: surface brightness element name
    private static final String XML_ELEMENT_SURFACEBRIGHTNESS = "surfBr";

    // ------------------
    // Instance Variables ------------------------------------------------
    // ------------------

    // The small diameter of the object (only positiv values allowed)
    private Angle smallDiameter = null;

    // The large diameter of the object (only positiv values allowed)
    private Angle largeDiameter = null;

    // The visible magnitude of the astronomical object
    // (Float.NaN indicates that field was not set)
    private float visibleMagnitude = Float.NaN;

    // The astronomical objects surface brightness
    // (null indicates that field was not set)
    private SurfaceBrightness surfaceBrightness = null;

    // ------------
    // Constructors ------------------------------------------------------
    // ------------

/**
     * Constructs a new instance of a DeepSkyTarget from a given DOM target Element.<br>
     * Normally this constructor is called by a subclass which itself is called by de.lehmannet.om.util.SchemaLoader.
     * Please mind that Target has to have a <observer> element, or a <datasource> element. If a <observer> element is
     * set, a array with Observers must be passed to check, whether the <observer> link is valid.
     * 
     * @param observers
     *            Array of IObserver that might be linked from this observation, can be <code>NULL</code> if datasource
     *            element is set
     * @param targetElement
     *            The origin XML DOM <target> Element
     * @throws SchemaException
     *             if given targetElement was <code>null</code>
     */
    DeepSkyTarget(Node targetElement, IObserver... observers) throws SchemaException {

        super(targetElement, observers);

        Element target = (Element) targetElement;

        Element child = null;
        NodeList children = null;

        // Getting data
        // First mandatory stuff and down below optional data

        // Get optional small diameter
        children = target.getElementsByTagName(DeepSkyTarget.XML_ELEMENT_SMALLDIAMETER);
        Angle smallDiameter = null;
        if (children != null) {
            if (children.getLength() == 1) {
                child = (Element) children.item(0);
                String value = child.getFirstChild().getNodeValue();
                String unit = child.getAttribute(Angle.XML_ATTRIBUTE_UNIT);
                smallDiameter = new Angle(Double.parseDouble(value), unit);
                this.setSmallDiameter(smallDiameter);
            } else if (children.getLength() > 1) {
                throw new SchemaException("DeepSkyTarget can only have one small diameter entry. ");
            }
        }

        // Get optional large diameter
        children = target.getElementsByTagName(DeepSkyTarget.XML_ELEMENT_LARGEDIAMETER);
        Angle largeDiameter = null;
        if (children != null) {
            if (children.getLength() == 1) {
                child = (Element) children.item(0);
                String value = child.getFirstChild().getNodeValue();
                String unit = child.getAttribute(Angle.XML_ATTRIBUTE_UNIT);
                largeDiameter = new Angle(Double.parseDouble(value), unit);
                this.setLargeDiameter(largeDiameter);
            } else if (children.getLength() > 1) {
                throw new SchemaException("DeepSkyTarget can only have one large diameter entry. ");
            }
        }

        // Get optional visible magintude
        children = target.getElementsByTagName(DeepSkyTarget.XML_ELEMENT_VISIBLEMAGNITUDE);
        String visMag = null;
        if (children != null) {
            if (children.getLength() == 1) {
                child = (Element) children.item(0);
                visMag = child.getFirstChild().getNodeValue();
                this.setVisibleMagnitude(FloatUtil.parseFloat(visMag));
            } else if (children.getLength() > 1) {
                throw new SchemaException("DeepSkyTarget can only have one visible magnitude entry. ");
            }
        }

        // Get optional surface brightness
        children = target.getElementsByTagName(DeepSkyTarget.XML_ELEMENT_SURFACEBRIGHTNESS);
        String surBright = null;
        if (children != null) {
            if (children.getLength() == 1) {
                child = (Element) children.item(0);
                surBright = child.getFirstChild().getNodeValue();
                // Check if unit is explicitly given (from OAL 2.0 this is the case)
                String unit = child.getAttribute(SurfaceBrightness.XML_ATTRIBUTE_UNIT);
                if ((unit == null) || ("".equals(unit))) {
                    unit = SurfaceBrightness.MAGS_SQR_ARC_MIN; // Prio to 2.0 the value was always giben in mags per sqr
                                                               // args min
                }
                this.setSurfaceBrightness(new SurfaceBrightness(FloatUtil.parseFloat(surBright), unit));
            } else if (children.getLength() > 1) {
                throw new SchemaException("DeepSkyTarget can only have one surface brightness entry. ");
            }
        }

    }

/**
     * Constructs a new instance of a DeepSkyTarget.
     * 
     * @param name
     *            The name of the astronomical object
     * @param datasource
     *            The datasource of the astronomical object
     */
    DeepSkyTarget(String name, String datasource) {

        super(name, datasource);

    }

/**
     * Constructs a new instance of a DeepSkyTarget.
     * 
     * @param name
     *            The name of the astronomical object
     * @param observer
     *            The observer who is the originator of the target
     */
    DeepSkyTarget(String name, IObserver observer) {

        super(name, observer);

    }

    // ------
    // Object ------------------------------------------------------------
    // ------

/**
     * Overwrittes toString() method from java.lang.Object.<br>
     * Returns the field values of this DeepSkyTarget.
     * 
     * @return This DeepSkyTarget field values
     * @see java.lang.Object
     */
    @Override
    public String toString() {

        StringBuilder buffer = new StringBuilder();
        buffer.append("DeepSkyTarget: Name=");
        buffer.append(this.getName());

        if ((this.getAliasNames() != null) && (this.getAliasNames().length > 0)) {
            buffer.append(" Alias names=");
            String[] an = this.getAliasNames();
            for (int i = 0; i < an.length; i++) {
                buffer.append(an[i]);
                if (i <= an.length - 2) {
                    buffer.append(", ");
                }
            }
        }

        if (this.getPosition() != null) {
            buffer.append(" Position=");
            buffer.append(this.getPosition());
        }

        if (smallDiameter != null) {
            buffer.append(" small Diameter=");
            buffer.append(smallDiameter);
        }

        if (largeDiameter != null) {
            buffer.append(" large Diameter=");
            buffer.append(largeDiameter);
        }

        if (visibleMagnitude != -1) {
            buffer.append(" visible Magnitude=");
            buffer.append(this.getVisibleMagnitude());
        }

        if (surfaceBrightness != null) {
            buffer.append(" surface Brightness=");
            buffer.append(this.getSurfaceBrightness());
        }

        return buffer.toString();

    }

    // ------------------------
    // IExtendableSchemaElement ------------------------------------------
    // ------------------------

/**
     * Returns the XML schema instance type of the implementation.<br>
     * Example:<br>
     * <target xsi:type="myOwnTarget"><br>
     * </target><br>
     * 
     * @return The xsi:type value of this implementation
     */
    @Override
    public abstract String getXSIType();

    // ------
    // Target ------------------------------------------------------------
    // ------

/**
     * Adds this Target to a given parent XML DOM Element. The Target element will be set as a child element of the
     * passed element.
     * 
     * @param parent
     *            The parent element for this Target
     * @see org.w3c.dom.Element
     */
    @Override
    public abstract void addToXmlElement(Element element);

    // -----------------
    // Protected methods -------------------------------------------------
    // -----------------

/**
     * Creates a deepkSkyTarget under the target container. If no target container exists under the given elements
     * ownerDocument, it will be created.<br>
     * This method should be called by subclasses, so that they only have to add their specific data to the element
     * returned. Example:<br>
     * &lt;parameterElement&gt;<br>
     * <b>&lt;targetLink&gt;123&lt;/targetLink&gt;</b><br>
     * &lt;/parameterElement&gt;<br>
     * <i>More stuff of the xml document goes here</i><br>
     * <b>&lt;targetContainer&gt;</b><br>
     * <b>&lt;target id="123"&gt;</b><br>
     * <i>target description goes here</i><br>
     * <b>&lt;/target&gt;</b><br>
     * <b>&lt;/targetContainer&gt;</b><br>
     * <br>
     * 
     * @param element
     *            The element under which the the target link is created
     * @param xsiType
     *            The XSI:Type identification of the child class
     * @return Returns a new created target Element that contains all data from a DeepSkyTarget. Please mind, NOT the
     *         passed element is given, but a child element of the passed elements ownerDocument. Might return
     *         <code>null</code> if element was <code>null</code>.
     * @see org.w3c.dom.Element
     */
    Element createXmlDeepSkyTargetElement(Element element, String xsiType) {

        if (element == null) {
            return null;
        }

        Document ownerDoc = element.getOwnerDocument();

        // Get or create the container element
        Element e_Targets = null;
        boolean created = false;
        NodeList nodeList = ownerDoc.getElementsByTagName(RootElement.XML_TARGET_CONTAINER);
        if (nodeList.getLength() == 0) { // we're the first element. Create container element
            e_Targets = ownerDoc.createElement(RootElement.XML_TARGET_CONTAINER);
            created = true;
        } else {
            e_Targets = (Element) nodeList.item(0); // there should be only one container element
        }

        // Check if this element doesn't exist so far
        nodeList = e_Targets.getElementsByTagName(ITarget.XML_ELEMENT_TARGET);
        if (nodeList.getLength() > 0) {
            Node currentNode = null;
            NamedNodeMap attributes = null;
            for (int i = 0; i < nodeList.getLength(); i++) { // iterate over all found nodes
                currentNode = nodeList.item(i);
                attributes = currentNode.getAttributes();
                Node idAttribute = attributes.getNamedItem(ISchemaElement.XML_ELEMENT_ATTRIBUTE_ID);
                if ((idAttribute != null) // if ID attribute is set and equals this objects ID, return existing element
                        && (idAttribute.getNodeValue().trim().equals(this.getID().trim()))) {
                    // Not sure if this is good!? Maybe we should return currentNode and make
                    // doublicity check in caller
                    // class!?
                    return null;
                }
            }
        }

        // Create the new target element
        Element e_Target = this.createXmlTargetElement(e_Targets);
        e_Targets.appendChild(e_Target);

        // Set XSI:Type
        e_Target.setAttribute(ITarget.XML_XSI_TYPE, xsiType);

        if (smallDiameter != null) {
            Element e_SmallDiameter = ownerDoc.createElement(XML_ELEMENT_SMALLDIAMETER);
            e_SmallDiameter = smallDiameter.setToXmlElement(e_SmallDiameter);

            e_Target.appendChild(e_SmallDiameter);
        }

        if (largeDiameter != null) {
            Element e_LargeDiameter = ownerDoc.createElement(XML_ELEMENT_LARGEDIAMETER);
            e_LargeDiameter = largeDiameter.setToXmlElement(e_LargeDiameter);

            e_Target.appendChild(e_LargeDiameter);
        }

        if (!Float.isNaN(visibleMagnitude)) {
            Element e_VisMag = ownerDoc.createElement(XML_ELEMENT_VISIBLEMAGNITUDE);
            Node n_VisMagText = ownerDoc.createTextNode(Float.toString(this.getVisibleMagnitude()));
            e_VisMag.appendChild(n_VisMagText);

            e_Target.appendChild(e_VisMag);
        }

        if (surfaceBrightness != null) {
            Element e_SurfBr = ownerDoc.createElement(XML_ELEMENT_SURFACEBRIGHTNESS);
            e_SurfBr = surfaceBrightness.setToXmlElement(e_SurfBr);

            e_Target.appendChild(e_SurfBr);
        }

        // If container element was created, add container here so that XML sequence
        // fits forward references
        // Calling the appendChild in the if avbe would cause the session container to
        // be located before
        // observers and sites container
        if (created) {
            ownerDoc.getDocumentElement().appendChild(e_Targets);
        }

        return e_Target;

    }

    // --------------
    // Public methods ----------------------------------------------------
    // --------------

/**
     * Returns the large diameter of the astronomical object.
     * 
     * @return The large diameter of the astronomical object as Angle The returned value might be <code>null</code> if
     *         the value was never set
     * @see de.lehmannet.om.Angle
     */
    public Angle getLargeDiameter() {

        return largeDiameter;

    }

/**
     * Returns the small diameter of the astronomical object.
     * 
     * @return The small diameter of the astronomical object as Angle The returned value might be <code>null</code> if
     *         the value was never set
     * @see de.lehmannet.om.Angle
     */
    public Angle getSmallDiameter() {

        return smallDiameter;

    }

/**
     * Returns the surface brightness of the astronomical object
     * 
     * @return The surface brightness of the astronomical object.<br>
     *         Might return null if surface brightness was never set
     */
    public SurfaceBrightness getSurfaceBrightness() {

        return surfaceBrightness;

    }

/**
     * Returns the visible magnitude of the astronomical object.
     * 
     * @return The visible magnitude of the astronomical object.<br>
     *         Might return Float.NaN if visible magnitude was never set
     */
    public float getVisibleMagnitude() {

        return visibleMagnitude;

    }

/**
     * Sets the large diameter of the DeepSkyTarget. A valid Angle has a value larger (or euqals) 0.0.
     * 
     * @param largeDiameter
     *            The large diameter to set
     */
    public void setLargeDiameter(Angle largeDiameter) {

        if (largeDiameter != null) {
            if (largeDiameter.getValue() < 0.0) {
                return;
            }
        }

        this.largeDiameter = largeDiameter;

    }

/**
     * Sets the small diameter of the DeepSkyTarget. A valid Angle has a value larger (or euqals) 0.0.
     * 
     * @param smallDiameter
     *            The small diameter to set
     */
    public void setSmallDiameter(Angle smallDiameter) {

        if (smallDiameter != null) {
            if (smallDiameter.getValue() < 0.0) {
                return;
            }
        }

        this.smallDiameter = smallDiameter;

    }

/**
     * Sets the surface brightness of the astronomical object.<br>
     * 
     * @param surfaceBrightness
     *            The surface brightness to set
     */
    public void setSurfaceBrightness(SurfaceBrightness surfaceBrightness) {

        this.surfaceBrightness = surfaceBrightness;

    }

/**
     * Sets the visible magnitude of the astronomical object.
     * 
     * @param visibleMagnitude
     *            The visible magnitude to set
     */
    public void setVisibleMagnitude(float visibleMagnitude) {

        this.visibleMagnitude = visibleMagnitude;

    }

}
