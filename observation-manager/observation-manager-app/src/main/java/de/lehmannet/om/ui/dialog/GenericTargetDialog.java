/* ====================================================================
 * /dialog/GenericTargetDialog.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.dialog;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.ResourceBundle;

import javax.swing.JFrame;

import de.lehmannet.om.ITarget;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.panel.GenericTargetPanel;
import de.lehmannet.om.ui.util.IConfiguration;
import de.lehmannet.om.ui.util.LocaleToolsFactory;
import de.lehmannet.om.ui.util.UserInterfaceHelper;

public class GenericTargetDialog extends AbstractDialog implements ITargetDialog {

    private static final long serialVersionUID = -8858493947135823299L;

    public GenericTargetDialog(JFrame om, IConfiguration configuration, UserInterfaceHelper uiHelper,
            ObservationManagerModel model, ITarget editableTarget) {

        super(om, model, uiHelper, new GenericTargetPanel(configuration, model, editableTarget, Boolean.TRUE));

        ResourceBundle bundle = LocaleToolsFactory.appInstance().getBundle("ObservationManager", Locale.getDefault());
        if (editableTarget == null) {
            this.setTitle(bundle.getString("dialog.genericTarget.title"));
        } else {
            this.setTitle(bundle.getString("dialog.genericTarget.titleEdit") + " " + editableTarget.getDisplayName());
        }

        this.setSize(GenericTargetDialog.serialVersionUID, 590, 260);

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
