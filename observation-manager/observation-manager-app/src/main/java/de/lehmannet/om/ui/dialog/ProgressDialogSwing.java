/* ====================================================================
 * /dialog/ProgressDialog.java
 *
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import de.lehmannet.om.ui.util.ConstraintsBuilder;

public class ProgressDialogSwing extends JDialog implements ComponentListener, PropertyChangeListener {

    private static final long serialVersionUID = -1;

    private final SwingWorker<Void, Void> task;
    private final JProgressBar progressBar;

    public ProgressDialogSwing(JFrame om, String title, String information, SwingWorker<Void, Void> task) {

        super(om);

        this.setTitle(title);
        this.setLocationRelativeTo(om);
        this.addComponentListener(this);

        this.progressBar = this.initDialog(information);

        this.task = task;
        this.task.addPropertyChangeListener(this);
        this.task.execute();

        // Show wait UI
        this.pack();

        this.setVisible(true);

    }

    // -----------------
    // ComponentListener ------------------------------------------------------
    // -----------------

    @Override
    public void componentResized(ComponentEvent e) {

        final int MIN_WIDTH = 300;

        int width = getWidth();

        // we check if the width is below minimum
        boolean resize = false;
        if (width < MIN_WIDTH) {
            resize = true;
            width = MIN_WIDTH;
        }
        if (resize) {
            setSize(width, this.getHeight());
        }

    }

    public void close() {

        this.dispose();

    }

    private JProgressBar initDialog(String information) {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        this.getContentPane().setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 1, 1, 1, 50);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.BOTH;
        JLabel information1 = new JLabel(information);
        gridbag.setConstraints(information1, constraints);
        this.getContentPane().add(information1);

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 1, 1, 1, 50);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.BOTH;

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        gridbag.setConstraints(progressBar, constraints);
        this.getContentPane().add(progressBar);

        return progressBar;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName()) && SwingWorker.StateValue.DONE == evt.getNewValue()) {
            this.setVisible(false);
            this.dispose();

        }

    }

    @Override
    public void componentMoved(ComponentEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void componentShown(ComponentEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void componentHidden(ComponentEvent e) {
        // TODO Auto-generated method stub

    }

}
