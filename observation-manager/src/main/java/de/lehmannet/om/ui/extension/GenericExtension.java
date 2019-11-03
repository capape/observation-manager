package de.lehmannet.om.ui.extension;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.JMenu;

import org.w3c.dom.Element;

import de.lehmannet.om.GenericFinding;
import de.lehmannet.om.GenericTarget;
import de.lehmannet.om.TargetStar;
import de.lehmannet.om.ui.catalog.ICatalog;
import de.lehmannet.om.ui.preferences.PreferencesPanel;
import de.lehmannet.om.util.SchemaElementConstants;

// Build-In extension for ObservationManager
public class GenericExtension implements IExtension {

	public static final String NAME = "Build-In extension";
	private static final float VERSION = 0.8f;
	
	private PropertyResourceBundle bundle = (PropertyResourceBundle)ResourceBundle.getBundle("genericTargetDisplayNames", Locale.getDefault());
	
	private final HashMap findingPanels = new HashMap();
	private final HashMap targetPanels = new HashMap();
	private final HashMap targetDialogs = new HashMap();
	
	public GenericExtension() {
		
		this.initFindingPanels();
		this.initTargetDialogs();
		this.initTargetPanels();
		
	}
	
	public boolean addOALExtensionElement(Element docElement) {
		
		// We don't have something to add to the schema
		return true;
		
	}
	
	public String getName() {
		
		return GenericExtension.NAME;
		
	}
	
	public float getVersion() {
		
		return GenericExtension.VERSION;
		
	}	
	
	public void reloadLanguage() {
		
		this.bundle = (PropertyResourceBundle)ResourceBundle.getBundle("genericTargetDisplayNames", Locale.getDefault());
		
	}
	
	public JMenu getMenu() {

		return null;
		
	}

	public PreferencesPanel getPreferencesPanel() {
		
		return null;
		
	}

	public ICatalog[] getCatalogs(File catalogDir) {

		return null;
		
	}

	public Set getAllSupportedXSITypes() {
		
		// Return all XSI types which are supported by this extension
		HashSet result = new HashSet();
		result.addAll(this.getSupportedFindingXSITypes());
		result.addAll(this.getSupportedTargetXSITypes());
		
		return result;
		
	}
	
	public Set getSupportedXSITypes(int schemaElementConstant) {
		
		Set result = null;
		if( SchemaElementConstants.TARGET == schemaElementConstant ) {			
			result = this.getSupportedTargetXSITypes();
		} else if( SchemaElementConstants.FINDING == schemaElementConstant ) {
			result = this.getSupportedFindingXSITypes();
		}
		
		return result;
		
	}
	
	private Set getSupportedTargetXSITypes() {
		
		HashSet result = new HashSet();
		result.add(GenericTarget.XML_XSI_TYPE_VALUE);
		result.add(TargetStar.XML_XSI_TYPE_VALUE);
		
		return result;
		
	}
	
	private Set getSupportedFindingXSITypes() {
		
		HashSet result = new HashSet();
		result.add(GenericFinding.XML_XSI_TYPE_VALUE);
		
		return result;
		
	}

	public String getPanelForXSIType(String xsiType, int schemaElementConstants) {
		
		if( SchemaElementConstants.FINDING == schemaElementConstants ) {
			return (String)this.findingPanels.get(xsiType);			
		} else if( SchemaElementConstants.TARGET == schemaElementConstants ) {
			return (String)this.targetPanels.get(xsiType);
		}
		
		return null;
		
	}

	public String getDialogForXSIType(String xsiType, int schemaElementConstants) {
		
		if( SchemaElementConstants.TARGET == schemaElementConstants ) {
			return (String)this.targetDialogs.get(xsiType);		
		}
		
		return null;
		
	}

	public boolean isCreationAllowed(String xsiType) {

		return true;
		
	}

	public String getDisplayNameForXSIType(String xsiType) {

		try {
			return this.bundle.getString(xsiType);	
		} catch( MissingResourceException mre ) {	// XSIType not found
			return null;
		}
		
	}
	
	private void initFindingPanels() {
		
		this.findingPanels.put(GenericTarget.XML_XSI_TYPE_VALUE, "de.lehmannet.om.ui.panel.GenericFindingPanel");
		this.findingPanels.put(GenericFinding.XML_XSI_TYPE_VALUE, "de.lehmannet.om.ui.panel.GenericFindingPanel");
		this.findingPanels.put(TargetStar.XML_XSI_TYPE_VALUE, "de.lehmannet.om.ui.panel.GenericFindingPanel");
		
	}
	
	private void initTargetPanels() {		

		this.targetPanels.put(TargetStar.XML_XSI_TYPE_VALUE, "de.lehmannet.om.ui.panel.TargetStarPanel");
		this.targetPanels.put(GenericTarget.XML_XSI_TYPE_VALUE, "de.lehmannet.om.ui.panel.GenericTargetPanel");
		
	}
	
	private void initTargetDialogs() {
		
		this.targetDialogs.put(GenericTarget.XML_XSI_TYPE_VALUE, "de.lehmannet.om.ui.dialog.GenericTargetDialog");
		this.targetDialogs.put(TargetStar.XML_XSI_TYPE_VALUE, "de.lehmannet.om.ui.dialog.TargetStarDialog");
		
	}	
	
	public PopupMenuExtension getPopupMenu() { 
		
		return null;
		
	}
	
	// No specific updates available. GenericExtension is part of ObservationManager itself
	public URL getUpdateInformationURL() {
		
		return null;
		
	}

}
