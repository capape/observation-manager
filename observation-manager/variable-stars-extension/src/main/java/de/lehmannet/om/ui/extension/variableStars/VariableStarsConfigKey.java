package de.lehmannet.om.ui.extension.variableStars;

import de.lehmannet.om.ui.util.IConfigKey;

public enum VariableStarsConfigKey implements IConfigKey {
    CONFIG_CACHE_ENABLED("om.extension.variableStar.cache.enabled");

    private final String key;

    private VariableStarsConfigKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
}
