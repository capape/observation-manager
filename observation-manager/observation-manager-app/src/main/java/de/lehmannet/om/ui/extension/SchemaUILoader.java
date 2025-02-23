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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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

        var types = getAllXSITypes(schemaElementConstants);

        return Arrays.stream(types)
                .map(this::getDisplayNameForType)
                .filter(Objects::nonNull)
                .toArray(String[]::new);
    }

    public String[] getAllXSIDisplayNamesForCreation(SchemaElementConstants schemaElementConstants) {

        var types = this.getAllXSITypes(schemaElementConstants);

        return Arrays.stream(types)
                .filter(type -> extensions.stream().anyMatch(extension -> extension.isCreationAllowed(type)))
                .map(this::getDisplayNameForType)
                .filter(Objects::nonNull)
                .sorted()
                .toArray(String[]::new);
    }

    public String getDisplayNameForType(String type) {

        return extensions.stream()
                .map(extension -> extension.getDisplayNameForXSIType(type))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse("");
    }

    public String getTypeForDisplayName(String displayName) {

        if (displayName == null) {
            return null;
        }

        // Get all known types
        var types = getAllXSITypes();

        return Arrays.stream(types)
                .filter(type -> extensions.stream()
                        .anyMatch(extension -> displayName.equals(extension.getDisplayNameForXSIType(type))))
                .findFirst()
                .orElse(null);
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
