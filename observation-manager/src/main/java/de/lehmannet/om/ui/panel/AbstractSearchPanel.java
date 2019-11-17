package de.lehmannet.om.ui.panel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.OMLabel;

public abstract class AbstractSearchPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = -1182026656125311590L;

    private final PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
            .getBundle("ObservationManager", Locale.getDefault());

    protected ISchemaElement searchResult = null;

    protected JTextField searchText = null;
    private final OMLabel searchFailed = new OMLabel(true);
    private final OMLabel generalInfoText = new OMLabel(false);
    private String infoText = "";
    private JButton search = null;
    private JButton cancel = null;

    protected AbstractSearchPanel() {

        this.search = new JButton();

        this.cancel = new JButton();

        this.searchText = new JTextField();

    }

    public ISchemaElement getSearchResult() {

        return this.searchResult;

    }

    public abstract void search(String searchString);

    public JButton getDefaultButton() {

        return this.search;

    }

    public void setGeneralInfoText(String info) {

        this.infoText = info;
        this.generalInfoText.setText(this.infoText);
        this.generalInfoText.setToolTipText(this.infoText);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();
        if (source instanceof JButton) {
            if (source.equals(this.search)) {
                this.search(this.searchText.getText());
                if (this.searchResult != null) { // Search was successful. Close dialog
                    this.processComponentEvent(new ComponentEvent(this, ComponentEvent.COMPONENT_HIDDEN));
                } else {
                    this.searchFailed.setText(this.bundle.getString("panel.search.label.searchFailed"));
                }
            } else if (source.equals(this.cancel)) {
                this.searchResult = null; // Make sure there's nothing to return
                this.processComponentEvent(new ComponentEvent(this, ComponentEvent.COMPONENT_HIDDEN));
            }
        }

    }

    protected void createPanel() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        this.setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 4, 1, 55, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.generalInfoText.setText(this.infoText);
        this.generalInfoText.setToolTipText(this.infoText);
        this.generalInfoText.setHorizontalAlignment(SwingConstants.CENTER);
        gridbag.setConstraints(this.generalInfoText, constraints);
        this.add(this.generalInfoText);

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 1, 1, 5, 1);
        constraints.anchor = GridBagConstraints.WEST;
        OMLabel LsearchString = new OMLabel(this.bundle.getString("panel.search.label.searchField"), true);
        LsearchString.setToolTipText(this.bundle.getString("panel.search.tooltip.searchField"));
        gridbag.setConstraints(LsearchString, constraints);
        this.add(LsearchString);
        ConstraintsBuilder.buildConstraints(constraints, 1, 1, 3, 1, 50, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.searchText.setToolTipText(this.bundle.getString("panel.search.tooltip.searchField"));
        gridbag.setConstraints(this.searchText, constraints);
        this.add(this.searchText);

        ConstraintsBuilder.buildConstraints(constraints, 0, 2, 4, 1, 55, 1);
        this.searchFailed.setForeground(Color.RED);
        this.searchFailed.setHorizontalAlignment(SwingConstants.CENTER);
        gridbag.setConstraints(this.searchFailed, constraints);
        this.add(this.searchFailed);

        ConstraintsBuilder.buildConstraints(constraints, 0, 3, 2, 1, 27, 1);
        this.search.setText(this.bundle.getString("panel.search.label.searchButton"));
        this.search.setToolTipText(this.bundle.getString("panel.search.tooltip.searchButton"));
        this.search.addActionListener(this);
        gridbag.setConstraints(this.search, constraints);
        this.add(this.search);

        ConstraintsBuilder.buildConstraints(constraints, 2, 3, 2, 1, 27, 1);
        this.cancel.setText(this.bundle.getString("panel.search.label.cancelButton"));
        this.cancel.setToolTipText(this.bundle.getString("panel.search.tooltip.cancelButton"));
        this.cancel.addActionListener(this);
        gridbag.setConstraints(this.cancel, constraints);
        this.add(this.cancel);

    }

    String formatName(String name) {

        // name = name.trim();
        name = name.toUpperCase();
        name = name.replaceAll(" ", "");

        return name;

    }

}
