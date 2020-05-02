package de.lehmannet.om.extension.skychart;

import de.lehmannet.om.ui.util.IConfigKey;

public enum SkyChartConfigKey implements IConfigKey {
    // Config keys
    CONFIG_APPLICATION_PATH("om.extension.starchart.application.bin"),
    CONFIG_SERVER_IP_KEY("om.extension.starchart.server.ip"),
    CONFIG_SERVER_PORT_KEY("om.extension.starchart.server.port");

    private final String key;

    private SkyChartConfigKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
}