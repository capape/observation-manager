/* ====================================================================
 * /extension/SchemaUILoader.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

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
import de.lehmannet.om.ui.dialog.AbstractDialog;
import de.lehmannet.om.ui.dialog.ITargetDialog;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.util.SchemaElementConstants;

public class SchemaUILoader {

    private ObservationManager observationManager = null;
    private List extensions = null;

    public SchemaUILoader(ObservationManager om, List extensions) {

        this.observationManager = om;
        this.extensions = extensions;

    }

    public AbstractPanel getFindingPanel(String xsiType, IFinding finding, ISession s, boolean editable) {

        return this.getFindingPanelFromXSIType(xsiType, finding, s, null, editable);

    }

    public AbstractPanel getFindingPanel(String xsiType, IFinding finding, ISession s, ITarget t, boolean editable) {

        return this.getFindingPanelFromXSIType(xsiType, finding, s, t, editable);

    }

    public AbstractPanel getFindingPanel(ITarget target, IFinding finding, boolean editable) {

        return this.getFindingPanel(target.getXSIType(), finding, null, editable);

    }

    public ITargetDialog getTargetDialog(String xsiType, ITarget target, IObservation o) {

        return this.getTargetDailogFromXSIType(xsiType, target, o, true);

    }

    public AbstractPanel getTargetPanel(String xsiType, ITarget target, IObservation o, boolean editable) {

        return this.getTargetPanelFromXSIType(xsiType, target, o, editable);

    }

    public AbstractPanel getSchemaElementPanel(String xsiType, int schemaElementConstant, ISchemaElement schemaElement,
            boolean editable) {

        return (AbstractPanel) this.getSchemaElementUIObject(xsiType, schemaElementConstant, schemaElement, editable,
                false);

    }

    public AbstractDialog getSchemaElementDialog(String xsiType, int schemaElementConstant,
            ISchemaElement schemaElement, boolean editable) {

        return (AbstractDialog) this.getSchemaElementUIObject(xsiType, schemaElementConstant, schemaElement, editable,
                true);

    }

    public String[] getAllXSITypes() {

        Iterator iterator = this.extensions.iterator();
        IExtension extension = null;
        HashSet result = new HashSet();
        while (iterator.hasNext()) {
            extension = (IExtension) iterator.next();
            if (extension.getAllSupportedXSITypes() != null) {
                result.addAll(extension.getAllSupportedXSITypes());
            }
        }

        return (String[]) result.toArray(new String[] {});

    }

    public String[] getAllXSITypes(int schemaElementConstants) {

        Iterator iterator = this.extensions.iterator();
        IExtension extension = null;
        HashSet result = new HashSet();
        while (iterator.hasNext()) {
            extension = (IExtension) iterator.next();
            if (extension.getSupportedXSITypes(schemaElementConstants) != null) {
                result.addAll(extension.getSupportedXSITypes(schemaElementConstants));
            }
        }

        return (String[]) result.toArray(new String[] {});

    }

    public String[] getAllXSIDisplayNames(int schemaElementConstants) {

        String[] types = this.getAllXSITypes(schemaElementConstants);

        Iterator iterator = this.extensions.iterator();
        IExtension extension = null;
        ArrayList result = new ArrayList();
        String dispName = null;
        while (iterator.hasNext()) {
            extension = (IExtension) iterator.next();
            for (int i = 0; i < types.length; i++) {
                dispName = extension.getDisplayNameForXSIType(types[i]);
                if (dispName != null) {
                    result.add(dispName);
                }
            }
        }

        return (String[]) result.toArray(new String[] {});

    }

    public String[] getAllXSIDisplayNamesForCreation(int schemaElementConstants) {

        String[] types = this.getAllXSITypes(schemaElementConstants);

        Iterator iterator = this.extensions.iterator();
        IExtension extension = null;
        ArrayList result = new ArrayList();
        String dispName = null;
        while (iterator.hasNext()) {
            extension = (IExtension) iterator.next();
            for (int i = 0; i < types.length; i++) {
                if (extension.isCreationAllowed(types[i])) {
                    dispName = extension.getDisplayNameForXSIType(types[i]);
                    if (dispName != null) {
                        result.add(dispName);
                    }
                }
            }
        }

        return (String[]) result.toArray(new String[] {});

    }

    public String getDisplayNameForType(String type) {

        Iterator iterator = this.extensions.iterator();
        IExtension currentExtension = null;
        String result = null;
        while (iterator.hasNext()) {
            currentExtension = (IExtension) iterator.next();
            result = currentExtension.getDisplayNameForXSIType(type);
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

        Iterator iterator = this.extensions.iterator();
        IExtension extension = null;
        String dispName = null;
        while (iterator.hasNext()) { // Iterator over all extensions
            extension = (IExtension) iterator.next();
            for (int i = 0; i < types.length; i++) { // Iterate over all types
                dispName = extension.getDisplayNameForXSIType(types[i]); // Check if extension knows a displayname for
                                                                         // this type
                if ((dispName != null) && (name.equals(dispName))) {
                    return types[i]; // Displayname found for this type
                }
            }
        }

        return null;

    }

    // ---------------
    // Private Methods --------------------------------------------------------
    // ---------------

    private AbstractPanel getFindingPanelFromXSIType(String xsiType, IFinding finding, ISession s, ITarget t,
            boolean editable) {

        Iterator iterator = this.extensions.iterator();
        IExtension extension = null;
        String classname = null;
        while (iterator.hasNext()) {
            extension = (IExtension) iterator.next();
            classname = extension.getPanelForXSIType(xsiType, SchemaElementConstants.FINDING);
            if (classname != null) {
                break;
            }
        }

        if (classname == null) {
            System.err.println("No installed extension can handle the type: " + xsiType);
            return null;
        }

        return (AbstractPanel) this.loadByReflection(classname, IFinding.class, finding, ISession.class, s,
                ITarget.class, t, editable);

    }

    private AbstractPanel getTargetPanelFromXSIType(String xsiType, ITarget target, IObservation o, boolean editable) {

        Iterator iterator = this.extensions.iterator();
        IExtension extension = null;
        String classname = null;
        while (iterator.hasNext()) {
            extension = (IExtension) iterator.next();
            classname = extension.getPanelForXSIType(xsiType, SchemaElementConstants.TARGET);
            if (classname != null) {
                break;
            }
        }

        if (classname == null) {
            System.err.println("No installed extension can handle the type: " + xsiType);
            return null;
        }

        return (AbstractPanel) this.loadByReflection(classname, ITarget.class, target, IObservation.class, o, null,
                null, editable);

    }

    private ITargetDialog getTargetDailogFromXSIType(String xsiType, ITarget target, IObservation o, boolean editable) {

        Iterator iterator = this.extensions.iterator();
        IExtension extension = null;
        String classname = null;
        while (iterator.hasNext()) {
            extension = (IExtension) iterator.next();
            classname = extension.getDialogForXSIType(xsiType, SchemaElementConstants.TARGET);
            if (classname != null) {
                break;
            }
        }

        if (classname == null) {
            System.err.println("No installed extension can handle the type: " + xsiType);
            return null;
        }

        return (ITargetDialog) this.loadByReflection(classname, ITarget.class, target, IObservation.class, o, null,
                null, editable);

    }

    private Object loadByReflection(String classname, Class exampleClass, Object findingOrTarget,
            Class additionalParameterClass1, Object additionalParameter1, Class additionalParameterClass2,
            Object additionalParameter2, boolean editable) {

        // Get Java class
        Class currentClass = null;
        try {
            currentClass = Class.forName(classname);
        } catch (ClassNotFoundException cnfe) {
            System.err.println("Unable to load " + currentClass + "\n" + cnfe);
        }

        if (currentClass == null) {
            System.err.println("Class not found for " + classname);
        }

        // Get constructors for class
        Constructor[] constructors = currentClass.getConstructors();
        Object object = null;
        if (constructors.length > 0) {
            try {
                Class[] parameters = null;
                for (int i = 0; i < constructors.length; i++) {
                    parameters = constructors[i].getParameterTypes();
                    if ((parameters.length == 2) && (parameters[0].isAssignableFrom(exampleClass))
                            && (parameters[1].isInstance(new Boolean(false)))) {
                        object = constructors[i].newInstance(new Object[] { findingOrTarget, new Boolean(editable) });
                        break;
                    } else if ((parameters.length == 2) && (parameters[0].isInstance(this.observationManager))
                            && (parameters[1].isAssignableFrom(exampleClass))) {
                        object = constructors[i].newInstance(new Object[] { this.observationManager, findingOrTarget });
                        break;
                    } else if ((parameters.length == 3) && (parameters[0].isInstance(this.observationManager))
                            && (parameters[1].isAssignableFrom(exampleClass))
                            && (parameters[2].isInstance(new Boolean(false)))) {
                        object = constructors[i].newInstance(
                                new Object[] { this.observationManager, findingOrTarget, new Boolean(editable) });
                        break;
                    } else if ((parameters.length == 4) && (parameters[0].isInstance(this.observationManager))
                            && (parameters[1].isAssignableFrom(exampleClass))
                            && (parameters[2].isAssignableFrom(additionalParameterClass1))
                            && (parameters[3].isInstance(new Boolean(false)))) {
                        object = constructors[i].newInstance(new Object[] { this.observationManager, findingOrTarget,
                                additionalParameter1, new Boolean(editable) });
                        break;
                    } else if ((parameters.length == 5) && (parameters[0].isInstance(this.observationManager))
                            && (parameters[1].isAssignableFrom(exampleClass))
                            && (parameters[2].isAssignableFrom(additionalParameterClass1))
                            && (parameters[3].isAssignableFrom(additionalParameterClass2))
                            && (parameters[4].isInstance(new Boolean(false)))) {
                        object = constructors[i].newInstance(new Object[] { this.observationManager, findingOrTarget,
                                additionalParameter1, additionalParameter2, new Boolean(editable) });
                        break;
                    } else if ((parameters.length == 1) // Maybe its the most simple extension of AbstractPanel?
                            && (parameters[0].isInstance(new Boolean(false)))) {
                        object = constructors[i].newInstance(new Object[] { new Boolean(editable) });
                        break;
                    }
                }
            } catch (InstantiationException ie) {
                System.err.println("Unable to instantiate class: " + classname + "\n" + ie.getMessage());
            } catch (InvocationTargetException ite) {
                System.err.println("Unable to invocate class: " + classname + "\n" + ite.getMessage());
            } catch (IllegalAccessException iae) {
                System.err.println("Unable to access class: " + classname + "\n" + iae.getMessage());
            }
        } else {
            System.err.println("Unable to load class: " + classname + "\nMaybe class has no correct constructor. ");
        }

        return object;

    }

    private Class getExampleClass(int schemaElementConstant) {

        switch (schemaElementConstant) {
        case SchemaElementConstants.EYEPIECE: {
            return IEyepiece.class;
        }
        case SchemaElementConstants.FILTER: {
            return IFilter.class;
        }
        case SchemaElementConstants.FINDING: {
            return IFinding.class;
        }
        case SchemaElementConstants.IMAGER: {
            return IImager.class;
        }
        case SchemaElementConstants.LENS: {
            return ILens.class;
        }
        case SchemaElementConstants.OBSERVATION: {
            return IObservation.class;
        }
        case SchemaElementConstants.OBSERVER: {
            return IObserver.class;
        }
        case SchemaElementConstants.SCOPE: {
            return IScope.class;
        }
        case SchemaElementConstants.SESSION: {
            return ISession.class;
        }
        case SchemaElementConstants.SITE: {
            return ISite.class;
        }
        case SchemaElementConstants.TARGET: {
            return ITarget.class;
        }
        }
        ;

        return null;

    }

    private Object getSchemaElementUIObject(String xsiType, int schemaElementConstant, ISchemaElement schemaElement,
            boolean editable, boolean dialog) {

        Iterator iterator = this.extensions.iterator();
        IExtension extension = null;
        String classname = null;
        while (iterator.hasNext()) {
            extension = (IExtension) iterator.next();
            if (dialog) {
                classname = extension.getDialogForXSIType(xsiType, schemaElementConstant);
            } else {
                classname = extension.getPanelForXSIType(xsiType, schemaElementConstant);
            }
            if (classname != null) {
                break;
            }
        }

        if (classname == null) {
            System.err.println("No installed extension can handle the type: " + xsiType);
            return null;
        }

        Class exampleClass = this.getExampleClass(schemaElementConstant);

        return this.loadByReflection(classname, exampleClass, schemaElement, null, null, null, null, editable);

    }

}
