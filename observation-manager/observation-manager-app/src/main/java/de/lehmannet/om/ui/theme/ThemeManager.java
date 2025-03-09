package de.lehmannet.om.ui.theme;

import de.lehmannet.om.ui.navigation.ObservationManager;

public interface ThemeManager {

    boolean isNightVision();

    void setTheme(String theme);

    void enableNightVision(ObservationManager om);

    void disableNightVision(ObservationManager om);
}
