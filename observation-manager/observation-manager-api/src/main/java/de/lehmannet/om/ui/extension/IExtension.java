/* ====================================================================
 * /extension/IExtension
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension;

import java.io.File;
import java.net.URL;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JMenu;

import org.w3c.dom.Element;

import de.lehmannet.om.IFinding;
import de.lehmannet.om.IImager;
import de.lehmannet.om.IObservation;
import de.lehmannet.om.ISession;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.SchemaOalTypeInfo;
import de.lehmannet.om.ui.catalog.ICatalog;
import de.lehmannet.om.ui.dialog.IImagerDialog;
import de.lehmannet.om.ui.dialog.ITargetDialog;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.ui.preferences.PreferencesPanel;
import de.lehmannet.om.util.SchemaElementConstants;

public interface IExtension {

    String getName();

    String getVersion();

    JMenu getMenu();

    PreferencesPanel getPreferencesPanel();

    ICatalog[] getCatalogs(File catalogDir);

    Set<String> getAllSupportedXSITypes();

    Set<String> getSupportedXSITypes(SchemaElementConstants schemaElementConstant);

    String getPanelForXSIType(String xsiType, SchemaElementConstants schemaElementConstant);

    String getDialogForXSIType(String xsiType, SchemaElementConstants schemaElementConstant);

    boolean isCreationAllowed(String xsiType);

    String getDisplayNameForXSIType(String xsiType);

    boolean addOALExtensionElement(Element docElement);

    void reloadLanguage();

    PopupMenuExtension getPopupMenu();

    /**
     * @since IExtension 0.9
     */
    URL getUpdateInformationURL();

    AbstractPanel getFindingPanelForXSIType(String xsiType, IFinding finding, ISession session, ITarget target,
            boolean editable);

    AbstractPanel getTargetPanelForXSIType(String xsiType, ITarget target, IObservation observation, boolean editable);

    ITargetDialog getTargetDialogForXSIType(String xsiType, JFrame parent, ITarget target, IObservation observation,
            boolean editable);

    IImagerDialog getImagerDialogForXSIType(String xsiType, JFrame parent, IImager imager, boolean editable);

    boolean supports(String xsiType);

    Set<SchemaOalTypeInfo> getExtensionTypes();
}
