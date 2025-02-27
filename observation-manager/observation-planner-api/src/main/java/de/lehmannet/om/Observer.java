/*
 * ====================================================================
 * /Observer.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om;

import de.lehmannet.om.mapper.ObserverMapper;
import de.lehmannet.om.util.SchemaException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * An Observer describes person, who does astronomical observations.<br>
 * The Observer class provides access to at least the name and surname of the person. Additionally address informations
 * may be stored here.
 *
 * @author doergn@users.sourceforge.net
 * @since 1.0
 */
public class Observer extends SchemaElement implements IObserver {

    private static Logger log = LoggerFactory.getLogger(Observer.class);

    // ---------
    // Constants ---------------------------------------------------------
    // ---------

    // Account identifier for german Deep-Sky List (http://www.deepskyliste.de/)
    public static final String ACCOUNT_DSL = "www.deepskyliste.de";

    // Account identifier for AAVSO (http://www.aavso.org/)
    public static final String ACCOUNT_AAVSO = "www.aavso.org";

    // Account identifier for DeepSky Log website (http://www.deepskylog.org)
    public static final String ACCOUNT_DEEPSKYLOG = "www.deepskylog.org";

    // ------------------
    // Instance Variables ------------------------------------------------
    // ------------------

    // The observers name
    private String name = "";

    // The observers surname
    private String surname = "";

    // The sites latitude in degrees
    private List<String> contacts = new LinkedList<>();

    // Usernames/UserIDs/accountNames of the observer in external
    // applications/websites
    private Map<String, String> accounts = new HashMap<>();

    // Personal fst Offset of the observer
    private float fstOffset = Float.NaN;

    // ------------
    // Constructors ------------------------------------------------------
    // ------------

    /**
     * Constructs a new Observer instance from a given XML Schema Node. Normally this constructor is only used by
     * de.lehmannet.om.util.SchemaLoader
     *
     * @param observer
     *            The XML Schema Node that represents this Observer object
     * @throws IllegalArgumentException
     *             if the given parameter is <code>null</code>
     * @throws SchemaException
     *             if the given Node does not match the XML Schema specifications
     */
    public Observer(Node observer) throws SchemaException, IllegalArgumentException {

        if (observer == null) {
            throw new IllegalArgumentException("Parameter observer node cannot be NULL. ");
        }

        // Cast to element as we need some methods from it
        Element observerElement = (Element) observer;

        this.setID(ObserverMapper.getMandatoryID(observerElement));
        this.setName(ObserverMapper.getMandatoryName(observerElement));
        this.setSurname(ObserverMapper.getMandatorySurname(observerElement));
        this.setContacts(ObserverMapper.getOptionalContacts(observerElement));
        this.setDSLCode(ObserverMapper.getOptionalDSL(observerElement));
        final String dslCode = this.getDSLCode();
        if (!StringUtils.isBlank(dslCode)) {
            this.accounts.put(ACCOUNT_DSL, dslCode);
        }

        this.setFSTOffset(ObserverMapper.getOptionalFstOffset(observerElement));
        this.accounts.putAll(ObserverMapper.getOptionalAccounts(observerElement));
    }

    /**
     * Constructs a new instance of an Observer.
     *
     * @param name
     *            The observers name
     * @param surname
     *            The observers surname
     * @throws IllegalArgumentException
     *             if one of the given parameters is <code>null</code>
     */
    public Observer(String name, String surname) throws IllegalArgumentException {

        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null. ");
        }
        this.name = name;

