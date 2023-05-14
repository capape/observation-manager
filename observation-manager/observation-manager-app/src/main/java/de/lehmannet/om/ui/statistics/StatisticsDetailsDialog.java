/*
 * ====================================================================
 * /statistics/StatisticsDetailsDialog.java
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
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

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
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.dialog.AbstractDialog;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.navigation.ObservationManagerHtmlHelper;
import de.lehmannet.om.ui.navigation.observation.utils.InstallDir;
import de.lehmannet.om.ui.navigation.tableModel.AbstractSchemaTableModel;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.ui.util.ConfigKey;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.IConfiguration;
import de.lehmannet.om.ui.util.UserInterfaceHelper;
import de.lehmannet.om.ui.util.Worker;
import de.lehmannet.om.ui.util.XMLFileLoader;
import de.lehmannet.om.ui.util.XMLFileLoaderImpl;

public class StatisticsDetailsDialog extends AbstractDialog {

    private static final long serialVersionUID = -9088082984657164772L;
    private TargetObservations[] targetObservations = null;
    private String catalogName = null;

    private JMenuItem exportObservedOAL = null;
    private JMenuItem exportObservedHTML = null;
    private JMenuItem exportMissingOAL = null;
    private JMenuItem exportMissingHTML = null;

    private final ObservationManagerModel model;
    private final UserInterfaceHelper uiHelper;
    private final ObservationManagerHtmlHelper htmlHelper;
    private final InstallDir installDir;
    private final IConfiguration configuration;

    public StatisticsDetailsDialog(final ObservationManager om, final ObservationManagerModel omModel,
            final ObservationStatisticsTableModel tableModel) {

        super(om, omModel, om.getUiHelper(), new DetailPanel(om, tableModel), true);
        this.uiHelper = om.getUiHelper();
        this.htmlHelper = om.getHtmlHelper();
        this.configuration = om.getConfiguration();
        this.installDir = om.getInstallDir();
        this.model = omModel;
        this.targetObservations = tableModel.getTargetObservations();
        this.catalogName = tableModel.getCatalogName();

        Cursor defaultCursor = new Cursor(Cursor.WAIT_CURSOR);
        om.setCursor(defaultCursor);
        this.setCursor(defaultCursor);

        this.setTitle(AbstractDialog.bundle.getString("dialog.statistics.title"));
        this.cancel.setText(AbstractDialog.bundle.getString("dialog.button.ok"));

        this.setSize(StatisticsDetailsDialog.serialVersionUID, 400, 310);

        defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
        om.setCursor(defaultCursor);
        this.setCursor(defaultCursor);

        this.createMenu();

        this.setVisible(true);

    }

    private void createMenu() {

        final JMenuBar menuBar = new JMenuBar();

        final JMenu exportMenu = new JMenu(AbstractDialog.bundle.getString("dialog.statistics.menu.title"));
        exportMenu.setMnemonic('e');
        menuBar.add(exportMenu);

        final JMenu exportObserved = new JMenu(AbstractDialog.bundle.getString("dialog.statistics.menu.observed"));
        exportObserved.setMnemonic('o');
        exportMenu.add(exportObserved);

        final JMenu exportMissing = new JMenu(AbstractDialog.bundle.getString("dialog.statistics.menu.missing"));
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
    public void actionPerformed(final ActionEvent e) {

        final Object source = e.getSource();
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

        final Worker calculation = new Worker() {

            private String message = null;
            private byte returnValue = Worker.RETURN_TYPE_OK;

            @Override
            public void run() {

                final XMLFileLoader xmlHelper = XMLFileLoaderImpl.newInstance(".exportTempFile");

                List<IObservation> observations = null;
                ListIterator<IObservation> iterator = null;
                for (final TargetObservations targetObservation : targetObservations) {
                    if (targetObservation.getObservations() != null) {
                        observations = targetObservation.getObservations();
                        iterator = observations.listIterator();
                        while (iterator.hasNext()) {
                            xmlHelper.addSchemaElement((IObservation) iterator.next(), true);
                        }
                    }
                }

                final String file = StatisticsDetailsDialog.this.model.getExportFile(catalogName + "_observed", "xml")
                        .getAbsolutePath();

                final boolean result = xmlHelper.save(file);

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

        this.uiHelper.createProgressDialog(AbstractDialog.bundle.getString("progress.wait.title"),
                AbstractDialog.bundle.getString("progress.wait.xml.info"), calculation);

        if (calculation.getReturnType() == Worker.RETURN_TYPE_OK) {
            if (calculation.getReturnMessage() != null) {
                this.uiHelper.showInfo(calculation.getReturnMessage());
            }
        } else {
            this.uiHelper.showWarning(calculation.getReturnMessage());
        }

    }

    private void exportObservedAsHTML() {

        final XMLFileLoader xmlHelper = XMLFileLoaderImpl.newInstance(".exportTempFile");

        // Create worker for first part of export
        final Worker calculation = new Worker() {

            private final String message = null;

            @Override
            public void run() {

                List<IObservation> observations = null;
                ListIterator<IObservation> iterator = null;
                for (final TargetObservations targetObservation : targetObservations) {
                    if (targetObservation.getObservations() != null) {
                        observations = targetObservation.getObservations();
                        iterator = observations.listIterator();
                        while (iterator.hasNext()) {
                            xmlHelper.addSchemaElement(iterator.next(), true);
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
        this.uiHelper.createProgressDialog(AbstractDialog.bundle.getString("progress.wait.title"),
                AbstractDialog.bundle.getString("progress.wait.html.info"), calculation);

        if (calculation.getReturnType() == Worker.RETURN_TYPE_OK) {
            if (calculation.getReturnMessage() != null) {
                this.uiHelper.showInfo(calculation.getReturnMessage());
            }
        } else {
            this.uiHelper.showWarning(calculation.getReturnMessage());
        }

        // Call OM and let him to the second part of the export
        this.htmlHelper.createHTML(xmlHelper.getDocument(), this.model.getExportFile(catalogName + "_observed", "html"),
                null);

    }

    private void exportMissingTargetAsXML() {

        final Worker calculation = new Worker() {

            private String message = null;
            private byte returnValue = Worker.RETURN_TYPE_OK;

            @Override
            public void run() {

                final XMLFileLoader xmlHelper = XMLFileLoaderImpl.newInstance(".exportTempFile");

                for (final TargetObservations targetObservation : targetObservations) {
                    if (targetObservation.getObservations() == null) {
                        xmlHelper.addSchemaElement(targetObservation.getTarget());
                    }
                }

                final String file = StatisticsDetailsDialog.this.model.getExportFile(catalogName + "_missing", "xml")
                        .getAbsolutePath();

                final boolean result = xmlHelper.save(file);

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

        this.uiHelper.createProgressDialog(AbstractDialog.bundle.getString("progress.wait.title"),
                AbstractDialog.bundle.getString("progress.wait.xml.info"), calculation);

        if (calculation.getReturnType() == Worker.RETURN_TYPE_OK) {
            if (calculation.getReturnMessage() != null) {
                this.uiHelper.showInfo(calculation.getReturnMessage());
            }
        } else {
            this.uiHelper.showWarning(calculation.getReturnMessage());
        }

    }

    private void exportMissingTargetAsHTML() {

        // Create worker for first part of export

        class MyWorker implements Worker {

            private final String message = null;

            private final XMLFileLoader xmlHelper = XMLFileLoaderImpl.newInstance(".exportTempFile");
            private Document document = null;

            @Override
            public void run() {

                for (final TargetObservations targetObservation : targetObservations) {
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

        final MyWorker calculation = new MyWorker();

        // Show progresDialog for first part of export
        this.uiHelper.createProgressDialog(AbstractDialog.bundle.getString("progress.wait.title"),
                AbstractDialog.bundle.getString("progress.wait.html.info"), calculation);

        if (calculation.getReturnType() == Worker.RETURN_TYPE_OK) {
            if (calculation.getReturnMessage() != null) {
                this.uiHelper.showInfo(calculation.getReturnMessage());
            }
        } else {
            this.uiHelper.showWarning(calculation.getReturnMessage());
        }

        // Call OM and let him to the second part of the export
        this.htmlHelper.createHTML(calculation.getDocument(),
                this.model.getExportFile(catalogName + "_missing", "html"), getXSLFile());

    }

    private File getXSLFile() {

        final String XSLFILENAME = "targetsOnly";

        String selectedTemplate = this.configuration.getConfig(ConfigKey.CONFIG_XSL_TEMPLATE);
        if ((selectedTemplate == null) // No config given, so take default one.
                                       // (Usefull for migrations)
                || ("".equals(selectedTemplate.trim()))) {
            selectedTemplate = "oal2html";
        }

        // Check if XSL directory exists
        final File path = new File(
                this.installDir.getPathForFolder("xsl") + selectedTemplate + File.separator + "targetOnly");
        if (!path.exists()) {
            this.uiHelper.showWarning(AbstractDialog.bundle.getString("warning.xslTemplate.dirDoesNotExist") + "\n"
                    + path.getAbsolutePath());
            return null;
        }

        // Try to load language dependend file
        File xslFile = new File(path.getAbsolutePath() + File.separator + XSLFILENAME + "_"
                + Locale.getDefault().getLanguage() + ".xsl");
        if (!xslFile.exists()) { // OK, maybe a language independent file can be found...
            xslFile = new File(path.getAbsolutePath() + File.separator + XSLFILENAME + ".xsl");
            if (!xslFile.exists()) { // Nothing found, raise warning
                this.uiHelper.showWarning(AbstractDialog.bundle.getString("warning.xslTemplate.noFileFoundWithName")
                        + "\n" + path.getAbsolutePath() + File.separator + "targetsOnly" + ".xsl\n"
                        + path.getAbsolutePath() + File.separator + "targetsOnly" + "_"
                        + Locale.getDefault().getLanguage() + ".xsl");
                return null;
            }
        }

        return xslFile;

    }

}

class DetailPanel extends AbstractPanel implements ActionListener {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private AbstractSchemaTableModel model = null;
    private JScrollPane scrollTable = null;
    private ObservationManager om = null;

    public DetailPanel(final ObservationManager om, final AbstractSchemaTableModel model) {

        super(true);

        this.model = model;
        this.om = om;

        final JTable table = new JTable();
        table.setModel(this.model);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        final ListSelectionModel lsm = table.getSelectionModel();
        lsm.addListSelectionListener(e -> {
            // Ignore extra messages.
            if (e.getValueIsAdjusting())
                return;

            final ListSelectionModel lsm1 = (ListSelectionModel) e.getSource();
            if (lsm1.isSelectionEmpty()) {
                // no rows are selected
            } else {
                final int selectedRow = lsm1.getMinSelectionIndex();
                final IObservation obs = (IObservation) DetailPanel.this.model.getValueAt(selectedRow, 1);
                DetailPanel.this.om.updateUI(obs);
                /*
                 * List l = (List)DetailPanel.this.model.getValueAt(selectedRow, 1); if( (l != null) && !(l.isEmpty()) )
                 * { IObservation obs = (IObservation)l.get(0); // Always show first observation
                 * DetailPanel.this.om.updateUI(obs); }
                 */
            }
        });

        table.setDefaultRenderer(ITarget.class, (table12, value, isSelected, hasFocus, row, column) -> {
            final DefaultTableCellRenderer cr = new DefaultTableCellRenderer();
            cr.setHorizontalAlignment(SwingConstants.CENTER);
            String text = null;
            if (value != null) {
                final ITarget t = (ITarget) value;
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
            final DefaultTableCellRenderer cr = new DefaultTableCellRenderer();
            cr.setHorizontalAlignment(SwingConstants.CENTER);
            if (value != null) {
                final IObservation o = (IObservation) value;
                cr.setText(this.om.getDateManager().zonedDateTimeToStringWithHour(o.getBegin().toZonedDateTime()));
            }

            if (isSelected) {
                cr.setForeground(Color.RED);
            } else {
                cr.setForeground(Color.BLACK);
            }

            return cr;
        });

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
    public void actionPerformed(final ActionEvent e) {

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

        final GridBagLayout gridbag = new GridBagLayout();
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        this.setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 1, 1, 100, 99);
        constraints.fill = GridBagConstraints.BOTH;
        gridbag.setConstraints(this.scrollTable, constraints);
        this.add(this.scrollTable);

    }

}
