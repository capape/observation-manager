/* ====================================================================
 * /IObserver.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

/**
 * An IObserver describes person, who does astronomical observations.<br>
 * The IObserver interface provides access to at least the name and surname of
 * the person. Additionally address informations may be stored here.
 * 
 * @author doergn@users.sourceforge.net
 * @since 1.0
 */
public interface IObserver extends ISchemaElement {

    // ---------
    // Constants ---------------------------------------------------------
    // ---------

    /**
     * Constant for XML representation: IObserver element name.<br>
     * Example:<br>
     * &lt;observer&gt;<i>More stuff goes here</i>&lt;/observer&gt;
     */
    public static final String XML_ELEMENT_OBSERVER = "observer";

    /**
     * Constant for XML representation: Observers name element name.<br>
     * Example:<br>
     * &lt;observer&gt; <br>
     * <i>More stuff goes here</i>
     * &lt;name&gt;<code>Observer name goes here</code>&lt;/name&gt; <i>More stuff
     * goes here</i> &lt;/observer&gt;
     */
    public static final String XML_ELEMENT_NAME = "name";

    /**
     * Constant for XML representation: Observers surname element name.<br>
     * Example:<br>
     * &lt;observer&gt; <br>
     * <i>More stuff goes here</i>
     * &lt;surname&gt;<code>Observer surname goes here</code>&lt;/surname&gt;
     * <i>More stuff goes here</i> &lt;/observer&gt;
     */
    public static final String XML_ELEMENT_SURNAME = "surname";

    /**
     * Constant for XML representation: Observers contact element name.<br>
     * Example:<br>
     * &lt;observer&gt; <br>
     * <i>More stuff goes here</i>
     * &lt;contact&gt;<code>Observer contact data goes here</code>&lt;/contact&gt;
     * <i>More stuff goes here</i> &lt;/observer&gt;
     */
    public static final String XML_ELEMENT_CONTACT = "contact";

    /**
     * Constant for XML representation: Observers DeepSkyList (DSL) code.<br>
     * Example:<br>
     * &lt;observer&gt; <br>
     * <i>More stuff goes here</i>
     * &lt;DSL&gt;<code>Observer DSL code goes here</code>&lt;/DSL&gt; <i>More stuff
     * goes here</i> &lt;/observer&gt;
     * 
     * @deprecated Use XML_ELEMENT_ACCOUNT/XML_ATTRIBUTE_ACCOUNT_NAME instead
     */
    @Deprecated
    public static final String XML_ELEMENT_DSL = "DSL";

    /**
     * Constant for XML representation: External account element.<br>
     * Example:<br>
     * &lt;observer&gt; <br>
     * <i>More stuff goes here</i> &lt;account
     * name="DSL"&gt;<code>Observer DSL code goes here</code>&lt;/account&gt;
     * &lt;account
     * name="AAVSO"&gt;<code>Observer AAVSO ID goes here</code>&lt;/account&gt;
     * <i>More stuff goes here</i> &lt;/observer&gt;
     * 
     * @since 2.0
     */
    public static final String XML_ELEMENT_ACCOUNT = "account";

    /**
     * Constant for XML representation: External account name.<br>
     * Example:<br>
     * &lt;observer&gt; <br>
     * <i>More stuff goes here</i> &lt;account
     * name="DSL"&gt;<code>Observer DSL code goes here</code>&lt;/account&gt;
     * &lt;account
     * name="AAVSO"&gt;<code>Observer AAVSO ID goes here</code>&lt;/account&gt;
     * <i>More stuff goes here</i> &lt;/observer&gt;
     * 
     * @since 2.0
     */
    public static final String XML_ATTRIBUTE_ACCOUNT_NAME = "name";

    /**
     * Constant for XML representation: Observers personal fst offset.<br>
     * Example:<br>
     * &lt;observer&gt; <br>
     * <i>More stuff goes here</i>
     * &lt;fstOffset&gt;<code>Observer fst value goes here</code>&lt;/fstOffset&gt;
     * <i>More stuff goes here</i> &lt;/observer&gt;
     */
    public static final String XML_ELEMENT_FST_OFFSET = "fstOffset";

    // --------------
    // Public Methods ----------------------------------------------------
    // --------------

    // -------------------------------------------------------------------
    /**
     * Adds this Observer to a given parent XML DOM Element. The Observer element
     * will be set as a child element of the passed element.
     * 
     * @param parent The parent element for this Observer
     * @return Returns the element given as parameter with this Observer as child
     *         element.<br>
     *         Might return <code>null</code> if parent was <code>null</code>.
     * @see org.w3c.dom.Element
     */
    public Element addToXmlElement(Element element);

