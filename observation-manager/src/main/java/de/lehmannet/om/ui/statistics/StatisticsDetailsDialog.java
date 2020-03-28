/* ====================================================================
 * /statistics/StatisticsDetailsDialog.java
 *
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.statistics;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import org.w3c.dom.Document;

import de.lehmannet.om.IObservation;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.ui.catalog.CatalogLoader;
import de.lehmannet.om.ui.dialog.AbstractDialog;
import de.lehmannet.om.ui.dialog.ProgressDialog;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.navigation.tableModel.AbstractSchemaTableModel;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.Worker;
import de.lehmannet.om.ui.util.XMLFileLoader;

public class StatisticsDetailsDialog extends AbstractDialog {

    private static final long serialVersionUID = -9088082984657164772L;
    private TargetObservations[] targetObservations = null;
    private String catalogName = null;

    private JMenuItem exportObservedOAL = null;
    private JMenuItem exportObservedHTML = null;
    private JMenuItem exportMissingOAL = null;
    private JMenuItem exportMissingHTML = null;

    public StatisticsDetailsDialog(ObservationManager om, ObservationStatisticsTableModel model) {

        super(om, new DetailPanel(om, model), true);

        this.targetObservations = model.getTargetObservations();
        this.catalogName = model.getCatalogName();

        Cursor defaultCursor = new Cursor(Cursor.WAIT_CURSOR);
        om.setCursor(defaultCursor);
        super.setCursor(defaultCursor);

        super.setTitle(AbstractDialog.bundle.getString("dialog.statistics.title"));
        super.cancel.setText(AbstractDialog.bundle.getString("dialog.button.ok"));

        super.setSize(StatisticsDetailsDialog.serialVersionUID, 400, 310);

        defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
        om.setCursor(defaultCursor);
        super.setCursor(defaultCursor);

        this.createMenu();

        // super.pack();
        super.setVisible(true);

    }

    private void createMenu() {

        JMenuBar menuBar = new JMenuBar();

        JMenu exportMenu = new JMenu(AbstractDialog.bundle.getString("dialog.statistics.menu.title"));
        exportMenu.setMnemonic('e');
        menuBar.add(exportMenu);

        JMenu exportObserved = new JMenu(AbstractDialog.bundle.getString("dialog.statistics.menu.observed"));
        exportObserved.setMnemonic('o');
        exportMenu.add(exportObserved);

        JMenu exportMissing = new JMenu(AbstractDialog.bundle.getString("dialog.statistics.menu.missing"));
        exportMissing.setMnemonic('m');
        exportMenu.add(exportMissing);

        this.exportObservedOAL = new JMenuItem(AbstractDialog.bundle.getString("dialog.statistics.menu.observed.xml"));
        this.exportObservedOAL.setMnemonic('x');
        this.exportObservedOAL.addActionListener(this);
        exportObserved.add(this.exportObservedOAL);

        this.exportObservedHTML = new JMenuItem(
                AbstractDialog.bundle.getString("dialog.statistics.menu.observed.html"));
        this.exportObservedHTML.setMnemonic('h');
        this.exportObservedHTML.addActionListener(this);
        exportObserved.add(this.exportObservedHTML);

        this.exportMissingOAL = new JMenuItem(AbstractDialog.bundle.getString("dialog.statistics.menu.missing.xml"));
        this.exportMissingOAL.setMnemonic('a');
        this.exportMissingOAL.addActionListener(this);
        exportMissing.add(this.exportMissingOAL);

        this.exportMissingHTML = new JMenuItem(AbstractDialog.bundle.getString("dialog.statistics.menu.missing.html"));
        this.exportMissingHTML.setMnemonic('t');
        this.exportMissingHTML.addActionListener(this);
        exportMissing.add(this.exportMissingHTML);

        this.setJMenuBar(menuBar);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();
        if (source instanceof JMenuItem) {
            if (source.equals(exportObservedOAL)) {
                exportObservedAsXML();
            } else if (source.equals(exportObservedHTML)) {
                exportObservedAsHTML();
            } else if (source.equals(exportMissingOAL)) {
                exportMissingTargetAsXML();
            } else if (source.equals(exportMissingHTML)) {
                exportMissingTargetAsHTML();
            }
        }

        super.actionPerformed(e);

    }

    private void exportObservedAsXML() {

        Worker calculation = new Worker() {

            private String message = null;
            private byte returnValue = Worker.RETURN_TYPE_OK;

            @Override
            public void run() {

                XMLFileLoader xmlHelper = new XMLFileLoader(new File(".exportTempFile"));

                List observations = null;
                ListIterator iterator = null;
                for (TargetObservations targetObservation : targetObservations) {
                    if (targetObservation.getObservations() != null) {
                        observations = targetObservation.getObservations();
                        iterator = observations.listIterator();
                        while (iterator.hasNext()) {
                            xmlHelper.addSchemaElement((IObservation) iterator.next(), true);
                        }
                    }
                }

                String file = getExportFile(catalogName + "_observed", "xml").getAbsolutePath();

                boolean result = xmlHelper.save(file);

                if (result) {
                    this.message = AbstractDialog.bundle.getString("dialog.statistics.observed.export.ok") + file;
                    this.returnValue = Worker.RETURN_TYPE_OK;
                } else {
                    this.message = AbstractDialog.bundle.getString("dialog.statistics.observed.export.nok");
                    this.returnValue = Worker.RETURN_TYPE_ERROR;
                }

            }

            @Override
            public String getReturnMessage() {

                return message;

            }

            @Override
            public byte getReturnType() {

                return returnValue;

            }

        };

        new ProgressDialog(this.observationManager, AbstractDialog.bundle.getString("progress.wait.title"),
                AbstractDialog.bundle.getString("progress.wait.xml.info"), calculation);

        if (calculation.getReturnType() == Worker.RETURN_TYPE_OK) {
            if (calculation.getReturnMessage() != null) {
                super.observationManager.createInfo(calculation.getReturnMessage());
            }
        } else {
            super.observationManager.createWarning(calculation.getReturnMessage());
        }

    }

    private void exportObservedAsHTML() {

        final XMLFileLoader xmlHelper = new XMLFileLoader(new File(".exportTempFile"));

        // Create worker for first part of export
        Worker calculation = new Worker() {

            private final String message = null;

            @Override
            public void run() {

                List observations = null;
                ListIterator iterator = null;
                for (TargetObservations targetObservation : targetObservations) {
                    if (targetObservation.getObservations() != null) {
                        observations = targetObservation.getObservations();
                        iterator = observations.listIterator();
                        while (iterator.hasNext()) {
                            xmlHelper.addSchemaElement((IObservation) iterator.next(), true);
                        }
                    }
                }

            }

            @Override
            public String getReturnMessage() {

                return message;

            }

            @Override
            public byte getReturnType() {

                return Worker.RETURN_TYPE_OK;

            }

        };

        // Show progresDialog for first part of export
        new ProgressDialog(this.observationManager, AbstractDialog.bundle.getString("progress.wait.title"),
                AbstractDialog.bundle.getString("progress.wait.html.info"), calculation);

        if (calculation.getReturnType() == Worker.RETURN_TYPE_OK) {
            if (calculation.getReturnMessage() != null) {
                super.observationManager.createInfo(calculation.getReturnMessage());
            }
        } else {
            super.observationManager.createWarning(calculation.getReturnMessage());
        }

        // Call OM and let him to the second part of the export
        super.observationManager.getHtmlHelper().createHTML(xmlHelper.getDocument(), getExportFile(catalogName + "_observed", "html"),
                null);

    }

    private void exportMissingTargetAsXML() {

        Worker calculation = new Worker() {

            private String message = null;
            private byte returnValue = Worker.RETURN_TYPE_OK;

            @Override
            public void run() {

                XMLFileLoader xmlHelper = new XMLFileLoader(new File(".exportTempFile"));

                for (TargetObservations targetObservation : targetObservations) {
                    if (targetObservation.getObservations() == null) {
                        xmlHelper.addSchemaElement(targetObservation.getTarget());
                    }
                }

                String file = getExportFile(catalogName + "_missing", "xml").getAbsolutePath();

                boolean result = xmlHelper.save(file);

                if (result) {
                    this.message = AbstractDialog.bundle.getString("dialog.statistics.missing.export.ok") + file;
                    this.returnValue = Worker.RETURN_TYPE_OK;
                } else {
                    this.message = AbstractDialog.bundle.getString("dialog.statistics.missing.export.nok");
                    this.returnValue = Worker.RETURN_TYPE_ERROR;
                }

            }

            @Override
            public String getReturnMessage() {

                return message;

            }

            @Override
            public byte getReturnType() {

                return returnValue;

            }

        };

        new ProgressDialog(this.observationManager, AbstractDialog.bundle.getString("progress.wait.title"),
                AbstractDialog.bundle.getString("progress.wait.xml.info"), calculation);

        if (calculation.getReturnType() == Worker.RETURN_TYPE_OK) {
            if (calculation.getReturnMessage() != null) {
                super.observationManager.createInfo(calculation.getReturnMessage());
            }
        } else {
            super.observationManager.createWarning(calculation.getReturnMessage());
        }

    }

    private void exportMissingTargetAsHTML() {

        // Create worker for first part of export

        class MyWorker implements Worker {

            private final String message = null;

            private final XMLFileLoader xmlHelper = new XMLFileLoader(new File(".exportTempFile"));
            private Document document = null;

            @Override
            public void run() {

                for (TargetObservations targetObservation : targetObservations) {
                    if (targetObservation.getObservations() == null) {
                        xmlHelper.addSchemaElement(targetObservation.getTarget());
                    }
                }

                this.document = xmlHelper.getDocument();

            }

            @Override
            public String getReturnMessage() {

                return message;

            }

            @Override
            public byte getReturnType() {

                return Worker.RETURN_TYPE_OK;

            }

            Document getDocument() {

                return this.document;

            }

        }

        MyWorker calculation = new MyWorker();

        // Show progresDialog for first part of export
        new ProgressDialog(this.observationManager, AbstractDialog.bundle.getString("progress.wait.title"),
                AbstractDialog.bundle.getString("progress.wait.html.info"), calculation);

        if (calculation.getReturnType() == Worker.RETURN_TYPE_OK) {
            if (calculation.getReturnMessage() != null) {
                super.observationManager.createInfo(calculation.getReturnMessage());
            }
        } else {
            super.observationManager.createWarning(calculation.getReturnMessage());
        }

        // Call OM and let him to the second part of the export
        super.observationManager.getHtmlHelper().createHTML(calculation.getDocument(), getExportFile(catalogName + "_missing", "html"),
                getXSLFile());

    }

    private File getExportFile(String filename, String extension) {

        String path = null;

        if ((super.observationManager.getXmlCache().getAllOpenedFiles() != null)
                && (super.observationManager.getXmlCache().getAllOpenedFiles().length > 0)) {
            path = new File(super.observationManager.getXmlCache().getAllOpenedFiles()[0]).getParent();
        } else {
            path = super.observationManager.getInstallDir().getInstallDir().getParent();
        }
        path = path + File.separator;

        File file = new File(path + filename + "." + extension);
        for (int i = 2; file.exists(); i++) {
            file = new File(path + filename + "(" + i + ")." + extension);
        }

        return file;

    }

    private File getXSLFile() {

        String XSLFILENAME = "targetsOnly";

        String selectedTemplate = super.observationManager.getConfiguration()
                .getConfig(ObservationManager.CONFIG_XSL_TEMPLATE);
        if ((selectedTemplate == null) // No config given, so take default one.
                                       // (Usefull for migrations)
                || ("".equals(selectedTemplate.trim()))) {
            selectedTemplate = "oal2html";
        }

        // Check if XSL directory exists
        File path = new File(super.observationManager.getInstallDir().getPathForFolder("xsl") + selectedTemplate + File.separator + "targetOnly");
        if (!path.exists()) {
            super.observationManager
                    .createWarning(AbstractDialog.bundle.getString("warning.xslTemplate.dirDoesNotExist") + "\n"
                            + path.getAbsolutePath());
            return null;
        }

        // Try to load language dependend file
        File xslFile = new File(path.getAbsolutePath() + File.separator + XSLFILENAME + "_"
                + Locale.getDefault().getLanguage() + ".xsl");
        if (!xslFile.exists()) { // OK, maybe a language independent file can be found...
            xslFile = new File(path.getAbsolutePath() + File.separator + XSLFILENAME + ".xsl");
            if (!xslFile.exists()) { // Nothing found, raise warning
                super.observationManager
                        .createWarning(AbstractDialog.bundle.getString("warning.xslTemplate.noFileFoundWithName") + "\n"
                                + path.getAbsolutePath() + File.separator + "targetsOnly" + ".xsl\n"
                                + path.getAbsolutePath() + File.separator + "targetsOnly" + "_"
                                + Locale.getDefault().getLanguage() + ".xsl");
                return null;
            }
        }

        return xslFile;

    }

}

class DetailPanel extends AbstractPanel implements ActionListener {

    private AbstractSchemaTableModel model = null;
    private JScrollPane scrollTable = null;
    private ObservationManager om = null;

    public DetailPanel(ObservationManager om, AbstractSchemaTableModel model) {

        super(true);

        this.model = model;
        this.om = om;

        JTable table = new JTable();
        table.setModel(this.model);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListSelectionModel lsm = table.getSelectionModel();
        lsm.addListSelectionListener(e -> {
            // Ignore extra messages.
            if (e.getValueIsAdjusting())
                return;

            ListSelectionModel lsm1 = (ListSelectionModel) e.getSource();
            if (lsm1.isSelectionEmpty()) {
                // no rows are selected
            } else {
                int selectedRow = lsm1.getMinSelectionIndex();
                IObservation obs = (IObservation) DetailPanel.this.model.getValueAt(selectedRow, 1);
                DetailPanel.this.om.updateUI(obs);
                /*
                 * List l = (List)DetailPanel.this.model.getValueAt(selectedRow, 1); if( (l != null) && !(l.isEmpty()) )
                 * { IObservation obs = (IObservation)l.get(0); // Always show first observation
                 * DetailPanel.this.om.updateUI(obs); }
                 */
            }
        });

        table.setDefaultRenderer(ITarget.class, (table12, value, isSelected, hasFocus, row, column) -> {
            DefaultTableCellRenderer cr = new DefaultTableCellRenderer();
            cr.setHorizontalAlignment(SwingConstants.CENTER);
            String text = null;
            if (value != null) {
                ITarget t = (ITarget) value;
                text = t.getDisplayName();
                cr.setText(text);

                /*
                 * if( DetailPanel.this.rowColor.containsKey(row) ) {
                 * cr.setBackground((Color)DetailPanel.this.rowColor.get(row)); } else { if(
                 * DetailPanel.this.currentBGColor.equals(Color.WHITE) ) { DetailPanel.this.currentBGColor =
                 * Color.LIGHT_GRAY; } else { DetailPanel.this.currentBGColor = Color.WHITE; }
                 * DetailPanel.this.rowColor.put(row, DetailPanel.this.currentBGColor);
                 * cr.setBackground(DetailPanel.this.currentBGColor); }
                 */
                cr.setBackground(Color.LIGHT_GRAY);
            }

            return cr;
        });
        table.setDefaultRenderer(IObservation.class, (table1, value, isSelected, hasFocus, row, column) -> {
            DefaultTableCellRenderer cr = new DefaultTableCellRenderer();
            cr.setHorizontalAlignment(SwingConstants.CENTER);
            if (value != null) {
                IObservation o = (IObservation) value;
                SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
                df.setCalendar(o.getBegin());
                cr.setText(df.format(o.getBegin().getTime()));
            }

            if (isSelected) {
                cr.setForeground(Color.RED);
            } else {
                cr.setForeground(Color.BLACK);
            }

            return cr;
        });

        /*
         * this.table.setDefaultRenderer(List.class, new TableCellRenderer() { public Component
         * getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int
         * column) { DefaultTableCellRenderer cr = new DefaultTableCellRenderer(); if( value != null ) { List l =
         * (List)value; Iterator i = l.iterator(); IObservation o = null; String text = ""; DateFormat df =
         * DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault()); while( i.hasNext() )
         * { o = (IObservation)i.next(); df.setCalendar(o.getBegin()); text = text + df.format(o.getBegin().getTime());
         * if( i.hasNext() ) { text = text + "; "; } } cr.setText(text); } else { cr.setText(""); } if( isSelected ) {
         * cr.setBackground(Color.LIGHT_GRAY); }
         *
         * return cr; } } );
         */
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        TableColumn c = table.getColumnModel().getColumn(0);
        c.setPreferredWidth(this.model.getColumnSize(0));
        c = table.getColumnModel().getColumn(1);
        c.setPreferredWidth(this.model.getColumnSize(1));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

        table.setDoubleBuffered(true);
        this.scrollTable = new JScrollPane(table);

        this.createPanel();

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public ISchemaElement createSchemaElement() {

        return this.model.getSchemaElement(0);

    }

    @Override
    public ISchemaElement getSchemaElement() {

        return this.model.getSchemaElement(0);

    }

    @Override
    public ISchemaElement updateSchemaElement() {

        return this.model.getSchemaElement(0);

    }

    private void createPanel() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        this.setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 1, 1, 100, 99);
        constraints.fill = GridBagConstraints.BOTH;
        gridbag.setConstraints(this.scrollTable, constraints);
        this.add(this.scrollTable);

    }

}
