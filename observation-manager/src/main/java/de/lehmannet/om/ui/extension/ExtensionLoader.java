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
import java.io.FilenameFilter;
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
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.swing.JMenu;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
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

    // ------------------
    // Instance Variables ------------------------------------------------
    // ------------------

    private LinkedList extensions = new LinkedList();

    private ObservationManager om = null;

    private CatalogLoader catalogLoader = null;

    private SchemaUILoader schemaUILoader = null;

    private URLClassLoader extensionClassLoader = null;

    private PopupMenuExtension[] cachedPopupMenus = null;

    private JMenu[] cachedMenus = null;

    // ------------
    // Constructors ------------------------------------------------------
    // ------------

    // -------------------------------------------------------------------
    public ExtensionLoader(ObservationManager om) {

        this.om = om;

        this.loadExtensions();

        this.catalogLoader = new CatalogLoader(this.om, this.extensions);
        this.schemaUILoader = new SchemaUILoader(this.om, this.extensions);

    }

    // --------------
    // Public Methods ----------------------------------------------------
    // --------------

    // -------------------------------------------------------------------
    public String addExtension(ZipFile extension) {

        // Unpack ZIP file
        Enumeration enumeration = extension.entries();
        ZipEntry ze = null;
        File lastFile = null;
        ArrayList jars = new ArrayList();
        while (enumeration.hasMoreElements()) {
            ze = (ZipEntry) enumeration.nextElement();
            lastFile = this.unpack(extension, ze);
            if (lastFile == null) {
                return null;
            }
            if (lastFile.getName().toLowerCase().endsWith(".jar")) {
                jars.add(lastFile);
            }
        }

        // Update classloader
        ListIterator iterator = jars.listIterator();
        ArrayList urlArray = new ArrayList();
        File current = null;
        try {
            while (iterator.hasNext()) {
                current = (File) iterator.next();
                urlArray.add(new URL("file:" + current.getAbsolutePath()));
            }
        } catch (MalformedURLException urle) {
            System.out.println("Unable to add jar file to classloader: " + current);
            return null;
        }
        if (this.extensionClassLoader != null) { // Add already loaded extensions as well
            URL[] oldURLs = this.extensionClassLoader.getURLs();
            urlArray.addAll(Arrays.asList(oldURLs));
        }
        this.extensionClassLoader = URLClassLoader.newInstance((URL[]) urlArray.toArray(new URL[] {}),
                ClassLoader.getSystemClassLoader());

        // Add classloader to XMLCache (-> API SchemaLoader) to make sure extension
        // classes can be found
        SchemaLoader.addClassloader(this.extensionClassLoader);
        try {
            ConfigLoader.reloadConfig();
        } catch (ConfigException ce) {
            System.err.println("Error reloading API config: " + ce);
        }

        // Load/add new extension (IExtension implementation)
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

    // -------------------------------------------------------------------
    public List getExtensions() {

        // Create new list to force user to call our addExtension methods
        // when installing a new extension
        ArrayList result = new ArrayList();
        Iterator iterator = this.extensions.iterator();
        IExtension current = null;
        while (iterator.hasNext()) {
            current = (IExtension) iterator.next();
            // Do not add generic extension
            if (!GenericExtension.NAME.equals(current.getName())) {
                result.add(current);
            }

        }

        return result;

    }

    // -------------------------------------------------------------------
    public void reloadLanguage() {

        Iterator i = this.extensions.iterator();
        IExtension current = null;
        while (i.hasNext()) {
            current = (IExtension) i.next();
            current.reloadLanguage();
        }
        this.cachedMenus = null;
        this.cachedPopupMenus = null;

    }

    // -------------------------------------------------------------------
    public CatalogLoader getCatalogLoader() {

        return this.catalogLoader;

    }

    // -------------------------------------------------------------------
    public SchemaUILoader getSchemaUILoader() {

        return this.schemaUILoader;

    }

    // -------------------------------------------------------------------
    public JMenu[] getMenus() {

        if (this.cachedMenus == null) {

            Iterator iterator = this.extensions.iterator();
            ArrayList result = new ArrayList();

            IExtension current = null;
            while (iterator.hasNext()) {
                current = (IExtension) iterator.next();
                if (current.getMenu() != null) {
                    result.add(current.getMenu());
                }
            }

            this.cachedMenus = (JMenu[]) result.toArray(new JMenu[] {});

        }

        return this.cachedMenus;

    }

    // -------------------------------------------------------------------
    public PopupMenuExtension[] getPopupMenus() {

        if (this.cachedPopupMenus == null) {

            Iterator iterator = this.extensions.iterator();
            ArrayList result = new ArrayList();

            IExtension current = null;
            while (iterator.hasNext()) {
                current = (IExtension) iterator.next();
                if (current.getPopupMenu() != null) {
                    result.add(current.getPopupMenu());
                }
            }

            this.cachedPopupMenus = (PopupMenuExtension[]) result.toArray(new PopupMenuExtension[] {});

        }

        return this.cachedPopupMenus;

    }

    // -------------------------------------------------------------------
    public PreferencesPanel[] getPreferencesTabs() {

        Iterator iterator = this.extensions.iterator();
        ArrayList result = new ArrayList();

        IExtension current = null;
        while (iterator.hasNext()) {
            current = (IExtension) iterator.next();
            if (current.getPreferencesPanel() != null) {
                result.add(current.getPreferencesPanel());
            }
        }

        return (PreferencesPanel[]) result.toArray(new PreferencesPanel[] {});

    }

    // ---------------
    // Private methods ---------------------------------------------------
    // ---------------

    // -------------------------------------------------------------------
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
        String extPath = System.getProperty("java.ext.dirs");
        File ext = new File(extPath);
        if (ext.exists()) {
            File[] jars = ext.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {

                    if (name.toLowerCase().endsWith(".jar"))
                        return true;

                    return false;
                }
            });

            if (jars != null) {
                for (int i = 0; i < jars.length; i++) {
                    scanJarFile(jars[i], false);
                }
            }
        }

    }

    // -------------------------------------------------------------------
    private String scanJarFile(File jar, boolean update) {

        ZipFile archive = null;
        try {
            archive = new ZipFile(jar);
        } catch (ZipException zipEx) {
            System.err.println("Error while accessing JAR file.\n" + zipEx);
            return null;
        } catch (IOException ioe) {
            System.err.println("Error while accessing JAR file.\n" + ioe);
            return null;
        }

        Enumeration enu = archive.entries();

        String result = null;
        String tempResult = null;
        while (enu.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) enu.nextElement();
            String name = entry.getName();

            if (name.toUpperCase().equals(ExtensionLoader.EXTENSION_FILENAME)) {
                InputStream in = null;
                try {

                    in = archive.getInputStream(entry);
                    Properties prop = new Properties();
                    prop.load(in);

                    tempResult = this.addExtension(prop, update);
                    result = tempResult == null ? result : tempResult;

                } catch (IOException ioe) {
                    System.err.println("Error while accessing entry from JAR file.\n" + ioe);
                    return null;
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException ioe) {
                            // we can't do anything here
                        }
                    }
                }
            }
        }

        return result;

    }

    // -------------------------------------------------------------------
    private String addExtension(Properties properties, boolean update) {

        // Get extension name and classname of main extension class
        String className = properties.getProperty(ExtensionLoader.CONFIG_FILE_ENTRY_EXTENSION_CLASS);

        // --- Load extension main class

        // Get Java class
        Class currentClass = null;
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
        Constructor[] constructors = currentClass.getConstructors();
        IExtension extension = null;
        if (constructors.length > 0) {
            try {
                Class[] parameters = null;
                for (int i = 0; i < constructors.length; i++) {
                    parameters = constructors[i].getParameterTypes();

                    if (parameters.length == 0) {
                        extension = (IExtension) constructors[i].newInstance(null); // 0 parameters
                        break;
                    } else if ((parameters.length == 1) && (parameters[0].isInstance(this.om))) {
                        extension = (IExtension) constructors[i].newInstance(new Object[] { this.om });
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
                System.out.println("Already loaded extension: " + extension.getName());
                return extension.getName();
            }
            this.extensions.add(extension);
            System.out.println("Successfully loaded extension: " + extension.getName());
            return extension.getName();
        }

        return null;

    }

    // -------------------------------------------------------------------
    private void addGenericExtension() {

        this.extensions.add(new GenericExtension());

    }

    // -------------------------------------------------------------------
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
        } catch (FileNotFoundException fnfe) {
            System.err.println("Unable to create file: " + file + "\n" + fnfe);
            return null;
        }
        int sz = (int) ze.getSize();
        final int N = 1024;
        byte buf[] = new byte[N];
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

    }

    private boolean addOALExtenstionElement(IExtension extension) {

        // Get latest schema file
        File schema = new File(this.om.getInstallDir().getAbsolutePath() + File.separator + "schema" + File.separator
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

        OutputFormat outputFormat = new OutputFormat(doc, "ISO-8859-1", true);
        XMLSerializer serializer = new XMLSerializer(outputFormat);
        try {
            serializer.setOutputByteStream(new FileOutputStream(schema));
            serializer.serialize(doc);
        } catch (FileNotFoundException fnfe) {
            System.err.println("File not found: " + schema.getAbsolutePath() + "\n" + fnfe);
            return false;
        } catch (IOException ioe) {
            System.err.println("Error while serializing. Nested Exception is: \n" + ioe);
            return false;
        }

        return result;

    }

}
