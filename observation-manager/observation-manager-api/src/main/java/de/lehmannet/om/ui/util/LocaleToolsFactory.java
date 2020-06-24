package de.lehmannet.om.ui.util;

public class LocaleToolsFactory {

    private static final Object LOCK = new Object();
    private static LocaleTools appInstance;

    public static final LocaleTools extensionInstance() {
        return new LocaleTools.Builder().build();
    }

    public static final LocaleTools appInstance() {

        synchronized (LOCK) {

            if (appInstance == null) {
                appInstance = new LocaleTools.Builder().build();
            }
            return appInstance;
        }
    }
}