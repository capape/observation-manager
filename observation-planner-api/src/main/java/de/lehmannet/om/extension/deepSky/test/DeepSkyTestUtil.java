/* ====================================================================
 * extension/deepSky/test/DeepSkyTestUtil
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.extension.deepSky.test;

import java.util.GregorianCalendar;

import de.lehmannet.om.Angle;
import de.lehmannet.om.EquPosition;
import de.lehmannet.om.Eyepiece;
import de.lehmannet.om.Filter;
import de.lehmannet.om.IEyepiece;
import de.lehmannet.om.IFilter;
import de.lehmannet.om.IFinding;
import de.lehmannet.om.IImager;
import de.lehmannet.om.ILens;
import de.lehmannet.om.IObservation;
import de.lehmannet.om.IObserver;
import de.lehmannet.om.IScope;
import de.lehmannet.om.ISession;
import de.lehmannet.om.ISite;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.Lens;
import de.lehmannet.om.Observation;
import de.lehmannet.om.Observer;
import de.lehmannet.om.Scope;
import de.lehmannet.om.Session;
import de.lehmannet.om.Site;
import de.lehmannet.om.SurfaceBrightness;
import de.lehmannet.om.extension.deepSky.DeepSkyFinding;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetDS;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetGC;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetOC;
import de.lehmannet.om.extension.imaging.CCDImager;

/**
 * Simple Utility class for testing
 */
public class DeepSkyTestUtil {

    private ITarget target1 = null;
    private ITarget target2 = null;
    private ITarget target3 = null;
    private IObserver observer1 = null;
    private IObserver observer2 = null;
    private ISite site1 = null;
    private ISite site2 = null;
    private IScope scope1 = null;
    private IScope scope2 = null;
    private IFilter filter1 = null;
    private IEyepiece eyepiece1 = null;
    private IEyepiece eyepiece2 = null;
    private ISession session = null;
    private IFinding finding1 = null;
    private IFinding finding2 = null;
    private IFinding finding3 = null;
    private IImager imager = null;
    private ILens lens1 = null;

    public DeepSkyTestUtil() {

        observer1 = createObserver();
        observer2 = createSecondObserver();
        target1 = createDeepSkyTarget();
        target2 = createSecondDeepSkyTarget(observer1);
        target3 = createThirdDeepSkyTarget();
        site1 = createSite();
        site2 = createSecondSite();
        scope1 = createScope();
        scope2 = createSecondScope();
        eyepiece1 = createEyepiece();
        eyepiece2 = createSecondEyepiece();
        session = createSession();
        finding1 = createFinding();
        filter1 = createFilter();
        finding2 = createSecondFinding();
        finding3 = createThirdFinding();
        imager = createFirstImager();
        lens1 = createFirstLens();

    }

    public IObservation createDeepSkyObservation() {

        IObservation observation = new Observation(new GregorianCalendar(2004, 01, 01, 22, 00),
                new GregorianCalendar(2004, 01, 01, 22, 10), 5.0f,
                new SurfaceBrightness(19.5f, SurfaceBrightness.MAGS_SQR_ARC_SEC), 4, 70.3f, target1, observer1, site1,
                scope1, "Narrow Band Filter", eyepiece1, filter1, imager, lens1, session, finding1);

        return observation;

    }

    public IObservation createDeepSkyObservation2() {

        IObservation observation = new Observation(new GregorianCalendar(2004, 01, 01, 22, 15),
                new GregorianCalendar(2004, 01, 01, 22, 30), 5.0f,
                new SurfaceBrightness(14.7f, SurfaceBrightness.MAGS_SQR_ARC_SEC), 2, 150.0f, target2, observer1, site2,
                scope1, "OIII Filter", eyepiece2, filter1, imager, lens1, session, finding3);

        observation.addResult(finding2);

        return observation;

    }

    public IObservation createDeepSkyObservation3() {

        IObservation observation = new Observation(new GregorianCalendar(2004, 01, 02, 22, 00),
                new GregorianCalendar(2004, 01, 02, 22, 30), target3, observer2, finding3);

        observation.setScope(scope2);

        return observation;

    }

    public IFinding createFinding() {

        DeepSkyFinding finding = new DeepSkyFinding("Looks twisted", 3);

        finding.setMottled(new Boolean(false));
        finding.setResolved(new Boolean(true));
        finding.setStellar(new Boolean(false));
        finding.setLargeDiameter(new Angle(10, Angle.ARCSECOND));

        return finding;

    }

