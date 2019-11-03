/* ====================================================================
 * /navigation/tableModel/EyepieceTableModel.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */


package de.lehmannet.om.ui.navigation.tableModel;

import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import de.lehmannet.om.Angle;
import de.lehmannet.om.IEyepiece;


public class EyepieceTableModel extends AbstractSchemaTableModel {
	
	private static final String MODEL_ID = "Eyepiece";
	
	private final PropertyResourceBundle bundle = (PropertyResourceBundle)ResourceBundle.getBundle("ObservationManager", Locale.getDefault());
	
	public EyepieceTableModel(IEyepiece[] eyepiece) {

		super.elements = eyepiece;
		
	}	
	
	public int getColumnCount() {
		
		return 4;
		
	}
	
    public String getID() {
    	
        return EyepieceTableModel.MODEL_ID;
        
    }	

	public int getRowCount() {

        if( super.elements == null ) {
            return 5;
        }
        return super.elements.length;
		
	}

    public Class getColumnClass(int columnIndex) {
    	
        Class c = null;

        switch (columnIndex) {
	        case 0 : {
	            		c = String.class;
	            		break; 
	         		 }        
            case 1 : {            	
                        c = String.class;
                        break; 
                     }
            case 2 : {            	
                		c = String.class;
                		break; 
             		  }            
            case 3 : {            	
		                c = Angle.class;
		                break; 
		             }            
        }        
        
        return c;    	
    	
    }	
	
	public Object getValueAt(int rowIndex, int columnIndex) {

        Object value = null;
        
        if( super.elements == null ) {
            return value;
        }
        
        IEyepiece eyepiece = (IEyepiece)super.elements[rowIndex];
        
        switch (columnIndex) {
            case 0 : {
                        value = eyepiece.getVendor();
                        break; 
                     }
            case 1 : {
            			value = eyepiece.getModel();
                        break; 
                     }
            case 2 : {
                        value = "" + eyepiece.getFocalLength();
                        if( eyepiece.isZoomEyepiece() ) {
                        	value = value + "-" + eyepiece.getMaxFocalLength();
                        }
                        break; 
                     }
            case 3 : {
            	        Angle afov = eyepiece.getApparentFOV();
            	        if( afov != null ) {
            	        	afov.toDegree();
            	        	value = afov;
            	        }                        
                        break; 
                     }         
        }      
        
		return value;
		
	}		
	
    public String getColumnName(int column) {

        String name = "";

        switch (column) {
            case 0 : {
                        name = this.bundle.getString("table.header.eyepiece.vendor");
                        break; 
                     }
            case 1 : {
                        name = this.bundle.getString("table.header.eyepiece.model");
                        break; 
                     }
            case 2 : {
                        name = this.bundle.getString("table.header.eyepiece.focalLength");
                        break; 
                     }
            case 3 : {
                        name = this.bundle.getString("table.header.eyepiece.apparentFoV");
                        break; 
                     }         
        }        
        
        return name;
     
    }	
	
}
