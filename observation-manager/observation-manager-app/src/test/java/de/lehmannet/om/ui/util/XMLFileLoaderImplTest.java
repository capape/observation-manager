package de.lehmannet.om.ui.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.ZonedDateTime;

import org.junit.Before;
import org.junit.Test;

import de.lehmannet.om.Angle;
import de.lehmannet.om.Eyepiece;
import de.lehmannet.om.Filter;
import de.lehmannet.om.GenericTarget;
import de.lehmannet.om.IEyepiece;
import de.lehmannet.om.IFilter;
import de.lehmannet.om.IObserver;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.IScope;
import de.lehmannet.om.ISession;
import de.lehmannet.om.ISite;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.Observer;
import de.lehmannet.om.Scope;
import de.lehmannet.om.Session;
import de.lehmannet.om.Site;
import de.lehmannet.om.util.DateManagerImpl;

public class XMLFileLoaderImplTest {

    private XMLFileLoader emptyNewFile;

    @Before
    public void initTest() {
        emptyNewFile = XMLFileLoaderImpl.newInstance(getTestFilePath("testfiles/empty.xml"));
    }

    private final String getTestFilePath(String fileRelativePath) {
        final ClassLoader classLoader = getClass().getClassLoader();

        final String filePath = classLoader.getResource(fileRelativePath).getPath();
        return filePath;
    }

    @Test
    public void isEmptyNewFileTest() {
        assertTrue("No file used", emptyNewFile.isEmpty());
    }

    @Test
    public void isNotEmptyNewFileAfterAddElementTest() {
        final ISchemaElement element = new Scope(150.0f, 0, "Orion XT6 Plus");
        emptyNewFile.addSchemaElement(element);
        assertFalse("Element scope added", emptyNewFile.isEmpty());
    }

    @Test
    public void clearEmptyTest() {
        emptyNewFile.clear();
        assertTrue("No file used", emptyNewFile.isEmpty());

    }

    @Test
    public void clearAfterAddElementTest() {
        final ISchemaElement element = new Scope(150.0f, 0, "Orion XT6 Plus");
        emptyNewFile.addSchemaElement(element);
        emptyNewFile.clear();
        assertTrue("No file used", emptyNewFile.isEmpty());

    }

    @Test
    public void getEmptyDocumentTest() {
        assertNull("Empty document", emptyNewFile.getDocument());
    }

    @Test
    public void getNonEmptyDocument() {
        final ISchemaElement element = new Scope(150.0f, 0, "Orion XT6 Plus");
        emptyNewFile.addSchemaElement(element);
        assertNotNull("Empty document", emptyNewFile.getDocument());

    }

    @Test
    public void addSchemaElementScope() {
        final ISchemaElement element = new Scope(150.0f, 0, "Orion XT6 Plus");
        emptyNewFile.addSchemaElement(element);

        final IScope[] scopes = emptyNewFile.getScopes();
        assertEquals("Only one scope", 1, scopes.length);
        assertEquals("Model added", "Orion XT6 Plus", scopes[0].getModel());
        assertFalse("No empty document", emptyNewFile.isEmpty());
    }

    @Test
    public void addSchemaElementObserver() {
        final ISchemaElement element = new Observer("Carl", "Sagan");
        emptyNewFile.addSchemaElement(element);

        final IObserver[] observers = emptyNewFile.getObservers();
        assertEquals("Only one observer", 1, observers.length);
        assertEquals("Observer added", "Carl", observers[0].getName());
        assertFalse("No empty document", emptyNewFile.isEmpty());

    }

    @Test
    public void addSchemaElementEyepiece() {
        final ISchemaElement element = new Eyepiece("Orion", 25.0f);
        emptyNewFile.addSchemaElement(element);

        final IEyepiece[] observers = emptyNewFile.getEyepieces();
        assertEquals("Only one eyepiece", 1, observers.length);
        assertEquals("Eyepiece added", "Orion", observers[0].getModel());
        assertFalse("No empty document", emptyNewFile.isEmpty());
    }

    /**
     * TODO:
     * 
     * @Test public void addSchemaElementImager() {
     * 
     *       emptyNewFile.addSchemaElement(element); IImager[] observers = emptyNewFile.getImagers(); assertEquals("Only
     *       one eyepiece", 1, observers.length); assertEquals("Eyepiece added", "Orion", observers[0].getModel());
     *       fail("IImager"); }
     */
    @Test
    public void addSchemaElementFilter() {
        final ISchemaElement element = new Filter("UVH", "color");
        emptyNewFile.addSchemaElement(element);

        final IFilter[] filters = emptyNewFile.getFilters();
        assertEquals("Only one filter", 1, filters.length);
        assertEquals("Filter added", "UVH", filters[0].getModel());
        assertFalse("No empty document", emptyNewFile.isEmpty());
    }

