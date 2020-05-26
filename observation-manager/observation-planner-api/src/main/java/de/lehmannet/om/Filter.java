/* ====================================================================
 * /Filter.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.lehmannet.om.mapper.FilterMapper;
import de.lehmannet.om.util.SchemaException;

/**
 * Filter implements the class de.lehmannet.om.IFilter. A Filter describes a optical filter used during the
 * observation.<br>
 * This includes all kind of filters like color filters, band filter, ... <br>
 * The model name and the type are mandatory fields which have to be set.
 * 
 * @author doergn@users.sourceforge.net
 * @since 1.5
 */
public class Filter extends SchemaElement implements IFilter {

    // ------------------
    // Instance Variables ------------------------------------------------
    // ------------------

    // Model name of the filter (usually given by vendor)
    private String model = "";

    // Type of filter (Narrow band, Color, O-III, ...)
    private String type = "";

    // Color of filter (only relevant for color filter type)
    private String color = null;

    // Wratten value of filter
    private String wratten = null;

    // Schott value of filter
    private String schott = null;

    // Vendor name of filter
    private String vendor = null;

    // Flag indicating whether Filter is still available
    private boolean available = true;

    // ------------
    // Constructors ------------------------------------------------------
    // ------------

/**
     * Constructs a new instance of a Filter from an given XML Schema Node.<br>
     * Normally this constructor is only used by de.lehmannet.om.util.SchemaLoader
     *
     * @param filter
     *            The XML Schema element that represents this filter
     * @throws IllegalArgumentException
     *             if parameter is <code>null</code>,
     * @throws SchemaException
     *             if the given Node does not match the XML Schema specifications
     */
    public Filter(Node filter) throws SchemaException, IllegalArgumentException {

        if (filter == null) {
            throw new IllegalArgumentException("Parameter filter node cannot be NULL. ");
        }

        // Cast to element as we need some methods from it
        Element filterElement = (Element) filter;

        // Helper classes
       
        

        // Getting data
        // First mandatory stuff and down below optional data
        this.setID(FilterMapper.getID(filterElement));
        this.setModel(FilterMapper.getMandatoryModel(filterElement));
        this.setType(FilterMapper.getMandatoryType(filterElement));
        this.setAvailability(FilterMapper.getOptionalAvailability(filterElement));
        this.setColor(FilterMapper.getOptionalColor(filterElement));
        this.setWratten(FilterMapper.getOptionalWrattenValue(filterElement));
        this.setSchott(FilterMapper.getOptionalSchottValue(filterElement));
        this.setVendor(FilterMapper.getOptionalVendorName(filterElement));

    }


/**
     * Constructs a new instance of a Filter.<br>
     *
     * @param model
     *            The filter model name
     * @param type
     *            The type of the filter (use IFilter constants)
     * @throws IllegalArgumentException
     *             if model is empty <code>null</code> or type is empty, <code>null</code> or does not represent a type
     *             value from IFilter.
     */
    public Filter(String model, String type) {

        this.setModel(model);
        this.setType(type);

    }

    // ------
    // Object ------------------------------------------------------------
    // ------

/**
     * Overwrittes toString() method from java.lang.Object.<br>
     * Returns all fields of the class Filter (unset field will be ignored). The result string will look like this:<br>
     * Example:<br>
     * <code>
     * Filter Model: Meade Narrowband<br>
     * Type: narrow band
     * </code>
     * 
     * @return A string representing the filter
     * @see java.lang.Object
     */
    @Override
    public String toString() {

        StringBuilder buffer = new StringBuilder();

        buffer.append("Filter Model: ");
        buffer.append(this.getModel()).append("\n");

        buffer.append("Type: ");
        buffer.append(this.getType()).append("\n");

        if (color != null) {
            buffer.append("Color: ");
            buffer.append(this.getColor()).append("\n");
        }

        if (wratten != null) {
            buffer.append("Wratten: ");
            buffer.append(this.getWratten()).append("\n");
        }

        if (schott != null) {
            buffer.append("Schott: ");
            buffer.append(this.getSchott()).append("\n");
        }

        return buffer.toString();

    }

/*
     * public boolean equals(Object obj) {
     * 
     * if( obj == null || !(obj instanceof IFilter) ) { return false; }
     * 
     * IFilter filter = (IFilter)obj;
     * 
     * if( !(this.model.equals(filter.getModel())) ) { return false; }
     * 
     * if( !(this.type.equals(filter.getType())) ) { return false; }
     * 
     * if( this.color != null ) { if( !this.color.equals(filter.getColor()) ) { return false; } } else if(
     * filter.getColor() != null ) { return false; }
     * 
     * if( this.wratten != null ) { if( !this.wratten.equals(filter.getWratten()) ) { return false; } } else if(
     * filter.getWratten() != null ) { return false; }
     * 
     * if( this.schott != null ) { if( !this.schott.equals(filter.getSchott()) ) { return false; } } else if(
     * filter.getSchott() != null ) { return false; }
     * 
     * return true;
     * 
     * }
     */

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
     * 
     */

