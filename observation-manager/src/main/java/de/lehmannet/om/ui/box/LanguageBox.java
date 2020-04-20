/* ====================================================================
 * /box/LanguageBox.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.box;

import java.util.*;

import javax.swing.JComboBox;

public class LanguageBox extends JComboBox {

    /**
     *
     */
    private static final long serialVersionUID = -1072100720767774207L;

    private static final String EMPTY_ENTRY = "----";

    // Allow empty entry
    private boolean allowEmptyEntry = true;

    // Use tree map for sorting
    private final Map<String, String> map = new TreeMap<>();

    private LanguageBox(List<String> acceptedLanguages, boolean acceptEmptyEntry) {

        // Load language file (default locale is set by OM)
        PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle.getBundle("contentLanguages",
                Locale.getDefault());

        this.allowEmptyEntry = acceptEmptyEntry;

        // Add empty item (as first entry)
        if (this.allowEmptyEntry) {
            this.addItem(LanguageBox.EMPTY_ENTRY);
        }

        // Put all isoKeys and language strings in a TreeMap which will sort them
        if (this.map.size() == 0) { // Do only once (static)
            Enumeration<String> e = bundle.getKeys();
            String isoKey = null;
            String lang = null;
            while (e.hasMoreElements()) {
                isoKey = e.nextElement();
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

        if ((isoKey == null) || ("".equals(isoKey.trim()))) {
            // Don't change current value
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
        System.out.println("ISO language key unknown: " + isoKey);

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
