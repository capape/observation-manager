/*
 * ====================================================================
 * /navigation/tableModel/TableSorter.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.navigation.tableModel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import de.lehmannet.om.EquPosition;
import de.lehmannet.om.Eyepiece;
import de.lehmannet.om.Imager;
import de.lehmannet.om.Lens;
import de.lehmannet.om.Observation;
import de.lehmannet.om.Observer;
import de.lehmannet.om.Scope;
import de.lehmannet.om.Session;
import de.lehmannet.om.Site;
import de.lehmannet.om.Target;
import de.lehmannet.om.ui.comparator.AngleComparator;
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

public class TableSorter extends AbstractSchemaTableModel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final String MODEL_ID = "Sorter";

    private transient AbstractSchemaTableModel tableModel;

    private static final int DESCENDING = -1;
    private static final int NOT_SORTED = 0;
    public static final int ASCENDING = 1;

    private static final Directive EMPTY_DIRECTIVE = new Directive(-1, NOT_SORTED);

    private static final Comparator<?> COMPARABLE_COMPARATOR = (o1, o2) -> ((Comparable) o1).compareTo(o2);
    private static final Comparator<?> LEXICAL_COMPARATOR = (o1, o2) -> {

        String o1S = o1.toString();
        String o2S = o2.toString();

        // If strings starts with "-" they might be negative DEC values, which
        // we'll sort reversed
        // Goal is we want targets with negative DEC to be sorted from -0.1deg to 90deg
        // ascending.
        if (o2S.startsWith("-") && o1S.startsWith("-")) {
            // Try if this string is a integer EquPosition Dec value
            try {
                Integer.parseInt("" + o1S.charAt(1));
                Integer.parseInt("" + o2S.charAt(1));

                // If we come to this point the strings starts with a negative number
                // Check if the number is a EquPosition DEC value
                new EquPosition("00h00m00s", o1S);
                new EquPosition("00h00m00s", o2S);

                // OK use reverse order
                return o2S.compareTo(o1S);

            } catch (NumberFormatException nfe) {
                // Ok, doesn't start with a number. Just compare Strings
            } catch (IllegalArgumentException iae) {
                // Ok, value is not a valid EquPosition DEC value
            }
        }

        return o1S.compareTo(o2S);
    };

    private static final Comparator<Float> FLOAT_COMPARATOR = (o1, o2) -> {
        Float f1 = (Float) o1;
        Float f2 = (Float) o2;
        if (f1 < f2) {
            return -1;
        } else if (f1 > f2) {
            return 1;
        }

        return 0;
    };
    private static final Comparator<Integer> INT_COMPARATOR = (o1, o2) -> {
        Integer i1 = (Integer) o1;
        Integer i2 = (Integer) o2;
        if (i1 < i2) {
            return -1;
        } else if (i1 > i2) {
            return 1;
        }

        return 0;
    };

    private final Map<Class<?>, Comparator<?>> columnComparators = new HashMap<>();
    private JTableHeader tableHeader;
    private transient Row[] viewToModel;
    private transient final List<Directive> sortingColumns = new ArrayList<>();
    private transient final MouseListener mouseListener;
    private transient final TableModelListener tableModelListener;
    private transient int[] modelToView;

    private TableSorter() {

        this.mouseListener = new MouseHandler();
        this.tableModelListener = new TableModelHandler();
        this.initColumnComparator();

    }

    public TableSorter(AbstractSchemaTableModel tableModel) {

        this();
        setTableModel(tableModel, true);

    }

    @Override
    public String getID() {

        return TableSorter.MODEL_ID;

    }

    private void clearSortingState() {

        viewToModel = null;
        modelToView = null;

    }

    public AbstractSchemaTableModel getTableModel() {

        return tableModel;

    }

    public void setTableModel(AbstractSchemaTableModel tableModel, boolean clear) {

        if (this.tableModel != null) {
            this.tableModel.removeTableModelListener(tableModelListener);
        }

        this.tableModel = tableModel;
        if (this.tableModel != null) {
            this.tableModel.addTableModelListener(tableModelListener);
        }

        if (this.tableModel != null) {
            this.elements = this.tableModel.elements;
        }

        if (clear) {
            clearSortingState();
            fireTableStructureChanged();
        }

    }

    public JTableHeader getTableHeader() {

        return tableHeader;

    }

    public void setTableHeader(JTableHeader tableHeader) {

        if (this.tableHeader != null) {
            this.tableHeader.removeMouseListener(mouseListener);
            TableCellRenderer defaultRenderer = this.tableHeader.getDefaultRenderer();
            if (defaultRenderer instanceof SortableHeaderRenderer) {
                this.tableHeader.setDefaultRenderer(((SortableHeaderRenderer) defaultRenderer).tableCellRenderer);
            }
        }

        this.tableHeader = tableHeader;
        if (this.tableHeader != null) {
            this.tableHeader.addMouseListener(mouseListener);
            this.tableHeader.setDefaultRenderer(new SortableHeaderRenderer(this.tableHeader.getDefaultRenderer()));
        }

    }

    public boolean isSorting() {

        return !sortingColumns.isEmpty();

    }

    private Directive getDirective(int column) {

        for (Directive directive : sortingColumns) {
            if (directive.column == column) {
                return directive;
            }
        }

        return EMPTY_DIRECTIVE;

    }

    private int getSortingStatus(int column) {

        return getDirective(column).direction;

    }

    private void sortingStatusChanged() {

        clearSortingState();
        fireTableDataChanged();
        if (tableHeader != null) {
            tableHeader.repaint();
        }

    }

    private void setSortingStatus(int column, int status) {

        Directive directive = getDirective(column);
        if (directive != EMPTY_DIRECTIVE) {
            sortingColumns.remove(directive);
        }
        if (status != NOT_SORTED) {
            sortingColumns.add(new Directive(column, status));
        }
        sortingStatusChanged();

    }

    private Icon getHeaderRendererIcon(int column, int size) {

        Directive directive = getDirective(column);
        if (directive == EMPTY_DIRECTIVE) {
            return null;
        }

        return new Arrow(directive.direction == DESCENDING, size, sortingColumns.indexOf(directive));

    }

    private void cancelSorting() {

        sortingColumns.clear();
        sortingStatusChanged();

    }

    public void setColumnComparator(Class<?> type, Comparator<?> comparator) {

        if (comparator == null) {
            columnComparators.remove(type);
        } else {
            columnComparators.put(type, comparator);
        }

    }

    private Comparator getComparator(int column) {

        Class<?> columnType = tableModel.getColumnClass(column);
        Comparator comparator = columnComparators.get(columnType);
        if (comparator != null) {
            return comparator;
        }
        if (Comparable.class.isAssignableFrom(columnType)) {
            return COMPARABLE_COMPARATOR;
        }

        return LEXICAL_COMPARATOR;
    }

    private void initColumnComparator() {

        this.columnComparators.put(ZonedDateTime.class,
                Comparator.comparing(zdt -> ((ZonedDateTime) zdt).truncatedTo(ChronoUnit.SECONDS)));
        this.columnComparators.put(Target.class, new TargetComparator());
        this.columnComparators.put(Site.class, new SiteComparator());
        this.columnComparators.put(Scope.class, new ScopeComparator());
        this.columnComparators.put(Observer.class, new ObserverComparator());
        this.columnComparators.put(Observation.class, new ObservationComparator());
        this.columnComparators.put(Eyepiece.class, new EyepieceComparator());
        this.columnComparators.put(Lens.class, new LensComparator());
        this.columnComparators.put(Imager.class, new ImagerComparator());
        this.columnComparators.put(Session.class, new SessionComparator());
        this.columnComparators.put(de.lehmannet.om.Filter.class, new FilterComparator());
        this.columnComparators.put(Integer.class, TableSorter.INT_COMPARATOR);
        this.columnComparators.put(Float.class, TableSorter.FLOAT_COMPARATOR);
        this.columnComparators.put(de.lehmannet.om.Angle.class, new AngleComparator());

    }

    private Row[] getViewToModel() {

        if (viewToModel == null) {
            if (tableModel == null) { // Might run into NPE here
                return new Row[] {};
            }
            int tableModelRowCount = tableModel.getRowCount();
            viewToModel = new Row[tableModelRowCount];
            for (int row = 0; row < tableModelRowCount; row++) {
                viewToModel[row] = new Row(row);
            }

            if (isSorting()) {
                Arrays.sort(viewToModel);
            }
        }

        return viewToModel;

    }

    public int modelIndex(int viewIndex) {

        if (viewIndex == -1 || getViewToModel() == null || getViewToModel().length == 0) {
            return -1;
        }

        if (getViewToModel().length > viewIndex && getViewToModel()[viewIndex] != null) {
            return getViewToModel()[viewIndex].modelIndex;
        } else {
            return -1;
        }

    }

    public int viewIndex(int modelIndex) {

        return getModelToView()[modelIndex];

    }

    private int[] getModelToView() {

        if (modelToView == null) {
            int n = getViewToModel().length;
            modelToView = new int[n];
            for (int i = 0; i < n; i++) {
                modelToView[modelIndex(i)] = i;
            }
        }

        return modelToView;

    }

    // ----------
    // TableModel -------------------------------------------------------------
    // ----------

    @Override
    public int getRowCount() {

        return (tableModel == null) ? 0 : tableModel.getRowCount();

    }

    @Override
    public int getColumnCount() {

        return (tableModel == null) ? 0 : tableModel.getColumnCount();

    }

    @Override
    public String getColumnName(int column) {

        return tableModel.getColumnName(column);

    }

    @Override
    public Class<?> getColumnClass(int column) {

        return tableModel.getColumnClass(column);

    }

    @Override
    public boolean isCellEditable(int row, int column) {

        return tableModel.isCellEditable(modelIndex(row), column);

    }

    @Override
    public Object getValueAt(int row, int column) {

        return tableModel.getValueAt(modelIndex(row), column);

    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {

        tableModel.setValueAt(aValue, modelIndex(row), column);

    }

    // --------------
    // Helper classes ---------------------------------------------------------
    // --------------

    private class Row implements Comparable<Row> {

        private int modelIndex = 0;

        Row(int index) {

            this.modelIndex = index;

        }

        @Override
        public int compareTo(Row o) {

            int row1 = modelIndex;
            int row2 = o.modelIndex;

            for (Directive directive : sortingColumns) {
                int column = directive.column;
                Object o1 = tableModel.getValueAt(row1, column);
                Object o2 = tableModel.getValueAt(row2, column);

                int comparison;
                // Define null less than everything, except null.
                if (o1 == null && o2 == null) {
                    comparison = 0;
                } else if (o1 == null) {
                    comparison = -1;
                } else if (o2 == null) {
                    comparison = 1;
                } else {
                    Class<?> classColumn = getColumnClass(column);
                    comparison = getComparator(column).compare(o1, o2);
                }
                if (comparison != 0) {
                    return directive.direction == DESCENDING ? -comparison : comparison;
                }
            }

            return 0;

        }

    }

    private class TableModelHandler implements TableModelListener {

        @Override
        public void tableChanged(TableModelEvent e) {

            // If we're not sorting by anything, just pass the event along.
            if (!isSorting()) {
                clearSortingState();
                fireTableChanged(e);
                return;
            }

            // If the table structure has changed, cancel the sorting; the
            // sorting columns may have been either moved or deleted from
            // the model.
            if (e.getFirstRow() == TableModelEvent.HEADER_ROW) {
                cancelSorting();
                fireTableChanged(e);
                return;
            }

            // We can map a cell event through to the view without widening
            // when the following conditions apply:
            //
            // a) all the changes are on one row (e.getFirstRow() ==
            // e.getLastRow()) and,
            // b) all the changes are in one column (column !=
            // TableModelEvent.ALL_COLUMNS) and,
            // c) we are not sorting on that column (getSortingStatus(column) ==
            // NOT_SORTED) and,
            // d) a reverse lookup will not trigger a sort (modelToView != null)
            //
            // Note: INSERT and DELETE events fail this test as they have column
            // == ALL_COLUMNS.
            //
            // The last check, for (modelToView != null) is to see if
            // modelToView
            // is already allocated. If we don't do this check; sorting can
            // become
            // a performance bottleneck for applications where cells
            // change rapidly in different parts of the table. If cells
            // change alternately in the sorting column and then outside of
            // it this class can end up re-sorting on alternate cell updates -
            // which can be a performance problem for large tables. The last
            // clause avoids this problem.
            int column = e.getColumn();
            if (e.getFirstRow() == e.getLastRow() && column != TableModelEvent.ALL_COLUMNS
                    && getSortingStatus(column) == NOT_SORTED && modelToView != null) {
                int viewIndex = getModelToView()[e.getFirstRow()];
                fireTableChanged(new TableModelEvent(TableSorter.this, viewIndex, viewIndex, column, e.getType()));
                return;

            }

            // Something has happened to the data that may have invalidated the
            // row order.
            clearSortingState();
            fireTableDataChanged();

        }

    }

    private class MouseHandler extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {

            JTableHeader h = (JTableHeader) e.getSource();
            TableColumnModel columnModel = h.getColumnModel();
            int viewColumn = columnModel.getColumnIndexAtX(e.getX());
            int column = columnModel.getColumn(viewColumn).getModelIndex();
            if (column != -1) {
                int status = getSortingStatus(column);
                if (!e.isControlDown()) {
                    cancelSorting();
                }
                // Cycle the sorting states through {NOT_SORTED, ASCENDING,
                // DESCENDING} or
                // {NOT_SORTED, DESCENDING, ASCENDING} depending on whether
                // shift is pressed.
                status = status + (e.isShiftDown() ? -1 : 1);
                status = (status + 4) % 3 - 1; // signed mod, returning {-1, 0,
                                               // 1}
                setSortingStatus(column, status);
            }

        }

    }

    private static class Arrow implements Icon {

        private final boolean descending;

        private final int size;

        private final int priority;

        Arrow(boolean descending, int size, int priority) {

            this.descending = descending;
            this.size = size;
            this.priority = priority;

        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {

            Color color = c == null ? Color.GRAY : c.getBackground();
            // In a compound sort, make each succesive triangle 20%
            // smaller than the previous one.
            int dx = (int) (size / 2 * Math.pow(0.8, priority));
            int dy = descending ? dx : -dx;
            // Align icon (roughly) with font baseline.
            y = y + 5 * size / 6 + (descending ? -dy : 0);
            int shift = descending ? 1 : -1;
            g.translate(x, y);

            // Right diagonal.
            g.setColor(color.darker());
            g.drawLine(dx / 2, dy, 0, 0);
            g.drawLine(dx / 2, dy + shift, 0, shift);

            // Left diagonal.
            g.setColor(color.brighter());
            g.drawLine(dx / 2, dy, dx, 0);
            g.drawLine(dx / 2, dy + shift, dx, shift);

            // Horizontal line.
            if (descending) {
                g.setColor(color.darker().darker());
            } else {
                g.setColor(color.brighter().brighter());
            }
            g.drawLine(dx, 0, 0, 0);

            g.setColor(color);
            g.translate(-x, -y);

        }

        @Override
        public int getIconWidth() {

            return size;

        }

        @Override
        public int getIconHeight() {

            return size;

        }

    }

    private class SortableHeaderRenderer implements TableCellRenderer {

        private final TableCellRenderer tableCellRenderer;

        SortableHeaderRenderer(TableCellRenderer tableCellRenderer) {

            this.tableCellRenderer = tableCellRenderer;

        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {

            Component c = tableCellRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                    column);

            if (c instanceof JLabel) {
                JLabel l = (JLabel) c;
                l.setHorizontalTextPosition(SwingConstants.LEFT);

                int modelColumn = 0;
                try {
                    // Keep an eye on this: The next line seems to cause ArrayIndexOutofBounds
                    // sometimes 0>=0...don't know when it does so...
                    modelColumn = table.convertColumnIndexToModel(column);
                } catch (ArrayIndexOutOfBoundsException aiobe) {
                    // Do nothing
                }
                l.setIcon(getHeaderRendererIcon(modelColumn, l.getFont().getSize()));
            }

            return c;

        }

    }

    private static class Directive {

        private final int column;
        private final int direction;

        Directive(int column, int direction) {

            this.column = column;
            this.direction = direction;

        }

    }

}