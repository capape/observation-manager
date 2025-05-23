/*
 * ====================================================================
 * /extension/solarSystem/dialog/SolarSystemTargetPlanetDialog.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.solarSystem.dialog;

import de.lehmannet.om.IObservation;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.dialog.AbstractDialog;
import de.lehmannet.om.ui.dialog.ITargetDialog;
import de.lehmannet.om.ui.extension.solarSystem.panel.SolarSystemTargetPlanetPanel;
import de.lehmannet.om.ui.util.UserInterfaceHelper;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.JFrame;

public class SolarSystemTargetPlanetDialog extends AbstractDialog implements ITargetDialog {

    private static final long serialVersionUID = -1824120647459055098L;

    public SolarSystemTargetPlanetDialog(
            JFrame om,
            UserInterfaceHelper uiHelper,
            ObservationManagerModel model,
            ITarget target,
            IObservation observation) {

        super(
                om,
                model,
                uiHelper,
                new SolarSystemTargetPlanetPanel(model.getConfiguration(), model, target, observation, Boolean.TRUE));

        ResourceBundle bundle =
                ResourceBundle.getBundle("de.lehmannet.om.ui.extension.solarSystem.SolarSystem", Locale.getDefault());
        if (target == null) {
            this.setTitle(bundle.getString("dialog.planet.title"));
        } else {
            this.setTitle(bundle.getString("dialog.planet.titleEdit") + " " + target.getDisplayName());
        }

        this.setSize(SolarSystemTargetPlanetDialog.serialVersionUID, 550, 575);

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
