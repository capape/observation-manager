/* ====================================================================
 * /util/UIDGenerator.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.util;

import java.rmi.server.UID;

/**
 * The UIDGenerator implements the IIDGenerator interface by using java.rmi.server.UID, which creates pretty exact
 * unique IDs.
 * 
 * @author doergn@users.sourceforge.net
 * @since 1.0
 */
public class UIDGenerator implements IIDGenerator {

    // ------------
    // IIDGenerator ------------------------------------------------------
    // ------------

    /**
     * Creates a unique ID that can be used to identify and link several schema elements.<br>
     * All elements inside a XML file need to have a unique ID.
     * 
     * @return Returns a unique ID that can be used to identify a schema element and to link several schema elements
     */
    @Override
    public String generateUID() {

        String uid = new UID().toString();

        // XML Schema ID's should not contain ':' or '-'
        // characters, so we replace them
        uid = uid.replaceAll("-", "");
        uid = uid.replaceAll(":", "");

        // As XML Schema requires an ID not to start with a number,
        // we put a 'OM' before each ID
        return "OM" + uid;

    }

}
