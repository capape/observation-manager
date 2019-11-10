/* ====================================================================
 * /extension/deepSky/dialog/DeepSkyTargetASDialog.java
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
import de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetASPanel;
import de.lehmannet.om.ui.navigation.ObservationManager;

public class DeepSkyTargetASDialog extends AbstractDialog implements ITargetDialog {

    private static final long serialVersionUID = -5853208729049643261L;
    private final PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
            .getBundle("de.lehmannet.om.ui.extension.deepSky.DeepSky", Locale.getDefault());

    public DeepSkyTargetASDialog(ObservationManager om, ITarget editableTarget) {

        super(om, new DeepSkyTargetASPanel(om, editableTarget, new Boolean(true)));

        if (editableTarget == null) {
            super.setTitle(this.bundle.getString("dialog.as.title"));
        } else {
            super.setTitle(this.bundle.getString("dialog.as.titleEdit") + " " + editableTarget.getDisplayName());
        }

        super.setSize(DeepSkyTargetASDialog.serialVersionUID, 575, 365);

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
