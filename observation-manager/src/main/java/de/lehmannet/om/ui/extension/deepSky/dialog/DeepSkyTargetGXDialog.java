/* ====================================================================
 * /extension/deepSky/dialog/DeepSkyTargetGXDialog.java
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
import de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetGXPanel;
import de.lehmannet.om.ui.navigation.ObservationManager;

public class DeepSkyTargetGXDialog extends AbstractDialog implements ITargetDialog {

    private static final long serialVersionUID = 3077406142342775238L;

    public DeepSkyTargetGXDialog(ObservationManager om, ITarget editableTarget) {

        super(om, new DeepSkyTargetGXPanel(om, editableTarget, Boolean.TRUE));

        PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
                .getBundle("de.lehmannet.om.ui.extension.deepSky.DeepSky", Locale.getDefault());
        if (editableTarget == null) {
            super.setTitle(bundle.getString("dialog.gx.title"));
        } else {
            super.setTitle(bundle.getString("dialog.gx.titleEdit") + " " + editableTarget.getDisplayName());
        }

        super.setSize(DeepSkyTargetGXDialog.serialVersionUID, 575, 360);

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
