package de.lehmannet.om.ui.navigation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.lehmannet.om.ui.dialog.AboutDialog;
import de.lehmannet.om.ui.util.IConfiguration;
import de.lehmannet.om.ui.util.XMLFileLoader;

public final class ObservationManagerMenuHelp {

    private final Logger LOGGER = LoggerFactory.getLogger(ObservationManagerMenuHelp.class);

    private final XMLFileLoader xmlCache;
    private final IConfiguration configuration;
    private final ObservationManager observationManager;
    

    public ObservationManagerMenuHelp(        
        IConfiguration configuration,
        XMLFileLoader xmlCache,
        ObservationManager om) {
       
        // Load configuration
        this.configuration = configuration; 
        this.xmlCache = xmlCache;
        this.observationManager = om;
 
    }


    public void showInfo() {

        new AboutDialog(this.observationManager);

    }

}