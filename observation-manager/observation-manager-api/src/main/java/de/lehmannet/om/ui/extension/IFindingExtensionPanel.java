package de.lehmannet.om.ui.extension;

import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ISession;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.navigation.observation.utils.InstallDir;
import de.lehmannet.om.ui.panel.IFindingPanel;
import de.lehmannet.om.ui.util.IConfiguration;
import de.lehmannet.om.ui.util.UserInterfaceHelper;

public interface IFindingExtensionPanel extends IFindingPanel {
    
    /**
     * 
     * @return name of panel
     */
    String getName();

    String getXSIType();
 
    ISchemaElement getSchemaElement();

    ISchemaElement createSchemaElement();

    ISchemaElement updateSchemaElement();

    
  

}