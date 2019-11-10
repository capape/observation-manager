/* ====================================================================
 * /dialog/ExtensionInfoDialog.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import de.lehmannet.om.ui.extension.IExtension;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.util.ConstraintsBuilder;

public class ExtensionInfoDialog extends OMDialog implements ActionListener {

    private static final long serialVersionUID = 3369603577422579950L;

    final PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle.getBundle("ObservationManager",
            Locale.getDefault());

    private JButton close = new JButton(this.bundle.getString("extensionInfo.button.close"));
    private JTable infoTable = null;

    private ObservationManager om = null;

    public ExtensionInfoDialog(ObservationManager om) {

        super(om);

        this.om = om;

        super.setTitle(this.bundle.getString("extensionInfo.title"));
        super.setSize(ExtensionInfoDialog.serialVersionUID, 390, 180);
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
        // this.pack();

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
        this.infoTable = new JTable(new ExtensionTableModel(this.om.getExtensionLoader().getExtensions()));
        this.infoTable.setRowSelectionAllowed(false);
        TableColumn col0 = this.infoTable.getColumnModel().getColumn(0);
        TableColumn col1 = this.infoTable.getColumnModel().getColumn(1);
        col0.setPreferredWidth((int) (col0.getWidth() + col1.getWidth() / 1.5));

        JScrollPane scrollPane = new JScrollPane(this.infoTable);
        gridbag.setConstraints(scrollPane, constraints);
        super.getContentPane().add(scrollPane);

        ConstraintsBuilder.buildConstraints(constraints, 0, 5, 1, 1, 1, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.close.addActionListener(this);
        gridbag.setConstraints(this.close, constraints);
        super.getContentPane().add(this.close);

    }

}

class ExtensionTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 7980803807760340818L;

    private PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle.getBundle("ObservationManager",
            Locale.getDefault());

    private List extensions = null;

    public ExtensionTableModel(List extensions) {

        this.extensions = extensions;

    }

    @Override
    public int getColumnCount() {

        return 2;

    }

    @Override
    public int getRowCount() {

        if ((this.extensions == null) || (this.extensions.isEmpty())) {
            return 5;
        }

        return this.extensions.size();

    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        switch (columnIndex) {
        case 0: {
            return ((IExtension) this.extensions.get(rowIndex)).getName();
        }
        case 1: {
            return "" + ((IExtension) this.extensions.get(rowIndex)).getVersion();
        }
        }

        return "";

    }

    @Override
    public String getColumnName(int column) {

        String name = "";

        switch (column) {
        case 0: {
            name = this.bundle.getString("extensionInfo.column.extensionName");
            break;
        }
        case 1: {
            name = this.bundle.getString("extensionInfo.column.version");
            break;
        }
        }

        return name;

    }

}
