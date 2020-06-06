package de.lehmannet.om.mapper;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.lehmannet.om.IEquipment;
import de.lehmannet.om.IFilter;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.util.SchemaException;

public class FilterMapper {

    public static String getOptionalVendorName(Element filterElement) throws SchemaException {

        // Get optional vendor name

        NodeList children = filterElement.getElementsByTagName(IFilter.XML_ELEMENT_VENDOR);
        StringBuilder vendor = new StringBuilder();
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            if (child != null) {
                NodeList textElements = child.getChildNodes();
                if (textElements.getLength() > 0) {
                    for (int te = 0; te < textElements.getLength(); te++) {
                        vendor.append(textElements.item(te).getNodeValue());
                    }
                    return vendor.toString().trim();
                }
            } else {
                throw new SchemaException("Problem while retrieving vendor name from filter. ");
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("Filter can have only one vendor name. ");
        }

        return null;
    }

    public static String getOptionalSchottValue(Element filterElement) throws SchemaException {

        // Get optional schott value

        NodeList children = filterElement.getElementsByTagName(IFilter.XML_ELEMENT_SCHOTT);
        StringBuilder schott = new StringBuilder();
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            if (child != null) {
                NodeList textElements = child.getChildNodes();
                if (textElements.getLength() > 0) {
                    for (int te = 0; te < textElements.getLength(); te++) {
                        schott.append(textElements.item(te).getNodeValue());
                    }
                    return schott.toString().trim();
                }
                /*
                 * schott = child.getFirstChild().getNodeValue(); if( schott != null ) { this.setSchott(schott); }
                 */
            } else {
                throw new SchemaException("Problem while retrieving schott value from filter. ");
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("Filter can have only one schott value. ");
        }

        return null;
    }

    public static String getOptionalWrattenValue(Element filterElement) throws SchemaException {

        // Get optional wratten value

        NodeList children = filterElement.getElementsByTagName(IFilter.XML_ELEMENT_WRATTEN);
        StringBuilder wratten = new StringBuilder();
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            if (child != null) {
                NodeList textElements = child.getChildNodes();
                if (textElements.getLength() > 0) {
                    for (int te = 0; te < textElements.getLength(); te++) {
                        wratten.append(textElements.item(te).getNodeValue());
                    }
                    return wratten.toString().trim();
                }
                /*
                 * wratten = child.getFirstChild().getNodeValue(); if( wratten != null ) { this.setWratten(wratten); }
                 */
            } else {
                throw new SchemaException("Problem while retrieving wratten value from filter. ");
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("Filter can have only one wratten value. ");
        }

        return null;
    }

    public static String getOptionalColor(Element filterElement) throws SchemaException {

        // Get optional color

        NodeList children = filterElement.getElementsByTagName(IFilter.XML_ELEMENT_COLOR);
        StringBuilder color = new StringBuilder();
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            if (child != null) {
                NodeList textElements = child.getChildNodes();
                if (textElements.getLength() > 0) {
                    for (int te = 0; te < textElements.getLength(); te++) {
                        color.append(textElements.item(te).getNodeValue());
                    }
                    return color.toString().trim();
                }
                /*
                 * color = child.getFirstChild().getNodeValue(); if( color != null ) { this.setColor(color); }
                 */
            } else {
                throw new SchemaException("Problem while retrieving color from filter. ");
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("Filter can have only one color. ");
        }

        return null;
    }

    public static boolean getOptionalAvailability(Element filterElement) {
        // Search for optional availability comment within nodes
        NodeList list = filterElement.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node c = list.item(i);
            if (c.getNodeType() == Node.COMMENT_NODE) {
                if (IEquipment.XML_COMMENT_ELEMENT_NOLONGERAVAILABLE.equals(c.getNodeValue())) {
                    return false;
                }
            }
        }
        return true;
    }

    public static String getMandatoryType(Element filterElement) throws SchemaException {

        // Get mandatory type
        NodeList children = filterElement.getElementsByTagName(IFilter.XML_ELEMENT_TYPE);
        if (children.getLength() != 1) {
            throw new SchemaException("Filter must have exact one type. ");
        }
        Element child = (Element) children.item(0);
        StringBuilder type = new StringBuilder();
        if (child == null) {
            throw new SchemaException("Filter must have a type. ");
        } else {
            if (child.getFirstChild() != null) {
                NodeList textElements = child.getChildNodes();
                if (textElements.getLength() > 0) {
                    for (int te = 0; te < textElements.getLength(); te++) {
                        type.append(textElements.item(te).getNodeValue());
                    }
                    return type.toString().trim();

                }
                // type = child.getFirstChild().getNodeValue();
            } else {
                throw new SchemaException("Filter cannot have an empty type. ");
            }
        }
        return "";
    }

    public static String getMandatoryModel(Element filterElement) throws SchemaException {

        // Get mandatory model
        NodeList children = filterElement.getElementsByTagName(IFilter.XML_ELEMENT_MODEL);
        if (children.getLength() != 1) {
            throw new SchemaException("Filter must have exact one model name. ");
        }
        Element child = (Element) children.item(0);
        StringBuilder model = new StringBuilder();
        if (child == null) {
            throw new SchemaException("Filter must have a model name. ");
        } else {
            if (child.getFirstChild() != null) {
                NodeList textElements = child.getChildNodes();
                if (textElements.getLength() > 0) {
                    for (int te = 0; te < textElements.getLength(); te++) {
                        model.append(textElements.item(te).getNodeValue());
                    }
                    return model.toString().trim();
                }
                // model = child.getFirstChild().getNodeValue();
            } else {
                throw new SchemaException("Filter cannot have an empty model name. ");
            }
        }
        return "";
    }

    public static String getID(Element filterElement) throws SchemaException {
        // Get ID from element
        String ID = filterElement.getAttribute(ISchemaElement.XML_ELEMENT_ATTRIBUTE_ID);
        if (StringUtils.isBlank(ID)) {
            throw new SchemaException("Filter must have a ID. ");
        }
        return ID;
    }
}