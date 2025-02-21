/*
 * ====================================================================
 * /util/EditPopupHandler.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.util;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;

public class EditPopupHandler implements ActionListener {

    private JTextArea area = null;

    private JMenuItem copy = null;
    private JMenuItem paste = null;
    private JMenuItem cut = null;

    public EditPopupHandler(int x, int y, JTextArea area) {

        this.area = area;

        JPopupMenu popupMenu = new JPopupMenu();

        ResourceBundle bundle = LocaleToolsFactory.appInstance().getBundle("ObservationManager", Locale.getDefault());
        this.copy = new JMenuItem(bundle.getString("copy"));
        this.copy.addActionListener(this);
        popupMenu.add(this.copy);

        this.paste = new JMenuItem(bundle.getString("paste"));
        this.paste.addActionListener(this);
        popupMenu.add(this.paste);

        this.cut = new JMenuItem(bundle.getString("cut"));
        this.cut.addActionListener(this);
        popupMenu.add(this.cut);

        this.enablePaste();
        this.enableCutAndCopy();

        popupMenu.setPopupSize(150, 75);
        popupMenu.show(area, x, y);
    }

    // --------------
    // ActionListener ---------------------------------------------------------
    // --------------

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() instanceof JMenuItem) {
            JMenuItem source = (JMenuItem) e.getSource();
            if (source.equals(this.copy)) {
                this.area.copy();
            } else if (source.equals(this.paste)) {
                this.area.paste();
            } else if (source.equals(this.cut)) {
                this.area.cut();
            }
        }
    }

    private void enablePaste() {

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);
        boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
        if (hasTransferableText) {
            this.paste.setEnabled(true);
        } else {
            this.paste.setEnabled(false);
        }
    }

    private void enableCutAndCopy() {

        if ((this.area.getSelectedText() != null)
                && !("".equals(this.area.getSelectedText().trim()))) {
            this.copy.setEnabled(true);
            this.cut.setEnabled(true);
        } else {
            this.copy.setEnabled(false);
            this.cut.setEnabled(false);
        }
    }
}
