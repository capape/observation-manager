/* ====================================================================
 * /extension/deepSky/dialog/DeepSkyTargetOCDialog.java
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
import de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetOCPanel;
import de.lehmannet.om.ui.navigation.ObservationManager;

public class DeepSkyTargetOCDialog extends AbstractDialog implements ITargetDialog {

    private static final long serialVersionUID = -3679948772002490835L;
    private final PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
            .getBundle("de.lehmannet.om.ui.extension.deepSky.DeepSky", Locale.getDefault());

    public DeepSkyTargetOCDialog(ObservationManager om, ITarget editableTarget) {

        super(om, new DeepSkyTargetOCPanel(om, editableTarget, new Boolean(true)));

        if (editableTarget == null) {
            super.setTitle(this.bundle.getString("dialog.oc.title"));
        } else {
            super.setTitle(this.bundle.getString("dialog.oc.titleEdit") + " " + editableTarget.getDisplayName());
        }

        super.setSize(DeepSkyTargetOCDialog.serialVersionUID, 575, 385);

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
