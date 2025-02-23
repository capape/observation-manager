package de.lehmannet.om.ui.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import java.time.OffsetDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class XMLFileLoaderImplTest {

    private XMLFileLoader emptyNewFile;

    @BeforeEach
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
        assertTrue(emptyNewFile.isEmpty(), "No file used");
    }

    @Test
    public void isNotEmptyNewFileAfterAddElementTest() {
        final ISchemaElement element = new Scope(150.0f, 0, "Orion XT6 Plus");
        emptyNewFile.addSchemaElement(element);
        assertFalse(emptyNewFile.isEmpty(), "Element scope added");
    }

    @Test
    public void clearEmptyTest() {
        emptyNewFile.clear();
        assertTrue(emptyNewFile.isEmpty(), "No file used");
    }

    @Test
    public void clearAfterAddElementTest() {
        final ISchemaElement element = new Scope(150.0f, 0, "Orion XT6 Plus");
        emptyNewFile.addSchemaElement(element);
        emptyNewFile.clear();
        assertTrue(emptyNewFile.isEmpty(), "No file used");
    }

    @Test
    public void getEmptyDocumentTest() {
        assertNull(emptyNewFile.getDocument(), "Empty document");
    }

    @Test
    public void getNonEmptyDocument() {
        final ISchemaElement element = new Scope(150.0f, 0, "Orion XT6 Plus");
        emptyNewFile.addSchemaElement(element);
        assertNotNull(emptyNewFile.getDocument(), "Empty document");
    }

    @Test
    public void addSchemaElementScope() {
        final ISchemaElement element = new Scope(150.0f, 0, "Orion XT6 Plus");
        emptyNewFile.addSchemaElement(element);

        final IScope[] scopes = emptyNewFile.getScopes();
        assertEquals(1, scopes.length, "Only one scope");
        assertEquals("Orion XT6 Plus", scopes[0].getModel(), "Model added");
        assertFalse(emptyNewFile.isEmpty(), "No empty document");
    }

    @Test
    public void addSchemaElementObserver() {
        final ISchemaElement element = new Observer("Carl", "Sagan");
        emptyNewFile.addSchemaElement(element);

        final IObserver[] observers = emptyNewFile.getObservers();
        assertEquals(1, observers.length);
        assertEquals("Carl", observers[0].getName());
        assertFalse(emptyNewFile.isEmpty());
    }

    @Test
    public void addSchemaElementEyepiece() {
        final ISchemaElement element = new Eyepiece("Orion", 25.0f);
        emptyNewFile.addSchemaElement(element);

        final IEyepiece[] observers = emptyNewFile.getEyepieces();
        assertEquals(1, observers.length);
        assertEquals("Orion", observers[0].getModel());
        assertFalse(emptyNewFile.isEmpty());
    }

    @Test
    public void addSchemaElementFilter() {
        final ISchemaElement element = new Filter("UVH", "color");
        emptyNewFile.addSchemaElement(element);

        final IFilter[] filters = emptyNewFile.getFilters();
        assertEquals(1, filters.length);
        assertEquals("UVH", filters[0].getModel());
        assertFalse(emptyNewFile.isEmpty());
    }

    @Test
    public void addSchemaElementSite() {
        final Angle longitude = new Angle(0, "");
        final Angle latitude = new Angle(0, "");

        final ISchemaElement element = new Site("Moon", longitude, latitude, 60);
        emptyNewFile.addSchemaElement(element);

        final ISite[] sites = emptyNewFile.getSites();
        assertEquals(1, sites.length);
        assertEquals("Moon", sites[0].getName());
        assertFalse(emptyNewFile.isEmpty());
    }

    @Test
    public void addSchemaElementSession() {

        final Angle longitude = new Angle(0, "");
        final Angle latitude = new Angle(0, "");

        final Site site = new Site("Moon", longitude, latitude, 60);

        final ISchemaElement element =
                new Session(new DateManagerImpl(), OffsetDateTime.now(), OffsetDateTime.now(), site);
        emptyNewFile.addSchemaElement(element);

        final ISession[] sessions = emptyNewFile.getSessions();
        assertEquals(1, sessions.length);
        assertEquals("Moon", sessions[0].getSite().getName());
        assertFalse(emptyNewFile.isEmpty());
    }

    @Test
    public void addSchemaElementTarget() {
        final ISchemaElement element = new GenericTarget("Mars", "planet");
        emptyNewFile.addSchemaElement(element);

        final ITarget[] targets = emptyNewFile.getTargets();
        assertEquals(1, targets.length);
        assertEquals("Mars", targets[0].getName());
        assertFalse(emptyNewFile.isEmpty());
    }

    @Test
    public void addSchemaElementLens() {
        final ISchemaElement element = new Eyepiece("Orion", 25.0f);
        emptyNewFile.addSchemaElement(element);

        final IEyepiece[] eyepieces = emptyNewFile.getEyepieces();
        assertEquals(1, eyepieces.length);
        assertEquals("Orion", eyepieces[0].getModel());
        assertFalse(emptyNewFile.isEmpty());
    }

    @Test
    public void addSchemaElementOther() {
        final ISchemaElement element = new Eyepiece("Orion", 25.0f);
        emptyNewFile.addSchemaElement(element);

        final IEyepiece[] eyepieces = emptyNewFile.getEyepieces();
        assertEquals(1, eyepieces.length);
        assertEquals("Orion", eyepieces[0].getModel());
        assertFalse(emptyNewFile.isEmpty());
    }

    @Test
    public void addSchemaElementNull() {

        emptyNewFile.addSchemaElement(null);
        assertTrue(emptyNewFile.isEmpty());
    }
}
