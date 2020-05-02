package de.lehmannet.om.ui.navigation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.navigation.observation.utils.SystemInfo;
import de.lehmannet.om.ui.util.ConfigKey;
import de.lehmannet.om.ui.util.IConfiguration;

public class ObservationManagerFileLoader {

    private final IConfiguration configuration;
    private final ObservationManagerModel model;

    public ObservationManagerFileLoader(IConfiguration configuration,
        ObservationManagerModel model) {
        this.configuration = configuration;
        this.model = model;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ObservationManagerFileLoader.class);
    
    public List<Pair<String,Boolean>> loadFiles(final String[] files) {

        if ((files == null) || (files.length == 0)) {
            return Collections.EMPTY_LIST;
        }

        
        return Arrays.stream(files).map(x -> Pair.of(x, this.loadFile(x))).collect(Collectors.toList());

    }

    /**
     * 
     * @return if empty no load has benn attempted. Other case a value indicating file and result of load.
     */
    public Optional<Pair<String, Boolean>> loadConfig() {

        // Check if we should load last loaded XML on startup
        final boolean load = this.configuration.getBooleanConfig(ConfigKey.CONFIG_OPENONSTARTUP);
        if (load) {
            final String lastFile = this.configuration.getConfig(ConfigKey.CONFIG_LASTXML);
            // Check if last file is set
            if (!StringUtils.isBlank(lastFile)) {
                boolean result = this.loadFile(lastFile);
                return Optional.of(Pair.of(lastFile, result));
            }
        }

        return Optional.empty();

    }

    private boolean loadFile(final String file) {

        if (file == null) {
            return false;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Loading File: {}", file);
            LOGGER.debug(SystemInfo.printMemoryUsage());
        }

        final boolean result = this.model.loadObservations(file);
                

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Loaded: {} , result: {}", file, result);
            LOGGER.debug(SystemInfo.printMemoryUsage());
        }
        return result;
        
        
    }

}