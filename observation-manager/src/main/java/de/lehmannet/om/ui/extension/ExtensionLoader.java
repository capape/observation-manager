/* ====================================================================
 * /extension/ExtensionLoader.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

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
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JMenu;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

import de.lehmannet.om.ui.catalog.CatalogLoader;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.preferences.PreferencesPanel;
import de.lehmannet.om.util.ConfigException;
import de.lehmannet.om.util.ConfigLoader;
import de.lehmannet.om.util.SchemaLoader;

public class ExtensionLoader {

    // ---------
    // Constants ---------------------------------------------------------
    // ---------

    // File name of extension config file
    private static final String EXTENSION_FILENAME = "META-INF/OM_EXTENSION";

    // Config file key for extension major class
    private static final String CONFIG_FILE_ENTRY_EXTENSION_CLASS = "Extension_Class";

    private static final Logger LOG = LoggerFactory.getLogger(ExtensionLoader.class);

    // ------------------
    // Instance Variables ------------------------------------------------
    // ------------------

    private final List<IExtension> extensions = new LinkedList<>();

    private ObservationManager om = null;

    private CatalogLoader catalogLoader = null;

    private SchemaUILoader schemaUILoader = null;

    private URLClassLoader extensionClassLoader = null;

    private PopupMenuExtension[] cachedPopupMenus = null;

    private JMenu[] cachedMenus = null;

    // ------------
    // Constructors ------------------------------------------------------
    // ------------

    public ExtensionLoader(ObservationManager om) {

        this.om = om;
        this.extensionClassLoader = URLClassLoader.newInstance(new URL[0],  ClassLoader.getSystemClassLoader());
        this.loadExtensions();

        this.catalogLoader = new CatalogLoader(this.om, this.extensions);
        this.schemaUILoader = new SchemaUILoader(this.om, this.extensions);
        

    }
    

    // --------------
    // Public Methods ----------------------------------------------------
    // --------------

    public String addExtension(ZipFile extension) {

        // Unpack ZIP file
        Enumeration<? extends ZipEntry> enumeration = extension.entries();
        ZipEntry ze = null;
        File lastFile = null;
        List<File> jars = new ArrayList<>();
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

       
        List<URL> urlArray = addJarsToClassLoader(jars);
        if (urlArray.isEmpty()) {
            return null;
        }
        try {
            ConfigLoader.reloadConfig();
        } catch (ConfigException ce) {
            System.err.println("Error reloading API config: " + ce);
        }

        // Load/add new extension (IExtension implementation)
        ListIterator<File> iterator;
        iterator = jars.listIterator();
        String result = null;
        String tempResult = null;
        while (iterator.hasNext()) {
            tempResult = this.scanJarFile((File) iterator.next(), true);
            result = tempResult == null ? result : tempResult; // At least one jar contained a extension description
                                                               // file
        }

        // Trigger CatalogLoader to reload catalogs (if neccessary)
        this.catalogLoader.update();

        // Clear Menu and PopupMenu Caches
        this.cachedMenus = null;
        this.cachedPopupMenus = null;

        return result;

    }


    private List<URL> addJarToClassLoader(File jar) {
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
            LOG.error("Unable to add jar file to classloader: {} ", current.getAbsolutePath());
        
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

    public List<IExtension> getExtensions() {

        // Create new list to force user to call our addExtension methods
        // when installing a new extension
        List<IExtension> result = new ArrayList<>();
        Iterator<IExtension> iterator = this.extensions.iterator();
        while (iterator.hasNext()) {
            IExtension current = iterator.next();
            // Do not add generic extension
            if (!GenericExtension.NAME.equals(current.getName())) {
                result.add(current);
            }

        }

        return result;

    }

    public void reloadLanguage() {

        for (IExtension extension : this.extensions) {
            extension.reloadLanguage();
        }

        this.cachedMenus = null;
        this.cachedPopupMenus = null;

    }

    public CatalogLoader getCatalogLoader() {

        return this.catalogLoader;

    }

    public SchemaUILoader getSchemaUILoader() {

        return this.schemaUILoader;

    }

    public JMenu[] getMenus() {

        if (this.cachedMenus == null) {

            List<JMenu> result = new ArrayList<>();

            for (IExtension current : this.extensions) {
                if (current.getMenu() != null) {
                    result.add(current.getMenu());
                }
            }

            this.cachedMenus = result.toArray(new JMenu[] {});

        }

        return this.cachedMenus;

    }

    public PopupMenuExtension[] getPopupMenus() {

        if (this.cachedPopupMenus == null) {

            List<PopupMenuExtension> result = new ArrayList<>();

            for (IExtension current : this.extensions) {
                if (current.getPopupMenu() != null) {
                    result.add(current.getPopupMenu());
                }
            }

            this.cachedPopupMenus = result.toArray(new PopupMenuExtension[] {});

        }

        return this.cachedPopupMenus;

    }

    public PreferencesPanel[] getPreferencesTabs() {

        List<PreferencesPanel> result = new ArrayList<>();

        for (IExtension current : this.extensions) {
            if (current.getPreferencesPanel() != null) {
                result.add(current.getPreferencesPanel());
            }
        }

        return result.toArray(new PreferencesPanel[] {});

    }

    // ---------------
    // Private methods ---------------------------------------------------
    // ---------------

    private void loadExtensions() {

        // Add fixed generic elements (no extenstion package required)
        this.addGenericExtension();

        // Get JARs from classpath
        String sep = System.getProperty("path.separator");
        String path = System.getProperty("java.class.path");

        StringTokenizer tokenizer = new StringTokenizer(path, sep);

        File token = null;
        while (tokenizer.hasMoreTokens()) {
            token = new File(tokenizer.nextToken());

            if ((token.isFile()) && (token.getName().endsWith(".jar"))) {
                scanJarFile(token, false);
            }
        }

        // Get JARs under extension path
        String extPath = System.getProperty("extension.dirs");
        File ext = new File(extPath);
        if (ext.exists()) {
            File[] jars = ext.listFiles((dir, name) -> name.toLowerCase().endsWith(".jar"));

            if (jars != null) {
                for (File jar : jars) {
                    scanJarFile(jar, false);
                }
            }
        }

    }

    private String scanJarFile(File jar, boolean update) {

        try (ZipFile archive = new ZipFile(jar)) {

            Enumeration<? extends ZipEntry> enu = archive.entries();

            String result = null;
            String tempResult = null;
            while (enu.hasMoreElements()) {
                ZipEntry entry = enu.nextElement();
                String name = entry.getName();

                if (name.toUpperCase().equals(ExtensionLoader.EXTENSION_FILENAME)) {

                    addJarToClassLoader(jar);
                    try (InputStream in = archive.getInputStream(entry)) {

                        Properties prop = new Properties();
                        prop.load(in);

                        tempResult = this.addExtension(prop, update);
                        result = tempResult == null ? result : tempResult;

                    } catch (IOException ioe) {
                        System.err.println("Error while accessing entry from JAR file.\n" + ioe);
                        return null;
                    }
                    // we can't do anything here
                }
            }

            return result;
        } catch (IOException zipEx) {
            System.err.println("Error while accessing JAR file.\n" + zipEx);
            return null;
        }

    }

    private String addExtension(Properties properties, boolean update) {

        // Get extension name and classname of main extension class
        String className = properties.getProperty(ExtensionLoader.CONFIG_FILE_ENTRY_EXTENSION_CLASS);

        // --- Load extension main class

        // Get Java class
        Class<?> currentClass = null;
        try { // First try default ClassLoader
            currentClass = Class.forName(className);
        } catch (ClassNotFoundException cnfe) {
            try { // Default ClassLoader cannot find it...so try extensionClassLoader
                currentClass = this.extensionClassLoader.loadClass(className);
            } catch (ClassNotFoundException cnfe2) {
                System.err.println("Unable to find class: " + className + "\n" + cnfe2.getMessage());
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

                    if (parameters.length == 0) {
                        extension = (IExtension) constructor.newInstance(); // 0 parameters
                        break;
                    } else if ((parameters.length == 1) && (parameters[0].isInstance(this.om))) {
                        extension = (IExtension) constructor.newInstance(new Object[] { this.om });
                        break;
                    }
                }
            } catch (InstantiationException ie) {
                System.err.println("Unable to instantiate class: " + className + "\n" + ie.getMessage());
                return null;
            } catch (InvocationTargetException ite) {
                System.err.println("Unable to invocate class: " + className + "\n" + ite.getMessage());
                return null;
            } catch (IllegalAccessException iae) {
                System.err.println("Unable to access class: " + className + "\n" + iae.getMessage());
                return null;
            }
        } else {
            System.err.println("Unable to load class: " + className + "\nMaybe class has no default constructor. ");
            return null;
        }

        // --- Add OAL extension (in case the extension was added during runtime
        if (update) {
            boolean oalResult = this.addOALExtenstionElement(extension);
            if (!oalResult) {
                System.err.println("Unable to add oal extension to schema file. Please check log for details.");
                return null;
            }
        }

        // --- Store extension main class
        if (extension != null) {
            if (this.extensions.contains(extension)) {
                LOG.info("Already loaded extension: {} ", extension.getName());
                return extension.getName();
            }
            this.extensions.add(extension);
            LOG.info("Successfully loaded extension: {} ", extension.getName());
            return extension.getName();
        }

        return null;

    }

    private void addGenericExtension() {

        this.extensions.add(new GenericExtension());

    }

    private File unpack(ZipFile zf, ZipEntry ze) {

        InputStream istr = null;
        try {
            istr = zf.getInputStream(ze);
        } catch (IOException ioe) {
            System.err.println("Unable to open input stream from zip file: " + zf + " for entry: " + ze);
            return null;
        }
        BufferedInputStream bis = new BufferedInputStream(istr);

        File file = new File(
                this.om.getInstallDir() + File.separator /* + "testing" + File.separator */ + ze.getName());

        if (ze.isDirectory()) {
            boolean createDir = false;
            if (file.exists()) {
                return file;
            }
            createDir = file.mkdir();
            if (!createDir) {
                System.err.println("Unable to create directory: " + file);
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
                System.err.println("Unable to write file: " + ze + "\n" + ioe);
                return null;
            }

            return file;
        } catch (FileNotFoundException fnfe) {
            System.err.println("Unable to create file: " + file + "\n" + fnfe);
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
        File schema = new File(this.om.getInstallDir().getPathForFolder("schema")
                + SchemaLoader.VERSIONS[SchemaLoader.VERSIONS.length - 1]);

        if (!schema.exists()) {
            System.err.println("Unable to find schema file: " + schema);
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
            System.err.println("Unable to parse file: " + schema + "\n" + pce);
            return false;
        } catch (SAXException saxe) {
            System.err.println("Error while parsing: " + schema + "\n" + saxe);
            return false;
        } catch (IOException ioe) {
            System.err.println("Unable to find file: " + schema + "\n" + ioe);
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
            LOG.error("Could not add extension: {} ", schema.getAbsolutePath(), ex);
            return false;
        } catch (ClassNotFoundException e) {
            LOG.error("Could not add extension: {} ", schema.getAbsolutePath(), e);
            return false;
        } catch (InstantiationException e) {
            LOG.error("Could not add extension: {} ", schema.getAbsolutePath(), e);
            return false;
        } catch (IllegalAccessException e) {
            LOG.error("Could not add extension: {} ", schema.getAbsolutePath(), e);
            return false;
        } catch (ClassCastException e) {
            LOG.error("Could not add extension: {} ", schema.getAbsolutePath(), e);
            return false;
        }

    }

}
