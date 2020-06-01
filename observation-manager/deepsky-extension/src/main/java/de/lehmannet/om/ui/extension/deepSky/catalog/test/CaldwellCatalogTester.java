/* ====================================================================
 * /extension/deepSky/catalog/test/CaldwellCatalogTester.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.deepSky.catalog.test;

import java.io.File;

import de.lehmannet.om.ITarget;
import de.lehmannet.om.ui.extension.deepSky.catalog.CaldwellCatalog;

class CaldwellCatalogTester {

    public static void main(String[] args) {

        File file = new File(args[0]);
        CaldwellCatalog cc = new CaldwellCatalog(file);
        String[] all = cc.getCatalogIndex();

        ITarget t = null;
        for (String s : all) {
            t = cc.getTarget(s);
            System.out.println("----");
            System.out.println(t.getClass().getName());
            System.out.println(t);
            System.out.println("----");
        }

    }

}
