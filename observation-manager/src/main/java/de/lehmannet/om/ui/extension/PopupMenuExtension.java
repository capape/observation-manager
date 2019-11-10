/* ====================================================================
 * /extension/PopupMenuExtension.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension;

import javax.swing.JMenu;

public class PopupMenuExtension {

    private int[] schemaElement = null;
    private JMenu menu = null;

    public PopupMenuExtension(int[] schemaElementTypes, JMenu menu) {

        this.schemaElement = schemaElementTypes;
        this.menu = menu;

    }

    public int[] getSchemaElementTypes() {

        return this.schemaElement;

    }

    public JMenu getMenu() {

        return this.menu;

    }

}
