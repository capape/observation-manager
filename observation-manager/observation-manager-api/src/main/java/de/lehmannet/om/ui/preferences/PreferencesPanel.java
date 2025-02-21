package de.lehmannet.om.ui.preferences;

import de.lehmannet.om.ui.util.IConfigKey;
import de.lehmannet.om.ui.util.IConfiguration;
import java.util.Optional;
import javax.swing.JPanel;
import org.apache.commons.lang3.StringUtils;

public abstract class PreferencesPanel extends JPanel {

    private IConfiguration configuration;

    protected PreferencesPanel(IConfiguration config) {

        this.configuration = config;
    }

    public abstract void writeConfig();

    public abstract String getTabTitle();

    public void setConfig(IConfigKey key, String value) {
        this.configuration.setConfig(key, value);
    }

    public Optional<String> getConfig(IConfigKey key) {

        final String value = this.configuration.getConfig(key);

        if (StringUtils.isBlank(value)) {
            return Optional.empty();
        }
        return Optional.of(value);
    }
}
