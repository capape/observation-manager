package de.lehmannet.om.mapper;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.lehmannet.om.extension.variableStars.FindingVariableStar;
import de.lehmannet.om.util.FloatUtil;
import de.lehmannet.om.util.SchemaException;

public class FindingVariableStarMapper {

    

   
    public static boolean extracted6(Element finding) {
        // Get optional unusual activity attribute
        boolean result = false;
        String una = finding.getAttribute(FindingVariableStar.XML_ELEMENT_FINDING_ATTRIBUTE_UNUSUALACTIVITY);
        if ((una != null) && (!"".equals(una.trim()))) {
            result = Boolean.parseBoolean(una);
        }
        return result;
    }

    public static boolean getOptionalStarIdentificationUncertain(Element finding) {
        // Get optional star identification uncertain attribute
        boolean result = false;
        String si = finding.getAttribute(FindingVariableStar.XML_ELEMENT_FINDING_ATTRIBUTE_STARIDENTIFICATIONUNCERTAIN);
        if ((si != null) && (!"".equals(si.trim()))) {
            result = Boolean.parseBoolean(si);
        }
        return result;
    }

    public static boolean getOptionalPoorSeeing(Element finding) {
        // Get optional poor seeing attribute
        boolean result = false;
        String ps = finding.getAttribute(FindingVariableStar.XML_ELEMENT_FINDING_ATTRIBUTE_POORSEEING);
        if ((ps != null) && (!"".equals(ps.trim()))) {
            result = Boolean.parseBoolean(ps);
        }
        return result;
    }

    public static boolean getOptionalOutburst(Element finding) {
        // Get optional outbust attribute
        boolean result = false;
        String ob = finding.getAttribute(FindingVariableStar.XML_ELEMENT_FINDING_ATTRIBUTE_OUTBURST);
        if ((ob != null) && (!"".equals(ob.trim()))) {
            result = Boolean.parseBoolean(ob);
        }
        return result;
    }

    public static boolean getOptionalNearHorizon(Element finding) {
        // Get optional near horizon attribute
        boolean result = false;
        String nh = finding.getAttribute(FindingVariableStar.XML_ELEMENT_FINDING_ATTRIBUTE_NEARHORIZON);
        if ((nh != null) && (!"".equals(nh.trim()))) {
            result = Boolean.parseBoolean(nh);
        }
        return result;
    }

    public static boolean getOptionalFaintStar(Element finding) {
        // Get optional faint star attribute
        boolean result = false;
        String fs = finding.getAttribute(FindingVariableStar.XML_ELEMENT_FINDING_ATTRIBUTE_FAINTSTAR);
        if ((fs != null) && (!"".equals(fs.trim()))) {
            result = Boolean.parseBoolean(fs);
        }
        return result;
    }

    public static boolean getOptionalComparismSequenceProblem(Element finding) {
        // Get optional comparism problem attribute
        boolean result = false;
        String cp = finding.getAttribute(FindingVariableStar.XML_ELEMENT_FINDING_ATTRIBUTE_COMPARISMSEQPROBLEM);
        if ((cp != null) && (!"".equals(cp.trim()))) {
            result = Boolean.parseBoolean(cp);
            
        }
        return result;
    }

    public static boolean getOptionalCloudAttributes(Element finding) {
        // Get optional clouds attribute
        boolean result = false;
        String c = finding.getAttribute(FindingVariableStar.XML_ELEMENT_FINDING_ATTRIBUTE_CLOUDS);
        if ((c != null) && (!"".equals(c.trim()))) {
            result = Boolean.parseBoolean(c);
        }
        return result;
    }

    public static boolean getOptionalBrightSky(Element finding) {
        // Get optional bright sky attribute
        boolean parseBoolean = false;
        String bs = finding.getAttribute(FindingVariableStar.XML_ELEMENT_FINDING_ATTRIBUTE_BRIGHTSKY);
        if ((bs != null) && (!"".equals(bs.trim()))) {
            parseBoolean = Boolean.parseBoolean(bs);
           
        }
        return parseBoolean;
    }

