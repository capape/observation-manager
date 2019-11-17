/* ====================================================================
 * /ISchemaElement.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om;

/**
 * The ISchemaElement is the root interface for almost all astro XML schema elements. It contains only element
 * informations that are common for all (or almost all) elements.
 * 
 * @author doergn@users.sourceforge.net
 * @since 1.0
 */
public interface ISchemaElement {

    // ---------
    // Constants ---------------------------------------------------------
    // ---------

    /**
     * Constant for XML representation: ID attribute<br>
     * Almost all astro XML schema elements contain the ID attribute which is used for linking the different elements
     * logical together.<br>
     * Example:<br>
     * &lt;AnAstroXmlElement id=&quot;someID&quot;&gt;<br>
     * <i>More stuff goes here</i><br>
     * &lt;/AnAstroXmlElement&gt;<br>
     * <br>
     * &lt;AnotherAstroXmlElement id=&quot;someOtherID&quot;&gt; <br>
     * <i>More stuff goes here</i> <br>
     * &lt;AnAstroXmlElement&gt;someID;&lt;/AnAstroXmlElement&gt;<br>
     * <br>
     * <i>More stuff goes here</i> &lt;/AnotherAstroXmlElement&gt;
     */
    String XML_ELEMENT_ATTRIBUTE_ID = "id";

    // --------------
    // Public Methods ----------------------------------------------------
    // --------------

    // -------------------------------------------------------------------
    /**
     * Returns a unique ID of this schema element.<br>
     * The ID is used to link this element with other XML elements in the schema.
     * 
     * @return Returns a String representing a unique ID of this schema element.
     */
    String getID();

    // -------------------------------------------------------------------
    /**
     * Returns a display name for this element.<br>
     * The method differs from the toString() method as toString() shows more technical information about the element.
     * Also the formating of toString() can spread over several lines.<br>
     * This method returns a string (in one line) that can be used as displayname in e.g. a UI dropdown box.
     * 
     * @return Returns a String with a one line display name
     * @see java.lang.Object.toString();
     */
    String getDisplayName();

}
