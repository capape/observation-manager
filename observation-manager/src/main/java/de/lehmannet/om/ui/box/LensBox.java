/* ====================================================================
 * /box/LensBox.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.box;

import de.lehmannet.om.ILens;
import de.lehmannet.om.ISchemaElement;

public class LensBox extends AbstractBox {

    @Override
    public void addItem(ISchemaElement element) {

        if (element == null) {
            return;
        }

        ILens lens = (ILens) element;
        String key = this.getKey(lens);

        super.addItem(key, lens);

    }

    @Override
    public String getKey(ISchemaElement element) {

        return element.getDisplayName();

    }

}
