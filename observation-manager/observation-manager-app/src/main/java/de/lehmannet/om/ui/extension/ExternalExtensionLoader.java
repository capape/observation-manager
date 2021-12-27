package de.lehmannet.om.ui.extension;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

import de.lehmannet.om.util.ConfigException;
import de.lehmannet.om.util.ConfigLoader;
import de.lehmannet.om.util.SchemaLoader;

public class ExternalExtensionLoader {

    /**
     * Property to define extesions dir.
     */
    public static final String EXTENSIONS_DIR_PROPERTY = "extensions.dir";

    // ---------
    // Constants ---------------------------------------------------------
    // ---------
    // Extension of config files
    private static final String MANIFEST_FILENAME = "META-INF/SCHEMATYPE";
    // Config file XSI relation entry: XSI_Type target ending
    private static final String CONFIG_FILE_TARGETENTRY_TYPE_ENDING = "XSI_Relation_Type";
    // Config file XSI relation entry: XSI_Type target ending
    private static final String CONFIG_FILE_TARGETENTRY_CLASSNAME_ENDING = "XSI_Relation_Class";
    // Config file XSI relation entry: XSI_Type finding ending
    private static final String CONFIG_FILE_FINDINGENTRY_TYPE_ENDING = "XSI_Relation_Finding_Type";
    // Config file XSI relation entry: XSI_Type finding ending
    private static final String CONFIG_FILE_FINDINGENTRY_CLASSNAME_ENDING = "XSI_Relation_Finding_Class";

    private static final Object LOCK = new Object();

    // File name of extension config file
    private static final String EXTENSION_FILENAME = "META-INF/OM_EXTENSION";

