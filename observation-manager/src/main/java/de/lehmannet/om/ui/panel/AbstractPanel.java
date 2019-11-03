/* ====================================================================
 * /panel/AbstractPanel.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */


package de.lehmannet.om.ui.panel;

import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.lehmannet.om.ISchemaElement;


public abstract class AbstractPanel extends JPanel {

	static PropertyResourceBundle bundle = (PropertyResourceBundle)ResourceBundle.getBundle("ObservationManager", Locale.getDefault());	
	
	private boolean editable = false;
	
	protected AbstractPanel(boolean editable) {
		
		this.editable = editable;
		
	}		

	protected AbstractPanel(Boolean editable) {
		
		this.editable = editable.booleanValue();
		
	}		
	
	public abstract ISchemaElement createSchemaElement();
	
	public abstract ISchemaElement getSchemaElement();
	
	public abstract ISchemaElement updateSchemaElement();
		
	public boolean isEditable() {
		
		return this.editable;		
		
	}
	
	public static void reloadLanguage() {
		
		bundle = (PropertyResourceBundle)ResourceBundle.getBundle("ObservationManager", Locale.getDefault());
		
	}
	
	protected void createWarning(String message) {
		
		JOptionPane.showMessageDialog(this,
				                      message,
				                      AbstractPanel.bundle.getString("title.warning"),
									  JOptionPane.WARNING_MESSAGE);
		
	}	 	
	        
}
