package de.lehmannet.om.mapper;

import java.util.Optional;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.lehmannet.om.extension.imaging.CCDImager;
import de.lehmannet.om.util.FloatUtil;
import de.lehmannet.om.util.SchemaException;

public class CCDImagerMapper {

    public static byte getBinningValue(Element imager) throws SchemaException {

        NodeList children = imager.getElementsByTagName(CCDImager.XML_ELEMENT_BINNING);
        Element bininElement = NodeParser.getChildrenOfUniqueInstanceNodeChildren(children)
                .orElseThrow(() -> new SchemaException("Cannot read binding found"));

        String textValue = NodeParser.getChildrenNodesAsText(bininElement);
        return Byte.parseByte(textValue);
    }

    public static float getYPixelSize(Element imager) throws SchemaException {

        NodeList children = imager.getElementsByTagName(CCDImager.XML_ELEMENT_YPIXELS_SIZE);

        Element ySizeElement = NodeParser.getChildrenOfUniqueInstanceNodeChildren(children)
                .orElseThrow(() -> new SchemaException("Cannot read  y pixel size from CCD Imager"));

        String textValue = NodeParser.getChildrenNodesAsText(ySizeElement);
        float yS = FloatUtil.parseFloat(textValue);
        if (yS > 0.0) {
            return yS;
        } else {
            return Float.NaN;
        }

    }

    public static float getXPixelSize(Element imager) throws SchemaException {

        NodeList children = imager.getElementsByTagName(CCDImager.XML_ELEMENT_XPIXELS_SIZE);
        StringBuilder xSize = new StringBuilder();
        if (children.getLength() == 1) {
            Element child = (Element) children.item(0);
            if (child != null) {
                NodeList textElements = child.getChildNodes();
                if (textElements.getLength() > 0) {
                    for (int te = 0; te < textElements.getLength(); te++) {
                        xSize.append(textElements.item(te).getNodeValue());
                    }
                    float xS = FloatUtil.parseFloat(xSize.toString());
                    if (xS > 0.0) {
                        return xS;
                    }
                }
            } else {
                throw new SchemaException("Problem while retrieving x pixel size from CCD Imager. ");
            }
        } else if (children.getLength() > 1) {
            throw new SchemaException("CCDImager can have only one x pixel size value. ");
        }

        return Float.NaN;
    }

    public static int getYPixels(Element imager) throws SchemaException {

        NodeList children = imager.getElementsByTagName(CCDImager.XML_ELEMENT_YPIXELS);
        if (children.getLength() != 1) {
            throw new SchemaException("CCDImager must have exact one y pixels element. ");
        }
        Element child = (Element) children.item(0);
        String y = null;
        if (child == null) {
            throw new SchemaException("CCDImager must have a y pixel element. ");
        } else {
            if (child.getFirstChild() != null) {
                y = child.getFirstChild().getNodeValue(); // Get yPixels from text node
            } else {
                throw new SchemaException("CCDImager cannot have an empty yPixels element. ");
            }

            return Integer.parseInt(y);
        }
    }

    public static int getXPixels(Element imager) throws SchemaException {

        // Get xPixels
        NodeList children = imager.getElementsByTagName(CCDImager.XML_ELEMENT_XPIXELS);
        if (children.getLength() != 1) {
            throw new SchemaException("CCDImager must have exact one x pixels element. ");
        }
        Element child = (Element) children.item(0);
        String x = null;
        if (child == null) {
            throw new SchemaException("CCDImager must have a x pixel element. ");
        } else {
            if (child.getFirstChild() != null) {
                x = child.getFirstChild().getNodeValue(); // Get xPixels from text node
            } else {
                throw new SchemaException("CCDImager cannot have an empty xPixels element. ");
            }

            return Integer.parseInt(x);
        }
    }
}