package de.lehmannet.om.ui.extension.variableStars;

import java.awt.Color;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.table.AbstractTableModel;

import de.lehmannet.om.IObserver;

public class ObserverColorTableModel extends AbstractTableModel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private final ResourceBundle bundle = ResourceBundle
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
