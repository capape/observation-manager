/* ====================================================================
 * /dialog/SessionDialog.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */


package de.lehmannet.om.ui.dialog;

import de.lehmannet.om.ISession;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.panel.SessionPanel;


public class SessionDialog extends AbstractDialog {

	private static final long serialVersionUID = 3246978868012633237L;

	public SessionDialog(ObservationManager om,
	            		 ISession editableSession) {
	
		super(om, new SessionPanel(om, editableSession, true));
		
		if( editableSession == null ) {
			super.setTitle(AbstractDialog.bundle.getString("dialog.session.title"));	
		} else {
			super.setTitle(AbstractDialog.bundle.getString("dialog.session.titleEdit") + " " + editableSession.getDisplayName());
		}				
				
		super.setSize(SessionDialog.serialVersionUID, 1090, 610);
		super.setLocationRelativeTo(om);	
		super.setVisible(true);		
		
	}				
	
	public ISession getSession() {
	
		if( super.schemaElement != null ) {
			return (ISession)this.schemaElement;
		}
		
		return null;
	
	}
	
}
