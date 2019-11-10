/* ====================================================================
 * /box/ImagerBox.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.box;

import de.lehmannet.om.IImager;
import de.lehmannet.om.ISchemaElement;

public class ImagerBox extends AbstractBox {

    @Override
    public void addItem(ISchemaElement element) {

        if (element == null) {
            return;
        }

        IImager imager = (IImager) element;
        String key = this.getKey(imager);

        super.addItem(key, imager);

    }

    @Override
    public String getKey(ISchemaElement element) {

        return element.getDisplayName();

    }

}
