/*
 * ====================================================================
 * /util/Worker.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.util;

public interface Worker extends Runnable {

    byte RETURN_TYPE_OK = 0;
    byte RETURN_TYPE_WARNING = 1;
    byte RETURN_TYPE_ERROR = 2;

    @Override
    void run();

    String getReturnMessage();

    byte getReturnType();
}
