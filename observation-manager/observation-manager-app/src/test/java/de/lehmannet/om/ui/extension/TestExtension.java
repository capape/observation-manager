package de.lehmannet.om.ui.extension;

import de.lehmannet.om.IFinding;
import de.lehmannet.om.IObservation;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ISession;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.SchemaOalTypeInfo;
import de.lehmannet.om.ui.catalog.ICatalog;
import de.lehmannet.om.ui.dialog.IDialog;
import de.lehmannet.om.ui.dialog.ITargetDialog;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.ui.panel.IPanel;
import de.lehmannet.om.ui.preferences.PreferencesPanel;
import de.lehmannet.om.util.SchemaElementConstants;
import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.JMenu;
import org.w3c.dom.Element;

public class TestExtension implements IExtension {

    private final String name;
    private final String version;
    private final ICatalog[] catalogs;

    public TestExtension(String name, String version, ICatalog[] catalogs) {
        this.name = name;
        this.version = version;
        this.catalogs = catalogs;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public ICatalog[] getCatalogs(File catalogDir) {
        return catalogs;
    }

    @Override
    public JMenu getMenu() {
        return null; // Mock implementation
    }

    @Override
    public PreferencesPanel getPreferencesPanel() {
        return null; // Mock implementation
    }

    @Override
    public Set<String> getAllSupportedXSITypes() {
        return Set.of(); // Mock implementation
    }

    @Override
    public boolean isCreationAllowed(String xsiType) {
        return true; // Mock implementation
    }

    @Override
    public String getDisplayNameForXSIType(String xsiType) {
        return "Test Display Name"; // Mock implementation
    }

    @Override
    public void reloadLanguage() {
        // Mock implementation, no action needed
    }

    @Override
    public PopupMenuExtension getPopupMenu() {
        return null; // Mock implementation
    }

    @Override
    public Optional<URL> getUpdateInformationURL() {
        return Optional.empty(); // Mock implementation
    }

    @Override
    public Set<String> getSupportedXSITypes(SchemaElementConstants schemaElementConstant) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSupportedXSITypes'");
    }

    @Override
    public boolean addOALExtensionElement(Element docElement) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addOALExtensionElement'");
    }

    @Override
    public AbstractPanel getFindingPanelForXSIType(
            String xsiType, IFinding finding, ISession session, ITarget target, boolean editable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getFindingPanelForXSIType'");
    }

    @Override
    public AbstractPanel getTargetPanelForXSIType(
            String xsiType, ITarget target, IObservation observation, boolean editable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTargetPanelForXSIType'");
    }

    @Override
    public ITargetDialog getTargetDialogForXSIType(
            String xsiType, JFrame parent, ITarget target, IObservation observation, boolean editable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTargetDialogForXSIType'");
    }

    @Override
    public IDialog getGenericDialogForXSIType(String xsiType, JFrame parent, ISchemaElement element, boolean editable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getGenericDialogForXSIType'");
    }

    @Override
    public IPanel getGenericPanelForXSIType(String xsiType, ISchemaElement element, boolean editable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getGenericPanelForXSIType'");
    }

    @Override
    public boolean supports(String xsiType) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'supports'");
    }

    @Override
    public Set<SchemaOalTypeInfo> getExtensionTypes() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getExtensionTypes'");
    }
}
