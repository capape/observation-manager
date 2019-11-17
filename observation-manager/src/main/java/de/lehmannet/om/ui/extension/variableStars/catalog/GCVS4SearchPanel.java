package de.lehmannet.om.ui.extension.variableStars.catalog;

import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import de.lehmannet.om.ui.extension.variableStars.VariableStarsPreferences;
import de.lehmannet.om.ui.panel.AbstractSearchPanel;

public class GCVS4SearchPanel extends AbstractSearchPanel {

    // Config keys
    private static final String CONFIG_LAST_SEARCHTERM = "om.extension.variableStar.finding.search.lastSearchterm";

    private GCVS4Catalog catalog = null;

    public GCVS4SearchPanel(GCVS4Catalog catalog) {

        this.catalog = catalog;
        PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
                .getBundle("de.lehmannet.om.ui.extension.variableStars.VariableStar", Locale.getDefault());
        super.setGeneralInfoText(bundle.getString("panel.search.label.searchInfo"));
        super.createPanel();

        // Set cached values
        if (Boolean.parseBoolean(catalog.observationManager.getConfiguration()
                .getConfig(VariableStarsPreferences.CONFIG_CACHE_ENABLED))) {
            String cachedSearchTerm = catalog.observationManager.getConfiguration().getConfig(CONFIG_LAST_SEARCHTERM);
            if ((cachedSearchTerm != null) && (!"".equals(cachedSearchTerm))) {
                super.searchText.setText(cachedSearchTerm);
            }
        }

    }

    @Override
    public void search(String searchString) {

        if (Boolean.parseBoolean(catalog.observationManager.getConfiguration()
                .getConfig(VariableStarsPreferences.CONFIG_CACHE_ENABLED))) {
            catalog.observationManager.getConfiguration().setConfig(GCVS4SearchPanel.CONFIG_LAST_SEARCHTERM,
                    searchString);
        }

        if ((searchString == null) || ("".equals(searchString.trim()))) {
            return;
        }

        super.searchResult = this.catalog.getTarget(searchString);

    }

}
