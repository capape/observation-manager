/*
 * ====================================================================
 * /dialog/FilterDialog.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.dialog;

import de.lehmannet.om.IFilter;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.panel.FilterPanel;

public class FilterDialog extends AbstractDialog {

    private static final long serialVersionUID = -6980698554201802941L;

    public FilterDialog(ObservationManager om, ObservationManagerModel model, IFilter filter) {

        super(om, model, om.getUiHelper(), new FilterPanel(filter, true));

        if (filter == null) {
            this.setTitle(AbstractDialog.bundle.getString("dialog.filter.title"));
        } else {
            this.setTitle(AbstractDialog.bundle.getString("dialog.filter.titleEdit") + " " + filter.getDisplayName());
        }

        this.setSize(FilterDialog.serialVersionUID, 490, 130);
        this.setVisible(true);

    }

    public IFilter getFilter() {

        if (this.schemaElement != null) {
            return (IFilter) this.schemaElement;
        }

        return null;

    }

}
