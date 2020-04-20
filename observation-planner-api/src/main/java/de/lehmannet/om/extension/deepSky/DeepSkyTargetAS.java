/* ====================================================================
 * extension/deepSky/DeepSkyTargetAS.java
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
 * DeepSkyTargetAS extends the de.lehmannet.om.extension.deepSky.DeepSkyTarget class.<br>
 * Its specialised for asterisms.<br>
 * 
 * @author doergn@users.sourceforge.net
 * @since 1.7
 */
public class DeepSkyTargetAS extends DeepSkyTarget {

    // ---------
    // Constants ---------------------------------------------------------
    // ---------

    // XSML schema instance value. Enables class/schema loaders to identify this
    // class
    public static final String XML_XSI_TYPE_VALUE = "oal:deepSkyAS";

    // Constant for XML representation: position angle element name
    private static final String XML_ELEMENT_POSITIONANGLE = "pa";

    // ------------------
    // Instance Variables ------------------------------------------------
    // ------------------

    // The position angle of the object (only positiv values allowed)
    private int positionAngle = -1;

    // ------------
    // Constructors ------------------------------------------------------
    // ------------

/**
     * Constructs a new instance of a DeepSkyTargetAS from a given DOM target Element.<br>
     * Normally this constructor is called by de.lehmannet.om.util.SchemaLoader. Please mind that Target has to have a
     * <observer> element, or a <datasource> element. If a <observer> element is set, a array with Observers must be
     * passed to check, whether the <observer> link is valid.
     * 
     * @param observers
     *            Array of IObserver that might be linked from this observation, can be <code>NULL</code> if datasource
     *            element is set
     * @param targetElement
     *            The origin XML DOM <target> Element
     * @throws SchemaException
     *             if given targetElement was <code>null</code>
     */
    public DeepSkyTargetAS(Node targetElement, IObserver... observers) throws SchemaException {

        super(targetElement, observers);

        Element target = (Element) targetElement;

        Element child = null;
        NodeList children = null;

        // Getting data

        // Get optional position angle
        children = target.getElementsByTagName(DeepSkyTargetAS.XML_ELEMENT_POSITIONANGLE);
        if (children != null) {
            if (children.getLength() == 1) {
                child = (Element) children.item(0);
                String value = child.getFirstChild().getNodeValue();
                this.setPositionAngle(Integer.parseInt(value));
            } else if (children.getLength() > 1) {
                throw new SchemaException("DeepSkyTargetAS can only have one position angle entry. ");
            }
        }

    }

/**
     * Constructs a new instance of a DeepSkyTargetAS.
     * 
     * @param name
     *            The name of the asterism
     * @param datasource
     *            The datasource of the asterism
     */
    public DeepSkyTargetAS(String name, String datasource) {

        super(name, datasource);

    }

/**
     * Constructs a new instance of a DeepSkyTargetAS.
     * 
     * @param name
     *            The name of the asterism
     * @param observer
     *            The observer who is the originator of the asterism
     */
    public DeepSkyTargetAS(String name, IObserver observer) {

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
     * @see org.w3c.dom.Element
     */
    @Override
    public void addToXmlElement(Element element) {

        if (element == null) {
            return;
        }

        Document ownerDoc = element.getOwnerDocument();

        Element e_DSTarget = this.createXmlDeepSkyTargetElement(element, DeepSkyTargetAS.XML_XSI_TYPE_VALUE);

        // Check if element already exists
        if (e_DSTarget == null) {
            return;
        }

        if (this.positionAngle != -1) {
            Element e_PositionAngle = ownerDoc.createElement(DeepSkyTargetAS.XML_ELEMENT_POSITIONANGLE);
            Node n_PAText = ownerDoc.createTextNode(Integer.toString(this.positionAngle));
            e_PositionAngle.appendChild(n_PAText);

            e_DSTarget.appendChild(e_PositionAngle);
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

        return DeepSkyTargetAS.XML_XSI_TYPE_VALUE;

    }

    // --------------
    // Public methods ----------------------------------------------------
    // --------------

/**
     * Returns the position angle of the asterism.
     * 
     * @return The position angle of the astronomical object as integer The returned value might be <code>-1</code> if
     *         the value was never set
     */
    public int getPositionAngle() {

        return this.positionAngle;

    }

/**
     * Sets the position angle of the asterism. If the given new position angle is < 0 or > 359 the position angle will
     * be unset again.
     * 
     * @param newPosAngle
     *            The new position angle of the asterism.
     */
    public void setPositionAngle(int newPosAngle) {

        if (((newPosAngle > 359) || (newPosAngle < 0))) {
            this.positionAngle = -1;
            return;
        }

        this.positionAngle = newPosAngle;

    }

}
