/*
 * ====================================================================
 * /Target.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.lehmannet.om.mapper.TargetMapper;
import de.lehmannet.om.util.SchemaException;

/**
 * The abstract class Target provides some common features that may be used by the subclasses of an
 * de.lehmannet.om.ITarget.<br>
 * The Target class stores the name, alias names and the position of an astronomical object. It also provides some basic
 * access methods for these attributes. Additionally It implements a basic XML DOM helper method that may be used by all
 * subclasses that have to implement the ITarget interface.
 *
 * @author doergn@users.sourceforge.net
 * @since 1.0
 */
public abstract class Target extends SchemaElement implements ITarget {

    // ---------
    // Constants ---------------------------------------------------------
    // ---------

    // Delimiter for alias name tokens
    private static final String ALIASNAMES_DELIMITER = ",";

    // ------------------
    // Instance Variables ------------------------------------------------
    // ------------------

    // Name of the astronomical object
    private String name = "";

    // Alternative names of the astronomical object
    private final List<String> aliasNames = new LinkedList<>();

    // Celestial position of the astronomical object
    private EquPosition position = null;

    // Celestial constellation where the astronomical object can be found
    private Constellation constellation = null;

    // Datasource that is the origin of this targets data (e.g. catalogue)
    private String dataSource = null;

    // If no Datasource is given, a observer has to be named that created this
    // target
    private IObserver observer = null;

    // Any additional notes to the target (descriptions, historical results, etc.)
    private String notes = null;

    // ------------
    // Constructors ------------------------------------------------------
    // ------------

    /**
     * Constructs a new instance of a Target from a given DOM target Element.<br>
     * Normally this constructor is called by a child class which itself was called by
     * de.lehmannet.om.util.SchemaLoader. Please mind that Target has to have a <observer> element, or a <datasource>
     * element. If a <observer> element is set, a array with Observers must be passed to check, whether the <observer>
     * link is valid.
     *
     * @param observers
     *            Array of IObserver that might be linked from this observation, can be <code>NULL</code> if datasource
     *            element is set
     * @param targetElement
     *            The origin XML DOM <target> Element
     * @throws SchemaException
     *             if given targetElement was <code>null</code>
     */
    public Target(Node targetElement, IObserver... observers) throws SchemaException {

        if (targetElement == null) {
            throw new SchemaException("Given element is NULL. ");
        }

        Element target = (Element) targetElement;
        this.setID(TargetMapper.getMandatoryID(target));

        this.setName(TargetMapper.getMandatoryName(target));

        String datasource = TargetMapper.getDatasource(target);
        this.setDatasource(datasource);
        this.setObserver(TargetMapper.getOptionalObserver(target, datasource, observers));

        List<String> alias = TargetMapper.getOptionalAliasNames(target);
        this.setAliasNames(alias.toArray(new String[alias.size()]));
        this.setPosition(TargetMapper.getOptionalPosition(target, this.getName()));
        this.setConstellation(TargetMapper.getOptionalConstellation(target));
        this.setNotes(TargetMapper.getOptionalNotes(target));

    }

    /**
     * Protected Constructor used by subclasses construction.
     *
     * @param name
     *            The name of the astronomical object
     * @param datasource
     *            The datasource which is the origin of the astronomical object
     * @throws IllegalArgumentException
     *             if name or datasource was <code>null</code>
     */
    protected Target(String name, String datasource) throws IllegalArgumentException {

        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null. ");
        }

        if (datasource == null) {
            throw new IllegalArgumentException("Datasource cannot be null. ");
        }

        this.dataSource = datasource;

