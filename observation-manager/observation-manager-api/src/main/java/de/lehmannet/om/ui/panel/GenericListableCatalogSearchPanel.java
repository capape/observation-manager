package de.lehmannet.om.ui.panel;

import de.lehmannet.om.ITarget;
import de.lehmannet.om.ui.catalog.IListableCatalog;

public class GenericListableCatalogSearchPanel extends AbstractSearchPanel {

    private IListableCatalog catalog = null;

    public GenericListableCatalogSearchPanel(IListableCatalog catalog) {

        this.catalog = catalog;
        this.createPanel();

    }

    @Override
    public void search(String searchText) {

        if ((searchText == null) || ("".equals(searchText.trim()))) {
            return;
        }

        // Is first char a number?
        if (((byte) searchText.charAt(0) >= 48) && ((byte) searchText.charAt(0) < 58)) {
            searchText = this.catalog.getAbbreviation() + searchText;
        }

        // Format search string, so that it matches most catalog names
        searchText = this.formatName(searchText);

        // Most simple way to "search"...so try this first
        this.searchResult = this.catalog.getTarget(searchText);

        if (this.searchResult != null) { // We directly found an entry, so quit here
            return;
        }

        // Need to search a bit more sophisticated
        String[] targetNames = this.catalog.getCatalogIndex();
        ITarget currentTarget = null;
        String[] currentTargetAliasNames = null;
        for (String targetName : targetNames) {
            currentTarget = this.catalog.getTarget(targetName);
            currentTargetAliasNames = currentTarget.getAliasNames();
            if (currentTargetAliasNames == null) {
                continue;
            }
            for (String currentTargetAliasName : currentTargetAliasNames) {
                if (searchText.equals(this.formatName(currentTargetAliasName))) { // Found string within alias
                    // names
                    this.searchResult = currentTarget;
                    return;
                }
            }
        }

    }

}
