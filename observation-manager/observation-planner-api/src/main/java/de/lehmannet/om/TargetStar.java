package de.lehmannet.om;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.lehmannet.om.util.FloatUtil;
import de.lehmannet.om.util.SchemaException;

/**
 * TargetStar extends the de.lehmannet.om.Target class.<br>
 * Its specialised for single stars.<br>
 *
 * @author doergn@users.sourceforge.net
 * @since 2.0
 */
public class TargetStar extends Target {

    // ---------
    // Constants ---------------------------------------------------------
    // ---------

    // XSML schema instance value. Enables class/schema loaders to identify this
    // class
    public static final String XML_XSI_TYPE_VALUE = "oal:starTargetType";

    // Constant for XML representation: large diameter element name
    private static final String XML_ELEMENT_MAG_APPARENT = "apparentMag";

    // Constant for XML representation: large diameter element name
    private static final String XML_ELEMENT_CLASSIFICATION = "classification";

    // ------------------
    // Instance Variables ------------------------------------------------
    // ------------------

    // Stars apparent magnitude
    private float magnitudeApparent = Float.NaN;

    // Stellar classification like O,B,A,F,G,K,M
    private String stellarClassification = null;

    // ------------
    // Constructors -----------------------------------------------------------
    // ------------

    /**
     * Constructs a new instance of a TargetStar from a given DOM target Element.<br>
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
    public TargetStar(Node targetElement, IObserver... observers) throws SchemaException {

        super(targetElement, observers);

        Element target = (Element) targetElement;

        // Getting data

        // Get optional apparent magnitude
        NodeList children = target.getElementsByTagName(TargetStar.XML_ELEMENT_MAG_APPARENT);
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            String value = child.getFirstChild().getNodeValue();
            this.setMagnitudeApparent(FloatUtil.parseFloat(value));
        } else if (children.getLength() > 1) {
            throw new SchemaException("TargetStar can only have one apparent magnitude. (ID: " + this.getID() + ")");
        }

        // Get optional classification
        children = target.getElementsByTagName(TargetStar.XML_ELEMENT_CLASSIFICATION);
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            String value = child.getFirstChild().getNodeValue();
            this.setStellarClassification(value);
        } else if (children.getLength() > 1) {
            throw new SchemaException(
                    "TargetStar can only have one stellar classification. (ID: " + this.getID() + ")");
        }

    }

    public TargetStar(String starName, String datasource) {

        super(starName, datasource);

    }

    public TargetStar(String starName, IObserver observer) {

        super(starName, observer);

    }

    // ------
    // Object ------------------------------------------------------------
    // ------

    /**
     * Overwrittes toString() method from java.lang.Object.<br>
     * Returns the field values of this TargetStar.
     *
     * @return This TargetStar field values
     * @see java.lang.Object
     */
    @Override
    public String toString() {

        StringBuilder buffer = new StringBuilder();
        buffer.append("TargetStar Name=");
        buffer.append(this.getName());

        if (this.getPosition() != null) {
            buffer.append("\nPosition:");
            buffer.append(this.getPosition());
        }

        if (this.getDatasource() != null) {
            buffer.append("\nDatasource:");
            buffer.append(this.getDatasource());
        } else {
            buffer.append("\nObserver:");
            buffer.append(this.getObserver());
        }

        if (this.getConstellation() != null) {
            buffer.append("\nConstellation:");
            buffer.append(this.getConstellation());
        }

        if (this.getAliasNames().length > 0) {
            buffer.append("\nAlias Names: ");
            String[] an = this.getAliasNames();
            for (String s : an) {
                buffer.append(s).append("  ");
            }
        }

        if (!Float.isNaN(this.magnitudeApparent)) {
            buffer.append("\nApparent magnitude=");
            buffer.append(this.magnitudeApparent);
        }

        if (this.stellarClassification != null) {
            buffer.append("\nStellar Classification=");
            buffer.append(this.stellarClassification);
        }

        return buffer.toString();

    }

    // ------
    // Target ------------------------------------------------------------
    // ------

    /**
     * Adds this TargetStar to a given parent XML DOM Element. The Target element will be set as a child element of the
     * passed element.
     *
     * @param parent
     *            The parent element for this TargetStar
     * @see org.w3c.dom.Element
     */
    @Override
    public void addToXmlElement(Element element) {

        this.createXmlTargetStarElement(element, this.getXSIType());

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

        return TargetStar.XML_XSI_TYPE_VALUE;

    }

    // --------------
    // public final methods ----------------------------------------------------
    // --------------

    /**
     * Returns the apparent magnitude of the star.<br>
     * Might be Float.NaN if value was never set.
     *
     * @return The apparent magnitude of the star or Float.NaN if value was never set
     */
    public final float getMagnitudeApparent() {

        return this.magnitudeApparent;

    }

    /**
     * Sets the apparent magnitude of the star.<br>
     */
    public final void setMagnitudeApparent(float magnitudeApparent) {

        this.magnitudeApparent = magnitudeApparent;

    }

    /**
     * Returns the stellar classification of the star.<br>
     * Classification might be e.g. O,B,A,F,G,K,M or some more specific value.<br>
     * Might be <code>null</code> if value was never set.
     *
     * @return The stellar classification of the star
     */
    public final String getStellarClassification() {

        return this.stellarClassification;

    }

    /**
     * Sets the stellar classification of the star.<br>
     */
    public final void setStellarClassification(String pstellarClassification) {

        // Convert empty string to NULL
        String stellarClassification;
        if (pstellarClassification != null) {
            stellarClassification = pstellarClassification.trim();

            if ("".equals(stellarClassification)) {
                stellarClassification = null;
            }
        } else {
            stellarClassification = null;
        }

        this.stellarClassification = stellarClassification;

    }

