/* ====================================================================
 * /ITarget.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */


package de.lehmannet.om;

import org.w3c.dom.Element;


/**
 * An ITarget describes a celestial object which might be interessting
 * for astronomical observation.<br>
 * An ITraget is a very general description of an astronomical object,
 * representing just the objects name, the celestial position and some
 * optional alias names.
 * The optional names might be used for colloquial names of a 
 * astronomical object.<br>
 * E.g. the Messier catalogue object M51 is also known as "Whirlpool Galaxy".
 * 
 * @author doergn@users.sourceforge.net
 * @since 1.0
 */
public interface ITarget extends ISchemaElement, IExtendableSchemaElement {

	// ---------
	// Constants ---------------------------------------------------------
	// ---------

	/**
     * Constant for XML representation: ITarget element name.<br>
     * Example:<br>
     * &lt;target&gt;<i>More stuff goes here</i>&lt;/target&gt;
     */
	public static final String XML_ELEMENT_TARGET = "target";


	/**
	 * Constant for XML Schema Instance type.<br>
	 * As target elements my differ from type to type (i.e. DeepSkyTarget,
	 * VariableStarTarget...) this constant can identifies a type.<br>
	 * Example:<br>
	 * &lt;target xsi:type="oal:deepSkyGX"&gt;<i>More stuff goes here</i>&lt;/target&gt;
	 */
	public static final String XML_XSI_TYPE = "xsi:type";


	/**
	 * Constant for XML representation: Targets name element name.<br>
	 * Example:<br>
	 * &lt;target&gt;
	 * <br><i>More stuff goes here</i>
	 * &lt;name&gt;<code>Target name goes here</code>&lt;/name&gt;
	 * <i>More stuff goes here</i>
	 * &lt;/target&gt;
	 */
	public static final String XML_ELEMENT_NAME = "name";

	/**
	 * Constant for XML representation: Targets alias name element name.<br>
	 * Example:<br>
	 * &lt;target&gt;
	 * <br><i>More stuff goes here</i>
	 * &lt;name&gt;<code>Target name goes here</code>&lt;/name&gt;
	 * &lt;alias&gt;<code>Target alias name goes here</code>&lt;/alias&gt;
	 * <i>More stuff goes here</i>
	 * &lt;/target&gt;
	 */
	public static final String XML_ELEMENT_ALIASNAME = "alias";


    /**
     * Constant for XML representation: Celestial constellation where the target can be found.<br>
     * Example:<br>
     * &lt;target&gt;
     * <br><i>More stuff goes here</i>
     * &lt;constellation&gt;<code>Constellationname goes here</code>&lt;/constellation&gt;
     * <i>More stuff goes here</i>
     * &lt;/target&gt;
     */
    public static final String XML_ELEMENT_CONSTELLATION = "constellation";


    /**
     * Constant for XML representation: Datasource that is the origin of this target.<br>
     * Whether the datasource is given, or a observer is referenced. 
     * Example:<br>
     * &lt;target&gt;
     * <br><i>More stuff goes here</i>
     * &lt;datasource&gt;<code>Datasource goes here</code>&lt;/datasource&gt;
     * <i>More stuff goes here</i>
     * &lt;/target&gt;
     */
    public static final String XML_ELEMENT_DATASOURCE = "datasource";


    /**
     * Constant for XML representation: Additional notes to the target.<br>
     * Example:<br>
     * &lt;target&gt;
     * <br><i>More stuff goes here</i>
     * &lt;notes&gt;<code>Notes go here</code>&lt;/notes&gt;
     * <i>More stuff goes here</i>
     * &lt;/target&gt;
     */
    public static final String XML_ELEMENT_NOTES = "notes";
    
    


	// --------------
	// Public Methods ----------------------------------------------------
	// --------------    

