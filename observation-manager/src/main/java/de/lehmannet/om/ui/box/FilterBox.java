/* ====================================================================
 * /box/FilterBox.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */


package de.lehmannet.om.ui.box;

import de.lehmannet.om.IFilter;
import de.lehmannet.om.ISchemaElement;

public class FilterBox extends AbstractBox {

	public void addItem(ISchemaElement element) {
		
		if( element == null ) {
			return;
		}		
		
		IFilter filter = (IFilter)element;
		String key = this.getKey(filter);
		
		super.addItem(key, filter);
		
	}
	
	public String getKey(ISchemaElement element) {
		
		return element.getDisplayName();
		
	}		

}
