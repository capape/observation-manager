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

	public String getName();
	
	public float getVersion();
	
	public JMenu getMenu();
	
	public PreferencesPanel getPreferencesPanel();
	
	public ICatalog[] getCatalogs(File catalogDir);
	
	public Set getAllSupportedXSITypes();
		
	public Set getSupportedXSITypes(int schemaElementConstant);
	
	public String getPanelForXSIType(String xsiType, int schemaElementConstant);
	
	public String getDialogForXSIType(String xsiType, int schemaElementConstant);
	
	public boolean isCreationAllowed(String xsiType);
	
	public String getDisplayNameForXSIType(String xsiType);
	
	public boolean addOALExtensionElement(Element docElement);
	
	public void reloadLanguage();
	
	public PopupMenuExtension getPopupMenu();
	
	/**
	 * @since IExtension 0.9
	 */
	public URL getUpdateInformationURL();
	
}
