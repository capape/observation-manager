/* ====================================================================
 * /box/SiteBox.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.box;

import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ISite;

public class SiteBox extends AbstractBox {

    @Override
    public void addItem(ISchemaElement element) {

        if (element == null) {
            return;
        }

        ISite site = (ISite) element;
        String key = this.getKey(site);

        super.addItem(key, site);

    }

    @Override
    protected String getKey(ISchemaElement element) {

        return element.getDisplayName();

    }

}
