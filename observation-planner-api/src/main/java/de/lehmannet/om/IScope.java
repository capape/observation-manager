/* ====================================================================
 * /IScope.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om;

import org.w3c.dom.Element;

/**
 * An IScope describes an optical instrument which can be used for astronomical observations.<br>
 * An IScope must have an aperture and a model name, as well as either a magnification <b>or</b> a focalLength.
 * (magnification should be set if the optical instrument does not allow to change eyepieces).<br>
 * 
 * @author doergn@users.sourceforge.net
 * @since 1.0
 */
public interface IScope extends ISchemaElement, IEquipment {

    // ---------
    // Constants ---------------------------------------------------------
    // ---------

    /**
     * Constant for XML Schema Instance type.<br>
     * This is more or less the same as in IExtendableSchemaElement.java but as extending IExtendableSchemaElement would
     * cause us to implement <code>String getXSIType()</code> (which we can't do here) we define this constant again in
     * IScope.
     */
    String XML_XSI_TYPE = "xsi:type";

    /**
     * Constant for XML representation: IScope element name.<br>
     * Example:<br>
     * &lt;scope&gt;<i>More stuff goes here</i>&lt;/scope&gt;
     */
    String XML_ELEMENT_SCOPE = "scope";

    /**
     * Constant for XML representation: Scopes model element name.<br>
     * Example:<br>
     * &lt;scope&gt; <br>
     * <i>More stuff goes here</i> &lt;model&gt;<code>Scopes model name goes here</code>&lt;/model&gt; <i>More stuff
     * goes here</i> &lt;/scope&gt;
     */
    String XML_ELEMENT_MODEL = "model";

    /**
     * Constant for XML representation: Scopes type element name.<br>
     * Example:<br>
     * &lt;scope&gt; <br>
     * <i>More stuff goes here</i> &lt;type&gt;<code>Scopes type goes here</code>&lt;/type&gt; <i>More stuff goes
     * here</i> &lt;/scope&gt;
     */
    String XML_ELEMENT_TYPE = "type";

    /**
     * Constant for XML representation: Scopes vendor element name.<br>
     * Example:<br>
     * &lt;scope&gt; <br>
     * <i>More stuff goes here</i> &lt;vendor&gt;<code>Scopes vendor goes here</code>&lt;/vendor&gt; <i>More stuff goes
     * here</i> &lt;/scope&gt;
     */
    String XML_ELEMENT_VENDOR = "vendor";

    /**
     * Constant for XML representation: Scopes aperture element name.<br>
     * Example:<br>
     * &lt;scope&gt; <br>
     * <i>More stuff goes here</i> &lt;aperture&gt;<code>Scopes aperture goes here</code>&lt;/aperture&gt; <i>More stuff
     * goes here</i> &lt;/scope&gt;
     */
    String XML_ELEMENT_APERTURE = "aperture";

    /**
     * Constant for XML representation: Scopes focalLength element name.<br>
     * Example:<br>
     * &lt;scope&gt; <br>
     * <i>More stuff goes here</i> &lt;focalLength&gt;<code>Scopes focalLength goes here</code>&lt;/focalLength&gt;
     * <i>More stuff goes here</i> &lt;/scope&gt;
     */
    String XML_ELEMENT_FOCALLENGTH = "focalLength";

    /**
     * Constant for XML representation: Scopes magnification element name.<br>
     * Example:<br>
     * &lt;scope&gt; <br>
     * <i>More stuff goes here</i>
     * &lt;magnification&gt;<code>Scopes magnification goes here</code>&lt;/magnification&gt; <i>More stuff goes
     * here</i> &lt;/scope&gt;
     */
    String XML_ELEMENT_MAGNIFICATION = "magnification";

    /**
     * Constant for XML representation: Scopes lightGrasp element name.<br>
     * Example:<br>
     * &lt;scope&gt; <br>
     * <i>More stuff goes here</i> &lt;lightGrasp&gt;<code>Scopes lightGrasp goes here</code>&lt;/lightGrasp&gt; <i>More
     * stuff goes here</i> &lt;/scope&gt;
     */
    String XML_ELEMENT_LIGHTGRASP = "lightGrasp";

