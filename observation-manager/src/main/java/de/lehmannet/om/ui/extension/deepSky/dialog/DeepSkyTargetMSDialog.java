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

    public DeepSkyTargetMSDialog(ObservationManager om, ITarget editableTarget) {

        super(om, new DeepSkyTargetMSPanel(om, editableTarget, Boolean.TRUE));

        PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
                .getBundle("de.lehmannet.om.ui.extension.deepSky.DeepSky", Locale.getDefault());
        if (editableTarget == null) {
            super.setTitle(bundle.getString("dialog.ms.title"));
        } else {
            super.setTitle(bundle.getString("dialog.ms.titleEdit") + " " + editableTarget.getDisplayName());
        }

        super.setSize(DeepSkyTargetMSDialog.serialVersionUID, 750, 407);

        super.setVisible(true);

    }

    @Override
    public ITarget getTarget() {

        if (super.schemaElement != null) {
            return (ITarget) super.schemaElement;
        }

        return null;

    }

}
