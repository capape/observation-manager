/* ====================================================================
 * /dialog/TargetStarDialog.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.dialog;

import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import de.lehmannet.om.ITarget;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.panel.TargetStarPanel;

public class TargetStarDialog extends AbstractDialog implements ITargetDialog {

    private static final long serialVersionUID = -923728327119653756L;

    public TargetStarDialog(ObservationManager om, ITarget editableTarget) {

        super(om, new TargetStarPanel(om, editableTarget, Boolean.TRUE));

        PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle.getBundle("ObservationManager",
                Locale.getDefault());
        if (editableTarget == null) {
            this.setTitle(bundle.getString("dialog.targetStar.title"));
        } else {
            this.setTitle(bundle.getString("dialog.targetStar.titleEdit") + " " + editableTarget.getDisplayName());
        }

        this.setSize(TargetStarDialog.serialVersionUID, 590, 313);

        this.pack();
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
