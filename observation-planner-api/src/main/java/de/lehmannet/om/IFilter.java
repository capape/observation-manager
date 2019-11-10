/* ====================================================================
 * /IFilter.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om;

import org.w3c.dom.Element;

/**
 * An IFilter describes a optical filter used during the observation.<br>
 * This includes all kind of filters like color filters, band filter, ... <br>
 * The model name and the type are mandatory fields which have to be set.
 * 
 * @author doergn@users.sourceforge.net
 * @since 1.5
 */
public interface IFilter extends ISchemaElement, IEquipment {

    // ---------
    // Constants ---------------------------------------------------------
    // ---------

    /**
     * Constant for XML representation: filter element name
     */
    public static final String XML_ELEMENT_FILTER = "filter";

    /**
     * Constant for XML representation: model element name
     * 
     * Example:<br>
     * &lt;filter&gt; <br>
     * <i>More stuff goes here</i>
     * &lt;model&gt;<code>Model name goes here</code>&lt;/model&gt; <i>More stuff
     * goes here</i> &lt;/filter&gt;
     */
    public static final String XML_ELEMENT_MODEL = "model";

    /**
     * Constant for XML representation: filter type element name
     * 
     * Example:<br>
     * &lt;filter&gt; <br>
     * <i>More stuff goes here</i>
     * &lt;type&gt;<code>Model name goes here</code>&lt;/type&gt; <i>More stuff goes
     * here</i> &lt;/filter&gt;
     */
    public static final String XML_ELEMENT_TYPE = "type";

    /**
     * Constant for XML representation: filter vendor element name
     * 
     * Example:<br>
     * &lt;filter&gt; <br>
     * <i>More stuff goes here</i>
     * &lt;vendor&gt;<code>Model vendor goes here</code>&lt;/vendor&gt; <i>More
     * stuff goes here</i> &lt;/filter&gt;
     */
    public static final String XML_ELEMENT_VENDOR = "vendor";

    /**
     * Constant for XML representation: color element name
     *
     * Example:<br>
     * &lt;filter&gt; <br>
     * <i>More stuff goes here</i>
     * &lt;color&gt;<code>Vendor name goes here</code>&lt;/color&gt; <i>More stuff
     * goes here</i> &lt;/filter&gt;
     */
    public static final String XML_ELEMENT_COLOR = "color";

    /**
     * Constant for XML representation: wratten element name
     *
     * Example:<br>
     * &lt;filter&gt; <br>
     * <i>More stuff goes here</i>
     * &lt;wratten&gt;<code>Focal length goes here</code>&lt;/wratten&gt; <i>More
     * stuff goes here</i> &lt;/filter&gt;
     */
    public static final String XML_ELEMENT_WRATTEN = "wratten";

    /**
     * Constant for XML representation: apparent field of view element name
     *
     * Example:<br>
     * &lt;filter&gt; <br>
     * <i>More stuff goes here</i>
     * &lt;schott&gt;<code>apparent field of view goes here</code>&lt;/schott&gt;
     * <i>More stuff goes here</i> &lt;/filter&gt;
     */
    public static final String XML_ELEMENT_SCHOTT = "schott";

    /**
     * Filter types
     */
    public static final String FILTER_TYPE_BROADBAND = "broad band";
    public static final String FILTER_TYPE_COLOR = "color";
    public static final String FILTER_TYPE_CORRECTIVE = "corrective";
    public static final String FILTER_TYPE_HALPHA = "H-alpha";
    public static final String FILTER_TYPE_HBETA = "H-beta";
    public static final String FILTER_TYPE_NARROWBAND = "narrow band";
    public static final String FILTER_TYPE_NEUTRAL = "neutral";
    public static final String FILTER_TYPE_OIII = "O-III";
    public static final String FILTER_TYPE_OTHER = "other";
    public static final String FILTER_TYPE_SOLAR = "solar";

    /**
     * Filter colors (only valid for filter type color)
     */
    public static final String FILTER_COLOR_BLUE = "blue";
    public static final String FILTER_COLOR_DEEPBLUE = "deep blue";
    public static final String FILTER_COLOR_DEEPRED = "deep red";
    public static final String FILTER_COLOR_DEEPYELLOW = "deep yellow";
    public static final String FILTER_COLOR_GREEN = "green";
    public static final String FILTER_COLOR_LIGHTGREEN = "light green";
    public static final String FILTER_COLOR_LIGHTRED = "light red";
    public static final String FILTER_COLOR_LIGHTYELLOW = "light yellow";
    public static final String FILTER_COLOR_MEDIUMBLUE = "medium blue";
    public static final String FILTER_COLOR_ORANGE = "orange";
    public static final String FILTER_COLOR_PALEBLUE = "pale blue";
    public static final String FILTER_COLOR_RED = "red";
    public static final String FILTER_COLOR_VIOLET = "violet";
    public static final String FILTER_COLOR_YELLOW = "yellow";
    public static final String FILTER_COLOR_YELLOWGREEN = "yellow-green";

    // --------------
    // Public methods ----------------------------------------------------
    // --------------

    // -------------------------------------------------------------------
    /**
     * Adds this Filter to a given parent XML DOM Element. The Filter element will
     * be set as a child element of the passed element.
     * 
     * @param parent The parent element for this Filter
     * @return Returns the element given as parameter with this Filter as child
     *         element.<br>
     *         Might return <code>null</code> if parent was <code>null</code>.
     * @see org.w3c.dom.Element
     */
    public Element addToXmlElement(Element element);