    // -----------------
    // Protected methods -------------------------------------------------
    // -----------------

    /**
     * Creates a deepkSkyTarget under the target container. If no target container exists under the given elements
     * ownerDocument, it will be created.<br>
     * This method should be called by subclasses, so that they only have to add their specific data to the element
     * returned. Example:<br>
     * &lt;parameterElement&gt;<br>
     * <b>&lt;targetLink&gt;123&lt;/targetLink&gt;</b><br>
     * &lt;/parameterElement&gt;<br>
     * <i>More stuff of the xml document goes here</i><br>
     * <b>&lt;targetContainer&gt;</b><br>
     * <b>&lt;target id="123"&gt;</b><br>
     * <i>target description goes here</i><br>
     * <b>&lt;/target&gt;</b><br>
     * <b>&lt;/targetContainer&gt;</b><br>
     * <br>
     *
     * @param element
     *            The element under which the the target link is created
     * @param xsiType
     *            The XSI:Type identification of the child class
     * @return Returns a new created target Element that contains all data from a DeepSkyTarget. Please mind, NOT the
     *         passed element is given, but a child element of the passed elements ownerDocument. Might return
     *         <code>null</code> if element was <code>null</code>.
     * @see org.w3c.dom.Element
     */
    protected Element createXmlTargetStarElement(Element element, String xsiType) {

        if (element == null) {
            return null;
        }

        Document ownerDoc = element.getOwnerDocument();

        // Get or create the container element
        Element e_Targets = null;
        boolean created = false;
        NodeList nodeList = ownerDoc.getElementsByTagName(RootElement.XML_TARGET_CONTAINER);
        if (nodeList.getLength() == 0) { // we're the first element. Create container element
            e_Targets = ownerDoc.createElement(RootElement.XML_TARGET_CONTAINER);
            created = true;
        } else {
            e_Targets = (Element) nodeList.item(0); // there should be only one container element
        }

        // Check if this element doesn't exist so far
        nodeList = e_Targets.getElementsByTagName(ITarget.XML_ELEMENT_TARGET);
        if (nodeList.getLength() > 0) {
            Node currentNode = null;
            NamedNodeMap attributes = null;
            for (int i = 0; i < nodeList.getLength(); i++) { // iterate over all found nodes
                currentNode = nodeList.item(i);
                attributes = currentNode.getAttributes();
                Node idAttribute = attributes.getNamedItem(ISchemaElement.XML_ELEMENT_ATTRIBUTE_ID);
                if ((idAttribute != null) // if ID attribute is set and equals this objects ID, return existing element
                        && (idAttribute.getNodeValue().trim().equals(this.getID().trim()))) {
                    // Not sure if this is good!? Maybe we should return currentNode and make
                    // doublicity check in caller
                    // class!?
                    return null;
                }
            }
        }

        // Create the new target element
        Element e_Target = this.createXmlTargetElement(e_Targets);
        e_Targets.appendChild(e_Target);

        // Set XSI:Type
        e_Target.setAttribute(ITarget.XML_XSI_TYPE, this.getXSIType());

        // Set optional values
        if (!Float.isNaN(this.magnitudeApparent)) {
            Element e_MagnitudeApp = ownerDoc.createElement(TargetStar.XML_ELEMENT_MAG_APPARENT);
            Node n_MagnitudeAppText = ownerDoc.createTextNode(Float.toString(this.magnitudeApparent));
            e_MagnitudeApp.appendChild(n_MagnitudeAppText);

            e_Target.appendChild(e_MagnitudeApp);
        }

        if (this.stellarClassification != null) {
            Element e_Classfication = ownerDoc.createElement(TargetStar.XML_ELEMENT_CLASSIFICATION);
            Node n_ClassificationText = ownerDoc.createTextNode(this.stellarClassification);
            e_Classfication.appendChild(n_ClassificationText);

            e_Target.appendChild(e_Classfication);
        }

        // If container element was created, add container here so that XML sequence
        // fits forward references
        // Calling the appendChild in the if avbe would cause the session container to
        // be located before
        // observers and sites container
        if (created) {
            ownerDoc.getDocumentElement().appendChild(e_Targets);
        }

        return e_Target;

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Float.floatToIntBits(magnitudeApparent);
        result = prime * result + ((stellarClassification == null) ? 0 : stellarClassification.hashCode());
        return result;
    }

    /**
     * Overwrittes equals(Object) method from java.lang.Object.<br>
     * Checks if this TargetStar and the given Object are equal. The given object is equal with this TargetStar, if it
     * derives from ITarget, both XSI types are equal and its name equals this TargetStar name.<br>
     *
     * @param obj
     *            The Object to compare this TargetStar with.
     * @return <code>true</code> if the given Object is an instance of ITarget, both XSI types are equal and its name is
     *         equal to this TargetStar name.<br>
     *         (Name comparism is <b>not</b> casesensitive)
     * @see java.lang.Object
     */
    @Override
    public boolean equals(Object obj) {

        if (obj == null || !(obj instanceof ITarget)) {
            return false;
        }

        ITarget other = (ITarget) obj;
        if (this == obj)
            return true;

        String targetName = other.getName();
        if (targetName == null) {
            return false;
        }

        return StringUtils.equalsIgnoreCase(this.getName(), targetName) && this.getXSIType().equals(other.getXSIType());

    }

}
