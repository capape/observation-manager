
package de.lehmannet.om;

import java.net.URL;
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

        final TextManager versionTextManager = new TextManagerImpl("version", "en");
        final String version = versionTextManager.getString("observation.manager.version");
        LOGGER.info("App version: {}", version);

        // Get install dir and parse arguments
        LOGGER.info("Reading command line arguments...");
        final ArgumentsParser argumentsParser = new ArgumentsParser.Builder(args).build();

        LOGGER.info("Reading configuration...");
        final String configDir = argumentsParser.getArgumentValue(ArgumentName.CONFIGURATION);
        final Configuration configuration = new Configuration(configDir, version);

        LOGGER.debug("Configure night vision...");
        boolean nightVision = Boolean
                .parseBoolean(configuration.getConfig(ConfigKey.CONFIG_NIGHTVISION_ENABLED, "false"));

        LOGGER.info("Initializing image resolver...");
        final ImageResolver imageResolver = new ImageClassLoaderResolverImpl("images");

        final URL splashURL = imageResolver.getImageURL("splash.png").orElse(null);

        SplashScreenWithText splash = new SplashScreenWithText.Builder(nightVision).image(splashURL).build();
        splash.showSplash();
        splash.updateText("Loading...");
        splash.updateTextVersion(String.format("Version: %s", version));

        final String installDirName = argumentsParser.getArgumentValue(ArgumentName.INSTALL_DIR);
        final InstallDir installDir = new InstallDir.Builder().withInstallDir(installDirName).build();

        LOGGER.info("Install dir: {}", installDir.getPath());
        splash.updateText("Setting installation folder...");

        LOGGER.info("Initializing xml loader...");
        splash.updateText("Initializing xml loader...");
        final XMLFileLoader xmlCache = XMLFileLoaderImpl.newInstance(installDir.getPathForFile("schema"));

        LOGGER.info("Initializing text manager...");
        splash.updateText("Initializing text manager...");
        final String isoKey = configuration.getConfig(ConfigKey.CONFIG_UILANGUAGE, Locale.getDefault().getLanguage());
        final TextManager textManager = new TextManagerImpl("ObservationManager", isoKey);

        final Locale aLocale = new Locale.Builder().setLanguage(isoKey).build();
        Locale.setDefault(aLocale);

        LOGGER.info("Creating model for app...");
        final ObservationManagerModel model = new ObservationManagerModelImpl(xmlCache, installDir, configuration);

        splash.updateText("Launching app...");
        LOGGER.info("Creating observation manager app...");
        // @formatter:off
        new ObservationManager.Builder(model).locale(isoKey).nightVision(nightVision).installDir(installDir)
                .configuration(configuration).imageResolver(imageResolver).textManager(textManager)
                .versionTextManager(versionTextManager).build();
        // @formatter:on

    }
}