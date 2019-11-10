package de.lehmannet.om;

public interface IEquipment {

    // Constant for XML representation: comment indicating that this element is no
    // longer available (e.g. has been sold, got broken, ...)
    public static final String XML_COMMENT_ELEMENT_NOLONGERAVAILABLE = "Element no longer available";

    // ------------------------------------------------------------------------
    /**
     * Returns <code>true</code> if this element is still available for use-<br>
     * 
     * @return a boolean with the availability of the element
     */
    public boolean isAvailable();

    // ------------------------------------------------------------------------
    /**
     * Sets the availability of this element.<br>
     * 
     * @param available A boolean value indicating whether this element is still
     *                  available for usage
     */
    public void setAvailability(boolean available);

}
