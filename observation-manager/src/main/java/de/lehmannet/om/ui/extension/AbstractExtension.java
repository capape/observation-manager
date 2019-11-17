package de.lehmannet.om.ui.extension;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JMenu;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import de.lehmannet.om.ui.catalog.ICatalog;
import de.lehmannet.om.ui.preferences.PreferencesPanel;
import de.lehmannet.om.util.SchemaElementConstants;

public abstract class AbstractExtension implements IExtension {

    protected String OAL_EXTENSION_FILE = "noCorrectSetByExtension";

    // HashMap:
    // Key: SchemaElementConstant Value: HashMap of xsiTypes
    // HashMap of xsiTypes:
    // Key: xsiType (String) Value: Panel/Dialog classname as String
    protected final Map panels = new HashMap();
    protected final Map dialogs = new HashMap();

    @Override
    public abstract String getName();

    @Override
    public abstract float getVersion();

    @Override
    public abstract JMenu getMenu();

    @Override
    public abstract PreferencesPanel getPreferencesPanel();

    @Override
    public abstract Set getSupportedXSITypes(int schemaElementConstant);

    @Override
    public abstract boolean isCreationAllowed(String xsiType);

    @Override
    public abstract String getDisplayNameForXSIType(String xsiType);

    @Override
    public abstract ICatalog[] getCatalogs(File catalogDir);

    @Override
    public Set getAllSupportedXSITypes() {

        // Return all XSI types which are supported by this extension
        HashSet result = new HashSet();
        result.addAll(this.getSupportedXSITypes(SchemaElementConstants.FINDING));
        result.addAll(this.getSupportedXSITypes(SchemaElementConstants.TARGET));

        return result;

    }

    @Override
    public String getPanelForXSIType(String xsiType, int schemaElementConstant) {

        Object o = this.panels.get(schemaElementConstant);

        if (o == null) { // nothing found for this schema Element type
            return null;
        }

        HashMap hm = (HashMap) o;
        return (String) hm.get(xsiType);

    }

    @Override
    public String getDialogForXSIType(String xsiType, int schemaElementConstant) {

        Object o = this.dialogs.get(schemaElementConstant);

        if (o == null) { // nothing found for this schema Element type
            return null;
        }

        HashMap hm = (HashMap) o;
        return (String) hm.get(xsiType);

    }

    @Override
    public boolean equals(Object o) {

        if (o instanceof IExtension) {
            IExtension ext = (IExtension) o;
            if (ext.getName().equals(this.getName())) {
                return ext.getVersion() == this.getVersion();
            }
        }

        return false;

    }

    @Override
    public boolean addOALExtensionElement(Element docElement) {

        // Check if include is already in place
        NodeList list = docElement.getElementsByTagName("xsd:include");
        NamedNodeMap attributes = null;
        for (int i = 0; i < list.getLength(); i++) {
            attributes = list.item(i).getAttributes();
            if (this.OAL_EXTENSION_FILE.equals(attributes.getNamedItem("schemaLocation").getNodeValue())) {
                return true;
            }
        }

        Document doc = docElement.getOwnerDocument();

        Element e = doc.createElement("xsd:include");
        e.setAttribute("schemaLocation", this.OAL_EXTENSION_FILE);

        docElement.appendChild(e);

        return true;

    }

}
