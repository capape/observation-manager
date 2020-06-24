/* ====================================================================
 * /dialog/TargetStarDialog.java
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
import de.lehmannet.om.ui.panel.TargetStarPanel;
import de.lehmannet.om.ui.util.LocaleToolsFactory;
import de.lehmannet.om.ui.util.UserInterfaceHelper;

public class TargetStarDialog extends AbstractDialog implements ITargetDialog {

    private static final long serialVersionUID = -923728327119653756L;

    public TargetStarDialog(JFrame om, UserInterfaceHelper uiHelper, ObservationManagerModel model,
            ITarget editableTarget) {

        super(om, model, uiHelper, new TargetStarPanel(model.getConfiguration(), model, editableTarget, Boolean.TRUE));

        ResourceBundle bundle = LocaleToolsFactory.appInstance().getBundle("ObservationManager", Locale.getDefault());
        if (editableTarget == null) {
            this.setTitle(bundle.getString("dialog.targetStar.title"));
        } else {
            this.setTitle(bundle.getString("dialog.targetStar.titleEdit") + " " + editableTarget.getDisplayName());
        }

        this.setSize(TargetStarDialog.serialVersionUID, 590, 313);

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
