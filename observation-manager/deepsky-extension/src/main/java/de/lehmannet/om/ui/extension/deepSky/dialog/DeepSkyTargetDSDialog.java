/*
 * ====================================================================
 * /extension/deepSky/dialog/DeepSkyTargetDSDialog.java
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
import de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetDSPanel;
import de.lehmannet.om.ui.util.UserInterfaceHelper;

public class DeepSkyTargetDSDialog extends AbstractDialog implements ITargetDialog {

    private static final long serialVersionUID = -3497916303476127451L;

    public DeepSkyTargetDSDialog(JFrame om, UserInterfaceHelper uiHelper, ObservationManagerModel model,
            ITarget editableTarget) {

        super(om, model, uiHelper, new DeepSkyTargetDSPanel(uiHelper, model, editableTarget, Boolean.TRUE));

        ResourceBundle bundle = ResourceBundle.getBundle("de.lehmannet.om.ui.extension.deepSky.DeepSky",
                Locale.getDefault());
        if (editableTarget == null) {
            this.setTitle(bundle.getString("dialog.ds.title"));
        } else {
            this.setTitle(bundle.getString("dialog.ds.titleEdit") + " " + editableTarget.getDisplayName());
        }

        this.setSize(DeepSkyTargetDSDialog.serialVersionUID, 620, 385);

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
