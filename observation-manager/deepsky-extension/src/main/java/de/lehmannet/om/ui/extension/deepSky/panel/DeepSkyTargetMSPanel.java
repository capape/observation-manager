/*
 * ====================================================================
 * /extension/deepSky/panel/DeepSkyTargetMSPanel.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.deepSky.panel;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import de.lehmannet.om.IObserver;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.TargetStar;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetMS;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.container.TargetContainer;
import de.lehmannet.om.ui.dialog.SchemaElementSelectorPopup;
import de.lehmannet.om.ui.dialog.TargetStarDialog;
import de.lehmannet.om.ui.navigation.tableModel.TargetTableModel;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.UserInterfaceHelper;
import de.lehmannet.om.util.SchemaElementConstants;

public class DeepSkyTargetMSPanel extends AbstractPanel implements ActionListener {

    private static final long serialVersionUID = 9161244919972564982L;

    private final ResourceBundle bundle = ResourceBundle.getBundle("de.lehmannet.om.ui.extension.deepSky.DeepSky",
            Locale.getDefault());

    private DeepSkyTargetMS target = null;

    private TargetContainer targetContainer = null;

    private JTable componentStars = null;
    private TargetTableModel tableModel = null;
    private TargetStar selectedStar = null;

    private JButton addExistingStar = null;
    private JButton addNewStar = null;
    private JButton editStar = null;
    private JButton deleteStar = null;
    private final ObservationManagerModel model;
    private final UserInterfaceHelper uiHelper;

    public DeepSkyTargetMSPanel(UserInterfaceHelper uiHelper, ObservationManagerModel model, ITarget target,
            Boolean editable) throws IllegalArgumentException {

        super(editable);

        if ((target != null) && !(target instanceof DeepSkyTargetMS)) {
            throw new IllegalArgumentException(
                    "Passed ITarget must derive from de.lehmannet.om.extension.deepSky.DeepSkyTargetMS\n");
        }

        this.target = (DeepSkyTargetMS) target;
        this.uiHelper = uiHelper;
        this.model = model;

        this.createPanel();

        if (this.target != null) {
            this.loadSchemaElement();
        }

    }

    // --------------
    // ActionListener ---------------------------------------------------------
    // --------------

    @Override
    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();
        if (source instanceof JButton) {
            if (source.equals(this.addNewStar)) {
                TargetStarDialog ts = new TargetStarDialog(null, this.uiHelper, this.model, null);
                ITarget t = ts.getTarget();
                if (t != null) {
                    this.tableModel.addTarget(t);
                }
            } else if (source.equals(this.addExistingStar)) {
                SchemaElementSelectorPopup targetSelector = new SchemaElementSelectorPopup(null, this.model,
                        this.bundle.getString("popup.ms.addExistingStars"), TargetStar.XML_XSI_TYPE_VALUE,
                        Arrays.asList(this.tableModel.getAllTargets()), true, SchemaElementConstants.TARGET);
                List<ISchemaElement> selectedTargets = targetSelector.getAllSelectedElements();
                if (selectedTargets == null) {
                    return;
                }
                this.tableModel.setTargets((ITarget[]) selectedTargets.toArray(new ITarget[] {}));
            } else if (source.equals(this.editStar)) {
                new TargetStarDialog(null, this.uiHelper, this.model, this.selectedStar);
            } else if (source.equals(this.deleteStar)) {
                if (this.selectedStar != null) {
                    this.tableModel.deleteTarget(this.selectedStar);
                    this.componentStars.getSelectionModel().clearSelection();
                }
            }

            this.componentStars.updateUI();
        }

    }

    @Override
    public ISchemaElement getSchemaElement() {

        return this.target;

    }

    @Override
    public ISchemaElement updateSchemaElement() {

        if (this.target == null) {
            return null;
        }

        // Update container fields
        ITarget t = this.targetContainer.updateTarget();
        if (t == null) {
            return null;
        } else {
            this.target = (DeepSkyTargetMS) t;
        }

        // Update specific fields
        ITarget[] targets = this.tableModel.getAllTargets();
        if ((targets == null) || (targets.length < 3)) {
            this.createWarning(this.bundle.getString("panel.ms.warning.threeComponentsRequired"));
            return null;
        }
        List<String> components = Arrays.asList(targets).stream().map(x -> x.getID()).collect(Collectors.toList());
        this.target.setComponents(components);

        return this.target;

    }

    @Override
    public ISchemaElement createSchemaElement() {

        String name = this.targetContainer.getName();
        String datasource = this.targetContainer.getDatasource();
        IObserver observer = this.targetContainer.getObserver();

        // Make sure only datasource or observer is set
        if (!this.targetContainer.checkOrigin(datasource, observer)) {
            return null;
        }

        ITarget[] targets = this.tableModel.getAllTargets();
        if ((targets == null) || (targets.length < 3)) {
            this.createWarning(this.bundle.getString("panel.ms.warning.threeComponentsRequired"));
            return null;
        }

        List<String> components = Arrays.asList(targets).stream().map(x -> x.getID()).collect(Collectors.toList());
        if (observer != null) {
            this.target = new DeepSkyTargetMS(name, observer, components);
        } else {
            this.target = new DeepSkyTargetMS(name, datasource, components);
        }

        return this.target;

    }

    private void loadSchemaElement() {

        List<ITarget> targets = this.target.getComponentTargets(this.model.getTargets());
        if ((targets != null) && !(targets.isEmpty())) {
            this.tableModel = new TargetTableModel((ITarget[]) targets.toArray(new ITarget[] {}),
                    this.model.getConfiguration());
            this.componentStars.setModel(this.tableModel);
        } else {
            this.createWarning(this.bundle.getString("panel.ms.warning.componentsNotFound"));
        }

    }

    private void createPanel() {

        // Prepare Table
        this.tableModel = new TargetTableModel(new ITarget[] {}, this.model.getConfiguration());
        this.componentStars = new JTable(this.tableModel);
        this.componentStars.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.componentStars.setDoubleBuffered(true);

        /*
         * DefaultTableCellRenderer renderer =
         * (DefaultTableCellRenderer)this.componentStars.getDefaultRenderer(String.class );
         * renderer.setBackground(Color.);
         */
        ListSelectionModel lsm = this.componentStars.getSelectionModel();
        lsm.addListSelectionListener(e -> {
            // Ignore extra messages.
            if (e.getValueIsAdjusting())
                return;

            ListSelectionModel lsm1 = (ListSelectionModel) e.getSource();
            if (lsm1.isSelectionEmpty()) {
                // no rows are selected
                DeepSkyTargetMSPanel.this.selectedStar = null;
                DeepSkyTargetMSPanel.this.activateChangeButtons(false);
            } else {
                int selectedRow = lsm1.getMinSelectionIndex();
                ISchemaElement se = DeepSkyTargetMSPanel.this.tableModel.getSchemaElement(selectedRow);

                // Set selected star
                if (se instanceof TargetStar) {
                    DeepSkyTargetMSPanel.this.selectedStar = (TargetStar) se;
                    DeepSkyTargetMSPanel.this.activateChangeButtons(true);
                }
            }
        });

        // UI
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 4, 1, 15, 1);
        this.targetContainer = new TargetContainer(this.model.getConfiguration(), this.model, this.target,
                this.isEditable(), false);
        gridbag.setConstraints(this.targetContainer, constraints);
        this.add(this.targetContainer);

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 4, 1, 100, 1);
        JSeparator seperator1 = new JSeparator(SwingConstants.HORIZONTAL);
        gridbag.setConstraints(seperator1, constraints);
        this.add(seperator1);

        ConstraintsBuilder.buildConstraints(constraints, 0, 2, 1, 1, 5, 1);
        constraints.fill = GridBagConstraints.NONE;
        JLabel Lcomponents = new JLabel(this.bundle.getString("panel.ms.label.components"));
        Lcomponents.setToolTipText(this.bundle.getString("panel.ms.tooltip.components"));
        gridbag.setConstraints(Lcomponents, constraints);
        this.add(Lcomponents);

        int y = 3;

        if (this.isEditable()) {
            ConstraintsBuilder.buildConstraints(constraints, 0, y++, 4, 1, 5, 1);
            constraints.fill = GridBagConstraints.HORIZONTAL;
            JLabel Lnote = new JLabel(this.bundle.getString("panel.ms.note.doubleStar"), SwingConstants.CENTER);
            Lnote.setFont(new Font("Arial", Font.ITALIC, 10));
            Lnote.setToolTipText(this.bundle.getString("panel.ms.note.doubleStar"));
            gridbag.setConstraints(Lnote, constraints);
            this.add(Lnote);
        }

        ConstraintsBuilder.buildConstraints(constraints, 0, y++, 4, 1, 40, 81);
        constraints.fill = GridBagConstraints.BOTH;
        JScrollPane componentStarScroll = new JScrollPane(this.componentStars);
        // this.componentStars.setFillsViewportHeight(true);
        componentStarScroll.setToolTipText(this.bundle.getString("panel.ms.tooltip.components"));
        gridbag.setConstraints(componentStarScroll, constraints);
        this.add(componentStarScroll);

        // If we're in edit mode, add buttons
        if (this.isEditable()) {

            // Add existing
            ConstraintsBuilder.buildConstraints(constraints, 0, y, 1, 1, 10, 1);
            constraints.fill = GridBagConstraints.HORIZONTAL;
            this.addExistingStar = new JButton(this.bundle.getString("panel.ms.button.label.addExisting"));
            this.addExistingStar.setToolTipText(this.bundle.getString("panel.ms.button.tooltip.addExisting"));
            gridbag.setConstraints(this.addExistingStar, constraints);
            this.addExistingStar.addActionListener(this);
            this.add(this.addExistingStar);

            // Add new
            ConstraintsBuilder.buildConstraints(constraints, 1, y, 1, 1, 10, 1);
            constraints.fill = GridBagConstraints.HORIZONTAL;
            this.addNewStar = new JButton(this.bundle.getString("panel.ms.button.label.addNew"));
            this.addNewStar.setToolTipText(this.bundle.getString("panel.ms.button.tooltip.addNew"));
            gridbag.setConstraints(this.addNewStar, constraints);
            this.addNewStar.addActionListener(this);
            this.add(this.addNewStar);

            // Edit
            ConstraintsBuilder.buildConstraints(constraints, 2, y, 1, 1, 10, 1);
            constraints.fill = GridBagConstraints.HORIZONTAL;
            this.editStar = new JButton(this.bundle.getString("panel.ms.button.label.edit"));
            this.editStar.setToolTipText(this.bundle.getString("panel.ms.button.tooltip.edit"));
            gridbag.setConstraints(this.editStar, constraints);
            this.editStar.addActionListener(this);
            this.add(this.editStar);

            // Delete
            ConstraintsBuilder.buildConstraints(constraints, 3, y, 1, 1, 10, 1);
            constraints.fill = GridBagConstraints.HORIZONTAL;
            this.deleteStar = new JButton(this.bundle.getString("panel.ms.button.label.delete"));
            this.deleteStar.setToolTipText(this.bundle.getString("panel.ms.button.tooltip.delete"));
            gridbag.setConstraints(this.deleteStar, constraints);
            this.deleteStar.addActionListener(this);
            this.add(this.deleteStar);

            // By default disable edit and delete button
            this.activateChangeButtons(false);

        } else {
            /*
             * ConstraintsBuilder.buildConstraints(constraints, 0, y, 8, 1, 100, 400); constraints.fill =
             * GridBagConstraints.BOTH; JLabel Lfill = new JLabel(""); gridbag.setConstraints(Lfill, constraints);
             * this.add(Lfill);
             */

            this.componentStars.setEnabled(false);
        }

    }

    private void activateChangeButtons(boolean enabled) {

        if (this.isEditable()) {
            this.editStar.setEnabled(enabled);
            this.deleteStar.setEnabled(enabled);
        }

    }

}
