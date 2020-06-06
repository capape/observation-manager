package de.lehmannet.om.ui.theme;

public interface ThemeManager {

    boolean isNightVision();

    void setTheme(String theme);

    void enableNightVision();

    void disableNightVision();

}