/* ====================================================================
 * extension/deepSky/DeepSkyTargetPN.java
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
 * DeepSkyTargetPN extends the de.lehmannet.om.extension.deepSky.DeepSkyTarget class.<br>
 * Its specialised for planetary nebulaes.<br>
 *
 * @author doergn@users.sourceforge.net
 *
 * @since 1.0
 */
public class DeepSkyTargetPN extends DeepSkyTarget {

    // ---------
    // Constants ---------------------------------------------------------
    // ---------

    // XSML schema instance value. Enables class/schema loaders to identify this
    // class
    public static final String XML_XSI_TYPE_VALUE = "oal:deepSkyPN";

    // Constant for XML representation: magstar element name
    private static final String XML_ELEMENT_MAGCENTRALSTAR = "magStar";

    // ------------------
    // Instance Variables ------------------------------------------------
    // ------------------

    // The magnitude of the central star
    private double magnitude = Double.NaN;

    // ------------
    // Constructors ------------------------------------------------------
    // ------------

    /**
     * Constructs a new instance of a DeepSkyTargetPN from a given DOM target Element.<br>
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
    public DeepSkyTargetPN(Node targetElement, IObserver... observers) throws SchemaException {

        super(targetElement, observers);

        Element target = (Element) targetElement;

        // Getting data

        // Get optional magnitude
        NodeList children = target.getElementsByTagName(DeepSkyTargetPN.XML_ELEMENT_MAGCENTRALSTAR);
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            magnitude = Double.parseDouble(child.getFirstChild().getNodeValue());
            this.setCentralStarMagnitude(magnitude);
        } else if (children.getLength() > 1) {
            throw new SchemaException("DeepSkyTargetPN can only have one value for central star magnitude. ");
        }

    }

    /**
     * Constructs a new instance of a DeepSkyTargetPN.
     *
     * @param name
     *            The name of the planetary nebulae
     * @param datasource
     *            The datasource of the planetary nebulae
     */
    public DeepSkyTargetPN(String name, String datasource) {

        super(name, datasource);

    }

    /**
     * Constructs a new instance of a DeepSkyTargetPN.
     *
     * @param name
     *            The name of the planetary nebulae
     * @param observer
     *            The observer who is the originator of the planetary nebulae
     */
    public DeepSkyTargetPN(String name, IObserver observer) {

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

        Element e_DSTarget = this.createXmlDeepSkyTargetElement(element, DeepSkyTargetPN.XML_XSI_TYPE_VALUE);

        // Check if element already exists
        if (e_DSTarget == null) {
            return;
        }

        if (!Double.isNaN(this.magnitude)) {
            Element e_Magnitude = ownerDoc.createElement(DeepSkyTargetPN.XML_ELEMENT_MAGCENTRALSTAR);
            Node n_MagnitudeText = ownerDoc.createTextNode(Double.toString(this.magnitude));
            e_Magnitude.appendChild(n_MagnitudeText);

            e_DSTarget.appendChild(e_Magnitude);
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

        return DeepSkyTargetPN.XML_XSI_TYPE_VALUE;

    }

    // --------------
    // Public methods ----------------------------------------------------
    // --------------

    /**
     * Returns the magnitude of the central star
     *
     * @return The magnitude of the central star The returned value might be <code>Double.NaN</code> if the value was
     *         never set
     */
    public double getCentralStarMagnitude() {

        return this.magnitude;

    }

    /**
     * Sets the magnitude of the central star
     *
     * @param newMagnitude
     *            The new magnitude of the central star
     */
    public void setCentralStarMagnitude(double newMagnitude) {

        this.magnitude = newMagnitude;

    }

}