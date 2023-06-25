package de.lehmannet.om.ui.navigation;

// import java.io.File;
// import java.io.IOException;
// import java.util.ArrayList;
// import java.util.Enumeration;
// import java.util.List;
// import java.util.zip.ZipEntry;
// import java.util.zip.ZipFile;

// import javax.swing.ImageIcon;
// import javax.swing.JFileChooser;
// import javax.swing.JMenu;
// import javax.swing.JMenuItem;

// import de.lehmannet.om.ui.util.ConfigKey;

/**
 * Transtion class
 */
public class ObservationManagerMenuExternalExtensions {

    // private void installExtension(File[] files) {

    // // No files passed, so need to ask user for list of extensions
    // if (files == null) {

    // // Let user choose extension zip file
    // JFileChooser chooser = new JFileChooser(this.textManager.getString("extenstion.chooser.title"));
    // FileFilter zipFileFilter = new FileFilter() {
    // @Override
    // public boolean accept(File f) {
    // return (f.getName().endsWith(".ome")) || (f.isDirectory());
    // }

    // @Override
    // public String getDescription() {
    // return "Observation Manager extensions";
    // }
    // };
    // chooser.setFileFilter(zipFileFilter);
    // String last = this.configuration.getConfig(ConfigKey.CONFIG_LASTDIR);
    // if ((last != null) && !("".equals(last.trim()))) {
    // File dir = FileSystems.getDefault().getPath(last).toFile();
    // if (dir.exists()) {
    // chooser.setCurrentDirectory(dir);
    // }
    // }
    // chooser.setMultiSelectionEnabled(true);
    // int returnVal = chooser.showOpenDialog(this.observationManager);
    // if (returnVal == JFileChooser.APPROVE_OPTION) {
    // files = chooser.getSelectedFiles();
    // } else {
    // return;
    // }

    // }

    // // Check whether deployment can be done -> whether we've write
    // // permissions for all files
    // StringBuilder negativeResult = new StringBuilder();
    // List<File> filesOK = new ArrayList<>();
    // try {
    // boolean checkResult = false;
    // for (File file : files) {
    // checkResult = this.checkWriteAccess(new ZipFile(file),
    // this.observationManager.getInstallDir().getInstallDir());
    // if (!checkResult) {
    // negativeResult.append(" ").append(file.getName());
    // } else {
    // filesOK.add(file);
    // }
    // }
    // } catch (IOException ioe) {
    // LOGGER.error("Error while checking extension zip file. Zip file may be corrupted.", ioe);
    // }
    // File[] filesCheckedOK = (File[]) filesOK.toArray(new File[] {});

    // // --- Start with deployment

    // Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
    // this.observationManager.setCursor(hourglassCursor);

    // StringBuilder positiveResult = new StringBuilder();
    // int successCounter = 0;
    // for (int i = 0; i < filesCheckedOK.length; i++) {
    // try {
    // this.observationManager.getExtensionLoader().addExternalExtension(new ZipFile(filesCheckedOK[i]));
    // positiveResult.append(" ");
    // successCounter++;
    // if (i < filesCheckedOK.length - 1) { // There is at least one
    // // more ZIP to add
    // positiveResult.append(", ");
    // }
    // } catch (IOException ioe) {
    // LOGGER.error("Error in extension zip file. Zip file may be corrupted.", ioe);

    // Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
    // this.observationManager.setCursor(normalCursor);

    // negativeResult.append(" ").append(filesCheckedOK[i].getName());
    // }
    // }

    // // Show all positive results
    // if (successCounter > 0) {
    // this.observationManager
    // .createInfo(this.textManager.getString("info.addExtensionSuccess") + " " + positiveResult);

    // // Until we found a better way to handle extension, we need to
    // // restart... :-(
    // if (true) {
    // this.observationManager.createInfo(this.textManager.getString("info.addExtensionRestart"));
    // // this.exit();
    // this.observationManager.exit();
    // }
    // }

    // // Show all negative results
    // if (successCounter < files.length) { // We check here against the
    // // original files Array, to see
    // // whether we had some
    // // problems during check OR
    // // installation
    // this.observationManager
    // .createWarning(this.textManager.getString("error.addExtensionFail") + " " + negativeResult);

    // }

    // // Inform about restart (if any installation was successfull)
    // /*
    // * if( successCounter > 0 ) { // Until we found a better way to handle extension, we need to restart... :-(
    // * this.createInfo(ObservationManager .bundle.getString("info.addExtensionRestart")); this.exit(); }
    // */

    // Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
    // this.observationManager.setCursor(normalCursor);

    // }

    // private boolean checkWriteAccess(ZipFile zipFile, File destinationRoot) {

    // Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
    // ZipEntry ze;

    // // Unpack all the ZIP file entries into install dir
    // File currentFile;
    // boolean result = true;
    // while (enumeration.hasMoreElements()) {

    // ze = (ZipEntry) enumeration.nextElement();
    // currentFile = this.getDestinationFile(ze.getName(), destinationRoot, false);

    // if (currentFile != null) {
    // while (!currentFile.exists()) { // New file/folder, which
    // // doesn't exist so far
    // currentFile = FileSystems.getDefault().getPath(currentFile.getParent()).toFile(); // Check
    // // write
    // // permission
    // // on
    // // parent
    // }

    // if (!currentFile.canWrite()) { // We've found at least one file,
    // // which we would need to
    // // overwrite, but do not
    // // have the permission to
    // LOGGER.error("Write check failed for: {}" , currentFile);
    // result = false;
    // }
    // }

    // }

    // return result;

    // }

    // private File getDestinationFile(String filename, File destinationFolder, boolean removeRootFolder) {

    // if (removeRootFolder) {
    // // Remove root folder
    // filename = filename.substring(filename.indexOf("/") + 1);

    // if ("".equals(filename)) { // That must have been the root folder
    // return null;
    // }
    // }

    // return new File(
    // destinationFolder.getAbsolutePath() + File.separator + /* "testing" + File.separator + */filename);

    // }

    // private JMenu createMenuExtensionItems() {
    // // ----- Extensions Menu
    // final JMenu extensionMenu = new JMenu(this.textManager.getString("menu.extension"));
    // extensionMenu.setMnemonic('x');

    // final JMenu[] menus = this.extensionLoader.getMenus();
    // for (final JMenu menu : menus) {
    // extensionMenu.add(menu);
    // }

    // if (menus.length != 0) {
    // extensionMenu.addSeparator();
    // }

    // JMenuItem extensionInfo = new JMenuItem(this.textManager.getString("menu.extensionInfo"),
    // new ImageIcon(this.imageResolver.getImageURL("extensionInfo.png").orElse(null), ""));
    // extensionInfo.setMnemonic('p');
    // extensionInfo.addActionListener(new ExtensionInfoListener());
    // extensionMenu.add(extensionInfo);

    // // TODO: implement new extension loader
    // // JMenuItem installExtension = new JMenuItem(this.textManager.getString("menu.installExtension"),
    // // new ImageIcon(this.imageResolver.getImageURL("extension.png").orElse(null), ""));
    // // installExtension.setMnemonic('i');
    // // installExtension.addActionListener(new AddExtensionListener());
    // // extensionMenu.add(installExtension);

    // return extensionMenu;
    // }

    // private class AddExtensionListener implements ActionListener {

    // @Override
    // public void actionPerformed(ActionEvent e) {
    // ObservationManagerMenuExtensions.this.installExtension(null);

    // }

    // }

    // public boolean checkWriteAccess(File file) {

    // return file.canWrite();

    // }
}
