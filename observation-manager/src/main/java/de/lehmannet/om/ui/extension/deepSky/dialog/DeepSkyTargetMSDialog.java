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
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.dialog.AbstractDialog;
import de.lehmannet.om.ui.dialog.ITargetDialog;
import de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetMSPanel;
import de.lehmannet.om.ui.navigation.ObservationManager;

public class DeepSkyTargetMSDialog extends AbstractDialog implements ITargetDialog {

    private static final long serialVersionUID = -8129406510069525991L;

    public DeepSkyTargetMSDialog(ObservationManager om, ObservationManagerModel model,  ITarget editableTarget) {

        super(om, new DeepSkyTargetMSPanel(om, model, editableTarget, Boolean.TRUE));

        PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
                .getBundle("de.lehmannet.om.ui.extension.deepSky.DeepSky", Locale.getDefault());
        if (editableTarget == null) {
            this.setTitle(bundle.getString("dialog.ms.title"));
        } else {
            this.setTitle(bundle.getString("dialog.ms.titleEdit") + " " + editableTarget.getDisplayName());
        }

        this.setSize(DeepSkyTargetMSDialog.serialVersionUID, 750, 407);

        this.pack();
        
        this.setVisible(true);

    }

    @Override
    public ITarget getTarget() {

        if (this.schemaElement != null) {
            return (ITarget) this.schemaElement;
        }

        return null;

    }

}
