/*
 * ====================================================================
 * /dialog/ITargetDialog.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.dialog;

import de.lehmannet.om.ITarget;

/*
 * Required as base interface from which all Target dialogs can
 * derive from. Make sure we can always call getTarget();
 */
public interface ITargetDialog extends IDialog {

    ITarget getTarget();

}