    public IFinding createThirdFinding() {

        DeepSkyFinding finding = new DeepSkyFinding("Wow!", 4);

        finding.setResolved(new Boolean(true));
        finding.setStellar(new Boolean(false));
        finding.setLargeDiameter(new Angle(10, Angle.ARCSECOND));

        return finding;

    }

    public IFinding createSecondFinding() {

        DeepSkyFinding finding = new DeepSkyFinding("Hey there're two!", 3);

        finding.setMottled(new Boolean(false));
        finding.setResolved(new Boolean(false));
        finding.setSmallDiameter(new Angle(7, Angle.ARCSECOND));

        return finding;

    }

    public ISession createSession() {

        ISession session = new Session(new GregorianCalendar(2004, 01, 01, 22, 00),
                new GregorianCalendar(2004, 01, 01, 23, 30), this.site1);

        session.addCoObserver(observer1);
        session.setComments("That was fun!");
        session.setEquipment("Red light, chair and Beethoven No.9");
        session.setWeather("Clear, not a single cloud");

        return session;

    }

    public IEyepiece createEyepiece() {

        IEyepiece eyepiece = new Eyepiece("Nagler", 31);

        eyepiece.setVendor("TeleVue");

        return eyepiece;

    }

    public IEyepiece createSecondEyepiece() {

        IEyepiece eyepiece = new Eyepiece("Speers Waler", 10);

        eyepiece.setApparentFOV(new Angle(80, Angle.ARCSECOND));

        return eyepiece;

    }

    public IFilter createFilter() {

        IFilter filter = new Filter("Meade Narrowband", IFilter.FILTER_TYPE_COLOR);

        filter.setColor(IFilter.FILTER_COLOR_BLUE);

        return filter;

    }

    public IScope createScope() {

        IScope scope = new Scope("Meade Starfinder 10\"", 254, 1140);

        return scope;

    }

    public IScope createSecondScope() {

        IScope scope = new Scope(50f, 10f, "Nikon 10x50 CF");

        return scope;

    }

    public IImager createFirstImager() {

        return new CCDImager("ToUCam", 800, 640);

    }

    public ISite createSite() {

        ISite site = new Site("Wehrheim", new Angle(8.567, Angle.DEGREE), new Angle(50.3, Angle.DEGREE), 2);

        return site;

    }

    public ISite createSecondSite() {

        ISite site = new Site("Dossenheim", new Angle(8.657, Angle.DEGREE), new Angle(49.45, Angle.DEGREE), 2);

        site.setElevation(300.5f);

        return site;

    }

    public IObserver createObserver() {

        IObserver observer = new Observer("John", "Doe");

        return observer;

    }

    public IObserver createSecondObserver() {

        IObserver observer = new Observer("Hans", "Mustermann");

        observer.addContact("Musterstrasse 1");
        observer.addContact("12345 Musterstadt");
        observer.addContact("mustermann@britneyspearsmail.com");

        return observer;

    }

    public ITarget createDeepSkyTarget() {

        DeepSkyTargetDS target = new DeepSkyTargetDS("A double star", "WU DC");

        target.setConstellation("Ursa Major");
        target.setSurfaceBrightness(new SurfaceBrightness(8.7f, SurfaceBrightness.MAGS_SQR_ARC_SEC));
        target.setVisibleMagnitude(10.3f);
        target.setSeparation(new Angle(51.5, Angle.DEGREE));
        target.setPositionAngle(134);

        return target;

    }

    public ITarget createSecondDeepSkyTarget(IObserver observer) {

        DeepSkyTargetOC target = new DeepSkyTargetOC("M45", observer);

        target.setPosition(new EquPosition(new Angle(18.536f, Angle.RADIANT), new Angle(33.02f, Angle.DEGREE)));

        target.setConstellation("Canis Vestaci");
        target.setBrightestStar(45.2);
        target.setAmountOfStars(120);
        target.setClusterClassification("Nide");

        return target;

    }

    public ITarget createThirdDeepSkyTarget() {

        DeepSkyTargetGC target = new DeepSkyTargetGC("M13", "Messier Catalogue");

        target.setConcentration("Dense in center");
        target.setMagnitude(45.4);

        return target;

    }

    public ILens createFirstLens() {

        ILens lens = new Lens("Powermate", 5.0f);

        lens.setVendor("TeleVue");

        return lens;

    }

}
