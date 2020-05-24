/* ====================================================================
 * /extension/deepSky/dialog/DeepSkyTargetPGDialog.java
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
import de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetCGPanel;
import de.lehmannet.om.ui.navigation.ObservationManager;

public class DeepSkyTargetCGDialog extends AbstractDialog implements ITargetDialog {

    private static final long serialVersionUID = -4936737277950814027L;

    public DeepSkyTargetCGDialog(ObservationManager om, ObservationManagerModel model, ITarget editableTarget) {

        super(om, new DeepSkyTargetCGPanel(om, model, editableTarget, Boolean.TRUE));

        PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
                .getBundle("de.lehmannet.om.ui.extension.deepSky.DeepSky", Locale.getDefault());
        if (editableTarget == null) {
            this.setTitle(bundle.getString("dialog.cg.title"));
        } else {
            this.setTitle(bundle.getString("dialog.cg.titleEdit") + " " + editableTarget.getDisplayName());
        }

        this.setSize(DeepSkyTargetCGDialog.serialVersionUID, 575, 360);

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
