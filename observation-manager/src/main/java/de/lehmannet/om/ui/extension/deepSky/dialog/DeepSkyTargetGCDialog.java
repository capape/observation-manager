/* ====================================================================
 * /extension/deepSky/dialog/DeepSkyTargetGCDialog.java
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
import de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetGCPanel;
import de.lehmannet.om.ui.navigation.ObservationManager;

public class DeepSkyTargetGCDialog extends AbstractDialog implements ITargetDialog {

    private static final long serialVersionUID = -7217690597304275686L;
    private final PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
            .getBundle("de.lehmannet.om.ui.extension.deepSky.DeepSky", Locale.getDefault());

    public DeepSkyTargetGCDialog(ObservationManager om, ITarget editableTarget) {

        super(om, new DeepSkyTargetGCPanel(om, editableTarget, new Boolean(true)));

        if (editableTarget == null) {
            super.setTitle(this.bundle.getString("dialog.gc.title"));
        } else {
            super.setTitle(this.bundle.getString("dialog.gc.titleEdit") + " " + editableTarget.getDisplayName());
        }

        super.setSize(DeepSkyTargetGCDialog.serialVersionUID, 575, 360);

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
