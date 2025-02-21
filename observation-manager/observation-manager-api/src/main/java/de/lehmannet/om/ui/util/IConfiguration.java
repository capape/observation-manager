package de.lehmannet.om.ui.util;

import java.util.Set;

public interface IConfiguration {

    boolean saveConfiguration();

    String getConfigPath();

    void setConfig(String key, String value);

    String getConfig(String key);

    boolean getBooleanConfig(String key);

    String getConfig(String key, String defaultValue);

    boolean getBooleanConfig(String key, boolean defaultValue);

    void setConfig(IConfigKey key, String value);

    String getConfig(IConfigKey key);

    boolean getBooleanConfig(IConfigKey key);

    String getConfig(IConfigKey key, String defaultValue);

    boolean getBooleanConfig(IConfigKey key, boolean defaultValue);

    void deleteKeysStartingWith(String prefix);

    Set<String> getKeysStartingWith(String prefix);
}
