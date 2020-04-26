package de.lehmannet.om.ui.util;

import java.util.Set;

public interface IConfiguration {

    boolean saveConfiguration();

    String getConfigPath();
    
    void setConfig(String key, String value) ;
    
    String getConfig(String key) ;
    
    String getConfig(String key, String defaultValue);
    
    void deleteKeysStartingWith(String prefix);
    
    Set<String> getKeysStartingWith(String prefix) ;
}