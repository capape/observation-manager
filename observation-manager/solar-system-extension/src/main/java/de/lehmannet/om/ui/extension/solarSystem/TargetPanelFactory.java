package de.lehmannet.om.ui.extension.solarSystem;

import de.lehmannet.om.IObservation;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.ui.extension.IExtensionContext;
import de.lehmannet.om.ui.extension.solarSystem.panel.SolarSystemTargetCometPanel;
import de.lehmannet.om.ui.extension.solarSystem.panel.SolarSystemTargetMinorPlanetPanel;
import de.lehmannet.om.ui.extension.solarSystem.panel.SolarSystemTargetMoonPanel;
import de.lehmannet.om.ui.extension.solarSystem.panel.SolarSystemTargetPlanetPanel;
import de.lehmannet.om.ui.extension.solarSystem.panel.SolarSystemTargetSunPanel;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.extension.solarSystem.SolarSystemTargetComet;
import de.lehmannet.om.extension.solarSystem.SolarSystemTargetMinorPlanet;
import de.lehmannet.om.extension.solarSystem.SolarSystemTargetMoon;
import de.lehmannet.om.extension.solarSystem.SolarSystemTargetPlanet;
import de.lehmannet.om.extension.solarSystem.SolarSystemTargetSun;

public class TargetPanelFactory {

    public static AbstractPanel newInstance(IExtensionContext context, String xsiType, ITarget target,
            IObservation observation, boolean editable) {

        switch (xsiType) {

            case SolarSystemTargetComet.XML_XSI_TYPE_VALUE:
                return new SolarSystemTargetCometPanel(context.getConfiguration(), context.getModel(), target,
                        editable);
            case SolarSystemTargetMinorPlanet.XML_XSI_TYPE_VALUE:
                return new SolarSystemTargetMinorPlanetPanel(context.getConfiguration(), context.getModel(), target,
                        editable);
            case SolarSystemTargetMoon.XML_XSI_TYPE_VALUE:
                return new SolarSystemTargetMoonPanel(context.getConfiguration(), context.getModel(), target,
                        observation, editable);
            case SolarSystemTargetSun.XML_XSI_TYPE_VALUE:
                return new SolarSystemTargetSunPanel(context.getConfiguration(), context.getModel(), target,
                        observation, editable);
            case SolarSystemTargetPlanet.XML_XSI_TYPE_VALUE:
                return new SolarSystemTargetPlanetPanel(context.getConfiguration(), context.getModel(), target,
                        observation, editable);
            default:
                throw new IllegalArgumentException("Invalid xsiType");
        }
    }

}
