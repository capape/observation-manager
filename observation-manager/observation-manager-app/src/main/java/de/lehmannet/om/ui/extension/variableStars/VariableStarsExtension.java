/* ====================================================================
 * /extension/variableStars/ExtensionLoader.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.variableStars;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.lehmannet.om.IFinding;
import de.lehmannet.om.IObservation;
import de.lehmannet.om.IObserver;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ISession;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.extension.variableStars.FindingVariableStar;
import de.lehmannet.om.extension.variableStars.TargetVariableStar;
import de.lehmannet.om.extension.variableStars.export.AAVSOVisualSerializer;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.catalog.ICatalog;
import de.lehmannet.om.ui.comparator.ObservationComparator;
import de.lehmannet.om.ui.dialog.ITargetDialog;
import de.lehmannet.om.ui.dialog.SchemaElementSelectorPopup;
import de.lehmannet.om.ui.extension.AbstractExtension;
import de.lehmannet.om.ui.extension.IExtensionContext;
import de.lehmannet.om.ui.extension.PopupMenuExtension;
import de.lehmannet.om.ui.extension.variableStars.catalog.GCVS4Catalog;
import de.lehmannet.om.ui.extension.variableStars.dialog.VariableStarChartDialog;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.navigation.tableModel.ExtendedSchemaTableModel;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.ui.preferences.PreferencesPanel;
import de.lehmannet.om.ui.util.ConfigKey;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.DatePicker;
import de.lehmannet.om.util.SchemaElementConstants;

public class VariableStarsExtension extends AbstractExtension implements ActionListener {

    private static final String NAME = "Variable Stars";
    private static final float VERSION = 0.92f;
    private static URL UPDATE_URL = null;
    static {
        try {
            VariableStarsExtension.UPDATE_URL = new URL(
                    "http://observation.sourceforge.net/extension/variableStars/update");
        } catch (MalformedURLException m_url) {
            // Do nothing
        }
    }

    private PropertyResourceBundle typeBundle = (PropertyResourceBundle) ResourceBundle.getBundle(
            "de.lehmannet.om.ui.extension.variableStars.oalVariableStarTargetDisplayNames", Locale.getDefault());
    private PropertyResourceBundle uiBundle = (PropertyResourceBundle) ResourceBundle
            .getBundle("de.lehmannet.om.ui.extension.variableStars.VariableStar", Locale.getDefault());

    private ObservationManager om = null;

    private JMenuItem exportAAVSO = null;
    private JMenuItem showChart = null;

    private static final Logger LOGGER = LoggerFactory.getLogger(VariableStarsExtension.class);

    private final ObservationManagerModel model;
    private IExtensionContext extensionContext;

    public VariableStarsExtension(ObservationManager om, ObservationManagerModel model) {

        this.om = om;
        this.model = model;
        this.OAL_EXTENSION_FILE = "./openastronomylog21/extensions/ext_VariableStars.xsd";

        this.initFindingPanels();
        this.initTargetPanels();
        this.initTargetDialogs();

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() instanceof JMenuItem) { // Should always be the case
            JMenuItem source = (JMenuItem) e.getSource();
            if (source.equals(this.exportAAVSO)) {

                // Get preselected observations
                IObservation[] allObservations = this.model.getObservations();
                if (allObservations.length == 0) {
                    this.om.createInfo(this.uiBundle.getString("info.noObservationsFound"));
                    return;
                }
                List<IObservation> preselectedObservations = new ArrayList<>();
                for (IObservation allObservation : allObservations) {
                    // Only the variable star observations are of interest
                    if (TargetVariableStar.XML_XSI_TYPE_VALUE.equals(allObservation.getTarget().getXSIType())) {
                        // @todo: This works only with one result!
                        if (!((FindingVariableStar) allObservation.getResults().get(0)).isAlreadyExportedToAAVSO()) {
                            preselectedObservations.add(allObservation);
                        }
                    }
                }

                // Create popup for variable star observations
                SchemaElementSelectorPopup popup = new SchemaElementSelectorPopup(this.om, this.model,
                        this.uiBundle.getString("popup.exportAAVSO.selectObservations"),
                        TargetVariableStar.XML_XSI_TYPE_VALUE, preselectedObservations, true,
                        SchemaElementConstants.OBSERVATION);
                List<ISchemaElement> variableStarObservations = popup.getAllSelectedElements();
                if ((variableStarObservations == null) || (variableStarObservations.isEmpty())) {
                    return;
                }

                List<IObservation> results = variableStarObservations.stream().map(x -> (IObservation) x)
                        .collect(Collectors.toList());

                AAVSOVisualSerializer aavsoExport = new AAVSOVisualSerializer(
                        "Observation Manager - " + ObservationManager.VERSION, results);

                // Create export file path
                String[] files = this.model.getAllOpenedFiles();
                if ((files == null) || (files.length == 0)) { // There is data (otherwise we wouldn't have come here),
                                                              // but data's not saved
                    this.om.createInfo(this.uiBundle.getString("error.noXMLFileOpen"));
                    return;
                }

                Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
                om.setCursor(hourglassCursor);

                // @todo This works only with ONE file opened
                File xmlFile = new File(files[0]);
                String exportFileName = xmlFile.getName();
                exportFileName = exportFileName.substring(0, exportFileName.indexOf('.'));
                exportFileName = xmlFile.getParent() + File.separatorChar + exportFileName + "_aavso.txt";
                File aavsoFile = new File(exportFileName);
                int i = 2;
                while (aavsoFile.exists()) { // Check if file exists...
                    exportFileName = exportFileName.substring(0, exportFileName.lastIndexOf("_aavso"));
                    exportFileName = exportFileName + "_aavso(" + i + ").txt";
                    i++;
                    aavsoFile = new File(exportFileName);
                }

                // Do the actual export
                int exportCounter = 0;
                try {
                    exportCounter = aavsoExport.serialize(new BufferedOutputStream(new FileOutputStream(aavsoFile)));
                } catch (FileNotFoundException fnfe) {
                    this.om.createInfo(this.uiBundle.getString("error.aavsoExportFileNotFound"));
                    System.err.println(fnfe);

                    Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
                    om.setCursor(defaultCursor);
                    return;
                } catch (Exception ex) {
                    this.om.createInfo(this.uiBundle.getString("error.aavsoExportNotOK"));
                    System.err.println(ex);

                    Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
                    om.setCursor(defaultCursor);
                    return;
                }

                // Set the om status to changed, as findings have been exported (which changes
                // their status)
                this.om.setChanged(true);

                Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
                om.setCursor(defaultCursor);

                if (exportCounter != variableStarObservations.size()) { // Not all observations were exported
                    this.om.createInfo(exportCounter + " " + this.uiBundle.getString("info.aavsoExport") + "\n"
                            + aavsoFile + "\n" + this.uiBundle.getString("info.aavsoExportCheckLog"));
                } else { // All observations exported
                    this.om.createInfo(
                            exportCounter + " " + this.uiBundle.getString("info.aavsoExport") + "\n" + aavsoFile);
                }

                if (exportCounter == 0) { // Nothing exported, so delete file (file only contains aavso header)
                    if (!aavsoFile.delete()) {
                        LOGGER.warn("File no deleted");
                    }
                }

            } else if (source.equals(this.showChart)) {

                // Create popup with variableStars
                VariableStarSelectorPopup popup = null;
                IObservation[] observations = null;
                boolean quitLoop = false;
                do {
                    try {
                        popup = new VariableStarSelectorPopup(this.om, this.model);
                    } catch (IllegalArgumentException iae) { // No variable star observation found
                        return;
                    }
                    if (popup.getAllSelectedObservations() != null) {
                        if (popup.getAllSelectedObservations().length > 0) {
                            observations = popup.getAllSelectedObservations();

                            if ((observations != null) // No observations for star
                                    && (observations.length <= 0)) {
                                this.om.createWarning(
                                        this.uiBundle.getString("popup.selectVariableStar.warning.noObservations"));
                            } else {
                                quitLoop = true;
                            }
                        } else { // No Star selected
                            this.om.createWarning(
                                    this.uiBundle.getString("popup.selectVariableStar.warning.noStarSelected"));
                        }
                    } else {
                        return; // User pressed cancel
                    }
                } while (!quitLoop); // Exit loop by pressing cancel

                // Show color selection
                ColorSelectionDialog colorDialog = new ColorSelectionDialog(this.om, observations);
                Map<IObserver, Color> colorMap = colorDialog.getColorMap();

                // Show chart
                if (colorMap != null) {
                    new VariableStarChartDialog(this.om, Objects.requireNonNull(observations), colorMap);
                }
            }
        }

    }

    @Override
    public String getName() {

        return VariableStarsExtension.NAME;

    }

    @Override
    public URL getUpdateInformationURL() {

        return VariableStarsExtension.UPDATE_URL;

    }

    @Override
    public float getVersion() {

        return VariableStarsExtension.VERSION;

    }

    @Override
    public void reloadLanguage() {

        this.typeBundle = (PropertyResourceBundle) ResourceBundle.getBundle(
                "de.lehmannet.om.ui.extension.variableStars.oalVariableStarTargetDisplayNames", Locale.getDefault());
        this.uiBundle = (PropertyResourceBundle) ResourceBundle
                .getBundle("de.lehmannet.om.ui.extension.variableStars.VariableStar", Locale.getDefault());

    }

    @Override
    public JMenu getMenu() {

        JMenu menu = new JMenu(this.uiBundle.getString("menu.main"));

        this.exportAAVSO = new JMenuItem(this.uiBundle.getString("menu.aavsoExport"));
        exportAAVSO.setMnemonic('e');
        exportAAVSO.addActionListener(this);
        menu.add(exportAAVSO);

        this.showChart = new JMenuItem(this.uiBundle.getString("menu.showChart"));
        showChart.setMnemonic('c');
        showChart.addActionListener(this);
        menu.add(showChart);

        return menu;

    }

    @Override
    public PreferencesPanel getPreferencesPanel() {

        return new VariableStarsPreferences(this.om.getConfiguration());

    }

    @Override
    public ICatalog[] getCatalogs(File catalogDir) {

        ICatalog gcvs = new GCVS4Catalog(catalogDir.getAbsoluteFile(), this.om);

        return new ICatalog[] { gcvs };

    }

    @Override
    public Set<String> getSupportedXSITypes(SchemaElementConstants schemaElementConstant) {

        Set<String> result = null;
        if (SchemaElementConstants.TARGET == schemaElementConstant) {
            result = this.getSupportedTargetXSITypes();
        } else if (SchemaElementConstants.FINDING == schemaElementConstant) {
            result = this.getSupportedFindingXSITypes();
        }

        return result;

    }

    private Set<String> getSupportedTargetXSITypes() {

        Set<String> result = new HashSet<>();
        result.add(TargetVariableStar.XML_XSI_TYPE_VALUE);

        return result;

    }

    private Set<String> getSupportedFindingXSITypes() {

        Set<String> result = new HashSet<>();
        result.add(FindingVariableStar.XML_XSI_TYPE_VALUE);

        return result;

    }

    @Override
    public String getDisplayNameForXSIType(String xsiType) {

        try {
            return this.typeBundle.getString(xsiType);
        } catch (MissingResourceException mre) { // XSIType not found
            return null;
        }

    }

    @Override
    public boolean isCreationAllowed(String xsiType) {

        // All elements are allowed for creation of new instances
        return true;

    }

    private void initFindingPanels() {

        Map<String, String> findingPanels = new HashMap<>();

        findingPanels.put(FindingVariableStar.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.variableStars.panel.VariableStarFindingPanel");
        findingPanels.put(TargetVariableStar.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.variableStars.panel.VariableStarFindingPanel");

        this.panels.put(SchemaElementConstants.FINDING, findingPanels);

    }

    private void initTargetPanels() {

        Map<String, String> targetPanels = new HashMap<>();

        targetPanels.put(TargetVariableStar.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.variableStars.panel.VariableStarTargetPanel");

        this.panels.put(SchemaElementConstants.TARGET, targetPanels);

    }

    private void initTargetDialogs() {

        Map<String, String> targetDialogs = new HashMap<>();

        targetDialogs.put(TargetVariableStar.XML_XSI_TYPE_VALUE,
                "de.lehmannet.om.ui.extension.variableStars.dialog.VariableStarTargetDialog");

        this.dialogs.put(SchemaElementConstants.TARGET, targetDialogs);

    }

    @Override
    public PopupMenuExtension getPopupMenu() {

        return null;

    }

    @Override
    public AbstractPanel getFindingPanelForXSIType(String xsiType, IFinding finding, ISession session, boolean editable) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public ITargetDialog getTargetDialogForXSIType(String xsiType, JFrame parent, ITarget target, IObservation observation, boolean editable) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setContext(IExtensionContext context) {
        this.extensionContext = context;

    }

  
    @Override
    public AbstractPanel getTargetPanelForXSIType(String xsiType, ITarget target, IObservation observation, boolean editable) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean supports(String xsiType) {
        // TODO Auto-generated method stub
        return false;
    }

}

class ColorSelectionDialog extends JDialog implements ActionListener {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private final PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
            .getBundle("de.lehmannet.om.ui.extension.variableStars.VariableStar", Locale.getDefault());

    private IObservation[] observations = null;

    private ObservationManager om = null;
    private JTable table = null;

    private JButton cancel = null;

    private Map<IObserver, Color> result = null;

    public ColorSelectionDialog(ObservationManager om, IObservation[] observations) {

        super(om);

        this.observations = observations;

        this.om = om;
        this.setTitle(this.bundle.getString("popup.observerColor.title"));
        this.setModal(true);

        this.setSize(550, 200);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(om);

        this.initDialog();

        this.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (this.cancel.equals(e.getSource())) { // Cancel pressed
            this.result = null;
        } else { // OK pressed
            this.result = this.createMap();
        }

        this.dispose();

    }

    private void initDialog() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        this.getContentPane().setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 2, 1, 100, 100);
        constraints.fill = GridBagConstraints.BOTH;
        Color defaultColor = null;
        if (this.om.isNightVisionEnabled()) {
            defaultColor = Color.DARK_GRAY;
        } else {
            defaultColor = Color.RED;
        }
        this.table = new JTable(new ObserverColorTableModel(this.getObservers(), defaultColor));
        this.table.setToolTipText(this.bundle.getString("popup.observerColor.tooltip.table"));
        this.table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        this.table.setDefaultEditor(Color.class, new ColorEditor());
        this.table.setDefaultRenderer(Color.class, (table, value, isSelected, hasFocus, row, column) -> {

            DefaultTableCellRenderer cr = new DefaultTableCellRenderer();
            if (value != null) {
                cr.setBackground((Color) value);
            } else {
                cr.setText(ColorSelectionDialog.this.bundle.getString("popup.observerColor.noColorSelection"));
            }

            return cr;
        });
        JScrollPane scrollPane = new JScrollPane(this.table);
        gridbag.setConstraints(scrollPane, constraints);
        this.getContentPane().add(scrollPane);

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 1, 1, 50, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        JButton ok = new JButton(this.bundle.getString("popup.observerColor.button.ok"));
        ok.addActionListener(this);
        gridbag.setConstraints(ok, constraints);
        this.getContentPane().add(ok);

        ConstraintsBuilder.buildConstraints(constraints, 1, 1, 1, 1, 50, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.cancel = new JButton(this.bundle.getString("popup.observerColor.button.cancel"));
        this.cancel.addActionListener(this);
        gridbag.setConstraints(this.cancel, constraints);
        this.getContentPane().add(cancel);

    }

    public Map<IObserver, Color> getColorMap() {

        return this.result;

    }

    private Map<IObserver, Color> createMap() {

        ObserverColorTableModel model = (ObserverColorTableModel) this.table.getModel();

        return model.getResult();

    }

    private IObserver[] getObservers() {

        // Make sure we only show the observers, which contributed a observation
        List<IObserver> list = new ArrayList<>();
        for (IObservation observation : this.observations) {
            if (!list.contains(observation.getObserver())) {
                // Make sure the default observer is the top entry
                if (observation.getObserver().getDisplayName()
                        .equals(this.om.getConfiguration().getConfig(ConfigKey.CONFIG_DEFAULT_OBSERVER))) {
                    list.add(0, observation.getObserver());
                } else {
                    list.add(observation.getObserver());
                }
            }
        }

        return (IObserver[]) list.toArray(new IObserver[] {});

    }

}

class ObserverColorTableModel extends AbstractTableModel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private final PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
            .getBundle("de.lehmannet.om.ui.extension.variableStars.VariableStar", Locale.getDefault());

    private IObserver[] observers = null;
    private Color[] colors = null;

    public ObserverColorTableModel(IObserver[] observers, Color defaultColor) {

        this.observers = observers;
        this.colors = new Color[observers.length];

        // The first observer (default observer) gets automatically a Color assigned
        this.colors[0] = defaultColor;

    }

    @Override
    public int getColumnCount() {

        return 2;

    }

    @Override
    public int getRowCount() {

        return this.observers.length;

    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        switch (columnIndex) {
        case 0: {
            return this.observers[rowIndex].getDisplayName();
        }
        case 1: {
            return this.colors[rowIndex];
        }
        }

        return "";

    }

    @Override
    public void setValueAt(Object value, int row, int col) {

        // Make sure both lists are always the same size
        if (col == 0) {
            this.observers[row] = (IObserver) value;
        } else {
            this.colors[row] = (Color) value;
            if (Color.white.equals(this.colors[row])) { // White color is treated as no color selected
                this.colors[row] = null;
            }
        }

        fireTableCellUpdated(row, col);

    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {

        Class<?> c = null;

        switch (columnIndex) {
        case 0: {
            c = String.class;
            break;
        }
        case 1: {
            c = Color.class;
            break;
        }
        }

        return c;

    }

    @Override
    public String getColumnName(int column) {

        String name = "";

        switch (column) {
        case 0: {
            name = this.bundle.getString("popup.observerColor.column0");
            break;
        }
        case 1: {
            name = this.bundle.getString("popup.observerColor.column1");
            break;
        }
        }

        return name;

    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {

        return columnIndex == 1;

    }

    public Map<IObserver, Color> getResult() {

        Map<IObserver, Color> map = new HashMap<>();

        for (int i = 0; i < this.observers.length; i++) {
            if (this.colors[i] != null) {
                map.put(this.observers[i], this.colors[i]);
            }
        }

        // All observers were unselected
        if (map.size() == 0) {
            map = null;
        }

        return map;

    }

}

class ColorEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private final PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
            .getBundle("de.lehmannet.om.ui.extension.variableStars.VariableStar", Locale.getDefault());

    private Color currentColor;
    private final JButton button;
    private final JColorChooser colorChooser;
    private final JDialog dialog;

    private final String EDIT = "edit";

    public ColorEditor() {

        this.button = new JButton();
        this.button.setActionCommand(EDIT);
        this.button.addActionListener(this);
        this.button.setBorderPainted(false);

        // Set up the dialog that the button brings up.
        this.colorChooser = new JColorChooser();
        this.dialog = JColorChooser.createDialog(button, this.bundle.getString("popup.observerColor.colorEditor.title"),
                true, // modal
                colorChooser, this, // OK button handler
                null); // no CANCEL button handler

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (EDIT.equals(e.getActionCommand())) {
            // The user has clicked the cell, so bring up the dialog.
            this.button.setBackground(currentColor);
            this.colorChooser.setColor(currentColor);
            this.dialog.setVisible(true);

            fireEditingStopped(); // Make the renderer reappear.

        } else { // User pressed dialog's "OK" button.
            currentColor = colorChooser.getColor();
        }

    }

    // Implement the one CellEditor method that AbstractCellEditor doesn't.
    @Override
    public Object getCellEditorValue() {

        return currentColor;

    }

    // Implement the one method defined by TableCellEditor.
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

        if (value != null) {
            currentColor = (Color) value;
            this.button.setBackground(currentColor);
            this.button.setText("");
        } else {
            currentColor = null;
            this.button.setText(this.bundle.getString("popup.observerColor.noColorSelection"));
            this.button.setBackground(Color.LIGHT_GRAY);
        }

        return this.button;

    }

}

class VariableStarSelectorPopup extends JDialog implements ActionListener, TableModelListener {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private JButton ok = null;
    private JButton cancel = null;

    private JTextField beginField = null;
    private Calendar beginDate = null;
    private JButton beginPicker = null;
    private JTextField endField = null;
    private Calendar endDate = null;
    private JButton endPicker = null;

    private ObservationManager om = null;

    private final PropertyResourceBundle uiBundle = (PropertyResourceBundle) ResourceBundle
            .getBundle("de.lehmannet.om.ui.extension.variableStars.VariableStar", Locale.getDefault());

    private ExtendedSchemaTableModel tableModel = null;
    private final ObservationManagerModel model;

    /**
     *  @see SchemaElementConstants
     */
    public VariableStarSelectorPopup(ObservationManager om, ObservationManagerModel model) throws IllegalArgumentException, NoSuchElementException { 

        super(om, true);

        this.om = om;
        this.model = model;

        this.setTitle(this.uiBundle.getString("popup.selectVariableStar.title"));
        this.setSize(500, 250);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);

        ITarget[] elements = this.model.getTargets();

        this.tableModel = new ExtendedSchemaTableModel(elements, SchemaElementConstants.TARGET,
                TargetVariableStar.XML_XSI_TYPE_VALUE, false, null);

        // Check if there're variable star observations at all. If not, show popup and
        // return...
        Object o = this.tableModel.getValueAt(0, 0);
        if (o == null || (o instanceof String && "".equals(o))) {
            om.createInfo(this.uiBundle.getString("popup.selectVariableStar.info.noVariableStarObservations"));
            throw new IllegalArgumentException("No Variable Star Observation found.");
        }

        this.initDialog();

        this.setVisible(true);

    }

    @Override
    public void tableChanged(TableModelEvent e) {

        ExtendedSchemaTableModel model = (ExtendedSchemaTableModel) e.getSource();
        int row = model.getSelectedRow();

        // Make sure to reset fields, as otherwise we won't find all observation ins
        // getAllSelectedObservations
        this.beginDate = null;
        this.endDate = null;

        // Also clear UI
        this.beginField.setText("");
        this.endField.setText("");

        Object o = model.getValueAt(row, 0);
        if (o instanceof Boolean) {
            if ((Boolean) o) { // If checkbox marked

                IObservation[] observations = this.getAllSelectedObservations();

                if ((observations == null) || (observations.length == 0)) {
                    return;
                }

                // Get observations in a sorted way (newest observation at the beginning)
                ObservationComparator comparator = new ObservationComparator(true);
                TreeSet<IObservation> set = new TreeSet<>(comparator);
                set.addAll(Arrays.asList(observations));

                this.beginDate = ((IObservation) set.first()).getBegin();
                this.endDate = ((IObservation) set.last()).getBegin();

                this.beginField.setText(this.formatDate(this.beginDate));
                this.endField.setText(this.formatDate(this.endDate));
            }
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();
        if (source instanceof JButton) {
            JButton sourceButton = (JButton) source;
            if (sourceButton.equals(this.ok)) {
                this.dispose();
            } else if (sourceButton.equals(this.cancel)) {
                this.dispose();
                this.tableModel = null; // Set TableModel = null to indicate canceled UI
            } else if (sourceButton.equals(this.beginPicker)) {
                DatePicker dp = null;
                if (this.beginDate != null) {
                    dp = new DatePicker(this.om,
                            this.uiBundle.getString("popup.selectVariableStar.start.datePicker.title"), this.beginDate);
                } else {
                    dp = new DatePicker(this.om,
                            this.uiBundle.getString("popup.selectVariableStar.start.datePicker.title"));
                }

                // Make sure selected date is in observation period
                IObservation[] observations = this.getAllSelectedObservations();
                if ((observations != null) && (observations.length > 0)) {
                    // Get observations in a sorted way
                    ObservationComparator comparator = new ObservationComparator(true);
                    TreeSet<IObservation> set = new TreeSet<>(comparator);
                    set.addAll(Arrays.asList(observations));

                    Calendar first = ((IObservation) set.first()).getBegin();
                    Calendar last = ((IObservation) set.last()).getBegin();

                    if ((dp.getDate().before(first)) || (dp.getDate().after(last))) {
                        this.om.createWarning(
                                this.uiBundle.getString("popup.selectVariableStar.begin.datePicker.outOfScope"));
                        return;
                    }
                }

                // Set selected date
                this.beginDate = dp.getDate();
                this.beginField.setText(dp.getDateString());
            } else if (sourceButton.equals(this.endPicker)) {
                DatePicker dp = null;
                if (this.endDate != null) {
                    dp = new DatePicker(this.om,
                            this.uiBundle.getString("popup.selectVariableStar.end.datePicker.title"), this.endDate);
                } else if (this.beginDate != null) { // Try to initialize endDate Picker with startdate
                    dp = new DatePicker(this.om,
                            this.uiBundle.getString("popup.selectVariableStar.end.datePicker.title"), this.beginDate);
                } else {
                    dp = new DatePicker(this.om,
                            this.uiBundle.getString("popup.selectVariableStar.end.datePicker.title"));
                }

                // Make sure selected date is in observation period
                IObservation[] observations = this.getAllSelectedObservations();
                if ((observations != null) && (observations.length > 0)) {
                    // Get observations in a sorted way
                    ObservationComparator comparator = new ObservationComparator();
                    TreeSet<IObservation> set = new TreeSet<>(comparator);
                    set.addAll(Arrays.asList(observations));

                    Calendar first = ((IObservation) set.first()).getBegin();
                    Calendar last = ((IObservation) set.last()).getBegin();

                    if ((dp.getDate().before(first)) || (dp.getDate().after(last))) {
                        this.om.createWarning(
                                this.uiBundle.getString("popup.selectVariableStar.end.datePicker.outOfScope"));
                        return;
                    }
                }

                // Set selected date
                this.endDate = dp.getDate();
                this.endField.setText(dp.getDateString());
            }
        }

    }

    public IObservation[] getAllSelectedObservations() {

        if (this.tableModel == null) {
            return null;
        }

        List<ISchemaElement> selectedStars = this.tableModel.getAllSelectedElements();
        if ((selectedStars == null) || (selectedStars.isEmpty())) {
            return new IObservation[] {};
        }
        ITarget selectedStar = (ITarget) selectedStars.get(0);
        IObservation[] observations = this.model.getObservations(selectedStar);

        // Filter by start/end date
        List<IObservation> result = new ArrayList<>();
        for (IObservation observation : observations) {
            if ((observation.getBegin().before(this.beginDate)) || (observation.getBegin().after(this.endDate))) {
                // Observation not in selected time period
            } else {
                result.add(observation);
            }
        }

        return (IObservation[]) result.toArray(new IObservation[] {});

    }

    private void initDialog() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        this.getContentPane().setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 6, 1, 90, 90);
        constraints.fill = GridBagConstraints.BOTH;
        JTable table = new JTable(this.tableModel);
        table.setEnabled(true);
        table.setEditingColumn(1);
        table.setRowSelectionAllowed(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setDoubleBuffered(true);
        table.getModel().addTableModelListener(this);
        table.setToolTipText(this.uiBundle.getString("popup.selectVariableStar.table.tooltip"));
        JScrollPane scrollPane = new JScrollPane(table);
        gridbag.setConstraints(scrollPane, constraints);
        this.getContentPane().add(scrollPane);

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 1, 1, 2, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        JLabel beginLabel = new JLabel(this.uiBundle.getString("popup.selectVariableStar.label.beginDate"));
        beginLabel.setToolTipText(this.uiBundle.getString("popup.selectVariableStar.tooltip.beginDate"));
        gridbag.setConstraints(beginLabel, constraints);
        this.getContentPane().add(beginLabel);
        this.beginField = new JTextField();
        this.beginField.setEditable(false);
        this.beginField.setToolTipText(this.uiBundle.getString("popup.selectVariableStar.label.beginDate"));
        ConstraintsBuilder.buildConstraints(constraints, 1, 1, 1, 1, 10, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(beginField, constraints);
        this.getContentPane().add(beginField);
        this.beginPicker = new JButton("...");
        this.beginPicker.addActionListener(this);
        this.beginPicker.setToolTipText(this.uiBundle.getString("popup.selectVariableStar.button.beginDate"));
        ConstraintsBuilder.buildConstraints(constraints, 2, 1, 1, 1, 2, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(this.beginPicker, constraints);
        this.getContentPane().add(this.beginPicker);

        ConstraintsBuilder.buildConstraints(constraints, 3, 1, 1, 1, 2, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        JLabel endLabel = new JLabel(this.uiBundle.getString("popup.selectVariableStar.label.endDate"));
        endLabel.setToolTipText(this.uiBundle.getString("popup.selectVariableStar.tooltip.endDate"));
        gridbag.setConstraints(endLabel, constraints);
        this.getContentPane().add(endLabel);
        this.endField = new JTextField();
        this.endField.setEditable(false);
        this.endField.setToolTipText(this.uiBundle.getString("popup.selectVariableStar.label.endDate"));
        ConstraintsBuilder.buildConstraints(constraints, 4, 1, 1, 1, 10, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(endField, constraints);
        this.getContentPane().add(endField);
        this.endPicker = new JButton("...");
        this.endPicker.addActionListener(this);
        this.endPicker.setToolTipText(this.uiBundle.getString("popup.selectVariableStar.button.endDate"));
        ConstraintsBuilder.buildConstraints(constraints, 5, 1, 1, 1, 2, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(this.endPicker, constraints);
        this.getContentPane().add(this.endPicker);

        ConstraintsBuilder.buildConstraints(constraints, 0, 2, 3, 1, 5, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.ok = new JButton(this.uiBundle.getString("dialog.button.ok"));
        this.ok.addActionListener(this);
        gridbag.setConstraints(this.ok, constraints);
        this.getContentPane().add(this.ok);

        ConstraintsBuilder.buildConstraints(constraints, 3, 2, 3, 1, 5, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.cancel = new JButton(this.uiBundle.getString("dialog.button.cancel"));
        this.cancel.addActionListener(this);
        gridbag.setConstraints(this.cancel, constraints);
        this.getContentPane().add(this.cancel);

    }

    private String formatDate(Calendar cal) {

        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

        format.setCalendar(cal);
        return format.format(cal.getTime());

    }

}