/*
 * ====================================================================
 * /dialog/UpdateInfoDialog
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.update;

import de.lehmannet.om.ObservationManagerContext;
import de.lehmannet.om.ui.dialog.OMDialog;
import de.lehmannet.om.ui.dialog.ProgressDialog;
import de.lehmannet.om.ui.i18n.TextManager;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.Worker;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.*;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateInfoDialog extends OMDialog implements ActionListener {

    private static final long serialVersionUID = -6681965343558223755L;

    private final JButton close;
    private final JButton download;
    private JTable infoTable = null;

    private ObservationManager om = null;
    private List<UpdateEntry> updateEntries = null;
    private final TextManager textManager;

    public UpdateInfoDialog(ObservationManagerContext context, ObservationManager om, UpdateChecker updateChecker) {

        super(om);

        this.om = om;
        this.updateEntries = updateChecker.getResult();
        this.textManager = context.getTextManager();

        this.close = new JButton(this.textManager.getString("dialog.button.cancel"));
        this.download = new JButton(this.textManager.getString("updateInfo.button.download"));

        this.setTitle(this.textManager.getString("updateInfo.title"));
        this.setSize(serialVersionUID, 390, 180);
        this.setModal(true);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(om);

        this.initDialog();
        this.setVisible(true);
    }

    // --------------
    // ActionListener ---------------------------------------------------------
    // --------------

    @Override
    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();
        if (source instanceof JButton) {
            if (source.equals(this.close)) {
                this.dispose();
            } else if (source.equals(this.download)) {
                List<UpdateEntry> downloadList = ((UpdateTableModel) this.infoTable.getModel()).getSelected();
                if ((downloadList != null) && !downloadList.isEmpty()) {

                    // ---------- Where to save the files?
                    JFileChooser chooser = new JFileChooser();
                    chooser.setMultiSelectionEnabled(false);
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    int returnVal = chooser.showOpenDialog(this);
                    File directory = null;
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        directory = chooser.getSelectedFile();

                        // Download the selected files
                        boolean result = this.downloadFiles(downloadList, directory);

                        if (result) {
                            this.om.getUiHelper().showInfo(this.textManager.getString("updateInfo.download.success"));
                            this.dispose();
                        } else {
                            this.om.getUiHelper().showWarning(this.textManager.getString("updateInfo.download.error"));
                        }

                    } else {
                    }
                }
            }
        }
    }

    private void initDialog() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        this.getContentPane().setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 2, 5, 20, 98);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.BOTH;
        this.infoTable = new JTable(new UpdateTableModel(this.updateEntries, this.download, this.textManager));
        this.infoTable.setRowSelectionAllowed(false);
        this.infoTable.setDefaultRenderer(String.class, (table, value, isSelected, hasFocus, row, column) -> {
            DefaultTableCellRenderer cr = new DefaultTableCellRenderer();

            if ((column == 2) || (column == 3)) {
                cr.setHorizontalAlignment(SwingConstants.CENTER);
            }

            cr.setText(value.toString());

            return cr;
        });

        /*
         * TableColumn col0 = this.infoTable.getColumnModel().getColumn(0); TableColumn col1 =
         * this.infoTable.getColumnModel().getColumn(1); col0.setPreferredWidth(preferredWidth)((int)(col0.getWidth() +
         * col1.getWidth() / 1.5));
         */

        JScrollPane scrollPane = new JScrollPane(this.infoTable);
        gridbag.setConstraints(scrollPane, constraints);
        this.getContentPane().add(scrollPane);

        ConstraintsBuilder.buildConstraints(constraints, 0, 5, 1, 1, 20, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.download.addActionListener(this);
        gridbag.setConstraints(this.download, constraints);
        this.getContentPane().add(this.download);

        ConstraintsBuilder.buildConstraints(constraints, 1, 5, 1, 1, 50, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.close.addActionListener(this);
        gridbag.setConstraints(this.close, constraints);
        this.getContentPane().add(this.close);
    }

    private boolean downloadFiles(List<UpdateEntry> updateEntries, File directory) {

        DownloadTask downloadTask = new DownloadTask(updateEntries, directory);

        new ProgressDialog(
                this.om,
                this.textManager.getString("updateInfo.downloadProgress.title"),
                this.textManager.getString("updateInfo.downloadProgress.information"),
                downloadTask);

        return downloadTask.getReturnType() == Worker.RETURN_TYPE_OK;
    }
}

