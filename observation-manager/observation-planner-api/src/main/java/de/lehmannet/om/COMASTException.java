/* ====================================================================
 * /COMASTException.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om;

/**
 * The root class for all exceptions used in the API. Was called FCGAException before version 1.7
 * 
 * @author doergn@users.sourceforge.net
 * @since 1.7
 * @deprecated Use OALException instead
 */
@Deprecated
public class COMASTException extends Exception {

    // ------------
    // Constructors ------------------------------------------------------
    // ------------

    /**
     * Constructs a new instance of a COMASTException.
     * 
     * @param message
     *            The exceptions message
     */
    public COMASTException(String message) {

        super(addMessageFlavour(message));

    }

    /**
     * Constructs a new instance of a COMASTException.
     * 
     * @param message
     *            The exceptions message
     * @param cause
     *            The exceptions cause
     */
    public COMASTException(String message, Throwable cause) {

        super(addMessageFlavour(message), cause);

    }

    // ---------------
    // Private methods ---------------------------------------------------
    // ---------------

    /**
     * Adds a special flavour around the exceptions message that sould make it easier to point out COMASTExceptions.
     */
    private static String addMessageFlavour(String message) {

        return "\n*********" + message + "*********\n";

    }

}
