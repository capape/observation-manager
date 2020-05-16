package de.lehmannet.om.ui.util;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import de.lehmannet.om.ui.dialog.OMDialog;
import de.lehmannet.om.ui.dialog.ProgressDialog;
import de.lehmannet.om.ui.i18n.TextManager;
import de.lehmannet.om.ui.navigation.ObservationManager;
import java.awt.BorderLayout;

public class UserInterfaceHelperImpl implements UserInterfaceHelper {

    private final JFrame parent;
    private final TextManager textManager;

    public UserInterfaceHelperImpl(JFrame parent, TextManager textManager) {
        this.parent = parent;
        this.textManager = textManager;

    }

    @Override
    public void showInfo(String message) {
        JOptionPane.showMessageDialog(parent, message, this.textManager.getString("title.info"),
                JOptionPane.INFORMATION_MESSAGE);

    }

    @Override
    public void showWarning(String message) {
        JOptionPane.showMessageDialog(parent, message, this.textManager.getString("title.warning"),
                JOptionPane.WARNING_MESSAGE);

    }

    @Override
    public void showError(String message) {
        JOptionPane.showMessageDialog(parent, message, this.textManager.getString("title.error"),
                JOptionPane.ERROR_MESSAGE);

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
            this.pack();
            this.setVisible(true);
    
        }
    
        private void waitForCatalogLoaders() {
    
            while (this.threadGroup.activeCount() > 0) {
                try {
                    this.threadGroup.wait(300);
                } catch (InterruptedException ie) {
                    System.err.println("Interrupted while waiting for ThreadGroup.\n" + ie);
                } catch (IllegalMonitorStateException imse) {
                    // Ignore this
                    System.err.println("Ingnoring \n " + imse);
                }
            }
            this.dispose();
    
        }
    }

}