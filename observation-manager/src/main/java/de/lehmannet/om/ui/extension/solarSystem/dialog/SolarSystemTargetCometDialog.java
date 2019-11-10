/* ====================================================================
 * /extension/solarSystem/dialog/SolarSystemTargetCometDialog.java
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
import de.lehmannet.om.ui.extension.solarSystem.panel.SolarSystemTargetCometPanel;
import de.lehmannet.om.ui.navigation.ObservationManager;

public class SolarSystemTargetCometDialog extends AbstractDialog implements ITargetDialog {

    private static final long serialVersionUID = -4143755484166642959L;
    private final PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
            .getBundle("de.lehmannet.om.ui.extension.solarSystem.SolarSystem", Locale.getDefault());

    public SolarSystemTargetCometDialog(ObservationManager om, ITarget editableTarget) {

        super(om, new SolarSystemTargetCometPanel(om, editableTarget, new Boolean(true)));

        if (editableTarget == null) {
            super.setTitle(this.bundle.getString("dialog.comet.title"));
        } else {
            super.setTitle(this.bundle.getString("dialog.comet.titleEdit") + " " + editableTarget.getDisplayName());
        }

        super.setSize(SolarSystemTargetCometDialog.serialVersionUID, 550, 260);

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
