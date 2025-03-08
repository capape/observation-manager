/*
 * ====================================================================
 * /dialog/PreferencesDialog.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.preferences;

import de.lehmannet.om.ObservationManagerContext;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.dialog.OMDialog;
import de.lehmannet.om.ui.i18n.TextManager;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

public class PreferencesDialog extends OMDialog implements ActionListener {

    private static final long serialVersionUID = -8289411368690909665L;

    private JTabbedPane tabbedPane = null;

    private final JButton ok;
    private final JButton cancel;

    private final ObservationManager om;
    private final ObservationManagerModel model;
    private final ObservationManagerContext context;
    private final TextManager textManager;

    public PreferencesDialog(
            ObservationManagerContext context,
            ObservationManager om,
            ObservationManagerModel model,
            PreferencesPanel[] additionalPanels) {

        super(om);

        this.om = om;
        this.model = model;
        this.context = context;
        this.textManager = context.getTextManager();

        this.ok = new JButton(this.textManager.getString("dialog.button.ok"));
        this.cancel = new JButton(this.textManager.getString("dialog.button.cancel"));

        this.setTitle(this.textManager.getString("dialog.preferences.title"));
        this.setSize(PreferencesDialog.serialVersionUID, 1400, 350);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(om);

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
        this.getContentPane().setLayout(gridbag);

        this.tabbedPane = new JTabbedPane();

        PreferencesPanel genericPanel = new GeneralPanel(this.context, this.om);
        PreferencesPanel behaviourPanel =
                new BehaviourPanel(this.om.getConfiguration(), this.om, this.model, this.textManager);
        this.tabbedPane.addTab(genericPanel.getTabTitle(), genericPanel);
        this.tabbedPane.addTab(behaviourPanel.getTabTitle(), behaviourPanel);
        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 2, 4, 33, 33);
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(this.tabbedPane, constraints);
        this.getContentPane().add(this.tabbedPane);

        ConstraintsBuilder.buildConstraints(constraints, 0, 3, 1, 1, 33, 33);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.SOUTH;
        this.ok.addActionListener(this);
        gridbag.setConstraints(this.ok, constraints);
        this.getContentPane().add(this.ok);

        ConstraintsBuilder.buildConstraints(constraints, 1, 3, 1, 1, 33, 33);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.SOUTH;
        this.cancel.addActionListener(this);
        gridbag.setConstraints(this.cancel, constraints);
        this.getContentPane().add(this.cancel);
    }
}
