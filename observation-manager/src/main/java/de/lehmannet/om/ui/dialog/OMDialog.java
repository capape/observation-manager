package de.lehmannet.om.ui.dialog;

import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JFrame;

import de.lehmannet.om.ui.navigation.ObservationManager;

public class OMDialog extends JDialog {

    private static final long serialVersionUID = -304493814237276957L;

    public static final String DIALOG_SIZE_KEY = "dialog.size.";

    private JFrame observationManager = null;
    private long dialogID = -1;

    protected OMDialog(JFrame om) {

        super(om, true);

        this.observationManager = om;

    }

    public void setSize(long uniqueDialogID, int width, int height) {

        // this.dialogID = uniqueDialogID;

        // // Read size from configuration
        // String sizeString = this.observationManager.getConfiguration()
        //         .getConfig(OMDialog.DIALOG_SIZE_KEY + this.dialogID);
        // if (sizeString != null) {
        //     String x = sizeString.substring(0, sizeString.indexOf("x"));
        //     String y = sizeString.substring(sizeString.indexOf("x") + 1);
        //     try {
        //         int w = Integer.parseInt(x);
        //         int h = Integer.parseInt(y);

        //         super.setSize(w, h);
        //     } catch (NumberFormatException nfe) {
        //         super.setSize(width, height); // Size from config is malformed
        //     }

        // } else { // No size configured...use default size
        //     super.setSize(width, height);
        // }
        setSize(width, height);
        // pack();
        //  setVisible(true);
    }

    @Override
    public void dispose() {

        this.saveWindowSize();
        super.dispose();

    }

    private void saveWindowSize() {

        // if (this.dialogID != -1) { // Check if child unique ID is known -> required for saving dialog size

        //     // Save current size
        //     Dimension size = super.getSize();

        //     this.observationManager.getConfiguration().setConfig(OMDialog.DIALOG_SIZE_KEY + this.dialogID,
        //             size.width + "x" + size.height);

        // }

    }

}
