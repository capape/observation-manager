package de.lehmannet.om.ui.navigation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Optional;

import javax.xml.XMLConstants;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import de.lehmannet.om.IObservation;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.i18n.TextManager;
import de.lehmannet.om.ui.navigation.observation.utils.InstallDir;
import de.lehmannet.om.ui.util.ConfigKey;
import de.lehmannet.om.ui.util.IConfiguration;
import de.lehmannet.om.ui.util.UserInterfaceHelper;
import de.lehmannet.om.ui.util.Worker;
import de.lehmannet.om.ui.util.XMLFileLoader;
import de.lehmannet.om.ui.util.XMLFileLoaderImpl;

public class ObservationManagerHtmlHelper {

    private final String TEMPLATE_FILENAME = "transform";
    private static final Logger LOGGER = LoggerFactory.getLogger(ObservationManagerHtmlHelper.class);
    private final UserInterfaceHelper uiHelper;
    private final TextManager textManager;
    private final IConfiguration configuration;
    private final InstallDir installDir;
    private final ObservationManagerModel model;

    public ObservationManagerHtmlHelper(UserInterfaceHelper uiHelper, TextManager textManager,
            IConfiguration configuration, InstallDir installDir, ObservationManagerModel model) {
        this.uiHelper = uiHelper;
        this.textManager = textManager;
        this.configuration = configuration;
        this.installDir = installDir;
        this.model = model;

    }

    public boolean transformXML2HTML(final Document doc, final File htmlFile, final File xslFile) {

        Worker calculation = new Worker() {

            private String message;
            private byte returnValue = Worker.RETURN_TYPE_OK;

            @Override
            public void run() {

                if ((doc == null) || (htmlFile == null)) {
                    returnValue = Worker.RETURN_TYPE_ERROR;
                    message = textManager.getString("error.transformation");
                    return;
                }

                DOMSource source = new DOMSource(doc);

                File xsl = (xslFile == null) ? ObservationManagerHtmlHelper.this.getXSLFile() : xslFile;
                StreamSource xslSource = getXslStreamSource(xsl);

                StreamResult result = null;
                FileOutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(htmlFile);
                    result = new StreamResult(outputStream);
                    result.setSystemId(htmlFile);
                } catch (FileNotFoundException fnfe) {
                    LOGGER.error("Cannot transform XML file.\n", fnfe);
                    returnValue = Worker.RETURN_TYPE_ERROR;
                    message = textManager.getString("error.transformation");
                    return;
                }

                // Transform
                try {
                    Templates template = ObservationManagerHtmlHelper.this.getTemplate(xslSource); // Different loading
                    // between JDK1.4 and
                    // JDK1.5
                    if (template == null) {
                        returnValue = Worker.RETURN_TYPE_ERROR;
                        message = textManager.getString("error.transformation");
                        try {
                            outputStream.close();
                        } catch (IOException ioe) {
                            LOGGER.error("Cannot close stream.", ioe);
                        }
                        return;
                    }

                    template.newTransformer().transform(source, result);
                    copyCssFile(htmlFile.getParent(), xsl);
                } catch (TransformerException tce) {
                    LOGGER.error("Cannot transform XML file.", tce);
                    returnValue = Worker.RETURN_TYPE_ERROR;
                    message = textManager.getString("error.transformation");
                    try {
                        outputStream.close();
                    } catch (IOException ioe) {
                        LOGGER.error("Cannot close stream.", ioe);
                    }
                    return;
                }

                try {
                    outputStream.close();
                } catch (IOException ioe) {
                    LOGGER.error("Cannot close stream.", ioe);
                }

            }

            private StreamSource getXslStreamSource(final File xsl) {

                StreamSource xslSource;
                // Get XSL Template
                // Cannot load XSL file. Error message was already given
                if (xsl == null) {

                    URL resource = getInternalXslURL();
                    xslSource = new StreamSource(resource.toExternalForm());

                } else {
                    xslSource = new StreamSource(xsl);
                }
                return xslSource;
            }

            private URL getInternalXslURL() {
                String lang = Locale.getDefault().getLanguage();
                String resourceName = "xsl/internal/transform_" + lang + ".xsl";

                URL resource = ObservationManagerHtmlHelper.class.getClassLoader().getResource(resourceName);
                if (resource == null) {
                    resource = ObservationManagerHtmlHelper.class.getClassLoader()
                            .getResource("xsl/internal/transform_en.xsl");
                }
                return resource;
            }

            private URL getInternalXslFolderURL() {

                String resourceName = "xsl/internal/";
                URL resource = ObservationManagerHtmlHelper.class.getClassLoader().getResource(resourceName);
                return resource;
            }

            @Override
            public String getReturnMessage() {

                return message;

            }

            @Override
            public byte getReturnType() {

                return returnValue;

            }

        };

