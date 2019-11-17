/* ====================================================================
 * /EquPosition.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.lehmannet.om.util.DateConverter;
import de.lehmannet.om.util.SchemaException;

/**
 * EquPosition provides a representation of a equatorial celestial position.<br>
 * A equatorial position is given with three values. The <code>right ascension</code> and the <code>declination</code>
 * are used to define the celestial position while both values are valid within a special reference time frame (the
 * third value).
 * 
 * @author doergn@users.sourceforge.net
 * @since 1.0
 */
public class EquPosition extends SchemaElement {

    // ---------
    // Constants ---------------------------------------------------------
    // ---------

    // Constant for XML representation: position element name
    public static final String XML_ELEMENT_POSITION = "position";

    // Constant for XML representation: right ascension element name
    private static final String XML_ELEMENT_RA = "ra";

    // Constant for XML representation: declination element name
    private static final String XML_ELEMENT_DEC = "dec";

    // Constant for RA string (hour)
    public static final String RA_HOUR = "h";

    // Constant for RA string (minute)
    public static final String RA_MIN = "m";

    // Constant for RA string (second)
    public static final String RA_SEC = "s";

    // Constant for DEC string (degree)
    public static final String DEC_DEG = "\u00b0";

    // Constant for DEC string (minute)
    public static final String DEC_MIN = "'";

    // Constant for DEC string (second)
    public static final String DEC_SEC = "\"";

    // ------------------
    // Instance Variables ------------------------------------------------
    // ------------------

    // Positions right ascension (Angle value has to be positiv)
    private Angle ra = null;

    // Positions declination
    private Angle dec = null;

    // The equatorial positions reference frame
    private EquPositionReferenceFrame frame = null;

    // ------------
    // Constructors ------------------------------------------------------
    // ------------

/**
     * Constructs a new instance of a EquPosition from a given DOM target Element.<br>
     * 
     * @param positionNode
     *            The origin XML DOM position Element
     * @throws SchemaException
     *             if given positionNode was <code>null</code>
     */
    public EquPosition(Node positionNode) throws SchemaException {

        Element position = (Element) positionNode;

        Angle ra = null;
        Angle dec = null;

        NodeList positionValues = null;
        positionValues = position.getElementsByTagName(EquPosition.XML_ELEMENT_RA);
        if (positionValues != null) {
            if (positionValues.getLength() == 1) {
                Node raElement = positionValues.item(0);

                ra = new Angle(raElement);
            } else if (positionValues.getLength() > 1) {
                throw new SchemaException("Position can have only one <ra> element. ");
            }
        }

        positionValues = position.getElementsByTagName(EquPosition.XML_ELEMENT_DEC);
        if (positionValues != null) {
            if (positionValues.getLength() == 1) {
                Node decElement = positionValues.item(0);

                dec = new Angle(decElement);
            } else if (positionValues.getLength() > 1) {
                throw new SchemaException("Position can have only one <dec> element. ");
            }
        }

        this.setDecAngle(dec);
        this.setRaAngle(ra);

    }

/*
     * Creates an instance of an EquPosition with a geocentric reference frame (equinox J2000.0).<br>
     * 
     * @param ra Angle with positiv value describing the right ascension of the equatorial position
     * 
     * @param dec Angle describing the declination of the equatorial position
     * 
     * @throws IllegalArgumentException if ra or dec parameter was <code>null</code> or ra value was negative
     */
    public EquPosition(Angle ra, Angle dec) throws IllegalArgumentException {

        if ((ra == null) || (dec == null)) {
            throw new IllegalArgumentException("ra or dec value cannot be null. ");
        }

        if (ra.getValue() < 0) {
            throw new IllegalArgumentException("ra value cannot be negative. ");
        }

        this.ra = ra;
        this.dec = dec;

    }

/*
     * Creates an instance of an EquPosition with a geocentric reference frame (equinox J2000.0).<br>
     * 
     * @param ra String describing the right ascension of the equatorial position. Input sting must be of format
     * <i>01</i>h<i>02</i>min<i>03</i>sec
     * 
     * @param dec String describing the declination of the equatorial position. Input string must be of format
     * <i>01</i>\u00b0<i>02</i>'<i>03</i>''.
     * 
     * @throws IllegalArgumentException if ra or dec parameter was <code>null</code>
     */
    public EquPosition(String ra, String dec) throws IllegalArgumentException {

        if ((ra == null) || (dec == null)) {
            throw new IllegalArgumentException("ra or dec value cannot be null. ");
        }

        this.setRa(ra);
        this.setDec(dec);

    }

