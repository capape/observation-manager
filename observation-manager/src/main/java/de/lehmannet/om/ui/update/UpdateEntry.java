package de.lehmannet.om.ui.update;

import java.net.URL;

public class UpdateEntry {
	
	private String name = null;
	private String oldVersion = null;
	private String newVersion = null;
	private URL downloadURL = null;
	
	public UpdateEntry(String name,
					   String oldVersion,
					   String newVersion,
					   URL downloadURL) {
		
		this.name = name;
		this.oldVersion = oldVersion;
		this.newVersion = newVersion;
		this.downloadURL = downloadURL;
		
	}

	public String getName() {
		
		return name;
		
	}

	public String getOldVersion() {
		
		return oldVersion;
		
	}

	public String getNewVersion() {
		
		return newVersion;
		
	}

	public URL getDownloadURL() {
		
		return downloadURL;
		
	}
	    		    	    		
}
