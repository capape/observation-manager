package de.lehmannet.om;

import de.lehmannet.om.ui.i18n.TextManager;
import de.lehmannet.om.ui.image.ImageResolver;
import de.lehmannet.om.ui.navigation.observation.utils.InstallDir;
import de.lehmannet.om.ui.util.IConfiguration;
import de.lehmannet.om.util.DateManager;

public final class ObservationManagerContext {
   

    private final IConfiguration configuration;
    private final DateManager dateManager;
    private final ImageResolver imageResolver;
    private final InstallDir installDir;
    private final String locale;
    private final boolean nightVision;
    private final TextManager textManager;
    private final TextManager versionTextManager;

    private ObservationManagerContext(Builder builder) {
        this.configuration = builder.configuration;
        this.dateManager = builder.dateManager;
        this.imageResolver = builder.imageResolver;
        this.installDir = builder.installDir;
        this.locale = builder.locale;
        this.nightVision = Boolean.valueOf(builder.nightVision);
        this.textManager = builder.textManager;
        this.versionTextManager = builder.versionTextManager;
    }

    public String getLocale() {
        return locale;
    }

    public Boolean getNightVision() {
        return nightVision;
    }

    public InstallDir getInstallDir() {
        return installDir;
    }

    public IConfiguration getConfiguration() {
        return configuration;
    }

    public ImageResolver getImageResolver() {
        return imageResolver;
    }

    public TextManager getTextManager() {
        return textManager;
    }

    public TextManager getVersionTextManager() {
        return versionTextManager;
    }

    public DateManager getDateManager() {
        return dateManager;
    }

    public static class Builder {
        
        private IConfiguration configuration;
        private DateManager dateManager;
        private ImageResolver imageResolver;
        private InstallDir installDir;
        private String locale;
        private boolean nightVision;
        private TextManager textManager;
        private TextManager versionTextManager;

        public Builder locale(String locale) {
            this.locale = locale;
            return this;
        }

        public Builder nightVision(boolean nightVision) {
            this.nightVision = nightVision;
            return this;
        }

        public Builder installDir(InstallDir installDir) {
            this.installDir = installDir;
            return this;
        }

        public Builder configuration(IConfiguration configuration) {
            this.configuration = configuration;
            return this;
        }

        public Builder imageResolver(ImageResolver imageResolver) {
            this.imageResolver = imageResolver;
            return this;
        }

        public Builder textManager(TextManager textManager) {
            this.textManager = textManager;
            return this;
        }

        public Builder versionTextManager(TextManager versionTextManager) {
            this.versionTextManager = versionTextManager;
            return this;
        }

        public Builder dateManager(DateManager dateManager) {
            this.dateManager = dateManager;
            return this;
        }

        public ObservationManagerContext build() {

            return new ObservationManagerContext(this);
        }
    }
}
