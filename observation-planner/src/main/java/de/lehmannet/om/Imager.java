/* ====================================================================
 * /Imager.java
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
 * An Imager describes a camera.
 * This class is an abstract implementation of de.lehmannet.om.IImager.<br>
 * If you need an instance use one of the subclasses.<br>
 * 
 * @author doergn@users.sourceforge.net
 * @since 1.3
 */
public abstract class Imager extends SchemaElement implements IImager {

    // ------------------
    // Instance Variables ------------------------------------------------
    // ------------------

    // Model name of the imager (usually given by vendor)
    private String model = new String();
       
    // Vendor name of imager (Canon, Meade, Attic, StarLight....)
    private String vendor = null;   
    
    // Remarks to this imager. 
    private String remarks = null;
    
    // Imager type. Only two values are allowed: IImager.CCD or IImager.Film
    //private String type = null;   Moved to concrete implementation in OAL 2.0	
	
    
	// Flag indicating whether Imager is still available
	private boolean available = true;  
	
    
	
	
    // ------------
    // Constructors ------------------------------------------------------
    // ------------

    // -------------------------------------------------------------------
    /**
     * Constructs a new instance of an Imager from an given XML Schema 
     * Node.<br>
     * Normally this constructor is only used by de.lehmannet.om.util.SchemaLoader 
     *
     * @param imager The XML Schema element that represents this imager  
     * @throws IllegalArgumentException if parameter is <code>null</code>,
     * @throws SchemaException if the given Node does not match the XML Schema
     * specifications
     */
    public Imager(Node imager) 
                    throws SchemaException, 
                           IllegalArgumentException {

        if( imager == null ) {
            throw new IllegalArgumentException("Parameter imager node cannot be NULL. ");
        }

        // Cast to element as we need some methods from it
        Element imagerElement = (Element)imager;

        // Helper classes
        NodeList children = null;
        Element child = null;

        // Getting data
        // First mandatory stuff and down below optional data
    
        // Get ID from element
        String ID = imagerElement.getAttribute(SchemaElement.XML_ELEMENT_ATTRIBUTE_ID);
        if(   (ID != null)
           && ("".equals(ID.trim()))
           ) {
               throw new SchemaException("Imager must have a ID. ");
        }       
        super.setID(ID);            
                        
        // Get mandatory model
        children = imagerElement.getElementsByTagName(IImager.XML_ELEMENT_MODEL);
        if(   (children == null)
           || (children.getLength() != 1)              
        )  {
              throw new SchemaException("Imager must have exact one model name. ");
        }
        child = (Element)children.item(0);
        String model = "";
        if( child == null ) {
          throw new SchemaException("Imager must have a model name. ");
        } else {
            if( child.getFirstChild() != null ) {
                //model = child.getFirstChild().getNodeValue();
           	  	NodeList textElements = child.getChildNodes();
	        	if(   (textElements != null)
	        	   && (textElements.getLength() > 0) 
	        	   ) {
	        		for(int te=0; te < textElements.getLength(); te++) {
	        			model = model + textElements.item(te).getNodeValue();
	        		}
	        		this.setModel(model);            		
	        	} 
            } else {           
                throw new SchemaException("Imager cannot have an empty model name. ");      
            }
        }        

		// Search for optional availability comment within nodes
		NodeList list = imagerElement.getChildNodes();
		for(int i=0; i < list.getLength(); i++) {
			Node c = list.item(i);
			if( c.getNodeType() == Node.COMMENT_NODE ) {
				if( IEquipment.XML_COMMENT_ELEMENT_NOLONGERAVAILABLE.equals(c.getNodeValue()) ) {
					this.available = false;
					break;
				}
			}
		}
        
        // Get optional vendor
        child = null;
        children = imagerElement.getElementsByTagName(IImager.XML_ELEMENT_VENDOR);
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
                 /*vendor = child.getFirstChild().getNodeValue();     
                 if( vendor != null ) {
                   this.setVendor(vendor);      
                 }*/
              } else {
                  throw new SchemaException("Problem while retrieving vendor from imager. ");                                       
              }
          } else if( children.getLength() > 1 ) {
              throw new SchemaException("Imager can have only one vendor. ");                   
          }               
        }

        // Get optional type
    /*    child = null;
        children = imagerElement.getElementsByTagName(IImager.XML_ELEMENT_TYPE);
        String type = "";
        if( children != null ) {
          if( children.getLength() == 1 ) {                   
             child = (Element)children.item(0);
              if( child != null ) {
           	  	NodeList textElements = child.getChildNodes();
	        	if(   (textElements != null)
	        	   && (textElements.getLength() > 0) 
	        	   ) {
	        		for(int te=0; te < textElements.getLength(); te++) {
	        			type = type + textElements.item(te).getNodeValue();
	        		}
	        		this.setType(type);              		
	        	} */
                 /*type = child.getFirstChild().getNodeValue();     
                 if( type != null ) {
                   this.setType(type);      
                 }*/
      /*        } else {
                  throw new SchemaException("Problem while retrieving type from imager. ");                                       
              }
          } else if( children.getLength() > 1 ) {
              throw new SchemaException("Imager can have only one type. ");                   
          }               
        }          */                              
     
        // Get optional remarks
        child = null;
        children = imagerElement.getElementsByTagName(IImager.XML_ELEMENT_REMARKS);
        String remarks = "";
        if( children != null ) {
          if( children.getLength() == 1 ) {                   
             child = (Element)children.item(0);
              if( child != null ) {
             	  	NodeList textElements = child.getChildNodes();
    	        	if(   (textElements != null)
    	        	   && (textElements.getLength() > 0) 
    	        	   ) {
    	        		for(int te=0; te < textElements.getLength(); te++) {
    	        			remarks = remarks + textElements.item(te).getNodeValue();
    	        		}
    	        		this.setRemarks(remarks);              		
    	        	} 
              	/*remarks = child.getFirstChild().getNodeValue();     
                 if( type != null ) {
                   this.setRemarks(remarks);      
                 }*/
              } else {
                  throw new SchemaException("Problem while retrieving remarks from imager. ");                                       
              }
          } else if( children.getLength() > 1 ) {
              throw new SchemaException("Imager can have only one remark element. ");                   
          }               
        }           
        
    }
    
    
    // -------------------------------------------------------------------
	/**
	 * Constructs a new instance of an Imager.<br>
	 *
	 * @param model The imager model name 	
	 * @throws IllegalArgumentException if model is <code>null</code>
	 */	    
    protected Imager(String model) throws IllegalArgumentException {
        
        this.setModel(model);       
        
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
		
		String dn = this.getModel();
		if(   (this.vendor != null)
		   && !("".equals(this.vendor.trim()))
		   ) {
			dn = this.getVendor() + " " + dn;
		}
		
		return dn;
		
	}

	
	
	
	// ------
	// Object ------------------------------------------------------------
	// ------

	// -------------------------------------------------------------------
	/**
	 * Overwrittes equals(Object) method from java.lang.Object.<br>
	 * Checks if this imager and the given Object are equal. The given 
	 * object is equal with this Imager is model, type and vendor are equal.<br>
	 * @param obj The Object to compare this Imager with.
	 * @return <code>true</code> if the given Object is an instance of IImager
	 * and its modelname, vendorname, and type matches with this Imager.<br>
	 * @see java.lang.Object
	 */    
