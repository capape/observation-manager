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

import de.lehmannet.om.util.SchemaException;


/**
 * Filter implements the class de.lehmannet.om.IFilter.
 * A Filter describes a optical filter used during the
 * observation.<br>
 * This includes all kind of filters like color filters, band filter, ...
 * <br>
 * The model name and the type are 
 * mandatory fields which have to be set.
 * 
 * @author doergn@users.sourceforge.net
 * @since 1.5
 */
public class Filter extends SchemaElement implements IFilter {
	
    // ------------------
    // Instance Variables ------------------------------------------------
    // ------------------

    // Model name of the filter (usually given by vendor)
    private String model = new String();
       
    // Type of filter (Narrow band, Color, O-III, ...)
    private String type = new String();  
    
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

    // -------------------------------------------------------------------
    /**
     * Constructs a new instance of a Filter from an given XML Schema 
     * Node.<br>
     * Normally this constructor is only used by de.lehmannet.om.util.SchemaLoader 
     *
     * @param filter The XML Schema element that represents this filter  
     * @throws IllegalArgumentException if parameter is <code>null</code>,
     * @throws SchemaException if the given Node does not match the XML Schema
     * specifications
     */
    public Filter(Node filter) 
                  throws SchemaException, 
                         IllegalArgumentException {

        if( filter == null ) {
            throw new IllegalArgumentException("Parameter filter node cannot be NULL. ");
        }

        // Cast to element as we need some methods from it
        Element filterElement = (Element)filter;

        // Helper classes
        NodeList children = null;
        Element child = null;

        // Getting data
        // First mandatory stuff and down below optional data
    
        // Get ID from element
        String ID = filterElement.getAttribute(IFilter.XML_ELEMENT_ATTRIBUTE_ID);
        if(   (ID != null)
           && ("".equals(ID.trim()))
           ) {
               throw new SchemaException("Filter must have a ID. ");
        }       
        super.setID(ID);            
                        
        // Get mandatory model
        children = filterElement.getElementsByTagName(IFilter.XML_ELEMENT_MODEL);
        if(   (children == null)
           || (children.getLength() != 1)              
        )  {
              throw new SchemaException("Filter must have exact one model name. ");
        }
        child = (Element)children.item(0);
        String model = "";
        if( child == null ) {
          throw new SchemaException("Filter must have a model name. ");
        } else {
            if( child.getFirstChild() != null ) {
        	  	NodeList textElements = child.getChildNodes();
	        	if(   (textElements != null)
	        	   && (textElements.getLength() > 0) 
	        	   ) {
	        		for(int te=0; te < textElements.getLength(); te++) {
	        			model = model + textElements.item(te).getNodeValue();
	        		}
	        		this.setModel(model);      		
	        	} 
//                model = child.getFirstChild().getNodeValue();                
            } else {           
                throw new SchemaException("Filter cannot have an empty model name. ");      
            }
        }        

        // Get mandatory type
        children = filterElement.getElementsByTagName(IFilter.XML_ELEMENT_TYPE);
        if(   (children == null)
           || (children.getLength() != 1)              
        )  {
              throw new SchemaException("Filter must have exact one type. ");
        }
        child = (Element)children.item(0);
        String type = "";
        if( child == null ) {
          throw new SchemaException("Filter must have a type. ");
        } else {
            if( child.getFirstChild() != null ) {
        	  	NodeList textElements = child.getChildNodes();
	        	if(   (textElements != null)
	        	   && (textElements.getLength() > 0) 
	        	   ) {
	        		for(int te=0; te < textElements.getLength(); te++) {
	        			type = type + textElements.item(te).getNodeValue();
	        		}
	        		this.setType(type);            		
	        	} 
                //type = child.getFirstChild().getNodeValue();                
            } else {           
                throw new SchemaException("Filter cannot have an empty type. ");      
            }
        } 
        
		// Search for optional availability comment within nodes
		NodeList list = filterElement.getChildNodes();
		for(int i=0; i < list.getLength(); i++) {
			Node c = list.item(i);
			if( c.getNodeType() == Node.COMMENT_NODE ) {
				if( IEquipment.XML_COMMENT_ELEMENT_NOLONGERAVAILABLE.equals(c.getNodeValue()) ) {
					this.available = false;
					break;
				}
			}
		}	
        
        // Get optional color
        child = null;
        children = filterElement.getElementsByTagName(IFilter.XML_ELEMENT_COLOR);
        String color = "";
        if( children != null ) {
          if( children.getLength() == 1 ) {                   
             child = (Element)children.item(0);
             if( child != null ) {
         	  	NodeList textElements = child.getChildNodes();
	        	if(   (textElements != null)
	        	   && (textElements.getLength() > 0) 
	        	   ) {
	        		for(int te=0; te < textElements.getLength(); te++) {
	        			color = color + textElements.item(te).getNodeValue();
	        		}
	        		this.setColor(color);           		
	        	} 	        	  
             	/*color = child.getFirstChild().getNodeValue();     
	            if( color != null ) {
	              this.setColor(color);      
	            }*/
	         } else {
	             throw new SchemaException("Problem while retrieving color from filter. ");                                       
	         }
          } else if( children.getLength() > 1 ) {
              throw new SchemaException("Filter can have only one color. ");                   
          }               
        }

        // Get optional wratten value
        child = null;
        children = filterElement.getElementsByTagName(IFilter.XML_ELEMENT_WRATTEN);
        String wratten = "";
        if( children != null ) {
          if( children.getLength() == 1 ) {                   
             child = (Element)children.item(0);
             if( child != null ) {
          	  	NodeList textElements = child.getChildNodes();
	        	if(   (textElements != null)
	        	   && (textElements.getLength() > 0) 
	        	   ) {
	        		for(int te=0; te < textElements.getLength(); te++) {
	        			wratten = wratten + textElements.item(te).getNodeValue();
	        		}
	        		this.setWratten(wratten);           		
	        	} 
             	/*wratten = child.getFirstChild().getNodeValue();     
	            if( wratten != null ) {
	              this.setWratten(wratten);      
	            }*/
	         } else {
	             throw new SchemaException("Problem while retrieving wratten value from filter. ");                                       
	         }
          } else if( children.getLength() > 1 ) {
              throw new SchemaException("Filter can have only one wratten value. ");                   
          }               
        }
        
        // Get optional schott value
        child = null;
        children = filterElement.getElementsByTagName(IFilter.XML_ELEMENT_SCHOTT);
        String schott = "";
        if( children != null ) {
          if( children.getLength() == 1 ) {                   
             child = (Element)children.item(0);
             if( child != null ) {
           	  	NodeList textElements = child.getChildNodes();
	        	if(   (textElements != null)
	        	   && (textElements.getLength() > 0) 
	        	   ) {
	        		for(int te=0; te < textElements.getLength(); te++) {
	        			schott = schott + textElements.item(te).getNodeValue();
	        		}
	        		this.setSchott(schott);            		
	        	}             	 
             	/*schott = child.getFirstChild().getNodeValue();     
	            if( schott != null ) {
	              this.setSchott(schott);      
	            }*/
	         } else {
	             throw new SchemaException("Problem while retrieving schott value from filter. ");                                       
	         }
          } else if( children.getLength() > 1 ) {
              throw new SchemaException("Filter can have only one schott value. ");                   
          }               
        }   
        
        // Get optional vendor name
        child = null;
        children = filterElement.getElementsByTagName(IFilter.XML_ELEMENT_VENDOR);
        String vendor = "";
        if( children != null ) {
          if( children.getLength() == 1 ) {                   
             child = (Element)children.item(0);
             if( child != null ) {
           	  	NodeList textElements = child.getChildNodes();
	        	if(   (textElements != null)
	        	   && (textElements.getLength() > 0) 
	        	   ) {
	        		for(int te=0; te < textElements.getLength(); te++) {
	        			vendor = vendor + textElements.item(te).getNodeValue();
	        		}
	        		this.setVendor(vendor);            		
	        	}             	 
	         } else {
	             throw new SchemaException("Problem while retrieving vendor name from filter. ");                                       
	         }
          } else if( children.getLength() > 1 ) {
              throw new SchemaException("Filter can have only one vendor name. ");                   
          }               
        }         
                
    }
    
    
    // -------------------------------------------------------------------
	/**
	 * Constructs a new instance of a Filter.<br>
	 *
	 * @param model The filter model name 
	 * @param type The type of the filter (use IFilter constants)	
	 * @throws IllegalArgumentException if model is empty <code>null</code> or
	 *         type is empty, <code>null</code> or does not represent a type value
	 * 		   from IFilter.
	 */	    
    public Filter(String model,
                  String type) throws IllegalArgumentException {
        
        this.setModel(model);
        this.setType(type);        
        
    }    
    
    
    
    
	// ------
	// Object ------------------------------------------------------------
	// ------

