/* ====================================================================
 * /Site.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */


package de.lehmannet.om;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.lehmannet.om.util.FloatUtil;
import de.lehmannet.om.util.SchemaException;


/**
 * An Site describes an observation site where an observation took place.<br>
 * A site can be identified by its latitude and longitude values, but
 * as for processing reasons this class has some more
 * mandatory fields, such as e.g. elevation and timezone.
 * 
 * @author doergn@users.sourceforge.net
 * @since 1.0
 */
public class Site extends SchemaElement implements ISite {

    // ------------------
    // Instance Variables ------------------------------------------------
    // ------------------

    // The sites name
    private String name = null;

	// The sites elevation 
	private float elevation = Float.NaN;

	// The sites IAU code 
	private String iauCode = null;
    
    // The sites longitude 
    private Angle longitude = null;
    
    // The sites latitude
    private Angle latitude = null;    

    // The sites timezone in minutes from GMT
    private int timezone = Integer.MAX_VALUE;
  
    
    
    
    // ------------
    // Constructors ------------------------------------------------------
    // ------------

    // -------------------------------------------------------------------
    /**
     * Constructs a new Site instance from a given XML Schema Node.
     * Normally this constructor is only called by 
     * de.lehmannet.om.util.SchemaLoader 
     * 
     * @param site The XML Schema Node that represents this Site object
     * @throws IllegalArgumentException if the parameter is <code>null</code>
     * @throws SchemaException if the given Node does not match the XML
     * Schema specifications 
     */       
    public Site(Node site) 
                throws SchemaException,
                       IllegalArgumentException {

        if( site == null ) {
            throw new IllegalArgumentException("Parameter site node cannot be NULL. ");
        }

        // Cast to element as we need some methods from it
        Element siteElement = (Element)site;                                                
          
        // Helper classes                                  
        Element child = null;
        NodeList children = null;       
            
        // Getting data
        // First mandatory stuff and down below optional data
            
        // Get ID from element
        String ID = siteElement.getAttribute(SchemaElement.XML_ELEMENT_ATTRIBUTE_ID);
        if(   (ID != null)
           && ("".equals(ID.trim()))
           ) {
                throw new SchemaException("Site must have a ID. ");
        }                   
        super.setID(ID);
                        
        // Get mandatory name
        children = siteElement.getElementsByTagName(ISite.XML_ELEMENT_NAME);
        if(   (children == null)
           || (children.getLength() != 1)              
        )  {
              throw new SchemaException("Site must have exact one name. ");
        }
        child = (Element)children.item(0);
        String name = "";
        if( child == null ) {
          throw new SchemaException("Site must have a name. ");
        } else {
            if( child.getFirstChild() != null ) {
                //name = child.getFirstChild().getNodeValue();
          	   	 NodeList textElements = child.getChildNodes();
            	 if(   (textElements != null)
            	    && (textElements.getLength() > 0) 
            	    ) {
             		for(int te=0; te < textElements.getLength(); te++) {
             			name = name + textElements.item(te).getNodeValue();
             		}
             		this.setName(name);    
               	 }               	
            } else {
                throw new SchemaException("Site cannot have an empty name. ");      
            }
                       
        }           
            
        // Get mandatory longitude
        child = null;
        children = siteElement.getElementsByTagName(ISite.XML_ELEMENT_LONGITUDE);
        if(   (children == null)
           || (children.getLength() != 1)              
        )  {
              throw new SchemaException("Site must have exact one longitude. ");
        }
        child = (Element)children.item(0);
        Angle longitude = null;
        if( child == null ) {
          throw new SchemaException("Site must have a longitude. ");
        } else {                
            String value = child.getFirstChild().getNodeValue();
            String unit = child.getAttribute(Angle.XML_ATTRIBUTE_UNIT);
            longitude = new Angle(Double.parseDouble(value), unit);
            this.setLongitude(longitude);             
        }               
            
        // Get mandatory latitude
        child = null;
        children = siteElement.getElementsByTagName(ISite.XML_ELEMENT_LATITUDE);
        if(   (children == null)
           || (children.getLength() != 1)              
        )  {
              throw new SchemaException("Site must have exact one latitude. ");
        }
        child = (Element)children.item(0);
        Angle latitude = null;
        if( child == null ) {
          throw new SchemaException("Site must have a latitude. ");
        } else {                
            String value = child.getFirstChild().getNodeValue();
            String unit = child.getAttribute(Angle.XML_ATTRIBUTE_UNIT);
            latitude = new Angle(Double.parseDouble(value), unit);
            this.setLatitude(latitude);              
        }                           
        
        // Get mandatory timezone
        child = null;
        children = siteElement.getElementsByTagName(ISite.XML_ELEMENT_TIMEZONE);
        if(   (children == null)
           || (children.getLength() != 1)              
        )  {
              throw new SchemaException("Site must have exact one timezone. ");
        }
        child = (Element)children.item(0);
        String timezone = null;
        if( child == null ) {
          throw new SchemaException("Site must have a timezone. ");
        } else {                
            timezone = child.getFirstChild().getNodeValue();
            this.setTimezone(Integer.parseInt(timezone));            
        }

        // Get optional elevation
        child = null;
        children = siteElement.getElementsByTagName(ISite.XML_ELEMENT_ELEVATION);
        String elevation = null;
        if( children != null ) {
          if( children.getLength() == 1 ) {                   
            child = (Element)children.item(0);
            if( child != null ) {
                elevation = child.getFirstChild().getNodeValue();
                this.setElevation(FloatUtil.parseFloat(elevation));                                      
            } else {
                throw new SchemaException("Problem while retrieving elevation from site. ");                                     
            }
          } else if( children.getLength() > 1 ) {
              throw new SchemaException("Site can have only one elevation level. ");                 
          }               
        }                   
                               
        // Get optional iauCode
        child = null;
        children = siteElement.getElementsByTagName(ISite.XML_ELEMENT_IAUCODE);
        String iauCode = null;
        if( children != null ) {
          if( children.getLength() == 1 ) {                   
            child = (Element)children.item(0);
            if( child != null ) {
              iauCode = child.getFirstChild().getNodeValue();
              this.setIAUCode(iauCode);                                     
            } else {
                throw new SchemaException("Problem while retrieving IAU code from site. ");                                     
            }
          } else if( children.getLength() > 1 ) {
              throw new SchemaException("Site can have only one IAU code. ");                 
          }               
        }                   
       
    }


