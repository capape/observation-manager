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

public interface IExtension {

    String getName();

    float getVersion();

    JMenu getMenu();

    PreferencesPanel getPreferencesPanel();

    ICatalog[] getCatalogs(File catalogDir);

    Set getAllSupportedXSITypes();

    Set getSupportedXSITypes(int schemaElementConstant);

    String getPanelForXSIType(String xsiType, int schemaElementConstant);

    String getDialogForXSIType(String xsiType, int schemaElementConstant);

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
