/*
 * ====================================================================
 * /dialog/ObserverDialog.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.dialog;

import de.lehmannet.om.IObserver;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.panel.ObserverPanel;

public class ObserverDialog extends AbstractDialog {

    private static final long serialVersionUID = -1694549916738040244L;

    public ObserverDialog(ObservationManager om, ObservationManagerModel model, IObserver editableObserver) {

        super(om, model, om.getUiHelper(), new ObserverPanel(editableObserver, true));

        if (editableObserver == null) {
            this.setTitle(AbstractDialog.bundle.getString("dialog.observer.title"));
        } else {
            this.setTitle(AbstractDialog.bundle.getString("dialog.observer.titleEdit") + " "
                    + editableObserver.getDisplayName());
        }

        this.setSize(ObserverDialog.serialVersionUID, 500, 300);
        this.setVisible(true);

    }

    public IObserver getObserver() {

        if (this.schemaElement != null) {
            return (IObserver) this.schemaElement;
        }

        return null;

    }

}