	// -------------------------------------------------------------------
	 /**
	  * Constructs a new instance of a Site.  
	  * 
	  * @param name The sites name
	  * @param longitude The sites longitude
	  * @param latitude The sites latitude
	  * @param timezone The sites timezone offset in minutes from GMT
	  */       
    public Site(String name,
                Angle longitude,
                Angle latitude,
                int timezone) throws IllegalArgumentException {
        
        this.setName(name);
        this.setLongitude(longitude);
        this.setLatitude(latitude);
        this.setTimezone(timezone);        
        
    }

    
    
    
	// -------------
	// SchemaElement -----------------------------------------------------
	// -------------
    
    
	// -------------------------------------------------------------------
	/**
	 * Returns a display name for this element.<br>
	 * The method differs from the toString() method as toString() shows
	 * more technical information about the element. Also the formating of
	 * toString() can spread over several lines.<br>
	 * This method returns a string (in one line) that can be used as 
	 * displayname in e.g. a UI dropdown box.
	 * 
	 * @return Returns a String with a one line display name
	 * @see java.lang.Object.toString();
	 */ 		    	    
	public String getDisplayName() {
		
		return this.getName();
		
	}


	
	
	// ------
	// Object ------------------------------------------------------------
	// ------

	// -------------------------------------------------------------------
	/**
	 * Overwrittes toString() method from java.lang.Object.<br>
	 * Returns the field values of this Site.

	 * @return This DeepSkyTarget field values
	 * @see java.lang.Object
	 */
	public String toString() {
        
		StringBuffer buffer = new StringBuffer();
		buffer.append("Site: Name=");
		buffer.append(name);

		if( iauCode != null ) {
			buffer.append(" IAU Code=");
			buffer.append(iauCode);			
		}
		
		buffer.append(" Longitude=");
		buffer.append(longitude);		

		buffer.append(" Latitude=");
		buffer.append(latitude);

        if( !Float.isNaN(elevation) ) {
          buffer.append(" Elevation=");
          buffer.append(elevation);            
        }

		buffer.append(" Timezone=");
		buffer.append(timezone);
        
		return buffer.toString();
        
	}


