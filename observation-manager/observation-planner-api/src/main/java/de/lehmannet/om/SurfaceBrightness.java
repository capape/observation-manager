/* ====================================================================
 * /SurfaceBrightness.java
 *
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.lehmannet.om.util.FloatUtil;
import de.lehmannet.om.util.SchemaException;

import java.util.Objects;

/**
 * Surface Brightness represents the value and the unit of a celestial objects surface brightness
 *
 * @author doergn@users.sourceforge.net
 * @since 2.0
 */
public class SurfaceBrightness {

    // ---------
    // Constants ---------------------------------------------------------
    // ---------

    /**
     * Surface Brightness unit: Magnitude per square arc second
     */
    public static final String MAGS_SQR_ARC_SEC = "mags-per-squarearcsec";

    /**
     * Surface Brightness unit: Magnitude per square arc minute
     */
    public static final String MAGS_SQR_ARC_MIN = "mags-per-squarearcmin";

    /**
     * XML Attribute name for unit
     */
    public static final String XML_ATTRIBUTE_UNIT = "unit";

    // ------------------
    // Instance Variables ------------------------------------------------
    // ------------------

    /* The sf value */
    private float value = Float.NaN;

    /* The sf value unit */
    private String unit = MAGS_SQR_ARC_SEC;

    // ------------
    // Constructors ------------------------------------------------------
    // ------------

/**
     * Constructs a new instance of Surface Brightness from a given DOM Element.<br>
     *
     * @throws SchemaException
     *             if given surfaceBrightnessNode was <code>null</code>
     */
    public SurfaceBrightness(Node sfNode) throws SchemaException {

        if (sfNode == null) {
            throw new SchemaException("Surface Brightness node cannot be NULL ");
        }

        Element sf = (Element) sfNode;

        String unit = sf.getAttribute(SurfaceBrightness.XML_ATTRIBUTE_UNIT).trim();
        if ((unit.equals(SurfaceBrightness.MAGS_SQR_ARC_MIN)) || (unit.equals(SurfaceBrightness.MAGS_SQR_ARC_SEC))) {
            this.value = FloatUtil.parseFloat(sf.getFirstChild().getNodeValue());
            this.unit = unit;
        } else {
            throw new SchemaException("Surface Brightness unit is unknown. ");
        }

    }

/*
     * Creates a new Surface Brightness instance.
     *
     * @param value The surface brightness value
     *
     * @param unit Surface brightness unit (use SurfaceBrightness constants)
     *
     * @throws IllegalArgumentException if unit parameter has unknown type. Allowed types can be accessed by public
     * constants.<br>
     *
     * @see de.lehmannet.om.SurfaceBrightness
     */
    public SurfaceBrightness(float value, String unit) throws IllegalArgumentException {

        if ((Float.isNaN(value)) || (unit == null)) {
            throw new IllegalArgumentException(
                    "Unable to create new SurfaceBrightness instance with given parameters.\n\tValue" + value
                            + "\n\tUnit:" + unit);
        }

        if (SurfaceBrightness.MAGS_SQR_ARC_MIN.equals(unit.trim())
                || SurfaceBrightness.MAGS_SQR_ARC_SEC.equals(unit.trim())) {
            this.unit = unit.trim();
        } else {
            throw new IllegalArgumentException(
                    "Unable to create new SurfaceBrightness instance with given unit: " + unit);
        }

        this.value = value;

    }

