/* ====================================================================
 * extension/deepSky/DeepSkyTargetCG.java
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
import de.lehmannet.om.util.FloatUtil;
import de.lehmannet.om.util.SchemaException;

/**
 * DeepSkyTargetCG extends the de.lehmannet.om.extension.deepSky.DeepSkyTarget class.<br>
 * Its specialised for clusters of galaxies.<br>
 * 
 * @author doergn@users.sourceforge.net
 * @since 2.0
 */
public class DeepSkyTargetCG extends DeepSkyTarget {

    // ---------
    // Constants ---------------------------------------------------------
    // ---------

    // XSML schema instance value. Enables class/schema loaders to identify this
    // class
    public static final String XML_XSI_TYPE_VALUE = "oal:deepSkyCG";

    // Constant for XML representation: magnitude of the 10th brightest member
    private static final String XML_ELEMENT_MAG10 = "mag10";

    // ------------------
    // Instance Variables ------------------------------------------------
    // ------------------

    // Magnitude of the 10th brightest member in [mag]
    private float magTen = Float.NaN;

    // ------------
    // Constructors ------------------------------------------------------
    // ------------

/**
     * Constructs a new instance of a DeepSkyTargetCG from a given DOM target Element.<br>
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
    public DeepSkyTargetCG(Node targetElement, IObserver... observers) throws SchemaException {

        super(targetElement, observers);

        Element target = (Element) targetElement;

        Element child = null;
        NodeList children = null;

        // Getting data

        // Get optional magTen
        children = target.getElementsByTagName(DeepSkyTargetCG.XML_ELEMENT_MAG10);
        if (children != null) {
            if (children.getLength() == 1) {
                child = (Element) children.item(0);
                String value = child.getFirstChild().getNodeValue();
                this.setMagnitudeOf10thBrightestMember(FloatUtil.parseFloat(value));
            } else if (children.getLength() > 1) {
                throw new SchemaException("DeepSkyTargetCG can only have one mag10 entry. ");
            }
        }

    }

/**
     * Constructs a new instance of a DeepSkyTargetCG.
     * 
     * @param name
     *            The name of the cluster of galaxies
     * @param datasource
     *            The datasource of the cluster of galaxies
     */
    public DeepSkyTargetCG(String name, String datasource) {

        super(name, datasource);

    }

/**
     * Constructs a new instance of a DeepSkyTargetCG.
     * 
     * @param name
     *            The name of the cluster of galaxies
     * @param observer
     *            The observer who is the originator of the cluster of galaxies
     */
    public DeepSkyTargetCG(String name, IObserver observer) {

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

        Element e_DSTarget = this.createXmlDeepSkyTargetElement(element, DeepSkyTargetCG.XML_XSI_TYPE_VALUE);

        // Check if element already exists
        if (e_DSTarget == null) {
            return;
        }

        if (!Float.isNaN(this.magTen)) {
            Element e_magTen = ownerDoc.createElement(DeepSkyTargetCG.XML_ELEMENT_MAG10);
            Node n_magTenText = ownerDoc.createTextNode(Float.toString(this.magTen));
            e_magTen.appendChild(n_magTenText);

            e_DSTarget.appendChild(e_magTen);
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

        return DeepSkyTargetCG.XML_XSI_TYPE_VALUE;

    }

    // --------------
    // Public methods ----------------------------------------------------
    // --------------

/**
     * Returns the magnitude of the 10th brightest member in mag.
     * 
     * @return The magnitude of the 10th brightest member in mag. The returned value might be <code>Float.NaN</code> if
     *         the value was never set
     */
    public float getMagnitudeOf10thBrightestMember() {

        return this.magTen;

    }

/**
     * Sets the magnitude of the 10th brightest member in mag
     * 
     * @param magTen
     *            The magnitude of the 10th brightest member in mag
     */
    public void setMagnitudeOf10thBrightestMember(float magTen) {

        this.magTen = magTen;

    }

}
