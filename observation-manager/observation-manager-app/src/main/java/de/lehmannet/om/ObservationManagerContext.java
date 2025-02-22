package de.lehmannet.om;

import de.lehmannet.om.ui.image.ImageResolver;
import de.lehmannet.om.ui.navigation.observation.utils.InstallDir;
import de.lehmannet.om.ui.theme.ThemeManager;
import de.lehmannet.om.ui.util.Configuration;
import de.lehmannet.om.ui.util.IConfiguration;
import de.lehmannet.om.ui.util.UserInterfaceHelper;

public final class ObservationManagerContext {

    private final String locale;
    private final String nightVision;
    private final InstallDir installDir;
    private final IConfiguration configuration;
    private final UserInterfaceHelper uiHelper;
    private final ThemeManager themeManager;
    private final ImageResolver imageResolver;
    
    private ObservationManagerContext(Builder builder) {
        this.locale = builder.locale;
        this.nightVision = builder.nightVision;
        this.installDir = builder.installDir;
        this.configuration = builder.configuration;
        this.uiHelper = builder.uiHelper;
        this.themeManager = builder.themeManager;
        this.imageResolver = builder.imageResolver;
       
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

    public IConfiguration getConfiguration() {
        return configuration;
    }

    public UserInterfaceHelper getUiHelper() {
        return uiHelper;
    }

    public ThemeManager getThemeManager() {
        return themeManager;
    }

    public ImageResolver getImageResolver() {
        return imageResolver;
    }

    public static class Builder {

        private String locale;
        private String nightVision;
        private InstallDir installDir;
        private IConfiguration configuration;
        private ImageResolver imageResolver;
        private UserInterfaceHelper uiHelper;
        private ThemeManager themeManager;

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

        public Builder uiHelper(UserInterfaceHelper uiHelper) {
            this.uiHelper = uiHelper;
            return this;
        }

        public Builder themeManager(ThemeManager themeManager) {
            this.themeManager = themeManager;
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

        public ObservationManagerContext build() {

            return new ObservationManagerContext(this);
        }
    }
}
