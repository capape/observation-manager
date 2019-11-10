/* ====================================================================
 * /extension/deepSky/dialog/DeepSkyTargetDNDialog.java
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
import de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetDNPanel;
import de.lehmannet.om.ui.navigation.ObservationManager;

public class DeepSkyTargetDNDialog extends AbstractDialog implements ITargetDialog {

    private static final long serialVersionUID = -6833056944401369012L;
    private final PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
            .getBundle("de.lehmannet.om.ui.extension.deepSky.DeepSky", Locale.getDefault());

    public DeepSkyTargetDNDialog(ObservationManager om, ITarget editableTarget) {

        super(om, new DeepSkyTargetDNPanel(om, editableTarget, new Boolean(true)));

        if (editableTarget == null) {
            super.setTitle(this.bundle.getString("dialog.dn.title"));
        } else {
            super.setTitle(this.bundle.getString("dialog.dn.titleEdit") + " " + editableTarget.getDisplayName());
        }

        super.setSize(DeepSkyTargetDNDialog.serialVersionUID, 575, 360);

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
