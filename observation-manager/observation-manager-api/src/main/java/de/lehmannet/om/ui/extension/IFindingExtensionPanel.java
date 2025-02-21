package de.lehmannet.om.ui.extension;

import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ui.panel.IFindingPanel;

public interface IFindingExtensionPanel extends IFindingPanel {

    /**
     * @return name of panel
     */
    String getName();

    String getXSIType();

    ISchemaElement getSchemaElement();

    ISchemaElement createSchemaElement();

    ISchemaElement updateSchemaElement();
}
