/*
 * ====================================================================
 * /navigation/tableModel/EyepieceTableModel.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.navigation.tableModel;

import java.util.Locale;
import java.util.ResourceBundle;

import de.lehmannet.om.Angle;
import de.lehmannet.om.ICloneable;
import de.lehmannet.om.IEyepiece;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ui.util.LocaleToolsFactory;

public class EyepieceTableModel extends AbstractSchemaTableModel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final String MODEL_ID = "Eyepiece";

    private final ResourceBundle bundle = LocaleToolsFactory.appInstance().getBundle("ObservationManager",
            Locale.getDefault());

    public EyepieceTableModel(IEyepiece[] eyepieces) {

        this.elements = ICloneable.copyToList(eyepieces).toArray(new ISchemaElement[eyepieces.length]);

    }

    @Override
    public int getColumnCount() {

        return 4;

    }

    @Override
    public String getID() {

        return EyepieceTableModel.MODEL_ID;

    }

    @Override
    public int getRowCount() {

        if (this.elements == null) {
            return 5;
        }
        return this.elements.length;

    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {

        Class<?> c = null;

        switch (columnIndex) {
            case 0:
            case 2:
            case 1: {
                c = String.class;
                break;
            }
            case 3: {
                c = Angle.class;
                break;
            }
        }

        return c;

    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        Object value = null;

        if (this.elements == null) {
            return null;
        }

        IEyepiece eyepiece = (IEyepiece) this.elements[rowIndex];

        switch (columnIndex) {
            case 0: {
                value = eyepiece.getVendor();
                break;
            }
            case 1: {
                value = eyepiece.getModel();
                break;
            }
            case 2: {
                value = "" + eyepiece.getFocalLength();
                if (eyepiece.isZoomEyepiece()) {
                    value = value + "-" + eyepiece.getMaxFocalLength();
                }
                break;
            }
            case 3: {
                Angle afov = eyepiece.getApparentFOV();
                if (afov != null) {
                    afov.toDegree();
                    value = afov;
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
                name = this.bundle.getString("table.header.eyepiece.vendor");
                break;
            }
            case 1: {
                name = this.bundle.getString("table.header.eyepiece.model");
                break;
            }
            case 2: {
                name = this.bundle.getString("table.header.eyepiece.focalLength");
                break;
            }
            case 3: {
                name = this.bundle.getString("table.header.eyepiece.apparentFoV");
                break;
            }
        }

        return name;

    }

}
