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
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.dialog.AbstractDialog;
import de.lehmannet.om.ui.dialog.ITargetDialog;
import de.lehmannet.om.ui.extension.variableStars.panel.VariableStarTargetPanel;
import de.lehmannet.om.ui.navigation.ObservationManager;

public class VariableStarTargetDialog extends AbstractDialog implements ITargetDialog {

    private static final long serialVersionUID = -5379806312927835453L;

    public VariableStarTargetDialog(ObservationManager om, ObservationManagerModel model, ITarget editableTarget) {

        super(om,  model, om.getUiHelper(), new VariableStarTargetPanel(om, model, editableTarget, Boolean.TRUE));

        PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
                .getBundle("de.lehmannet.om.ui.extension.variableStars.VariableStar", Locale.getDefault());
        if (editableTarget == null) {
            this.setTitle(bundle.getString("dialog.variableTarget.title"));
        } else {
            this.setTitle(bundle.getString("dialog.variableTarget.titleEdit") + " " + editableTarget.getDisplayName());
        }

        this.setSize(VariableStarTargetDialog.serialVersionUID, 575, 375);

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
