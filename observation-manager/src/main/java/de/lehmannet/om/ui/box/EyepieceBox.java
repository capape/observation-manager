/* ====================================================================
 * /box/EyepieceBox.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.box;

import de.lehmannet.om.IEyepiece;
import de.lehmannet.om.ISchemaElement;

public class EyepieceBox extends AbstractBox {

    @Override
    public void addItem(ISchemaElement element) {

        if (element == null) {
            return;
        }

        IEyepiece eyepiece = (IEyepiece) element;
        String key = this.getKey(eyepiece);

        super.addItem(key, eyepiece);

    }

    @Override
    protected String getKey(ISchemaElement element) {

        return element.getDisplayName();

    }

}
