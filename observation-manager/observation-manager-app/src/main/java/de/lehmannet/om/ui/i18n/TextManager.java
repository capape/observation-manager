package de.lehmannet.om.ui.i18n;

public interface TextManager {

    String getString(String key);

    String getCurrentLanguage();

    void useLanguage(String isoKey);

}