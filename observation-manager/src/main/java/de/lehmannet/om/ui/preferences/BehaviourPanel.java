package de.lehmannet.om.ui.preferences;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import de.lehmannet.om.IFinding;
import de.lehmannet.om.IObservation;
import de.lehmannet.om.IObserver;
import de.lehmannet.om.ISession;
import de.lehmannet.om.ui.box.LanguageBox;
import de.lehmannet.om.ui.box.ObserverBox;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.util.Configuration;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.OMLabel;

public class BehaviourPanel extends PreferencesPanel {

    private static final long serialVersionUID = -9046543688331125983L;

    private final PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
            .getBundle("ObservationManager", Locale.getDefault());

    private ObservationManager om = null;

    private LanguageBox defaultContentLanguage = null;
    private JCheckBox imagesPathRelative = null;
    private ObserverBox defaultObserver = null;
    private JComboBox catalogBox = null;
    private JCheckBox retrieveEndDateFromSession = null;
    private JCheckBox useCoObserverInStatistics = null;
    private JCheckBox useLatinConstellationNames = null;

    public BehaviourPanel(Configuration config, ObservationManager om) {

        super(config);

        this.om = om;
        this.createPanel();

    }

    @Override
    public String getTabTitle() {

        return this.bundle.getString("dialog.preferences.behaviourTab.title");

    }

    @Override
    public void writeConfig() {

        // Use relative filepath on images
        super.configuration.setConfig(ObservationManager.CONFIG_IMAGESDIR_RELATIVE,
                "" + this.imagesPathRelative.isSelected());

        // ------------------
        // Set default content language
        // First check if default content language was already set
        // If not, ask if language should be set to all findings and all sessions
        String currentValue = super.configuration.getConfig(ObservationManager.CONFIG_CONTENTDEFAULTLANG);
        if (((currentValue == null || "".equals(currentValue.trim()))
                && defaultContentLanguage.getSelectedISOLanguage() != null)
                || (defaultContentLanguage.getSelectedISOLanguage() != null
                        && !defaultContentLanguage.getSelectedISOLanguage().equals(currentValue))) {
            // Ask for setting to all
            boolean setAll = this.showSetLanguageDialog();
            if (setAll) {
                this.setLanguageForAllFindings(this.defaultContentLanguage.getSelectedISOLanguage());
                this.setLanguageForAllSessions(this.defaultContentLanguage.getSelectedISOLanguage());
            }
        }
        super.configuration.setConfig(ObservationManager.CONFIG_CONTENTDEFAULTLANG,
                this.defaultContentLanguage.getSelectedISOLanguage());

        // ------------------

        // Set default observer
        if (this.defaultObserver.getSelectedSchemaElement() != null) {
            super.configuration.setConfig(ObservationManager.CONFIG_DEFAULT_OBSERVER,
                    "" + this.defaultObserver.getSelectedSchemaElement().getDisplayName());
        }

        // ------------------

        // Set default catalog
        if (this.catalogBox.getSelectedItem() != null) {
            super.configuration.setConfig(ObservationManager.CONFIG_DEFAULT_CATALOG,
                    "" + this.catalogBox.getSelectedItem());
        }

        // ------------------

        // Set retrieve end date from session
        super.configuration.setConfig(ObservationManager.CONFIG_RETRIEVE_ENDDATE_FROM_SESSION,
                "" + this.retrieveEndDateFromSession.isSelected());

        // ------------------

        // Set retrieve end date from session
        super.configuration.setConfig(ObservationManager.CONFIG_STATISTICS_USE_COOBSERVERS,
                "" + this.useCoObserverInStatistics.isSelected());

        // ------------------

        // Set retrieve end date from session
        super.configuration.setConfig(ObservationManager.CONFIG_CONSTELLATION_USEI18N,
                "" + !this.useLatinConstellationNames.isSelected());

    }

    private void createPanel() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        super.setLayout(gridbag);

        // ------------------

