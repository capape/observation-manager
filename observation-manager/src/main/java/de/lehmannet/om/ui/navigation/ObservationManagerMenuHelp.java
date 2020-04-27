package de.lehmannet.om.ui.navigation;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.lehmannet.om.ui.dialog.AboutDialog;
import de.lehmannet.om.ui.util.IConfiguration;
import de.lehmannet.om.ui.util.XMLFileLoader;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class ObservationManagerMenuHelp {

    private final Logger LOGGER = LoggerFactory.getLogger(ObservationManagerMenuHelp.class);

    private final IConfiguration configuration;
    private final ObservationManager observationManager;
    private JMenu aboutMenu;

    public ObservationManagerMenuHelp(IConfiguration configuration, ObservationManager om) {

        // Load configuration
        this.configuration = configuration;
        this.observationManager = om;

        this.aboutMenu = this.createMenuAboutItems();

    }

    public JMenu getMenu() {
        return aboutMenu;
    }

    private JMenu createMenuAboutItems() {
        // ----- About Menu
        final JMenu aboutMenu = new JMenu(ObservationManager.bundle.getString("menu.about"));
        aboutMenu.setMnemonic('a');

        final JMenuItem aboutInfo = new JMenuItem(ObservationManager.bundle.getString("menu.aboutOM"),
                new ImageIcon(this.observationManager.getImageResolver().getImageURL("about.png").orElse(null), ""));
        aboutInfo.setMnemonic('i');
        aboutInfo.addActionListener(new AboutInfoListener());
        aboutMenu.add(aboutInfo);
        return aboutMenu;
    }

    class AboutInfoListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            new AboutDialog(ObservationManagerMenuHelp.this.observationManager);
        }
    }
}