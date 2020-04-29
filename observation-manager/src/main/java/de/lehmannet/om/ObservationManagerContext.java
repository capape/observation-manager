package de.lehmannet.om;

import de.lehmannet.om.ui.navigation.observation.utils.InstallDir;
import de.lehmannet.om.ui.util.IConfiguration;

public interface ObservationManagerContext {

  
    String getLocale();
    String getNightVision();
    InstallDir getInstallDir();
    IConfiguration getConfiguration();

}