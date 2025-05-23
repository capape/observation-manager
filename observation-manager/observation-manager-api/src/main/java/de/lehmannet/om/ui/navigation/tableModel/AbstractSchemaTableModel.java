/*
 * ====================================================================
 * /navigation/tableModel/AbstractSchemaTableModel.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.navigation.tableModel;

import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ui.util.LocaleToolsFactory;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.table.AbstractTableModel;

public abstract class AbstractSchemaTableModel extends AbstractTableModel {

    /**
     *
     */
    private static final long serialVersionUID = -3423592371149917442L;

    static ResourceBundle bundle =
            LocaleToolsFactory.appInstance().getBundle("ObservationManager", Locale.getDefault());

    protected ISchemaElement[] elements = null;

    public ISchemaElement getSchemaElement(int row) {

        if (row == -1) {
            return null;
        }

        if ((this.elements == null) || (this.elements.length == 0)) {
            return null;
        }

        return this.elements[row];
    }

    public int getRow(ISchemaElement element) {

        for (int x = 0; x < this.elements.length; x++) {
            if (this.elements[x].equals(element)) {
                return x;
            }
        }

        return 0;
    }

    public int getColumnSize(int columnIndex) {

        return -1;
    }

    public static void reloadLanguage() {

        bundle = LocaleToolsFactory.appInstance().getBundle("ObservationManager", Locale.getDefault());
    }

    public abstract String getID();
}
