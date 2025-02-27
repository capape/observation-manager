package de.lehmannet.om.ui.preferences;

import de.lehmannet.om.ui.box.LanguageBox;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.util.ConfigKey;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.IConfiguration;
import de.lehmannet.om.ui.util.LocaleToolsFactory;
import de.lehmannet.om.ui.util.OMLabel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeneralPanel extends PreferencesPanel {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralPanel.class);

    private static final long serialVersionUID = 7383101472997890151L;

    private final ResourceBundle bundle =
            LocaleToolsFactory.appInstance().getBundle("ObservationManager", Locale.getDefault());

    private ObservationManager om = null;

    private JCheckBox loadLastFile = null;
    private JCheckBox checkForUpdates = null;
    private JCheckBox fixXmlOnLoad = null;
    private LanguageBox uiLanguage = null;
    private JComboBox<String> xslTemplate = null;
    private final IConfiguration configuration;

    public GeneralPanel(IConfiguration config, ObservationManager om) {

        super(config);
        this.configuration = config;
        this.om = om;
        this.createPanel();
    }

    @Override
    public String getTabTitle() {

        return this.bundle.getString("dialog.preferences.generalTab.title");
    }

    @Override
    public void writeConfig() {

        // Load last opened XML file on startup
        this.setConfig(ConfigKey.CONFIG_OPENONSTARTUP, String.valueOf(this.loadLastFile.isSelected()));
        this.setConfig(ConfigKey.CONFIG_UPDATECHECK_STARTUP, String.valueOf(this.checkForUpdates.isSelected()));
        this.setConfig(ConfigKey.CONFIG_UILANGUAGE, String.valueOf(this.uiLanguage.getSelectedISOLanguage()));
        this.setConfig(ConfigKey.CONFIG_FIX_XML_ERRORS_ON_LOAD, String.valueOf(this.fixXmlOnLoad.isSelected()));
        this.configuration.saveConfiguration();

        this.om.reloadLanguage();
        if (this.xslTemplate.getSelectedItem() != null) {
            this.setConfig(ConfigKey.CONFIG_XSL_TEMPLATE, String.valueOf(this.xslTemplate.getSelectedItem()));
        }
    }

    private void createPanel() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        this.setLayout(gridbag);

        addLoadXmlAtStartupPreference(gridbag, constraints);
        addCheckForUpdatesPreference(gridbag, constraints);
        addUILanguagePreference(gridbag, constraints);
        addXslTemplatePreference(gridbag, constraints);
        addResetWindowsSizePreference(gridbag, constraints);
        addFixXmlErrosOnLoadPreference(gridbag, constraints);

        ConstraintsBuilder.buildConstraints(constraints, 0, 6, 2, 1, 100, 40);
        constraints.fill = GridBagConstraints.BOTH;
        JLabel labelFill = new JLabel("");
        gridbag.setConstraints(labelFill, constraints);
        this.add(labelFill);
    }

    private void addResetWindowsSizePreference(GridBagLayout gridbag, GridBagConstraints constraints) {
        ConstraintsBuilder.buildConstraints(constraints, 0, 4, 1, 1, 10, 15);
        constraints.anchor = GridBagConstraints.WEST;
        OMLabel labelResetWindowsSizes =
                new OMLabel(this.bundle.getString("dialog.preferences.label.resetWindowSizes"), true);
        labelResetWindowsSizes.setToolTipText(this.bundle.getString("dialog.preferences.tooltip.resetWindowSizes"));
        gridbag.setConstraints(labelResetWindowsSizes, constraints);
        this.add(labelResetWindowsSizes);

        ConstraintsBuilder.buildConstraints(constraints, 1, 4, 1, 1, 40, 15);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        JButton resetWindowSizes = new JButton(this.bundle.getString("dialog.preferences.button.resetWindowSizes"));
        resetWindowSizes.setActionCommand("ResetWindowSizes");
        resetWindowSizes.addActionListener(resetWindowSizesListener());
        resetWindowSizes.setToolTipText(this.bundle.getString("dialog.preferences.tooltip.resetWindowSizes"));
        gridbag.setConstraints(resetWindowSizes, constraints);
        this.add(resetWindowSizes);
    }

    private ActionListener resetWindowSizesListener() {
        return e -> {
            if ("ResetWindowSizes".equals(e.getActionCommand())) {
                GeneralPanel.this.om.resetWindowSizes();
            }
        };
    }

    private void addXslTemplatePreference(GridBagLayout gridbag, GridBagConstraints constraints) {
        ConstraintsBuilder.buildConstraints(constraints, 0, 3, 1, 1, 10, 15);
        constraints.anchor = GridBagConstraints.WEST;
        OMLabel labelXslTemplate = new OMLabel(this.bundle.getString("dialog.preferences.label.xslTemplate"), true);
        labelXslTemplate.setToolTipText(this.bundle.getString("dialog.preferences.tooltip.xslTemplate"));
        gridbag.setConstraints(labelXslTemplate, constraints);
        this.add(labelXslTemplate);

        ConstraintsBuilder.buildConstraints(constraints, 1, 3, 1, 1, 40, 15);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        this.createXslTemplateBox();
        this.xslTemplate.setEnabled(true);
        this.xslTemplate.setToolTipText(this.bundle.getString("dialog.preferences.tooltip.xslTemplate"));
        gridbag.setConstraints(this.xslTemplate, constraints);
        this.add(this.xslTemplate);
    }

    private void addUILanguagePreference(GridBagLayout gridbag, GridBagConstraints constraints) {
        ConstraintsBuilder.buildConstraints(constraints, 0, 2, 1, 1, 10, 15);
        constraints.anchor = GridBagConstraints.WEST;
        OMLabel uiLanguageLabel = new OMLabel(this.bundle.getString("dialog.preferences.label.uiLanguage"), true);
        uiLanguageLabel.setToolTipText(this.bundle.getString("dialog.preferences.tooltip.uiLanguage"));
        gridbag.setConstraints(uiLanguageLabel, constraints);
        this.add(uiLanguageLabel);

        ConstraintsBuilder.buildConstraints(constraints, 1, 2, 1, 1, 40, 15);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        List<String> acceptedLanguages = this.getInstalledLanguages();
        this.uiLanguage = new LanguageBox(acceptedLanguages, Locale.getDefault().getLanguage(), false);
        this.uiLanguage.setEnabled(true);
        this.uiLanguage.setToolTipText(this.bundle.getString("dialog.preferences.tooltip.uiLanguage"));
        gridbag.setConstraints(this.uiLanguage, constraints);
        this.add(this.uiLanguage);
    }

    private void addFixXmlErrosOnLoadPreference(GridBagLayout gridbag, GridBagConstraints constraints) {
        ConstraintsBuilder.buildConstraints(constraints, 0, 5, 1, 1, 10, 15);
        constraints.anchor = GridBagConstraints.WEST;
        OMLabel labelFixXmlOnLoad =
                new OMLabel(this.bundle.getString("dialog.preferences.label.fix.xml.on.load"), true);
        labelFixXmlOnLoad.setToolTipText(this.bundle.getString("dialog.preferences.tooltip.fix.xml.on.load"));
        gridbag.setConstraints(labelFixXmlOnLoad, constraints);
        this.add(labelFixXmlOnLoad);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        ConstraintsBuilder.buildConstraints(constraints, 1, 5, 1, 1, 40, 15);
        this.fixXmlOnLoad = new JCheckBox();
        var configValue = Boolean.parseBoolean(
                this.getConfig(ConfigKey.CONFIG_FIX_XML_ERRORS_ON_LOAD).orElse("false"));
        this.fixXmlOnLoad.setSelected(configValue);
        this.fixXmlOnLoad.setToolTipText(this.bundle.getString("dialog.preferences.tooltip.fix.xml.on.load"));
        gridbag.setConstraints(this.fixXmlOnLoad, constraints);
        this.add(this.fixXmlOnLoad);
    }

    private void addCheckForUpdatesPreference(GridBagLayout gridbag, GridBagConstraints constraints) {
        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 1, 1, 10, 15);
        constraints.anchor = GridBagConstraints.WEST;
        OMLabel checkForUpdates =
                new OMLabel(this.bundle.getString("dialog.preferences.label.checkForUpdatesDuringStartup"), true);
        checkForUpdates.setToolTipText(
                this.bundle.getString("dialog.preferences.tooltip.checkForUpdatesDuringStartup"));
        gridbag.setConstraints(checkForUpdates, constraints);
        this.add(checkForUpdates);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        ConstraintsBuilder.buildConstraints(constraints, 1, 1, 1, 1, 40, 15);
        this.checkForUpdates = new JCheckBox();
        this.checkForUpdates.setSelected(Boolean.parseBoolean(
                this.getConfig(ConfigKey.CONFIG_UPDATECHECK_STARTUP).orElse("false")));
        this.checkForUpdates.setToolTipText(
                this.bundle.getString("dialog.preferences.tooltip.checkForUpdatesDuringStartup"));
        gridbag.setConstraints(this.checkForUpdates, constraints);
        this.add(this.checkForUpdates);
    }

    private void addLoadXmlAtStartupPreference(GridBagLayout gridbag, GridBagConstraints constraints) {
        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 1, 1, 10, 15);
        constraints.anchor = GridBagConstraints.WEST;
        OMLabel loadLastXMLLabel = new OMLabel(this.bundle.getString("dialog.preferences.label.loadLastXML"), true);
        loadLastXMLLabel.setToolTipText(this.bundle.getString("dialog.preferences.tooltip.loadLastXML"));
        gridbag.setConstraints(loadLastXMLLabel, constraints);
        this.add(loadLastXMLLabel);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        ConstraintsBuilder.buildConstraints(constraints, 1, 0, 1, 1, 40, 15);
        this.loadLastFile = new JCheckBox();
        this.loadLastFile.setSelected(Boolean.parseBoolean(
                this.getConfig(ConfigKey.CONFIG_OPENONSTARTUP).orElse("false")));
        this.loadLastFile.setToolTipText(this.bundle.getString("dialog.preferences.tooltip.loadLastXML"));
        gridbag.setConstraints(this.loadLastFile, constraints);
        this.add(this.loadLastFile);
    }

    private void createXslTemplateBox() {

        this.xslTemplate = new JComboBox<>();

        File path = new File(this.om.getInstallDir().getPathForFolder("xsl"));
        if (!path.exists()) { // Should never happen in a correct installation
            return;
        }

        // Get all directories and add them to the JComboBox
        String[] directories = path.list((dir, name) -> {
            File file = new File(dir.getAbsolutePath() + File.separator + name);
            return file.isDirectory();
        });
        if (directories == null) {
            this.xslTemplate.setSelectedItem(this.om.getConfiguration().getConfig(ConfigKey.CONFIG_XSL_TEMPLATE));

        } else {
            for (String directory : directories) {
                this.xslTemplate.addItem(directory);
            }

            if (directories.length > 0) {
                this.xslTemplate.setSelectedItem(
                        this.om.getConfiguration().getConfig(ConfigKey.CONFIG_XSL_TEMPLATE, directories[0]));
            } else {
                this.xslTemplate.setSelectedItem(this.om.getConfiguration().getConfig(ConfigKey.CONFIG_XSL_TEMPLATE));
            }
        }
    }

    // Installed languages get determined by accessing the observationManager.jar
    // and getting all locales for ObservationManager_??.properties
    private List<String> getInstalledLanguages() {

        List<String> result = new ArrayList<>();

        // Get JARs from classpath
        String sep = System.getProperty("path.separator");
        String path = System.getProperty("java.class.path");

        StringTokenizer tokenizer = new StringTokenizer(path, sep);

        File token = null;
        while (tokenizer.hasMoreTokens()) {
            token = new File(tokenizer.nextToken());

            if ((token.isFile()) && ("observationManager.jar".equals(token.getName()))) {
                result.addAll(Objects.requireNonNull(scanJarFile(token)));
                return result;
            }
        }

        // Get JARs under extension path
        // TODO: new external extension loader
        // String extPath = System.getProperty(ConfigLoader.EXTENSIONS_DIR_PROPERTY);
        // File ext = new File(extPath);
        // if (ext.exists()) {
        // File[] jars = ext.listFiles((dir, name) -> "observationManager.jar".equals(name));

        // if (jars != null) {
        // for (File jar : jars) {
        // result.addAll(Objects.requireNonNull(scanJarFile(jar)));
        // return result;
        // }
        // }
        // }

        return result;
    }

    private List<String> scanJarFile(File jarFile) {

        try (ZipFile archive = new ZipFile(jarFile)) {
            List<String> result = new ArrayList<>();
            Enumeration<? extends ZipEntry> enu = archive.entries();
            Locale l = null;
            while (enu.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) enu.nextElement();
                String name = entry.getName();

                if ((name.startsWith("ObservationManager_")) && (name.endsWith(".properties"))) {
                    l = new Locale(name.substring(name.indexOf("_") + 1, name.indexOf(".")));
                    result.add(l.getLanguage());
                }
            }

            return result;
        } catch (IOException zipEx) {
            LOGGER.error("Error while accessing JAR file.", zipEx);
            return null;
        }
    }
}
