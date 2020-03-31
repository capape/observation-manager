package de.lehmannet.om.ui.navigation.tableModel;

import java.util.*;

import javax.swing.table.AbstractTableModel;

import de.lehmannet.om.IExtendableSchemaElement;
import de.lehmannet.om.IObservation;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ui.comparator.EyepieceComparator;
import de.lehmannet.om.ui.comparator.FilterComparator;
import de.lehmannet.om.ui.comparator.ImagerComparator;
import de.lehmannet.om.ui.comparator.LensComparator;
import de.lehmannet.om.ui.comparator.ObservationComparator;
import de.lehmannet.om.ui.comparator.ObserverComparator;
import de.lehmannet.om.ui.comparator.ScopeComparator;
import de.lehmannet.om.ui.comparator.SessionComparator;
import de.lehmannet.om.ui.comparator.SiteComparator;
import de.lehmannet.om.ui.comparator.TargetComparator;
import de.lehmannet.om.util.SchemaElementConstants;

public class ExtendedSchemaTableModel extends AbstractTableModel {

    private final PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
            .getBundle("ObservationManager", Locale.getDefault());

    private Map elementMap = null;
    private boolean multipleSelection = false;
    private int currentSelectedRow = 0; // Row number of currently selected row in case of singleSelection

    public ExtendedSchemaTableModel(ISchemaElement[] elements, SchemaElementConstants schemaElementType, String xsiFilter,
            boolean multipleSelection, List preSelectedTargets) {

        this.multipleSelection = multipleSelection;

        // Load comparator
        Comparator comparator = null;
        switch (schemaElementType) {
        case IMAGER: {
            comparator = new ImagerComparator();
            break;
        }
        case EYEPIECE: {
            comparator = new EyepieceComparator();
            break;
        }
        case FILTER: {
            comparator = new FilterComparator();
            break;
        }
        case LENS: {
            comparator = new LensComparator();
            break;
        }
        case OBSERVATION: {
            comparator = new ObservationComparator();
            break;
        }
        case OBSERVER: {
            comparator = new ObserverComparator();
            break;
        }
        case SCOPE: {
            comparator = new ScopeComparator();
            break;
        }
        case SESSION: {
            comparator = new SessionComparator();
            break;
        }
        case SITE: {
            comparator = new SiteComparator();
            break;
        }
        case TARGET: {
            comparator = new TargetComparator();
            break;
        }
        default:
            break;
        }

        // Initialize TreeMap
        this.elementMap = new TreeMap(comparator);

        // Add elements to treeMap
        for (ISchemaElement element : elements) {
            // In case of targets, filter xsiTypes
            if ((SchemaElementConstants.TARGET == schemaElementType) && (xsiFilter != null)) {
                if (!((IExtendableSchemaElement) element).getXSIType().equals(xsiFilter)) { // Check if xsiType
                    // matches
                    continue;
                }
            }
            // In case of observations, filter xsiTypes of target
            if ((SchemaElementConstants.OBSERVATION == schemaElementType) && (xsiFilter != null)) {
                if (!((IObservation) element).getTarget().getXSIType().equals(xsiFilter)) { // Check if xsiType
                    // matches
                    continue;
                }
            }

            if (!this.multipleSelection) {
                this.elementMap.put(element, Boolean.FALSE);
            } else {
                if (preSelectedTargets != null) {
                    if (preSelectedTargets.contains(element)) {
                        this.elementMap.put(element, Boolean.TRUE);
                        continue;
                    }
                }
                this.elementMap.put(element, Boolean.FALSE);
            }
        }

    }

    @Override
    public int getColumnCount() {

        return 2;

    }

    @Override
    public int getRowCount() {

        if (elementMap == null) {
            return 5;
        }

        return elementMap.size();

    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {

        return columnIndex == 0;

    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        if (this.elementMap.isEmpty()) {
            return "";
        }

        ISchemaElement keySchemaElement = (ISchemaElement) this.elementMap.keySet().toArray(new Object[0])[rowIndex];

        if (keySchemaElement == null) {
            return "";
        }

        switch (columnIndex) {
        case 0: {
            return this.elementMap.get(keySchemaElement); // Returns a Boolean
        }
        case 1: {
            return keySchemaElement.getDisplayName(); // Returns a String
        }
        }

        return "";

    }

    @Override
    public void setValueAt(Object o, int row, int column) {

        if (column == 0) {
            if (o instanceof Boolean) {
                if (this.multipleSelection) {
                    this.setSelection(row, (Boolean) o);
                } else { // Deselect all other entries
                    this.setSingleSelection(row, ((Boolean) o));
                }
                super.fireTableDataChanged();
            }
        }

    }

    @Override
    public Class getColumnClass(int c) {

        switch (c) {
        case 0: {
            return Boolean.class;
        }
        case 1: {
            return String.class;
        }
        }

        return String.class;

    }

    private void setSelection(int row, boolean selection) {

        ISchemaElement keySchemaElement = (ISchemaElement) this.elementMap.keySet().toArray(new Object[0])[row];
        this.elementMap.remove(keySchemaElement);
        this.elementMap.put(keySchemaElement, selection);

    }

    private void setSingleSelection(int row, boolean selection) {

        // Disable current row if multiple selection is not allowed
        if (!this.multipleSelection) {
            this.setSelection(this.currentSelectedRow, false);
        }

        // Set new row to selection (new value)
        this.setSelection(row, selection);

        // Keep current row for next call
        this.currentSelectedRow = row;

    }

    public List getAllSelectedElements() {

        ArrayList result = new ArrayList();

        Iterator keyIterator = this.elementMap.keySet().iterator();
        ISchemaElement current = null;
        Boolean currentValue = null;
        while (keyIterator.hasNext()) {
            current = (ISchemaElement) keyIterator.next();
            currentValue = (Boolean) this.elementMap.get(current);
            if (currentValue) {
                result.add(current);
            }
        }

        return result;

    }

    @Override
    public String getColumnName(int column) {

        String name = "";

        switch (column) {
        case 0: {
            name = this.bundle.getString("popup.schemaSelector.selection");
            break;
        }
        case 1: {
            name = this.bundle.getString("popup.schemaSelector.schemaElement");
            break;
        }
        }

        return name;

    }

    public int getSelectedRow() {

        if (this.multipleSelection) {
            return -1;
        }

        return this.currentSelectedRow;

    }

}
