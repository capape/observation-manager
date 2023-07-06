package de.lehmannet.om.ui.extension;

import de.lehmannet.om.GenericTarget;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.TargetStar;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.ui.panel.GenericTargetPanel;
import de.lehmannet.om.ui.panel.TargetStarPanel;

public class TargetPanelFactory {
    public static AbstractPanel newInstance(IExtensionContext extensionContext, String xsiType, ITarget target,
            boolean editable) {
        switch (xsiType) {
            case GenericTarget.XML_XSI_TYPE_VALUE:
                return new GenericTargetPanel(extensionContext.getConfiguration(), extensionContext.getModel(),
                        (GenericTarget) target, editable);

            case TargetStar.XML_XSI_TYPE_VALUE:
                return new TargetStarPanel(extensionContext.getConfiguration(), extensionContext.getModel(),
                        target == null ? null : (TargetStar) target, editable);
            default:
                throw new IllegalArgumentException("Invalid xsi:type");

        }
    }

}
