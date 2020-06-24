package de.lehmannet.om.ui.i18n;

import java.util.ResourceBundle;

public interface TextManager {

    String getString(String key);

    String getCurrentLanguage();

    void useLanguage(String isoKey);

    /**
     * To remove after refactor all textbundles
     */
    @Deprecated
    ResourceBundle getBundle();

}