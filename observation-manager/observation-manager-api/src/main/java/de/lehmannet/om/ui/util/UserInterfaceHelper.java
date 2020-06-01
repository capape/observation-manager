package de.lehmannet.om.ui.util;

/**
 * Refactor to avoid dependency on ObservationManager from other modules.
 * 
 * @author Antonio Capape
 */
public interface UserInterfaceHelper {

    void showInfo(String message);
    void showWarning(String message);
    void showError(String message);


    void createProgressDialog(String title, String messageInfo, Worker task);

    void createWaitPopUp(String title, ThreadGroup threadGrop);

    void refreshUI();    

}