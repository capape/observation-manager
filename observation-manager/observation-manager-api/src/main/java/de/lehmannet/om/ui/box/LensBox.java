/*
 * ====================================================================
 * /box/LensBox.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.box;

import de.lehmannet.om.ILens;
import de.lehmannet.om.ISchemaElement;

public class LensBox extends AbstractBox {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

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
    protected String getKey(ISchemaElement element) {

        return element.getDisplayName();

    }

}
