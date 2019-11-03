package de.lehmannet.om.ui.extension.imaging;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.JMenu;

import de.lehmannet.om.extension.imaging.CCDImager;
import de.lehmannet.om.ui.catalog.ICatalog;
import de.lehmannet.om.ui.extension.AbstractExtension;
import de.lehmannet.om.ui.extension.PopupMenuExtension;
import de.lehmannet.om.ui.preferences.PreferencesPanel;
import de.lehmannet.om.util.SchemaElementConstants;

public class ImagerExtension extends AbstractExtension {

	private static final String NAME = "Imager";
	private static final float VERSION = 0.9f;
	private static URL UPDATE_URL = null;
	static {
		try {
			ImagerExtension.UPDATE_URL = new URL("http://observation.sourceforge.net/extension/imaging/update");
		} catch( MalformedURLException m_url ) {
			// Do nothing
		}
	}	
	
	private PropertyResourceBundle bundle = (PropertyResourceBundle)ResourceBundle.getBundle("de.lehmannet.om.ui.extension.imaging.oalImagingDisplayNames", Locale.getDefault());
	
	
	public ImagerExtension() {
		
		super.OAL_EXTENSION_FILE = "./openastronomylog21/extensions/ext_Imaging.xsd";
		
		this.initPanels();
		this.initDialogs();
		
	}
	
	
	public ICatalog[] getCatalogs(File catalogDir) {
 
		return null;
		
	}

	public String getDisplayNameForXSIType(String xsiType) {

		try {
			return this.bundle.getString(xsiType);	
		} catch( MissingResourceException mre ) {	// XSIType not found
			return null;
		}
		
	}

	public JMenu getMenu() {

		return null;
		
	}
	
	public void reloadLanguage() {
		
		this.bundle = (PropertyResourceBundle)ResourceBundle.getBundle("de.lehmannet.om.ui.extension.imaging.oalImagingDisplayNames", Locale.getDefault());
		
	}	

	public String getName() {

		return ImagerExtension.NAME;
		
	}
	
	public URL getUpdateInformationURL() {
		
		return ImagerExtension.UPDATE_URL;
		
	}

	public PreferencesPanel getPreferencesPanel() {

		return null;
		
	}

	public Set getSupportedXSITypes(int schemaElementConstants) {
		
		if( SchemaElementConstants.IMAGER == schemaElementConstants ) {
			HashSet hs = new HashSet();
			hs.add(CCDImager.XML_ATTRIBUTE_CCDIMAGER);			
			
			return hs;
		}
		
		return null;
		
	}
	
	public Set getAllSupportedXSITypes() {
		
		return this.getSupportedXSITypes(SchemaElementConstants.IMAGER);
		
	}	

	public float getVersion() {

		return ImagerExtension.VERSION;
		
	}

	public boolean isCreationAllowed(String xsiType) {

		return true;
		
	}

	private void initPanels() {
		
		HashMap panels = new HashMap();
		
		panels.put(CCDImager.XML_ATTRIBUTE_CCDIMAGER, "de.lehmannet.om.ui.extension.imaging.panel.CCDImagerPanel");
		
		super.panels.put(new Integer(SchemaElementConstants.IMAGER), panels);
		
	}
	
	private void initDialogs() {
		
		HashMap dialogs = new HashMap();
		
		dialogs.put(CCDImager.XML_ATTRIBUTE_CCDIMAGER, "de.lehmannet.om.ui.extension.imaging.dialog.CCDImagerDialog");
	
		super.dialogs.put(new Integer(SchemaElementConstants.IMAGER), dialogs);
		
	}	
	
	public PopupMenuExtension getPopupMenu() { 
		
		return null;
		
	}
	
}
