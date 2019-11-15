/* ====================================================================
 * /dialog/ObserverDialog.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.dialog;

import de.lehmannet.om.IObserver;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.panel.ObserverPanel;

public class ObserverDialog extends AbstractDialog {

    private static final long serialVersionUID = -1694549916738040244L;

    public ObserverDialog(ObservationManager om, IObserver editableObserver) {

        super(om, new ObserverPanel(editableObserver, true));

        if (editableObserver == null) {
            super.setTitle(AbstractDialog.bundle.getString("dialog.observer.title"));
        } else {
            super.setTitle(AbstractDialog.bundle.getString("dialog.observer.titleEdit") + " "
                    + editableObserver.getDisplayName());
        }

        super.setSize(ObserverDialog.serialVersionUID, 500, 300);
        super.setVisible(true);

    }

    public IObserver getObserver() {

        if (super.schemaElement != null) {
            return (IObserver) super.schemaElement;
        }

        return null;

    }

}
