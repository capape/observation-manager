/*
 * ====================================================================
 * /Angle.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om;

import java.util.Locale;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.lehmannet.om.util.SchemaException;

/**
 * Angle is a wrapper class for angles used in the XML Schema definition. It stores an angle as double value along with
 * the unit of the angle. All possible units for angles can be accessed by this objects constants.
 *
 * @author doergn@users.sourceforge.net
 * @since 1.0
 */
public class Angle {

    // ---------
    // Constants ---------------------------------------------------------
    // ---------

    /**
     * Angle unit degree. A degree is the 360th part of a circle. A degree can be divided in 60 arcseconds. In radiant
     * values a degree is <code>degreeValue/180*PI</code>.
     */
    public static final String DEGREE = "deg";

    /**
     * Angle unit radiant. In degree values a radiant is <code>radiantValue/PI*180</code>.
     */
    public static final String RADIANT = "rad";

    /**
     * Angle unit arcminute. A arcminute is the 60th part of a degree.
     */
    public static final String ARCMINUTE = "arcmin";

    /**
     * Angle unit arcsecond. A arcsecond is the 60th part of a arcminute.
     */
    public static final String ARCSECOND = "arcsec";

    /**
     * XML Attribute name for unit
     */
    public static final String XML_ATTRIBUTE_UNIT = "unit";

    // ------------------
    // Instance Variables ------------------------------------------------
    // ------------------

    /* The angles value */
    private double value = Double.NaN;

    /* The angles value unit */
    private String unit = DEGREE;

    // ------------
    // Constructors ------------------------------------------------------
    // ------------

    /**
     * Constructs a new instance of an Angle from a given DOM target Element.<br>
     *
     * @param angleNode
     *            The origin XML DOM angle Element
     * @throws SchemaException
     *             if given angleNode was <code>null</code>
     */
    public Angle(Node angleNode) throws SchemaException {

        if (angleNode == null) {
            throw new SchemaException("Angle node cannot be NULL ");
        }

        Element angle = (Element) angleNode;

        String unit = angle.getAttribute(Angle.XML_ATTRIBUTE_UNIT).trim();
        if (isTextADoubleUnit(unit)) {
            try {
                String textNode = readTextFromElement(angle);
                this.value = Double.parseDouble(textNode);
            } catch (NumberFormatException e) {
                this.value = 0.0d;
            }
            this.unit = unit;
        } else {
            throw new SchemaException("Angle unit is unknown. ");
        }

    }

    private boolean isTextADoubleUnit(String unit) {
        return (unit.equals(Angle.RADIANT)) || (unit.equals(Angle.DEGREE)) || (unit.equals(Angle.ARCSECOND))
                || (unit.equals(Angle.ARCMINUTE));
    }

    private String readTextFromElement(Element element) {
        StringBuffer textNode = new StringBuffer();
        NodeList textElements = element.getChildNodes();
        if (textElements.getLength() > 0) {
            for (int te = 0; te < textElements.getLength(); te++) {
                textNode.append(textElements.item(te).getNodeValue());
            }
        }
        return textNode.toString().trim();
    }

    /*
     * Creates a new Angle instace.
     *
     * @param value The angles value
     *
     * @param unit Format of the value
     *
     * @throws IllegalArgumentException if unit parameter has unknwon type. Allowed types can be accessed by public
     * constants.<br>
     */
    public Angle(double value, String unit) throws IllegalArgumentException {

        if (DEGREE.equals(unit) || RADIANT.equals(unit) || ARCMINUTE.equals(unit) || ARCSECOND.equals(unit)) {
            this.unit = unit;
        }

        if (unit.equals(DEGREE)) {
            this.value = value % 360; // Sets value between 0-359.9
        } else {
            this.value = value;
        }

    }

    // ------
    // Object ------------------------------------------------------------
    // ------

    /**
     * Overwrittes toString() method from java.lang.Object.<br>
     * Returns the value of this Angle followed by its unit.<br>
     * Example:<br>
     * 42.0 DEGREE
     *
     * @return The angle value followed by its unit
     * @see java.lang.Object
     */
    @Override
    public String toString() {

        return value + " " + unit.toUpperCase(Locale.getDefault());

    }

