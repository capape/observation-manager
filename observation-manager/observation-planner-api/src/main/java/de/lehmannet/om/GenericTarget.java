/* ====================================================================
 * /GenericTarget.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om;

import java.util.Locale;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.lehmannet.om.util.SchemaException;

public class GenericTarget extends Target {

    // ---------
    // Constants ---------------------------------------------------------
    // ---------

    // XSML schema instance value. Enables class/schema loaders to identify this
    // class
    public static final String XML_XSI_TYPE_VALUE = "oal:observationTargetType";

    // -----------
    // Constructor -------------------------------------------------------
    // -----------

public GenericTarget(Node targetElement, IObserver... observers) throws SchemaException {

        super(targetElement, observers);

    }

public GenericTarget(String name, String datasource) throws IllegalArgumentException {

        super(name, datasource);

    }

public GenericTarget(String name, IObserver observer) throws IllegalArgumentException {

        super(name, observer);

    }

    // ------
    // Object ------------------------------------------------------------
    // ------

/**
     * Overwrittes toString() method from java.lang.Object.<br>
     * Returns the field values of this GenericTarget.
     * 
     * @return This GenericTarget field values
     * @see java.lang.Object
     */
    @Override
    public String toString() {

        StringBuilder buffer = new StringBuilder();
        buffer.append("GenericTarget Name=");
        buffer.append(this.getName());

        if (this.getAliasNames().length > 0) {
            buffer.append(" Alias names=");
            String[] an = this.getAliasNames();
            for (int i = 0; i < an.length; i++) {
                buffer.append(an[i]);
                if (i <= an.length - 2) {
                    buffer.append(", ");
                }
            }
        }

        if (this.getPosition() != null) {
            buffer.append(" Position=");
            buffer.append(this.getPosition());
        }

        return buffer.toString();

    }

/**
     * Overwrittes equals(Object) method from java.lang.Object.<br>
     * Checks if this GenericTarget and the given Object are equal. The given object is equal with this GenericTarget,
     * if it derives from ITarget, both XSI types are equal and its name equals this GenericTarget name.<br>
     * 
     * @param obj
     *            The Object to compare this GenericTarget with.
     * @return <code>true</code> if the given Object is an instance of ITarget, both XSI types are equal and its name is
     *         equal to this GenericTarget name.<br>
     *         (Name comparism is <b>not</b> casesensitive)
     * @see java.lang.Object
     */
    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof ITarget)) {
            return false;
        }

        ITarget target = (ITarget) obj;

        String targetName = target.getName();
        if (targetName == null) {
            return false;
        }

        return (this.getName().toLowerCase(Locale.getDefault()).equals(targetName.toLowerCase(Locale.getDefault())))
                && (this.getXSIType()).equals(target.getXSIType());

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((this.getName() == null) ? 0 : this.getName().hashCode());
        result = prime * result +  this.getXSIType().hashCode();
        return result;
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
                    return;
                }
            }
        }

        // Create the new target element
        Element e_Target = this.createXmlTargetElement(e_Targets);
        e_Targets.appendChild(e_Target);

        // Set XSI:Type
        e_Target.setAttribute(ITarget.XML_XSI_TYPE, this.getXSIType());

        // If container element was created, add container here so that XML sequence
        // fits forward references
        // Calling the appendChild in the if avbe would cause the session container to
        // be located before
        // observers and sites container
        if (created) {
            ownerDoc.getDocumentElement().appendChild(e_Targets);
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

        return GenericTarget.XML_XSI_TYPE_VALUE;

    }

}
