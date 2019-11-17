/* ====================================================================
 * /extension/deepSky/dialog/VariableStarTargetDialog.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.variableStars.dialog;

import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import de.lehmannet.om.ITarget;
import de.lehmannet.om.ui.dialog.AbstractDialog;
import de.lehmannet.om.ui.dialog.ITargetDialog;
import de.lehmannet.om.ui.extension.variableStars.panel.VariableStarTargetPanel;
import de.lehmannet.om.ui.navigation.ObservationManager;

public class VariableStarTargetDialog extends AbstractDialog implements ITargetDialog {

    private static final long serialVersionUID = -5379806312927835453L;

    public VariableStarTargetDialog(ObservationManager om, ITarget editableTarget) {

        super(om, new VariableStarTargetPanel(om, editableTarget, Boolean.TRUE));

        PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
                .getBundle("de.lehmannet.om.ui.extension.variableStars.VariableStar", Locale.getDefault());
        if (editableTarget == null) {
            super.setTitle(bundle.getString("dialog.variableTarget.title"));
        } else {
            super.setTitle(bundle.getString("dialog.variableTarget.titleEdit") + " " + editableTarget.getDisplayName());
        }

        super.setSize(VariableStarTargetDialog.serialVersionUID, 575, 375);

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
