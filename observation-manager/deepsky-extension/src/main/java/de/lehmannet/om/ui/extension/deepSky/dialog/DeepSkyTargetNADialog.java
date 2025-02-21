/*
 * ====================================================================
 * /extension/deepSky/dialog/DeepSkyTargetNADialog.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.deepSky.dialog;

import de.lehmannet.om.ITarget;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.dialog.AbstractDialog;
import de.lehmannet.om.ui.dialog.ITargetDialog;
import de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetNAPanel;
import de.lehmannet.om.ui.util.UserInterfaceHelper;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.JFrame;

public class DeepSkyTargetNADialog extends AbstractDialog implements ITargetDialog {

    private static final long serialVersionUID = -1996388086618176126L;

    public DeepSkyTargetNADialog(
            JFrame om, UserInterfaceHelper uiHelper, ObservationManagerModel model, ITarget editableTarget) {

        super(om, model, uiHelper, new DeepSkyTargetNAPanel(uiHelper, model, editableTarget, Boolean.TRUE));

        ResourceBundle bundle =
                ResourceBundle.getBundle("de.lehmannet.om.ui.extension.deepSky.DeepSky", Locale.getDefault());
        if (editableTarget == null) {
            this.setTitle(bundle.getString("dialog.na.title"));
        } else {
            this.setTitle(bundle.getString("dialog.na.titleEdit") + " " + editableTarget.getDisplayName());
        }

        this.setSize(DeepSkyTargetNADialog.serialVersionUID, 590, 575);

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
