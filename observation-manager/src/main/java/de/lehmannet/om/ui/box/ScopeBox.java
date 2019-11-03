/* ====================================================================
 * /box/ScopeBox.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */


package de.lehmannet.om.ui.box;

import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.IScope;

public class ScopeBox extends AbstractBox {
	
	public void addItem(ISchemaElement element) {
		
		if( element == null ) {
			return;
		}		
		
		IScope scope = (IScope)element;
		String key = this.getKey(scope);
		
		super.addItem(key, scope);
		
	}
	
	public String getKey(ISchemaElement element) {
		
		return element.getDisplayName();
		
	}		

}
