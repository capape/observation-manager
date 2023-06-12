package de.lehmannet.om.ui.navigation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.lehmannet.om.ui.dialog.ExtensionInfoDialog;
import de.lehmannet.om.ui.extension.ExtensionLoader;
import de.lehmannet.om.ui.i18n.TextManager;
import de.lehmannet.om.ui.image.ImageResolver;
import de.lehmannet.om.ui.util.IConfiguration;
import de.lehmannet.om.ui.util.UserInterfaceHelper;

public final class ObservationManagerMenuExtensions {

    private final Logger LOGGER = LoggerFactory.getLogger(ObservationManagerMenuExtensions.class);

    private final IConfiguration configuration;
    private final ObservationManager observationManager;
    private final JMenu menu;
    private final ExtensionLoader extensionLoader;
    private final ImageResolver imageResolver;
    private final TextManager textManager;
    private final UserInterfaceHelper uiHelper;

    public ObservationManagerMenuExtensions(IConfiguration configuration, ExtensionLoader extLoader,
            ImageResolver imageResolver, TextManager textManager, UserInterfaceHelper uiHelper, ObservationManager om) {

        // Load configuration
        this.configuration = configuration;
        this.observationManager = om;
        this.extensionLoader = extLoader;
        this.imageResolver = imageResolver;
        this.textManager = textManager;
        this.uiHelper = uiHelper;
        this.menu = this.createMenuExtensionItems();

    }

    public JMenu getMenu() {
        return menu;
    }

    private JMenu createMenuExtensionItems() {
        // ----- Extensions Menu
        final JMenu extensionMenu = new JMenu(this.textManager.getString("menu.extension"));
        extensionMenu.setMnemonic('x');

        final JMenu[] menus = this.extensionLoader.getMenus();
        for (final JMenu menu : menus) {
            extensionMenu.add(menu);
        }

        if (menus.length != 0) {
            extensionMenu.addSeparator();
        }

        JMenuItem extensionInfo = new JMenuItem(this.textManager.getString("menu.extensionInfo"),
                new ImageIcon(this.imageResolver.getImageURL("extensionInfo.png").orElse(null), ""));
        extensionInfo.setMnemonic('p');
        extensionInfo.addActionListener(new ExtensionInfoListener());
        extensionMenu.add(extensionInfo);

        return extensionMenu;
    }

    private class ExtensionInfoListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            if (extensionLoader.getExtensions().isEmpty()) {
                ObservationManagerMenuExtensions.this.uiHelper.showInfo(
                        ObservationManagerMenuExtensions.this.textManager.getString("info.noExtensionsInstalled"));
            } else {
                new ExtensionInfoDialog(ObservationManagerMenuExtensions.this.observationManager);
            }

        }

    }

}