    /**
     * Constant for XML representation: Orientation of scope.<br>
     * Example:<br>
     * &lt;scope&gt; <br>
     * <i>More stuff goes here</i> &lt;orientation erect=true truesided=true /&gt; <i>More stuff goes here</i>
     * &lt;/scope&gt;
     */
    String XML_ELEMENT_ORENTATION = "orientation";

    /**
     * Constant for XML representation: Orientation attribute erect.<br>
     * Example:<br>
     * &lt;scope&gt; <br>
     * <i>More stuff goes here</i> &lt;orientation erect=true truesided=true /&gt; <i>More stuff goes here</i>
     * &lt;/scope&gt;
     */
    String XML_ELEMENT_ORENTATION_ATTRIBUTE_ERECT = "erect";

    /**
     * Constant for XML representation: Orientation attribute truesided.<br>
     * Example:<br>
     * &lt;scope&gt; <br>
     * <i>More stuff goes here</i> &lt;orientation erect=true truesided=true /&gt; <i>More stuff goes here</i>
     * &lt;/scope&gt;
     */
    String XML_ELEMENT_ORENTATION_ATTRIBUTE_TRUESIDED = "truesided";

    /**
     * Constant for XML representation: If magnification is given, the true field of view can be passed
     * additionally.<br>
     * Example:<br>
     * &lt;scope&gt; <br>
     * <i>More stuff goes here</i>
     * &lt;magnification&gt;<code>Scopes magnification goes here</code>&lt;/magnification&gt;
     * &lt;trueField&gt;<code>True field of view goes here</code>&lt;/trueField&gt; <i>More stuff goes here</i>
     * &lt;/scope&gt;
     */
    String XML_ELEMENT_TRUEFIELD = "trueField";

    // --------------
    // Public Methods ----------------------------------------------------
    // --------------

    // -------------------------------------------------------------------
    /**
     * Adds this Scope to a given parent XML DOM Element. The Scope element will be set as a child element of the passed
     * element.
     * 
     * @param parent
     *            The parent element for this Scope
     * @see org.w3c.dom.Element
     */
    void addToXmlElement(Element element);

    // -------------------------------------------------------------------
    /**
     * Adds the scope link to an given XML DOM Element The scope element itself will be attached to given elements
     * ownerDocument if the passed boolean was <code>true</code>. If the ownerDocument has no scope container, it will
     * be created (in case the passed boolean was <code>true</code>).<br>
     * Example:<br>
     * &lt;parameterElement&gt;<br>
     * <b>&lt;scopeLink&gt;123&lt;/scopeLink&gt;</b><br>
     * &lt;/parameterElement&gt;<br>
     * <i>More stuff of the xml document goes here</i><br>
     * <b>&lt;scopeContainer&gt;</b><br>
     * <b>&lt;scope id="123"&gt;</b><br>
     * <i>scope description goes here</i><br>
     * <b>&lt;/scope&gt;</b><br>
     * <b>&lt;/scopeContainer&gt;</b><br>
     * <br>
     * 
     * @param element
     *            The element under which the the scope link is created
     * @param addElementToContainer
     *            if <code>true</code> it's ensured that the linked element exists in the corresponding container
     *            element. Please note, passing <code>true</code> slowes down XML serialization.
     * @return Returns the Element given as parameter with a additional scope link, and the scope element under the
     *         scope container of the ownerDocument Might return <code>null</code> if element was <code>null</code>.
     * @see org.w3c.dom.Element
     * @since 2.0
     */
    Element addAsLinkToXmlElement(Element element, boolean addElementToContainer);

