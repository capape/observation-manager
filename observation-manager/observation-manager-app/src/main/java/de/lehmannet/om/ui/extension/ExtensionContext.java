package de.lehmannet.om.ui.extension;

import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.navigation.observation.utils.InstallDir;
import de.lehmannet.om.ui.util.IConfiguration;
import de.lehmannet.om.ui.util.UserInterfaceHelper;

public class ExtensionContext implements IExtensionContext {

    private final IConfiguration configuration;
    private final InstallDir installDir;
    private final ObservationManagerModel model;
    private final UserInterfaceHelper uiHelper;

    private ExtensionContext(Builder builder) {
        this.configuration = builder.configuration;
        this.installDir = builder.installDir;
        this.model = builder.model;
        this.uiHelper = builder.uiHelper;
    }

    @Override
    public IConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    public InstallDir getInstallDir() {
        return this.installDir;
    }

    @Override
    public ObservationManagerModel getModel() {
        return this.model;
    }

    @Override
    public UserInterfaceHelper getUserInterfaceHelper() {

        return this.uiHelper;
    }

    public static class Builder {

        private UserInterfaceHelper uiHelper;
        private ObservationManagerModel model;
        private InstallDir installDir;
        private IConfiguration configuration;

        public Builder() {

        }

        public Builder uiHelper(UserInterfaceHelper value) {
            this.uiHelper = value;
            return this;
        }

        public Builder model(ObservationManagerModel value) {
            this.model = value;
            return this;
        }

        public Builder installDir(InstallDir value) {
            this.installDir = value;
            return this;
        }

        public Builder configuration(IConfiguration value) {
            this.configuration = value;
            return this;
        }

        public ExtensionContext build() {
            return new ExtensionContext(this);
        }

    }
}