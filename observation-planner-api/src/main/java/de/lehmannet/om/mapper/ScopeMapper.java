package de.lehmannet.om.mapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.lehmannet.om.Angle;
import de.lehmannet.om.IEquipment;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.IScope;
import de.lehmannet.om.util.FloatUtil;
import de.lehmannet.om.util.SchemaException;

public class ScopeMapper {

    public static Pair<Boolean, Boolean> getOptionalOrientation(Element scopeElement) throws SchemaException {

        // Get optional orientation

        NodeList children = scopeElement.getElementsByTagName(IScope.XML_ELEMENT_ORENTATION);
        String ori_Erect = null;
        String ori_Truesided = null;
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            if (child != null) {
                ori_Erect = child.getAttribute(IScope.XML_ELEMENT_ORENTATION_ATTRIBUTE_ERECT).trim().toLowerCase();
                ori_Truesided = child.getAttribute(IScope.XML_ELEMENT_ORENTATION_ATTRIBUTE_TRUESIDED).trim()
                        .toLowerCase();
                return Pair.of(Boolean.parseBoolean(ori_Erect), Boolean.parseBoolean(ori_Truesided));

            } else {
                throw new SchemaException("Problem while retrieving orientation element from scope. ");
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("Scope can have only one orientation. ");
        }

        return Pair.of(false, false);
    }

    public static float getOptionalLightGrasp(Element scopeElement) throws SchemaException {

        // Get optional lightGrasp

        NodeList children = scopeElement.getElementsByTagName(IScope.XML_ELEMENT_LIGHTGRASP);
        String lightGrasp = null;
            if (children.getLength() == 1) {
                Element child = (Element) children.item(0);
                if (child != null) {
                    lightGrasp = child.getFirstChild().getNodeValue();
                    return FloatUtil.parseFloat(lightGrasp);
                } else {
                    throw new SchemaException("Problem while retrieving light grasp from scope. ");
                }
            } else if (children.getLength() > 1) {
                throw new SchemaException("Scope can have only one light grasp. ");
            }
        return Float.NaN;
    }

    public static String getOptionalVendor(Element scopeElement) throws SchemaException {

        // Get optional vendor

        NodeList children = scopeElement.getElementsByTagName(IScope.XML_ELEMENT_VENDOR);
        StringBuilder vendor = new StringBuilder();
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            if (child != null) {
                // vendor = child.getFirstChild().getNodeValue();
                NodeList textElements = child.getChildNodes();
                if (textElements.getLength() > 0) {
                    for (int te = 0; te < textElements.getLength(); te++) {
                        vendor.append(textElements.item(te).getNodeValue());
                    }
                    return vendor.toString();
                }
            } else {
                throw new SchemaException("Problem while retrieving vendor from scope. ");
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("Scope can have only one vendor. ");
        }

        return null;
    }

    public static String getOptionalType(Element scopeElement) throws SchemaException {

        // Get optional type

        NodeList children = scopeElement.getElementsByTagName(IScope.XML_ELEMENT_TYPE);
        StringBuilder type = new StringBuilder();
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            if (child != null) {
                // type = child.getFirstChild().getNodeValue();
                NodeList textElements = child.getChildNodes();
                if (textElements.getLength() > 0) {
                    for (int te = 0; te < textElements.getLength(); te++) {
                        type.append(textElements.item(te).getNodeValue());
                    }
                    return type.toString();
                }
            } else {
                throw new SchemaException("Problem while retrieving type from scope. ");
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("Scope can have only one type. ");
        }

        return null;
    }

    public static float getMandatoryAperture(Element scopeElement) throws SchemaException {

        // Get mandatory aperture

        NodeList children = scopeElement.getElementsByTagName(IScope.XML_ELEMENT_APERTURE);
        if (children.getLength() != 1) {
            throw new SchemaException("Scope must have exact one aperture. ");
        }
        Element child = (Element) children.item(0);
        String aperture = null;
        if (child == null) {
            throw new SchemaException("Scope must have a aperture. ");
        } else {
            aperture = child.getFirstChild().getNodeValue();
            return FloatUtil.parseFloat(aperture);
        }
    }

