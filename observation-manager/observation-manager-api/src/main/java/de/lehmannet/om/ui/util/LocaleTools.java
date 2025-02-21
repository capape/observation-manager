package de.lehmannet.om.ui.util;

import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

public class LocaleTools {

    private final Map<Pair<String, Locale>, ResourceBundle> bundles;
    private static final Object LOCK = new Object();

    private LocaleTools() {
        bundles = new ConcurrentHashMap<>();
    }

    public final ResourceBundle getBundle(String resource, String isoKey) {

        if (isoKey == null) {
            return getBundle(resource, Locale.ENGLISH);
        }
        final Locale locale = new Locale.Builder().setLanguage(isoKey).build();
        return getBundle(resource, locale);
    }

    public final ResourceBundle getBundle(String resource, Locale locale) {

        if (StringUtils.isBlank(resource)) {
            throw new IllegalArgumentException("Invalid resource");
        }

        // Try to find value in config
        final Locale localeToUse;
        if (locale == null) {
            localeToUse = Locale.ENGLISH;
        } else {
            localeToUse = locale;
        }

        final Pair<String, Locale> key = Pair.of(resource, localeToUse);

        synchronized (LOCK) {
            ResourceBundle bundle = bundles.get(key);
            if (bundle != null) {
                return bundle;
            }

            bundle = readBundle(resource, locale);
            bundles.put(key, bundle);
            return bundle;
        }
    }

    private ResourceBundle readBundle(String resource, Locale locale) {

        try {
            return ResourceBundle.getBundle(resource, locale);
        } catch (final MissingResourceException mre) { // Unknown VM language (and

            try {
                return ResourceBundle.getBundle(resource, Locale.ENGLISH);
            } catch (final MissingResourceException mre1) {

                try {
                    return ResourceBundle.getBundle(resource);
                } catch (final MissingResourceException mre2) {
                    return ResourceBundle.getBundle("ObservationManager", Locale.ENGLISH);
                }
            }
        }
    }

    public static class Builder {

        public LocaleTools build() {
            return new LocaleTools();
        }
    }
}