    public static boolean getOptionalAlreadyExportedToAAVSO(Element finding) {
        // Search for optional export comment within nodes
        boolean result = false;
        NodeList list = finding.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node c = list.item(i);
            if (c.getNodeType() == Node.COMMENT_NODE) {
                if (FindingVariableStar.XML_COMMENT_FINDING_EXPORTED_TO_AAVSO.equals(c.getNodeValue())) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    public static List<String> getMandatoryCompStars(Element finding) throws SchemaException {
        Element child;
        NodeList children;
        // Get mandatory compStars
        children = finding.getElementsByTagName(FindingVariableStar.XML_ELEMENT_COMPARISMSTAR);
        if ((children == null) || (children.getLength() < 1)) {
            throw new SchemaException(
                    "FindingVariableStar must have at least one comparism star.");
        }
       
        List<String> results = new ArrayList<>();
        for (int i = 0; i < children.getLength(); i++) {
            StringBuilder currentCompStar = new StringBuilder();
            child = (Element) children.item(i);
            if (child == null) {
                throw new SchemaException(
                        "FindingVariableStar must have at least one comparism star. ");
            } else {
                NodeList textElements = child.getChildNodes();
                if ((textElements != null) && (textElements.getLength() > 0)) {
                    for (int te = 0; te < textElements.getLength(); te++) {
                        currentCompStar.append(textElements.item(te).getNodeValue());
                    }
                    // currentCompStar = child.getFirstChild().getNodeValue();
                    results.add(currentCompStar.toString());
                }
            }
        }
        return results;
    }

    public static String getMandatoryChartDate(Element finding) throws SchemaException {
        Element child;
        NodeList children;
        // Get mandatory chartDate
        children = finding.getElementsByTagName(FindingVariableStar.XML_ELEMENT_CHARTID);
        if ((children == null) || (children.getLength() != 1)) {
            throw new SchemaException(
                    "FindingVariableStar must have exact one chart ID or date. ");
        }
        child = (Element) children.item(0);
        StringBuilder chartID = new StringBuilder();
        if (child == null) {
            throw new SchemaException("FindingVariableStar must have a chart ID or date. ");
        } else {
            NodeList textElements = child.getChildNodes();
            if ((textElements != null) && (textElements.getLength() > 0)) {
                for (int te = 0; te < textElements.getLength(); te++) {
                    chartID.append(textElements.item(te).getNodeValue());
                }
                // chartID = child.getFirstChild().getNodeValue();
                return chartID.toString();
            }
        }
        return null;
    }

    public static boolean getOptionalNonAAVSOchart(Element finding) throws SchemaException {
        Element child;
        NodeList children;
        // Get mandatory chartDate
        children = finding.getElementsByTagName(FindingVariableStar.XML_ELEMENT_CHARTID);
        if ((children == null) || (children.getLength() != 1)) {
            throw new SchemaException(
                    "FindingVariableStar must have exact one chart ID or date. ");
        }
        child = (Element) children.item(0);
        if (child == null) {
            throw new SchemaException("FindingVariableStar must have a chart ID or date. ");
        } else {
            
            // Get optional non aavso chart attribute
            String na = child.getAttribute(FindingVariableStar.XML_ELEMENT_FINDING_ATTRIBUTE_NONAAVSOCHART);
            if ((na != null) && (!"".equals(na.trim()))) {
                return Boolean.parseBoolean(na);
            }
        }
        return false;
    }


    public static float getMandatoryMagnitude(Element finding) throws SchemaException {
        Element child;
        NodeList children;
        // Get mandatory magnitude
        children = finding.getElementsByTagName(FindingVariableStar.XML_ELEMENT_VISMAG);
        if ((children == null) || (children.getLength() != 1)) {
            throw new SchemaException(
                    "FindingVariableStar must have exact one visual magnitude value. ");
        }
        child = (Element) children.item(0);
        String visMag = null;
        if (child == null) {
            throw new SchemaException("FindingVariableStar must have a visual magnitude. ");
        } else {
            visMag = child.getFirstChild().getNodeValue();
            try {
                return FloatUtil.parseFloat(visMag);
            } catch (NumberFormatException nfe) {
                throw new SchemaException(
                        "FindingVariableStar visual magnitude must be a numeric value. ",
                        nfe);
            }

        }
    }

    public static boolean getOptionalMagnitudeFainterThan(Element finding) throws SchemaException {
        Element child;
        NodeList children;
        // Get mandatory magnitude
        children = finding.getElementsByTagName(FindingVariableStar.XML_ELEMENT_VISMAG);
        if ((children == null) || (children.getLength() != 1)) {
            throw new SchemaException(
                    "FindingVariableStar must have exact one visual magnitude value. ");
        }
        child = (Element) children.item(0);
       
        if (child == null) {
            throw new SchemaException("FindingVariableStar must have a visual magnitude. ");
        } else {

            // Get optional magnitude fainter than attribute
            String ft = child.getAttribute(FindingVariableStar.XML_ELEMENT_FINDING_ATTRIBUTE_FAINTERTHAN);
            if ((ft != null) && (!"".equals(ft.trim()))) {
                return Boolean.parseBoolean(ft);
            }
        }
        return false;
    }

    public static boolean getOptionalMagnitudeUncertain(Element finding) throws SchemaException {
        Element child;
        NodeList children;
        // Get mandatory magnitude
        children = finding.getElementsByTagName(FindingVariableStar.XML_ELEMENT_VISMAG);
        if ((children == null) || (children.getLength() != 1)) {
            throw new SchemaException(
                    "FindingVariableStar must have exact one visual magnitude value. ");
        }
        child = (Element) children.item(0);
       
        if (child == null) {
            throw new SchemaException("FindingVariableStar must have a visual magnitude. ");
        } else {

            // Get optional magnitude uncertain attribute
            String un = child.getAttribute(FindingVariableStar.XML_ELEMENT_FINDING_ATTRIBUTE_UNCERTAIN);
            if ((un != null) && (!"".equals(un.trim()))) {
                return Boolean.parseBoolean(un);
            }
        }
        return false;
    }

}