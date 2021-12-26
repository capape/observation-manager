/* ====================================================================
 * /util/SchemaElementConstants
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.util;

public enum SchemaElementConstants {

    // SchemaElement types represented as constants

    NONE(-1), EYEPIECE(0), SCOPE(1), OBSERVATION(2), IMAGER(3), SITE(4), SESSION(5), OBSERVER(6), TARGET(7), FILTER(8),
    LENS(9), FINDING(10);

    private final int value;

    private SchemaElementConstants(int val) {
        this.value = val;
    }

    public int getValue() {
        return this.value;
    }

}
