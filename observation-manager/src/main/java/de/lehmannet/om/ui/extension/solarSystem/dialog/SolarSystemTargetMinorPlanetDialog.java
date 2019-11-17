/* ====================================================================
 * /extension/solarSystem/dialog/SolarSystemTargetMinorPlanetDialog.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.solarSystem.dialog;

import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import de.lehmannet.om.ITarget;
import de.lehmannet.om.ui.dialog.AbstractDialog;
import de.lehmannet.om.ui.dialog.ITargetDialog;
import de.lehmannet.om.ui.extension.solarSystem.panel.SolarSystemTargetMinorPlanetPanel;
import de.lehmannet.om.ui.navigation.ObservationManager;

public class SolarSystemTargetMinorPlanetDialog extends AbstractDialog implements ITargetDialog {

    private static final long serialVersionUID = -4406827373886902739L;

    public SolarSystemTargetMinorPlanetDialog(ObservationManager om, ITarget editableTarget) {

        super(om, new SolarSystemTargetMinorPlanetPanel(om, editableTarget, Boolean.TRUE));

        PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
                .getBundle("de.lehmannet.om.ui.extension.solarSystem.SolarSystem", Locale.getDefault());
        if (editableTarget == null) {
            super.setTitle(bundle.getString("dialog.minor.title"));
        } else {
            super.setTitle(bundle.getString("dialog.minor.titleEdit") + " " + editableTarget.getDisplayName());
        }

        super.setSize(SolarSystemTargetMinorPlanetDialog.serialVersionUID, 550, 260);

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
