/*
 * ====================================================================
 * extension/deepSky/DeepSkyTargetDS.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.extension.deepSky;

import de.lehmannet.om.Angle;
import de.lehmannet.om.IObserver;
import de.lehmannet.om.util.SchemaException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * DeepSkyTargetDS extends the de.lehmannet.om.extension.deepSky.DeepSkyTarget class.<br>
 * Its specialised for double stars.<br>
 *
 * @author doergn@users.sourceforge.net
 * @since 1.0
 */
public class DeepSkyTargetDS extends DeepSkyTarget {

    // ---------
    // Constants ---------------------------------------------------------
    // ---------

    // XSML schema instance value. Enables class/schema loaders to identify this
    // class
    public static final String XML_XSI_TYPE_VALUE = "oal:deepSkyDS";

    // Constant for XML representation: separation element name
    private static final String XML_ELEMENT_SEPARATION = "separation";

    // Constant for XML representation: position angle element name
    private static final String XML_ELEMENT_POSITIONANGLE = "pa";

    // Constant for XML representation: magnitude of companion star
    private static final String XML_ELEMENT_MAGCOMP = "magComp";

    // ------------------
    // Instance Variables ------------------------------------------------
    // ------------------

    // The separation between the double star components (only positiv values
    // allowed)
    private Angle separation = null;

    // The position angle of the object (only positiv values allowed)
    private int positionAngle = -1;

    // The magnitude of the companion star
    private double magComp = Double.NaN;

    // ------------
    // Constructors ------------------------------------------------------
    // ------------

    /**
     * Constructs a new instance of a DeepSkyTargetDS from a given DOM target Element.<br>
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
    public DeepSkyTargetDS(Node targetElement, IObserver... observers) throws SchemaException {

        super(targetElement, observers);

        Element target = (Element) targetElement;

        // Getting data

        // Get optional position angle
        NodeList children = target.getElementsByTagName(DeepSkyTargetDS.XML_ELEMENT_POSITIONANGLE);
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            String value = child.getFirstChild().getNodeValue();
            this.setPositionAngle(Integer.parseInt(value));
        } else if (children.getLength() > 1) {
            throw new SchemaException("DeepSkyTargetDS can only have one position angle entry. ");
        }

        // Get optional separation
        children = target.getElementsByTagName(DeepSkyTargetDS.XML_ELEMENT_SEPARATION);
        Angle separation = null;
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            String value = child.getFirstChild().getNodeValue();
            String unit = child.getAttribute(Angle.XML_ATTRIBUTE_UNIT);
            separation = new Angle(Double.parseDouble(value), unit);
            this.setSeparation(separation);
        } else if (children.getLength() > 1) {
            throw new SchemaException("DeepSkyTargetDS can only have one separation value. ");
        }

        // Get optional magComp
        children = target.getElementsByTagName(DeepSkyTargetDS.XML_ELEMENT_MAGCOMP);
        double mc = Double.NaN;

        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            mc = Double.parseDouble(child.getFirstChild().getNodeValue());
            this.setCompanionMag(mc);
        } else if (children.getLength() > 1) {
            throw new SchemaException("DeepSkyTargetDS can only have one separation value. ");
        }
    }

    /**
     * Constructs a new instance of a DeepSkyTargetDS.
     *
     * @param name
     *            The name of the double star
     * @param datasource
     *            The datasource of the double star
     */
    public DeepSkyTargetDS(String name, String datasource) {

        super(name, datasource);
    }

    /**
     * Constructs a new instance of a DeepSkyTargetDS.
     *
     * @param name
     *            The name of the double star
     * @param observer
     *            The observer who is the originator of the double star
     */
    public DeepSkyTargetDS(String name, IObserver observer) {

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

        Element e_DSTarget = this.createXmlDeepSkyTargetElement(element, DeepSkyTargetDS.XML_XSI_TYPE_VALUE);

        // Check if element already exists
        if (e_DSTarget == null) {
            return;
        }

        if (this.separation != null) {
            Element e_Separation = ownerDoc.createElement(DeepSkyTargetDS.XML_ELEMENT_SEPARATION);
            e_Separation = this.separation.setToXmlElement(e_Separation);

            e_DSTarget.appendChild(e_Separation);
        }

        if (this.positionAngle != -1) {
            Element e_PositionAngle = ownerDoc.createElement(DeepSkyTargetDS.XML_ELEMENT_POSITIONANGLE);
            Node n_PAText = ownerDoc.createTextNode(Integer.toString(this.positionAngle));
            e_PositionAngle.appendChild(n_PAText);

            e_DSTarget.appendChild(e_PositionAngle);
        }

        if (!Double.isNaN(this.magComp)) {
            Element e_MagComp = ownerDoc.createElement(DeepSkyTargetDS.XML_ELEMENT_MAGCOMP);
            Node n_MagnitudeText = ownerDoc.createTextNode(Double.toString(this.magComp));
            e_MagComp.appendChild(n_MagnitudeText);

            e_DSTarget.appendChild(e_MagComp);
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

        return DeepSkyTargetDS.XML_XSI_TYPE_VALUE;
    }

    // --------------
    // Public methods ----------------------------------------------------
    // --------------

    /**
     * Returns the position angle of the double star.
     *
     * @return The position angle of the astronomical object as integer The returned value might be <code>-1</code> if
     *         the value was never set
     */
    public int getPositionAngle() {

        return this.positionAngle;
    }

    /**
     * Returns the separation of the double star components.
     *
     * @return The separation of the double star components as Angle The returned value might be <code>null</code> if
     *         the value was never set
     * @see de.lehmannet.om.Angle
     */
    public Angle getSeparation() {

        return this.separation;
    }

    /**
     * Returns the magnitude of the companion star.
     *
     * @return The magnitude of the companion star. The returned value might be <code>Double.NaN</code> if the value was
     *         never set
     */
    public double getCompanionMag() {

        return this.magComp;
    }

    /**
     * Sets the magnitude of the companion star.
     *
     * @param newMagnitude
     *            The new magnitude of the companion star.
     */
    public void setCompanionMag(double newMagnitude) {

        this.magComp = newMagnitude;
    }

    /**
     * Sets the position angle of the double star. If the given new position angle is < 0 or > 359 the position angle
     * will be unset again.
     *
     * @param newPosAngle
     *            The new position angle of the double star.
     */
    public void setPositionAngle(int newPosAngle) {

        if (((newPosAngle > 359) || (newPosAngle < 0))) {
            this.positionAngle = -1;
            return;
        }

        this.positionAngle = newPosAngle;
    }

    /**
     * Sets the separation of the double star components.<br>
     * Only positiv angles are allowed.
     *
     * @param newSeparation
     *            The new separation of the double star components.
     * @throws IllegalArgumentException
     *             If new angle doesn't have a degree value between 0 and 359.9
     * @see de.lehmannet.om.Angle
     */
    public void setSeparation(Angle newSeparation) {

        Angle copy = null;
        if (newSeparation != null) {
            // Make copy to ensure we're not changing the origin unit
            copy = new Angle(newSeparation.getValue(), newSeparation.getUnit());
            if (((newSeparation.toDegree() > 359.9) || (newSeparation.toDegree() < 0))) {
                throw new IllegalArgumentException(
                        "DeepSkyTargetDS: Separation must have a degree value between 0-359.9 ");
            }
        }

        this.separation = copy;
    }
}
