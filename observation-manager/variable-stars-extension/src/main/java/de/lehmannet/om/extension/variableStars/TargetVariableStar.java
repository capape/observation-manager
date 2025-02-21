package de.lehmannet.om.extension.variableStars;

import de.lehmannet.om.IObserver;
import de.lehmannet.om.TargetStar;
import de.lehmannet.om.util.FloatUtil;
import de.lehmannet.om.util.SchemaException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * TargetVariableStar extends the de.lehmannet.om.TargetStar class.<br>
 * Its specialised for variable stars.<br>
 *
 * @author doergn@users.sourceforge.net
 * @since 2.0
 */
public class TargetVariableStar extends TargetStar {

    // ---------
    // Constants ---------------------------------------------------------
    // ---------

    // XSML schema instance value. Enables class/schema loaders to identify this
    // class
    public static final String XML_XSI_TYPE_VALUE = "oal:variableStarTargetType";

    // Constant for XML representation: variable star type element name
    private static final String XML_ELEMENT_VARIABLESTARTYPE = "type";

    // Constant for XML representation: maximal apparent magnitude element name
    private static final String XML_ELEMENT_MAXAPPARENTMAG = "maxApparentMag";

    // Constant for XML representation: period of variable star (if any)
    private static final String XML_ELEMENT_PERIOD = "period";

    // ------------------
    // Instance Variables ------------------------------------------------
    // ------------------

    // Variable star type like pulsating variable star, Delta Cepheid, ...
    private String type = null;

    // Variable star maximal magnitude (super.magnitudeApparent is used for minimal
    // value)
    private float maxApparentMag = Float.NaN;

    // Variable star period (if any)
    private float period = Float.NaN;

    // ------------
    // Constructors ------------------------------------------------------
    // ------------

    /**
     * Constructs a new instance of a TargetVariableStar from a given DOM target Element.<br>
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
    public TargetVariableStar(Node targetElement, IObserver... observers) throws SchemaException {

        super(targetElement, observers);

        Element target = (Element) targetElement;

        // Getting data

        // Get optional max app. magnitude
        NodeList children = target.getElementsByTagName(TargetVariableStar.XML_ELEMENT_MAXAPPARENTMAG);
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            String value = child.getFirstChild().getNodeValue();
            try {
                float fMaxAppMag = FloatUtil.parseFloat(value);
                this.setMaxMagnitudeApparent(fMaxAppMag);
            } catch (NumberFormatException nfe) {
                throw new SchemaException(
                        "Maximal apparent magnitude of TargetVariableStar must be a numeric value. (ID: " + this.getID()
                                + ")",
                        nfe);
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException(
                    "TargetVariableStar can only have one maximal apparent magnitude. (ID: " + this.getID() + ")");
        }

        // Get optional type
        children = target.getElementsByTagName(TargetVariableStar.XML_ELEMENT_VARIABLESTARTYPE);
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            StringBuilder value = new StringBuilder();
            NodeList textElements = child.getChildNodes();
            if (textElements.getLength() > 0) {
                for (int te = 0; te < textElements.getLength(); te++) {
                    value.append(textElements.item(te).getNodeValue());
                }
                // currentCompStar = child.getFirstChild().getNodeValue();
                this.setType(value.toString());
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException(
                    "TargetVariableStar can only have one variable star type. (ID: " + this.getID() + ")");
        }

        // Get optional period
        children = target.getElementsByTagName(TargetVariableStar.XML_ELEMENT_PERIOD);
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            String value = child.getFirstChild().getNodeValue();
            try {
                float period = FloatUtil.parseFloat(value);
                this.setPeriod(period);
            } catch (NumberFormatException nfe) {
                throw new SchemaException(
                        "Period of TargetVariableStar must be a numeric value. (ID: " + this.getID() + ")", nfe);
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("TargetVariableStar can only have one period. (ID: " + this.getID() + ")");
        }
    }

    public TargetVariableStar(String starName, String datasource) {

        super(starName, datasource);
    }

    public TargetVariableStar(String starName, IObserver observer) {

        super(starName, observer);
    }

    // ------
    // Object ------------------------------------------------------------
    // ------

    /**
     * Overwrittes toString() method from java.lang.Object.<br>
     * Returns the field values of this TargetVariableStar.
     *
     * @return This TargetVariableStar field values
     * @see java.lang.Object
     */
    @Override
    public String toString() {

        StringBuilder buffer = new StringBuilder(super.toString());
        buffer.append("\n ----- Target VariableStar Values=");

        if (this.type != null) {
            buffer.append("\nVariable star type=");
            buffer.append(this.type);
        }

        if (!Float.isNaN(this.maxApparentMag)) {
            buffer.append("\nMax apparent magnitude=");
            buffer.append(this.maxApparentMag);
        }

        if (!Float.isNaN(this.period)) {
            buffer.append("\nPeriod=");
            buffer.append(this.period);
        }

        return buffer.toString();
    }

