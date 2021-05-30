package de.lehmannet.om.ui.cache;

import java.io.File;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import de.lehmannet.om.IObserver;
import de.lehmannet.om.IScope;
import de.lehmannet.om.ISession;
import de.lehmannet.om.ISite;
import de.lehmannet.om.SurfaceBrightness;

public class UIDataCacheImpl implements UIDataCache {

    private Map<String, Object> cache = new ConcurrentHashMap<>();

    public void put(String key, Object value) {
        if (value == null) {
            return;
        }
        this.cache.putIfAbsent(key, value);
    }

    public Object get(String key) {
        return this.cache.get(key);
    }

    public Set<String> keySet() {
        return this.cache.keySet();
    }

    @Override
    public String getString(String key) {
        return (String) this.cache.get(key);
    }

    @Override
    public void putString(String key, String value) {
        if (value == null) {
            return;
        }
        this.cache.putIfAbsent(key, value);

    }

    @Override
    public void remove(String key) {
        this.cache.remove(key);

    }

    @Override
    public void putObserver(String key, IObserver value) {
        if (value == null) {
            return;
        }
        this.cache.putIfAbsent(key, value);

    }

    @Override
    public IObserver getObserver(String key) {
        return (IObserver) this.cache.get(key);
    }

    @Override
    public void putDate(String key, OffsetDateTime value) {
        if (value == null) {
            return;
        }
        this.cache.putIfAbsent(key, value);
    }

    @Override
    public OffsetDateTime getDate(String key) {
        return (OffsetDateTime) this.cache.get(key);
    }

    @Override
    public void putSession(String key, ISession value) {
        if (value == null) {
            return;
        }
        this.cache.putIfAbsent(key, value);

    }

    @Override
    public ISession getSession(String key) {
        return (ISession) this.cache.get(key);
    }

    @Override
    public void putScope(String key, IScope value) {
        if (value == null) {
            return;
        }
        this.cache.putIfAbsent(key, value);

    }

    @Override
    public IScope getScope(String key) {
        return (IScope) this.cache.get(key);
    }

    @Override
    public void putSite(String key, ISite value) {
        if (value == null) {
            return;
        }
        this.cache.putIfAbsent(key, value);

    }

    @Override
    public ISite getSite(String key) {
        return (ISite) this.cache.get(key);
    }

    @Override
    public void putSurfaceBrightness(String key, SurfaceBrightness value) {
        if (value == null) {
            return;
        }
        this.cache.putIfAbsent(key, value);

    }

    @Override
    public SurfaceBrightness getSurfaceBrightness(String key) {
        return (SurfaceBrightness) this.cache.get(key);
    }

    @Override
    public void putInteger(String key, Integer value) {
        if (value == null) {
            return;
        }
        this.cache.putIfAbsent(key, value);

    }

    @Override
    public Integer getInteger(String key) {
        return (Integer) this.cache.get(key);
    }

    @Override
    public void putFloat(String key, Float value) {
        if (value == null) {
            return;
        }
        this.cache.putIfAbsent(key, value);

    }

    @Override
    public Float getFloat(String key) {
        return (Float) this.cache.get(key);
    }

    @Override
    public void putFile(String key, File value) {
        if (value == null) {
            return;
        }
        this.cache.putIfAbsent(key, value);

    }

    @Override
    public File getFile(String key) {

        return (File) this.cache.get(key);
    }
}