	// -------------------------------------------------------------------
	/**
	 * Overwrittes equals(Object) method from java.lang.Object.<br>
	 * Checks if this Site and the given Object are equal. The given 
	 * object is equal with this Site, if it derives from ISite 
	 * and if its name, longitude, latitude and elevation equals this Sites 
	 * name, longitude, latitude and elevation.<br>
	 * @param obj The Object to compare this Site with.
	 * @return <code>true</code> if the given Object is an instance of ISite
	 * and its name, longitude, latitude and elevation is equal to this 
	 * Sites name, longitude, latitude and elevation.<br>
	 * (Name comparism is <b>not</b> casesensitive)
	 * @see java.lang.Object
	 */    
	/*public boolean equals(Object obj) {
        
		if(   obj == null
		   || !(obj instanceof ISite)
		   ) {
			return false;
		}
                        
		ISite site = (ISite)obj; 
		
		String siteName = site.getName();
		if(   (siteName == null) 
		   || !(name.toLowerCase().equals(siteName.toLowerCase()))
		   ) {
			return false;			                       	
		}                        

		if( !site.getLatitude().equals(latitude) ) {
			return false;
		}

		if( !site.getLongitude().equals(longitude) ) {
			return false;
		}		
        
		return true;
                         
	}*/




    // -----
    // ISite -------------------------------------------------------------
    // -----

