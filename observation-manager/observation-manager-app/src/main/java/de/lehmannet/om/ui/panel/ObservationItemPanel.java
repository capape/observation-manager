/* ====================================================================
 * /panel/ObservationItemPanel.java
 *
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.panel;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import de.lehmannet.om.Angle;
import de.lehmannet.om.EquPosition;
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
import de.lehmannet.om.SurfaceBrightness;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.container.AngleContainer;
import de.lehmannet.om.ui.container.HorizontalSkymap;
import de.lehmannet.om.ui.container.ImageContainer;
import de.lehmannet.om.ui.container.MoonDetailContainer;
import de.lehmannet.om.ui.container.SurfaceBrightnessContainer;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.OMLabel;
import de.lehmannet.om.util.DateConverter;
import de.lehmannet.om.util.Ephemerides;
import de.lehmannet.om.util.OpticsUtil;

public class ObservationItemPanel extends AbstractPanel {

    private static final long serialVersionUID = 1738702184674803563L;

    // Observations "own" fields
    private final JTextField begin = new JTextField(14);
    private final JTextField end = new JTextField(14);
    private final JTextField faintestStar = new JTextField(4);
    private final SurfaceBrightnessContainer sqm = new SurfaceBrightnessContainer(null, false,
            new String[] { SurfaceBrightness.MAGS_SQR_ARC_MIN, SurfaceBrightness.MAGS_SQR_ARC_SEC });
    private final JTextField magnification = new JTextField(4);
    private final JTextField seeing = new JTextField(2);
    private final JTextField images = new JTextField(15);
    private final JTextField accessories = new JTextField(15);

    // This might be calculated or retrieved from scope (with fixed focal length)
    private final AngleContainer trueFoV = new AngleContainer(Angle.ARCMINUTE, false);
    // This might be calculated or retrieved from scope and eyepiece
    private final JTextField exitPupil = new JTextField(4);

    // Linked elements
    private final JTextField imager = new JTextField();
    private final JTextField session = new JTextField();
    private final JTextField target = new JTextField();
    private final JTextField observer = new JTextField();
    private final JTextField site = new JTextField();
    private final JTextField scope = new JTextField();
    private final JTextField filter = new JTextField();
    private final JTextField eyepiece = new JTextField();
    private final JTextField lens = new JTextField();
    private final JTextArea finding = new JTextArea(5, 40);

    private IObservation observation = null;

    private final ObservationManager om;
    private final ObservationManagerModel model;

    // Only used to display all observation own values plus some values from other
    // elementso
    // For Create/Edit use ObservationDialogPanel
    public ObservationItemPanel(ObservationManager om, ObservationManagerModel model, IObservation observation) {

        super(false);

        this.om = om;
        this.model = model;

        this.observation = observation;

        this.createPanel();

        this.loadSchemaElement();

        this.setVisible(true);

    }

    private void loadSchemaElement() {

        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());

        DecimalFormat df = new DecimalFormat("0.00");
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(dfs);

        // Load mandatory stuff
        Calendar begin = observation.getBegin();
        format.setCalendar(begin);
        this.begin.setText(format.format(begin.getTime()));
        this.begin.setCaretPosition(0);
        this.begin.setToolTipText("JD: " + DateConverter.toJulianDate(begin));

        ITarget target = observation.getTarget();
        this.target.setText(target.getDisplayName());

        IObserver observer = observation.getObserver();
        this.observer.setText(observer.getDisplayName());

        if (!observation.getResults().isEmpty()) {
            IFinding finding = (IFinding) observation.getResults().get(0); // @todo: Works only with one finding!
            this.finding.setText(finding.getDescription());
        }
        // Load optional stuff
        Calendar end = observation.getEnd();
        if (end != null) {
            format.setCalendar(end);
            this.end.setText(format.format(end.getTime()));
            this.end.setCaretPosition(0);
            this.end.setToolTipText("JD: " + DateConverter.toJulianDate(end));
        }

        float fs = observation.getFaintestStar();
        if (!Float.isNaN(fs)) {
            this.faintestStar.setText("" + fs);
        } else if (observation.getSkyQuality() != null) {
            SurfaceBrightness sqm = observation.getSkyQuality();
            double sqmValue = sqm.getValueAs(SurfaceBrightness.MAGS_SQR_ARC_SEC);
            // Math.log(x) / Math.log(10) as workaround as java.lang.Math only provides
            // natural log values
            // fst = 5*(1.586-log(10^((21.568-BSB)/5)+1))
            double fst = 5 * (1.586 - (Math.log(Math.pow(10, (21.568 - sqmValue) / 5) + 1) / Math.log(10)));
            this.faintestStar.setText("~" + df.format(fst));
            this.faintestStar.setToolTipText(AbstractPanel.bundle.getString("info.fst_BSB.calculated"));
        }

        SurfaceBrightness sqm = observation.getSkyQuality();
        if (sqm != null) {
            this.sqm.setSurfaceBrightness(sqm);
        } else if (!Float.isNaN(fs)) {
            double fst = fs;
            if (!Float.isNaN(observer.getFSTOffset())) {
                fst = fst - observer.getFSTOffset();
            }
            // Math.log(x) / Math.log(10) as workaround as java.lang.Math only provides
            // natural log values
            // Ba = 21.58 - 5 log(10(1.586-Im/5)-1)
            double sqmCalc = 21.58 - (5 * (Math.log(Math.pow(10, (1.586 - fst / 5)) - 1.0) / Math.log(10)));
            if (!Double.isNaN(sqmCalc)) {
                sqm = new SurfaceBrightness((float) sqmCalc, SurfaceBrightness.MAGS_SQR_ARC_SEC);
                this.sqm.setSurfaceBrightness(sqm, true);
            }
        }

        float mag = observation.getMagnification();
        if (!Float.isNaN(mag)) {
            this.magnification.setText("" + mag);
        }

        int s = observation.getSeeing();
        if (s != -1) {
            this.seeing.setText(AbstractPanel.bundle.getString("seeing.antoniadi.short." + s));
            this.seeing.setCaretPosition(0);
            this.seeing.setToolTipText(AbstractPanel.bundle.getString("seeing.antoniadi.long." + s));
        }

        String acc = this.observation.getAccessories();
        if (acc != null) {
            this.accessories.setText(acc);
        }

        List<String> images = observation.getImages();
        if ((images != null) && !(images.isEmpty())) {
            StringBuilder imagesString = new StringBuilder();
            Iterator<String> iterator = images.iterator();
            while (iterator.hasNext()) {
                imagesString.append(iterator.next());
                if (iterator.hasNext()) {
                    imagesString.append("; ");
                }
            }
            this.images.setText(imagesString.toString());
        }

        IImager imager = observation.getImager();
        if (imager != null) {
            this.imager.setText(imager.getDisplayName());
        }

        ISession session = observation.getSession();
        if (session != null) {
            this.session.setText(session.getDisplayName());
        }

        ISite si = observation.getSite();
        if (si != null) {
            this.site.setText(si.getDisplayName());
        }

        ILens le = observation.getLens();
        if (le != null) {
            this.lens.setText(le.getDisplayName());
        }

        IScope sc = observation.getScope();
        if (sc != null) {
            this.scope.setText(sc.getDisplayName());
        }

        IEyepiece eye = observation.getEyepiece();
        if (eye != null) {
            String actualFocalLength = "";
            if (eye.isZoomEyepiece()) { // Zoom eyepiece used
                if ((sc != null) // Scope used
                        && (!Float.isNaN(mag)) // Magnification set
                ) {
                    // No need to check lense...it can be null
                    float afl = OpticsUtil.getActualFocalLength(sc, le, mag);

                    int afl_i = Math.round(afl);

                    // Do some checks that should never be true...hopefully
                    // Comment this out, as by using a lens those checks have a good chance to fail
                    /*
                     * if( afl_i > eye.getMaxFocalLength() ) { System.err.
                     * println("Actual focal length is larger then max focal length of eyepiece. " + observation); }
                     * else if( afl_i < eye.getFocalLength() ) { System.err.
                     * println("Actual focal length is lower then min focal length of eyepiece. " + observation); }
                     */

                    // Set text with additional info on actual focal length
                    actualFocalLength = " @" + afl_i + "mm";
                }
            }

            this.eyepiece.setText(eye.getDisplayName() + actualFocalLength);
        }

        // We might be able to calculate the true field of view...
        float actualFocalLength = OpticsUtil.getActualFocalLength(sc, le, mag);
        Angle tfov = OpticsUtil.getTrueFieldOfView(sc, actualFocalLength, eye, le);
        if (tfov != null) {
            this.trueFoV.setAngle(tfov);
        }

        if (sc != null) {
            float ep = OpticsUtil.getExitPupil(sc, actualFocalLength, le);
            if (!Float.isNaN(ep)) {
                this.exitPupil.setText(df.format(ep));
                this.exitPupil.setEditable(this.isEditable());
            }
        }

        IFilter filter = observation.getFilter();
        if (filter != null) {
            this.filter.setText(filter.getDisplayName());
        }

    }

    @Override
    public ISchemaElement createSchemaElement() {

        return this.observation;

    }

    @Override
    public ISchemaElement updateSchemaElement() {

        return this.observation;

    }

    @Override
    public ISchemaElement getSchemaElement() {

        return this.observation;

    }

    private void createPanel() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        this.setLayout(gridbag);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 1, 1, 8, 1);
        OMLabel Lbegin = new OMLabel(AbstractPanel.bundle.getString("panel.observationItem.label.begin"), true);
        Lbegin.setToolTipText(AbstractPanel.bundle.getString("panel.observationItem.tooltip.begin"));
        gridbag.setConstraints(Lbegin, constraints);
        this.add(Lbegin);
        ConstraintsBuilder.buildConstraints(constraints, 1, 0, 1, 1, 25, 1);
        this.begin.setEditable(false);
        this.begin.setToolTipText(AbstractPanel.bundle.getString("panel.observationItem.tooltip.begin"));
        gridbag.setConstraints(this.begin, constraints);
        this.add(this.begin);

        ConstraintsBuilder.buildConstraints(constraints, 2, 0, 1, 1, 8, 1);
        OMLabel Lend = new OMLabel(AbstractPanel.bundle.getString("panel.observationItem.label.end"),
                SwingConstants.RIGHT, false);
        Lend.setToolTipText(AbstractPanel.bundle.getString("panel.observationItem.tooltip.end"));
        gridbag.setConstraints(Lend, constraints);
        this.add(Lend);
        ConstraintsBuilder.buildConstraints(constraints, 3, 0, 1, 1, 25, 1);
        this.end.setEditable(false);
        this.end.setToolTipText(AbstractPanel.bundle.getString("panel.observationItem.tooltip.end"));
        gridbag.setConstraints(this.end, constraints);
        this.add(this.end);

        ConstraintsBuilder.buildConstraints(constraints, 4, 0, 1, 1, 8, 1);
        OMLabel LfaintestStar = new OMLabel(AbstractPanel.bundle.getString("panel.observationItem.label.faintestStar"),
                SwingConstants.RIGHT, false);
        LfaintestStar.setToolTipText(AbstractPanel.bundle.getString("panel.observationItem.tooltip.faintestStar"));
        gridbag.setConstraints(LfaintestStar, constraints);
        this.add(LfaintestStar);
        ConstraintsBuilder.buildConstraints(constraints, 5, 0, 3, 1, 25, 1);
        this.faintestStar.setEditable(false);
        this.faintestStar.setToolTipText(AbstractPanel.bundle.getString("panel.observationItem.tooltip.faintestStar"));
        gridbag.setConstraints(this.faintestStar, constraints);
        this.add(this.faintestStar);

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 1, 1, 8, 1);
        OMLabel Lmag = new OMLabel(AbstractPanel.bundle.getString("panel.observationItem.label.magnification"), false);
        Lmag.setToolTipText(AbstractPanel.bundle.getString("panel.observationItem.tooltip.magnification"));
        gridbag.setConstraints(Lmag, constraints);
        this.add(Lmag);
        ConstraintsBuilder.buildConstraints(constraints, 1, 1, 1, 1, 25, 1);
        this.magnification.setEditable(false);
        this.magnification
                .setToolTipText(AbstractPanel.bundle.getString("panel.observationItem.tooltip.magnification"));
        gridbag.setConstraints(this.magnification, constraints);
        this.add(this.magnification);

        ConstraintsBuilder.buildConstraints(constraints, 2, 1, 1, 1, 8, 1);
        OMLabel Lseeing = new OMLabel(AbstractPanel.bundle.getString("panel.observationItem.label.seeing"),
                SwingConstants.RIGHT, false);
        Lseeing.setToolTipText(AbstractPanel.bundle.getString("panel.observationItem.tooltip.seeing"));
        gridbag.setConstraints(Lseeing, constraints);
        this.add(Lseeing);
        ConstraintsBuilder.buildConstraints(constraints, 3, 1, 1, 1, 25, 1);
        this.seeing.setEditable(false);
        this.seeing.setToolTipText(AbstractPanel.bundle.getString("panel.observationItem.tooltip.seeing"));
        gridbag.setConstraints(this.seeing, constraints);
        this.add(this.seeing);

        ConstraintsBuilder.buildConstraints(constraints, 4, 1, 1, 1, 8, 1);
        OMLabel Lsqm = new OMLabel(AbstractPanel.bundle.getString("panel.observationItem.label.sqm"),
                SwingConstants.RIGHT, false);
        Lsqm.setToolTipText(AbstractPanel.bundle.getString("panel.observationItem.tooltip.sqm"));
        gridbag.setConstraints(Lsqm, constraints);
        this.add(Lsqm);
        ConstraintsBuilder.buildConstraints(constraints, 5, 1, 3, 1, 25, 1);
        this.sqm.setEditable(false);
        this.sqm.setToolTipText(AbstractPanel.bundle.getString("panel.observationItem.tooltip.sqm"));
        gridbag.setConstraints(this.sqm, constraints);
        this.add(this.sqm);

        ConstraintsBuilder.buildConstraints(constraints, 0, 2, 1, 1, 8, 1);
        OMLabel Laccessories = new OMLabel(AbstractPanel.bundle.getString("panel.observationItem.label.accessories"),
                false);
        Laccessories.setToolTipText(AbstractPanel.bundle.getString("panel.observationItem.tooltip.accessories"));
        gridbag.setConstraints(Laccessories, constraints);
        this.add(Laccessories);
        ConstraintsBuilder.buildConstraints(constraints, 1, 2, 7, 1, 91, 1);
        this.accessories.setEditable(false);
        this.accessories.setToolTipText(AbstractPanel.bundle.getString("panel.observationItem.tooltip.accessories"));
        gridbag.setConstraints(this.accessories, constraints);
        this.add(this.accessories);

        ConstraintsBuilder.buildConstraints(constraints, 0, 3, 8, 1, 99, 1);
        JSeparator seperator1 = new JSeparator(SwingConstants.HORIZONTAL);
        gridbag.setConstraints(seperator1, constraints);
        this.add(seperator1);

        ConstraintsBuilder.buildConstraints(constraints, 0, 4, 1, 1, 8, 1);
        OMLabel Lsession = new OMLabel(AbstractPanel.bundle.getString("panel.observationItem.label.session"), false);
        Lsession.setToolTipText(AbstractPanel.bundle.getString("panel.observationItem.tooltip.session"));
        gridbag.setConstraints(Lsession, constraints);
        this.add(Lsession);
        ConstraintsBuilder.buildConstraints(constraints, 1, 4, 1, 1, 25, 1);
        this.session.setEditable(false);
        this.session.setToolTipText(AbstractPanel.bundle.getString("panel.observationItem.tooltip.session"));
        gridbag.setConstraints(this.session, constraints);
        this.add(this.session);

        ConstraintsBuilder.buildConstraints(constraints, 2, 4, 1, 1, 8, 1);
        OMLabel Lsite = new OMLabel(AbstractPanel.bundle.getString("panel.observationItem.label.site"),
                SwingConstants.RIGHT, false);
        Lsite.setToolTipText(AbstractPanel.bundle.getString("panel.observationItem.tooltip.site"));
        gridbag.setConstraints(Lsite, constraints);
        this.add(Lsite);
        ConstraintsBuilder.buildConstraints(constraints, 3, 4, 1, 1, 25, 1);
        this.site.setEditable(false);
        this.site.setToolTipText(AbstractPanel.bundle.getString("panel.observationItem.tooltip.site"));
        gridbag.setConstraints(this.site, constraints);
        this.add(this.site);

        ConstraintsBuilder.buildConstraints(constraints, 4, 4, 1, 1, 8, 1);
        OMLabel Lobserver = new OMLabel(AbstractPanel.bundle.getString("panel.observationItem.label.observer"),
                SwingConstants.RIGHT, true);
        Lobserver.setToolTipText(AbstractPanel.bundle.getString("panel.observationItem.tooltip.observer"));
        gridbag.setConstraints(Lobserver, constraints);
        this.add(Lobserver);
        ConstraintsBuilder.buildConstraints(constraints, 5, 4, 3, 1, 25, 1);
        this.observer.setEditable(false);
        this.observer.setToolTipText(AbstractPanel.bundle.getString("panel.observationItem.tooltip.observer"));
        gridbag.setConstraints(this.observer, constraints);
        this.add(this.observer);

        ConstraintsBuilder.buildConstraints(constraints, 0, 5, 8, 1, 99, 1);
        JSeparator seperator2 = new JSeparator(SwingConstants.HORIZONTAL);
        gridbag.setConstraints(seperator2, constraints);
        this.add(seperator2);

        ConstraintsBuilder.buildConstraints(constraints, 0, 6, 1, 1, 8, 1);
        OMLabel Lscope = new OMLabel(AbstractPanel.bundle.getString("panel.observationItem.label.scope"), false);
        Lscope.setToolTipText(AbstractPanel.bundle.getString("panel.observationItem.tooltip.scope"));
        gridbag.setConstraints(Lscope, constraints);
        this.add(Lscope);
        ConstraintsBuilder.buildConstraints(constraints, 1, 6, 1, 1, 25, 1);
        this.scope.setEditable(false);
        this.scope.setToolTipText(AbstractPanel.bundle.getString("panel.observationItem.tooltip.scope"));
        gridbag.setConstraints(this.scope, constraints);
        this.add(this.scope);

        ConstraintsBuilder.buildConstraints(constraints, 2, 6, 1, 1, 8, 1);
        OMLabel Leyepiece = new OMLabel(AbstractPanel.bundle.getString("panel.observationItem.label.eyepiece"),
                SwingConstants.RIGHT, false);
        Leyepiece.setToolTipText(AbstractPanel.bundle.getString("panel.observationItem.tooltip.eyepiece"));
        gridbag.setConstraints(Leyepiece, constraints);
        this.add(Leyepiece);
        ConstraintsBuilder.buildConstraints(constraints, 3, 6, 1, 1, 25, 1);
        this.eyepiece.setEditable(false);
        this.eyepiece.setToolTipText(AbstractPanel.bundle.getString("panel.observationItem.tooltip.eyepiece"));
        gridbag.setConstraints(this.eyepiece, constraints);
        this.add(this.eyepiece);

        ConstraintsBuilder.buildConstraints(constraints, 4, 6, 1, 1, 8, 1);
        OMLabel Lfilter = new OMLabel(AbstractPanel.bundle.getString("panel.observationItem.label.filter"),
                SwingConstants.RIGHT, false);
        Lfilter.setToolTipText(AbstractPanel.bundle.getString("panel.observationItem.tooltip.filter"));
        gridbag.setConstraints(Lfilter, constraints);
        this.add(Lfilter);
        ConstraintsBuilder.buildConstraints(constraints, 5, 6, 2, 1, 25, 1);
        this.filter.setEditable(false);
        this.filter.setToolTipText(AbstractPanel.bundle.getString("panel.observationItem.tooltip.filter"));
        gridbag.setConstraints(this.filter, constraints);
        this.add(this.filter);

        ConstraintsBuilder.buildConstraints(constraints, 0, 7, 1, 1, 8, 1);
        String labelText = AbstractPanel.bundle.getString("panel.observationItem.label.lens");
        if ((this.observation != null) && (this.observation.getLens() != null)) {
            if (this.observation.getLens().getFactor() > 1) {
                labelText = AbstractPanel.bundle.getString("panel.observationItem.label.lens.barlow");
            } else if (this.observation.getLens().getFactor() < 1) {
                labelText = AbstractPanel.bundle.getString("panel.observationItem.label.lens.sharpley");
            } else {
                labelText = AbstractPanel.bundle.getString("panel.observationItem.label.lens");
            }
        }
        OMLabel Llens = new OMLabel(labelText, false);
        Llens.setToolTipText(AbstractPanel.bundle.getString("panel.observationItem.tooltip.lens"));
        gridbag.setConstraints(Llens, constraints);
        this.add(Llens);
        ConstraintsBuilder.buildConstraints(constraints, 1, 7, 1, 1, 25, 1);
        this.lens.setEditable(false);
        this.lens.setToolTipText(AbstractPanel.bundle.getString("panel.observationItem.tooltip.lens"));
        gridbag.setConstraints(this.lens, constraints);
        this.add(this.lens);

        ConstraintsBuilder.buildConstraints(constraints, 2, 7, 1, 1, 8, 1);
        JLabel LtrueFoV = new JLabel(AbstractPanel.bundle.getString("panel.observationItem.label.trueFoV"),
                SwingConstants.RIGHT);
        LtrueFoV.setToolTipText(AbstractPanel.bundle.getString("panel.observationItem.tooltip.trueFoV"));
        gridbag.setConstraints(LtrueFoV, constraints);
        LtrueFoV.setFont(new Font("sansserif", Font.ITALIC + Font.BOLD, 12));
        this.add(LtrueFoV);
        ConstraintsBuilder.buildConstraints(constraints, 3, 7, 1, 1, 25, 1);
        this.trueFoV.setEditable(false);
        this.trueFoV.setToolTipText(AbstractPanel.bundle.getString("panel.observationItem.tooltip.trueFoV"));
        gridbag.setConstraints(this.trueFoV, constraints);
        this.add(this.trueFoV);

        ConstraintsBuilder.buildConstraints(constraints, 4, 7, 1, 1, 8, 1);
        JLabel LexitPupil = new JLabel(AbstractPanel.bundle.getString("panel.observationItem.label.exitPupil"),
                SwingConstants.RIGHT);
        LexitPupil.setToolTipText(AbstractPanel.bundle.getString("panel.observationItem.tooltip.exitPupil"));
        gridbag.setConstraints(LexitPupil, constraints);
        LexitPupil.setFont(new Font("sansserif", Font.ITALIC + Font.BOLD, 12));
        this.add(LexitPupil);
        ConstraintsBuilder.buildConstraints(constraints, 5, 7, 1, 1, 25, 1);
        this.exitPupil.setEditable(false);
        this.exitPupil.setToolTipText(AbstractPanel.bundle.getString("panel.observationItem.tooltip.exitPupil"));
        gridbag.setConstraints(this.exitPupil, constraints);
        this.add(this.exitPupil);

        ConstraintsBuilder.buildConstraints(constraints, 6, 7, 1, 1, 1, 1);
        JLabel LexitPupilUnit = new JLabel(AbstractPanel.bundle.getString("panel.observationItem.label.exitPupilUnit"),
                SwingConstants.RIGHT);
        LexitPupilUnit.setToolTipText(AbstractPanel.bundle.getString("panel.observationItem.tooltip.exitPupil"));
        gridbag.setConstraints(LexitPupilUnit, constraints);
        LexitPupilUnit.setFont(new Font("sansserif", Font.BOLD, 12));
        LexitPupilUnit.setHorizontalAlignment(SwingConstants.LEFT);
        this.add(LexitPupilUnit);

        ConstraintsBuilder.buildConstraints(constraints, 0, 8, 8, 1, 99, 1);
        JSeparator seperator3 = new JSeparator(SwingConstants.HORIZONTAL);
        gridbag.setConstraints(seperator3, constraints);
        this.add(seperator3);

        ConstraintsBuilder.buildConstraints(constraints, 0, 9, 1, 1, 10, 1);
        OMLabel Ltarget = new OMLabel(AbstractPanel.bundle.getString("panel.observationItem.label.target"), true);
        Ltarget.setToolTipText(AbstractPanel.bundle.getString("panel.observationItem.tooltip.target"));
        gridbag.setConstraints(Ltarget, constraints);
        this.add(Ltarget);
        ConstraintsBuilder.buildConstraints(constraints, 1, 9, 6, 1, 50, 1);
        this.target.setEditable(false);
        this.target.setToolTipText(AbstractPanel.bundle.getString("panel.observationItem.tooltip.target"));
        gridbag.setConstraints(this.target, constraints);
        this.add(this.target);

        if (this.observation != null) {
            EquPosition pos = this.observation.getTarget().getPosition();
            if (pos == null) { // Try to calculate position
                pos = Ephemerides.getPosition(Ephemerides.planetKey(this.observation.getTarget().getName()),
                        this.observation.getBegin());
            }
            if ((pos != null) && (this.observation.getSite() != null)) {
                ConstraintsBuilder.buildConstraints(constraints, 7, 6, 1, 2, 1, 1);
                constraints.fill = GridBagConstraints.BOTH;
                constraints.anchor = GridBagConstraints.EAST;
                HorizontalSkymap skyMap = new HorizontalSkymap(pos, this.observation.getBegin(),
                        this.observation.getSite());
                skyMap.setHorizontalAlignment(SwingConstants.RIGHT);
                gridbag.setConstraints(skyMap, constraints);
                this.add(skyMap);
            }
        }

        ConstraintsBuilder.buildConstraints(constraints, 7, 9, 1, 4, 1, 1);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.EAST;
        MoonDetailContainer moonContainer = new MoonDetailContainer(this.observation);
        moonContainer.setHorizontalAlignment(SwingConstants.RIGHT);
        gridbag.setConstraints(moonContainer, constraints);
        this.add(moonContainer);

        ConstraintsBuilder.buildConstraints(constraints, 0, 10, 6, 1, 8, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        OMLabel Lfinding = new OMLabel(AbstractPanel.bundle.getString("panel.observationItem.label.description"), true);
        Lfinding.setToolTipText(AbstractPanel.bundle.getString("panel.observationItem.tooltip.description"));
        gridbag.setConstraints(Lfinding, constraints);
        this.add(Lfinding);
        ConstraintsBuilder.buildConstraints(constraints, 0, 11, 8, 3, 99, 40);
        constraints.fill = GridBagConstraints.BOTH;
        this.finding.setEditable(false);
        this.finding.setLineWrap(true);
        this.finding.setToolTipText(AbstractPanel.bundle.getString("panel.observationItem.tooltip.description"));
        /*
         * if( this.om.isNightVisionEnabled() ) { this.finding.setBackground(new Color(255, 175, 175)); } else {
         * this.finding.setBackground(Color.WHITE); }
         */
        JScrollPane findingScroll = new JScrollPane(this.finding);
        findingScroll.setMinimumSize(new Dimension(300, 60));
        gridbag.setConstraints(findingScroll, constraints);
        this.add(findingScroll);

        ConstraintsBuilder.buildConstraints(constraints, 0, 14, 8, 1, 9, 1);
        JSeparator seperator4 = new JSeparator(SwingConstants.HORIZONTAL);
        gridbag.setConstraints(seperator4, constraints);
        this.add(seperator4);

        ConstraintsBuilder.buildConstraints(constraints, 0, 15, 1, 1, 9, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        OMLabel Limager = new OMLabel(AbstractPanel.bundle.getString("panel.observationItem.label.imager"), false);
        Limager.setToolTipText(AbstractPanel.bundle.getString("panel.observationItem.tooltip.imager"));
        gridbag.setConstraints(Limager, constraints);
        this.add(Limager);
        ConstraintsBuilder.buildConstraints(constraints, 1, 15, 7, 1, 90, 1);
        this.imager.setEditable(false);
        this.imager.setToolTipText(AbstractPanel.bundle.getString("panel.observationItem.tooltip.imager"));
        gridbag.setConstraints(this.imager, constraints);
        this.add(this.imager);

        /*
         * ConstraintsBuilder.buildConstraints(constraints, 0, 14, 1, 1, 9, 1); JLabel Limages = new
         * JLabel(AbstractPanel.bundle.getString("panel.observationItem.label.images"));
         * Limages.setToolTipText(AbstractPanel.bundle.getString( "panel.observationItem.tooltip.images"));
         * gridbag.setConstraints(Limages, constraints); this.add(Limages);
         * ConstraintsBuilder.buildConstraints(constraints, 1, 14, 5, 1, 90, 1); this.images.setEditable(false);
         * this.images.setToolTipText(AbstractPanel.bundle.getString( "panel.observationItem.tooltip.images"));
         * gridbag.setConstraints(this.images, constraints); this.add(this.images);
         */

        if (this.observation != null && this.observation.getImages() != null) {
            ConstraintsBuilder.buildConstraints(constraints, 0, 16, 1, 1, 99, 1);
            OMLabel LimageContainer = new OMLabel(AbstractPanel.bundle.getString("panel.observationItem.label.images"),
                    false);
            LimageContainer.setToolTipText(AbstractPanel.bundle.getString("panel.observationItem.tooltip.images"));
            gridbag.setConstraints(LimageContainer, constraints);
            this.add(LimageContainer);
            ConstraintsBuilder.buildConstraints(constraints, 0, 17, 8, 1, 99, 1);
            constraints.fill = GridBagConstraints.BOTH;
            ImageContainer imageContainer = new ImageContainer(
                    this.model.getFilesFromPath(this.observation.getImages()), this.om, this.om.getConfiguration(),
                    this.model, false, this.om.getImageResolver());
            JScrollPane imageContainerScroll = new JScrollPane(imageContainer,
                    ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            gridbag.setConstraints(imageContainerScroll, constraints);
            // Make sure size of scroll container can handle image thumbnail
            imageContainerScroll.setPreferredSize(imageContainer.getPreferredSize());
            this.add(imageContainerScroll);
        } /*
           * ConstraintsBuilder.buildConstraints(constraints, 0, 16, 6, 1, 99, 30); constraints.fill =
           * GridBagConstraints.BOTH; JLabel Lfill = new JLabel(""); gridbag.setConstraints(Lfill, constraints);
           * this.add(Lfill);
           */

    }

}
