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
            this.setTitle(bundle.getString("dialog.gx.title"));
        } else {
            this.setTitle(bundle.getString("dialog.gx.titleEdit") + " " + editableTarget.getDisplayName());
        }

        this.setSize(DeepSkyTargetGXDialog.serialVersionUID, 575, 360);

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
