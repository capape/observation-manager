package de.lehmannet.om.ui.extension;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.lehmannet.om.IFinding;
import de.lehmannet.om.IObservation;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ISession;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.ui.dialog.AbstractDialog;
import de.lehmannet.om.ui.dialog.ITargetDialog;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.util.SchemaElementConstants;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SchemaUILoaderTest {

    @Mock
    private ObservationManager observationManager;

    @Mock
    private IExtension extension;

    private SchemaUILoader schemaUILoader;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        schemaUILoader = new SchemaUILoader(observationManager, List.of(extension));
    }

    @Test
    void testGetFindingPanel() {
        String xsiType = "testType";
        IFinding finding = mock(IFinding.class);
        ISession session = mock(ISession.class);
        ITarget target = mock(ITarget.class);
        boolean editable = true;

        when(extension.supports(xsiType)).thenReturn(true);
        AbstractPanel panel = mock(AbstractPanel.class);
        when(extension.getFindingPanelForXSIType(xsiType, finding, session, target, editable))
                .thenReturn(panel);

        AbstractPanel result = schemaUILoader.getFindingPanel(xsiType, finding, session, target, editable);
        assertEquals(panel, result);
    }

    @Test
    void testGetTargetDialog() {
        String xsiType = "testType";
        ITarget target = mock(ITarget.class);
        IObservation observation = mock(IObservation.class);

        when(extension.supports(xsiType)).thenReturn(true);
        ITargetDialog dialog = mock(ITargetDialog.class);
        when(extension.getTargetDialogForXSIType(xsiType, observationManager, target, observation, true))
                .thenReturn(dialog);

        ITargetDialog result = schemaUILoader.getTargetDialog(xsiType, target, observation);
        assertEquals(dialog, result);
    }

    @Test
    void testGetTargetPanel() {
        String xsiType = "testType";
        ITarget target = mock(ITarget.class);
        IObservation observation = mock(IObservation.class);
        boolean editable = true;

        when(extension.supports(xsiType)).thenReturn(true);
        AbstractPanel panel = mock(AbstractPanel.class);
        when(extension.getTargetPanelForXSIType(xsiType, target, observation, editable))
                .thenReturn(panel);

        AbstractPanel result = schemaUILoader.getTargetPanel(xsiType, target, observation, editable);
        assertEquals(panel, result);
    }

    @Test
    void testGetSchemaElementPanel() {
        String xsiType = "testType";
        ISchemaElement schemaElement = mock(ISchemaElement.class);
        boolean editable = true;

        when(extension.supports(xsiType)).thenReturn(true);
        AbstractPanel panel = mock(AbstractPanel.class);
        when(extension.getGenericPanelForXSIType(xsiType, schemaElement, editable))
                .thenReturn(panel);

        AbstractPanel result = schemaUILoader.getSchemaElementPanel(xsiType, schemaElement, editable);
        assertEquals(panel, result);
    }

    @Test
    void testGetSchemaElementDialog() {
        String xsiType = "testType";
        ISchemaElement schemaElement = mock(ISchemaElement.class);
        boolean editable = true;

        when(extension.supports(xsiType)).thenReturn(true);
        AbstractDialog dialog = mock(AbstractDialog.class);
        when(extension.getGenericDialogForXSIType(xsiType, observationManager, schemaElement, true))
                .thenReturn(dialog);

        AbstractDialog result = schemaUILoader.getSchemaElementDialog(xsiType, schemaElement, editable);
        assertEquals(dialog, result);
    }

    @Test
    void testGetAllXSIDisplayNames() {
        SchemaElementConstants constants = SchemaElementConstants.FINDING;
        when(extension.getSupportedXSITypes(constants)).thenReturn(Set.of("type1"));
        when(extension.getDisplayNameForXSIType("type1")).thenReturn("DisplayName1");

        String[] result = schemaUILoader.getAllXSIDisplayNames(constants);
        assertArrayEquals(new String[] {"DisplayName1"}, result);
    }

    @Test
    void testGetAllXSIDisplayNamesForCreation() {
        SchemaElementConstants constants = SchemaElementConstants.FINDING;
        when(extension.getSupportedXSITypes(constants)).thenReturn(Set.of("type1"));
        when(extension.isCreationAllowed("type1")).thenReturn(true);
        when(extension.getDisplayNameForXSIType("type1")).thenReturn("DisplayName1");

        String[] result = schemaUILoader.getAllXSIDisplayNamesForCreation(constants);
        assertArrayEquals(new String[] {"DisplayName1"}, result);
    }

    @Test
    void testGetDisplayNameForType() {
        String type = "testType";
        when(extension.getDisplayNameForXSIType(type)).thenReturn("DisplayName");

        String result = schemaUILoader.getDisplayNameForType(type);
        assertEquals("DisplayName", result);
    }

    @Test
    void testGetTypeForDisplayName() {
        String displayName = "DisplayName";
        when(extension.getAllSupportedXSITypes()).thenReturn(Set.of("type1"));
        when(extension.getDisplayNameForXSIType("type1")).thenReturn(displayName);

        String result = schemaUILoader.getTypeForDisplayName(displayName);
        assertEquals("type1", result);
    }
}
