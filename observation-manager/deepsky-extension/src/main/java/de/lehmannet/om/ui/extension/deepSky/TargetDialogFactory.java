package de.lehmannet.om.ui.extension.deepSky;

import javax.swing.JFrame;

import de.lehmannet.om.IObservation;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.ui.dialog.ITargetDialog;
import de.lehmannet.om.ui.extension.IExtensionContext;
import de.lehmannet.om.ui.extension.deepSky.dialog.DeepSkyTargetASDialog;
import de.lehmannet.om.ui.extension.deepSky.dialog.DeepSkyTargetCGDialog;
import de.lehmannet.om.ui.extension.deepSky.dialog.DeepSkyTargetDNDialog;
import de.lehmannet.om.ui.extension.deepSky.dialog.DeepSkyTargetDSDialog;
import de.lehmannet.om.ui.extension.deepSky.dialog.DeepSkyTargetGCDialog;
import de.lehmannet.om.ui.extension.deepSky.dialog.DeepSkyTargetGNDialog;
import de.lehmannet.om.ui.extension.deepSky.dialog.DeepSkyTargetGXDialog;
import de.lehmannet.om.ui.extension.deepSky.dialog.DeepSkyTargetMSDialog;
import de.lehmannet.om.ui.extension.deepSky.dialog.DeepSkyTargetNADialog;
import de.lehmannet.om.ui.extension.deepSky.dialog.DeepSkyTargetOCDialog;
import de.lehmannet.om.ui.extension.deepSky.dialog.DeepSkyTargetPNDialog;
import de.lehmannet.om.ui.extension.deepSky.dialog.DeepSkyTargetQSDialog;
import de.lehmannet.om.ui.extension.deepSky.dialog.DeepSkyTargetSCDialog;
import de.lehmannet.om.extension.deepSky.DeepSkyFinding;
import de.lehmannet.om.extension.deepSky.DeepSkyFindingDS;
import de.lehmannet.om.extension.deepSky.DeepSkyFindingOC;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetAS;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetCG;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetDN;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetDS;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetGC;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetGN;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetGX;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetMS;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetNA;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetOC;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetPN;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetQS;
import de.lehmannet.om.extension.deepSky.DeepSkyTargetSC;

public class TargetDialogFactory {

    public static ITargetDialog newInstance(IExtensionContext context, String xsiType, JFrame parent, ITarget target, boolean editable) {

        switch (xsiType) {
            case DeepSkyTargetDN.XML_XSI_TYPE_VALUE:
                return new DeepSkyTargetDNDialog(parent, context.getUserInterfaceHelper(), context.getModel(), target);
            case DeepSkyTargetDS.XML_XSI_TYPE_VALUE:
                return new DeepSkyTargetDSDialog(parent, context.getUserInterfaceHelper(), context.getModel(), target);
            case DeepSkyTargetGC.XML_XSI_TYPE_VALUE:
                return new DeepSkyTargetGCDialog(parent, context.getUserInterfaceHelper(), context.getModel(), target);
            case DeepSkyTargetGN.XML_XSI_TYPE_VALUE:
                return new DeepSkyTargetGNDialog(parent, context.getUserInterfaceHelper(), context.getModel(), target);
            case DeepSkyTargetGX.XML_XSI_TYPE_VALUE:
                return new DeepSkyTargetGXDialog(parent, context.getUserInterfaceHelper(), context.getModel(), target);
            case DeepSkyTargetOC.XML_XSI_TYPE_VALUE:
                return new DeepSkyTargetOCDialog(parent, context.getUserInterfaceHelper(), context.getModel(), target);
            case DeepSkyTargetPN.XML_XSI_TYPE_VALUE:
                return new DeepSkyTargetPNDialog(parent, context.getUserInterfaceHelper(), context.getModel(), target);
            case DeepSkyTargetQS.XML_XSI_TYPE_VALUE:
                return new DeepSkyTargetQSDialog(parent, context.getUserInterfaceHelper(), context.getModel(), target);
            case DeepSkyTargetNA.XML_XSI_TYPE_VALUE:
                return new DeepSkyTargetNADialog(parent, context.getUserInterfaceHelper(), context.getModel(), target);
            case DeepSkyTargetAS.XML_XSI_TYPE_VALUE:
                return new DeepSkyTargetASDialog(parent, context.getUserInterfaceHelper(), context.getModel(), target);
            case DeepSkyTargetSC.XML_XSI_TYPE_VALUE:
                return new DeepSkyTargetSCDialog(parent, context.getUserInterfaceHelper(), context.getModel(), target);
            case DeepSkyTargetMS.XML_XSI_TYPE_VALUE:
                return new DeepSkyTargetMSDialog(parent, context.getUserInterfaceHelper(), context.getModel(), target);
            case DeepSkyTargetCG.XML_XSI_TYPE_VALUE:
                return new DeepSkyTargetCGDialog(parent, context.getUserInterfaceHelper(), context.getModel(), target);
            default:
                throw new IllegalArgumentException("Invalid dialog type");
        }
    }
}
