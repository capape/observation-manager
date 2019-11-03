/* ====================================================================
 * /extension/deepSky/catalog/test/MessierCatalogTester.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */


package de.lehmannet.om.ui.extension.deepSky.catalog.test;

import java.io.File;

import de.lehmannet.om.ITarget;
import de.lehmannet.om.ui.extension.deepSky.catalog.MessierCatalog;

public class MessierCatalogTester {

	public static void main(String[] args) {
		
		File file = new File(args[0]);
		MessierCatalog mc = new MessierCatalog(file);
		String[] all = mc.getCatalogIndex();
		
		ITarget t = null;
		for(int i=0; i < all.length; i++) {
			t = mc.getTarget(all[i]);
			System.out.println("----");
			System.out.println(t.getClass().getName());
			System.out.println(t);
			System.out.println("----");
		}
		
	}	
	
}
