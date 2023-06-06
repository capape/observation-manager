/*
 * ====================================================================
 * /dialog/AboutDialog.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */
package de.lehmannet.om.ui.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import de.lehmannet.om.ui.i18n.TextManager;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.util.ConstraintsBuilder;

public class AboutDialog extends OMDialog implements ActionListener {

    private static final long serialVersionUID = 4875893088001020590L;

    private final JButton close;

    private final TextManager textManager;
    private final TextManager versionTextManager;

    public AboutDialog(ObservationManager om, TextManager textManager, TextManager versionTextManager) {

        super(om);

        this.textManager = textManager;
        this.versionTextManager = versionTextManager;

        this.close = new JButton(textManager.getString("about.button.close"));
        this.setTitle(textManager.getString("about.button.title"));
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(om);

        this.initDialog();
        this.setSize(AboutDialog.serialVersionUID, 400, 260);

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
        this.getContentPane().setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 1, 5, 1, 99);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.BOTH;
        JTextArea text = new JTextArea();
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setEditable(false);
        String about = textManager.getString("about.dialog.text") + System.lineSeparator() + System.lineSeparator()
                + String.format("Open Astronomy Log Version: %s", this.versionTextManager.getString("oal.version"));

        text.setText(about);
        gridbag.setConstraints(text, constraints);
        this.getContentPane().add(text);

        ConstraintsBuilder.buildConstraints(constraints, 0, 5, 1, 1, 1, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.close.addActionListener(this);
        gridbag.setConstraints(this.close, constraints);
        this.getContentPane().add(this.close);

    }

}
