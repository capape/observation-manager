/* ====================================================================
 * /IFinding.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om;

/**
 * A IFinding describes the impressions a observer had during a observation of an astronomical object.<br>
 * A IFinding is a very general description without observation or object typical parameters. Subclasses of IFinding
 * have to provide a specialised way to describe different astronmical observartions or obejects (e.g. variable Stars,
 * DeepSky, Planets...).
 * 
 * @author doergn@users.sourceforge.net
 * 
 * @since 1.0
 */
public interface IFinding extends ISchemaElement, IExtendableSchemaElement {

    // ---------
    // Constants ---------------------------------------------------------
    // ---------

    /**
     * Constant for XML representation: IFinding element name.<br>
     * Example:<br>
     * &lt;result&gt;<i>More stuff goes here</i>&lt;/result&gt;
     */
    String XML_ELEMENT_FINDING = "result";

    /**
     * Constant for XML representation: Description element name.<br>
     * Example:<br>
     * &lt;result&gt; <br>
     * <i>More stuff goes here</i> &lt;description&gt;<code>Finding description goes here</code>&lt;/description&gt;
     * <i>More stuff goes here</i> &lt;/result&gt;
     */
    String XML_ELEMENT_DESCRIPTION = "description";

    /**
     * Constant for XML representation: language attribute<br>
     * Since COMAST 1.5 it is possible to add a language description to a finding element. This language description
     * give the language in which all finding related entrys were made.<br>
     * The value is given as ISO String. (E.g. de=German, fr=Frensh, ...)<br>
     * Example:<br>
     * &lt;session id=&quot;someID&quot; lang=&quot;someISOString&quot;&gt;<br>
     * <br>
     * <i>More stuff goes here</i> &lt;/session&gt;
     * 
     * @since 1.5
     */
    String XML_ELEMENT_ATTRIBUTE_LANGUAGE = "lang";

    // --------------
    // Public Methods ---------------------------------------------------------
    // --------------

    /**
     * Adds the IFinding implementation to an given parent XML DOM Element. The finding Element will be set as a child
     * element of the passed Element.<br>
     * Example:<br>
     * &lt;parentElement&gt;<br>
     * &lt;result&gt;<br>
     * <i>More stuff goes here</i><br>
     * &lt;/result&gt;<br>
     * &lt;/parentElement&gt;
     * 
     * @param parent
     *            The parent element for the IFinding implementation
     * 
     * @return Returns the Element given as parameter with the IFinding implementation as child Element.
     * 
     * @see org.w3c.dom.Element
     */
    org.w3c.dom.Element addToXmlElement(org.w3c.dom.Element parent);

    /**
     * Returns the description of the IFinding. The string describes the impressions the observer had during the
     * observation of an object.
     * 
     * @return The description of the finding.
     */
    String getDescription();

    /**
     * Sets the description of the IFinding. The string should describe the impressions the observer had during the
     * observation of an object.
     * 
     * @param description
     *            A description of the finding.
     */
    void setDescription(String description);

    /**
     * Returns the language in which this finding is described as ISO language string. E.g. de=German, fr=French,
     * ...<br>
     * Might return <code>null</code> if no language was set for this finding.
     * 
     * @return Returns a ISO language code that represents the finding describtion language or <code>null</code> if no
     *         language was set at all.
     * 
     * @since 1.5
     */
    String getLanguage();

    /**
     * Sets the language in which this finding is described. String must be given as ISO language string. E.g.
     * de=German, fr=French, ...<br>
     * 
     * @param language
     *            ISO language string
     * 
     * @since 1.5
     */
    void setLanguage(String language);

    /**
     * Returns <code>true</code> if the target was seen with this finding or not. As findings might be created to
     * document that an object was not seen this flag can be used for checks.
     * 
     * @return Returns <code>true</code> if the target was seen with this finding
     * 
     * @since 1.6
     */
    boolean wasSeen();

    /**
     * Set to <code>true</code> if the target was seen by the observer.<br>
     * 
     * @param seen
     *            <code>true</code> if the target was seen by the observer or <code>false</code> if the target was not
     *            seen
     * 
     * @since 1.6
     */
    void setSeen(boolean seen);

}
