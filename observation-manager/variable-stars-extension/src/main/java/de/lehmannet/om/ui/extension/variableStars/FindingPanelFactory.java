package de.lehmannet.om.ui.extension.variableStars;

import de.lehmannet.om.IFinding;
import de.lehmannet.om.ISession;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.ui.extension.IExtensionContext;
import de.lehmannet.om.ui.extension.variableStars.panel.VariableStarFindingPanel;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.extension.variableStars.TargetVariableStar;
import de.lehmannet.om.extension.variableStars.FindingVariableStar;
public class FindingPanelFactory {

	public static AbstractPanel newInstance(IExtensionContext extensionContext, String xsiType, IFinding finding,
			ISession session, ITarget target, boolean editable) {
                switch (xsiType) {

                    case FindingVariableStar.XML_XSI_TYPE_VALUE:
                        return new VariableStarFindingPanel(extensionContext.getConfiguration(), finding, session, target, editable);
                    case TargetVariableStar.XML_XSI_TYPE_VALUE:
                        return new VariableStarFindingPanel(extensionContext.getConfiguration(), finding, session, target, editable);
                    default:
                        throw new IllegalArgumentException("Invalid xsi:type");
        
                }

	}

}
