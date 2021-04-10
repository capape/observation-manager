package de.lehmannet.om.ui.preferences;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Iterator;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.apache.commons.lang3.StringUtils;

import de.lehmannet.om.IFinding;
import de.lehmannet.om.IObservation;
import de.lehmannet.om.IObserver;
import de.lehmannet.om.ISession;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.box.LanguageBox;
import de.lehmannet.om.ui.box.ObserverBox;
import de.lehmannet.om.ui.i18n.TextManager;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.util.ConfigKey;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.IConfiguration;
import de.lehmannet.om.ui.util.OMLabel;

public class BehaviourPanel extends PreferencesPanel {

    private static final long serialVersionUID = -9046543688331125983L;

    private LanguageBox defaultContentLanguage = null;
    private JCheckBox imagesPathRelative = null;
    private ObserverBox defaultObserver = null;
    private JComboBox catalogBox = null;
    private JCheckBox retrieveEndDateFromSession = null;
    private JCheckBox useCoObserverInStatistics = null;
    private JCheckBox useLatinConstellationNames = null;
    private final ObservationManagerModel model;
    private final TextManager textManager;
    private final ObservationManager om;

    public BehaviourPanel(IConfiguration config, ObservationManager om, ObservationManagerModel model,
            TextManager textManager) {

        super(config);

        this.om = om;
        this.model = model;
        this.textManager = textManager;
        this.createPanel();

    }

    @Override
    public String getTabTitle() {

        return this.textManager.getString("dialog.preferences.behaviourTab.title");

    }

    @Override
    public void writeConfig() {

        // Use relative filepath on images
        this.setConfig(ConfigKey.CONFIG_IMAGESDIR_RELATIVE, String.valueOf(this.imagesPathRelative.isSelected()));

        // ------------------
        // Set default content language
        // First check if default content language was already set
        // If not, ask if language should be set to all findings and all sessions
        String currentValue = this.getConfig(ConfigKey.CONFIG_CONTENTDEFAULTLANG).orElse("");
        if ((!StringUtils.isBlank(currentValue) && defaultContentLanguage.getSelectedISOLanguage() != null)
                || (defaultContentLanguage.getSelectedISOLanguage() != null
                        && !defaultContentLanguage.getSelectedISOLanguage().equals(currentValue))) {
            // Ask for setting to all
            boolean setAll = this.showSetLanguageDialog();
            if (setAll) {
                this.setLanguageForAllFindings(this.defaultContentLanguage.getSelectedISOLanguage());
                this.setLanguageForAllSessions(this.defaultContentLanguage.getSelectedISOLanguage());
            }
        }
        this.setConfig(ConfigKey.CONFIG_CONTENTDEFAULTLANG, this.defaultContentLanguage.getSelectedISOLanguage());

        // ------------------

        // Set default observer
        if (this.defaultObserver.getSelectedSchemaElement() != null) {
            this.setConfig(ConfigKey.CONFIG_DEFAULT_OBSERVER,
                    String.valueOf(this.defaultObserver.getSelectedSchemaElement().getDisplayName()));
        }

        // ------------------

        // Set default catalog
        if (this.catalogBox.getSelectedItem() != null) {
            this.setConfig(ConfigKey.CONFIG_DEFAULT_CATALOG, String.valueOf(this.catalogBox.getSelectedItem()));
        }

        // ------------------

        // Set retrieve end date from session
        this.setConfig(ConfigKey.CONFIG_RETRIEVE_ENDDATE_FROM_SESSION,
                String.valueOf(this.retrieveEndDateFromSession.isSelected()));

        // ------------------

        // Set retrieve end date from session
        this.setConfig(ConfigKey.CONFIG_STATISTICS_USE_COOBSERVERS,
                String.valueOf(this.useCoObserverInStatistics.isSelected()));

        // ------------------

        // Set retrieve end date from session
        this.setConfig(ConfigKey.CONFIG_CONSTELLATION_USEI18N,
                String.valueOf(!this.useLatinConstellationNames.isSelected()));

    }

    private void createPanel() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        this.setLayout(gridbag);

        // ------------------

