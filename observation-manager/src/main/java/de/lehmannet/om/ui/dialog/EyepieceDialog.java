/* ====================================================================
 * /dialog/EyepieceDialog.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.dialog;

import de.lehmannet.om.IEyepiece;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.panel.EyepiecePanel;

public class EyepieceDialog extends AbstractDialog {

    private static final long serialVersionUID = 6473227962668060876L;

    public EyepieceDialog(ObservationManager om, IEyepiece editableEyepiece) {

        super(om, new EyepiecePanel(editableEyepiece, true));

        if (editableEyepiece == null) {
            super.setTitle(AbstractDialog.bundle.getString("dialog.eyepiece.title"));
        } else {
            super.setTitle(AbstractDialog.bundle.getString("dialog.eyepiece.titleEdit") + " "
                    + editableEyepiece.getDisplayName());
        }

        super.setSize(EyepieceDialog.serialVersionUID, 530, 155);
        super.setVisible(true);

    }

    public IEyepiece getEyepiece() {

        if (super.schemaElement != null) {
            return (IEyepiece) this.schemaElement;
        }

        return null;

    }

}
