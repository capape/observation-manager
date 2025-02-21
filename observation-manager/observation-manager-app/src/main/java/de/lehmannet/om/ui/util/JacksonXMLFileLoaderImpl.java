package de.lehmannet.om.ui.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import de.lehmannet.om.IEyepiece;
import de.lehmannet.om.IFilter;
import de.lehmannet.om.IImager;
import de.lehmannet.om.ILens;
import de.lehmannet.om.IObservation;
import de.lehmannet.om.IObserver;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.IScope;
import de.lehmannet.om.ISession;
import de.lehmannet.om.ISite;
import de.lehmannet.om.ITarget;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class JacksonXMLFileLoaderImpl implements XMLFileLoader {

    private final XmlMapper mapper = new XmlMapper();
    private File schemaPath;

    private static final Logger LOGGER = LoggerFactory.getLogger(JacksonXMLFileLoaderImpl.class);

    public static final XMLFileLoader newInstance(String pathFile) throws JsonProcessingException, IOException {
        final File file = new File(pathFile);
        if (!file.exists()) {

            LOGGER.error("Comast schema path not found:{}.", pathFile);
        }
        return new JacksonXMLFileLoaderImpl(file);
    }

    private JacksonXMLFileLoaderImpl(File path) throws JsonProcessingException, IOException {
        this.schemaPath = path;

        mapper.readTree(this.schemaPath);
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean save(String path) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean saveAs(String oldPath, String newPath) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Document getDocument() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getXMLFileForSchemaElement(ISchemaElement schemaElement) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getXMLPathForSchemaElement(ISchemaElement schemaElement) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Document getDocumentForSchemaElement(ISchemaElement schemaElement) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addSchemaElement(ISchemaElement element) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addSchemaElement(ISchemaElement element, boolean dependend) {
        // TODO Auto-generated method stub

    }

    @Override
    public List<ISchemaElement> removeSchemaElement(ISchemaElement element) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateSchemaElement(ISchemaElement element) {
        // TODO Auto-generated method stub

    }

    @Override
    public String[] getAllOpenedFiles() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IObserver[] getObservers() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IEyepiece[] getEyepieces() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IImager[] getImagers() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IFilter[] getFilters() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IObservation[] getObservations() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IObservation[] getObservations(ISchemaElement element) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IObservation[] getCoObserverObservations(IObserver observer) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IScope[] getScopes() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ISession[] getSessions() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ISite[] getSites() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ITarget[] getTargets() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ILens[] getLenses() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean loadObservations(String xmlPath, boolean fixErrors) {
        // TODO Auto-generated method stub
        return false;
    }
}
