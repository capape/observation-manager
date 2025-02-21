package de.lehmannet.om.mapper;

import de.lehmannet.om.Angle;
import de.lehmannet.om.IEquipment;
import de.lehmannet.om.IEyepiece;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.util.FloatUtil;
import de.lehmannet.om.util.SchemaException;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class EyePieceMapper {

    public static Angle getApparentFOV(Element eyepieceElement) throws SchemaException {

        // Get optional apparent field of view

        NodeList children = eyepieceElement.getElementsByTagName(IEyepiece.XML_ELEMENT_APPARENTFOV);
        Angle apparentFOV = null;
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            if (child != null) {
                String value = child.getFirstChild().getNodeValue();
                String unit = child.getAttribute(Angle.XML_ATTRIBUTE_UNIT);
                apparentFOV = new Angle(Double.parseDouble(value), unit);
            } else {
                throw new SchemaException("Problem while retrieving apparent field of view from eyepiece. ");
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("Eyepiece can have only one apparent field of view. ");
        }
        return apparentFOV;
    }

    public static float getOptionalMaximunFocusLength(Element eyepieceElement) throws SchemaException {

        // Get optional maximal focus length

        NodeList children = eyepieceElement.getElementsByTagName(IEyepiece.XML_ELEMENT_MAXFOCALLENGTH);
        String maxFL = null;
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            if (child != null) {
                maxFL = child.getFirstChild().getNodeValue();
                if (maxFL != null) {
                    return FloatUtil.parseFloat(maxFL);
                }
            } else {
                throw new SchemaException("Problem while retrieving max focal length from eyepiece. ");
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("Eyepiece can have only one max focal length. ");
        }
        return Float.NaN;
    }

    public static String getOptionalVendor(Element eyepieceElement) throws SchemaException {

        NodeList children = eyepieceElement.getElementsByTagName(IEyepiece.XML_ELEMENT_VENDOR);

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
                // vendor = child.getFirstChild().getNodeValue();
                /*
                 * if( vendor != null ) { this.setVendor(vendor); }
                 */
            } else {
                throw new SchemaException("Problem while retrieving vendor from eyepiece. ");
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("Eyepiece can have only one vendor. ");
        }

        return null;
    }

    public static String getMandatoryID(Element eyepieceElement) throws SchemaException {
        // Get ID from element
        String ID = eyepieceElement.getAttribute(ISchemaElement.XML_ELEMENT_ATTRIBUTE_ID);
        if (StringUtils.isBlank(ID)) {
            throw new SchemaException("Eyepiece must have a ID. ");
        }
        return ID;
    }

    public static boolean getOptionalAvailability(Element eyepieceElement) {
        // Search for optional availability comment within nodes
        // TODO:
        NodeList list = eyepieceElement.getChildNodes();
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

    public static float getMandatoryFocalLength(Element eyepieceElement) throws SchemaException {

        // Get mandatory focalLength

        NodeList children = eyepieceElement.getElementsByTagName(IEyepiece.XML_ELEMENT_FOCALLENGTH);
        if (children.getLength() != 1) {
            throw new SchemaException("Eyepiece must have exact one focal length. ");
        }
        Element child = (Element) children.item(0);
        String focalLength = null;
        if (child == null) {
            throw new SchemaException("Eyepiece must have a focal length. ");
        } else {
            focalLength = child.getFirstChild().getNodeValue();
        }
        return FloatUtil.parseFloat(focalLength);
    }

    public static String getMandatoryModel(Element eyepieceElement) throws SchemaException {

        // Get mandatory model
        NodeList children = eyepieceElement.getElementsByTagName(IEyepiece.XML_ELEMENT_MODEL);
        if (children.getLength() != 1) {
            throw new SchemaException("Eyepiece must have exact one model name. ");
        }
        Element child = (Element) children.item(0);
        StringBuilder model = new StringBuilder();
        if (child == null) {
            throw new SchemaException("Eyepiece must have a model name. ");
        } else {
            if (child.getFirstChild() != null) {
                NodeList textElements = child.getChildNodes();
                if (textElements.getLength() > 0) {
                    for (int te = 0; te < textElements.getLength(); te++) {
                        model.append(textElements.item(te).getNodeValue());
                    }
                    return model.toString().trim();
                }
            } else {
                throw new SchemaException("Eyepiece cannot have an empty model name. ");
            }
        }
        return "";
    }
}
