/* ====================================================================
 * /dialog/FilterDialog.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.dialog;

import de.lehmannet.om.IFilter;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.panel.FilterPanel;

public class FilterDialog extends AbstractDialog {

    private static final long serialVersionUID = -6980698554201802941L;

    public FilterDialog(ObservationManager om, IFilter filter) {

        super(om, new FilterPanel(filter, true));

        if (filter == null) {
            super.setTitle(AbstractDialog.bundle.getString("dialog.filter.title"));
        } else {
            super.setTitle(AbstractDialog.bundle.getString("dialog.filter.titleEdit") + " " + filter.getDisplayName());
        }

        super.setSize(FilterDialog.serialVersionUID, 490, 130);
        super.setVisible(true);

    }

    public IFilter getFilter() {

        if (super.schemaElement != null) {
            return (IFilter) super.schemaElement;
        }

        return null;

    }

}