        if (surname == null) {
            throw new IllegalArgumentException("Surname cannot be null. ");
        }
        this.surname = surname;
    }

    // -------------
    // SchemaElement -----------------------------------------------------
    // -------------

    /**
     * Returns a display name for this element.<br>
     * The method differs from the toString() method as toString() shows more technical information about the element.
     * Also the formating of toString() can spread over several lines.<br>
     * This method returns a string (in one line) that can be used as displayname in e.g. a UI dropdown box.
     *
     * @return Returns a String with a one line display name
     */
    @Override
    public String getDisplayName() {

        return this.getSurname() + ", " + this.getName();
    }

    // ------
    // Object ------------------------------------------------------------
    // ------

    /**
     * Overwrittes toString() method from java.lang.Object.<br>
     * Returns the name, surname and contact informations of this observer.
     *
     * @return This observers name, surname and contact informations
     * @see java.lang.Object
     */
    @Override
    public String toString() {

        StringBuilder buffer = new StringBuilder();
        buffer.append("Observer: Name=");
        buffer.append(name);

        buffer.append(" Surname=");
        buffer.append(surname);

        buffer.append(" Contacts=");
        if ((contacts != null) && (!contacts.isEmpty())) {
            ListIterator<String> iterator = contacts.listIterator();
            while (iterator.hasNext()) {

                buffer.append(iterator.next());

                if (iterator.hasNext()) {
                    buffer.append(" --- ");
                }
            }
        }

        buffer.append(" Accounts=");
        if ((accounts != null) && (!accounts.isEmpty())) {

            Iterator<Entry<String, String>> iterator = accounts.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, String> entry = iterator.next();
                buffer.append(entry.getKey()).append(": ").append(entry.getValue());
                if (iterator.hasNext()) {
                    buffer.append(" --- ");
                }
            }
        }

        buffer.append(" fstOffset=");
        buffer.append(this.fstOffset);

        return buffer.toString();
    }

    // ---------
    // IObserver ---------------------------------------------------------
    // ---------

    /**
     * Adds this Observer to a given parent XML DOM Element. The Observer element will be set as a child element of the
     * passed element.
     *
     * @param element
     *            The parent element for this Observer
     * @see org.w3c.dom.Element
     */
    @Override
    public void addToXmlElement(Element element) {

        if (element == null) {
            return;
        }

        Document ownerDoc = element.getOwnerDocument();

        // Check if this element doesn't exist so far
        NodeList nodeList = element.getElementsByTagName(IObserver.XML_ELEMENT_OBSERVER);
        if (nodeList.getLength() > 0) {
            Node currentNode = null;
            NamedNodeMap attributes = null;
            for (int i = 0; i < nodeList.getLength(); i++) { // iterate over all found nodes
                currentNode = nodeList.item(i);
                attributes = currentNode.getAttributes();
                Node idAttribute = attributes.getNamedItem(ISchemaElement.XML_ELEMENT_ATTRIBUTE_ID);
                if ((idAttribute != null) // if ID attribute is set and equals this objects ID, return existing element
                        && (idAttribute
                                .getNodeValue()
                                .trim()
                                .equals(this.getID().trim()))) {
                    return;
                }
            }
        }

        Element e_Observer = ownerDoc.createElement(XML_ELEMENT_OBSERVER);
        e_Observer.setAttribute(XML_ELEMENT_ATTRIBUTE_ID, this.getID());

        element.appendChild(e_Observer);

        Element e_Name = ownerDoc.createElement(XML_ELEMENT_NAME);
        Node n_NameText = ownerDoc.createCDATASection(this.name);
        e_Name.appendChild(n_NameText);
        e_Observer.appendChild(e_Name);

        Element e_Surname = ownerDoc.createElement(XML_ELEMENT_SURNAME);
        Node n_SurnameText = ownerDoc.createCDATASection(this.surname);
        e_Surname.appendChild(n_SurnameText);
        e_Observer.appendChild(e_Surname);

        if ((contacts != null) && !(contacts.isEmpty())) {
            Element e_Contact = null;
            ListIterator<String> iterator = contacts.listIterator();
            String contact = null;
            while (iterator.hasNext()) {

                contact = (String) iterator.next();

                e_Contact = ownerDoc.createElement(XML_ELEMENT_CONTACT);
                Node n_ContactText = ownerDoc.createCDATASection(contact);
                e_Contact.appendChild(n_ContactText);
                e_Observer.appendChild(e_Contact);
            }
        }

        if ((accounts != null) && !(accounts.isEmpty())) {
            Element e_Account = null;

            Iterator<Entry<String, String>> iterator = accounts.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, String> entry = iterator.next();
                String accountKey = entry.getKey();
                String accountValue = this.accounts.get(accountKey);
                e_Account = ownerDoc.createElement(XML_ELEMENT_ACCOUNT);
                e_Account.setAttribute(IObserver.XML_ATTRIBUTE_ACCOUNT_NAME, accountKey);
                Node n_AccountText = ownerDoc.createCDATASection(accountValue);
                e_Account.appendChild(n_AccountText);
                e_Observer.appendChild(e_Account);
            }
        }

        if (!Float.isNaN(this.fstOffset)) {
            Element e_fstOffset = ownerDoc.createElement(XML_ELEMENT_FST_OFFSET);
            Node n_fstOffset = ownerDoc.createTextNode(Float.toString(this.fstOffset));
            e_fstOffset.appendChild(n_fstOffset);
            e_Observer.appendChild(e_fstOffset);
        }
    }

    /**
     * Adds a Observer link to an given XML DOM Element. The Observer element itself will be attached to given elements
     * ownerDocument if the passed boolean is <code>true</code>. If the ownerDocument has no observer container, it will
     * be created (in case the passed boolean was <code>true</code>.<br>
     * It might look a little odd that observers addAsLinkToXmlElement() method takes two parameters, but it is nessary
     * as IObserver is once used as <coObserver> (under <session>) and used as <observer> under other elements. This is
     * why the name of the link element has to be specified. The link element will be created under the passed parameter
     * element. Example:<br>
     * &lt;parameterElement&gt;<br>
     * <b>&lt;linkNameElement&gt;123&lt;/linkNameElement&gt;</b><br>
     * &lt;/parameterElement&gt;<br>
     * <i>More stuff of the xml document goes here</i><br>
     * <b>&lt;observerContainer&gt;</b><br>
     * <b>&lt;observer id="123"&gt;</b><br>
     * <i>Observer description goes here</i><br>
     * <b>&lt;/observer&gt;</b><br>
     * <b>&lt;/observerContainer&gt;</b><br>
     * <br>
     *
     * @param element
     *            The element at which the Observer link will be created.
     * @param nameOfLinkElement
     *            The name of the link element, which is set under the passed element
     * @param addElementToContainer
     *            if <code>true</code> it's ensured that the linked element exists in the corresponding container
     *            element. Please note, passing <code>true</code> slowes down XML serialization.
     * @return Returns the Element given as parameter with the Observer as linked child element, and the elements
     *         ownerDocument with the additional Observer element Might return <code>null</code> if element was
     *         <code>null</code>.
     * @see org.w3c.dom.Element
     * @since 2.0
     */
    @Override
    public Element addAsLinkToXmlElement(Element element, String nameOfLinkElement, boolean addElementToContainer) {

        if (element == null) {
            return null;
        }

        Document ownerDoc = element.getOwnerDocument();

        // Create the link element
        Element e_Link = ownerDoc.createElement(nameOfLinkElement);
        Node n_LinkText = ownerDoc.createTextNode(this.getID());
        e_Link.appendChild(n_LinkText);

        element.appendChild(e_Link);

        if (addElementToContainer) {
            // Get or create the container element
            Element e_Observers = null;
            NodeList nodeList = ownerDoc.getElementsByTagName(RootElement.XML_OBSERVER_CONTAINER);
            if (nodeList.getLength() == 0) { // we're the first element. Create container element
                e_Observers = ownerDoc.createElement(RootElement.XML_OBSERVER_CONTAINER);
                ownerDoc.getDocumentElement().appendChild(e_Observers);
            } else {
                e_Observers = (Element) nodeList.item(0); // there should be only one container element
            }

            this.addToXmlElement(e_Observers);
        }

        return element;
    }

    /**
     * Adds the observer link to an given XML DOM Element The observer element itself will <b>NOT</b> be attached to
     * given elements ownerDocument. Calling this method is equal to calling <code>addAsLinkToXmlElement</code> with
     * parameters <code>element, nameOfLinkElement, false</code><br>
     * Example:<br>
     * &lt;parameterElement&gt;<br>
     * <b>&lt;observerLink&gt;123&lt;/observerLink&gt;</b><br>
     * &lt;/parameterElement&gt;<br>
     * <br>
     *
     * @param element
     *            The element under which the the observer link is created
     * @return Returns the Element given as parameter with a additional observer link Might return <code>null</code> if
     *         element was <code>null</code>.
     * @see org.w3c.dom.Element
     */
    @Override
    public Element addAsLinkToXmlElement(Element element, String nameOfLinkElement) {

        return this.addAsLinkToXmlElement(element, nameOfLinkElement, false);
    }

    /**
     * Returns a List with contact information of the observer<br>
     * The returned List may contain e-Mail address, phone number, fax number, postal adress, webpage....whatever. No
     * garantee is given what the list should/may contain, or in which order the elements are placed.<br>
     * If no contact informations where given, the method might return <code>null</code>
     *
     * @return a List with contact information of the observer, or <code>null</code> if not informations are given.
     */
    @Override
    public List<String> getContacts() {

        return contacts;
    }

    /**
     * Adds a new contact information to the observer.<br>
     *
     * @param newContact
     *            the additional contact information
     */
    @Override
    public void addContact(String newContact) {

        if (newContact == null || "".equals(newContact)) {
            return;
        }

        this.contacts.add(newContact);
    }

    /**
     * Sets the contact information to the observer.<br>
     * All current contacts will be deleted!<br>
     * If you want to add a contact use addContact(String)<br>
     *
     * @param newContacts
     *            new list of contact informations
     */
    @Override
    public void setContacts(List<String> newContacts) {

        if (newContacts == null) {
            return;
        }

        this.contacts = new LinkedList<>(newContacts);
    }

    /**
     * Returns the name of the observer<br>
     * The name (and the surname) are the only mandatory fields this interface requires.
     *
     * @return the name of the observer
     */
    @Override
    public String getName() {

        return name;
    }

    /**
     * Returns the DeepSkyList (DSL) Code of the observer<br>
     * Might return <code>NULL</code> if observer has no DSL code
     *
     * @return the DeepSkyList (DSL) Code of the observer, or <code>NULL</code> if DSL was never set
     * @deprecated Use getUsernameForAccount(String accountName) instead
     */
    @Deprecated
    @Override
    public String getDSLCode() {

        return this.getUsernameForAccount(Observer.ACCOUNT_DSL);
    }

    /**
     * Returns the surname of the observer<br>
     * The surname (and the name) are the only mandatory fields this interface requires.
     *
     * @return the surname of the observer
     */
    @Override
    public String getSurname() {

        return surname;
    }

    /**
     * Sets the DeepSkyList (DSL) Code of the observer<br>
     *
     * @param DSLCode
     *            the DeepSkyList (DSL) Code of the observer
     * @deprecated Use addAccount(String accountName, String username) instead
     */
    @Deprecated
    @Override
    public void setDSLCode(String DSLCode) {

        this.addAccount(Observer.ACCOUNT_DSL, DSLCode);
    }

    /**
     * Sets a new name to the observer.<br>
     * As the name is mandatory it cannot be <code>null</code>
     *
     * @param name
     *            the new name of the observer
     * @throws IllegalArgumentException
     *             if the given name is <code>null</code>
     */
    @Override
    public void setName(String name) throws IllegalArgumentException {

        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null. ");
        }

        this.name = name;
    }

    /**
     * Sets a new surname to the observer.<br>
     * As the surname is mandatory it cannot be <code>null</code>
     *
     * @param surname
     *            the new surname of the observer
     * @throws IllegalArgumentException
     *             if the given surname is <code>null</code>
     */
    @Override
    public void setSurname(String surname) {

        if (surname == null) {
            throw new IllegalArgumentException("Surname cannot be null. ");
        }

        this.surname = surname;
    }

    /**
     * Returns a Map with external account information of the observer<br>
     * The returned Map contains external service/website/organisation names etc. as key values. The corresponding
     * values are usernames/userid, accountnames, membernumbers, etc. identifing this oberver on an external
     * site/service/organisation. If no additional account informations where given, the method might return
     * <code>null</code>
     *
     * @return a Map with additional account information of the observer, or <code>null</code> if no informations are
     *         given.
     * @since 2.0
     */
    @Override
    public Map<String, String> getAccounts() {

        return Collections.unmodifiableMap(this.accounts);
    }

    /**
     * Adds a new account information to the observer.<br>
     * If the account name does already exist, the existing value gets overwritten with the passed new value.<br>
     *
     * @param accountName
     *            the new account name (name of service, organisation, website, ...)
     * @param username
     *            the username/ID/User#/... to the new account
     * @return <b>true</b> if the new accout information could be added successfully. <b>false</b> if the new accout
     *         information could not be added.
     * @since 2.0
     */
    @Override
    public boolean addAccount(String accountName, String username) {

        if ((accountName == null)
                || ("".equals(accountName.trim()))
                || (username == null)
                || ("".equals(username.trim()))) {
            return false;
        }

        this.accounts.put(accountName, username);

        return true;
    }

    /**
     * Removes an existing account information from the observer.<br>
     *
     * @param accountName
     *            the account name (name of service, organisation, website, ...) to be removed
     * @return <b>true</b> if the accout information could be removed successfully. <b>false</b> if the accout
     *         information could not be removed.
     * @since 2.0
     */
    @Override
    public boolean removeAccount(String accountName) {

        this.accounts.remove(accountName);
        return true;
    }

    /**
     * Sets the account information to the observer.<br>
     * All current accounts will be deleted!<br>
     * If you want to add a single account use addAccount(String, String)<br>
     * If <code>NULL</code> is passed, the all current accounts will be deleted.
     *
     * @param newAccounts
     *            new list of account informations
     * @since 2.0
     */
    @Override
    public void setAccounts(Map<String, String> newAccounts) {

        if (newAccounts == null) {
            this.accounts = new HashMap<>();
        } else {
            this.accounts = new HashMap<>(newAccounts);
        }
    }

    /**
     * Returns the username/ID/User#/... belonging to the passed accountName, or <code>NULL</code> if the accountName
     * wasn't set for this observer.<br>
     *
     * @param accountName
     *            Name of service, organisation, website, ...
     * @return The username/ID/User#/... of this observer beloging to the passed accountName, or <code>NULL</code> if
     *         the accountName wasn't set for this observer.
     * @since 2.0
     */
    @Override
    public String getUsernameForAccount(String accountName) {

        return (String) this.accounts.get(accountName);
    }

    /**
     * Sets a new fst offset to the observer.<br>
     * Float.NaN will clear the current set value.
     *
     * @param fstOffset
     *            the new faintest star offset of the observer
     */
    @Override
    public void setFSTOffset(float fstOffset) {

        if (Float.isNaN(fstOffset)) {
            this.fstOffset = Float.NaN;
            return;
        }

        this.fstOffset = fstOffset;
    }

    /**
     * Returns the fst Offset of this observer or <code>Float.NaN</code> if the value was never set.<br>
     * Personal fst offset between the "reference" correlation of the sky quality meter as it can be measured with an
     * SQM and the estimated naked eye limiting magnitude (fst) The individual observer's offset depends mainly on the
     * visual acuity of the observer. If the fstOffset is known, the sky quality may be derived from faintestStar
     * estimates by this observer. The "reference" correlation used to convert between sky quality and fst was given by
     * Bradley Schaefer: fst = 5*(1.586-log(10^((21.568-BSB)/5)+1)) where BSB is the sky quality (or background surface
     * brightness) given in magnitudes per square arcsecond
     *
     * @return the fst Offset of the Observer or <code>Float.NaN</code> if the value was never set.
     * @since 2.0
     */
    @Override
    public float getFSTOffset() {

        return this.fstOffset;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((accounts == null) ? 0 : accounts.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((surname == null) ? 0 : surname.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        Observer other = (Observer) obj;
        if (accounts == null) {
            if (other.accounts != null) return false;
        } else if (!accounts.equals(other.accounts)) return false;
        if (name == null) {
            if (other.name != null) return false;
        } else if (!name.equals(other.name)) return false;
        if (surname == null) {
            if (other.surname != null) return false;
        } else if (!surname.equals(other.surname)) return false;
        return true;
    }
}