/*	public boolean equals(Object obj) {
        
		if(   obj == null
		   || !(obj instanceof IImager)
		   ) {
			return false;
		}
                        
		IImager imager = (IImager)obj; 
		
		if( !(this.model.equals(imager.getModel())) ) {
			return false;
		}
		
		if( type != null ) {
			if( !type.equals(imager.getType()) ) {
				return false;
			}
		} else if( imager.getType() != null ) {
			return false;
		}	
		
		if( vendor != null ) {
			if( !vendor.equals(imager.getVendor()) ) {
				return false;
			}
		} else if( imager.getVendor() != null ) {
			return false;
		}
			
		return true;
                         
	}*/

	
	
	
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
    // IImager -----------------------------------------------------------
    // -------	

    // -------------------------------------------------------------------		
	/**
	 * Adds this IImager to a given parent XML DOM Element.
	 * The IImager element will be set as a child element of
	 * the passed element.
	 * 
	 * @param parent The parent element for this IImager
	 * @return Returns the element given as parameter with this 
	 * IImager as child element.<br>
     * Might return <code>null</code> if parent was <code>null</code>.
	 * @see org.w3c.dom.Element
	 */    
	public abstract Element addToXmlElement(Element element);

	
    // -------------------------------------------------------------------
    /**
     * Adds the imager link to an given XML DOM Element
     * The IImager element itself will be attached to given elements 
     * ownerDocument if the passed boolean was <code>true</code>. If the 
     * ownerDocument has no IImager container, it will
     * be created (in case the passed boolean was <code>true</code>).<br>
     * Example:<br>
     * &lt;parameterElement&gt;<br>
     * <b>&lt;imagerLink&gt;123&lt;/imagerLink&gt;</b><br>
     * &lt;/parameterElement&gt;<br>   
     * <i>More stuff of the xml document goes here</i><br>
     * <b>&lt;imagerContainer&gt;</b><br>
     * <b>&lt;imager id="123"&gt;</b><br>
     * <i>imager description goes here</i><br>
     * <b>&lt;/imager&gt;</b><br>
     * <b>&lt;/imagerContainer&gt;</b><br>
     * <br>
     * 
     * @param parent The element under which the the imager link is created
     * @param addElementToContainer if <code>true</code> it's ensured that the linked
     * element exists in the corresponding container element. Please note, passing
     * <code>true</code> slowes down XML serialization.  
     * @return Returns the Element given as parameter with a additional  
     * imager link, and the imager element under the imager container
     * of the ownerDocument
     * Might return <code>null</code> if element was <code>null</code>.
     * @see org.w3c.dom.Element
     * @since 2.0
     */ 	
	public Element addAsLinkToXmlElement(Element parent, boolean addElementToContainer) {
		
        if( parent == null ) {
            return null;
        }
        
        Document ownerDoc = parent.getOwnerDocument(); 
                                                                                       
        // Create the link element
        Element e_Link = ownerDoc.createElement(IImager.XML_ELEMENT_IMAGER);
        Node n_LinkText = ownerDoc.createTextNode(super.getID());
        e_Link.appendChild(n_LinkText);
            
        parent.appendChild(e_Link);
        
        if( addElementToContainer ) {
            // Get or create the container element        
            Element e_Imagers = null;
            NodeList nodeList = ownerDoc.getElementsByTagName(RootElement.XML_IMAGER_CONTAINER);
            if( nodeList.getLength() == 0 ) {  // we're the first element. Create container element
            	e_Imagers = ownerDoc.createElement(RootElement.XML_IMAGER_CONTAINER);
                ownerDoc.getDocumentElement().appendChild(e_Imagers);
            } else {
            	e_Imagers = (Element)nodeList.item(0);  // there should be only one container element
            }             
            
            this.addToXmlElement(e_Imagers);          	
        }    
        
        return parent;        		
		
	}
	
	
    // -------------------------------------------------------------------
    /**
     * Adds the imager link to an given XML DOM Element
     * The imager element itself will <b>NOT</b> be attached to given elements 
     * ownerDocument. Calling this method is equal to calling
     * <code>addAsLinkToXmlElement</code> with parameters <code>element, false</code><br>
     * Example:<br>
     * &lt;parameterElement&gt;<br>
     * <b>&lt;imagerLink&gt;123&lt;/imagerLink&gt;</b><br>
     * &lt;/parameterElement&gt;<br>   
     * <br>
     * 
     * @param element The element under which the the imager link is created
     * @return Returns the Element given as parameter with a additional  
     * imager link
     * Might return <code>null</code> if element was <code>null</code>.
     * @see org.w3c.dom.Element
     */ 
    public Element addAsLinkToXmlElement(Element element) {
    	
    	return this.addAsLinkToXmlElement(element, false);
    	
    } 	


    // -------------------------------------------------------------------
	/**
	 * Returns the model name of this imager.<br>
	 * 
	 * @return Returns the model name of this imager.<br>
	 */   
	public String getModel() {
		
		return this.model;
		
	}

	
	// -------------------------------------------------------------------
	/**
	 * Returns the vendor name of this imager.<br>
	 * 
	 * @return Returns the vendor name of this imager or <code>NULL</code>
	 * if vendor name was not set.<br>
	 */    
	public String getVendor() {
		
		return this.vendor;
		
	}
	
	
	// -------------------------------------------------------------------
	/**
	 * Returns the type of this imager.<br>
	 * Only two types are allowed:<br>
	 * - IImager.CCD<br>
	 * - IImager.Film<br> 
	 * 
	 * @return Returns the type imager or <code>NULL</code>
	 * if type was not set.<br>
	 */   
