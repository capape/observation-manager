/* ====================================================================
 * /util/ExtenableSchemaElementSelector.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.util;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import de.lehmannet.om.ui.dialog.IDialog;
import de.lehmannet.om.ui.extension.SchemaUILoader;
import de.lehmannet.om.util.SchemaElementConstants;

public class ExtenableSchemaElementSelector extends JDialog implements ActionListener {

    private final PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
            .getBundle("ObservationManager", Locale.getDefault());

    private JButton ok = null;
    private JButton cancel = null;
    private JComboBox selector = null;

    private IDialog selectedSchemaElementDialog = null;

    private boolean result = false;

    private SchemaElementConstants schemaElementConstant = SchemaElementConstants.NONE;

    private SchemaUILoader loader = null;

    public ExtenableSchemaElementSelector(JDialog parent, SchemaUILoader loader, SchemaElementConstants schemaElementConstant) {

        super(parent, true);

        this.schemaElementConstant = schemaElementConstant;

        this.setTitle(this.bundle.getString("selector.title"));
        this.init(parent, loader);

    }

    public ExtenableSchemaElementSelector(JFrame parent, SchemaUILoader loader, SchemaElementConstants schemaElementConstant) {

        super(parent, true);

        this.schemaElementConstant = schemaElementConstant;

        this.setTitle(this.bundle.getString("selector.title"));
        this.init(parent, loader);

    }

    private void init(Window window, SchemaUILoader loader) {

        this.loader = loader;
        this.initFindingChooser();

        if ((this.loader.getAllXSIDisplayNamesForCreation(this.schemaElementConstant) == null)
                || (this.loader.getAllXSIDisplayNamesForCreation(this.schemaElementConstant).length == 0)) {
            JOptionPane.showMessageDialog(this,
                    this.bundle.getString("extenableSchemaElementSelector.warning.noElements"),
                    this.bundle.getString("title.warning"), JOptionPane.WARNING_MESSAGE);
            return;
        }

        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setSize(300, 80);
        // Calculate center
        Point obsLocation = window.getLocationOnScreen();
        Dimension obsSize = window.getSize();
        int x = obsLocation.x + (obsSize.width / 2) - (this.getSize().width / 2);
        int y = obsLocation.y + (obsSize.height / 2) - (this.getSize().height / 2);
        this.setLocation(x, y);

        this.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(this.ok)) {
            this.result = true; // Must be called first, as all later calls trigger the ChangeListener again...
            String selectedSE = (String) this.selector.getSelectedItem();

            // Map the selected result to target and finding (HashMap)
            this.selectedSchemaElementDialog = this.loadDialog(selectedSE);

            this.dispose();
        } else if (e.getSource().equals(this.cancel)) {
            this.cancel();
        }
    }

    public IDialog getDialog() {

        return this.selectedSchemaElementDialog;

    }

    public boolean getResult() {

        // As dialog might by canceled, we need to know if
        return this.result;

    }

    private void initFindingChooser() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        this.getContentPane().setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 2, 1, 100, 100);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.selector = new JComboBox();
        gridbag.setConstraints(this.selector, constraints);
        this.fillSelector();
        this.getContentPane().add(this.selector);

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 1, 1, 50, 100);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.ok = new JButton(this.bundle.getString("selector.button.select"));
        gridbag.setConstraints(this.ok, constraints);
        this.ok.addActionListener(this);
        this.getContentPane().add(this.ok);

        ConstraintsBuilder.buildConstraints(constraints, 1, 1, 1, 1, 50, 100);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.cancel = new JButton(this.bundle.getString("selector.button.cancel"));
        gridbag.setConstraints(this.cancel, constraints);
        this.cancel.addActionListener(this);
        this.getContentPane().add(this.cancel);

    }

    private void fillSelector() {

        String[] names = this.loader.getAllXSIDisplayNamesForCreation(this.schemaElementConstant);
        for (String name : names) {
            this.selector.addItem(name);
        }

    }

    private void cancel() {

        this.dispose();
        this.result = false;

    }

    private IDialog loadDialog(String name) {

        String type = this.loader.getTypeForDisplayName(name);

        if (SchemaElementConstants.TARGET == this.schemaElementConstant) {
            return this.loader.getTargetDialog(type, null, null);
        } else {
            return this.loader.getSchemaElementDialog(type, this.schemaElementConstant, null, true);
        }

    }

}
