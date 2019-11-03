/* ====================================================================
 * /box/LanguageBox.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */


package de.lehmannet.om.ui.box;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javax.swing.JComboBox;


public class LanguageBox extends JComboBox {

	private static final String EMPTY_ENTRY = "----";		
	
	private PropertyResourceBundle bundle = null;
	
	// Allow empty entry
	private boolean allowEmptyEntry = true;
			   
	// Use tree map for sorting
	private TreeMap map = new TreeMap();
		
	
    public LanguageBox(List acceptedLanguages, boolean acceptEmptyEntry) {
    
    	// Load language file (default locale is set by OM)
    	this.bundle = (PropertyResourceBundle)ResourceBundle.getBundle("contentLanguages", Locale.getDefault());
    	
    	this.allowEmptyEntry = acceptEmptyEntry;
    	
    	// Add empty item (as first entry)
    	if( this.allowEmptyEntry ) {
    		super.addItem(LanguageBox.EMPTY_ENTRY);	
    	}    	
    	    	
    	// Put all isoKeys and language strings in a TreeMap which will sort them
    	if( this.map.size() == 0 ) {   // Do only once (static)
	    	Enumeration e = this.bundle.getKeys();
	    	String isoKey = null;
	    	String lang = null;
	    	while( e.hasMoreElements() ) {
	    		isoKey = (String)e.nextElement();
	    		if( !acceptedLanguages.isEmpty() ) { 			 // An empty list = accept all languages
		    		if( !acceptedLanguages.contains(isoKey) ) {  // Check if language is allowed
		    			continue;
		    		}	    			
	    		}
	    		lang = this.bundle.getString(isoKey);    		
	    		this.map.put(lang, isoKey);
	    	}
    	}
    	
    	// Add all languagestrings to super class (now as they're sorted) 
    	Iterator i = map.keySet().iterator();
    	String key = null;
    	while( i.hasNext()) {
    		key = (String)i.next();
    		super.addItem(key);	
    	}
    	    	
    	// Preselect EMPTY_ENTRY or default language
    	this.setDefaultEntry();
    	
		// No typing in the box allowed
		this.setEditable(false);
		
    }
    
    public LanguageBox(boolean allowEmptyEntry) {
    	
    	this(new ArrayList(), allowEmptyEntry);
    	
    }    
    
    public LanguageBox(String isoKey, boolean allowEmptyEntry) {
    	
    	this(new ArrayList(), allowEmptyEntry);
    	this.setLanguage(isoKey);
    	
    }
    
    public LanguageBox(List acceptedLanguages, String isoKey, boolean allowEmptyEntry) {
    	
    	this(acceptedLanguages, allowEmptyEntry);
    	this.setLanguage(isoKey);
    	
    }
    
    
	// --------------
	// Public Methods ---------------------------------------------------------
	// --------------	
	
	public String getSelectedISOLanguage() {	
	
		Object si= super.getSelectedItem();
		if( LanguageBox.EMPTY_ENTRY.equals(si) ) {
			return null;
		} else {
		    return (String)map.get(si);
		}
		
	}
	
	public void setLanguage(String isoKey) {
		
		if(   (isoKey == null)
		   || ("".equals(isoKey.trim()))
		   ) {
			// Don't change current value
			return;
		}
		
		isoKey = isoKey.toLowerCase().trim();
		if( this.map.values().contains(isoKey) ) {
			Iterator i = this.map.keySet().iterator();
			String current = null;
			while( i.hasNext() ) {
				current = (String)i.next();
				if( isoKey.equals(this.map.get(current)) ) {
					super.setSelectedItem(current);
					return;
				}
			}
		}
		
		// Key not found 
		this.setDefaultEntry();
		System.out.println("ISO language key unknown: " + isoKey);
		
	}
	
	private void setDefaultEntry() {
		
		if( this.allowEmptyEntry ) {
			super.setSelectedItem(LanguageBox.EMPTY_ENTRY);	
		} else {  // Cannot set EMPTY_VALUE
			if( this.map.values().contains(Locale.getDefault().getLanguage()) ) {  // Try to set VM language
				this.setLanguage(Locale.getDefault().getLanguage());	
			} else {
				// We cannot set the EMPTY_ENTRY, nor the VM language, as it's not accepted
				// Set en as very last fallback, as this will always work
				this.setLanguage(Locale.ENGLISH.getLanguage());  
			}
			
		}		
		
	}
	
}
