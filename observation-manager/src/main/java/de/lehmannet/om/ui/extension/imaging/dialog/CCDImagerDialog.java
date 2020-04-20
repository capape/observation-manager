/* ====================================================================
 * /dialog/CCDImagerDialog.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.imaging.dialog;

import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import de.lehmannet.om.IImager;
import de.lehmannet.om.extension.imaging.CCDImager;
import de.lehmannet.om.ui.dialog.AbstractDialog;
import de.lehmannet.om.ui.dialog.IImagerDialog;
import de.lehmannet.om.ui.extension.imaging.panel.CCDImagerPanel;
import de.lehmannet.om.ui.navigation.ObservationManager;

public class CCDImagerDialog extends AbstractDialog implements IImagerDialog {

    private static final long serialVersionUID = 8966093381064556604L;

    public CCDImagerDialog(ObservationManager om, IImager editableImager) {

        super(om, new CCDImagerPanel(editableImager, Boolean.TRUE));

        PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
                .getBundle("de.lehmannet.om.ui.extension.imaging.Imaging", Locale.getDefault());
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
