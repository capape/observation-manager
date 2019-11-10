package de.lehmannet.om.ui.extension.variableStars.catalog.test;

import java.io.File;

import de.lehmannet.om.ITarget;
import de.lehmannet.om.ui.extension.variableStars.catalog.GCVS4Catalog;

public class GCVS4CatalogTester {

    public static void main(String[] args) {

        GCVS4Catalog cat = new GCVS4Catalog(new File("C:\\private\\java\\application\\catalog"), null);
        ITarget target = cat.getTarget("SU Tau");

        System.out.println(target);

    }

}
