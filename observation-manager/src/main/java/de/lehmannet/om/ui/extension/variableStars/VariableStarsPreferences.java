/* ====================================================================
 * /extension/variableStars/VariableStarsPreferences.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.variableStars;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.JCheckBox;
import javax.swing.JLabel;

import de.lehmannet.om.ui.preferences.PreferencesPanel;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.IConfiguration;

public class VariableStarsPreferences extends PreferencesPanel {

    
    private final PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
            .getBundle("de.lehmannet.om.ui.extension.variableStars.VariableStar", Locale.getDefault());

    private JCheckBox cacheEnabled = null;

    public VariableStarsPreferences(IConfiguration config) {

        super(config);

        this.createPanel();

    }

    @Override
    public void writeConfig() {

        // Use cache
       this.setConfig(VariableStarsConfigKey.CONFIG_CACHE_ENABLED,
               String.valueOf(this.cacheEnabled.isSelected()));

    }

    @Override
    public String getTabTitle() {

        return this.bundle.getString("preferences.title");

    }

    private void createPanel() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        this.setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 1, 1, 10, 15);
        constraints.anchor = GridBagConstraints.WEST;
        JLabel cacheEnabledLabel = new JLabel(this.bundle.getString("preferences.label.cacheEnabled"));
        cacheEnabledLabel.setToolTipText(this.bundle.getString("preferences.tooltip.cacheEnabled"));
        gridbag.setConstraints(cacheEnabledLabel, constraints);
        this.add(cacheEnabledLabel);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        ConstraintsBuilder.buildConstraints(constraints, 1, 0, 1, 1, 40, 15);
        this.cacheEnabled = new JCheckBox();
        this.cacheEnabled.setSelected(Boolean
                .parseBoolean(this.getConfig(VariableStarsConfigKey.CONFIG_CACHE_ENABLED).orElse("true")));
        this.cacheEnabled.setToolTipText(this.bundle.getString("preferences.tooltip.cacheEnabled"));
        gridbag.setConstraints(this.cacheEnabled, constraints);
        this.add(this.cacheEnabled);

        // ------------------

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 2, 1, 100, 60);
        constraints.fill = GridBagConstraints.BOTH;
        JLabel Lfill = new JLabel("");
        gridbag.setConstraints(Lfill, constraints);
        this.add(Lfill);

    }

}
