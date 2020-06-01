/* ====================================================================
 * /box/TargetBox.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.box;

import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ITarget;

public class TargetBox extends AbstractBox {

    /**
     *
     */
    private static final long serialVersionUID = 7187306084567226194L;

    @Override
    public void addItem(ISchemaElement element) {

        if (element == null) {
            return;
        }

        ITarget target = (ITarget) element;
        String key = this.getKey(target);

        super.addItem(key, target);

    }

    @Override
    protected String getKey(ISchemaElement element) {

        return element.getDisplayName();

    }

}
