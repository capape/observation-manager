package de.lehmannet.om.mapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.lehmannet.om.extension.variableStars.FindingVariableStar;
import de.lehmannet.om.util.FloatUtil;
import de.lehmannet.om.util.SchemaException;

public class FindingVariableStarMapper {

    public static boolean getOptionalUnsualActivity(Element finding) {
        // Get optional unusual activity attribute
        return getBooleanAttribute(finding, FindingVariableStar.XML_ELEMENT_FINDING_ATTRIBUTE_UNUSUALACTIVITY);
    }

    public static boolean getOptionalStarIdentificationUncertain(Element finding) {
        // Get optional star identification uncertain attribute
        return getBooleanAttribute(finding,
                FindingVariableStar.XML_ELEMENT_FINDING_ATTRIBUTE_STARIDENTIFICATIONUNCERTAIN);
    }

    public static boolean getOptionalPoorSeeing(Element finding) {
        // Get optional poor seeing attribute
        return getBooleanAttribute(finding, FindingVariableStar.XML_ELEMENT_FINDING_ATTRIBUTE_POORSEEING);

    }

    public static boolean getOptionalOutburst(Element finding) {
        return getBooleanAttribute(finding, FindingVariableStar.XML_ELEMENT_FINDING_ATTRIBUTE_OUTBURST);
    }

    public static boolean getOptionalNearHorizon(Element finding) {
        return getBooleanAttribute(finding, FindingVariableStar.XML_ELEMENT_FINDING_ATTRIBUTE_NEARHORIZON);
    }

    public static boolean getOptionalFaintStar(Element finding) {
        return getBooleanAttribute(finding, FindingVariableStar.XML_ELEMENT_FINDING_ATTRIBUTE_FAINTSTAR);
    }

    public static boolean getOptionalComparismSequenceProblem(Element finding) {
        return getBooleanAttribute(finding, FindingVariableStar.XML_ELEMENT_FINDING_ATTRIBUTE_COMPARISMSEQPROBLEM);
    }

    public static boolean getOptionalCloudAttributes(Element finding) {
        return getBooleanAttribute(finding, FindingVariableStar.XML_ELEMENT_FINDING_ATTRIBUTE_CLOUDS);
    }

    public static boolean getOptionalBrightSky(Element finding) {
        return getBooleanAttribute(finding, FindingVariableStar.XML_ELEMENT_FINDING_ATTRIBUTE_BRIGHTSKY);
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

        // Get mandatory compStars
        NodeList children = finding.getElementsByTagName(FindingVariableStar.XML_ELEMENT_COMPARISMSTAR);
        if (children.getLength() < 1) {
            throw new SchemaException("FindingVariableStar must have at least one comparism star.");
        }

        List<String> results = new ArrayList<>();
        for (int i = 0; i < children.getLength(); i++) {
            StringBuilder currentCompStar = new StringBuilder();
            Element child = (Element) children.item(i);
            if (child == null) {
                throw new SchemaException("FindingVariableStar must have at least one comparism star. ");
            } else {
                NodeList textElements = child.getChildNodes();
                if (textElements.getLength() > 0) {
                    for (int te = 0; te < textElements.getLength(); te++) {
                        currentCompStar.append(textElements.item(te).getNodeValue());
                    }
                    // currentCompStar = child.getFirstChild().getNodeValue();
                    results.add(currentCompStar.toString().trim());
                }
            }
        }
        return results;
    }

    public static String getMandatoryChartDate(Element finding) throws SchemaException {

        // Get mandatory chartDate
        NodeList children = finding.getElementsByTagName(FindingVariableStar.XML_ELEMENT_CHARTID);
        if (children.getLength() != 1) {
            throw new SchemaException("FindingVariableStar must have exact one chart ID or date. ");
        }
        Element child = (Element) children.item(0);
        StringBuilder chartID = new StringBuilder();
        if (child == null) {
            throw new SchemaException("FindingVariableStar must have a chart ID or date. ");
        } else {
            NodeList textElements = child.getChildNodes();
            if (textElements.getLength() > 0) {
                for (int te = 0; te < textElements.getLength(); te++) {
                    chartID.append(textElements.item(te).getNodeValue());
                }
                // chartID = child.getFirstChild().getNodeValue();
                return chartID.toString().trim();
            }
        }
        return null;
    }