        this.setName(name);

    }

    /**
     * Protected Constructor used by subclasses construction.
     *
     * @param name
     *            The name of the astronomical object
     * @param observer
     *            The observer which is the originator of the astronomical object
     * @throws IllegalArgumentException
     *             if name or observer was <code>null</code>
     */
    protected Target(String name, IObserver observer) throws IllegalArgumentException {

        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null. ");
        }

        if (observer == null) {
            throw new IllegalArgumentException("Observer cannot be null. ");
        }

        this.observer = observer;

        this.setName(name);

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
     * @see java.lang.Object.toString();
     */
    @Override
    public String getDisplayName() {

        return this.name;

    }

    // ------
    // Object ------------------------------------------------------------
    // ------

    /**
     * Overwrittes equals(Object) method from java.lang.Object.<br>
     * Checks if this Target and the given Object are equal. The given object is equal with this Target, if it derives
     * from ITarget, both XSI types are equal, the datasource or observer is equal and its name is equal to this Target
     * name.<br>
     * Please note, that no checks on the alias names is done. So e.g. M13 and NGC6205 are NOT equal. (Even if they come
     * from the same catalog/datasource/observer)
     *
     * @param obj
     *            The Object to compare this Target with.
     * @return <code>true</code> if the given Object is an instance of ITarget, both XSI types are equal, the datasource
     *         or observer is equal and its name is equal to this Target name.<br>
     *         (Name comparism is <b>not</b> casesensitive)
     * @see java.lang.Object
     */
    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof ITarget)) {
            return false;
        }

        ITarget target = (ITarget) obj;

        // Check datasources/observer

        if ((this.getDatasource() != null) // Our datasource was set
                && !("".equals(this.getDatasource()))) {
            String dataSource = target.getDatasource();
            if (dataSource != null) {
                if (!StringUtils.equalsIgnoreCase(this.getDatasource().trim(), dataSource.trim())) {
                    return false; // Datasources do not match
                }
            } else {
                return false;
            }
        } else if (this.getObserver() != null) { // Our Observer was set
            IObserver observer = target.getObserver();
            if (!this.getObserver().equals(observer)) {
                return false; // Observers do not match
            }
        } else {
            return false; // No datasource and no observer set...something went completely wrong
        }

        // Check names

        String targetName = target.getName();
        if (targetName == null) {
            return false;
        }

        return (StringUtils.equalsIgnoreCase(this.getName(), targetName))
                && (this.getXSIType()).equals(target.getXSIType());

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((this.getName() == null) ? 0 : this.getName().hashCode());
        result = prime * result + ((this.getXSIType() == null) ? 0 : this.getXSIType().hashCode());
        return result;
    }

    // -------
    // ITarget -----------------------------------------------------------
    // -------

    /**
     * Returns true if the given object is an instance of ITarget<br>
     * and the given objects SchmemaElement.getID() returns the same string as this objects getID() method. This method
     * is required in the deletion of elements, and equal to the equals() method in all other SchemaElements (they do
     * only a check on the ID)
     *
     * @return Returns <b>true</b> if this Targets ID is equal to the given objects ID
     */
    @Override
    public boolean equalsID(Object o) {

        if (o instanceof ITarget) {
            return this.getID().equals(((ITarget) o).getID());
        }

        return false;

    }

    /**
     * Adds this Target to a given parent XML DOM Element. The Target element will be set as a child element of the
     * passed element.
     *
     * @param parent
     *            The parent element for this Target
     * @see org.w3c.dom.Element
     */
    @Override
    public abstract void addToXmlElement(Element element);

    /**
     * Adds the target link to an given XML DOM Element The target element itself will be attached to given elements
     * ownerDocument if the passed boolean was <code>true</code>. If the ownerDocument has no target container, it will
     * be created (in case the passed boolean was <code>true</code>).<br>
     * Example:<br>
     * &lt;parameterElement&gt;<br>
     * <b>&lt;targetLink&gt;123&lt;/targetLink&gt;</b><br>
     * &lt;/parameterElement&gt;<br>
     * <i>More stuff of the xml document goes here</i><br>
     * <b>&lt;targetContainer&gt;</b><br>
     * <b>&lt;target id="123"&gt;</b><br>
     * <i>target description goes here</i><br>
     * <b>&lt;/target&gt;</b><br>
     * <b>&lt;/targetContainer&gt;</b><br>
     * <br>
     *
     * @param pxmlElementName
     *            The name of the element that contains the link
     * @param addElementToContainer
     *            if <code>true</code> it's ensured that the linked element exists in the corresponding container
     *            element. Please note, passing <code>true</code> slowes down XML serialization.
     * @return Returns the Element given as parameter with a additional target link, and the target element under the
     *         target container of the ownerDocument Might return <code>null</code> if element was <code>null</code>.
     * @see org.w3c.dom.Element
     * @since 2.0
     */
    @Override
    public Element addAsLinkToXmlElement(Element element, String pxmlElementName, boolean addElementToContainer) {

        if (element == null) {
            return null;
        }

        String xmlElementName;
        if ((pxmlElementName == null) || ("".equals(pxmlElementName.trim()))) {
            xmlElementName = ITarget.XML_ELEMENT_TARGET;
        } else {
            xmlElementName = pxmlElementName;
        }

        Document ownerDoc = element.getOwnerDocument();

        // Create the link element
        Element e_Link = ownerDoc.createElement(xmlElementName);
        Node n_LinkText = ownerDoc.createTextNode(this.getID());
        e_Link.appendChild(n_LinkText);

        element.appendChild(e_Link);

        if (addElementToContainer) {
            // Get container element
            Element container = null;
            NodeList nodeList = ownerDoc.getElementsByTagName(RootElement.XML_TARGET_CONTAINER);
            if (nodeList.getLength() == 0) { // we're the first element. Create container element
                container = ownerDoc.createElement(RootElement.XML_TARGET_CONTAINER);
            } else {
                container = (Element) nodeList.item(0); // there should be only one container element
            }

            this.addToXmlElement(container);
        }

        return element;

    }

    /**
     * Adds the target link to an given XML DOM Element The target element itself will <b>NOT</b> be attached to given
     * elements ownerDocument. Calling this method is equal to calling <code>addAsLinkToXmlElement</code> with
     * parameters <code>element, false</code><br>
     * Example:<br>
     * &lt;parameterElement&gt;<br>
     * <b>&lt;targetLink&gt;123&lt;/targetLink&gt;</b><br>
     * &lt;/parameterElement&gt;<br>
     * <br>
     *
     * @param element
     *            The element under which the the target link is created
     * @param xmlElementName
     *            The name of the element that contains the link
     * @return Returns the Element given as parameter with a additional target link Might return <code>null</code> if
     *         element was <code>null</code>.
     * @see org.w3c.dom.Element
     */
    @Override
    public Element addAsLinkToXmlElement(Element element, String xmlElementName) {

        return this.addAsLinkToXmlElement(element, xmlElementName, false);

    }

    /**
     * Adds a new alias name to the Target e.g. name = M42 ; alias name = "Great Orion Nebulae"
     *
     * @param newAliasName
     *            A new alias name
     * @return Returns <code>true</code> if the alias name could be added. If <code>false</code> is returned the new
     *         alias was <code>null</code> or an empty String.
     */
    @Override
    public boolean addAliasName(String newAliasName) {

        if (newAliasName == null || "".equals(newAliasName)) {
            return false;
        }

        this.aliasNames.add(newAliasName);

        return true;

    }

    /**
     * Sets an array of new alias names to this target.<br>
     * All current aliasNames will be deleted! If you want to add alias names without deleting the existing ones, please
     * use Target.addAliasNames(String) or Target.addAliasName(String).<br>
     * If <code>null</code> is passed, the given alias names are deleted.
     *
     * @param newAliasNames
     *            An array with new alias name
     */
    @Override
    public void setAliasNames(String... newAliasNames) {

        if ((newAliasNames == null) || (newAliasNames.length == 0)) {
            this.aliasNames.clear();
            return;
        }

        this.aliasNames.clear();

        // Make sure only valid names can be set
        for (String newAliasName : newAliasNames) {
            if ((newAliasName == null) || (newAliasName.trim().equals(""))) {
                continue;
            }
            this.aliasNames.add(newAliasName);
        }

    }

    /**
     * Returns all alias names.<br>
     *
     * @return Returns a String array with all alias names. If no alias names were set <code>null</code> is returned.
     */
    @Override
    public String[] getAliasNames() {

        if (aliasNames.isEmpty()) {
            return new String[0];
        }

        return (String[]) this.aliasNames.toArray(new String[] {});
        /*
         * String[] result = new String[aliasNames.size()];
         *
         * int i = 0; ListIterator iterator = aliasNames.listIterator(); String next = null; while( iterator.hasNext() )
         * { next = (String)iterator.next();
         *
         * // Make aliasNames homogeneous // next = next.trim(); // next = next.toUpperCase(Locale.getDefault());
         *
         * result[i++] = next; }
         *
         * return result;
         */
    }

    /**
     * Removes a alias name from the target.<br>
     *
     * @param aliasName
     *            The alias name that should be removed
     * @return Returns <code>true</code> if the alias name could be removed from the target. If <code>false</code> is
     *         returned the given alias name could not be found in the targets alias name list or the parameter was
     *         <code>null<code> or contained a empty string.
     */
    @Override
    public boolean removeAliasName(String aliasName) {

        if (aliasName == null || "".equals(aliasName)) {
            return false;
        }

        if (this.aliasNames.contains(aliasName)) {
            aliasNames.remove(aliasName);
            return true;
        }

        return false;

    }

    /**
     * Returns the name of the target.<br>
     * The name should clearly identify the astronomical object. Use alias names for colloquial names of the object.
     *
     * @return Returns the name of the astronomical object
     */
    @Override
    public String getName() {

        // Make name homogeneous
        // String n = this.name.trim();
        // n = n.toUpperCase(Locale.getDefault());

        return this.name;

    }

    /**
     * Returns the celestial constellation, where the target can be found.<br>
     * Might return <code>NULL</code> if constellation was never set
     *
     * @return The celestial constellation
     */
    @Override
    public Constellation getConstellation() {

        return this.constellation;

    }

    /**
     * Sets the celestial constellation, where the target can be found.<br>
     *
     * @param constellation
     *            The celestial constellation of the target
     */
    @Override
    public void setConstellation(String constellation) {

        if (StringUtils.isBlank(constellation)) {
            this.constellation = null;
        } else {

            this.constellation = Constellation.getConstellationByAbbOrName(constellation);
        }

    }

    /**
     * Sets the celestial constellation, where the target can be found.<br>
     *
     * @param constellation
     *            The celestial constellation of the target
     */
    @Override
    public void setConstellation(Constellation constellation) {

        this.constellation = constellation;

    }

    /**
     * Sets the name of the target.<br>
     * The name should clearly identify the astronomical object. For alternative names of the object add a new alias
     * name.<br>
     * If a name is already set to the target, the old name will be overwritten with new new name.
     *
     * @return The name of the target
     * @throws IllegalArgumentException
     *             if name was <code>null</code>
     */
    @Override
    public void setName(String name) throws IllegalArgumentException {

        if (name == null) {
            throw new IllegalArgumentException("name cannot be null. ");
        }

        this.name = name;

    }

    /**
     * Returns the position of the target.<br>
     * The position of the target describes the location of the astronomical object in any popular celestial
     * coordination system.
     *
     * @return The celestial position of the astronomical object Might return <code>null</code> if position was never
     *         set.
     */
    @Override
    public EquPosition getPosition() {

        return position == null ? null : new EquPosition(position.getRa(), position.getDec());

    }

    /**
     * Returns the observer who is the originator of the target.<br>
     *
     * @return The observer who is the originator of this target. Might return <code>null</code> if observer was never
     *         set. (In this case a dataSource must exist)
     */
    @Override
    public IObserver getObserver() {
        if (this.observer == null) {
            return null;
        }
        return (IObserver) this.observer.getCopy();

    }

    /**
     * Returns the datasource which is the origin of the target.<br>
     *
     * @return The datasource which is the origin of this target Might return <code>null</code> if datasource was never
     *         set. (In this case a observer must exist)
     */
    @Override
    public String getDatasource() {

        return this.dataSource;

    }

    /**
     * Sets the position of the target.<br>
     * The position of the target describes the location of the astronomical object in a popular celestial coordination
     * system.
     *
     * @param position
     *            The position of the astronomical object in a popular coordination system
     */
    @Override
    public void setPosition(EquPosition position) {

        this.position = position == null ? null : new EquPosition(position.getRa(), position.getDec());

    }

    /**
     * Sets the datasource of the target.<br>
     *
     * @param datasource
     *            The datasource of the astronomical object
     */
    @Override
    public void setDatasource(String datasource) {

        if (datasource != null) {
            this.observer = null;
            this.dataSource = datasource;
        }

    }

    /**
     * Sets the observer who is the originator of the target.<br>
     *
     * @param observer
     *            The observer who is the originator of this target
     */
    @Override
    public void setObserver(IObserver observer) {

        if (observer != null) {
            this.dataSource = null;
            this.observer = (IObserver) observer.getCopy();
        }

    }

    /**
     * Returns additional notes of the target.<br>
     * Notes can be used for additional descriptions of the target.
     *
     * @return Notes on the target or <code>null</code> if no notes were set
     */
    @Override
    public String getNotes() {

        return this.notes;

    }

    /**
     * Sets additional notes to the target.<br>
     * Additional notes can be used to add any additional textual information to the target.
     *
     * @param notes
     *            Additional notes
     */
    @Override
    public void setNotes(String notes) {

        this.notes = notes;

    }

    // -----------------
    // Protected methods -------------------------------------------------
    // -----------------

    /**
     * Creates an XML DOM Element for the Target. The new Target element will be added as child element to an given
     * parent element. The given parent element should be the target container. All specialised subclasses may use this
     * method to create a Target element under which they may store their addition data.<br>
     * Example:<br>
     * &lt;targetContainer&gt;<br>
     * &lt;target&gt;<br>
     * <i>More specialised stuff goes here</i><br>
     * &lt;/target&gt;<br>
     * &lt;/targetContainer&gt;
     *
     * @param parent
     *            The target container element
     * @return Returns the new created target element (which is a child of the passed container element) Might return
     *         <code>null</code> if parent was <code>null</code>.
     * @see org.w3c.dom.Element
     */
    protected Element createXmlTargetElement(Element parent) {

        if (parent == null) {
            return null;
        }

        Document ownerDoc = parent.getOwnerDocument();

        Element e_Target = ownerDoc.createElement(XML_ELEMENT_TARGET);
        e_Target.setAttribute(XML_ELEMENT_ATTRIBUTE_ID, this.getID());

        parent.appendChild(e_Target);

        if (this.dataSource != null) {
            Element e_DataSource = ownerDoc.createElement(ITarget.XML_ELEMENT_DATASOURCE);
            this.convertDataSource(); // For OM 0.617->0.717
            Node n_DataSourceText = ownerDoc.createCDATASection(this.dataSource);
            e_DataSource.appendChild(n_DataSourceText);
            e_Target.appendChild(e_DataSource);
        } else {
            this.observer.addAsLinkToXmlElement(e_Target, IObserver.XML_ELEMENT_OBSERVER);
        }

        Element e_Name = ownerDoc.createElement(XML_ELEMENT_NAME);
        Node n_NameText = ownerDoc.createCDATASection(this.name);
        e_Name.appendChild(n_NameText);
        e_Target.appendChild(e_Name);

        if (!aliasNames.isEmpty()) {

            Element e_Alias = null;
            ListIterator<String> iterator = aliasNames.listIterator();
            String alias = null;
            while (iterator.hasNext()) {

                alias = iterator.next();

                e_Alias = ownerDoc.createElement(XML_ELEMENT_ALIASNAME);
                Node n_AliasText = ownerDoc.createCDATASection(alias);
                e_Alias.appendChild(n_AliasText);
                e_Target.appendChild(e_Alias);

            }
        }

        if (position != null) {
            e_Target = position.addToXmlElement(e_Target);
        }

        if (constellation != null) {
            Element e_Constellation = ownerDoc.createElement(ITarget.XML_ELEMENT_CONSTELLATION);
            Node n_ConstellationText = ownerDoc.createCDATASection(this.constellation.getName());
            e_Constellation.appendChild(n_ConstellationText);
            e_Target.appendChild(e_Constellation);
        }

        if (notes != null) {
            Element e_Notes = ownerDoc.createElement(ITarget.XML_ELEMENT_NOTES);
            Node n_NotesText = ownerDoc.createCDATASection(this.notes);
            e_Notes.appendChild(n_NotesText);
            e_Target.appendChild(e_Notes);
        }

        return e_Target;

    }

    // ---------------
    // Private methods ---------------------------------------------------
    // ---------------

    // Ok this might be a bit ugly bit for data consistency this is the
    // best way to do so. :-(
    // From OM 0.617 to 0.717 the catalog datasouce names changed in that
    // way that the OM (or Extenstion) Version is no longer part
    // of the Datasource description. Instead the actual catalog version
    // is now part of the datasource description.
    // As from previous releases there many targets out there with a
    // datasource descrption like "Observation Manager {SomeOMVersion}"
    // we just convert them now (once) to their correct version.
    private void convertDataSource() {

        // Correct old HCNGC entries
        if (this.dataSource
                .startsWith("The NGC/IC Project LLC (http://www.ngcic.org) - Imported by ObservationManager")) {
            this.dataSource = "The NGC/IC Project LLC (http://www.ngcic.org) - Ver 1.11";
            return;
        }

        // Correct old messier entires
        if (this.dataSource.endsWith("Messier Catalog Extension")) {
            this.dataSource = "ObservationManager - Messier Catalog 1.0";
            return;
        }

        // Correct old caldwell entries
        if (this.dataSource.endsWith("Caldwell Catalog Extension")) {
            this.dataSource = "ObservationManager - Caldwell Catalog 1.0";
            return;
        }

        // Correct old solar system entries
        if (this.dataSource.endsWith("SolarSystem Extension")) {
            this.dataSource = "ObservationManager - SolarSystem Catalog 1.0";
        }

    }

    // --------------
    // @Override public methods ----------------------------------------------------
    // --------------

    /**
     * Adds a comma seperated list of new alias names to the Target.<br>
     *
     * @param aliasNames
     *            Comma seperated list with alternative names of the astronomical object
     * @return Returns <code>true</code> if all alias names of the list could be added. If <code>false</code> is
     *         returned the new alias was <code>null</code>.
     */
    public boolean addAliasNames(String aliasNames) {

        if (aliasNames == null) {
            return false;
        }

        StringTokenizer tokenizer = new StringTokenizer(aliasNames, ALIASNAMES_DELIMITER);
        String token = null;
        if (tokenizer.hasMoreTokens()) {

            token = tokenizer.nextToken();

            this.aliasNames.add(token);

        }

        return true;

    }

    @Override
    public ICloneable getCopy() {
        try {
            return (Target) this.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }

    }

}
