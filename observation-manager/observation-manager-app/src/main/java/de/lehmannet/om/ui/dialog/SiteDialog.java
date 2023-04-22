/* ====================================================================
 * /dialog/SiteDialog.java
 *
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.dialog;

import de.lehmannet.om.ISite;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.panel.SitePanel;

public class SiteDialog extends AbstractDialog {

    private static final long serialVersionUID = 9057953593845468049L;

    public SiteDialog(ObservationManager om, ISite editableSite) {

        super(om, om.getModel(), om.getUiHelper(), new SitePanel(editableSite, true));

        if (editableSite == null) {
            this.setTitle(AbstractDialog.bundle.getString("dialog.site.title"));
        } else {
            this.setTitle(
                    AbstractDialog.bundle.getString("dialog.site.titleEdit") + " " + editableSite.getDisplayName());
        }

        this.setSize(SiteDialog.serialVersionUID, 550, 140);
        this.setVisible(true);

    }

    public ISite getSite() {

        if (this.schemaElement != null) {
            return (ISite) this.schemaElement;
        }

        return null;

    }

}
