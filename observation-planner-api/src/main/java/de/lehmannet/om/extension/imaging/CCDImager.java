/* ====================================================================
 * /CCDImager.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.extension.imaging;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.lehmannet.om.IImager;
import de.lehmannet.om.Imager;
import de.lehmannet.om.util.FloatUtil;
import de.lehmannet.om.util.SchemaException;

/**
 * Describes a CCD camera.
 * 
 * @author doergn@users.sourceforge.net
 * @since 1.3
 */
public class CCDImager extends Imager {

    // ---------
    // Constants ---------------------------------------------------------
    // ---------

    /**
     * Constant for XML representation: ccd imager attribute name
     */
    public static final String XML_ATTRIBUTE_CCDIMAGER = "oal:ccdCameraType";

    /**
     * Constant for XML representation: pixels on x axis
     */
    private static final String XML_ELEMENT_XPIXELS = "pixelsX";

    /**
     * Constant for XML representation: pixels on y axis
     */
    private static final String XML_ELEMENT_YPIXELS = "pixelsY";

    /**
     * Constant for XML representation: pixel size on x axis
     */
    private static final String XML_ELEMENT_XPIXELS_SIZE = "pixelXSize";

    /**
     * Constant for XML representation: pixel site on y axis
     */
    private static final String XML_ELEMENT_YPIXELS_SIZE = "pixelYSize";

    /**
     * Constant for XML representation: binning value
     */
    private static final String XML_ELEMENT_BINNING = "binning";

    // ------------------
    // Instance Variables ------------------------------------------------
    // ------------------

    // Number of pixel on the x axis
    private int xPixels = 0;

    // Number of pixel on the y axis
    private int yPixels = 0;

    // Pixel size on the x axis
    private float xPixelSize = Float.NaN;

    // Pixel size on the y axis
    private float yPixelSize = Float.NaN;

    // Binning (default 1x1)
    private byte binning = 1;

    // ------------
    // Constructors ------------------------------------------------------
    // ------------

/**
     * Constructs a new instance of a CCDImager from a given DOM target Element.<br>
     * Normally this constructor is called by a child class which itself was called by
     * de.lehmannet.om.util.SchemaLoader.
     * 
     * @param imagerElement
     *            The origin XML DOM <target> Element
     * @throws SchemaException
     *             if given imagerElement was <code>null</code>
     */
    public CCDImager(Node imagerElement) throws SchemaException {

        super(imagerElement);

        Element imager = (Element) imagerElement;

        Element child = null;
        NodeList children = null;

        // Getting data

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

            this.setXPixels(Integer.parseInt(x));
        }

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

            this.setYPixels(Integer.parseInt(y));
        }

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
                            this.setXPixelSize(xS);
                        }
                    }
                } else {
                    throw new SchemaException("Problem while retrieving x pixel size from CCD Imager. ");
                }
            } else if (children.getLength() > 1) {
                throw new SchemaException("CCDImager can have only one x pixel size value. ");
            }
        }

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
                            this.setYPixelSize(yS);
                        }
                    }
                } else {
                    throw new SchemaException("Problem while retrieving y pixel size from CCD Imager. ");
                }
            } else if (children.getLength() > 1) {
                throw new SchemaException("CCDImager can have only one y pixel size value. ");
            }
        }

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
                        this.setBinning(b);
                    }
                } else {
                    throw new SchemaException("Problem while retrieving binning value from CCD Imager. ");
                }
            } else if (children.getLength() > 1) {
                throw new SchemaException("CCDImager can have only one binning value. ");
            }
        }

    }

/**
     * Constructs a new instance of a CCDImager.<br>
     * 
     * @param model
     *            The model name
     * @param xPixels
     *            The amount of pixel on the x axis
     * @param yPixels
     *            The amount of pixel on the y axis
     * @throws SchemaException
     *             if given model was <code>null</code>, or on of the pixel values was <= 0.
     */
    public CCDImager(String model, int xPixels, int yPixels) {

        super(model);

        this.setXPixels(xPixels);
        this.setYPixels(yPixels);

    }

    // ------------------------
    // IExtendableSchemaElement ------------------------------------------
    // ------------------------

