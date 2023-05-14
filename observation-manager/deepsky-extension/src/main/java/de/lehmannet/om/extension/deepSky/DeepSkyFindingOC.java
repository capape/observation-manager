/*
 * ====================================================================
 * extension/deepSky/DeepSkyFindingOC.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.extension.deepSky;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.lehmannet.om.IExtendableSchemaElement;
import de.lehmannet.om.util.SchemaException;

/**
 * DeepSkyFindingOC extends the de.lehmannet.om.DeepSkyFinding class. Its specialised for open cluster observations and
 * their findings. The class is mostly oriented after the recommondations of the german "VdS - DeepSky" group
 * (<a href="http://www.fachgruppe-deepsky.de/">Homepage</a>).<br>
 * The field rating is based on a seven step scale recommended by "VDS - DeepSky" group. The scales value should be
 * interpreted as the following table explains:
 * <table>
 * <tr>
 * <td>1</td>
 * <td>Simple conspicuous object in the eyepiece</td>
 * <td>2</td>
 * <td>Good viewable with direct vision</td>
 * <td>3</td>
 * <td>Viewable with direct vision</td>
 * <td>4</td>
 * <td>Viewable only with averted vision</td>
 * <td>5</td>
 * <td>Object can hardly be seen with averted vision</td>
 * <td>6</td>
 * <td>Object dubiously sighted</td>
 * <td>7</td>
 * <td>Object not sighted</td>
 * </tr>
 * </table>
 *
 * @author doergn@users.sourceforge.net
 * @since 1.5
 */
public class DeepSkyFindingOC extends DeepSkyFinding {

    // ---------
    // Constants ---------------------------------------------------------
    // ---------

    // XSML schema instance value. Enables class/schema loaders to identify this
    // class
    public static final String XML_XSI_TYPE_VALUE = "oal:findingsDeepSkyOCType";

    // Constant for XML representation: finding element character
    private static final String XML_ELEMENT_CHARACTER = "character";

    // Constant for XML representation: finding element attribute character
    private static final String XML_ELEMENT_FINDING_ATTRIBUTE_UNUSUALSHAPE = "unusualShape";

    // Constant for XML representation: finding element attribute character
    private static final String XML_ELEMENT_FINDING_ATTRIBUTE_PARTLYUNRESOLVED = "partlyUnresolved";

    // Constant for XML representation: finding element attribute character
    private static final String XML_ELEMENT_FINDING_ATTRIBUTE_COLORCONTRASTS = "colorContrasts";

    // Open cluster characteristics as definied by the german "Deep Sky List"
    // project;
    public static final char CHARACTER_A = 'A';
    public static final char CHARACTER_B = 'B';
    public static final char CHARACTER_C = 'C';
    public static final char CHARACTER_D = 'D';
    public static final char CHARACTER_E = 'E';
    public static final char CHARACTER_F = 'F';
    public static final char CHARACTER_G = 'G';
    public static final char CHARACTER_H = 'H';
    public static final char CHARACTER_I = 'I';

    // ------------------
    // Instance Variables ------------------------------------------------
    // ------------------

    // Open cluster characteristic
    private Character character = null;

    // 1 if the object has an unusualShape
    // 0 if the object hasn't an unusualShape
    // -1 the value was not set
    private int unusualShape = -1;

    // 1 if the object was partly unresolved
    // 0 if the object wan't partly unresolved
    // -1 the value was not set
    private int partlyUnresolved = -1;

    // 1 if the object showed some colorContrasts
    // 0 if the object didn't show some colorContrasts
    // -1 the value was not set
    private int colorContrasts = -1;

    // ------------
    // Constructors ------------------------------------------------------
    // ------------

    public DeepSkyFindingOC(Node findingElement) throws SchemaException {

        super(findingElement);

        Element finding = (Element) findingElement;

        // Getting data

        // Get optional unusualShape attribute
        String unSh = finding.getAttribute(DeepSkyFindingOC.XML_ELEMENT_FINDING_ATTRIBUTE_UNUSUALSHAPE);
        if (!StringUtils.isBlank(unSh)) {
            this.setUnusualShape(Boolean.valueOf(unSh));
        }

        // Get optional stellar attribute
        String paUn = finding.getAttribute(DeepSkyFindingOC.XML_ELEMENT_FINDING_ATTRIBUTE_PARTLYUNRESOLVED);
        if (!StringUtils.isBlank(paUn)) {
            this.setPartlyUnresolved(Boolean.valueOf(paUn));
        }

        // Get optional colorContrast attribute
        String coCo = finding.getAttribute(DeepSkyFindingOC.XML_ELEMENT_FINDING_ATTRIBUTE_COLORCONTRASTS);
        if (!StringUtils.isBlank(coCo)) {
            this.setColorContrasts(Boolean.valueOf(coCo));
        }

        // Get optional character
        NodeList children = finding.getElementsByTagName(DeepSkyFindingOC.XML_ELEMENT_CHARACTER);
        String c = null;
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            if (child != null) {
                c = child.getFirstChild().getNodeValue();
                this.setCharacter(c.toCharArray()[0]);
            } else {
                throw new SchemaException("Problem while retrieving character from DeepSkyFindingOC. ");
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("DeepSkyFindingOC can have only one character entry. ");
        }

    }

