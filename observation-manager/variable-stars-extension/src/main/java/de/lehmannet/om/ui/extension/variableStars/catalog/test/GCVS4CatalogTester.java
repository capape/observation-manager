package de.lehmannet.om.ui.extension.variableStars.catalog.test;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.lehmannet.om.ITarget;
import de.lehmannet.om.ui.extension.variableStars.catalog.GCVS4Catalog;

class GCVS4CatalogTester {

    private static final Logger LOGGER = LoggerFactory.getLogger(GCVS4CatalogTester.class);

    public static void main(String[] args) {

        GCVS4Catalog cat = new GCVS4Catalog(new File("C:\\private\\java\\application\\catalog"), null);
        ITarget target = cat.getTarget("SU Tau");

        LOGGER.debug("target: {}", target);

    }

}
