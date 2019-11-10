/* ====================================================================
 * /extension/deepSky/dialog/DeepSkyTargetNADialog.java
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
import de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetNAPanel;
import de.lehmannet.om.ui.navigation.ObservationManager;

public class DeepSkyTargetNADialog extends AbstractDialog implements ITargetDialog {

    private static final long serialVersionUID = -1996388086618176126L;
    private final PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
            .getBundle("de.lehmannet.om.ui.extension.deepSky.DeepSky", Locale.getDefault());

    public DeepSkyTargetNADialog(ObservationManager om, ITarget editableTarget) {

        super(om, new DeepSkyTargetNAPanel(om, editableTarget, new Boolean(true)));

        if (editableTarget == null) {
            super.setTitle(this.bundle.getString("dialog.na.title"));
        } else {
            super.setTitle(this.bundle.getString("dialog.na.titleEdit") + " " + editableTarget.getDisplayName());
        }

        super.setSize(DeepSkyTargetNADialog.serialVersionUID, 590, 330);

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
