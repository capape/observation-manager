/* ====================================================================
 * /navigation/tableModel/TargetTableModel.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.navigation.tableModel;

import java.util.ArrayList;
import java.util.List;

import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.ui.navigation.ObservationManager;

public class TargetTableModel extends AbstractSchemaTableModel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final String MODEL_ID = "Target";

    private ObservationManager om = null;

    public TargetTableModel(ITarget[] target, ObservationManager om) {

        this.om = om;
        super.elements = target;

    }

    @Override
    public int getColumnCount() {

        return 7;

    }

    @Override
    public String getID() {

        return TargetTableModel.MODEL_ID;

    }

    @Override
    public int getRowCount() {

        if ((super.elements == null) || (super.elements.length == 0)) {
            return 3;
        }
        return super.elements.length;

    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        StringBuilder value = new StringBuilder();

        if ((super.elements == null) || (super.elements.length == 0)) {
            return value.toString();
        }

        if (rowIndex >= super.elements.length) {
            return value.toString();
        }

        ITarget target = (ITarget) super.elements[rowIndex];

        switch (columnIndex) {
        case 0: {
            value = new StringBuilder(target.getDisplayName());
            break;
        }
        case 1: {
            String[] alias = target.getAliasNames();
            if (alias != null) {
                for (int x = 0; x < alias.length; x++) {
                    value.append(alias[x]);
                    if (x < alias.length - 1) {
                        value.append("; ");
                    }
                }
            }
            break;
        }
        case 2: {
            if (target.getPosition() != null) {
                value = new StringBuilder("" + target.getPosition().getRa());
            }
            break;
        }
        case 3: {
            if (target.getPosition() != null) {
                value = new StringBuilder("" + target.getPosition().getDec());
            }
            break;
        }
        case 4: {
            value = new StringBuilder(
                    this.om.getExtensionLoader().getSchemaUILoader().getDisplayNameForType(target.getXSIType()));
            break;
        }
        case 5: {
            if (target.getConstellation() != null) {
                boolean i18N = Boolean.parseBoolean(
                        this.om.getConfiguration().getConfig(ObservationManager.CONFIG_CONSTELLATION_USEI18N, "true"));
                if (i18N) {
                    value = new StringBuilder(target.getConstellation().getDisplayName());
                } else {
                    value = new StringBuilder(target.getConstellation().getName());
                }
            }
            break;
        }
        case 6: {
            if (target.getDatasource() != null) {
                value = new StringBuilder(target.getDatasource());
            } else {
                value = new StringBuilder(target.getObserver().getDisplayName());
            }
            break;
        }
        }

        return value.toString();

    }

    @Override
    public String getColumnName(int column) {

        String name = "";

        switch (column) {
        case 0: {
            name = AbstractSchemaTableModel.bundle.getString("table.header.target.name");
            break;
        }
        case 1: {
            name = AbstractSchemaTableModel.bundle.getString("table.header.target.aliasnames");
            break;
        }
        case 2: {
            name = AbstractSchemaTableModel.bundle.getString("table.header.target.ra");
            break;
        }
        case 3: {
            name = AbstractSchemaTableModel.bundle.getString("table.header.target.dec");
            break;
        }
        case 4: {
            name = AbstractSchemaTableModel.bundle.getString("table.header.target.type");
            break;
        }
        case 5: {
            name = AbstractSchemaTableModel.bundle.getString("table.header.target.constellation");
            break;
        }
        case 6: {
            name = AbstractSchemaTableModel.bundle.getString("table.header.target.source");
            break;
        }
        }

        return name;

    }

    public void addTarget(ITarget target) {

        List<ISchemaElement> list = new ArrayList<>(java.util.Arrays.asList(super.elements));
        list.add(target);
        super.elements = (ITarget[]) list.toArray(new ITarget[] {});

    }

    public void deleteTarget(ITarget target) {

        List<ISchemaElement> list = new ArrayList<>(java.util.Arrays.asList(super.elements));
        list.remove(target);
        super.elements = (ITarget[]) list.toArray(new ITarget[] {});

    }

    public void setTargets(ITarget[] targets) {

        super.elements = targets;

    }

    public ITarget[] getAllTargets() {

        return (ITarget[]) super.elements;

    }

}
