package de.lehmannet.om.ui.navigation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.lehmannet.om.ui.dialog.DidYouKnowDialog;
import de.lehmannet.om.ui.dialog.LogDialog;
import de.lehmannet.om.ui.image.ImageResolver;
import de.lehmannet.om.ui.preferences.PreferencesDialog;
import de.lehmannet.om.ui.statistics.StatisticsDialog;
import de.lehmannet.om.ui.theme.ThemeManager;
import de.lehmannet.om.ui.update.UpdateChecker;
import de.lehmannet.om.ui.update.UpdateInfoDialog;
import de.lehmannet.om.ui.util.IConfiguration;
import de.lehmannet.om.ui.util.XMLFileLoader;

public final class ObservationManagerMenuExtras {

    private final Logger LOGGER = LoggerFactory.getLogger(ObservationManagerMenuExtras.class);

    private final XMLFileLoader xmlCache;
    private final IConfiguration configuration;
    private final ObservationManager observationManager;
    private final JMenu menu;
    private final ImageResolver imageResolver;
    private final ThemeManager themeManager;


    public ObservationManagerMenuExtras(        
        IConfiguration configuration,
        XMLFileLoader xmlCache,
        ImageResolver imageResolver,
        ThemeManager themeManager,
        ObservationManager om) {
       
        // Load configuration
        this.configuration = configuration; 
        this.xmlCache = xmlCache;
        this.observationManager = om;
        this.imageResolver = imageResolver;
        this.themeManager = themeManager;

        this.menu = this.createMenuExtraItems();
 
    }

    public JMenu getMenu() {
        return this.menu;
    }

    public void enableNightVisionTheme(boolean enable) {

        LOGGER.debug("Night vision enabled: {}", enable);

        if (enable) { // Turn on night vision theme
            this.themeManager.enableNightVision();
        } else { // Turn off night vision theme
            this.themeManager.disableNightVision();
        }

    }

    public void showStatistics() {

        if (this.observationManager.getXmlCache().getObservations().length == 0) {
            this.observationManager.createWarning(ObservationManager.bundle.getString("error.noStatisticsData"));
            return;
        }

        if (this.observationManager.getExtensionLoader().getExtensions().isEmpty()) {
            this.observationManager.createInfo(ObservationManager.bundle.getString("info.noCatalogsInstalled"));
            return;
        }

        new StatisticsDialog(this.observationManager);

    }

    public void showPreferencesDialog() {

        new PreferencesDialog(this.observationManager, this.observationManager.getExtensionLoader().getPreferencesTabs());

    }

    public void showDidYouKnow() {

        new DidYouKnowDialog(this.observationManager);

    }

    private File logFile;

  

    public void showLogDialog() {

        new LogDialog(this.observationManager, this.logFile);

    }

    public void checkUpdates() {
        UpdateChecker checker = this.checkForUpdates();
        if (checker.isUpdateAvailable()) {
            new UpdateInfoDialog(this.observationManager, checker);

        } else { // Something went wrong
            this.observationManager.createInfo(ObservationManager.bundle.getString("updates.check.noAvailable"));

        }
    }

    private UpdateChecker checkForUpdates() {

        // The updateChecker
        UpdateChecker updateChecker = new UpdateChecker(this.observationManager);

        updateChecker.run();
        
        return updateChecker;

    }

    private JMenu createMenuExtraItems() {
        // ----- Extras Menu
        final JMenu extraMenu = new JMenu(ObservationManager.bundle.getString("menu.extra"));
        extraMenu.setMnemonic('e');
      

        JMenuItem  showStatistics = new JMenuItem(ObservationManager.bundle.getString("menu.showStatistics"),
                new ImageIcon(this.imageResolver.getImageURL("statistic.png").orElse(null), ""));
        showStatistics.setMnemonic('s');
        showStatistics.addActionListener(new StatisticsActionListener());
        extraMenu.add(showStatistics);

        JMenuItem preferences = new JMenuItem(ObservationManager.bundle.getString("menu.preferences"),
                new ImageIcon(this.imageResolver.getImageURL("preferences.png").orElse(null), ""));
        preferences.setMnemonic('p');
        preferences.addActionListener(new PreferencesActionListener());
        extraMenu.add(preferences);

        extraMenu.addSeparator();

        JMenuItem didYouKnow = new JMenuItem(ObservationManager.bundle.getString("menu.didYouKnow"),
                new ImageIcon(this.imageResolver.getImageURL("questionMark.png").orElse(null), ""));
        didYouKnow.setMnemonic('d');
        didYouKnow.addActionListener(new DidYouKnowDialogActionListener());
        didYouKnow.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        extraMenu.add(didYouKnow);

        extraMenu.addSeparator();

        JMenuItem nightVision = new JCheckBoxMenuItem(ObservationManager.bundle.getString("menu.nightVision"));
        nightVision.setMnemonic('v');
        nightVision.addActionListener(new NightVisionActionListener());
        extraMenu.add(nightVision);
        extraMenu.addSeparator();

        JMenuItem logMenuEntry = new JMenuItem(ObservationManager.bundle.getString("menu.log"),
                new ImageIcon(this.imageResolver.getImageURL("logviewer.png").orElse(null), ""));
        logMenuEntry.setMnemonic('l');
        logMenuEntry.addActionListener(new LogMenuActionListener());
        extraMenu.add(logMenuEntry);

        extraMenu.addSeparator();

        JMenuItem updateMenuEntry = new JMenuItem(ObservationManager.bundle.getString("menu.updateCheck"),
                new ImageIcon(this.imageResolver.getImageURL("updater.png").orElse(null), ""));
        updateMenuEntry.setMnemonic('u');
        updateMenuEntry.addActionListener(new UpdateMenuListener());
        extraMenu.add(updateMenuEntry);
        return extraMenu;
    }

    class StatisticsActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            ObservationManagerMenuExtras.this.showStatistics();

        }

    }

    class PreferencesActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            ObservationManagerMenuExtras.this.showPreferencesDialog();
        }
    }
    class DidYouKnowDialogActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            ObservationManagerMenuExtras.this.showDidYouKnow();

        }
    }
    class NightVisionActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (ObservationManagerMenuExtras.this.themeManager.isNightVision()) {
                ObservationManagerMenuExtras.this.enableNightVisionTheme(false);
            } else {
                ObservationManagerMenuExtras.this.enableNightVisionTheme(true);
            }
        }
    }
    class LogMenuActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            ObservationManagerMenuExtras.this.showStatistics();

        }
    }
    class UpdateMenuListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            ObservationManagerMenuExtras.this.checkUpdates();

        }
    }


}