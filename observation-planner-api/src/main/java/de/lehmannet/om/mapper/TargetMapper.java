package de.lehmannet.om.mapper;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.lehmannet.om.EquPosition;
import de.lehmannet.om.IObserver;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.util.SchemaException;

public class TargetMapper {

    public static String getMandatoryID(Element target) throws SchemaException {
        // Get mandatory ID
        String ID = target.getAttribute(ISchemaElement.XML_ELEMENT_ATTRIBUTE_ID);
        if ((ID != null) && ("".equals(ID.trim()))) {
            throw new SchemaException("Target must have a ID. ");
        }
        return ID;
    }
    
    public static String getMandatoryName(Element target) throws SchemaException {
        Element child;
        NodeList children;
        // Get mandatory name
        children = target.getElementsByTagName(ITarget.XML_ELEMENT_NAME);
        if ((children == null) || (children.getLength() != 1)) {
            throw new SchemaException("Target must have exact one name. ");
        }
        child = (Element) children.item(0);
        StringBuilder name = new StringBuilder();
        if (child == null) {
            throw new SchemaException("Target must have a name. ");
        } else {
            if (child.getFirstChild() != null) {
                // name = child.getFirstChild().getNodeValue(); // Get name from text node
                NodeList textElements = child.getChildNodes();
                if ((textElements != null) && (textElements.getLength() > 0)) {
                    for (int te = 0; te < textElements.getLength(); te++) {
                        name.append(textElements.item(te).getNodeValue());
                    }
                   return name.toString();
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
        if (children != null) {
            if (children.getLength() == 1) {
                Element child = (Element) children.item(0);
                // datasource = child.getFirstChild().getNodeValue();
                NodeList textElements = child.getChildNodes();
                if ((textElements != null) && (textElements.getLength() > 0)) {
                    StringBuilder datasource = new StringBuilder();
                    for (int te = 0; te < textElements.getLength(); te++) {
                        datasource.append(textElements.item(te).getNodeValue());
                    }
                    return datasource.toString();
                }
            } else if (children.getLength() > 1) {
                throw new SchemaException("Target can only have one datasource entry. ");
            }
        }
        return null;
    }
    
    public static IObserver getOptionalObserver(Element target, String datasource, IObserver... observers) throws SchemaException {
        Element child;
        NodeList children;
        children = target.getElementsByTagName(IObserver.XML_ELEMENT_OBSERVER);
        if (children != null) {
            if ((children.getLength() == 1) && (datasource == null)) {
                child = (Element) children.item(0);
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
        } else if (datasource == null) {
            throw new SchemaException("Target must have datasource or observer specified as target origin. ");
        }
        return null;
    }
    
    public static List<String> getOptionalAliasNames(Element target) {
        Element child;
        NodeList children;
        // Get optional alias names
        List<String> result = new ArrayList<>();
        children = target.getElementsByTagName(ITarget.XML_ELEMENT_ALIASNAME);
        if ((children != null) && (children.getLength() >= 1)) {
            for (int j = 0; j < children.getLength(); j++) {
                child = (Element) children.item(j);
                StringBuilder aliasNameEntry = new StringBuilder();
                NodeList textElements = child.getChildNodes();
                if ((textElements != null) && (textElements.getLength() > 0)) {
                    for (int te = 0; te < textElements.getLength(); te++) {
                        aliasNameEntry.append(textElements.item(te).getNodeValue());
                    }
                    result.add(aliasNameEntry.toString());
                }
                // this.addAliasName(children.item(j).getFirstChild().getNodeValue());
            }
        }
        return result;
    }
    
    public static EquPosition getOptionalPosition(Element target, String name) throws SchemaException {
        NodeList children;
        // Get optional position
        children = target.getElementsByTagName(EquPosition.XML_ELEMENT_POSITION);
      
        if (children != null) {
            if (children.getLength() == 1) {
                try {
                    return new EquPosition(children.item(0));
                } catch (SchemaException schema) {
                    throw new SchemaException("Target cannot set position from element: " + name, schema);
                }
                
            } else if (children.getLength() > 1) {
                throw new SchemaException("Target can only have one position. ");
            }
        }
        return null;
    }
    
    public static String getOptionalConstellation(Element target) throws SchemaException {
        Element child;
        NodeList children;
        // Get optional constellation
        children = target.getElementsByTagName(ITarget.XML_ELEMENT_CONSTELLATION);
        StringBuilder constellation = new StringBuilder();
        if (children != null) {
            if (children.getLength() == 1) {
                child = (Element) children.item(0);
                // constellation = child.getFirstChild().getNodeValue();
                NodeList textElements = child.getChildNodes();
                if ((textElements != null) && (textElements.getLength() > 0)) {
                    for (int te = 0; te < textElements.getLength(); te++) {
                        constellation.append(textElements.item(te).getNodeValue());
                    }
                    return constellation.toString();
                }
            } else if (children.getLength() > 1) {
                throw new SchemaException("Target can only have one constellation. ");
            }
        
        }
        return null;
    }
    
    public static String getOptionalNotes(Element target) throws SchemaException {
        Element child;
        NodeList children;
        // Get optional notes
        children = target.getElementsByTagName(ITarget.XML_ELEMENT_NOTES);
        StringBuilder notes = new StringBuilder();
        if (children != null) {
            if (children.getLength() == 1) {
                child = (Element) children.item(0);
                NodeList textElements = child.getChildNodes();
                if ((textElements != null) && (textElements.getLength() > 0)) {
                    for (int te = 0; te < textElements.getLength(); te++) {
                        notes.append(textElements.item(te).getNodeValue());
                    }
                    return notes.toString();
                }
            } else if (children.getLength() > 1) {
                throw new SchemaException("Target can only have one notes element. ");
            }
        }
        return null;
    }
    
}