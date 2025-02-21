package de.lehmannet.om.mapper;

import de.lehmannet.om.IObserver;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.util.SchemaException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

public class ObserverMapper {

    private static final Logger log = LoggerFactory.getLogger(ObserverMapper.class);

    public static Map<String, String> getOptionalAccounts(Element observerElement) throws SchemaException {

        // Get optional accounts

        NodeList children = observerElement.getElementsByTagName(IObserver.XML_ELEMENT_ACCOUNT);
        Map<String, String> accountsElement = new HashMap<>();
        for (int x = 0; x < children.getLength(); x++) {
            Element child = (Element) children.item(x);
            if (child != null) {
                String accountName =
                        child.getAttribute(IObserver.XML_ATTRIBUTE_ACCOUNT_NAME).trim();
                StringBuilder accountID = new StringBuilder(); // child.getFirstChild().getNodeValue();
                NodeList textElements = child.getChildNodes();
                if (textElements.getLength() > 0) {
                    for (int te = 0; te < textElements.getLength(); te++) {
                        accountID.append(textElements.item(te).getNodeValue());
                    }
                    accountsElement.put(accountName, accountID.toString().trim());
                }
            } else {
                throw new SchemaException(
                        "Problem retrieving account information from Observer. " + getMandatoryID(observerElement));
            }
        }

        return accountsElement;
    }

    public static float getOptionalFstOffset(Element observerElement) throws SchemaException {

        // Get optional fstOffset

        NodeList children = observerElement.getElementsByTagName(IObserver.XML_ELEMENT_FST_OFFSET);
        StringBuilder fstOffset = new StringBuilder();

        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            if (child != null) {
                NodeList textElements = child.getChildNodes();
                if (textElements.getLength() > 0) {
                    for (int te = 0; te < textElements.getLength(); te++) {
                        fstOffset.append(textElements.item(te).getNodeValue());
                    }
                    return Float.parseFloat(fstOffset.toString());
                }
            } else {
                log.error("Problem while retrieving fst Offset from observer: {} ", getMandatoryID(observerElement));
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("Observer can have only one fst Offset. ");
        }

        return Float.NaN;
    }

    public static String getOptionalDSL(Element observerElement) throws SchemaException {

        NodeList children = observerElement.getElementsByTagName(IObserver.XML_ELEMENT_DSL);
        StringBuilder DSLCode = new StringBuilder();
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            if (child != null) {
                // DSLCode = child.getFirstChild().getNodeValue();
                NodeList textElements = child.getChildNodes();
                if (textElements.getLength() > 0) {
                    for (int te = 0; te < textElements.getLength(); te++) {
                        DSLCode.append(textElements.item(te).getNodeValue());
                    }
                    return DSLCode.toString().trim();
                }
            } else {
                log.error(
                        "Problem while retrieving DSL code from observer: {}. As this element is deprecated, error will be ignored.",
                        getMandatoryID(observerElement));
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("Observer can have only one DSL Code. ");
        }

        return "";
    }

    public static List<String> getOptionalContacts(Element observerElement) throws SchemaException {

        // Get optional contacts

        NodeList children = observerElement.getElementsByTagName(IObserver.XML_ELEMENT_CONTACT);
        List<String> contacts = new ArrayList<>();
        for (int x = 0; x < children.getLength(); x++) {
            Element child = (Element) children.item(x);
            if (child != null) {
                StringBuilder contactEntry = new StringBuilder();
                NodeList textElements = child.getChildNodes();
                if (textElements.getLength() > 0) {
                    for (int te = 0; te < textElements.getLength(); te++) {
                        contactEntry.append(textElements.item(te).getNodeValue());
                    }
                    contacts.add(contactEntry.toString().trim());
                }
            } else {
                throw new SchemaException("Problem retrieving contact information from Observer. ");
            }
        }

        return contacts;
    }

    public static String getMandatorySurname(Element observerElement) throws SchemaException {

        // Get mandatory surname

        NodeList children = observerElement.getElementsByTagName(IObserver.XML_ELEMENT_SURNAME);
        if (children.getLength() != 1) {
            throw new SchemaException("Observer must have exact one surname. ");
        }
        Element child = (Element) children.item(0);
        StringBuilder surname = new StringBuilder();
        if (child == null) {
            throw new SchemaException("Observer must have a surname. ");
        } else {
            // surname = child.getFirstChild().getNodeValue();
            NodeList textElements = child.getChildNodes();
            if ((textElements != null) && (textElements.getLength() > 0)) {
                for (int te = 0; te < textElements.getLength(); te++) {
                    surname.append(textElements.item(te).getNodeValue());
                }
                return surname.toString().trim();
            }
        }
        return "";
    }

    public static String getMandatoryName(Element observerElement) throws SchemaException {

        // Get mandatory name
        NodeList children = observerElement.getElementsByTagName(IObserver.XML_ELEMENT_NAME);
        if (children.getLength() != 1) {
            throw new SchemaException("Observer must have exact one name. ");
        }
        Element child = (Element) children.item(0);
        StringBuilder name = new StringBuilder();
        if (child == null) {
            throw new SchemaException("Observer must have a name. ");
        } else {
            if (child.getFirstChild() != null) {
                // name = child.getFirstChild().getNodeValue();
                NodeList textElements = child.getChildNodes();
                if ((textElements != null) && (textElements.getLength() > 0)) {
                    for (int te = 0; te < textElements.getLength(); te++) {
                        name.append(textElements.item(te).getNodeValue());
                    }
                    return name.toString().trim();
                }
            } else {
                // Some applications (like DSP) don't set a name, which is OK with OAL
                return "";
                // throw new SchemaException("Observer cannot have a empty name. ");
            }
        }
        return "";
    }

    public static String getMandatoryID(Element observerElement) throws SchemaException {
        // Get ID from element
        NamedNodeMap attributes = observerElement.getAttributes();
        if (attributes.getLength() == 0) {
            throw new SchemaException("Observer must have a unique ID. ");
        }
        String ID = observerElement.getAttribute(ISchemaElement.XML_ELEMENT_ATTRIBUTE_ID);
        return ID;
    }
}