    // Config file key for extension major class
    private static final String CONFIG_FILE_ENTRY_EXTENSION_CLASS = "Extension_Class";

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalExtensionLoader.class);

    private URLClassLoader extensionClassLoader = URLClassLoader.newInstance(new URL[0],
            ClassLoader.getSystemClassLoader());

    private final IExtensionContext context;

    public ExternalExtensionLoader(IExtensionContext context) {
        this.context = context;
    }

    public IExtension addExtension(ZipFile extension) {

        List<File> jars = this.getJarFilesInExtensionFile(extension);
        List<URL> urlArray = this.addJarsToClassLoader(jars);

        if (urlArray.isEmpty()) {
            return null;
        }
        try {
            ConfigLoader.reloadConfig();
        } catch (ConfigException ce) {
            LOGGER.error("Error reloading API config ", ce);
        }

        // Load/add new extension (IExtension implementation)
        ListIterator<File> iterator;
        iterator = jars.listIterator();
        IExtension result = null;
        IExtension tempResult = null;
        while (iterator.hasNext()) {
            tempResult = this.scanJarFile((File) iterator.next(), true);
            result = tempResult == null ? result : tempResult; // At least one jar contained a extension description
                                                               // file
        }

        return result;

    }

    private List<File> getJarFilesInExtensionFile(ZipFile extension) {
        List<File> jars = new ArrayList<>();

        Enumeration<? extends ZipEntry> enumeration = extension.entries();
        File lastFile = null;
        // Unpack ZIP file
        ZipEntry ze = null;
        while (enumeration.hasMoreElements()) {
            ze = enumeration.nextElement();
            lastFile = this.unpack(extension, ze);
            if (lastFile == null) {
                return null;
            }
            if (lastFile.getName().toLowerCase().endsWith(".jar")) {
                jars.add(lastFile);
            }
        }
        return jars;
    }

    private List<URL> addJarToClassLoader(File jar) {

        LOGGER.debug("Adding {} to class loader", jar.getName());
        List<File> files = new ArrayList<>(1);
        files.add(jar);
        return addJarsToClassLoader(files);
    }

    private List<URL> addJarsToClassLoader(List<File> jars) {
        List<URL> urlArray = getClassesToLoad(jars);

        this.updateExtensionClassLoader(urlArray);
        // classes can be found
        SchemaLoader.addClassloader(this.extensionClassLoader);
        return urlArray;
    }

    private List<URL> getClassesToLoad(List<File> jars) {
        // Update classloader
        ListIterator<File> iterator = jars.listIterator();
        List<URL> urlArray = new ArrayList<>();
        File current = null;
        try {
            while (iterator.hasNext()) {
                current = (File) iterator.next();
                urlArray.add(new URL("file:" + current.getAbsolutePath()));
            }
        } catch (MalformedURLException urle) {
            LOGGER.error("Unable to add jar file to classloader: {} ", current.getAbsolutePath());

        }
        return urlArray;
    }

    private void updateExtensionClassLoader(List<URL> urlArray) {
        if (this.extensionClassLoader != null) { // Add already loaded extensions as well
            URL[] oldURLs = this.extensionClassLoader.getURLs();
            urlArray.addAll(Arrays.asList(oldURLs));

        }
        this.extensionClassLoader = URLClassLoader.newInstance((URL[]) urlArray.toArray(new URL[] {}),
                ClassLoader.getSystemClassLoader());
        // Add classloader to XMLCache (-> API SchemaLoader) to make sure extension
    }

    private IExtension scanJarFile(File jar, boolean update) {

        try (ZipFile archive = new ZipFile(jar)) {

            Enumeration<? extends ZipEntry> enu = archive.entries();

            IExtension result = null;
            while (enu.hasMoreElements()) {
                ZipEntry entry = enu.nextElement();
                String name = entry.getName();

                if (name.toUpperCase().equals(ExternalExtensionLoader.EXTENSION_FILENAME)) {

                    addJarToClassLoader(jar);
                    try (InputStream in = archive.getInputStream(entry)) {

                        Properties prop = new Properties();
                        prop.load(in);

                        return this.loadExtension(prop, update);

                    } catch (IOException ioe) {
                        LOGGER.error("Error while accessing entry from JAR file.\n", ioe);
                        return null;
                    }
                    // we can't do anything here
                }
            }

            return result;
        } catch (IOException zipEx) {
            LOGGER.error("Error while accessing JAR file.\n", zipEx);
            return null;
        }

    }

    private IExtension loadExtension(Properties properties, boolean update) {

        // Get extension name and classname of main extension class
        String className = properties.getProperty(ExternalExtensionLoader.CONFIG_FILE_ENTRY_EXTENSION_CLASS);

        // Get Java class
        Class<?> currentClass = null;
        try { // First try default ClassLoader
            currentClass = Class.forName(className);
        } catch (ClassNotFoundException cnfe) {
            try { // Default ClassLoader cannot find it...so try extensionClassLoader
                currentClass = this.extensionClassLoader.loadClass(className);
            } catch (ClassNotFoundException cnfe2) {
                LOGGER.error("Unable to find class:  {} : {}", className, cnfe2.getMessage(), cnfe2);
                return null;
            }
        }

        // Get constructors for class
        Constructor<?>[] constructors = currentClass.getConstructors();
        IExtension extension = null;
        if (constructors.length > 0) {
            try {
                Class<?>[] parameters = null;
                for (Constructor<?> constructor : constructors) {
                    parameters = constructor.getParameterTypes();
                    if ((parameters.length == 1) && (parameters[0].isInstance(this.context))) {
                        extension = (IExtension) constructor.newInstance(new Object[] { this.context });
                        break;
                    }
                }
            } catch (InstantiationException ie) {
                LOGGER.error("Unable to instantiate class: {}:{}, ", className, ie.getMessage(), ie);
                return null;
            } catch (InvocationTargetException ite) {
                LOGGER.error("Unable to invocate class: {}:{}, ", className, ite.getMessage(), ite);
                return null;
            } catch (IllegalAccessException iae) {
                LOGGER.error("Unable to access class: {}:{}, ", className, iae.getMessage(), iae);
                return null;
            }
        } else {
            LOGGER.error("Unable to load class: {}. Maybe class has no default constructor. ", className);
            return null;
        }

        // --- Add OAL extension (in case the extension was added during runtime
        if (update) {
            boolean oalResult = this.addOALExtenstionElement(extension);
            if (!oalResult) {
                LOGGER.error("Unable to add oal extension to schema file. Please check log for details.");
                return null;
            }
        }

        return extension;

    }

    private File unpack(ZipFile zf, ZipEntry ze) {

        InputStream istr = null;
        try {
            istr = zf.getInputStream(ze);
        } catch (IOException ioe) {
            LOGGER.error("Unable to open input stream from zip file: {}  for entry: {}", zf, ze);
            return null;
        }
        BufferedInputStream bis = new BufferedInputStream(istr);

        File file = new File(this.context.getInstallDir().getPathForFile(ze.getName()));

        if (ze.isDirectory()) {
            boolean createDir = false;
            if (file.exists()) {
                return file;
            }
            createDir = file.mkdir();
            if (!createDir) {
                LOGGER.error("Unable to create directory: {} ", file);
                return null;
            } else {
                return file;
            }
        }

        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(file);
            int sz = (int) ze.getSize();
            final int N = 1024;
            byte[] buf = new byte[N];
            int ln = 0;
            try {
                while ((sz > 0) // workaround for bug
                        && ((ln = bis.read(buf, 0, Math.min(N, sz))) != -1)) {
                    fos.write(buf, 0, ln);
                    sz -= ln;
                }
                bis.close();
                fos.flush();
            } catch (IOException ioe) {
                LOGGER.error("Unable to write file: {} ", ze, ioe);
                return null;
            }

            return file;
        } catch (FileNotFoundException fnfe) {
            LOGGER.error("Unable to create file: {}", file, fnfe);
            return null;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }

    private boolean addOALExtenstionElement(IExtension extension) {

        // Get latest schema file
        final String[] versions = SchemaLoader.getVersions();
        File schema = new File(this.context.getInstallDir().getPathForFolder("schema") + versions[versions.length - 1]);

        if (!schema.exists()) {
            LOGGER.error("Unable to find schema file: {}", schema);
            return false;
        }

        // Get schema XML document
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
                "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        dbf.setNamespaceAware(false);
        Document doc = null;
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(new FileInputStream(schema));
        } catch (ParserConfigurationException pce) {
            LOGGER.error("Unable to parse file: {} ", schema, pce);
            return false;
        } catch (SAXException saxe) {
            LOGGER.error("Error while parsing: {}", schema, saxe);
            return false;
        } catch (IOException ioe) {
            LOGGER.error("Unable to find file: {}", schema, ioe);
            return false;
        }

        boolean result = extension.addOALExtensionElement(doc.getDocumentElement());

        try {
            DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();

            DOMImplementationLS domImplLS = (DOMImplementationLS) registry.getDOMImplementation("LS");

            LSSerializer writer = domImplLS.createLSSerializer();

            writer.writeToURI(doc, schema.toURI().toURL().toString());
            return result;
        } catch (IOException ex) {
            LOGGER.error("Could not add extension: {} ", schema.getAbsolutePath(), ex);
            return false;
        } catch (ClassNotFoundException e) {
            LOGGER.error("Could not add extension: {} ", schema.getAbsolutePath(), e);
            return false;
        } catch (InstantiationException e) {
            LOGGER.error("Could not add extension: {} ", schema.getAbsolutePath(), e);
            return false;
        } catch (IllegalAccessException e) {
            LOGGER.error("Could not add extension: {} ", schema.getAbsolutePath(), e);
            return false;
        } catch (ClassCastException e) {
            LOGGER.error("Could not add extension: {} ", schema.getAbsolutePath(), e);
            return false;
        }

    }

    private void readConfigForExtension() throws Exception {
        // Get JARs under extension path
        String extPath = System.getProperty(EXTENSIONS_DIR_PROPERTY);
        if (!StringUtils.isBlank(extPath)) {
            File ext = new File(extPath);
            if (ext.exists()) {
                File[] jars = ext.listFiles((dir, name) -> name.toLowerCase(Locale.getDefault()).endsWith(".jar"));
                if (jars != null) {
                    for (File jar : jars) {
                        LOGGER.debug("Searching configured dir extensions. File {}", jar.getName());
                        scanJarFile(jar);
                    }
                }
            }
        }
    }

    private static void loadExternalConfig() {
        // Get JARs from classpath
        String sep = System.getProperty("path.separator");
        String path = System.getProperty("java.class.path");
        StringTokenizer tokenizer = new StringTokenizer(path, sep);
        File token = null;
        while (tokenizer.hasMoreTokens()) {
            token = new File(tokenizer.nextToken());
            if ((token.isFile()) && (token.getName().endsWith(".jar"))) {
                LOGGER.debug("Searching classpath extensions. File {}", token.getName());
                try {
                    scanJarFile(token);
                } catch (Exception e) {
                    LOGGER.error("Ignoring file {}", token.getName(), e);
                }
            }
        }

    }

    // TODO
    private static void scanJarFile(File jar) throws Exception {

        try (ZipFile archive = new ZipFile(jar)) {
            Enumeration<? extends ZipEntry> enu = archive.entries();
            while (enu.hasMoreElements()) {
                ZipEntry entry = enu.nextElement();
                String name = entry.getName();
                if (name.toUpperCase(Locale.getDefault()).equals(MANIFEST_FILENAME)) {
                    LOGGER.debug("Loading extension {}:{}", jar.getName(), name);
                    try (InputStream in = archive.getInputStream(entry)) {

                        Properties prop = new Properties();
                        prop.load(in);
                        addConfig(prop);
                    } catch (IOException ioe) {
                        throw new Exception("Error while accessing entry from JAR file. ", ioe);
                    }
                    // we can't do anything here
                }
            }
        } catch (IOException zipEx) {
            throw new Exception("Error while accessing JAR file. ", zipEx);
        }

    }

    private static void addConfig(Properties newProperties) {
        Iterator keys = newProperties.keySet().iterator();
        String currentKey = null;
        String prefix = null;
        String target_classname = null;
        String target_type = null;
        String finding_classname = null;
        String finding_type = null;

        while (keys.hasNext()) {
            currentKey = (String) keys.next();
            // Check if key ends with target TYPE ending
            if (currentKey.endsWith(CONFIG_FILE_TARGETENTRY_TYPE_ENDING)) {
                // Get target TYPE value
                target_type = newProperties.getProperty(currentKey);

                // Get prefix (everything that is before our target TYPE ending)
                prefix = currentKey.substring(0, currentKey.lastIndexOf(CONFIG_FILE_TARGETENTRY_TYPE_ENDING));
                // Use prefix and CLASS ending to get target class property
                target_classname = newProperties.getProperty(prefix + CONFIG_FILE_TARGETENTRY_CLASSNAME_ENDING);
                // Use prefix and CLASS ending to get finding class property
                finding_type = newProperties.getProperty(prefix + CONFIG_FILE_FINDINGENTRY_TYPE_ENDING);
                // Use prefix and CLASS ending to get finding class property
                finding_classname = newProperties.getProperty(prefix + CONFIG_FILE_FINDINGENTRY_CLASSNAME_ENDING);

                // Add type and classname to our list of known types
                synchronized (LOCK) {
                    // TODO add to config loader
                    // targets.put(target_type, target_classname);
                    // findings.put(finding_type, finding_classname);
                    // target_findings.put(target_type, finding_type);
                }

            }
        }

    }
}
