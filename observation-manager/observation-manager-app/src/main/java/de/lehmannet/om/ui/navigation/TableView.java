/*
 * ====================================================================
 * /navigation/TableView.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.navigation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.lehmannet.om.Angle;
import de.lehmannet.om.IEyepiece;
import de.lehmannet.om.IFilter;
import de.lehmannet.om.IImager;
import de.lehmannet.om.ILens;
import de.lehmannet.om.IObservation;
import de.lehmannet.om.IObserver;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.IScope;
import de.lehmannet.om.ISession;
import de.lehmannet.om.ISite;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.SchemaElement;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.cache.UIDataCache;
import de.lehmannet.om.ui.i18n.TextManager;
import de.lehmannet.om.ui.navigation.tableModel.AbstractSchemaTableModel;
import de.lehmannet.om.ui.navigation.tableModel.EyepieceTableModel;
import de.lehmannet.om.ui.navigation.tableModel.FilterTableModel;
import de.lehmannet.om.ui.navigation.tableModel.ImagerTableModel;
import de.lehmannet.om.ui.navigation.tableModel.LensTableModel;
import de.lehmannet.om.ui.navigation.tableModel.ObservationTableModel;
import de.lehmannet.om.ui.navigation.tableModel.ObserverTableModel;
import de.lehmannet.om.ui.navigation.tableModel.ScopeTableModel;
import de.lehmannet.om.ui.navigation.tableModel.SessionTableModel;
import de.lehmannet.om.ui.navigation.tableModel.SiteTableModel;
import de.lehmannet.om.ui.navigation.tableModel.TableSorter;
import de.lehmannet.om.ui.navigation.tableModel.TargetTableModel;
import de.lehmannet.om.ui.util.IConfiguration;
import de.lehmannet.om.util.SchemaElementConstants;

public class TableView extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = 5954822521626021103L;

    private static final String CONFIG_TABLESETTINGS_PREFIX = "om.tableSetting.";
    private static final Logger LOGGER = LoggerFactory.getLogger(TableView.class);

    final private ObservationManager observationManager;
    private JTable table = null;
    private AbstractSchemaTableModel abstractSchemaTableModel = null;
    private JScrollPane scrollTable = null;
    private TableSorter sorter = null;

    private ISchemaElement selectedElement = null;
    // Parent element (in TreeView) of the selectedElement
    // Can be null -> Show all oberservations
    private ISchemaElement parentElement = null;

    private final ObservationManagerModel model;
    private final TextManager textManager;

    private final UIDataCache cache;

    public TableView(ObservationManager om, ObservationManagerModel omModel, TextManager textManager,
            UIDataCache cache) {

        this.observationManager = om;
        this.model = omModel;
        this.textManager = textManager;
        this.cache = cache;

        // this.observationManager = om;

        this.abstractSchemaTableModel = new ObservationTableModel(null, this.observationManager);
        this.sorter = new TableSorter(null);
        this.table = new JTable(this.abstractSchemaTableModel);
        this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListSelectionModel lsm = this.table.getSelectionModel();
        lsm.addListSelectionListener(selectionListener());

        this.table.setDoubleBuffered(true);
        this.setLayout(new BorderLayout());
        this.scrollTable = new JScrollPane(this.table);
        this.add(scrollTable);

        this.table.setDefaultRenderer(Angle.class, angleRenderer());
        this.table.setDefaultRenderer(Float.class, floatRenderer());
        this.table.setDefaultRenderer(Integer.class, integerRenderer());
        this.table.setDefaultRenderer(LocalDateTime.class, localDateTimeRenderer());
        this.table.setDefaultRenderer(ZonedDateTime.class, zonedDateTimeRenderer());
        this.table.setDefaultRenderer(OffsetDateTime.class, offsetDateTimeRenderer());
        this.table.setDefaultRenderer(SchemaElement.class, schemaElementRenderer());
        this.table.setDefaultRenderer(Object.class, objectRenderer());

        MouseListener ml = mouseListener();
        this.table.addMouseListener(ml);

        // Load table column settings
        this.loadSettings();

    }

    private MouseAdapter mouseListener() {
        return new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {

                    // Convert coordinates
                    MouseEvent c = SwingUtilities.convertMouseEvent(TableView.this.table, e,
                            TableView.this.observationManager);
                    Point p = new Point(c.getX(), c.getY());

                    int row = TableView.this.table.getSelectedRow();
                    if (TableView.this.sorter.isSorting()) {
                        row = TableView.this.sorter.modelIndex(row);
                    }

                    ISchemaElement element = ((AbstractSchemaTableModel) (TableView.this.table.getModel()))
                            .getSchemaElement(row);

                    if (element != null) {
                        byte options = (byte) (PopupMenuHandler.EDIT + PopupMenuHandler.CREATE_HTML
                                + PopupMenuHandler.CREATE_XML + PopupMenuHandler.DELETE
                                + PopupMenuHandler.CREATE_NEW_OBSERVATION + PopupMenuHandler.EXTENSIONS);

                        new PopupMenuHandler(TableView.this.observationManager, TableView.this.model,
                                TableView.this.textManager, element, p.x, p.y, options, SchemaElementConstants.NONE,
                                TableView.this.observationManager.getExtensionLoader().getPopupMenus(),
                                TableView.this.cache);
                    }
                }
            }
        };
    }

    private ListSelectionListener selectionListener() {

        return new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                // TODO Auto-generated method stub
                if (e.getValueIsAdjusting())
                    return;

                ListSelectionModel lsm1 = (ListSelectionModel) e.getSource();
                if (lsm1.isSelectionEmpty()) {
                    // no rows are selected
                } else {
                    int selectedRow = lsm1.getMinSelectionIndex();
                    int selectedSortedRow = TableView.this.sorter.modelIndex(selectedRow);
                    ISchemaElement se = abstractSchemaTableModel.getSchemaElement(selectedSortedRow);

                    // Update ItemView
                    TableView.this.updateItemView(se);

                    // If current selection and new selection is equal stop here
                    // Don't do this before the ItemView was updated (above) as otherwise, clicking
                    // in the
                    // TreeView won't update the ItemView.
                    // RootCause for this is the setting of the selectedElement down in the
                    // updateTable()
                    // method, which come before the rowSelectionInterval gets reset. (Chaning the
                    // sequence there
                    // causes even more trouble...:-( )
                    if ((se != null) && (se.equals(TableView.this.selectedElement))) {
                        return;
                    }

                    // Update TreeView
                    if (se instanceof IObservation) {
                        observationManager.getTreeView().setSelection(se, parentElement);
                    } else {
                        observationManager.getTreeView().setSelection(se, null);
                    }
                }
            }

        };

    }

    private TableCellRenderer objectRenderer() {
        return (table, value, isSelected, hasFocus, row, column) -> {
            DefaultTableCellRenderer cr = new DefaultTableCellRenderer();
            if (value != null) {
                cr.setText(value.toString());
            }
            if (isSelected) {
                cr.setBackground(Color.LIGHT_GRAY);
            }

            return cr;
        };
    }

    private TableCellRenderer schemaElementRenderer() {
        return (table, value, isSelected, hasFocus, row, column) -> {
            DefaultTableCellRenderer cr = new DefaultTableCellRenderer();
            if (value != null) {
                if (value instanceof ISchemaElement) {
                    ISchemaElement se = (ISchemaElement) value;
                    cr.setText(se.getDisplayName());
                }
            }
            if (isSelected) {
                cr.setBackground(Color.LIGHT_GRAY);
            }

            return cr;
        };
    }

    private TableCellRenderer offsetDateTimeRenderer() {
        return (table, value, isSelected, hasFocus, row, column) -> {
            DefaultTableCellRenderer cr = new DefaultTableCellRenderer();
            if (value != null) {

                if (value instanceof OffsetDateTime) {
                    final OffsetDateTime cal = (OffsetDateTime) value;
                    cr.setText(this.observationManager.getDateManager()
                            .zonedDateTimeToStringWithHour(cal.toZonedDateTime()));
                } else {
                    LOGGER.warn("Bad data {}", value.getClass(), value);
                }
            }

            if (isSelected) {
                cr.setBackground(Color.LIGHT_GRAY);
            }

            return cr;
        };
    }

    private TableCellRenderer zonedDateTimeRenderer() {
        return (table, value, isSelected, hasFocus, row, column) -> {
            DefaultTableCellRenderer cr = new DefaultTableCellRenderer();
            if (value != null) {

                if (value instanceof ZonedDateTime) {
                    final ZonedDateTime cal = (ZonedDateTime) value;
                    cr.setText(this.observationManager.getDateManager().zonedDateTimeToStringWithHour(cal));
                } else {
                    LOGGER.warn("Bad data {}", value.getClass(), value);
                }
            }

            if (isSelected) {
                cr.setBackground(Color.LIGHT_GRAY);
            }

            return cr;
        };
    }

    private TableCellRenderer localDateTimeRenderer() {
        return (table, value, isSelected, hasFocus, row, column) -> {
            DefaultTableCellRenderer cr = new DefaultTableCellRenderer();
            if (value != null) {

                if (value instanceof LocalDateTime) {
                    final LocalDateTime cal = (LocalDateTime) value;
                    cr.setText(this.observationManager.getDateManager()
                            .zonedDateTimeToStringWithHour(ZonedDateTime.of(cal, ZonedDateTime.now().getOffset())));
                } else {
                    LOGGER.warn("Bad data {}", value.getClass(), value);
                }
            }

            if (isSelected) {
                cr.setBackground(Color.LIGHT_GRAY);
            }

            return cr;
        };
    }

    private TableCellRenderer integerRenderer() {
        return (table, value, isSelected, hasFocus, row, column) -> {

            DefaultTableCellRenderer cr = new DefaultTableCellRenderer();
            if (value != null) {
                Integer i = (Integer) value;
                cr.setText("" + i);
            }
            if (isSelected) {
                cr.setBackground(Color.LIGHT_GRAY);
            }

            return cr;
        };
    }

    private TableCellRenderer angleRenderer() {
        return (table, value, isSelected, hasFocus, row, column) -> {

            DefaultTableCellRenderer cr = new DefaultTableCellRenderer();
            if (value != null) {
                String result = null;
                Angle angle = (Angle) value;
                DecimalFormat df = new DecimalFormat("0.00");
                DecimalFormatSymbols dfs = new DecimalFormatSymbols();
                dfs.setDecimalSeparator('.');
                df.setDecimalFormatSymbols(dfs);
                result = df.format(angle.getValue()) + " " + angle.getUnit();
                cr.setText(result);
            }
            if (isSelected) {
                cr.setBackground(Color.LIGHT_GRAY);
            }

            return cr;
        };
    }

    private TableCellRenderer floatRenderer() {
        return (table, value, isSelected, hasFocus, row, column) -> {

            DefaultTableCellRenderer cr = new DefaultTableCellRenderer();
            if (value != null) {

                Float f = (Float) value;
                if (Float.isNaN(f)) {
                    cr.setText("");
                } else {
                    String result = null;
                    DecimalFormat df = new DecimalFormat("0.00");
                    DecimalFormatSymbols dfs = new DecimalFormatSymbols();
                    dfs.setDecimalSeparator('.');
                    df.setDecimalFormatSymbols(dfs);
                    result = df.format(f.floatValue());
                    cr.setText(result);
                }
            }
            if (isSelected) {
                cr.setBackground(Color.LIGHT_GRAY);
            }

            return cr;
        };
    }

    public void showObservations(IObservation selected, ISchemaElement parentElement) {

        // Save column settings from current table model
        this.saveCurrentTableModelSettings();

        // parentElement can be null
        IObservation[] obs = null;
        if (parentElement == null) { // Load all observations
            obs = this.model.getObservations();
            this.parentElement = null; // No parent was found, clear our instance parentElement
        } else { // Load all observations belonging to the parent element
            obs = this.model.getObservations(parentElement);

            // If the parentElement is an IObserver, we also need to access the observations
            // where this observer
            // is the coObserver
            // Also we attach the coObserver Observations to the other observations, as the
            // both will
            // be listed under the observer node (in different font/color)
            if (parentElement instanceof IObserver) {
                IObservation[] coObserver = this.model.getCoObserverObservations((IObserver) parentElement);
                if (coObserver != null) {
                    // Add coObserver observations to other observations (and remove doublicates via
                    // HashSet)
                    List<IObservation> obsList = new ArrayList<>(Arrays.asList(obs));
                    int coObsLength = coObserver.length;
                    for (IObservation iObservation : coObserver) {
                        if (!obsList.contains(iObservation)) { // New observation
                            obsList.add(iObservation);
                        } else { // Doublicate
                            coObsLength--; // One coObserver observation that won't be counted
                        }
                    }
                    obs = (IObservation[]) obsList.toArray(new IObservation[] {});
                }
            }

            this.parentElement = parentElement;
        }

        if ((obs != null) && (obs.length > 0)) {
            /*
             * if( this.sorter.getTableModel() instanceof ObservationTableModel) { this.sorter.setTableModel(new
             * ObservationTableModel(obs), false); } else {
             */
            this.sorter.setTableModel(new ObservationTableModel(obs, this.observationManager), true);
            // }
            this.abstractSchemaTableModel = this.sorter;
            this.sorter.setTableHeader(table.getTableHeader());

            this.updateTable(selected);
        } else {

            // Nothing to show...remove tableModel, and clear ItemView

            // Do this later in UI Thread to avoid exception:
            // java.lang.ArrayIndexOutOfBoundsException: 0 >= 0
            // at java.util.Vector.elementAt(Vector.java:427)
            SwingUtilities.invokeLater(() -> TableView.this.table
                    .setModel(new ObservationTableModel(null, TableView.this.observationManager)));
            // this.table.setModel(new ObservationTableModel(null,
            // this.observationManager));

            this.observationManager.getItemView().clear();
        }

        // Load new column settings for new table model
        this.loadCurrentTableModelSettings();

    }

    public void showObservers(IObserver selected) {

        // Save column settings from current table model
        this.saveCurrentTableModelSettings();

        IObserver[] obs = this.model.getObservers();
        if ((obs != null) && (obs.length > 0)) {
            /*
             * if( this.sorter.getTableModel() instanceof ObserverTableModel) { this.sorter.setTableModel(new
             * ObserverTableModel(obs), false); } else {
             */
            this.sorter.setTableModel(new ObserverTableModel(obs), true);
            // }
            this.abstractSchemaTableModel = this.sorter;
            this.sorter.setTableHeader(table.getTableHeader());

            this.updateTable(selected);
        } else {
            // Nothing to show...remove tableModel, and clear ItemView
            this.table.setModel(new ObserverTableModel(null));
            this.observationManager.getItemView().clear();
        }

        // Load new column settings for new table model
        this.loadCurrentTableModelSettings();

    }

    public void showTargets(ITarget selected) {

        // Save column settings from current table model
        this.saveCurrentTableModelSettings();

        ITarget[] targets = this.model.getTargets();
        if ((targets != null) && (targets.length > 0)) {
            /*
             * if( this.sorter.getTableModel() instanceof TargetTableModel) { this.sorter.setTableModel(new
             * TargetTableModel(targets), false); } else {
             */
            this.sorter.setTableModel(new TargetTableModel(targets, this.observationManager.getConfiguration()), true);
            // }
            this.abstractSchemaTableModel = this.sorter;
            this.sorter.setTableHeader(table.getTableHeader());

            this.updateTable(selected);
        } else {
            // Nothing to show...remove tableModel, and clear ItemView
            this.table.setModel(new TargetTableModel(null, this.observationManager.getConfiguration()));
            this.observationManager.getItemView().clear();
        }

        // Load new column settings for new table model
        this.loadCurrentTableModelSettings();

    }

    public void showSites(ISite selected) {

        // Save column settings from current table model
        this.saveCurrentTableModelSettings();

        ISite[] sites = this.model.getSites();
        if ((sites != null) && (sites.length > 0)) {
            /*
             * if( this.sorter.getTableModel() instanceof SiteTableModel) { this.sorter.setTableModel(new
             * SiteTableModel(sites), false); } else {
             */
            this.sorter.setTableModel(new SiteTableModel(sites), true);
            // }
            this.abstractSchemaTableModel = this.sorter;
            this.sorter.setTableHeader(table.getTableHeader());

            this.updateTable(selected);
        } else {
            // Nothing to show...remove tableModel, and clear ItemView
            this.table.setModel(new SiteTableModel(null));
            this.observationManager.getItemView().clear();
        }

        // Load new column settings for new table model
        this.loadCurrentTableModelSettings();

    }

    public void showScopes(IScope selected) {

        // Save column settings from current table model
        this.saveCurrentTableModelSettings();

        IScope[] scopes = this.model.getScopes();
        if ((scopes != null) && (scopes.length > 0)) {
            /*
             * if( this.sorter.getTableModel() instanceof ScopeTableModel) { this.sorter.setTableModel(new
             * ScopeTableModel(scopes), false); } else {
             */
            this.sorter.setTableModel(new ScopeTableModel(scopes), true);
            // }
            this.abstractSchemaTableModel = this.sorter;
            this.sorter.setTableHeader(table.getTableHeader());

            this.updateTable(selected);
        } else {
            // Nothing to show...remove tableModel, and clear ItemView
            this.table.setModel(new ScopeTableModel(null));
            this.observationManager.getItemView().clear();
        }

        // Load new column settings for new table model
        this.loadCurrentTableModelSettings();

    }

    public void showSessions(ISession selected) {

        // Save column settings from current table model
        this.saveCurrentTableModelSettings();

        ISession[] session = this.model.getSessions();
        if ((session != null) && (session.length > 0)) {
            /*
             * if( this.sorter.getTableModel() instanceof SessionTableModel) { this.sorter.setTableModel(new
             * SessionTableModel(session), false); } else {
             */
            this.sorter.setTableModel(new SessionTableModel(session), true);
            // }
            this.abstractSchemaTableModel = this.sorter;
            this.sorter.setTableHeader(table.getTableHeader());

            this.updateTable(selected);
        } else {
            // Nothing to show...remove tableModel, and clear ItemView
            this.table.setModel(new SessionTableModel(null));
            this.observationManager.getItemView().clear();
        }

        // Load new column settings for new table model
        this.loadCurrentTableModelSettings();

    }

    public void showImagers(IImager selected) {

        // Save column settings from current table model
        this.saveCurrentTableModelSettings();

        IImager[] imager = this.model.getImagers();
        if ((imager != null) && (imager.length > 0)) {
            /*
             * if( this.sorter.getTableModel() instanceof ImagerTableModel) { this.sorter.setTableModel(new
             * ImagerTableModel(imager), false); } else {
             */
            this.sorter.setTableModel(new ImagerTableModel(imager), true);
            // }
            this.abstractSchemaTableModel = this.sorter;
            this.sorter.setTableHeader(table.getTableHeader());

            this.updateTable(selected);
        } else {
            // Nothing to show...remove tableModel, and clear ItemView
            this.table.setModel(new ImagerTableModel(null));
            this.observationManager.getItemView().clear();
        }

        // Load new column settings for new table model
        this.loadCurrentTableModelSettings();

    }

    public void showFilters(IFilter selected) {

        // Save column settings from current table model
        this.saveCurrentTableModelSettings();

        IFilter[] filter = this.model.getFilters();
        if ((filter != null) && (filter.length > 0)) {
            /*
             * if( this.sorter.getTableModel() instanceof FilterTableModel) { this.sorter.setTableModel(new
             * FilterTableModel(filter), false); } else {
             */
            this.sorter.setTableModel(new FilterTableModel(filter), true);
            // }
            this.abstractSchemaTableModel = this.sorter;
            this.sorter.setTableHeader(table.getTableHeader());

            this.updateTable(selected);
        } else {
            // Nothing to show...remove tableModel, and clear ItemView
            this.table.setModel(new FilterTableModel(null));
            this.observationManager.getItemView().clear();
        }

        // Load new column settings for new table model
        this.loadCurrentTableModelSettings();

    }

    public void showEyepieces(IEyepiece selected) {

        // Save column settings from current table model
        this.saveCurrentTableModelSettings();

        IEyepiece[] eyepiece = this.model.getEyepieces();
        if ((eyepiece != null) && (eyepiece.length > 0)) {
            /*
             * if( this.sorter.getTableModel() instanceof EyepieceTableModel) { this.sorter.setTableModel(new
             * EyepieceTableModel(eyepiece), false); } else {
             */
            this.sorter.setTableModel(new EyepieceTableModel(eyepiece), true);
            // }
            this.abstractSchemaTableModel = this.sorter;
            this.sorter.setTableHeader(table.getTableHeader());

            this.updateTable(selected);
        } else {
            // Nothing to show...remove tableModel, and clear ItemView
            this.table.setModel(new EyepieceTableModel(null));
            this.observationManager.getItemView().clear();
        }

        // Load new column settings for new table model
        this.loadCurrentTableModelSettings();

    }

    public void showLenses(ILens selected) {

        // Save column settings from current table model
        this.saveCurrentTableModelSettings();

        ILens[] lens = this.model.getLenses();
        if ((lens != null) && (lens.length > 0)) {
            /*
             * if( this.sorter.getTableModel() instanceof EyepieceTableModel) { this.sorter.setTableModel(new
             * EyepieceTableModel(eyepiece), false); } else {
             */
            this.sorter.setTableModel(new LensTableModel(lens), true);
            // }
            this.abstractSchemaTableModel = this.sorter;
            this.sorter.setTableHeader(table.getTableHeader());

            this.updateTable(selected);
        } else {
            // Nothing to show...remove tableModel, and clear ItemView
            this.table.setModel(new LensTableModel(null));
            this.observationManager.getItemView().clear();
        }

        // Load new column settings for new table model
        this.loadCurrentTableModelSettings();

    }

    public ISchemaElement getSelectedElement() {

        return this.selectedElement;

    }

    public void reloadLanguage() {

        AbstractSchemaTableModel.reloadLanguage(); // Reload static bundle

    }

    void saveSettings() {

        // Make sure current settings are saved as well
        this.saveCurrentTableModelSettings();

        IConfiguration config = observationManager.getConfiguration();
        Iterator<String> iterator = cache.keySet().iterator();
        String currentKey = null;
        while (iterator.hasNext()) {
            currentKey = iterator.next();
            if (currentKey.startsWith(TableView.CONFIG_TABLESETTINGS_PREFIX)) {
                config.setConfig(currentKey, "" + cache.getString(currentKey));
            }
        }

    }

    private void loadSettings() {

        final IConfiguration config = this.observationManager.getConfiguration();
        final Set<String> tableKeys = config.getKeysStartingWith(TableView.CONFIG_TABLESETTINGS_PREFIX);

        for (String currentKey : tableKeys) {
            cache.putString(currentKey, config.getConfig(currentKey));
        }

        // Now set the loaded settings
        this.loadCurrentTableModelSettings();

    }

    private void updateItemView(ISchemaElement se) {

        if (se != null) {
            if (se instanceof IObservation) {
                observationManager.getItemView().showObservation((IObservation) se);
            } else if (se instanceof ITarget) {
                observationManager.getItemView().showTarget((ITarget) se, null);
            } else if (se instanceof ISite) {
                observationManager.getItemView().showSite((ISite) se);
            } else if (se instanceof IScope) {
                observationManager.getItemView().showScope((IScope) se);
            } else if (se instanceof ISession) {
                observationManager.getItemView().showSession((ISession) se);
            } else if (se instanceof IObserver) {
                observationManager.getItemView().showObserver((IObserver) se);
            } else if (se instanceof IFilter) {
                observationManager.getItemView().showFilter((IFilter) se);
            } else if (se instanceof IEyepiece) {
                observationManager.getItemView().showEyepiece((IEyepiece) se);
            } else if (se instanceof IImager) {
                observationManager.getItemView().showImager((IImager) se);
            } else if (se instanceof ILens) {
                observationManager.getItemView().showLens((ILens) se);
            }
        }

    }

    private void updateTable(ISchemaElement selected) {

        // Current and selected element is equal...so we can stop here
        /*
         * if( (selected != null) && (selected.equals(this.selectedElement)) ) { return; }
         */// Comment this out 02.04.08: Need to update table (setSelection) and ItemView
            // as selected element can have
            // different parent elements in tree

        // Do this later in UI Thread to avoid exception:
        // java.lang.ArrayIndexOutOfBoundsException: 0 >= 0
        // at java.util.Vector.elementAt(Vector.java:427)
        final ISchemaElement finalSelected = selected;
        SwingUtilities.invokeLater(() -> {

            TableView.this.table.setModel(TableView.this.abstractSchemaTableModel);

            int sel = TableView.this.abstractSchemaTableModel.getRow(finalSelected);
            if (TableView.this.sorter.isSorting()) {
                sel = TableView.this.sorter.viewIndex(sel);
            }

            TableView.this.selectedElement = finalSelected;
            TableView.this.model.setSelectedElement(finalSelected);

            TableView.this.table.setRowSelectionInterval(sel, sel);

            // 2011/09/06 --- Put the whole block into the invokeLater method, as sorted
            // tables don't scroll ViewPort to
            // right possition -> Bug ID: 3404084 (Sort Targets by RA, select Target in
            // Table)
            // 2011/09/06 }

            // 2011/09/06 });
            // 2011/09/06 int sel = this.model.getRow(selected);

            /*
             * this.table.setModel(this.model);
             *
             * int sel = this.model.getRow(selected); if( this.sorter.isSorting() ) { sel = this.sorter.viewIndex(sel);
             * }
             *
             * this.selectedElement = selected;
             *
             * this.table.setRowSelectionInterval(sel, sel);
             */

            // 2011/09/06 JViewport viewport = this.scrollTable.getViewport();
            JViewport viewport = TableView.this.scrollTable.getViewport();

            // This rectangle is relative to the table where the
            // northwest corner of cell (0,0) is always (0,0).
            Rectangle rect = table.getCellRect(sel, sel, true);

            // The location of the view relative to the table
            Rectangle viewRect = viewport.getViewRect();

            // Translate the cell location so that it is relative
            // to the view, assuming the northwest corner of the
            // view is (0,0).
            rect.setLocation(rect.x - viewRect.x, rect.y - viewRect.y);

            // Calculate location of rect if it were at the center of view
            int centerX = (viewRect.width - rect.width) / 2;
            int centerY = (viewRect.height - rect.height) / 2;

            // Fake the location of the cell so that scrollRectToVisible
            // will move the cell to the center
            if (rect.x < centerX) {
                centerX = -centerX;
            }
            if (rect.y < centerY) {
                centerY = -centerY;
            }
            rect.translate(centerX, centerY);

            // Scroll the area into view.
            viewport.scrollRectToVisible(rect);

        });
    }

    private void saveCurrentTableModelSettings() {

        String currentTableModelID = null;
        if ((sorter != null) && (sorter.getTableModel() != null)) {
            currentTableModelID = sorter.getTableModel().getID();
        } else {
            currentTableModelID = abstractSchemaTableModel.getID();
        }

        TableColumnModel tcm = table.getColumnModel();
        Enumeration<TableColumn> en = tcm.getColumns();
        TableColumn current = null;
        int preferedWidth;
        while (en.hasMoreElements()) {
            current = en.nextElement();
            preferedWidth = current.getPreferredWidth();
            String key = TableView.CONFIG_TABLESETTINGS_PREFIX + currentTableModelID + "." + current.getModelIndex();
            this.cache.putString(key, String.valueOf(preferedWidth));
        }

    }

    private void loadCurrentTableModelSettings() {

        String currentTableModelID = null;
        if ((sorter != null) && (sorter.getTableModel() != null)) {
            currentTableModelID = sorter.getTableModel().getID();
        } else {
            currentTableModelID = abstractSchemaTableModel.getID();
        }

        TableColumnModel tcm = table.getColumnModel();
        Enumeration<TableColumn> en = tcm.getColumns();
        while (en.hasMoreElements()) {
            TableColumn current = en.nextElement();
            String keySettings = TableView.CONFIG_TABLESETTINGS_PREFIX + currentTableModelID + "."
                    + current.getModelIndex();
            String value = this.cache.getString(keySettings);
            if (value == null)
                break;
            try {
                int preferedWidth = Integer.parseInt(value);
                current.setPreferredWidth(preferedWidth);
            } catch (NumberFormatException nfe) {
                LOGGER.warn("Cannot read property {}", current, nfe);
            }
        }

    }

}
