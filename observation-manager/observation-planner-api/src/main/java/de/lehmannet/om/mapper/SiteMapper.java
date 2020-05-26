package de.lehmannet.om.mapper;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.lehmannet.om.Angle;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ISite;
import de.lehmannet.om.util.FloatUtil;
import de.lehmannet.om.util.SchemaException;

public class SiteMapper {

    public static String getOptionalIauCode(Element siteElement) throws SchemaException {

        // Get optional iauCode

        NodeList children = siteElement.getElementsByTagName(ISite.XML_ELEMENT_IAUCODE);
        String iauCode = null;
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            if (child != null) {
                iauCode = child.getFirstChild().getNodeValue();
                return iauCode.trim();
            } else {
                throw new SchemaException("Problem while retrieving IAU code from site. ");
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("Site can have only one IAU code. ");
        }
        return null;
    }

    public static float getOptionalElevation(Element siteElement) throws SchemaException {

        // Get optional elevation

        NodeList children = siteElement.getElementsByTagName(ISite.XML_ELEMENT_ELEVATION);
        String elevation = null;
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            if (child != null) {
                elevation = child.getFirstChild().getNodeValue();
                return FloatUtil.parseFloat(elevation);
            } else {
                throw new SchemaException("Problem while retrieving elevation from site. ");
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("Site can have only one elevation level. ");
        }
        return Float.NaN;
    }

    public static int getMandatoryTimeZone(Element siteElement) throws SchemaException {

        // Get mandatory timezone

        NodeList children = siteElement.getElementsByTagName(ISite.XML_ELEMENT_TIMEZONE);
        if (children.getLength() != 1) {
            throw new SchemaException("Site must have exact one timezone. ");
        }
        Element child = (Element) children.item(0);

        if (child == null) {
            throw new SchemaException("Site must have a timezone. ");
        } else {
            String timezone = child.getFirstChild().getNodeValue();
            return Integer.parseInt(timezone);
        }
    }

    public static Angle getMandatoryLatitude(Element siteElement) throws SchemaException {

        // Get mandatory latitude

        NodeList children = siteElement.getElementsByTagName(ISite.XML_ELEMENT_LATITUDE);
        if (children.getLength() != 1) {
            throw new SchemaException("Site must have exact one latitude. ");
        }
        Element child = (Element) children.item(0);
        if (child == null) {
            throw new SchemaException("Site must have a latitude. ");
        } else {
            String value = child.getFirstChild().getNodeValue();
            String unit = child.getAttribute(Angle.XML_ATTRIBUTE_UNIT);
            return new Angle(Double.parseDouble(value), unit);
        }
    }

    public static Angle getMandatoryLongitude(Element siteElement) throws SchemaException {

        // Get mandatory longitude

        NodeList children = siteElement.getElementsByTagName(ISite.XML_ELEMENT_LONGITUDE);
        if (children.getLength() != 1) {
            throw new SchemaException("Site must have exact one longitude. ");
        }
        Element child = (Element) children.item(0);

        if (child == null) {
            throw new SchemaException("Site must have a longitude. ");
        } else {
            String value = child.getFirstChild().getNodeValue();
            String unit = child.getAttribute(Angle.XML_ATTRIBUTE_UNIT);
            return new Angle(Double.parseDouble(value), unit);
        }

    }

    public static String getMandatoryName(Element siteElement) throws SchemaException {

        // Get mandatory name
        NodeList children = siteElement.getElementsByTagName(ISite.XML_ELEMENT_NAME);
        if (children.getLength() != 1) {
            throw new SchemaException("Site must have exact one name. ");
        }
        Element child = (Element) children.item(0);
        StringBuilder name = new StringBuilder();
        if (child == null) {
            throw new SchemaException("Site must have a name. ");
        } else {
            if (child.getFirstChild() != null) {
                // name = child.getFirstChild().getNodeValue();
                NodeList textElements = child.getChildNodes();
                if (textElements.getLength() > 0) {
                    for (int te = 0; te < textElements.getLength(); te++) {
                        name.append(textElements.item(te).getNodeValue());
                    }
                    return name.toString().trim();
                }
            } else {
                throw new SchemaException("Site cannot have an empty name. ");
            }

        }
        return "";
    }

    public static String getMandatoryID(Element siteElement) throws SchemaException {
        // Get ID from element
        String ID = siteElement.getAttribute(ISchemaElement.XML_ELEMENT_ATTRIBUTE_ID);
        if (StringUtils.isBlank(ID)) {
            throw new SchemaException("Site must have a ID. ");
        }
        return ID;
    }

}