    // ------
    // Object ------------------------------------------------------------
    // ------

/**
     * Overwrittes toString() method from java.lang.Object.<br>
     * Returns the value of this surface brightness followed by its unit.<br>
     * Example:<br>
     * 5.67 mags-per-squarearcsec
     *
     * @return The surface brightness value followed by its unit
     * @see java.lang.Object
     */
    @Override
    public String toString() {

        return value + " " + unit;

    }

/**
     * Overwrittes equals(Object) method from java.lang.Object.<br>
     * Checks if this surface brightness and the given Object are equal. Two surface brightness' are equal if both have
     * the same value when represented as mags-per-squarearcsec.<br>
     *
     * @param obj
     *            The Object to compare this surface brightness with.
     * @return <code>true</code> if both Objects are instances from class de.lehmannet.om.SurfaceBrightness and their
     *         values in mags-per-squarearcsec is equal.
     * @see java.lang.Object
     */
    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof SurfaceBrightness)) {
            return false;
        }

        SurfaceBrightness foreign = (SurfaceBrightness) obj;

        float foreignMagsPerSqrArcSec = foreign.getValueAs(SurfaceBrightness.MAGS_SQR_ARC_SEC);

        float thisMagsPerSqrArcSec = this.value;
        if (!SurfaceBrightness.MAGS_SQR_ARC_SEC.equals(unit)) {
            thisMagsPerSqrArcSec = this.getValueAs(SurfaceBrightness.MAGS_SQR_ARC_SEC);
        }

        return thisMagsPerSqrArcSec == foreignMagsPerSqrArcSec;

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((unit == null) ? 0 : unit.hashCode());
        result = prime * result + Float.floatToIntBits(value);
        return result;
    }

    // --------------
    // Public methods ----------------------------------------------------
    // --------------

/**
     * Sets this SurfaceBrightness to an given XML DOM Element.<br>
     * The SurfaceBrightness value will be set as the Elements value and the SurfaceBrightness unit will be set as the
     * Elements attribute.<br>
     * Example:<br>
     * &lt;surfaceBrightness unit="mags-per-squarearcsec"&gt;20.3&lt;/surfaceBrightness&gt;
     *
     * @param element
     *            The XML DOM Element this SurfaceBrightness belongs to
     * @return Returns the element given as parameter with this SurfaceBrightness value as element value and this
     *         SurfaceBrightness unit as attribute
     * @see org.w3c.dom.Element
     */
    public Element setToXmlElement(Element element) {

        if (element == null) {
            return null;
        }

        element.setAttribute(XML_ATTRIBUTE_UNIT, unit);
        Node n_ValueText = element.getOwnerDocument().createTextNode(Float.toString(value));
        element.appendChild(n_ValueText);

        return element;

    }

/**
     * Returns the unit of the SurfaceBrightness value.<br>
     * The returned value may be any valid SurfaceBrightness unit, which can be accessed by SurfaceBrightness constants.
     *
     * @return This SurfaceBrightness value unit.
     */
    public String getUnit() {

        return unit;

    }

/**
     * Returns the value of this SurfaceBrightness.<br>
     * The returned value may be any float value (positiv or negativ).
     *
     * @return This SurfaceBrightness value
     */
    public float getValue() {

        return value;

    }

/**
     * Returns the value of this SurfaceBrightness given in the passed unit.<br>
     * The returned value may be any float value (positiv or negativ). If the passed unit is invalid,
     * <code>Float.NaN</code> is returned.
     *
     * @return This SurfaceBrightness value or <code>Float.NaN</code> if the given unit parameter was invalid
     */
    public float getValueAs(String unit) {

        if (unit == null) {
            return Float.NaN;
        }

        // Requested unit is current unit
        if (Objects.equals(unit, this.unit)) {
            return this.getValue();
        }

        float result = Float.NaN;

        if (SurfaceBrightness.MAGS_SQR_ARC_MIN.equals(unit)) {
            result = this.value - 8.875f;
        } else if (SurfaceBrightness.MAGS_SQR_ARC_SEC.equals(unit)) {
            result = this.value + 8.875f;
        }

        return result;

    }

/**
     * Checks if a given String is a valid Surface Brightness unit.<br>
     * Means, if the given string is equal to SurfaceBrightness.MAGS_SQR_ARC_MIN or SurfaceBrightness.MAGS_SQR_ARC_SEC
     *
     * @param unit
     *            A String which represents an Surface Brightness unit
     * @return true if the given string is a valid Surface Brightness unit
     */
    public static boolean isValidUnit(String unit) {

        return (SurfaceBrightness.MAGS_SQR_ARC_MIN.equals(unit)) || (SurfaceBrightness.MAGS_SQR_ARC_SEC.equals(unit));

    }

}
