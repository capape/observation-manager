package de.lehmannet.om.ui.navigation;

import de.lehmannet.om.ui.dialog.AboutDialog;
import de.lehmannet.om.ui.i18n.TextManager;
import de.lehmannet.om.ui.util.IConfiguration;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ObservationManagerMenuHelp {

    private final Logger LOGGER = LoggerFactory.getLogger(ObservationManagerMenuHelp.class);

    private final IConfiguration configuration;
    private final ObservationManager observationManager;
    private JMenu aboutMenu;

    private final TextManager textManager;
    private final TextManager versionTextManager;

    public ObservationManagerMenuHelp(
            IConfiguration configuration,
            TextManager textManager,
            TextManager versionTextManager,
            ObservationManager om) {

        // Load configuration
        this.configuration = configuration;
        this.observationManager = om;
        this.textManager = textManager;
        this.versionTextManager = versionTextManager;

        this.aboutMenu = this.createMenuAboutItems();
    }

    public JMenu getMenu() {
        return aboutMenu;
    }

    private JMenu createMenuAboutItems() {
        // ----- About Menu
        final JMenu aboutMenu = new JMenu(this.textManager.getString("menu.about"));
        aboutMenu.setMnemonic('a');

        final JMenuItem aboutInfo = new JMenuItem(
                this.textManager.getString("menu.aboutOM"),
                new ImageIcon(
                        this.observationManager
                                .getImageResolver()
                                .getImageURL("about.png")
                                .orElse(null),
                        ""));
        aboutInfo.setMnemonic('i');
        aboutInfo.addActionListener(new AboutInfoListener());
        aboutMenu.add(aboutInfo);
        return aboutMenu;
    }

    class AboutInfoListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            new AboutDialog(
                    ObservationManagerMenuHelp.this.observationManager,
                    ObservationManagerMenuHelp.this.textManager,
                    ObservationManagerMenuHelp.this.versionTextManager);
        }
    }
}
