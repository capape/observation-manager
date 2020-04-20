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

    public DeepSkyTargetNADialog(ObservationManager om, ITarget editableTarget) {

        super(om, new DeepSkyTargetNAPanel(om, editableTarget, Boolean.TRUE));

        PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
                .getBundle("de.lehmannet.om.ui.extension.deepSky.DeepSky", Locale.getDefault());
        if (editableTarget == null) {
            this.setTitle(bundle.getString("dialog.na.title"));
        } else {
            this.setTitle(bundle.getString("dialog.na.titleEdit") + " " + editableTarget.getDisplayName());
        }

        this.setSize(DeepSkyTargetNADialog.serialVersionUID, 590, 330);

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
