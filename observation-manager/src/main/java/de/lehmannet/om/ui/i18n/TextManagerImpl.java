package de.lehmannet.om.ui.i18n;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.JComponent;

public class TextManagerImpl implements TextManager {

    private static final String DEFAULT_RESOURCE = "ObservationManager";
    private PropertyResourceBundle bundle;
    private String isoLanguage;

    public TextManagerImpl(String isoLanguage) {
        this.isoLanguage = isoLanguage.trim();
        this.bundle = this.initLanguage(isoLanguage);
    }

    private PropertyResourceBundle initLanguage(String isoKey) {

        PropertyResourceBundle bundle;

        // Try to find value in config
        if (isoKey != null) {
            Locale.setDefault(new Locale(isoKey, isoKey));
            System.setProperty("user.language", isoKey);
            System.setProperty("user.region", isoKey);
            JComponent.setDefaultLocale(Locale.getDefault());
        }

        try {
            bundle = (PropertyResourceBundle) ResourceBundle.getBundle(DEFAULT_RESOURCE, Locale.getDefault());
        } catch (final MissingResourceException mre) { // Unknown VM language (and
            // language not explicitly
            // set)
            Locale.setDefault(Locale.ENGLISH);
            bundle = (PropertyResourceBundle) ResourceBundle.getBundle(DEFAULT_RESOURCE, Locale.getDefault());
        }

        return bundle;
    }

    @Override
    public String getString(String key) {
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