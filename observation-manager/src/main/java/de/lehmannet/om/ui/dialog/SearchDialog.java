/* ====================================================================
 * /dialog/SearchDialog.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.dialog;

import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.WindowConstants;

import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.panel.AbstractSearchPanel;

class SearchDialog extends OMDialog implements ComponentListener {

    private static final long serialVersionUID = 3212116551261771429L;

    private AbstractSearchPanel panel = null;

    public SearchDialog(String title, AbstractSearchPanel panel, Component parentComponent, ObservationManager om) {

        super(om);

        this.setTitle(title);
        this.setModal(true);

        this.setSize(SearchDialog.serialVersionUID, 370, 110);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(parentComponent);

        this.panel = panel;

        this.getContentPane().add(this.panel);
        this.getRootPane().setDefaultButton(this.panel.getDefaultButton());
        this.panel.addComponentListener(this);

        this.pack();
        this.setVisible(true);

    }

    public ISchemaElement getSearchResult() {

        return this.panel.getSearchResult();

    }

    @Override
    public void componentHidden(ComponentEvent e) {

        this.dispose();

    }

    @Override
    public void componentMoved(ComponentEvent e) {

        // Do nothing

    }

    @Override
    public void componentResized(ComponentEvent e) {

        // Do nothing

    }

    @Override
    public void componentShown(ComponentEvent e) {

        // Do nothing

    }

}
