/* ====================================================================
 * /extension/solarSystem/dialog/SolarSystemTargetSunDialog.java
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
import de.lehmannet.om.ui.extension.solarSystem.panel.SolarSystemTargetSunPanel;
import de.lehmannet.om.ui.navigation.ObservationManager;

public class SolarSystemTargetSunDialog extends AbstractDialog implements ITargetDialog {

    private static final long serialVersionUID = -8081855396371032209L;

    public SolarSystemTargetSunDialog(ObservationManager om, IObservation o, ITarget editableTarget) {

        super(om, new SolarSystemTargetSunPanel(om, editableTarget, o, Boolean.TRUE));

        PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
                .getBundle("de.lehmannet.om.ui.extension.solarSystem.SolarSystem", Locale.getDefault());
        if (editableTarget == null) {
            super.setTitle(bundle.getString("dialog.sun.title"));
        } else {
            super.setTitle(bundle.getString("dialog.sun.titleEdit") + " " + editableTarget.getDisplayName());
        }

        super.setSize(SolarSystemTargetSunDialog.serialVersionUID, 550, 260);
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
