package de.lehmannet.om.extension.deepSky;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.lehmannet.om.IObserver;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.RootElement;
import de.lehmannet.om.Target;
import de.lehmannet.om.ITargetContaining;
import de.lehmannet.om.TargetStar;
import de.lehmannet.om.util.SchemaException;

/**
 * DeepSkyTargetMS extends the de.lehmannet.om.extension.deepSky.DeepSkyTarget class.<br>
 * Its specialised for multiple stars. (Star systems with at least three components)<br>
 *
 * @author doergn@users.sourceforge.net
 * @since 2.0
 */
public class DeepSkyTargetMS extends Target implements ITargetContaining {

    private static Logger log = LoggerFactory.getLogger(DeepSkyTargetMS.class);
    // ---------
    // Constants ---------------------------------------------------------
    // ---------

    // XSML schema instance value. Enables class/schema loaders to identify this
    // class
    public static final String XML_XSI_TYPE_VALUE = "oal:deepSkyMS";

    // Constant for XML representation: component star of multiple star system
    private static final String XML_ELEMENT_COMPONENT = "component";

    // ------------------
    // Instance Variables ------------------------------------------------
    // ------------------

    // List of component stars as TargetStar unique IDs
    private List<String> components = null;

    // ------------
    // Constructors ------------------------------------------------------
    // ------------

    /**
     * Constructs a new instance of a DeepSkyTargetMS from a given DOM target Element.<br>
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
    public DeepSkyTargetMS(Node targetElement, IObserver... observers) throws SchemaException {

        super(targetElement, observers);

        Element target = (Element) targetElement;

        // Getting data

        // Get list of component stars
        NodeList children = target.getElementsByTagName(DeepSkyTargetMS.XML_ELEMENT_COMPONENT);
        List<String> componentTargetIDs = new ArrayList<>();
        if (children.getLength() > 0) {
            for (int i = 0; i < children.getLength(); i++) {
                Element child = (Element) children.item(i);
                String value = child.getFirstChild().getNodeValue();
                componentTargetIDs.add(value);
            }
            boolean setComponentsResult = this.setComponents(componentTargetIDs);
            if (!setComponentsResult) {
                throw new SchemaException(
                        "DeepSkyTargetMS is unable to add TargetStars as components. (ID: " + this.getID() + ")");
            }
        } else if (children.getLength() < 3) {
            throw new SchemaException(
                    "DeepSkyTargetMS must have at least three component stars. (ID: " + this.getID() + ")");
        }

    }

    public DeepSkyTargetMS(String starName, String datasource, List<String> componentStars) {

        super(starName, datasource);
        this.setComponents(componentStars);

    }

    public DeepSkyTargetMS(String starName, IObserver observer, List<String> componentStars) {

        super(starName, observer);
        this.setComponents(componentStars);

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

        return DeepSkyTargetMS.XML_XSI_TYPE_VALUE;

    }

    // ------
    // Target ------------------------------------------------------------
    // ------

    /**
     * Adds this DeepSkyTargetMS to a given parent XML DOM Element. The DeepSkyTargetMS element will be set as a child
     * element of the passed element.
     *
     * @param parent
     *            The parent element for this DeepSkyTargetMS
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
        NodeList nodeList = ownerDoc.getElementsByTagName(RootElement.XML_TARGET_CONTAINER);
        if (nodeList.getLength() == 0) { // we're the first element. Create container element
            e_Targets = ownerDoc.createElement(RootElement.XML_TARGET_CONTAINER);
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
        Element e_MSTarget = this.createXmlTargetElement(e_Targets);
        e_Targets.appendChild(e_MSTarget);

        // Check if element already exists
        if (e_MSTarget == null) {
            return;
        }
        // Set XSI:Type
        e_MSTarget.setAttribute(ITarget.XML_XSI_TYPE, DeepSkyTargetMS.XML_XSI_TYPE_VALUE);

        // Add all components
        // Cannot use addAsLinkToXMLElement as we're dealing with unique ID links only
        // here
        List<String> ct = this.getComponents();
        ListIterator<String> iterator = ct.listIterator();
        String current = null;
        while (iterator.hasNext()) {
            current = iterator.next();

            // Create the link element
            Element e_Link = ownerDoc.createElement(DeepSkyTargetMS.XML_ELEMENT_COMPONENT);
            Node n_LinkText = ownerDoc.createTextNode(current);
            e_Link.appendChild(n_LinkText);

            e_MSTarget.appendChild(e_Link);
        }

    }

    // --------------
    // Public methods ----------------------------------------------------
    // --------------

    /**
     * Returns a list with all components of this multiple star given as unique ID link.<br>
     * If you want to access all linked targets as de.lehmannet.om.TargetStar objects, call getComponentTargets().<br>
     *
     * @return A list with all components of this multiple star, as unique IDs.
     */
    private List<String> getComponents() {

        return new ArrayList<>(this.components);

    }

