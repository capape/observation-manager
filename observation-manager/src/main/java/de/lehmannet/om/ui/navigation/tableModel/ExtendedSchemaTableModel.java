package de.lehmannet.om.ui.navigation.tableModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.TreeMap;

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

    private PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle.getBundle("ObservationManager",
            Locale.getDefault());

    private TreeMap elementMap = null;
    private boolean multipleSelection = false;
    private int currentSelectedRow = 0; // Row number of currently selected row in case of singleSelection

    public ExtendedSchemaTableModel(ISchemaElement[] elements, int schemaElementType, String xsiFilter,
            boolean multipleSelection, List preSelectedTargets) {

        this.multipleSelection = multipleSelection;

        // Load comparator
        Comparator comparator = null;
        switch (schemaElementType) {
        case SchemaElementConstants.IMAGER: {
            comparator = new ImagerComparator();
            break;
        }
        case SchemaElementConstants.EYEPIECE: {
            comparator = new EyepieceComparator();
            break;
        }
        case SchemaElementConstants.FILTER: {
            comparator = new FilterComparator();
            break;
        }
        case SchemaElementConstants.LENS: {
            comparator = new LensComparator();
            break;
        }
        case SchemaElementConstants.OBSERVATION: {
            comparator = new ObservationComparator();
            break;
        }
        case SchemaElementConstants.OBSERVER: {
            comparator = new ObserverComparator();
            break;
        }
        case SchemaElementConstants.SCOPE: {
            comparator = new ScopeComparator();
            break;
        }
        case SchemaElementConstants.SESSION: {
            comparator = new SessionComparator();
            break;
        }
        case SchemaElementConstants.SITE: {
            comparator = new SiteComparator();
            break;
        }
        case SchemaElementConstants.TARGET: {
            comparator = new TargetComparator();
            break;
        }
        }

        // Initialize TreeMap
        this.elementMap = new TreeMap(comparator);

        // Add elements to treeMap
        for (int i = 0; i < elements.length; i++) {
            // In case of targets, filter xsiTypes
            if ((SchemaElementConstants.TARGET == schemaElementType) && (xsiFilter != null)) {
                if (!((IExtendableSchemaElement) elements[i]).getXSIType().equals(xsiFilter)) { // Check if xsiType
                                                                                                // matches
                    continue;
                }
            }
            // In case of observations, filter xsiTypes of target
            if ((SchemaElementConstants.OBSERVATION == schemaElementType) && (xsiFilter != null)) {
                if (!((IObservation) elements[i]).getTarget().getXSIType().equals(xsiFilter)) { // Check if xsiType
                                                                                                // matches
                    continue;
                }
            }

            if (!this.multipleSelection) {
                this.elementMap.put(elements[i], new Boolean(false));
            } else {
                if (preSelectedTargets != null) {
                    if (preSelectedTargets.contains(elements[i])) {
                        this.elementMap.put(elements[i], new Boolean(true));
                        continue;
                    }
                }
                this.elementMap.put(elements[i], new Boolean(false));
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

        if (columnIndex == 0) {
            return true;
        }

        return false;

    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        if (this.elementMap.isEmpty()) {
            return "";
        }

        ISchemaElement keySchemaElement = (ISchemaElement) this.elementMap.keySet()
                .toArray(new ISchemaElement[] {})[rowIndex];

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
                    this.setSelection(row, ((Boolean) o).booleanValue());
                } else { // Deselect all other entries
                    this.setSingleSelection(row, (((Boolean) o).booleanValue()));
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

    public void setSelection(int row, boolean selection) {

        ISchemaElement keySchemaElement = (ISchemaElement) this.elementMap.keySet()
                .toArray(new ISchemaElement[] {})[row];
        this.elementMap.remove(keySchemaElement);
        this.elementMap.put(keySchemaElement, new Boolean(selection));

    }

    public void setSingleSelection(int row, boolean selection) {

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
            if (currentValue.booleanValue()) {
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
