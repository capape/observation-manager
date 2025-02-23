/*
 * ====================================================================
 * /dialog/IImagerDialog.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.dialog;

import de.lehmannet.om.IImager;

/*
 * Required as base interface from which all Imager dialogs can
 * derive from. Make sure we can always call getImager();
 */
public interface IImagerDialog extends IDialog {

    IImager getImager();
}
