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
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.dialog.AbstractDialog;
import de.lehmannet.om.ui.dialog.ITargetDialog;
import de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetDNPanel;
import de.lehmannet.om.ui.navigation.ObservationManager;

public class DeepSkyTargetDNDialog extends AbstractDialog implements ITargetDialog {

    private static final long serialVersionUID = -6833056944401369012L;

    public DeepSkyTargetDNDialog(ObservationManager om, ObservationManagerModel model, ITarget editableTarget) {

        super(om, new DeepSkyTargetDNPanel(om, model, editableTarget, Boolean.TRUE));

        PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
                .getBundle("de.lehmannet.om.ui.extension.deepSky.DeepSky", Locale.getDefault());
        if (editableTarget == null) {
            this.setTitle(bundle.getString("dialog.dn.title"));
        } else {
            this.setTitle(bundle.getString("dialog.dn.titleEdit") + " " + editableTarget.getDisplayName());
        }

        this.setSize(DeepSkyTargetDNDialog.serialVersionUID, 575, 360);

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
