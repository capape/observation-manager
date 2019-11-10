/* ====================================================================
 * /dialog/AboutDialog.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */
package de.lehmannet.om.ui.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.util.ConstraintsBuilder;

public class AboutDialog extends OMDialog implements ActionListener {

    private static final long serialVersionUID = 4875893088001020590L;

    final PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle.getBundle("ObservationManager",
            Locale.getDefault());

    protected JButton close = new JButton(this.bundle.getString("about.button.close"));

    public AboutDialog(ObservationManager om) {

        super(om);

        super.setTitle(this.bundle.getString("about.button.title"));
        super.setSize(AboutDialog.serialVersionUID, 400, 260);
        super.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        super.setLocationRelativeTo(om);

        // Try to set system default look and feel
        /*
         * try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
         * catch(UnsupportedLookAndFeelException lfe) { } catch(InstantiationException
         * ie) { } catch(IllegalAccessException iae) { } catch(ClassNotFoundException
         * cnfe) { }
         */

        this.initDialog();

        this.setVisible(true);

    }

    // --------------
    // ActionListener ---------------------------------------------------------
    // --------------

    @Override
    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();
        if (source instanceof JButton) {
            if (source.equals(this.close)) {
                this.dispose();
            }
        }

    }

    private void initDialog() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        super.getContentPane().setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 1, 5, 1, 99);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.BOTH;
        JTextArea text = new JTextArea();
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setEditable(false);
        text.setText("Observation Manager - Version " + ObservationManager.VERSION + "\n" + "(c) Dirk Lehmann\n\n"
                + "http://observation.sourceforge.net/\n\n" + "Distributed under the Apache Software License 2.0\n"
                + "Please see file LICENSE-2.0.txt\n\n"
                + "This product includes software developed by\nThe Apache Software Foundation (http://www.apache.org/)");
        gridbag.setConstraints(text, constraints);
        super.getContentPane().add(text);

        ConstraintsBuilder.buildConstraints(constraints, 0, 5, 1, 1, 1, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.close.addActionListener(this);
        gridbag.setConstraints(this.close, constraints);
        super.getContentPane().add(this.close);

    }

}