    // -------------------------------------------------------------------
    /**
     * Adds a Observer link to an given XML DOM Element. The Observer element itself
     * will be attached to given elements ownerDocument if the passed boolean is
     * <code>true</code>. If the ownerDocument has no observer container, it will be
     * created (in case the passed boolean was <code>true</code>.<br>
     * It might look a little odd that observers addAsLinkToXmlElement() method
     * takes two parameters, but it is nessary as IObserver is once used as
     * <coObserver> (under <session>) and used as <observer> under other elements.
     * This is why the name of the link element has to be specified. The link
     * element will be created under the passed parameter element. Example:<br>
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
     * @param element               The element at which the Observer link will be
     *                              created.
     * @param nameOfLinkElement     The name of the link element, which is set under
     *                              the passed element
     * @param addElementToContainer if <code>true</code> it's ensured that the
     *                              linked element exists in the corresponding
     *                              container element. Please note, passing
     *                              <code>true</code> slowes down XML serialization.
     * @return Returns the Element given as parameter with the Observer as linked
     *         child element, and the elements ownerDocument with the additional
     *         Observer element Might return <code>null</code> if element was
     *         <code>null</code>.
     * @see org.w3c.dom.Element
     * @since 2.0
     */
    public org.w3c.dom.Element addAsLinkToXmlElement(org.w3c.dom.Element parent, String NameOfLinkElement,
            boolean addElementToContainer);

    // -------------------------------------------------------------------
    /**
     * Adds the observer link to an given XML DOM Element The observer element
     * itself will <b>NOT</b> be attached to given elements ownerDocument. Calling
     * this method is equal to calling <code>addAsLinkToXmlElement</code> with
     * parameters <code>element, nameOfLinkElement, false</code><br>
     * Example:<br>
     * &lt;parameterElement&gt;<br>
     * <b>&lt;observerLink&gt;123&lt;/observerLink&gt;</b><br>
     * &lt;/parameterElement&gt;<br>
     * <br>
     * 
     * @param element           The element under which the the observer link is
     *                          created
     * @param NameOfLinkElement The name of the link element, which is set under the
     *                          passed element
     * @return Returns the Element given as parameter with a additional observer
     *         link Might return <code>null</code> if element was <code>null</code>.
     * @see org.w3c.dom.Element
     */
    public Element addAsLinkToXmlElement(Element element, String nameOfLinkElement);

    // -------------------------------------------------------------------
    /**
     * Returns a List with contact information of the observer<br>
     * The returned List may contain e-Mail address, phone number, fax number,
     * postal adress, webpage....whatever. No garantee is given what the list
     * should/may contain, or in which order the elements are placed.<br>
     * If no contact informations where given, the method might return
     * <code>null</code>
     * 
     * @return a List with contact information of the observer, or <code>null</code>
     *         if no informations are given.
     */
    public java.util.List getContacts();

    // -------------------------------------------------------------------
    /**
     * Returns a Map with external account information of the observer<br>
     * The returned Map contains external service/website/organisation names etc. as
     * key values. The corresponding values are usernames/userid, accountnames,
     * membernumbers, etc. identifing this oberver on an external
     * site/service/organisation. If no additional account informations where given,
     * the method might return <code>null</code>
     * 
     * @return a Map with additional account information of the observer, or
     *         <code>null</code> if no informations are given.
     * @since 2.0
     */
    public java.util.Map getAccounts();

    // -------------------------------------------------------------------
    /**
     * Returns the name of the observer<br>
     * The name (and the surname) are the only mandatory fields this interface
     * requires.
     * 
     * @return the name of the observer
     */
    public String getName();

    /**
     * Returns the surname of the observer<br>
     * The surname (and the name) are the only mandatory fields this interface
     * requires.
     * 
     * @return the surname of the observer
     */
    public String getSurname();

    // -------------------------------------------------------------------
    /**
     * Returns the DeepSkyList (DSL) Code of the observer<br>
     * Might return <code>NULL</code> if observer has no DSL code
     * 
     * @return the DeepSkyList (DSL) Code of the observer, or <code>NULL</code> if
     *         DSL was never set
     * @deprecated Use getUsernameForAccount(String accountName) instead
     */
    @Deprecated
    public String getDSLCode();

