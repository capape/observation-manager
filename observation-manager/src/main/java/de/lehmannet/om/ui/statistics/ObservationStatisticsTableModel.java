/* ====================================================================
 * /statistics/ObservationStatisticsTableModel.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */


package de.lehmannet.om.ui.statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import de.lehmannet.om.IObservation;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.ui.navigation.tableModel.AbstractSchemaTableModel;

public class ObservationStatisticsTableModel extends AbstractSchemaTableModel {
	
	private static final String MODEL_ID = "Statistics";
	
	private final PropertyResourceBundle bundle = (PropertyResourceBundle)ResourceBundle.getBundle("ObservationManager", Locale.getDefault());
	
	private CatalogTargets catalogTargets = null;
	private ArrayList rowIndexMapping = null;
	
    public ObservationStatisticsTableModel(CatalogTargets catalogTargets) {        
    	
    	this.catalogTargets = catalogTargets;

    	// Build row index map
    	// Maps the table row against the array index of the catalogTarget array
    	this.rowIndexMapping = new ArrayList();
    	TargetObservations[] to = this.catalogTargets.getTargetObservations();
    	int rowNumber = 0;
    	int obsNumber = 0;
    	for(int i=0; i < to.length; i++) {
    		if( to[i].getObservations() == null) {
    			this.rowIndexMapping.add(rowNumber++, new TargetObservation(i, -1));
    		} else {
	    		obsNumber = to[i].getObservations().size();
	    		for(int x=0; x < obsNumber; x++) {
	    			this.rowIndexMapping.add(rowNumber++, new TargetObservation(i, x));	
	    		}
    		}
    	}
    	
    }	
	
	public int getColumnCount() {
		
		return 2;
		
	}
	
    public String getID() {
    	
        return ObservationStatisticsTableModel.MODEL_ID;
        
    }		

	public int getRowCount() {

		return this.rowIndexMapping.size();
		
	}
	
    public Class getColumnClass(int columnIndex) {
    	
        Class c = null;

        switch (columnIndex) {
	        case 0 : {
	            		c = ITarget.class;
	            		break; 
	         		 }        
            case 1 : {            	
                        c = IObservation.class;
                        break; 
                     }
        }        
        
        return c;    	
    	
    }	
	
	public Object getValueAt(int rowIndex, int columnIndex) {

        Object value = null;
        
        if( this.catalogTargets.getTargetObservations() == null ) {
            return value;
        }                
               
        TargetObservation to = (TargetObservation)this.rowIndexMapping.get(rowIndex);
        
        switch (columnIndex) {
	        case 0 : {
				        ITarget t = this.catalogTargets.getTargetObservations()[to.targetIndex].getTarget();
				        value = t;
				        
				        // If row above has the same target then this one, return null 
				        if( rowIndex > 0 ) {        	
						    TargetObservation toAbove = (TargetObservation)this.rowIndexMapping.get(rowIndex-1);
						    if(   (toAbove != null)
						       && (to.targetIndex == toAbove.targetIndex) 
						       ) {
						    	value = null;
						    }
				        }				        
				        
			            break; 
	         		 }        
            case 1 : {             	
            			List l = this.catalogTargets.getTargetObservations()[to.targetIndex].getObservations();
            			if( l != null ) {
                			IObservation o = (IObservation)l.get(to.observtionIndex);
                			value = o;            				
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
                        name = this.bundle.getString("table.header.catalogStatistics.target");
                        break; 
                     }
            case 1 : {
                        name = this.bundle.getString("table.header.catalogStatistics.observation");
                        break; 
                     }                 
        }        
        
        return name;
     
    }	
    
    public int getColumnSize(int columnIndex) {
        
        switch (columnIndex) {
	        case 0 : {
	                    return 95; 
	                 }
	        case 1 : {
	        			return 275; 
	                 }   
        }     
        
        return super.getColumnSize(columnIndex);
    	
    }     
    
    TargetObservations[] getTargetObservations() {
    	
		return catalogTargets.getTargetObservations();
		
	}

	String getCatalogName() {
		
		return catalogTargets.getCatalog().getName();
		
	}    
    
}

class TargetObservation {
	
	public int targetIndex = 0;
	public int observtionIndex = 0;
	
	public TargetObservation(int targetIndex, int observationIndex) {
		
		this.targetIndex = targetIndex;
		this.observtionIndex = observationIndex;
		
	}
		
}