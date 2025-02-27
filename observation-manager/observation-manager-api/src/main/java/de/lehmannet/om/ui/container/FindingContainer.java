/*
 * ====================================================================
 * /container/FindingContainer.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.container;

import de.lehmannet.om.IFinding;
import de.lehmannet.om.ISession;
import de.lehmannet.om.ui.box.LanguageBox;
import de.lehmannet.om.ui.util.ConfigKey;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.EditPopupHandler;
import de.lehmannet.om.ui.util.IConfiguration;
import de.lehmannet.om.ui.util.LocaleToolsFactory;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class FindingContainer extends Container implements MouseListener {

    /**
     *
     */
    private static final long serialVersionUID = 6234708970033440904L;

    private final ResourceBundle bundle =
            LocaleToolsFactory.appInstance().getBundle("ObservationManager", Locale.getDefault());

    private IFinding finding = null;
    private ISession session = null;
    private boolean editable = false;

    private JTextArea description = null;

    private LanguageBox language = null;
    private final IConfiguration configuration;

    public FindingContainer(IConfiguration configuration, IFinding finding, ISession session, boolean editable) {

        this.finding = finding;
        this.session = session;
        this.editable = editable;
        this.configuration = configuration;

        this.language = new LanguageBox(this.configuration.getConfig(ConfigKey.CONFIG_CONTENTDEFAULTLANG), true);

        this.createContainer();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        // Check only button
        // Source component is always the JTextArea
        if (e.getButton() == MouseEvent.BUTTON3) {
            new EditPopupHandler(e.getX(), e.getY(), this.description);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // Do nothing
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // Do nothing
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // Do nothing
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // Do nothing
    }

    public String getDescription() {

        return this.description.getText();
    }

    public String getLanguage() {

        return this.language.getSelectedISOLanguage();
    }

    private void createContainer() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.BOTH;
        this.setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 1, 1, 20, 1);
        JLabel Ldescription = new JLabel(this.bundle.getString("finding.label.description"));
        Ldescription.setToolTipText(this.bundle.getString("finding.tooltip.description"));
        gridbag.setConstraints(Ldescription, constraints);
        this.add(Ldescription);
        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 4, 1, 100, 97);
        this.description = new JTextArea(10, 40);
        this.description.setToolTipText(this.bundle.getString("finding.tooltip.description"));
        this.description.setEditable(this.editable);
        this.description.setLineWrap(true);
        this.description.addMouseListener(this);
        if (this.finding != null) {
            this.description.setText(this.finding.getDescription());
        }
        // if (!this.editable) {
        // if (this.om.isNightVisionEnabled()) {
        // this.description.setBackground(new Color(255, 175, 175));
        // } else {
        // this.description.setBackground(Color.WHITE);
        // }
        // }
        JScrollPane descriptionScroll = new JScrollPane(this.description);
        descriptionScroll.setMinimumSize(new Dimension(300, 60));
        gridbag.setConstraints(descriptionScroll, constraints);
        this.add(descriptionScroll);

        ConstraintsBuilder.buildConstraints(constraints, 1, 0, 1, 1, 10, 1);
        constraints.anchor = GridBagConstraints.CENTER;
        JLabel Llanguage = new JLabel(this.bundle.getString("panel.session.label.language"), SwingConstants.RIGHT);
        Llanguage.setToolTipText(this.bundle.getString("panel.session.tooltip.language"));
        gridbag.setConstraints(Llanguage, constraints);
        Llanguage.setFont(new Font("sansserif", Font.ITALIC + Font.BOLD, 12));
        this.add(Llanguage);
        ConstraintsBuilder.buildConstraints(constraints, 2, 0, 2, 1, 70, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.language.setToolTipText(this.bundle.getString("panel.session.tooltip.language"));
        this.language.setEnabled(this.editable);
        if (this.finding != null) {
            this.language.setLanguage(this.finding.getLanguage());
        } else {
            if ((this.session != null) // If session laguage is set, prefer the session language in finding
                    && (this.session.getLanguage() != null)) {
                this.language.setLanguage(this.session.getLanguage());
            }
        }
        gridbag.setConstraints(this.language, constraints);
        this.add(this.language);
    }
}
