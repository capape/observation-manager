/* ====================================================================
 * /dialog/CatalogDialog.java
 *
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.catalog.CatalogLoader;
import de.lehmannet.om.ui.catalog.ICatalog;
import de.lehmannet.om.ui.catalog.IListableCatalog;
import de.lehmannet.om.ui.i18n.TextManager;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.navigation.tableModel.AbstractSchemaTableModel;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.ui.panel.AbstractSearchPanel;
import de.lehmannet.om.ui.util.ConfigKey;
import de.lehmannet.om.ui.util.ConstraintsBuilder;

public class CatalogDialog extends OMDialog implements ComponentListener {

    private static final long serialVersionUID = 3360836026940203287L;

    private CatalogPanel panel = null;
    private final ObservationManagerModel model;
    private final TextManager textManager;

    public CatalogDialog(ObservationManager om, ObservationManagerModel model, TextManager textManager) {

        super(om);

        this.panel = new CatalogPanel(om, model, textManager);
        this.panel.addComponentListener(this);
        this.model = model;
        this.textManager = textManager;

        this.getContentPane().add(this.panel);

        this.setTitle(this.textManager.getString("dialog.catalog.title"));
        this.setModal(true);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setSize(CatalogDialog.serialVersionUID, 660, 340);
        this.setLocationRelativeTo(om);

        this.setVisible(true);

    }

    public ITarget getTarget() {

        return (ITarget) this.panel.getSchemaElement();

    }

    @Override
    public void componentHidden(ComponentEvent e) {

        this.dispose();

    }

    @Override
    public void componentMoved(ComponentEvent e) {

        // Do nothing

    }

    @Override
    public void componentResized(ComponentEvent e) {

        // Do nothing

    }

    @Override
    public void componentShown(ComponentEvent e) {

        // Do nothing

    }

}

class CatalogPanel extends AbstractPanel implements ActionListener {

    /**
     *
     */
    private static final long serialVersionUID = -8388323169559287306L;
    private final JComboBox<String> catalogBox = new JComboBox<>();
    private JButton searchButton = null;

    private ICatalog selectedCatalog = null;

    // In case a table can be shown (IListableCatalog)
    private final JTable table = new JTable();
    private AbstractSchemaTableModel tableModel = null;
    private JScrollPane scrollTable = null;
    private ObservationManager om = null;

    private JButton positive = null;

    private CatalogLoader loader = null;
    private ITarget selectedTarget = null;
    private final ObservationManagerModel model;
    private final TextManager textManager;

    public CatalogPanel(ObservationManager om, ObservationManagerModel model, TextManager textManager) {

        super(true);

        this.loader = om.getExtensionLoader().getCatalogLoader();
        this.om = om;
        this.textManager = textManager;
        this.model = model;

        String[] cNames = this.loader.getCatalogNames(); // Get all catalogs (listable and non-listable
        for (String cName : cNames) {
            this.catalogBox.addItem(cName);
        }
        String defaultCatalog = this.om.getConfiguration().getConfig(ConfigKey.CONFIG_DEFAULT_CATALOG);
        if ((defaultCatalog != null) && (!"".equals(defaultCatalog))) {
            this.catalogBox.setSelectedItem(this.om.getConfiguration().getConfig(ConfigKey.CONFIG_DEFAULT_CATALOG));
        }
        this.catalogBox.addActionListener(this);

        this.selectedCatalog = loader.getCatalog((String) this.catalogBox.getSelectedItem());

        // Load table (if possible)
        if (this.selectedCatalog instanceof IListableCatalog) {
            this.tableModel = ((IListableCatalog) this.selectedCatalog).getTableModel();
            this.table.setModel(this.tableModel);
        }

        // Do some settings for table
        this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListSelectionModel lsm = this.table.getSelectionModel();
        lsm.addListSelectionListener(e -> {
            // Ignore extra messages.
            if (e.getValueIsAdjusting())
                return;

            ListSelectionModel lsm1 = (ListSelectionModel) e.getSource();
            if (lsm1.isSelectionEmpty()) {
                // no rows are selected
            } else {
                int selectedRow = lsm1.getMinSelectionIndex();
                CatalogPanel.this.selectedTarget = (ITarget) tableModel.getSchemaElement(selectedRow);

                // Check if selected target exists already in cache...if so, use that one.
                ITarget[] targets = CatalogPanel.this.model.getTargets();
                for (ITarget target : targets) {
                    if (target.equals(CatalogPanel.this.selectedTarget)) {
                        CatalogPanel.this.selectedTarget = target; // Return already existing target, instead of
                        // the newly created one
                        break;
                    }
                }

            }
        });

        this.table.setDoubleBuffered(true);

        // Set column size
        this.setColumnSize();

        this.scrollTable = new JScrollPane(this.table);

        this.createPanel();

    }

    @Override
    public ISchemaElement createSchemaElement() {

        return this.selectedTarget;

    }

    @Override
    public ISchemaElement getSchemaElement() {

        return this.selectedTarget;

    }

    @Override
    public ISchemaElement updateSchemaElement() {

        return this.selectedTarget;

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // Change table if different catalog is selected
        if (e.getSource() instanceof JComboBox) {
            JComboBox box = (JComboBox) e.getSource();
            if (this.catalogBox.equals(box)) {
                String selected = (String) this.catalogBox.getSelectedItem();
                this.selectedCatalog = this.loader.getCatalog(selected);

                // Activate or deactivate search button
                if (this.selectedCatalog.getSearchPanel() == null) {
                    this.searchButton.setEnabled(false);
                } else {
                    this.searchButton.setEnabled(true);
                }

                // Show table in case catalog is listable. If not, show search dialog
                if (this.selectedCatalog instanceof IListableCatalog) {
                    this.tableModel = ((IListableCatalog) this.selectedCatalog).getTableModel();
                    this.table.setModel(this.tableModel);

                    // Set column size
                    this.setColumnSize();

                } else {
                    this.table.setModel(new DefaultTableModel());
                    this.showSearchDialog();
                }
            }
        } else if (e.getSource() instanceof JButton) {
            if (this.searchButton.equals(e.getSource())) {
                this.showSearchDialog();
            } else if (this.positive.equals(e.getSource())) {
                ISchemaElement result = this.createSchemaElement();
                if (result != null) {
                    this.model.add(result);
                    this.om.setChanged(true);
                    this.processComponentEvent(new ComponentEvent(this, ComponentEvent.COMPONENT_HIDDEN));
                }
            }
        }

    }

    private void showSearchDialog() {

        AbstractSearchPanel panel = this.selectedCatalog.getSearchPanel();
        SearchDialog sd = new SearchDialog(this.textManager.getString("dialog.catalog.search.title"), panel, this, om);
        this.selectedTarget = (ITarget) sd.getSearchResult();
        if (this.selectedTarget != null) {
            // Check if selected target exists already in cache...if so, use that one.
            ITarget[] targets = this.model.getTargets();
            for (ITarget target : targets) {
                if (target.equals(this.selectedTarget)) {
                    this.selectedTarget = target; // Return already existing target, instead of the newly created
                    // one
                    this.processComponentEvent(new ComponentEvent(this, ComponentEvent.COMPONENT_HIDDEN));
                    return;
                }
            }
            // Target didn't exist so far, so create new one
            this.model.add(this.selectedTarget);
            this.om.setChanged(true);
            this.processComponentEvent(new ComponentEvent(this, ComponentEvent.COMPONENT_HIDDEN));
        }

    }

    private void setColumnSize() {

        if (table.getColumnModel().getColumnCount() <= 1) {
            return; // No settings necessary
        }

        this.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        TableColumn col = table.getColumnModel().getColumn(0);
        col.setPreferredWidth(((AbstractSchemaTableModel) this.table.getModel()).getColumnSize(0));

        col = table.getColumnModel().getColumn(1);
        col.setPreferredWidth(((AbstractSchemaTableModel) this.table.getModel()).getColumnSize(1));

        this.table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

    }

    private void createPanel() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        this.setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 1, 1, 5, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        JLabel LcatalogName = new JLabel(this.textManager.getString("dialog.catalog.label.catalogName"));
        gridbag.setConstraints(LcatalogName, constraints);
        this.add(LcatalogName);
        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 1, 1, 45, 1);
        gridbag.setConstraints(this.catalogBox, constraints);
        this.add(this.catalogBox);

        ConstraintsBuilder.buildConstraints(constraints, 1, 1, 1, 1, 20, 1);
        this.searchButton = new JButton(this.textManager.getString("dialog.catalog.label.searchButton"));
        this.searchButton.setToolTipText(this.textManager.getString("dialog.catalog.tooltip.searchButton"));
        this.searchButton.addActionListener(this);
        if (this.selectedCatalog.getSearchPanel() == null) { // No search dialog available
            this.searchButton.setEnabled(false);
        }
        gridbag.setConstraints(this.searchButton, constraints);
        this.add(this.searchButton);

        ConstraintsBuilder.buildConstraints(constraints, 0, 2, 2, 1, 5, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        JLabel LTargets = new JLabel(this.textManager.getString("dialog.catalog.label.targets"));
        gridbag.setConstraints(LTargets, constraints);
        this.add(LTargets);
        ConstraintsBuilder.buildConstraints(constraints, 0, 3, 2, 5, 45, 10);
        gridbag.setConstraints(this.scrollTable, constraints);
        this.scrollTable.setMinimumSize(new Dimension(200, 200));
        this.add(this.scrollTable);

        ConstraintsBuilder.buildConstraints(constraints, 0, 8, 2, 1, 45, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.positive = new JButton(this.textManager.getString("dialog.catalog.positive"));
        this.positive.addActionListener(this);
        gridbag.setConstraints(this.positive, constraints);
        this.add(this.positive);

        ConstraintsBuilder.buildConstraints(constraints, 0, 9, 2, 1, 45, 86);
        constraints.fill = GridBagConstraints.BOTH;
        JLabel Lfill = new JLabel("");
        gridbag.setConstraints(Lfill, constraints);
        this.add(Lfill);

    }

}
