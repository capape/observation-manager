package de.lehmannet.om;

public interface ICloneable extends Cloneable {

    ICloneable getCopy();

    default <T extends ICloneable> T copy() {

        return (T) getCopy();
    }

    static <T extends ICloneable> T copyOrNull(T toCopy) {
        return toCopy == null ? null : toCopy.copy();
    }

}
