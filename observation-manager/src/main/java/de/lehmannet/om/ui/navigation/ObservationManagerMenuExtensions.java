package de.lehmannet.om.ui.navigation;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.lehmannet.om.ui.dialog.ExtensionInfoDialog;
import de.lehmannet.om.ui.extension.ExtensionLoader;
import de.lehmannet.om.ui.i18n.TextManager;
import de.lehmannet.om.ui.image.ImageResolver;
import de.lehmannet.om.ui.util.ConfigKey;
import de.lehmannet.om.ui.util.IConfiguration;
import de.lehmannet.om.ui.util.UserInterfaceHelper;
import de.lehmannet.om.ui.util.XMLFileLoader;

public final class ObservationManagerMenuExtensions {

    private final Logger LOGGER = LoggerFactory.getLogger(ObservationManagerMenuExtensions.class);

    private final IConfiguration configuration;
    private final ObservationManager observationManager;
    private final JMenu menu;
    private final ExtensionLoader extensionLoader;
    private final ImageResolver imageResolver;
    private final TextManager textManager;
    private final UserInterfaceHelper uiHelper;

    public ObservationManagerMenuExtensions(IConfiguration configuration, 
            ExtensionLoader extLoader, ImageResolver imageResolver,
            TextManager textManager, UserInterfaceHelper uiHelper, ObservationManager om) {

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

    private void installExtension(File[] files) {

        // No files passed, so need to ask user for list of extensions
        if (files == null) {

            // Let user choose extension zip file
            JFileChooser chooser = new JFileChooser(this.textManager.getString("extenstion.chooser.title"));
            FileFilter zipFileFilter = new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return (f.getName().endsWith(".ome")) || (f.isDirectory());
                }

                @Override
                public String getDescription() {
                    return "Observation Manager extensions";
                }
            };
            chooser.setFileFilter(zipFileFilter);
            String last = this.configuration.getConfig(ConfigKey.CONFIG_LASTDIR);
            if ((last != null) && !("".equals(last.trim()))) {
                File dir = new File(last);
                if (dir.exists()) {
                    chooser.setCurrentDirectory(dir);
                }
            }
            chooser.setMultiSelectionEnabled(true);
            int returnVal = chooser.showOpenDialog(this.observationManager);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                files = chooser.getSelectedFiles();
            } else {
                return;
            }

        }

        // Check whether deployment can be done -> whether we've write
        // permissions for all files
        StringBuilder negativeResult = new StringBuilder();
        List<File> filesOK = new ArrayList<>();
        try {
            boolean checkResult = false;
            for (File file : files) {
                checkResult = this.checkWriteAccess(new ZipFile(file),
                        this.observationManager.getInstallDir().getInstallDir());
                if (!checkResult) {
                    negativeResult.append(" ").append(file.getName());
                } else {
                    filesOK.add(file);
                }
            }
        } catch (IOException ioe) {
            System.out.println("Error while checking extension zip file. Zip file may be corrupted.\n" + ioe);
        }
        File[] filesCheckedOK = (File[]) filesOK.toArray(new File[] {});

        // --- Start with deployment

        Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
        this.observationManager.setCursor(hourglassCursor);

        StringBuilder positiveResult = new StringBuilder();
        int successCounter = 0;
        for (int i = 0; i < filesCheckedOK.length; i++) {
            try {
                positiveResult.append(" ").append(
                        this.observationManager.getExtensionLoader().addExtension(new ZipFile(filesCheckedOK[i])));
                successCounter++;
                if (i < filesCheckedOK.length - 1) { // There is at least one
                                                     // more ZIP to add
                    positiveResult.append(", ");
                }
            } catch (IOException ioe) {
                System.out.println("Error in extension zip file. Zip file may be corrupted.\n" + ioe);

                Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
                this.observationManager.setCursor(normalCursor);

                negativeResult.append(" ").append(filesCheckedOK[i].getName());
            }
        }

        // Show all positive results
        if (successCounter > 0) {
            this.observationManager
                    .createInfo(this.textManager.getString("info.addExtensionSuccess") + " " + positiveResult);

            // Until we found a better way to handle extension, we need to
            // restart... :-(
            if (true) {
                this.observationManager.createInfo(this.textManager.getString("info.addExtensionRestart"));
                // this.exit();
                this.observationManager.exit();
            }
        }

        // Show all negative results
        if (successCounter < files.length) { // We check here against the
                                             // original files Array, to see
                                             // whether we had some
                                             // problems during check OR
                                             // installation
            this.observationManager.createWarning(
                    this.textManager.getString("error.addExtensionFail") + " " + negativeResult);

        }

        // Inform about restart (if any installation was successfull)
        /*
         * if( successCounter > 0 ) { // Until we found a better way to handle
         * extension, we need to restart... :-( this.createInfo(ObservationManager
         * .bundle.getString("info.addExtensionRestart")); this.exit(); }
         */

        Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
        this.observationManager.setCursor(normalCursor);

    }

    private boolean checkWriteAccess(ZipFile zipFile, File destinationRoot) {

        Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
        ZipEntry ze;

        // Unpack all the ZIP file entries into install dir
        File currentFile;
        boolean result = true;
        while (enumeration.hasMoreElements()) {

            ze = (ZipEntry) enumeration.nextElement();
            currentFile = this.getDestinationFile(ze.getName(), destinationRoot, false);

            if (currentFile != null) {
                while (!currentFile.exists()) { // New file/folder, which
                                                // doesn't exist so far
                    currentFile = new File(currentFile.getParent()); // Check
                                                                     // write
                                                                     // permission
                                                                     // on
                                                                     // parent
                }

                if (!currentFile.canWrite()) { // We've found at least one file,
                                               // which we would need to
                                               // overwrite, but do not
                                               // have the permission to
                    System.err.println("Write check failed for: " + currentFile);
                    result = false;
                }
            }

        }

        return result;

    }

    private File getDestinationFile(String filename, File destinationFolder, boolean removeRootFolder) {

        if (removeRootFolder) {
            // Remove root folder
            filename = filename.substring(filename.indexOf("/") + 1);

            if ("".equals(filename)) { // That must have been the root folder
                return null;
            }
        }

        return new File(
                destinationFolder.getAbsolutePath() + File.separator + /* "testing" + File.separator + */filename);

    }

    public boolean checkWriteAccess(File file) {

        return file.canWrite();

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

        JMenuItem installExtension = new JMenuItem(this.textManager.getString("menu.installExtension"),
                new ImageIcon(this.imageResolver.getImageURL("extension.png").orElse(null), ""));
        installExtension.setMnemonic('i');
        installExtension.addActionListener(new AddExtensionListener());
        extensionMenu.add(installExtension);

        return extensionMenu;
    }

    private class ExtensionInfoListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
          
            ObservationManagerMenuExtensions.this.uiHelper.showInfo(
                ObservationManagerMenuExtensions.this.textManager.getString("info.noExtensionsInstalled"));
          
            if (extensionLoader.getExtensions().isEmpty()) {
                ObservationManagerMenuExtensions.this.uiHelper.showInfo(
                    ObservationManagerMenuExtensions.this.textManager.getString("info.noExtensionsInstalled"));
            } else {
                new ExtensionInfoDialog(ObservationManagerMenuExtensions.this.observationManager);
            }

        }

    }

    private class AddExtensionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            ObservationManagerMenuExtensions.this.installExtension(null);

        }

    }
}