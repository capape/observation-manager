package de.lehmannet.om.mapper;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.lehmannet.om.IEquipment;
import de.lehmannet.om.IImager;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.util.SchemaException;

public class ImagerMapper {

    public static String getOptionalRemarks(Element imagerElement) throws SchemaException {
        NodeList children;
        Element child;
        // Get optional remarks
        child = null;
        children = imagerElement.getElementsByTagName(IImager.XML_ELEMENT_REMARKS);
        StringBuilder remarks = new StringBuilder();
        if (children != null) {
            if (children.getLength() == 1) {
                child = (Element) children.item(0);
                if (child != null) {
                    NodeList textElements = child.getChildNodes();
                    if ((textElements != null) && (textElements.getLength() > 0)) {
                        for (int te = 0; te < textElements.getLength(); te++) {
                            remarks.append(textElements.item(te).getNodeValue());
                        }
                        return remarks.toString();
                    }
                    /*
                     * remarks = child.getFirstChild().getNodeValue(); if( type != null ) {
                     * this.setRemarks(remarks); }
                     */
                } else {
                    throw new SchemaException("Problem while retrieving remarks from imager. ");
                }
            } else if (children.getLength() > 1) {
                throw new SchemaException("Imager can have only one remark element. ");
            }
        }
        return null;
    }

    public static String getOptionalVendor(Element imagerElement) throws SchemaException {
        NodeList children;
        Element child;
        // Get optional vendor
        child = null;
        children = imagerElement.getElementsByTagName(IImager.XML_ELEMENT_VENDOR);
        StringBuilder vendor = new StringBuilder();
        if (children != null) {
            if (children.getLength() == 1) {
                child = (Element) children.item(0);
                if (child != null) {
                    NodeList textElements = child.getChildNodes();
                    if ((textElements != null) && (textElements.getLength() > 0)) {
                        for (int te = 0; te < textElements.getLength(); te++) {
                            vendor.append(textElements.item(te).getNodeValue());
                        }
                        return vendor.toString();
                    }
                    /*
                     * vendor = child.getFirstChild().getNodeValue(); if( vendor != null ) {
                     * this.setVendor(vendor); }
                     */
                } else {
                    throw new SchemaException("Problem while retrieving vendor from imager. ");
                }
            } else if (children.getLength() > 1) {
                throw new SchemaException("Imager can have only one vendor. ");
            }
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
        NodeList children;
        Element child;
        // Get mandatory model
        children = imagerElement.getElementsByTagName(IImager.XML_ELEMENT_MODEL);
        if ((children == null) || (children.getLength() != 1)) {
            throw new SchemaException("Imager must have exact one model name. ");
        }
        child = (Element) children.item(0);
        StringBuilder model = new StringBuilder();
        if (child == null) {
            throw new SchemaException("Imager must have a model name. ");
        } else {
            if (child.getFirstChild() != null) {
                // model = child.getFirstChild().getNodeValue();
                NodeList textElements = child.getChildNodes();
                if ((textElements != null) && (textElements.getLength() > 0)) {
                    for (int te = 0; te < textElements.getLength(); te++) {
                        model.append(textElements.item(te).getNodeValue());
                    }
                    return model.toString();
                }
            } else {
                throw new SchemaException("Imager cannot have an empty model name. ");
            }
        }
        return "";
    }

    public static String getMandatoryID(Element imagerElement) throws SchemaException {
        String ID = imagerElement.getAttribute(ISchemaElement.XML_ELEMENT_ATTRIBUTE_ID);
        if ((ID != null) && ("".equals(ID.trim()))) {
            throw new SchemaException("Imager must have a ID. ");
        }
        return ID;
    }

}