    // -------------
    // SchemaElement -----------------------------------------------------
    // -------------

/**
     * Returns a display name for this element.<br>
     * The method differs from the toString() method as toString() shows more technical information about the element.
     * Also the formating of toString() can spread over several lines.<br>
     * This method returns a string (in one line) that can be used as displayname in e.g. a UI dropdown box.
     * 
     * @return Returns a String with a one line display name
     * @see java.lang.Object.toString();
     */
    @Override
    public String getDisplayName() {

        return this.toString();

    }

    // ------
    // Object ------------------------------------------------------------
    // ------

/**
     * Overwrittes toString() method from java.lang.Object.<br>
     * Returns the equatorial position as string in form:<br>
     * Example:<br>
     * <code>
     * Right ascension: 4.159876545 RAD<br>
     * Declination: -0.258774154 RAD
     * Reference frame: Topocentric J2000.0
     * </code>
     * 
     * @return A string representing the equatorial position
     * @see java.lang.Object
     */
    @Override
    public String toString() {

        StringBuilder buffer = new StringBuilder();

        buffer.append(" Right ascension: ");
        buffer.append(this.getRa());
        buffer.append(" Declination: ");
        buffer.append(this.getDec());
        if (frame != null) {
            buffer.append(frame);
        }

        return buffer.toString();

    }

/**
     * Overwrittes equals(Object) method from java.lang.Object.<br>
     * Checks if this EquPosition and the given Object are equal. The given object is equal with this EquPosition, if
     * the right ascension, declination and the position reference frame are equal.
     * 
     * @param obj
     *            The Object to compare this EquPosition with.
     * @return <code>true</code> if the given Object is an instance of EquPosition and its right ascension, declination
     *         and position reference frame are equal with this EquPosition.<br>
     * @see java.lang.Object
     */
    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof EquPosition)) {
            return false;
        }

        EquPosition equPosition = (EquPosition) obj;

        // Check if frames match
        if (!(frame.equals(equPosition.getFrame()))) {
            return false;
        }

        return (ra.equals(equPosition.getRaAngle())) && (dec.equals(equPosition.getDecAngle()));

    }

    // ---------------------
    // Public static methods ---------------------------------------------
    // ---------------------

/**
     * Returns a correct formed right ascension string.
     * 
     * @param min
     *            Minute value
     * @param sec
     *            Second value
     * @return The right ascension as correct formated sting.
     */
    private static String getRaString(int hours, int min, int sec) {

        return DateConverter.setLeadingZero(hours) + EquPosition.RA_HOUR + DateConverter.setLeadingZero(min)
                + EquPosition.RA_MIN + DateConverter.setLeadingZero(sec) + EquPosition.RA_SEC;

    }

/**
     * Returns a correct formed right ascension string.
     * 
     * @param min
     *            Minute value
     * @param sec
     *            Second value
     * @return The right ascension as correct formated sting.
     */
    public static String getRaString(int hours, int min, double sec) {

        return DateConverter.setLeadingZero(hours) + EquPosition.RA_HOUR + DateConverter.setLeadingZero(min)
                + EquPosition.RA_MIN + DateConverter.setLeadingZero(sec) + EquPosition.RA_SEC;

    }

/**
     * Returns a correct formed declination string.
     * 
     * @param deg
     *            Degree value
     * @param min
     *            Minute value
     * @param sec
     *            Second value
     * @return The declination as correct formated sting.
     */
    public static String getDecString(int deg, int min, int sec) {

        if ((deg == 0) && ((min < 0) || ((min == 0) && (sec < 0)))) {
            return "-" + DateConverter.setLeadingZero(deg) + EquPosition.DEC_DEG
                    + DateConverter.setLeadingZero(Math.abs(min)) + EquPosition.DEC_MIN
                    + DateConverter.setLeadingZero(Math.abs(sec)) + EquPosition.DEC_SEC;
        }

        return DateConverter.setLeadingZero(deg) + EquPosition.DEC_DEG + DateConverter.setLeadingZero(Math.abs(min))
                + EquPosition.DEC_MIN + DateConverter.setLeadingZero(Math.abs(sec)) + EquPosition.DEC_SEC;

    }

