package de.lehmannet.om.ui.util;

import de.lehmannet.om.ObservationManagerContext;
import de.lehmannet.om.ui.dialog.OMDialog;
import de.lehmannet.om.ui.dialog.ProgressDialog;
import de.lehmannet.om.ui.i18n.TextManager;
import de.lehmannet.om.ui.navigation.ObservationManager;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserInterfaceHelperImpl implements UserInterfaceHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserInterfaceHelperImpl.class);
    private final ObservationManager parent;
    private final TextManager textManager;

    public UserInterfaceHelperImpl(ObservationManager parent, TextManager textManager) {
        this.parent = parent;
        this.textManager = textManager;
    }

    @Override
    public void showInfo(String message) {
        JOptionPane.showMessageDialog(
                parent, message, this.textManager.getString("title.info"), JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void showWarning(String message) {
        JOptionPane.showMessageDialog(
                parent, message, this.textManager.getString("title.warning"), JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public void showError(String message) {
        JOptionPane.showMessageDialog(
                parent, message, this.textManager.getString("title.error"), JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void createProgressDialog(String title, String messageInfo, Worker task) {
        new ProgressDialog(parent, title, messageInfo, task);
    }

    @Override
    public void createWaitPopUp(String title, ThreadGroup threadGrop) {

        new WaitPopup(threadGrop, this.parent, title);
    }

    class WaitPopup extends OMDialog {

        private static final long serialVersionUID = -3950819080525084021L;

        private ThreadGroup threadGroup = null;

        public WaitPopup(ThreadGroup threadGroup, JFrame om, String title) {

            super(om);
            this.setLocationRelativeTo(om);
            this.setTitle(title);

            this.threadGroup = threadGroup;

            this.getContentPane().setLayout(new BorderLayout());

            JProgressBar progressBar = new JProgressBar(0, 100);
            progressBar.setValue(0);
            progressBar.setIndeterminate(true);

            this.getContentPane().add(progressBar, BorderLayout.CENTER);

            this.setSize(WaitPopup.serialVersionUID, 250, 60);

            Runnable wait = WaitPopup.this::waitForCatalogLoaders;

            Thread waitThread = new Thread(wait, "WaitPopup");
            waitThread.start();
            this.setVisible(true);
        }

        private void waitForCatalogLoaders() {

            while (this.threadGroup.activeCount() > 0) {
                try {
                    this.threadGroup.wait(300);
                } catch (InterruptedException ie) {
                    LOGGER.error("Interrupted while waiting for ThreadGroup", ie);
                } catch (IllegalMonitorStateException imse) {
                    // Ignore this
                    LOGGER.error("Ingnoring", imse);
                }
            }
            this.dispose();
        }
    }

    @Override
    public void refreshUI() {

        this.parent.refreshUI();
    }
}
