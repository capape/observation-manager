/* ====================================================================
 * /OALException.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om;

/**
 * The root class for all exceptions used in the API. Replaces COMASTException with OAL 2.0
 * 
 * @author doergn@users.sourceforge.net
 * @since 1.7
 */
public class OALException extends Exception {

    // ------------
    // Constructors ------------------------------------------------------
    // ------------

    /**
     * Constructs a new instance of a OALException.
     * 
     * @param message
     *            The exceptions message
     */
    public OALException(String message) {

        super(addMessageFlavour(message));

    }

    /**
     * Constructs a new instance of a OALException.
     * 
     * @param message
     *            The exceptions message
     * @param cause
     *            The exceptions cause
     */
    public OALException(String message, Throwable cause) {

        super(addMessageFlavour(message), cause);

    }

    // ---------------
    // Private methods ---------------------------------------------------
    // ---------------

    /**
     * Adds a special flavour around the exceptions message that should make it easier to point out OALExceptions.
     */
    private static String addMessageFlavour(String message) {

        return "\n*********" + message + "*********\n";

    }

}
