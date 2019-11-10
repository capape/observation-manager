package de.lehmannet.om.ui.preferences;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import de.lehmannet.om.ui.box.LanguageBox;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.util.Configuration;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.OMLabel;

public class GeneralPanel extends PreferencesPanel {

    private static final long serialVersionUID = 7383101472997890151L;

    private PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle.getBundle("ObservationManager",
            Locale.getDefault());

    private ObservationManager om = null;

    private JCheckBox loadLastFile = null;
    private JCheckBox checkForUpdates = null;
    private LanguageBox uiLanguage = null;
    private JComboBox xslTemplate = null;
    private JButton resetWindowSizes = null;

    public GeneralPanel(Configuration config, ObservationManager om) {

        super(config);

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
        super.configuration.setConfig(ObservationManager.CONFIG_OPENONSTARTUP, "" + this.loadLastFile.isSelected());

        // ------------------

        // Load last opened XML file on startup
        super.configuration.setConfig(ObservationManager.CONFIG_UPDATECHECK_STARTUP,
                "" + this.checkForUpdates.isSelected());

        // ------------------

        // Set UI Language
        super.configuration.setConfig(ObservationManager.CONFIG_UILANGUAGE,
                "" + this.uiLanguage.getSelectedISOLanguage());
        this.om.reloadLanguage();

        // ------------------

        // Set default catalog
        if (this.xslTemplate.getSelectedItem() != null) {
            super.configuration.setConfig(ObservationManager.CONFIG_XSL_TEMPLATE,
                    "" + this.xslTemplate.getSelectedItem());
        }

    }