    // -------------------------------------------------------------------
	/**
	 * Adds this Site to a given parent XML DOM Element.
	 * The Site element will be set as a child element of
	 * the passed element.
	 * 
	 * @param parent The parent element for this Site
	 * @return Returns the element given as parameter with this 
	 * Site as child element.<br>
     * Might return <code>null</code> if parent was <code>null</code>.
	 * @see org.w3c.dom.Element
	 */    	
	public Element addToXmlElement(Element element) {
		
    	if( element == null ) {
    		return null;
    	}
    
        Document ownerDoc = element.getOwnerDocument();		
		
        // Check if this element doesn't exist so far
        NodeList nodeList = element.getElementsByTagName(ISite.XML_ELEMENT_SITE);
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
                        return element;
                }
            }
        }                    
        
        // Create the new site element
        Element e_Site = ownerDoc.createElement(XML_ELEMENT_SITE);        
        e_Site.setAttribute(XML_ELEMENT_ATTRIBUTE_ID, super.getID());
        
        element.appendChild(e_Site);
        
        Element e_Name = ownerDoc.createElement(XML_ELEMENT_NAME);
        Node n_NameText = ownerDoc.createCDATASection(this.name);
        e_Name.appendChild(n_NameText);           
        e_Site.appendChild(e_Name);
        
        Element e_Longitude = ownerDoc.createElement(XML_ELEMENT_LONGITUDE);    
		e_Longitude = longitude.setToXmlElement(e_Longitude);
        e_Site.appendChild(e_Longitude);        
        
        Element e_Latitude = ownerDoc.createElement(XML_ELEMENT_LATITUDE);
		e_Latitude = latitude.setToXmlElement(e_Latitude);
        e_Site.appendChild(e_Latitude);                
        
        if( !Float.isNaN(this.elevation) ) {
            Element e_Elevation = ownerDoc.createElement(XML_ELEMENT_ELEVATION);
            Node n_ElevationText = ownerDoc.createTextNode(String.valueOf(this.elevation));
            e_Elevation.appendChild(n_ElevationText);        
            e_Site.appendChild(e_Elevation);            
        }          
        
        Element e_Timezone = ownerDoc.createElement(XML_ELEMENT_TIMEZONE);
        Node n_TimezoneText = ownerDoc.createTextNode(Integer.toString(this.timezone));
        e_Timezone.appendChild(n_TimezoneText);           
        e_Site.appendChild(e_Timezone); 
      
		if( iauCode != null ) {
			Element e_IAUCode = ownerDoc.createElement(XML_ELEMENT_IAUCODE);
            Node n_IAUCodeText = ownerDoc.createTextNode(iauCode);
            e_IAUCode.appendChild(n_IAUCodeText);            
			e_Site.appendChild(e_IAUCode);			
		}
		
		return element;
		
	}
	
	
    // -------------------------------------------------------------------
    /**
     * Adds the site link to an given XML DOM Element
     * The site element itself will be attached to given elements 
     * ownerDocument if the passed boolean value is <code>true</code>. 
     * Also if the ownerDocument has no site container, it will
     * be created (if the passed boolean value is <code>true</code> <br>
     * Example:<br>
     * &lt;parameterElement&gt;<br>
     * <b>&lt;siteLink&gt;123&lt;/siteLink&gt;</b><br>
     * &lt;/parameterElement&gt;<br>   
     * <i>More stuff of the xml document goes here</i><br>
     * <b>&lt;siteContainer&gt;</b><br>
     * <b>&lt;site id="123"&gt;</b><br>
     * <i>site description goes here</i><br>
     * <b>&lt;/site&gt;</b><br>
     * <b>&lt;/siteContainer&gt;</b><br>
     * <br>
     * 
     * @param parent The element under which the the site link is created
     * @param addElementToContainer if <code>true</code> it's ensured that the linked
     * element exists in the corresponding container element. Please note, passing
     * <code>true</code> slowes down XML serialization. 
     * @return Returns the Element given as parameter with a additional  
     * site link, and the site element under the site container
     * of the ownerDocument
     * Might return <code>null</code> if element was <code>null</code>.
     * @see org.w3c.dom.Element
     * @since 2.0
     */ 
    public Element addAsLinkToXmlElement(Element element, boolean addElementToContainer) {
    
    	if( element == null ) {
    		return null;
    	}
    
        Document ownerDoc = element.getOwnerDocument();

        // Create the link element
        Element e_Link = ownerDoc.createElement(XML_ELEMENT_SITE);
        Node n_LinkText = ownerDoc.createTextNode(super.getID());
        e_Link.appendChild(n_LinkText);
            
        element.appendChild(e_Link);                     
                
        if( addElementToContainer ) {
            // Get or create the container element        
            Element e_Sites = null;
            NodeList nodeList = ownerDoc.getElementsByTagName(RootElement.XML_SITE_CONTAINER);
            if( nodeList.getLength() == 0 ) {  // we're the first element. Create container element
                e_Sites = ownerDoc.createElement(RootElement.XML_SITE_CONTAINER);
                ownerDoc.getDocumentElement().appendChild(e_Sites);
            } else {
                e_Sites = (Element)nodeList.item(0);  // there should be only one container element
            }                
            
            this.addToXmlElement(e_Sites);
        }
        
        return element;
        
    }
    
    // -------------------------------------------------------------------
    /**
     * Adds the site link to an given XML DOM Element
     * The site element itself will <b>NOT</b> be attached to given elements 
     * ownerDocument. Calling this method is equal to calling
     * <code>addAsLinkToXmlElement</code> with parameters <code>element, false</code><br>
     * Example:<br>
     * &lt;parameterElement&gt;<br>
     * <b>&lt;siteLink&gt;123&lt;/siteLink&gt;</b><br>
     * &lt;/parameterElement&gt;<br>   
     * <br>
     * 
     * @param element The element under which the the site link is created 
     * @return Returns the Element given as parameter with a additional  
     * site link
     * Might return <code>null</code> if element was <code>null</code>.
     * @see org.w3c.dom.Element
     */ 
    public Element addAsLinkToXmlElement(Element element) {
    	
    	return this.addAsLinkToXmlElement(element, false);
    	
    }


    // -------------------------------------------------------------------
	/**
	 * Returns the latitude of the site.<br>
	 * The latitude is a positiv angle if its north of the equator,
	 * and negative if south of the equator.
	 * 
	 * @return Returns an Angle with the geographical latitude
	 */
	public Angle getLatitude() {
        
		return latitude;
        
	}


    // -------------------------------------------------------------------
	/**
	 * Returns the longitude of the site.<br>
	 * The longitude is a positiv angle if its east of Greenwich,
	 * and negative if west of Greenwich.
	 * 
	 * @return Returns an Angle with the geographical longitude
	 */
	public Angle getLongitude() {
        
		return longitude;
        
	}


    // -------------------------------------------------------------------
	/**
	 * Returns the name of the site.<br>
	 * The name may be any string describing the site as precise as it 
	 * can be.
	 * 
	 * @return Returns the name of the site
	 */
	public String getName() {
        
		return name;
        
	}


	/**
	 * Returns the timezone of the site.<br>
	 * The timezone is given as positiv or negative value,
	 * depending on the sites timezone difference
	 * to the GMT in minutes. 
	 * 
	 * @return Returns timzone offset (in comparism to GMT) in minutes 
	 */
	public int getTimezone() {
        
		return timezone;
        
	}


    // -------------------------------------------------------------------
	/**
	 * Sets the latitude of the site.<br>
	 * The latitude must be a positiv angle if its north of the equator,
	 * and negative if south of the equator. 
	 * 
	 * @param latitude The new latitude for this site
	 * @throws IllegalArgumentException if latitude is <code>null</code>
	 */
	public void setLatitude(Angle latitude) throws IllegalArgumentException {
        
        if( latitude == null ) {
        	throw new IllegalArgumentException("Latitude cannot be null. ");
        }
        
		this.latitude = latitude;
        
	}


    // -------------------------------------------------------------------
	/**
	 * Sets the longitude of the site.<br>
	 * The longitude must be a positiv angle if its east of Greenwich,
	 * and negative if west of Greenwich.
	 * 
	 * @param longitude The new longitude for this site
	 * @throws IllegalArgumentException if longitude is <code>null</code>
	 */
	public void setLongitude(Angle longitude) throws IllegalArgumentException {
        
		if( longitude == null ) {
			throw new IllegalArgumentException("Longitude cannot be null. ");
		}        
        
		this.longitude = longitude;
        
	}


    // -------------------------------------------------------------------
	/**
	 * Sets the name of the site.<br>
	 * The name should be any string describing the site as precise as it 
	 * can be.
	 * 
	 * @param name The new name for this site
	 * @throws IllegalArgumentException if name is <code>null</code>
	 */
	public void setName(String name) throws IllegalArgumentException {
        
		if( name == null ) {
			throw new IllegalArgumentException("Site name cannot be null. ");
		}          
        
		this.name = name;
        
	}


    // -------------------------------------------------------------------
	/**
	 * Sets the timezone of the site.<br>
	 * The timezone must be given as positiv or negative value,
	 * depending on the sites timezone difference
	 * to the GMT in minutes. 
	 * 
	 * @param timezone The new timezone for this site in minutes
	 * @throws IllegalArgumentException if new timezone is greater than
	 * 720 (12*60) or lower than -720 (12*60) 
	 */
	public void setTimezone(int timezone) throws IllegalArgumentException {
        
        if(   (timezone > 720)
           || (timezone < -720) ) {
			throw new IllegalArgumentException("Timezone cannot be > 720 or < -720. ");           	
        }
        
		this.timezone = timezone;
        
	}
	
	
	// -------------------------------------------------------------------
	/**
	 * Sets the IAU code of the site.<br>
	 * 
	 * @param IAUCode The new IAU code for this site
	 */
	public void setIAUCode(String IAUCode) {
	
		if(   (IAUCode != null)
		   && ("".equals(IAUCode.trim()))
		   ) {
				this.iauCode = null;
				return;
		}		
		
		this.iauCode = IAUCode;
	
	}
	
	
	// -------------------------------------------------------------------
	/**
	 * Sets the elevation of the site.<br>
	 * The elevation should be given in meters above/under sea level. 
	 * 
	 * @param elevation The new elevation for this site
	 */
	public void setElevation(float elevation) {		
		
		this.elevation = elevation;
	
	}
	
	
	// -------------------------------------------------------------------
	/**
	 * Returns the elevation of the site.<br>
	 * The elevation is given in meters above/under sea level. 
	 * 
	 * @return Returns the sites elevation in meters above or 
	 * under sea level, or <code>NULL</code> if value was never set
	 */
	public float getElevation() {
		
		return elevation;
	
	}
	
	
	// -------------------------------------------------------------------
	/**
	 * Returns the IAU station code of the site.<br>
	 * This method may return <code>null</code> as the site may not
	 * have an IAU (International Astronomical Union) station code.
	 * 
	 * @return Returns the IAU code of the site, or <code>null</code>
	 * if no code exists, or was never set.
	 */
	public String getIAUCode() {
		
		return iauCode;
			
	}


}
