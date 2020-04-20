/* ====================================================================
 * /extension/deepSky/dialog/DeepSkyTargetQSDialog.java
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
import de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetQSPanel;
import de.lehmannet.om.ui.navigation.ObservationManager;

public class DeepSkyTargetQSDialog extends AbstractDialog implements ITargetDialog {

    private static final long serialVersionUID = -5112331874887900986L;

    public DeepSkyTargetQSDialog(ObservationManager om, ITarget editableTarget) {

        super(om, new DeepSkyTargetQSPanel(om, editableTarget, Boolean.TRUE));

        PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
                .getBundle("de.lehmannet.om.ui.extension.deepSky.DeepSky", Locale.getDefault());
        if (editableTarget == null) {
            this.setTitle(bundle.getString("dialog.qs.title"));
        } else {
            this.setTitle(bundle.getString("dialog.qs.titleEdit") + " " + editableTarget.getDisplayName());
        }

        this.setSize(DeepSkyTargetQSDialog.serialVersionUID, 590, 330);

        // this.pack();
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
