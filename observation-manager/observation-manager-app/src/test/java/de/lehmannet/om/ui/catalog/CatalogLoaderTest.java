package de.lehmannet.om.ui.catalog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import de.lehmannet.om.ITarget;
import de.lehmannet.om.TestTarget;
import de.lehmannet.om.ui.extension.TestExtension;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.navigation.observation.utils.InstallDir;
import de.lehmannet.om.ui.util.IConfiguration;
import java.io.File;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CatalogLoaderTest {

    @Mock
    private ObservationManager observationManager;

    @Mock
    private IConfiguration configuration;

    @Mock
    private InstallDir installDir;

    @Mock
    private ICatalog catalog;

    @Mock
    private IListableCatalog listableCatalog;

    @TempDir
    File tempDir;

    private CatalogLoader catalogLoader;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(observationManager.getInstallDir()).thenReturn(installDir);
        when(installDir.getPathForFolder(anyString())).thenReturn(tempDir.getPath());

        when(catalog.getName()).thenReturn("TestCatalog");
        when(catalog.getAbbreviation()).thenReturn("TC");
        when(catalog.getTarget("TestTarget")).thenReturn(new TestTarget("TestTarget", "TestDatasource"));
        when(catalog.getSearchPanel()).thenReturn(null);

        when(listableCatalog.getName()).thenReturn("TestListableCatalog");
        when(listableCatalog.getAbbreviation()).thenReturn("TLC");
        when(listableCatalog.getTarget("TestTarget")).thenReturn(new TestTarget("TestTarget", "TestDatasource"));
        when(listableCatalog.getSearchPanel()).thenReturn(null);

        var extension = new TestExtension("TestExtension1", "1.0.0", new ICatalog[] {catalog, listableCatalog});
        catalogLoader = new CatalogLoader(observationManager, List.of(extension));
    }

    @Test
    void testGetCatalogNames_EmptyCatalog() {
        var emptyCatalogLoader = new CatalogLoader(observationManager, List.of());
        var names = emptyCatalogLoader.getCatalogNames();
        assertEquals(0, names.length);
    }

    @Test
    void testGetCatalogNames_WithCatalogs() {
        String[] names = catalogLoader.getCatalogNames();

        assertEquals(2, names.length);
        assertTrue(List.of(names).contains("TestCatalog"));
        assertTrue(List.of(names).contains("TestListableCatalog"));
    }

    @Test
    void testGetListableCatalogNames() {
        String[] names = catalogLoader.getListableCatalogNames();

        assertEquals(1, names.length);
        assertTrue(List.of(names).contains("TestListableCatalog"));
    }

    @Test
    void testGetTarget_ExistingCatalog() {

        ITarget result = catalogLoader.getTarget("TestCatalog", "TestTarget");

        assertSame("TestTarget", result.getName());
    }

    @Test
    void testGetTarget_NonExistingCatalog() {
        ITarget result = catalogLoader.getTarget("TestCatalog", "obj1");
        assertNull(result);
    }

    @Test
    void testGetCatalog_ByAbbreviation() {

        ICatalog result = catalogLoader.getCatalog("TC");

        assertSame("TC", result.getAbbreviation());
    }

    @Test
    void testIsFromCatalog_ExistingTarget() {

        boolean result = catalogLoader.isFromCatalog("TestTarget");

        assertTrue(result);
    }

    @Test
    void testIsFromCatalog_NonExistingTarget() {
        boolean result = catalogLoader.isFromCatalog("nonexisting");

        assertFalse(result);
    }

    @Test
    void testLoadCatalogues_VersionHandling() {

        var extensionVersion2 = new TestExtension("TestExt", "2.0.0", new ICatalog[] {catalog});
        var extensionVersion1 = new TestExtension("TestExt", "1.0.0", new ICatalog[] {catalog});

        // First load with version 2.0.0
        catalogLoader = new CatalogLoader(observationManager, List.of(extensionVersion1, extensionVersion2));
        String[] names = catalogLoader.getCatalogNames();
        assertEquals(1, names.length);
        assertEquals("TestCatalog", names[0]);
    }
}
