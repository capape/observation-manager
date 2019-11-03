package de.lehmannet.om.ui.preferences;

import javax.swing.JPanel;

import de.lehmannet.om.ui.util.Configuration;

public abstract class PreferencesPanel extends JPanel {
	
	protected Configuration configuration = null;
	
	public PreferencesPanel(Configuration config) {
		
		this.configuration = config;
		
	}
	
	public abstract void writeConfig();
	
	public abstract String getTabTitle();
	
}
