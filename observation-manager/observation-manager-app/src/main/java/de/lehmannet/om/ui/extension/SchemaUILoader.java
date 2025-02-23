/*
 * ====================================================================
 * /extension/SchemaUILoader.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension;

import de.lehmannet.om.IFinding;
import de.lehmannet.om.IObservation;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ISession;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.ui.dialog.AbstractDialog;
import de.lehmannet.om.ui.dialog.IDialog;
import de.lehmannet.om.ui.dialog.ITargetDialog;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.ui.panel.IPanel;
import de.lehmannet.om.util.SchemaElementConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchemaUILoader {

    private final ObservationManager observationManager;
    private List<IExtension> extensions = null;

    private static final Logger LOGGER = LoggerFactory.getLogger(SchemaUILoader.class);

    public SchemaUILoader(ObservationManager om, List<IExtension> extensions) {

        this.observationManager = om;
        this.extensions = extensions;
    }

    public AbstractPanel getFindingPanel(String xsiType, IFinding finding, ISession s, ITarget t, boolean editable) {

        return this.getFindingPanelFromXSIType(xsiType, finding, s, t, editable);
    }

    public ITargetDialog getTargetDialog(String xsiType, ITarget target, IObservation o) {

        return this.getTargetDialogFromXSIType(xsiType, target, o);
    }

    public AbstractPanel getTargetPanel(String xsiType, ITarget target, IObservation o, boolean editable) {

        return this.getTargetPanelFromXSIType(xsiType, target, o, editable);
    }

    public AbstractPanel getSchemaElementPanel(String xsiType, ISchemaElement schemaElement, boolean editable) {

        return (AbstractPanel) this.getGenericPanelForSchemaElementAndXSIType(xsiType, schemaElement, editable);
    }

    public AbstractDialog getSchemaElementDialog(String xsiType, ISchemaElement schemaElement, boolean editable) {

        return (AbstractDialog) this.getGenericDialogForSchemaElementAndXSIType(xsiType, schemaElement, editable);
    }

    private String[] getAllXSITypes() {

        Set<String> result = new HashSet<>();
        for (var extension : this.extensions) {
            LOGGER.debug("Getting all xsi types for extension {}", extension.getName());
            if (extension.getAllSupportedXSITypes() != null) {
                result.addAll(extension.getAllSupportedXSITypes());
            }
        }

        return result.toArray(new String[] {});
    }

    private String[] getAllXSITypes(SchemaElementConstants schemaElementConstants) {

        Set<String> result = new HashSet<>();
        for (var extension : this.extensions) {
            LOGGER.debug(
                    "Getting all xsi types for extension {}, constant {}", extension.getName(), schemaElementConstants);
            if (extension.getSupportedXSITypes(schemaElementConstants) != null) {
                result.addAll(extension.getSupportedXSITypes(schemaElementConstants));
            }
        }

        return result.toArray(new String[] {});
    }

    public String[] getAllXSIDisplayNames(SchemaElementConstants schemaElementConstants) {

        String[] types = this.getAllXSITypes(schemaElementConstants);
        List<String> result = new ArrayList<>();
        String dispName = null;

        for (var extension : this.extensions) {
            for (String type : types) {
                dispName = extension.getDisplayNameForXSIType(type);
                LOGGER.debug("Extension: {}, type: {}, dispName: {}", extension.getName(), type, dispName);
                if (dispName != null) {
                    result.add(dispName);
                }
            }
        }

        return (String[]) result.toArray(new String[] {});
    }

    public String[] getAllXSIDisplayNamesForCreation(SchemaElementConstants schemaElementConstants) {

        String[] types = this.getAllXSITypes(schemaElementConstants);
        List<String> result = new ArrayList<>();
        String dispName = null;

        for (var extension : this.extensions) {
            for (String type : types) {
                if (extension.isCreationAllowed(type)) {
                    dispName = extension.getDisplayNameForXSIType(type);
                    LOGGER.debug("Extension: {}, type: {}, dispName: {}", extension.getName(), type, dispName);
                    if (dispName != null) {
                        result.add(dispName);
                    }
                }
            }
        }
        Collections.sort(result);

        return (String[]) result.toArray(new String[] {});
    }

    public String getDisplayNameForType(String type) {

        for (var currentExtension : this.extensions) {
            var result = currentExtension.getDisplayNameForXSIType(type);
            LOGGER.debug("extension: {}, type: {}, dispName: {}", currentExtension.getName(), type, result);
            if (result != null) {
                return result;
            }
        }

        return "";
    }

    public String getTypeForDisplayName(String name) {

        if (name == null) {
            return null;
        }

        // Get all known types
        String[] types = this.getAllXSITypes();

        String dispName = null;
        for (var extension : this.extensions) {
            for (String type : types) { // Iterate over all types
                dispName = extension.getDisplayNameForXSIType(type); // Check if extension knows a displayname for
                LOGGER.debug(
                        "extension: {}, type: {}, name: {}, dispName: {}", extension.getName(), type, name, dispName);
                // this type
                if (name.equals(dispName)) {
                    return type; // Displayname found for this type
                }
            }
        }

        return null;
    }

    // ---------------
    // Private Methods --------------------------------------------------------
    // ---------------

    private AbstractPanel getFindingPanelFromXSIType(
            String xsiType, IFinding finding, ISession session, ITarget target, boolean editable) {

        for (var extension : this.extensions) {
            if (extension.supports(xsiType)) {
                LOGGER.debug("New load without reflection");
                return extension.getFindingPanelForXSIType(xsiType, finding, session, target, editable);
            }
        }

        LOGGER.error("No installed extension can handle finding panels for the type: {}", xsiType);
        return null;
    }

    private AbstractPanel getTargetPanelFromXSIType(
            String xsiType, ITarget target, IObservation observation, boolean editable) {

        for (var extension : this.extensions) {
            if (extension.supports(xsiType)) {
                LOGGER.debug("New load without reflection");
                return extension.getTargetPanelForXSIType(xsiType, target, observation, editable);
            }
        }

        LOGGER.error("No installed extension can handle target panels for the type: {}", xsiType);

        return null;
    }

    private ITargetDialog getTargetDialogFromXSIType(String xsiType, ITarget target, IObservation o) {

        for (var extension : this.extensions) {

            if (extension.supports(xsiType)) {
                LOGGER.debug("New load without reflection");
                return extension.getTargetDialogForXSIType(xsiType, this.observationManager, target, o, true);
            }
        }

        LOGGER.error("No installed extension can handle target dialogs for the type: {}", xsiType);

        return null;
    }

    private IPanel getGenericPanelForSchemaElementAndXSIType(
            String xsiType, ISchemaElement schemaElement, boolean editable) {

        for (var extension : this.extensions) {

            if (extension.supports(xsiType)) {
                LOGGER.debug("New load without reflection");
                return extension.getGenericPanelForXSIType(xsiType, schemaElement, editable);
            }
        }

        LOGGER.error("No installed extension can handle the type: {}", xsiType);
        return null;
    }

    private IDialog getGenericDialogForSchemaElementAndXSIType(
            String xsiType, ISchemaElement schemaElement, boolean editable) {

        for (var extension : this.extensions) {

            if (extension.supports(xsiType)) {
                LOGGER.debug("New load without reflection");
                return extension.getGenericDialogForXSIType(xsiType, this.observationManager, schemaElement, true);
            }
        }

        LOGGER.error("No installed extension can handle generic dialogs for the type: {}", xsiType);
        return null;
    }
}
