/* ====================================================================
 * /extension/deepSky/dialog/DeepSkyTargetPGDialog.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */


package de.lehmannet.om.ui.extension.deepSky.dialog;

import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import de.lehmannet.om.ITarget;
import de.lehmannet.om.ui.dialog.AbstractDialog;
import de.lehmannet.om.ui.dialog.ITargetDialog;
import de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetCGPanel;
import de.lehmannet.om.ui.navigation.ObservationManager;


public class DeepSkyTargetCGDialog extends AbstractDialog implements ITargetDialog {

	private static final long serialVersionUID = -4936737277950814027L;
	
	private final PropertyResourceBundle bundle = (PropertyResourceBundle)ResourceBundle.getBundle("de.lehmannet.om.ui.extension.deepSky.DeepSky", Locale.getDefault());	
	
	public DeepSkyTargetCGDialog(ObservationManager om,
	   		                     ITarget editableTarget) {
	
		super(om, new DeepSkyTargetCGPanel(om, editableTarget, new Boolean(true)));
		
		if( editableTarget == null ) {
			super.setTitle(this.bundle.getString("dialog.cg.title"));	
		} else {
			super.setTitle(this.bundle.getString("dialog.cg.titleEdit") + " " + editableTarget.getDisplayName());
		}				
		
		super.setSize(DeepSkyTargetCGDialog.serialVersionUID, 575, 360);
		
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