    // -------------------------------------------------------------------
    /**
     * Adds the filter link to an given XML DOM Element The filter element itself
     * will be attached to given elements ownerDocument if the passed boolean was
     * <code>true</code>. If the ownerDocument has no filter container, it will be
     * created (in case the passed boolean was <code>true</code>).<br>
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
     * @param element               The element under which the the filter link is
     *                              created
     * @param addElementToContainer if <code>true</code> it's ensured that the
     *                              linked element exists in the corresponding
     *                              container element. Please note, passing
     *                              <code>true</code> slowes down XML serialization.
     * @return Returns the Element given as parameter with a additional filter link,
     *         and the filter element under the filter container of the
     *         ownerDocument Might return <code>null</code> if element was
     *         <code>null</code>.
     * @see org.w3c.dom.Element
     * @since 2.0
     */
    public Element addAsLinkToXmlElement(Element element, boolean addElementToContainer);

    // -------------------------------------------------------------------
    /**
     * Adds the filter link to an given XML DOM Element The filter element itself
     * will <b>NOT</b> be attached to given elements ownerDocument. Calling this
     * method is equal to calling <code>addAsLinkToXmlElement</code> with parameters
     * <code>element, false</code><br>
     * Example:<br>
     * &lt;parameterElement&gt;<br>
     * <b>&lt;filterLink&gt;123&lt;/filterLink&gt;</b><br>
     * &lt;/parameterElement&gt;<br>
     * <br>
     * 
     * @param element The element under which the the filter link is created
     * @return Returns the Element given as parameter with a additional filter link
     *         Might return <code>null</code> if element was <code>null</code>.
     * @see org.w3c.dom.Element
     */
    public Element addAsLinkToXmlElement(Element element);

    // -------------------------------------------------------------------
    /**
     * Returns the color of this filter.<br>
     * 
     * @return Returns a String representing the filters color, or <code>null</code>
     *         if the color was never set (e.g. filter type is not
     *         IFilter.FILTER_TYPE_COLOR).<br>
     */
    public String getColor();

    // -------------------------------------------------------------------
    /**
     * Sets the color of this filter.<br>
     * In case the current filter type is not IFilter.FILTER_TYPE_COLOR a
     * IllegalArgumentException is thrown, so make sure to set Filter type to color
     * first.<br>
     * Valid color values can be retrieved from IFilter constants.<br>
     * 
     * @param color The new color of the filter.
     * @throws IllegalArgumentException if filter type is not
     *                                  IFilter.FILTER_TYPE_COLOR, or the given
     *                                  color is empty <code>null</code> or does not
     *                                  contain a valid value.
     * @see IFilter
     */
    public void setColor(String color);

    // -------------------------------------------------------------------
    /**
     * Returns the model of this filter.<br>
     * 
     * @return Returns a String representing the filter model.<br>
     */
    public String getModel();

    // -------------------------------------------------------------------
    /**
     * Sets the model name for the filter.<br>
     * 
     * @param model The new model name to be set.
     * @throws IllegalArgumentException if modelname was <code>null</code>
     */
    public void setModel(String model);

    // -------------------------------------------------------------------
    /**
     * Returns the schott value of this filter.<br>
     * 
     * @return Returns a String representing the schott value of the filter, or
     *         <code>null</code> if the value was never set.<br>
     */
    public String getSchott();

    // -------------------------------------------------------------------
    /**
     * Sets the schott value for the filter.<br>
     * 
     * @param schott The new schott value to be set.
     */
    public void setSchott(String schott);

    // -------------------------------------------------------------------
    /**
     * Returns the filter type.<br>
     * 
     * @return Returns a String representing the filter type.<br>
     */
    public String getType();

    // -------------------------------------------------------------------
    /**
     * Sets the filter type.<br>
     * The filter type must be a value from the types defined in IFilter.<br>
     * If the old filter type was IFilter.FILTER_TYPE_COLOR and the new filter type
     * is not, then the filters color is reset to <code>null</code>.
     * 
     * @param type The new filter type to be set.
     * @throws IllegalArgumentException if type was empty, <code>null</code> or does
     *                                  not contain a valid value (see IFilter
     *                                  constants).
     * @see IFilter
     */
    public void setType(String type);

    // -------------------------------------------------------------------
    /**
     * Returns the wratten value of this filter.<br>
     * 
     * @return Returns a String representing the wratten value of the filter, or
     *         <code>null</code> if the value was never set.<br>
     */
    public String getWratten();

    // -------------------------------------------------------------------
    /**
     * Sets the wratten value for the filter.<br>
     * 
     * @param schott The new wratten value to be set.
     */
    public void setWratten(String wratten);

    // -------------------------------------------------------------------
    /**
     * Returns the vendor name of this filter.<br>
     * 
     * @return Returns a String representing the vendor name of the filter, or
     *         <code>null</code> if the vendor was never set.<br>
     */
    public String getVendor();

    // -------------------------------------------------------------------
    /**
     * Sets the vendor name of the filter.<br>
     * 
     * @param vendor The new vendor name to be set.
     */
    public void setVendor(String vendor);

}
