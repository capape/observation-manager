/*
 * ====================================================================
 * /extension/solarSystem/dialog/SolarSystemTargetCometDialog.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.solarSystem.dialog;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JFrame;

import de.lehmannet.om.ITarget;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.dialog.AbstractDialog;
import de.lehmannet.om.ui.dialog.ITargetDialog;
import de.lehmannet.om.ui.extension.solarSystem.panel.SolarSystemTargetCometPanel;
import de.lehmannet.om.ui.util.UserInterfaceHelper;

public class SolarSystemTargetCometDialog extends AbstractDialog implements ITargetDialog {

    private static final long serialVersionUID = -4143755484166642959L;

    public SolarSystemTargetCometDialog(JFrame om, UserInterfaceHelper uiHelper, ObservationManagerModel model,
            ITarget editableTarget) {

        super(om, model, uiHelper,
                new SolarSystemTargetCometPanel(model.getConfiguration(), model, editableTarget, Boolean.TRUE));

        ResourceBundle bundle = ResourceBundle.getBundle("de.lehmannet.om.ui.extension.solarSystem.SolarSystem",
                Locale.getDefault());
        if (editableTarget == null) {
            this.setTitle(bundle.getString("dialog.comet.title"));
        } else {
            this.setTitle(bundle.getString("dialog.comet.titleEdit") + " " + editableTarget.getDisplayName());
        }

        this.setSize(SolarSystemTargetCometDialog.serialVersionUID, 550, 575);

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
