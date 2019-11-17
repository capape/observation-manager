/* ====================================================================
 * extension/deepSky/test/TestReadXml
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.extension.deepSky.test;

import java.io.File;

import de.lehmannet.om.OALException;
import de.lehmannet.om.RootElement;
import de.lehmannet.om.util.SchemaLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple Test class that reads a XML and writes it again
 */
class TestReadXml {

    private static Logger log = LoggerFactory.getLogger(TestReadXml.class);

    public static void main(String[] args) {

        if (args.length != 2) {
            log.error(
                    "Need to pass xmlFilePath and newXmlFilePath as arguments. E.g. /home/john/myTestObservation.xml /home/john/myNewTestObservation.xml");
            return;
        }

        SchemaLoader loader = null;
        try {
            // Read
            loader = new SchemaLoader();
            RootElement obs = loader.load(new File(args[0]),
                    new File("file:/home/dirk/programming/java/observation/xml/basic/oal20.xsd"));

            obs.serializeAsXml(new File(args[1]));
        } catch (OALException fgca) {
            log.error("Error while loading document: ", fgca.getMessage());
        }

    }

}
