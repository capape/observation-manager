/*
 * ====================================================================
 * /extension/deepSky/dialog/DeepSkyTargetGXDialog.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.deepSky.dialog;

import de.lehmannet.om.ITarget;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.dialog.AbstractDialog;
import de.lehmannet.om.ui.dialog.ITargetDialog;
import de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetGXPanel;
import de.lehmannet.om.ui.util.UserInterfaceHelper;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.JFrame;

public class DeepSkyTargetGXDialog extends AbstractDialog implements ITargetDialog {

    private static final long serialVersionUID = 3077406142342775238L;

    public DeepSkyTargetGXDialog(
            JFrame om, UserInterfaceHelper uiHelper, ObservationManagerModel model, ITarget editableTarget) {

        super(om, model, uiHelper, new DeepSkyTargetGXPanel(uiHelper, model, editableTarget, Boolean.TRUE));

        ResourceBundle bundle =
                ResourceBundle.getBundle("de.lehmannet.om.ui.extension.deepSky.DeepSky", Locale.getDefault());
        if (editableTarget == null) {
            this.setTitle(bundle.getString("dialog.gx.title"));
        } else {
            this.setTitle(bundle.getString("dialog.gx.titleEdit") + " " + editableTarget.getDisplayName());
        }

        this.setSize(DeepSkyTargetGXDialog.serialVersionUID, 575, 575);

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
