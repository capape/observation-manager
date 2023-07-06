/*
 * ====================================================================
 * /panel/SitePanel.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import de.lehmannet.om.Angle;
import de.lehmannet.om.ICloneable;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ISite;
import de.lehmannet.om.Site;
import de.lehmannet.om.ui.container.AngleContainer;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.OMLabel;
import de.lehmannet.om.util.FloatUtil;

public class SitePanel extends AbstractPanel implements ActionListener {

    private static final long serialVersionUID = -6461891214285550244L;

    private ISite site = null;

    private JTextField name = null;
    private JTextField iauCode = null;
    private JTextField timezone = null;
    private AngleContainer latitude = null;
    private AngleContainer longitude = null;
    private JButton changeArcDec = null;
    private JTextField elevation = null;
    private boolean toggleArcDec = false;

    public SitePanel(ISite site, boolean editable) {

        super(editable);

        this.site = ICloneable.copyOrNull(site);

        this.createPanel();

        if (site != null) {
            this.loadSchemaElement();
        }

        this.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource().equals(this.changeArcDec)) {
            toggleArcDec = !toggleArcDec;
            this.latitude.setArcDecTransformation(toggleArcDec);
            this.longitude.setArcDecTransformation(toggleArcDec);

            this.updateUI();
        }

    }

    private void loadSchemaElement() {

        float elevation = this.site.getElevation();
        if (!Float.isNaN(elevation)) {
            this.elevation.setText(String.valueOf(elevation));
        }
        this.elevation.setEditable(this.isEditable());

        this.iauCode.setText(this.site.getIAUCode());
        this.iauCode.setEditable(this.isEditable());

        this.latitude.setAngle(this.site.getLatitude());
        this.latitude.setEditable(this.isEditable());

        this.longitude.setAngle(this.site.getLongitude());
        this.longitude.setEditable(this.isEditable());

        this.name.setText(this.site.getName());
        this.name.setEditable(this.isEditable());

        this.timezone.setText(String.valueOf(this.site.getTimezone()));
        this.timezone.setEditable(this.isEditable());

    }

    @Override
    public ISchemaElement getSchemaElement() {

        return ICloneable.copyOrNull(this.site);

    }

    @Override
    public ISchemaElement updateSchemaElement() {

        if (this.site == null) {
            return null;
        }

        // Get mandatory fields
        String name = this.getSiteName();
        if (name == null) {
            this.createWarning(AbstractPanel.bundle.getString("panel.site.warning.noName"));
            return null;
        }
        this.site.setName(name);

        Angle longitude = this.getLongitude();
        if (longitude == null) {
            this.createWarning(AbstractPanel.bundle.getString("panel.site.warning.noLongitude"));
            return null;
        }
        if ((longitude.getValue() > 180) || (longitude.getValue() < -180)) {
            this.createWarning(AbstractPanel.bundle.getString("panel.site.warning.LongitudeInvalid"));
            return null;
        }
        this.site.setLongitude(longitude);

        Angle latitude = this.getLatitude();
        if (latitude == null) {
            this.createWarning(AbstractPanel.bundle.getString("panel.site.warning.noLatitude"));
            return null;
        }
        if ((latitude.getValue() > 90) || (latitude.getValue() < -90)) {
            this.createWarning(AbstractPanel.bundle.getString("panel.site.warning.LatitudeInvalid"));
            return null;
        }
        this.site.setLatitude(latitude);

        String timezone = this.getTimezone();
        if (timezone == null) {
            this.createWarning(AbstractPanel.bundle.getString("panel.site.warning.noTimezone"));
            return null;
        }
        int tz = 0;
        try {
            tz = Integer.parseInt(timezone);
        } catch (NumberFormatException nfe) {
            this.createWarning(AbstractPanel.bundle.getString("panel.site.warning.timezoneNumeric"));
            return null;
        }
        if ((tz > 720) || (tz < -720)) {
            this.createWarning(AbstractPanel.bundle.getString("panel.site.warning.timezoneInvalid"));
            return null;
        }
        this.site.setTimezone(tz);

        // Set optional elements
        String elevation = this.elevation.getText();
        if ((elevation != null) && !("".equals(elevation.trim()))) {
            try {
                float e = FloatUtil.parseFloat(elevation);
                this.site.setElevation(e);
            } catch (NumberFormatException nfe) {
                this.createWarning(AbstractPanel.bundle.getString("panel.site.warning.ElevationNumeric"));
                return null;
            }
        }

        String iauCode = this.iauCode.getText();
        if ((iauCode != null) && !("".equals(iauCode))) {
            this.site.setIAUCode(iauCode);
        }

        return ICloneable.copyOrNull(this.site);

    }

    @Override
    public ISchemaElement createSchemaElement() {

        // Get mandatory fields
        String name = this.getSiteName();
        if (name == null) {
            this.createWarning(AbstractPanel.bundle.getString("panel.site.warning.noName"));
            return null;
        }

        Angle longitude = this.getLongitude();
        if (longitude == null) {
            this.createWarning(AbstractPanel.bundle.getString("panel.site.warning.noLongitude"));
            return null;
        }
        if ((longitude.getValue() > 180) || (longitude.getValue() < -180)) {
            this.createWarning(AbstractPanel.bundle.getString("panel.site.warning.LongitudeInvalid"));
            return null;
        }

        Angle latitude = this.getLatitude();
        if (latitude == null) {
            this.createWarning(AbstractPanel.bundle.getString("panel.site.warning.noLatitude"));
            return null;
        }
        if ((latitude.getValue() > 90) || (latitude.getValue() < -90)) {
            this.createWarning(AbstractPanel.bundle.getString("panel.site.warning.LatitudeInvalid"));
            return null;
        }

        String timezone = this.getTimezone();
        if (timezone == null) {
            this.createWarning(AbstractPanel.bundle.getString("panel.site.warning.noTimezone"));
            return null;
        }
        int tz = 0;
        try {
            tz = Integer.parseInt(timezone);
        } catch (NumberFormatException nfe) {
            this.createWarning(AbstractPanel.bundle.getString("panel.site.warning.timezoneNumeric"));
            return null;
        }
        if ((tz > 720) || (tz < -720)) {
            this.createWarning(AbstractPanel.bundle.getString("panel.site.warning.timezoneInvalid"));
            return null;
        }

        this.site = new Site(name, longitude, latitude, tz);

        // Set optional elements
        String elevation = this.elevation.getText();
        if ((elevation != null) && !("".equals(elevation.trim()))) {
            try {
                float e = FloatUtil.parseFloat(elevation);
                this.site.setElevation(e);
            } catch (NumberFormatException nfe) {
                this.createWarning(AbstractPanel.bundle.getString("panel.site.warning.ElevationNumeric"));
                return null;
            }
        }

        String iauCode = this.iauCode.getText();
        if ((iauCode != null) && !("".equals(iauCode))) {
            this.site.setIAUCode(iauCode);
        }

        return ICloneable.copyOrNull(this.site);

    }

    private String getSiteName() {

        String name = this.name.getText();
        if ((name == null) || ("".equals(name))) {
            this.createWarning(AbstractPanel.bundle.getString("panel.site.warning.noName"));
            return null;
        }

        return name;

    }

    private Angle getLongitude() {

        Angle longitude = null;
        try {
            longitude = this.longitude.getAngle();
        } catch (NumberFormatException nfe) {
            this.createWarning(AbstractPanel.bundle.getString("panel.warning.longitudeNoNumber"));
            return null;
        }
        if (longitude == null) {
            this.createWarning(AbstractPanel.bundle.getString("panel.site.warning.noLongitude"));
            return null;
        }

        return longitude;

    }

    private Angle getLatitude() {

        Angle latitude = null;
        try {
            latitude = this.latitude.getAngle();
        } catch (NumberFormatException nfe) {
            this.createWarning(AbstractPanel.bundle.getString("panel.warning.latitudeNoNumber"));
            return null;
        }
        if (latitude == null) {
            this.createWarning(AbstractPanel.bundle.getString("panel.site.warning.noLatitude"));
            return null;
        }

        return latitude;

    }

    private String getTimezone() {

        String timezone = this.timezone.getText();
        if ((timezone == null) || ("".equals(timezone))) {
            this.createWarning(AbstractPanel.bundle.getString("panel.site.warning.noTimezone"));
            return null;
        }

        try {
            Integer.parseInt(timezone);
        } catch (NumberFormatException nfe) {
            this.createWarning(AbstractPanel.bundle.getString("panel.site.warning.timezoneNumeric"));
            return null;
        }

        return timezone;

    }

    private void createPanel() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 1, 1, 10, 1);

        OMLabel LName = new OMLabel(AbstractPanel.bundle.getString("panel.site.label.name"), true);
        gridbag.setConstraints(LName, constraints);
        LName.setToolTipText(AbstractPanel.bundle.getString("panel.site.tooltip.name"));
        this.add(LName);
        ConstraintsBuilder.buildConstraints(constraints, 1, 0, 2, 1, 40, 1);
        this.name = new JTextField();
        this.name.setToolTipText(AbstractPanel.bundle.getString("panel.site.tooltip.name"));
        gridbag.setConstraints(this.name, constraints);
        this.add(this.name);

        ConstraintsBuilder.buildConstraints(constraints, 3, 0, 1, 1, 10, 1);
        OMLabel LiauCode = new OMLabel(AbstractPanel.bundle.getString("panel.site.label.iau"), SwingConstants.RIGHT,
                false);
        LiauCode.setToolTipText(AbstractPanel.bundle.getString("panel.site.tooltip.iau"));
        gridbag.setConstraints(LiauCode, constraints);
        this.add(LiauCode);
        ConstraintsBuilder.buildConstraints(constraints, 4, 0, 2, 1, 40, 1);
        this.iauCode = new JTextField();
        this.iauCode.setToolTipText(AbstractPanel.bundle.getString("panel.site.tooltip.iau"));
        gridbag.setConstraints(this.iauCode, constraints);
        this.add(this.iauCode);

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 1, 1, 10, 1);
        OMLabel llongitude = new OMLabel(AbstractPanel.bundle.getString("panel.site.label.longitude"), true);
        llongitude.setToolTipText(AbstractPanel.bundle.getString("panel.site.tooltip.longitude"));
        gridbag.setConstraints(llongitude, constraints);
        this.add(llongitude);
        ConstraintsBuilder.buildConstraints(constraints, 1, 1, 2, 1, 35, 1);
        this.longitude = new AngleContainer(Angle.DEGREE, this.isEditable());
        this.longitude.setToolTipText(AbstractPanel.bundle.getString("panel.site.tooltip.longitude"));
        gridbag.setConstraints(this.longitude, constraints);
        this.add(this.longitude);

        ConstraintsBuilder.buildConstraints(constraints, 3, 1, 1, 1, 10, 1);
        OMLabel llatitude = new OMLabel(AbstractPanel.bundle.getString("panel.site.label.latitude"),
                SwingConstants.RIGHT, true);
        llatitude.setToolTipText(AbstractPanel.bundle.getString("panel.site.tooltip.latitude"));
        gridbag.setConstraints(llatitude, constraints);
        this.add(llatitude);
        ConstraintsBuilder.buildConstraints(constraints, 4, 1, 2, 1, 35, 1);
        this.latitude = new AngleContainer(Angle.DEGREE, this.isEditable());
        this.latitude.setToolTipText(AbstractPanel.bundle.getString("panel.site.tooltip.latitude"));
        gridbag.setConstraints(this.latitude, constraints);
        this.add(this.latitude);

        if (this.isEditable()) {
            ConstraintsBuilder.buildConstraints(constraints, 6, 1, 1, 1, 5, 1);
            this.changeArcDec = new JButton(AbstractPanel.bundle.getString("panel.site.button.arcDec"));
            this.changeArcDec.addActionListener(this);
            this.changeArcDec.setToolTipText(AbstractPanel.bundle.getString("panel.site.tooltip.arcDec"));
            gridbag.setConstraints(this.changeArcDec, constraints);
            this.add(this.changeArcDec);
        }

        ConstraintsBuilder.buildConstraints(constraints, 0, 2, 1, 1, 10, 1);
        OMLabel Lelevation = new OMLabel(AbstractPanel.bundle.getString("panel.site.label.elevation"), false);
        Lelevation.setToolTipText(AbstractPanel.bundle.getString("panel.site.tooltip.elevation"));
        gridbag.setConstraints(Lelevation, constraints);
        this.add(Lelevation);
        ConstraintsBuilder.buildConstraints(constraints, 1, 2, 1, 1, 35, 1);
        this.elevation = new JTextField();
        this.elevation.setToolTipText(AbstractPanel.bundle.getString("panel.site.tooltip.elevation"));
        gridbag.setConstraints(this.elevation, constraints);
        this.add(this.elevation);
        ConstraintsBuilder.buildConstraints(constraints, 2, 2, 1, 1, 5, 1);
        JLabel LelevationUnit = new JLabel(AbstractPanel.bundle.getString("panel.site.label.meter"));
        LelevationUnit.setToolTipText(AbstractPanel.bundle.getString("panel.site.tooltip.elevation"));
        gridbag.setConstraints(LelevationUnit, constraints);
        this.add(LelevationUnit);

        ConstraintsBuilder.buildConstraints(constraints, 3, 2, 1, 1, 10, 1);
        OMLabel Ltimezone = new OMLabel(AbstractPanel.bundle.getString("panel.site.label.timezone"),
                SwingConstants.RIGHT, true);
        gridbag.setConstraints(Ltimezone, constraints);
        Ltimezone.setToolTipText(AbstractPanel.bundle.getString("panel.site.tooltip.timezone"));
        this.add(Ltimezone);
        ConstraintsBuilder.buildConstraints(constraints, 4, 2, 1, 1, 35, 1);
        this.timezone = new JTextField();
        this.timezone.setToolTipText(AbstractPanel.bundle.getString("panel.site.tooltip.timezone"));
        gridbag.setConstraints(this.timezone, constraints);
        this.add(this.timezone);
        ConstraintsBuilder.buildConstraints(constraints, 5, 2, 1, 1, 5, 1);
        OMLabel LtimezoneUnit = new OMLabel(AbstractPanel.bundle.getString("panel.site.label.minute"), true);
        LtimezoneUnit.setToolTipText(AbstractPanel.bundle.getString("panel.site.tooltip.timezone"));
        gridbag.setConstraints(LtimezoneUnit, constraints);
        this.add(LtimezoneUnit);

        ConstraintsBuilder.buildConstraints(constraints, 0, 3, 7, 1, 100, 86);
        constraints.fill = GridBagConstraints.BOTH;
        JLabel Lfill = new JLabel("");
        gridbag.setConstraints(Lfill, constraints);
        this.add(Lfill);

    }

}