    // -------------------------------------------------------------------
    /**
     * Adds the scope link to an given XML DOM Element The scope element itself will <b>NOT</b> be attached to given
     * elements ownerDocument. Calling this method is equal to calling <code>addAsLinkToXmlElement</code> with
     * parameters <code>element, false</code><br>
     * Example:<br>
     * &lt;parameterElement&gt;<br>
     * <b>&lt;scopeLink&gt;123&lt;/observerLink&gt;</b><br>
     * &lt;/parameterElement&gt;<br>
     * <br>
     * 
     * @param element
     *            The element under which the the scope link is created
     * @return Returns the Element given as parameter with a additional scope link Might return <code>null</code> if
     *         element was <code>null</code>.
     * @see org.w3c.dom.Element
     */
    Element addAsLinkToXmlElement(Element element);

    // -------------------------------------------------------------------
    /**
     * Returns the aperture of the scope.<br>
     * The aperture can be any positive float value.
     * 
     * @return Returns the aperture of the scope
     */
    float getAperture();

    // -------------------------------------------------------------------
    /**
     * Returns the focal length of the scope.<br>
     * This value might return <code>Float.NaN</code> in case the focal length is not set for this scope. In that case
     * the magnification must return a value.
     * 
     * @return Returns the focal length of the scope, or Float.NaN if the value does not suit this scope
     */
    float getFocalLength();

    // -------------------------------------------------------------------
    /**
     * Returns the light grasp value of the scope.<br>
     * Allowed values are between 0.0 and 1.0 (including 0.0 and 1.0)<br>
     * This value might return <code>Float.NaN</code> in case the light grasp was never set.
     * 
     * @return Returns the light grasp value of the scope, or Float.NaN if the value was never set.
     */
    float getLightGrasp();

    // -------------------------------------------------------------------
    /**
     * Returns the magnification of the scope.<br>
     * This value might return <code>Float.NaN</code> in case the magnification is not set for this scope. In that case
     * the focal length must return a value.
     * 
     * @return Returns the magnification of the scope, or Float.NaN if the value does not suit this scope
     */
    float getMagnification();

    // -------------------------------------------------------------------
    /**
     * Returns the scopes model name.<br>
     * 
     * @return Returns the model name of the scope
     */
    String getModel();

    // -------------------------------------------------------------------
    /**
     * Returns the scope type.<br>
     * E.g. Newton, Binocular, Reflector...<br>
     * If abbreviation is returned, here's the mapping: A: Naked eye<br>
     * C: Cassegrain<br>
     * B: Binoculars<br>
     * S: Schmidt-Cassegrain<br>
     * N: Newton<br>
     * K: Kutter (Schiefspiegler)<br>
     * R: Refractor<br>
     * M: Maksutov<br>
     * <br>
     * 
     * This method might return <code>null</code> if the type was never set.
     * 
     * @return Returns the scope type, or <code>null</code> if the type was never set.
     */
    String getType();

    // -------------------------------------------------------------------
    /**
     * Returns the true field of view, if set.<br>
     * Might return <code>NULL</code> as the field is optional only if magnification is set.
     * 
     * @return Returns the true field of view of the scope, or <code>NULL</code> if the value was never set.
     */
    Angle getTrueFieldOfView();

    // -------------------------------------------------------------------
    /**
     * Returns the scope's vendor name.<br>
     * E.g. Celestron, TeleVue, Meade, Vixen...<br>
     * This method might return <code>null</code> if the vendor was never set.
     * 
     * @return Returns the scope's vendor name, or <code>null</code> if the type was never set.
     */
    String getVendor();

    // -------------------------------------------------------------------
    /**
     * Returns the scopes picture vertical orientation.
     * 
     * @return <b>true</b> if the scopes picture is errected
     * @throws IllegalStateException
     *             if orientation was not set by the user, so the class cannot return <b>true</b> or <b>false</b>
     */
    boolean isOrientationErected() throws IllegalStateException;

