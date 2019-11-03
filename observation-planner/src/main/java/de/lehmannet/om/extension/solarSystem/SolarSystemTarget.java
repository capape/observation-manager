/* ====================================================================
 * extension/solarSystem/SolarSystemTarget.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */


package de.lehmannet.om.extension.solarSystem;

import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.lehmannet.om.IObserver;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.RootElement;
import de.lehmannet.om.SchemaElement;
import de.lehmannet.om.Target;
import de.lehmannet.om.util.SchemaException;

/**
 * SolarSystemTarget extends the de.lehmannet.om.Target class.
 * Its specialised for solar system targets. 
 * A SolarSystemTarget target can be any astronomical object inside our
 * solar system.<br>
 * This class exists more due to extension reasons as this class does not
 * add new functions to de.lehmannet.om.Target 
 * 
 * @author doergn@users.sourceforge.net
 * @since 1.3
 */
public abstract class SolarSystemTarget extends Target {

    // ---------
    // Constants --------------------------------------------------------------
    // ---------
	
	// Key IDs for major solar system bodies
	public static final String KEY_SUN = "SUN";
	public static final String KEY_MERCURY = "MERCURY";
	public static final String KEY_VENUS = "VENUS";
	public static final String KEY_EARTH = "EARTH";
	public static final String KEY_MOON = "MOON";
	public static final String KEY_MARS = "MARS";
	public static final String KEY_JUPITER = "JUPITER";
	public static final String KEY_SATURN = "SATURN";
	public static final String KEY_URANUS = "URANUS";
	public static final String KEY_NEPTUNE = "NEPTUNE";	
	
	
    // ---------
    // Variables --------------------------------------------------------------
    // ---------
	
	// Locale dependent Name
	private String i18nName = null; 
	
	// Last known locale 
	private Locale lastKnown = Locale.getDefault();

	
	
	
    // ------------
    // Constructors ------------------------------------------------------
    // ------------

    // ------------------------------------------------------------------- 
    /**
     * Constructs a new instance of a SolarSystemTarget from a given DOM 
     * target Element.<br>
     * Normally this constructor is called by a subclass which itself is
     * called by de.lehmannet.om.util.SchemaLoader.
     * Please mind that Target has to have a <observer> element, or a <datasource>
     * element. If a <observer> element is set, a array with Observers
     * must be passed to check, whether the <observer> link is valid.
     *  
     * @param observers Array of IObserver that might be linked from this observation,
     * can be <code>NULL</code> if datasource element is set
     * @param targetElement The origin XML DOM <target> Element 
     * @throws SchemaException if given targetElement was <code>null</code>
     */ 
    protected SolarSystemTarget(Node targetElement,
                                IObserver[] observers) throws SchemaException {
    
    	super(targetElement, observers);
    	
    	this.setI18NName();		
    	
    }
    
    
    // -------------------------------------------------------------------
    /**
     * Constructs a new instance of a SolarSystemTarget.
     * 
	 * @param name The name of the astronomical object
     * @param datasource The datasource of the astronomical object
	 */    
    protected SolarSystemTarget(String name,
                                String datasource) {
    
    	super(name, datasource);
    	this.setI18NName();
    
    }


    // -------------------------------------------------------------------
    /**
     * Constructs a new instance of a SolarSystemTarget.
     * 
     * @param name The name of the astronomical object
     * @param observer The observer who is the originator of the target
     */    
    protected SolarSystemTarget(String name,
                                IObserver observer) {
    
        super(name, observer);
        this.setI18NName();
    
    }    
    
    
    // -------------------------------------------------------------------
    /**
     * Constructs a new instance of a SolarSystemTarget.
     * 
	 * @param name The name of the astronomical object
     * @param i18nName Localized name of the target 
     * @param datasource The datasource of the astronomical object
	 */    
  /*  protected SolarSystemTarget(String name,
    							String i18nName,
                                String datasource) {
    
    	super(name, datasource);
    	this.i18nName = i18nName;
    
    }*/


