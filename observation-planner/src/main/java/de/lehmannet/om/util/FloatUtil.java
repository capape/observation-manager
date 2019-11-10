/* ====================================================================
 * /util/FloatUtil.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.util;

/**
 * Utility class for java.lang.Float object
 * 
 * 
 * @author doergn@users.sourceforge.net
 * @since 2.1_p1
 */
public class FloatUtil {

    public static float parseFloat(String floatString) {

        if ((floatString == null) || ("".equals(floatString.trim()))) {
            return Float.NaN;
        }

        floatString = floatString.replace(',', '.');

        return Float.parseFloat(floatString);

    }

}
