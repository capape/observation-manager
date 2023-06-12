/*
 * ====================================================================
 * /dialog/ProgressDialog.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.Worker;

public class ProgressDialog extends OMDialog implements ComponentListener {

    private static final long serialVersionUID = -6190690843181392987L;

    private Thread calculation = null;
    private Thread watchdog = null;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProgressDialog.class);

    public ProgressDialog(JFrame om, String title, String information, Worker runnable) {

        super(om);

        this.setSize(ProgressDialog.serialVersionUID, 300, 100);
        this.setTitle(title);
        this.setLocationRelativeTo(om);
        this.addComponentListener(this);

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

        try {
            this.setVisible(true);
        } catch (ArrayIndexOutOfBoundsException e) {
            LOGGER.error("Ignoring unexpected exception", e);
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

            this.watchdog.interrupt();
            this.calculation.interrupt();
            ;

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
        LOGGER.debug("Closing progress dialog");
        this.dispose();

    }

    private void initDialog(String information) {

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

    }

}

class Watchdog implements Runnable {

    private ProgressDialog progress = null;
    private Thread calculation = null;
    private static final Logger LOGGER = LoggerFactory.getLogger(Watchdog.class);

    public Watchdog(ProgressDialog progress, Thread calculation) {

        this.progress = progress;
        this.calculation = calculation;
        LOGGER.debug("Create progress dialog {}:{}", calculation.getName(), progress.getTitle());
    }

    @Override
    public void run() {

        try {
            this.calculation.join();
        } catch (InterruptedException ie) {
            LOGGER.warn("Ignoring", ie);
            // Can't do anything here
        }

        // Close the wait/progress UI;
        this.progress.close();

    }

}