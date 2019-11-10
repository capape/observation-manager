/* ====================================================================
 * /util/Worker.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.util;

public interface Worker extends Runnable {

    public static byte RETURN_TYPE_OK = 0;
    public static byte RETURN_TYPE_WARNING = 1;
    public static byte RETURN_TYPE_ERROR = 2;

    @Override
    public abstract void run();

    public String getReturnMessage();

    public byte getReturnType();

}
