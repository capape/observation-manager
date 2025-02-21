package de.lehmannet.om.ui.extension.solarSystem;

import de.lehmannet.om.IFinding;
import de.lehmannet.om.ISession;
import de.lehmannet.om.extension.solarSystem.SolarSystemTargetComet;
import de.lehmannet.om.extension.solarSystem.SolarSystemTargetMinorPlanet;
import de.lehmannet.om.extension.solarSystem.SolarSystemTargetMoon;
import de.lehmannet.om.extension.solarSystem.SolarSystemTargetPlanet;
import de.lehmannet.om.extension.solarSystem.SolarSystemTargetSun;
import de.lehmannet.om.ui.extension.IExtensionContext;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.ui.panel.GenericFindingPanel;

public class FindingPanelFactory {

    public static AbstractPanel newInstance(
            IExtensionContext context, String xsiType, IFinding finding, ISession session, boolean editable) {

        switch (xsiType) {
            case SolarSystemTargetComet.XML_XSI_TYPE_VALUE:
                return new GenericFindingPanel(context.getConfiguration(), finding, session, editable);
            case SolarSystemTargetMinorPlanet.XML_XSI_TYPE_VALUE:
                return new GenericFindingPanel(context.getConfiguration(), finding, session, editable);
            case SolarSystemTargetMoon.XML_XSI_TYPE_VALUE:
                return new GenericFindingPanel(context.getConfiguration(), finding, session, editable);
            case SolarSystemTargetSun.XML_XSI_TYPE_VALUE:
                return new GenericFindingPanel(context.getConfiguration(), finding, session, editable);
            case SolarSystemTargetPlanet.XML_XSI_TYPE_VALUE:
                return new GenericFindingPanel(context.getConfiguration(), finding, session, editable);
            default:
                return new GenericFindingPanel(context.getConfiguration(), finding, session, editable);
        }
    }
}
