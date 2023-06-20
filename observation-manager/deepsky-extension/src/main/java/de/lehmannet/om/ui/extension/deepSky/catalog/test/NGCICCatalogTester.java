/*
 * ====================================================================
 * /extension/deepSky/catalog/test/HCNGCCatalogTester.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.deepSky.catalog.test;

import java.io.File;

import de.lehmannet.om.ITarget;
import de.lehmannet.om.ui.extension.deepSky.catalog.ICCatalog;
import de.lehmannet.om.ui.extension.deepSky.catalog.NGCCatalog;

class NGCICCatalogTester {

    public static void main(String[] args) {

        File ngcFile = new File(NGCICCatalogTester.class.getClassLoader().getResource(args[0]).getFile());
        File icFile = new File(NGCICCatalogTester.class.getClassLoader().getResource(args[1]).getFile());

        System.out.println("---------- NGC Catalog ----------");
        long start = System.currentTimeMillis();
        NGCCatalog c = new NGCCatalog(ngcFile);
        long end = System.currentTimeMillis();
        System.out.println("Load time: " + (end - start));

        String[] all = c.getCatalogIndex();
        ITarget t = null;

        for (String value : all) {
            t = c.getTarget(value);
            System.out.println("----");
            System.out.println(t.getClass().getName());
            System.out.println(t);
            System.out.println("----");
        }

        System.out.println("---------- IC Catalog ----------");
        start = System.currentTimeMillis();
        ICCatalog ic = new ICCatalog(icFile);
        end = System.currentTimeMillis();
        System.out.println("Load time: " + (end - start));
        all = ic.getCatalogIndex();

        for (String s : all) {
            t = ic.getTarget(s);
            System.out.println("----");
            System.out.println(t.getClass().getName());
            System.out.println(t);
            System.out.println("----");
        }

    }

}
