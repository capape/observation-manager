/* ====================================================================
 * extension/deepSky/DeepSkyTargetGN.java
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
 * DeepSkyTargetGN extends the de.lehmannet.om.extension.deepSky.DeepSkyTarget class.<br>
 * Its specialised for galactic nebulaes.<br>
 * 
 * @author doergn@users.sourceforge.net
 * @since 1.0
 */
public class DeepSkyTargetGN extends DeepSkyTarget {

    // ---------
    // Constants ---------------------------------------------------------
    // ---------

    // XSML schema instance value. Enables class/schema loaders to identify this
    // class
    public static final String XML_XSI_TYPE_VALUE = "oal:deepSkyGN";

    // Constant for XML representation: nebula type element name
    private static final String XML_ELEMENT_NEBULATYPE = "nebulaType";

    // Constant for XML representation: position angle element name
    private static final String XML_ELEMENT_POSITIONANGLE = "pa";

    // ------------------
    // Instance Variables ------------------------------------------------
    // ------------------

    // The nebula type (e.g. emission, reflection, dark nebula...)
    private String nebulaType = null;

    // The large axis position angle of the object (only positiv values allowed)
    private int positionAngle = -1;

    // ------------
    // Constructors ------------------------------------------------------
    // ------------

    /**
     * Constructs a new instance of a DeepSkyTargetGN from a given DOM target Element.<br>
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
    public DeepSkyTargetGN(Node targetElement, IObserver... observers) throws SchemaException {

        super(targetElement, observers);

        Element target = (Element) targetElement;

        // Getting data

        // Get optional position angle
        NodeList children = target.getElementsByTagName(DeepSkyTargetGN.XML_ELEMENT_POSITIONANGLE);
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            String value = child.getFirstChild().getNodeValue();
            this.setPositionAngle(Integer.parseInt(value));
        } else if (children.getLength() > 1) {
            throw new SchemaException("DeepSkyTargetGN can only have one position angle entry. ");
        }

        // Get optional nebula type
        children = target.getElementsByTagName(DeepSkyTargetGN.XML_ELEMENT_NEBULATYPE);
        StringBuilder nebulaType = new StringBuilder();
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            NodeList textElements = child.getChildNodes();
            if (textElements.getLength() > 0) {
                for (int te = 0; te < textElements.getLength(); te++) {
                    nebulaType.append(textElements.item(te).getNodeValue());
                }
                // nebulaType = child.getFirstChild().getNodeValue();
                this.setNebulaType(nebulaType.toString());
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("DeepSkyTargetGN can only have one nebula type. ");
        }

    }

    /**
     * Constructs a new instance of a DeepSkyTargetGN.
     * 
     * @param name
     *            The name of the galactic nebula
     * @param datasource
     *            The datasource of the galactic nebula
     */
    public DeepSkyTargetGN(String name, String datasource) {

        super(name, datasource);

    }

    /**
     * Constructs a new instance of a DeepSkyTargetGN.
     * 
     * @param name
     *            The name of the galactic nebula
     * @param observer
     *            The observer who is the originator of the galactic nebula
     */
    public DeepSkyTargetGN(String name, IObserver observer) {

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

        Element e_DSTarget = this.createXmlDeepSkyTargetElement(element, DeepSkyTargetGN.XML_XSI_TYPE_VALUE);

        // Check if element already exists
        if (e_DSTarget == null) {
            return;
        }

        Document ownerDoc = e_DSTarget.getOwnerDocument();

        if (this.nebulaType != null) {
            Element e_NebulaType = ownerDoc.createElement(DeepSkyTargetGN.XML_ELEMENT_NEBULATYPE);
            Node n_NebulaTypeText = ownerDoc.createTextNode(this.nebulaType);
            e_NebulaType.appendChild(n_NebulaTypeText);

            e_DSTarget.appendChild(e_NebulaType);
        }

        if (this.positionAngle != -1) {
            Element e_PositionAngle = ownerDoc.createElement(DeepSkyTargetGN.XML_ELEMENT_POSITIONANGLE);
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

        return DeepSkyTargetGN.XML_XSI_TYPE_VALUE;

    }

    // --------------
    // Public methods ----------------------------------------------------
    // --------------

    /**
     * Returns the large axis position angle of the galactic nebula.
     * 
     * @return The position angle of the astronomical object as integer The returned value might be <code>-1</code> if
     *         the value was never set
     */
    public int getPositionAngle() {

        return this.positionAngle;

    }

    /**
     * Returns the nebula type. Nebular type might be something like e.g. emission, reflection, dark nebula
     * 
     * @return The nebula type as String The returned value might be <code>null</code> if the value was never set
     */
    public String getNebulaType() {

        return this.nebulaType;

    }

    /**
     * Sets the large axis position angle of the galactic nebula. If the given new position angle is < 0 or > 359 the
     * position angle will be unset again.
     * 
     * @param newPosAngle
     *            The new position angle of the galactic nebula.
     */
    public void setPositionAngle(int newPosAngle) {

        if (((newPosAngle > 359) || (newPosAngle < 0))) {
            this.positionAngle = -1;
            return;
        }

        this.positionAngle = newPosAngle;

    }

    /**
     * Sets the nebula type. E.g. emission, reflection, dark nebula...
     * 
     * @param newNebulaType
     *            The new nebula type
     */
    public void setNebulaType(String newNebulaType) {

        if ((newNebulaType != null) && ("".equals(newNebulaType.trim()))) {
            this.nebulaType = null;
            return;
        }

        this.nebulaType = newNebulaType;

    }

}
