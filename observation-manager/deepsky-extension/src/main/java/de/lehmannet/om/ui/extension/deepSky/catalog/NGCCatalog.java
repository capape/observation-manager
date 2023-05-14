/*
 * ====================================================================
 * /extension/deepSky/catalog/NGCCatalog
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.deepSky.catalog;

import java.io.File;

public class NGCCatalog extends AbstractNGCICCatalog {

    private static final String CATALOG_NAME = "Revised New General Catalogue";
    private static final String CATALOG_ABB = "NGC";

    public NGCCatalog(File ngcCatalogFile) {

        super(ngcCatalogFile);

    }

    @Override
    public String getAbbreviation() {

        return NGCCatalog.CATALOG_ABB;

    }

    @Override
    public String getName() {

        return NGCCatalog.CATALOG_NAME;

    }

}
