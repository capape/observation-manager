/* ====================================================================
 * /util/ICatalog.java
 *
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.catalog;

import de.lehmannet.om.ITarget;
import de.lehmannet.om.ui.panel.AbstractSearchPanel;

public interface ICatalog {

    ITarget getTarget(String objectName);

    String getName();

    AbstractSearchPanel getSearchPanel();

    String getAbbreviation();

}
