package de.lehmannet.om;

import de.lehmannet.om.ui.navigation.observation.utils.InstallDir;
import de.lehmannet.om.ui.util.Configuration;

public class ObservationManagerContextImpl implements ObservationManagerContext {

    private final String locale;
    private final String nightVision;
    private final InstallDir installDir;
    private final Configuration configuration;

    private static ObservationManagerContext INSTANCE = null;

    private ObservationManagerContextImpl(Builder builder) {
        this.locale = builder.locale;
        this.nightVision = builder.nightVision;
        this.installDir = builder.installDir;
        this.configuration = builder.configuration;
        INSTANCE = this;
    }

    public static ObservationManagerContext getInstance() {
        if (INSTANCE == null) {
            new Builder().build();
        }
        return INSTANCE;
    }

    public String getLocale() {
        return locale;
    }

    public String getNightVision() {
        return nightVision;
    }

    public InstallDir getInstallDir() {
        return installDir;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public static class Builder {

        private String locale;
        private String nightVision;
        private InstallDir installDir;
        private Configuration configuration;

        public Builder locale(String locale) {
            this.locale = locale;
            return this;
        }

        public Builder nightVision(String nightVision) {
            this.nightVision = nightVision;
            return this;
        }

        public Builder installDir(InstallDir installDir) {
            this.installDir = installDir;
            return this;
        }

        public Builder configuration(Configuration configuration) {
            this.configuration = configuration;
            return this;
        }

        public ObservationManagerContext build() {

            return new ObservationManagerContextImpl(this);
        }
    }

}