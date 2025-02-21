/*
 * ====================================================================
 * /panel/AbstractPanel.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.panel;

import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ui.util.LocaleToolsFactory;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public abstract class AbstractPanel extends JPanel {

    static ResourceBundle bundle =
            LocaleToolsFactory.appInstance().getBundle("ObservationManager", Locale.getDefault());

    private boolean editable = false;

    protected AbstractPanel(boolean editable) {

        this.editable = editable;
    }

    protected AbstractPanel(Boolean editable) {

        this.editable = editable;
    }

    public abstract ISchemaElement createSchemaElement();

    public abstract ISchemaElement getSchemaElement();

    public abstract ISchemaElement updateSchemaElement();

    public boolean isEditable() {

        return this.editable;
    }

    public static void reloadLanguage() {

        bundle = LocaleToolsFactory.appInstance().getBundle("ObservationManager", Locale.getDefault());
    }

    protected void createWarning(String message) {

        JOptionPane.showMessageDialog(
                this, message, AbstractPanel.bundle.getString("title.warning"), JOptionPane.WARNING_MESSAGE);
    }
}
