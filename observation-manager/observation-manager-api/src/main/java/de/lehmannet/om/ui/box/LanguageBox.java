/*
 * ====================================================================
 * /box/LanguageBox.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.box;

import de.lehmannet.om.ui.util.LocaleToolsFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.JComboBox;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LanguageBox extends JComboBox<String> {

    /**
     *
     */
    private static final long serialVersionUID = -1072100720767774207L;

    private static final Logger LOGGER = LoggerFactory.getLogger(LanguageBox.class);
    private static final String EMPTY_ENTRY = "----";

    // Allow empty entry
    private boolean allowEmptyEntry = true;

    // Use tree map for sorting
    private final Map<String, String> map = new TreeMap<>();

    private LanguageBox(List<String> acceptedLanguages, boolean acceptEmptyEntry) {

        // Load language file (default locale is set by OM)
        ResourceBundle bundle = LocaleToolsFactory.appInstance().getBundle("contentLanguages", Locale.getDefault());

        this.allowEmptyEntry = acceptEmptyEntry;

        // Add empty item (as first entry)
        if (this.allowEmptyEntry) {
            this.addItem(LanguageBox.EMPTY_ENTRY);
        }

        // Put all isoKeys and language strings in a TreeMap which will sort them
        if (this.map.size() == 0) { // Do only once (static)
            Set<String> e = bundle.keySet();
            String lang = null;
            for (String isoKey : e) {

                if (!acceptedLanguages.isEmpty()) { // An empty list = accept all languages
                    if (!acceptedLanguages.contains(isoKey)) { // Check if language is allowed
                        continue;
                    }
                }
                lang = bundle.getString(isoKey);
                this.map.put(lang, isoKey);
            }
        }

        // Add all languagestrings to super class (now as they're sorted)
        Iterator<String> i = map.keySet().iterator();
        String key = null;
        while (i.hasNext()) {
            key = i.next();
            this.addItem(key);
        }

        // Preselect EMPTY_ENTRY or default language
        this.setDefaultEntry();

        // No typing in the box allowed
        this.setEditable(false);
    }

    public LanguageBox(boolean allowEmptyEntry) {

        this(new ArrayList<String>(), allowEmptyEntry);
    }

    public LanguageBox(String isoKey, boolean allowEmptyEntry) {

        this(new ArrayList<String>(), allowEmptyEntry);
        this.setLanguage(isoKey);
    }

    public LanguageBox(List<String> acceptedLanguages, String isoKey, boolean allowEmptyEntry) {

        this(acceptedLanguages, allowEmptyEntry);
        this.setLanguage(isoKey);
    }

    // --------------
    // Public Methods ---------------------------------------------------------
    // --------------

    public String getSelectedISOLanguage() {

        Object si = this.getSelectedItem();
        if (LanguageBox.EMPTY_ENTRY.equals(si)) {
            return null;
        } else {
            return (String) map.get(si);
        }
    }

    public void setLanguage(String isoKey) {

        if (StringUtils.isBlank(isoKey)) {
            return;
        }

        isoKey = isoKey.toLowerCase().trim();
        if (this.map.containsValue(isoKey)) {
            Iterator<String> i = this.map.keySet().iterator();
            String current = null;
            while (i.hasNext()) {
                current = i.next();
                if (isoKey.equals(this.map.get(current))) {
                    this.setSelectedItem(current);
                    return;
                }
            }
        }

        // Key not found
        this.setDefaultEntry();
        LOGGER.warn("ISO language key unknown: {}", isoKey);
    }

    private void setDefaultEntry() {

        if (this.allowEmptyEntry) {
            this.setSelectedItem(LanguageBox.EMPTY_ENTRY);
        } else { // Cannot set EMPTY_VALUE
            if (this.map.containsValue(Locale.getDefault().getLanguage())) { // Try to set VM language
                this.setLanguage(Locale.getDefault().getLanguage());
            } else {
                // We cannot set the EMPTY_ENTRY, nor the VM language, as it's not accepted
                // Set en as very last fallback, as this will always work
                this.setLanguage(Locale.ENGLISH.getLanguage());
            }
        }
    }
}
