package de.lehmannet.om.ui.navigation;

import java.lang.reflect.Constructor;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.lehmannet.om.ui.preferences.PreferencesDialog;
import de.lehmannet.om.ui.statistics.StatisticsDialog;
import de.lehmannet.om.ui.util.Configuration;
import de.lehmannet.om.ui.util.XMLFileLoader;

public final class ObservationManagerMenuExtras {

    private final Logger LOGGER = LoggerFactory.getLogger(ObservationManagerMenuExtras.class);

    private final XMLFileLoader xmlCache;
    private final Configuration configuration;
    private final ObservationManager observationManager;
    

    public ObservationManagerMenuExtras(        
        Configuration configuration,
        XMLFileLoader xmlCache,
        ObservationManager om) {
       
        // Load configuration
        this.configuration = configuration; 
        this.xmlCache = xmlCache;
        this.observationManager = om;
 
    }

    public void enableNightVisionTheme(boolean enable) {

        if (enable) { // Turn on night vision theme

            try {
                // Check for Metal LAF
                LookAndFeelInfo[] laf = UIManager.getInstalledLookAndFeels();
                boolean found = false;
                for (LookAndFeelInfo lookAndFeelInfo : laf) {
                    if ("metal".equals(lookAndFeelInfo.getName().toLowerCase())) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    System.err.println(ObservationManager.bundle.getString("error.noMetalLAF"));
                    this.observationManager.createWarning(ObservationManager.bundle.getString("error.noNightVision"));
                    return;
                }

                // Try to load MetalLookAndFeel
                MetalLookAndFeel.setCurrentTheme(new NightVisionTheme());
                UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                SwingUtilities.updateComponentTreeUI(this.observationManager);

                // Make all frames and dialogs use the LookAndFeel

                JFrame.setDefaultLookAndFeelDecorated(true);
                JDialog.setDefaultLookAndFeelDecorated(true);
                this.observationManager.dispose();
                this.observationManager.setUndecorated(true);
                this.observationManager.addNotify();
                this.observationManager.createBufferStrategy(2);
                this.observationManager.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
                this.observationManager.update(this.observationManager.getGraphics());
                this.configuration.setConfig(ObservationManager.CONFIG_NIGHTVISION_ENABLED, Boolean.toString(true));
                this.observationManager.setVisible(true);

            } catch (Exception e) {
                System.err.println(e);
                this.observationManager.createWarning(ObservationManager.bundle.getString("error.noNightVision"));
            }

        } else { // Turn off night vision theme
            try {

                // Try to load (default) OceanThema (available since Java 1.5)
                // with relfection
                Class themeClass = null;
                try {
                    themeClass = ClassLoader.getSystemClassLoader().loadClass("javax.swing.plaf.metal.OceanTheme");
                } catch (ClassNotFoundException cnfe) {
                    // Can do nothing in here...defaultMetalTheme will be
                    // loaded...
                }

                boolean problem = true;
                if (themeClass != null) { // Check if load OceanTheme succeeded
                    Constructor[] constructors = themeClass.getConstructors();
                    if (constructors.length > 0) {
                        Class[] parameters = null;
                        for (Constructor constructor : constructors) {
                            parameters = constructor.getParameterTypes();
                            if (parameters.length == 0) { // Use default
                                // constructor and
                                // set theme
                                MetalTheme theme = (MetalTheme) constructor.newInstance(null);
                                MetalLookAndFeel.setCurrentTheme(theme);
                                problem = false; // No problem -> no need to
                                // load DefaultMetalTheme as
                                // we can use OceanTheme
                                break;
                            }
                        }
                    }
                }

                if (problem) { // Ocean Theme cannot be used for whatever reason
                    MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
                }

                UIManager.setLookAndFeel(new MetalLookAndFeel());
                SwingUtilities.updateComponentTreeUI(this.observationManager);

                // Make all frames and dialogs use the LookAndFeel
                JFrame.setDefaultLookAndFeelDecorated(false);
                JDialog.setDefaultLookAndFeelDecorated(false);
                this.observationManager.dispose();
                this.observationManager.setUndecorated(false);
                this.observationManager.addNotify();
                this.observationManager.createBufferStrategy(2);
                this.observationManager.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
                this.observationManager.update(this.observationManager.getGraphics());
                this.configuration.setConfig(ObservationManager.CONFIG_NIGHTVISION_ENABLED, Boolean.toString(false));
                this.observationManager.setVisible(true);

            } catch (Exception e) {
                System.err.println(e);
            }
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

}