    @Override
    public String getDisplayName() {

        /*
         * if( (this.color != null) && !("".equals(this.color.trim())) ) { dn = dn + " " + this.getColor(); }
         */

        // Don't add type and color as we wanna translate them later on UI

        return this.getModel();

    }

    // ----------
    // IEquipment --------------------------------------------------------
    // ----------

/**
     * Returns <code>true</code> if this element is still available for use-<br>
     * 
     * @return a boolean with the availability of the element
     */
    @Override
    public boolean isAvailable() {

        return this.available;

    }

/**
     * Sets the availability of this element.<br>
     * 
     * @param available
     *            A boolean value indicating whether this element is still available for usage
     */
    @Override
    public void setAvailability(boolean available) {

        this.available = available;

    }

    // -------
    // IFilter -----------------------------------------------------------
    // -------

/**
     * Adds this Filter to a given parent XML DOM Element. The Filter element will be set as a child element of the
     * passed element.
     * 
     * @param element
     *            The parent element for this Filter
     * @see org.w3c.dom.Element
     */
    @Override
    public void addToXmlElement(Element element) {

        if (element == null) {
            return;
        }

        Document ownerDoc = element.getOwnerDocument();

        // Check if this element doesn't exist so far
        NodeList nodeList = element.getElementsByTagName(IFilter.XML_ELEMENT_FILTER);
        if (nodeList.getLength() > 0) {
            Node currentNode = null;
            NamedNodeMap attributes = null;
            for (int i = 0; i < nodeList.getLength(); i++) { // iterate over all found nodes
                currentNode = nodeList.item(i);
                attributes = currentNode.getAttributes();
                Node idAttribute = attributes.getNamedItem(ISchemaElement.XML_ELEMENT_ATTRIBUTE_ID);
                if ((idAttribute != null) // if ID attribute is set and equals this objects ID, return existing element
                        && (idAttribute.getNodeValue().trim().equals(this.getID().trim()))) {
                    return;
                }
            }
        }

        // Create the new filter element
        Element eFilter = ownerDoc.createElement(IFilter.XML_ELEMENT_FILTER);
        eFilter.setAttribute(ISchemaElement.XML_ELEMENT_ATTRIBUTE_ID, this.getID());

        element.appendChild(eFilter);

        // ----- Set Comments (do this at the very beginning to possibly increase speed
        // during read)
        if (!this.isAvailable()) {
            Comment comment = ownerDoc.createComment(IEquipment.XML_COMMENT_ELEMENT_NOLONGERAVAILABLE);
            eFilter.appendChild(comment);
        }

        Element eModel = ownerDoc.createElement(IFilter.XML_ELEMENT_MODEL);
        Node nModelText = ownerDoc.createCDATASection(this.model);
        eModel.appendChild(nModelText);
        eFilter.appendChild(eModel);

        if (vendor != null) {
            Element eVendor = ownerDoc.createElement(IFilter.XML_ELEMENT_VENDOR);
            Node nVendorText = ownerDoc.createCDATASection(this.vendor);
            eVendor.appendChild(nVendorText);
            eFilter.appendChild(eVendor);
        }

        Element e_Type = ownerDoc.createElement(IFilter.XML_ELEMENT_TYPE);
        Node n_TypeText = ownerDoc.createCDATASection(this.type);
        e_Type.appendChild(n_TypeText);
        eFilter.appendChild(e_Type);

        if (color != null) {
            Element e_Color = ownerDoc.createElement(IFilter.XML_ELEMENT_COLOR);
            Node n_ColorText = ownerDoc.createCDATASection(this.color);
            e_Color.appendChild(n_ColorText);
            eFilter.appendChild(e_Color);
        }

        if (wratten != null) {
            Element e_Wratten = ownerDoc.createElement(IFilter.XML_ELEMENT_WRATTEN);
            Node n_WrattenText = ownerDoc.createCDATASection(this.wratten);
            e_Wratten.appendChild(n_WrattenText);
            eFilter.appendChild(e_Wratten);
        }

        if (schott != null) {
            Element e_Schott = ownerDoc.createElement(IFilter.XML_ELEMENT_SCHOTT);
            Node n_SchottText = ownerDoc.createCDATASection(this.schott);
            e_Schott.appendChild(n_SchottText);
            eFilter.appendChild(e_Schott);
        }

    }

/**
     * Adds the filter link to an given XML DOM Element The filter element itself will be attached to given elements
     * ownerDocument if the passed boolean was <code>true</code>. If the ownerDocument has no filter container, it will
     * be created (in case the passed boolean was <code>true</code>).<br>
     * Example:<br>
     * &lt;parameterElement&gt;<br>
     * <b>&lt;filter&gt;123&lt;/filter&gt;</b><br>
     * &lt;/parameterElement&gt;<br>
     * <i>More stuff of the xml document goes here</i><br>
     * <b>&lt;filterContainer&gt;</b><br>
     * <b>&lt;filter id="123"&gt;</b><br>
     * <i>filter description goes here</i><br>
     * <b>&lt;/filter&gt;</b><br>
     * <b>&lt;/filterContainer&gt;</b><br>
     * <br>
     * 
     * @param element
     *            The element under which the the filter link is created
     * @param addElementToContainer
     *            if <code>true</code> it's ensured that the linked element exists in the corresponding container
     *            element. Please note, passing <code>true</code> slowes down XML serialization.
     * @return Returns the Element given as parameter with a additional filter link, and the filter element under the
     *         filter container of the ownerDocument Might return <code>null</code> if element was <code>null</code>.
     * @see org.w3c.dom.Element
     * @since 2.0
     */
    @Override
    public Element addAsLinkToXmlElement(Element element, boolean addElementToContainer) {

        if (element == null) {
            return null;
        }

        Document ownerDoc = element.getOwnerDocument();

        // Create the link element
        Element e_Link = ownerDoc.createElement(IFilter.XML_ELEMENT_FILTER);
        Node n_LinkText = ownerDoc.createTextNode(this.getID());
        e_Link.appendChild(n_LinkText);

        element.appendChild(e_Link);

        if (addElementToContainer) {
            // Get or create the container element
            Element e_Filters = null;
            NodeList nodeList = ownerDoc.getElementsByTagName(RootElement.XML_FILTER_CONTAINER);
            if (nodeList.getLength() == 0) { // we're the first element. Create container element
                e_Filters = ownerDoc.createElement(RootElement.XML_FILTER_CONTAINER);
                ownerDoc.getDocumentElement().appendChild(e_Filters);
            } else {
                e_Filters = (Element) nodeList.item(0); // there should be only one container element
            }

            this.addToXmlElement(e_Filters);
        }

        return element;

    }

/**
     * Adds the filter link to an given XML DOM Element The filter element itself will <b>NOT</b> be attached to given
     * elements ownerDocument. Calling this method is equal to calling <code>addAsLinkToXmlElement</code> with
     * parameters <code>element, false</code><br>
     * Example:<br>
     * &lt;parameterElement&gt;<br>
     * <b>&lt;filterLink&gt;123&lt;/filterLink&gt;</b><br>
     * &lt;/parameterElement&gt;<br>
     * <br>
     * 
     * @param element
     *            The element under which the the filter link is created
     * @return Returns the Element given as parameter with a additional filter link Might return <code>null</code> if
     *         element was <code>null</code>.
     * @see org.w3c.dom.Element
     */
    @Override
    public Element addAsLinkToXmlElement(Element element) {

        return this.addAsLinkToXmlElement(element, false);

    }

/**
     * Returns the color of this filter.<br>
     * 
     * @return Returns a String representing the filters color, or <code>null</code> if the color was never set (e.g.
     *         filter type is not IFilter.FILTER_TYPE_COLOR).<br>
     */
    @Override
    public String getColor() {

        return this.color;

    }

/**
     * Sets the color of this filter.<br>
     * In case the current filter type is not IFilter.FILTER_TYPE_COLOR a IllegalArgumentException is thrown, so make
     * sure to set Filter type to color first.<br>
     * Valid color values can be retrieved from IFilter constants.<br>
     * 
     * @param color
     *            The new color of the filter.
     * @throws IllegalArgumentException
     *             if filter type is not IFilter.FILTER_TYPE_COLOR, or the given color is empty <code>null</code> or
     *             does not contain a valid value.
     * @see IFilter
     */
    @Override
    public void setColor(String color) {

        if ((color == null) || ("".equals(color.trim()))) {
            return;
        }

        if (!this.getType().equals(IFilter.FILTER_TYPE_COLOR)) {
            throw new IllegalArgumentException(
                    "Filter type is not color.\n" + "setColor(...) can only be called on color filters");
        }

        if (IFilter.FILTER_COLOR_BLUE.equals(color) || IFilter.FILTER_COLOR_DEEPBLUE.equals(color)
                || IFilter.FILTER_COLOR_DEEPRED.equals(color) || IFilter.FILTER_COLOR_DEEPYELLOW.equals(color)
                || IFilter.FILTER_COLOR_GREEN.equals(color) || IFilter.FILTER_COLOR_LIGHTGREEN.equals(color)
                || IFilter.FILTER_COLOR_LIGHTRED.equals(color) || IFilter.FILTER_COLOR_LIGHTYELLOW.equals(color)
                || IFilter.FILTER_COLOR_MEDIUMBLUE.equals(color) || IFilter.FILTER_COLOR_PALEBLUE.equals(color)
                || IFilter.FILTER_COLOR_ORANGE.equals(color) || IFilter.FILTER_COLOR_RED.equals(color)
                || IFilter.FILTER_COLOR_VIOLET.equals(color) || IFilter.FILTER_COLOR_YELLOW.equals(color)
                || IFilter.FILTER_COLOR_YELLOWGREEN.equals(color)) {
            this.color = color;
        } else {
            throw new IllegalArgumentException("Given color is unknown.\n");
        }

    }

/**
     * Returns the model of this filter.<br>
     * 
     * @return Returns a String representing the filter model.<br>
     */
    @Override
    public String getModel() {

        return this.model;

    }

/**
     * Sets the model name for the filter.<br>
     * 
     * @param model
     *            The new model name to be set.
     * @throws IllegalArgumentException
     *             if modelname was <code>null</code>
     */
    @Override
    public void setModel(String model) {

        if ((model == null) || ("".equals(model.trim()))) {
            throw new IllegalArgumentException("Filter model cannot be null or empty string.\n");
        }

        this.model = model;

    }

/**
     * Returns the schott value of this filter.<br>
     * 
     * @return Returns a String representing the schott value of the filter, or <code>null</code> if the value was never
     *         set.<br>
     */
    @Override
    public String getSchott() {

        return this.schott;

    }

/**
     * Sets the schott value for the filter.<br>
     * 
     * @param schott
     *            The new schott value to be set.
     */
    @Override
    public void setSchott(String schott) {

        if ((schott != null) && ("".equals(schott.trim()))) {
            this.schott = null;
            return;
        }

        this.schott = schott;

    }

/**
     * Returns the filter type.<br>
     * 
     * @return Returns a String representing the filter type.<br>
     */
    @Override
    public String getType() {

        return this.type;

    }

/**
     * Sets the filter type.<br>
     * The filter type must be a value from the types defined in IFilter.<br>
     * If the old filter type was IFilter.FILTER_TYPE_COLOR and the new filter type is not, then the filters color is
     * reset to <code>null</code>.
     * 
     * @param type
     *            The new filter type to be set.
     * @throws IllegalArgumentException
     *             if type was empty, <code>null</code> or does not contain a valid value (see IFilter constants).
     * @see IFilter
     */
    @Override
    public void setType(String type) {

        if ((type == null) || ("".equals(type.trim()))) {
            throw new IllegalArgumentException("Type cannot be null or empty string.\n");
        }

        if (IFilter.FILTER_TYPE_BROADBAND.equals(type) || IFilter.FILTER_TYPE_COLOR.equals(type)
                || IFilter.FILTER_TYPE_CORRECTIVE.equals(type) || IFilter.FILTER_TYPE_HALPHA.equals(type)
                || IFilter.FILTER_TYPE_HBETA.equals(type) || IFilter.FILTER_TYPE_NARROWBAND.equals(type)
                || IFilter.FILTER_TYPE_SOLAR.equals(type) || IFilter.FILTER_TYPE_NEUTRAL.equals(type)
                || IFilter.FILTER_TYPE_OIII.equals(type) || IFilter.FILTER_TYPE_OTHER.equals(type)) {
            // In case that old value was color filter (and new value is not color filter)
            // clear color value
            if (IFilter.FILTER_TYPE_COLOR.equals(this.type) && !IFilter.FILTER_TYPE_COLOR.equals(type)) {
                this.color = null;
            }

            this.type = type;

        } else {
            throw new IllegalArgumentException("Given filter type is unknown.\n");
        }

    }

/**
     * Returns the wratten value of this filter.<br>
     * 
     * @return Returns a String representing the wratten value of the filter, or <code>null</code> if the value was
     *         never set.<br>
     */
    @Override
    public String getWratten() {

        return this.wratten;

    }

/**
     * Sets the wratten value for the filter.<br>
     *
     */
    @Override
    public void setWratten(String wratten) {

        if ((wratten != null) && ("".equals(wratten.trim()))) {
            this.wratten = null;
            return;
        }

        this.wratten = wratten;

    }

/**
     * Returns the vendor name of this filter.<br>
     * 
     * @return Returns a String representing the vendor name of the filter, or <code>null</code> if the vendor was never
     *         set.<br>
     */
    @Override
    public String getVendor() {

        return this.vendor;

    }

/**
     * Sets the vendor name of the filter.<br>
     * 
     * @param vendor
     *            The new vendor name to be set.
     */
    @Override
    public void setVendor(String vendor) {

        this.vendor = vendor;

    }

}
