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
import de.lehmannet.om.ui.dialog.AbstractDialog;
import de.lehmannet.om.ui.dialog.ITargetDialog;
import de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetDSPanel;
import de.lehmannet.om.ui.navigation.ObservationManager;

public class DeepSkyTargetDSDialog extends AbstractDialog implements ITargetDialog {

    private static final long serialVersionUID = -3497916303476127451L;

    public DeepSkyTargetDSDialog(ObservationManager om, ITarget editableTarget) {

        super(om, new DeepSkyTargetDSPanel(om, editableTarget, Boolean.TRUE));

        PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
                .getBundle("de.lehmannet.om.ui.extension.deepSky.DeepSky", Locale.getDefault());
        if (editableTarget == null) {
            super.setTitle(bundle.getString("dialog.ds.title"));
        } else {
            super.setTitle(bundle.getString("dialog.ds.titleEdit") + " " + editableTarget.getDisplayName());
        }

        super.setSize(DeepSkyTargetDSDialog.serialVersionUID, 620, 385);

        // super.pack();
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
