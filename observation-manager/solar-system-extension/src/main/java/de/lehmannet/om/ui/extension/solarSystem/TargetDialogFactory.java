package de.lehmannet.om.ui.extension.solarSystem;

import javax.swing.JFrame;

import de.lehmannet.om.IObservation;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.ui.dialog.ITargetDialog;
import de.lehmannet.om.ui.extension.IExtensionContext;
import de.lehmannet.om.ui.extension.solarSystem.dialog.SolarSystemTargetCometDialog;
import de.lehmannet.om.ui.extension.solarSystem.dialog.SolarSystemTargetMinorPlanetDialog;
import de.lehmannet.om.ui.extension.solarSystem.dialog.SolarSystemTargetMoonDialog;
import de.lehmannet.om.ui.extension.solarSystem.dialog.SolarSystemTargetPlanetDialog;
import de.lehmannet.om.ui.extension.solarSystem.dialog.SolarSystemTargetSunDialog;
import de.lehmannet.om.extension.solarSystem.SolarSystemTargetComet;
import de.lehmannet.om.extension.solarSystem.SolarSystemTargetMinorPlanet;
import de.lehmannet.om.extension.solarSystem.SolarSystemTargetMoon;
import de.lehmannet.om.extension.solarSystem.SolarSystemTargetPlanet;
import de.lehmannet.om.extension.solarSystem.SolarSystemTargetSun;

public class TargetDialogFactory {

    public static ITargetDialog newInstance(IExtensionContext context, String xsiType, JFrame parent, ITarget target,
            IObservation observation, boolean editable) {

        switch (xsiType) {
        case SolarSystemTargetComet.XML_XSI_TYPE_VALUE:
            return new SolarSystemTargetCometDialog(parent, context.getUserInterfaceHelper(), context.getModel(),
                    target);
        case SolarSystemTargetMinorPlanet.XML_XSI_TYPE_VALUE:
            return new SolarSystemTargetMinorPlanetDialog(parent, context.getUserInterfaceHelper(), context.getModel(),
                    target);
        case SolarSystemTargetMoon.XML_XSI_TYPE_VALUE:
            return new SolarSystemTargetMoonDialog(parent, context.getUserInterfaceHelper(), context.getModel(),
                    observation, target);
        case SolarSystemTargetSun.XML_XSI_TYPE_VALUE:
            return new SolarSystemTargetSunDialog(parent, context.getUserInterfaceHelper(), context.getModel(),
                    observation, target);
        case SolarSystemTargetPlanet.XML_XSI_TYPE_VALUE:
            return new SolarSystemTargetPlanetDialog(parent, context.getUserInterfaceHelper(), context.getModel(),
                    target, observation);
        default:
            throw new IllegalArgumentException("Invalid xsiType");

        }
    }

}
