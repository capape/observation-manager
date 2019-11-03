package de.lehmannet.om.ui.panel;

import de.lehmannet.om.ITarget;
import de.lehmannet.om.ui.catalog.IListableCatalog;

public class GenericListableCatalogSearchPanel extends AbstractSearchPanel {

	IListableCatalog catalog = null;
	
	public GenericListableCatalogSearchPanel(IListableCatalog catalog) {
		
		this.catalog = catalog;
		super.createPanel();
		
	}
	
	public void search(String searchText) {
		
		if(   (searchText == null)
		   || ("".equals(searchText.trim()))
		   ) {
			return;
		}
		
		// Is first char a number?
		if(   ((byte)searchText.charAt(0) >= 48)
		   && ((byte)searchText.charAt(0) < 58)
		   ) {
			searchText = this.catalog.getAbbreviation() + searchText;
		}		
		
		// Format search string, so that it matches most catalog names
		searchText = super.formatName(searchText);
			
		// Most simple way to "search"...so try this first
		super.searchResult = this.catalog.getTarget(searchText);
		
		if( super.searchResult != null ) {		// We directly found an entry, so quit here
			return;
		}
		
		// Need to search a bit more sophisticated
		String[] targetNames = this.catalog.getCatalogIndex();
		ITarget currentTarget = null;
		String[] currentTargetAliasNames = null;
		for(int i=0; i < targetNames.length; i++) {
			currentTarget = this.catalog.getTarget(targetNames[i]);
			currentTargetAliasNames = currentTarget.getAliasNames();
			if( currentTargetAliasNames == null ) {
				continue;
			}
			for(int j=0; j < currentTargetAliasNames.length; j++) {
				if( searchText.equals(super.formatName(currentTargetAliasNames[j])) ) {  // Found string within alias names
					super.searchResult = currentTarget;
					return;
				}
			}
		}
		
	}
	
}
