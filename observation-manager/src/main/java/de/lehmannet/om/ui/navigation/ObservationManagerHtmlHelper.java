package de.lehmannet.om.ui.navigation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

import de.lehmannet.om.IObservation;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ui.dialog.ProgressDialog;
import de.lehmannet.om.ui.util.Worker;
import de.lehmannet.om.ui.util.XMLFileLoader;
import de.lehmannet.om.ui.util.XMLFileLoaderImpl;

public class ObservationManagerHtmlHelper {

    private final ObservationManager observationManager;

    public ObservationManagerHtmlHelper(ObservationManager om) {
        this.observationManager=om;
    }


    public boolean transformXML2HTML(final Document doc, final File htmlFile, final File xslFile) {

        Worker calculation = new Worker() {

            private String message;
            private byte returnValue = Worker.RETURN_TYPE_OK;

            @Override
            public void run() {

                if ((doc == null) || (htmlFile == null)) {
                    returnValue = Worker.RETURN_TYPE_ERROR;
                    message = ObservationManager.bundle.getString("error.transformation");
                    return;
                }

                DOMSource source = new DOMSource(doc);

                File xsl = (xslFile == null) ? ObservationManagerHtmlHelper.this.getXSLFile() : xslFile;

                // Get XSL Template
                if (xsl == null) { // Cannot load XSL file. Error message was
                                   // already given

                    returnValue = Worker.RETURN_TYPE_ERROR;
                    message = ObservationManager.bundle.getString("error.transformation");
                    return;

                }
                StreamSource xslSource = new StreamSource(xsl);

                StreamResult result = null;
                FileOutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(htmlFile);
                    result = new StreamResult(outputStream);
                } catch (FileNotFoundException fnfe) {
                    System.err.println("Cannot transform XML file.\n" + fnfe);
                    returnValue = Worker.RETURN_TYPE_ERROR;
                    message = ObservationManager.bundle.getString("error.transformation");
                    return;
                }

                // Transform
                try {
                    Templates template = ObservationManagerHtmlHelper.this.getTemplate(xslSource); // Different loading
                                                                                         // between JDK1.4 and
                                                                                         // JDK1.5
                    if (template == null) {
                        returnValue = Worker.RETURN_TYPE_ERROR;
                        message = ObservationManager.bundle.getString("error.transformation");
                        try {
                            outputStream.close();
                        } catch (IOException ioe) {
                            System.err.println("Cannot close stream.\n" + ioe);
                        }
                        return;
                    }

                    template.newTransformer().transform(source, result);
                } catch (TransformerException tce) {
                    System.err.println("Cannot transform XML file.\n" + tce);
                    returnValue = Worker.RETURN_TYPE_ERROR;
                    message = ObservationManager.bundle.getString("error.transformation");
                    try {
                        outputStream.close();
                    } catch (IOException ioe) {
                        System.err.println("Cannot close stream.\n" + ioe);
                    }
                    return;
                }

                try {
                    outputStream.close();
                } catch (IOException ioe) {
                    System.err.println("Cannot close stream.\n" + ioe);
                }

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
        new ProgressDialog(this.observationManager, ObservationManager.bundle.getString("progress.wait.title"),
                ObservationManager.bundle.getString("progress.wait.html.info"), calculation);

        if (calculation.getReturnType() == Worker.RETURN_TYPE_OK) {
            if (calculation.getReturnMessage() != null) {
                this.observationManager.createInfo(calculation.getReturnMessage());
            }
            return true;
        } else {
            this.observationManager.createWarning(calculation.getReturnMessage());
            return false;
        }

    }

    private void createHTMLForSchemaElement(ISchemaElement schemaElement, File htmlFile) {

        // With that we can check whether there are observations at all.
        IObservation[] observations = this.observationManager.getXmlCache().getObservations(schemaElement);
        if ((observations == null) || (observations.length == 0)) {
            this.observationManager.createWarning(ObservationManager.bundle.getString("error.export.xml.noObservationsForSchemaElement"));
            return;
        }

        // Get DOM source
        Document doc = this.observationManager.getXmlCache().getDocumentForSchemaElement(schemaElement);

        // XML File needs to be saved, as otherwise we don't get the path
        String[] files = this.observationManager.getXmlCache().getAllOpenedFiles();
        if ((files == null) || (files.length == 0)) { // There is data
                                                      // (otherwise we
                                                      // wouldn't have come
                                                      // here), but data's
                                                      // not saved
            this.observationManager.createInfo(ObservationManager.bundle.getString("error.noXMLFileOpen"));
            return;
        }

        this.transformXML2HTML(doc, htmlFile, null);
        this.observationManager.createInfo(ObservationManager.bundle.getString("info.htmlExportDir") + " " + htmlFile);

    }

    private void createXMLForSchemaElement(ISchemaElement schemaElement, String xmlFile) {

        /*
         * ProgressDialog progress = new ProgressDialog(this,
         * ObservationManager.bundle.getString("progress.wait.title"),
         * ObservationManager.bundle.getString("progress.wait.xml.info"));
         */
        // Create new XMLFileLoader for saving our new XML file
        XMLFileLoader xmlHelper =  XMLFileLoaderImpl.newInstance(xmlFile);

        // Get all observations from currently opened XML that belong to the
        // given schemaElement
        IObservation[] observations = null;
        if (schemaElement instanceof IObservation) {
            observations = new IObservation[] { (IObservation) schemaElement };
        } else {
            observations = this.observationManager.getXmlCache().getObservations(schemaElement);
        }

        if ((observations == null) || (observations.length == 0)) {
            // progress.close();
            this.observationManager.createWarning(ObservationManager.bundle.getString("error.export.xml.noObservationsForSchemaElement"));
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
            this.observationManager.createInfo(ObservationManager.bundle.getString("error.export.xml.ok") + xmlFile);
        } else {
            this.observationManager.createWarning(ObservationManager.bundle.getString("error.export.xml.nok"));
        }

    }

    public void createHTMLForSchemaElement(ISchemaElement schemaElement) {

        // Build filename
        String htmlName = schemaElement.getDisplayName();
        htmlName = this.replaceSpecialChars(htmlName);

        String fullFileName = this.getCurrentXMLParentPath() + File.separatorChar + htmlName + ".html";
        File html = new File(fullFileName);
        int i = 2;
        while (html.exists()) { // Check if file exists (e.g. Two session or
                                // observations (at same (start) time) from
                                // different users...
            fullFileName = this.getCurrentXMLParentPath() + File.separatorChar + htmlName + "(" + i + ").html";
            i++;
            html = new File(fullFileName);
        }

        this.createHTMLForSchemaElement(schemaElement, html);

    }

    public void createHTML(Document doc, File html, File xslFile) {

        if (doc == null) {
            doc = this.observationManager.getXmlCache().getDocument();
        }

        String[] files = this.observationManager.getXmlCache().getAllOpenedFiles();
        if ((files == null) || (files.length == 0)) {
            this.observationManager.createInfo(ObservationManager.bundle.getString("error.noXMLFileOpen"));
            return;
        }

        if (html == null) {
            File xmlFile = new File(files[0]);
            String htmlName = xmlFile.getName();
            htmlName = htmlName.substring(0, htmlName.indexOf('.'));
            htmlName = xmlFile.getParent() + File.separatorChar + htmlName + ".html";
            html = new File(htmlName);
        }

        boolean result = this.transformXML2HTML(doc, html, xslFile);
        if (result) {
            this.observationManager.createInfo(ObservationManager.bundle.getString("info.htmlExportDir") + " " + html);
        } // Otherwise error message have been provided

    }

   

    private File getXSLFile() {

        final String TEMPLATE_FILENAME = "transform";

        String selectedTemplate = this.observationManager.getConfiguration().getConfig(ObservationManager.CONFIG_XSL_TEMPLATE);
        if ((selectedTemplate == null) // No config given, so take default one.
                                       // (Usefull for migrations)
                || ("".equals(selectedTemplate.trim()))) {
            selectedTemplate = "oal2html";
        }

        File path = new File(this.observationManager.getInstallDir().getPathForFolder("xsl") + selectedTemplate + File.separator);
        if (!path.exists()) {
            this.observationManager.createWarning(ObservationManager.bundle.getString("warning.xslTemplate.dirDoesNotExist") + "\n"
                    + path.getAbsolutePath());
            return null;
        }

        // Try to load language dependend file first
        File xslFile = new File(path.getAbsolutePath() + File.separator + TEMPLATE_FILENAME + "_"
                + Locale.getDefault().getLanguage() + ".xsl");
        if (!xslFile.exists()) { // Ok, maybe theres a general version which is
                                 // not translated
            xslFile = new File(path.getAbsolutePath() + File.separator + TEMPLATE_FILENAME + ".xsl");
            if (!xslFile.exists()) {
                this.observationManager.createWarning(ObservationManager.bundle.getString("warning.xslTemplate.noFileFoundWithName") + "\n"
                        + path.getAbsolutePath() + File.separator + TEMPLATE_FILENAME + ".xsl\n"
                        + path.getAbsolutePath() + File.separator + TEMPLATE_FILENAME + "_"
                        + Locale.getDefault().getLanguage() + ".xsl");
                return null;
            }
        }

        return xslFile;

    }

    public void createXMLForSchemaElement(ISchemaElement schemaElement) {

        // Build filename
        String xmlName = schemaElement.getDisplayName();
        xmlName = this.replaceSpecialChars(xmlName);

        String fullFileName = this.getCurrentXMLParentPath() + File.separatorChar + xmlName + ".xml";
        File xml = new File(fullFileName);
        int i = 2;
        while (xml.exists()) { // Check if file exists (e.g. Two session or
                               // observations (at same (start) time) from
                               // different users...
            fullFileName = this.getCurrentXMLParentPath() + File.separatorChar + xmlName + "(" + i + ").xml";
            i++;
            xml = new File(fullFileName);
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

        File xmlFile = new File(this.observationManager.getXmlCache().getAllOpenedFiles()[0]);

        return xmlFile.getParent();

    }

    private Templates getTemplate(StreamSource xslSource) {

        Templates template = null;

        /*
         * *********** AS WE PACK APACHE XALAN WITH OM (since 0.314) we don't need this
         * any loner *************)
         *
         * // Classname (package) changed between JDK1.4 and JDK1.5 // So we check VM
         * version and load the right class via reflection...hopefully String version =
         * System.getProperty("java.version"); // String classname =
         * "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl" ; // >=
         * JDK1.5 String classname = "javax.xml.transform.TransformerFactory"; if(
         * version.startsWith("1.4") ) { String classname =
         * "org.apache.xalan.processor.TransformerFactoryImpl"; // JDK1.4 }
         *
         * // Even if classname changed the class itself if the same, so we need only on
         * block to load the class.... try { Class transformerClass =
         * Class.forName(classname); Method newInstance =
         * transformerClass.getMethod("newInstance", null); Object transformerClassImpl
         * = newInstance.invoke(transformerClass, null); Method newTemplates =
         * transformerClassImpl.getClass().getMethod("newTemplates", new Class[] {
         * Source.class }); template =
         * (Templates)newTemplates.invoke(transformerClassImpl, new StreamSource[] {
         * xslSource }); } catch( ClassNotFoundException cnfe ) {
         * System.out.println("--- Unable to load class: " + classname); } catch(
         * NoSuchMethodException nsme ) { System.out.println("--- Unable to class: " +
         * classname + " looks strange. What JDK version is this? " + version); } catch(
         * IllegalAccessException iae ) {
         * System.out.println("--- Unable to access class: " + classname); } catch(
         * InvocationTargetException ite ) {
         * System.out.println("--- Unable to invoke method on class: " + classname); }
         */

        try {
            template = TransformerFactory.newInstance().newTemplates(xslSource);
        } catch (TransformerConfigurationException tce) {
            System.err.println("--- Unable to get XSLTransformator: " + tce);
        }

        return template;

    }

}