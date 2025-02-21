package de.lehmannet.om.ui.navigation;

import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.util.ConfigKey;
import de.lehmannet.om.ui.util.IConfiguration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObservationManagerFileLoader {

    private final IConfiguration configuration;
    private final ObservationManagerModel model;
    private static final Logger LOGGER = LoggerFactory.getLogger(ObservationManagerFileLoader.class);

    public ObservationManagerFileLoader(IConfiguration configuration, ObservationManagerModel model) {
        this.configuration = configuration;
        this.model = model;
    }

    public List<Pair<String, Boolean>> loadFiles(final String[] files) {

        if ((files == null) || (files.length == 0)) {
            LOGGER.debug("No files to load");
            return Collections.emptyList();
        }

        return Arrays.stream(files).map(x -> Pair.of(x, this.loadFile(x))).collect(Collectors.toList());
    }

    /**
     * @return if empty no load has benn attempted. Other case a value indicating file and result of load.
     */
    public Optional<Pair<String, Boolean>> loadConfig() {

        // Check if we should load last loaded XML on startup
        final boolean load = this.configuration.getBooleanConfig(ConfigKey.CONFIG_OPENONSTARTUP);
        LOGGER.debug("Loading config at startup: {}", load);
        final String lastFile = this.configuration.getConfig(ConfigKey.CONFIG_LASTXML);
        LOGGER.debug("Last config file: {}", lastFile);
        if (load) {

            // Check if last file is set
            if (!StringUtils.isBlank(lastFile)) {
                boolean result = this.loadFile(lastFile);
                return Optional.of(Pair.of(lastFile, result));
            }
        }

        return Optional.of(Pair.of(lastFile, false));
    }

    private boolean loadFile(final String file) {

        if (file == null) {
            return false;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Loading File: {}", file);
        }

        final boolean result = this.model.loadObservations(file);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Loaded: {} , result: {}", file, result);
        }
        return result;
    }
}
