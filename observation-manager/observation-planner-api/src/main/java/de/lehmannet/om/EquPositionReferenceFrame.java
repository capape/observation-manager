/* ====================================================================
 * /EquPositionReferenceFrame.java
 *
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om;

import java.util.Locale;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * EquPositionReferenceFrame describes the landscape in which certain position values are valid.<br>
 * This class should be used by all instances of de.lehmannet.om.IPosition to describe in which position reference frame
 * their position values are valid.
 *
 * @author doergn@users.sourceforge.net
 *
 * @since 1.0
 */
class EquPositionReferenceFrame {

    // ---------
    // Constants ---------------------------------------------------------
    // ---------

    // EquPositionReferenceFrame origin: geocentric
    public static final String ORIGIN_GEOCENTRIC = "geocentric";

    // EquPositionReferenceFrame origin: topocentric
    private static final String ORIGIN_TOPOCENTRIC = "topocentric";

    // Default value of equinox
    public static final String EQUINOX_2000 = "J2000.0";

    // Default value of equinox 2000 as Juliandate (01.01.2000 00:00h GMT)
    public static final double EQUINOX_2000_JULIANDATE = 2451545.0;

    // Constant for XML representation: Position reference frame element name
    private static final String XML_ELEMENT_REFERENCEFRAME = "referenceFrame";

    // Constant for XML representation: Position reference frame origin element name
    private static final String XML_ELEMENT_ORIGIN = "origin";

    // Constant for XML representation: Position reference frame equinox
    private static final String XML_ELEMENT_EQUINOX = "equinox";

    // ------------------
    // Instance Variables ------------------------------------------------
    // ------------------

    // The EquPositionReferenceFrame origin (Valid values: geocentric or
    // topocentric)
    private String origin = ORIGIN_GEOCENTRIC;

    // Equinox date
    private String equinox = EQUINOX_2000;

    // ------------
    // Constructors ------------------------------------------------------
    // ------------

    /**
     * Creates an instance of an EquPositionReferenceFrame.<br>
     *
     * @param origin
     *            The origin of the position reference frame. All valid values can be accessed by this classes
     *            constants.
     * @param equinox
     *            A equinox date. Should be formed like e.g. <code>J2000.0</code>
     *
     * @throws IllegalArgumentException
     *             if origin or equinox is <code>null</code> or origin does not have a valid value.
     */
    public EquPositionReferenceFrame(String origin, String equinox) throws IllegalArgumentException {

        if ((origin == null) || (equinox == null)) {
            throw new IllegalArgumentException("Origin or equinox cannot be null. ");
        }

        if ((ORIGIN_GEOCENTRIC.equals(origin)) || (ORIGIN_TOPOCENTRIC.equals(origin))) {
            setEquinox(equinox);
            setOrigin(origin);
        } else {
            throw new IllegalArgumentException("Origin did not have valid value. ");
        }

    }

    // ------
    // Object ------------------------------------------------------------
    // ------

    /**
     * Overwrittes toString() method from java.lang.Object.<br>
     * Returns the position reference frame as string in form:<br>
     * Example:<br>
     * <code>
     * Topocentric J2000.0
     * </code>
     *
     * @return A string representing the position reference frame
     *
     * @see java.lang.Object
     */
    @Override
    public String toString() {

        return this.getOrigin() + " " + this.getEquinox();

    }

    /**
     * Overwrittes equals(Object) method from java.lang.Object.<br>
     * Checks if this EquPositionReferenceFrame and the given Object are equal. The given object is equal with this
     * EquPositionReferenceFrame, if its an instance from class EquPositionReferenceFrame and its equinox date and the
     * position origin are equal with this instances values.
     *
     * @param obj
     *            The Object to compare this EquPositionReferenceFrame with.
     *
     * @return <code>true</code> if the given Object is an instance of EquPositionReferenceFrame and its equinox date
     *         and origin are equal with this PositionReferenceFrames values.<br>
     *
     * @see java.lang.Object
     */
    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof EquPositionReferenceFrame)) {
            return false;
        }

        EquPositionReferenceFrame frame = (EquPositionReferenceFrame) obj;

        return (frame.getEquinox().toLowerCase(Locale.getDefault()).trim()
                .equals(equinox.toLowerCase(Locale.getDefault()).trim()))
                && (frame.getOrigin().toLowerCase(Locale.getDefault()).trim()
                        .equals(origin.toLowerCase(Locale.getDefault()).trim()));

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((equinox == null) ? 0 : equinox.hashCode());
        result = prime * result + ((origin == null) ? 0 : origin.hashCode());
        return result;
    }

    // --------------
    // Public methods ----------------------------------------------------
    // --------------

    /**
     * Adds this EquPositionReferenceFrame to an given parent XML DOM Element. The EquPositionReferenceFrame Element
     * will be set as a child element of the passed Element.
     *
     * @param parent
     *            The parent element for this EquPositionReferenceFrame
     *
     * @return Returns the Element given as parameter with this EquPositionReferenceFrame as child Element.<br>
     *         Might return <code>null</code> if parent was <code>null</code>.
     *
     * @see org.w3c.dom.Element
     */
    public Element addToXmlElement(Element parent) {

        if (parent == null) {
            return null;
        }

        Document ownerDoc = parent.getOwnerDocument();

        Element e_Frame = ownerDoc.createElement(XML_ELEMENT_REFERENCEFRAME);

        Element e_Origin = ownerDoc.createElement(XML_ELEMENT_ORIGIN);
        e_Origin.setNodeValue(origin);
        e_Frame.appendChild(e_Origin);

        Element e_Equinox = ownerDoc.createElement(XML_ELEMENT_EQUINOX);
        e_Equinox.setNodeValue(equinox);
        e_Frame.appendChild(e_Equinox);

        parent.appendChild(e_Frame);

        return parent;

    }

    /**
     * Returns the equinox date of this position reference frame.
     *
     * @return The equinox date of this position reference frame
     */
    private String getEquinox() {

        return equinox;

    }

    /**
     * Returns the origin of this position reference frame.
     *
     * @return The origin of this position reference frame
     */
    private String getOrigin() {

        return origin;

    }

    /**
     * Sets the equinox date of this position reference frame.<br>
     * String should be formed like <code>J2000.0</code>
     *
     * @param equinox
     *            The equinox date to set
     *
     * @throws IllegalArgumentException
     *             if equinox was <code>null</code>
     */
    private void setEquinox(String equinox) throws IllegalArgumentException {

        if (equinox == null) {
            throw new IllegalArgumentException("Equinox cannot be null. ");
        }

        this.equinox = equinox;

    }

    /**
     * Sets the origin of this position reference frame.<br>
     * All valid origin values can be accessed by this classes constants.
     *
     * @param origin
     *            The new origin of this position reference frame
     *
     * @throws IllegalArgumentException
     *             if origin was <code>null</code> or did not have valid value.
     */
    private void setOrigin(String origin) throws IllegalArgumentException {

        if (origin == null) {
            throw new IllegalArgumentException("Origin cannot be null. ");
        }

        if ((ORIGIN_GEOCENTRIC.equals(origin)) || (ORIGIN_TOPOCENTRIC.equals(origin))) {
            this.origin = origin;
        } else {
            throw new IllegalArgumentException("Origin did not have valid value. ");
        }

    }

}
