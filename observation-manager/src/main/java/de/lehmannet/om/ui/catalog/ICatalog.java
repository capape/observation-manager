/* ====================================================================
 * /util/ICatalog.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.catalog;

import de.lehmannet.om.ITarget;
import de.lehmannet.om.ui.panel.AbstractSearchPanel;

public interface ICatalog {

	public ITarget getTarget(String objectName);

	public String getName();	
	
	public AbstractSearchPanel getSearchPanel();
	
	public String getAbbreviation();
	
}
