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
import java.awt.event.WindowEvent;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.Worker;

public class ProgressDialog extends OMDialog implements ComponentListener {

    private static final long serialVersionUID = -6190690843181392987L;

    private Thread calculation = null;
    private Thread watchdog = null;

    public ProgressDialog(ObservationManager om, String title, String information, Worker runnable) {

        super(om);

        super.setSize(ProgressDialog.serialVersionUID, 300, 100);
        super.setTitle(title);
        super.setLocationRelativeTo(om);
        super.addComponentListener(this);

        this.initDialog(information);

        // Start execution thread
        // Will do the "long term" calculation of whatever
        this.calculation = new Thread(runnable, "ProcessDialog: Calculation thread");
        this.calculation.start();

        // Start watchdog
        // Watchdog will call the close() method as soon as calculation thread
        // is done.
        this.watchdog = new Thread(new Watchdog(this, calculation), "ProcessDialog: Watchdog thread");
        this.watchdog.start();

        // Show wait UI
        this.pack();

        try {
            this.setVisible(true);
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            System.out.println("Krach!");
        }

    }

    // ------
    // JFrame -----------------------------------------------------------------
    // ------

    @Override
    protected void processWindowEvent(WindowEvent e) {

        super.processWindowEvent(e);

        // Catch window closing (via x Button)
        // and stop threads
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {

            this.watchdog.stop();
            this.calculation.stop();

            this.close();

        }

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

    @Override
    public void componentMoved(ComponentEvent e) {
        // Do nothing
    }

    @Override
    public void componentShown(ComponentEvent e) {
        // Do nothing
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        // Do nothing
    }

    public void close() {

        this.dispose();

    }

    private void initDialog(String information) {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        super.getContentPane().setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 1, 1, 1, 50);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.BOTH;
        JLabel information1 = new JLabel(information);
        gridbag.setConstraints(information1, constraints);
        super.getContentPane().add(information1);

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 1, 1, 1, 50);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.BOTH;
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        gridbag.setConstraints(progressBar, constraints);
        super.getContentPane().add(progressBar);

    }

}

class Watchdog implements Runnable {

    private ProgressDialog progress = null;
    private Thread calculation = null;

    public Watchdog(ProgressDialog progress, Thread calculation) {

        this.progress = progress;
        this.calculation = calculation;

    }

    @Override
    public void run() {

        try {
            this.calculation.join();
        } catch (InterruptedException ie) {
            // Can't do anything here
        }

        // Close the wait/progress UI;
        this.progress.close();

    }

}