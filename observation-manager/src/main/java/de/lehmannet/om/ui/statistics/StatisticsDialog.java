/* ====================================================================
 * /statistics/StatisticsDialog.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.statistics;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

import de.lehmannet.om.IFinding;
import de.lehmannet.om.IObservation;
import de.lehmannet.om.IObserver;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.ui.catalog.ICatalog;
import de.lehmannet.om.ui.catalog.IListableCatalog;
import de.lehmannet.om.ui.dialog.OMDialog;
import de.lehmannet.om.ui.dialog.SchemaElementSelectorPopup;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.util.ConfigKey;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.util.SchemaElementConstants;

public class StatisticsDialog extends OMDialog implements ActionListener, ComponentListener {

    private static final long serialVersionUID = 6609511333362846103L;

    private final PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
            .getBundle("ObservationManager", Locale.getDefault());

    // The observers for which the statistics will be shown
    private List<IObserver> observers = null;

    private CatalogTargets[] catalogTargets = null;

    private JLabel[] resultLabels = new JLabel[0];
    private JButton[] catButtons = new JButton[0];
    private JProgressBar[] catProgress = new JProgressBar[0];
    private Thread[] checkersThreads = new Thread[0];
    CatalogChecker[] checkers = new CatalogChecker[0];
    private UpdateRunnable updateUIRunnable = null;

    private ObservationManager om = null;

    // All selected catalogs
    private List<ICatalog> selectedCatalogs = null;

    private final JButton close = new JButton(this.bundle.getString("dialog.button.ok"));

    public StatisticsDialog(ObservationManager om) {

        super(om);

        StatisticsQueryDialog queryDialog = new StatisticsQueryDialog(om);
        this.selectedCatalogs = queryDialog.getSelectedCatalogs();

        Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
        om.setCursor(defaultCursor);
        this.setCursor(defaultCursor);

        if ((this.selectedCatalogs == null) || (this.selectedCatalogs.isEmpty())) {
            return;
        }

        this.om = om;

        this.setSize(StatisticsDialog.serialVersionUID, 600, 170);
        this.addComponentListener(this); // Make sure we have a certain width at least
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(om);

        // Check how many observers exist...
        // If only one observer use this one, if several create pop-up for selection
        IObserver[] observers = om.getXmlCache().getObservers();
        if ((observers == null) || (observers.length == 0)) {
            return;
        } else if (observers.length > 1) {
            // Get default observer for preselection
            String defaultObserverDisplayName = this.om.getConfiguration()
                    .getConfig(ConfigKey.CONFIG_DEFAULT_OBSERVER);
            List<IObserver> preselectedObserver = new ArrayList<>();
            for (IObserver observer : observers) {
                if (observer.getDisplayName().equals(defaultObserverDisplayName)) {
                    preselectedObserver.add(observer);
                    break;
                }
            }
            // Show popup
            SchemaElementSelectorPopup popup = new SchemaElementSelectorPopup(this.om,
                    this.bundle.getString("dialog.statistics.observerPopup.title"), null, preselectedObserver, true,
                    SchemaElementConstants.OBSERVER);
            this.observers = popup.getAllSelectedElements().stream().map(x->{return (IObserver) x;}).collect(Collectors.toList());
            if ((this.observers == null) || (this.observers.isEmpty())) {
                return;
            }
        } else { // only one observer so we don't show selection screen
            this.observers = Arrays.asList(observers);
        }

        this.setTitle(this.bundle.getString("dialog.statistics.title.prefix"));

        // Show dialog
        this.initDialog();
        this.pack();

        // Start calculation of statistics
        this.createCatalogueStatistics();

        this.setVisible(true);

    }

    // --------------
    // ActionListener ---------------------------------------------------------
    // --------------

    @Override
    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();
        if (source instanceof JButton) {
            if (source.equals(this.close)) {
                // Kill all checker threads
                for (int j = 0; j < checkersThreads.length; j++) {
                    if (checkersThreads[j].isAlive()) { // Check if thread is still running
                        this.checkers[j].stop(); // Tell checkers to stop...this will end the thread cleanly
                    }
                }
                // Kill update UI thread
                if (this.updateUIRunnable != null) {
                    this.updateUIRunnable.stop();
                }
                this.dispose();
            } else {
                JButton current = null;
                for (JButton catButton : this.catButtons) {
                    current = catButton;
                    if (source.equals(current)) {
                        // We set the Catalogname as ActionCommand
                        ListIterator<ICatalog> iterator = this.selectedCatalogs.listIterator();
                        boolean found = false;
                        IListableCatalog currentCat = null;
                        while (iterator.hasNext() && !found) {
                            currentCat = (IListableCatalog) iterator.next();
                            if (current.getActionCommand().equals(currentCat.getName())) {
                                found = true;
                            }
                        }

                        this.getObservationsForCatalog(currentCat);
                    }
                }
            }
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

            // Kill all checker threads
            for (int j = 0; j < checkersThreads.length; j++) {
                if (checkersThreads[j].isAlive()) { // Check if thread is still running
                    this.checkers[j].stop(); // Tell checkers to stop...this will end the thread cleanly
                }
            }
            // Kill update UI thread
            this.updateUIRunnable.stop();

            this.dispose();
        }

    }

    // -----------------
    // ComponentListener ------------------------------------------------------
    // -----------------

    @Override
    public void componentResized(ComponentEvent e) {

        final int MIN_WIDTH = 600;

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

    private void createCatalogueStatistics() {

        Iterator<ICatalog> iterator = this.selectedCatalogs.iterator();
        IListableCatalog current = null;
        IObservation[] observations = this.om.getXmlCache().getObservations();

        // Get config
        boolean useCoObservers = Boolean.parseBoolean(
                this.om.getConfiguration().getConfig(ConfigKey.CONFIG_STATISTICS_USE_COOBSERVERS));

        // Iterate over all selected catalogs, create CatalogCheckers and start threads
        this.checkers = new CatalogChecker[this.selectedCatalogs.size()];
        this.checkersThreads = new Thread[this.selectedCatalogs.size()];
        int i = 0;
        while (iterator.hasNext()) {
            current = (IListableCatalog) iterator.next();

            checkers[i] = new CatalogChecker(current, observations, this.observers, useCoObservers,
                    this.catProgress[i]);
            checkersThreads[i] = new Thread(checkers[i], current.getName());
            // checkersThreads[i].setPriority(Thread.MAX_PRIORITY);
            checkersThreads[i++].start();

        }

        // Start monitor thread...
        // This will call the showButton method as soon as a thread is finished
        this.updateUIRunnable = new UpdateRunnable(this);
        Thread updateUI = new Thread(this.updateUIRunnable, "Update Statistics UI");
        updateUI.start();

    }

    boolean showButton(int index) {

        if (this.catProgress[index] == null) { // progressbar already removed
            return false;
        }

        this.catalogTargets[index] = this.checkers[index].getCatalogTargets();

        // Set result
        int observed = this.catalogTargets[index].numberOfObservations();
        int total = this.checkers[index].getCatalog().getCatalogIndex().length;
        int percentage = (int) ((100.0 / total) * observed);
        String resultText = observed + "/" + total + " (" + percentage + "%)\n";
        this.resultLabels[index].setText(resultText);

        // Remove progressbar and replace it with button
        // Get Layout
        GridBagLayout gridbag = (GridBagLayout) this.getContentPane().getLayout();
        // Get constraints from progressbar and remove progress bar
        GridBagConstraints constraints = gridbag.getConstraints(this.catProgress[index]);
        this.getContentPane().remove(this.catProgress[index]);
        this.catProgress[index] = null;
        // Use constraints for button and add button
        gridbag.setConstraints(this.catButtons[index], constraints);
        this.getContentPane().add(this.catButtons[index]);
        if (this.getGraphics() != null) { // Might be null if window is already closed
            this.getContentPane().update(this.getGraphics());
        }

        this.invalidate();
        this.validate();
        this.pack();
        this.repaint();

        return true;

    }

    private void getObservationsForCatalog(IListableCatalog cat) {

        for (CatalogTargets catalogTarget : this.catalogTargets) {
            if (catalogTarget != null) {
                if (catalogTarget.getCatalog().equals(cat)) {
                    ObservationStatisticsTableModel tableModel = new ObservationStatisticsTableModel(catalogTarget);
                    new StatisticsDetailsDialog(om, tableModel); // Show Details dialog
                    break;
                }
            }
        }

    }

    private void initDialog() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.CENTER;
        this.getContentPane().setLayout(gridbag);

        // Set Headers
        constraints.fill = GridBagConstraints.NONE;
        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 4, 1, 100, 1);
        JLabel headerLabel = new JLabel(this.bundle.getString("dialog.statistics.label.catalogs"));
        gridbag.setConstraints(headerLabel, constraints);
        this.getContentPane().add(headerLabel);
        constraints.fill = GridBagConstraints.HORIZONTAL;

        constraints.anchor = GridBagConstraints.CENTER;
        ConstraintsBuilder.buildConstraints(constraints, 1, 1, 1, 1, 50, 1);
        JLabel catNameLabel = new JLabel(this.bundle.getString("dialog.statistics.label.catalogName"));
        catNameLabel.setFont(new Font("Arial", Font.ITALIC + Font.BOLD, 12));
        gridbag.setConstraints(catNameLabel, constraints);
        this.getContentPane().add(catNameLabel);

        ConstraintsBuilder.buildConstraints(constraints, 2, 1, 1, 1, 50, 1);
        JLabel catResultLabel = new JLabel(this.bundle.getString("dialog.statistics.label.result"));
        catResultLabel.setFont(new Font("Arial", Font.ITALIC + Font.BOLD, 12));
        gridbag.setConstraints(catResultLabel, constraints);
        this.getContentPane().add(catResultLabel);
        constraints.anchor = GridBagConstraints.WEST;

        int size = this.selectedCatalogs.size();
        JLabel[] catLabels = new JLabel[size];
        this.resultLabels = new JLabel[size];
        this.catButtons = new JButton[size];
        this.catProgress = new JProgressBar[size];
        this.catalogTargets = new CatalogTargets[size];

        IListableCatalog current = null;
        int i = 0;
        for (Object selectedCatalog : this.selectedCatalogs) {

            current = (IListableCatalog) selectedCatalog;

            // Set Catalog Label
            ConstraintsBuilder.buildConstraints(constraints, 1, i + 2, 1, 1, 33, 1);
            catLabels[i] = new JLabel(current.getName());
            catLabels[i].setFont(new Font("Arial", Font.PLAIN, 12));
            gridbag.setConstraints(catLabels[i], constraints);
            this.getContentPane().add(catLabels[i]);

            // Set Result Label
            ConstraintsBuilder.buildConstraints(constraints, 2, i + 2, 1, 1, 33, 1);
            resultLabels[i] = new JLabel();
            resultLabels[i].setFont(new Font("Arial", Font.PLAIN, 12));
            gridbag.setConstraints(resultLabels[i], constraints);
            this.getContentPane().add(resultLabels[i]);

            // Prepare Button (show them when calculation is done (replaceing progressbar)
            this.catButtons[i] = new JButton(this.bundle.getString("dialog.statistics.button.details"));
            this.catButtons[i].addActionListener(this);
            // Set catalogname as action...
            this.catButtons[i].setActionCommand(current.getName());

            // Set progressbar
            ConstraintsBuilder.buildConstraints(constraints, 3, i + 2, 1, 1, 33, 1);
            this.catProgress[i] = new JProgressBar();
            this.catProgress[i].setStringPainted(true);
            gridbag.setConstraints(this.catProgress[i], constraints);
            this.getContentPane().add(this.catProgress[i]);

            i++;

        }

        ConstraintsBuilder.buildConstraints(constraints, 0, i + 2, 4, 1, 100, 1);
        this.close.addActionListener(this);
        gridbag.setConstraints(this.close, constraints);
        this.getContentPane().add(this.close);

    }

}

class CatalogChecker implements Runnable {

    private IListableCatalog catalog = null;
    private IObservation[] observations = null;
    private CatalogTargets catalogTargets = null;
    private List<IObserver> selectedObservers = null;
    private boolean useCoObservers = true;

    private boolean run = true; // Set to false to stop thread

    private JProgressBar progressBar = null;

    public CatalogChecker(IListableCatalog catalog, IObservation[] observations, List<IObserver> observers, boolean useCoObservers,
            JProgressBar progress) {

        this.catalog = catalog;
        this.observations = observations;
        this.selectedObservers = observers;
        this.useCoObservers = useCoObservers;

        this.progressBar = progress;

    }

    public CatalogTargets getCatalogTargets() {

        return this.catalogTargets;

    }

    public IListableCatalog getCatalog() {

        return this.catalog;

    }

    public void stop() {

        this.run = false;

    }

    public boolean isRunning() {

        return this.run;

    }

    // Runnable
    @Override
    public void run() {

        // ---------- Preparation

        // Get all targets from catalog
        ITarget[] targets = this.catalog.getTargets();

        // Create CatalogTargets object
        this.catalogTargets = new CatalogTargets(this.catalog, targets);

        double onePercent = (double) 100 / this.observations.length;

        // ---------- Checks (will add observation in catalogTargets object if target
        // was found
        for (int i = 0; i < this.observations.length; i++) {

            if (this.run) {

                // Observation doesn't belong to selected observers
                if (!this.selectedObservers.contains(this.observations[i].getObserver())) {

                    if (this.useCoObservers) {
                        // Check via coObservers
                        if (this.observations[i].getSession() != null) {
                            if ((this.observations[i].getSession().getCoObservers() != null)
                                    && !(this.observations[i].getSession().getCoObservers().isEmpty())) {
                                List<IObserver> coObservers = this.observations[i].getSession().getCoObservers();
                                ListIterator<IObserver> iterator = coObservers.listIterator();
                                IObserver current = null;
                                boolean found = false;
                                while (iterator.hasNext()) {
                                    current = iterator.next();
                                    if (this.selectedObservers.contains(current)) {
                                        found = true; // Keep in mind that we found something
                                        break; // Break while loop
                                    }
                                }
                                if (!found) { // Did we come here, cause we found something?
                                    continue; // Continue for loop
                                }
                            } else {
                                continue; // Continue for loop as session has no coObservers
                            }
                        } else {
                            continue; // Continue for loop as observation has no session
                        }
                    } else {
                        continue; // Continue for loop as coObservers shouldn't be considered
                    }

                }

                // Iterate over all findings to findout whether the target was seen at all
                ListIterator<IFinding> findingIterator = this.observations[i].getResults().listIterator();
                boolean findingSeen = false;
                while (findingIterator.hasNext()) {
                    if (findingIterator.next().wasSeen()) {
                        findingSeen = true;
                        break;
                    }
                }
                if (!findingSeen) { // There was no finding where the target object was seen...skip observation
                    continue;
                }

                this.catalogTargets.checkTarget(this.observations[i]);

                // Update progress bar
                this.progressBar.setValue((int) Math.round(i * onePercent));
                this.progressBar.setString("" + (int) Math.round(i * onePercent) + "%");

            } else {
                break; // Stop loop -> stop thread
            }

        }

        this.run = false; // Indicate we're done

    }

}

class UpdateRunnable implements Runnable {

    private StatisticsDialog dialog = null;
    private boolean running = true;

    public UpdateRunnable(StatisticsDialog dialog) {

        this.dialog = dialog;

    }

    public void stop() {

        this.running = false;

    }

    public boolean isRunning() {

        return this.running;

    }

    @Override
    public void run() {

        // Wait until all threads are done
        Object watchDog = new Object(); // Simple object to wait on
        synchronized (watchDog) {
            int counter = 0; // Counter to realize end
            while ((counter <= this.dialog.checkers.length) && (running)) { // ...until all buttons are visible
                for (int j = 0; j < this.dialog.checkers.length; j++) {
                    if (!this.dialog.checkers[j].isRunning()) { // Check if thread is finished
                        if (this.dialog.showButton(j)) { // Show button
                            counter++; // If button was made visible, inc. counter
                        }
                    }
                }
                try {
                    watchDog.wait(1000);
                } catch (InterruptedException ie) {
                    System.err.println("CatalogChecker thread was interrupted!\n" + ie);
                }
            }
            this.running = false;
        }

    }

}