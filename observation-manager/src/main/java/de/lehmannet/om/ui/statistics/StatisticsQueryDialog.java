/* ====================================================================
 * /statistics/StatisticsQueryDialog.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.statistics;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import de.lehmannet.om.ui.catalog.ICatalog;
import de.lehmannet.om.ui.dialog.OMDialog;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.util.ConstraintsBuilder;

class StatisticsQueryDialog extends OMDialog implements ActionListener {

    private static final long serialVersionUID = -3368366919141412617L;

    private final PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
            .getBundle("ObservationManager", Locale.getDefault());

    private ICatalog[] catalogs = null;

    private final JButton cancel = new JButton(this.bundle.getString("dialog.button.cancel"));
    private final JButton ok = new JButton(this.bundle.getString("dialog.button.ok"));

    private JCheckBox[] catalogCheckBoxes = null;

    private ArrayList<ICatalog> result = null;

    public StatisticsQueryDialog(ObservationManager om) {

        super(om);
        super.setTitle(this.bundle.getString("dialog.statisticsQuery.title"));

        // Init catalogs and arrays
        String[] catalogNames = om.getExtensionLoader().getCatalogLoader().getListableCatalogNames();

        // Init projects
        ICatalog[] projectCatalogs = om.getProjects();

        // Calculate total amount of catalogs
        int catalogNumber = catalogNames.length;
        if (projectCatalogs != null) {
            catalogNumber = catalogNumber + projectCatalogs.length;
        }

        this.catalogs = new ICatalog[catalogNumber]; // All catalogs
        this.catalogCheckBoxes = new JCheckBox[catalogNumber]; // All catalog UI checkboxes
        this.result = new ArrayList<>(catalogNumber); // All selected checkboxes that are used to create stats

        // Load all "real" catalogs
        for (int i = 0; i < catalogNames.length; i++) {
            this.catalogs[i] = om.getExtensionLoader().getCatalogLoader().getCatalog(catalogNames[i]);
        }

        // Add project catalogs
        int x = 0;
        if (projectCatalogs != null) {
            for (int i = catalogNames.length; i < catalogNumber; i++) {
                this.catalogs[i] = projectCatalogs[x++];
            }
        }

        super.setSize(StatisticsQueryDialog.serialVersionUID, 540, 170);
        super.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        super.setLocationRelativeTo(om);

        // Try to set system default look and feel
        /*
         * try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
         * catch(UnsupportedLookAndFeelException lfe) { } catch(InstantiationException ie) { }
         * catch(IllegalAccessException iae) { } catch(ClassNotFoundException cnfe) { }
         */

        this.initDialog();

        super.pack();
        super.setVisible(true);

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
                for (int i = 0; i < this.catalogCheckBoxes.length; i++) {
                    if (this.catalogCheckBoxes[i].isSelected()) {
                        this.result.add(this.catalogs[i]);
                    }
                }
                this.dispose();
            }
        }

    }

    public List<ICatalog> getSelectedCatalogs() {

        return this.result;

    }

    private void initDialog() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.CENTER;
        super.getContentPane().setLayout(gridbag);

        // Set Header
        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 4, 1, 100, 1);
        constraints.fill = GridBagConstraints.CENTER;
        JLabel headerLabel = new JLabel(this.bundle.getString("dialog.statisticsQuery.header"));
        gridbag.setConstraints(headerLabel, constraints);
        super.getContentPane().add(headerLabel);
        constraints.fill = GridBagConstraints.HORIZONTAL;

        int i = 0;
        for (; i < this.catalogCheckBoxes.length; i++) {

            ConstraintsBuilder.buildConstraints(constraints, 1, i + 1, 3, 1, 100, 1);
            this.catalogCheckBoxes[i] = new JCheckBox(this.catalogs[i].getName(), true);
            this.catalogCheckBoxes[i].setFont(new Font("Arial", Font.ITALIC + Font.BOLD, 12));
            gridbag.setConstraints(this.catalogCheckBoxes[i], constraints);
            super.getContentPane().add(this.catalogCheckBoxes[i]);

        }

        i++;

        ConstraintsBuilder.buildConstraints(constraints, 0, i, 4, 1, 100, 1);
        constraints.fill = GridBagConstraints.CENTER;
        JLabel note = new JLabel(this.bundle.getString("dialog.statisticsQuery.note"));
        note.setFont(new Font("Arial", Font.ITALIC, 10));
        gridbag.setConstraints(note, constraints);
        super.getContentPane().add(note);

        i++;

        ConstraintsBuilder.buildConstraints(constraints, 0, i, 2, 1, 50, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(this.ok, constraints);
        this.ok.addActionListener(this);
        super.getContentPane().add(this.ok);

        ConstraintsBuilder.buildConstraints(constraints, 2, i, 2, 1, 50, 1);
        gridbag.setConstraints(this.cancel, constraints);
        this.cancel.addActionListener(this);
        super.getContentPane().add(this.cancel);

    }

}