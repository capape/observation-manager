package de.lehmannet.om.ui.theme;

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

import de.lehmannet.om.ui.i18n.TextManager;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.util.ConfigKey;
import de.lehmannet.om.ui.util.IConfiguration;

public class ThemeManagerImpl implements ThemeManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThemeManagerImpl.class);

    private boolean nightVision = false;
    private final ObservationManager observationManager;
    private final IConfiguration configuration;
    private final TextManager textManager;

    private static NightVisionTheme NIGHT_THEME = new NightVisionTheme();

    public ThemeManagerImpl(IConfiguration config, TextManager textManager, ObservationManager om) {
        this.observationManager = om;
        this.configuration = config;
        this.textManager = textManager;
    }

    @Override
    public boolean isNightVision() {

        return this.nightVision;
    }

    @Override
    public void setTheme(String theme) {
        // TODO Auto-generated method stub

    }

    @Override
    public void enableNightVision() {
        this.nightVision = true;
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
                LOGGER.error(this.textManager.getString("error.noMetalLAF"));
                this.observationManager.createWarning(this.textManager.getString("error.noNightVision"));
                return;
            }

            // Try to load MetalLookAndFeel
            MetalLookAndFeel.setCurrentTheme(NIGHT_THEME);
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
            this.configuration.setConfig(ConfigKey.CONFIG_NIGHTVISION_ENABLED, Boolean.toString(true));
            this.observationManager.setVisible(true);

        } catch (Exception e) {
            LOGGER.error("Error in theme", e);
            this.observationManager.createWarning(this.textManager.getString("error.noNightVision"));
        }

    }

    @Override
    public void disableNightVision() {
        this.nightVision = false;
        try {

            // Try to load (default) OceanThema (available since Java 1.5)
            // with relfection
            Class<?> themeClass = null;
            try {
                themeClass = ClassLoader.getSystemClassLoader().loadClass("javax.swing.plaf.metal.OceanTheme");
            } catch (ClassNotFoundException cnfe) {
                // Can do nothing in here...defaultMetalTheme will be
                // loaded...
            }

            boolean problem = true;
            if (themeClass != null) { // Check if load OceanTheme succeeded
                Constructor<?>[] constructors = themeClass.getConstructors();
                if (constructors.length > 0) {

                    for (Constructor<?> constructor : constructors) {
                        if (constructor.getParameterTypes().length == 0) { // Use default
                            // constructor and
                            // set theme
                            MetalTheme theme = (MetalTheme) constructor.newInstance();
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
            this.configuration.setConfig(ConfigKey.CONFIG_NIGHTVISION_ENABLED, Boolean.toString(false));
            this.observationManager.setVisible(true);

        } catch (Exception e) {
            LOGGER.error("Error in theme", e);
        }
    }

}