	// -------------------------------------------------------------------
	/**
	 * Overwrittes toString() method from java.lang.Object.<br>
	 * Returns all fields of the class Filter (unset field will be
	 * ignored). The result string will look like this:<br>
	 * Example:<br>
	 * <code>
	 * Filter Model: Meade Narrowband<br>
	 * Type: narrow band
	 * </code>
	 * @return A string representing the filter
	 * @see java.lang.Object
	 */
	public String toString() {
        
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("Filter Model: ");
		buffer.append(this.getModel() + "\n");
		
		buffer.append("Type: ");
		buffer.append(this.getType() + "\n");		
		
		if( color != null ) {
			buffer.append("Color: ");
			buffer.append(this.getColor() + "\n");			
		} 
								
		if( wratten != null ) {
			buffer.append("Wratten: ");
			buffer.append(this.getWratten() + "\n");			
		} 
		
		if( schott != null ) {
			buffer.append("Schott: ");
			buffer.append(this.getSchott() + "\n");			
		} 		
		
		return buffer.toString();
		        
	}


	// -------------------------------------------------------------------
	/**
	 * Overwrittes equals(Object) method from java.lang.Object.<br>
	 * Checks if this Filter and the given Object are equal. The given 
	 * object is equal with this Filter, if its implementing IFilter 
	 * and its modelname, type, color, wratten and schott value
	 * matches the values of this Filter.
	 * @param obj The Object to compare this Filter with.
	 * @return <code>true</code> if the given Object is an instance of IFilter
	 * and its modelname, type, color, wratten and schott value
	 * matches with this Filter.<br>
	 * @see java.lang.Object
	 */    
	/*public boolean equals(Object obj) {
        
		if(   obj == null
		   || !(obj instanceof IFilter)
		   ) {
			return false;
		}
                        
		IFilter filter = (IFilter)obj; 
		
		if( !(this.model.equals(filter.getModel())) ) {
			return false;
		}
		
		if( !(this.type.equals(filter.getType())) ) {
			return false;
		}		
		
		if( this.color != null ) {
			if( !this.color.equals(filter.getColor()) ) {
				return false;
			}
		} else if( filter.getColor() != null ) {
			return false;
		}
	
		if( this.wratten != null ) {
			if( !this.wratten.equals(filter.getWratten()) ) {
				return false;
			}
		} else if( filter.getWratten() != null ) {
			return false;
		}
		
		if( this.schott != null ) {
			if( !this.schott.equals(filter.getSchott()) ) {
				return false;
			}
		} else if( filter.getSchott() != null ) {
			return false;
		}		
		
		return true;
                         
	}    */
    
    
	
	
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