        // Show progresDialog for first part of export
        uiHelper.createProgressDialog(textManager.getString("progress.wait.title"),
                textManager.getString("progress.wait.html.info"), calculation);

        if (calculation.getReturnType() == Worker.RETURN_TYPE_OK) {
            if (calculation.getReturnMessage() != null) {
                uiHelper.showInfo(calculation.getReturnMessage());
            }

            return true;
        } else {
            uiHelper.showWarning(calculation.getReturnMessage());
            return false;
        }

    }

    private String getCustomCssName(boolean usingInternal) {

        if (usingInternal) {
            return "internal-custom.css";
        }

        String selectedTemplate = this.configuration.getConfig(ConfigKey.CONFIG_XSL_TEMPLATE);
        if (StringUtils.isBlank(selectedTemplate)) {
            selectedTemplate = "oal2html";
        }

        return selectedTemplate + "-custom.css";
    }

    private void copyCssFile(String htmlFolder, final File xslFile) {

        Optional<Path> originalPath = this.getCssSource(xslFile);

        boolean usingInternal = xslFile == null;

        Path destination = Path.of(htmlFolder, getCustomCssName(usingInternal));

        originalPath.ifPresent(path -> {

            if (path.toFile().exists()) {

                try {
                    Files.copy(path, destination, StandardCopyOption.REPLACE_EXISTING);

                } catch (IOException e) {
                    LOGGER.error("Problem copying css {}   ->    {}", path, destination, e);
                }
            }
        });

    }

    private Optional<Path> getCssSource(File xslFile) {

        boolean usingInternal = xslFile == null;
        String cssFileName = getCustomCssName(usingInternal);

        if (xslFile == null) {

            URL resource = ObservationManagerHtmlHelper.class.getClassLoader()
                    .getResource("xsl/internal/" + cssFileName);

            if (resource == null) {
                return Optional.empty();
            }

            return Optional.of(Path.of(resource.getPath()));

        } else {
            return Optional.of(Path.of(xslFile.getParent(), cssFileName));
        }

    }

    private void createHTMLForSchemaElement(ISchemaElement schemaElement, File htmlFile) {

        // With that we can check whether there are observations at all.
        IObservation[] observations = this.model.getObservations(schemaElement);
        if ((observations == null) || (observations.length == 0)) {
            this.uiHelper.showWarning(textManager.getString("error.export.xml.noObservationsForSchemaElement"));
            return;
        }

        // Get DOM source
        Document doc = this.model.getDocumentForElement(schemaElement);

        // XML File needs to be saved, as otherwise we don't get the path
        String[] files = this.model.getAllOpenedFiles();
        if ((files == null) || (files.length == 0)) {
            this.uiHelper.showError(textManager.getString("error.noXMLFileOpen"));
            return;
        }

        this.transformXML2HTML(doc, htmlFile, null);

        this.uiHelper.showInfo(textManager.getString("info.htmlExportDir") + " " + htmlFile);

    }

    private void createXMLForSchemaElement(ISchemaElement schemaElement, String xmlFile) {

        // Create new XMLFileLoader for saving our new XML file
        XMLFileLoader xmlHelper = XMLFileLoaderImpl.newInstance(xmlFile);

        // Get all observations from currently opened XML that belong to the
        // given schemaElement
        IObservation[] observations = null;
        if (schemaElement instanceof IObservation) {
            observations = new IObservation[] { (IObservation) schemaElement };
        } else {
            observations = this.model.getObservations(schemaElement);
        }

        if ((observations == null) || (observations.length == 0)) {
            // progress.close();
            this.uiHelper.showWarning(textManager.getString("error.export.xml.noObservationsForSchemaElement"));
            return;
        }

        // Add all observations and their depending elements to new
        // XMLFileLoader
        for (IObservation observation : observations) {
            xmlHelper.addSchemaElement(observation, true);
        }

        boolean result = xmlHelper.save(xmlFile);

        // progress.close();

        if (result) {
            this.uiHelper.showInfo(textManager.getString("error.export.xml.ok") + xmlFile);
        } else {
            this.uiHelper.showWarning(textManager.getString("error.export.xml.nok"));
        }

    }

    public void createHTMLForSchemaElement(ISchemaElement schemaElement) {

        // Build filename
        String htmlName = schemaElement.getDisplayName();
        htmlName = this.replaceSpecialChars(htmlName);

        String fullFileName = this.getCurrentXMLParentPath() + File.separatorChar + htmlName + ".html";
        File html = FileSystems.getDefault().getPath(fullFileName).toFile();
        int i = 2;
        while (html.exists()) { // Check if file exists (e.g. Two session or
                                // observations (at same (start) time) from
                                // different users...
            fullFileName = this.getCurrentXMLParentPath() + File.separatorChar + htmlName + "(" + i + ").html";
            i++;
            html = FileSystems.getDefault().getPath(fullFileName).toFile();
        }

        this.createHTMLForSchemaElement(schemaElement, html);

    }

    public void createHTML(Document doc, File html, File xslFile) {

        Document xmlDoc = this.model.getDocument(doc);

        String[] files = this.model.getAllOpenedFiles();
        if ((files == null) || (files.length == 0)) {
            this.uiHelper.showInfo(textManager.getString("error.noXMLFileOpen"));
            return;
        }

        if (html == null) {
            File xmlFile = FileSystems.getDefault().getPath(files[0]).toFile();
            String htmlName = xmlFile.getName();
            htmlName = htmlName.substring(0, htmlName.indexOf('.'));
            htmlName = xmlFile.getParent() + File.separatorChar + htmlName + ".html";
            html = FileSystems.getDefault().getPath(htmlName).toFile();
        }

        boolean result = this.transformXML2HTML(xmlDoc, html, xslFile);
        if (result) {
            this.uiHelper.showInfo(textManager.getString("info.htmlExportDir") + " " + html);
        } // Otherwise error message have been provided

    }

    private File getXSLFile() {

        String pathXslTemplateFolder = this.getTemplateFolderPath();

        File path = FileSystems.getDefault().getPath(pathXslTemplateFolder).toFile();
        if (!path.exists()) {
            this.uiHelper.showWarning(
                    textManager.getString("warning.xslTemplate.dirDoesNotExist") + "\n" + path.getAbsolutePath());
            return null;
        }

        String pathXsl = getPathXslFileWithLocale();
        File xslFile = FileSystems.getDefault().getPath(pathXsl).toFile();

        if (!xslFile.exists()) { // Ok, maybe theres a general version which is
                                 // not translated
            String pathXslFileWithoutLocale = this.getPathXslFileWithoutLocale();
            xslFile = FileSystems.getDefault().getPath(pathXslFileWithoutLocale).toFile();
            if (!xslFile.exists()) {
                this.uiHelper.showWarning(textManager.getString("warning.xslTemplate.noFileFoundWithName") + "\n"
                        + pathXslFileWithoutLocale + "\n" + pathXsl);
                return null;
            }
        }

        return xslFile;

    }

    private String getPathXslFileWithoutLocale() {
        String pathXsl = this.getTemplateFolderPath() + File.separator + TEMPLATE_FILENAME + ".xsl";
        return pathXsl;
    }

    private String getPathXslFileWithLocale() {
        String pathXsl = this.getTemplateFolderPath() + File.separator + TEMPLATE_FILENAME + "_"
                + Locale.getDefault().getLanguage() + ".xsl";
        return pathXsl;
    }

    private String getTemplateFolderPath() {
        String selectedTemplate = this.configuration.getConfig(ConfigKey.CONFIG_XSL_TEMPLATE);
        if ((selectedTemplate == null) // No config given, so take default one.
                                       // (Usefull for migrations)
                || ("".equals(selectedTemplate.trim()))) {
            selectedTemplate = "oal2html";
        }

        String pathXslTemplateFolder = this.installDir.getPathForFolder("xsl") + selectedTemplate + File.separator;
        return pathXslTemplateFolder;
    }

    public void createXMLForSchemaElement(ISchemaElement schemaElement) {

        // Build filename
        String xmlName = schemaElement.getDisplayName();
        xmlName = this.replaceSpecialChars(xmlName);

        String fullFileName = this.getCurrentXMLParentPath() + File.separatorChar + xmlName + ".xml";
        File xml = FileSystems.getDefault().getPath(fullFileName).toFile();
        int i = 2;
        while (xml.exists()) { // Check if file exists (e.g. Two session or
                               // observations (at same (start) time) from
                               // different users...
            fullFileName = this.getCurrentXMLParentPath() + File.separatorChar + xmlName + "(" + i + ").xml";
            i++;
            xml = FileSystems.getDefault().getPath(fullFileName).toFile();
        }

        this.createXMLForSchemaElement(schemaElement, xml.getAbsolutePath());

    }

    private String replaceSpecialChars(String string) {

        string = string.replace('/', '_');
        string = string.replace('\\', '_');
        string = string.replace('@', '_');
        string = string.replace('$', '_');
        string = string.replace('%', '_');
        string = string.replace('&', '_');
        string = string.replace(':', '_');
        string = string.replace(';', '_');

        return string;

    }

    private String getCurrentXMLParentPath() {

        // @todo
        // This whole method work only with one file opened!

        File xmlFile = FileSystems.getDefault().getPath(this.model.getAllOpenedFiles()[0]).toFile();

        return xmlFile.getParent();

    }

    private Templates getTemplate(StreamSource xslSource) {

        Templates template = null;

        try {
            var factory = TransformerFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            template = factory.newTemplates(xslSource);
        } catch (TransformerConfigurationException tce) {
            LOGGER.error("--- Unable to get XSLTransformator: ", tce);
        }

        return template;

    }

}