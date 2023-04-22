/* ====================================================================
 * /util/IIDGenerator.java
 *
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.util;

/**
 * The IIDGenerator provides simple method access for generating a unique IDs, which are used to identify and link
 * several schema elements.
 *
 *
 * @author doergn@users.sourceforge.net
 *
 * @since 1.0
 */
public interface IIDGenerator {

    // --------------
    // Public Methods ----------------------------------------------------
    // --------------

    /**
     * Creates a unique ID that can be used to identify and link several schema elements.<br>
     * All elements inside a XML file need to have a unique ID.
     *
     * @return Returns a unique ID that can be used to identify a schema element and to link several schema elements
     */
    String generateUID();

}