    /**
     * Overwrittes equals(Object) method from java.lang.Object.<br>
     * Checks if this Angle and the given Object are equal. Two Angles are equal if both have the same value when
     * represented as degree based angle.<br>
     *
     * @param obj
     *            The Object to compare this Angle with.
     * @return <code>true</code> if both Objects are instances from class Angle and their values in degrees is equal.
     * @see java.lang.Object
     */
    @Override
    public boolean equals(Object obj) {

        // TODO: review equals implementation
        if (!(obj instanceof Angle)) {
            return false;
        }

        Angle original = (Angle) obj;

        // Make copy of obj so that we can call toDegree() method, without modifying the
        // original object
        Angle angle = new Angle(original.getValue(), original.getUnit());

        double degree = angle.toDegree();

        return Double.valueOf(degree).equals(value);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((unit == null) ? 0 : unit.hashCode());
        long temp;
        temp = Double.doubleToLongBits(value);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    // --------------
    // Public methods ----------------------------------------------------
    // --------------

    /**
     * Sets this Angle to an given XML DOM Element.<br>
     * The Angles value will be set as the Elements value and the Angles unit will be set as the Elements attribute.<br>
     * Example:<br>
     * &lt;element unit="degree"&gt;40.3&lt;/element&gt;
     *
     * @param element
     *            The XML DOM Element this Angle belongs to
     * @return Returns the element given as parameter with this Angles value as element value and this Angles unit as
     *         attribute
     * @see org.w3c.dom.Element
     */
    public Element setToXmlElement(Element element) {

        if (element == null) {
            return null;
        }

        element.setAttribute(XML_ATTRIBUTE_UNIT, unit);
        Node n_ValueText = element.getOwnerDocument().createTextNode(Double.toString(value));
        element.appendChild(n_ValueText);

        return element;

    }

    /**
     * Returns the unit of the angles value.<br>
     * The returned value may be any valid Angle unit, which can be accessed by Angles constants.
     *
     * @return This angles value unit.
     */
    public String getUnit() {

        return unit;

    }

    /**
     * Returns the value of this angle.<br>
     * The returned value may be any double value (positiv or negativ).
     *
     * @return This angles value
     */
    public double getValue() {

        return value;

    }

    /**
     * Converts and sets this Angles value and unit to an Angle given in degrees.<br>
     * If the Angle is already given in degree, the Angle will not change.
     *
     * @return This Angles new value based on degree units.
     */
    public double toDegree() {

        if (ARCSECOND.equals(unit)) {
            value = value / 60 / 60;
            unit = Angle.DEGREE;
            return value;
        }

        if (ARCMINUTE.equals(unit)) {
            value = value / 60;
            unit = Angle.DEGREE;
            return value;
        }

        if (RADIANT.equals(unit)) {
            value = value * 180 / Math.PI;
            unit = Angle.DEGREE;
            return value;
        }

        return value;

    }

    /**
     * Converts and sets this Angles value and unit to an Angle given in arcminutes.<br>
     * If the Angle is already given in arcminutes, the Angle will not change.
     *
     * @return This Angles new value based on arcminute units.
     */
    public double toArcMin() {

        if (ARCSECOND.equals(unit)) {
            value = value / 60;
            unit = Angle.ARCMINUTE;
            return value;
        }

        if (DEGREE.equals(unit)) {
            value = value * 60;
            unit = Angle.ARCMINUTE;
            return value;
        }

        if (RADIANT.equals(unit)) {
            value = toDegree() * 60;
            unit = Angle.ARCMINUTE;
            return value;
        }

        return value;

    }

    /**
     * Converts and sets this Angles value and unit to an Angle given in arcseconds.<br>
     * If the Angle is already given in arcseconds, the Angle will not change.
     *
     * @return This Angles new value based on arcsecond units.
     */
    public double toArcSec() {

        if (DEGREE.equals(unit)) {
            value = value * 60 * 60;
            unit = Angle.ARCSECOND;
            return value;
        }

        if (ARCMINUTE.equals(unit)) {
            value = value * 60;
            unit = Angle.ARCSECOND;
            return value;
        }

        if (RADIANT.equals(unit)) {
            value = toDegree() * 60 * 60;
            unit = Angle.ARCSECOND;
            return value;
        }

        return value;

    }

    /**
     * Converts and sets this Angles value and unit to an Angle given in radiant.<br>
     * If the Angle is already given in radiants, the Angle will not change.
     *
     * @return This Angles new value based on radiant units.
     */
    public double toRadiant() {

        if (ARCSECOND.equals(unit)) {
            value = value / 180 * Math.PI;
            unit = Angle.DEGREE;
            value = toArcSec();
            unit = Angle.RADIANT;
            return value;
        }

        if (ARCMINUTE.equals(unit)) {
            value = value / 180 * Math.PI;
            unit = Angle.DEGREE;
            value = toArcMin();
            unit = Angle.RADIANT;
            return value;
        }

        if (DEGREE.equals(unit)) {
            value = value / 180 * Math.PI;
            unit = Angle.RADIANT;
            return value;
        }

        return value;

    }

    /**
     * Checks if a given String is a valid Angle unit.<br>
     * Means, if the given string is equal to Angle.DEGREE, Angle.RADIANT, Angle.ARCMINUTE, Angle.ARCSECOND
     *
     * @param unit
     *            A String which represents an angle unit
     * @return true if the given string is a valid Angle unit
     */
    public static boolean isValidUnit(String unit) {

        return (Angle.ARCMINUTE.equals(unit)) || (Angle.ARCSECOND.equals(unit)) || (Angle.DEGREE.equals(unit))
                || (Angle.RADIANT.equals(unit));

    }

}