/*	public String getType() {
		
		return this.type;
		
	}*/

	
	// -------------------------------------------------------------------
	/**
	 * Returns the remarks to this imager.<br>
	 * 
	 * @return Returns the remarks to this imager or <code>NULL</code>
	 * if no remarks were set.<br>
	 */ 
	public String getRemarks() {
		
		return this.remarks;
		
	}


	// -------------------------------------------------------------------
	/**
	 * Sets the model name for the imager.<br>
	 * 
	 * @param modelname The new model name to be set.
	 * @throws IllegalArgumentException if new modelname is <code>null</code> or empty string
	 */  
	public void setModel(String modelname) throws IllegalArgumentException {

		if(   (modelname == null)
	       || ("".equals(modelname))
		   ) {
			throw new IllegalArgumentException("Model name cannot be NULL or empty string.S\n");
		}
		
		this.model = modelname;
		
	}

	
	// -------------------------------------------------------------------
	/**
	 * Sets the vendor name for the imager.<br>
	 * 
	 * @param vendor The new vendor name to be set.
	 */  	
	public void setVendor(String vendor) {

		if(   (vendor != null)
		   && ("".equals(vendor.trim()))
		   ) {
				this.vendor = null;
				return;
		}		
		
		this.vendor = vendor;

	}

	
	// -------------------------------------------------------------------
	/**
	 * Sets the type for this imager.<br>
	 * Only two types are allowed:<br>
	 * - IImager.CCD<br>
	 * - IImager.Film<br> 
	 * If some other type is given, no change will take place.<br>
	 * 
	 * @param type The new image type.
	 */  	