        constraints.fill = GridBagConstraints.NONE;
        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 1, 1, 10, 10);
        OMLabel imagesPathRelativeLabel = new OMLabel(
                this.textManager.getString("dialog.preferences.label.imagesPathRelative"), true);
        imagesPathRelativeLabel
                .setToolTipText(this.textManager.getString("dialog.preferences.tooltip.imagesPathRelative"));
        gridbag.setConstraints(imagesPathRelativeLabel, constraints);
        this.add(imagesPathRelativeLabel);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        ConstraintsBuilder.buildConstraints(constraints, 1, 0, 1, 1, 10, 10);
        this.imagesPathRelative = new JCheckBox();
        this.imagesPathRelative
                .setSelected(Boolean.valueOf(this.getConfig(ConfigKey.CONFIG_IMAGESDIR_RELATIVE).orElse("true")));
        this.imagesPathRelative
                .setToolTipText(this.textManager.getString("dialog.preferences.tooltip.imagesPathRelative"));
        gridbag.setConstraints(this.imagesPathRelative, constraints);
        this.add(this.imagesPathRelative);

        // ------------------

        constraints.fill = GridBagConstraints.NONE;
        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 1, 1, 10, 10);
        OMLabel contentDefaultLanguageLabel = new OMLabel(
                this.textManager.getString("dialog.preferences.label.contentDefaultLang"), true);
        contentDefaultLanguageLabel
                .setToolTipText(this.textManager.getString("dialog.preferences.tooltip.contentDefaultLang"));
        gridbag.setConstraints(contentDefaultLanguageLabel, constraints);
        this.add(contentDefaultLanguageLabel);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        ConstraintsBuilder.buildConstraints(constraints, 1, 1, 1, 1, 10, 10);
        this.defaultContentLanguage = new LanguageBox(true);
        this.defaultContentLanguage.setEnabled(true);
        String currentValue = this.getConfig(ConfigKey.CONFIG_CONTENTDEFAULTLANG).orElse("");
        if (!StringUtils.isEmpty(currentValue)) {
            this.defaultContentLanguage.setLanguage(currentValue);
        }
        this.defaultContentLanguage
                .setToolTipText(this.textManager.getString("dialog.preferences.tooltip.contentDefaultLang"));
        gridbag.setConstraints(this.defaultContentLanguage, constraints);
        this.add(this.defaultContentLanguage);

        // ------------------

        constraints.fill = GridBagConstraints.NONE;
        ConstraintsBuilder.buildConstraints(constraints, 0, 2, 1, 1, 10, 10);
        OMLabel defaultObserverLabel = new OMLabel(
                this.textManager.getString("dialog.preferences.label.defaultObserver"), true);
        defaultObserverLabel.setToolTipText(this.textManager.getString("dialog.preferences.tooltip.defaultObserver"));
        gridbag.setConstraints(defaultObserverLabel, constraints);
        this.add(defaultObserverLabel);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        ConstraintsBuilder.buildConstraints(constraints, 1, 2, 1, 1, 10, 10);
        this.defaultObserver = new ObserverBox();
        this.defaultObserver.setEnabled(true);
        this.fillObserverBox();
        this.defaultObserver.setToolTipText(this.textManager.getString("dialog.preferences.tooltip.defaultObserver"));
        gridbag.setConstraints(this.defaultObserver, constraints);
        this.add(this.defaultObserver);

        // ------------------

        constraints.fill = GridBagConstraints.NONE;
        ConstraintsBuilder.buildConstraints(constraints, 0, 3, 1, 1, 10, 10);
        OMLabel defaultCatalogLabel = new OMLabel(this.textManager.getString("dialog.preferences.label.defaultCatalog"),
                true);
        defaultCatalogLabel.setToolTipText(this.textManager.getString("dialog.preferences.tooltip.defaultCatalog"));
        gridbag.setConstraints(defaultCatalogLabel, constraints);
        this.add(defaultCatalogLabel);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        ConstraintsBuilder.buildConstraints(constraints, 1, 3, 1, 1, 10, 10);
        this.catalogBox = new JComboBox();
        this.catalogBox.setEnabled(true);
        this.fillCatalogBox();
        this.catalogBox.setToolTipText(this.textManager.getString("dialog.preferences.tooltip.defaultCatalog"));
        gridbag.setConstraints(this.catalogBox, constraints);
        this.add(this.catalogBox);

        // ------------------

        constraints.fill = GridBagConstraints.NONE;
        ConstraintsBuilder.buildConstraints(constraints, 0, 4, 1, 1, 10, 10);
        OMLabel LretrieveEndDateFromSession = new OMLabel(
                this.textManager.getString("dialog.preferences.label.retrieveEndDateFromSession"), true);
        LretrieveEndDateFromSession
                .setToolTipText(this.textManager.getString("dialog.preferences.tooltip.retrieveEndDateFromSession"));
        gridbag.setConstraints(LretrieveEndDateFromSession, constraints);
        this.add(LretrieveEndDateFromSession);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        ConstraintsBuilder.buildConstraints(constraints, 1, 4, 1, 1, 10, 10);
        this.retrieveEndDateFromSession = new JCheckBox();
        this.retrieveEndDateFromSession.setSelected(
                Boolean.valueOf(this.getConfig(ConfigKey.CONFIG_RETRIEVE_ENDDATE_FROM_SESSION).orElse("false")));
        this.retrieveEndDateFromSession
                .setToolTipText(this.textManager.getString("dialog.preferences.tooltip.retrieveEndDateFromSession"));
        gridbag.setConstraints(this.retrieveEndDateFromSession, constraints);
        this.add(this.retrieveEndDateFromSession);

        // ------------------

        constraints.fill = GridBagConstraints.NONE;
        ConstraintsBuilder.buildConstraints(constraints, 0, 5, 1, 1, 10, 10);
        OMLabel LuseCoObserverInStatistics = new OMLabel(
                this.textManager.getString("dialog.preferences.label.useCoObserverInStatistics"), true);
        LuseCoObserverInStatistics
                .setToolTipText(this.textManager.getString("dialog.preferences.tooltip.useCoObserverInStatistics"));
        gridbag.setConstraints(LuseCoObserverInStatistics, constraints);
        this.add(LuseCoObserverInStatistics);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        ConstraintsBuilder.buildConstraints(constraints, 1, 5, 1, 1, 10, 10);
        this.useCoObserverInStatistics = new JCheckBox();
        this.useCoObserverInStatistics.setSelected(
                Boolean.valueOf(this.getConfig(ConfigKey.CONFIG_STATISTICS_USE_COOBSERVERS).orElse("true")));
        this.useCoObserverInStatistics
                .setToolTipText(this.textManager.getString("dialog.preferences.tooltip.useCoObserverInStatistics"));
        gridbag.setConstraints(this.useCoObserverInStatistics, constraints);
        this.add(this.useCoObserverInStatistics);

        // ------------------

        constraints.fill = GridBagConstraints.NONE;
        ConstraintsBuilder.buildConstraints(constraints, 0, 6, 1, 1, 10, 10);
        OMLabel LuseLatinConstellationNames = new OMLabel(
                this.textManager.getString("dialog.preferences.label.useLatinConstellationNames"), true);
        LuseLatinConstellationNames
                .setToolTipText(this.textManager.getString("dialog.preferences.tooltip.useLatinConstellationNames"));
        gridbag.setConstraints(LuseLatinConstellationNames, constraints);
        this.add(LuseLatinConstellationNames);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        ConstraintsBuilder.buildConstraints(constraints, 1, 6, 1, 1, 10, 10);
        this.useLatinConstellationNames = new JCheckBox();
        this.useLatinConstellationNames
                .setSelected(!Boolean.valueOf(this.getConfig(ConfigKey.CONFIG_CONSTELLATION_USEI18N).orElse("true")));
        this.useLatinConstellationNames
                .setToolTipText(this.textManager.getString("dialog.preferences.tooltip.useLatinConstellationNames"));
        gridbag.setConstraints(this.useLatinConstellationNames, constraints);
        this.add(this.useLatinConstellationNames);

    }

    private void fillObserverBox() {

        String currentValue = this.getConfig(ConfigKey.CONFIG_DEFAULT_OBSERVER).orElse(null);

        IObserver[] observers = this.model.getObservers();
        IObserver defaultObserver = null;
        for (IObserver observer : observers) {
            this.defaultObserver.addItem(observer);
            if ((currentValue != null) && (currentValue.equals(observer.getDisplayName()))) {
                defaultObserver = observer;
            }
        }

        if (defaultObserver != null) {
            this.defaultObserver.setSelectedItem(defaultObserver);
        }

    }

    private void fillCatalogBox() {

        String[] cNames = this.om.getExtensionLoader().getCatalogLoader().getCatalogNames();
        for (String cName : cNames) {
            this.catalogBox.addItem(cName);
        }

        this.catalogBox.setSelectedItem(this.om.getConfiguration().getConfig(ConfigKey.CONFIG_DEFAULT_CATALOG));

    }

    private void setLanguageForAllFindings(String isoLanguage) {

        IObservation[] observations = this.model.getObservations();
        List<IFinding> findings = null;
        Iterator<IFinding> iter = null;
        for (IObservation observation : observations) {
            findings = observation.getResults();
            iter = findings.listIterator();
            while (iter.hasNext()) {
                iter.next().setLanguage(isoLanguage);
            }
        }

    }

    private void setLanguageForAllSessions(String isoLanguage) {

        ISession[] sessions = this.model.getSessions();
        for (ISession session : sessions) {
            session.setLanguage(isoLanguage);
        }

    }

    private boolean showSetLanguageDialog() {

        JOptionPane pane = new JOptionPane(this.textManager.getString("dialog.preferences.setLanguage2All.question"),
                JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
        JDialog dialog = pane.createDialog(this.om,
                this.textManager.getString("dialog.preferences.setLanguage2All.title"));
        dialog.setVisible(true);
        Object selectedValue = pane.getValue();

        if ((selectedValue instanceof Integer)) {
            return (Integer) selectedValue == JOptionPane.YES_OPTION;
        }

        return false;

    }

}