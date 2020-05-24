/* ====================================================================
 * /extension/solarSystem/dialog/SolarSystemTargetPlanetDialog.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.solarSystem.dialog;

import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import de.lehmannet.om.IObservation;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.dialog.AbstractDialog;
import de.lehmannet.om.ui.dialog.ITargetDialog;
import de.lehmannet.om.ui.extension.solarSystem.panel.SolarSystemTargetPlanetPanel;
import de.lehmannet.om.ui.navigation.ObservationManager;

public class SolarSystemTargetPlanetDialog extends AbstractDialog implements ITargetDialog {

    private static final long serialVersionUID = -1824120647459055098L;

    public SolarSystemTargetPlanetDialog(ObservationManager om, ObservationManagerModel model, ITarget editableTarget, IObservation o) {

        super(om, new SolarSystemTargetPlanetPanel(om, model, editableTarget, o, Boolean.TRUE));

        PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
                .getBundle("de.lehmannet.om.ui.extension.solarSystem.SolarSystem", Locale.getDefault());
        if (editableTarget == null) {
            this.setTitle(bundle.getString("dialog.planet.title"));
        } else {
            this.setTitle(bundle.getString("dialog.planet.titleEdit") + " " + editableTarget.getDisplayName());
        }

        this.setSize(SolarSystemTargetPlanetDialog.serialVersionUID, 550, 260);

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
