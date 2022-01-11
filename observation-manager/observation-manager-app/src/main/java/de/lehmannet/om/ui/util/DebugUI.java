package de.lehmannet.om.ui.util;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import java.awt.Color;

import java.awt.GridBagLayout;

public final class DebugUI {
    public static void showGridToDebug(JPanel panelToDebug) {
        int numComponents = panelToDebug.getComponentCount();
        for (int index = 0; index < numComponents; index++) {
            final Border redline = BorderFactory.createLineBorder(Color.red);

            final Component component = panelToDebug.getComponent(index);

            if (component instanceof JPanel) {
                JPanel panel = (JPanel) component;
                panel.setBorder(redline);

            } else if (component instanceof JComponent) {

                JComponent jcomponent = (JComponent) component;
                
                jcomponent.setBorder(redline);
                jcomponent.setBackground(Color.yellow);

            }

            /*
             * Component container = component.getParent();
             * if (container instanceof JComponent) {
             * final Border blackLine = BorderFactory.createLineBorder(Color.black);
             * JComponent panel = (JComponent) container;
             * panel.setBorder(blackLine);
             * }
             */

        }
    }
}
