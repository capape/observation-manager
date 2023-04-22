/* ====================================================================
 * extension/deepSky/DeepSkyTargetGX.java
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
 * DeepSkyTargetGX extends the de.lehmannet.om.extension.deepSky.DeepSkyTarget class.<br>
 * Its specialised for Galaxies.<br>
 *
 * @author doergn@users.sourceforge.net
 *
 * @since 1.0
 */
public class DeepSkyTargetGX extends DeepSkyTarget {

    // ---------
    // Constants ---------------------------------------------------------
    // ---------

    // XSML schema instance value. Enables class/schema loaders to identify this
    // class
    public static final String XML_XSI_TYPE_VALUE = "oal:deepSkyGX";

    // Constant for XML representation: hubble type element name
    private static final String XML_ELEMENT_HUBBLETYPE = "hubbleType";

    // Constant for XML representation: position angle element name
    private static final String XML_ELEMENT_POSITIONANGLE = "pa";

    // ------------------
    // Instance Variables ------------------------------------------------
    // ------------------

    // The Hubble type of the object
    private String hubbleType = null;

    // The (large axis) position angle of the object (only positiv values allowed)
    private int positionAngle = -1;

    // ------------
    // Constructors ------------------------------------------------------
    // ------------

    /**
     * Constructs a new instance of a DeepSkyTargetGX from a given DOM target Element.<br>
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
    public DeepSkyTargetGX(Node targetElement, IObserver... observers) throws SchemaException {

        super(targetElement, observers);

        Element target = (Element) targetElement;

        // Getting data

        // Get optional position angle
        NodeList children = target.getElementsByTagName(DeepSkyTargetGX.XML_ELEMENT_POSITIONANGLE);
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            String value = child.getFirstChild().getNodeValue();
            this.setPositionAngle(Integer.parseInt(value));
        } else if (children.getLength() > 1) {
            throw new SchemaException("DeepSkyTargetGX can only have one position angle entry. ");
        }

        // Get optional hubble type
        children = target.getElementsByTagName(DeepSkyTargetGX.XML_ELEMENT_HUBBLETYPE);
        StringBuilder hubble = new StringBuilder();
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            NodeList textElements = child.getChildNodes();
            if (textElements.getLength() > 0) {
                for (int te = 0; te < textElements.getLength(); te++) {
                    hubble.append(textElements.item(te).getNodeValue());
                }
                // hubble = child.getFirstChild().getNodeValue();
                this.setHubbleType(hubble.toString());
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("DeepSkyTargetGX can only have one hubble type entry. ");
        }

    }

    /**
     * Constructs a new instance of a DeepSkyTargetGX.
     *
     * @param name
     *            The name of the galaxy
     * @param datasource
     *            The datasource of the galaxy
     */
    public DeepSkyTargetGX(String name, String datasource) {

        super(name, datasource);

    }

    /**
     * Constructs a new instance of a DeepSkyTargetGX.
     *
     * @param name
     *            The name of the galaxy
     * @param observer
     *            The observer who is the originator of the galaxy
     */
    public DeepSkyTargetGX(String name, IObserver observer) {

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

        Element e_DSTarget = this.createXmlDeepSkyTargetElement(element, DeepSkyTargetGX.XML_XSI_TYPE_VALUE);

        // Check if element already exists
        if (e_DSTarget == null) {
            return;
        }

        if (this.hubbleType != null) {
            Element e_HubbleType = ownerDoc.createElement(DeepSkyTargetGX.XML_ELEMENT_HUBBLETYPE);
            Node n_HubbleTypeText = ownerDoc.createTextNode(this.hubbleType);
            e_HubbleType.appendChild(n_HubbleTypeText);

            e_DSTarget.appendChild(e_HubbleType);
        }

        if (this.positionAngle != -1) {
            Element e_PositionAngle = ownerDoc.createElement(DeepSkyTargetGX.XML_ELEMENT_POSITIONANGLE);
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

        return DeepSkyTargetGX.XML_XSI_TYPE_VALUE;

    }

    // --------------
    // Public methods ----------------------------------------------------
    // --------------

    /**
     * Returns the large axis position angle of the galaxy.
     *
     * @return The large axis position angle of the astronomical object as integer The returned value might be
     *         <code>-1</code> if the value was never set
     */
    public int getPositionAngle() {

        return this.positionAngle;

    }

    /**
     * Returns the hubble type of the galaxy.
     *
     * @return The hubble type of the galaxy as String The returned value might be <code>null</code> if the value was
     *         never set
     */
    public String getHubbleType() {

        return this.hubbleType;

    }

    /**
     * Sets the large axis position angle of the galaxy. If the given new position angle is < 0 or > 359 the position
     * angle will be unset again.
     *
     * @param newPosAngle
     *            The new position angle of the galaxy.
     */
    public void setPositionAngle(int newPosAngle) {

        if (((newPosAngle > 359) || (newPosAngle < 0))) {
            this.positionAngle = -1;
            return;
        }

        this.positionAngle = newPosAngle;

    }

    /**
     * Sets the hubble type of the galaxy.
     *
     * @param newHubbleType
     *            The new hubble type of the galaxy as String
     */
    public void setHubbleType(String newHubbleType) {

        if ((newHubbleType != null) && ("".equals(newHubbleType.trim()))) {
            this.hubbleType = null;
            return;
        }

        this.hubbleType = newHubbleType;

    }

}
