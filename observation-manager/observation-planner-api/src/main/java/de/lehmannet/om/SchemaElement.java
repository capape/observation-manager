/*
 * ====================================================================
 * /SchemaElement.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.lehmannet.om.util.IIDGenerator;
import de.lehmannet.om.util.UIDGenerator;

/**
 * The SchemaElement represents the root class for all schema element classes.
 * It provides a simple implemantation of
 * the ISchemaInterface, so that any subclass inherits a unique ID.
 *
 * @author doergn@users.sourceforge.net
 * @since 1.0
 */
public abstract class SchemaElement implements ISchemaElement {

    private static Logger log = LoggerFactory.getLogger(SchemaElement.class);
    // ---------------
    // Class Variables ---------------------------------------------------
    // ---------------

    // UID Generator
    protected final static IIDGenerator IDGenerator = new UIDGenerator();

    // ------------------
    // Instance Variables ------------------------------------------------
    // ------------------

    // The unique ID of this schema element
    private String ID = "";

    // ------------
    // Constructors ------------------------------------------------------
    // ------------

    /**
     * Constructs a new instance of a Schema Element.<br>
     * Any instance of a Schema Element has a unique ID which identifies the
     * element, and which allows to link serveral
     * elements.
     */
    public SchemaElement() {

        ID = SchemaElement.IDGenerator.generateUID();

    }

    /**
     * Constructs a new instance of a Schema Element with a given ID.<br>
     * Any instance of a Schema Element has a unique ID which identifies the
     * element, and which allows to link serveral
     * elements.<br>
     *
     * @throws IllegalArgumentException
     *             if ID is <code>null</code> or contains empty
     *             string.
     */
    SchemaElement(String id) throws IllegalArgumentException {

        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null or empty! ");
        }
        ID = id;

    }

    // --------------
    // ISchemaElement ----------------------------------------------------
    // --------------

    /**
     * Returns a unique ID of this schema element.<br>
     * The ID is used to link this element with other XML elements in the schema.
     *
     * @return Returns a String representing a unique ID of this schema element.
     */
    @Override
    public final String getID() {

        return ID;

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ID == null) ? 0 : ID.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SchemaElement other = (SchemaElement) obj;
        if (ID == null) {
            return other.ID == null;
        } else
            return ID.equals(other.ID);
    }

    /**
     * Sets a unique ID of this schema element.<br>
     * The ID is used to link this element with other XML elements in the
     * schema.<br>
     * Call this method only, if your know what you're doing.
     *
     * @param newID
     *            The new unique ID for this object.
     */
    void setID(String newID) {

        this.ID = newID;

    }

    /**
     * Returns a display name for this element.<br>
     * The method differs from the toString() method as toString() shows more
     * technical information about the element.
     * Also the formating of toString() can spread over several lines.<br>
     * This method returns a string (in one line) that can be used as displayname in
     * e.g. a UI dropdown box.
     *
     * @return Returns a String with a one line display name
     * @see java.lang.Object.toString();
     */
    @Override
    public abstract String getDisplayName();

}