        constraints.fill = GridBagConstraints.NONE;
        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 1, 1, 10, 10);
        OMLabel imagesPathRelativeLabel = new OMLabel(
                this.bundle.getString("dialog.preferences.label.imagesPathRelative"), true);
        imagesPathRelativeLabel.setToolTipText(this.bundle.getString("dialog.preferences.tooltip.imagesPathRelative"));
        gridbag.setConstraints(imagesPathRelativeLabel, constraints);
        super.add(imagesPathRelativeLabel);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        ConstraintsBuilder.buildConstraints(constraints, 1, 0, 1, 1, 10, 10);
        this.imagesPathRelative = new JCheckBox();
        this.imagesPathRelative.setSelected(
                Boolean.valueOf(super.configuration.getConfig(ObservationManager.CONFIG_IMAGESDIR_RELATIVE, "true"))
                        .booleanValue());
        this.imagesPathRelative.setToolTipText(this.bundle.getString("dialog.preferences.tooltip.imagesPathRelative"));
        gridbag.setConstraints(this.imagesPathRelative, constraints);
        super.add(this.imagesPathRelative);

        // ------------------

        constraints.fill = GridBagConstraints.NONE;
        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 1, 1, 10, 10);
        OMLabel contentDefaultLanguageLabel = new OMLabel(
                this.bundle.getString("dialog.preferences.label.contentDefaultLang"), true);
        contentDefaultLanguageLabel
                .setToolTipText(this.bundle.getString("dialog.preferences.tooltip.contentDefaultLang"));
        gridbag.setConstraints(contentDefaultLanguageLabel, constraints);
        super.add(contentDefaultLanguageLabel);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        ConstraintsBuilder.buildConstraints(constraints, 1, 1, 1, 1, 10, 10);
        this.defaultContentLanguage = new LanguageBox(true);
        this.defaultContentLanguage.setEnabled(true);
        String currentValue = super.configuration.getConfig(ObservationManager.CONFIG_CONTENTDEFAULTLANG);
        if ((currentValue != null) && (!"".equals(currentValue.trim()))) {
            this.defaultContentLanguage.setLanguage(currentValue);
        }
        this.defaultContentLanguage
                .setToolTipText(this.bundle.getString("dialog.preferences.tooltip.contentDefaultLang"));
        gridbag.setConstraints(this.defaultContentLanguage, constraints);
        super.add(this.defaultContentLanguage);

        // ------------------

        constraints.fill = GridBagConstraints.NONE;
        ConstraintsBuilder.buildConstraints(constraints, 0, 2, 1, 1, 10, 10);
        OMLabel defaultObserverLabel = new OMLabel(this.bundle.getString("dialog.preferences.label.defaultObserver"),
                true);
        defaultObserverLabel.setToolTipText(this.bundle.getString("dialog.preferences.tooltip.defaultObserver"));
        gridbag.setConstraints(defaultObserverLabel, constraints);
        super.add(defaultObserverLabel);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        ConstraintsBuilder.buildConstraints(constraints, 1, 2, 1, 1, 10, 10);
        this.defaultObserver = new ObserverBox();
        this.defaultObserver.setEnabled(true);
        this.fillObserverBox();
        this.defaultObserver.setToolTipText(this.bundle.getString("dialog.preferences.tooltip.defaultObserver"));
        gridbag.setConstraints(this.defaultObserver, constraints);
        super.add(this.defaultObserver);

        // ------------------

        constraints.fill = GridBagConstraints.NONE;
        ConstraintsBuilder.buildConstraints(constraints, 0, 3, 1, 1, 10, 10);
        OMLabel defaultCatalogLabel = new OMLabel(this.bundle.getString("dialog.preferences.label.defaultCatalog"),
                true);
        defaultCatalogLabel.setToolTipText(this.bundle.getString("dialog.preferences.tooltip.defaultCatalog"));
        gridbag.setConstraints(defaultCatalogLabel, constraints);
        super.add(defaultCatalogLabel);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        ConstraintsBuilder.buildConstraints(constraints, 1, 3, 1, 1, 10, 10);
        this.catalogBox = new JComboBox();
        this.catalogBox.setEnabled(true);
        this.fillCatalogBox();
        this.catalogBox.setToolTipText(this.bundle.getString("dialog.preferences.tooltip.defaultCatalog"));
        gridbag.setConstraints(this.catalogBox, constraints);
        super.add(this.catalogBox);

        // ------------------

        constraints.fill = GridBagConstraints.NONE;
        ConstraintsBuilder.buildConstraints(constraints, 0, 4, 1, 1, 10, 10);
        OMLabel LretrieveEndDateFromSession = new OMLabel(
                this.bundle.getString("dialog.preferences.label.retrieveEndDateFromSession"), true);
        LretrieveEndDateFromSession
                .setToolTipText(this.bundle.getString("dialog.preferences.tooltip.retrieveEndDateFromSession"));
        gridbag.setConstraints(LretrieveEndDateFromSession, constraints);
        super.add(LretrieveEndDateFromSession);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        ConstraintsBuilder.buildConstraints(constraints, 1, 4, 1, 1, 10, 10);
        this.retrieveEndDateFromSession = new JCheckBox();
        this.retrieveEndDateFromSession.setSelected(
                Boolean.valueOf(super.configuration.getConfig(ObservationManager.CONFIG_RETRIEVE_ENDDATE_FROM_SESSION))
                        .booleanValue());
        this.retrieveEndDateFromSession
                .setToolTipText(this.bundle.getString("dialog.preferences.tooltip.retrieveEndDateFromSession"));
        gridbag.setConstraints(this.retrieveEndDateFromSession, constraints);
        super.add(this.retrieveEndDateFromSession);

        // ------------------

        constraints.fill = GridBagConstraints.NONE;
        ConstraintsBuilder.buildConstraints(constraints, 0, 5, 1, 1, 10, 10);
        OMLabel LuseCoObserverInStatistics = new OMLabel(
                this.bundle.getString("dialog.preferences.label.useCoObserverInStatistics"), true);
        LuseCoObserverInStatistics
                .setToolTipText(this.bundle.getString("dialog.preferences.tooltip.useCoObserverInStatistics"));
        gridbag.setConstraints(LuseCoObserverInStatistics, constraints);
        super.add(LuseCoObserverInStatistics);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        ConstraintsBuilder.buildConstraints(constraints, 1, 5, 1, 1, 10, 10);
        this.useCoObserverInStatistics = new JCheckBox();
        this.useCoObserverInStatistics.setSelected(Boolean
                .valueOf(super.configuration.getConfig(ObservationManager.CONFIG_STATISTICS_USE_COOBSERVERS, "true"))
                .booleanValue());
        this.useCoObserverInStatistics
                .setToolTipText(this.bundle.getString("dialog.preferences.tooltip.useCoObserverInStatistics"));
        gridbag.setConstraints(this.useCoObserverInStatistics, constraints);
        super.add(this.useCoObserverInStatistics);

        // ------------------

        constraints.fill = GridBagConstraints.NONE;
        ConstraintsBuilder.buildConstraints(constraints, 0, 6, 1, 1, 10, 10);
        OMLabel LuseLatinConstellationNames = new OMLabel(
                this.bundle.getString("dialog.preferences.label.useLatinConstellationNames"), true);
        LuseLatinConstellationNames
                .setToolTipText(this.bundle.getString("dialog.preferences.tooltip.useLatinConstellationNames"));
        gridbag.setConstraints(LuseLatinConstellationNames, constraints);
        super.add(LuseLatinConstellationNames);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        ConstraintsBuilder.buildConstraints(constraints, 1, 6, 1, 1, 10, 10);
        this.useLatinConstellationNames = new JCheckBox();
        this.useLatinConstellationNames.setSelected(
                !Boolean.valueOf(super.configuration.getConfig(ObservationManager.CONFIG_CONSTELLATION_USEI18N, "true"))
                        .booleanValue());
        this.useLatinConstellationNames
                .setToolTipText(this.bundle.getString("dialog.preferences.tooltip.useLatinConstellationNames"));
        gridbag.setConstraints(this.useLatinConstellationNames, constraints);
        super.add(this.useLatinConstellationNames);

    }

    private void fillObserverBox() {

        String currentValue = super.configuration.getConfig(ObservationManager.CONFIG_DEFAULT_OBSERVER);

        IObserver[] observers = this.om.getXmlCache().getObservers();
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

        this.catalogBox
                .setSelectedItem(this.om.getConfiguration().getConfig(ObservationManager.CONFIG_DEFAULT_CATALOG));

    }

    private void setLanguageForAllFindings(String isoLanguage) {

        IObservation[] observations = this.om.getXmlCache().getObservations();
        List findings = null;
        Iterator iter = null;
        for (IObservation observation : observations) {
            findings = observation.getResults();
            iter = findings.listIterator();
            while (iter.hasNext()) {
                ((IFinding) iter.next()).setLanguage(isoLanguage);
            }
        }

    }

    private void setLanguageForAllSessions(String isoLanguage) {

        ISession[] sessions = this.om.getXmlCache().getSessions();
        for (ISession session : sessions) {
            session.setLanguage(isoLanguage);
        }

    }

    private boolean showSetLanguageDialog() {

        JOptionPane pane = new JOptionPane(this.bundle.getString("dialog.preferences.setLanguage2All.question"),
                JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
        JDialog dialog = pane.createDialog(this.om, this.bundle.getString("dialog.preferences.setLanguage2All.title"));
        dialog.setVisible(true);
        Object selectedValue = pane.getValue();

        if ((selectedValue instanceof Integer)) {
            return (Integer) selectedValue == JOptionPane.YES_OPTION;
        }

        return false;

    }

}