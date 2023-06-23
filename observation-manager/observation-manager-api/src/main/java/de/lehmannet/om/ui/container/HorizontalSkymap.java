package de.lehmannet.om.ui.container;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import de.lehmannet.om.EquPosition;
import de.lehmannet.om.ICloneable;
import de.lehmannet.om.ISite;
import de.lehmannet.om.ui.util.LocaleToolsFactory;
import de.lehmannet.om.util.Ephemerides;

public class HorizontalSkymap extends JLabel {

    /**
     *
     */
    private static final long serialVersionUID = -1501342339873856272L;

    private final ResourceBundle bundle = LocaleToolsFactory.appInstance().getBundle("ObservationManager",
            Locale.getDefault());

    // Width and height of our map
    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;

    // Border of sky circle (Border between circle and square)
    private static final int SKY_BORDER = 1;

    // Object diameter
    private static final int POINT_DIAMETER = 4;

    // The object data to display on the sky
    private EquPosition position = null;
    private ZonedDateTime calendar = null;
    private ISite site = null;

    public HorizontalSkymap(EquPosition position, ZonedDateTime date, ISite site) {

        this.position = position == null ? null : new EquPosition(position.getRa(), position.getDec());
        this.calendar = date;
        this.site = ICloneable.copyOrNull(site);

        this.createImage();

    }

    private void createImage() {

        BufferedImage image = new BufferedImage(HorizontalSkymap.WIDTH, HorizontalSkymap.HEIGHT,
                BufferedImage.TYPE_INT_ARGB);

        this.paintSky(image);
        this.paintLabel(image);
        this.paintObjectPosition(image);

        this.setIcon(new ImageIcon(image));

    }

    private void paintSky(BufferedImage image) {

        Graphics2D g2d = (Graphics2D) image.getGraphics();

        // Print large box as border
        g2d.setPaint(new Color(12, 114, 12));
        g2d.fillRect(0, 0, HorizontalSkymap.WIDTH, HorizontalSkymap.HEIGHT);

        // Print circle as sky
        g2d.setPaint(Color.black);
        g2d.fillOval(HorizontalSkymap.SKY_BORDER, HorizontalSkymap.SKY_BORDER,
                (HorizontalSkymap.WIDTH) - 2 * HorizontalSkymap.SKY_BORDER,
                (HorizontalSkymap.HEIGHT) - 2 * HorizontalSkymap.SKY_BORDER);

    }

    private void paintObjectPosition(BufferedImage image) {

        Graphics2D g2d = (Graphics2D) image.getGraphics();

        // Get object location
        double azimut = Ephemerides.getAzimut(this.position, this.calendar, this.site.getLongitude().getValue(),
                this.site.getLatitude().getValue());
        double altitude = Ephemerides.altitudeAboveHorizon(this.position, this.calendar,
                this.site.getLongitude().getValue(), this.site.getLatitude().getValue());

        DecimalFormat df = new DecimalFormat("0.00");
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(dfs);

        this.setToolTipText("<html>" + this.bundle.getString("horizontalSkymap.azimut") + ": " + df.format(azimut)
                + "<br>" + this.bundle.getString("horizontalSkymap.altitude") + ": " + df.format(altitude) + "</html>");

        // Calculate some helpers
        int radius = (HorizontalSkymap.WIDTH / 2) - HorizontalSkymap.SKY_BORDER; // Radius of sky circle
        Point center = new Point(HorizontalSkymap.WIDTH / 2, HorizontalSkymap.HEIGHT / 2); // Center point of sky
                                                                                           // (zenith)

        if (azimut == 0) { // Make sure next calculation doesn't div by zero
            azimut = 1;
        }

        // Length of object radius (or how far from center is the object point)
        // Substract radius at the end to make sure 90 deg is on the center of the
        // circle (radius =0)
        double altitudeOnRadius = Math.abs(radius - (radius / (90 / altitude)));

        // Claculate location of object point (90 - azimut as 'north' is 0 degrees for
        // us and not 'east' or x-axis)
        double x = Math.cos(Math.toRadians(90 - azimut)) * altitudeOnRadius;
        double y = Math.sin(Math.toRadians(90 - azimut)) * altitudeOnRadius;

        // Check in witch section (of the sky circle) the object is located
        // Caution! The skymap is astronomically oriented! East is 'left' and azimut 0
        // is north or 'top'
        if ((azimut > 0) && (azimut < 90)) {
            if (x > 0) {
                x = x * -1;
            }
            if (y < 0) {
                y = y * -1;
            }
        } else if ((azimut > 90) && (azimut < 180)) {
            if (x > 0) {
                x = x * -1;
            }
            if (y > 0) {
                y = y * -1;
            }
        } else if ((azimut > 180) && (azimut < 270)) {
            if (x < 0) {
                x = x * -1;
            }
            if (y > 0) {
                y = y * -1;
            }
        } else if ((azimut > 270)) {
            if (x < 0) {
                x = x * -1;
            }
            if (y < 0) {
                y = y * -1;
            }
        }

        // Center location offset
        x = center.getX() + x;
        y = center.getY() - y; // y-axis increases downwards

        g2d.setPaint(Color.yellow);
        g2d.fillOval((int) x, (int) y, HorizontalSkymap.POINT_DIAMETER, HorizontalSkymap.POINT_DIAMETER);

    }

    private void paintLabel(BufferedImage image) {

        Graphics2D g2d = (Graphics2D) image.getGraphics();

        String west = this.bundle.getString("horizontalSkymap.abbr.west");
        String north = this.bundle.getString("horizontalSkymap.abbr.north");

        // Draw legend
        g2d.setPaint(Color.LIGHT_GRAY);
        g2d.setFont(new Font("sansserif", Font.PLAIN, 9));
        g2d.drawChars(north.toCharArray(), 0, north.length(), HorizontalSkymap.WIDTH / 2 - 3, 8);
        g2d.drawChars(west.toCharArray(), 0, west.length(), HorizontalSkymap.WIDTH - 9,
                HorizontalSkymap.HEIGHT / 2 + 3);

    }

}
