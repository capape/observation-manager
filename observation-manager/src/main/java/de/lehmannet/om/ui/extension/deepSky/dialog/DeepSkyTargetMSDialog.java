/* ====================================================================
 * /extension/deepSky/dialog/DeepSkyTargetMSDialog.java
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
import de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetMSPanel;
import de.lehmannet.om.ui.navigation.ObservationManager;


public class DeepSkyTargetMSDialog extends AbstractDialog implements ITargetDialog {

	private static final long serialVersionUID = -8129406510069525991L;
	private final PropertyResourceBundle bundle = (PropertyResourceBundle)ResourceBundle.getBundle("de.lehmannet.om.ui.extension.deepSky.DeepSky", Locale.getDefault());	
	
	public DeepSkyTargetMSDialog(ObservationManager om,
	   		                     ITarget editableTarget) {
	
		super(om, new DeepSkyTargetMSPanel(om, editableTarget, new Boolean(true)));
		
		if( editableTarget == null ) {
			super.setTitle(this.bundle.getString("dialog.ms.title"));	
		} else {
			super.setTitle(this.bundle.getString("dialog.ms.titleEdit") + " " + editableTarget.getDisplayName());
		}				
		
		super.setSize(DeepSkyTargetMSDialog.serialVersionUID, 750, 407);
				
		super.setVisible(true);		
	
	}				
	
	public ITarget getTarget() {

		if( super.schemaElement != null ) {
			return (ITarget)super.schemaElement;
		}
	
		return null;
	
	}	
	
}
