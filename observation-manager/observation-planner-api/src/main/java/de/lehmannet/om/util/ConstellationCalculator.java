package de.lehmannet.om.util;

import java.util.ArrayList;
import java.util.List;

import de.lehmannet.om.Angle;
import de.lehmannet.om.Constellation;
import de.lehmannet.om.EquPosition;

public final class ConstellationCalculator {

    private List<ConstellationLimit> limits = new ArrayList<>();

    private static final ConstellationCalculator INSTANCE = new ConstellationCalculator();

    public static ConstellationCalculator getInstance() {
        return INSTANCE;
    }

    private ConstellationCalculator() {
        initLimits();
    }

    record ConstellationLimit(double ra, double rau, double dec, Constellation constellation) {
    }

    //
    // Input: coordinates (RA in hours, declination in degrees, epoch in years AD)
    // Output: constellation abbreviation (3 letters), or '' on error
    //
    public Constellation getConstellation(EquPosition pos, double epoch) {

        var newCoords = precess(pos, epoch, 1875.0);
        var dec = newCoords.getDecAngle().toDegree();
        var ra = newCoords.getRaAngle().toDegree() / 15;
        for (ConstellationLimit limit : limits) {

            if (dec >= limit.dec && ra >= limit.ra && ra < limit.rau) {
                return limit.constellation;
            }
        }

        throw new RuntimeException("Constellation not found");
    }

    private EquPosition precess(EquPosition position, double epoch1, double epoch2) {

        var cdr = Math.PI / 180.0;
        var csr = cdr / 3600.0;

        var dec1 = position.getDecAngle().toRadiant();
        var a = Math.cos(dec1);

        var ra1 = position.getRaAngle().toRadiant();
        double x1[] = { a * Math.cos(ra1), a * Math.sin(ra1), Math.sin(dec1) };

        var t = 0.001 * (epoch2 - epoch1);
        var st = 0.001 * (epoch1 - 1900.0);
        a = csr * t * (23042.53 + st * (139.75 + 0.06 * st) + t * (30.23 - 0.27 * st + 18.0 * t));
        var b = csr * t * t * (79.27 + 0.66 * st + 0.32 * t) + a;
        var c = csr * t * (20046.85 - st * (85.33 + 0.37 * st) + t * (-42.67 - 0.37 * st - 41.8 * t));
        var sina = Math.sin(a);
        var sinb = Math.sin(b);
        var sinc = Math.sin(c);
        var cosa = Math.cos(a);
        var cosb = Math.cos(b);
        var cosc = Math.cos(c);
        double r[][] = { { 0.0, 0.0, 0.0 }, { 0.0, 0.0, 0.0 }, { 0.0, 0.0, 0.0 } };
        r[0][0] = cosa * cosb * cosc - sina * sinb;
        r[0][1] = -cosa * sinb - sina * cosb * cosc;
        r[0][2] = -cosb * sinc;
        r[1][0] = sina * cosb + cosa * sinb * cosc;
        r[1][1] = cosa * cosb - sina * sinb * cosc;
        r[1][2] = -sinb * sinc;
        r[2][0] = cosa * sinc;
        r[2][1] = -sina * sinc;
        r[2][2] = cosc;

        double x2[] = { 0.0, 0.0, 0.0 };
        for (var i = 0; i < 3; i++) {
            x2[i] = r[i][0] * x1[0] + r[i][1] * x1[1] + r[i][2] * x1[2];
        }
        var ra2 = Math.atan2(x2[1], x2[0]);

        if (ra2 < 0.0) {
            ra2 += 2.0 * Math.PI;
        }

        var dec2 = Math.asin(x2[2]);

        return new EquPosition(new Angle(ra2, Angle.RADIANT), new Angle(dec2, Angle.RADIANT));

    }

