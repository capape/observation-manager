package de.lehmannet.om.ui.cache;

import java.io.File;
import java.time.OffsetDateTime;
import java.util.Set;

import de.lehmannet.om.IObserver;
import de.lehmannet.om.IScope;
import de.lehmannet.om.ISession;
import de.lehmannet.om.ISite;
import de.lehmannet.om.SurfaceBrightness;

public interface UIDataCache {

    /*
     * void put(String key, Object value);
     * 
     * Object get(String key);
     */

    String getString(String key);

    void putString(String key, String value);

    void putObserver(String key, IObserver value);

    IObserver getObserver(String key);

    void putDate(String key, OffsetDateTime value);

    OffsetDateTime getDate(String key);

    void putSession(String key, ISession value);

    ISession getSession(String key);

    void putScope(String key, IScope value);

    IScope getScope(String key);

    void putSite(String key, ISite value);

    ISite getSite(String key);

    void putSurfaceBrightness(String key, SurfaceBrightness value);

    SurfaceBrightness getSurfaceBrightness(String key);

    void putInteger(String key, Integer value);

    Integer getInteger(String key);

    void putFloat(String key, Float value);

    Float getFloat(String key);

    void putFile(String key, File value);

    File getFile(String key);

    void remove(String key);

    Set<String> keySet();
}
