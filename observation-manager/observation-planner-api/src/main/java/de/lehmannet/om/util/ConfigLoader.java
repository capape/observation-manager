/*
 * ====================================================================
 * /util/ConfigLoader.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.util;

import de.lehmannet.om.SchemaOalTypeInfo;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ConfigLoader is used to find config files inside the classpath (and the extension directory), and if config files
 * are found, it can provide easy access to the config information.
 *
 * @author doergn@users.sourceforge.net
 * @since 1.0
 */
public class ConfigLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigLoader.class);

    // ------------------
    // Instance variables ------------------------------------------------
    // ------------------
    // All target xsi:types as key and Java classname as value
    private static final Map<String, String> targets = new ConcurrentHashMap<>();
    // All finding xsi:types as key and Java classname as value
    private static final Map<String, String> findings = new ConcurrentHashMap<>();
    // All target xsi:types as key and finding xsi:types as value
    private static final Map<String, String> target_findings = new ConcurrentHashMap<>();

    private static final Object LOCK = new Object();

    // --------------
    // Public methods ----------------------------------------------------
    // --------------
    /**
     * Returns the java classname for a target that matches the given xsi:type attribute, which can be found at
     * additional schema elements<br>
     * E.g.:<br>
     * <target id="someID" <b>xsi:type="oal:deepSkyGX"</b>><br>
     * // More Target data goes here<br>
     * </target><br>
     * If for example the type "oal:deepSkyGX" would be passed to this method, it would return the classname:
     * "de.lehmannet.om.deepSky.DeepSkyTarget". The classname may then be used to load the corresponding java class via
     * java reflection API for a given schema element.
     *
     * @param ptype
     *            The xsi:type value which can be found at additional schema elements (can be a finding xsi:type or an
     *            target xsi_type)
     * @return The corresponding target java classname for the given type, or <code>null</code> if the type could not be
     *         resolved.
     * @throws ConfigException
     *             if problems occured during load of config
     */
    public static String getTargetClassnameFromType(String ptype) throws ConfigException {

        checkValidPtype(ptype);

        LOGGER.debug("Searching class for type: {}", ptype);
        synchronized (LOCK) {
            if (targets.isEmpty()) {
                LOGGER.debug("NO targets configured. Loading config");
                loadConfig();
            }
        }

        String type = ConfigLoader.checkAncestorTypes(ptype);
        LOGGER.debug("Real type to search: {}", type);

        if (!targets.containsKey(type)) { // Given type is finding type...try to get target type
            LOGGER.debug("Type  not in targets. Searching in findings");
            Iterator<Entry<String, String>> iterator =
                    target_findings.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, String> entry = iterator.next();
                String currentKey = entry.getKey();
                String currentValue = entry.getValue();
                if (type.equals(currentValue)) {
                    type = currentKey;
                    break;
                }
            }
        }
        String classname = targets.get(type);
        checkValidClassname(type, classname);

        LOGGER.debug("Found {} for {}", classname, type);
        return classname;
    }

    private static void checkValidClassname(String type, String classname) throws ConfigException {
        if ((classname == null) || ("".equals(classname.trim()))) {
            throw new ConfigException("No target class defined for target type: " + type
                    + ". Please check plugin Manifest files, or download new extension. ");
        }
    }

    private static void checkValidPtype(String ptype) {
        if (ptype == null) {
            throw new IllegalArgumentException("Ptype is null");
        }
    }

    /**
     * Returns the java classname for a finding that matches the given xsi:type attribute, which can be found at
     * additional schema elements<br>
     * E.g.:<br>
     * <result id="someID" <b>xsi:type="oal:findingsDeepSky"</b>><br>
     * // More finding data goes here<br>
     * </result><br>
     * If for example the type "oal:findingsDeepSky" would be passed to this method, it would return the classname:
     * "de.lehmannet.om.extension.deepSky.DeepSkyFinding". The classname may then be used to load the corresponding java
     * class via java reflection API for a given schema element.
     *
     * @param ptype
     *            The xsi:type value which can be found at additional schema elements (can be a finding xsi:type or an
     *            target xsi_type)
     * @return The corresponding finding java classname for the given type, or <code>null</code> if the type could not
     *         be resolved.
     * @throws ConfigException
     *             if problems occured during load of config
     */
    public static String getFindingClassnameFromType(String ptype) throws ConfigException {
        checkValidPtype(ptype);
        synchronized (LOCK) {
            if (findings.isEmpty()) {
                loadConfig();
            }
        }
        String type = ConfigLoader.checkAncestorTypes(ptype);

        if (!findings.containsKey(type)) { // Given type is target type...try to get finding type
            Iterator<Entry<String, String>> iterator =
                    target_findings.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, String> entry = iterator.next();
                String currentKey = entry.getKey();
                String currentValue = entry.getValue();
                if (type.equals(currentKey)) {
                    type = currentValue;
                    break;
                }
            }
        }

        final String classname = findings.get(type);
        checkValidClassname(type, classname);

        LOGGER.debug("Found {} for {}", classname, type);
        return classname;
    }

    // ---------------
    // Private methods ---------------------------------------------------
    // ---------------
    private static void loadConfig() throws ConfigException {
        // Add fixed generic elements (no extenstion package required)
        ConfigLoader.addGenericElements();
    }

    private static void addGenericElements() {

        // This is the most simple element relation
        final String target_type = "oal:observationTargetType";
        final String target_classname = "de.lehmannet.om.GenericTarget";
        final String finding_type = "oal:findingsType";
        final String finding_classname = "de.lehmannet.om.GenericFinding";

        // Add type and classname to our list of known types
        synchronized (LOCK) {
            targets.put(target_type, target_classname);
            findings.put(finding_type, finding_classname);
            target_findings.put(target_type, finding_type);
        }

        // This is a simple star
        final String starTarget_type = "oal:starTargetType";
        final String starTarget_classname = "de.lehmannet.om.TargetStar";
        final String starTarget_finding_type = "oal:findingsType";
        final String starTarget_finding_classname = "de.lehmannet.om.GenericFinding";

        // Add type and classname to our list of known types
        synchronized (LOCK) {
            targets.put(starTarget_type, starTarget_classname);
            findings.put(starTarget_finding_type, starTarget_finding_classname);
            target_findings.put(starTarget_type, starTarget_finding_type);
        }
    }

    // Check on old xsi types/names (before OAL 2.0)
    private static String checkAncestorTypes(String type) {

        if (type.startsWith("fgca")) {
            return type.replaceAll("fgca", "oal");
        } else {
            return type;
        }
    }

    public static void loadInternalExtension(SchemaOalTypeInfo schemaOalTypeInfo) {

        synchronized (LOCK) {
            if (hasTargetDefined(schemaOalTypeInfo)) {
                targets.put(schemaOalTypeInfo.getTargetType(), schemaOalTypeInfo.getTargetClassName());
            }

            if (hasFindingDefined(schemaOalTypeInfo)) {
                findings.put(schemaOalTypeInfo.getFindingType(), schemaOalTypeInfo.getFindingClassName());
            }

            if (hasTypesDefined(schemaOalTypeInfo)) {
                target_findings.put(schemaOalTypeInfo.getTargetType(), schemaOalTypeInfo.getFindingType());
            }
        }
    }

    private static boolean hasTypesDefined(SchemaOalTypeInfo schemaOalTypeInfo) {
        return StringUtils.isNotBlank(schemaOalTypeInfo.getTargetType())
                && StringUtils.isNotBlank(schemaOalTypeInfo.getFindingType());
    }

    private static boolean hasFindingDefined(SchemaOalTypeInfo schemaOalTypeInfo) {
        return StringUtils.isNotBlank(schemaOalTypeInfo.getFindingType())
                && StringUtils.isNotBlank(schemaOalTypeInfo.getFindingClassName());
    }

    private static boolean hasTargetDefined(SchemaOalTypeInfo schemaOalTypeInfo) {
        return StringUtils.isNotBlank(schemaOalTypeInfo.getTargetType())
                && StringUtils.isNotBlank(schemaOalTypeInfo.getTargetClassName());
    }
}
