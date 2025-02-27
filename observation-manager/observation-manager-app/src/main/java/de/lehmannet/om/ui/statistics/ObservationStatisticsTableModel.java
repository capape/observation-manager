/*
 * ====================================================================
 * /statistics/ObservationStatisticsTableModel.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.statistics;

import de.lehmannet.om.IObservation;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.ui.navigation.tableModel.AbstractSchemaTableModel;
import de.lehmannet.om.ui.util.LocaleToolsFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class ObservationStatisticsTableModel extends AbstractSchemaTableModel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final String MODEL_ID = "Statistics";

    private final ResourceBundle bundle =
            LocaleToolsFactory.appInstance().getBundle("ObservationManager", Locale.getDefault());

    private CatalogTargets catalogTargets = null;
    private List<TargetObservation> rowIndexMapping = null;

    public ObservationStatisticsTableModel(CatalogTargets catalogTargets) {

        this.catalogTargets = catalogTargets;

        // Build row index map
        // Maps the table row against the array index of the catalogTarget array
        this.rowIndexMapping = new ArrayList<>();
        TargetObservations[] to = this.catalogTargets.getTargetObservations();
        int rowNumber = 0;
        int obsNumber = 0;
        for (int i = 0; i < to.length; i++) {
            if (to[i].getObservations() == null) {
                this.rowIndexMapping.add(rowNumber++, new TargetObservation(i, -1));
            } else {
                obsNumber = to[i].getObservations().size();
                for (int x = 0; x < obsNumber; x++) {
                    this.rowIndexMapping.add(rowNumber++, new TargetObservation(i, x));
                }
            }
        }
    }

    @Override
    public int getColumnCount() {

        return 2;
    }

    @Override
    public String getID() {

        return ObservationStatisticsTableModel.MODEL_ID;
    }

    @Override
    public int getRowCount() {

        return this.rowIndexMapping.size();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {

        Class<?> c = null;

        switch (columnIndex) {
            case 0: {
                c = ITarget.class;
                break;
            }
            case 1: {
                c = IObservation.class;
                break;
            }
        }

        return c;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        Object value = null;

        if (this.catalogTargets.getTargetObservations() == null) {
            return null;
        }

        TargetObservation to = (TargetObservation) this.rowIndexMapping.get(rowIndex);

        switch (columnIndex) {
            case 0: {
                value = this.catalogTargets.getTargetObservations()[to.targetIndex].getTarget();

                // If row above has the same target then this one, return null
                if (rowIndex > 0) {
                    TargetObservation toAbove = (TargetObservation) this.rowIndexMapping.get(rowIndex - 1);
                    if ((toAbove != null) && (to.targetIndex == toAbove.targetIndex)) {
                        value = null;
                    }
                }

                break;
            }
            case 1: {
                List<IObservation> l = this.catalogTargets.getTargetObservations()[to.targetIndex].getObservations();
                if (l != null) {
                    value = l.get(to.observtionIndex);
                }
                break;
            }
        }

        return value;
    }

    @Override
    public String getColumnName(int column) {

        String name = "";

        switch (column) {
            case 0: {
                name = this.bundle.getString("table.header.catalogStatistics.target");
                break;
            }
            case 1: {
                name = this.bundle.getString("table.header.catalogStatistics.observation");
                break;
            }
        }

        return name;
    }

    @Override
    public int getColumnSize(int columnIndex) {

        switch (columnIndex) {
            case 0: {
                return 95;
            }
            case 1: {
                return 275;
            }
        }

        return super.getColumnSize(columnIndex);
    }

    TargetObservations[] getTargetObservations() {

        return catalogTargets.getTargetObservations();
    }

    String getCatalogName() {

        return catalogTargets.getCatalog().getName();
    }
}

class TargetObservation {

    public int targetIndex = 0;
    public int observtionIndex = 0;

    public TargetObservation(int targetIndex, int observationIndex) {

        this.targetIndex = targetIndex;
        this.observtionIndex = observationIndex;
    }
}
