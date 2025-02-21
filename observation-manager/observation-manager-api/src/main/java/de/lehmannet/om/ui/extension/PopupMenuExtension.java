/*
 * ====================================================================
 * /extension/PopupMenuExtension.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension;

import de.lehmannet.om.util.SchemaElementConstants;
import javax.swing.JMenu;

public class PopupMenuExtension {

    private SchemaElementConstants[] schemaElement = null;
    private JMenu menu = null;

    public PopupMenuExtension(SchemaElementConstants[] schemaElementTypes, JMenu menu) {

        this.schemaElement = schemaElementTypes.clone();
        this.menu = menu;
    }

    public SchemaElementConstants[] getSchemaElementTypes() {

        return this.schemaElement.clone();
    }

    public JMenu getMenu() {

        return this.menu;
    }
}