    private void createPanel() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        super.setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 1, 1, 10, 15);
        constraints.anchor = GridBagConstraints.WEST;
        OMLabel loadLastXMLLabel = new OMLabel(this.bundle.getString("dialog.preferences.label.loadLastXML"), true);
        loadLastXMLLabel.setToolTipText(this.bundle.getString("dialog.preferences.tooltip.loadLastXML"));
        gridbag.setConstraints(loadLastXMLLabel, constraints);
        super.add(loadLastXMLLabel);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        ConstraintsBuilder.buildConstraints(constraints, 1, 0, 1, 1, 40, 15);
        this.loadLastFile = new JCheckBox();
        this.loadLastFile.setSelected(
                Boolean.valueOf(super.configuration.getConfig(ObservationManager.CONFIG_OPENONSTARTUP)).booleanValue());
        this.loadLastFile.setToolTipText(this.bundle.getString("dialog.preferences.tooltip.loadLastXML"));
        gridbag.setConstraints(this.loadLastFile, constraints);
        super.add(this.loadLastFile);

        // ------------------

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 1, 1, 10, 15);
        constraints.anchor = GridBagConstraints.WEST;
        OMLabel checkForUpdates = new OMLabel(
                this.bundle.getString("dialog.preferences.label.checkForUpdatesDuringStartup"), true);
        checkForUpdates
                .setToolTipText(this.bundle.getString("dialog.preferences.tooltip.checkForUpdatesDuringStartup"));
        gridbag.setConstraints(checkForUpdates, constraints);
        super.add(checkForUpdates);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        ConstraintsBuilder.buildConstraints(constraints, 1, 1, 1, 1, 40, 15);
        this.checkForUpdates = new JCheckBox();
        this.checkForUpdates.setSelected(Boolean
                .valueOf(super.configuration.getConfig(ObservationManager.CONFIG_UPDATECHECK_STARTUP)).booleanValue());
        this.checkForUpdates
                .setToolTipText(this.bundle.getString("dialog.preferences.tooltip.checkForUpdatesDuringStartup"));
        gridbag.setConstraints(this.checkForUpdates, constraints);
        super.add(this.checkForUpdates);

        // ------------------

        ConstraintsBuilder.buildConstraints(constraints, 0, 2, 1, 1, 10, 15);
        constraints.anchor = GridBagConstraints.WEST;
        OMLabel uiLanguageLabel = new OMLabel(this.bundle.getString("dialog.preferences.label.uiLanguage"), true);
        uiLanguageLabel.setToolTipText(this.bundle.getString("dialog.preferences.tooltip.uiLanguage"));
        gridbag.setConstraints(uiLanguageLabel, constraints);
        super.add(uiLanguageLabel);

        ConstraintsBuilder.buildConstraints(constraints, 1, 2, 1, 1, 40, 15);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        List acceptedLanguages = this.getInstalledLanguages();
        this.uiLanguage = new LanguageBox(acceptedLanguages, Locale.getDefault().getLanguage(), false);
        this.uiLanguage.setEnabled(true);
        this.uiLanguage.setToolTipText(this.bundle.getString("dialog.preferences.tooltip.uiLanguage"));
        gridbag.setConstraints(this.uiLanguage, constraints);
        super.add(this.uiLanguage);

        // ------------------

        ConstraintsBuilder.buildConstraints(constraints, 0, 3, 1, 1, 10, 15);
        constraints.anchor = GridBagConstraints.WEST;
        OMLabel LxslTemplate = new OMLabel(this.bundle.getString("dialog.preferences.label.xslTemplate"), true);
        LxslTemplate.setToolTipText(this.bundle.getString("dialog.preferences.tooltip.xslTemplate"));
        gridbag.setConstraints(LxslTemplate, constraints);
        super.add(LxslTemplate);

        ConstraintsBuilder.buildConstraints(constraints, 1, 3, 1, 1, 40, 15);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        this.createXslTemplateBox();
        this.xslTemplate.setEnabled(true);
        this.xslTemplate.setToolTipText(this.bundle.getString("dialog.preferences.tooltip.xslTemplate"));
        gridbag.setConstraints(this.xslTemplate, constraints);
        super.add(this.xslTemplate);

        // ------------------

        ConstraintsBuilder.buildConstraints(constraints, 0, 4, 1, 1, 10, 15);
        constraints.anchor = GridBagConstraints.WEST;
        OMLabel LresetWindowsSizes = new OMLabel(this.bundle.getString("dialog.preferences.label.resetWindowSizes"),
                true);
        LresetWindowsSizes.setToolTipText(this.bundle.getString("dialog.preferences.tooltip.resetWindowSizes"));
        gridbag.setConstraints(LresetWindowsSizes, constraints);
        super.add(LresetWindowsSizes);

        ConstraintsBuilder.buildConstraints(constraints, 1, 4, 1, 1, 40, 15);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        this.resetWindowSizes = new JButton(this.bundle.getString("dialog.preferences.button.resetWindowSizes"));
        this.resetWindowSizes.setActionCommand("ResetWindowSizes");
        this.resetWindowSizes.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if ("ResetWindowSizes".equals(e.getActionCommand())) {
                    GeneralPanel.this.om.resetWindowSizes();
                }

            }
        });
        this.resetWindowSizes.setToolTipText(this.bundle.getString("dialog.preferences.tooltip.resetWindowSizes"));
        gridbag.setConstraints(this.resetWindowSizes, constraints);
        super.add(this.resetWindowSizes);

        // ------------------

        ConstraintsBuilder.buildConstraints(constraints, 0, 5, 2, 1, 100, 40);
        constraints.fill = GridBagConstraints.BOTH;
        JLabel Lfill = new JLabel("");
        gridbag.setConstraints(Lfill, constraints);
        super.add(Lfill);

    }

    private void createXslTemplateBox() {

        this.xslTemplate = new JComboBox();

        File path = new File(this.om.getInstallDir().getAbsolutePath() + File.separator + "xsl");
        if (!path.exists()) { // Should never happen in a correct installation
            return;
        }

        // Get all directories and add them to the JComboBox
        String[] directories = path.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {

                File file = new File(dir.getAbsolutePath() + File.separator + name);
                return file.isDirectory() && !"CVS".equals(file.getName()); // For developers ;-)

            }
        });

        for (int i = 0; i < directories.length; i++) {
            this.xslTemplate.addItem(directories[i]);
        }

        if (directories.length > 0) {
            this.xslTemplate.setSelectedItem(
                    this.om.getConfiguration().getConfig(ObservationManager.CONFIG_XSL_TEMPLATE, directories[0]));
        } else {
            this.xslTemplate
                    .setSelectedItem(this.om.getConfiguration().getConfig(ObservationManager.CONFIG_XSL_TEMPLATE));
        }

    }

    // Installed languages get determined by accessing the observationManager.jar
    // and getting all locales for ObservationManager_??.properties
    private List getInstalledLanguages() {

        ArrayList result = new ArrayList();

        // Get JARs from classpath
        String sep = System.getProperty("path.separator");
        String path = System.getProperty("java.class.path");

        StringTokenizer tokenizer = new StringTokenizer(path, sep);

        File token = null;
        while (tokenizer.hasMoreTokens()) {
            token = new File(tokenizer.nextToken());

            if ((token.isFile()) && ("observationManager.jar".equals(token.getName()))) {
                result.addAll(scanJarFile(token));
                return result;
            }
        }

        // Get JARs under extension path
        String extPath = System.getProperty("java.ext.dirs");
        File ext = new File(extPath);
        if (ext.exists()) {
            File[] jars = ext.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {

                    if ("observationManager.jar".equals(name))
                        return true;

                    return false;
                }
            });

            if (jars != null) {
                for (int i = 0; i < jars.length; i++) {
                    result.addAll(scanJarFile(jars[i]));
                    return result;
                }
            }
        }

        return result;

    }

    private ArrayList scanJarFile(File jarFile) {

        ArrayList result = new ArrayList();

        ZipFile archive = null;
        try {
            archive = new ZipFile(jarFile);
        } catch (ZipException zipEx) {
            System.err.println("Error while accessing JAR file.\n" + zipEx);
            return null;
        } catch (IOException ioe) {
            System.err.println("Error while accessing JAR file.\n" + ioe);
            return null;
        }

        Enumeration enu = archive.entries();
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

    }

}