package de.lehmannet.om.ui.extension.variableStars.catalog;

import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import de.lehmannet.om.ui.extension.variableStars.VariableStarsConfigKey;
import de.lehmannet.om.ui.panel.AbstractSearchPanel;
import de.lehmannet.om.ui.util.IConfiguration;

public class GCVS4SearchPanel extends AbstractSearchPanel {

    // Config keys
    private static final String CONFIG_LAST_SEARCHTERM = "om.extension.variableStar.finding.search.lastSearchterm";

    private final GCVS4Catalog catalog;
    private final IConfiguration configuration;

    public GCVS4SearchPanel(GCVS4Catalog catalog, IConfiguration configuration) {

        this.catalog = catalog;
        this.configuration = configuration;
        PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
                .getBundle("de.lehmannet.om.ui.extension.variableStars.VariableStar", Locale.getDefault());
        this.setGeneralInfoText(bundle.getString("panel.search.label.searchInfo"));
        this.createPanel();

        // Set cached values
        if (Boolean.parseBoolean(this.configuration.getConfig(VariableStarsConfigKey.CONFIG_CACHE_ENABLED))) {
            String cachedSearchTerm = this.configuration.getConfig(CONFIG_LAST_SEARCHTERM);
            if ((cachedSearchTerm != null) && (!"".equals(cachedSearchTerm))) {
                this.searchText.setText(cachedSearchTerm);
            }
        }

    }

    @Override
    public void search(String searchString) {

        if (Boolean.parseBoolean(this.configuration.getConfig(VariableStarsConfigKey.CONFIG_CACHE_ENABLED))) {
            this.configuration.setConfig(GCVS4SearchPanel.CONFIG_LAST_SEARCHTERM, searchString);
        }

        if ((searchString == null) || ("".equals(searchString.trim()))) {
            return;
        }

        this.searchResult = this.catalog.getTarget(searchString);

    }

}
