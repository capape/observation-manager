/*
 * ====================================================================
 * extension/deepSky/DeepSkyTargetOC.java
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
 * DeepSkyTargetOC extends the de.lehmannet.om.extension.deepSky.DeepSkyTarget class.<br>
 * Its specialised for open clusters.<br>
 *
 * @author doergn@users.sourceforge.net
 * @since 1.0
 */
public class DeepSkyTargetOC extends DeepSkyTarget {

    // ---------
    // Constants ---------------------------------------------------------
    // ---------

    // XSML schema instance value. Enables class/schema loaders to identify this
    // class
    public static final String XML_XSI_TYPE_VALUE = "oal:deepSkyOC";

    // Constant for XML representation: stars element name
    private static final String XML_ELEMENT_STARS = "stars";

    // Constant for XML representation: brightest Star element name
    private static final String XML_ELEMENT_BRIGHTESTSTAR = "brightestStar";

    // Constant for XML representation: cluster class element name
    private static final String XML_ELEMENT_CLUSTERCLASSIFICATION = "class";

    // ------------------
    // Instance Variables ------------------------------------------------
    // ------------------

    // The magnitude of the brightest Star in the open cluster
    private double brightestStar = Double.NaN;

    // The amount of stars in the cluster (has to be positiv)
    private int stars = -1;

    // The classificaion (according to Trumpler) of the open cluster
    private String clusterClassification = null;

    // ------------
    // Constructors ------------------------------------------------------
    // ------------

    /**
     * Constructs a new instance of a DeepSkyTargetOC from a given DOM target Element.<br>
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
    public DeepSkyTargetOC(Node targetElement, IObserver... observers) throws SchemaException {

        super(targetElement, observers);

        Element target = (Element) targetElement;

        // Getting data

        // Get optional cluster class
        NodeList children = target.getElementsByTagName(DeepSkyTargetOC.XML_ELEMENT_CLUSTERCLASSIFICATION);
        StringBuilder cclass = new StringBuilder();
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            NodeList textElements = child.getChildNodes();
            if (textElements.getLength() > 0) {
                for (int te = 0; te < textElements.getLength(); te++) {
                    cclass.append(textElements.item(te).getNodeValue());
                }
                // cclass = child.getFirstChild().getNodeValue();
                this.setClusterClassification(cclass.toString());
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("DeepSkyTargetOC can only have one cluster class. ");
        }

        // Get optional stars
        children = target.getElementsByTagName(DeepSkyTargetOC.XML_ELEMENT_STARS);
        int stars = -1;
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            stars = Integer.parseInt(child.getFirstChild().getNodeValue());
            this.setAmountOfStars(stars);
        } else if (children.getLength() > 1) {
            throw new SchemaException("DeepSkyTargetOC can only have one value for amount of stars. ");
        }

        // Get optional brightest stars
        children = target.getElementsByTagName(DeepSkyTargetOC.XML_ELEMENT_BRIGHTESTSTAR);
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            double value = Double.parseDouble(child.getFirstChild().getNodeValue());
            this.setBrightestStar(value);
        } else if (children.getLength() > 1) {
            throw new SchemaException("DeepSkyTargetOC can only have one value for brightest star. ");
        }

    }

    /**
     * Constructs a new instance of a DeepSkyTargetOC.
     *
     * @param name
     *            The name of the open cluster
     * @param datasource
     *            The datasource of the open cluster
     */
    public DeepSkyTargetOC(String name, String datasource) {

        super(name, datasource);

    }

    /**
     * Constructs a new instance of a DeepSkyTargetOC.
     *
     * @param name
     *            The name of the open cluster
     * @param observer
     *            The observer who is the originator of the open cluster
     */
    public DeepSkyTargetOC(String name, IObserver observer) {

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

        Element e_DSTarget = this.createXmlDeepSkyTargetElement(element, DeepSkyTargetOC.XML_XSI_TYPE_VALUE);

        // Check if element already exists
        if (e_DSTarget == null) {
            return;
        }

        if (this.stars != -1) {
            Element e_Stars = ownerDoc.createElement(DeepSkyTargetOC.XML_ELEMENT_STARS);
            Node n_StarsText = ownerDoc.createTextNode(Integer.toString(this.stars));
            e_Stars.appendChild(n_StarsText);

            e_DSTarget.appendChild(e_Stars);
        }

        if (!Double.isNaN(this.brightestStar)) {
            Element e_BrightestStar = ownerDoc.createElement(DeepSkyTargetOC.XML_ELEMENT_BRIGHTESTSTAR);
            Node n_BrightestStarText = ownerDoc.createTextNode(Double.toString(this.brightestStar));
            e_BrightestStar.appendChild(n_BrightestStarText);

            e_DSTarget.appendChild(e_BrightestStar);
        }

        if (this.clusterClassification != null) {
            Element e_Classification = ownerDoc.createElement(DeepSkyTargetOC.XML_ELEMENT_CLUSTERCLASSIFICATION);
            Node n_ClassificationText = ownerDoc.createTextNode(this.clusterClassification);
            e_Classification.appendChild(n_ClassificationText);

            e_DSTarget.appendChild(e_Classification);
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

        return DeepSkyTargetOC.XML_XSI_TYPE_VALUE;

    }

    // --------------
    // Public methods ----------------------------------------------------
    // --------------

    /**
     * Returns the amount of stars in the open cluster.
     *
     * @return The amount of stars in the open cluster. The returned value might be <code>-1</code> if the value was
     *         never set
     */
    public int getAmountOfStars() {

        return this.stars;

    }

    /**
     * Returns the classification (according to Trumpler) of the open cluster.
     *
     * @return The classification of the open cluster (according to Trumpler) The returned value might be
     *         <code>null</code> if the value was never set
     */
    public String getClusterClassification() {

        return this.clusterClassification;

    }

    /**
     * Returns the magnitude of the brightest star in the open cluster.
     *
     * @return The magnitude of the brightest star in the open cluster. The returned value might be
     *         <code>Double.NaN</code> if the value was never set
     */
    public double getBrightestStar() {

        return this.brightestStar;

    }

    /**
     * Sets the amount of stars in the open cluster. All passed values lower than 1 are treated as -1, which means that
     * the value was never set.
     *
     * @param newAmountOfStars
     *            The new amount of stars in the open cluster
     */
    public void setAmountOfStars(int newAmountOfStars) {

        if (newAmountOfStars < 1) {
            this.stars = -1;
        } else {

            this.stars = newAmountOfStars;
        }

    }

    /**
     * Sets the magnitude of the brightest star in the open cluster
     *
     * @param newBrightestStar
     *            The new magnitude of the birghtest star in the open cluster
     */
    public void setBrightestStar(double newBrightestStar) {

        this.brightestStar = newBrightestStar;

    }

    /**
     * Sets the cluster classification (according to Trumpler).
     *
     * @param newClassification
     *            The new cluster classification as String
     */
    public void setClusterClassification(String newClassification) {

        if ((newClassification != null) && ("".equals(newClassification.trim()))) {
            this.clusterClassification = null;
            return;
        }

        this.clusterClassification = newClassification;

    }

}