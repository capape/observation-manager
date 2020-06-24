package de.lehmannet.om.ui.extension.variableStars;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.ResourceBundle;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import de.lehmannet.om.IObservation;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.extension.variableStars.TargetVariableStar;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.comparator.ObservationComparator;
import de.lehmannet.om.ui.navigation.tableModel.ExtendedSchemaTableModel;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.DatePicker;
import de.lehmannet.om.ui.util.UserInterfaceHelper;
import de.lehmannet.om.util.SchemaElementConstants;

public class VariableStarSelectorPopup extends JDialog implements ActionListener, TableModelListener {

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

    private final ResourceBundle uiBundle = ResourceBundle
            .getBundle("de.lehmannet.om.ui.extension.variableStars.VariableStar", Locale.getDefault());

    private ExtendedSchemaTableModel tableModel = null;
    private final ObservationManagerModel model;
    private final JFrame parent;
    private final UserInterfaceHelper uiHelper;

    /**
     * @see SchemaElementConstants
     */
    public VariableStarSelectorPopup(JFrame om, UserInterfaceHelper uiHelper, ObservationManagerModel model)
            throws IllegalArgumentException, NoSuchElementException {

        super(om, true);

        this.parent = om;
        this.uiHelper = uiHelper;

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
            this.uiHelper.showInfo(this.uiBundle.getString("popup.selectVariableStar.info.noVariableStarObservations"));
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
                    dp = new DatePicker(this.parent,
                            this.uiBundle.getString("popup.selectVariableStar.start.datePicker.title"), this.beginDate);
                } else {
                    dp = new DatePicker(this.parent,
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
                        this.uiHelper.showWarning(
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
                    dp = new DatePicker(this.parent,
                            this.uiBundle.getString("popup.selectVariableStar.end.datePicker.title"), this.endDate);
                } else if (this.beginDate != null) { // Try to initialize endDate Picker with startdate
                    dp = new DatePicker(this.parent,
                            this.uiBundle.getString("popup.selectVariableStar.end.datePicker.title"), this.beginDate);
                } else {
                    dp = new DatePicker(this.parent,
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
                        this.uiHelper.showWarning(
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