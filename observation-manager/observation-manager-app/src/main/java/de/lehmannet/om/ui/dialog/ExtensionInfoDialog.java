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
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import de.lehmannet.om.ui.extension.IExtension;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.LocaleToolsFactory;

public class ExtensionInfoDialog extends OMDialog implements ActionListener {

    private static final long serialVersionUID = 3369603577422579950L;

    private final ResourceBundle bundle = LocaleToolsFactory.appInstance().getBundle("ObservationManager",
            Locale.getDefault());

    private final JButton close = new JButton(this.bundle.getString("extensionInfo.button.close"));

    private ObservationManager om = null;

    public ExtensionInfoDialog(ObservationManager om) {

        super(om);

        this.om = om;

        this.setTitle(this.bundle.getString("extensionInfo.title"));
        this.setSize(ExtensionInfoDialog.serialVersionUID, 390, 180);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(om);

        // Try to set system default look and feel
        /*
         * try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
         * catch(UnsupportedLookAndFeelException lfe) { } catch(InstantiationException ie) { }
         * catch(IllegalAccessException iae) { } catch(ClassNotFoundException cnfe) { }
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
        this.getContentPane().setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 1, 5, 1, 99);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.BOTH;
        JTable infoTable = new JTable(new ExtensionTableModel(this.om.getExtensionLoader().getExtensions()));
        infoTable.setRowSelectionAllowed(false);
        TableColumn col0 = infoTable.getColumnModel().getColumn(0);
        TableColumn col1 = infoTable.getColumnModel().getColumn(1);
        col0.setPreferredWidth((int) (col0.getWidth() + col1.getWidth() / 1.5));

        JScrollPane scrollPane = new JScrollPane(infoTable);
        gridbag.setConstraints(scrollPane, constraints);
        this.getContentPane().add(scrollPane);

        ConstraintsBuilder.buildConstraints(constraints, 0, 5, 1, 1, 1, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.close.addActionListener(this);
        gridbag.setConstraints(this.close, constraints);
        this.getContentPane().add(this.close);

    }

}

class ExtensionTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 7980803807760340818L;

    private final ResourceBundle bundle = LocaleToolsFactory.appInstance().getBundle("ObservationManager",
            Locale.getDefault());

    private List<IExtension> extensions = null;

    public ExtensionTableModel(List<IExtension> extensions) {

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
        case 0:
            return ((IExtension) this.extensions.get(rowIndex)).getName();

        case 1: {
            return "" + ((IExtension) this.extensions.get(rowIndex)).getVersion();
        }
        default:
            return "";
        }

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
        default:
            name = "";
        }

        return name;

    }

}