/**
     * Returns a correct formed declination string.
     * 
     * @param deg
     *            Degree value
     * @param min
     *            Minute value
     * @param sec
     *            Second value
     * @return The declination as correct formated sting.
     */
    public static String getDecString(int deg, int min, double sec) {

        if ((deg == 0) && ((min < 0) || ((min == 0) && (sec < 0)))) {
            return "-" + DateConverter.setLeadingZero(deg) + EquPosition.DEC_DEG
                    + DateConverter.setLeadingZero(Math.abs(min)) + EquPosition.DEC_MIN
                    + DateConverter.setLeadingZero(Math.abs(sec)) + EquPosition.DEC_SEC;
        }

        return DateConverter.setLeadingZero(deg) + EquPosition.DEC_DEG + DateConverter.setLeadingZero(Math.abs(min))
                + EquPosition.DEC_MIN + DateConverter.setLeadingZero(Math.abs(sec)) + EquPosition.DEC_SEC;

    }

    // --------------
    // Public methods ----------------------------------------------------
    // --------------

/**
     * Adds this EquPosition to an given parent XML DOM Element. The EquPosition Element will be set as a child element
     * of the passed Element.
     * 
     * @param parent
     *            The parent element for this EquPosition
     * @return Returns the Element given as parameter with this EquPosition as child Element.<br>
     *         Might return <code>null</code> if parent was <code>null</code>.
     * @see org.w3c.dom.Element
     */
    public Element addToXmlElement(Element parent) {

        if (parent == null) {
            return null;
        }

        Document ownerDoc = parent.getOwnerDocument();

        Element e_Position = ownerDoc.createElement(XML_ELEMENT_POSITION);

        // Mandatory Reference Frame
        if (frame != null) {
            e_Position = frame.addToXmlElement(e_Position);
        }

        Element e_Ra = ownerDoc.createElement(XML_ELEMENT_RA);

        e_Ra = ra.setToXmlElement(e_Ra);
        e_Position.appendChild(e_Ra);

        Element e_Dec = ownerDoc.createElement(XML_ELEMENT_DEC);
        e_Dec = dec.setToXmlElement(e_Dec);
        e_Position.appendChild(e_Dec);

        parent.appendChild(e_Position);

        return parent;

    }

/**
     * Returns the declination of this equatorial position as Angle. This might have a strange unit for Dec values.
     * 
     * @return The declination as instance of class Angle
     */
    public Angle getDecAngle() {

        return dec;

    }

/**
     * Returns the declination of this equatorial position..
     * 
     * @return The declination as displayable value
     */
    public String getDec() {

        double degree = dec.toDegree();
        int deg = 0;
        if (degree > 0) {
            deg = (int) Math.floor(degree);
        } else {
            deg = (int) Math.ceil(degree);
        }
        double rest = degree - deg;

        double md = rest * 60.0;
        int min = 0;
        if (md > 0) {
            min = (int) Math.floor(md);
        } else {
            min = (int) Math.ceil(md);
        }
        rest = rest - (min / 60.0);

        double sd = rest * 3600.0;
        int sec = (int) Math.round(sd);
        if (sec == 60) {
            min++;
            if (min >= 60) {
                deg++;
                min = 0;
            }
            sec = 0;
        } else if (sec == -60) {
            min--;
            if (min >= 60) {
                deg--;
                min = 0;
            }
            sec = 0;
        }

        return EquPosition.getDecString(deg, min, sec);

    }

/**
     * Returns the right ascension of this equatorial position as Angle. This might have a strange unit for RA values.
     * Maybe getRa() is what you want.
     * 
     * @return The right ascension as instance of class Angle
     */
    public Angle getRaAngle() {

        return ra;

    }

/**
     * Returns the right ascension of this equatorial position.
     * 
     * @return The right ascension as displayable sting.
     */
    public String getRa() {

        double deg = ra.toDegree();

        int hours = (int) Math.floor(deg / 15.0);
        double rest = deg - (hours * 15);

        double md = rest * 60.0 / 15;
        int min = (int) Math.floor(md);
        rest = rest - (min / 60.0 * 15);

        double sd = rest * 3600.0 / 15;
        int sec = (int) Math.round(sd);

        return EquPosition.getRaString(hours, min, sec);

    }

/**
     * Returns the right ascension of this equatorial position in decimal hours
     * 
     * @return The right ascension in decimal hours as double
     */
    public double getRaDecimalHours() {

        String s = this.getRa();
        double ra = Integer.parseInt(s.substring(0, s.indexOf(EquPosition.RA_HOUR)));
        ra = ra + (Integer.parseInt(s.substring(s.indexOf(EquPosition.RA_HOUR) + 1, s.indexOf(EquPosition.RA_MIN))))
                / 60.0;
        ra = ra + (Integer.parseInt(s.substring(s.indexOf(EquPosition.RA_MIN) + 1, s.indexOf(EquPosition.RA_SEC))))
                / (60.0 * 60.0);

        return ra;

    }

