
package de.lehmannet.om;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.model.ObservationManagerModelImpl;
import de.lehmannet.om.ui.i18n.TextManager;
import de.lehmannet.om.ui.i18n.TextManagerImpl;
import de.lehmannet.om.ui.image.ImageClassLoaderResolverImpl;
import de.lehmannet.om.ui.image.ImageResolver;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.navigation.observation.utils.ArgumentName;
import de.lehmannet.om.ui.navigation.observation.utils.ArgumentsParser;
import de.lehmannet.om.ui.navigation.observation.utils.InstallDir;
import de.lehmannet.om.ui.util.ConfigKey;
import de.lehmannet.om.ui.util.Configuration;
import de.lehmannet.om.ui.util.SplashScreenWithText;
import de.lehmannet.om.ui.util.XMLFileLoader;
import de.lehmannet.om.ui.util.XMLFileLoaderImpl;

public class ObservationManagerApp {

    /**
     *
     */
    private static final long serialVersionUID = -1094139001194654080L;

    private static final Logger LOGGER = LoggerFactory.getLogger(ObservationManagerApp.class);

    // Working directory
    public static final String WORKING_DIR = ".observationManager";

    public static void main(final String[] args) {

        SplashScreenWithText.showSplash();
        SplashScreenWithText.updateText("Loading...");

        // Get install dir and parse arguments
        LOGGER.info("Reading command line arguments...");
        final ArgumentsParser argumentsParser = new ArgumentsParser.Builder(args).build();

        final String installDirName = argumentsParser.getArgumentValue(ArgumentName.INSTALL_DIR);
        final InstallDir installDir = new InstallDir.Builder().withInstallDir(installDirName).build();

        LOGGER.info("Install dir: {}", installDir.getPath());
        SplashScreenWithText.updateText("Setting installation folder...");

        final TextManager versionTextManager = new TextManagerImpl("version", "en");
        final String version = versionTextManager.getString("observation.manager.version");
        LOGGER.info("App version: {}", version);

        SplashScreenWithText.updateTextVersion(String.format("Version: %s",version));

        LOGGER.info("Reading configuration...");
        SplashScreenWithText.updateText("Reading configuration....");
        final String configDir = argumentsParser.getArgumentValue(ArgumentName.CONFIGURATION);
        final Configuration configuration = new Configuration(configDir, version);

        final String locale = argumentsParser.getArgumentValue(ArgumentName.LANGUAGE);
        final String nightVision = argumentsParser.getArgumentValue(ArgumentName.NIGHTVISION);
        final String logging = argumentsParser.getArgumentValue(ArgumentName.LOGGING);

        LOGGER.info("Initializing xml loader...");
        SplashScreenWithText.updateText("Initializing xml loader...");
        final XMLFileLoader xmlCache = XMLFileLoaderImpl.newInstance(installDir.getPathForFile("schema"));

        LOGGER.info("Initializing image resolver...");
        SplashScreenWithText.updateText("Initializing images...");
        final ImageResolver imageResolver = new ImageClassLoaderResolverImpl("images");

        LOGGER.info("Initializing text manager...");
        SplashScreenWithText.updateText("Initializing text manager...");
        final String isoKey = configuration.getConfig(ConfigKey.CONFIG_UILANGUAGE, Locale.getDefault().getLanguage());
        final TextManager textManager = new TextManagerImpl("ObservationManager", isoKey);

        final Locale aLocale = new Locale.Builder().setLanguage(isoKey).build();
        Locale.setDefault(aLocale);

        LOGGER.info("Creating model for app...");
        final ObservationManagerModel model = new ObservationManagerModelImpl(xmlCache, installDir, configuration);

        SplashScreenWithText.updateText("Launching app...");
        LOGGER.info("Creating observation manager app...");
        // @formatter:off
        new ObservationManager.Builder(model).locale(locale).nightVision(nightVision).installDir(installDir)
                .configuration(configuration).imageResolver(imageResolver).textManager(textManager)
                .versionTextManager(versionTextManager).build();
        // @formatter:on

    }
}