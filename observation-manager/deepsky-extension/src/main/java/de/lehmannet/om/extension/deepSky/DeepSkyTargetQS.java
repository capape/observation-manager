/* ====================================================================
 * extension/deepSky/DeepSkyTargetQS.java
 *
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.extension.deepSky;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.lehmannet.om.IObserver;
import de.lehmannet.om.util.SchemaException;

/**
 * DeepSkyTargetQS extends the de.lehmannet.om.extension.deepSky.DeepSkyTarget class.<br>
 * Its specialised for quasars.<br>
 *
 * @author doergn@users.sourceforge.net
 *
 * @since 1.0
 */
public class DeepSkyTargetQS extends DeepSkyTarget {

    // ---------
    // Constants ---------------------------------------------------------
    // ---------

    // XSML schema instance value. Enables class/schema loaders to identify this
    // class
    public static final String XML_XSI_TYPE_VALUE = "oal:deepSkyQS";

    // ------------
    // Constructors ------------------------------------------------------
    // ------------

    /**
     * Constructs a new instance of a DeepSkyTargetQS from a given DOM target Element.<br>
     * Normally this constructor is called by de.lehmannet.om.util.SchemaLoader. Please mind that Target has to have a
     * <observer> element, or a <datasource> element. If a <observer> element is set, a array with Observers must be
     * passed to check, whether the <observer> link is valid.
     *
     * @param observers
     *            Array of IObserver that might be linked from this observation, can be <code>NULL</code> if datasource
     *            element is set
     * @param targetElement
     *            The origin XML DOM <target> Element
     *
     * @throws SchemaException
     *             if given targetElement was <code>null</code>
     */
    public DeepSkyTargetQS(Node targetElement, IObserver... observers) throws SchemaException {

        super(targetElement, observers);

    }

    /**
     * Constructs a new instance of a DeepSkyTargetQS.
     *
     * @param name
     *            The name of the quasar
     * @param datasource
     *            The datasource of the quasar
     */
    public DeepSkyTargetQS(String name, String datasource) {

        super(name, datasource);

    }

    /**
     * Constructs a new instance of a DeepSkyTargetQS.
     *
     * @param name
     *            The name of the quasar
     * @param observer
     *            The observer who is the originator of the quasar
     */
    public DeepSkyTargetQS(String name, IObserver observer) {

        super(name, observer);

    }

    // ------
    // Target ------------------------------------------------------------
    // ------

    /**
     * Adds this Target to a given parent XML DOM Element. The Target element will be set as a child element of the
     * passed element.
     *
     * @param parent
     *            The parent element for this Target
     *
     * @see org.w3c.dom.Element
     */
    @Override
    public void addToXmlElement(Element element) {

        if (element == null) {
            return;
        }

        this.createXmlDeepSkyTargetElement(element, DeepSkyTargetQS.XML_XSI_TYPE_VALUE);

    }

    // ------------------------
    // IExtendableSchemaElement ------------------------------------------
    // ------------------------

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

        return DeepSkyTargetQS.XML_XSI_TYPE_VALUE;

    }

}