    // -------------------------------------------------------------------
    /**
     * Constructs a new instance of a SolarSystemTarget.
     * 
     * @param name The name of the astronomical object
     * @param i18nName Localized name of the target
     * @param observer The observer who is the originator of the target
     */    
 /*   protected SolarSystemTarget(String name,
    							String i18nName,
                                IObserver observer) {
    
        super(name, observer);
        this.i18nName = i18nName;
    
    } */  	
    
    
    
    
    // --------------
    // Public methods ----------------------------------------------------
    // --------------

    // -------------------------------------------------------------------
 /*   public String getI18NName() {
    	
    	return this.i18nName;
    	
    } */
    
    
    // Overwritte setName from Target to make sure the I18N name gets set as well
	public void setName(String name) throws IllegalArgumentException {               
		
		super.setName(name);		
		
		this.setI18NName();
        
	}    
    

    
    // ------
    // Target ------------------------------------------------------------
    // ------

    // -------------------------------------------------------------------
    public String getDisplayName() {
    	
    	// Check if language has changed
    	if( !Locale.getDefault().equals(this.lastKnown) ) {
    		this.setI18NName();		// Set new I18N name
    	}
    	
    	// String i18n = this.getI18NName();
    	if(   (this.i18nName == null)
    	   || ("".equals(this.i18nName))
		   ) {
    		return this.getName();
    	}
    	
    	return this.i18nName; 
    	
    }
    
    
    
    
	// ------
	// Object ------------------------------------------------------------
	// ------

	// -------------------------------------------------------------------
	/**
	 * Overwrittes toString() method from java.lang.Object.<br>
	 * Returns the field values of this SolarSystemTarget.

	 * @return This SolarSystemTarget field values
	 * @see java.lang.Object
	 */
	public String toString() {
        
		StringBuffer buffer = new StringBuffer();
		buffer.append("SolarSystemTarget: Name=");
		buffer.append(super.getName());
		
		if(    (super.getAliasNames() != null)
			&& (super.getAliasNames().length > 0)				
			) {
			buffer.append(" Alias names=");
			buffer.append(super.getAliasNames());						
		} 
		
		if( super.getPosition() != null ) {
			buffer.append(" Position=");
			buffer.append(super.getPosition());		
		} 
		
		if( super.getConstellation() != null ) {
			buffer.append(" Constellation=");
			buffer.append(super.getConstellation());				
		} 

		return buffer.toString();
        
	}

	
	// -------------------------------------------------------------------
	/**
	 * Overwrittes equals(Object) method from java.lang.Object.<br>
	 * Checks if this Target and the given Object are equal. The given 
	 * object is equal with this Target, if it derives from ITarget, 
	 * both XSI types are equal and its name is equal to this Target name.<br>
	 * @param obj The Object to compare this Target with.
	 * @return <code>true</code> if the given Object is an instance of ITarget,
	 * both XSI types are equal and its name is equal to this Target name.<br>
	 * (Name comparism is <b>not</b> casesensitive)
	 * @see java.lang.Object
	 */    
	// Behaves different then DeepSkyTarget and Target...we do not check in datasource/observer
	// as the SolarSystem catalog is in hardcoded internal catalog.
	public boolean equals(Object obj) {
        
		if(   obj == null
		   || !(obj instanceof ITarget)
		   ) {
			return false;
		}
                        
		ITarget target = (ITarget)obj; 						   
		
		// Check names
		
		String targetName = target.getName();
		if( targetName == null ) {
  		  	return false;			                       	
		}
		
		if(   (this.getName().toLowerCase().equals(targetName.toLowerCase()))
		   && (this.getXSIType()).equals(target.getXSIType())
		   ) {
			return true;
		} 
        
		return false;
                         
	}
    
	
	
	
    // ------------------------
    // IExtendableSchemaElement ------------------------------------------
    // ------------------------
    
    // -------------------------------------------------------------------
    /**
     * Returns the XML schema instance type of the implementation.<br>
     * Example:<br>
     * <target xsi:type="myOwnTarget"><br>
     * </target><br>
     * 
     * @return The xsi:type value of this implementation 
     */ 
    public abstract String getXSIType();
	
    
    
    
	// ------
	// Target ------------------------------------------------------------
	// ------

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
	public abstract Element addToXmlElement(Element element);
	
    
 
       
    // -----------------
    // Protected methods -------------------------------------------------
    // -----------------

