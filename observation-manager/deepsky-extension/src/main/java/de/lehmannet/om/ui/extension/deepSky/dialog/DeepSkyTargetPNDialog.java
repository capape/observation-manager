/*
 * ====================================================================
 * /extension/deepSky/dialog/DeepSkyTargetPNDialog.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.deepSky.dialog;

import de.lehmannet.om.ITarget;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.dialog.AbstractDialog;
import de.lehmannet.om.ui.dialog.ITargetDialog;
import de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetPNPanel;
import de.lehmannet.om.ui.util.UserInterfaceHelper;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.JFrame;

public class DeepSkyTargetPNDialog extends AbstractDialog implements ITargetDialog {

    private static final long serialVersionUID = 5570306211051069777L;

    public DeepSkyTargetPNDialog(
            JFrame om, UserInterfaceHelper uiHelper, ObservationManagerModel model, ITarget editableTarget) {

        super(om, model, uiHelper, new DeepSkyTargetPNPanel(uiHelper, model, editableTarget, Boolean.TRUE));

        ResourceBundle bundle =
                ResourceBundle.getBundle("de.lehmannet.om.ui.extension.deepSky.DeepSky", Locale.getDefault());
        if (editableTarget == null) {
            this.setTitle(bundle.getString("dialog.pn.title"));
        } else {
            this.setTitle(bundle.getString("dialog.pn.titleEdit") + " " + editableTarget.getDisplayName());
        }

        this.setSize(DeepSkyTargetPNDialog.serialVersionUID, 575, 575);

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
