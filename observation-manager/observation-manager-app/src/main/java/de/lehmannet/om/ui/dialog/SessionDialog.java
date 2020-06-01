/* ====================================================================
 * /dialog/SessionDialog.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.dialog;

import de.lehmannet.om.ISession;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.panel.SessionPanel;

public class SessionDialog extends AbstractDialog {

    private static final long serialVersionUID = 3246978868012633237L;

    public SessionDialog(ObservationManager om, ObservationManagerModel model,ISession editableSession) {

        super(om,  om.getModel(), om.getUiHelper(), new SessionPanel(om, model, editableSession, true));

        if (editableSession == null) {
            this.setTitle(AbstractDialog.bundle.getString("dialog.session.title"));
        } else {
            this.setTitle(AbstractDialog.bundle.getString("dialog.session.titleEdit") + " "
                    + editableSession.getDisplayName());
        }

        this.setSize(SessionDialog.serialVersionUID, 1090, 610);
        this.setLocationRelativeTo(om);
        this.setVisible(true);

    }

    public ISession getSession() {

        if (this.schemaElement != null) {
            return (ISession) this.schemaElement;
        }

        return null;

    }

}
