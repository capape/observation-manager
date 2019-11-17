/* ====================================================================
 * /dialog/PreferencesDialog.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.preferences;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import de.lehmannet.om.ui.dialog.OMDialog;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.util.ConstraintsBuilder;

public class PreferencesDialog extends OMDialog implements ActionListener {

    private static final long serialVersionUID = -8289411368690909665L;

    private final PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
            .getBundle("ObservationManager", Locale.getDefault());

    private ObservationManager om = null;

    private JTabbedPane tabbedPane = null;

    private final JButton ok = new JButton(this.bundle.getString("dialog.button.ok"));
    private final JButton cancel = new JButton(this.bundle.getString("dialog.button.cancel"));

    public PreferencesDialog(ObservationManager om, PreferencesPanel[] additionalPanels) {

        super(om);

        this.om = om;

        super.setTitle(this.bundle.getString("dialog.preferences.title"));
        super.setSize(PreferencesDialog.serialVersionUID, 750, 267);
        super.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        super.setLocationRelativeTo(om);

        this.initDialog();

        for (PreferencesPanel additionalPanel : additionalPanels) {
            this.addPreferencesTab(additionalPanel);
        }

        this.setVisible(true);

    }

    // --------------
    // ActionListener ---------------------------------------------------------
    // --------------

    @Override
    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();
        if (source instanceof JButton) {
            if (source.equals(this.cancel)) {
                this.dispose();
            } else {
                this.applySettings();
                this.dispose();
            }
        }

    }

    private void addPreferencesTab(PreferencesPanel panel) {

        if (panel != null) {
            this.tabbedPane.addTab(panel.getTabTitle(), panel);
        }

    }

    private void applySettings() {

        for (int i = 0; i < this.tabbedPane.getTabCount(); i++) {
            ((PreferencesPanel) this.tabbedPane.getComponentAt(i)).writeConfig();
        }

    }

    private void initDialog() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        super.getContentPane().setLayout(gridbag);

        this.tabbedPane = new JTabbedPane();

        PreferencesPanel genericPanel = new GeneralPanel(this.om.getConfiguration(), this.om);
        PreferencesPanel behaviourPanel = new BehaviourPanel(this.om.getConfiguration(), this.om);
        this.tabbedPane.addTab(genericPanel.getTabTitle(), genericPanel);
        this.tabbedPane.addTab(behaviourPanel.getTabTitle(), behaviourPanel);
        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 2, 4, 33, 33);
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(this.tabbedPane, constraints);
        super.getContentPane().add(this.tabbedPane);

        ConstraintsBuilder.buildConstraints(constraints, 0, 3, 1, 1, 33, 33);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.SOUTH;
        this.ok.addActionListener(this);
        gridbag.setConstraints(this.ok, constraints);
        super.getContentPane().add(this.ok);

        ConstraintsBuilder.buildConstraints(constraints, 1, 3, 1, 1, 33, 33);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.SOUTH;
        this.cancel.addActionListener(this);
        gridbag.setConstraints(this.cancel, constraints);
        super.getContentPane().add(this.cancel);

    }

}