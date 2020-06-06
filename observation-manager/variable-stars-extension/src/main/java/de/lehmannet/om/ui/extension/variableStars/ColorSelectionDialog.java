package de.lehmannet.om.ui.extension.variableStars;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableCellRenderer;

import de.lehmannet.om.IObservation;
import de.lehmannet.om.IObserver;
import de.lehmannet.om.ui.util.ConfigKey;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.IConfiguration;

public class ColorSelectionDialog extends JDialog implements ActionListener {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private final PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
            .getBundle("de.lehmannet.om.ui.extension.variableStars.VariableStar", Locale.getDefault());

    private IObservation[] observations = null;

    private JTable table = null;

    private JButton cancel = null;

    private Map<IObserver, Color> result = null;

    private final IConfiguration configuration;

    public ColorSelectionDialog(JFrame om, IConfiguration configuration, IObservation[] observations) {

        super(om);

        this.configuration = configuration;
        this.observations = observations;

        this.setTitle(this.bundle.getString("popup.observerColor.title"));
        this.setModal(true);

        this.setSize(550, 200);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(om);

        this.initDialog();

        this.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (this.cancel.equals(e.getSource())) { // Cancel pressed
            this.result = null;
        } else { // OK pressed
            this.result = this.createMap();
        }

        this.dispose();

    }

    private void initDialog() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        this.getContentPane().setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 2, 1, 100, 100);
        constraints.fill = GridBagConstraints.BOTH;
        Color defaultColor = null;
        // if (this.om.isNightVisionEnabled()) {
        // defaultColor = Color.DARK_GRAY;
        // } else {
        // defaultColor = Color.RED;
        // }
        this.table = new JTable(new ObserverColorTableModel(this.getObservers(), defaultColor));
        this.table.setToolTipText(this.bundle.getString("popup.observerColor.tooltip.table"));
        this.table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        this.table.setDefaultEditor(Color.class, new ColorEditor());
        this.table.setDefaultRenderer(Color.class, (table, value, isSelected, hasFocus, row, column) -> {

            DefaultTableCellRenderer cr = new DefaultTableCellRenderer();
            if (value != null) {
                cr.setBackground((Color) value);
            } else {
                cr.setText(ColorSelectionDialog.this.bundle.getString("popup.observerColor.noColorSelection"));
            }

            return cr;
        });
        JScrollPane scrollPane = new JScrollPane(this.table);
        gridbag.setConstraints(scrollPane, constraints);
        this.getContentPane().add(scrollPane);

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 1, 1, 50, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        JButton ok = new JButton(this.bundle.getString("popup.observerColor.button.ok"));
        ok.addActionListener(this);
        gridbag.setConstraints(ok, constraints);
        this.getContentPane().add(ok);

        ConstraintsBuilder.buildConstraints(constraints, 1, 1, 1, 1, 50, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.cancel = new JButton(this.bundle.getString("popup.observerColor.button.cancel"));
        this.cancel.addActionListener(this);
        gridbag.setConstraints(this.cancel, constraints);
        this.getContentPane().add(cancel);

    }

    public Map<IObserver, Color> getColorMap() {

        return this.result;

    }

    private Map<IObserver, Color> createMap() {

        ObserverColorTableModel model = (ObserverColorTableModel) this.table.getModel();

        return model.getResult();

    }

    private IObserver[] getObservers() {

        // Make sure we only show the observers, which contributed a observation
        List<IObserver> list = new ArrayList<>();
        for (IObservation observation : this.observations) {
            if (!list.contains(observation.getObserver())) {
                // Make sure the default observer is the top entry
                if (observation.getObserver().getDisplayName()
                        .equals(this.configuration.getConfig(ConfigKey.CONFIG_DEFAULT_OBSERVER))) {
                    list.add(0, observation.getObserver());
                } else {
                    list.add(observation.getObserver());
                }
            }
        }

        return (IObserver[]) list.toArray(new IObserver[] {});

    }

}