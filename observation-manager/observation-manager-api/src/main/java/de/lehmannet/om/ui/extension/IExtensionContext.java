package de.lehmannet.om.ui.extension;

import javax.swing.JFrame;

import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.navigation.observation.utils.InstallDir;
import de.lehmannet.om.ui.util.IConfiguration;
import de.lehmannet.om.ui.util.UserInterfaceHelper;

public interface IExtensionContext {

    IConfiguration getConfiguration();

    InstallDir getInstallDir();

    ObservationManagerModel getModel();

    UserInterfaceHelper getUserInterfaceHelper();

}
