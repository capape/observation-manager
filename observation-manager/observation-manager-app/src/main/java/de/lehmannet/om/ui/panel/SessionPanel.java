/* ====================================================================
 * /panel/SessionPanel.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.panel;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;

import de.lehmannet.om.IObserver;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ISession;
import de.lehmannet.om.ISite;
import de.lehmannet.om.Session;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.box.LanguageBox;
import de.lehmannet.om.ui.box.SiteBox;
import de.lehmannet.om.ui.cache.UIDataCache;
import de.lehmannet.om.ui.container.ImageContainer;
import de.lehmannet.om.ui.container.TimeContainer;
import de.lehmannet.om.ui.dialog.ObserverDialog;
import de.lehmannet.om.ui.dialog.SchemaElementSelectorPopup;
import de.lehmannet.om.ui.dialog.SiteDialog;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.util.ConfigKey;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.DatePicker;
import de.lehmannet.om.ui.util.EditPopupHandler;
import de.lehmannet.om.ui.util.OMLabel;
import de.lehmannet.om.util.DateManager;
import de.lehmannet.om.util.DateManagerImpl;
import de.lehmannet.om.util.SchemaElementConstants;

public class SessionPanel extends AbstractPanel implements ActionListener, MouseListener {

    private static final long serialVersionUID = 3701541525499161393L;

    private ISession session = null;

    private ObservationManager observationManager = null;

    private static final DateTimeFormatter formatterDay = DateTimeFormatter.ofPattern("dd.MM.yy", Locale.getDefault());

    private JTextField begin = null;
    private OffsetDateTime beginDate = null;
    private JButton beginPicker = null;
    private JButton beginNow = null;
    private TimeContainer beginTime = null;
    private JTextField end = null;
    private OffsetDateTime endDate = null;
    private JButton endPicker = null;
    private JButton endNow = null;
    private TimeContainer endTime = null;
    private JTextArea weather = null;
    private JTextArea equipment = null;
    private JTextArea comments = null;
    private final JTextField coObservers = new JTextField(); // Shows selected coObservers in a TextField (Creation,
                                                             // Edit)
    private JTabbedPane coObserverTabbedPane = null; // Shows selected coObservers in a TabbedPane (Show)
    private JButton selectCoObservers = null; // Brings up ObserverSelectorPopup (Creation, Edit)
    private JButton newCoObservers = null; // Brings up observer dialog for creating new observer
    private SchemaElementSelectorPopup coObsSelector = null; // The ObserverSelectorPopup (Creation, Edit)
    private SiteBox siteBox = null; // Shows possible site selections (Creation, Edit)
    private JButton newSite = null; // Brings up site dialog for creating new site
    private LanguageBox language = null;
    private ImageContainer imageContainer = null;
    private JButton newImage = null;

    private final List<IObserver> coObserversList = new ArrayList<>();
    private final ObservationManagerModel model;

    private final DateManager dateManager;
    private final UIDataCache cache;

    // Requires ObservationManager to load Observers
    public SessionPanel(ObservationManager manager, ObservationManagerModel model, ISession session, boolean editable,
            UIDataCache cache) {

        super(editable);

        this.observationManager = manager;
        this.session = session;
        this.model = model;

        this.cache = cache;

        this.language = new LanguageBox(
                this.observationManager.getConfiguration().getConfig(ConfigKey.CONFIG_CONTENTDEFAULTLANG), true);

        this.createPanel();

        if (session != null) {
            this.loadSchemaElement();
        }

        this.setVisible(true);
        this.dateManager = new DateManagerImpl();

    }

    @Override
    public void mouseClicked(MouseEvent e) {

        // Check only button
        // Source component is always the JTextArea
        if (e.getButton() == MouseEvent.BUTTON3) {
            JTextArea area = null;
            if (e.getSource() == this.comments) {
                area = this.comments;
            } else if (e.getSource() == this.weather) {
                area = this.weather;
            } else if (e.getSource() == this.equipment) {
                area = this.equipment;
            }
            new EditPopupHandler(e.getX(), e.getY(), area);
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

    @Override
    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();
        if (source instanceof JButton) {
            JButton sourceButton = (JButton) source;
            if (sourceButton.equals(this.selectCoObservers)) {
                this.coObsSelector = new SchemaElementSelectorPopup(this.observationManager, this.model,
                        AbstractPanel.bundle.getString("dialog.coObserver.title"), null, this.coObserversList, true,
                        SchemaElementConstants.OBSERVER);
                List<ISchemaElement> selected = new ArrayList<>();
                if (this.coObsSelector.getAllSelectedElements() != null) {
                    selected = new ArrayList<>(this.coObsSelector.getAllSelectedElements()); // Create new list
                                                                                             // instance,
                                                                                             // as
                                                                                             // otherwise the clear()
                                                                                             // below, will
                                                                                             // clear selection
                }
                this.coObserversList.clear(); // Remove entries first
                List<IObserver> result = selected.stream().map(x -> {
                    return (IObserver) x;
                }).collect(Collectors.toList());

                this.fillCoObserverTextField(result);
            } else if (sourceButton.equals(this.beginPicker)) {
                DatePicker dp = null;
                if (this.beginDate != null) {
                    dp = new DatePicker(this.observationManager,
                            AbstractPanel.bundle.getString("panel.session.datePicker.start"), this.beginDate,
                            this.dateManager);
                } else {
                    dp = new DatePicker(this.observationManager,
                            AbstractPanel.bundle.getString("panel.session.datePicker.start"), this.dateManager);
                }
                this.beginDate = dp.getDate();
                this.begin.setText(dp.getDateString());
            } else if (sourceButton.equals(this.endPicker)) {
                DatePicker dp = null;
                if (this.endDate != null) {
                    dp = new DatePicker(this.observationManager,
                            AbstractPanel.bundle.getString("panel.session.datePicker.end"), this.endDate,
                            this.dateManager);
                } else if (this.beginDate != null) { // Try to initialize endDate Picker with startdate
                    dp = new DatePicker(this.observationManager,
                            AbstractPanel.bundle.getString("panel.session.datePicker.end"), this.beginDate,
                            this.dateManager);
                } else {
                    dp = new DatePicker(this.observationManager,
                            AbstractPanel.bundle.getString("panel.session.datePicker.end"), this.dateManager);
                }
                this.endDate = dp.getDate();
                this.end.setText(dp.getDateString());

            } else if (source.equals(this.endNow)) {

                this.endDate = OffsetDateTime.now();
                this.endTime.setTime(endDate.getHour(), endDate.getMinute(), endDate.getSecond());
                this.end.setText(this.formatDate(this.endDate));
            } else if (source.equals(this.beginNow)) {

                this.beginDate = OffsetDateTime.now();
                this.beginTime.setTime(beginDate.getHour(), beginDate.getMinute(), beginDate.getSecond());
                this.begin.setText(this.formatDate(this.beginDate));
            } else if (sourceButton.equals(this.newSite)) {
                SiteDialog dialog = new SiteDialog(this.observationManager, null);
                this.siteBox.addItem(dialog.getSite());
            } else if (sourceButton.equals(this.newCoObservers)) {
                ObserverDialog dialog = new ObserverDialog(this.observationManager, null);
                if (dialog.getObserver() != null) { // Maybe no observer was created
                    this.addCoObserverToTextfield(dialog.getObserver().getDisplayName());
                    this.coObserversList.add(dialog.getObserver());
                }
            } else if (sourceButton.equals(this.newImage)) {
                this.addNewImages();
            }
        }

    }

    @Override
    public ISchemaElement getSchemaElement() {

        return this.session;

    }

    private void loadSchemaElement() {

        this.beginDate = this.session.getBegin();

        this.begin.setText(this.beginDate.format(formatterDay));
        this.begin.setEditable(this.isEditable());
        this.beginTime.setTime(this.beginDate.getHour(), this.beginDate.getMinute(), this.beginDate.getSecond());
        this.beginTime.setEditable(this.isEditable());

        this.endDate = this.session.getEnd();

        this.end.setText(this.endDate.format(formatterDay));
        this.end.setEditable(this.isEditable());
        this.endTime.setTime(this.endDate.getHour(), this.endDate.getMinute(), this.endDate.getSecond());
        this.endTime.setEditable(this.isEditable());

        this.weather.setText(this.session.getWeather());
        this.weather.setEditable(this.isEditable());

        this.equipment.setText(this.session.getEquipment());
        this.equipment.setEditable(this.isEditable());

        this.comments.setText(this.session.getComments());
        this.comments.setEditable(this.isEditable());

        /*
         * if( !this.isEditable() ) { this.weather.setBackground(Color.LIGHT_GRAY);
         * this.equipment.setBackground(Color.LIGHT_GRAY); this.comments.setBackground(Color.LIGHT_GRAY); }
         */

        this.language.setLanguage(this.session.getLanguage());
        this.language.setEditable(this.isEditable());

        this.fillCoObserverTextField(this.session.getCoObservers());

        this.imageContainer.addImagesFromPath(this.session.getImages());

        this.setVisible(false);
        this.setVisible(true);

    }

    @Override
    public ISchemaElement updateSchemaElement() {

        if (this.session == null) {
            return null;
        }

        if (this.beginDate == null) {
            this.createWarning(AbstractPanel.bundle.getString("panel.session.warning.noStart"));
            return null;
        }

        if (this.beginTime.checkTime()) {
            this.createWarning(AbstractPanel.bundle.getString("panel.session.warning.startIncorrect"));
            return null;
        }

        if (this.endDate == null) {
            this.createWarning(AbstractPanel.bundle.getString("panel.session.warning.noEnd"));
            return null;
        }

        if (this.endTime.checkTime()) {
            this.createWarning(AbstractPanel.bundle.getString("panel.session.warning.endIncorrect"));
            return null;
        }

        if (this.siteBox.getSelectedSchemaElement() == null) {
            this.createWarning(AbstractPanel.bundle.getString("panel.session.warning.noSite"));
            return null;
        }

        ISite site = (ISite) this.siteBox.getSelectedSchemaElement();

        // Set site's timezone (make sure that timezone is set in ms)
        this.session.setSite(site);

        SimpleTimeZone simpleTimeZone = new SimpleTimeZone(site.getTimezone() * 60 * 1000, site.getName());
        this.endDate = this.createOffSetDateTime(this.endDate, this.endTime, simpleTimeZone);
        this.beginDate = this.createOffSetDateTime(this.beginDate, this.beginTime, simpleTimeZone);

        if (this.endDate.isBefore(this.beginDate)) {
            this.createWarning(AbstractPanel.bundle.getString("panel.session.warning.endBeforeStart"));
            return null;
        }

        // Set optional elements
        String weather = this.weather.getText();
        this.session.setWeather(weather);

        String equipment = this.equipment.getText();
        this.session.setEquipment(equipment);

        String comments = this.comments.getText();
        this.session.setComments(comments);

        if (this.coObsSelector != null) {
            this.session.setCoObservers(this.coObserversList);
        }

        if (this.siteBox.getSelectedSchemaElement() != null) {
            this.session.setSite((ISite) this.siteBox.getSelectedSchemaElement());
        }

        if (this.language.getSelectedISOLanguage() != null) {
            this.session.setLanguage(this.language.getSelectedISOLanguage());
        }

        this.session.setImages(this.imageContainer.getImages(this.model.getXMLFileForSchemaElement(session)));

        return this.session;

    }

    private OffsetDateTime createOffSetDateTime(OffsetDateTime date, TimeContainer timeContainer,
            SimpleTimeZone simpleTimeZone) {

        final ZoneOffset zoneOffSet = ZoneOffset.ofTotalSeconds(simpleTimeZone.getRawOffset() / 1000);

        return OffsetDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), timeContainer.getHour(),
                timeContainer.getMinutes(), timeContainer.getSeconds(), 0, zoneOffSet);

    }

    @Override
    public ISchemaElement createSchemaElement() {

        if (beginDate == null) {
            this.createWarning(AbstractPanel.bundle.getString("panel.session.warning.noStart"));
            return null;
        }

        if (this.beginTime.checkTime()) {
            this.createWarning(AbstractPanel.bundle.getString("panel.session.warning.startIncorrect"));
            return null;
        }

        if (endDate == null) {
            this.createWarning(AbstractPanel.bundle.getString("panel.session.warning.noEnd"));
            return null;
        }

        if (this.endTime.checkTime()) {
            this.createWarning(AbstractPanel.bundle.getString("panel.session.warning.endIncorrect"));
            return null;
        }

        if (siteBox.getSelectedSchemaElement() == null) {
            this.createWarning(AbstractPanel.bundle.getString("panel.session.warning.noSite"));
            return null;
        }

        ISite site = (ISite) this.siteBox.getSelectedSchemaElement();
        // Set site's timezone (make sure that timezone is set in ms)
        SimpleTimeZone simpleTimeZone = new SimpleTimeZone(site.getTimezone() * 60 * 1000, site.getName());

        this.endDate = createOffSetDateTime(endDate, endTime, simpleTimeZone);
        this.beginDate = createOffSetDateTime(beginDate, beginTime, simpleTimeZone);

        if (this.endDate.isBefore(this.beginDate)) {
            this.createWarning(AbstractPanel.bundle.getString("panel.session.warning.endBeforeStart"));
            return null;
        }

        // Create session
        this.session = new Session(this.dateManager, beginDate, endDate, site);

        // Set optional elements
        String weather = this.weather.getText();
        if ((weather != null) && !("".equals(weather))) {
            this.session.setWeather(weather);
        }

        String equipment = this.equipment.getText();
        if ((equipment != null) && !("".equals(equipment))) {
            this.session.setEquipment(equipment);
        }

        String comments = this.comments.getText();
        if ((comments != null) && !("".equals(comments))) {
            this.session.setComments(comments);
        }

        this.session.setCoObservers(this.coObserversList);

        if (this.siteBox.getSelectedSchemaElement() != null) {
            this.session.setSite((ISite) this.siteBox.getSelectedSchemaElement());
        }

        if (this.language.getSelectedISOLanguage() != null) {
            this.session.setLanguage(this.language.getSelectedISOLanguage());
        }

        this.session.setImages(this.imageContainer.getImages(this.model.getXMLFileForSchemaElement(session)));

        return this.session;

    }

    private void createPanel() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 1, 1, 5, 1);
        addBeginDateLabel(gridbag, constraints);

        ConstraintsBuilder.buildConstraints(constraints, 1, 0, 4, 1, 25, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addBeginDateBox(gridbag, constraints);

        ConstraintsBuilder.buildConstraints(constraints, 5, 0, 1, 1, 2, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addBeginDateSelectorButton(gridbag, constraints);

        ConstraintsBuilder.buildConstraints(constraints, 6, 0, 1, 1, 25, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addBeginTimeLabel(gridbag, constraints);

        ConstraintsBuilder.buildConstraints(constraints, 7, 0, 1, 1, 3, 1);
        constraints.anchor = GridBagConstraints.WEST;
        addBeginTimeBox(gridbag, constraints);

        ConstraintsBuilder.buildConstraints(constraints, 8, 0, 1, 1, 5, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addSeparator1(gridbag, constraints);

        ConstraintsBuilder.buildConstraints(constraints, 9, 0, 1, 1, 5, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addLanguageLabel(gridbag, constraints);

        ConstraintsBuilder.buildConstraints(constraints, 10, 0, 1, 1, 15, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addLanguageSelector(gridbag, constraints);

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 1, 1, 5, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        addEndDateLabel(gridbag, constraints);

        ConstraintsBuilder.buildConstraints(constraints, 1, 1, 4, 1, 25, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addEndDateBox(gridbag, constraints);

        ConstraintsBuilder.buildConstraints(constraints, 5, 1, 1, 1, 2, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addEndDateSelectorButton(gridbag, constraints);

        ConstraintsBuilder.buildConstraints(constraints, 6, 1, 1, 1, 25, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addEndTimeLabel(gridbag, constraints);

        ConstraintsBuilder.buildConstraints(constraints, 7, 1, 1, 1, 3, 1);
        constraints.anchor = GridBagConstraints.WEST;
        addEndTimeBox(gridbag, constraints);

        ConstraintsBuilder.buildConstraints(constraints, 0, 2, 13, 1, 5, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addWeatherLabel(gridbag, constraints);

        ConstraintsBuilder.buildConstraints(constraints, 0, 3, 13, 1, 15, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addWeatherTextArea(gridbag, constraints);

        ConstraintsBuilder.buildConstraints(constraints, 0, 4, 13, 1, 5, 1);
        addEquipmentLabel(gridbag, constraints);

        ConstraintsBuilder.buildConstraints(constraints, 0, 5, 13, 1, 15, 1);
        addEquipmentTextArea(gridbag, constraints);

        ConstraintsBuilder.buildConstraints(constraints, 0, 6, 13, 1, 5, 1);
        addCommentsLabel(gridbag, constraints);

        ConstraintsBuilder.buildConstraints(constraints, 0, 7, 13, 1, 15, 1);
        addCommentsTextArea(gridbag, constraints);

        ConstraintsBuilder.buildConstraints(constraints, 0, 8, 13, 1, 15, 1);
        addSeparator(gridbag, constraints);

        final boolean readOnly = this.session != null && !this.isEditable();
        if (readOnly) {
            showNonEditableData(gridbag, constraints);
        } else { // Create or edit
            showEditableData(gridbag, constraints);

        }

    }

    private void addSeparator(GridBagLayout gridbag, GridBagConstraints constraints) {
        JSeparator seperator1 = new JSeparator(SwingConstants.HORIZONTAL);
        gridbag.setConstraints(seperator1, constraints);
        this.add(seperator1);
    }

    private void addCommentsTextArea(GridBagLayout gridbag, GridBagConstraints constraints) {
        this.comments = new JTextArea(3, 40);
        this.comments.addMouseListener(this);
        this.comments.setLineWrap(true);
        this.comments.setToolTipText(AbstractPanel.bundle.getString("panel.session.tooltip.comments"));
        JScrollPane commentsScroll = new JScrollPane(this.comments);
        commentsScroll.setMinimumSize(new Dimension(300, 60));
        gridbag.setConstraints(commentsScroll, constraints);
        this.add(commentsScroll);
    }

    private void addCommentsLabel(GridBagLayout gridbag, GridBagConstraints constraints) {
        OMLabel Lcomments = new OMLabel(AbstractPanel.bundle.getString("panel.session.label.comments"), false);
        Lcomments.setToolTipText(AbstractPanel.bundle.getString("panel.session.tooltip.comments"));
        gridbag.setConstraints(Lcomments, constraints);
        this.add(Lcomments);
    }

    private void addEquipmentTextArea(GridBagLayout gridbag, GridBagConstraints constraints) {
        this.equipment = new JTextArea(3, 30);
        this.equipment.addMouseListener(this);
        this.equipment.setLineWrap(true);
        this.equipment.setToolTipText(AbstractPanel.bundle.getString("panel.session.tooltip.equipment"));
        JScrollPane equipmentScroll = new JScrollPane(this.equipment);
        equipmentScroll.setMinimumSize(new Dimension(300, 60));
        gridbag.setConstraints(equipmentScroll, constraints);
        this.add(equipmentScroll);
    }

    private void addEquipmentLabel(GridBagLayout gridbag, GridBagConstraints constraints) {
        OMLabel Lequipment = new OMLabel(AbstractPanel.bundle.getString("panel.session.label.equipment"), false);
        Lequipment.setToolTipText(AbstractPanel.bundle.getString("panel.session.tooltip.equipment"));
        gridbag.setConstraints(Lequipment, constraints);
        this.add(Lequipment);
    }

    private void addWeatherTextArea(GridBagLayout gridbag, GridBagConstraints constraints) {
        ConstraintsBuilder.buildConstraints(constraints, 0, 3, 13, 1, 15, 1);
        this.weather = new JTextArea(3, 30);
        this.weather.setLineWrap(true);
        this.weather.setToolTipText(AbstractPanel.bundle.getString("panel.session.tooltip.weather"));
        JScrollPane weatherScroll = new JScrollPane(this.weather);
        weatherScroll.setMinimumSize(new Dimension(300, 60));
        gridbag.setConstraints(weatherScroll, constraints);
        this.add(weatherScroll);
    }

    private void addWeatherLabel(GridBagLayout gridbag, GridBagConstraints constraints) {
        OMLabel Lweather = new OMLabel(AbstractPanel.bundle.getString("panel.session.label.weather"), false);
        Lweather.setToolTipText(AbstractPanel.bundle.getString("panel.session.tooltip.weather"));
        gridbag.setConstraints(Lweather, constraints);
        this.add(Lweather);
    }

    private void addEndTimeBox(GridBagLayout gridbag, GridBagConstraints constraints) {
        if (this.isEditable()) {
            this.endNow = new JButton(AbstractPanel.bundle.getString("panel.observation.button.endNow"));
            this.endNow.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.endNow"));
            this.endNow.addActionListener(this);
            gridbag.setConstraints(this.endNow, constraints);
            this.add(this.endNow);
        }
    }

    private void addEndTimeLabel(GridBagLayout gridbag, GridBagConstraints constraints) {
        this.endTime = new TimeContainer(0, 0, 0, this.isEditable());
        this.endTime.setToolTipText(AbstractPanel.bundle.getString("panel.session.tooltip.end"));
        gridbag.setConstraints(this.endTime, constraints);
        this.add(this.endTime);
    }

    private void addEndDateBox(GridBagLayout gridbag, GridBagConstraints constraints) {
        this.end = new JTextField(8);
        this.end.setToolTipText(AbstractPanel.bundle.getString("panel.session.tooltip.end"));
        this.end.setEditable(false);
        gridbag.setConstraints(this.end, constraints);
        this.add(this.end);

    }

    private void addEndDateSelectorButton(GridBagLayout gridbag, GridBagConstraints constraints) {
        if (this.isEditable()) {

            this.endPicker = new JButton("...");
            this.endPicker.setToolTipText(AbstractPanel.bundle.getString("panel.session.tooltip.selectEndDate"));
            this.endPicker.addActionListener(this);
            gridbag.setConstraints(this.endPicker, constraints);
            this.add(this.endPicker);
        }
    }

    private void addEndDateLabel(GridBagLayout gridbag, GridBagConstraints constraints) {
        OMLabel Lend = new OMLabel(AbstractPanel.bundle.getString("panel.session.label.end"), SwingConstants.LEFT,
                true);
        Lend.setToolTipText(AbstractPanel.bundle.getString("panel.session.tooltip.end"));
        gridbag.setConstraints(Lend, constraints);
        this.add(Lend);
    }

    private void addLanguageSelector(GridBagLayout gridbag, GridBagConstraints constraints) {
        this.language.setToolTipText(AbstractPanel.bundle.getString("panel.session.tooltip.language"));
        this.language.setEnabled(this.isEditable());
        gridbag.setConstraints(this.language, constraints);
        this.add(this.language);
    }

    private void addSeparator1(GridBagLayout gridbag, GridBagConstraints constraints) {
        JLabel Ldummy = new JLabel(" ");
        gridbag.setConstraints(Ldummy, constraints);
        this.add(Ldummy);
    }

    private void addLanguageLabel(GridBagLayout gridbag, GridBagConstraints constraints) {
        OMLabel Llanguage = new OMLabel(AbstractPanel.bundle.getString("panel.session.label.language"),
                SwingConstants.RIGHT, false);
        Llanguage.setToolTipText(AbstractPanel.bundle.getString("panel.session.tooltip.language"));
        gridbag.setConstraints(Llanguage, constraints);
        this.add(Llanguage);
    }

    private void addBeginTimeBox(GridBagLayout gridbag, GridBagConstraints constraints) {
        if (this.isEditable()) {
            this.beginNow = new JButton(AbstractPanel.bundle.getString("panel.observation.button.beginNow"));
            this.beginNow.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.beginNow"));
            this.beginNow.addActionListener(this);
            gridbag.setConstraints(this.beginNow, constraints);
            this.add(this.beginNow);
        }
    }

    private void addBeginTimeLabel(GridBagLayout gridbag, GridBagConstraints constraints) {
        this.beginTime = new TimeContainer(0, 0, 0, this.isEditable());
        this.beginTime.setToolTipText(AbstractPanel.bundle.getString("panel.session.tooltip.begin"));
        gridbag.setConstraints(this.beginTime, constraints);
        this.add(this.beginTime);
    }

    private void addBeginDateBox(GridBagLayout gridbag, GridBagConstraints constraints) {
        this.begin = new JTextField(8);
        this.begin.setToolTipText(AbstractPanel.bundle.getString("panel.session.tooltip.begin"));
        this.begin.setEditable(false);

        gridbag.setConstraints(this.begin, constraints);

        this.add(this.begin);

    }

    private void addBeginDateSelectorButton(GridBagLayout gridbag, GridBagConstraints constraints) {
        if (this.isEditable()) {
            this.beginPicker = new JButton("...");
            this.beginPicker.setToolTipText(AbstractPanel.bundle.getString("panel.session.tooltip.selectStartDate"));
            this.beginPicker.addActionListener(this);

            gridbag.setConstraints(this.beginPicker, constraints);

            this.add(this.beginPicker);
        }
    }

    private void addBeginDateLabel(GridBagLayout gridbag, GridBagConstraints constraints) {

        OMLabel Lbegin = new OMLabel(AbstractPanel.bundle.getString("panel.session.label.begin"), true);
        Lbegin.setToolTipText(AbstractPanel.bundle.getString("panel.session.tooltip.begin"));

        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(Lbegin, constraints);
        this.add(Lbegin);
    }

    private void showEditableData(GridBagLayout gridbag, GridBagConstraints constraints) {

        ConstraintsBuilder.buildConstraints(constraints, 0, 10, 1, 1, 5, 1);
        addSiteLabel(gridbag, constraints);
        ConstraintsBuilder.buildConstraints(constraints, 0, 11, 9, 1, 5, 1);
        addSiteSelectBox(gridbag, constraints);
        ConstraintsBuilder.buildConstraints(constraints, 9, 11, 4, 1, 10, 1);
        addNewSiteButton(gridbag, constraints);

        ConstraintsBuilder.buildConstraints(constraints, 0, 12, 13, 1, 15, 1);
        addSeparator2(gridbag, constraints);
        ConstraintsBuilder.buildConstraints(constraints, 0, 13, 13, 1, 15, 1);
        addObserversLabel(gridbag, constraints);
        ConstraintsBuilder.buildConstraints(constraints, 0, 14, 9, 1, 10, 1);
        addObserversTextBox(gridbag, constraints);
        ConstraintsBuilder.buildConstraints(constraints, 9, 14, 2, 1, 5, 1);
        addObserversSelectButton(gridbag, constraints);

        ConstraintsBuilder.buildConstraints(constraints, 11, 14, 2, 1, 5, 1);
        addNewObserverButton(gridbag, constraints);

        ConstraintsBuilder.buildConstraints(constraints, 0, 15, 13, 1, 2, 1);
        addImagesLabel(gridbag, constraints);

        ConstraintsBuilder.buildConstraints(constraints, 0, 16, 11, 4, 1, 100);
        constraints.fill = GridBagConstraints.BOTH;
        addImagesPanel(gridbag, constraints);

        ConstraintsBuilder.buildConstraints(constraints, 11, 16, 2, 1, 1, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.CENTER;
        addNewImageButton(gridbag, constraints);

    }

    private void addNewImageButton(GridBagLayout gridbag, GridBagConstraints constraints) {

        this.newImage = new JButton(AbstractPanel.bundle.getString("panel.session.button.newImages"));
        this.newImage.setToolTipText(AbstractPanel.bundle.getString("panel.session.tooltip.newImages"));
        this.newImage.addActionListener(this);
        gridbag.setConstraints(this.newImage, constraints);
        this.add(this.newImage);
    }

    private void addImagesPanel(GridBagLayout gridbag, GridBagConstraints constraints) {
        this.imageContainer = new ImageContainer(null, this.observationManager,
                this.observationManager.getConfiguration(), this.model, true,
                this.observationManager.getImageResolver());
        JScrollPane imageContainerScroll = new JScrollPane(this.imageContainer,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        gridbag.setConstraints(imageContainerScroll, constraints);
        imageContainerScroll.setMinimumSize(new Dimension(this.getWidth(), 130));
        this.add(imageContainerScroll);
    }

    private void addImagesLabel(GridBagLayout gridbag, GridBagConstraints constraints) {
        OMLabel LimageContainer = new OMLabel(AbstractPanel.bundle.getString("panel.session.label.images"), false);
        LimageContainer.setToolTipText(AbstractPanel.bundle.getString("panel.session.tooltip.images"));
        gridbag.setConstraints(LimageContainer, constraints);
        this.add(LimageContainer);
    }

    private void addNewObserverButton(GridBagLayout gridbag, GridBagConstraints constraints) {
        this.newCoObservers = new JButton(AbstractPanel.bundle.getString("panel.session.label.newObserver"));
        this.newCoObservers.setToolTipText(AbstractPanel.bundle.getString("panel.session.tooltip.newObserver"));
        this.newCoObservers.addActionListener(this);
        gridbag.setConstraints(this.newCoObservers, constraints);
        this.add(this.newCoObservers);
    }

    private void addObserversSelectButton(GridBagLayout gridbag, GridBagConstraints constraints) {
        this.selectCoObservers = new JButton(AbstractPanel.bundle.getString("panel.session.label.selectCoObserver"));
        this.selectCoObservers.setToolTipText(AbstractPanel.bundle.getString("panel.session.tooltip.selectCoObserver"));
        this.selectCoObservers.addActionListener(this);
        gridbag.setConstraints(this.selectCoObservers, constraints);
        this.add(this.selectCoObservers);
    }

    private void addObserversTextBox(GridBagLayout gridbag, GridBagConstraints constraints) {
        this.coObservers.setToolTipText(AbstractPanel.bundle.getString("panel.session.tooltip.addObservers"));
        this.coObservers.setEditable(false);
        if (this.session != null) {
            this.fillCoObserverTextField(this.session.getCoObservers());
        }
        gridbag.setConstraints(this.coObservers, constraints);
        this.add(this.coObservers);
    }

    private void addObserversLabel(GridBagLayout gridbag, GridBagConstraints constraints) {
        OMLabel LcoObservers = new OMLabel(AbstractPanel.bundle.getString("panel.session.label.addObservers"), false);
        LcoObservers.setToolTipText(AbstractPanel.bundle.getString("panel.session.tooltip.addObservers"));
        gridbag.setConstraints(LcoObservers, constraints);
        this.add(LcoObservers);
    }

    private void addSeparator2(GridBagLayout gridbag, GridBagConstraints constraints) {
        JSeparator seperator2 = new JSeparator(SwingConstants.HORIZONTAL);
        gridbag.setConstraints(seperator2, constraints);
        this.add(seperator2);
    }

    private void addNewSiteButton(GridBagLayout gridbag, GridBagConstraints constraints) {
        this.newSite = new JButton(AbstractPanel.bundle.getString("panel.session.label.newSite"));
        gridbag.setConstraints(this.newSite, constraints);
        this.newSite.setToolTipText(AbstractPanel.bundle.getString("panel.session.tooltip.newSite"));
        this.newSite.addActionListener(this);
        this.add(this.newSite);
    }

    private void addSiteSelectBox(GridBagLayout gridbag, GridBagConstraints constraints) {
        this.createSiteDropDownBox();
        gridbag.setConstraints(siteBox, constraints);
        siteBox.setToolTipText(AbstractPanel.bundle.getString("panel.session.tooltip.site"));
        this.add(siteBox);
    }

    private void addSiteLabel(GridBagLayout gridbag, GridBagConstraints constraints) {
        OMLabel Lsite = new OMLabel(AbstractPanel.bundle.getString("panel.session.label.site"), false);
        Lsite.setToolTipText(AbstractPanel.bundle.getString("panel.session.tooltip.site"));
        gridbag.setConstraints(Lsite, constraints);
        this.add(Lsite);
    }

    private void showNonEditableData(GridBagLayout gridbag, GridBagConstraints constraints) {
        ConstraintsBuilder.buildConstraints(constraints, 0, 10, 13, 1, 15, 1);
        addSitePanel(gridbag, constraints);

        int yPos = 10;
        if (!(this.session.getCoObservers().isEmpty())) {
            yPos = addNewSeparator(gridbag, constraints, yPos);
            yPos = addObserverTabbedPanel(gridbag, constraints, yPos);
            this.createObserverPanel();

        }

        addImagesScrollPanel(gridbag, constraints, yPos);

    }

    private void addImagesScrollPanel(GridBagLayout gridbag, GridBagConstraints constraints, int yPos) {
        ConstraintsBuilder.buildConstraints(constraints, 0, ++yPos, 13, 1, 15, 30);
        constraints.fill = GridBagConstraints.BOTH;
        this.imageContainer = new ImageContainer(null, this.observationManager,
                this.observationManager.getConfiguration(), this.model, false,
                this.observationManager.getImageResolver());
        JScrollPane imageContainerScroll = new JScrollPane(this.imageContainer,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        imageContainerScroll.setBorder(
                BorderFactory.createTitledBorder(AbstractPanel.bundle.getString("panel.observationItem.label.images")));
        imageContainerScroll.setToolTipText(AbstractPanel.bundle.getString("panel.observationItem.tooltip.images"));
        gridbag.setConstraints(imageContainerScroll, constraints);
        // Make sure size of scroll container can handle image thumbnail
        imageContainerScroll.setPreferredSize(this.imageContainer.getPreferredSize());
        this.add(imageContainerScroll);
    }

    private int addObserverTabbedPanel(GridBagLayout gridbag, GridBagConstraints constraints, int yPos) {
        ConstraintsBuilder.buildConstraints(constraints, 0, ++yPos, 13, 1, 15, 45);
        this.coObserverTabbedPane = new JTabbedPane(SwingConstants.TOP);
        this.coObserverTabbedPane.setBorder(
                BorderFactory.createTitledBorder(AbstractPanel.bundle.getString("panel.session.label.addObservers")));
        this.coObserverTabbedPane.setToolTipText(AbstractPanel.bundle.getString("panel.session.tooltip.addObservers"));
        JScrollPane scrollPane = new JScrollPane(this.coObserverTabbedPane,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        gridbag.setConstraints(scrollPane, constraints);
        this.add(scrollPane);
        return yPos;
    }

    private int addNewSeparator(GridBagLayout gridbag, GridBagConstraints constraints, int yPos) {
        ConstraintsBuilder.buildConstraints(constraints, 0, ++yPos, 13, 1, 15, 1);
        JSeparator seperator2 = new JSeparator(SwingConstants.HORIZONTAL);
        gridbag.setConstraints(seperator2, constraints);
        this.add(seperator2);
        return yPos;
    }

    private void addSitePanel(GridBagLayout gridbag, GridBagConstraints constraints) {
        SitePanel sitePanel = new SitePanel(this.session.getSite(), false);
        gridbag.setConstraints(sitePanel, constraints);
        sitePanel.setToolTipText(AbstractPanel.bundle.getString("panel.session.tooltip.site"));
        sitePanel.setBorder(
                BorderFactory.createTitledBorder(AbstractPanel.bundle.getString("panel.session.label.site")));
        this.add(sitePanel);
    }

    private void fillCoObserverTextField(List<IObserver> coObservers) {

        this.coObservers.setText("");
        Iterator<IObserver> iterator = coObservers.iterator();
        IObserver current = null;
        while (iterator.hasNext()) {
            current = (IObserver) iterator.next();
            coObserversList.add(current);
            this.addCoObserverToTextfield(current.getDisplayName());
        }

    }

    private void addCoObserverToTextfield(String coObserver) {

        String t = this.coObservers.getText();
        t = coObserver + "; " + t;
        this.coObservers.setText(t);

    }

    private void createObserverPanel() {

        Iterator<IObserver> iterator = this.session.getCoObservers().iterator();
        IObserver current = null;
        while (iterator.hasNext()) {
            current = iterator.next();
            ObserverPanel observerPanel = new ObserverPanel(current, false);
            this.coObserverTabbedPane.add(current.getDisplayName(), observerPanel);
        }

    }

    private void createSiteDropDownBox() {

        this.siteBox = new SiteBox();

        ISite[] sites = this.model.getSites();
        for (ISite site : sites) {
            this.siteBox.addItem(site);
        }

        if (this.session != null) {
            this.siteBox.setSelectedItem(this.session.getSite());
        } else {
            this.siteBox.setSelectedItem(null);
        }

    }

    private String formatDate(OffsetDateTime cal) {
        return this.dateManager.offsetDateTimeToString(cal);
    }

    private void addNewImages() {

        JFileChooser chooser = new JFileChooser();
        FileFilter imageFileFilter = new FileFilter() {
            @Override
            public boolean accept(File f) {
                return (f.getName().toLowerCase().endsWith(".jpg")) || (f.getName().toLowerCase().endsWith(".jpeg"))
                        || (f.getName().toLowerCase().endsWith(".gif")) || (f.getName().toLowerCase().endsWith(".png"))
                        || (f.getName().toLowerCase().endsWith(".fits")) || (f.getName().toLowerCase().endsWith(".fit"))
                        || (f.getName().toLowerCase().endsWith(".fts")) || (f.isDirectory());
            }

            @Override
            public String getDescription() {
                return AbstractPanel.bundle.getString("panel.observation.addNewImages.fileSelector.description");
            }
        };
        chooser.setFileFilter(imageFileFilter);
        chooser.setMultiSelectionEnabled(true);
        String pathname = this.cache.getString(ObservationDialogPanel.CACHEKEY_LASTIMAGEDIR);
        if (pathname != null) {
            File last = new File(pathname);
            if ((last != null) && (last.exists()) && (last.isDirectory())) {
                chooser.setCurrentDirectory(last);
            }
        }
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File[] files = chooser.getSelectedFiles();

            Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
            setCursor(hourglassCursor);

            List<String> images = new ArrayList<>(files.length);
            for (File file : files) {
                images.add(file.getAbsolutePath());
            }
            this.imageContainer.addImagesFromPath(images);
            this.repaint();
            this.updateUI();

            if (files.length > 0) {
                this.cache.putString(ObservationDialogPanel.CACHEKEY_LASTIMAGEDIR,
                        files[0].getParentFile().getAbsolutePath());
            }

            Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
            setCursor(normalCursor);
        }

    }

}
