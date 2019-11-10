/* ====================================================================
 * /navigation/PopupMenuHandler.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.navigation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

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
import de.lehmannet.om.ui.dialog.EyepieceDialog;
import de.lehmannet.om.ui.dialog.FilterDialog;
import de.lehmannet.om.ui.dialog.IImagerDialog;
import de.lehmannet.om.ui.dialog.ITargetDialog;
import de.lehmannet.om.ui.dialog.LensDialog;
import de.lehmannet.om.ui.dialog.ObservationDialog;
import de.lehmannet.om.ui.dialog.ObserverDialog;
import de.lehmannet.om.ui.dialog.ScopeDialog;
import de.lehmannet.om.ui.dialog.SessionDialog;
import de.lehmannet.om.ui.dialog.SiteDialog;
import de.lehmannet.om.ui.extension.PopupMenuExtension;
import de.lehmannet.om.ui.util.ExtenableSchemaElementSelector;
import de.lehmannet.om.util.SchemaElementConstants;

public class PopupMenuHandler implements ActionListener {

    public static final byte CREATE = 0x01;
    public static final byte EDIT = 0x02;
    public static final byte DELETE = 0x04;
    public static final byte CREATE_HTML = 0x08;
    public static final byte CREATE_NEW_OBSERVATION = 0x10;
    public static final byte EXTENSIONS = 0x20;
    public static final byte CREATE_XML = 0x40;

    private final PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
            .getBundle("ObservationManager", Locale.getDefault());

    private ObservationManager observationManager = null;
    private ISchemaElement element = null;

    private JMenuItem create = null;
    private JMenuItem edit = null;
    private JMenuItem delete = null;
    private JMenuItem createHTML = null;
    private JMenuItem createNewObservation = null;
    private JMenuItem createXML = null;

    private int createType = SchemaElementConstants.NONE; // In case of create, this indicates which type of
                                                          // SchemaElement needs to be created

    public PopupMenuHandler(ObservationManager om, ISchemaElement se, int x, int y, byte operation, int createType,
            PopupMenuExtension[] extensions) {

        final int xSize = 150;
        final int ySize = 25;

        int entries = 1;

        this.observationManager = om;
        this.element = se;

        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem menuTitle = null;
        // Save schemaElement type we found as we need it for the extensions popup menus
        // again (see below)
        int seType = SchemaElementConstants.NONE;

        if (se instanceof IObservation || createType == 2) {
            menuTitle = new JMenuItem(this.bundle.getString("observation"));
            seType = SchemaElementConstants.OBSERVATION;
        } else if (se instanceof IObserver || createType == 6) {
            menuTitle = new JMenuItem(this.bundle.getString("observer"));
            seType = SchemaElementConstants.OBSERVER;
        } else if (se instanceof ISite || createType == 4) {
            menuTitle = new JMenuItem(this.bundle.getString("site"));
            seType = SchemaElementConstants.SITE;
        } else if (se instanceof ISession || createType == 5) {
            menuTitle = new JMenuItem(this.bundle.getString("session"));
            seType = SchemaElementConstants.SESSION;
        } else if (se instanceof IScope || createType == 1) {
            menuTitle = new JMenuItem(this.bundle.getString("scope"));
            seType = SchemaElementConstants.SCOPE;
        } else if (se instanceof IEyepiece || createType == 0) {
            menuTitle = new JMenuItem(this.bundle.getString("eyepiece"));
            seType = SchemaElementConstants.EYEPIECE;
        } else if (se instanceof IFilter || createType == 8) {
            menuTitle = new JMenuItem(this.bundle.getString("filter"));
            seType = SchemaElementConstants.FILTER;
        } else if (se instanceof ILens || createType == 9) {
            if (createType == 9) { // se object is NULL! (Root lens element clicked)
                menuTitle = new JMenuItem(this.bundle.getString("lens"));
            } else { // se object must not be NULL!
                if (((ILens) se).getFactor() > 1) {
                    menuTitle = new JMenuItem(this.bundle.getString("lens.barlow"));
                } else if (((ILens) se).getFactor() < 1) {
                    menuTitle = new JMenuItem(this.bundle.getString("lens.sharpley"));
                } else {
                    menuTitle = new JMenuItem(this.bundle.getString("lens"));
                }
            }
            seType = SchemaElementConstants.LENS;
        } else if (se instanceof IImager || createType == 3) {
            menuTitle = new JMenuItem(this.bundle.getString("imager"));
            seType = SchemaElementConstants.IMAGER;
        } else if (se instanceof ITarget || createType == 7) {
            menuTitle = new JMenuItem(this.bundle.getString("target"));
            seType = SchemaElementConstants.TARGET;
        }

        // Cannot identify element, stop here
        // This can happen e.g. when user clicks on root (file) element
        if (menuTitle == null) {
            return;
        }

        menuTitle.setEnabled(false);

        popupMenu.add(menuTitle);
        popupMenu.addSeparator();

        if (((operation & PopupMenuHandler.CREATE) == PopupMenuHandler.CREATE)
                && ((createType >= 0) && (createType < 10))) {
            this.createType = createType;

            this.create = new JMenuItem(this.bundle.getString("create"));
            this.create.addActionListener(this);
            popupMenu.add(this.create);
            entries++;
        }
        if ((operation & PopupMenuHandler.EDIT) == PopupMenuHandler.EDIT) {
            this.edit = new JMenuItem(this.bundle.getString("edit"));
            this.edit.addActionListener(this);
            popupMenu.add(this.edit);
            entries++;
        }
        if ((operation & PopupMenuHandler.DELETE) == PopupMenuHandler.DELETE) {
            this.delete = new JMenuItem(this.bundle.getString("delete"));
            this.delete.addActionListener(this);
            popupMenu.add(this.delete);
            entries++;
        }
        if (((operation & PopupMenuHandler.CREATE_HTML) == PopupMenuHandler.CREATE_HTML)
                && ((operation & PopupMenuHandler.CREATE_XML) == PopupMenuHandler.CREATE_XML)) {
            JMenu export = new JMenu(this.bundle.getString("menu.export"));

            // Add HTML export
            this.createHTML = new JMenuItem(this.bundle.getString("menu.htmlExport"));
            this.createHTML.addActionListener(this);
            export.add(this.createHTML);

            // Add OAL export
            this.createXML = new JMenuItem(this.bundle.getString("menu.oalExport"));
            this.createXML.addActionListener(this);
            export.add(this.createXML);

            entries++;
            popupMenu.add(export);

        } else {
            if (((operation & PopupMenuHandler.CREATE_HTML) == PopupMenuHandler.CREATE_HTML)
            /*
             * && ( (element instanceof IObservation) || (element instanceof ISession) )
             */
            ) {
                this.createHTML = new JMenuItem(this.bundle.getString("menu.htmlExport"));
                this.createHTML.addActionListener(this);
                popupMenu.add(this.createHTML);
                entries++;
            }
            if (((operation & PopupMenuHandler.CREATE_XML) == PopupMenuHandler.CREATE_XML)
            /*
             * && ( (element instanceof IObservation) || (element instanceof ISession) )
             */
            ) {
                this.createXML = new JMenuItem(this.bundle.getString("menu.oalExport"));
                this.createXML.addActionListener(this);
                popupMenu.add(this.createXML);
                entries++;
            }
        }
        if (((operation & PopupMenuHandler.CREATE_NEW_OBSERVATION) == PopupMenuHandler.CREATE_NEW_OBSERVATION)) {
            this.createNewObservation = new JMenuItem(this.bundle.getString("menu.newObservation"));
            this.createNewObservation.addActionListener(this);
            popupMenu.add(this.createNewObservation);
            entries++;
        }

        // Extensions
        if (((operation & PopupMenuHandler.EXTENSIONS) == PopupMenuHandler.EXTENSIONS) && (extensions != null)
                && (extensions.length > 0)) {

            int schemaElementTypes[] = null;
            int contained = -1;
            boolean found = false;
            for (int i = 0; i < extensions.length; i++) {
                schemaElementTypes = extensions[i].getSchemaElementTypes();
                // Check if schemaElementType is requested by the PopupMenuExtension
                contained = Arrays.binarySearch(schemaElementTypes, seType);
                if (contained >= 0) { // Found
                    // Add Menu separator only once
                    if (!found) {
                        popupMenu.addSeparator();
                    }
                    found = true;

                    popupMenu.add(extensions[i].getMenu());
                    entries++;
                }
            }

        }

        popupMenu.setPopupSize(xSize, entries * ySize);
        popupMenu.show(this.observationManager, x, y);

    }

    // --------------
    // ActionListener ---------------------------------------------------------
    // --------------

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() instanceof JMenuItem) {
            JMenuItem source = (JMenuItem) e.getSource();
            if (source.equals(this.edit)) {
                if (element instanceof IObservation) {
                    // Edit current/selected observation
                    ObservationDialog dialog = new ObservationDialog(this.observationManager,
                            (IObservation) this.element);
                    // Create new observation
                    while ((dialog == null) || ((dialog != null) && (dialog.isCreateAdditionalObservation()))) {
                        dialog = new ObservationDialog(this.observationManager, null);
                        this.observationManager.update(dialog.getObservation());
                    }
                } else if (element instanceof ITarget) {
                    ITarget target = (ITarget) element;
                    ITargetDialog dialog = this.observationManager.getExtensionLoader().getSchemaUILoader()
                            .getTargetDialog(target.getXSIType(), target, null);
                } else if (element instanceof IScope) {
                    ScopeDialog dialog = new ScopeDialog(this.observationManager, (IScope) this.element);
                } else if (element instanceof IEyepiece) {
                    EyepieceDialog dialog = new EyepieceDialog(this.observationManager, (IEyepiece) this.element);
                } else if (element instanceof IImager) {
                    IImager imager = (IImager) element;
                    IImagerDialog dialog = (IImagerDialog) this.observationManager.getExtensionLoader()
                            .getSchemaUILoader()
                            .getSchemaElementDialog(imager.getXSIType(), SchemaElementConstants.IMAGER, imager, true);
                } else if (element instanceof ISite) {
                    SiteDialog dialog = new SiteDialog(this.observationManager, (ISite) this.element);
                } else if (element instanceof IFilter) {
                    FilterDialog dialog = new FilterDialog(this.observationManager, (IFilter) this.element);
                } else if (element instanceof ISession) {
                    SessionDialog dialog = new SessionDialog(this.observationManager, (ISession) this.element);
                } else if (element instanceof IObserver) {
                    ObserverDialog dialog = new ObserverDialog(this.observationManager, (IObserver) this.element);
                } else if (element instanceof ILens) {
                    LensDialog dialog = new LensDialog(this.observationManager, (ILens) this.element);
                }
            } else if (source.equals(this.create)) {
                switch (this.createType) {
                case SchemaElementConstants.EYEPIECE: {
                    EyepieceDialog dialog = new EyepieceDialog(this.observationManager, null);
                    this.observationManager.update(dialog.getEyepiece());
                    break;
                }
                case SchemaElementConstants.SCOPE: {
                    ScopeDialog dialog = new ScopeDialog(this.observationManager, null);
                    this.observationManager.update(dialog.getScope());
                    break;
                }
                case SchemaElementConstants.OBSERVATION: {
                    ObservationDialog dialog = null;
                    while ((dialog == null) || ((dialog != null) && (dialog.isCreateAdditionalObservation()))) {
                        dialog = new ObservationDialog(this.observationManager, null);
                        this.observationManager.update(dialog.getObservation());
                    }
                    break;
                }
                case SchemaElementConstants.IMAGER: {
                    ExtenableSchemaElementSelector is = new ExtenableSchemaElementSelector(this.observationManager,
                            this.observationManager.getExtensionLoader().getSchemaUILoader(),
                            SchemaElementConstants.IMAGER);
                    if (is.getResult()) {
                        // Get Imager Dialog
                        IImagerDialog imagerDialog = (IImagerDialog) is.getDialog();
                        this.observationManager.update(imagerDialog.getImager());
                    }
                    break;
                }
                case SchemaElementConstants.SITE: {
                    SiteDialog dialog = new SiteDialog(this.observationManager, null);
                    this.observationManager.update(dialog.getSite());
                    break;
                }
                case SchemaElementConstants.SESSION: {
                    SessionDialog dialog = new SessionDialog(this.observationManager, null);
                    this.observationManager.update(dialog.getSession());
                    break;
                }
                case SchemaElementConstants.OBSERVER: {
                    ObserverDialog dialog = new ObserverDialog(this.observationManager, null);
                    this.observationManager.update(dialog.getObserver());
                    break;
                }
                case SchemaElementConstants.FILTER: {
                    FilterDialog dialog = new FilterDialog(this.observationManager, null);
                    this.observationManager.update(dialog.getFilter());
                    break;
                }
                case SchemaElementConstants.TARGET: {
                    ExtenableSchemaElementSelector ts = new ExtenableSchemaElementSelector(this.observationManager,
                            this.observationManager.getExtensionLoader().getSchemaUILoader(),
                            SchemaElementConstants.TARGET);
                    if (ts.getResult()) {
                        // Get TargetContainer
                        ITargetDialog targetDialog = (ITargetDialog) ts.getDialog();
                        this.observationManager.update(targetDialog.getTarget());
                    }
                    break;
                }
                case SchemaElementConstants.LENS: {
                    LensDialog dialog = new LensDialog(this.observationManager, null);
                    this.observationManager.update(dialog.getLens());
                    break;
                }
                }
            } else if (source.equals(this.delete)) {
                this.observationManager.deleteSchemaElement(element);
            } else if (source.equals(this.createHTML)) {
                this.observationManager.createHTMLForSchemaElement(element);
            } else if (source.equals(this.createXML)) {
                this.observationManager.createXMLForSchemaElement(element);
            } else if (source.equals(this.createNewObservation)) {
                ObservationDialog dialog = null;
                while ((dialog == null) || ((dialog != null) && (dialog.isCreateAdditionalObservation()))) {
                    dialog = new ObservationDialog(this.observationManager, null, element);
                    this.observationManager.update(dialog.getObservation());
                }
            }
        }
    }

}
