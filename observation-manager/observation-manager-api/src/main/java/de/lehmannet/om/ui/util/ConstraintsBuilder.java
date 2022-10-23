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

    public static void buildConstraints(GridBagConstraints gbc, int gx, int gy, int gwith, int gheight, int wx,
            int wy) {

        gbc.gridx = gx;
        gbc.gridy = gy;
        gbc.gridwidth = gwith;
        gbc.gridheight = gheight;
        gbc.weightx = wx;
        gbc.weighty = wy;
        gbc.ipadx = 1;
        gbc.ipady = 1;
        gbc.insets = new Insets(1, 1, 1, 1);

    }

}
