
package de.lehmannet.om;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.navigation.observation.utils.ArgumentName;
import de.lehmannet.om.ui.navigation.observation.utils.ArgumentsParser;
import de.lehmannet.om.ui.navigation.observation.utils.InstallDir;
import de.lehmannet.om.ui.util.Configuration;

public class ObservationManagerApp  {

 
    /**
     *
     */
    private static final long serialVersionUID = -1094139001194654080L;
   

    private final Logger LOGGER = LoggerFactory.getLogger(ObservationManagerApp.class);

    // Version
    public static final String VERSION = "1.421";

    // Working directory
    public static final String WORKING_DIR = ".observationManager";

   
    public static void main(final String[] args) {

        // Get install dir and parse arguments
        final ArgumentsParser argumentsParser = new ArgumentsParser.Builder(args).build();
        
        final String installDirName = argumentsParser.getArgumentValue(ArgumentName.INSTALL_DIR);
        final InstallDir installDir = new InstallDir.Builder().withInstallDir(installDirName).build();

        final String configDir =argumentsParser.getArgumentValue(ArgumentName.CONFIGURATION);
        final Configuration configuration = new Configuration(configDir);

        final String locale = argumentsParser.getArgumentValue(ArgumentName.LANGUAGE);
        final String nightVision =argumentsParser.getArgumentValue(ArgumentName.NIGHTVISION);
        final String logging =argumentsParser.getArgumentValue(ArgumentName.LOGGING);
        

        ObservationManager.newInstance(installDir, configuration);

    }
}