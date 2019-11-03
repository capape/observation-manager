/* ====================================================================
 * /navigation/tableModel/FilterTableModel.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */


package de.lehmannet.om.ui.navigation.tableModel;

import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import de.lehmannet.om.IFilter;
import de.lehmannet.om.ui.panel.FilterPanel;


public class FilterTableModel extends AbstractSchemaTableModel {
	
	private static final String MODEL_ID = "Filter";
	
	private final PropertyResourceBundle bundle = (PropertyResourceBundle)ResourceBundle.getBundle("ObservationManager", Locale.getDefault());
	
	public FilterTableModel(IFilter[] filter) {

		super.elements = filter;
		
	}	
	
	public int getColumnCount() {
		
		return 5;
		
	}	

    public String getID() {
    	
        return FilterTableModel.MODEL_ID;
        
    }	
	
	public int getRowCount() {

        if( super.elements == null ) {
            return 5;
        }
        return super.elements.length;
		
	}

	public Object getValueAt(int rowIndex, int columnIndex) {

        String value = "";
        
        if( super.elements == null ) {
            return value;
        }
        
        IFilter filter = (IFilter)super.elements[rowIndex];
        
        switch (columnIndex) {
            case 0 : {
                        value = filter.getModel();
                        break; 
                     }
            case 1 : {
            			value = FilterPanel.getI18Ntype(filter.getType());
                        break; 
                     }
            case 2 : {
                        if( filter.getColor() == null ) {
                        	return "";
                        } else {
                        	value = "" + FilterPanel.getI18Ntype(filter.getColor());	
                        }            	        
                        break; 
                     }
            case 3 : {
		                if( filter.getWratten() == null ) {
		                	return "";
		                } else {
		                	value = "" + filter.getWratten();	
		                }            	        
		                break; 
                     }
            case 4 : {
		                if( filter.getSchott() == null ) {
		                	return "";
		                } else {
		                	value = "" + filter.getSchott();	
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
                        name = this.bundle.getString("table.header.filter.model");
                        break; 
                     }
            case 1 : {
                        name = this.bundle.getString("table.header.filter.type");
                        break; 
                     }
            case 2 : {
                        name = this.bundle.getString("table.header.filter.colorType");
                        break; 
                     }
            case 3 : {
                        name = this.bundle.getString("table.header.filter.wratten");
                        break; 
                     }
            case 4 : {
                		name = this.bundle.getString("table.header.filter.schott");
                		break; 
             		 }                     
        }        
        
        return name;
     
    }	
	
}
