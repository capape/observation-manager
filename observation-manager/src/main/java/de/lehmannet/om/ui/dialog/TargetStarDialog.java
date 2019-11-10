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
    private final PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
            .getBundle("ObservationManager", Locale.getDefault());

    public TargetStarDialog(ObservationManager om, ITarget editableTarget, Boolean editable) {

        super(om, new TargetStarPanel(om, editableTarget, new Boolean(true)));

        if (editableTarget == null) {
            super.setTitle(this.bundle.getString("dialog.targetStar.title"));
        } else {
            super.setTitle(
                    this.bundle.getString("dialog.targetStar.titleEdit") + " " + editableTarget.getDisplayName());
        }

        super.setSize(TargetStarDialog.serialVersionUID, 590, 313);

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
