/* ====================================================================
 * /Finding.java
 *
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.lehmannet.om.util.SchemaException;

/**
 * The abstract class Finding provides some common features that may be used by the subclasses of an
 * de.lehmannet.om.IFinding.<br>
 * The Finding class stores a description of the findings and provides simple access to this description field. It also
 * implements a basic XML DOM helper method that may be used by all subclasses that have to implement the IFinding
 * interface.
 *
 * @author doergn@users.sourceforge.net
 *
 * @since 1.0
 */
public abstract class Finding extends SchemaElement implements IFinding {

    // ------------------
    // Instance Variables ------------------------------------------------
    // ------------------

    // Description of the finding
    private String description = null;

    // ISO language string giving this findings language
    private String lang = null;

    // Indicates whether the target has been successfuly seen, or not
    private boolean targetSeen = true;

    /**
     * Protected constructor used by subclasses construction.
     *
     * @param findingElement
     *            The XML Node representing this Finding
     *
     * @throws SchemaException
     *             if the XML element is malformed
     */
    protected Finding(Node findingElement) throws SchemaException {

        if (findingElement == null) {
            throw new SchemaException("Given element cannot be NULL. ");
        }

        Element finding = (Element) findingElement;

        // Make sure the element belongs to this class
        String xsiType = finding.getAttribute(IExtendableSchemaElement.XML_XSI_TYPE);
        if (!StringUtils.isBlank(xsiType)) {
            // Get mandatory description
            NodeList children = finding.getElementsByTagName(IFinding.XML_ELEMENT_DESCRIPTION);
            if (children.getLength() != 1) {
                throw new SchemaException("Finding must have exact one description. ");
            }
            Element child = (Element) children.item(0);
            StringBuilder description = null;
            if (child == null) {
                throw new SchemaException("Finding must have a description. ");
            } else {
                // Check if description has a child
                NodeList childrenNodeList = child.getChildNodes();
                if (childrenNodeList.getLength() > 0) {
                    description = new StringBuilder();
                    for (int i = 0; i < childrenNodeList.getLength(); i++) {
                        description.append(childrenNodeList.item(i).getNodeValue());
                    }
                    this.setDescription(description.toString());
                } else {

                    this.setDescription("");

                }
            }

        }

        // Get optional language
        String language = finding.getAttribute(IFinding.XML_ELEMENT_ATTRIBUTE_LANGUAGE);
        if (!StringUtils.isBlank(language)) {
            this.setLanguage(language);
        }

    }

    /**
     * Protected constructor used by subclasses construction.
     *
     * @param ID
     *            This elements unique ID
     * @param description
     *            The description of the finding
     *
     * @throws IllegalArgumentException
     *             if description was <code>null</code>
     */
    Finding(String ID, String description) throws IllegalArgumentException {

        super(ID);

        if (description == null) {
            throw new IllegalArgumentException("Description cannot be null. ");
        }
        this.description = description;

    }

    /**
     * Protected constructor used by subclasses construction.
     *
     * @param description
     *            The description of the finding
     *
     * @throws IllegalArgumentException
     *             if description was <code>null</code>
     */
    protected Finding(String description) throws IllegalArgumentException {

        if (description == null) {
            throw new IllegalArgumentException("Description cannot be null. ");
        }

        this.description = description;

    }

    // --------
    // IFinding ----------------------------------------------------------
    // --------

    /**
     * Adds the Finding to an given parent XML DOM Element. This abstract method is derived from
     * de.lehmannet.om.IFinding.
     *
     * @param parent
     *            The parent element for the Finding
     *
     * @return Returns the Element given as parameter with the Finding as child Element.
     *
     * @see org.w3c.dom.Element
     * @see de.lehmannet.om.IFinding
     */
    @Override
    public abstract org.w3c.dom.Element addToXmlElement(org.w3c.dom.Element parent);

    /**
     * Returns the description of the Finding. The string describes the impressions the observer had during the
     * observation of an object.
     *
     * @return The description of the finding.
     */
    @Override
    public String getDescription() {

        return description;

    }

    /**
     * Sets the description of the Finding. The string should describe the impressions the observer had during the
     * observation of an object.
     *
     * @param description
     *            A description of the finding.
     *
     * @throws IllegalArgumentException
     *             if description was <code>null</code>
     */
    @Override
    public void setDescription(String description) throws IllegalArgumentException {

        if (description == null) {
            throw new IllegalArgumentException("Description cannot be null. ");
        }

        this.description = description;

    }

    /**
     * Returns the language in which this session is described as ISO language string. E.g. de=German, fr=French,
     * ...<br>
     * Might return <code>null</code> if no language was set for this session.
     *
     * @return Returns a ISO language code that represents the sessions describtion language or <code>null</code> if no
     *         language was set at all.
     *
     * @since 1.5
     */
    @Override
    public String getLanguage() {

        return this.lang;

    }

    /**
     * Sets the language in which this finding is described. String must be given as ISO language string. E.g.
     * de=German, fr=French, ...<br>
     *
     * @param language
     *            ISO language string
     *
     * @since 1.5
     */
    @Override
    public void setLanguage(String language) {

        if ((language != null) && ("".equals(language.trim()))) {
            this.lang = null;
            return;
        }

        this.lang = language;

    }

    /**
     * Returns <code>true</code> if the target was seen with this finding or not. As findings might be created to
     * document that an object was not seen this flag can be used for checks.
     *
     * @return Returns <code>true</code> if the target was seen with this finding
     *
     * @since 1.6
     */
    @Override
    public boolean wasSeen() {

        return this.targetSeen;

    }

    /**
     * Set to <code>true</code> if the target was seen by the observer.<br>
     *
     * @param seen
     *            <code>true</code> if the target was seen by the observer or <code>false</code> if the target was not
     *            seen
     *
     * @since 1.6
     */
    @Override
    public void setSeen(boolean seen) {

        this.targetSeen = seen;

    }

    // -----------------
    // Protected methods -------------------------------------------------
    // -----------------

    /**
     * Creates an XML DOM Element for the Finding. The new Finding element will be added as child element to an given
     * parent element. All specialised subclasses may use this method to create a Finding element under which they may
     * store their addition data.<br>
     * Example:<br>
     * &lt;parentElement&gt;<br>
     * &lt;result&gt;<br>
     * <i>More specialised stuff goes here</i><br>
     * &lt;/result&gt;<br>
     * &lt;/parentElement&gt;
     *
     * @param parent
     *            The parent element for the Finding
     *
     * @return Returns a Finding element which is a child of the given parent element.<br>
     *         Might return <code>null</code> if parent was <code>null</code>.
     *
     * @see org.w3c.dom.Element
     */
    protected Element createXmlFindingElement(Element parent) {

        if (parent == null) {
            return null;
        }

        Document ownerDoc = parent.getOwnerDocument();

        Element e_Finding = ownerDoc.createElement(XML_ELEMENT_FINDING);

        Element e_Description = ownerDoc.createElement(XML_ELEMENT_DESCRIPTION);

        if (description != null) {
            Node n_DescriptionText = ownerDoc.createCDATASection(description);
            e_Description.appendChild(n_DescriptionText);
        }
        e_Finding.appendChild(e_Description);

        if (this.lang != null) {
            e_Finding.setAttribute(IFinding.XML_ELEMENT_ATTRIBUTE_LANGUAGE, this.lang);
        }

        return e_Finding;

    }

}
