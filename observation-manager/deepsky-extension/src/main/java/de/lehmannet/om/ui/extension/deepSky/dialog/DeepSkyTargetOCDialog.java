/*
 * ====================================================================
 * /extension/deepSky/dialog/DeepSkyTargetOCDialog.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.deepSky.dialog;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JFrame;

import de.lehmannet.om.ITarget;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetOC;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.dialog.AbstractDialog;
import de.lehmannet.om.ui.dialog.ITargetDialog;
import de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetOCPanel;
import de.lehmannet.om.ui.util.UserInterfaceHelper;

public class DeepSkyTargetOCDialog extends AbstractDialog implements ITargetDialog {

    private static final long serialVersionUID = -3679948772002490835L;

    public DeepSkyTargetOCDialog(JFrame om, UserInterfaceHelper uiHelper, ObservationManagerModel model,
            DeepSkyTargetOC editableTarget) {

        super(om, model, uiHelper, new DeepSkyTargetOCPanel(uiHelper, model, editableTarget, Boolean.TRUE));

        ResourceBundle bundle = ResourceBundle.getBundle("de.lehmannet.om.ui.extension.deepSky.DeepSky",
                Locale.getDefault());
        if (editableTarget == null) {
            this.setTitle(bundle.getString("dialog.oc.title"));
        } else {
            this.setTitle(bundle.getString("dialog.oc.titleEdit") + " " + editableTarget.getDisplayName());
        }

        this.setSize(DeepSkyTargetOCDialog.serialVersionUID, 575, 385);

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
