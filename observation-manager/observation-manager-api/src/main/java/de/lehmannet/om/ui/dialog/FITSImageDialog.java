/*
 * ====================================================================
 * /dialog/FITSImageDialog.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */
package de.lehmannet.om.ui.dialog;

import de.lehmannet.om.ui.util.LocaleToolsFactory;
import eap.fitsbrowser.FITSFileDisplay;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.WindowConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FITSImageDialog extends OMDialog implements ActionListener {

    private final Logger LOGGER = LoggerFactory.getLogger(FITSImageDialog.class);

    private static final long serialVersionUID = 5090506213345186056L;

    private final ResourceBundle bundle =
            LocaleToolsFactory.appInstance().getBundle("ObservationManager", Locale.getDefault());

    private File fitsImageFile = null;

    public FITSImageDialog(JFrame om, File fitsFile) {

        super(om);

        this.fitsImageFile = fitsFile;

        this.setTitle(this.bundle.getString("dialog.image.title"));

        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(om);

        this.initDialog();

        this.pack();
        this.setVisible(true);
    }

    private void initDialog() {

        // Add Menu for close operation
        JMenuBar menuBar = new JMenuBar();

        // ----- File Menu
        JMenu windowMenu = new JMenu(this.bundle.getString("dialog.image.menu.window"));
        windowMenu.setMnemonic('w');
        menuBar.add(windowMenu);

        JMenuItem close = new JMenuItem(this.bundle.getString("dialog.image.menu.window.close"));
        close.setMnemonic('c');
        close.addActionListener(this);
        windowMenu.add(close);

        this.setJMenuBar(menuBar);

        // Init FITSFileDisplay and load fits file
        FITSFileDisplay fitsDisplay = new FITSFileDisplay();
        try {
            fitsDisplay.load(this.fitsImageFile);
        } catch (IOException ioe) {
            LOGGER.error("Error while loading fits file: {}", this.fitsImageFile);
        }

        fitsDisplay.setPreferredSize(new Dimension(608, 337));
        this.getContentPane().add(fitsDisplay);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // No need to do many checks...we have only on menu entry
        if (e.getSource() instanceof JMenuItem) {
            this.dispose();
        }
    }
}
