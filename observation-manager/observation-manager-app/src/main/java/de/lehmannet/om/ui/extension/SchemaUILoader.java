/*
 * ====================================================================
 * /extension/SchemaUILoader.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension;

import de.lehmannet.om.IEyepiece;
import de.lehmannet.om.IFilter;
import de.lehmannet.om.IFinding;
import de.lehmannet.om.IImager;
import de.lehmannet.om.ILens;
import de.lehmannet.om.IObservation;
import de.lehmannet.om.IObserver;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.IScope;
import de.lehmannet.om.ISession;
import de.lehmannet.om.ISite;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.dialog.AbstractDialog;
import de.lehmannet.om.ui.dialog.ITargetDialog;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.util.SchemaElementConstants;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchemaUILoader {

    private final ObservationManager observationManager;
    private final ObservationManagerModel model;
    private List<IExtension> extensions = null;

    private static final Logger LOGGER = LoggerFactory.getLogger(SchemaUILoader.class);

    public SchemaUILoader(ObservationManager om, List<IExtension> extensions, ObservationManagerModel model) {

        this.observationManager = om;
        this.extensions = extensions;
        this.model = model;
    }

    private AbstractPanel getFindingPanel(String xsiType, IFinding finding, boolean editable) {

        return this.getFindingPanelFromXSIType(xsiType, finding, null, null, editable);
    }

    public AbstractPanel getFindingPanel(String xsiType, IFinding finding, ISession s, ITarget t, boolean editable) {

        return this.getFindingPanelFromXSIType(xsiType, finding, s, t, editable);
    }

    public AbstractPanel getFindingPanel(ITarget target, IFinding finding, boolean editable) {

        return this.getFindingPanel(target.getXSIType(), finding, editable);
    }

    public ITargetDialog getTargetDialog(String xsiType, ITarget target, IObservation o) {

        return this.getTargetDialogFromXSIType(xsiType, target, o);
    }

    public AbstractPanel getTargetPanel(String xsiType, ITarget target, IObservation o, boolean editable) {

        return this.getTargetPanelFromXSIType(xsiType, target, o, editable);
    }

    public AbstractPanel getSchemaElementPanel(
            String xsiType,
            SchemaElementConstants schemaElementConstant,
            ISchemaElement schemaElement,
            boolean editable) {

        return (AbstractPanel)
                this.getSchemaElementUIObject(xsiType, schemaElementConstant, schemaElement, editable, false);
    }

    public AbstractDialog getSchemaElementDialog(
            String xsiType,
            SchemaElementConstants schemaElementConstant,
            ISchemaElement schemaElement,
            boolean editable) {

        return (AbstractDialog)
                this.getSchemaElementUIObject(xsiType, schemaElementConstant, schemaElement, editable, true);
    }

    private String[] getAllXSITypes() {

        Iterator<IExtension> iterator = this.extensions.iterator();
        IExtension extension = null;
        Set<String> result = new HashSet<>();
        while (iterator.hasNext()) {
            extension = iterator.next();
            LOGGER.debug("Getting all xsi types for extension {}", extension.getName());
            if (extension.getAllSupportedXSITypes() != null) {
                result.addAll(extension.getAllSupportedXSITypes());
            }
        }

        return (String[]) result.toArray(new String[] {});
    }

    private String[] getAllXSITypes(SchemaElementConstants schemaElementConstants) {

        Iterator<IExtension> iterator = this.extensions.iterator();
        IExtension extension = null;
        Set<String> result = new HashSet<>();
        while (iterator.hasNext()) {
            extension = iterator.next();
            LOGGER.debug(
                    "Getting all xsi types for extension {}, constant {}", extension.getName(), schemaElementConstants);
            if (extension.getSupportedXSITypes(schemaElementConstants) != null) {
                result.addAll(extension.getSupportedXSITypes(schemaElementConstants));
            }
        }

        return (String[]) result.toArray(new String[] {});
    }

    public String[] getAllXSIDisplayNames(SchemaElementConstants schemaElementConstants) {

        String[] types = this.getAllXSITypes(schemaElementConstants);

        Iterator<IExtension> iterator = this.extensions.iterator();
        IExtension extension = null;
        List<String> result = new ArrayList<>();
        String dispName = null;
        while (iterator.hasNext()) {
            extension = iterator.next();
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

        Iterator<IExtension> iterator = this.extensions.iterator();
        IExtension extension = null;
        List<String> result = new ArrayList<>();
        String dispName = null;
        while (iterator.hasNext()) {
            extension = iterator.next();
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

        Iterator<IExtension> iterator = this.extensions.iterator();
        IExtension currentExtension = null;
        String result = null;
        while (iterator.hasNext()) {
            currentExtension = iterator.next();
            result = currentExtension.getDisplayNameForXSIType(type);
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

        Iterator<IExtension> iterator = this.extensions.iterator();
        IExtension extension = null;
        String dispName = null;
        while (iterator.hasNext()) { // Iterator over all extensions
            extension = iterator.next();
            for (String type : types) { // Iterate over all types
                dispName = extension.getDisplayNameForXSIType(type); // Check if extension knows a displayname for
                LOGGER.debug(
                        "extension: {}, type: {}, name: {}, dispName: {}", extension.getName(), type, name, dispName);
                // this type
                if ((name.equals(dispName))) {
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

        Iterator<IExtension> iterator = this.extensions.iterator();
        IExtension extension = null;
        String classname = null;
        while (iterator.hasNext()) {
            extension = iterator.next();

            if (extension.supports(xsiType)) {
                LOGGER.debug("New load without reflection");
                return extension.getFindingPanelForXSIType(xsiType, finding, session, target, editable);
            }

            classname = extension.getPanelForXSIType(xsiType, SchemaElementConstants.FINDING);
            if (classname != null) {
                break;
            }
        }

        if (classname == null) {
            LOGGER.error("No installed extension can handle the type: {}", xsiType);
            return null;
        }

        return (AbstractPanel) this.loadByReflection(
                classname, IFinding.class, finding, ISession.class, session, ITarget.class, target, editable);
    }

    private AbstractPanel getTargetPanelFromXSIType(
            String xsiType, ITarget target, IObservation observation, boolean editable) {

        Iterator<IExtension> iterator = this.extensions.iterator();
        IExtension extension = null;
        String classname = null;
        while (iterator.hasNext()) {
            extension = iterator.next();

            if (extension.supports(xsiType)) {
                LOGGER.debug("New load without reflection");
                return extension.getTargetPanelForXSIType(xsiType, target, observation, editable);
            }

            classname = extension.getPanelForXSIType(xsiType, SchemaElementConstants.TARGET);
            if (classname != null) {
                break;
            }
        }

        if (classname == null) {
            LOGGER.error("No installed extension can handle the type: {}", xsiType);
            return null;
        }

        return (AbstractPanel) this.loadByReflection(
                classname, ITarget.class, target, IObservation.class, observation, null, null, editable);
    }

    private ITargetDialog getTargetDialogFromXSIType(String xsiType, ITarget target, IObservation o) {

        Iterator<IExtension> iterator = this.extensions.iterator();
        IExtension extension = null;
        String classname = null;
        while (iterator.hasNext()) {
            extension = iterator.next();

            if (extension.supports(xsiType)) {
                LOGGER.debug("New load without reflection");
                return extension.getTargetDialogForXSIType(xsiType, this.observationManager, target, o, true);
            }

            classname = extension.getDialogForXSIType(xsiType, SchemaElementConstants.TARGET);
            if (classname != null) {
                break;
            }
        }

        if (classname == null) {
            LOGGER.error("No installed extension can handle the type: {}", xsiType);
            return null;
        }

        return (ITargetDialog)
                this.loadByReflection(classname, ITarget.class, target, IObservation.class, o, null, null, true);
    }

    private Object loadByReflection(
            String classname,
            Class<?> exampleClass,
            Object findingOrTarget,
            Class<?> additionalParameterClass1,
            Object additionalParameter1,
            Class<?> additionalParameterClass2,
            Object additionalParameter2,
            boolean editable) {

        LOGGER.warn("***WARNING*** loading by reflection class: {}, object: {}", classname, findingOrTarget);
        // Get Java class
        Class<?> currentClass = null;
        try {
            currentClass = Class.forName(classname);
        } catch (ClassNotFoundException cnfe) {
            LOGGER.error("Unable to load {}", classname, cnfe);
        }

        if (currentClass == null) {
            LOGGER.error("Class not found for {}", classname);
            return null;
        }

        // Get constructors for class
        Constructor<?>[] constructors = currentClass.getConstructors();
        Object object = null;
        if (constructors.length > 0) {
            try {
                Class<?>[] parameters = null;
                for (Constructor<?> constructor : constructors) {
                    parameters = constructor.getParameterTypes();
                    if ((parameters.length == 2)
                            && (parameters[0].isAssignableFrom(exampleClass))
                            && (parameters[1].isInstance(Boolean.FALSE))) {
                        object = constructor.newInstance(findingOrTarget, editable);
                        break;
                    } else if ((parameters.length == 2)
                            && (parameters[0].isInstance(this.observationManager))
                            && (parameters[1].isAssignableFrom(exampleClass))) {
                        object = constructor.newInstance(this.observationManager, findingOrTarget);
                        break;
                    } else if ((parameters.length == 3)
                            && (parameters[0].isInstance(this.observationManager))
                            && (parameters[1].isAssignableFrom(exampleClass))
                            && (parameters[2].isInstance(Boolean.FALSE))) {
                        object = constructor.newInstance(this.observationManager, findingOrTarget, editable);
                        break;
                    } else if ((parameters.length == 3)
                            && (parameters[0].isInstance(this.observationManager))
                            && (parameters[1].isInstance(this.model))
                            && (parameters[2].isAssignableFrom(exampleClass))) {
                        object = constructor.newInstance(this.observationManager, this.model, findingOrTarget);
                        break;
                    } else if ((parameters.length == 4)
                            && (parameters[0].isInstance(this.observationManager))
                            && (parameters[1].isAssignableFrom(exampleClass))
                            && (parameters[2].isAssignableFrom(additionalParameterClass1))
                            && (parameters[3].isInstance(Boolean.FALSE))) {
                        object = constructor.newInstance(
                                this.observationManager, findingOrTarget, additionalParameter1, editable);
                        break;
                    } else if ((parameters.length == 4)
                            && (parameters[0].isInstance(this.observationManager))
                            && (parameters[1].isInstance(this.model)
                                    && (parameters[3].isInstance(Boolean.FALSE))
                                    && (parameters[2].isAssignableFrom(exampleClass)))) {
                        object =
                                constructor.newInstance(this.observationManager, this.model, findingOrTarget, editable);
                        break;
                    } else if ((parameters.length == 5)
                            && (parameters[0].isInstance(this.observationManager))
                            && (parameters[1].isAssignableFrom(exampleClass))
                            && (parameters[2].isAssignableFrom(additionalParameterClass1))
                            && (parameters[3].isAssignableFrom(additionalParameterClass2))
                            && (parameters[4].isInstance(Boolean.FALSE))) {
                        object = constructor.newInstance(
                                this.observationManager,
                                findingOrTarget,
                                additionalParameter1,
                                additionalParameter2,
                                editable);
                        break;
                    } else if ((parameters.length == 1) // Maybe its the most simple extension of AbstractPanel?
                            && (parameters[0].isInstance(Boolean.FALSE))) {
                        object = constructor.newInstance(editable);
                        break;
                    } else {
                        LOGGER.error("Unable to instantiate class: {}. No constructor found", classname);
                    }
                }
            } catch (InstantiationException ie) {
                LOGGER.error("Unable to instantiate class: {}", classname, ie);
            } catch (InvocationTargetException ite) {
                LOGGER.error("Unable to invocate class: {} ", classname, ite);
            } catch (IllegalAccessException iae) {
                LOGGER.error("Unable to access class: {} ", classname, iae);
            }
        } else {
            LOGGER.error("Unable to load class: {}. Maybe class has no correct constructor. ", classname);
        }

        return object;
    }

    private Class<?> getExampleClass(SchemaElementConstants schemaElementConstant) {

        switch (schemaElementConstant) {
            case EYEPIECE: {
                return IEyepiece.class;
            }
            case FILTER: {
                return IFilter.class;
            }
            case FINDING: {
                return IFinding.class;
            }
            case IMAGER: {
                return IImager.class;
            }
            case LENS: {
                return ILens.class;
            }
            case OBSERVATION: {
                return IObservation.class;
            }
            case OBSERVER: {
                return IObserver.class;
            }
            case SCOPE: {
                return IScope.class;
            }
            case SESSION: {
                return ISession.class;
            }
            case SITE: {
                return ISite.class;
            }
            case TARGET: {
                return ITarget.class;
            }
            default:
                break;
        }

        return null;
    }

    private Object getSchemaElementUIObject(
            String xsiType,
            SchemaElementConstants schemaElementConstant,
            ISchemaElement schemaElement,
            boolean editable,
            boolean dialog) {

        Iterator<IExtension> iterator = this.extensions.iterator();
        IExtension extension = null;
        String classname = null;
        while (iterator.hasNext()) {
            extension = iterator.next();

            if (dialog) {

                if (extension.supports(xsiType)) {
                    // TODO: extract real types
                    LOGGER.debug("New load without reflection");
                    return extension.getImagerDialogForXSIType(
                            xsiType, this.observationManager, (IImager) schemaElement, true);
                }
                classname = extension.getDialogForXSIType(xsiType, schemaElementConstant);

            } else {
                classname = extension.getPanelForXSIType(xsiType, schemaElementConstant);
            }
            if (classname != null) {
                break;
            }
        }

        if (classname == null) {
            LOGGER.error("No installed extension can handle the type: {}", xsiType);
            return null;
        }

        Class<?> exampleClass = this.getExampleClass(schemaElementConstant);

        return this.loadByReflection(classname, exampleClass, schemaElement, null, null, null, null, editable);
    }
}
