package de.lehmannet.om;

public interface IEquipment extends ICloneable {

    // Constant for XML representation: comment indicating that this element is no
    // longer available (e.g. has been sold, got broken, ...)
    String XML_COMMENT_ELEMENT_NOLONGERAVAILABLE = "Element no longer available";

    // ------------------------------------------------------------------------
    /**
     * Returns <code>true</code> if this element is still available for use-<br>
     *
     * @return a boolean with the availability of the element
     */
    boolean isAvailable();

    // ------------------------------------------------------------------------
    /**
     * Sets the availability of this element.<br>
     *
     * @param available
     *            A boolean value indicating whether this element is still available for usage
     */
    void setAvailability(boolean available);

}