	// -------------------------------------------------------------------
	/**
	 * Returns true if the given object is an instance of ITarget<br>
	 * and the given objects SchmemaElement.getID() returns the same
	 * string as this objects getID() method.
	 * 
	 * @return Returns <b>true</b> if this Targets ID is equal to the
	 * given objects ID
	 */ 		    	    
	public boolean equalsID(Object o);    
    
	
    // -------------------------------------------------------------------
	/**
	 * Adds this Target to a given parent XML DOM Element.
	 * The Target element will be set as a child element of
	 * the passed element.
	 * 
	 * @param parent The parent element for this Target
	 * @return Returns the element given as parameter with this 
	 * Target as child element.<br>
     * Might return <code>null</code> if parent was <code>null</code>.
	 * @see org.w3c.dom.Element
	 */    	
	public Element addToXmlElement(Element element);
	
    
    // -------------------------------------------------------------------
    /**
     * Adds the target link to an given XML DOM Element
     * The target element itself will be attached to given elements 
     * ownerDocument if the passed boolean was <code>true</code>. If the
     * ownerDocument has no target container, it will
     * be created (in case the passed boolean was <code>true</code>).<br>
     * Example:<br>
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
     * @param parent The element under which the the target link is created
     * @param xmlElementName The name of the element that contains the link
     * @param addElementToContainer if <code>true</code> it's ensured that the linked
     * element exists in the corresponding container element. Please note, passing
     * <code>true</code> slowes down XML serialization.  
     * @return Returns the Element given as parameter with a additional  
     * target link, and the target element under the target container
     * of the ownerDocument
     * Might return <code>null</code> if element was <code>null</code>.
     * @see org.w3c.dom.Element
     * @since 2.0
     */   
    public Element addAsLinkToXmlElement(Element element, String xmlElementName, boolean addElementToContainer);
    
    
    // -------------------------------------------------------------------
    /**
     * Adds the target link to an given XML DOM Element
     * The target element itself will <b>NOT</b> be attached to given elements 
     * ownerDocument. Calling this method is equal to calling
     * <code>addAsLinkToXmlElement</code> with parameters <code>element, false</code><br>
     * Example:<br>
     * &lt;parameterElement&gt;<br>
     * <b>&lt;targetLink&gt;123&lt;/targetLink&gt;</b><br>
     * &lt;/parameterElement&gt;<br>   
     * <br>
     * 
     * @param element The element under which the the target link is created
     * @param xmlElementName The name of the element that contains the link
     * @return Returns the Element given as parameter with a additional  
     * target link
     * Might return <code>null</code> if element was <code>null</code>.
     * @see org.w3c.dom.Element
     */ 
    public Element addAsLinkToXmlElement(Element element, String xmlElementName);  

    
	// -------------------------------------------------------------------    
	/**
	 * Adds a new alias name to the target.<br>
	 * The alias name can be any alternative name of
	 * the astronomical object.<br>
	 * Example:<br>
	 * Object name = M51<br>
	 * Alias name = Whirlpool Galaxy<br>
	 * 
	 * @param newAliasName A alias name of the astronomical object
	 * @return Returns <code>true</code> if the alias name 
	 * could be added to the target.
	 */ 	
    public boolean addAliasName(String newAliasName);
    
    
	// -------------------------------------------------------------------    
	/**
	 * Sets an array of new alias names to this target.<br>
	 * All current aliasNames will be deleted! If you want to add alias names
	 * without deleting the existing ones, please use Target.addAliasNames(String) or
	 * Target.addAliasName(String).<br>
	 * If <code>null</code> is passed, the given alias names are deleted.
	 * 
	 * @param newAliasNames An arry with new alias name 
	 */
	public void setAliasNames(String[] newAliasNames);
			
    
	// -------------------------------------------------------------------    
	/**
	 * Removes a alias name from the target.<br>
	 * 
	 * @param aliasName The alias name that should be removed
	 * @return Returns <code>true</code> if the alias name 
	 * could be removed from the target. If <code>false</code>
	 * is returned the given alias name could not be found
	 * in the targets alias name list.
	 */ 	
	public boolean removeAliasName(String aliasName);    
    
    
    // -------------------------------------------------------------------
    /**
     * Returns the celestial constellation, where the target can be found.<br>
     * Might return <code>NULL</code> if constellation was never set
     * 
     * @return The celestial constellation
     */     
    public Constellation getConstellation();

    
    // -------------------------------------------------------------------
    /**
     * Sets the celestial constellation, where the target can be found.<br>
     * 
     * @param constellation The celestial constellation of the target
     */   
    public void setConstellation(Constellation constellation);
    

