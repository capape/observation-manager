/*
 * ====================================================================
 * /extension/deepSky/dialog/DeepSkyTargetPGDialog.java
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
import de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetCGPanel;
import de.lehmannet.om.ui.util.UserInterfaceHelper;

public class DeepSkyTargetCGDialog extends AbstractDialog implements ITargetDialog {

    private static final long serialVersionUID = -4936737277950814027L;

    public DeepSkyTargetCGDialog(JFrame om, UserInterfaceHelper uiHelper, ObservationManagerModel model,
            ITarget editableTarget) {

        super(om, model, uiHelper,
                new DeepSkyTargetCGPanel(model.getConfiguration(), uiHelper, model, editableTarget, Boolean.TRUE));

        ResourceBundle bundle = ResourceBundle.getBundle("de.lehmannet.om.ui.extension.deepSky.DeepSky",
                Locale.getDefault());
        if (editableTarget == null) {
            this.setTitle(bundle.getString("dialog.cg.title"));
        } else {
            this.setTitle(bundle.getString("dialog.cg.titleEdit") + " " + editableTarget.getDisplayName());
        }

        this.setSize(DeepSkyTargetCGDialog.serialVersionUID, 575, 575);

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
