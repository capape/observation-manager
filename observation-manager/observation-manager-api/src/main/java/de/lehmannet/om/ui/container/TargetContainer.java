/* ====================================================================
 * /container/TargetContainer.java
 *
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.container;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import de.lehmannet.om.Constellation;
import de.lehmannet.om.EquPosition;
import de.lehmannet.om.IObserver;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.box.ConstellationBox;
import de.lehmannet.om.ui.box.ObserverBox;
import de.lehmannet.om.ui.util.ConfigKey;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.EditPopupHandler;
import de.lehmannet.om.ui.util.IConfiguration;
import de.lehmannet.om.ui.util.LocaleToolsFactory;
import de.lehmannet.om.ui.util.OMLabel;
import de.lehmannet.om.util.AtlasUtil;

public class TargetContainer extends Container implements MouseListener {

    private static final long serialVersionUID = -9052717626196198395L;

    private final ResourceBundle bundle = LocaleToolsFactory.appInstance().getBundle("ObservationManager",
            Locale.getDefault());

    private ITarget target = null;

    private boolean editable = false;
    private boolean positionDisabled = false;

    private JTextField targetName = null;
    private JTextField targetAliasNames = null;
    private EquPositionContainer equPosition = null;
    private final JTextField targetConstellation = new JTextField();
    private ConstellationBox constellationBox = null;
    private JTextField targetDatasource = null;
    private ObserverBox sourceObserverBox = null;
    private JTextArea notes = null;

    private final ObservationManagerModel model;
    private final IConfiguration configuration;

    public TargetContainer(IConfiguration configuration, ObservationManagerModel model, ITarget target,
            boolean editable, boolean positionDisabled) {

        this.target = target;
        this.model = model;
        this.editable = editable;
        this.positionDisabled = positionDisabled;
        this.configuration = configuration;

        this.constellationBox = new ConstellationBox(
                Boolean.parseBoolean(configuration.getConfig(ConfigKey.CONFIG_CONSTELLATION_USEI18N, "true")));

        this.createPanel();

        if (target != null) {
            this.loadSchemaElement();
        }

    }

    public ITarget updateTarget() {

        String name = this.getName();
        if ((name == null) || ("".equals(name.trim()))) {
            this.createWarning(this.bundle.getString("target.warning.noName"));
            return null;
        }
        target.setName(name);

        IObserver observer = this.getObserver();
        if (observer != null) {
            target.setObserver(observer);
        } else {
            this.createWarning(this.bundle.getString("target.warning.noObserver"));
            return null;
        }

        // Optional parameters
        String[] aliasNames = this.getAliasNames();
        target.setAliasNames(aliasNames);

        EquPosition pos = this.getPosition();
        /*
         * if( pos == null ) { this.createWarning(TargetContainer.bundle.getString( "target.warning.posMalformed"));
         * return null; }
         */
        target.setPosition(pos);

        Constellation constellation = this.getConstellation();
        /*
         * if( (constellation != null) && !("".equals(constellation)) ) {
         */
        target.setConstellation(constellation);
        // }

        target.setNotes(this.notes.getText().trim());

        return target;

    }

    @Override
    public void mouseClicked(MouseEvent e) {

        // Check only button
        // Source component is always the JTextArea
        if (e.getButton() == MouseEvent.BUTTON3) {
            new EditPopupHandler(e.getX(), e.getY(), this.notes);
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

    public String getDatasource() {

        return targetDatasource.getText();

    }

    public IObserver getObserver() {

        return (IObserver) this.sourceObserverBox.getSelectedSchemaElement();

    }

    public Constellation getConstellation() {

        if (!this.editable) { // Show
            String c = this.targetConstellation.getText();
            return Constellation.getConstellationByAbbOrName(c);
        } else { // Create, edit
            return this.constellationBox.getSelectedConstellation();
        }

    }

    public EquPosition getPosition() {

        return this.equPosition.getEquPosition();

    }

    public void setPosition(EquPosition equPos) {

        this.equPosition.setEquPosition(equPos);

    }

    public void setTarget(ITarget target) {

        this.target = target;

    }

    @Override
    public String getName() {

        return this.targetName.getText();

    }

    public String getNotes() {

        return this.notes.getText().trim();

    }

    public String[] getAliasNames() {

        List<String> list = new ArrayList<>();

        String aliasNames = this.targetAliasNames.getText();
        if ((aliasNames != null) && !("".equals(aliasNames))) {
            StringTokenizer tokenizer = new StringTokenizer(aliasNames, ";");
            while (tokenizer.hasMoreTokens()) {
                list.add(tokenizer.nextToken().trim());
            }

            return (String[]) list.toArray(new String[] {});
        } else {
            return null;
        }

    }

    public boolean checkOrigin(String datasource, IObserver observer) {

        // Both cannot be null
        if (((datasource == null) || ("".equals(datasource))) && (observer == null)) {
            this.createWarning(this.bundle.getString("target.warning.datasourceOrObserver"));
            return false;
        }

        // Both cannot be set
        if (((datasource != null) && !("".equals(datasource))) && (observer != null)) {
            this.createWarning(this.bundle.getString("target.warning.datasourceOrObserverBoth"));
            return false;
        }

        return true;

    }

    private void loadSchemaElement() {

        this.targetName.setText(this.target.getDisplayName());
        this.targetName.setEditable(this.editable);

        String[] aliasNames = target.getAliasNames();
        StringBuilder buffer = new StringBuilder();
        if (aliasNames != null) {
            for (int i = 0; i < aliasNames.length; i++) {
                buffer.append(aliasNames[i]);
                if (i < aliasNames.length - 1) {
                    buffer.append("; ");
                }
            }
            this.targetAliasNames.setText(buffer.toString());
        }
        this.targetAliasNames.setEditable(this.editable);

        Constellation c = this.target.getConstellation();
        if (c != null) {
            this.constellationBox.setSelectedConstellation(c);
            String cName = null;
            boolean i18N = Boolean
                    .parseBoolean(this.configuration.getConfig(ConfigKey.CONFIG_CONSTELLATION_USEI18N, "true"));
            if (i18N) {
                cName = c.getDisplayName();
            } else {
                cName = c.getName();
            }
            this.targetConstellation.setText(cName);

            /*
             * if( cName != null ) { this.targetConstellation.setText(cName); } else {
             * this.targetConstellation.setText(c.getName()); }
             */
        }

        if (this.positionDisabled) {
            this.targetConstellation.setEditable(false);
        } else {
            this.targetConstellation.setEditable(this.editable);
        }

        if (this.target.getPosition() != null) {
            this.equPosition.setEquPosition(this.target.getPosition());
        }
        if (this.positionDisabled) {
            this.equPosition.setEditable(false);
        } else {
            this.equPosition.setEditable(this.editable);
        }

        // Origin of target can be an observer, or an external datasource (catalogue)
        if ((this.target.getDatasource() != null) && !("".equals(target.getDatasource()))) {
            this.targetDatasource.setText(this.target.getDatasource());
            this.targetDatasource.setEditable(this.editable);
        } else {
            if (!this.editable) {
                IObserver observer = this.target.getObserver();
                this.targetDatasource.setText(observer.getDisplayName());
                this.targetDatasource.setEditable(this.editable);
            }
        }

        if (this.target.getNotes() != null) {
            this.notes.setText(this.target.getNotes());
        }
        this.notes.setEditable(this.editable);

    }

    private void createPanel() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        this.setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 1, 1, 5, 1);
        OMLabel LtargetName = new OMLabel(this.bundle.getString("target.label.name"), true);
        LtargetName.setToolTipText(this.bundle.getString("target.tooltip.name"));
        gridbag.setConstraints(LtargetName, constraints);
        this.add(LtargetName);
        ConstraintsBuilder.buildConstraints(constraints, 1, 0, 1, 1, 45, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.targetName = new JTextField();
        this.targetName.setEditable(this.editable);
        this.targetName.setToolTipText(this.bundle.getString("target.tooltip.name"));
        gridbag.setConstraints(this.targetName, constraints);
        this.add(this.targetName);

        ConstraintsBuilder.buildConstraints(constraints, 2, 0, 1, 1, 5, 1);
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.EAST;
        OMLabel LAliasName = new OMLabel(this.bundle.getString("target.label.aliasNames"), SwingConstants.RIGHT, false);
        LAliasName.setToolTipText(this.bundle.getString("target.tooltip.aliasNames"));
        gridbag.setConstraints(LAliasName, constraints);
        this.add(LAliasName);
        ConstraintsBuilder.buildConstraints(constraints, 3, 0, 5, 1, 45, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        this.targetAliasNames = new JTextField();
        this.targetAliasNames.setToolTipText(this.bundle.getString("target.tooltip.aliasNames"));
        this.targetAliasNames.setEditable(this.editable);
        gridbag.setConstraints(this.targetAliasNames, constraints);
        this.add(this.targetAliasNames);

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 2, 3, 100, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.equPosition = new EquPositionContainer(null, this.editable);
        gridbag.setConstraints(this.equPosition, constraints);
        if (this.positionDisabled) {
            this.equPosition.setEditable(false);
        } else {
            this.equPosition.setEditable(this.editable);
        }
        this.add(this.equPosition);

        ConstraintsBuilder.buildConstraints(constraints, 0, 4, 1, 1, 4, 1);
        OMLabel LConstallation = new OMLabel(this.bundle.getString("target.label.constellation"), false);
        LConstallation.setToolTipText(this.bundle.getString("target.tooltip.constellation"));
        gridbag.setConstraints(LConstallation, constraints);
        this.add(LConstallation);

        JLabel targetDatasourceLabel = null;
        if ((this.target != null) // Show
                && !(this.editable)) {
            ConstraintsBuilder.buildConstraints(constraints, 1, 4, 1, 1, 45, 1);
            constraints.fill = GridBagConstraints.HORIZONTAL;
            this.targetConstellation.setToolTipText(this.bundle.getString("target.tooltip.constellation"));
            this.targetConstellation.setEditable(this.editable);
            gridbag.setConstraints(this.targetConstellation, constraints);
            this.add(this.targetConstellation);

            targetDatasourceLabel = new OMLabel(this.bundle.getString("target.label.datasource"), SwingConstants.RIGHT,
                    true);
            targetDatasourceLabel.setToolTipText(this.bundle.getString("target.tooltip.datasource"));
            ConstraintsBuilder.buildConstraints(constraints, 2, 4, 1, 1, 5, 1);
            constraints.fill = GridBagConstraints.NONE;
            constraints.anchor = GridBagConstraints.EAST;
            gridbag.setConstraints(targetDatasourceLabel, constraints);
            this.targetDatasource = new JTextField();
            this.targetDatasource.setToolTipText(this.bundle.getString("target.tooltip.datasource"));
            this.targetDatasource.setEditable(this.editable);
            this.add(targetDatasourceLabel);
            this.createObserverDropDownBox(); // Make sure this is not NULL
            ConstraintsBuilder.buildConstraints(constraints, 3, 4, 5, 1, 45, 1);
            constraints.fill = GridBagConstraints.HORIZONTAL;
            gridbag.setConstraints(targetDatasource, constraints);
            this.add(targetDatasource);

            ConstraintsBuilder.buildConstraints(constraints, 0, 5, 1, 1, 100, 1);
            OMLabel Lnotes = new OMLabel(this.bundle.getString("target.label.notes"), false);
            Lnotes.setToolTipText(this.bundle.getString("target.tooltip.notes"));
            gridbag.setConstraints(Lnotes, constraints);
            this.add(Lnotes);
            ConstraintsBuilder.buildConstraints(constraints, 0, 6, 8, 2, 100, 45);
            constraints.fill = GridBagConstraints.HORIZONTAL;
            this.notes = new JTextArea(10, 40);
            this.notes.setToolTipText(this.bundle.getString("target.tooltip.notes"));
            this.notes.setEditable(this.editable);
            this.notes.setLineWrap(true);
            this.notes.addMouseListener(this);
            if (this.target != null) {
                this.notes.setText(this.target.getNotes());
            }
            // if (!this.editable) {
            // if (this.observationManager.isNightVisionEnabled()) {
            // this.notes.setBackground(new Color(255, 175, 175));
            // } else {
            // this.notes.setBackground(Color.WHITE);
            // }
            // }
            JScrollPane descriptionScroll = new JScrollPane(this.notes);
            descriptionScroll.setMinimumSize(new Dimension(300, 60));
            gridbag.setConstraints(descriptionScroll, constraints);
            this.add(descriptionScroll);

            EquPosition pos = this.target.getPosition();
            if (pos != null) { // Target might not have position (e.g. SolarSystem Targets)

                JLabel LAtlas = new JLabel(this.bundle.getString("target.label.atlas"));
                ConstraintsBuilder.buildConstraints(constraints, 0, 8, 8, 1, 100, 1);
                gridbag.setConstraints(LAtlas, constraints);
                this.add(LAtlas);

                JLabel LUranometeria = new JLabel(this.bundle.getString("target.label.uranometeria"));
                JTextField uranometeria = new JTextField("" + AtlasUtil.getUranometriaPage(pos), 4);
                uranometeria.setEditable(false);
                ConstraintsBuilder.buildConstraints(constraints, 0, 9, 1, 1, 45, 1);
                constraints.fill = GridBagConstraints.NONE;
                constraints.anchor = GridBagConstraints.EAST;
                gridbag.setConstraints(LUranometeria, constraints);
                this.add(LUranometeria);
                ConstraintsBuilder.buildConstraints(constraints, 1, 9, 1, 1, 45, 1);
                constraints.fill = GridBagConstraints.HORIZONTAL;
                gridbag.setConstraints(uranometeria, constraints);
                this.add(uranometeria);

                JLabel LUranometeria2 = new JLabel(this.bundle.getString("target.label.uranometeria2"));
                JTextField uranometeria2 = new JTextField("" + AtlasUtil.getUranometria2000Page(pos), 4);
                uranometeria2.setEditable(false);
                ConstraintsBuilder.buildConstraints(constraints, 2, 9, 1, 1, 45, 1);
                constraints.fill = GridBagConstraints.NONE;
                constraints.anchor = GridBagConstraints.EAST;
                gridbag.setConstraints(LUranometeria2, constraints);
                this.add(LUranometeria2);
                ConstraintsBuilder.buildConstraints(constraints, 3, 9, 1, 1, 45, 1);
                constraints.fill = GridBagConstraints.HORIZONTAL;
                gridbag.setConstraints(uranometeria2, constraints);
                this.add(uranometeria2);

                JLabel LMillenium = new JLabel(this.bundle.getString("target.label.milleniumStarAtlas"));
                JTextField millenium = new JTextField("" + AtlasUtil.getMilleniumStarAtlasPage(pos), 4);
                millenium.setEditable(false);
                ConstraintsBuilder.buildConstraints(constraints, 4, 9, 1, 1, 45, 1);
                constraints.fill = GridBagConstraints.NONE;
                constraints.anchor = GridBagConstraints.EAST;
                gridbag.setConstraints(LMillenium, constraints);
                this.add(LMillenium);
                ConstraintsBuilder.buildConstraints(constraints, 5, 9, 1, 1, 45, 1);
                constraints.fill = GridBagConstraints.HORIZONTAL;
                gridbag.setConstraints(millenium, constraints);
                this.add(millenium);

                JLabel LStarAtlas200 = new JLabel(this.bundle.getString("target.label.skyAtlas2000"));
                JTextField startAtlas = new JTextField("" + AtlasUtil.getSkyAtlas2000Page(pos), 4);
                startAtlas.setEditable(false);
                ConstraintsBuilder.buildConstraints(constraints, 6, 9, 1, 1, 45, 1);
                constraints.fill = GridBagConstraints.NONE;
                constraints.anchor = GridBagConstraints.EAST;
                gridbag.setConstraints(LStarAtlas200, constraints);
                this.add(LStarAtlas200);
                ConstraintsBuilder.buildConstraints(constraints, 7, 9, 1, 1, 45, 1);
                constraints.fill = GridBagConstraints.HORIZONTAL;
                gridbag.setConstraints(startAtlas, constraints);
                this.add(startAtlas);
            }

        } else {
            // Create Edit (Datasource cannot be created/changed by user)
            // New Targets always have a observer as source
            ConstraintsBuilder.buildConstraints(constraints, 1, 4, 1, 1, 45, 1);
            this.constellationBox.setToolTipText(this.bundle.getString("target.tooltip.constellation"));
            gridbag.setConstraints(this.constellationBox, constraints);
            if (this.positionDisabled) {
                this.constellationBox.setEnabled(false);
            } else {
                this.constellationBox.setEnabled(this.editable);
            }
            this.add(this.constellationBox);

            targetDatasourceLabel = new OMLabel(this.bundle.getString("target.label.datasourceObserver"),
                    SwingConstants.RIGHT, true);
            targetDatasourceLabel.setToolTipText(this.bundle.getString("target.tooltip.datasourceObserver"));
            ConstraintsBuilder.buildConstraints(constraints, 2, 4, 1, 1, 5, 1);
            gridbag.setConstraints(targetDatasourceLabel, constraints);
            this.add(targetDatasourceLabel);
            this.targetDatasource = new JTextField(); // make sure this is not NULL
            this.targetDatasource.setToolTipText(this.bundle.getString("target.tooltip.datasourceObserver"));
            this.targetDatasource.setEditable(this.editable);
            ConstraintsBuilder.buildConstraints(constraints, 3, 4, 1, 1, 45, 1);
            this.createObserverDropDownBox();
            gridbag.setConstraints(sourceObserverBox, constraints);
            this.add(sourceObserverBox);

            ConstraintsBuilder.buildConstraints(constraints, 0, 5, 1, 1, 100, 1);
            OMLabel Lnotes = new OMLabel(this.bundle.getString("target.label.notes"), false);
            Lnotes.setToolTipText(this.bundle.getString("target.tooltip.notes"));
            gridbag.setConstraints(Lnotes, constraints);
            this.add(Lnotes);
            ConstraintsBuilder.buildConstraints(constraints, 0, 6, 8, 3, 100, 45);
            constraints.fill = GridBagConstraints.HORIZONTAL;
            this.notes = new JTextArea(10, 40);
            this.notes.setToolTipText(this.bundle.getString("target.tooltip.notes"));
            this.notes.setEditable(this.editable);
            this.notes.setLineWrap(true);
            this.notes.addMouseListener(this);
            if (this.target != null) {
                this.notes.setText(this.target.getNotes());
            }
            if (!this.editable) {
                this.notes.setBackground(Color.WHITE);
            }
            JScrollPane descriptionScroll = new JScrollPane(this.notes);
            descriptionScroll.setMinimumSize(new Dimension(300, 60));
            gridbag.setConstraints(descriptionScroll, constraints);
            this.add(descriptionScroll);
        }

    }

    private void createObserverDropDownBox() {

        this.sourceObserverBox = new ObserverBox();
        this.sourceObserverBox.setToolTipText(this.bundle.getString("target.dropdown.selectObserver"));

        IObserver[] observer = this.model.getObservers();
        if (observer != null) {
            for (IObserver iObserver : observer) {
                this.sourceObserverBox.addItem(iObserver);
            }
        } else {
            this.sourceObserverBox.addEmptyItem();
        }

        if ((this.target != null) && (this.target.getObserver() != null)) {
            this.sourceObserverBox.setSelectedItem(this.target.getObserver());
        } else {
            this.sourceObserverBox.setSelectedItem(null);
        }
    }

    private void createWarning(String message) {

        JOptionPane.showMessageDialog(this, message, this.bundle.getString("target.warning.title"),
                JOptionPane.WARNING_MESSAGE);

    }

}
