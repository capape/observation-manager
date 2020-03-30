/* ====================================================================
 * /box/AbstractBox.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.box;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JComboBox;

import de.lehmannet.om.ISchemaElement;

public abstract class AbstractBox extends JComboBox {

    /**
     *
     */
    private static final long serialVersionUID = 800092920666294074L;

    private static final String EMPTY_ENTRY = "----";

    private final Map map = new HashMap();

    // ---------
    // JComboBox --------------------------------------------------------------
    // ---------

    public abstract void addItem(ISchemaElement element);

    protected abstract String getKey(ISchemaElement element);

    // --------------
    // Public Methods ---------------------------------------------------------
    // --------------

    public ISchemaElement getSelectedSchemaElement() {

        Object si = super.getSelectedItem();
        if (AbstractBox.EMPTY_ENTRY.equals(si)) {
            return null;
        } else {
            return (ISchemaElement) map.get(si);
        }

    }

    public void addEmptyItem() {

        super.addItem(AbstractBox.EMPTY_ENTRY);
        super.setSelectedItem(AbstractBox.EMPTY_ENTRY);

    }

    public void selectEmptyItem() {

        super.setSelectedItem(AbstractBox.EMPTY_ENTRY);

    }

    public void setSelectedItem(ISchemaElement element) {

        if (element == null) {
            this.addEmptyItem();
        } else {
            super.setSelectedItem(this.getKey(element));
        }

    }

    // -----------------
    // Protected Methods ------------------------------------------------------
    // -----------------

    void addItem(String key, ISchemaElement element) {

        // Item already exists
        if (this.map.containsKey(key)) {
            super.setSelectedItem(key);
            return;
        }

        this.map.put(key, element);
        super.addItem(key);
        super.setSelectedItem(key);

    }

}