    @Test
    public void addSchemaElementSite() {
        final Angle longitude = new Angle(0, "");
        final Angle latitude = new Angle(0, "");

        final ISchemaElement element = new Site("Moon", longitude, latitude, 60);
        emptyNewFile.addSchemaElement(element);

        final ISite[] sites = emptyNewFile.getSites();
        assertEquals("Only one site", 1, sites.length);
        assertEquals("Site added", "Moon", sites[0].getName());
        assertFalse("No empty document", emptyNewFile.isEmpty());
    }

    @Test
    public void addSchemaElementSession() {

        final Angle longitude = new Angle(0, "");
        final Angle latitude = new Angle(0, "");

        final Site site = new Site("Moon", longitude, latitude, 60);

        final ISchemaElement element = new Session(new DateManagerImpl(), ZonedDateTime.now(), ZonedDateTime.now(),
                site);
        emptyNewFile.addSchemaElement(element);

        final ISession[] sessions = emptyNewFile.getSessions();
        assertEquals("Only one session", 1, sessions.length);
        assertEquals("Session added", "Moon", sessions[0].getSite().getName());
        assertFalse("No empty document", emptyNewFile.isEmpty());

    }

    @Test
    public void addSchemaElementTarget() {
        final ISchemaElement element = new GenericTarget("Mars", "planet");
        emptyNewFile.addSchemaElement(element);

        final ITarget[] targets = emptyNewFile.getTargets();
        assertEquals("Only one target", 1, targets.length);
        assertEquals("Target added", "Mars", targets[0].getName());
        assertFalse("No empty document", emptyNewFile.isEmpty());

    }

    @Test
    public void addSchemaElementLens() {
        final ISchemaElement element = new Eyepiece("Orion", 25.0f);
        emptyNewFile.addSchemaElement(element);

        final IEyepiece[] eyepieces = emptyNewFile.getEyepieces();
        assertEquals("Only one eyepiece", 1, eyepieces.length);
        assertEquals("Eyepiece added", "Orion", eyepieces[0].getModel());
        assertFalse("No empty document", emptyNewFile.isEmpty());
    }

    @Test
    public void addSchemaElementOther() {
        final ISchemaElement element = new Eyepiece("Orion", 25.0f);
        emptyNewFile.addSchemaElement(element);

        final IEyepiece[] eyepieces = emptyNewFile.getEyepieces();
        assertEquals("Only one eyepiece", 1, eyepieces.length);
        assertEquals("Eyepiece added", "Orion", eyepieces[0].getModel());
        assertFalse("No empty document", emptyNewFile.isEmpty());
    }

    @Test
    public void addSchemaElementNull() {

        emptyNewFile.addSchemaElement(null);
        assertTrue("Empty document", emptyNewFile.isEmpty());

    }

    /*
     * void addSchemaElement(ISchemaElement element);
     * 
     * void addSchemaElement(ISchemaElement element, boolean dependend);
     * 
     * boolean save(String path);
     * 
     * boolean saveAs(String oldPath, String newPath);
     * 
     * 
     * String getXMLFileForSchemaElement(ISchemaElement schemaElement);
     * 
     * String getXMLPathForSchemaElement(ISchemaElement schemaElement);
     * 
     * Document getDocumentForSchemaElement(ISchemaElement schemaElement);
     * 
     * 
     * List<ISchemaElement> removeSchemaElement(ISchemaElement element);
     * 
     * void updateSchemaElement(ISchemaElement element);
     * 
     * String[] getAllOpenedFiles();
     * 
     * IObserver[] getObservers();
     * 
     * IEyepiece[] getEyepieces();
     * 
     * IImager[] getImagers();
     * 
     * IFilter[] getFilters();
     * 
     * IObservation[] getObservations();
     * 
     * IObservation[] getObservations(ISchemaElement element);
     * 
     * IObservation[] getCoObserverObservations(IObserver observer);
     * 
     * IScope[] getScopes();
     * 
     * ISession[] getSessions();
     * 
     * ISite[] getSites();
     * 
     * ITarget[] getTargets();
     * 
     * ILens[] getLenses();
     * 
     * boolean loadObservations(String xmlPath);
     * 
     */
}
