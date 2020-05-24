/* ====================================================================
 * /extension/deepSky/dialog/DeepSkyTargetDSDialog.java
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
import de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetDSPanel;
import de.lehmannet.om.ui.navigation.ObservationManager;

public class DeepSkyTargetDSDialog extends AbstractDialog implements ITargetDialog {

    private static final long serialVersionUID = -3497916303476127451L;

    public DeepSkyTargetDSDialog(ObservationManager om, ObservationManagerModel model,ITarget editableTarget) {

        super(om, new DeepSkyTargetDSPanel(om, model, editableTarget, Boolean.TRUE));

        PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
                .getBundle("de.lehmannet.om.ui.extension.deepSky.DeepSky", Locale.getDefault());
        if (editableTarget == null) {
            this.setTitle(bundle.getString("dialog.ds.title"));
        } else {
            this.setTitle(bundle.getString("dialog.ds.titleEdit") + " " + editableTarget.getDisplayName());
        }

        this.setSize(DeepSkyTargetDSDialog.serialVersionUID, 620, 385);

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
