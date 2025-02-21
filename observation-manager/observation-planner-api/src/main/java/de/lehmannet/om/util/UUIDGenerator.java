/*
 * ====================================================================
 * /util/UUIDGenerator.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.util;

import java.util.UUID;

/**
 * The UUIDGenerator implements the IIDGenerator interface by using java.util.UUID, which creates universal unique IDs.
 * (Requires JRE 1.5 or higher)
 *
 * @author doergn@users.sourceforge.net
 * @since 2.0
 */
public class UUIDGenerator implements IIDGenerator {

    // ------------
    // Constructors ------------------------------------------------------
    // ------------

    public UUIDGenerator() throws Exception {

        // Check if VM Version is correct
        // This will cause the ClassNotFoundException in case the UUID class cannot be
        // found (VM Version < 1.5)
        ClassLoader.getSystemClassLoader().loadClass("java.util.UUID");
    }

    // ------------
    // IIDGenerator ------------------------------------------------------
    // ------------

    /**
     * Creates a universal unique ID that can be used to identify and link several schema elements.<br>
     * All elements inside a XML file need to have a unique ID.
     *
     * @return Returns a universal unique ID that can be used to identify a schema element and to link several schema
     *         elements
     */
    @Override
    public String generateUID() {

        String uid = UUID.randomUUID().toString();

        // XML Schema ID's should not contain '-'
        // characters, so we replace them
        uid = uid.replaceAll("-", "");

        // As XML Schema requires an ID not to start with a number,
        // we put a 'OM' before each ID
        return "OM" + uid;
    }
}
