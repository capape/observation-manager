/* ====================================================================
 * /dialog/SiteDialog.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */


package de.lehmannet.om.ui.dialog;

import de.lehmannet.om.ISite;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.panel.SitePanel;

public class SiteDialog extends AbstractDialog {

	private static final long serialVersionUID = 9057953593845468049L;

	public SiteDialog(ObservationManager om,
			          ISite editableSite) {

		super(om, new SitePanel(editableSite, true));
		
		if( editableSite == null ) {
			super.setTitle(AbstractDialog.bundle.getString("dialog.site.title"));	
		} else {
			super.setTitle(AbstractDialog.bundle.getString("dialog.site.titleEdit") + " " + editableSite.getDisplayName());
		}						
		
		super.setSize(SiteDialog.serialVersionUID, 550, 140);
		super.setVisible(true);	
		
    }	
	
	public ISite getSite() {
		
		if( super.schemaElement != null ) {
			return (ISite)this.schemaElement;
		}
		
		return null;
		
	}	

}
