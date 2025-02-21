package de.lehmannet.om.mapper;

import de.lehmannet.om.EquPosition;
import de.lehmannet.om.IObserver;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.util.SchemaException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class TargetMapper {

    public static String getMandatoryID(Element target) throws SchemaException {
        // Get mandatory ID
        String ID = target.getAttribute(ISchemaElement.XML_ELEMENT_ATTRIBUTE_ID);
        if (StringUtils.isBlank(ID)) {
            throw new SchemaException("Target must have a ID. ");
        }
        return ID;
    }

    public static String getMandatoryName(Element target) throws SchemaException {

        // Get mandatory name
        NodeList children = target.getElementsByTagName(ITarget.XML_ELEMENT_NAME);
        if (children.getLength() != 1) {
            throw new SchemaException("Target must have exact one name. ");
        }
        Element child = (Element) children.item(0);
        if (child == null) {
            throw new SchemaException("Target must have a name. ");
        } else {
            if (child.getFirstChild() != null) {
                StringBuilder name = new StringBuilder();
                NodeList textElements = child.getChildNodes();
                if (textElements.getLength() > 0) {
                    for (int te = 0; te < textElements.getLength(); te++) {
                        name.append(textElements.item(te).getNodeValue());
                    }
                    return name.toString().trim();
                }
            } else {
                throw new SchemaException("Target cannot have an empty name. ");
            }

            // this.setName(name);
        }
        return "";
    }

    public static String getDatasource(Element target) throws SchemaException {

        NodeList children = target.getElementsByTagName(ITarget.XML_ELEMENT_DATASOURCE);

        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            NodeList textElements = child.getChildNodes();
            if (textElements.getLength() > 0) {
                StringBuilder datasource = new StringBuilder();
                for (int te = 0; te < textElements.getLength(); te++) {
                    datasource.append(textElements.item(te).getNodeValue());
                }
                return datasource.toString().trim();
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("Target can only have one datasource entry. ");
        }

        return null;
    }

    public static IObserver getOptionalObserver(Element target, String datasource, IObserver... observers)
            throws SchemaException {

        NodeList children = target.getElementsByTagName(IObserver.XML_ELEMENT_OBSERVER);

        if ((children.getLength() == 1) && (datasource == null)) {
            Element child = (Element) children.item(0);
            String observerID = child.getFirstChild().getNodeValue();

            if ((observers != null) && (observers.length > 0)) {
                // Check if observer exists
                boolean found = false;
                for (IObserver iObserver : observers) {
                    if (iObserver.getID().equals(observerID)) {
                        found = true;
                        return iObserver;
                    }
                }
                if (!found) {
                    throw new SchemaException("Target observer links to not existing observer element. ");
                }
            } else {
                throw new SchemaException(
                        "Passed IObserver array is empty or NULL. As no datasource is given, this is invalid. ");
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("Target can only have one observer entry. ");
        } else if (datasource == null) {
            throw new SchemaException("Target can only have a observer entry or a datasource entry. ");
        }

        return null;
    }

    public static List<String> getOptionalAliasNames(Element target) {

        // Get optional alias names
        List<String> result = new ArrayList<>();
        NodeList children = target.getElementsByTagName(ITarget.XML_ELEMENT_ALIASNAME);
        if (children.getLength() >= 1) {
            for (int j = 0; j < children.getLength(); j++) {
                Element child = (Element) children.item(j);
                StringBuilder aliasNameEntry = new StringBuilder();
                NodeList textElements = child.getChildNodes();
                if ((textElements != null) && (textElements.getLength() > 0)) {
                    for (int te = 0; te < textElements.getLength(); te++) {
                        aliasNameEntry.append(textElements.item(te).getNodeValue());
                    }
                    result.add(aliasNameEntry.toString().trim());
                }
                // this.addAliasName(children.item(j).getFirstChild().getNodeValue());
            }
        }
        return result;
    }

    public static EquPosition getOptionalPosition(Element target, String name) throws SchemaException {

        // Get optional position
        NodeList children = target.getElementsByTagName(EquPosition.XML_ELEMENT_POSITION);

        if (children.getLength() == 1) {
            try {
                if (children.item(0) == null) {
                    return null;
                }
                return new EquPosition(children.item(0));
            } catch (SchemaException schema) {
                throw new SchemaException("Target cannot set position from element: " + name, schema);
            }

        } else if (children.getLength() > 1) {
            throw new SchemaException("Target can only have one position. ");
        }

        return null;
    }

    public static String getOptionalConstellation(Element target) throws SchemaException {

        // Get optional constellation
        NodeList children = target.getElementsByTagName(ITarget.XML_ELEMENT_CONSTELLATION);
        StringBuilder constellation = new StringBuilder();

        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            // constellation = child.getFirstChild().getNodeValue();
            NodeList textElements = child.getChildNodes();
            if ((textElements != null) && (textElements.getLength() > 0)) {
                for (int te = 0; te < textElements.getLength(); te++) {
                    constellation.append(textElements.item(te).getNodeValue());
                }
                return constellation.toString().trim();
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("Target can only have one constellation. ");
        }

        return null;
    }

    public static String getOptionalNotes(Element target) throws SchemaException {

        // Get optional notes
        NodeList children = target.getElementsByTagName(ITarget.XML_ELEMENT_NOTES);
        StringBuilder notes = new StringBuilder();
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            NodeList textElements = child.getChildNodes();
            if ((textElements != null) && (textElements.getLength() > 0)) {
                for (int te = 0; te < textElements.getLength(); te++) {
                    notes.append(textElements.item(te).getNodeValue());
                }
                return notes.toString().trim();
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("Target can only have one notes element. ");
        }
        return null;
    }
}
