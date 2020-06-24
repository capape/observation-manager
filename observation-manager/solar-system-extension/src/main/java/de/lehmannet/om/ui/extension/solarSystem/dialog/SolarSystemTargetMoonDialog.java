/* ====================================================================
 * /extension/solarSystem/dialog/SolarSystemTargetMoonDialog.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.solarSystem.dialog;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.ResourceBundle;

import javax.swing.JFrame;

import de.lehmannet.om.IObservation;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.dialog.AbstractDialog;
import de.lehmannet.om.ui.dialog.ITargetDialog;
import de.lehmannet.om.ui.extension.solarSystem.panel.SolarSystemTargetMoonPanel;
import de.lehmannet.om.ui.util.UserInterfaceHelper;

public class SolarSystemTargetMoonDialog extends AbstractDialog implements ITargetDialog {

    private static final long serialVersionUID = 11451630089774356L;

    public SolarSystemTargetMoonDialog(JFrame om, UserInterfaceHelper uiHelper, ObservationManagerModel model,
            IObservation o, ITarget target) {

        super(om, model, uiHelper,
                new SolarSystemTargetMoonPanel(model.getConfiguration(), model, target, o, Boolean.TRUE));

        ResourceBundle bundle = ResourceBundle.getBundle("de.lehmannet.om.ui.extension.solarSystem.SolarSystem",
                Locale.getDefault());
        if (target == null) {
            this.setTitle(bundle.getString("dialog.moon.title"));
        } else {
            this.setTitle(bundle.getString("dialog.moon.titleEdit") + " " + target.getDisplayName());
        }

        this.setSize(SolarSystemTargetMoonDialog.serialVersionUID, 550, 260);

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