    // ------
    // Target ------------------------------------------------------------
    // ------

    /**
     * Adds this TargetVariableStar to a given parent XML DOM Element. The TargetVariableStar element will be set as a
     * child element of the passed element.
     *
     * @param parent
     *            The parent element for this TargetVariableStar
     * @see org.w3c.dom.Element
     */
    @Override
    public void addToXmlElement(Element element) {

        if (element == null) {
            return;
        }

        // Create TargetStar element
        Element e_VSTarget = this.createXmlTargetStarElement(element, TargetVariableStar.XML_XSI_TYPE_VALUE);

        // Check if element already exists
        if (e_VSTarget == null) {
            return;
        }

        Document ownerDoc = element.getOwnerDocument();

        // Set optional values
        if (this.type != null) {
            Element e_Type = ownerDoc.createElement(TargetVariableStar.XML_ELEMENT_VARIABLESTARTYPE);
            Node n_TypeText = ownerDoc.createTextNode(this.type);
            e_Type.appendChild(n_TypeText);

            e_VSTarget.appendChild(e_Type);
        }

        if (!Float.isNaN(this.maxApparentMag)) {
            Element e_maxMag = ownerDoc.createElement(TargetVariableStar.XML_ELEMENT_MAXAPPARENTMAG);
            Node n_maxMagText = ownerDoc.createTextNode(Float.toString(this.maxApparentMag));
            e_maxMag.appendChild(n_maxMagText);

            e_VSTarget.appendChild(e_maxMag);
        }

        if (!Float.isNaN(this.period)) {
            Element e_Period = ownerDoc.createElement(TargetVariableStar.XML_ELEMENT_PERIOD);
            Node n_Period = ownerDoc.createTextNode(Float.toString(this.period));
            e_Period.appendChild(n_Period);

            e_VSTarget.appendChild(e_Period);
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

        return TargetVariableStar.XML_XSI_TYPE_VALUE;
    }

    // --------------
    // Public methods ----------------------------------------------------
    // --------------

    /**
     * Returns the maximal apparent magnitude of this variable star.<br>
     * To access the minimal apparent magnitude of this variable star, please use getMagnitudeApparent() (derived vom
     * de.lehmannet.om.TargetStar)<br>
     * Might be <code>Float.NaN</code> if value was never set.
     *
     * @return The maximal apparent magnitude
     */
    public float getMaxApparentMag() {

        return this.maxApparentMag;
    }

    /**
     * Sets the maximal apparent magnitude of this variable star.<br>
     * For unsetting this value, please pass <code>Float.NaN</code> as parameter.
     */
    public void setMaxMagnitudeApparent(float maxApparentMag) {

        this.maxApparentMag = maxApparentMag;
    }

    /**
     * Returns the type of this variable star.<br>
     * This can be any free string describing the variable star type like: Cepheids, RR Lyrae stars, Semiregular,
     * Supernovae, Novae, R Coronae Borealis, Eclipsing Binary Stars, ... <br>
     * A good description of variable star types can be found at the
     * <a href="http://www.aavso.org/vstar/types.shtml">AAVSO page</a><br>
     * Might be <code>null</code> if value was never set.
     *
     * @return The variable star classification
     */
    public String getType() {

        return type;
    }

    /**
     * Sets the type of this variable star.<br>
     */
    public void setType(String type) {

        if ((type == null) || ("".equals(type.trim()))) {
            this.type = null;
            return;
        }

        this.type = type.trim();
    }

    /**
     * Returns the period of this variable star in days.<br>
     * Might be <code>Float.NaN</code> if value was never set (or the variable star has no period)
     *
     * @return The period of the variable star in days
     */
    public float getPeriod() {

        return this.period;
    }

    /**
     * Sets the period of this variable star in days.<br>
     * For unsetting this value, please pass <code>Float.NaN</code> as parameter.
     */
    public void setPeriod(float period) {

        this.period = period;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
