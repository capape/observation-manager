/* ====================================================================
 * /util/IListableCatalog.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.catalog;

import de.lehmannet.om.ITarget;
import de.lehmannet.om.ui.navigation.tableModel.AbstractSchemaTableModel;

public interface IListableCatalog extends ICatalog {

    public ITarget[] getTargets();

    public String[] getCatalogIndex();

    public AbstractSchemaTableModel getTableModel();

}