    // -------------------------------------------------------------------
    /**
     * Sets the celestial constellation, where the target can be found.<br>
     * 
     * @param constellation The celestial constellation of the target
     */   
    public void setConstellation(String constellation);
        
        
    // -------------------------------------------------------------------
    /**
     * Sets the datasource of the target.<br>
     * 
     * @param datasource The datasource of the astronomical object
     */ 
    public void setDatasource(String datasource);   
    
    
    // -------------------------------------------------------------------
    /**
     * Sets the observer who is the originator of the target.<br>
     * 
     * @param observer The observer who is the originator of this target
     */ 
    public void setObserver(IObserver observer);  
    
    
    // -------------------------------------------------------------------
    /**
     * Returns the observer who is the originator of the target.<br>
     * 
     * @return The observer who is the originator of this target.
     * Might return <code>null</code> if observer was never set. (In this
     * case a dataSource must exist)
     */  
    public IObserver getObserver();    
    
    
    // -------------------------------------------------------------------
    /**
     * Returns the datasource which is the origin of the target.<br>
     * 
     * @return The datasource which is the origin of this target
     * Might return <code>null</code> if datasource was never set. (In this
     * case a observer must exist)
     */  
    public String getDatasource(); 
    
    
	// -------------------------------------------------------------------
	/**
	 * Returns all alias names.<br>
	 * 
	 * @return Returns a String array with all alias
	 * names. If no alias names were set <code>null</code>
	 * is returned.
	 */ 		    
    public String[] getAliasNames();

        
	// -------------------------------------------------------------------
	/**
	 * Returns the name of the target.<br>
	 * The name should clearly identify the astronomical
	 * object. Use alias names for colloquial names of
	 * the object.
	 * 
	 * @return Returns the name of the astronomical 
	 * object
	 */ 		  
    public String getName();
  
            
	// -------------------------------------------------------------------
	/**
	 * Sets the name of the target.<br>
	 * The name should clearly identify the astronomical
	 * object. For alternative names of the object add a
	 * new alias name.<br>
	 * If a name is already set to the target, the old name
	 * will be overwritten with new new name.
	 * 
	 * @return The name of the target
	 */	    
    public void setName(String name);


	// -------------------------------------------------------------------
	/**
	 * Returns the position of the target.<br>
	 * The position of the target describes the location of
	 * the astronomical object in any popular celestial
	 * coordination system.
	 * 
	 * @return The celestial position of the astronomical
	 * object
	 */	    	
    public EquPosition getPosition();
    
    
	// -------------------------------------------------------------------
	/**
	 * Sets the position of the target.<br>
	 * The position of the target describes the location of
	 * the astronomical object in a popular celestial
	 * coordination system.
	 * 
	 * @param position The position of the astronomical object
	 * in a popular coordination system
	 */	
    public void setPosition(EquPosition position);

    
	// -------------------------------------------------------------------
	/**
	 * Returns additional notes of the target.<br>
	 * Notes can be used for additional descriptions of the target.
	 * 
	 * @return Notes on the target or <code>null</code> if no notes were set
	 */	    	
    public String getNotes();
    
    
	// -------------------------------------------------------------------
	/**
	 * Sets additional notes to the target.<br>
	 * Additional notes can be used to add any additional textual information
	 * to the target.
	 * 
	 * @param notes Additional notes
	 */	
    public void setNotes(String notes);    
    
}
