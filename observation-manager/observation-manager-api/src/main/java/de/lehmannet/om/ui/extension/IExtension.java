/*
 * ====================================================================
 * /extension/IExtension
 * (c) by Dirk Lehmann
 * ====================================================================
 */

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

public interface IExtension {

    String getName();

    String getVersion();

    JMenu getMenu();

    PreferencesPanel getPreferencesPanel();

    ICatalog[] getCatalogs(File catalogDir);

    Set<String> getAllSupportedXSITypes();

    Set<String> getSupportedXSITypes(SchemaElementConstants schemaElementConstant);

    boolean isCreationAllowed(String xsiType);

    String getDisplayNameForXSIType(String xsiType);

    boolean addOALExtensionElement(Element docElement);

    void reloadLanguage();

    PopupMenuExtension getPopupMenu();

    Optional<URL> getUpdateInformationURL();

    AbstractPanel getFindingPanelForXSIType(
            String xsiType, IFinding finding, ISession session, ITarget target, boolean editable);

    AbstractPanel getTargetPanelForXSIType(String xsiType, ITarget target, IObservation observation, boolean editable);

    ITargetDialog getTargetDialogForXSIType(
            String xsiType, JFrame parent, ITarget target, IObservation observation, boolean editable);

    IDialog getGenericDialogForXSIType(String xsiType, JFrame parent, ISchemaElement element, boolean editable);

    IPanel getGenericPanelForXSIType(String xsiType, ISchemaElement element, boolean editable);

    boolean supports(String xsiType);

    Set<SchemaOalTypeInfo> getExtensionTypes();
}
