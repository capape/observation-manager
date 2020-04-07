package de.lehmannet.om.mapper;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.lehmannet.om.extension.imaging.CCDImager;
import de.lehmannet.om.util.FloatUtil;
import de.lehmannet.om.util.SchemaException;

public class CCDImagerMapper {

    public static byte getBinningValue(Element imager) throws SchemaException {
        Element child;
        NodeList children;
        // Get binning value
        child = null;
        children = imager.getElementsByTagName(CCDImager.XML_ELEMENT_BINNING);
        StringBuilder sBin = new StringBuilder();
        if (children != null) { // Use default value of 1
            if (children.getLength() == 1) {
                child = (Element) children.item(0);
                if (child != null) {
                    NodeList textElements = child.getChildNodes();
                    if ((textElements != null) && (textElements.getLength() > 0)) {
                        for (int te = 0; te < textElements.getLength(); te++) {
                            sBin.append(textElements.item(te).getNodeValue());
                        }
                        byte b = Byte.parseByte(sBin.toString());
                        return b;
                    }
                } else {
                    throw new SchemaException("Problem while retrieving binning value from CCD Imager. ");
                }
            } else if (children.getLength() > 1) {
                throw new SchemaException("CCDImager can have only one binning value. ");
            }
        }
        return -1;
    }

    public static float getYPixelSize(Element imager) throws SchemaException {
        Element child;
        NodeList children;
        // Get optional y-Pixel size
        child = null;
        children = imager.getElementsByTagName(CCDImager.XML_ELEMENT_YPIXELS_SIZE);
        StringBuilder ySize = new StringBuilder();
        if (children != null) {
            if (children.getLength() == 1) {
                child = (Element) children.item(0);
                if (child != null) {
                    NodeList textElements = child.getChildNodes();
                    if ((textElements != null) && (textElements.getLength() > 0)) {
                        for (int te = 0; te < textElements.getLength(); te++) {
                            ySize.append(textElements.item(te).getNodeValue());
                        }
                        float yS = FloatUtil.parseFloat(ySize.toString());
                        if (yS > 0.0) {
                            return yS;
                        }
                    }
                } else {
                    throw new SchemaException("Problem while retrieving y pixel size from CCD Imager. ");
                }
            } else if (children.getLength() > 1) {
                throw new SchemaException("CCDImager can have only one y pixel size value. ");
            }
        }
        return Float.NaN;
    }

    public static float getXPixelSize(Element imager) throws SchemaException {
        Element child;
        NodeList children;
        // Get optional x-Pixel size
        child = null;
        children = imager.getElementsByTagName(CCDImager.XML_ELEMENT_XPIXELS_SIZE);
        StringBuilder xSize = new StringBuilder();
        if (children != null) {
            if (children.getLength() == 1) {
                child = (Element) children.item(0);
                if (child != null) {
                    NodeList textElements = child.getChildNodes();
                    if ((textElements != null) && (textElements.getLength() > 0)) {
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
        }

        return Float.NaN;
    }

    public static int getYPixels(Element imager) throws SchemaException {
        Element child;
        NodeList children;
        // Get yPixels
        children = imager.getElementsByTagName(CCDImager.XML_ELEMENT_YPIXELS);
        if ((children == null) || (children.getLength() != 1)) {
            throw new SchemaException("CCDImager must have exact one y pixels element. ");
        }
        child = (Element) children.item(0);
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
        Element child;
        NodeList children;
        // Get xPixels
        children = imager.getElementsByTagName(CCDImager.XML_ELEMENT_XPIXELS);
        if ((children == null) || (children.getLength() != 1)) {
            throw new SchemaException("CCDImager must have exact one x pixels element. ");
        }
        child = (Element) children.item(0);
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