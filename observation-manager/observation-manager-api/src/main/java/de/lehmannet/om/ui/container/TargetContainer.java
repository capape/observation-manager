/*
 * ====================================================================
 * /container/TargetContainer.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.container;

import de.lehmannet.om.Constellation;
import de.lehmannet.om.EquPosition;
import de.lehmannet.om.IObserver;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.box.ConstellationBox;
import de.lehmannet.om.ui.box.OMComboBox;
import de.lehmannet.om.ui.util.ConfigKey;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.EditPopupHandler;
import de.lehmannet.om.ui.util.IConfiguration;
import de.lehmannet.om.ui.util.LocaleToolsFactory;
import de.lehmannet.om.ui.util.OMLabel;
import de.lehmannet.om.util.AtlasUtil;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import org.apache.commons.lang3.StringUtils;

public class TargetContainer extends Container implements MouseListener {

    private static final long serialVersionUID = -9052717626196198395L;

    private final ResourceBundle bundle =
            LocaleToolsFactory.appInstance().getBundle("ObservationManager", Locale.getDefault());

    private ITarget target = null;

    private boolean editable = false;
    private boolean positionDisabled = false;

    private JTextField targetName = null;
    private JTextField targetAliasNames = null;
    private EquPositionContainer equPosition = null;
    private final JTextField targetConstellation = new JTextField();
    private ConstellationBox constellationBox = null;
    private JTextField targetDatasource = null;
    private OMComboBox<IObserver> sourceObserverBox = null;
    private JTextArea notes = null;

    private final ObservationManagerModel model;
    private final IConfiguration configuration;

    public TargetContainer(
            IConfiguration configuration,
            ObservationManagerModel model,
            ITarget target,
            boolean editable,
            boolean positionDisabled) {

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
        if (StringUtils.isBlank(name)) {
            this.createWarning(this.bundle.getString("target.warning.noName"));
            return null;
        }
        target.setName(name);

        if (this.targetDatasource.isEnabled()) {
            if (StringUtils.isBlank(this.getDatasource())) {
                this.createWarning(this.bundle.getString("target.warning.datasourceOrObserver"));
                return null;
            }
            target.setDatasource(this.getDatasource());
            target.setObserver(null);

        } else {
            IObserver observer = this.getObserver();
            if (observer != null) {
                target.setObserver(observer);
                target.setDatasource(null);

            } else {
                this.createWarning(this.bundle.getString("target.warning.datasourceOrObserver"));
                return null;
            }
        }

        // Optional parameters
        String[] aliasNames = this.getAliasNames();
        target.setAliasNames(aliasNames);

        EquPosition pos = this.getPosition();
        target.setPosition(pos);

        Constellation constellation = this.getConstellation();
        target.setConstellation(constellation);

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

        return this.sourceObserverBox.getSelectedSchemaElement();
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
        if (aliasNames != null && !"".equals(aliasNames)) {
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
        if (StringUtils.isBlank(datasource) && observer == null) {
            this.createWarning(this.bundle.getString("target.warning.datasourceOrObserver"));
            return false;
        }

        // Both cannot be set
        if (StringUtils.isNotBlank(datasource) && observer != null) {
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
            boolean i18N =
                    Boolean.parseBoolean(this.configuration.getConfig(ConfigKey.CONFIG_CONSTELLATION_USEI18N, "true"));
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
        if (StringUtils.isNotBlank(this.target.getDatasource())) {
            this.targetDatasource.setText(this.target.getDatasource());
            this.targetDatasource.setEditable(this.editable);
            this.targetDatasource.setEnabled(true);
            this.sourceObserverBox.setEnabled(false);

            if (isFromCatalog(this.target)) {
                this.targetDatasource.setEditable(false);
                // this.targetDatasource.setEnabled(false);
            }

        } else {
            if (!this.editable) {
                IObserver observer = this.target.getObserver();
                this.targetDatasource.setText(observer.getDisplayName());
                this.targetDatasource.setEditable(this.editable);
                this.targetDatasource.setEnabled(false);
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
        OMLabel labelTargetName = new OMLabel(this.bundle.getString("target.label.name"), true);
        labelTargetName.setToolTipText(this.bundle.getString("target.tooltip.name"));
        gridbag.setConstraints(labelTargetName, constraints);
        this.add(labelTargetName);
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
        OMLabel labelAliasName =
                new OMLabel(this.bundle.getString("target.label.aliasNames"), SwingConstants.RIGHT, false);
        labelAliasName.setToolTipText(this.bundle.getString("target.tooltip.aliasNames"));
        gridbag.setConstraints(labelAliasName, constraints);
        this.add(labelAliasName);
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
        OMLabel labelConstallation = new OMLabel(this.bundle.getString("target.label.constellation"), false);
        labelConstallation.setToolTipText(this.bundle.getString("target.tooltip.constellation"));
        gridbag.setConstraints(labelConstallation, constraints);
        this.add(labelConstallation);

        JLabel targetDatasourceLabel = null;
        if ((this.target != null) // Show
                && !(this.editable)) {
            ConstraintsBuilder.buildConstraints(constraints, 1, 4, 1, 1, 45, 1);
            constraints.fill = GridBagConstraints.HORIZONTAL;
            this.targetConstellation.setToolTipText(this.bundle.getString("target.tooltip.constellation"));
            this.targetConstellation.setEditable(this.editable);
            gridbag.setConstraints(this.targetConstellation, constraints);
            this.add(this.targetConstellation);

            targetDatasourceLabel =
                    new OMLabel(this.bundle.getString("target.label.datasource"), SwingConstants.RIGHT, true);
            targetDatasourceLabel.setToolTipText(this.bundle.getString("target.tooltip.datasource"));
            ConstraintsBuilder.buildConstraints(constraints, 2, 4, 1, 1, 5, 1);
            constraints.fill = GridBagConstraints.NONE;
            constraints.anchor = GridBagConstraints.EAST;
            gridbag.setConstraints(targetDatasourceLabel, constraints);
            this.targetDatasource = new JTextField();
            this.targetDatasource.setToolTipText(this.bundle.getString("target.tooltip.datasource"));
            this.targetDatasource.setEditable(this.editable);
            this.add(targetDatasourceLabel);

            this.sourceObserverBox = this.createObserverDropDownBox(); // Make sure this is not NULL

            ConstraintsBuilder.buildConstraints(constraints, 3, 4, 5, 1, 45, 1);
            constraints.fill = GridBagConstraints.HORIZONTAL;
            gridbag.setConstraints(targetDatasource, constraints);
            this.add(targetDatasource);

            ConstraintsBuilder.buildConstraints(constraints, 0, 5, 1, 1, 100, 1);
            OMLabel labelNotes = new OMLabel(this.bundle.getString("target.label.notes"), false);
            labelNotes.setToolTipText(this.bundle.getString("target.tooltip.notes"));
            gridbag.setConstraints(labelNotes, constraints);
            this.add(labelNotes);
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

                JLabel labelAtlas = new JLabel(this.bundle.getString("target.label.atlas"));
                ConstraintsBuilder.buildConstraints(constraints, 0, 8, 8, 1, 100, 1);
                gridbag.setConstraints(labelAtlas, constraints);
                this.add(labelAtlas);

                JLabel labelUranometeria = new JLabel(this.bundle.getString("target.label.uranometeria"));
                JTextField uranometeria = new JTextField("" + AtlasUtil.getUranometriaPage(pos), 4);
                uranometeria.setEditable(false);
                ConstraintsBuilder.buildConstraints(constraints, 0, 9, 1, 1, 45, 1);
                constraints.fill = GridBagConstraints.NONE;
                constraints.anchor = GridBagConstraints.EAST;
                gridbag.setConstraints(labelUranometeria, constraints);
                this.add(labelUranometeria);
                ConstraintsBuilder.buildConstraints(constraints, 1, 9, 1, 1, 45, 1);
                constraints.fill = GridBagConstraints.HORIZONTAL;
                gridbag.setConstraints(uranometeria, constraints);
                this.add(uranometeria);

                JLabel labelUranometeria2 = new JLabel(this.bundle.getString("target.label.uranometeria2"));
                JTextField uranometeria2 = new JTextField("" + AtlasUtil.getUranometria2000Page(pos), 4);
                uranometeria2.setEditable(false);
                ConstraintsBuilder.buildConstraints(constraints, 2, 9, 1, 1, 45, 1);
                constraints.fill = GridBagConstraints.NONE;
                constraints.anchor = GridBagConstraints.EAST;
                gridbag.setConstraints(labelUranometeria2, constraints);
                this.add(labelUranometeria2);
                ConstraintsBuilder.buildConstraints(constraints, 3, 9, 1, 1, 45, 1);
                constraints.fill = GridBagConstraints.HORIZONTAL;
                gridbag.setConstraints(uranometeria2, constraints);
                this.add(uranometeria2);

                JLabel labelMillenium = new JLabel(this.bundle.getString("target.label.milleniumStarAtlas"));
                JTextField millenium = new JTextField("" + AtlasUtil.getMilleniumStarAtlasPage(pos), 4);
                millenium.setEditable(false);
                ConstraintsBuilder.buildConstraints(constraints, 4, 9, 1, 1, 45, 1);
                constraints.fill = GridBagConstraints.NONE;
                constraints.anchor = GridBagConstraints.EAST;
                gridbag.setConstraints(labelMillenium, constraints);
                this.add(labelMillenium);
                ConstraintsBuilder.buildConstraints(constraints, 5, 9, 1, 1, 45, 1);
                constraints.fill = GridBagConstraints.HORIZONTAL;
                gridbag.setConstraints(millenium, constraints);
                this.add(millenium);

                JLabel labelStarAtlas200 = new JLabel(this.bundle.getString("target.label.skyAtlas2000"));
                JTextField startAtlas = new JTextField("" + AtlasUtil.getSkyAtlas2000Page(pos), 4);
                startAtlas.setEditable(false);
                ConstraintsBuilder.buildConstraints(constraints, 6, 9, 1, 1, 45, 1);
                constraints.fill = GridBagConstraints.NONE;
                constraints.anchor = GridBagConstraints.EAST;
                gridbag.setConstraints(labelStarAtlas200, constraints);
                this.add(labelStarAtlas200);
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

            targetDatasourceLabel =
                    new OMLabel(this.bundle.getString("target.label.datasource"), SwingConstants.RIGHT, true);
            targetDatasourceLabel.setToolTipText(this.bundle.getString("target.tooltip.datasource"));
            ConstraintsBuilder.buildConstraints(constraints, 0, 5, 1, 1, 4, 1);
            gridbag.setConstraints(targetDatasourceLabel, constraints);
            this.add(targetDatasourceLabel);

            this.targetDatasource = new JTextField(); // make sure this is not NULL
            this.targetDatasource.setToolTipText(this.bundle.getString("target.tooltip.datasource"));
            this.targetDatasource.setEditable(this.editable);
            ConstraintsBuilder.buildConstraints(constraints, 1, 5, 1, 1, 45, 1);
            this.sourceObserverBox = this.createObserverDropDownBox();
            gridbag.setConstraints(sourceObserverBox, constraints);
            this.add(sourceObserverBox);

            ConstraintsBuilder.buildConstraints(constraints, 1, 6, 1, 1, 45, 1);
            gridbag.setConstraints(this.targetDatasource, constraints);
            this.add(this.targetDatasource);

            selectSourceType(gridbag);

            ConstraintsBuilder.buildConstraints(constraints, 0, 7, 1, 1, 100, 1);
            OMLabel labelNotes = new OMLabel(this.bundle.getString("target.label.notes"), false);
            labelNotes.setToolTipText(this.bundle.getString("target.tooltip.notes"));
            gridbag.setConstraints(labelNotes, constraints);
            this.add(labelNotes);
            ConstraintsBuilder.buildConstraints(constraints, 0, 8, 8, 3, 100, 45);
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

    private void selectSourceType(GridBagLayout gridbag) {

        ButtonGroup group = new ButtonGroup();
        GridBagConstraints constraints = ConstraintsBuilder.createConstraints(4, 5, 2, 1, 25, 2);
        constraints.fill = GridBagConstraints.HORIZONTAL;

        var observerText = this.bundle.getString("observer");
        JRadioButton observer = new JRadioButton(observerText);
        observer.setToolTipText(observerText);

        observer.addActionListener(selectSourceObserverActionListener());

        observer.setSelected(isAssignedObserver());
        gridbag.setConstraints(observer, constraints);

        var datasourceText = this.bundle.getString("target.label.datasource.other");
        JRadioButton otherSource = new JRadioButton(datasourceText);
        otherSource.setToolTipText(datasourceText);
        otherSource.addActionListener(selectOtherSourceActionListener());
        otherSource.setSelected(!isAssignedObserver());
        constraints.gridy = 6;
        gridbag.setConstraints(otherSource, constraints);

        group.add(observer);
        group.add(otherSource);

        this.add(observer);
        this.add(otherSource);

        if (isFromCatalog(this.target)) {
            observer.setEnabled(false);
            otherSource.setEnabled(false);
            this.targetDatasource.setEditable(false);
            // this.targetDatasource.setEnabled(false);
        }
    }

    private ActionListener selectOtherSourceActionListener() {

        return new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                TargetContainer.this.sourceObserverBox.setEnabled(false);
                TargetContainer.this.targetDatasource.setEnabled(true);
                TargetContainer.this.targetDatasource.setEditable(true);
            }
        };
    }

    private ActionListener selectSourceObserverActionListener() {

        return new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                TargetContainer.this.sourceObserverBox.setEnabled(true);
                TargetContainer.this.targetDatasource.setEnabled(false);
                TargetContainer.this.targetDatasource.setEditable(false);
            }
        };
    }

    private boolean isAssignedObserver() {
        return this.target != null && this.target.getObserver() != null;
    }

    private OMComboBox<IObserver> createObserverDropDownBox() {

        var sourceObserverBox = new OMComboBox<IObserver>();
        sourceObserverBox.setToolTipText(this.bundle.getString("target.dropdown.selectObserver"));

        fillComboWithObservers(sourceObserverBox);

        if (isAssignedObserver()) {
            sourceObserverBox.setSelectedItem(this.target.getObserver());
            this.targetDatasource.setEditable(false);
            this.targetDatasource.setEnabled(false);
        } else {
            sourceObserverBox.setSelectedItem(null);
            this.targetDatasource.setEditable(true);
            this.targetDatasource.setEnabled(true);
            if (isFromCatalog(this.target)) {
                this.targetDatasource.setEditable(false);
                // this.targetDatasource.setEnabled(false);
            }
        }

        return sourceObserverBox;
    }

    private boolean isFromCatalog(ITarget target) {

        if (target == null) {
            return false;
        }

        // TODO: handle this catalogs.
        var catalogs = List.of(
                "ObservationManager - SolarSystem Catalog 1.0",
                "Revised Index Catalogue",
                "The Historically Corrected New General Catalogue (HCNGC) Ver 1.11 ",
                "Caldwell",
                "ObservationManager - Messier Catalog 1.0",
                "ObservationManager - Caldwell Catalog 1.0",
                "Revised New General Catalogue",
                "The NGC/IC Project LLC (http://www.ngcic.org) - Ver 1.11",
                "General Catalogue of Variable Stars - Volumes I-III, 4th Edition - (GCVS4)",
                "Solar System",
                "Sternberg Astronomical Institute, Moscow, Russia (http://www.sai.msu.su/groups/cluster/gcvs/gcvs/) - Edition: 4");

        return catalogs.stream().anyMatch(p -> p.equalsIgnoreCase(target.getDatasource()));
    }

    private void fillComboWithObservers(OMComboBox<IObserver> sourceObserverBox) {
        IObserver[] observer = this.model.getObservers();
        if (observer != null) {
            for (IObserver iObserver : observer) {
                sourceObserverBox.addItem(iObserver);
            }
        } else {
            sourceObserverBox.addEmptyItem();
        }
    }

    private void createWarning(String message) {

        JOptionPane.showMessageDialog(
                this, message, this.bundle.getString("target.warning.title"), JOptionPane.WARNING_MESSAGE);
    }
}
