package de.lehmannet.om.mapper;

import de.lehmannet.om.util.SchemaException;
import java.util.Optional;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class NodeParser {

    public static Optional<Element> getChildrenOfUniqueInstanceNodeChildren(NodeList nodes) throws SchemaException {
        if (nodes == null || nodes.getLength() == 0) {
            return Optional.empty();
        }

        if (nodes.getLength() > 1) {
            throw new SchemaException("Can have only one child");
        }

        return Optional.of((Element) nodes.item(0));
    }

    public static String getChildrenNodesAsText(Element element) {

        NodeList textElements = element.getChildNodes();
        StringBuilder sBin = new StringBuilder();
        if ((textElements != null) && (textElements.getLength() > 0)) {
            for (int te = 0; te < textElements.getLength(); te++) {
                sBin.append(textElements.item(te).getNodeValue());
            }
        }
        return sBin.toString();
    }
}
