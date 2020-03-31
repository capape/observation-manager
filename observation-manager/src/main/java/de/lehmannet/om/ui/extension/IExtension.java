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

import javax.swing.JMenu;

import org.w3c.dom.Element;

import de.lehmannet.om.ui.catalog.ICatalog;
import de.lehmannet.om.ui.preferences.PreferencesPanel;
import de.lehmannet.om.util.SchemaElementConstants;

public interface IExtension {

    String getName();

    float getVersion();

    JMenu getMenu();

    PreferencesPanel getPreferencesPanel();

    ICatalog[] getCatalogs(File catalogDir);

    Set<Integer> getAllSupportedXSITypes();

    Set<Integer> getSupportedXSITypes(SchemaElementConstants schemaElementConstant);

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

}
