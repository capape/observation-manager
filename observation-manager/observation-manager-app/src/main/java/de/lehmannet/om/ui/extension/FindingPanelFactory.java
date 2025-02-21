package de.lehmannet.om.ui.extension;

import de.lehmannet.om.GenericFinding;
import de.lehmannet.om.GenericTarget;
import de.lehmannet.om.IFinding;
import de.lehmannet.om.ISession;
import de.lehmannet.om.TargetStar;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.ui.panel.GenericFindingPanel;

public class FindingPanelFactory {

    public static AbstractPanel newInstance(
            IExtensionContext extensionContext, String xsiType, IFinding finding, ISession session, boolean editable) {
        switch (xsiType) {
            case GenericTarget.XML_XSI_TYPE_VALUE:
                return new GenericFindingPanel(extensionContext.getConfiguration(), finding, session, editable);
            case GenericFinding.XML_XSI_TYPE_VALUE:
                return new GenericFindingPanel(extensionContext.getConfiguration(), finding, session, editable);
            case TargetStar.XML_XSI_TYPE_VALUE:
                return new GenericFindingPanel(extensionContext.getConfiguration(), finding, session, editable);
            default:
                throw new IllegalArgumentException("Invalid xsi:type");
        }
    }
}
