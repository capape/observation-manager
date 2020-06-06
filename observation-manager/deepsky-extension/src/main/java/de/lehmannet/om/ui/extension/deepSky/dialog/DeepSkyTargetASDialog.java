/* ====================================================================
 * /extension/deepSky/dialog/DeepSkyTargetASDialog.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.deepSky.dialog;

import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.JFrame;

import de.lehmannet.om.ITarget;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.dialog.AbstractDialog;
import de.lehmannet.om.ui.dialog.ITargetDialog;
import de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetASPanel;
import de.lehmannet.om.ui.util.UserInterfaceHelper;

public class DeepSkyTargetASDialog extends AbstractDialog implements ITargetDialog {

    private static final long serialVersionUID = -5853208729049643261L;

    public DeepSkyTargetASDialog(JFrame om, UserInterfaceHelper uiHelper, ObservationManagerModel model,
            ITarget editableTarget) {

        super(om, model, uiHelper, new DeepSkyTargetASPanel(uiHelper, model, editableTarget, Boolean.TRUE));

        PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
                .getBundle("de.lehmannet.om.ui.extension.deepSky.DeepSky", Locale.getDefault());
        if (editableTarget == null) {
            this.setTitle(bundle.getString("dialog.as.title"));
        } else {
            this.setTitle(bundle.getString("dialog.as.titleEdit") + " " + editableTarget.getDisplayName());
        }

        this.setSize(DeepSkyTargetASDialog.serialVersionUID, 575, 365);

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
