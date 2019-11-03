/* ====================================================================
 * /dialog/GenericTargetDialog.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.dialog;

import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import de.lehmannet.om.ITarget;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.panel.GenericTargetPanel;

public class GenericTargetDialog extends AbstractDialog implements ITargetDialog {

	private static final long serialVersionUID = -8858493947135823299L;
	
	private final PropertyResourceBundle bundle = (PropertyResourceBundle)ResourceBundle.getBundle("ObservationManager", Locale.getDefault());	
	
	public GenericTargetDialog(ObservationManager om,
                               ITarget editableTarget,
                               Boolean editable
                               ) {
	
		super(om, new GenericTargetPanel(om, editableTarget, new Boolean(true)));
		
		if( editableTarget == null ) {
			super.setTitle(this.bundle.getString("dialog.genericTarget.title"));	
		} else {
			super.setTitle(this.bundle.getString("dialog.genericTarget.titleEdit") + " " + editableTarget.getDisplayName());
		}				
		
		super.setSize(GenericTargetDialog.serialVersionUID ,590, 260);
		
	//	super.pack();
		super.setVisible(true);		
	
	}	
	
	public ITarget getTarget() {
		
		if( super.schemaElement != null ) {
			return (ITarget)super.schemaElement;
		}
		
		return null;
		
	}

}
