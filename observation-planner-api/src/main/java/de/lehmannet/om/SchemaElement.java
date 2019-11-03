/* ====================================================================
 * /SchemaElement.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */


package de.lehmannet.om;

import de.lehmannet.om.util.IIDGenerator;


/**
 * The SchemaElement represents the root class for all
 * schema element classes. It provides a simple
 * implemantation of the ISchemaInterface, so that
 * any subclass inherits a unique ID.
 * 
 * @author doergn@users.sourceforge.net
 * @since 1.0
 */
public abstract class SchemaElement implements ISchemaElement {
		
	// ---------------
	// Class Variables ---------------------------------------------------
	// ---------------	
	
	// UID Generator
	private static IIDGenerator IDGenerator = null;
	
	
	
	
	// -------------
	// Static blocks -----------------------------------------------------
	// -------------
	
	static {
		// Determin VM Version once	
		String vmVersion = System.getProperty("java.version");
		
		// Determin if we're running in VM 1.5 or higher. 
		boolean after14 = !vmVersion.startsWith("1.4");
		
		if( after14 ) {   	// Use UUID
			try {
				IDGenerator = new de.lehmannet.om.util.UUIDGenerator();				
			} catch( Exception e ) {				
				System.err.println("Cannot create UUID Generator. Try to get UIDGenerator...");
				IDGenerator = new de.lehmannet.om.util.UIDGenerator();
			}
		} else {      		// Use UID
			IDGenerator = new de.lehmannet.om.util.UIDGenerator();
		}
		
	}			
	
	
	
	
	// ------------------
	// Instance Variables ------------------------------------------------
	// ------------------

	// The unique ID of this schema element
	private String ID = new String();

	

	
	// ------------
	// Constructors ------------------------------------------------------
	// ------------

	// -------------------------------------------------------------------    
	/**
	 * Constructs a new instance of a Schema Element.<br>
	 * Any instance of a Schema Element has a unique ID
	 * which identifies the element, and which allows to
	 * link serveral elements.
	 */
	public SchemaElement() {
		
		ID = SchemaElement.IDGenerator.generateUID();
		
	}


	// -------------------------------------------------------------------    
	/**
	 * Constructs a new instance of a Schema Element with a given ID.<br>
	 * Any instance of a Schema Element has a unique ID
	 * which identifies the element, and which allows to
	 * link serveral elements.<br>
	 * 
	 * @param ID This elements unique ID
	 * @throws IllegalArgumentException if ID is <code>null</code> or contains
	 * empty string.
	 */
	SchemaElement(String id) throws IllegalArgumentException {
		
		if( id == null ) {
			throw new IllegalArgumentException("ID cannot be null or empty! ");
		}
		ID = id;
		
	}

	
	
	
	// ------
	// Object ------------------------------------------------------------
	// ------
	
	
	// -------------------------------------------------------------------
	/**
	 * Overwrittes equals(Object) method from java.lang.Object.<br>
	 * Checks if the ID of this SchemaElement is equal to the given SchemaElement. 
	 * If the given object is not derived from ISchemaElemnt, false is returned as well. 
	 * @param obj The Object to compare this SchemaElement with.
	 * @return <code>true</code> if the given Object is an instance of ISchemaElement
	 * and its ID equals this SchemaElements ID.<br>
	 * @see java.lang.Object
	 */    
	public boolean equals(Object obj) {
        
		if(   obj == null
		   || !(obj instanceof ISchemaElement)
		   ) {
			return false;
		}
                        
		ISchemaElement schemaElement = (ISchemaElement)obj; 
		
		if( this.getID().equals(schemaElement.getID()) ) {
			return true;
		}		
		
		return false;
                         
	}
	
	

	
	// --------------
	// ISchemaElement ----------------------------------------------------
	// --------------

	// -------------------------------------------------------------------
	/**
	 * Returns a unique ID of this schema element.<br>
	 * The ID is used to link this element with other
	 * XML elements in the schema.
	 * 
	 * @return Returns a String representing a unique ID
	 * of this schema element.
	 */ 
	public String getID() {
		
		return ID;
		
	}
    
    
    // -------------------------------------------------------------------
    /**
     * Sets a unique ID of this schema element.<br>
     * The ID is used to link this element with other
     * XML elements in the schema.<br>
     * Call this method only, if your know what you're doing. 
     * 
     * @param newID The new unique ID for this object.
     */ 
    public void setID(String newID) {
        
        this.ID = newID;
        
    }    

    
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
	public abstract String getDisplayName();
	
}
