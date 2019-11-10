/* ====================================================================
 * extension/deepSky/DeepSkyTarget.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */
package de.lehmannet.om.extension.deepSky;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.lehmannet.om.IObserver;
import de.lehmannet.om.util.SchemaException;

/**
 * DeepSkyTarget extends the de.lehmannet.om.extension.deepSky.DeepSkyTarget
 * class. Its a simple implementation for miscellaneous/uncategorized deep sky
 * objects. A DeepSky target can be an astronomical object outside our solar
 * system.<br>
 * 
 * @author doergn@users.sourceforge.net
 * @since 1.3
 */
public class DeepSkyTargetNA extends DeepSkyTarget {

    // ---------
    // Constants ---------------------------------------------------------
    // ---------

    // XSML schema instance value. Enables class/schema loaders to identify this
    // class
    public static final String XML_XSI_TYPE_VALUE = "oal:deepSkyNA";

    // ------------
    // Constructors ------------------------------------------------------
    // ------------

    // -------------------------------------------------------------------
    /**
     * Constructs a new instance of a DeepSkyTargetNA from a given DOM target
     * Element.<br>
     * Normally this constructor is called by de.lehmannet.om.util.SchemaLoader.
     * Please mind that Target has to have a <observer> element, or a <datasource>
     * element. If a <observer> element is set, a array with Observers must be
     * passed to check, whether the <observer> link is valid.
     * 
     * @param observers     Array of IObserver that might be linked from this
     *                      observation, can be <code>NULL</code> if datasource
     *                      element is set
     * @param targetElement The origin XML DOM <target> Element
     * @throws SchemaException if given targetElement was <code>null</code>
     */
    public DeepSkyTargetNA(Node targetElement, IObserver[] observers) throws SchemaException {

        super(targetElement, observers);

    }

    // -------------------------------------------------------------------
    /**
     * Constructs a new instance of a DeepSkyTargetNA.
     * 
     * @param name       The name of the deepsky target
     * @param datasource The datasource of the deepsky target
     */
    public DeepSkyTargetNA(String name, String datasource) {

        super(name, datasource);

    }

    // -------------------------------------------------------------------
    /**
     * Constructs a new instance of a DeepSkyTargetNA.
     * 
     * @param name     The name of the deepsky target
     * @param observer The observer who is the originator of the deepsky target
     */
    public DeepSkyTargetNA(String name, IObserver observer) {

        super(name, observer);

    }

    // ------------------------
    // IExtendableSchemaElement ------------------------------------------
    // ------------------------

    // -------------------------------------------------------------------
    /**
     * Returns the XML schema instance type of the implementation.<br>
     * Example:<br>
     * <target xsi:type="myOwnTarget"><br>
     * </target><br>
     * 
     * @return The xsi:type value of this implementation
     */
    @Override
    public String getXSIType() {

        return DeepSkyTargetNA.XML_XSI_TYPE_VALUE;

    }

    // ------
    // Target ------------------------------------------------------------
    // ------

    // -------------------------------------------------------------------
    /**
     * Adds this Target to a given parent XML DOM Element. The Target element will
     * be set as a child element of the passed element.
     * 
     * @param parent The parent element for this Target
     * @return Returns the element given as parameter with this Target as child
     *         element.<br>
     *         Might return <code>null</code> if parent was <code>null</code>.
     * @see org.w3c.dom.Element
     */
    @Override
    public Element addToXmlElement(Element element) {

        if (element == null) {
            return null;
        }

        Document ownerDoc = element.getOwnerDocument();

        Element e_DSTarget = super.createXmlDeepSkyTargetElement(element, DeepSkyTargetNA.XML_XSI_TYPE_VALUE);

        return element;

    }

}