    // -------------------------------------------------------------------
    /**
     * Returns the scopes picture horizontal orientation.
     * 
     * @return <b>true</b> if the scopes picture is truesided
     * @throws IllegalStateException
     *             if orientation was not set by the user, so the class cannot return <b>true</b> or <b>false</b>
     */
    boolean isOrientationTruesided() throws IllegalStateException;

    // -------------------------------------------------------------------
    /**
     * Sets the true field of view, if magnification is given.<br>
     * 
     * @param tfov
     *            The true field of view of the scope
     * @throws IllegalArgumentException
     *             if focal length is set.
     */
    void setTrueFieldOfView(Angle tfov) throws IllegalArgumentException;

    // -------------------------------------------------------------------
    /**
     * Sets the aperture of the scope.<br>
     * The aperture must be a positive float value.
     * 
     * @param aperture
     *            The new aperture of the scope
     * @throws IllegalArgumentException
     *             if the given aperture is smaller or equal 0.0 or if the aperture is Float.NaN
     */
    void setAperture(float aperture) throws IllegalArgumentException;

    // -------------------------------------------------------------------
    /**
     * Sets the focal length of the scope.<br>
     * Throws IllegalArgumentException if the given parameter is Float.NaN and magnification is not set. This mechanism
     * should prevent that both values contain an invalid value at the same time. Either focal length <b>or</b>
     * magnification must be set. Also if the new focal Length is smaller or equal 0.0 a IllegalArgumentException is
     * thrown.
     * 
     * @param focalLength
     *            The new focal length of the scope
     * @throws IllegalArgumentException
     *             if the given focal length is smaller or equal 0.0 or if the new focal length is Float.NaN while
     *             magnification is also Float.NaN. Or magnification is already set.
     */
    void setFocalLength(float focalLength) throws IllegalArgumentException;

    // -------------------------------------------------------------------
    /**
     * Sets the light grasp value of the scope.<br>
     * The light grasp must be a positive float value between 0.0 and 1.0 (including 0.0 and 1.0).
     * 
     * @param lightGrasp
     *            The new light grasp value of the scope
     */
    void setLightGrasp(float lightGrasp);

    // -------------------------------------------------------------------
    /**
     * Sets the magnification of the scope.<br>
     * Throws IllegalArgumentException if the given parameter is Float.NaN and focal length is not set. This mechanism
     * should prevent that both values contain an invalid value at the same time. Either focal length <b>or</b>
     * magnification must be set.
     * 
     * @param magnification
     *            The new magnification value of the scope
     * @throws IllegalArgumentException
     *             if the given focal length is Float.NaN while focal length is not set. Or focal length is already set.
     */
    void setMagnification(float magnification) throws IllegalArgumentException;

    /**
     * Sets the model name of the scope.<br>
     * The model name cannot be <code>null</code>.
     * 
     * @param model
     *            The new model name of the scope
     * @throws IllegalArgumentException
     *             if the given model name is <code>null</code>
     */
    void setModel(String model) throws IllegalArgumentException;

    // -------------------------------------------------------------------
    /**
     * Sets the type of the scope.<br>
     * E.g. Newton, Reflector, Binocular... The type is optional but should be given if known!<br>
     * When applicable, the following coding (according to the DSL) should be used:<br>
     * A: Naked eye C: Cassegrain<br>
     * B: Binoculars S: Schmidt-Cassegrain<br>
     * N: Newton K: Kutter (Schiefspiegler)<br>
     * R: Refractor M: Maksutov <br>
     * <br>
     * 
     * @param type
     *            The new type of the scope
     */
    void setType(String type);

    // -------------------------------------------------------------------
    /**
     * Sets the vendor name of the scope.<br>
     * 
     * @param vendor
     *            The new vendor name of the scope
     */
    void setVendor(String vendor);

    // -------------------------------------------------------------------
    /**
     * Sets the orientation of the scope.<br>
     * 
     * @param errected
     *            <code>true</code> if the scope produces errected pictures
     * @param truesided
     *            <code>true</code> if the scope produces truesided pictures
     */
    void setOrientation(boolean errected, boolean truesided);

}