		String dn = this.getModel();// + " " + this.getType();
		/*if(   (this.color != null)
		   && !("".equals(this.color.trim()))
		   ) {
			dn = dn + " " + this.getColor();
		}*/
		
		// Don't add type and color as we wanna translate them later on UI
		
		return dn;
		
	}
	
	
	
	
    // ----------
    // IEquipment --------------------------------------------------------
    // ----------

    // -------------------------------------------------------------------
	/**
	 * Returns <code>true</code> if this element is still available for use-<br>
	 * 
	 * @return a boolean with the availability of the element
	 */	
    public boolean isAvailable() {
    	
		return this.available;
		
	}

    // -------------------------------------------------------------------
	/**
	 * Sets the availability of this element.<br>
	 * 
	 * @param available A boolean value indicating whether this element is 
	 * still available for usage
	 */
	public void setAvailability(boolean available) {
		
		this.available = available;
		
	}
	
	
	
	
    // -------
    // IFilter -----------------------------------------------------------
    // -------	
	
    // -------------------------------------------------------------------
	/**
	 * Adds this Filter to a given parent XML DOM Element.
	 * The Filter element will be set as a child element of
	 * the passed element.
	 * 
	 * @param parent The parent element for this Filter
	 * @return Returns the element given as parameter with this 
	 * Filter as child element.<br>
     * Might return <code>null</code> if parent was <code>null</code>.
	 * @see org.w3c.dom.Element
	 */    	
	public Element addToXmlElement(Element element) {
		
        if( element == null ) {
        	return null;
        }
        
        Document ownerDoc = element.getOwnerDocument();  		
		
        // Check if this element doesn't exist so far
        NodeList nodeList = element.getElementsByTagName(IFilter.XML_ELEMENT_FILTER);
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
       
       
        // Create the new filter element
        Element e_Filter = ownerDoc.createElement(IFilter.XML_ELEMENT_FILTER);        
        e_Filter.setAttribute(IFilter.XML_ELEMENT_ATTRIBUTE_ID, super.getID());
        
        element.appendChild(e_Filter);
        
		// ----- Set Comments (do this at the very beginning to possibly increase speed during read)
		if( !this.isAvailable() ) {
			Comment comment = ownerDoc.createComment(IEquipment.XML_COMMENT_ELEMENT_NOLONGERAVAILABLE);
			e_Filter.appendChild(comment);			
		}          

        Element e_Model = ownerDoc.createElement(IFilter.XML_ELEMENT_MODEL);  
        Node n_ModelText = ownerDoc.createCDATASection(this.model);
        e_Model.appendChild(n_ModelText);
        e_Filter.appendChild(e_Model);        

		if( vendor != null ) {			
			Element e_Vendor = ownerDoc.createElement(IFilter.XML_ELEMENT_VENDOR);  
            Node n_VendorText = ownerDoc.createCDATASection(this.vendor);
            e_Vendor.appendChild(n_VendorText);
            e_Filter.appendChild(e_Vendor);						
		}		        
        
        Element e_Type = ownerDoc.createElement(IFilter.XML_ELEMENT_TYPE);  
        Node n_TypeText = ownerDoc.createCDATASection(this.type);
        e_Type.appendChild(n_TypeText);
        e_Filter.appendChild(e_Type);        
        
		if( color != null ) {			
			Element e_Color = ownerDoc.createElement(IFilter.XML_ELEMENT_COLOR);  
            Node n_ColorText = ownerDoc.createCDATASection(this.color);
            e_Color.appendChild(n_ColorText);
            e_Filter.appendChild(e_Color);						
		}                            
  
		if( wratten != null ) {			
			Element e_Wratten = ownerDoc.createElement(IFilter.XML_ELEMENT_WRATTEN);  
            Node n_WrattenText = ownerDoc.createCDATASection(this.wratten);
            e_Wratten.appendChild(n_WrattenText);
            e_Filter.appendChild(e_Wratten);						
		}
		
		if( schott != null ) {			
			Element e_Schott = ownerDoc.createElement(IFilter.XML_ELEMENT_SCHOTT);  
            Node n_SchottText = ownerDoc.createCDATASection(this.schott);
            e_Schott.appendChild(n_SchottText);
            e_Filter.appendChild(e_Schott);						
		}		
        
		return element;
		
	}		
	
    // -------------------------------------------------------------------
    /**
     * Adds the filter link to an given XML DOM Element
     * The filter element itself will be attached to given elements 
     * ownerDocument if the passed boolean was <code>true</code>. If the 
     * ownerDocument has no filter container, it will
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
     * @param element The element under which the the filter link is created
     * @param addElementToContainer if <code>true</code> it's ensured that the linked
     * element exists in the corresponding container element. Please note, passing
     * <code>true</code> slowes down XML serialization.  
     * @return Returns the Element given as parameter with a additional  
     * filter link, and the filter element under the filter container
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
        Element e_Link = ownerDoc.createElement(IFilter.XML_ELEMENT_FILTER);
        Node n_LinkText = ownerDoc.createTextNode(super.getID());
        e_Link.appendChild(n_LinkText);
            
        element.appendChild(e_Link);         
                                                                                                 
        if( addElementToContainer ) {
            // Get or create the container element        
            Element e_Filters = null;
            NodeList nodeList = ownerDoc.getElementsByTagName(RootElement.XML_FILTER_CONTAINER);
            if( nodeList.getLength() == 0 ) {  // we're the first element. Create container element
            	e_Filters = ownerDoc.createElement(RootElement.XML_FILTER_CONTAINER);
                ownerDoc.getDocumentElement().appendChild(e_Filters);
            } else {
            	e_Filters = (Element)nodeList.item(0);  // there should be only one container element
            }                                             
           
            this.addToXmlElement(e_Filters);
        }

        return element;        
        
    }		
    
    
    // -------------------------------------------------------------------
    /**
     * Adds the filter link to an given XML DOM Element
     * The filter element itself will <b>NOT</b> be attached to given elements 
     * ownerDocument. Calling this method is equal to calling
     * <code>addAsLinkToXmlElement</code> with parameters <code>element, false</code><br>
     * Example:<br>
     * &lt;parameterElement&gt;<br>
     * <b>&lt;filterLink&gt;123&lt;/filterLink&gt;</b><br>
     * &lt;/parameterElement&gt;<br>   
     * <br>
     * 
     * @param element The element under which the the filter link is created
     * @return Returns the Element given as parameter with a additional  
     * filter link
     * Might return <code>null</code> if element was <code>null</code>.
     * @see org.w3c.dom.Element
     */ 
    public Element addAsLinkToXmlElement(Element element) {
    	
    	return this.addAsLinkToXmlElement(element, false);
    	
    }     
    
	
	// -------------------------------------------------------------------
	/**
	 * Returns the color of this filter.<br>
	 * 
	 * @return Returns a String representing the filters color, or <code>null</code>
	 * if the color was never set (e.g. filter type is not IFilter.FILTER_TYPE_COLOR).<br>
	 */      
	public String getColor() {
		
		return this.color;
		
	}
	
    // -------------------------------------------------------------------
	/**
	 * Sets the color of this filter.<br>
	 * In case the current filter type is not IFilter.FILTER_TYPE_COLOR a
	 * IllegalArgumentException is thrown, so make sure to set Filter type to
	 * color first.<br>
	 * Valid color values can be retrieved from IFilter constants.<br>
	 * 
	 * @param color The new color of the filter.
	 * @throws IllegalArgumentException if filter type is not IFilter.FILTER_TYPE_COLOR,
	 * or the given color is empty <code>null</code> or does not contain a valid value.
	 * @see IFilter
	 */ 
	public void setColor(String color) throws IllegalArgumentException {
		
		if(    (color == null)
		   ||  ("".equals(color.trim()))		
		   ) {
			return;
		}
		
		if( !this.getType().equals(IFilter.FILTER_TYPE_COLOR) ) {
			throw new IllegalArgumentException("Filter type is not color.\n" +
					                           "setColor(...) can only be called on color filters");			
		}		
		
		if(   IFilter.FILTER_COLOR_BLUE.equals(color)
		   || IFilter.FILTER_COLOR_DEEPBLUE.equals(color)		
		   || IFilter.FILTER_COLOR_DEEPRED.equals(color)
		   || IFilter.FILTER_COLOR_DEEPYELLOW.equals(color)
		   || IFilter.FILTER_COLOR_GREEN.equals(color)
		   || IFilter.FILTER_COLOR_LIGHTGREEN.equals(color)
		   || IFilter.FILTER_COLOR_LIGHTRED.equals(color)
		   || IFilter.FILTER_COLOR_LIGHTYELLOW.equals(color)
		   || IFilter.FILTER_COLOR_MEDIUMBLUE.equals(color)
		   || IFilter.FILTER_COLOR_PALEBLUE.equals(color)
		   || IFilter.FILTER_COLOR_ORANGE.equals(color)
		   || IFilter.FILTER_COLOR_RED.equals(color)
		   || IFilter.FILTER_COLOR_VIOLET.equals(color)
		   || IFilter.FILTER_COLOR_YELLOW.equals(color)
		   || IFilter.FILTER_COLOR_YELLOWGREEN.equals(color)		   
		   ) {
			this.color = color;	
		} else {
			throw new IllegalArgumentException("Given color is unknown.\n");
		}
		
	}
	
	// -------------------------------------------------------------------
	/**
	 * Returns the model of this filter.<br>
	 * 
	 * @return Returns a String representing the filter model.<br>
	 */ 	
	public String getModel() {
		
		return this.model;
		
	}
	
    // -------------------------------------------------------------------
	/**
	 * Sets the model name for the filter.<br>
	 * 
	 * @param model The new model name to be set.
	 * @throws IllegalArgumentException if modelname was <code>null</code>
	 */  	
	public void setModel(String model) {
		
		if(    (model == null)
		   ||  ("".equals(model.trim()))		
		   ) {
			throw new IllegalArgumentException("Filter model cannot be null or empty string.\n");
		}				
		
		this.model = model;
		
	}
	
	// -------------------------------------------------------------------
	/**
	 * Returns the schott value of this filter.<br>
	 * 
	 * @return Returns a String representing the schott value of the filter,
	 * or <code>null</code> if the value was never set.<br>
	 */ 	
	public String getSchott() {
		
		return this.schott;
		
	}
	
    // -------------------------------------------------------------------
	/**
	 * Sets the schott value for the filter.<br>
	 * 
	 * @param schott The new schott value to be set.
	 */  	
	public void setSchott(String schott) {
		
		if(    (schott != null)
		   &&  ("".equals(schott.trim()))		
		   ) {
			this.schott = null;
			return;
		}
		
		this.schott = schott;
		
	}
	
	// -------------------------------------------------------------------
	/**
	 * Returns the filter type.<br>
	 * 
	 * @return Returns a String representing the filter type.<br>
	 */ 	
	public String getType() {
		
		return this.type;
		
	}
	
    // -------------------------------------------------------------------
	/**
	 * Sets the filter type.<br>
	 * The filter type must be a value from the types defined in IFilter.<br>
	 * If the old filter type was IFilter.FILTER_TYPE_COLOR and the new filter
	 * type is not, then the filters color is reset to <code>null</code>.
	 * 
	 * @param type The new filter type to be set.
	 * @throws IllegalArgumentException if type was empty, <code>null</code>
	 * or does not contain a valid value (see IFilter constants).
	 * @see IFilter
	 */  	
	public void setType(String type) {
		
		if(    (type == null)
		   ||  ("".equals(type.trim()))		
		   ) {
			throw new IllegalArgumentException("Type cannot be null or empty string.\n");
		}			
		
		if(   IFilter.FILTER_TYPE_BROADBAND.equals(type)
		   || IFilter.FILTER_TYPE_COLOR.equals(type)
		   || IFilter.FILTER_TYPE_CORRECTIVE.equals(type)
		   || IFilter.FILTER_TYPE_HALPHA.equals(type)			   
		   || IFilter.FILTER_TYPE_HBETA.equals(type)
		   || IFilter.FILTER_TYPE_NARROWBAND.equals(type)
		   || IFilter.FILTER_TYPE_SOLAR.equals(type)
		   || IFilter.FILTER_TYPE_NEUTRAL.equals(type)
		   || IFilter.FILTER_TYPE_OIII.equals(type)
		   || IFilter.FILTER_TYPE_OTHER.equals(type)
		   ) {
			// In case that old value was color filter (and new value is not color filter)
			// clear color value
			if(    IFilter.FILTER_TYPE_COLOR.equals(this.type)
			   &&  !IFilter.FILTER_TYPE_COLOR.equals(type)
			   ) {
				this.color = null;
			}
			
			this.type = type;			

		} else {
			throw new IllegalArgumentException("Given filter type is unknown.\n");
		}				
		
	}
	
	// -------------------------------------------------------------------
	/**
	 * Returns the wratten value of this filter.<br>
	 * 
	 * @return Returns a String representing the wratten value of the filter,
	 * or <code>null</code> if the value was never set.<br>
	 */	
	public String getWratten() {
		
		return this.wratten;
		
	}
	
    // -------------------------------------------------------------------
	/**
	 * Sets the wratten value for the filter.<br>
	 * 
	 * @param schott The new wratten value to be set.
	 */  	
	public void setWratten(String wratten) {
		
		if(    (wratten != null)
		   &&  ("".equals(wratten.trim()))		
		   ) {
			this.wratten = null;
			return;
		}		
		
		this.wratten = wratten;
		
	}
	
	
	// -------------------------------------------------------------------
	/**
	 * Returns the vendor name of this filter.<br>
	 * 
	 * @return Returns a String representing the vendor name of the filter,
	 * or <code>null</code> if the vendor was never set.<br>
	 */	
	public String getVendor() {
		
		return this.vendor;		
		
	}
	
	
    // -------------------------------------------------------------------
	/**
	 * Sets the vendor name of the filter.<br>
	 * 
	 * @param vendor The new vendor name to be set.
	 */  	
	public void setVendor(String vendor) {
		
		this.vendor = vendor;
		
	}
	
}