    /**
     * Returns a list with all components of this multiple star given as de.lehmannet.om.TargetStar.<br>
     * If you want to access all linked targets as unique ID (String) objects, call getComponents().<br>
     *
     * @return A list with all components of this multiple star, as de.lehmannet.om.TargetStar or an empty list if the
     *         components of this multiple star coundn't be found in the given array.
     */
    @Override
    public List<ITarget> getComponentTargets(ITarget... targets) {

        List<ITarget> result = new ArrayList<>();

        if (targets == null) {
            return result;
        }

        for (ITarget target : targets) {
            if (target instanceof TargetStar) {
                if (this.components.contains(target.getID())) {
                    result.add(target);
                }
            }
        }

        return result;

    }

    /**
     * Sets a new list of component stars for this multiple star target.<br>
     * The given list must contain at least three entries of type de.lehmannet.TargetStar or of type String (which will
     * be interpreted as unique ID of a linked TargetStar.<br>
     * For double stars use: de.lehmannet.om.extension.deepSky.DeepSkyTargetDS<br>
     * The current list of component stars will be overwritten. To add one or more component stars use
     * addComponent(de.lehmannet.om.TargetStar), addComponents(List), addComponent(String)
     *
     * @see de.lehmannet.om.extension.deepSky.DeepSkyTargetDS
     * @see de.lehmannet.om.TargetStar
     * @param newComponents
     *            A list with at least 3 entries of type de.lehmannet.om.TargetStar or java.lang.String that represent
     *            the components of this multiple star system
     * @return <code>true</code> only if operation succeeded.
     */
    public boolean setComponents(List<String> newComponents) {

        if ((newComponents == null) || newComponents.isEmpty()) {
            return false;
        }

        // Make sure all entries are from type de.lehmannet.om.TargetStar or String
        ListIterator<String> iterator = newComponents.listIterator();
        Object current = null;
        List<String> resultList = new ArrayList<>();
        while (iterator.hasNext()) {
            current = iterator.next();
            if (current instanceof TargetStar) {
                resultList.add(((TargetStar) current).getID()); // Add only the ID of the target
            } else if (current instanceof String) {
                resultList.add(current.toString());
            } else {
                log.error("DeepSkyTargetMS cannot add: {} as component. Continue with next entry...", current);
            }
        }

        // Check if result list still contains at least three stars
        if (resultList.size() < 3) {
            return false;
        }

        this.components = resultList;
        return true;

    }

    /**
     * Adds a list of component stars to this multiple star target.<br>
     * The given list must contain at least three entries of type de.lehmannet.TargetStar or java.lang.String in case
     * getComponents() returns <code>null</code><br>
     * For double stars use: de.lehmannet.om.extension.deepSky.DeepSkyTargetDS<br>
     * The given list will be added to the current list of component stars.
     *
     * @see de.lehmannet.om.TargetStar
     * @param additionalComponents
     *            A list of new components stars
     * @return <code>true</code> only if operation succeeded.
     */
    public boolean addComponents(List<String> additionalComponents) {

        if ((additionalComponents == null) || additionalComponents.isEmpty()) {
            return false;
        }

        // Make sure all entries are from type de.lehmannet.om.TargetStar
        ListIterator<String> iterator = additionalComponents.listIterator();
        Object current = null;
        List<String> resultList = new ArrayList<>();
        while (iterator.hasNext()) {
            current = iterator.next();
            if (current instanceof TargetStar) {
                resultList.add(((TargetStar) current).getID()); // Only add ID
            } else if (current instanceof String) {
                resultList.add(current.toString());
            }
        }

        // Check if components list exists already or
        // result list contains at least three stars
        if ((resultList.size() < 3) && (this.components == null)) {
            return false;
        }

        this.components.addAll(additionalComponents);
        return true;

    }

    /**
     * Adds a new star to a given list of component stars. If getComponents() returns <code>null</code> you need to use
     * setComponents(List) first, to initially add (at least three) component stars.
     *
     * @see addComponents(List)
     * @param additionalStar
     *            A new component star
     * @return <code>true</code> only if operation succeeded.
     */
    public boolean addComponent(TargetStar additionalStar) {

        if (additionalStar == null) {
            return false;
        }

        if (this.components == null) {
            return false;
        }

        this.components.add(additionalStar.getID());
        return true;

    }

    /**
     * Adds a new star to a given list of component stars. If getComponents() returns <code>null</code> you need to use
     * setComponents(List) first, to initially add (at least three) component stars.
     *
     * @see addComponents(List)
     * @param additionalStar
     *            A new component star (as unique ID string)
     * @return <code>true</code> only if operation succeeded.
     */
    public boolean addComponent(String additionalStar) {

        if (additionalStar == null) {
            return false;
        }

        if (this.components == null) {
            return false;
        }

        this.components.add(additionalStar);
        return true;

    }

}