class DownloadTask implements Worker {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadTask.class);

    private File targetDir = null;
    private List<UpdateEntry> updateEntries = null;

    private byte returnValue = Worker.RETURN_TYPE_OK;

    public DownloadTask(List<UpdateEntry> updateEntries, File targetDirectory) {

        this.updateEntries = updateEntries;
        this.targetDir = targetDirectory;
    }

    @Override
    public byte getReturnType() {

        return this.returnValue;
    }

    @Override
    public void run() {

        for (UpdateEntry currentEntry : this.updateEntries) {

            try {

                HttpURLConnection conn =
                        (HttpURLConnection) currentEntry.getDownloadURL().openConnection();
                conn.setRequestProperty("User-Agent", "Observation Manager Update Client");
                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) { // HTTP connection error
                    LOGGER.error(
                            "No download possible from: {}. HTTP Response was: {}",
                            currentEntry.getDownloadURL(),
                            conn.getResponseMessage());
                    conn.disconnect();
                    this.returnValue = Worker.RETURN_TYPE_ERROR;
                } else { // Download file

                    // Get download filename
                    String path = currentEntry.getDownloadURL().getPath(); // Get rid of query parameters
                    String filename = path.substring(path.lastIndexOf('/') + 1); // Get filename

                    BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                    FileOutputStream fos =
                            new FileOutputStream(this.targetDir.getAbsolutePath() + File.separator + filename);
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = bis.read(buf)) > 0) {
                        fos.write(buf, 0, len);
                    }

                    // Close streams and connections
                    bis.close();
                    fos.flush();
                    fos.close();
                    conn.disconnect();
                }

            } catch (IOException ioe) {
                LOGGER.error("Error while downloading file: {}", currentEntry.getDownloadURL(), ioe);
            }
        }

        this.returnValue = Worker.RETURN_TYPE_OK;
    }

    @Override
    public String getReturnMessage() {
        // TODO Auto-generated method stub
        return null;
    }
}

class UpdateTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 3059700226953902438L;

    private final TextManager textManager;

    private List<UpdateEntry> updateEntries = null;
    private boolean[] checkBoxes = null;
    private JButton download = null;
    private int activeCounter = 0;

    public UpdateTableModel(List<UpdateEntry> updateEntries, JButton download, TextManager textManager) {

        this.updateEntries = updateEntries;
        this.textManager = textManager;

        // Initialize checkboxes
        this.checkBoxes = new boolean[this.updateEntries.size()];
        Arrays.fill(this.checkBoxes, true);

        this.activeCounter = this.updateEntries.size();
        this.download = download;
    }

    @Override
    public int getColumnCount() {

        return 4;
    }

    @Override
    public int getRowCount() {

        if ((this.updateEntries == null) || this.updateEntries.isEmpty()) {
            return 5;
        }

        return this.updateEntries.size();
    }

    @Override
    public void setValueAt(Object o, int row, int column) {

        if (column == 0) {
            if (o instanceof Boolean) {

                // If all entries are deselected, deactivate Download Button
                if (this.checkBoxes[row]) {
                    this.activeCounter--;
                } else {
                    this.activeCounter++;
                }

                if (this.activeCounter == 0) {
                    this.download.setEnabled(false);
                } else {
                    this.download.setEnabled(true);
                }

                this.checkBoxes[row] = !this.checkBoxes[row];
                this.fireTableDataChanged();
            }
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        switch (columnIndex) {
            case 0: {
                return this.checkBoxes[rowIndex];
            }
            case 1: {
                return "" + ((UpdateEntry) this.updateEntries.get(rowIndex)).getName();
            }
            case 2: {
                return "" + ((UpdateEntry) this.updateEntries.get(rowIndex)).getOldVersion();
            }
            case 3: {
                return "" + ((UpdateEntry) this.updateEntries.get(rowIndex)).getNewVersion();
            }
            default:
                return "";
        }
       
    }

    @Override
    public Class getColumnClass(int columnIndex) {

        Class c = null;

        if (columnIndex == 0) {
            c = Boolean.class;
        } else {
            c = String.class;
        }

        return c;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {

        return columnIndex == 0;
    }

    @Override
    public String getColumnName(int column) {

        String name = "";

        switch (column) {
            case 0: {
                name = this.textManager.getString("updateInfo.column.download");
                break;
            }
            case 1: {
                name = this.textManager.getString("updateInfo.column.name");
                break;
            }
            case 2: {
                name = this.textManager.getString("updateInfo.column.oldVersion");
                break;
            }
            case 3: {
                name = this.textManager.getString("updateInfo.column.newVersion");
                break;
            }
            default: {
                name = "";
                break;
            }
        }

        return name;
    }

    public List<UpdateEntry> getSelected() {

        List<UpdateEntry> result = new ArrayList<UpdateEntry>(this.checkBoxes.length);
        boolean currentValue = false;
        for (int i = 0; i < this.checkBoxes.length; i++) {
            currentValue = (Boolean) this.getValueAt(i, 0);
            if (currentValue) {
                result.add(this.updateEntries.get(i));
            }
        }

        return result;
    }
}