/**
     * Sets the declination of the equatorial position. Input string must be of format
     * <i>01</i>\u00b0<i>02</i>'<i>03</i>''.
     * 
     * @param paramdec
     *            The new declination of the equatorial position
     * @throws IllegalArgumentException
     *             if dec was <code>null</code> or the string was malformed
     */
    private void setDec(String paramdec) throws IllegalArgumentException {

        String dec = paramdec.replace('+', ' ');
        dec = dec.replaceAll(" ", "");
        try {
            int deg = Integer.parseInt(dec.substring(0, dec.indexOf(EquPosition.DEC_DEG)));
            int min = Integer
                    .parseInt(dec.substring(dec.indexOf(EquPosition.DEC_DEG) + 1, dec.indexOf(EquPosition.DEC_MIN)));
            double sec = Double
                    .parseDouble(dec.substring(dec.indexOf(EquPosition.DEC_MIN) + 1, dec.indexOf(EquPosition.DEC_SEC)));

            double d = 0.0;
            if (dec.startsWith("-")) {
                d = deg - (min / 60.0) - (sec / 3600.0);
            } else {
                d = deg + (min / 60.0) + (sec / 3600.0);
            }

            this.dec = new Angle(d, Angle.DEGREE);
        } catch (NumberFormatException | StringIndexOutOfBoundsException nfe) {
            throw new IllegalArgumentException("DEC string malformed. " + dec, nfe);
        }

    }

/**
     * Sets the right ascension of this equatorial position. Input sting must be of format
     * <i>01</i>h<i>02</i>min<i>03</i>sec
     * 
     * @param paramra
     *            The right ascension of the equatorial position
     * @throws IllegalArgumentException
     *             if ra is <code>null</code> or the string was malformed
     */
    private void setRa(String paramra) throws IllegalArgumentException {

        String ra = paramra.replaceAll(" ", "");
        try {
            int hour = Integer.parseInt(ra.substring(0, ra.indexOf(EquPosition.RA_HOUR)));
            int min = Integer
                    .parseInt(ra.substring(ra.indexOf(EquPosition.RA_HOUR) + 1, ra.indexOf(EquPosition.RA_MIN)));
            double sec = Double
                    .parseDouble(ra.substring(ra.indexOf(EquPosition.RA_MIN) + 1, ra.indexOf(EquPosition.RA_SEC)));

            double r = (hour * 15) + (min / 60.0 * 15) + (sec / 3600.0 * 15);

            this.ra = new Angle(r, Angle.DEGREE);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("RA string malformed. " + ra, nfe);
        } catch (StringIndexOutOfBoundsException siobe) {
            throw new IllegalArgumentException("RA string malformed. " + siobe, siobe);
        }

    }

/**
     * Sets the declination of the equatorial position.
     * 
     * @param dec
     *            The new declination of the equatorial position
     * @throws IllegalArgumentException
     *             if dec was <code>null</code>
     */
    private void setDecAngle(Angle dec) throws IllegalArgumentException {

        if (dec == null) {
            throw new IllegalArgumentException("dec value cannot be null. ");
        }

        this.dec = dec;

    }

/**
     * Sets the right ascension of this equatorial position. The Angles value has to be positive.
     * 
     * @param ra
     *            The right ascension of the equatorial position
     * @throws IllegalArgumentException
     *             if ra is <code>null</code> or ra value is negative
     */
    private void setRaAngle(Angle ra) throws IllegalArgumentException {

        if (ra == null) {
            throw new IllegalArgumentException("ra value cannot be null. ");
        }

        if (ra.getValue() < 0) {
            throw new IllegalArgumentException("ra value cannot be negative. ");
        }

        this.ra = ra;

    }

/**
     * Returns the position reference frame of this equatorial position.
     * 
     * @return The position reference frame of this equatorial position
     */
    private EquPositionReferenceFrame getFrame() {

        return frame;

    }

/**
     * Sets the position reference frame of this equatorial position.<br>
     * If <code>null</code> is passed the new position reference frame that is set is geocentric with equinox J2000.0
     * 
     * @param frame
     *            The new position reference frame
     */
    public void setFrame(EquPositionReferenceFrame frame) {

        if (frame == null) {
            this.frame = new EquPositionReferenceFrame(EquPositionReferenceFrame.ORIGIN_GEOCENTRIC,
                    EquPositionReferenceFrame.EQUINOX_2000);
        } else {
            this.frame = frame;
        }

    }

}
