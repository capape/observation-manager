package de.lehmannet.om.mapper;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.lehmannet.om.IEquipment;
import de.lehmannet.om.ILens;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.util.FloatUtil;
import de.lehmannet.om.util.SchemaException;

public class LensMapper {

    public static String getOptionalVendor(Element lensElement) throws SchemaException {

        // Get optional vendor

        NodeList children = lensElement.getElementsByTagName(ILens.XML_ELEMENT_VENDOR);
        StringBuilder vendor = new StringBuilder();
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            if (child != null) {
                NodeList textElements = child.getChildNodes();
                if (textElements.getLength() > 0) {
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
                throw new SchemaException("Problem while retrieving vendor from lens. ");
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("Lens can have only one vendor. ");
        }

        return null;
    }

    public static boolean getOptionalAvailability(Element lensElement) {
        // Search for optional availability comment within nodes
        NodeList list = lensElement.getChildNodes();
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

    public static float getMandatoryFactor(Element lensElement) throws SchemaException {

        // Get mandatory factor

        NodeList children = lensElement.getElementsByTagName(ILens.XML_ELEMENT_FACTOR);
        if (children.getLength() != 1) {
            throw new SchemaException("Lens must have exact one focal length factor. ");
        }
        Element child = (Element) children.item(0);
        String factor = null;
        if (child == null) {
            throw new SchemaException("Lens must have a focal length factor. ");
        } else {
            factor = child.getFirstChild().getNodeValue();
        }
        return FloatUtil.parseFloat(factor);

    }

    public static String getMandatoryModel(Element lensElement) throws SchemaException {

        // Get mandatory model
        NodeList children = lensElement.getElementsByTagName(ILens.XML_ELEMENT_MODEL);
        if (children.getLength() != 1) {
            throw new SchemaException("Lens must have exact one model name. ");
        }
        Element child = (Element) children.item(0);
        StringBuilder model = new StringBuilder();
        if (child == null) {
            throw new SchemaException("Lens must have a model name. ");
        } else {
            if (child.getFirstChild() != null) {
                NodeList textElements = child.getChildNodes();
                if (textElements.getLength() > 0) {
                    for (int te = 0; te < textElements.getLength(); te++) {
                        model.append(textElements.item(te).getNodeValue());
                    }
                    return model.toString();
                }
                // model = child.getFirstChild().getNodeValue();
            } else {
                throw new SchemaException("Lens cannot have an empty model name. ");
            }
        }
        return null;
    }

    public static String getMandatoryID(Element lensElement) throws SchemaException {
        // Get ID from element
        String ID = lensElement.getAttribute(ISchemaElement.XML_ELEMENT_ATTRIBUTE_ID);
        if (StringUtils.isBlank(ID)) {
            throw new SchemaException("Lens must have a ID. ");
        }
        return ID;
    }

}