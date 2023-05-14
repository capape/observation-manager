/*
 * ====================================================================
 * /extension/deepSky/catalog/test/HCNGCCatalogTester.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.deepSky.catalog.test;

import java.io.File;

import de.lehmannet.om.ITarget;
import de.lehmannet.om.ui.extension.deepSky.catalog.HCNGCCatalog;

class HCNGCCatalogTester {

    public static void main(String[] args) {

        File file = new File(args[0]);

        long start = System.currentTimeMillis();
        HCNGCCatalog c = new HCNGCCatalog(file);
        long end = System.currentTimeMillis();
        System.out.println("Load time: " + (end - start));

        String[] all = c.getCatalogIndex();

        ITarget t = null;
        for (String s : all) {
            t = c.getTarget(s);
            System.out.println("----");
            System.out.println(t.getClass().getName());
            System.out.println(t);
            System.out.println("----");
        }

    }

}
