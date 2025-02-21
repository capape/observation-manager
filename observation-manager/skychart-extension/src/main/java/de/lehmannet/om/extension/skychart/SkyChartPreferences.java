/*
 * ====================================================================
 * /extension/skychart/SkyChartPreferences.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.extension.skychart;

import de.lehmannet.om.ui.preferences.PreferencesPanel;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.IConfiguration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class SkyChartPreferences extends PreferencesPanel {

    private static final long serialVersionUID = -4937900578188172966L;

    // Starchart default port and IP
    public static final String SERVER_DEFAULT_IP = "127.0.0.1";
    public static final int SERVER_DEFAULT_PORT = 3292;

    private final ResourceBundle bundle =
            ResourceBundle.getBundle("de.lehmannet.om.extension.skychart.Skychart", Locale.getDefault());

    private JTextField serverIP = null;
    private JTextField serverPort = null;
    private JTextField applicationPath = null;

    public SkyChartPreferences(IConfiguration config) {

        super(config);

        this.createPanel();
    }

    @Override
    public void writeConfig() {

        this.setConfig(SkyChartConfigKey.CONFIG_SERVER_IP_KEY, "" + this.getServerIP());
        this.setConfig(SkyChartConfigKey.CONFIG_SERVER_PORT_KEY, "" + this.getServerPort());
        this.setConfig(SkyChartConfigKey.CONFIG_APPLICATION_PATH, "" + this.getApplicationPath());
    }

    @Override
    public String getTabTitle() {

        return this.bundle.getString("preferences.title");
    }

    private void createPanel() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        this.setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 1, 1, 5, 15);
        constraints.anchor = GridBagConstraints.WEST;
        JLabel pathLabel = new JLabel(this.bundle.getString("preferences.label.path"));
        pathLabel.setToolTipText(this.bundle.getString("preferences.tooltip.path"));
        gridbag.setConstraints(pathLabel, constraints);
        this.add(pathLabel);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        ConstraintsBuilder.buildConstraints(constraints, 1, 0, 1, 1, 20, 15);
        this.applicationPath = new JTextField(
                this.getConfig(SkyChartConfigKey.CONFIG_APPLICATION_PATH).orElse(""));
        this.applicationPath.setToolTipText(this.bundle.getString("preferences.tooltip.path"));
        gridbag.setConstraints(this.applicationPath, constraints);
        this.add(this.applicationPath);

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 1, 1, 5, 15);
        constraints.anchor = GridBagConstraints.WEST;
        JLabel ipLabel = new JLabel(this.bundle.getString("preferences.label.ip"));
        ipLabel.setToolTipText(this.bundle.getString("preferences.tooltip.ip"));
        gridbag.setConstraints(ipLabel, constraints);
        this.add(ipLabel);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        ConstraintsBuilder.buildConstraints(constraints, 1, 1, 1, 1, 20, 15);
        this.serverIP = new JTextField(this.getConfig(SkyChartConfigKey.CONFIG_SERVER_IP_KEY)
                .orElse(String.valueOf(SkyChartPreferences.SERVER_DEFAULT_IP)));
        this.serverIP.setToolTipText(this.bundle.getString("preferences.tooltip.ip"));
        gridbag.setConstraints(this.serverIP, constraints);
        this.add(this.serverIP);

        ConstraintsBuilder.buildConstraints(constraints, 0, 2, 1, 1, 5, 15);
        constraints.anchor = GridBagConstraints.WEST;
        JLabel portLabel = new JLabel(this.bundle.getString("preferences.label.port"));
        portLabel.setToolTipText(this.bundle.getString("preferences.tooltip.port"));
        gridbag.setConstraints(portLabel, constraints);
        this.add(portLabel);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        ConstraintsBuilder.buildConstraints(constraints, 1, 2, 1, 1, 20, 15);
        this.serverPort = new JTextField(this.getConfig(SkyChartConfigKey.CONFIG_SERVER_PORT_KEY)
                .orElse(String.valueOf(SkyChartPreferences.SERVER_DEFAULT_PORT)));
        this.serverPort.setToolTipText(this.bundle.getString("preferences.tooltip.port"));
        gridbag.setConstraints(this.serverPort, constraints);
        this.add(this.serverPort);

        // ------------------

        ConstraintsBuilder.buildConstraints(constraints, 0, 3, 2, 1, 25, 70);
        constraints.fill = GridBagConstraints.BOTH;
        JLabel Lfill = new JLabel("");
        gridbag.setConstraints(Lfill, constraints);
        this.add(Lfill);
    }

    private String getServerIP() {

        String ip = this.serverIP.getText();
        if ((ip == null) || ("".equals(ip.trim()))) {
            ip = SkyChartPreferences.SERVER_DEFAULT_IP;
        }

        ip = ip.trim();

        if (ip.contains(" ")) {
            return SkyChartPreferences.SERVER_DEFAULT_IP;
        }

        return ip;
    }

    private int getServerPort() {

        String port = this.serverPort.getText();
        if ((port == null) || ("".equals(port.trim()))) {
            return SkyChartPreferences.SERVER_DEFAULT_PORT;
        }

        int p = SkyChartPreferences.SERVER_DEFAULT_PORT;
        try {
            p = Integer.parseInt(port);
        } catch (NumberFormatException nfe) {
            return SkyChartPreferences.SERVER_DEFAULT_PORT;
        }

        return p;
    }

    private String getApplicationPath() {

        String path = this.applicationPath.getText();
        if ((path == null) || ("".equals(path.trim()))) {
            return "";
        }

        path = path.replace('\\', '/');

        return path;
    }
}
