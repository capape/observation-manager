/* ====================================================================
 * /box/ObserverBox.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.box;

import de.lehmannet.om.IObserver;
import de.lehmannet.om.ISchemaElement;

public class ObserverBox extends AbstractBox {

    @Override
    public void addItem(ISchemaElement element) {

        if (element == null) {
            return;
        }

        IObserver observer = (IObserver) element;
        String key = this.getKey(observer);

        super.addItem(key, observer);

    }

    @Override
    protected String getKey(ISchemaElement element) {

        return element.getDisplayName();

    }

}
