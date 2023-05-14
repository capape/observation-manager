package de.lehmannet.om.ui.i18n;

import java.util.ResourceBundle;

import de.lehmannet.om.ui.util.LocaleToolsFactory;

// import javax.swing.JComponent;

public class TextManagerImpl implements TextManager {

    private final String resource;
    private ResourceBundle bundle;
    private String isoLanguage;

    public TextManagerImpl(String resource, String isoLanguage) {
        this.resource = resource;
        this.isoLanguage = isoLanguage.trim();
        this.bundle = LocaleToolsFactory.appInstance().getBundle(resource, isoLanguage);

    }

    @Override
    public String getString(String key) {

        final String value = this.bundle.getString(key);
        if (value == null) {
            return key;
        }
        return this.bundle.getString(key);
    }

    @Override
    public String getCurrentLanguage() {
        return this.isoLanguage;
    }

    @Override
    public void useLanguage(String newIsoKey) {
        if (!this.isoLanguage.equalsIgnoreCase(newIsoKey.trim())) {
            this.isoLanguage = newIsoKey.trim();
            this.bundle = LocaleToolsFactory.appInstance().getBundle(resource, newIsoKey);
        }

    }

    @Override
    public ResourceBundle getBundle() {
        return this.bundle;
    }

}