/**
     * Returns the XML schema instance type of the implementation.<br>
     * Example:<br>
     * <imager xsi:type="myOwnImagerType"><br>
     * </imager><br>
     * 
     * @return The xsi:type value of this implementation
     */
    @Override
    public String getXSIType() {

        return CCDImager.XML_ATTRIBUTE_CCDIMAGER;

    }

    // ------
    // Imager ------------------------------------------------------------
    // ------

    @Override
    public void addToXmlElement(Element element) {

        if (element == null) {
            return;
        }

        Document ownerDoc = element.getOwnerDocument();

        Element e_Imager = super.createXmlImagerElement(element);
        if (e_Imager == element) {
            return; // Already added
        }
        e_Imager.setAttribute(IImager.XML_XSI_TYPE, CCDImager.XML_ATTRIBUTE_CCDIMAGER);

        Element e_XPixels = ownerDoc.createElement(CCDImager.XML_ELEMENT_XPIXELS);
        Node n_xPixelsText = ownerDoc.createTextNode("" + this.xPixels);
        e_XPixels.appendChild(n_xPixelsText);
        e_Imager.appendChild(e_XPixels);

        Element e_YPixels = ownerDoc.createElement(CCDImager.XML_ELEMENT_YPIXELS);
        Node n_yPixelsText = ownerDoc.createTextNode("" + this.yPixels);
        e_YPixels.appendChild(n_yPixelsText);
        e_Imager.appendChild(e_YPixels);

        if (!Float.isNaN(this.xPixelSize)) {
            Element e_XPixelSize = ownerDoc.createElement(CCDImager.XML_ELEMENT_XPIXELS_SIZE);
            Node n_xPixelSizeText = ownerDoc.createTextNode("" + this.xPixelSize);
            e_XPixelSize.appendChild(n_xPixelSizeText);
            e_Imager.appendChild(e_XPixelSize);
        }

        if (!Float.isNaN(this.yPixelSize)) {
            Element e_YPixelSize = ownerDoc.createElement(CCDImager.XML_ELEMENT_YPIXELS_SIZE);
            Node n_yPixelSizeText = ownerDoc.createTextNode("" + this.yPixelSize);
            e_YPixelSize.appendChild(n_yPixelSizeText);
            e_Imager.appendChild(e_YPixelSize);
        }

        Element e_Binning = ownerDoc.createElement(CCDImager.XML_ELEMENT_BINNING);
        Node n_BinningText = ownerDoc.createTextNode("" + this.binning);
        e_Binning.appendChild(n_BinningText);
        e_Imager.appendChild(e_Binning);

    }

    // --------------
    // Public Methods ----------------------------------------------------
    // --------------

/**
     * Returns the amount of pixels on the x axis.<br>
     * 
     * @return Returns amount of pixels on the x axis<br>
     */
    public int getXPixels() {

        return xPixels;

    }

/**
     * Sets the amount of pixels on the x axis.<br>
     * 
     * @param pixels
     *            The new amount of pixel on the x axis
     * @throws IllegalArgumentException
     *             if given pixels are <= 0
     */
    public void setXPixels(int pixels) throws IllegalArgumentException {

        if (pixels <= 0) {
            throw new IllegalArgumentException("Amount of pixels on x axis must be greater than 0\n");
        }

        xPixels = pixels;

    }

/**
     * Returns the amount of pixels on the y axis.<br>
     * 
     * @return Returns amount of pixels on the y axis<br>
     */
    public int getYPixels() {

        return yPixels;

    }

/**
     * Sets the amount of pixels on the y axis.<br>
     * 
     * @param pixels
     *            The new amount of pixel on the y axis
     * @throws IllegalArgumentException
     *             if given pixels are <= 0
     */
    public void setYPixels(int pixels) {

        if (pixels <= 0) {
            throw new IllegalArgumentException("Amount of pixels on y axis must be greater than 0\n");
        }

        yPixels = pixels;

    }

/**
     * Returns the pixel size on the x axis.<br>
     * 
     * @return Returns the pixel size on the x axis or Float.NaN if the value was never set
     * @since 2.0
     */
    public float getXPixelSize() {

        return xPixelSize;

    }

/**
     * Sets the pixel size on the x axis.<br>
     * 
     * @param pixelSize
     *            The new size of the pixel on the x axis
     * @throws IllegalArgumentException
     *             if given pixel size is < 0
     * @since 2.0
     */
    public void setXPixelSize(float pixelSize) throws IllegalArgumentException {

        if (pixelSize < 0.0) {
            throw new IllegalArgumentException("Amount of pixels on x axis must be greater or equal then 0\n");
        }

        if (pixelSize == 0.0f) {
            this.xPixelSize = Float.NaN;
            return;
        }

        xPixelSize = pixelSize;

    }

/**
     * Returns the pixel size on the y axis.<br>
     * 
     * @return Returns the pixel size on the y axis or Float.NaN if the value was never set<br>
     * @since 2.0
     */
    public float getYPixelSize() {

        return yPixelSize;

    }

/**
     * Sets the pixel size on the y axis.<br>
     * 
     * @param pixelSize
     *            The new size of the pixel on the y axis
     * @throws IllegalArgumentException
     *             if given pixel size is < 0
     * @since 2.0
     */
    public void setYPixelSize(float pixelSize) throws IllegalArgumentException {

        if (pixelSize < 0.0) {
            throw new IllegalArgumentException("Amount of pixels on y axis must be greater or equal then 0\n");
        }

        if (pixelSize == 0.0f) {
            this.yPixelSize = Float.NaN;
            return;
        }

        yPixelSize = pixelSize;

    }

/**
     * Returns the binning.<br>
     * 
     * @return Returns the binning value of the camera. Default = 1<br>
     * @since 2.0
     */
    public byte getBinning() {

        return this.binning;

    }

/**
     * Sets the binning value of the camera.<br>
     * 
     * @param binning
     *            The new binning value of the camera
     * @throws IllegalArgumentException
     *             if given binning value size is < 1 or > 9
     * @since 2.0
     */
    public void setBinning(byte binning) throws IllegalArgumentException {

        if ((binning < 1) || (binning > 9)) {
            throw new IllegalArgumentException("Binning value must be between 1 to 9. Was: " + binning);
        }

        this.binning = binning;

    }

}
