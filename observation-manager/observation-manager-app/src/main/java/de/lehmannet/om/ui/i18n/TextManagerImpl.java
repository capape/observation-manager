package de.lehmannet.om.ui.i18n;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

//import javax.swing.JComponent;

public class TextManagerImpl implements TextManager {

    private final String resource;
    private PropertyResourceBundle bundle;
    private String isoLanguage;

    public TextManagerImpl(String resource, String isoLanguage) {
        this.resource = resource;
        this.isoLanguage = isoLanguage.trim();
        this.bundle = this.initLanguage(isoLanguage);

    }

    private PropertyResourceBundle initLanguage(String isoKey) {

        PropertyResourceBundle bundle;

        // Try to find value in config
        if (isoKey == null) {
            bundle = (PropertyResourceBundle) ResourceBundle.getBundle(resource, Locale.ENGLISH);
        } else {

            try {
                final Locale textLocale = new Locale.Builder().setLanguage(isoKey).build();
                // System.setProperty("user.language", isoKey);
                // System.setProperty("user.region", isoKey);
                // JComponent.setDefaultLocale(Locale.getDefault());

                bundle = (PropertyResourceBundle) ResourceBundle.getBundle(resource, textLocale);
            } catch (final MissingResourceException mre) { // Unknown VM language (and

                bundle = (PropertyResourceBundle) ResourceBundle.getBundle(resource, Locale.ENGLISH);
            }
        }

        return bundle;
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
            this.bundle = this.initLanguage(newIsoKey);
        }

    }

    @Override
    public PropertyResourceBundle getBundle() {
        return this.bundle;
    }

}