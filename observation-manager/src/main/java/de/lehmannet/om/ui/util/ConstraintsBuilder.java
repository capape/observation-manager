/* ====================================================================
 * /util/ConstraintsBuilder.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.util;

import java.awt.GridBagConstraints;
import java.awt.Insets;

public class ConstraintsBuilder {

    public static void buildConstraints(GridBagConstraints gbc, int gx, int gy, int gw, int gh, int wx, int wy) {

        gbc.gridx = gx;
        gbc.gridy = gy;
        gbc.gridwidth = gw;
        gbc.gridheight = gh;
        gbc.weightx = wx;
        gbc.weighty = wy;
        gbc.ipadx = 1;
        gbc.ipady = 1;
        gbc.insets = new Insets(1, 1, 1, 1);

    }

}
