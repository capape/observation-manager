package de.lehmannet.om.extension.variableStars.export;

import java.io.OutputStream;

/**
 * Interface for variable star observations serializers
 * 
 * @author doergn@users.sourceforge.net
 * 
 * @since 2.0
 */
interface ISerializer {

    // --------------
    // Public methods ---------------------------------------------------------
    // --------------

    // ------------------------------------------------------------------------
    /**
     * Serialize all observations to stream.
     * 
     * @return int value with the number of exported observations
     */
    int serialize(OutputStream stream) throws Exception;

}
