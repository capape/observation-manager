package de.lehmannet.om.ui.extension.deepSky;

import de.lehmannet.om.ITarget;
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
import de.lehmannet.om.ui.extension.IExtensionContext;
import de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetASPanel;
import de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetCGPanel;
import de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetDNPanel;
import de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetDSPanel;
import de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetGCPanel;
import de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetGNPanel;
import de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetGXPanel;
import de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetMSPanel;
import de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetNAPanel;
import de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetOCPanel;
import de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetPNPanel;
import de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetQSPanel;
import de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyTargetSCPanel;
import de.lehmannet.om.ui.panel.AbstractPanel;

public class TargetPanelFactory {

    public static AbstractPanel newInstance(IExtensionContext context, String xsiType, ITarget target,
            boolean editable) {

        switch (xsiType) {
        case DeepSkyTargetDN.XML_XSI_TYPE_VALUE:
            return new DeepSkyTargetDNPanel(context.getConfiguration(), context.getUserInterfaceHelper(),
                    context.getModel(), target, editable);
        case DeepSkyTargetDS.XML_XSI_TYPE_VALUE:
            return new DeepSkyTargetDSPanel(context.getUserInterfaceHelper(), context.getModel(), target, editable);
        case DeepSkyTargetGC.XML_XSI_TYPE_VALUE:
            return new DeepSkyTargetGCPanel(context.getUserInterfaceHelper(), context.getModel(), target, editable);
        case DeepSkyTargetGN.XML_XSI_TYPE_VALUE:
            return new DeepSkyTargetGNPanel(context.getUserInterfaceHelper(), context.getModel(), target, editable);
        case DeepSkyTargetGX.XML_XSI_TYPE_VALUE:
            return new DeepSkyTargetGXPanel(context.getUserInterfaceHelper(), context.getModel(), target, editable);
        case DeepSkyTargetOC.XML_XSI_TYPE_VALUE:
            return new DeepSkyTargetOCPanel(context.getUserInterfaceHelper(), context.getModel(), target, editable);
        case DeepSkyTargetPN.XML_XSI_TYPE_VALUE:
            return new DeepSkyTargetPNPanel(context.getUserInterfaceHelper(), context.getModel(), target, editable);
        case DeepSkyTargetQS.XML_XSI_TYPE_VALUE:
            return new DeepSkyTargetQSPanel(context.getUserInterfaceHelper(), context.getModel(), target, editable);
        case DeepSkyTargetNA.XML_XSI_TYPE_VALUE:
            return new DeepSkyTargetNAPanel(context.getUserInterfaceHelper(), context.getModel(), target, editable);
        case DeepSkyTargetAS.XML_XSI_TYPE_VALUE:
            return new DeepSkyTargetASPanel(context.getUserInterfaceHelper(), context.getModel(), target, editable);
        case DeepSkyTargetSC.XML_XSI_TYPE_VALUE:
            return new DeepSkyTargetSCPanel(context.getUserInterfaceHelper(), context.getModel(), target, editable);
        case DeepSkyTargetMS.XML_XSI_TYPE_VALUE:
            return new DeepSkyTargetMSPanel(context.getUserInterfaceHelper(), context.getModel(), target, editable);
        case DeepSkyTargetCG.XML_XSI_TYPE_VALUE:
            return new DeepSkyTargetCGPanel(context.getConfiguration(), context.getUserInterfaceHelper(),
                    context.getModel(), target, editable);
        default:
            throw new IllegalArgumentException("Invalid target panel");
        }
    }

}
