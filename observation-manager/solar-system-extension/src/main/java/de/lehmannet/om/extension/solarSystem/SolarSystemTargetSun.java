/*
 * ====================================================================
 * extension/solarSystem/SolarSystemTargetSun.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.extension.solarSystem;

import de.lehmannet.om.IObserver;
import de.lehmannet.om.util.SchemaException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * SolarSystemTargetSun extends the de.lehmannet.om.extension.solarSystem.SolarSystemTarget class. This class exists
 * more due to extension reasons as this class does not add new functions to de.lehmannet.om.Target
 *
 * @author doergn@users.sourceforge.net
 * @since 1.3
 */
public class SolarSystemTargetSun extends SolarSystemTarget {

    // ---------
    // Constants ---------------------------------------------------------
    // ---------

    // XSML schema instance value. Enables class/schema loaders to identify this
    // class
    public static final String XML_XSI_TYPE_VALUE = "oal:SunTargetType";

    // ------------
    // Constructors ------------------------------------------------------
    // ------------

    /**
     * Constructs a new instance of a SolarSystemTargetSun from a given DOM target Element.<br>
     * Normally this constructor is called by a subclass which itself is called by de.lehmannet.om.util.SchemaLoader.
     * Please mind that Target has to have a <observer> element, or a <datasource> element. If a <observer> element is
     * set, a array with Observers must be passed to check, whether the <observer> link is valid.
     *
     * @param observers
     *            Array of IObserver that might be linked from this observation, can be <code>NULL</code> if datasource
     *            element is set
     * @param targetElement
     *            The origin XML DOM <target> Element
     * @throws SchemaException
     *             if given targetElement was <code>null</code>
     */
    public SolarSystemTargetSun(Node targetElement, IObserver... observers) throws SchemaException {

        super(targetElement, observers);
    }

    /**
     * Constructs a new instance of a SolarSystemTargetSun.<br>
     *
     * @param name
     *            String with the name of the target
     * @param observer
     *            The observer who created this <target> Element
     */
    public SolarSystemTargetSun(String name, IObserver observer) {

        super(name, observer);
    }

    /**
     * Constructs a new instance of a SolarSystemTargetSun.<br>
     *
     * @param name
     *            String with the name of the target
     * @param datasource
     *            The origin of the <target> Element
     */
    public SolarSystemTargetSun(String name, String datasource) {

        super(name, datasource);
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

        return SolarSystemTargetSun.XML_XSI_TYPE_VALUE;
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
     * @see org.w3c.dom.Element
     */
    @Override
    public void addToXmlElement(Element element) {

        if (element == null) {
            return;
        }

        this.createXmlSolarSystemTargetElement(element, SolarSystemTargetSun.XML_XSI_TYPE_VALUE);
    }
}
