package de.lehmannet.om.mapper;

import de.lehmannet.om.IEquipment;
import de.lehmannet.om.IImager;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.util.SchemaException;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ImagerMapper {

    public static String getOptionalRemarks(Element imagerElement) throws SchemaException {

        // Get optional remarks

        NodeList children = imagerElement.getElementsByTagName(IImager.XML_ELEMENT_REMARKS);
        StringBuilder remarks = new StringBuilder();
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            if (child != null) {
                NodeList textElements = child.getChildNodes();
                if (textElements.getLength() > 0) {
                    for (int te = 0; te < textElements.getLength(); te++) {
                        remarks.append(textElements.item(te).getNodeValue());
                    }
                    return remarks.toString().trim();
                }
                /*
                 * remarks = child.getFirstChild().getNodeValue(); if( type != null ) { this.setRemarks(remarks); }
                 */
            } else {
                throw new SchemaException("Problem while retrieving remarks from imager. ");
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("Imager can have only one remark element. ");
        }

        return null;
    }

    public static String getOptionalVendor(Element imagerElement) throws SchemaException {

        // Get optional vendor

        NodeList children = imagerElement.getElementsByTagName(IImager.XML_ELEMENT_VENDOR);
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
                /*
                 * vendor = child.getFirstChild().getNodeValue(); if( vendor != null ) { this.setVendor(vendor); }
                 */
            } else {
                throw new SchemaException("Problem while retrieving vendor from imager. ");
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("Imager can have only one vendor. ");
        }

        return null;
    }

    public static boolean getOptionalAvailability(Element imagerElement) {
        // Search for optional availability comment within nodes
        NodeList list = imagerElement.getChildNodes();
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

    public static String getMandatoryModel(Element imagerElement) throws SchemaException {

        // Get mandatory model
        NodeList children = imagerElement.getElementsByTagName(IImager.XML_ELEMENT_MODEL);
        if (children.getLength() != 1) {
            throw new SchemaException("Imager must have exact one model name. ");
        }
        Element child = (Element) children.item(0);
        StringBuilder model = new StringBuilder();
        if (child == null) {
            throw new SchemaException("Imager must have a model name. ");
        } else {
            if (child.getFirstChild() != null) {
                // model = child.getFirstChild().getNodeValue();
                NodeList textElements = child.getChildNodes();
                if (textElements.getLength() > 0) {
                    for (int te = 0; te < textElements.getLength(); te++) {
                        model.append(textElements.item(te).getNodeValue());
                    }
                    return model.toString().trim();
                }
            } else {
                throw new SchemaException("Imager cannot have an empty model name. ");
            }
        }
        return "";
    }

    public static String getMandatoryID(Element imagerElement) throws SchemaException {
        String ID = imagerElement.getAttribute(ISchemaElement.XML_ELEMENT_ATTRIBUTE_ID);
        if (StringUtils.isBlank(ID)) {
            throw new SchemaException("Imager must have a ID. ");
        }
        return ID;
    }
}
