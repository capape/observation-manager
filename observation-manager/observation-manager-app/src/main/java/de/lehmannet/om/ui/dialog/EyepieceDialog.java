/*
 * ====================================================================
 * /dialog/EyepieceDialog.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.dialog;

import de.lehmannet.om.IEyepiece;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.panel.EyepiecePanel;

public class EyepieceDialog extends AbstractDialog {

    private static final long serialVersionUID = 6473227962668060876L;

    public EyepieceDialog(ObservationManager om, ObservationManagerModel model, IEyepiece editableEyepiece) {

        super(om, model, om.getUiHelper(), new EyepiecePanel(editableEyepiece, true));

        if (editableEyepiece == null) {
            this.setTitle(AbstractDialog.bundle.getString("dialog.eyepiece.title"));
        } else {
            this.setTitle(AbstractDialog.bundle.getString("dialog.eyepiece.titleEdit") + " "
                    + editableEyepiece.getDisplayName());
        }

        this.setSize(EyepieceDialog.serialVersionUID, 530, 155);
        this.setVisible(true);

    }

    public IEyepiece getEyepiece() {

        if (this.schemaElement != null) {
            return (IEyepiece) this.schemaElement;
        }

        return null;

    }

}