    public static float getOptionalMagnification(Element scopeElement) throws SchemaException {
        String foc = ScopeMapper.getFocalLengthValueAsString(scopeElement);

        NodeList children = scopeElement.getElementsByTagName(IScope.XML_ELEMENT_MAGNIFICATION);
        String magnification = null;

        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            if (child != null) {
                if (foc != null) {
                    throw new SchemaException("Scope can only have a focalLength entry OR a magnification entry! ");
                }
                magnification = child.getFirstChild().getNodeValue();
                return FloatUtil.parseFloat(magnification);

            } else {
                throw new SchemaException("Problem while retrieving magnification from scope. ");
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("Scope can have only one magnification. ");
        }

        return Float.NaN;
    }

    public static Angle getOptionalTrueViewOfField(Element scopeElement) throws SchemaException {

        NodeList children = scopeElement.getElementsByTagName(IScope.XML_ELEMENT_TRUEFIELD);
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            if (child != null) {
                Angle trueField = new Angle(child);
                // Check whether true FOV is > 0
                // Do this with a copy of the retrieved Angle as otherwise the original object
                // will
                // change it's unit to ArcSec
                Angle checkAngle = new Angle(trueField.getValue(), trueField.getUnit());
                if (checkAngle.toArcSec() < 0) {
                    throw new SchemaException(
                            "Problem while retrieving true field of view from scope. Value cannot be nagative. ");
                }
                return trueField;
            } else {
                throw new SchemaException("Problem while retrieving true field of view from scope. ");
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("Scope can have only one true field of view. ");
        }

        return null;
    }

    public static String getFocalLengthValueAsString(Element scopeElement) {

        // Get optional magnification

        NodeList children = scopeElement.getElementsByTagName(IScope.XML_ELEMENT_FOCALLENGTH);
        String foc = null;
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            if (child != null) {
                foc = child.getFirstChild().getNodeValue();
            }
        }

        return foc;
    }

    public static float getOptionalFocalLength(Element scopeElement) throws SchemaException {

        // Get optional focalLength
        NodeList children = scopeElement.getElementsByTagName(IScope.XML_ELEMENT_FOCALLENGTH);

        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            if (child != null) {
                String focalLength = child.getFirstChild().getNodeValue();
                return FloatUtil.parseFloat(focalLength);
            } else {
                throw new SchemaException("Problem while retrieving focalLength from scope. ");
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("Scope can have only one focal length. ");
        }

        return Float.NaN;
    }

    public static String getMandatoryModel(Element scopeElement) throws SchemaException {

        // Get mandatory model
        NodeList children = scopeElement.getElementsByTagName(IScope.XML_ELEMENT_MODEL);
        if (children.getLength() != 1) {
            throw new SchemaException("Scope must have exact one model name. ");
        }
        Element child = (Element) children.item(0);
        StringBuilder model = new StringBuilder();
        if (child == null) {
            throw new SchemaException("Scope must have a model name. ");
        } else {
            // model = child.getFirstChild().getNodeValue();
            NodeList textElements = child.getChildNodes();
            if (textElements.getLength() > 0) {
                for (int te = 0; te < textElements.getLength(); te++) {
                    model.append(textElements.item(te).getNodeValue());
                }
                return model.toString();
            }
        }
        return "";
    }

    public static boolean getOptionalAvailability(Element scopeElement) {
        // Search for optional availability comment within nodes
        NodeList list = scopeElement.getChildNodes();
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

    public static String getMandatoryID(Element scopeElement) throws SchemaException {
        // Get ID from element
        String ID = scopeElement.getAttribute(ISchemaElement.XML_ELEMENT_ATTRIBUTE_ID);
        if (StringUtils.isBlank(ID)) {
            throw new SchemaException("DeepSkyTarget must have a ID. ");
        }
        return ID;
    }

}