/*	public void setType(String type) {
		
		if(   (IImager.CCD.equals(type))
		   || (IImager.FILM.equals(type))
		   ) {
			this.type = type;
		} else {
			throw new IllegalArgumentException("Imager type must be IImager.CCD or IImager.Film.\n");
		}

	}*/

	
	// -------------------------------------------------------------------
	/**
	 * Sets the remarks for this imager.<br>
	 * 
	 * @param remarks The new remarks.
	 */  	
	public void setRemarks(String remarks) {
		
		if(   (remarks != null)
		   && ("".equals(remarks.trim()))
		   ) {
				this.remarks = null;
				return;
		}		
		
		this.remarks = remarks;

	}

	
	
	
	// -----------------
	// Protected methods -------------------------------------------------
	// -----------------

	// -------------------------------------------------------------------
	/**
	 * Creates an XML DOM Element for the Imager.
	 * The new Imager element will be added as child
	 * element to an given parent element. 
     * The given parent element should be the target container.
	 * All specialised subclasses may use this method to
	 * create a Imager element under which they may store
	 * their addition data.<br>
	 * Example:<br>
	 * &lt;imagerContainer&gt;<br>
	 * &lt;imager&gt;<br>
	 * <i>More specialised stuff goes here</i><br>
	 * &lt;/imager&gt;<br>
	 * &lt;/imagerContainer&gt;
	 * 
	 * @param parent The target container element
	 * @return Returns the new created target element (which is a child
     * of the passed container element)
     * Might return <code>null</code> if parent was <code>null</code>.
	 * @see org.w3c.dom.Element
	 */  
	protected Element createXmlImagerElement(Element parent) {
    
		if( parent == null ) {                        
			return null;
		}     
    
		Document ownerDoc = parent.getOwnerDocument();     
        
        // Check if this element doesn't exist so far
        NodeList nodeList = parent.getElementsByTagName(IImager.XML_ELEMENT_IMAGER);
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
                        return parent;
                }
            }
        }   		
		
        // Create the new imager element
		Element e_Imager = ownerDoc.createElement(IImager.XML_ELEMENT_IMAGER);        
		e_Imager.setAttribute(ISchemaElement.XML_ELEMENT_ATTRIBUTE_ID, super.getID());
        
        parent.appendChild(e_Imager);     
        
		// ----- Set Comments (do this at the very beginning to possibly increase speed during read)
		if( !this.isAvailable() ) {
			Comment comment = ownerDoc.createComment(IEquipment.XML_COMMENT_ELEMENT_NOLONGERAVAILABLE);
			e_Imager.appendChild(comment);			
		}  
        
		Element e_Model = ownerDoc.createElement(IImager.XML_ELEMENT_MODEL);
        Node n_ModelText = ownerDoc.createCDATASection(this.model);
        e_Model.appendChild(n_ModelText);
        e_Imager.appendChild(e_Model);
       
   /*     if( this.type != null ) {
            Element e_Type = ownerDoc.createElement(IImager.XML_ELEMENT_TYPE);
            Node n_TypeText = ownerDoc.createCDATASection(this.type);
            e_Type.appendChild(n_TypeText);
            e_Imager.appendChild(e_Type);            
        }        */
        
        if( this.vendor != null ) {
            Element e_Vendor = ownerDoc.createElement(IImager.XML_ELEMENT_VENDOR);
            Node n_VendorText = ownerDoc.createCDATASection(this.vendor);
            e_Vendor.appendChild(n_VendorText);
            e_Imager.appendChild(e_Vendor);            
        }
                
        if( this.remarks != null ) {
            Element e_Remarks = ownerDoc.createElement(IImager.XML_ELEMENT_REMARKS);
            Node n_RemarksText = ownerDoc.createCDATASection(this.remarks);
            e_Remarks.appendChild(n_RemarksText);
            e_Imager.appendChild(e_Remarks);            
        }        
        
		return e_Imager;
        
	}       
	
}