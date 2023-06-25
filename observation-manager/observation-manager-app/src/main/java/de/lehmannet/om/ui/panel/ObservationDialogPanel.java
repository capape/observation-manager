/*
 * ====================================================================
 * /panel/ObservationDialogPanel.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.panel;

import static de.lehmannet.om.ICloneable.copyOrNull;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import de.lehmannet.om.IEyepiece;
import de.lehmannet.om.IFilter;
import de.lehmannet.om.IFinding;
import de.lehmannet.om.IImager;
import de.lehmannet.om.ILens;
import de.lehmannet.om.IObservation;
import de.lehmannet.om.IObserver;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.IScope;
import de.lehmannet.om.ISession;
import de.lehmannet.om.ISite;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.Observation;
import de.lehmannet.om.SurfaceBrightness;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.box.OMComboBox;
import de.lehmannet.om.ui.box.SessionBox;
import de.lehmannet.om.ui.cache.UIDataCache;
import de.lehmannet.om.ui.container.ImageContainer;
import de.lehmannet.om.ui.container.SurfaceBrightnessContainer;
import de.lehmannet.om.ui.container.TimeContainer;
import de.lehmannet.om.ui.dialog.CatalogDialog;
import de.lehmannet.om.ui.dialog.EyepieceDialog;
import de.lehmannet.om.ui.dialog.FilterDialog;
import de.lehmannet.om.ui.dialog.IImagerDialog;
import de.lehmannet.om.ui.dialog.ITargetDialog;
import de.lehmannet.om.ui.dialog.LensDialog;
import de.lehmannet.om.ui.dialog.ObserverDialog;
import de.lehmannet.om.ui.dialog.ScopeDialog;
import de.lehmannet.om.ui.dialog.SessionDialog;
import de.lehmannet.om.ui.dialog.SiteDialog;
import de.lehmannet.om.ui.extension.SchemaUILoader;
import de.lehmannet.om.ui.i18n.TextManager;
import de.lehmannet.om.ui.image.ImageResolver;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.util.ConfigKey;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.DatePicker;
import de.lehmannet.om.ui.util.ExtenableSchemaElementSelector;
import de.lehmannet.om.ui.util.IConfiguration;
import de.lehmannet.om.ui.util.LocaleToolsFactory;
import de.lehmannet.om.ui.util.OMLabel;
import de.lehmannet.om.util.FloatUtil;
import de.lehmannet.om.util.OpticsUtil;
import de.lehmannet.om.util.SchemaElementConstants;

public class ObservationDialogPanel extends AbstractPanel implements ActionListener, ItemListener, ChangeListener {

    private static final long serialVersionUID = -6652173787359719576L;

    // Keys for UI Data Cache
    private static final String CACHEKEY_STARTDATE = "ObservationDialogPanel.startdate";
    private static final String CACHEKEY_OBSERVER = "ObservationDialogPanel.observer";
    private static final String CACHEKEY_SESSION = "ObservationDialogPanel.session";
    private static final String CACHEKEY_SCOPE = "ObservationDialogPanel.scope";
    private static final String CACHEKEY_SITE = "ObservationDialogPanel.site";
    private static final String CACHEKEY_ENDDATE = "ObservationDialogPanel.enddate";
    private static final String CACHEKEY_FAINTESTSTAR = "ObservationDialogPanel.fainteststar";
    private static final String CACHEKEY_SQM = "ObservationDialogPanel.sqmValue";
    private static final String CACHEKEY_SEEING = "ObservationDialogPanel.seeing";
    private static final String CACHEKEY_ACCESSORIES = "ObservationDialogPanel.accessories";
    public static final String CACHEKEY_LASTIMAGEDIR = "ObservationDialogPanel.lastImageDir";
    public static final long OBSERVATION_DURATION_MINUTES = 10L;

    private ObservationManager observationManager = null;
    private IObservation observation = null;

    private JTabbedPane tabbedPane = null;
    private JPanel selectionPanel = null;
    private AbstractPanel findingsPanel = null;

    private final OMComboBox<IObserver> observerBox = new OMComboBox<>();
    private JButton newObserver = null;
    private final OMComboBox<ITarget> targetBox = new OMComboBox<>();
    private JButton newTarget = null;
    private JButton selectTarget = null;
    private final SessionBox sessionBox;
    private JButton newSession = null;
    private final OMComboBox<IScope> scopeBox = new OMComboBox<>();
    private JButton newScope = null;
    private final OMComboBox<IEyepiece> eyepieceBox = new OMComboBox<>();
    private final JSlider eyepieceFLSlider = new JSlider(JSlider.HORIZONTAL);
    private JButton newEyepiece = null;
    private final OMComboBox<ILens> lensBox = new OMComboBox<>();
    private JButton newLens = null;
    private final OMComboBox<IFilter> filterBox = new OMComboBox<>();
    private JButton newFilter = null;
    private final OMComboBox<ISite> siteBox = new OMComboBox<>();
    private JButton newSite = null;
    private final OMComboBox<IImager> imagerBox = new OMComboBox<>();
    private JButton newImager = null;
    private ImageContainer imageContainer = null;
    private JButton newImage = null;

    private JTextField begin = null;
    private ZonedDateTime beginDate = null;
    private OffsetDateTime offSetBeginDate = null;
    private JButton beginPicker = null;
    private TimeContainer beginTime = null;
    private JButton beginNow = null;
    private JTextField end = null;
    private ZonedDateTime endDate = null;
    private JButton endPicker = null;
    private TimeContainer endTime = null;
    private JButton endNow = null;
    private JButton clearEndDateAndTime = null;
    private JTextField faintestStar = null;
    private SurfaceBrightnessContainer sqmValue = null;
    private JTextField magnification = null;
    private JComboBox<SeeingBoxEntry> seeing = null;
    private JTextField accessories = null;

    private final ImageResolver imageResolver;
    private final ObservationManagerModel model;
    private final TextManager textManager;
    private final UIDataCache cache;

    private OffsetDateTime offSetEndDate;

    // Requires ObservationManager for instancating all dialoges
    // Receives (non-persistent) cache in order to preset some UI values with recent
    // values
    public ObservationDialogPanel(ObservationManager om, ObservationManagerModel model, TextManager textManager,
            IObservation observation, ISchemaElement se, ImageResolver resolver, UIDataCache uiCache) {

        super(true);

        this.setVisible(true);

        this.observationManager = om;
        this.model = model;
        this.imageResolver = resolver;
        this.observation = copyOrNull(observation);
        this.textManager = textManager;

        // TODO IOC
        this.sessionBox = new SessionBox(this.observationManager.getDateManager());

        this.cache = uiCache;

        this.tabbedPane = new JTabbedPane(SwingConstants.TOP);

        // First fill lists, then create panel! Otherwise panel tries to load
        // wrong findings which causes NullPointerException as type is wrong/cannot be
        // found
        this.fillLists(false);
        this.createPanel();

        if (this.observation != null) {
            this.loadSchemaElement();
        } else if (this.cache != null) { // Create new observation and cache is passed
            this.loadFromCache();
        }

        // Set preselected elements
        if (se != null) {
            if (se instanceof ISession) {
                this.sessionBox.setSelectedItem(se);
            } else if (se instanceof IObserver) {
                this.observerBox.setSelectedItem(se);
            } else if (se instanceof ITarget) {
                ITarget target = (ITarget) se;
                this.targetBox.addItem(target);
                // Set Finding Tab in Observation Dialog
                this.setFindingPanel(target);
            } else if (se instanceof IScope) {
                this.scopeBox.setSelectedItem(se);
            } else if (se instanceof IEyepiece) {
                this.eyepieceBox.setSelectedItem(se);
            } else if (se instanceof ILens) {
                this.lensBox.setSelectedItem(se);
            } else if (se instanceof IFilter) {
                this.filterBox.setSelectedItem(se);
            } else if (se instanceof ISite) {
                this.siteBox.setSelectedItem(se);
            } else if (se instanceof IImager) {
                this.imagerBox.setSelectedItem(se);
            } else if (se instanceof IObservation) {
                IObservation o = (IObservation) se;

                if (o.getAccessories() != null) {
                    this.accessories.setText(o.getAccessories());
                }
                if (o.getEyepiece() != null) {
                    this.eyepieceBox.setSelectedItem(o.getEyepiece());
                }
                if (!Float.isNaN(o.getFaintestStar())) {
                    this.faintestStar.setText("" + o.getFaintestStar());
                }
                if (o.getFilter() != null) {
                    this.filterBox.setSelectedItem(o.getFilter());
                }
                if (o.getImager() != null) {
                    this.imagerBox.setSelectedItem(o.getImager());
                }
                if (o.getLens() != null) {
                    this.lensBox.setSelectedItem(o.getLens());
                }
                if (o.getObserver() != null) {
                    this.observerBox.setSelectedItem(o.getObserver());
                }
                if (o.getScope() != null) {
                    this.scopeBox.setSelectedItem(o.getScope());
                }
                if (o.getSeeing() != -1) {
                    this.seeing.setSelectedIndex(o.getSeeing());
                }
                if (o.getSite() != null) {
                    this.siteBox.setSelectedItem(o.getSite());
                }
                if (o.getSession() != null) {
                    this.sessionBox.setSelectedItem(o.getSession());
                }
                if (o.getTarget() != null) {
                    this.targetBox.addItem(o.getTarget());
                    // Set Finding Tab in Observation Dialog
                    this.setFindingPanel(o.getTarget());
                }
                if (o.getSkyQuality() != null) {
                    this.sqmValue.setSurfaceBrightness(o.getSkyQuality());
                }
            }
        }

        // DebugUI.showGridToDebug(selectionPanel);

    }

    @Override
    public ISchemaElement getSchemaElement() {

        return copyOrNull(this.observation);

    }

    @Override
    public ISchemaElement updateSchemaElement() {

        if (this.observation == null) {
            return null;
        }

        // Get mandatory fields
        IFinding finding = this.getFinding();
        if (finding == null) {
            this.createWarning(AbstractPanel.bundle.getString("panel.observation.warning.noFinding"));
            return null;
        }

        ITarget target = this.getTarget();
        if (target == null) {
            this.createWarning(AbstractPanel.bundle.getString("panel.observation.warning.noTarget"));
            return null;
        }

        IObserver observer = this.getObserver();
        if (observer == null) {
            this.createWarning(AbstractPanel.bundle.getString("panel.observation.warning.noObserver"));
            return null;
        }

        if (this.beginTime.checkTime()) {
            this.createWarning(AbstractPanel.bundle.getString("panel.observation.warning.wrongStartTime"));
            return null;
        }

        if (this.endTime.checkTime()) {
            this.createWarning(AbstractPanel.bundle.getString("panel.observation.warning.wrongEndTime"));
            return null;
        }

        if (this.beginDate == null) {
            this.createWarning(AbstractPanel.bundle.getString("panel.observation.warning.noStartTime"));
            return null;
        }

        this.observation.setResult(finding);
        this.observation.setTarget(target);
        this.observation.setObserver(observer);
        this.cache.putObserver(ObservationDialogPanel.CACHEKEY_OBSERVER, observer); // Fill cache

        // Try to get and set timezone
        ISite site = this.siteBox.getSelectedSchemaElement();
        if (site == null) {
            ISession session = this.sessionBox.getSelectedSchemaElement();
            if (session != null) {
                site = session.getSite();
            }
        }

        this.beginDate = createDateTimeInUTC(this.beginDate, this.beginTime);
        this.offSetBeginDate = this.beginDate.withZoneSameInstant(ZoneId.of("UTC")).toOffsetDateTime();

        this.observation.setBegin(this.offSetBeginDate);
        this.cache.putDate(ObservationDialogPanel.CACHEKEY_STARTDATE, this.beginDate); // Fill cache

        // Set optional fields

        // Setting of end date must be before setting of session, as otherwise check in
        // setting session (start-end-date) will fail
        this.cache.remove(ObservationDialogPanel.CACHEKEY_ENDDATE); // Reset cache
        if (this.endDate != null) {

            this.endDate = createDateTimeInUTC(this.endDate, this.endTime);
            this.offSetEndDate = this.endDate.withZoneSameInstant(ZoneId.of("UTC")).toOffsetDateTime();

            if (this.endDate.isBefore(this.beginDate)) {
                this.createWarning(AbstractPanel.bundle.getString("panel.observation.warning.endBeforeStart"));
                return null;
            }
            this.cache.putDate(ObservationDialogPanel.CACHEKEY_ENDDATE, this.endDate);
            this.observation.setEnd(this.offSetEndDate);
        } else {
            this.observation.setEnd(null);
        }

        this.cache.remove(ObservationDialogPanel.CACHEKEY_SESSION); // Reset cache
        try {
            // Check whether observer is not listed as coObserver
            ISession session = this.sessionBox.getSelectedSchemaElement();
            if ((session != null) && (session.getCoObservers() != null) && !(session.getCoObservers().isEmpty())
                    && (session.getCoObservers().contains(observer))) {
                JOptionPane pane = new JOptionPane(
                        AbstractPanel.bundle.getString("panel.observation.warning.coObserverIsObserver"),
                        JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
                JDialog dialog = pane.createDialog(this,
                        AbstractPanel.bundle.getString("panel.observation.warning.coObserverIsObserver.title"));
                dialog.setVisible(true);
                Object selectedValue = pane.getValue();
                if ((selectedValue instanceof Integer)) {
                    if ((Integer) selectedValue == JOptionPane.NO_OPTION) {
                        return null; // User want's to rework settings
                    }
                }
            }

            this.observation.setSession((ISession) this.sessionBox.getSelectedSchemaElement());
            this.cache.putSession(ObservationDialogPanel.CACHEKEY_SESSION, this.observation.getSession());
        } catch (IllegalArgumentException iae) {
            this.createWarning(AbstractPanel.bundle.getString("panel.observation.warning.wrongTimeForSession"));
            return null;
        }

        this.cache.remove(ObservationDialogPanel.CACHEKEY_SCOPE); // Reset cache
        this.observation.setScope(this.scopeBox.getSelectedSchemaElement());
        this.cache.putScope(ObservationDialogPanel.CACHEKEY_SCOPE, this.observation.getScope());

        this.observation.setEyepiece(this.eyepieceBox.getSelectedSchemaElement());

        this.observation.setFilter(this.filterBox.getSelectedSchemaElement());

        // this.observation.setSite((ISite)this.siteBox.getSelectedSchemaElement());
        this.cache.remove(ObservationDialogPanel.CACHEKEY_SITE); // Reset cache
        this.observation.setSite(site);
        this.cache.putSite(ObservationDialogPanel.CACHEKEY_SITE, site);

        this.observation.setImager(this.imagerBox.getSelectedSchemaElement());

        this.observation.setLens(this.lensBox.getSelectedSchemaElement());

        this.cache.remove(ObservationDialogPanel.CACHEKEY_FAINTESTSTAR); // Reset cache
        String fs = this.faintestStar.getText();
        if ((fs != null) && !("".equals(fs.trim()))) {
            try {
                float faintest = FloatUtil.parseFloat(fs);
                this.observation.setFaintestStar(faintest);
                this.cache.putString(ObservationDialogPanel.CACHEKEY_FAINTESTSTAR, fs);
            } catch (NumberFormatException nfe) {
                this.createWarning(AbstractPanel.bundle.getString("panel.observation.warning.noNumberFaintestStar"));
                return null;
            }
        } else {
            this.observation.setFaintestStar(Float.NaN);
        }

        this.cache.remove(ObservationDialogPanel.CACHEKEY_SQM); // Reset cache
        SurfaceBrightness sqm = null;
        try {
            sqm = this.sqmValue.getSurfaceBrightness();
        } catch (NumberFormatException nfe) {
            this.createWarning(AbstractPanel.bundle.getString("panel.observation.warning.noNumberSQM"));
            return null;
        }
        if (sqm != null) {
            this.observation.setSkyQuality(sqm);
            this.cache.putSurfaceBrightness(ObservationDialogPanel.CACHEKEY_SQM, sqm);
        } else {
            this.observation.setSkyQuality(null);
        }

        String mg = this.magnification.getText();
        if ((mg != null) && !("".equals(mg.trim()))) {
            try {
                float mag = FloatUtil.parseFloat(mg);
                this.observation.setMagnification(mag);
            } catch (NumberFormatException nfe) {
                this.createWarning(AbstractPanel.bundle.getString("panel.observation.warning.noNumberMagnitude"));
                return null;
            }
        } else {
            this.observation.setMagnification(Float.NaN);
        }

        this.cache.remove(ObservationDialogPanel.CACHEKEY_SEEING); // Reset cache
        SeeingBoxEntry s = (SeeingBoxEntry) this.seeing.getSelectedItem();
        if (Objects.requireNonNull(s).isEmptyItem()) { // No seeing selected
            this.observation.setSeeing(-1);
        } else {
            this.observation.setSeeing(s.getValue());
            this.cache.putInteger(ObservationDialogPanel.CACHEKEY_SEEING, s.getValue());
        }

        this.observation.setAccessories(this.accessories.getText());

        this.observation
                .setImages(this.imageContainer.getImages(this.model.getXMLFileForSchemaElement(this.observation)));

        return copyOrNull(this.observation);

    }

    @Override
    public ISchemaElement createSchemaElement() {

        // Get mandatory fields
        IFinding finding = this.getFinding();
        if (finding == null) {
            this.createWarning(AbstractPanel.bundle.getString("panel.observation.warning.noFinding"));
            return null;
        }

        ITarget target = this.getTarget();
        if (target == null) {
            this.createWarning(AbstractPanel.bundle.getString("panel.observation.warning.noTarget"));
            return null;
        }

        IObserver observer = this.getObserver();
        if (observer == null) {
            this.createWarning(AbstractPanel.bundle.getString("panel.observation.warning.noObserver"));
            return null;
        }

        if (this.beginTime.checkTime()) {
            this.createWarning(AbstractPanel.bundle.getString("panel.observation.warning.wrongStartTime"));
            return null;
        }

        if (this.endTime.checkTime()) {
            this.createWarning(AbstractPanel.bundle.getString("panel.observation.warning.wrongEndTime"));
            return null;
        }

        if (this.beginDate == null) {
            this.createWarning(AbstractPanel.bundle.getString("panel.observation.warning.noStartTime"));
            return null;
        }

        this.beginDate = createDateTimeInUTC(this.beginDate, this.beginTime);

        this.observation = new Observation(this.beginDate, target, observer, finding);

        // Fill cache
        this.cache.putDate(ObservationDialogPanel.CACHEKEY_STARTDATE, this.beginDate);
        this.cache.putObserver(ObservationDialogPanel.CACHEKEY_OBSERVER, observer);

        // Set optional fields
        ISchemaElement se = null;

        // Set endDate here, as only setSession will fail if observation begin/end date
        // are not within sessions begin/end date
        this.cache.remove(ObservationDialogPanel.CACHEKEY_ENDDATE); // Reset cache
        if (this.endDate != null) {

            final ZonedDateTime newEndDate = createDateTimeInUTC(this.endDate, this.endTime);
            if (newEndDate.isBefore(this.beginDate)) {
                this.createWarning(AbstractPanel.bundle.getString("panel.observation.warning.endBeforeStart"));
                return null;
            }

            this.endDate = newEndDate;
            this.observation.setEnd(this.endDate.withZoneSameInstant(ZoneId.of("UTC")).toOffsetDateTime());
            this.cache.putDate(ObservationDialogPanel.CACHEKEY_ENDDATE, this.endDate);
        } else {
            this.observation.setEnd(null);
        }

        this.cache.remove(ObservationDialogPanel.CACHEKEY_SESSION); // Reset cache
        try {
            se = this.sessionBox.getSelectedSchemaElement();
            if (se != null) {
                // Check whether observer is not listed as coObserver
                ISession session = this.sessionBox.getSelectedSchemaElement();
                if ((session != null) && (session.getCoObservers() != null) && !(session.getCoObservers().isEmpty())
                        && (session.getCoObservers().contains(observer))) {
                    JOptionPane pane = new JOptionPane(
                            AbstractPanel.bundle.getString("panel.observation.warning.coObserverIsObserver"),
                            JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
                    JDialog dialog = pane.createDialog(this,
                            AbstractPanel.bundle.getString("panel.observation.warning.coObserverIsObserver.title"));
                    dialog.setVisible(true);
                    Object selectedValue = pane.getValue();
                    if ((selectedValue instanceof Integer)) {
                        if ((Integer) selectedValue == JOptionPane.NO_OPTION) {
                            return null; // User want's to rework settings
                        }
                    }
                }

                this.observation.setSession((ISession) se);
                this.cache.putSession(ObservationDialogPanel.CACHEKEY_SESSION, this.observation.getSession());
            }
        } catch (IllegalArgumentException iae) {
            this.createWarning(AbstractPanel.bundle.getString("panel.observation.warning.wrongTimeForSession"));
            return null;
        }

        this.cache.remove(ObservationDialogPanel.CACHEKEY_SCOPE); // Reset cache
        se = this.scopeBox.getSelectedSchemaElement();
        if (se != null) {
            this.observation.setScope((IScope) se);
            this.cache.putScope(ObservationDialogPanel.CACHEKEY_SCOPE, this.observation.getScope());
        }

        se = this.eyepieceBox.getSelectedSchemaElement();
        if (se != null) {
            this.observation.setEyepiece((IEyepiece) se);
        }

        se = this.lensBox.getSelectedSchemaElement();
        if (se != null) {
            this.observation.setLens((ILens) se);
        }

        se = this.filterBox.getSelectedSchemaElement();
        if (se != null) {
            this.observation.setFilter((IFilter) se);
        }

        this.cache.remove(ObservationDialogPanel.CACHEKEY_SITE); // Reset cache
        se = this.siteBox.getSelectedSchemaElement();
        if (se != null) {
            this.observation.setSite((ISite) se);
            this.cache.putSite(ObservationDialogPanel.CACHEKEY_SITE, this.observation.getSite());
        }

        se = this.imagerBox.getSelectedSchemaElement();
        if (se != null) {
            this.observation.setImager((IImager) se);
        }

        String fs = this.faintestStar.getText();
        this.cache.remove(ObservationDialogPanel.CACHEKEY_FAINTESTSTAR); // Reset cache
        if ((fs != null) && !("".equals(fs.trim()))) {
            try {
                float faintest = FloatUtil.parseFloat(fs);
                this.observation.setFaintestStar(faintest);
                this.cache.putFloat(ObservationDialogPanel.CACHEKEY_FAINTESTSTAR, faintest);
            } catch (NumberFormatException nfe) {
                this.createWarning(AbstractPanel.bundle.getString("panel.observation.warning.noNumberFaintestStar"));
                return null;
            }
        }

        SurfaceBrightness sqm = null;
        try {
            sqm = this.sqmValue.getSurfaceBrightness();
        } catch (NumberFormatException nfe) {
            this.createWarning(AbstractPanel.bundle.getString("panel.observation.warning.noNumberSQM"));
            return null;
        }
        this.cache.putSurfaceBrightness(ObservationDialogPanel.CACHEKEY_SQM, null); // Reset cache
        if (sqm != null) {
            this.observation.setSkyQuality(sqm);
            this.cache.putSurfaceBrightness(ObservationDialogPanel.CACHEKEY_SQM, sqm);
        }

        String mg = this.magnification.getText();
        if ((mg != null) && !("".equals(mg.trim()))) {
            try {
                float mag = FloatUtil.parseFloat(mg);
                this.observation.setMagnification(mag);
            } catch (NumberFormatException nfe) {
                this.createWarning(AbstractPanel.bundle.getString("panel.observation.warning.noNumberMagnitude"));
                return null;
            }
        }

        SeeingBoxEntry s = (SeeingBoxEntry) this.seeing.getSelectedItem();
        this.cache.putInteger(ObservationDialogPanel.CACHEKEY_SEEING, null); // Reset cache
        if (Objects.requireNonNull(s).isEmptyItem()) { // No seeing selected
            this.observation.setSeeing(-1);
        } else {
            this.observation.setSeeing(s.getValue());
            this.cache.putInteger(ObservationDialogPanel.CACHEKEY_SEEING, s.getValue());
        }

        /*
         * if( (s != null) && !("".equals(s.trim())) ) { try { int seeing = Integer.parseInt(s); try {
         * this.observation.setSeeing(seeing); } catch(IllegalArgumentException iae) {
         * this.createWarning(AbstractPanel.bundle.getString( "panel.observation.warning.invalidSeeing")); return null;
         * } this.cache.put(ObservationDialogPanel.CACHEKEY_SEEING, new Integer(s)); } catch(NumberFormatException nfe)
         * { this.createWarning(AbstractPanel.bundle.getString( "panel.observation.warning.noNumberSeeing")); return
         * null; } }
         */

        String ac = this.accessories.getText();
        if ((ac != null) && !("".equals(ac.trim()))) {
            this.observation.setAccessories(ac);
            this.cache.putString(ObservationDialogPanel.CACHEKEY_ACCESSORIES, ac);
        }

        this.observation
                .setImages(this.imageContainer.getImages(this.model.getXMLFileForSchemaElement(this.observation)));

        return copyOrNull(this.observation);

    }

    // --------------
    // ActionListener ---------------------------------------------------------
    // --------------

    @Override
    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();
        if (source instanceof JButton) {
            if (source.equals(this.newEyepiece)) {
                EyepieceDialog eyepieceDialog = new EyepieceDialog(this.observationManager, this.model, null);
                this.eyepieceBox.addItem(eyepieceDialog.getEyepiece());
            } else if (source.equals(this.newObserver)) {
                ObserverDialog observerDialog = new ObserverDialog(this.observationManager, this.model, null);
                this.observerBox.addItem(observerDialog.getObserver());
            } else if (source.equals(this.newScope)) {
                ScopeDialog scopeDialog = new ScopeDialog(this.observationManager, this.model, null);
                this.scopeBox.addItem(scopeDialog.getScope());
            } else if (source.equals(this.newFilter)) {
                FilterDialog filterDialog = new FilterDialog(this.observationManager, this.model, null);
                this.filterBox.addItem(filterDialog.getFilter());
            } else if (source.equals(this.newLens)) {
                LensDialog lensDialog = new LensDialog(this.observationManager, this.model, null);
                this.lensBox.addItem(lensDialog.getLens());
            } else if (source.equals(this.newSession)) {
                SessionDialog sessionDialog = new SessionDialog(this.observationManager, this.model, null, this.cache);
                this.sessionBox.addItem(sessionDialog.getSession());
                // In the session dialog there might be new observers
                // or sites be created, so refill lists
                this.fillLists(true);
            } else if (source.equals(this.newSite)) {
                SiteDialog siteDialog = new SiteDialog(this.observationManager, this.model, null);
                this.siteBox.addItem(siteDialog.getSite());
            } else if (source.equals(this.selectTarget)) {
                CatalogDialog cDialog = new CatalogDialog(this.observationManager, this.model, this.textManager);
                ITarget target = cDialog.getTarget();
                if (target != null) { // Check this as cancel might be pressed -> No target was created
                    this.targetBox.addItem(target);

                    // Set Finding Tab in Observation Dialog
                    this.setFindingPanel(target);
                }
            } else if (source.equals(this.newImager)) {
                ExtenableSchemaElementSelector imagerChooser = new ExtenableSchemaElementSelector(
                        this.observationManager, this.observationManager.getExtensionLoader().getSchemaUILoader(),
                        SchemaElementConstants.IMAGER);
                if (imagerChooser.getResult()) {
                    // Get ImagerDialog
                    IImagerDialog imagerDialog = (IImagerDialog) imagerChooser.getDialog();
                    IImager imager = imagerDialog.getImager();
                    if (imager != null) { // Check this as cancel might be pressed -> No imager was created
                        this.imagerBox.addItem(imager);
                    }
                }
            } else if (source.equals(this.newTarget)) {
                ExtenableSchemaElementSelector targetChooser = new ExtenableSchemaElementSelector(
                        this.observationManager, this.observationManager.getExtensionLoader().getSchemaUILoader(),
                        SchemaElementConstants.TARGET);
                if (targetChooser.getResult()) {
                    // Get TargetContainer
                    ITargetDialog targetDialog = (ITargetDialog) targetChooser.getDialog();
                    if (targetDialog != null) {
                        ITarget target = targetDialog.getTarget();
                        if (target != null) { // Check this as cancel might be pressed -> No target was created
                            this.targetBox.addItem(target);

                            // Set Finding Tab in Observation Dialog
                            this.setFindingPanel(target);
                        }
                    }
                }
            } else if (source.equals(this.beginPicker)) {
                this.readBeginDate();
            } else if (source.equals(this.endPicker)) {
                this.readEndDate();
            } else if (source.equals(this.newImage)) {
                this.addNewImages();
            } else if (source.equals(this.clearEndDateAndTime)) {
                this.end.setText("");
                this.endDate = null;
                this.endTime.setTime(0, 0, 0);
            } else if (source.equals(this.endNow)) {
                this.endDate = ZonedDateTime.now();
                this.endTime.setTime(this.endDate.getHour(), this.endDate.getMinute(), this.endDate.getSecond());
                this.end.setText(this.observationManager.getDateManager().zonedDateTimeToString(this.endDate));
            } else if (source.equals(this.beginNow)) {
                this.beginDate = ZonedDateTime.now();
                this.beginTime.setTime(this.beginDate.getHour(), this.beginDate.getMinute(),
                        this.beginDate.getSecond());
                this.begin.setText(this.observationManager.getDateManager().zonedDateTimeToString(this.beginDate));
            }
        } else {
            if (source.equals(this.newEyepiece) || source.equals(this.newScope))
                // As we've only added the ActionListener to ScopeBox and EyepieceBox
                // we don't need to do further checks here
                this.calculateMagnification(-1);
        }

    }

    private void readEndDate() {
        DatePicker dp = null;

        if (this.endDate != null) {
            dp = new DatePicker(this.observationManager,
                    AbstractPanel.bundle.getString("panel.observation.end.datePicker.title"), this.endDate,
                    this.observationManager.getDateManager());
        } else if (this.beginDate != null) { // Try to initialize endDate Picker with startdate
            dp = new DatePicker(this.observationManager,
                    AbstractPanel.bundle.getString("panel.observation.end.datePicker.title"), this.beginDate,
                    this.observationManager.getDateManager());
        } else {
            dp = new DatePicker(this.observationManager,
                    AbstractPanel.bundle.getString("panel.observation.end.datePicker.title"),
                    this.observationManager.getDateManager());
        }
        this.endDate = dp.getDate();
        this.endTime.setTime(this.endDate.getHour(), this.endDate.getMinute(), this.endDate.getSecond());
        this.end.setText(this.observationManager.getDateManager().zonedDateTimeToString(this.endDate));
    }

    private void readBeginDate() {
        DatePicker dp = null;

        if (this.beginDate != null) {
            dp = new DatePicker(this.observationManager,
                    AbstractPanel.bundle.getString("panel.observation.start.datePicker.title"), this.beginDate,
                    this.observationManager.getDateManager());
        } else {
            dp = new DatePicker(this.observationManager,
                    AbstractPanel.bundle.getString("panel.observation.start.datePicker.title"),
                    this.observationManager.getDateManager());
        }
        this.beginDate = dp.getDate();
        this.beginTime.setTime(this.beginDate.getHour(), this.beginDate.getMinute(), this.beginDate.getSecond());
        this.begin.setText(this.observationManager.getDateManager().zonedDateTimeToString(this.beginDate));
    }

    // --------------
    // ChangeListener ---------------------------------------------------------
    // --------------

    @Override
    public void stateChanged(ChangeEvent e) {

        if (e.getSource().equals(this.eyepieceFLSlider)) {
            int currentValue = this.eyepieceFLSlider.getValue();
            this.calculateMagnification(currentValue);
        }

    }

    // ------------
    // ItemListener -----------------------------------------------------------
    // ------------

    @Override
    public void itemStateChanged(ItemEvent e) {

        Object source = e.getSource();
        if (source.equals(this.targetBox)) {
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                OMComboBox<ITarget> tb = (OMComboBox<ITarget>) source;
                ITarget target = tb.getSelectedSchemaElement();
                this.setFindingPanel(target);
            }
        } else if (source.equals(this.sessionBox)) {
            SessionBox sb = (SessionBox) source;
            ISession session = sb.getSelectedSchemaElement();
            // Make sure that if session changes, the observations start and end date match
            if (e.getStateChange() == ItemEvent.SELECTED) {
                // && (this.observation == null) // (Autom. set date only in Creation mode. In
                // edit mode this would set session dates as begin and end)

                if (session != null) {

                    // Check if session really changed from observation session!
                    // E.g. we might be called by inital panel setup, where nothing changed at all
                    if (observation != null) {
                        if (session.equals(observation.getSession())) {
                            // As we unset the site below (in else case) we have to make sure it's set here
                            // again
                            // Test: Session and Site set. Switch Session to "----", -> Site will also be
                            // set to "----"
                            // Now set Session back to original value -> Site must be recovered as well
                            // Set site
                            if (session.getSite() != null) {
                                // this.siteBox.setSelectedItem(session.getSite());
                                this.siteBox.addItem(session.getSite());
                                this.siteBox.setEnabled(false);
                            }

                            return; // Nothing changed at all
                        }
                    }

                    // Clear cache and all preset fields if different session is choosen
                    if (this.cache.getSession(CACHEKEY_SESSION) != null) {
                        ISession cacheSession = this.cache.getSession(CACHEKEY_SESSION);
                        if (!cacheSession.equals(session)) {
                            this.clearCacheData();
                            // this.cache.clear();
                        }
                    }

                    this.beginDate = session.getBegin().toZonedDateTime().withZoneSameInstant(ZoneId.systemDefault());
                    this.begin.setText(this.observationManager.getDateManager().zonedDateTimeToString(beginDate));
                    this.beginTime.setTime(beginDate.getHour(), beginDate.getMinute(), beginDate.getSecond());

                    // Check whether enddate/time should be autom. set
                    if (Boolean.parseBoolean(this.observationManager.getConfiguration()
                            .getConfig(ConfigKey.CONFIG_RETRIEVE_ENDDATE_FROM_SESSION))) {
                        this.endDate = this.beginDate.plusMinutes(OBSERVATION_DURATION_MINUTES); // Add 10 minutes, as
                                                                                                 // end date should be
                                                                                                 // after
                        // begin date
                        this.end.setText(this.observationManager.getDateManager().zonedDateTimeToString(endDate));
                        this.endTime.setTime(this.endDate.getHour(), this.endDate.getMinute(),
                                this.endDate.getSecond());
                    }

                    // Set site
                    if (session.getSite() != null) {
                        // this.siteBox.setSelectedItem(session.getSite());
                        this.siteBox.addItem(session.getSite());
                        this.siteBox.setEnabled(false);
                    }

                } else {
                    this.siteBox.selectEmptyItem(); // Session was deselected, also deselect Site
                    this.siteBox.setEnabled(true);
                }
            }
        } else if (source.equals(this.eyepieceBox)) {
            IEyepiece eyepiece = this.eyepieceBox.getSelectedSchemaElement();

            // Always restore original layout first

            GridBagLayout gridbag = (GridBagLayout) this.selectionPanel.getLayout();
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.anchor = GridBagConstraints.WEST;
            constraints.fill = GridBagConstraints.HORIZONTAL;

            // Expand eyepiece box
            ConstraintsBuilder.buildConstraints(constraints, 2, 8, 14, 1, 6, 1);
            gridbag.setConstraints(this.eyepieceBox, constraints);

            // Remove Slider
            this.eyepieceFLSlider.setVisible(false);

            // Expand create new button
            ConstraintsBuilder.buildConstraints(constraints, 16, 8, 2, 1, 1, 1);
            gridbag.setConstraints(this.newEyepiece, constraints);

            this.updateUI();

            // Nothing to show...
            if (eyepiece == null) {
                return;
            }

            if (eyepiece.isZoomEyepiece()) {

                // Narrow eyepiece box
                ConstraintsBuilder.buildConstraints(constraints, 2, 8, 9, 1, 6, 1);
                gridbag.setConstraints(this.eyepieceBox, constraints);

                // Add/show Slider
                ConstraintsBuilder.buildConstraints(constraints, 11, 8, 4, 1, 2, 1);
                constraints.fill = GridBagConstraints.HORIZONTAL;
                gridbag.setConstraints(this.eyepieceFLSlider, constraints);
                this.eyepieceFLSlider.setMaximum((int) Math.ceil(eyepiece.getMaxFocalLength()));
                this.eyepieceFLSlider.setMinimum((int) Math.floor(eyepiece.getFocalLength()));
                this.eyepieceFLSlider.setVisible(true);

                // Narrow create new button
                ConstraintsBuilder.buildConstraints(constraints, 15, 8, 3, 1, 1, 1);
                gridbag.setConstraints(this.newEyepiece, constraints);

                this.updateUI();
            }
        }

    }

    // ---------------
    // Private Methods --------------------------------------------------------
    // ---------------

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
        File last = this.cache.getFile(ObservationDialogPanel.CACHEKEY_LASTIMAGEDIR);
        if ((last != null) && (last.exists()) && (last.isDirectory())) {
            chooser.setCurrentDirectory(last);
        }
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File[] files = chooser.getSelectedFiles();

            Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
            setCursor(hourglassCursor);

            List<File> images = Arrays.asList(files);
            this.imageContainer.addImages(images);
            this.repaint();

            if (files.length > 0) {
                this.cache.putFile(ObservationDialogPanel.CACHEKEY_LASTIMAGEDIR, files[0].getParentFile());
            }

            Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
            setCursor(normalCursor);
        }

    }

    private ITarget getTarget() {

        ITarget target = this.targetBox.getSelectedSchemaElement();
        if (target == null) {
            this.createWarning(AbstractPanel.bundle.getString("panel.observation.warning.noTarget"));
            return null;
        }

        return target;

    }

    private IObserver getObserver() {

        IObserver observer = this.observerBox.getSelectedSchemaElement();
        if (observer == null) {
            this.createWarning(AbstractPanel.bundle.getString("panel.observation.warning.noObserver"));
            return null;
        }

        return observer;

    }

    private IFinding getFinding() {

        if (this.findingsPanel == null) {
            this.createWarning(AbstractPanel.bundle.getString("panel.observation.warning.targetAndfinding"));
            return null;
        }

        return (IFinding) this.findingsPanel.createSchemaElement();

    }

    private void loadSchemaElement() {

        // Load mandatory stuff

        this.beginDate = this.observation.getBegin().toZonedDateTime().withZoneSameInstant(ZoneId.systemDefault());
        this.begin.setText(this.observationManager.getDateManager().zonedDateTimeToString(this.beginDate));
        this.beginPicker.setEnabled(this.isEditable());

        this.beginTime.setTime(this.beginDate.getHour(), this.beginDate.getMinute(), this.beginDate.getSecond());
        this.beginTime.setEditable(this.isEditable());

        this.observerBox.setSelectedItem(this.observation.getObserver());
        this.observerBox.setEditable(this.isEditable());
        this.newObserver.setEnabled(this.isEditable());

        this.targetBox.setSelectedItem(this.observation.getTarget());
        this.targetBox.setEditable(this.isEditable());
        this.newTarget.setEnabled(this.isEditable());

        this.setFindingPanel(this.observation.getTarget());

        // Load optional stuff
        if (this.observation.getEnd() != null) {
            this.endDate = this.observation.getEnd().toZonedDateTime().withZoneSameInstant(ZoneId.systemDefault());
        }
        if (this.endDate != null) {
            this.end.setText(this.observationManager.getDateManager().zonedDateTimeToString(this.endDate));
            this.endPicker.setEnabled(this.isEditable());
            this.endTime.setTime(this.endDate.getHour(), this.endDate.getMinute(), this.endDate.getSecond());
        }
        this.endTime.setEditable(this.isEditable());

        if (this.observation.getSeeing() != -1) {
            this.seeing.setSelectedItem(new SeeingBoxEntry(this.observation.getSeeing()));
        }
        // this.seeing.setEditable(this.isEditable());

        if (!Float.isNaN(this.observation.getMagnification())) {
            this.magnification.setText("" + this.observation.getMagnification());
        }
        this.magnification.setEditable(this.isEditable());

        if (!Float.isNaN(this.observation.getFaintestStar())) {
            this.faintestStar.setText("" + this.observation.getFaintestStar());
        }
        this.faintestStar.setEditable(this.isEditable());

        if (this.observation.getSkyQuality() != null) {
            this.sqmValue.setSurfaceBrightness(this.observation.getSkyQuality());
        }
        this.sqmValue.setEditable(this.isEditable());

        if (this.observation.getAccessories() != null) {
            this.accessories.setText(this.observation.getAccessories());
        }
        this.accessories.setEditable(this.isEditable());

        this.sessionBox.setEditable(this.isEditable());
        if (this.observation.getSession() != null) {
            this.sessionBox.setSelectedItem(this.observation.getSession());
        }
        this.newSession.setEnabled(this.isEditable());

        this.scopeBox.setEditable(this.isEditable());
        if (this.observation.getScope() != null) {
            this.scopeBox.setSelectedItem(this.observation.getScope());
        }
        this.newScope.setEnabled(this.isEditable());

        this.filterBox.setEditable(this.isEditable());
        if (this.observation.getFilter() != null) {
            this.filterBox.setSelectedItem(this.observation.getFilter());
        }
        this.newFilter.setEnabled(this.isEditable());

        this.lensBox.setEditable(this.isEditable());
        if (this.observation.getLens() != null) {
            this.lensBox.setSelectedItem(this.observation.getLens());
        }
        this.newLens.setEnabled(this.isEditable());

        this.eyepieceBox.setEditable(this.isEditable());
        if (this.observation.getEyepiece() != null) {
            this.eyepieceBox.setSelectedItem(this.observation.getEyepiece());
            if (this.observation.getEyepiece().isZoomEyepiece()) {
                float afl = OpticsUtil.getActualFocalLength(this.observation.getScope(), this.observation.getLens(),
                        this.observation.getMagnification());
                this.eyepieceFLSlider.setValue(Math.round(afl));
            }
        }
        this.newEyepiece.setEnabled(this.isEditable());

        this.siteBox.setEditable(this.isEditable());
        if (this.observation.getSite() != null) {
            this.siteBox.setSelectedItem(this.observation.getSite());
        }
        this.newSite.setEnabled(this.isEditable());

        this.imagerBox.setEditable(this.isEditable());
        if (this.observation.getImager() != null) {
            this.imagerBox.setSelectedItem(this.observation.getImager());
        }
        this.newImager.setEnabled(this.isEditable());

        this.imageContainer.addImagesFromPath(this.observation.getImages());

    }

    private void createPanel() {

        this.selectionPanel = new JPanel();

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.selectionPanel.setLayout(gridbag);

        sessionRow(gridbag, constraints);
        timeSessionRow(gridbag, constraints);
        starAndSeeingRow(gridbag, constraints);
        magnificationRow(gridbag, constraints);
        accesoriesRow(gridbag, constraints);
        observerRow(gridbag, constraints);
        targetRow(gridbag, constraints);
        telescopeRow(gridbag, constraints);
        eyepieceRow(gridbag, constraints);
        lenseRow(gridbag, constraints);
        filterRow(gridbag, constraints);
        siteRow(gridbag, constraints);
        imagerRow(gridbag, constraints);
        imagesRow(gridbag, constraints);

        this.tabbedPane.addTab(AbstractPanel.bundle.getString("panel.observation.mainPannel.title"),
                this.selectionPanel);

        this.setLayout(new BorderLayout());
        this.add(this.tabbedPane);

    }

    private void imagesRow(GridBagLayout gridbag, GridBagConstraints constraints) {
        ConstraintsBuilder.buildConstraints(constraints, 0, 13, 2, 1, 2, 1);
        OMLabel LimageContainer = new OMLabel(AbstractPanel.bundle.getString("panel.observation.label.images"), false);
        LimageContainer.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.images"));
        gridbag.setConstraints(LimageContainer, constraints);
        this.selectionPanel.add(LimageContainer);
        ConstraintsBuilder.buildConstraints(constraints, 2, 13, 14, 4, 1, 100);
        constraints.fill = GridBagConstraints.BOTH;
        this.imageContainer = new ImageContainer(null, this.observationManager,
                this.observationManager.getConfiguration(), this.model, true, this.imageResolver);
        JScrollPane imageContainerScroll = new JScrollPane(this.imageContainer,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        gridbag.setConstraints(imageContainerScroll, constraints);
        imageContainerScroll.setMinimumSize(new Dimension(this.getWidth(), 130));
        this.selectionPanel.add(imageContainerScroll);
        ConstraintsBuilder.buildConstraints(constraints, 16, 13, 0, 1, 1, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.newImage = new JButton(AbstractPanel.bundle.getString("panel.observation.button.newImages"));
        this.newImage.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.newImages"));
        this.newImage.addActionListener(this);
        gridbag.setConstraints(this.newImage, constraints);
        this.selectionPanel.add(this.newImage);
    }

    private void imagerRow(GridBagLayout gridbag, GridBagConstraints constraints) {
        ConstraintsBuilder.buildConstraints(constraints, 0, 12, 2, 1, 2, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        OMLabel LimagerName = new OMLabel(AbstractPanel.bundle.getString("panel.observation.label.imager"), false);
        LimagerName.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.imager"));
        gridbag.setConstraints(LimagerName, constraints);
        this.selectionPanel.add(LimagerName);
        ConstraintsBuilder.buildConstraints(constraints, 2, 12, 14, 1, 6, 1);
        this.imagerBox.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.imager"));
        gridbag.setConstraints(this.imagerBox, constraints);
        this.selectionPanel.add(this.imagerBox);
        ConstraintsBuilder.buildConstraints(constraints, 16, 12, 0, 1, 1, 1);
        this.newImager = new JButton(AbstractPanel.bundle.getString("panel.observation.button.newImager"));
        this.newImager.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.newImager"));
        this.newImager.addActionListener(this);
        gridbag.setConstraints(this.newImager, constraints);
        this.selectionPanel.add(this.newImager);
    }

    private void siteRow(GridBagLayout gridbag, GridBagConstraints constraints) {
        ConstraintsBuilder.buildConstraints(constraints, 0, 11, 2, 1, 2, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        OMLabel LsiteName = new OMLabel(AbstractPanel.bundle.getString("panel.observation.label.site"), false);
        LsiteName.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.site"));
        gridbag.setConstraints(LsiteName, constraints);
        this.selectionPanel.add(LsiteName);
        ConstraintsBuilder.buildConstraints(constraints, 2, 11, 14, 1, 6, 1);
        this.siteBox.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.site"));
        gridbag.setConstraints(this.siteBox, constraints);
        this.selectionPanel.add(this.siteBox);
        ConstraintsBuilder.buildConstraints(constraints, 16, 11, 0, 1, 1, 1);
        this.newSite = new JButton(AbstractPanel.bundle.getString("panel.observation.button.newSite"));
        this.newSite.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.newSite"));
        this.newSite.addActionListener(this);
        gridbag.setConstraints(this.newSite, constraints);
        this.selectionPanel.add(this.newSite);
    }

    private void filterRow(GridBagLayout gridbag, GridBagConstraints constraints) {
        ConstraintsBuilder.buildConstraints(constraints, 0, 10, 2, 1, 2, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        OMLabel LfilterName = new OMLabel(AbstractPanel.bundle.getString("panel.observation.label.filter"), false);
        LfilterName.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.filter"));
        gridbag.setConstraints(LfilterName, constraints);
        this.selectionPanel.add(LfilterName);
        ConstraintsBuilder.buildConstraints(constraints, 2, 10, 14, 1, 6, 1);
        this.filterBox.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.filter"));
        this.filterBox.addActionListener(this);
        gridbag.setConstraints(this.filterBox, constraints);
        this.selectionPanel.add(this.filterBox);
        ConstraintsBuilder.buildConstraints(constraints, 16, 10, 0, 1, 1, 1);
        this.newFilter = new JButton(AbstractPanel.bundle.getString("panel.observation.button.newFilter"));
        this.newFilter.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.newFilter"));
        this.newFilter.addActionListener(this);
        gridbag.setConstraints(this.newFilter, constraints);
        this.selectionPanel.add(this.newFilter);
    }

    private void lenseRow(GridBagLayout gridbag, GridBagConstraints constraints) {
        ConstraintsBuilder.buildConstraints(constraints, 0, 9, 2, 1, 2, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        OMLabel LlensName = new OMLabel(AbstractPanel.bundle.getString("panel.observation.label.lens"), false);
        LlensName.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.lens"));
        gridbag.setConstraints(LlensName, constraints);
        this.selectionPanel.add(LlensName);
        ConstraintsBuilder.buildConstraints(constraints, 2, 9, 14, 1, 6, 1);
        this.lensBox.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.lens"));
        this.lensBox.addActionListener(this);
        gridbag.setConstraints(this.lensBox, constraints);
        this.selectionPanel.add(this.lensBox);
        ConstraintsBuilder.buildConstraints(constraints, 16, 9, 0, 1, 1, 1);
        this.newLens = new JButton(AbstractPanel.bundle.getString("panel.observation.button.newLens"));
        this.newLens.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.newLens"));
        this.newLens.addActionListener(this);
        gridbag.setConstraints(this.newLens, constraints);
        this.selectionPanel.add(this.newLens);
    }

    private void eyepieceRow(GridBagLayout gridbag, GridBagConstraints constraints) {
        ConstraintsBuilder.buildConstraints(constraints, 0, 8, 2, 1, 2, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        OMLabel leyepieceName = new OMLabel(AbstractPanel.bundle.getString("panel.observation.label.eyepiece"), false);
        leyepieceName.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.eyepiece"));
        gridbag.setConstraints(leyepieceName, constraints);
        this.selectionPanel.add(leyepieceName);
        ConstraintsBuilder.buildConstraints(constraints, 2, 8, 14, 1, 6, 1);
        this.eyepieceBox.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.eyepiece"));
        this.eyepieceBox.addActionListener(this);
        gridbag.setConstraints(this.eyepieceBox, constraints);
        this.eyepieceBox.addItemListener(this);
        this.selectionPanel.add(this.eyepieceBox);
        // Add Slider (but don't show it
        ConstraintsBuilder.buildConstraints(constraints, 11, 8, 4, 1, 2, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(this.eyepieceFLSlider, constraints);
        this.eyepieceFLSlider.setMaximum(10);
        this.eyepieceFLSlider.setMinimum(1);
        this.eyepieceFLSlider.setMajorTickSpacing(1);
        this.eyepieceFLSlider.setPaintTicks(true);
        this.eyepieceFLSlider.setPaintLabels(true);
        this.eyepieceFLSlider.setSnapToTicks(true);
        this.eyepieceFLSlider.addChangeListener(this);
        this.selectionPanel.add(this.eyepieceFLSlider);
        this.eyepieceFLSlider.setVisible(false);
        // Add new button
        ConstraintsBuilder.buildConstraints(constraints, 16, 8, 0, 1, 1, 1);
        this.newEyepiece = new JButton(AbstractPanel.bundle.getString("panel.observation.button.newEyepiece"));
        this.newEyepiece.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.newEyepiece"));
        this.newEyepiece.addActionListener(this);
        gridbag.setConstraints(this.newEyepiece, constraints);
        this.selectionPanel.add(this.newEyepiece);
    }

    private void telescopeRow(GridBagLayout gridbag, GridBagConstraints constraints) {
        ConstraintsBuilder.buildConstraints(constraints, 0, 7, 2, 1, 2, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        OMLabel LscopeName = new OMLabel(AbstractPanel.bundle.getString("panel.observation.label.scope"), false);
        LscopeName.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.scope"));
        gridbag.setConstraints(LscopeName, constraints);
        this.selectionPanel.add(LscopeName);
        ConstraintsBuilder.buildConstraints(constraints, 2, 7, 14, 1, 6, 1);
        this.scopeBox.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.scope"));
        this.scopeBox.addActionListener(this);
        gridbag.setConstraints(this.scopeBox, constraints);
        this.selectionPanel.add(this.scopeBox);
        ConstraintsBuilder.buildConstraints(constraints, 16, 7, 0, 1, 1, 1);
        this.newScope = new JButton(AbstractPanel.bundle.getString("panel.observation.button.newScope"));
        this.newScope.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.newScope"));
        this.newScope.addActionListener(this);
        gridbag.setConstraints(this.newScope, constraints);
        this.selectionPanel.add(this.newScope);
    }

    private void targetRow(GridBagLayout gridbag, GridBagConstraints constraints) {
        ConstraintsBuilder.buildConstraints(constraints, 0, 6, 2, 1, 2, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        OMLabel LtargetName = new OMLabel(AbstractPanel.bundle.getString("panel.observation.label.target"), true);
        LtargetName.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.target"));
        gridbag.setConstraints(LtargetName, constraints);
        this.selectionPanel.add(LtargetName);
        ConstraintsBuilder.buildConstraints(constraints, 2, 6, 9, 1, 6, 1);
        this.targetBox.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.target"));
        this.targetBox.addItemListener(this);
        gridbag.setConstraints(this.targetBox, constraints);
        this.selectionPanel.add(this.targetBox);
        ConstraintsBuilder.buildConstraints(constraints, 11, 6, 4, 1, 1, 1);
        this.selectTarget = new JButton(
                AbstractPanel.bundle.getString("panel.observation.button.selectTargetfromCatalog"));
        this.selectTarget
                .setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.selectTargetfromCatalog"));
        this.selectTarget.addActionListener(this);
        // Check if there are catalogs installed. If not, disable button
        if ((this.observationManager.getExtensionLoader().getCatalogLoader().getCatalogNames() == null)
                || (this.observationManager.getExtensionLoader().getCatalogLoader().getCatalogNames().length == 0)) {
            this.selectTarget.setEnabled(false);
        }
        gridbag.setConstraints(this.selectTarget, constraints);
        this.selectionPanel.add(this.selectTarget);
        ConstraintsBuilder.buildConstraints(constraints, 15, 6, 0, 1, 1, 1);
        this.newTarget = new JButton(AbstractPanel.bundle.getString("panel.observation.button.newTarget"));
        this.newTarget.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.newTarget"));
        this.newTarget.addActionListener(this);
        gridbag.setConstraints(this.newTarget, constraints);
        this.selectionPanel.add(this.newTarget);
    }

    private void observerRow(GridBagLayout gridbag, GridBagConstraints constraints) {
        ConstraintsBuilder.buildConstraints(constraints, 0, 5, 2, 1, 2, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        OMLabel LobserverName = new OMLabel(AbstractPanel.bundle.getString("panel.observation.label.observer"), true);
        LobserverName.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.observer"));
        gridbag.setConstraints(LobserverName, constraints);
        this.selectionPanel.add(LobserverName);
        ConstraintsBuilder.buildConstraints(constraints, 2, 5, 14, 1, 6, 1);
        this.observerBox.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.observer"));
        gridbag.setConstraints(this.observerBox, constraints);
        this.selectionPanel.add(this.observerBox);
        ConstraintsBuilder.buildConstraints(constraints, 16, 5, 0, 1, 1, 1);
        this.newObserver = new JButton(AbstractPanel.bundle.getString("panel.observation.button.newObserver"));
        this.newObserver.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.newObserver"));
        this.newObserver.addActionListener(this);
        gridbag.setConstraints(this.newObserver, constraints);
        this.selectionPanel.add(this.newObserver);
    }

    private void accesoriesRow(GridBagLayout gridbag, GridBagConstraints constraints) {
        ConstraintsBuilder.buildConstraints(constraints, 0, 4, 1, 1, 3, 1);
        OMLabel LAccessories = new OMLabel(AbstractPanel.bundle.getString("panel.observation.label.accessories"),
                false);
        LAccessories.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.accessories"));
        gridbag.setConstraints(LAccessories, constraints);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.selectionPanel.add(LAccessories);
        ConstraintsBuilder.buildConstraints(constraints, 1, 4, 0, 1, 6, 1);
        this.accessories = new JTextField();
        this.accessories.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.accessories"));
        gridbag.setConstraints(this.accessories, constraints);
        this.selectionPanel.add(this.accessories);
    }

    private void magnificationRow(GridBagLayout gridbag, GridBagConstraints constraints) {
        ConstraintsBuilder.buildConstraints(constraints, 0, 3, 2, 1, 3, 1);
        OMLabel LMagnification = new OMLabel(AbstractPanel.bundle.getString("panel.observation.label.magnification"),
                false);
        LMagnification.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.magnification"));
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(LMagnification, constraints);
        this.selectionPanel.add(LMagnification);
        ConstraintsBuilder.buildConstraints(constraints, 2, 3, 6, 1, 6, 1);
        this.magnification = new JTextField();
        this.magnification.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.magnification"));
        gridbag.setConstraints(this.magnification, constraints);
        this.selectionPanel.add(this.magnification);

        ConstraintsBuilder.buildConstraints(constraints, 8, 3, 2, 1, 1, 1);
        OMLabel Lsqm = new OMLabel(AbstractPanel.bundle.getString("panel.observation.label.sqm"), SwingConstants.RIGHT,
                false);
        Lsqm.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.sqm"));
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(Lsqm, constraints);
        this.selectionPanel.add(Lsqm);
        ConstraintsBuilder.buildConstraints(constraints, 10, 3, 0, 1, 6, 1);
        this.sqmValue = new SurfaceBrightnessContainer(null, true,
                new String[] { SurfaceBrightness.MAGS_SQR_ARC_MIN, SurfaceBrightness.MAGS_SQR_ARC_SEC });
        this.sqmValue.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.sqm"));
        gridbag.setConstraints(this.sqmValue, constraints);
        this.selectionPanel.add(this.sqmValue);
    }

    private void sessionRow(GridBagLayout gridbag, GridBagConstraints constraints) {
        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 2, 1, 2, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        OMLabel LsessionName = new OMLabel(AbstractPanel.bundle.getString("panel.observation.label.session"), false);
        LsessionName.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.session"));
        gridbag.setConstraints(LsessionName, constraints);
        this.selectionPanel.add(LsessionName);
        ConstraintsBuilder.buildConstraints(constraints, 2, 0, 15, 1, 6, 1);
        this.sessionBox.addItemListener(this);
        this.sessionBox.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.session"));
        gridbag.setConstraints(this.sessionBox, constraints);
        this.selectionPanel.add(this.sessionBox);
        ConstraintsBuilder.buildConstraints(constraints, 17, 0, 0, 1, 1, 1);
        this.newSession = new JButton(AbstractPanel.bundle.getString("panel.observation.button.newSession"));
        this.newSession.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.newSession"));
        this.newSession.addActionListener(this);
        gridbag.setConstraints(this.newSession, constraints);
        this.selectionPanel.add(this.newSession);
    }

    private void starAndSeeingRow(GridBagLayout gridbag, GridBagConstraints constraints) {
        ConstraintsBuilder.buildConstraints(constraints, 0, 2, 2, 1, 3, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        OMLabel LfaintestStar = new OMLabel(AbstractPanel.bundle.getString("panel.observation.label.faintestStar"),
                false);
        LfaintestStar.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.faintestStar"));
        gridbag.setConstraints(LfaintestStar, constraints);
        this.selectionPanel.add(LfaintestStar);
        ConstraintsBuilder.buildConstraints(constraints, 2, 2, 7, 1, 6, 1);
        this.faintestStar = new JTextField();
        this.faintestStar.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.faintestStar"));
        gridbag.setConstraints(this.faintestStar, constraints);
        this.selectionPanel.add(this.faintestStar);

        ConstraintsBuilder.buildConstraints(constraints, 9, 2, 1, 1, 1, 1);
        OMLabel LSeeing = new OMLabel(AbstractPanel.bundle.getString("panel.observation.label.seeing"),
                SwingConstants.RIGHT, false);
        LSeeing.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.seeing"));
        gridbag.setConstraints(LSeeing, constraints);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.selectionPanel.add(LSeeing);
        ConstraintsBuilder.buildConstraints(constraints, 10, 2, 0, 1, 6, 1);
        this.seeing = new JComboBox<SeeingBoxEntry>();
        this.seeing.setEditable(false);
        this.fillSeeingBox();
        this.seeing.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.seeing"));
        gridbag.setConstraints(this.seeing, constraints);
        this.selectionPanel.add(this.seeing);
    }

    private void timeSessionRow(GridBagLayout gridbag, GridBagConstraints constraints) {
        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 1, 1, 2, 1);
        constraints.anchor = GridBagConstraints.WEST;
        OMLabel Lbegin = new OMLabel(AbstractPanel.bundle.getString("panel.observation.label.begin"), true);
        Lbegin.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.begin"));
        gridbag.setConstraints(Lbegin, constraints);
        this.selectionPanel.add(Lbegin);

        ConstraintsBuilder.buildConstraints(constraints, 1, 1, 4, 1, 5, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.begin = new JTextField(6);
        this.begin.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.begin"));
        this.begin.setEditable(false);
        gridbag.setConstraints(this.begin, constraints);
        this.selectionPanel.add(this.begin);
        if (this.isEditable()) {
            ConstraintsBuilder.buildConstraints(constraints, 5, 1, 1, 1, 1, 1);
            constraints.fill = GridBagConstraints.NONE;
            this.beginPicker = new JButton("...");
            this.beginPicker.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.selectBegin"));
            this.beginPicker.addActionListener(this);
            gridbag.setConstraints(this.beginPicker, constraints);
            this.selectionPanel.add(this.beginPicker);
        }
        ConstraintsBuilder.buildConstraints(constraints, 6, 1, 2, 1, 2, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.beginTime = new TimeContainer(0, 0, 0, this.isEditable());
        this.beginTime.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.begin"));
        gridbag.setConstraints(this.beginTime, constraints);
        this.selectionPanel.add(this.beginTime);

        ConstraintsBuilder.buildConstraints(constraints, 8, 1, 1, 1, 1, 1);
        this.beginNow = new JButton(AbstractPanel.bundle.getString("panel.observation.button.beginNow"));
        this.beginNow.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.beginNow"));
        this.beginNow.addActionListener(this);
        gridbag.setConstraints(this.beginNow, constraints);
        this.selectionPanel.add(this.beginNow);

        ConstraintsBuilder.buildConstraints(constraints, 9, 1, 1, 1, 1, 1);
        constraints.anchor = GridBagConstraints.EAST;
        OMLabel Lend = new OMLabel(AbstractPanel.bundle.getString("panel.observation.label.end"), SwingConstants.RIGHT,
                false);
        Lend.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.end"));
        gridbag.setConstraints(Lend, constraints);

        this.selectionPanel.add(Lend);

        ConstraintsBuilder.buildConstraints(constraints, 10, 1, 2, 1, 5, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.end = new JTextField(6);
        this.end.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.end"));
        this.end.setEditable(false);
        constraints.anchor = GridBagConstraints.WEST;
        gridbag.setConstraints(this.end, constraints);
        this.selectionPanel.add(this.end);
        if (this.isEditable()) {
            ConstraintsBuilder.buildConstraints(constraints, 12, 1, 1, 1, 1, 1);
            constraints.fill = GridBagConstraints.NONE;
            this.endPicker = new JButton("...");
            this.endPicker.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.selectEnd"));
            this.endPicker.addActionListener(this);
            gridbag.setConstraints(this.endPicker, constraints);
            this.selectionPanel.add(this.endPicker);
        }
        ConstraintsBuilder.buildConstraints(constraints, 13, 1, 2, 1, 5, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.endTime = new TimeContainer(0, 0, 0, this.isEditable());
        this.endTime.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.end"));
        gridbag.setConstraints(this.endTime, constraints);
        this.selectionPanel.add(this.endTime);

        ConstraintsBuilder.buildConstraints(constraints, 15, 1, 2, 1, 2, 1);
        this.endNow = new JButton(AbstractPanel.bundle.getString("panel.observation.button.endNow"));
        this.endNow.setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.endNow"));
        this.endNow.addActionListener(this);
        gridbag.setConstraints(this.endNow, constraints);
        this.selectionPanel.add(this.endNow);

        ConstraintsBuilder.buildConstraints(constraints, 17, 1, 0, 1, 1, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        this.clearEndDateAndTime = new JButton();
        this.clearEndDateAndTime
                .setText(AbstractPanel.bundle.getString("panel.observation.button.clearEndDateAndTime"));
        this.clearEndDateAndTime
                .setToolTipText(AbstractPanel.bundle.getString("panel.observation.tooltip.clearEndDateAndTime"));
        this.clearEndDateAndTime.addActionListener(this);
        gridbag.setConstraints(this.clearEndDateAndTime, constraints);
        this.selectionPanel.add(this.clearEndDateAndTime);
    }

    private void fillLists(boolean refill) {

        // Used as cache for selected value in case of refill
        ISchemaElement element = null;

        if (refill) {
            element = this.observerBox.getSelectedSchemaElement();
        }
        IConfiguration config = this.observationManager.getConfiguration();
        String currentValue = config.getConfig(ConfigKey.CONFIG_DEFAULT_OBSERVER);

        IObserver[] observers = this.model.getObservers();
        IObserver defaultObserver = null;
        for (IObserver observer : observers) {
            this.observerBox.addItem(observer);
            if ((currentValue != null) && (currentValue.equals(observer.getDisplayName()))) {
                defaultObserver = observer;
            }
        }
        if (defaultObserver != null) {
            this.observerBox.setSelectedItem(defaultObserver);
        }

        if (refill) {
            this.observerBox.setSelectedItem(element);
        }

        // ---------------------------------------------

        if (refill) {
            element = this.targetBox.getSelectedSchemaElement();
        }
        ITarget[] targets = this.model.getTargets();
        for (ITarget target : targets) {
            if (target.getObserver() != null) {
                this.targetBox.addItem(target);
            }
            if (this.observation != null) { // In edit mode, add current target to list
                this.targetBox.addItem(this.observation.getTarget());
            }

            // if( this.observation == null) { // Only show non-catalog targets (on create
            // new observation)
            // if( targets[i].getObserver() != null ) {
            // this.targetBox.addItem(targets[i]);
            // }
            // } else { // In edit case show all targets
            // this.targetBox.addItem(targets[i]);
            // }
        }
        if (refill) {
            this.targetBox.setSelectedItem(element);
        } else {
            this.targetBox.addEmptyItem();
        }

        // ---------------------------------------------

        if (refill) {
            element = this.sessionBox.getSelectedSchemaElement();
        }
        ISession[] session = this.model.getSessions();
        for (ISession iSession : session) {
            this.sessionBox.addItem(iSession);
        }
        if (refill) {
            this.sessionBox.setSelectedItem(element);
        } else {
            this.sessionBox.addEmptyItem();
        }

        // ---------------------------------------------

        if (refill) {
            element = this.scopeBox.getSelectedSchemaElement();
        }
        IScope[] scopes = this.model.getScopes();
        for (IScope scope : scopes) {
            if (this.observation == null) { // In create mode only show still active equipment
                if (scope.isAvailable()) {
                    this.scopeBox.addItem(scope);
                }
            } else { // In edit mode show also deactivated equipment
                this.scopeBox.addItem(scope);
            }
        }
        if (refill) {
            this.scopeBox.setSelectedItem(element);
        } else {
            this.scopeBox.addEmptyItem();
        }

        // ---------------------------------------------

        if (refill) {
            element = this.eyepieceBox.getSelectedSchemaElement();
        }
        IEyepiece[] eyepieces = this.model.getEyepieces();
        for (IEyepiece eyepiece : eyepieces) {
            if (this.observation == null) { // In create mode only show still active equipment
                if (eyepiece.isAvailable()) {
                    this.eyepieceBox.addItem(eyepiece);
                }
            } else { // In edit mode show also deactivated equipment
                this.eyepieceBox.addItem(eyepiece);
            }
        }
        if (refill) {
            this.eyepieceBox.setSelectedItem(element);
        } else {
            this.eyepieceBox.addEmptyItem();
        }

        // ---------------------------------------------

        if (refill) {
            element = this.lensBox.getSelectedSchemaElement();
        }
        ILens[] lenses = this.model.getLenses();
        for (ILens lens : lenses) {
            if (this.observation == null) { // In create mode only show still active equipment
                if (lens.isAvailable()) {
                    this.lensBox.addItem(lens);
                }
            } else { // In edit mode show also deactivated equipment
                this.lensBox.addItem(lens);
            }
        }
        if (refill) {
            this.lensBox.setSelectedItem(element);
        } else {
            this.lensBox.addEmptyItem();
        }

        // ---------------------------------------------

        if (refill) {
            element = this.filterBox.getSelectedSchemaElement();
        }
        IFilter[] filters = this.model.getFilters();
        for (IFilter filter : filters) {
            if (this.observation == null) { // In create mode only show still active equipment
                if (filter.isAvailable()) {
                    this.filterBox.addItem(filter);
                }
            } else { // In edit mode show also deactivated equipment
                this.filterBox.addItem(filter);
            }
        }
        if (refill) {
            this.filterBox.setSelectedItem(element);
        } else {
            this.filterBox.addEmptyItem();
        }

        // ---------------------------------------------

        if (refill) {
            element = this.siteBox.getSelectedSchemaElement();
        }
        ISite[] sites = this.model.getSites();
        for (ISite site : sites) {
            this.siteBox.addItem(site);
        }
        if (refill) {
            this.siteBox.setSelectedItem(element);
        } else {
            this.siteBox.addEmptyItem();
        }

        // ---------------------------------------------

        if (refill) {
            element = this.imagerBox.getSelectedSchemaElement();
        }
        IImager[] imagers = this.model.getImagers();
        for (IImager imager : imagers) {
            if (this.observation == null) { // In create mode only show still active equipment
                if (imager.isAvailable()) {
                    this.imagerBox.addItem(imager);
                }
            } else { // In edit mode show also deactivated equipment
                this.imagerBox.addItem(imager);
            }
        }
        if (refill) {
            this.imagerBox.setSelectedItem(element);
        } else {
            this.imagerBox.addEmptyItem();
        }

    }

    private void setFindingPanel(ITarget target) {

        if (this.getFindingPanel() != null) {
            this.tabbedPane.remove(this.getFindingPanel()); // Remove old from tabbedPanel
        }

        if (target == null) {
            return;
        }

        String xsi = target.getXSIType();

        SchemaUILoader loader = this.observationManager.getExtensionLoader().getSchemaUILoader();
        ISession session = null;
        if (this.sessionBox.getSelectedSchemaElement() != null) {
            session = this.sessionBox.getSelectedSchemaElement();
        }
        if (this.observation != null) {
            List<IFinding> findings = this.observation.getResults();
            if (findings.isEmpty()) {
                this.findingsPanel = loader.getFindingPanel(xsi, null, session, target, true);
                this.tabbedPane.addTab(this.findingsPanel.getName(), this.findingsPanel);
            } else {
                for (IFinding current : findings) {
                    this.findingsPanel = loader.getFindingPanel(xsi, current, session, target, true);
                    this.tabbedPane.addTab(this.findingsPanel.getName(), this.findingsPanel);
                }
            }
        } else {
            this.findingsPanel = loader.getFindingPanel(xsi, null, session, target, true);
            this.tabbedPane.addTab(this.findingsPanel.getName(), this.findingsPanel);
        }

        this.repaint();

    }

    private AbstractPanel getFindingPanel() {

        return this.findingsPanel;

    }

    private void calculateMagnification(float focalLength) {

        // Prepare output format once at the beginning
        DecimalFormat df = new DecimalFormat("0.00");
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(dfs);

        // First get scope and check it
        IScope scope = this.scopeBox.getSelectedSchemaElement();
        if (scope == null) {
            this.magnification.setEditable(true);
            this.magnification.setText(null);
            return;
        }

        // Scope has fixed magnification, nothing to calculate...
        if (!Float.isNaN(scope.getMagnification())) {
            this.magnification.setText(df.format(scope.getMagnification()));
            this.eyepieceBox.selectEmptyItem();
            this.eyepieceBox.setEnabled(false);
            this.lensBox.selectEmptyItem();
            this.lensBox.setEnabled(false);
            this.magnification.setEditable(false);
            return;
        }

        // As scope has variable focal length, reenable eyepiece and lens boxes
        this.magnification.setEditable(true);
        this.eyepieceBox.setEnabled(true);
        this.lensBox.setEnabled(true);

        // Get eyepiece focal length
        if (focalLength == -1) { // Get current eyepiece (shouldn't be a zoom eyepiece)
            IEyepiece eyep = this.eyepieceBox.getSelectedSchemaElement();
            if (eyep == null) { // Without eyepiece we cannot calculate anything
                this.magnification.setEnabled(true);
                this.magnification.setText(null);
                return;
            }
            focalLength = eyep.getFocalLength();
        }

        // Get lens
        ILens lens = this.lensBox.getSelectedSchemaElement();

        // Do calculations
        float m = OpticsUtil.getMagnification(scope, focalLength, lens);

        this.magnification.setEditable(false);
        this.magnification.setText(df.format(m));

    }

    private void loadFromCache() {

        // Set new begin date to last observation end date
        if (this.cache.getDate(CACHEKEY_ENDDATE) != null) {
            this.beginDate = this.cache.getDate(CACHEKEY_ENDDATE).withZoneSameInstant(ZoneId.systemDefault());
            this.begin.setText(this.observationManager.getDateManager().zonedDateTimeToString(this.beginDate));
            this.beginPicker.setEnabled(this.isEditable());
            this.beginTime.setTime(this.beginDate.getHour(), this.beginDate.getMinute(), this.beginDate.getSecond());
        }

        if (this.cache.getObserver(CACHEKEY_OBSERVER) != null) {
            this.observerBox.setSelectedItem(this.cache.getObserver(CACHEKEY_OBSERVER));
        }

        if (this.cache.getSite(CACHEKEY_SITE) != null) {
            this.siteBox.setSelectedItem(this.cache.getSite(CACHEKEY_SITE));
        }

        if (this.cache.getSession(CACHEKEY_SESSION) != null) {
            ISession session = this.cache.getSession(CACHEKEY_SESSION);
            this.sessionBox.setSelectedItem(session);
            // If session has own site overwrite previous value
            if (session.getSite() != null) {
                this.siteBox.setSelectedItem(session.getSite());
            }
        }

        if (this.cache.getScope(CACHEKEY_SCOPE) != null) {
            this.scopeBox.setSelectedItem(this.cache.getScope(CACHEKEY_SCOPE));
        }

        if (this.cache.getDate(CACHEKEY_ENDDATE) != null) {
            // Set end date to last observation end date +10 minutes
            this.endDate = this.cache.getDate(CACHEKEY_ENDDATE).withZoneSameInstant(ZoneId.systemDefault());
            this.endDate = this.endDate.plus(OBSERVATION_DURATION_MINUTES, ChronoUnit.MINUTES);
            this.end.setText(this.observationManager.getDateManager().zonedDateTimeToString(this.endDate));
            this.endPicker.setEnabled(this.isEditable());
            this.endTime.setTime(this.endDate.getHour(), this.endDate.getMinute(), this.endDate.getSecond());
        }

        if (this.cache.getFloat(CACHEKEY_FAINTESTSTAR) != null) {
            this.faintestStar.setText(String.valueOf(this.cache.getFloat(CACHEKEY_FAINTESTSTAR)));
        }

        if (this.cache.getSurfaceBrightness(CACHEKEY_SQM) != null) {
            this.sqmValue.setSurfaceBrightness(this.cache.getSurfaceBrightness(CACHEKEY_SQM));
        }

        if (this.cache.getInteger(CACHEKEY_SEEING) != null) {
            this.seeing.setSelectedItem(new SeeingBoxEntry(this.cache.getInteger(CACHEKEY_SEEING)));
        }

        if (this.cache.getString(CACHEKEY_ACCESSORIES) != null) {
            this.accessories.setText("" + this.cache.getString(CACHEKEY_ACCESSORIES));
        }

    }

    private void clearCacheData() {

        this.beginDate = null;
        this.cache.remove(ObservationDialogPanel.CACHEKEY_STARTDATE);

        // this.observerBox.selectEmptyItem(); Don't clear observer
        // this.sessionBox.selectEmptyItem(); Don' clear session, was we're called from
        // session changed
        // this.scopeBox.selectEmptyItem(); Don't clear scope

        this.siteBox.selectEmptyItem();
        this.cache.remove(ObservationDialogPanel.CACHEKEY_SITE);

        this.endDate = null;
        this.cache.remove(ObservationDialogPanel.CACHEKEY_ENDDATE);

        this.faintestStar.setText("");
        this.cache.remove(ObservationDialogPanel.CACHEKEY_FAINTESTSTAR);

        this.sqmValue.setSurfaceBrightness(null);
        this.cache.remove(ObservationDialogPanel.CACHEKEY_SQM);

        this.seeing.setSelectedItem(new SeeingBoxEntry(0));
        this.cache.remove(ObservationDialogPanel.CACHEKEY_SEEING);

        this.accessories.setText("");

    }

    private void fillSeeingBox() {

        // Set emptyItem
        SeeingBoxEntry emptyItem = new SeeingBoxEntry(0);
        this.seeing.addItem(emptyItem);

        this.seeing.addItem(new SeeingBoxEntry(1));
        this.seeing.addItem(new SeeingBoxEntry(2));
        this.seeing.addItem(new SeeingBoxEntry(3));
        this.seeing.addItem(new SeeingBoxEntry(4));
        this.seeing.addItem(new SeeingBoxEntry(5));

        // Select empty item
        this.seeing.setSelectedItem(emptyItem);

        // this.seeing.setPrototypeDisplayValue("WWWWWWWWWWWWWWWWWWWW");

    }

    private ZonedDateTime createDateTimeInUTC(ZonedDateTime date, TimeContainer timeContainer) {

        // Ensure time zone is default.
        ZonedDateTime dateInDefaultZone = ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());

        ZonedDateTime currentZoneDateTime = ZonedDateTime.of(dateInDefaultZone.getYear(),
                dateInDefaultZone.getMonthValue(), dateInDefaultZone.getDayOfMonth(), timeContainer.getHour(),
                timeContainer.getMinutes(), timeContainer.getSeconds(), 0, ZoneId.systemDefault());

        return ZonedDateTime.ofInstant(currentZoneDateTime.toInstant(), ZoneId.of("UTC"));

    }

}

class SeeingBoxEntry {

    private static final String BUNDLE_PREFIX = "seeing.antoniadi.short.";
    private final ResourceBundle bundle = LocaleToolsFactory.appInstance().getBundle("ObservationManager",
            Locale.getDefault());

    private static final String EMPTY_ENTRY = "----";

    private int value = 0; // 0 = Empty item

    public SeeingBoxEntry(int value) {

        if ((value < 0) || (value > 5)) {
            return;
        }

        this.value = value;

    }

    @Override
    public String toString() {

        if (this.value == 0) {
            return SeeingBoxEntry.EMPTY_ENTRY;
        }

        // Get I18N string for value
        return this.bundle.getString(BUNDLE_PREFIX + this.value);

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bundle == null) ? 0 : bundle.hashCode());
        result = prime * result + value;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SeeingBoxEntry other = (SeeingBoxEntry) obj;
        if (bundle == null) {
            if (other.bundle != null) {
                return false;
            }
        } else if (!bundle.equals(other.bundle)) {
            return false;
        }

        return value == other.value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SeeingBoxEntry other = (SeeingBoxEntry) obj;
        if (bundle == null) {
            if (other.bundle != null) {
                return false;
            }
        } else if (!bundle.equals(other.bundle)) {
            return false;
        }
        
        return value == other.value;
    }
    

    public int getValue() {

        return this.value;

    }

    public boolean isEmptyItem() {

        return this.value == 0;

    }

}