/* ====================================================================
 * /container/MoonDetailContainer.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.container;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import de.lehmannet.om.IObservation;
import de.lehmannet.om.ISite;
import de.lehmannet.om.ui.image.ImageClassLoaderResolverImpl;
import de.lehmannet.om.ui.image.ImageResolver;
import de.lehmannet.om.ui.util.LocaleToolsFactory;
import de.lehmannet.om.util.Ephemerides;

public class MoonDetailContainer extends JLabel {

    /**
     *
     */
    private static final long serialVersionUID = -8924593147222865850L;

    private final ResourceBundle bundle = LocaleToolsFactory.appInstance().getBundle("ObservationManager",
            Locale.getDefault());

    private static final String BELOW_HORIZON = "below.png";

    private static final String NEW_MOON = "new.png";
    private static final String FULL_MOON = "full.png";

    private IObservation observation = null;

    private transient final ImageResolver moonImages = new ImageClassLoaderResolverImpl(
            "images" + File.separator + "moon");

    public MoonDetailContainer(IObservation obs) {

        this.observation = obs;

        this.setImage();

    }

    public void setObservation(IObservation obs) {

        this.observation = obs;
        this.setImage();

    }

    private void setImage() {

        ISite site = this.observation.getSite();
        if (site == null) {
            return;
        }

        ZonedDateTime date = this.observation.getBegin();

        String path = "";
        if (Ephemerides.isMoonAboveHorizon(date, site.getLongitude().toDegree(), site.getLatitude().toDegree())) {

            double phase = Ephemerides.getMoonPhase(date) * 100;
            double absPhase = Math.abs(phase);

            if (absPhase <= 5.0) { // New moon
                path = path + MoonDetailContainer.NEW_MOON;
                this.setToolTipText(this.bundle.getString("moonDetail.newMoon"));
            } else if (absPhase > 95.0) { // Full moon
                path = path + MoonDetailContainer.FULL_MOON;
                this.setToolTipText(this.bundle.getString("moonDetail.fullMoon"));
            } else {
                // Moon waning or waxing?
                if (phase <= 0) {
                    path = path + "waning";
                } else {
                    path = path + "waxing";
                }

                // Get phase percentage
                if ((absPhase > 5) && (absPhase <= 15)) {
                    path = path + "10.png";
                } else if ((absPhase > 15) && (absPhase <= 25)) {
                    path = path + "20.png";
                } else if ((absPhase > 25) && (absPhase <= 35)) {
                    path = path + "30.png";
                } else if ((absPhase > 35) && (absPhase <= 45)) {
                    path = path + "40.png";
                } else if ((absPhase > 45) && (absPhase <= 55)) {
                    path = path + "50.png";
                } else if ((absPhase > 55) && (absPhase <= 65)) {
                    path = path + "60.png";
                } else if ((absPhase > 65) && (absPhase <= 75)) {
                    path = path + "70.png";
                } else if ((absPhase > 75) && (absPhase <= 85)) {
                    path = path + "80.png";
                } else if ((absPhase > 85) && (absPhase <= 95)) {
                    path = path + "90.png";
                }

                // Output format
                DecimalFormat df = new DecimalFormat("0.00");
                DecimalFormatSymbols dfs = new DecimalFormatSymbols();
                dfs.setDecimalSeparator('.');
                df.setDecimalFormatSymbols(dfs);

                this.setToolTipText(this.bundle.getString("moonDetail.moonIllumination") + df.format(absPhase));
            }

        } else { // Moon is below horizon
            path = path + MoonDetailContainer.BELOW_HORIZON;
            this.setToolTipText(this.bundle.getString("moonDetail.moonBelowHorizon"));
        }

        // Load & set image
        URL urlImage = this.moonImages.getImageURL(path).orElse(null);
        Image image = Toolkit.getDefaultToolkit().getImage(urlImage);
        ImageIcon icon = new ImageIcon(image);

        this.setHorizontalAlignment(SwingConstants.LEFT);
        this.setIcon(icon);

    }

}