    // TODO read from file
    private void initLimits() {

        limits.add(new ConstellationLimit(0.0000, 24.0000, 88.0000, Constellation.getConstellationByAbb("UMi")));
        limits.add(new ConstellationLimit(8.0000, 14.5000, 86.5000, Constellation.getConstellationByAbb("UMi")));
        limits.add(new ConstellationLimit(21.0000, 23.0000, 86.1667, Constellation.getConstellationByAbb("UMi")));
        limits.add(new ConstellationLimit(18.0000, 21.0000, 86.0000, Constellation.getConstellationByAbb("UMi")));
        limits.add(new ConstellationLimit(0.0000, 8.0000, 85.0000, Constellation.getConstellationByAbb("Cep")));
        limits.add(new ConstellationLimit(9.1667, 10.6667, 82.0000, Constellation.getConstellationByAbb("Cam")));
        limits.add(new ConstellationLimit(0.0000, 5.0000, 80.0000, Constellation.getConstellationByAbb("Cep")));
        limits.add(new ConstellationLimit(10.6667, 14.5000, 80.0000, Constellation.getConstellationByAbb("Cam")));
        limits.add(new ConstellationLimit(17.5000, 18.0000, 80.0000, Constellation.getConstellationByAbb("UMi")));
        limits.add(new ConstellationLimit(20.1667, 21.0000, 80.0000, Constellation.getConstellationByAbb("Dra")));
        limits.add(new ConstellationLimit(0.0000, 3.5083, 77.0000, Constellation.getConstellationByAbb("Cep")));
        limits.add(new ConstellationLimit(11.5000, 13.5833, 77.0000, Constellation.getConstellationByAbb("Cam")));
        limits.add(new ConstellationLimit(16.5333, 17.5000, 75.0000, Constellation.getConstellationByAbb("UMi")));
        limits.add(new ConstellationLimit(20.1667, 20.6667, 75.0000, Constellation.getConstellationByAbb("Cep")));
        limits.add(new ConstellationLimit(7.9667, 9.1667, 73.5000, Constellation.getConstellationByAbb("Cam")));
        limits.add(new ConstellationLimit(9.1667, 11.3333, 73.5000, Constellation.getConstellationByAbb("Dra")));
        limits.add(new ConstellationLimit(13.0000, 16.5333, 70.0000, Constellation.getConstellationByAbb("UMi")));
        limits.add(new ConstellationLimit(3.1000, 3.4167, 68.0000, Constellation.getConstellationByAbb("Cas")));
        limits.add(new ConstellationLimit(20.4167, 20.6667, 67.0000, Constellation.getConstellationByAbb("Dra")));
        limits.add(new ConstellationLimit(11.3333, 12.0000, 66.5000, Constellation.getConstellationByAbb("Dra")));
        limits.add(new ConstellationLimit(0.0000, 0.3333, 66.0000, Constellation.getConstellationByAbb("Cep")));
        limits.add(new ConstellationLimit(14.0000, 15.6667, 66.0000, Constellation.getConstellationByAbb("UMi")));
        limits.add(new ConstellationLimit(23.5833, 24.0000, 66.0000, Constellation.getConstellationByAbb("Cep")));
        limits.add(new ConstellationLimit(12.0000, 13.5000, 64.0000, Constellation.getConstellationByAbb("Dra")));
        limits.add(new ConstellationLimit(13.5000, 14.4167, 63.0000, Constellation.getConstellationByAbb("Dra")));
        limits.add(new ConstellationLimit(23.1667, 23.5833, 63.0000, Constellation.getConstellationByAbb("Cep")));
        limits.add(new ConstellationLimit(6.1000, 7.0000, 62.0000, Constellation.getConstellationByAbb("Cam")));
        limits.add(new ConstellationLimit(20.0000, 20.4167, 61.5000, Constellation.getConstellationByAbb("Dra")));
        limits.add(new ConstellationLimit(20.5367, 20.6000, 60.9167, Constellation.getConstellationByAbb("Cep")));
        limits.add(new ConstellationLimit(7.0000, 7.9667, 60.0000, Constellation.getConstellationByAbb("Cam")));
        limits.add(new ConstellationLimit(7.9667, 8.4167, 60.0000, Constellation.getConstellationByAbb("UMa")));
        limits.add(new ConstellationLimit(19.7667, 20.0000, 59.5000, Constellation.getConstellationByAbb("Dra")));
        limits.add(new ConstellationLimit(20.0000, 20.5367, 59.5000, Constellation.getConstellationByAbb("Cep")));
        limits.add(new ConstellationLimit(22.8667, 23.1667, 59.0833, Constellation.getConstellationByAbb("Cep")));
        limits.add(new ConstellationLimit(0.0000, 2.4333, 58.5000, Constellation.getConstellationByAbb("Cas")));
        limits.add(new ConstellationLimit(19.4167, 19.7667, 58.0000, Constellation.getConstellationByAbb("Dra")));
        limits.add(new ConstellationLimit(1.7000, 1.9083, 57.5000, Constellation.getConstellationByAbb("Cas")));
        limits.add(new ConstellationLimit(2.4333, 3.1000, 57.0000, Constellation.getConstellationByAbb("Cas")));
        limits.add(new ConstellationLimit(3.1000, 3.1667, 57.0000, Constellation.getConstellationByAbb("Cam")));
        limits.add(new ConstellationLimit(22.3167, 22.8667, 56.2500, Constellation.getConstellationByAbb("Cep")));
        limits.add(new ConstellationLimit(5.0000, 6.1000, 56.0000, Constellation.getConstellationByAbb("Cam")));
        limits.add(new ConstellationLimit(14.0333, 14.4167, 55.5000, Constellation.getConstellationByAbb("UMa")));
        limits.add(new ConstellationLimit(14.4167, 19.4167, 55.5000, Constellation.getConstellationByAbb("Dra")));
        limits.add(new ConstellationLimit(3.1667, 3.3333, 55.0000, Constellation.getConstellationByAbb("Cam")));
        limits.add(new ConstellationLimit(22.1333, 22.3167, 55.0000, Constellation.getConstellationByAbb("Cep")));
        limits.add(new ConstellationLimit(20.6000, 21.9667, 54.8333, Constellation.getConstellationByAbb("Cep")));
        limits.add(new ConstellationLimit(0.0000, 1.7000, 54.0000, Constellation.getConstellationByAbb("Cas")));
        limits.add(new ConstellationLimit(6.1000, 6.5000, 54.0000, Constellation.getConstellationByAbb("Lyn")));
        limits.add(new ConstellationLimit(12.0833, 13.5000, 53.0000, Constellation.getConstellationByAbb("UMa")));
        limits.add(new ConstellationLimit(15.2500, 15.7500, 53.0000, Constellation.getConstellationByAbb("Dra")));
        limits.add(new ConstellationLimit(21.9667, 22.1333, 52.7500, Constellation.getConstellationByAbb("Cep")));
        limits.add(new ConstellationLimit(3.3333, 5.0000, 52.5000, Constellation.getConstellationByAbb("Cam")));
        limits.add(new ConstellationLimit(22.8667, 23.3333, 52.5000, Constellation.getConstellationByAbb("Cas")));
        limits.add(new ConstellationLimit(15.7500, 17.0000, 51.5000, Constellation.getConstellationByAbb("Dra")));
        limits.add(new ConstellationLimit(2.0417, 2.5167, 50.5000, Constellation.getConstellationByAbb("Per")));
        limits.add(new ConstellationLimit(17.0000, 18.2333, 50.5000, Constellation.getConstellationByAbb("Dra")));
        limits.add(new ConstellationLimit(0.0000, 1.3667, 50.0000, Constellation.getConstellationByAbb("Cas")));
        limits.add(new ConstellationLimit(1.3667, 1.6667, 50.0000, Constellation.getConstellationByAbb("Per")));
        limits.add(new ConstellationLimit(6.5000, 6.8000, 50.0000, Constellation.getConstellationByAbb("Lyn")));
        limits.add(new ConstellationLimit(23.3333, 24.0000, 50.0000, Constellation.getConstellationByAbb("Cas")));
        limits.add(new ConstellationLimit(13.5000, 14.0333, 48.5000, Constellation.getConstellationByAbb("UMa")));
        limits.add(new ConstellationLimit(0.0000, 1.1167, 48.0000, Constellation.getConstellationByAbb("Cas")));
        limits.add(new ConstellationLimit(23.5833, 24.0000, 48.0000, Constellation.getConstellationByAbb("Cas")));
        limits.add(new ConstellationLimit(18.1750, 18.2333, 47.5000, Constellation.getConstellationByAbb("Her")));
        limits.add(new ConstellationLimit(18.2333, 19.0833, 47.5000, Constellation.getConstellationByAbb("Dra")));
        limits.add(new ConstellationLimit(19.0833, 19.1667, 47.5000, Constellation.getConstellationByAbb("Cyg")));
        limits.add(new ConstellationLimit(1.6667, 2.0417, 47.0000, Constellation.getConstellationByAbb("Per")));
        limits.add(new ConstellationLimit(8.4167, 9.1667, 47.0000, Constellation.getConstellationByAbb("UMa")));
        limits.add(new ConstellationLimit(0.1667, 0.8667, 46.0000, Constellation.getConstellationByAbb("Cas")));
        limits.add(new ConstellationLimit(12.0000, 12.0833, 45.0000, Constellation.getConstellationByAbb("UMa")));
        limits.add(new ConstellationLimit(6.8000, 7.3667, 44.5000, Constellation.getConstellationByAbb("Lyn")));
        limits.add(new ConstellationLimit(21.9083, 21.9667, 44.0000, Constellation.getConstellationByAbb("Cyg")));
        limits.add(new ConstellationLimit(21.8750, 21.9083, 43.7500, Constellation.getConstellationByAbb("Cyg")));
        limits.add(new ConstellationLimit(19.1667, 19.4000, 43.5000, Constellation.getConstellationByAbb("Cyg")));
        limits.add(new ConstellationLimit(9.1667, 10.1667, 42.0000, Constellation.getConstellationByAbb("UMa")));
        limits.add(new ConstellationLimit(10.1667, 10.7833, 40.0000, Constellation.getConstellationByAbb("UMa")));
        limits.add(new ConstellationLimit(15.4333, 15.7500, 40.0000, Constellation.getConstellationByAbb("Boo")));
        limits.add(new ConstellationLimit(15.7500, 16.3333, 40.0000, Constellation.getConstellationByAbb("Her")));
        limits.add(new ConstellationLimit(9.2500, 9.5833, 39.7500, Constellation.getConstellationByAbb("Lyn")));
        limits.add(new ConstellationLimit(0.0000, 2.5167, 36.7500, Constellation.getConstellationByAbb("And")));
        limits.add(new ConstellationLimit(2.5167, 2.5667, 36.7500, Constellation.getConstellationByAbb("Per")));
        limits.add(new ConstellationLimit(19.3583, 19.4000, 36.5000, Constellation.getConstellationByAbb("Lyr")));
        limits.add(new ConstellationLimit(4.5000, 4.6917, 36.0000, Constellation.getConstellationByAbb("Per")));
        limits.add(new ConstellationLimit(21.7333, 21.8750, 36.0000, Constellation.getConstellationByAbb("Cyg")));
        limits.add(new ConstellationLimit(21.8750, 22.0000, 36.0000, Constellation.getConstellationByAbb("Lac")));
        limits.add(new ConstellationLimit(6.5333, 7.3667, 35.5000, Constellation.getConstellationByAbb("Aur")));
        limits.add(new ConstellationLimit(7.3667, 7.7500, 35.5000, Constellation.getConstellationByAbb("Lyn")));
        limits.add(new ConstellationLimit(0.0000, 2.0000, 35.0000, Constellation.getConstellationByAbb("And")));
        limits.add(new ConstellationLimit(22.0000, 22.8167, 35.0000, Constellation.getConstellationByAbb("Lac")));
        limits.add(new ConstellationLimit(22.8167, 22.8667, 34.5000, Constellation.getConstellationByAbb("Lac")));
        limits.add(new ConstellationLimit(22.8667, 23.5000, 34.5000, Constellation.getConstellationByAbb("And")));
        limits.add(new ConstellationLimit(2.5667, 2.7167, 34.0000, Constellation.getConstellationByAbb("Per")));
        limits.add(new ConstellationLimit(10.7833, 11.0000, 34.0000, Constellation.getConstellationByAbb("UMa")));
        limits.add(new ConstellationLimit(12.0000, 12.3333, 34.0000, Constellation.getConstellationByAbb("CVn")));
        limits.add(new ConstellationLimit(7.7500, 9.2500, 33.5000, Constellation.getConstellationByAbb("Lyn")));
        limits.add(new ConstellationLimit(9.2500, 9.8833, 33.5000, Constellation.getConstellationByAbb("LMi")));
        limits.add(new ConstellationLimit(0.7167, 1.4083, 33.0000, Constellation.getConstellationByAbb("And")));
        limits.add(new ConstellationLimit(15.1833, 15.4333, 33.0000, Constellation.getConstellationByAbb("Boo")));
        limits.add(new ConstellationLimit(23.5000, 23.7500, 32.0833, Constellation.getConstellationByAbb("And")));
        limits.add(new ConstellationLimit(12.3333, 13.2500, 32.0000, Constellation.getConstellationByAbb("CVn")));
        limits.add(new ConstellationLimit(23.7500, 24.0000, 31.3333, Constellation.getConstellationByAbb("And")));
        limits.add(new ConstellationLimit(13.9583, 14.0333, 30.7500, Constellation.getConstellationByAbb("CVn")));
        limits.add(new ConstellationLimit(2.4167, 2.7167, 30.6667, Constellation.getConstellationByAbb("Tri")));
        limits.add(new ConstellationLimit(2.7167, 4.5000, 30.6667, Constellation.getConstellationByAbb("Per")));
        limits.add(new ConstellationLimit(4.5000, 4.7500, 30.0000, Constellation.getConstellationByAbb("Aur")));
        limits.add(new ConstellationLimit(18.1750, 19.3583, 30.0000, Constellation.getConstellationByAbb("Lyr")));
        limits.add(new ConstellationLimit(11.0000, 12.0000, 29.0000, Constellation.getConstellationByAbb("UMa")));
        limits.add(new ConstellationLimit(19.6667, 20.9167, 29.0000, Constellation.getConstellationByAbb("Cyg")));
        limits.add(new ConstellationLimit(4.7500, 5.8833, 28.5000, Constellation.getConstellationByAbb("Aur")));
        limits.add(new ConstellationLimit(9.8833, 10.5000, 28.5000, Constellation.getConstellationByAbb("LMi")));
        limits.add(new ConstellationLimit(13.2500, 13.9583, 28.5000, Constellation.getConstellationByAbb("CVn")));
        limits.add(new ConstellationLimit(0.0000, 0.0667, 28.0000, Constellation.getConstellationByAbb("And")));
        limits.add(new ConstellationLimit(1.4083, 1.6667, 28.0000, Constellation.getConstellationByAbb("Tri")));
        limits.add(new ConstellationLimit(5.8833, 6.5333, 28.0000, Constellation.getConstellationByAbb("Aur")));
        limits.add(new ConstellationLimit(7.8833, 8.0000, 28.0000, Constellation.getConstellationByAbb("Gem")));
        limits.add(new ConstellationLimit(20.9167, 21.7333, 28.0000, Constellation.getConstellationByAbb("Cyg")));
        limits.add(new ConstellationLimit(19.2583, 19.6667, 27.5000, Constellation.getConstellationByAbb("Cyg")));
        limits.add(new ConstellationLimit(1.9167, 2.4167, 27.2500, Constellation.getConstellationByAbb("Tri")));
        limits.add(new ConstellationLimit(16.1667, 16.3333, 27.0000, Constellation.getConstellationByAbb("CrB")));
        limits.add(new ConstellationLimit(15.0833, 15.1833, 26.0000, Constellation.getConstellationByAbb("Boo")));
        limits.add(new ConstellationLimit(15.1833, 16.1667, 26.0000, Constellation.getConstellationByAbb("CrB")));
        limits.add(new ConstellationLimit(18.3667, 18.8667, 26.0000, Constellation.getConstellationByAbb("Lyr")));
        limits.add(new ConstellationLimit(10.7500, 11.0000, 25.5000, Constellation.getConstellationByAbb("LMi")));
        limits.add(new ConstellationLimit(18.8667, 19.2583, 25.5000, Constellation.getConstellationByAbb("Lyr")));
        limits.add(new ConstellationLimit(1.6667, 1.9167, 25.0000, Constellation.getConstellationByAbb("Tri")));
        limits.add(new ConstellationLimit(0.7167, 0.8500, 23.7500, Constellation.getConstellationByAbb("Psc")));
        limits.add(new ConstellationLimit(10.5000, 10.7500, 23.5000, Constellation.getConstellationByAbb("LMi")));
        limits.add(new ConstellationLimit(21.2500, 21.4167, 23.5000, Constellation.getConstellationByAbb("Vul")));
        limits.add(new ConstellationLimit(5.7000, 5.8833, 22.8333, Constellation.getConstellationByAbb("Tau")));
        limits.add(new ConstellationLimit(0.0667, 0.1417, 22.0000, Constellation.getConstellationByAbb("And")));
        limits.add(new ConstellationLimit(15.9167, 16.0333, 22.0000, Constellation.getConstellationByAbb("Ser")));
        limits.add(new ConstellationLimit(5.8833, 6.2167, 21.5000, Constellation.getConstellationByAbb("Gem")));
        limits.add(new ConstellationLimit(19.8333, 20.2500, 21.2500, Constellation.getConstellationByAbb("Vul")));
        limits.add(new ConstellationLimit(18.8667, 19.2500, 21.0833, Constellation.getConstellationByAbb("Vul")));
        limits.add(new ConstellationLimit(0.1417, 0.8500, 21.0000, Constellation.getConstellationByAbb("And")));
        limits.add(new ConstellationLimit(20.2500, 20.5667, 20.5000, Constellation.getConstellationByAbb("Vul")));
        limits.add(new ConstellationLimit(7.8083, 7.8833, 20.0000, Constellation.getConstellationByAbb("Gem")));
        limits.add(new ConstellationLimit(20.5667, 21.2500, 19.5000, Constellation.getConstellationByAbb("Vul")));
        limits.add(new ConstellationLimit(19.2500, 19.8333, 19.1667, Constellation.getConstellationByAbb("Vul")));
        limits.add(new ConstellationLimit(3.2833, 3.3667, 19.0000, Constellation.getConstellationByAbb("Ari")));
        limits.add(new ConstellationLimit(18.8667, 19.0000, 18.5000, Constellation.getConstellationByAbb("Sge")));
        limits.add(new ConstellationLimit(5.7000, 5.7667, 18.0000, Constellation.getConstellationByAbb("Ori")));
        limits.add(new ConstellationLimit(6.2167, 6.3083, 17.5000, Constellation.getConstellationByAbb("Gem")));
        limits.add(new ConstellationLimit(19.0000, 19.8333, 16.1667, Constellation.getConstellationByAbb("Sge")));
        limits.add(new ConstellationLimit(4.9667, 5.3333, 16.0000, Constellation.getConstellationByAbb("Tau")));
        limits.add(new ConstellationLimit(15.9167, 16.0833, 16.0000, Constellation.getConstellationByAbb("Her")));
        limits.add(new ConstellationLimit(19.8333, 20.2500, 15.7500, Constellation.getConstellationByAbb("Sge")));
        limits.add(new ConstellationLimit(4.6167, 4.9667, 15.5000, Constellation.getConstellationByAbb("Tau")));
        limits.add(new ConstellationLimit(5.3333, 5.6000, 15.5000, Constellation.getConstellationByAbb("Tau")));
        limits.add(new ConstellationLimit(12.8333, 13.5000, 15.0000, Constellation.getConstellationByAbb("Com")));
        limits.add(new ConstellationLimit(17.2500, 18.2500, 14.3333, Constellation.getConstellationByAbb("Her")));
        limits.add(new ConstellationLimit(11.8667, 12.8333, 14.0000, Constellation.getConstellationByAbb("Com")));
        limits.add(new ConstellationLimit(7.5000, 7.8083, 13.5000, Constellation.getConstellationByAbb("Gem")));
        limits.add(new ConstellationLimit(16.7500, 17.2500, 12.8333, Constellation.getConstellationByAbb("Her")));
        limits.add(new ConstellationLimit(0.0000, 0.1417, 12.5000, Constellation.getConstellationByAbb("Peg")));
        limits.add(new ConstellationLimit(5.6000, 5.7667, 12.5000, Constellation.getConstellationByAbb("Tau")));
        limits.add(new ConstellationLimit(7.0000, 7.5000, 12.5000, Constellation.getConstellationByAbb("Gem")));
        limits.add(new ConstellationLimit(21.1167, 21.3333, 12.5000, Constellation.getConstellationByAbb("Peg")));
        limits.add(new ConstellationLimit(6.3083, 6.9333, 12.0000, Constellation.getConstellationByAbb("Gem")));
        limits.add(new ConstellationLimit(18.2500, 18.8667, 12.0000, Constellation.getConstellationByAbb("Her")));
        limits.add(new ConstellationLimit(20.8750, 21.0500, 11.8333, Constellation.getConstellationByAbb("Del")));
        limits.add(new ConstellationLimit(21.0500, 21.1167, 11.8333, Constellation.getConstellationByAbb("Peg")));
        limits.add(new ConstellationLimit(11.5167, 11.8667, 11.0000, Constellation.getConstellationByAbb("Leo")));
        limits.add(new ConstellationLimit(6.2417, 6.3083, 10.0000, Constellation.getConstellationByAbb("Ori")));
        limits.add(new ConstellationLimit(6.9333, 7.0000, 10.0000, Constellation.getConstellationByAbb("Gem")));
        limits.add(new ConstellationLimit(7.8083, 7.9250, 10.0000, Constellation.getConstellationByAbb("Cnc")));
        limits.add(new ConstellationLimit(23.8333, 24.0000, 10.0000, Constellation.getConstellationByAbb("Peg")));
        limits.add(new ConstellationLimit(1.6667, 3.2833, 9.9167, Constellation.getConstellationByAbb("Ari")));
        limits.add(new ConstellationLimit(20.1417, 20.3000, 8.5000, Constellation.getConstellationByAbb("Del")));
        limits.add(new ConstellationLimit(13.5000, 15.0833, 8.0000, Constellation.getConstellationByAbb("Boo")));
        limits.add(new ConstellationLimit(22.7500, 23.8333, 7.5000, Constellation.getConstellationByAbb("Peg")));
        limits.add(new ConstellationLimit(7.9250, 9.2500, 7.0000, Constellation.getConstellationByAbb("Cnc")));
        limits.add(new ConstellationLimit(9.2500, 10.7500, 7.0000, Constellation.getConstellationByAbb("Leo")));
        limits.add(new ConstellationLimit(18.2500, 18.6622, 6.2500, Constellation.getConstellationByAbb("Oph")));
        limits.add(new ConstellationLimit(18.6622, 18.8667, 6.2500, Constellation.getConstellationByAbb("Aql")));
        limits.add(new ConstellationLimit(20.8333, 20.8750, 6.0000, Constellation.getConstellationByAbb("Del")));
        limits.add(new ConstellationLimit(7.0000, 7.0167, 5.5000, Constellation.getConstellationByAbb("CMi")));
        limits.add(new ConstellationLimit(18.2500, 18.4250, 4.5000, Constellation.getConstellationByAbb("Ser")));
        limits.add(new ConstellationLimit(16.0833, 16.7500, 4.0000, Constellation.getConstellationByAbb("Her")));
        limits.add(new ConstellationLimit(18.2500, 18.4250, 3.0000, Constellation.getConstellationByAbb("Oph")));
        limits.add(new ConstellationLimit(21.4667, 21.6667, 2.7500, Constellation.getConstellationByAbb("Peg")));
        limits.add(new ConstellationLimit(0.0000, 2.0000, 2.0000, Constellation.getConstellationByAbb("Psc")));
        limits.add(new ConstellationLimit(18.5833, 18.8667, 2.0000, Constellation.getConstellationByAbb("Ser")));
        limits.add(new ConstellationLimit(20.3000, 20.8333, 2.0000, Constellation.getConstellationByAbb("Del")));
        limits.add(new ConstellationLimit(20.8333, 21.3333, 2.0000, Constellation.getConstellationByAbb("Equ")));
        limits.add(new ConstellationLimit(21.3333, 21.4667, 2.0000, Constellation.getConstellationByAbb("Peg")));
        limits.add(new ConstellationLimit(22.0000, 22.7500, 2.0000, Constellation.getConstellationByAbb("Peg")));
        limits.add(new ConstellationLimit(21.6667, 22.0000, 1.7500, Constellation.getConstellationByAbb("Peg")));
        limits.add(new ConstellationLimit(7.0167, 7.2000, 1.5000, Constellation.getConstellationByAbb("CMi")));
        limits.add(new ConstellationLimit(3.5833, 4.6167, 0.0000, Constellation.getConstellationByAbb("Tau")));
        limits.add(new ConstellationLimit(4.6167, 4.6667, 0.0000, Constellation.getConstellationByAbb("Ori")));
        limits.add(new ConstellationLimit(7.2000, 8.0833, 0.0000, Constellation.getConstellationByAbb("CMi")));
        limits.add(new ConstellationLimit(14.6667, 15.0833, 0.0000, Constellation.getConstellationByAbb("Vir")));
        limits.add(new ConstellationLimit(17.8333, 18.2500, 0.0000, Constellation.getConstellationByAbb("Oph")));
        limits.add(new ConstellationLimit(2.6500, 3.2833, -01.7500, Constellation.getConstellationByAbb("Cet")));
        limits.add(new ConstellationLimit(3.2833, 3.5833, -01.7500, Constellation.getConstellationByAbb("Tau")));
        limits.add(new ConstellationLimit(15.0833, 16.2667, -03.2500, Constellation.getConstellationByAbb("Ser")));
        limits.add(new ConstellationLimit(4.6667, 5.0833, -04.0000, Constellation.getConstellationByAbb("Ori")));
        limits.add(new ConstellationLimit(5.8333, 6.2417, -04.0000, Constellation.getConstellationByAbb("Ori")));
        limits.add(new ConstellationLimit(17.8333, 17.9667, -04.0000, Constellation.getConstellationByAbb("Ser")));
        limits.add(new ConstellationLimit(18.2500, 18.5833, -04.0000, Constellation.getConstellationByAbb("Ser")));
        limits.add(new ConstellationLimit(18.5833, 18.8667, -04.0000, Constellation.getConstellationByAbb("Aql")));
        limits.add(new ConstellationLimit(22.7500, 23.8333, -04.0000, Constellation.getConstellationByAbb("Psc")));
        limits.add(new ConstellationLimit(10.7500, 11.5167, -06.0000, Constellation.getConstellationByAbb("Leo")));
        limits.add(new ConstellationLimit(11.5167, 11.8333, -06.0000, Constellation.getConstellationByAbb("Vir")));
        limits.add(new ConstellationLimit(0.0000, 00.3333, -07.0000, Constellation.getConstellationByAbb("Psc")));
        limits.add(new ConstellationLimit(23.8333, 24.0000, -07.0000, Constellation.getConstellationByAbb("Psc")));
        limits.add(new ConstellationLimit(14.2500, 14.6667, -08.0000, Constellation.getConstellationByAbb("Vir")));
        limits.add(new ConstellationLimit(15.9167, 16.2667, -08.0000, Constellation.getConstellationByAbb("Oph")));
        limits.add(new ConstellationLimit(20.0000, 20.5333, -09.0000, Constellation.getConstellationByAbb("Aql")));
        limits.add(new ConstellationLimit(21.3333, 21.8667, -09.0000, Constellation.getConstellationByAbb("Aqr")));
        limits.add(new ConstellationLimit(17.1667, 17.9667, -10.0000, Constellation.getConstellationByAbb("Oph")));
        limits.add(new ConstellationLimit(5.8333, 8.0833, -11.0000, Constellation.getConstellationByAbb("Mon")));
        limits.add(new ConstellationLimit(4.9167, 5.0833, -11.0000, Constellation.getConstellationByAbb("Eri")));
        limits.add(new ConstellationLimit(5.0833, 5.8333, -11.0000, Constellation.getConstellationByAbb("Ori")));
        limits.add(new ConstellationLimit(8.0833, 8.3667, -11.0000, Constellation.getConstellationByAbb("Hya")));
        limits.add(new ConstellationLimit(9.5833, 10.7500, -11.0000, Constellation.getConstellationByAbb("Sex")));
        limits.add(new ConstellationLimit(11.8333, 12.8333, -11.0000, Constellation.getConstellationByAbb("Vir")));
        limits.add(new ConstellationLimit(17.5833, 17.6667, -11.6667, Constellation.getConstellationByAbb("Oph")));
        limits.add(new ConstellationLimit(18.8667, 20.0000, -12.0333, Constellation.getConstellationByAbb("Aql")));
        limits.add(new ConstellationLimit(4.8333, 4.9167, -14.5000, Constellation.getConstellationByAbb("Eri")));
        limits.add(new ConstellationLimit(20.5333, 21.3333, -15.0000, Constellation.getConstellationByAbb("Aqr")));
        limits.add(new ConstellationLimit(17.1667, 18.2500, -16.0000, Constellation.getConstellationByAbb("Ser")));
        limits.add(new ConstellationLimit(18.2500, 18.8667, -16.0000, Constellation.getConstellationByAbb("Sct")));
        limits.add(new ConstellationLimit(8.3667, 8.5833, -17.0000, Constellation.getConstellationByAbb("Hya")));
        limits.add(new ConstellationLimit(16.2667, 16.3750, -18.2500, Constellation.getConstellationByAbb("Oph")));
        limits.add(new ConstellationLimit(8.5833, 9.0833, -19.0000, Constellation.getConstellationByAbb("Hya")));
        limits.add(new ConstellationLimit(10.7500, 10.8333, -19.0000, Constellation.getConstellationByAbb("Crt")));
        limits.add(new ConstellationLimit(16.2667, 16.3750, -19.2500, Constellation.getConstellationByAbb("Sco")));
        limits.add(new ConstellationLimit(15.6667, 15.9167, -20.0000, Constellation.getConstellationByAbb("Lib")));
        limits.add(new ConstellationLimit(12.5833, 12.8333, -22.0000, Constellation.getConstellationByAbb("Crv")));
        limits.add(new ConstellationLimit(12.8333, 14.2500, -22.0000, Constellation.getConstellationByAbb("Vir")));
        limits.add(new ConstellationLimit(9.0833, 9.7500, -24.0000, Constellation.getConstellationByAbb("Hya")));
        limits.add(new ConstellationLimit(1.6667, 2.6500, -24.3833, Constellation.getConstellationByAbb("Cet")));
        limits.add(new ConstellationLimit(2.6500, 3.7500, -24.3833, Constellation.getConstellationByAbb("Eri")));
        limits.add(new ConstellationLimit(10.8333, 11.8333, -24.5000, Constellation.getConstellationByAbb("Crt")));
        limits.add(new ConstellationLimit(11.8333, 12.5833, -24.5000, Constellation.getConstellationByAbb("Crv")));
        limits.add(new ConstellationLimit(14.2500, 14.9167, -24.5000, Constellation.getConstellationByAbb("Lib")));
        limits.add(new ConstellationLimit(16.2667, 16.7500, -24.5833, Constellation.getConstellationByAbb("Oph")));
        limits.add(new ConstellationLimit(0.0000, 1.6667, -25.5000, Constellation.getConstellationByAbb("Cet")));
        limits.add(new ConstellationLimit(21.3333, 21.8667, -25.5000, Constellation.getConstellationByAbb("Cap")));
        limits.add(new ConstellationLimit(21.8667, 23.8333, -25.5000, Constellation.getConstellationByAbb("Aqr")));
        limits.add(new ConstellationLimit(23.8333, 24.0000, -25.5000, Constellation.getConstellationByAbb("Cet")));
        limits.add(new ConstellationLimit(9.7500, 10.2500, -26.5000, Constellation.getConstellationByAbb("Hya")));
        limits.add(new ConstellationLimit(4.7000, 4.8333, -27.2500, Constellation.getConstellationByAbb("Eri")));
        limits.add(new ConstellationLimit(4.8333, 6.1167, -27.2500, Constellation.getConstellationByAbb("Lep")));
        limits.add(new ConstellationLimit(20.0000, 21.3333, -28.0000, Constellation.getConstellationByAbb("Cap")));
        limits.add(new ConstellationLimit(10.2500, 10.5833, -29.1667, Constellation.getConstellationByAbb("Hya")));
        limits.add(new ConstellationLimit(12.5833, 14.9167, -29.5000, Constellation.getConstellationByAbb("Hya")));
        limits.add(new ConstellationLimit(14.9167, 15.6667, -29.5000, Constellation.getConstellationByAbb("Lib")));
        limits.add(new ConstellationLimit(15.6667, 16.0000, -29.5000, Constellation.getConstellationByAbb("Sco")));
        limits.add(new ConstellationLimit(4.5833, 4.7000, -30.0000, Constellation.getConstellationByAbb("Eri")));
        limits.add(new ConstellationLimit(16.7500, 17.6000, -30.0000, Constellation.getConstellationByAbb("Oph")));
        limits.add(new ConstellationLimit(17.6000, 17.8333, -30.0000, Constellation.getConstellationByAbb("Sgr")));
        limits.add(new ConstellationLimit(10.5833, 10.8333, -31.1667, Constellation.getConstellationByAbb("Hya")));
        limits.add(new ConstellationLimit(6.1167, 7.3667, -33.0000, Constellation.getConstellationByAbb("CMa")));
        limits.add(new ConstellationLimit(12.2500, 12.5833, -33.0000, Constellation.getConstellationByAbb("Hya")));
        limits.add(new ConstellationLimit(10.8333, 12.2500, -35.0000, Constellation.getConstellationByAbb("Hya")));
        limits.add(new ConstellationLimit(3.5000, 3.7500, -36.0000, Constellation.getConstellationByAbb("For")));
        limits.add(new ConstellationLimit(8.3667, 9.3667, -36.7500, Constellation.getConstellationByAbb("Pyx")));
        limits.add(new ConstellationLimit(4.2667, 4.5833, -37.0000, Constellation.getConstellationByAbb("Eri")));
        limits.add(new ConstellationLimit(17.8333, 19.1667, -37.0000, Constellation.getConstellationByAbb("Sgr")));
        limits.add(new ConstellationLimit(21.3333, 23.0000, -37.0000, Constellation.getConstellationByAbb("PsA")));
        limits.add(new ConstellationLimit(23.0000, 23.3333, -37.0000, Constellation.getConstellationByAbb("Scl")));
        limits.add(new ConstellationLimit(3.0000, 3.5000, -39.5833, Constellation.getConstellationByAbb("For")));
        limits.add(new ConstellationLimit(9.3667, 11.0000, -39.7500, Constellation.getConstellationByAbb("Ant")));
        limits.add(new ConstellationLimit(0.0000, 1.6667, -40.0000, Constellation.getConstellationByAbb("Scl")));
        limits.add(new ConstellationLimit(1.6667, 3.0000, -40.0000, Constellation.getConstellationByAbb("For")));
        limits.add(new ConstellationLimit(3.8667, 4.2667, -40.0000, Constellation.getConstellationByAbb("Eri")));
        limits.add(new ConstellationLimit(23.3333, 24.0000, -40.0000, Constellation.getConstellationByAbb("Scl")));
        limits.add(new ConstellationLimit(14.1667, 14.9167, -42.0000, Constellation.getConstellationByAbb("Cen")));
        limits.add(new ConstellationLimit(15.6667, 16.0000, -42.0000, Constellation.getConstellationByAbb("Lup")));
        limits.add(new ConstellationLimit(16.0000, 16.4208, -42.0000, Constellation.getConstellationByAbb("Sco")));
        limits.add(new ConstellationLimit(4.8333, 5.0000, -43.0000, Constellation.getConstellationByAbb("Cae")));
        limits.add(new ConstellationLimit(5.0000, 6.5833, -43.0000, Constellation.getConstellationByAbb("Col")));
        limits.add(new ConstellationLimit(8.0000, 8.3667, -43.0000, Constellation.getConstellationByAbb("Pup")));
        limits.add(new ConstellationLimit(3.4167, 3.8667, -44.0000, Constellation.getConstellationByAbb("Eri")));
        limits.add(new ConstellationLimit(16.4208, 17.8333, -45.5000, Constellation.getConstellationByAbb("Sco")));
        limits.add(new ConstellationLimit(17.8333, 19.1667, -45.5000, Constellation.getConstellationByAbb("CrA")));
        limits.add(new ConstellationLimit(19.1667, 20.3333, -45.5000, Constellation.getConstellationByAbb("Sgr")));
        limits.add(new ConstellationLimit(20.3333, 21.3333, -45.5000, Constellation.getConstellationByAbb("Mic")));
        limits.add(new ConstellationLimit(3.0000, 3.4167, -46.0000, Constellation.getConstellationByAbb("Eri")));
        limits.add(new ConstellationLimit(4.5000, 4.8333, -46.5000, Constellation.getConstellationByAbb("Cae")));
        limits.add(new ConstellationLimit(15.3333, 15.6667, -48.0000, Constellation.getConstellationByAbb("Lup")));
        limits.add(new ConstellationLimit(0.0000, 2.3333, -48.1667, Constellation.getConstellationByAbb("Phe")));
        limits.add(new ConstellationLimit(2.6667, 3.0000, -49.0000, Constellation.getConstellationByAbb("Eri")));
        limits.add(new ConstellationLimit(4.0833, 4.2667, -49.0000, Constellation.getConstellationByAbb("Hor")));
        limits.add(new ConstellationLimit(4.2667, 4.5000, -49.0000, Constellation.getConstellationByAbb("Cae")));
        limits.add(new ConstellationLimit(21.3333, 22.0000, -50.0000, Constellation.getConstellationByAbb("Gru")));
        limits.add(new ConstellationLimit(6.0000, 8.0000, -50.7500, Constellation.getConstellationByAbb("Pup")));
        limits.add(new ConstellationLimit(8.0000, 8.1667, -50.7500, Constellation.getConstellationByAbb("Vel")));
        limits.add(new ConstellationLimit(2.4167, 2.6667, -51.0000, Constellation.getConstellationByAbb("Eri")));
        limits.add(new ConstellationLimit(3.8333, 4.0833, -51.0000, Constellation.getConstellationByAbb("Hor")));
        limits.add(new ConstellationLimit(0.0000, 1.8333, -51.5000, Constellation.getConstellationByAbb("Phe")));
        limits.add(new ConstellationLimit(6.0000, 6.1667, -52.5000, Constellation.getConstellationByAbb("Car")));
        limits.add(new ConstellationLimit(8.1667, 8.4500, -53.0000, Constellation.getConstellationByAbb("Vel")));
        limits.add(new ConstellationLimit(3.5000, 3.8333, -53.1667, Constellation.getConstellationByAbb("Hor")));
        limits.add(new ConstellationLimit(3.8333, 4.0000, -53.1667, Constellation.getConstellationByAbb("Dor")));
        limits.add(new ConstellationLimit(0.0000, 1.5833, -53.5000, Constellation.getConstellationByAbb("Phe")));
        limits.add(new ConstellationLimit(2.1667, 2.4167, -54.0000, Constellation.getConstellationByAbb("Eri")));
        limits.add(new ConstellationLimit(4.5000, 5.0000, -54.0000, Constellation.getConstellationByAbb("Pic")));
        limits.add(new ConstellationLimit(15.0500, 15.3333, -54.0000, Constellation.getConstellationByAbb("Lup")));
        limits.add(new ConstellationLimit(8.4500, 8.8333, -54.5000, Constellation.getConstellationByAbb("Vel")));
        limits.add(new ConstellationLimit(6.1667, 6.5000, -55.0000, Constellation.getConstellationByAbb("Car")));
        limits.add(new ConstellationLimit(11.8333, 12.8333, -55.0000, Constellation.getConstellationByAbb("Cen")));
        limits.add(new ConstellationLimit(14.1667, 15.0500, -55.0000, Constellation.getConstellationByAbb("Lup")));
        limits.add(new ConstellationLimit(15.0500, 15.3333, -55.0000, Constellation.getConstellationByAbb("Nor")));
        limits.add(new ConstellationLimit(4.0000, 4.3333, -56.5000, Constellation.getConstellationByAbb("Dor")));
        limits.add(new ConstellationLimit(8.8333, 11.0000, -56.5000, Constellation.getConstellationByAbb("Vel")));
        limits.add(new ConstellationLimit(11.0000, 11.2500, -56.5000, Constellation.getConstellationByAbb("Cen")));
        limits.add(new ConstellationLimit(17.5000, 18.0000, -57.0000, Constellation.getConstellationByAbb("Ara")));
        limits.add(new ConstellationLimit(18.0000, 20.3333, -57.0000, Constellation.getConstellationByAbb("Tel")));
        limits.add(new ConstellationLimit(22.0000, 23.3333, -57.0000, Constellation.getConstellationByAbb("Gru")));
        limits.add(new ConstellationLimit(3.2000, 3.5000, -57.5000, Constellation.getConstellationByAbb("Hor")));
        limits.add(new ConstellationLimit(5.0000, 5.5000, -57.5000, Constellation.getConstellationByAbb("Pic")));
        limits.add(new ConstellationLimit(6.5000, 6.8333, -58.0000, Constellation.getConstellationByAbb("Car")));
        limits.add(new ConstellationLimit(0.0000, 1.3333, -58.5000, Constellation.getConstellationByAbb("Phe")));
        limits.add(new ConstellationLimit(1.3333, 2.1667, -58.5000, Constellation.getConstellationByAbb("Eri")));
        limits.add(new ConstellationLimit(23.3333, 24.0000, -58.5000, Constellation.getConstellationByAbb("Phe")));
        limits.add(new ConstellationLimit(4.3333, 4.5833, -59.0000, Constellation.getConstellationByAbb("Dor")));
        limits.add(new ConstellationLimit(15.3333, 16.4208, -60.0000, Constellation.getConstellationByAbb("Nor")));
        limits.add(new ConstellationLimit(20.3333, 21.3333, -60.0000, Constellation.getConstellationByAbb("Ind")));
        limits.add(new ConstellationLimit(5.5000, 6.0000, -61.0000, Constellation.getConstellationByAbb("Pic")));
        limits.add(new ConstellationLimit(15.1667, 15.3333, -61.0000, Constellation.getConstellationByAbb("Cir")));
        limits.add(new ConstellationLimit(16.4208, 16.5833, -61.0000, Constellation.getConstellationByAbb("Ara")));
        limits.add(new ConstellationLimit(14.9167, 15.1667, -63.5833, Constellation.getConstellationByAbb("Cir")));
        limits.add(new ConstellationLimit(16.5833, 16.7500, -63.5833, Constellation.getConstellationByAbb("Ara")));
        limits.add(new ConstellationLimit(6.0000, 6.8333, -64.0000, Constellation.getConstellationByAbb("Pic")));
        limits.add(new ConstellationLimit(6.8333, 9.0333, -64.0000, Constellation.getConstellationByAbb("Car")));
        limits.add(new ConstellationLimit(11.2500, 11.8333, -64.0000, Constellation.getConstellationByAbb("Cen")));
        limits.add(new ConstellationLimit(11.8333, 12.8333, -64.0000, Constellation.getConstellationByAbb("Cru")));
        limits.add(new ConstellationLimit(12.8333, 14.5333, -64.0000, Constellation.getConstellationByAbb("Cen")));
        limits.add(new ConstellationLimit(13.5000, 13.6667, -65.0000, Constellation.getConstellationByAbb("Cir")));
        limits.add(new ConstellationLimit(16.7500, 16.8333, -65.0000, Constellation.getConstellationByAbb("Ara")));
        limits.add(new ConstellationLimit(2.1667, 3.2000, -67.5000, Constellation.getConstellationByAbb("Hor")));
        limits.add(new ConstellationLimit(3.2000, 4.5833, -67.5000, Constellation.getConstellationByAbb("Ret")));
        limits.add(new ConstellationLimit(14.7500, 14.9167, -67.5000, Constellation.getConstellationByAbb("Cir")));
        limits.add(new ConstellationLimit(16.8333, 17.5000, -67.5000, Constellation.getConstellationByAbb("Ara")));
        limits.add(new ConstellationLimit(17.5000, 18.0000, -67.5000, Constellation.getConstellationByAbb("Pav")));
        limits.add(new ConstellationLimit(22.0000, 23.3333, -67.5000, Constellation.getConstellationByAbb("Tuc")));
        limits.add(new ConstellationLimit(4.5833, 6.5833, -70.0000, Constellation.getConstellationByAbb("Dor")));
        limits.add(new ConstellationLimit(13.6667, 14.7500, -70.0000, Constellation.getConstellationByAbb("Cir")));
        limits.add(new ConstellationLimit(14.7500, 17.0000, -70.0000, Constellation.getConstellationByAbb("TrA")));
        limits.add(new ConstellationLimit(0.0000, 1.3333, -75.0000, Constellation.getConstellationByAbb("Tuc")));
        limits.add(new ConstellationLimit(3.5000, 4.5833, -75.0000, Constellation.getConstellationByAbb("Hyi")));
        limits.add(new ConstellationLimit(6.5833, 9.0333, -75.0000, Constellation.getConstellationByAbb("Vol")));
        limits.add(new ConstellationLimit(9.0333, 11.2500, -75.0000, Constellation.getConstellationByAbb("Car")));
        limits.add(new ConstellationLimit(11.2500, 13.6667, -75.0000, Constellation.getConstellationByAbb("Mus")));
        limits.add(new ConstellationLimit(18.0000, 21.3333, -75.0000, Constellation.getConstellationByAbb("Pav")));
        limits.add(new ConstellationLimit(21.3333, 23.3333, -75.0000, Constellation.getConstellationByAbb("Ind")));
        limits.add(new ConstellationLimit(23.3333, 24.0000, -75.0000, Constellation.getConstellationByAbb("Tuc")));
        limits.add(new ConstellationLimit(0.7500, 1.3333, -76.0000, Constellation.getConstellationByAbb("Tuc")));
        limits.add(new ConstellationLimit(0.0000, 3.5000, -82.5000, Constellation.getConstellationByAbb("Hyi")));
        limits.add(new ConstellationLimit(7.6667, 13.6667, -82.5000, Constellation.getConstellationByAbb("Cha")));
        limits.add(new ConstellationLimit(13.6667, 18.0000, -82.5000, Constellation.getConstellationByAbb("Aps")));
        limits.add(new ConstellationLimit(3.5000, 7.6667, -85.0000, Constellation.getConstellationByAbb("Men")));
        limits.add(new ConstellationLimit(0.0000, 24.0000, -90.0000, Constellation.getConstellationByAbb("Oct")));

    }
}
