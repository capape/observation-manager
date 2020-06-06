/* ====================================================================
 * extension/deepSky/DeepSkyTargetGC.java
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
 * DeepSkyTargetGC extends the de.lehmannet.om.extension.deepSky.DeepSkyTarget class.<br>
 * Its specialised for globular clusters.<br>
 *
 * @author doergn@users.sourceforge.net
 * @since 1.0
 */
public class DeepSkyTargetGC extends DeepSkyTarget {

    // ---------
    // Constants ---------------------------------------------------------
    // ---------

    // XSML schema instance value. Enables class/schema loaders to identify this
    // class
    public static final String XML_XSI_TYPE_VALUE = "oal:deepSkyGC";

    // Constant for XML representation: magstars element name
    private static final String XML_ELEMENT_MAGSTARS = "magStars";

    // Constant for XML representation: concentration element name
    private static final String XML_ELEMENT_CONCENTRATION = "conc";

    // ------------------
    // Instance Variables ------------------------------------------------
    // ------------------

    // The magnitude of the stars in the galactic cluster
    private double magnitude = Double.NaN;

    private String concentration = null;

    // ------------
    // Constructors ------------------------------------------------------
    // ------------

    /**
     * Constructs a new instance of a DeepSkyTargetGC from a given DOM target Element.<br>
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
    public DeepSkyTargetGC(Node targetElement, IObserver... observers) throws SchemaException {

        super(targetElement, observers);

        Element target = (Element) targetElement;

        // Getting data

        // Get optional magnitude
        NodeList children = target.getElementsByTagName(DeepSkyTargetGC.XML_ELEMENT_MAGSTARS);
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            magnitude = Double.parseDouble(child.getFirstChild().getNodeValue());
            this.setMagnitude(magnitude);
        } else if (children.getLength() > 1) {
            throw new SchemaException("DeepSkyTargetGC can only have one value for magnitude. ");
        }

        // Get optional concentration
        children = target.getElementsByTagName(DeepSkyTargetGC.XML_ELEMENT_CONCENTRATION);
        StringBuilder conc = new StringBuilder();
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            NodeList textElements = child.getChildNodes();
            if (textElements.getLength() > 0) {
                for (int te = 0; te < textElements.getLength(); te++) {
                    conc.append(textElements.item(te).getNodeValue());
                }
                // conc = child.getFirstChild().getNodeValue();
                this.setConcentration(conc.toString());
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("DeepSkyTargetGC can only have one concentration. ");
        }

    }

    /**
     * Constructs a new instance of a DeepSkyTargetGC.
     *
     * @param name
     *            The name of the galactic cluster
     * @param datasource
     *            The datasource of the galactic cluster
     */
    public DeepSkyTargetGC(String name, String datasource) {

        super(name, datasource);

    }

    /**
     * Constructs a new instance of a DeepSkyTargetGC.
     *
     * @param name
     *            The name of the galactic cluster
     * @param observer
     *            The observer who is the originator of the galactic cluster
     */
    public DeepSkyTargetGC(String name, IObserver observer) {

        super(name, observer);

    }

    // ------
    // Target ------------------------------------------------------------
    // ------

    /**
     * Adds this Target to a given parent XML DOM Element. The Target element will be set as a child element of the
     * passed element.
     *
     * @param element
     *            The parent element for this Target
     * @see org.w3c.dom.Element
     */
    @Override
    public void addToXmlElement(Element element) {

        if (element == null) {
            return;
        }

        Document ownerDoc = element.getOwnerDocument();

        Element e_DSTarget = this.createXmlDeepSkyTargetElement(element, DeepSkyTargetGC.XML_XSI_TYPE_VALUE);

        // Check if element already exists
        if (e_DSTarget == null) {
            return;
        }

        if (!Double.isNaN(this.magnitude)) {
            Element e_Magnitude = ownerDoc.createElement(DeepSkyTargetGC.XML_ELEMENT_MAGSTARS);
            Node n_MagnitudeText = ownerDoc.createTextNode(Double.toString(this.magnitude));
            e_Magnitude.appendChild(n_MagnitudeText);

            e_DSTarget.appendChild(e_Magnitude);
        }

        if (this.concentration != null) {
            Element e_Concentration = ownerDoc.createElement(DeepSkyTargetGC.XML_ELEMENT_CONCENTRATION);
            Node n_ConcentrationText = ownerDoc.createTextNode(this.concentration);
            e_Concentration.appendChild(n_ConcentrationText);

            e_DSTarget.appendChild(e_Concentration);
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

        return DeepSkyTargetGC.XML_XSI_TYPE_VALUE;

    }

    // --------------
    // Public methods ----------------------------------------------------
    // --------------

    /**
     * Returns the concentration of the stars in the galactic cluster.
     *
     * @return Description of the concentration of the galactic cluster
     */
    public String getConcentration() {

        return this.concentration;

    }

    /**
     * Returns the magnitude of the stars in the galactic cluster.
     *
     * @return The magnitude of the stars in the galactic cluster. The returned value might be <code>Double.NaN</code>
     *         if the value was never set
     */
    public double getMagnitude() {

        return this.magnitude;

    }

    /**
     * Sets the concentration of the galactic cluster.
     *
     * @param newConcentration
     *            The new concentration of the stars in the galactic cluster
     */
    public void setConcentration(String newConcentration) {

        if ((newConcentration != null) && ("".equals(newConcentration.trim()))) {
            this.concentration = null;
            return;
        }

        this.concentration = newConcentration;

    }

    /**
     * Sets the magnitude of the stars in the galactic cluster
     *
     * @param newMagnitude
     *            The new magnitude of the stars in the galactic cluster
     */
    public void setMagnitude(double newMagnitude) {

        this.magnitude = newMagnitude;

    }

}