    /**
     * Constructs a new instance of a DeepSkyFindingOC.
     *
     * @param description
     *            The description of the finding
     * @param rating
     *            The rating of the finding
     * @throws IllegalArgumentException
     *             if description was <code>null</code> or rating had a illegal value.
     */
    public DeepSkyFindingOC(String description, int rating) throws IllegalArgumentException {

        super(description, rating);

    }

    // ------
    // Object ------------------------------------------------------------
    // ------

    /**
     * Overwrittes toString() method from java.lang.Object.<br>
     * Returns the field values of this DeepSkyFindingOC.
     *
     * @return This DeepSkyFindingOC field values
     * @see java.lang.Object
     */
    @Override
    public String toString() {

        StringBuilder buffer = new StringBuilder(super.toString());

        if (unusualShape != -1) {
            buffer.append(" UnusualShape=");
            buffer.append(this.getUnusualShape());
        }

        if (partlyUnresolved != -1) {
            buffer.append(" PartlyUnresolved=");
            buffer.append(this.getPartlyUnresolved());
        }

        if (colorContrasts != -1) {
            buffer.append(" ColorContrasts=");
            buffer.append(this.getColorContrasts());
        }

        if (character != null) {
            buffer.append(" Character=");
            buffer.append(this.getCharacter());
        }

        String result = buffer.toString();
        result = result.replaceAll("DeepSkyFinding", "DeepSkyFindingOC");

        return result;

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

        return DeepSkyFindingOC.XML_XSI_TYPE_VALUE;

    }

