/* ====================================================================
 * /util/Ephemerides.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.util;

import java.util.Calendar;
import java.util.Objects;

import de.lehmannet.om.Angle;
import de.lehmannet.om.EquPosition;

// See http://ssd.jpl.nasa.gov/txt/aprx_pos_planets.pdf
// http://www.stjarnhimlen.se/comp/ppcomp.html
// and http://www.ngc0815.de/ephemeriden.html  (german)
// and http://members.aon.at/puschnig/Ephemeriden/Index.html  (german)
public class Ephemerides {

    public static final int SUN = 0;
    public static final int MERCURY = 1;
    public static final int VENUS = 2;
    public static final int MOON = 301;
    public static final int MARS = 4;
    public static final int JUPITER = 5;
    public static final int SATURN = 6;
    public static final int URANUS = 7;
    public static final int NEPTUNE = 8;

    // This is not very nice, as those keys should map to the keys from
    // SolarSystemTarget extenstion
    private static final String KEY_SUN = "SUN";
    private static final String KEY_MERCURY = "MERCURY";
    private static final String KEY_VENUS = "VENUS";
    private static final String KEY_EARTH = "EARTH";
    private static final String KEY_MOON = "MOON";
    private static final String KEY_MARS = "MARS";
    private static final String KEY_JUPITER = "JUPITER";
    private static final String KEY_SATURN = "SATURN";
    private static final String KEY_URANUS = "URANUS";
    private static final String KEY_NEPTUNE = "NEPTUNE";

    public static int planetKey(String planet) {

        if (KEY_SUN.equals(planet)) {
            return Ephemerides.SUN;
        } else if (KEY_MERCURY.equals(planet)) {
            return Ephemerides.MERCURY;
        } else if (KEY_VENUS.equals(planet)) {
            return Ephemerides.VENUS;
        } else if (KEY_MOON.equals(planet)) {
            return Ephemerides.MOON;
        } else if (KEY_MARS.equals(planet)) {
            return Ephemerides.MARS;
        } else if (KEY_JUPITER.equals(planet)) {
            return Ephemerides.JUPITER;
        } else if (KEY_SATURN.equals(planet)) {
            return Ephemerides.SATURN;
        } else if (KEY_URANUS.equals(planet)) {
            return Ephemerides.URANUS;
        } else if (KEY_NEPTUNE.equals(planet)) {
            return Ephemerides.NEPTUNE;
        }

        return -1;

    }

    public static EquPosition getPosition(int planet, Calendar date) {

        // We've own methods for sun...
        if (planet == SUN) {
            return Ephemerides.getSunPosition(date);
        } else if (planet == -1) {
            return null; // Object/Planet unknown
        }

        double[] geo = Ephemerides.getGeocentricPosition(planet, date);
        double ecl = Ephemerides.getObliquityOfTheEcliptic(date);
        ecl = Math.toRadians(ecl);

        double xe = geo[0];
        double ye = geo[1] * Math.cos(ecl) - geo[2] * Math.sin(ecl);
        double ze = geo[1] * Math.sin(ecl) + geo[2] * Math.cos(ecl);

        double RA = Math.atan2(ye, xe);
        double Dec = Math.atan2(ze, Math.sqrt(xe * xe + ye * ye));
        RA = Math.toDegrees(RA);
        Dec = Math.toDegrees(Dec);

        if (RA < 0) {
            RA = 360 + RA;
        }

        return new EquPosition(new Angle(RA, Angle.DEGREE), new Angle(Dec, Angle.DEGREE));

    }

    public static EquPosition getSunPosition(Calendar date) {

        double[] equRecPos = Ephemerides.getSunEquatorialRectangularGeocentric(date);

        double RA = Math.atan2(equRecPos[1], equRecPos[0]);
        double Dec = Math.atan2(equRecPos[2], Math.sqrt(equRecPos[0] * equRecPos[0] + equRecPos[1] * equRecPos[1]));
        RA = Math.toDegrees(RA);
        Dec = Math.toDegrees(Dec);

        if (RA < 0) {
            RA = 360 + RA;
        }

        return new EquPosition(new Angle(RA, Angle.DEGREE), new Angle(Dec, Angle.DEGREE));

    }

    public static EquPosition getMoonPosition(Calendar date, double geoLongitude) {

        double[] helioPos = Ephemerides.getHeliocentricPosition(Ephemerides.MOON, date);

        double gcLat = helioPos[4] - 0.1924 * Math.sin(Math.toRadians(2 * helioPos[4]));
        gcLat = Math.toDegrees(gcLat);
        double rho = 0.99833 + 0.00167 * Math.cos(Math.toRadians(2 * helioPos[4]));
        rho = Math.toDegrees(rho);

        // Moons equatorial position
        EquPosition equPos = Ephemerides.getPosition(Ephemerides.MOON, date);
        double RA = Objects.requireNonNull(equPos).getRaAngle().toDegree();
        double Dec = equPos.getDecAngle().toDegree();

        double HA = Ephemerides.getMoonHourAngle(date, geoLongitude);

        double g = Math.atan(Math.tan(Math.toRadians(gcLat)) / Math.cos(Math.toRadians(HA)));
        g = Math.toDegrees(g);

        // Moon parallax
        double r = Ephemerides.getDistance(Ephemerides.MOON, date);
        r = Ephemerides.getPerturbationsForMoonDistance(r, date);
        double mpar = Math.asin(Math.toRadians(1 / r));
        mpar = Math.toDegrees(mpar);

        double topRA = RA - mpar * rho * Math.cos(Math.toRadians(gcLat)) * Math.sin(Math.toRadians(HA))
                / Math.cos(Math.toRadians(Dec));
        double topDecl = Dec - mpar * rho * Math.sin(Math.toRadians(gcLat)) * Math.sin(Math.toRadians(g - Dec))
                / Math.sin(Math.toRadians(g));

        if (topRA < 0) {
            topRA = 360 + topRA;
        }

        return new EquPosition(new Angle(topRA, Angle.DEGREE), new Angle(topDecl, Angle.DEGREE));

    }

    public static boolean isMoonAboveHorizon(Calendar date, double geoLongitude, double geoLatitude) {

        // Moons equatorial position
        EquPosition equPos = Ephemerides.getPosition(Ephemerides.MOON, date);
        double Dec = Objects.requireNonNull(equPos).getDecAngle().toDegree();

        // Moon hour angle
        double HA = Ephemerides.getMoonHourAngle(date, geoLongitude);

        double sinh = Math.sin(Math.toRadians(geoLatitude)) * Math.sin(Math.toRadians(Dec))
                + Math.cos(Math.toRadians(geoLatitude)) * Math.cos(Math.toRadians(Dec)) * Math.cos(Math.toRadians(HA));
        double h = Math.asin(sinh);
        h = Math.toDegrees(h);

        return h > 0;

    }

    public static double altitudeAboveHorizon(EquPosition equPos, Calendar date, double geoLongitude,
            double geoLatitude) {

        double Dec = equPos.getDecAngle().toDegree();
        double RA = equPos.getRaAngle().toDegree();

        double Ls = Ephemerides.getSunLongitude(date);

        // Greenwich Mean Sidereal Time
        double GMST0 = Ls + 180;

        double UT = date.get(Calendar.HOUR_OF_DAY) - (date.get(Calendar.ZONE_OFFSET) / 3600000)
                + date.get(Calendar.MINUTE) / 60.0; // Offset is in ms

        // Local Sidereal Time
        double LST = GMST0 + (UT * 15) + geoLongitude;

        // Moons hour angle
        double HA = LST - RA;

        double sinh = Math.sin(Math.toRadians(geoLatitude)) * Math.sin(Math.toRadians(Dec))
                + Math.cos(Math.toRadians(geoLatitude)) * Math.cos(Math.toRadians(Dec)) * Math.cos(Math.toRadians(HA));
        double h = Math.asin(sinh);
        h = Math.toDegrees(h);

        return h;

    }

    public static double getAzimut(EquPosition equPos, Calendar date, double geoLongitude, double geoLatitude) {

        double Dec = equPos.getDecAngle().toDegree();

        // Moon hour angle
        double RA = equPos.getRaAngle().toDegree();

        double Ls = Ephemerides.getSunLongitude(date);

        // Greenwich Mean Sidereal Time
        double GMST0 = Ls + 180;

        double UT = date.get(Calendar.HOUR_OF_DAY) - (date.get(Calendar.ZONE_OFFSET) / 3600000)
                + date.get(Calendar.MINUTE) / 60.0; // Offset is in ms

        // Local Sidereal Time
        double LST = GMST0 + (UT * 15) + geoLongitude;

        // Hour angle
        double HA = LST - RA;

        double sinHA = Math.sin(Math.toRadians(HA));
        double Nnr = Math.cos(Math.toRadians(HA)) * Math.sin(Math.toRadians(geoLatitude))
                - Math.tan(Math.toRadians(Dec)) * Math.cos(Math.toRadians(geoLatitude));
        double at = Math.atan2(sinHA, Nnr);
        at = Math.toDegrees(at);

        at = at + 180; // North = 180deg;

        return (at % 360);

    }

    public static double getMoonPhase(Calendar date) {

        // Get current phase
        double currentPhase = Ephemerides.getMoonPhasePercentage(date);

        // Get phase in 1 hours
        Calendar phaseTrendDate = (Calendar) date.clone();
        phaseTrendDate.add(Calendar.HOUR_OF_DAY, 24); // Calculate phase one day ahead to get trend

        double trendPhase = Ephemerides.getMoonPhasePercentage(phaseTrendDate);

        if (currentPhase > trendPhase) { // Moon waning
            currentPhase = currentPhase * -1; // Nagative phase indicated waning
        }

        return currentPhase;

    }

    private static double getMoonPhasePercentage(Calendar date) {

        double[] moonEcliptic = Ephemerides.getGeocentricPosition(Ephemerides.MOON, date);

        double slon = Ephemerides.getSunLongitude(date);
        double mlon = moonEcliptic[3];
        double mlat = moonEcliptic[4];

        double elong = Math.acos(Math.cos(Math.toRadians(slon - mlon)) * Math.cos(Math.toRadians(mlat)));
        elong = Math.toDegrees(elong);

        double FV = 180 - elong;

        return (1 + Math.cos(Math.toRadians(FV))) / 2;

    }

    private static double getMoonHourAngle(Calendar date, double geoLongitude) {

        // Moons equatorial position
        EquPosition equPos = Ephemerides.getPosition(Ephemerides.MOON, date);
        double RA = Objects.requireNonNull(equPos).getRaAngle().toDegree();

        double Ls = Ephemerides.getSunLongitude(date);

        // Greenwich Mean Sidereal Time
        double GMST0 = Ls + 180;

        double UT = date.get(Calendar.HOUR_OF_DAY) - (date.get(Calendar.ZONE_OFFSET) / 3600000)
                + date.get(Calendar.MINUTE) / 60.0; // Offset is in ms

        // Local Sidereal Time
        double LST = GMST0 + (UT * 15) + geoLongitude;

        // Moons hour angle

        return LST - RA;

    }

    private static double[] getSunXY(int planet, Calendar date) {

        double e = Ephemerides.getEccentricity(planet, date);
        double E = Ephemerides.getEccentricAnomaly(planet, date);

        double xv = Math.cos(Math.toRadians(E)) - e;
        double yv = Math.sqrt(1.0 - e * e) * Math.sin(Math.toRadians(E));

        return new double[] { xv, yv };

    }

    private static double[] getXY(int planet, Calendar date) {

        double e = Ephemerides.getEccentricity(planet, date);
        double E = Ephemerides.getEccentricAnomaly(planet, date);
        double a = Ephemerides.getSemiMajorAxis(planet, date);

        double xv = a * (Math.cos(Math.toRadians(E)) - e);
        double yv = a * (Math.sqrt(1.0 - e * e) * Math.sin(Math.toRadians(E)));

        return new double[] { xv, yv };

    }

    private static double getDistance(int planet, Calendar date) {

        double[] XvYv = Ephemerides.getXY(planet, date);

        if (Ephemerides.SUN == planet) {
            XvYv = Ephemerides.getSunXY(planet, date);
        }

        return Math.sqrt(XvYv[0] * XvYv[0] + XvYv[1] * XvYv[1]);

    }

    private static double getTrueAnomaly(int planet, Calendar date) {

        double[] XvYv = Ephemerides.getXY(planet, date);

        if (Ephemerides.SUN == planet) {
            XvYv = Ephemerides.getSunXY(planet, date);
        }

        double v = Math.atan2(XvYv[1], XvYv[0]);

        return Math.toDegrees(v);

    }

    private static double[] getSunEclipticRectangularGeocentric(Calendar date) {

        double lonsun = Ephemerides.getSunLongitude(date);
        double r = Ephemerides.getDistance(Ephemerides.SUN, date);

        double xs = r * Math.cos(Math.toRadians(lonsun));
        double ys = r * Math.sin(Math.toRadians(lonsun));

        return new double[] { xs, ys };

    }

    private static double getSunLongitude(Calendar date) {

        int planet = Ephemerides.SUN;

        return Ephemerides.getTrueAnomaly(planet, date) + Ephemerides.getArgumentOfPerihelion(planet, date);

    }

    private static double[] getSunEquatorialRectangularGeocentric(Calendar date) {

        double[] ecliptivRectangularGeocentric = Ephemerides.getSunEclipticRectangularGeocentric(date);

        double ecl = Ephemerides.getObliquityOfTheEcliptic(date);
        ecl = Math.toRadians(ecl);

        double xe = ecliptivRectangularGeocentric[0];
        double ye = ecliptivRectangularGeocentric[1] * Math.cos(ecl);
        double ze = ecliptivRectangularGeocentric[1] * Math.sin(ecl);

        return new double[] { xe, ye, ze };

    }

    private static double[] getHeliocentricPosition(int planet, Calendar date) {

        double r = Ephemerides.getDistance(planet, date);

        if (planet == Ephemerides.MOON) {
            double perturbationDist = 0;
            perturbationDist = Ephemerides.getPerturbationsForMoonDistance(r, date);
            r = perturbationDist;
        }

        double N = Ephemerides.getLongitudeOfTheAscendingNode(planet, date);
        N = Math.toRadians(N);
        double v = Ephemerides.getTrueAnomaly(planet, date);
        v = Math.toRadians(v);
        double w = Ephemerides.getArgumentOfPerihelion(planet, date);
        w = Math.toRadians(w);
        double i = Ephemerides.getInclination(planet, date);
        i = Math.toRadians(i);

        double xh = r * (Math.cos(N) * Math.cos(v + w) - Math.sin(N) * Math.sin(v + w) * Math.cos(i));
        double yh = r * (Math.sin(N) * Math.cos(v + w) + Math.cos(N) * Math.sin(v + w) * Math.cos(i));
        double zh = r * (Math.sin(v + w) * Math.sin(i));

        double lonecl = Math.atan2(yh, xh);
        double latecl = Math.atan2(zh, Math.sqrt(xh * xh + yh * yh));
        lonecl = Math.toDegrees(lonecl);
        latecl = Math.toDegrees(latecl);

        if (planet == Ephemerides.MOON) {
            double[] per = Ephemerides.getPerturbationsForMoon(lonecl, latecl, date);
            lonecl = per[0];
            latecl = per[1];

            xh = r * Math.cos(Math.toRadians(lonecl)) * Math.cos(Math.toRadians(latecl));
            yh = r * Math.sin(Math.toRadians(lonecl)) * Math.cos(Math.toRadians(latecl));
            zh = r * Math.sin(Math.toRadians(latecl));
        }

        return new double[] { xh, yh, zh, lonecl, latecl };

    }

    private static double[] getGeocentricPosition(int planet, Calendar date) {

        double[] helio = Ephemerides.getHeliocentricPosition(planet, date);

        if (planet == Ephemerides.MOON) {
            return helio;
        }

        // Sun
        double rs = Ephemerides.getDistance(Ephemerides.SUN, date);
        double lonsun = Ephemerides.getSunLongitude(date);
        lonsun = Math.toRadians(lonsun);
        double xs = rs * Math.cos(lonsun);
        double ys = rs * Math.sin(lonsun);

        // Planet
        double xg = helio[0] + xs;
        double yg = helio[1] + ys;
        double zg = helio[2];

        return new double[] { xg, yg, zg };

    }

    private static double getD(Calendar date) {

        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH) + 1;
        int day = date.get(Calendar.DAY_OF_MONTH);
        double time = (double) date.get(Calendar.HOUR_OF_DAY) / (double) 24;

        double d = 367 * year - 7 * (year + (month + 9) / 12) / 4 + 275 * month / 9 + day - 730530;
        d = d + time;

        return d;

    }

    private static double getObliquityOfTheEcliptic(Calendar date) {

        double d = Ephemerides.getD(date);

        return 23.4393 - 3.563E-7 * d;

    }

    private static double getLongitudeOfTheAscendingNode(int planet, Calendar date) {

        double d = Ephemerides.getD(date);

        switch (planet) {
        case Ephemerides.SUN:
            return 0.0;
        case Ephemerides.MERCURY:
            return 48.3313 + 3.24587E-5 * d;
        case Ephemerides.VENUS:
            return 76.6799 + 2.46590E-5 * d;
        case Ephemerides.MOON:
            return 125.1228 - 0.0529538083 * d;
        case Ephemerides.MARS:
            return 49.5574 + 2.11081E-5 * d;
        case Ephemerides.JUPITER:
            return 100.4542 + 2.76854E-5 * d;
        case Ephemerides.SATURN:
            return 113.6634 + 2.38980E-5 * d;
        case Ephemerides.URANUS:
            return 74.0005 + 1.3978E-5 * d;
        case Ephemerides.NEPTUNE:
            return 131.7806 + 3.0173E-5 * d;
        default:
            return Double.NaN;
        }

    }

    private static double getInclination(int planet, Calendar date) {

        double d = Ephemerides.getD(date);

        switch (planet) {
        case Ephemerides.SUN:
            return 0.0;
        case Ephemerides.MERCURY:
            return 7.0047 + 5.00E-8 * d;
        case Ephemerides.VENUS:
            return 3.3946 + 2.75E-8 * d;
        case Ephemerides.MOON:
            return 5.1454;
        case Ephemerides.MARS:
            return 1.8497 - 1.78E-8 * d;
        case Ephemerides.JUPITER:
            return 1.3030 - 1.557E-7 * d;
        case Ephemerides.SATURN:
            return 2.4886 - 1.081E-7 * d;
        case Ephemerides.URANUS:
            return 0.7733 + 1.9E-8 * d;
        case Ephemerides.NEPTUNE:
            return 11.7700 - 2.55E-7 * d;
        default:
            return Double.NaN;
        }

    }

    private static double getArgumentOfPerihelion(int planet, Calendar date) {

        double d = Ephemerides.getD(date);

        switch (planet) {
        case Ephemerides.SUN:
            return 282.9404 + 4.70935E-5 * d;
        case Ephemerides.MERCURY:
            return 29.1241 + 1.01444E-5 * d;
        case Ephemerides.VENUS:
            return 54.8910 + 1.38374E-5 * d;
        case Ephemerides.MOON:
            return 318.0634 + 0.1643573223 * d;
        case Ephemerides.MARS:
            return 286.5016 + 2.92961E-5 * d;
        case Ephemerides.JUPITER:
            return 273.8777 + 1.64505E-5 * d;
        case Ephemerides.SATURN:
            return 339.3939 + 2.97661E-5 * d;
        case Ephemerides.URANUS:
            return 96.6612 + 3.0565E-5 * d;
        case Ephemerides.NEPTUNE:
            return 272.8461 - 6.027E-6 * d;
        default:
            return Double.NaN;
        }

    }

    private static double getSemiMajorAxis(int planet, Calendar date) {

        double d = Ephemerides.getD(date);

        switch (planet) {
        case Ephemerides.SUN:
            return 1.000000;
        case Ephemerides.MERCURY:
            return 0.387098;
        case Ephemerides.VENUS:
            return 0.723330;
        case Ephemerides.MOON:
            return 60.2666; // EARTH RADI!!!
        case Ephemerides.MARS:
            return 1.523688;
        case Ephemerides.JUPITER:
            return 5.20256;
        case Ephemerides.SATURN:
            return 9.55475;
        case Ephemerides.URANUS:
            return 19.18171 - 1.55E-8 * d;
        case Ephemerides.NEPTUNE:
            return 30.05826 + 3.313E-8 * d;
        default:
            return Double.NaN;
        }

    }

    private static double getEccentricity(int planet, Calendar date) {

        double d = Ephemerides.getD(date);

        switch (planet) {
        case Ephemerides.SUN:
            return 0.016709 - 1.151E-9 * d;
        case Ephemerides.MERCURY:
            return 0.205635 + 5.59E-10 * d;
        case Ephemerides.VENUS:
            return 0.006773 - 1.302E-9 * d;
        case Ephemerides.MOON:
            return 0.054900;
        case Ephemerides.MARS:
            return 0.093405 + 2.516E-9 * d;
        case Ephemerides.JUPITER:
            return 0.048498 + 4.469E-9 * d;
        case Ephemerides.SATURN:
            return 0.055546 - 9.499E-9 * d;
        case Ephemerides.URANUS:
            return 0.047318 + 7.45E-9 * d;
        case Ephemerides.NEPTUNE:
            return 0.008606 + 2.15E-9 * d;
        default:
            return Double.NaN;
        }

    }

    private static double getMeanAnomaly(int planet, Calendar date) {

        double d = Ephemerides.getD(date);

        switch (planet) {
        case Ephemerides.SUN:
            return 356.0470 + 0.9856002585 * d;
        case Ephemerides.MERCURY:
            return 168.6562 + 4.0923344368 * d;
        case Ephemerides.VENUS:
            return 48.0052 + 1.6021302244 * d;
        case Ephemerides.MOON:
            return 115.3654 + 13.0649929509 * d;
        case Ephemerides.MARS:
            return 18.6021 + 0.5240207766 * d;
        case Ephemerides.JUPITER:
            return 19.8950 + 0.0830853001 * d;
        case Ephemerides.SATURN:
            return 316.9670 + 0.0334442282 * d;
        case Ephemerides.URANUS:
            return 142.5905 + 0.011725806 * d;
        case Ephemerides.NEPTUNE:
            return 260.2471 + 0.005995147 * d;
        default:
            return Double.NaN;
        }

    }

    private static double getEccentricAnomaly(int planetKey, Calendar date) {

        double aoP = Ephemerides.getArgumentOfPerihelion(planetKey, date);

        // Key might be invalid
        if (Double.isNaN(aoP)) {
            return Double.NaN;
        }

        // Calculate mean anomaly
        double M = Ephemerides.getMeanAnomaly(planetKey, date);
        // Calculate eccentric anomaly (with Newton)
        double e = Ephemerides.getEccentricity(planetKey, date);

        double E = M + (180 / Math.PI) * e * Math.sin(Math.toRadians(M));
        double deltaM = 0.0;
        double deltaE = 0.0;
        for (int i = 0; i < 10; i++) {

            deltaM = M + (e * (180 / Math.PI)) * Math.sin(Math.toRadians(E)) - E;
            deltaE = deltaM / (1 - e * Math.cos(Math.toRadians(E)));

            E = E + deltaE;

        }

        return E;

    }

    private static double getPerturbationsForMoonDistance(double pdistance, Calendar date) {

        double Mm = Ephemerides.getMeanAnomaly(Ephemerides.MOON, date);
        double Ms = Ephemerides.getMeanAnomaly(Ephemerides.SUN, date);
        double Nm = Ephemerides.getLongitudeOfTheAscendingNode(Ephemerides.MOON, date);
        double wm = Ephemerides.getArgumentOfPerihelion(Ephemerides.MOON, date);
        double ws = Ephemerides.getArgumentOfPerihelion(Ephemerides.SUN, date);
        double Ls = Ms + ws;
        double Lm = Mm + wm + Nm;
        double D = Lm - Ls;
        double F = Lm - Nm;

        double distance = pdistance + (-0.58 * Math.cos(Math.toRadians(Mm - 2 * D)));
        distance = distance + (-0.46 * Math.cos(Math.toRadians(2 * D)));

        return distance;

    }

    private static double[] getPerturbationsForMoon(double plongitude, double platitude, Calendar date) {

        double Mm = Ephemerides.getMeanAnomaly(Ephemerides.MOON, date);
        double Ms = Ephemerides.getMeanAnomaly(Ephemerides.SUN, date);
        double Nm = Ephemerides.getLongitudeOfTheAscendingNode(Ephemerides.MOON, date);
        double wm = Ephemerides.getArgumentOfPerihelion(Ephemerides.MOON, date);
        double ws = Ephemerides.getArgumentOfPerihelion(Ephemerides.SUN, date);
        double Ls = Ms + ws;
        double Lm = Mm + wm + Nm;
        double D = Lm - Ls;
        double F = Lm - Nm;

        double longitude = plongitude + (-1.274 * Math.sin(Math.toRadians(Mm - 2 * D)));
        longitude = longitude + (+0.658 * Math.sin(Math.toRadians(2 * D)));
        longitude = longitude + (-0.186 * Math.sin(Math.toRadians(Ms)));
        longitude = longitude + (-0.059 * Math.sin(Math.toRadians(2 * Mm - 2 * D)));
        longitude = longitude + (-0.057 * Math.sin(Math.toRadians(Mm - 2 * D + Ms)));
        longitude = longitude + (+0.053 * Math.sin(Math.toRadians(Mm + 2 * D)));
        longitude = longitude + (+0.046 * Math.sin(Math.toRadians(2 * D - Ms)));
        longitude = longitude + (+0.041 * Math.sin(Math.toRadians(Mm - Ms)));
        longitude = longitude + (-0.035 * Math.sin(Math.toRadians(D)));
        longitude = longitude + (-0.031 * Math.sin(Math.toRadians(Mm + Ms)));
        longitude = longitude + (-0.015 * Math.sin(Math.toRadians(2 * F - 2 * D)));
        longitude = longitude + (+0.011 * Math.sin(Math.toRadians(Mm - 4 * D)));

        double latitude = platitude + (-0.173 * Math.sin(Math.toRadians(F - 2 * D)));
        latitude = latitude + (-0.055 * Math.sin(Math.toRadians(Mm - F - 2 * D)));
        latitude = latitude + (-0.046 * Math.sin(Math.toRadians(Mm + F - 2 * D)));
        latitude = latitude + (+0.033 * Math.sin(Math.toRadians(F + 2 * D)));
        latitude = latitude + (+0.017 * Math.sin(Math.toRadians(2 * Mm + F)));

        return new double[] { longitude, latitude };

    }

    /*
     * public static void main(String args[]) {
     * 
     * Calendar date = Calendar.getInstance(); date.set(Calendar.HOUR_OF_DAY, 1); date.set(Calendar.DAY_OF_MONTH, 22);
     * //date.set(Calendar.MONTH, 11); System.out.println("" + new java.util.Date(date.getTimeInMillis()) + "\n" +
     * Ephemerides.getMoonPosition(date, 0)); System.out.println("Moon phase: " + Ephemerides.getMoonPhase(date) +
     * "\t Date: " + new java.util.Date(date.getTimeInMillis())); System.out.println("Moon auf? " +
     * Ephemerides.isMoonAboveHorizon(date, 9, 49)); }
     */

}
