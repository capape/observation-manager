/* ====================================================================
 * /extension/solarSystem/dialog/SolarSystemTargetMinorPlanetDialog.java
 * 
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
import de.lehmannet.om.ui.extension.solarSystem.panel.SolarSystemTargetMinorPlanetPanel;
import de.lehmannet.om.ui.util.UserInterfaceHelper;

public class SolarSystemTargetMinorPlanetDialog extends AbstractDialog implements ITargetDialog {

    private static final long serialVersionUID = -4406827373886902739L;

    public SolarSystemTargetMinorPlanetDialog(JFrame om, UserInterfaceHelper uiHelper, ObservationManagerModel model,
            ITarget editableTarget) {

        super(om, model, uiHelper,
                new SolarSystemTargetMinorPlanetPanel(model.getConfiguration(), model, editableTarget, Boolean.TRUE));

        ResourceBundle bundle = ResourceBundle.getBundle("de.lehmannet.om.ui.extension.solarSystem.SolarSystem",
                Locale.getDefault());
        if (editableTarget == null) {
            this.setTitle(bundle.getString("dialog.minor.title"));
        } else {
            this.setTitle(bundle.getString("dialog.minor.titleEdit") + " " + editableTarget.getDisplayName());
        }

        this.setSize(SolarSystemTargetMinorPlanetDialog.serialVersionUID, 550, 260);

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
