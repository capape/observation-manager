package de.lehmannet.om.mapper;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.lehmannet.om.Angle;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ISite;
import de.lehmannet.om.util.FloatUtil;
import de.lehmannet.om.util.SchemaException;

public class SiteMapper {

    public static String getOptionalIauCode(Element siteElement) throws SchemaException {
        Element child;
        NodeList children;
        // Get optional iauCode
        child = null;
        children = siteElement.getElementsByTagName(ISite.XML_ELEMENT_IAUCODE);
        String iauCode = null;
        if (children != null) {
            if (children.getLength() == 1) {
                child = (Element) children.item(0);
                if (child != null) {
                    iauCode = child.getFirstChild().getNodeValue();
                    return iauCode;
                } else {
                    throw new SchemaException("Problem while retrieving IAU code from site. ");
                }
            } else if (children.getLength() > 1) {
                throw new SchemaException("Site can have only one IAU code. ");
            }
        }
        return null;
    }
    
    public static float getOptionalElevation(Element siteElement) throws SchemaException {
        Element child;
        NodeList children;
        // Get optional elevation
        child = null;
        children = siteElement.getElementsByTagName(ISite.XML_ELEMENT_ELEVATION);
        String elevation = null;
        if (children != null) {
            if (children.getLength() == 1) {
                child = (Element) children.item(0);
                if (child != null) {
                    elevation = child.getFirstChild().getNodeValue();
                    return FloatUtil.parseFloat(elevation);
                } else {
                    throw new SchemaException("Problem while retrieving elevation from site. ");
                }
            } else if (children.getLength() > 1) {
                throw new SchemaException("Site can have only one elevation level. ");
            }
        }
        return Float.NaN;
    }
    
    public static int getMandatoryTimeZone(Element siteElement) throws SchemaException {
        Element child;
        NodeList children;
        // Get mandatory timezone
        child = null;
        children = siteElement.getElementsByTagName(ISite.XML_ELEMENT_TIMEZONE);
        if ((children == null) || (children.getLength() != 1)) {
            throw new SchemaException("Site must have exact one timezone. ");
        }
        child = (Element) children.item(0);
        
        if (child == null) {
            throw new SchemaException("Site must have a timezone. ");
        } else {
            String timezone = child.getFirstChild().getNodeValue();
            return Integer.parseInt(timezone);
        }
    }
    
    public static Angle getMandatoryLatitude(Element siteElement) throws SchemaException {
        Element child;
        NodeList children;
        // Get mandatory latitude
        child = null;
        children = siteElement.getElementsByTagName(ISite.XML_ELEMENT_LATITUDE);
        if ((children == null) || (children.getLength() != 1)) {
            throw new SchemaException("Site must have exact one latitude. ");
        }
        child = (Element) children.item(0);
        if (child == null) {
            throw new SchemaException("Site must have a latitude. ");
        } else {
            String value = child.getFirstChild().getNodeValue();
            String unit = child.getAttribute(Angle.XML_ATTRIBUTE_UNIT);
            return new Angle(Double.parseDouble(value), unit);
        }
    }
    
    public static Angle getMandatoryLongitude(Element siteElement) throws SchemaException {
        Element child;
        NodeList children;
        // Get mandatory longitude
        child = null;
        children = siteElement.getElementsByTagName(ISite.XML_ELEMENT_LONGITUDE);
        if ((children == null) || (children.getLength() != 1)) {
            throw new SchemaException("Site must have exact one longitude. ");
        }
        child = (Element) children.item(0);
        
        if (child == null) {
            throw new SchemaException("Site must have a longitude. ");
        } else {
            String value = child.getFirstChild().getNodeValue();
            String unit = child.getAttribute(Angle.XML_ATTRIBUTE_UNIT);
            return new Angle(Double.parseDouble(value), unit);
        }    
        
    }
    
    public static String getMandatoryName(Element siteElement) throws SchemaException {
        Element child;
        NodeList children;
        // Get mandatory name
        children = siteElement.getElementsByTagName(ISite.XML_ELEMENT_NAME);
        if ((children == null) || (children.getLength() != 1)) {
            throw new SchemaException("Site must have exact one name. ");
        }
        child = (Element) children.item(0);
        StringBuilder name = new StringBuilder();
        if (child == null) {
            throw new SchemaException("Site must have a name. ");
        } else {
            if (child.getFirstChild() != null) {
                // name = child.getFirstChild().getNodeValue();
                NodeList textElements = child.getChildNodes();
                if ((textElements != null) && (textElements.getLength() > 0)) {
                    for (int te = 0; te < textElements.getLength(); te++) {
                        name.append(textElements.item(te).getNodeValue());
                    }
                   return name.toString();
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
        if ((ID != null) && ("".equals(ID.trim()))) {
            throw new SchemaException("Site must have a ID. ");
        }
        return ID;
    }
    
}