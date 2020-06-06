/* ====================================================================
 * /IExtendableSchemaElement.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om;

/**
 * Some schema elements (e.g. IFinding, ITarget) have to be extended in several different types (e.g. DeepSkyTarget,
 * VariableStarTarget..) representing several different astronomical objects.<br>
 * These interfaces extend IExtendableSchemaElement, as it provides access to e.g. XML XSI Type information.<br>
 * 
 * @author doergn@users.sourceforge.net
 * @since 1.0
 */
public interface IExtendableSchemaElement {

    // ---------
    // Constants ---------------------------------------------------------
    // ---------

    /**
     * Constant for XML Schema Instance type.<br>
     * As target elements my differ from type to type (i.e. DeepSkyTarget, VariableStarTarget...) this constant can
     * identifies a type.<br>
     * Example:<br>
     * &lt;target xsi:type="oal:deepSkyGX"&gt;<i>More stuff goes here</i>&lt;/target&gt;
     */
    String XML_XSI_TYPE = "xsi:type";

    // --------------
    // Public Methods ----------------------------------------------------
    // --------------

    /**
     * Returns the XML schema instance type of the implementation.<br>
     * Example:<br>
     * <target xsi:type="myOwnTarget"><br>
     * </target><br>
     * 
     * @return The xsi:type value of this implementation
     */
    String getXSIType();

}
