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
import de.lehmannet.om.ui.dialog.AbstractDialog;
import de.lehmannet.om.ui.dialog.ITargetDialog;
import de.lehmannet.om.ui.extension.solarSystem.panel.SolarSystemTargetPlanetPanel;
import de.lehmannet.om.ui.navigation.ObservationManager;

public class SolarSystemTargetPlanetDialog extends AbstractDialog implements ITargetDialog {

    private static final long serialVersionUID = -1824120647459055098L;
    private final PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
            .getBundle("de.lehmannet.om.ui.extension.solarSystem.SolarSystem", Locale.getDefault());

    public SolarSystemTargetPlanetDialog(ObservationManager om, ITarget editableTarget, IObservation o,
            Boolean editable) {

        super(om, new SolarSystemTargetPlanetPanel(om, editableTarget, o, new Boolean(true)));

        if (editableTarget == null) {
            super.setTitle(this.bundle.getString("dialog.planet.title"));
        } else {
            super.setTitle(this.bundle.getString("dialog.planet.titleEdit") + " " + editableTarget.getDisplayName());
        }

        super.setSize(SolarSystemTargetPlanetDialog.serialVersionUID, 550, 260);

        // super.pack();
        super.setVisible(true);

    }

    @Override
    public ITarget getTarget() {

        if (super.schemaElement != null) {
            return (ITarget) super.schemaElement;
        }

        return null;

    }

}
