package de.lehmannet.om.ui.navigation.observation.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Enum of valid command line arguments
 */
public enum ArgumentName {
    
    INSTALL_DIR("instDir"),
    LANGUAGE("lang"),
    CONFIGURATION("config"),
    NIGHTVISION("nightvision"),
    LOGGING("log"),
    DEBUG("debug");

    private final String value;
    private static final List<String> validNames = new ArrayList<>();

    static {
        for (ArgumentName arg : ArgumentName.values()) {
            validNames.add(arg.value);
        }   
    }
    

    ArgumentName(String value) {
        this.value = value;
       
    }

    public final String getValue() {
        return this.value;
    }

    public static boolean isValid(String param) {
        return ArgumentName.validNames.contains(param);
    }
}