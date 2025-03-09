package de.lehmannet.om.ui.dialog;

import java.awt.Dimension;
import javax.swing.JDialog;
import javax.swing.JFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class OMDialog extends JDialog {

    private static final long serialVersionUID = -304493814237276957L;

    public static final String DIALOG_SIZE_KEY = "dialog.size.";

    private static final Logger LOGGER = LoggerFactory.getLogger(OMDialog.class);

    private long dialogID = -1;

    protected OMDialog(JFrame om) {

        super(om, true);

        LOGGER.debug(
                "Creating dialog {} with size: {},{}", this.getClass().getName(), this.getWidth(), this.getHeight());
    }

    public void setSize(long uniqueDialogID, int width, int height) {

        LOGGER.debug("Setting {} to size: {},{}", this.getClass().getName(), width, height);

        // this.dialogID = uniqueDialogID;

        // // Read size from configuration
        // String sizeString = this.observationManager.getConfiguration()
        // .getConfig(OMDialog.DIALOG_SIZE_KEY + this.dialogID);
        // if (sizeString != null) {
        // String x = sizeString.substring(0, sizeString.indexOf("x"));
        // String y = sizeString.substring(sizeString.indexOf("x") + 1);
        // try {
        // int w = Integer.parseInt(x);
        // int h = Integer.parseInt(y);

        // this.setSize(w, h);
        // } catch (NumberFormatException nfe) {
        // this.setSize(width, height); // Size from config is malformed
        // }

        // } else { // No size configured...use default size
        // this.setSize(width, height);
        // }
        this.setSize(width, height);
    }

    @Override
    public void dispose() {

        this.saveWindowSize();
        super.dispose();
    }

    private void saveWindowSize() {

        // Save current size
        Dimension size = this.getSize();

        LOGGER.debug(
                "Closing dialog {} with size: {},{}", this.getClass().getName(), this.getWidth(), this.getHeight());

        
        // if (this.dialogID != -1) { // Check if child unique ID is known -> required for saving dialog size                
        //     this.observationManager.getContext().setConfig(OMDialog.DIALOG_SIZE_KEY + this.dialogID,
        //          size.width + "x" + size.height);        
        // }

    }
}
