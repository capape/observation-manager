/*
 * ====================================================================
 * /util/Configuration.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.util;

import de.lehmannet.om.ui.navigation.ObservationManager;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configuration implements IConfiguration {

    private static final String CONFIG_FILE = "config";

    private Properties persistence = null;

    private boolean changed = false;

    private String configPath;

    private static final Logger LOGGER = LoggerFactory.getLogger(Configuration.class);
    private final String version;
    private final String configFileHeader;

    public Configuration(String path, String appVersion) {
        this.configPath = path;
        this.version = appVersion;
        this.configFileHeader = String.format(
                "ObservationManager configfile \n#Written by version: %s \n#If you edit this, make sure you know what you're doing...",
                this.version);

        try {
            this.loadConfiguration(this.configPath);
        } catch (IOException ioe) {
            LOGGER.error("Cannot find configuration file {} ", path, ioe);
        }
    }

    public boolean saveConfiguration() {
        return this.saveConfiguration(this.configPath);
    }

    public String getConfigPath() {
        return this.configPath;
    }

    private void loadConfiguration(String path) throws IOException {

        this.persistence = new Properties();

        path = this.getConfigPath(path) + File.separatorChar + CONFIG_FILE;
        if (!new File(path).exists()) {
            // No configuration found...must be first time user -> Create empty config
            return;
        }

        FileInputStream fis = new FileInputStream(path);
        BufferedInputStream bis = new BufferedInputStream(fis);
        try {
            this.persistence.load(bis);
        } catch (IOException ioe) {

            LOGGER.error("Cannot load configuration file {} ", path, ioe);
        }
    }

    private boolean saveConfiguration(String path) {

        if (!this.changed) {
            return true;
        }

        String realPath = this.getConfigPath(path);

        File configFolder = new File(realPath);

        if (configFolder.exists() || configFolder.mkdirs()) { // Create directories}

            String configFilePath = realPath + File.separatorChar + CONFIG_FILE;
            try {
                FileOutputStream fos = new FileOutputStream(configFilePath);
                this.persistence.store(new BufferedOutputStream(fos), this.configFileHeader);
                fos.close();
            } catch (IOException ioe) {
                LOGGER.error("Cannot save configuration file {} ", configFilePath);
                return false;
            }

            return true;
        }
        LOGGER.error("Cannot create config folders {} ", realPath);
        return false;
    }

    public void setConfig(String key, String value) {

        if ((value == null) || ("".equals(value.trim()))) {
            this.persistence.remove(key);
            this.changed = true;
            return;
        }

        this.persistence.setProperty(key, value);
        this.changed = true;
    }

    public String getConfig(String key) {

        return this.persistence.getProperty(key);
    }

    public String getConfig(String key, String defaultValue) {

        return this.persistence.getProperty(key, defaultValue);
    }

    private String getConfigPath(String path) {

        if (path == null) { // Search in user home
            path = System.getProperty("user.home");
        }

        path = path + File.separatorChar + ObservationManager.WORKING_DIR;

        return path;
    }

    public void deleteKeysStartingWith(String prefix) {

        List<String> removeKeys = new ArrayList<>();
        for (String currentKey : this.persistence.stringPropertyNames()) {
            if (currentKey.startsWith(prefix)) {
                removeKeys.add(currentKey);
            }
        }

        // Delete all window size information
        for (String removeKey : removeKeys) {
            this.persistence.remove(removeKey);
        }
    }

    @Override
    public Set<String> getKeysStartingWith(String prefix) {
        Set<String> result = new HashSet<>();
        for (String currentKey : this.persistence.stringPropertyNames()) {
            if (currentKey.startsWith(prefix)) {
                result.add(currentKey);
            }
        }
        return result;
    }

    @Override
    public boolean getBooleanConfig(String key) {

        return Boolean.parseBoolean(this.getConfig(key));
    }

    @Override
    public boolean getBooleanConfig(String key, boolean defaultValue) {
        return Boolean.parseBoolean(this.getConfig(key, String.valueOf(defaultValue)));
    }

    @Override
    public void setConfig(IConfigKey key, String value) {
        this.setConfig(key.getKey(), value);
    }

    @Override
    public String getConfig(IConfigKey key) {
        return this.getConfig(key.getKey());
    }

    @Override
    public boolean getBooleanConfig(IConfigKey key) {

        return this.getBooleanConfig(key.getKey());
    }

    @Override
    public String getConfig(IConfigKey key, String defaultValue) {

        return this.getConfig(key.getKey(), defaultValue);
    }

    @Override
    public boolean getBooleanConfig(IConfigKey key, boolean defaultValue) {

        return this.getBooleanConfig(key.getKey(), defaultValue);
    }
}