    // -------------------------------------------------------------------
    /**
     * Creates a SolarSystemTarget under the target container. If no target 
     * container exists under the given elements ownerDocument, it will be
     * created.<br>
     * This method should be called by subclasses, so that they only have to
     * add their specific data to the element returned.
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
     * @param element The element under which the the target link is created
     * @param xsiType The XSI:Type identification of the child class
     * @return Returns a new created target Element that contains all data 
     * from a SolarSystemTarget. Please mind, NOT the passed element is given, but
     * a child element of the passed elements ownerDocument.
     * Might return <code>null</code> if element was <code>null</code>.
     * @see org.w3c.dom.Element
     */ 
    protected Element createXmlSolarSystemTargetElement(Element element, String xsiType) {  

        if( element == null ) {
            return null;
        }
        
        Document ownerDoc = element.getOwnerDocument();                                                                                          
                        
        // Get or create the container element        
        Element e_Targets = null;
        NodeList nodeList = ownerDoc.getElementsByTagName(RootElement.XML_TARGET_CONTAINER);
        if( nodeList.getLength() == 0 ) {  // we're the first element. Create container element
            e_Targets = ownerDoc.createElement(RootElement.XML_TARGET_CONTAINER);
            ownerDoc.getDocumentElement().appendChild(e_Targets);
        } else {
            e_Targets = (Element)nodeList.item(0);  // there should be only one container element
        }                       
        
        // Check if this element doesn't exist so far
        nodeList = e_Targets.getElementsByTagName(ITarget.XML_ELEMENT_TARGET);
        if( nodeList.getLength() > 0 ) {
            Node currentNode = null;
            NamedNodeMap attributes = null;
            for(int i=0; i < nodeList.getLength(); i++) {   // iterate over all found nodes
                currentNode = nodeList.item(i);
                attributes = currentNode.getAttributes();   
                Node idAttribute = attributes.getNamedItem(SchemaElement.XML_ELEMENT_ATTRIBUTE_ID);
                if(   (idAttribute != null)    // if ID attribute is set and equals this objects ID, return existing element
                   && (idAttribute.getNodeValue().trim().equals(super.getID().trim()))
                  ) {
	            		// Not sure if this is good!? Maybe we should return currentNode and make doublicity check in caller
	            	    // class!?                	
                        return null;
                }
            }
        }        
        
        // Create the new target element
        Element e_Target = super.createXmlTargetElement(e_Targets);                                      
        e_Targets.appendChild(e_Target);      


        // Set XSI:Type
        e_Target.setAttribute(ITarget.XML_XSI_TYPE, xsiType);
     
        return e_Target;
        
    }
    
    // -------------------------------------------------------------------
    private void setI18NName() {
    	
    	PropertyResourceBundle bundle = (PropertyResourceBundle)ResourceBundle.getBundle("de.lehmannet.om.extension.solarSystem.SolarSystem", Locale.getDefault());
		
		if( this.getName().equals(SolarSystemTarget.KEY_SUN) ) {
			this.i18nName = bundle.getString("catalog.sun");
		} else if( this.getName().equals(SolarSystemTarget.KEY_MERCURY) ) {
			this.i18nName = bundle.getString("catalog.mercury");
		} else if( this.getName().equals(SolarSystemTarget.KEY_VENUS) ) {
			this.i18nName = bundle.getString("catalog.venus");
		} else if( this.getName().equals(SolarSystemTarget.KEY_MOON) ) {
			this.i18nName = bundle.getString("catalog.moon");
		} else if( this.getName().equals(SolarSystemTarget.KEY_MARS) ) {
			this.i18nName = bundle.getString("catalog.mars");
		} else if( this.getName().equals(SolarSystemTarget.KEY_JUPITER) ) {
			this.i18nName = bundle.getString("catalog.jupiter");
		} else if( this.getName().equals(SolarSystemTarget.KEY_SATURN) ) {
			this.i18nName = bundle.getString("catalog.saturn");
		} else if( this.getName().equals(SolarSystemTarget.KEY_URANUS) ) {
			this.i18nName = bundle.getString("catalog.uranus");
		} else if( this.getName().equals(SolarSystemTarget.KEY_NEPTUNE) ) {
			this.i18nName = bundle.getString("catalog.neptune");
		} else {
			this.i18nName = super.getName();	
		}			
    	
    } 
    
}
