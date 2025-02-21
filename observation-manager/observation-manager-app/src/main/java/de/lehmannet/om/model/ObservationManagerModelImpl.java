package de.lehmannet.om.model;

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
import de.lehmannet.om.ui.navigation.observation.utils.InstallDir;
import de.lehmannet.om.ui.util.IConfiguration;
import de.lehmannet.om.ui.util.XMLFileLoader;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.w3c.dom.Document;

public class ObservationManagerModelImpl implements ObservationManagerModel {

    private boolean changed = false;

    private static final String CHANGED_SUFFIX = " *";
    private String title = "";
    private String titleWhenChanges = CHANGED_SUFFIX;

    private final XMLFileLoader xmlCache;
    private final InstallDir installDir;
    private final IConfiguration configuration;

    private ISchemaElement selected;

    public ObservationManagerModelImpl(XMLFileLoader cache, InstallDir installDir, IConfiguration configuration) {
        this.xmlCache = cache;
        this.installDir = installDir;
        this.configuration = configuration;
    }

    @Override
    public boolean hasChanged() {
        return changed;
    }

    @Override
    public void setChanged(boolean b) {
        this.changed = true;
    }

    @Override
    public void setTitle(String title) {
        if (!this.title.equals(title)) {
            this.changed = true;
            this.title = title;
            this.titleWhenChanges = this.title + CHANGED_SUFFIX;
        }
    }

    @Override
    public String getTittle() {
        if (changed) {
            return titleWhenChanges;
        } else {
            return title;
        }
    }

    @Override
    public void clear() {
        this.xmlCache.clear();
        this.setChanged(true);
    }

    @Override
    public boolean isEmpty() {

        return this.xmlCache.isEmpty();
    }

    @Override
    public void add(ISchemaElement element) {
        this.xmlCache.addSchemaElement(element);
        this.setChanged(true);
    }

    @Override
    public void add(ISchemaElement element, boolean dependend) {
        this.xmlCache.addSchemaElement(element, dependend);
        this.setChanged(true);
    }

    @Override
    public List<ISchemaElement> remove(ISchemaElement element) {

        final List<ISchemaElement> result = this.xmlCache.removeSchemaElement(element);
        this.setChanged(true);
        return result;
    }

    @Override
    public void update(ISchemaElement element) {
        this.xmlCache.updateSchemaElement(element);
        this.setChanged(true);
    }

    @Override
    public IObserver[] getObservers() {
        return this.xmlCache.getObservers();
    }

    @Override
    public IEyepiece[] getEyepieces() {
        return this.xmlCache.getEyepieces();
    }

    @Override
    public IImager[] getImagers() {
        return this.xmlCache.getImagers();
    }

    @Override
    public IFilter[] getFilters() {

        return this.xmlCache.getFilters();
    }

    @Override
    public IObservation[] getObservations() {
        return this.xmlCache.getObservations();
    }

    @Override
    public IObservation[] getObservations(ISchemaElement element) {

        return this.xmlCache.getObservations(element);
    }

    @Override
    public IObservation[] getCoObserverObservations(IObserver observer) {
        return this.xmlCache.getCoObserverObservations(observer);
    }

    @Override
    public IScope[] getScopes() {

        return this.xmlCache.getScopes();
    }

    @Override
    public ISession[] getSessions() {
        return this.xmlCache.getSessions();
    }

    @Override
    public ISite[] getSites() {
        return this.xmlCache.getSites();
    }

    @Override
    public ITarget[] getTargets() {
        return this.xmlCache.getTargets();
    }

    @Override
    public ILens[] getLenses() {
        return this.xmlCache.getLenses();
    }

    @Override
    public boolean loadObservations(final String filePath) {
        this.clear();
        boolean fixErrors = configuration.getBooleanConfig("om.fix.xml.errors.on.load");
        return this.xmlCache.loadObservations(filePath, fixErrors);
    }

    public void exportToHtml() {}

    @Override
    public Optional<String> getRootName() {
        String[] fileNames = this.xmlCache.getAllOpenedFiles();
        if ((fileNames != null) && (fileNames.length > 0)) {
            String rootName = new File(fileNames[0]).getName();
            return Optional.of(rootName);
        }
        return Optional.empty();
    }

    @Override
    public List<File> getFilesFromPath(List<String> imagePath) {
        if (imagePath == null) {
            return Collections.emptyList();
        }
        return imagePath.stream()
                .map(x -> this.createPath(x))
                .filter(x -> x.exists())
                .collect(Collectors.toList());
    }

    private File createPath(String x) {
        if (x.startsWith("." + File.separator)) {
            return new File(this.xmlCache.getXMLPathForSchemaElement(this.getSelectedElement()) + File.separator + x);
        } else {
            return new File(x);
        }
    }

    @Override
    public Document getDocument(Document doc) {
        if (doc == null) {
            return this.xmlCache.getDocument();
        }
        return doc;
    }

    @Override
    public Document getDocumentForElement(ISchemaElement schemaElement) {
        return this.xmlCache.getDocumentForSchemaElement(schemaElement);
    }

    @Override
    public String getXMLFileForSchemaElement(ISchemaElement schemaElement) {
        return this.xmlCache.getXMLFileForSchemaElement(schemaElement);
    }

    @Override
    public String getXMLPathForSchemaElement(ISchemaElement schemaElement) {
        return this.xmlCache.getXMLPathForSchemaElement(schemaElement);
    }

    public File getExportFile(final String filename, final String extension) {

        String path = null;

        if ((this.xmlCache.getAllOpenedFiles() != null) && (this.xmlCache.getAllOpenedFiles().length > 0)) {
            path = new File(this.xmlCache.getAllOpenedFiles()[0]).getParent();
        } else {
            path = this.installDir.getInstallDir().getParent();
        }
        path = path + File.separator;

        File file = new File(path + filename + "." + extension);
        for (int i = 2; file.exists(); i++) {
            file = new File(path + filename + "(" + i + ")." + extension);
        }

        return file;
    }

    @Override
    public String[] getAllOpenedFiles() {

        return this.xmlCache.getAllOpenedFiles();
    }

    @Override
    public final void setSelectedElement(ISchemaElement selected) {
        this.selected = selected;
    }

    @Override
    public final ISchemaElement getSelectedElement() {
        return this.selected;
    }

    @Override
    public boolean save(String name) {

        return this.xmlCache.save(name);
    }

    @Override
    public boolean saveAs(String oldPath, String newPath) {
        return this.xmlCache.saveAs(oldPath, newPath);
    }

    @Override
    public IConfiguration getConfiguration() {
        return this.configuration;
    }
}
