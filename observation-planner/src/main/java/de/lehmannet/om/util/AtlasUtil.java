/* ====================================================================
 * /util/AtlasUtil.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.util;

import de.lehmannet.om.EquPosition;

public class AtlasUtil {

    // Uranometria borders and pages
    private static final int DEC_BORDERS[] = new int[] { -900, -845, -725, -610, -500, -390, -280, -170, -55, 55, 170,
            280, 390, 500, 610, 725, 845, 900 };
    private static final int DIV[] = new int[] { 2, 12, 20, 24, 30, 36, 45, 45, 45, 45, 45, 36, 30, 24, 20, 12, 2 };

    // Uranometria 2000.0 borders and pages
    private static final double DEC_BORDERS2000[] = new double[] { 84.5, 73.5, 62.0, 51.0, 40.0, 29.0, 17.0, 5.5, 0.0,
            0.0 };
    private static final int DIV2000[] = new int[] { 1, 6, 10, 12, 15, 18, 18, 20, 20, 0 };

    // Millenium StarAtlas pages
    private static final int[] P_ZONE = { 2, 4, 8, 10, 12, 14, 16, 20, 20, 22, 22, 24, 24, 24, 24, 24, 24, 24, 24, 24,
            22, 22, 20, 20, 16, 14, 12, 10, 8, 4, 2 };

    public static int getSkyAtlas2000Page(EquPosition position) {

        int start = 472;

        double dec = position.getDecAngle().toDegree();
        double ra = position.getRaDecimalHours();

        int page = -1;

        if (Math.abs(dec) < 18.5) {
            page = 9 + (int) (ra / 3 + 5 / 6);
            if (page == 9) {
                page = 17;
            }
        } else if (Math.abs(dec) < 52) {
            page = 4 + (int) (ra / 4);
            if (dec < 0) {
                page = page + 14;
            }
        } else {
            page = 1 + (int) (ra / 8);
            if (dec < 0) {
                page = page + 23;
            }
        }

        return page;

    }

    public static int getUranometriaPage(EquPosition position) {

        int start = 472;

        double dec = position.getDecAngle().toDegree();
        double ra = position.getRaDecimalHours();

        int page = -1;

        int divisor = 0;
        while (DEC_BORDERS[divisor + 1] < (dec * 10)) {
            start = start - DIV[divisor + 1];
            divisor++;
        }

        double angle = (ra * DIV[divisor]) / 24;
        if (DIV[divisor] >= 20) {
            angle = angle + 0.5;
        } else if (DIV[divisor] == 12) {
            angle = angle + (5 / 12);
        }

        page = ((int) angle % DIV[divisor]) + start;
        if (page >= 472) {
            page = (472 + 473) - page;
        }

        return page;

    }

    public static String getUranometria2000Page(EquPosition position) {

        int i = 0;
        boolean south = false;
        int panel;

        double ra = position.getRaDecimalHours();
        double dec = position.getDecAngle().toDegree();

        if (dec < 0.0) {
            dec = -dec;
            south = true; /* South is mirror of North */
        }

        panel = 1;
        while (DIV2000[i] != 0 && dec <= DEC_BORDERS2000[i]) {
            panel += DIV2000[i];
            i++;
        }

        ra -= 12.0 / DIV2000[i];
        if (ra >= 24.0) {
            ra -= 24.0;
        }

        if (ra < 0.0) {
            ra += 24.0;
        }

        if (south && (DIV2000[i + 1] != 0)) {
            panel = 222 - panel - DIV2000[i];
        }

        panel = panel + (int) (DIV2000[i] * (24.0 - ra) / 24.0);
        int volume = south ? 2 : 1;

        return "" + panel + " (Vol. " + volume + ")";

    }

    public static int getMilleniumStarAtlasPage(EquPosition position) {

        double dec = position.getDecAngle().toDegree();
        double ra = position.getRaDecimalHours();

        int page = -1;

        if (dec >= 87) {
            if ((ra < 4) || (ra > 16)) {
                page = 2;
            } else {
                page = 1;
            }
        } else if (dec <= -87) {
            if ((ra < 4) || (ra > 16)) {
                page = 516;
            } else {
                page = 515;
            }
        } else {
            int gore = (int) (ra / 8);
            int zone = (int) ((93 - dec) / 6);

            double remain = (Math.ceil(ra / 8.) * 8.) - ra;

            page = (int) ((remain * P_ZONE[zone]) / 8.) + 1 + (gore * 516);

            while (zone != 0) {
                page += P_ZONE[zone];
                zone--;
            }

        }

        return page;

    }

}
