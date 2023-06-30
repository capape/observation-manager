/*
 * ====================================================================
 * /navigation/tableModel/ObservationTableModel.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.navigation.tableModel;

import java.time.OffsetDateTime;

import de.lehmannet.om.ICloneable;
import de.lehmannet.om.IObservation;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.Observer;
import de.lehmannet.om.Scope;
import de.lehmannet.om.Site;
import de.lehmannet.om.Target;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.util.ConfigKey;

public class ObservationTableModel extends AbstractSchemaTableModel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final String MODEL_ID = "Observation";

    private ObservationManager om = null;

    public ObservationTableModel(IObservation[] observations, ObservationManager om) {

        this.om = om;
        this.elements = ICloneable.copyToList(observations)
                .toArray(new ISchemaElement[observations == null ? 0 : observations.length]);

    }

    @Override
    public String getID() {

        return ObservationTableModel.MODEL_ID;

    }

    @Override
    public int getColumnCount() {

        return 7;

    }

    @Override
    public int getRowCount() {

        if (this.elements == null) {
            return 30;
        }
        return this.elements.length;

    }

    @Override
    public String getColumnName(int column) {

        String name;

        switch (column) {
            case 0: {
                name = AbstractSchemaTableModel.bundle.getString("table.header.observation.date");
                break;
            }
            case 1: {
                name = AbstractSchemaTableModel.bundle.getString("table.header.observation.target");
                break;
            }
            case 2: {
                name = AbstractSchemaTableModel.bundle.getString("table.header.observation.constellation");
                break;
            }
            case 3: {
                name = AbstractSchemaTableModel.bundle.getString("table.header.observation.targetType");
                break;
            }
            case 4: {
                name = AbstractSchemaTableModel.bundle.getString("table.header.observation.site");
                break;
            }
            case 5: {
                name = AbstractSchemaTableModel.bundle.getString("table.header.observation.scope");
                break;
            }
            case 6: {
                name = AbstractSchemaTableModel.bundle.getString("table.header.observation.observer");
                break;
            }
            default: {
                name = "";
                break;
            }
        }

        return name;

    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {

        Class<?> c;

        switch (columnIndex) {
            case 0: {
                c = OffsetDateTime.class;
                break;
            }
            case 1: {
                c = Target.class;
                break;
            }
            case 2:
            case 3: {
                c = String.class;
                break;
            }
            case 4: {
                c = Site.class;
                break;
            }
            case 5: {
                c = Scope.class;
                break;
            }
            case 6: {
                c = Observer.class;
                break;
            }
            default: {
                c = null;
                break;
            }
        }

        return c;

    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        Object value = null;

        if ((this.elements == null) || (rowIndex < 0) // Might happen during load of an XML file
        ) {
            return null;
        }

        IObservation observation = (IObservation) this.elements[rowIndex];
        switch (columnIndex) {
            case 0: {
                value = observation.getBegin();
                break;
            }
            case 1: {
                value = observation.getTarget();
                break;
            }
            case 2: {
                value = "";
                if (observation.getTarget().getConstellation() != null) {
                    boolean i18N = Boolean.parseBoolean(
                            this.om.getConfiguration().getConfig(ConfigKey.CONFIG_CONSTELLATION_USEI18N, "true"));
                    if (i18N) {
                        value = observation.getTarget().getConstellation().getDisplayName();
                    } else {
                        value = observation.getTarget().getConstellation().getName();
                    }
                }
                break;
            }
            case 3: {
                value = this.om.getExtensionLoader().getSchemaUILoader()
                        .getDisplayNameForType(observation.getTarget().getXSIType());
                break;
            }
            case 4: {
                value = observation.getSite();
                break;
            }
            case 5: {
                value = observation.getScope();
                break;
            }
            case 6: {
                value = observation.getObserver();
                break;
            }
            default: {
                value = null;
                break;
            }
        }

        return value;

    }

}
