/*
 * ====================================================================
 * /extension/deepSky/catalog/ICCatalog
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.deepSky.catalog;

import java.io.File;

public class ICCatalog extends AbstractNGCICCatalog {

    private static final String CATALOG_NAME = "Revised Index Catalogue";
    private static final String CATALOG_ABB = "IC";

    public ICCatalog(File icCatalogFile) {

        super(icCatalogFile);

    }

    @Override
    public String getAbbreviation() {

        return ICCatalog.CATALOG_ABB;

    }

    @Override
    public String getName() {

        return ICCatalog.CATALOG_NAME;

    }

}
