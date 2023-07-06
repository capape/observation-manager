/*
 * ====================================================================
 * /project/ProjectCatalog.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.project;

import de.lehmannet.om.ICloneable;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.ui.catalog.IListableCatalog;
import de.lehmannet.om.ui.navigation.tableModel.AbstractSchemaTableModel;
import de.lehmannet.om.ui.panel.AbstractSearchPanel;

public class ProjectCatalog implements IListableCatalog {

    private ITarget[] targets = null;
    private String[] catalogIndex = null;
    private String name = null;

    public ProjectCatalog(String name, ITarget[] targets) {

        this.targets = targets == null ? null : ICloneable.copyToList(targets).toArray(new ITarget[targets.length]);
        this.name = name;
        this.catalogIndex = this.createCatalogIndex();

    }

    @Override
    public String[] getCatalogIndex() {

        return this.catalogIndex;

    }

    @Override
    public AbstractSchemaTableModel getTableModel() {

        return null;

    }

    @Override
    public ITarget[] getTargets() {

        return this.targets == null ? null
                : ICloneable.copyToList(this.targets).toArray(new ITarget[this.targets.length]);

    }

    @Override
    public String getAbbreviation() {

        // Not implemented
        // Currently only used by search function, which doesn't exist for
        // ProjectCatalogs
        return null;

    }

    @Override
    public String getName() {

        return this.name;

    }

    @Override
    public AbstractSearchPanel getSearchPanel() {

        // Not implemented
        // There is no search in ProjectCatalogs
        return null;

    }

    @Override
    public ITarget getTarget(String objectName) {

        if ((objectName == null) || ("".equals(objectName.trim()))) {
            return null;
        }

        // Do some formating to enhance the chances we find the entry
        objectName = objectName.trim();
        objectName = objectName.replaceAll(" ", "");
        objectName = objectName.toUpperCase();

        for (ITarget target : this.targets) {
            if (objectName.equals(target.getName().toUpperCase())) {
                return target;
            }
        }

        return null;

    }

    private String[] createCatalogIndex() {

        String[] result = new String[this.targets.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = "" + i;
        }

        return result;

    }

}
