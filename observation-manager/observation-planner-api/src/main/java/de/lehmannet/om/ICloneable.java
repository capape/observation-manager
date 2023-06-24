package de.lehmannet.om;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface ICloneable extends Cloneable {

    ICloneable getCopy();

    default <T extends ICloneable> T copy() {

        return (T) getCopy();
    }

    static <T extends ICloneable> T copyOrNull(T toCopy) {
        return toCopy == null ? null : toCopy.copy();
    }

    static <T extends ICloneable> List<T> copyToList(T[] toCopy) {
        if (toCopy == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(toCopy).stream().map(a -> copyOrNull(a)).toList();
    }
}
