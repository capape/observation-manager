/*
 * ====================================================================
 * /extension/deepSky/dialog/DeepSkyTargetSCDialog.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.deepSky.dialog;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JFrame;

import de.lehmannet.om.ITarget;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.dialog.AbstractDialog;
import de.lehmannet.om.ui.dialog.ITargetDialog;
import de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetSCPanel;
import de.lehmannet.om.ui.util.UserInterfaceHelper;

public class DeepSkyTargetSCDialog extends AbstractDialog implements ITargetDialog {

    private static final long serialVersionUID = -714136627680620087L;

    public DeepSkyTargetSCDialog(JFrame om, UserInterfaceHelper uiHelper, ObservationManagerModel model,
            ITarget editableTarget) {

        super(om, model, uiHelper, new DeepSkyTargetSCPanel(uiHelper, model, editableTarget, Boolean.TRUE));

        ResourceBundle bundle = ResourceBundle.getBundle("de.lehmannet.om.ui.extension.deepSky.DeepSky",
                Locale.getDefault());
        if (editableTarget == null) {
            this.setTitle(bundle.getString("dialog.sc.title"));
        } else {
            this.setTitle(bundle.getString("dialog.sc.titleEdit") + " " + editableTarget.getDisplayName());
        }

        this.setSize(DeepSkyTargetSCDialog.serialVersionUID, 620, 365);

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