    // -------
    // Finding -----------------------------------------------------------
    // -------

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((character == null) ? 0 : character.hashCode());
        result = prime * result + colorContrasts;
        result = prime * result + partlyUnresolved;
        result = prime * result + unusualShape;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        DeepSkyFindingOC other = (DeepSkyFindingOC) obj;
        if (character == null) {
            if (other.character != null)
                return false;
        } else if (!character.equals(other.character))
            return false;
        if (colorContrasts != other.colorContrasts)
            return false;
        if (partlyUnresolved != other.partlyUnresolved)
            return false;
        return unusualShape == other.unusualShape;
    }

    /**
     * Adds this DeepSkyFindingOC to an given parent XML DOM Element. The DeepSkyFindingOC Element will be set as a
     * child element of the passed Element.
     *
     * @param parent
     *            The parent element for this DeepSkyFindingOC
     * @return Returns the Element given as parameter with this DeepSkyFindingOC as child Element.<br>
     *         Might return <code>null</code> if parent was <code>null</code>.
     * @see org.w3c.dom.Element
     */
    @Override
    public Element addToXmlElement(Element parent) {

        if (parent == null) {
            return null;
        }

        Document ownerDoc = parent.getOwnerDocument();

        Element e_Finding = this.createXmlFindingElement(parent);

        // Set XSI:Type
        e_Finding.setAttribute(IExtendableSchemaElement.XML_XSI_TYPE, DeepSkyFindingOC.XML_XSI_TYPE_VALUE);

        if (this.unusualShape != -1) {
            e_Finding.setAttribute(XML_ELEMENT_FINDING_ATTRIBUTE_UNUSUALSHAPE,
                    Boolean.toString(this.getUnusualShape()));
        }

        if (this.partlyUnresolved != -1) {
            e_Finding.setAttribute(XML_ELEMENT_FINDING_ATTRIBUTE_PARTLYUNRESOLVED,
                    Boolean.toString(this.getPartlyUnresolved()));
        }

        if (this.colorContrasts != -1) {
            e_Finding.setAttribute(XML_ELEMENT_FINDING_ATTRIBUTE_COLORCONTRASTS,
                    Boolean.toString(this.getColorContrasts()));
        }

        if (this.character != null) {
            Element e_Character = ownerDoc.createElement(XML_ELEMENT_CHARACTER);
            Node n_CharacterText = ownerDoc.createCDATASection(this.getCharacter().toString());
            e_Character.appendChild(n_CharacterText);
            e_Finding.appendChild(e_Character);
        }

        parent.appendChild(e_Finding);

        return parent;

    }

    // --------------
    // Public methods ----------------------------------------------------
    // --------------

    /**
     * Returns the unusualShape value of this DeepSkyFindingOC.<br>
     * Describes if the observed object has an unusualShape.
     *
     * @return <code>true</code> if the observed object has an unusual Shape
     * @throws IllegalStateException
     *             if unusualShape was not set by the user so the class cannot return <b>true</b> or <b>false</b>
     */
    public boolean getUnusualShape() throws IllegalStateException {

        if (this.unusualShape == -1) {
            throw new IllegalStateException("UnusualShape value was never set for: " + this);
        }

        return unusualShape == 1;

    }

    /**
     * Returns the partlyUnresolved value of this DeepSkyFindingOC.<br>
     * Describes if the observed object was partly unresolved.
     *
     * @return <code>true</code> if the observed object was partly unresolved
     * @throws IllegalStateException
     *             if partlyUnresolved was not set by the user so the class cannot return <b>true</b> or <b>false</b>
     */
    public boolean getPartlyUnresolved() throws IllegalStateException {

        if (this.partlyUnresolved == -1) {
            throw new IllegalStateException("PartlyUnresolved value was never set for: " + this);
        }

        return partlyUnresolved == 1;

    }

    /**
     * Returns the colorContrasts value of this DeepSkyFindingOC.<br>
     * Describes if the observed object showed some color contrasts.
     *
     * @return <code>true</code> if the observed object showed some color contrasts
     * @throws IllegalStateException
     *             if colorContrasts was not set by the user so the class cannot return <b>true</b> or <b>false</b>
     */
    public boolean getColorContrasts() throws IllegalStateException {

        if (this.colorContrasts == -1) {
            throw new IllegalStateException("ColorContrasts value was never set for: " + this);
        }

        return colorContrasts == 1;

    }

    /**
     * Returns the character of this DeepSkyFindingOC.<br>
     * See DeepSkyFindingOC constants for valid values.<br>
     *
     * @return A character describing the open cluster characteristics according to the german "Deep Sky Liste" or
     *         <code>null</code> if the value was never set
     */
    public Character getCharacter() {

        return this.character;

    }

    /**
     * Sets the unusualShape value of this DeepSkyFindingOC.<br>
     * Describes if the observed object has an unusual shape.
     *
     * @param unusualShape
     *            The unusual shape value to set for this DeepSkyFindingOC or <code>NULL</code> if the value should be
     *            not set at all.
     */
    public void setUnusualShape(Boolean unusualShape) {

        if (unusualShape == null) {
            this.unusualShape = -1;
            return;
        }

        if (unusualShape) {
            this.unusualShape = 1;
        } else {
            this.unusualShape = 0;
        }

    }

    /**
     * Sets the partlyUnresolved value of this DeepSkyFindingOC.<br>
     * Describes if the observed object was partly unresolved.
     *
     * @param partlyUnresolved
     *            The partlyUnresolved value to set for this DeepSkyFindingOC or <code>NULL</code> if the value should
     *            be not set at all.
     */
    public void setPartlyUnresolved(Boolean partlyUnresolved) {

        if (partlyUnresolved == null) {
            this.partlyUnresolved = -1;
            return;
        }

        if (partlyUnresolved) {
            this.partlyUnresolved = 1;
        } else {
            this.partlyUnresolved = 0;
        }

    }

    /**
     * Sets the colorContrasts value of this DeepSkyFindingOC.<br>
     * Describes if the observed object showed some color contrasts.
     *
     * @param colorContrasts
     *            The colorContrasts value to set for this DeepSkyFindingOC or <code>NULL</code> if the value should be
     *            not set at all.
     */
    public void setColorContrasts(Boolean colorContrasts) {

        if (colorContrasts == null) {
            this.colorContrasts = -1;
            return;
        }

        if (colorContrasts) {
            this.colorContrasts = 1;
        } else {
            this.colorContrasts = 0;
        }

    }

    /**
     * Sets the colorContrasts value of this DeepSkyFindingOC.<br>
     * Describes if the observed object showed some color contrasts.
     *
     * @param c
     *            The colorContrasts value to set for this DeepSkyFindingOC or <code>NULL</code> if the value should be
     *            not set at all.
     * @throws IllegalArgumentException
     *             if the given character value is invalid
     */
    public void setCharacter(Character c) throws IllegalArgumentException {

        if ((c == null) || (Character.toUpperCase(c) == 'X')) {
            this.character = null;
            return;
        }

        char cv = c;
        if (DeepSkyFindingOC.CHARACTER_A == cv || DeepSkyFindingOC.CHARACTER_B == cv
                || DeepSkyFindingOC.CHARACTER_C == cv || DeepSkyFindingOC.CHARACTER_D == cv
                || DeepSkyFindingOC.CHARACTER_E == cv || DeepSkyFindingOC.CHARACTER_F == cv
                || DeepSkyFindingOC.CHARACTER_G == cv || DeepSkyFindingOC.CHARACTER_H == cv
                || DeepSkyFindingOC.CHARACTER_I == cv) {
            this.character = c;
        } else {
            throw new IllegalArgumentException("Character value is not valid.\n");
        }

    }

}
