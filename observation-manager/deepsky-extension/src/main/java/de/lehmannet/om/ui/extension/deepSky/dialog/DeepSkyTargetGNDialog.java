/*
 * ====================================================================
 * /extension/deepSky/dialog/DeepSkyTargetGNDialog.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.deepSky.dialog;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JFrame;

import de.lehmannet.om.ITarget;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetGN;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.dialog.AbstractDialog;
import de.lehmannet.om.ui.dialog.ITargetDialog;
import de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetGNPanel;
import de.lehmannet.om.ui.util.UserInterfaceHelper;

public class DeepSkyTargetGNDialog extends AbstractDialog implements ITargetDialog {

    private static final long serialVersionUID = 3434636227305354938L;

    public DeepSkyTargetGNDialog(JFrame om, UserInterfaceHelper uiHelper, ObservationManagerModel model,
            DeepSkyTargetGN editableTarget) {

        super(om, model, uiHelper, new DeepSkyTargetGNPanel(uiHelper, model, editableTarget, Boolean.TRUE));

        ResourceBundle bundle = ResourceBundle.getBundle("de.lehmannet.om.ui.extension.deepSky.DeepSky",
                Locale.getDefault());
        if (editableTarget == null) {
            this.setTitle(bundle.getString("dialog.gn.title"));
        } else {
            this.setTitle(bundle.getString("dialog.gn.titleEdit") + " " + editableTarget.getDisplayName());
        }

        this.setSize(DeepSkyTargetGNDialog.serialVersionUID, 575, 360);

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
