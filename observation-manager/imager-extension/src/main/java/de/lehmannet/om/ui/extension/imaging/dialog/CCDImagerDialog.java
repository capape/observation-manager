/*
 * ====================================================================
 * /dialog/CCDImagerDialog.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.imaging.dialog;

import de.lehmannet.om.IImager;
import de.lehmannet.om.extension.imaging.CCDImager;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.dialog.AbstractDialog;
import de.lehmannet.om.ui.dialog.IImagerDialog;
import de.lehmannet.om.ui.extension.imaging.panel.CCDImagerPanel;
import de.lehmannet.om.ui.util.UserInterfaceHelper;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.JFrame;

public class CCDImagerDialog extends AbstractDialog implements IImagerDialog {

    private static final long serialVersionUID = 8966093381064556604L;

    public CCDImagerDialog(
            JFrame om,
            UserInterfaceHelper uiHelper,
            ObservationManagerModel model,
            IImager editableImager,
            boolean editable) {

        super(om, model, uiHelper, new CCDImagerPanel(editableImager, editable));

        ResourceBundle bundle =
                ResourceBundle.getBundle("de.lehmannet.om.ui.extension.imaging.Imaging", Locale.getDefault());
        if (editableImager == null) {
            this.setTitle(bundle.getString("dialog.ccdImager.title"));
        } else {
            this.setTitle(bundle.getString("dialog.ccdImager.titleEdit") + " " + editableImager.getDisplayName());
        }

        this.setSize(CCDImagerDialog.serialVersionUID, 520, 240);

        this.setVisible(true);
    }

    @Override
    public IImager getImager() {

        if (this.schemaElement != null) {
            return (CCDImager) this.schemaElement;
        }

        return null;
    }
}
