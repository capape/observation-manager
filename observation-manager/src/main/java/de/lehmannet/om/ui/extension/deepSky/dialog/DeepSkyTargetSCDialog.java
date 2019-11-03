/* ====================================================================
 * /extension/deepSky/dialog/DeepSkyTargetSCDialog.java
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
import de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetSCPanel;
import de.lehmannet.om.ui.navigation.ObservationManager;


public class DeepSkyTargetSCDialog extends AbstractDialog implements ITargetDialog {

	private static final long serialVersionUID = -714136627680620087L;
	private final PropertyResourceBundle bundle = (PropertyResourceBundle)ResourceBundle.getBundle("de.lehmannet.om.ui.extension.deepSky.DeepSky", Locale.getDefault());	
	
	public DeepSkyTargetSCDialog(ObservationManager om,
	   		                     ITarget editableTarget) {
	
		super(om, new DeepSkyTargetSCPanel(om, editableTarget, new Boolean(true)));
		
		if( editableTarget == null ) {
			super.setTitle(this.bundle.getString("dialog.sc.title"));	
		} else {
			super.setTitle(this.bundle.getString("dialog.sc.titleEdit") + " " + editableTarget.getDisplayName());
		}				
		
		super.setSize(DeepSkyTargetSCDialog.serialVersionUID, 620, 365);
		
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
