package de.lehmannet.om.ui.extension;

import de.lehmannet.om.ISession;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.navigation.observation.utils.InstallDir;
import de.lehmannet.om.ui.panel.IFindingPanel;
import de.lehmannet.om.ui.util.IConfiguration;
import de.lehmannet.om.ui.util.UserInterfaceHelper;

public interface IExtensionPanelFactory {

    IFindingPanel newInstance(IConfiguration configuration, InstallDir installDir, UserInterfaceHelper uiHelper,
            ObservationManagerModel model, ITarget target, ISession s, Boolean editable);
}