/* ====================================================================
 * extension/deepSky/DeepSkyTargetDN.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */
package de.lehmannet.om.extension.deepSky;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.lehmannet.om.IObserver;
import de.lehmannet.om.util.SchemaException;

/**
 * DeepSkyTargetDN extends the de.lehmannet.om.extension.deepSky.DeepSkyTarget class.<br>
 * Its specialised for drak nebulaes.<br>
 * 
 * @author doergn@users.sourceforge.net
 * 
 * @since 1.0
 */
public class DeepSkyTargetDN extends DeepSkyTarget {

    // ---------
    // Constants ---------------------------------------------------------
    // ---------

    // XSML schema instance value. Enables class/schema loaders to identify this
    // class
    public static final String XML_XSI_TYPE_VALUE = "oal:deepSkyDN";

    // Constant for XML representation: opacity element name
    private static final String XML_ELEMENT_OPACITY = "opacity";

    // Constant for XML representation: position angle element name
    private static final String XML_ELEMENT_POSITIONANGLE = "pa";

    // ------------------
    // Instance Variables ------------------------------------------------
    // ------------------

    // The opacity of the object (allowed values 1-6 (after Lynds 1:min 6:max)
    private int opacity = -1;

    // The (large axis) position angle of the object (only positiv values allowed)
    private int positionAngle = -1;

    // ------------
    // Constructors ------------------------------------------------------
    // ------------

    /**
     * Constructs a new instance of a DeepSkyTargetDN from a given DOM target Element.<br>
     * Normally this constructor is called by de.lehmannet.om.util.SchemaLoader. Please mind that Target has to have a
     * <observer> element, or a <datasource> element. If a <observer> element is set, a array with Observers must be
     * passed to check, whether the <observer> link is valid.
     * 
     * @param observers
     *            Array of IObserver that might be linked from this observation, can be <code>NULL</code> if datasource
     *            element is set
     * @param targetElement
     *            The origin XML DOM <target> Element
     * 
     * @throws SchemaException
     *             if given targetElement was <code>null</code>
     */
    public DeepSkyTargetDN(Node targetElement, IObserver... observers) throws SchemaException {

        super(targetElement, observers);

        Element target = (Element) targetElement;

        // Getting data

        // Get optional position angle
        NodeList children = target.getElementsByTagName(DeepSkyTargetDN.XML_ELEMENT_POSITIONANGLE);
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            String value = child.getFirstChild().getNodeValue();
            this.setPositionAngle(Integer.parseInt(value));
        } else if (children.getLength() > 1) {
            throw new SchemaException("DeepSkyTargetDN can only have one position angle entry. ");
        }

        // Get optional opacity
        children = target.getElementsByTagName(DeepSkyTargetDN.XML_ELEMENT_OPACITY);
        int opacity = -1;
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            opacity = Integer.parseInt(child.getFirstChild().getNodeValue());
            this.setOpacity(opacity);
        } else if (children.getLength() > 1) {
            throw new SchemaException("DeepSkyTargetDN can only have one opacity value. ");
        }

    }

    /**
     * Constructs a new instance of a DeepSkyTargetDN.
     * 
     * @param name
     *            The name of the dark nebula
     * @param datasource
     *            The datasource of the dark nebula
     */
    public DeepSkyTargetDN(String name, String datasource) {

        super(name, datasource);

    }

    /**
     * Constructs a new instance of a DeepSkyTargetDN.
     * 
     * @param name
     *            The name of the dark nebula
     * @param observer
     *            The observer who is the originator of the dark nebula
     */
    public DeepSkyTargetDN(String name, IObserver observer) {

        super(name, observer);

    }

    // ------
    // Target ------------------------------------------------------------
    // ------

    /**
     * Adds this Target to a given parent XML DOM Element. The Target element will be set as a child element of the
     * passed element.
     * 
     * @param parent
     *            The parent element for this Target
     * 
     * @see org.w3c.dom.Element
     */
    @Override
    public void addToXmlElement(Element element) {

        if (element == null) {
            return;
        }

        Document ownerDoc = element.getOwnerDocument();

        Element e_DSTarget = this.createXmlDeepSkyTargetElement(element, DeepSkyTargetDN.XML_XSI_TYPE_VALUE);

        // Check if element already exists
        if (e_DSTarget == null) {
            return;
        }

        if (this.positionAngle != -1) {
            Element e_PositionAngle = ownerDoc.createElement(DeepSkyTargetDN.XML_ELEMENT_POSITIONANGLE);
            Node n_PAText = ownerDoc.createTextNode(Integer.toString(this.positionAngle));
            e_PositionAngle.appendChild(n_PAText);

            e_DSTarget.appendChild(e_PositionAngle);
        }

        if (this.opacity != -1) {
            Element e_Opacity = ownerDoc.createElement(DeepSkyTargetDN.XML_ELEMENT_OPACITY);
            Node n_OpacityText = ownerDoc.createTextNode(Integer.toString(this.opacity));
            e_Opacity.appendChild(n_OpacityText);

            e_DSTarget.appendChild(e_Opacity);
        }

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
    public String getXSIType() {

        return DeepSkyTargetDN.XML_XSI_TYPE_VALUE;

    }

    // --------------
    // Public methods ----------------------------------------------------
    // --------------

    /**
     * Returns the position angle of the large axis of the dark nebula.
     * 
     * @return The position angle of the astronomical object as integer The returned value might be <code>-1</code> if
     *         the value was never set
     */
    public int getPositionAngle() {

        return this.positionAngle;

    }

    /**
     * Returns the opacity of the dark nebula. After Lynds: 1=min; 6=max
     * 
     * @return The opacity of the dark nebula as integer between 1-6 The returned value might be <code>-1</code> if the
     *         value was never set
     */
    public int getOpacity() {

        return this.opacity;

    }

    /**
     * Sets the position angle of the large axis of the dark nebula. If the given new position angle is < 0 or > 359 the
     * position angle will be unset again.
     * 
     * @param newPosAngle
     *            The new position angle of the dark nebula.
     */
    public void setPositionAngle(int newPosAngle) {

        if (((newPosAngle > 359) || (newPosAngle < 0))) {
            this.positionAngle = -1;
            return;
        }

        this.positionAngle = newPosAngle;

    }

    /**
     * Sets the opacity of the dark nebula. The opacity value has to be between 1 and 6. (After Lynds: 1=min; 6=max)<br>
     * All other values will be interpreted as -1, which means that the value gets cleared (means: is treated like it
     * was never set)
     * 
     * @param newOpacity
     *            The new opacity of the dark nebula as int
     */
    public void setOpacity(int newOpacity) {

        if ((newOpacity < 1) || (newOpacity > 6)) {
            this.opacity = -1;
        } else {

            this.opacity = newOpacity;
        }

    }

}