    /**
     * Adds a new account information to the observer.<br>
     * If the account name does already exist, the existing value gets overwritten
     * with the passed new value.<br>
     * 
     * @param accountName the new account name (name of service, organisation,
     *                    website, ...)
     * @param username    the username/ID/User#/... to the new account
     * @return <b>true</b> if the new accout information could be added
     *         successfully. <b>false</b> if the new accout information could not be
     *         added.
     * @since 2.0
     */
    public boolean addAccount(String accountName, String username);

    // -------------------------------------------------------------------
    /**
     * Removes an existing account information from the observer.<br>
     * 
     * @param accountName the account name (name of service, organisation, website,
     *                    ...) to be removed
     * @return <b>true</b> if the accout information could be removed successfully.
     *         <b>false</b> if the accout information could not be removed.
     * @since 2.0
     */
    public boolean removeAccount(String accountName);

    // -------------------------------------------------------------------
    /**
     * Adds a new contact information to the observer.<br>
     * 
     * @param newContact the additional contact information
     * @return <b>true</b> if the new contact information could be added
     *         successfully. <b>false</b> if the new contact information could not
     *         be added.
     */
    public boolean addContact(String newContact);

    // -------------------------------------------------------------------
    /**
     * Sets the account information to the observer.<br>
     * All current accounts will be deleted!<br>
     * If you want to add a single account use addAccount(String, String)<br>
     * If <code>NULL</code> is passed, the all current accounts will be deleted.
     * 
     * @param newAccounts new list of account informations
     * @return <b>true</b> if the new account information could be set successfully.
     *         <b>false</b> if the new account information could not be set.
     * @since 2.0
     */
    public boolean setAccounts(Map newAccounts);

    // -------------------------------------------------------------------
    /**
     * Returns the username/ID/User#/... belonging to the passed accountName, or
     * <code>NULL</code> if the accountName wasn't set for this observer.<br>
     * 
     * @param accountName Name of service, organisation, website, ...
     * @return The username/ID/User#/... of this observer beloging to the passed
     *         accountName, or <code>NULL</code> if the accountName wasn't set for
     *         this observer.
     * @since 2.0
     */
    public String getUsernameForAccount(String accountName);

    // -------------------------------------------------------------------
    /**
     * Sets the contact information to the observer.<br>
     * All current contacts will be deleted!<br>
     * If you want to add a contact use addContact(String)<br>
     * 
     * @param newContacts new list of contact informations
     * @return <b>true</b> if the new contact information could be set successfully.
     *         <b>false</b> if the new contact information could not be set.
     */
    public boolean setContacts(List newContacts);

    // -------------------------------------------------------------------
    /**
     * Sets the DeepSkyList (DSL) Code of the observer<br>
     * 
     * @param DSLCode the DeepSkyList (DSL) Code of the observer
     * @deprecated Use addAccount(String accountName, String username) instead
     */
    @Deprecated
    public void setDSLCode(String DSLCode);

    /**
     * Sets a new name to the observer.<br>
     * As the name is mandatory it cannot be <code>null</code>
     * 
     * @param name the new name of the observer
     * @throws IllegalArgumentException if the given name is <code>null</code>
     */
    public void setName(String name) throws IllegalArgumentException;

    /**
     * Sets a new surname to the observer.<br>
     * As the surname is mandatory it cannot be <code>null</code>
     * 
     * @param surname the new surname of the observer
     * @throws IllegalArgumentException if the given surname is <code>null</code>
     */
    public void setSurname(String surname) throws IllegalArgumentException;

    /**
     * Sets a new fst offset to the observer.<br>
     * Float.NaN will clear the current set value.
     * 
     * @param fstOffset the new faintest star offset of the observer
     */
    public void setFSTOffset(float fstOffset);

    /**
     * Returns the fst Offset of this observer or <code>Float.NaN</code> if the
     * value was never set.<br>
     * Personal fst offset between the "reference" correlation of the sky quality
     * meter as it can be measured with an SQM and the estimated naked eye limiting
     * magnitude (fst) The individual observer's offset depends mainly on the visual
     * acuity of the observer. If the fstOffset is known, the sky quality may be
     * derived from faintestStar estimates by this observer. The "reference"
     * correlation used to convert between sky quality and fst was given by Bradley
     * Schaefer: fst = 5*(1.586-log(10^((21.568-BSB)/5)+1)) where BSB is the sky
     * quality (or background surface brightness) given in magnitudes per square
     * arcsecond
     * 
     * @return the fst Offset of the Observer or <code>Float.NaN</code> if the value
     *         was never set.
     * @since 2.0
     */
    public float getFSTOffset();

}
