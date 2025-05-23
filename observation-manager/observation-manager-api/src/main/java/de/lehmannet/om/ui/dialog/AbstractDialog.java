/*
 * ====================================================================
 * /dialog/AbstractDialog.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.dialog;

import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.LocaleToolsFactory;
import de.lehmannet.om.ui.util.UserInterfaceHelper;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public abstract class AbstractDialog extends OMDialog implements ActionListener, IDialog {

    private static final long serialVersionUID = 3761803558554164428L;

    protected static ResourceBundle bundle =
            LocaleToolsFactory.appInstance().getBundle("ObservationManager", Locale.getDefault());

    protected ISchemaElement schemaElement = null;

    final JButton positive = new JButton(AbstractDialog.bundle.getString("dialog.button.create"));
    protected final JButton cancel = new JButton(AbstractDialog.bundle.getString("dialog.button.cancel"));

    private AbstractPanel panel = null;
    private boolean oneButton = false;

    private final ObservationManagerModel model;
    private final UserInterfaceHelper uiHelper;

    protected AbstractDialog(
            JFrame om, ObservationManagerModel model, UserInterfaceHelper uiHelper, AbstractPanel panel) {

        this(false, om, model, uiHelper, panel);

        this.initDialog();
    }

    protected AbstractDialog(
            JFrame om,
            ObservationManagerModel model,
            UserInterfaceHelper uiHelper,
            AbstractPanel panel,
            boolean oneButton) {

        this(true, om, model, uiHelper, panel);

        this.initDialog();
    }

    private AbstractDialog(
            boolean oneButton,
            JFrame om,
            ObservationManagerModel model,
            UserInterfaceHelper uiHelper,
            AbstractPanel panel) {

        super(om);

        this.model = model;
        this.uiHelper = uiHelper;
        this.panel = panel;
        this.schemaElement = this.panel.getSchemaElement();

        if (this.panel.isEditable()) {
            this.positive.setText(AbstractDialog.bundle.getString("dialog.button.ok"));
        }

        this.setSize(550, 450);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(om);

        // Try to set system default look and feel
        /*
         * try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
         * catch(UnsupportedLookAndFeelException lfe) { } catch(InstantiationException ie) { }
         * catch(IllegalAccessException iae) { } catch(ClassNotFoundException cnfe) { }
         */

        this.getRootPane().setDefaultButton(this.positive);

        this.oneButton = oneButton;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();
        if (source instanceof JButton) {
            if (source.equals(this.positive)) {
                if (this.panel.isEditable()) {

                    ISchemaElement result = null;
                    if (this.schemaElement != null) {
                        result = this.panel.updateSchemaElement();
                        if (result != null) {
                            this.schemaElement = result;

                            // Make sure all screen are refreshed
                            this.model.update(this.schemaElement);
                            this.uiHelper.refreshUI();

                            this.dispose();
                        }
                    } else {
                        result = this.panel.createSchemaElement();
                        if (result != null) {
                            this.schemaElement = result;
                            // this.model.addSchemaElement(this.schemaElement);
                            this.model.add(this.schemaElement);
                            this.uiHelper.refreshUI();

                            this.dispose();
                        }
                    }
                }
            } else if (source.equals(this.cancel)) {
                this.dispose();
            }
        }
    }

    public static void reloadLanguage() {

        bundle = LocaleToolsFactory.appInstance().getBundle("ObservationManager", Locale.getDefault());
    }

    private void initDialog() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        this.getContentPane().setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 4, 1, 100, 90);
        constraints.fill = GridBagConstraints.BOTH;
        gridbag.setConstraints(this.panel, constraints);
        this.getContentPane().add(this.panel);

        if (!this.oneButton) {
            ConstraintsBuilder.buildConstraints(constraints, 1, 1, 1, 1, 100, 5);
            this.positive.addActionListener(this);
            gridbag.setConstraints(this.positive, constraints);
            this.getContentPane().add(this.positive);
        }

        ConstraintsBuilder.buildConstraints(constraints, 2, 1, 1, 1, 100, 5);
        this.cancel.addActionListener(this);
        gridbag.setConstraints(this.cancel, constraints);
        this.getContentPane().add(this.cancel);
    }
}
