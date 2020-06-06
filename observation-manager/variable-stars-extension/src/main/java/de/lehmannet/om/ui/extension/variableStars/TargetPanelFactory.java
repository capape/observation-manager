package de.lehmannet.om.ui.extension.variableStars;

import de.lehmannet.om.ITarget;
import de.lehmannet.om.ui.extension.IExtensionContext;
import de.lehmannet.om.ui.extension.variableStars.panel.VariableStarTargetPanel;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.extension.variableStars.TargetVariableStar;

public class TargetPanelFactory {

    public static AbstractPanel newInstance(IExtensionContext extensionContext, String xsiType, ITarget target,
            boolean editable) {

        switch (xsiType) {

        case TargetVariableStar.XML_XSI_TYPE_VALUE:
            return new VariableStarTargetPanel(extensionContext.getConfiguration(), extensionContext.getModel(), target,
                    editable);
        default:
            throw new IllegalArgumentException("Invalid xsi:type");

        }
    }

}