    public static boolean getOptionalNonAAVSOchart(Element finding) throws SchemaException {

        // Get mandatory chartDate
        NodeList children = finding.getElementsByTagName(FindingVariableStar.XML_ELEMENT_CHARTID);
        if (children.getLength() != 1) {
            throw new SchemaException("FindingVariableStar must have exact one chart ID or date. ");
        }
        Element child = (Element) children.item(0);
        if (child == null) {
            throw new SchemaException("FindingVariableStar must have a chart ID or date. ");
        } else {

            // Get optional non aavso chart attribute
            String na = child.getAttribute(FindingVariableStar.XML_ELEMENT_FINDING_ATTRIBUTE_NONAAVSOCHART);
            if (!StringUtils.isBlank(na)) {
                return Boolean.parseBoolean(na);
            }
        }
        return false;
    }

    public static float getMandatoryMagnitude(Element finding) throws SchemaException {

        // Get mandatory magnitude
        NodeList children = finding.getElementsByTagName(FindingVariableStar.XML_ELEMENT_VISMAG);
        if (children.getLength() != 1) {
            throw new SchemaException("FindingVariableStar must have exact one visual magnitude value. ");
        }
        Element child = (Element) children.item(0);
        String visMag = null;
        if (child == null) {
            throw new SchemaException("FindingVariableStar must have a visual magnitude. ");
        } else {
            visMag = child.getFirstChild().getNodeValue();
            try {
                return FloatUtil.parseFloat(visMag);
            } catch (NumberFormatException nfe) {
                throw new SchemaException("FindingVariableStar visual magnitude must be a numeric value. ", nfe);
            }

        }
    }

    public static boolean getOptionalMagnitudeFainterThan(Element finding) throws SchemaException {

        // Get mandatory magnitude
        NodeList children = finding.getElementsByTagName(FindingVariableStar.XML_ELEMENT_VISMAG);
        if (children.getLength() != 1) {
            throw new SchemaException("FindingVariableStar must have exact one visual magnitude value. ");
        }
        Element child = (Element) children.item(0);

        if (child == null) {
            throw new SchemaException("FindingVariableStar must have a visual magnitude. ");
        } else {

            // Get optional magnitude fainter than attribute
            String ft = child.getAttribute(FindingVariableStar.XML_ELEMENT_FINDING_ATTRIBUTE_FAINTERTHAN);
            if (!StringUtils.isBlank(ft)) {
                return Boolean.parseBoolean(ft);
            }
        }
        return false;
    }

    public static boolean getOptionalMagnitudeUncertain(Element finding) throws SchemaException {

        // Get mandatory magnitude
        NodeList children = finding.getElementsByTagName(FindingVariableStar.XML_ELEMENT_VISMAG);
        if (children.getLength() != 1) {
            throw new SchemaException("FindingVariableStar must have exact one visual magnitude value. ");
        }
        Element child = (Element) children.item(0);

        if (child == null) {
            throw new SchemaException("FindingVariableStar must have a visual magnitude. ");
        } else {

            // Get optional magnitude uncertain attribute
            String un = child.getAttribute(FindingVariableStar.XML_ELEMENT_FINDING_ATTRIBUTE_UNCERTAIN);
            if (!StringUtils.isBlank(un)) {
                return Boolean.parseBoolean(un);
            }
        }
        return false;
    }

    private static boolean getBooleanAttribute(Element finding, String attribute) {
        boolean result = false;

        String una = finding.getAttribute(attribute);
        if (!StringUtils.isBlank(una)) {
            result = Boolean.parseBoolean(una);
        }
        return result;
    }
}