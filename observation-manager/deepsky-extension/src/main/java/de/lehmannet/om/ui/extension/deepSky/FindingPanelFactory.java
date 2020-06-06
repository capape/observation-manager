package de.lehmannet.om.ui.extension.deepSky;

import de.lehmannet.om.ui.extension.IExtensionContext;
import de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyFindingDSPanel;
import de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyFindingOCPanel;
import de.lehmannet.om.ui.extension.deepSky.panel.DeepSkyFindingPanel;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.ui.panel.GenericFindingPanel;
import de.lehmannet.om.util.SchemaElementConstants;
import de.lehmannet.om.IFinding;
import de.lehmannet.om.ISession;
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

public class FindingPanelFactory {

    public static final AbstractPanel newInstance(IExtensionContext context, String xsiType, IFinding finding,
            ISession session, boolean editable) {
        switch (xsiType) {

        case DeepSkyTargetDN.XML_XSI_TYPE_VALUE:
            return new DeepSkyFindingPanel(context.getConfiguration(), finding, session, editable);

        case DeepSkyTargetNA.XML_XSI_TYPE_VALUE:
            return new GenericFindingPanel(context.getConfiguration(), finding, session, editable);

        case DeepSkyTargetDS.XML_XSI_TYPE_VALUE:
            return new DeepSkyFindingDSPanel(context.getConfiguration(), finding, session, editable);

        case DeepSkyTargetGC.XML_XSI_TYPE_VALUE:
            return new DeepSkyFindingPanel(context.getConfiguration(), finding, session, editable);

        case DeepSkyTargetGN.XML_XSI_TYPE_VALUE:
            return new DeepSkyFindingPanel(context.getConfiguration(), finding, session, editable);

        case DeepSkyTargetGX.XML_XSI_TYPE_VALUE:
            return new DeepSkyFindingPanel(context.getConfiguration(), finding, session, editable);

        case DeepSkyTargetOC.XML_XSI_TYPE_VALUE:
            return new DeepSkyFindingOCPanel(context.getConfiguration(), context.getInstallDir(), finding, session,
                    editable);

        case DeepSkyTargetPN.XML_XSI_TYPE_VALUE:
            return new DeepSkyFindingPanel(context.getConfiguration(), finding, session, editable);

        case DeepSkyTargetQS.XML_XSI_TYPE_VALUE:
            return new DeepSkyFindingPanel(context.getConfiguration(), finding, session, editable);

        case DeepSkyTargetAS.XML_XSI_TYPE_VALUE:
            return new DeepSkyFindingPanel(context.getConfiguration(), finding, session, editable);

        case DeepSkyTargetSC.XML_XSI_TYPE_VALUE:
            return new DeepSkyFindingPanel(context.getConfiguration(), finding, session, editable);

        case DeepSkyTargetMS.XML_XSI_TYPE_VALUE:
            return new GenericFindingPanel(context.getConfiguration(), finding, session, editable);

        case DeepSkyTargetCG.XML_XSI_TYPE_VALUE:
            return new DeepSkyFindingPanel(context.getConfiguration(), finding, session, editable);

        case DeepSkyFinding.XML_XSI_TYPE_VALUE:
            return new DeepSkyFindingPanel(context.getConfiguration(), finding, session, editable);

        case DeepSkyFindingOC.XML_XSI_TYPE_VALUE:
            return new DeepSkyFindingOCPanel(context.getConfiguration(), context.getInstallDir(), finding, session,
                    editable);

        case DeepSkyFindingDS.XML_XSI_TYPE_VALUE:
            return new DeepSkyFindingDSPanel(context.getConfiguration(), finding, session, editable);

        default:
            return new GenericFindingPanel(context.getConfiguration(), finding, session, editable);
        }

    }

}