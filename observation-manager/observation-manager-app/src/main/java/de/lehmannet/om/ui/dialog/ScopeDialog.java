/*
 * ====================================================================
 * /dialog/ScopeDialog.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.dialog;

import de.lehmannet.om.IScope;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.panel.ScopePanel;

public class ScopeDialog extends AbstractDialog {

    private static final long serialVersionUID = 8212243666553820686L;

    public ScopeDialog(ObservationManager om, ObservationManagerModel model, IScope editableScope) {

        super(om, model, om.getUiHelper(), new ScopePanel(editableScope, true));

        if (editableScope == null) {
            this.setTitle(AbstractDialog.bundle.getString("dialog.scope.title"));
        } else {
            this.setTitle(
                    AbstractDialog.bundle.getString("dialog.scope.titleEdit") + " " + editableScope.getDisplayName());
        }

        this.setSize(ScopeDialog.serialVersionUID, 650, 250);
        this.setVisible(true);
    }

    public IScope getScope() {

        if (this.schemaElement != null) {
            return (IScope) this.schemaElement;
        }

        return null;
    }
}
