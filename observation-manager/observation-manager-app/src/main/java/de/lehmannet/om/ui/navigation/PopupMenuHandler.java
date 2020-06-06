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
import de.lehmannet.om.model.ObservationManagerModel;
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
import de.lehmannet.om.ui.i18n.TextManager;
import de.lehmannet.om.ui.util.ExtenableSchemaElementSelector;
import de.lehmannet.om.util.SchemaElementConstants;

class PopupMenuHandler implements ActionListener {

    public static final byte CREATE = 0x01;
    public static final byte EDIT = 0x02;
    public static final byte DELETE = 0x04;
    public static final byte CREATE_HTML = 0x08;
    public static final byte CREATE_NEW_OBSERVATION = 0x10;
    public static final byte EXTENSIONS = 0x20;
    public static final byte CREATE_XML = 0x40;

    private ObservationManager observationManager = null;
    private ISchemaElement element = null;

    private JMenuItem create = null;
    private JMenuItem edit = null;
    private JMenuItem delete = null;
    private JMenuItem createHTML = null;
    private JMenuItem createNewObservation = null;
    private JMenuItem createXML = null;

    private SchemaElementConstants createType = SchemaElementConstants.NONE; // In case of create, this indicates which
                                                                             // type of
    // SchemaElement needs to be created

    private final ObservationManagerModel model;
    private final TextManager textManager;

    public PopupMenuHandler(ObservationManager om, ObservationManagerModel model, TextManager textManager,
            ISchemaElement se, int x, int y, byte operation, SchemaElementConstants createType,
            PopupMenuExtension[] extensions) {

        final int xSize = 450;
        final int ySize = 25;

        int entries = 1;

        this.observationManager = om;
        this.element = se;
        this.model = model;
        this.textManager = textManager;

        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem menuTitle = null;
        // Save schemaElement type we found as we need it for the extensions popup menus
        // again (see below)
        SchemaElementConstants seType = SchemaElementConstants.NONE;

        PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle.getBundle("ObservationManager",
                Locale.getDefault());
        if (se instanceof IObservation || createType == SchemaElementConstants.OBSERVATION) {
            menuTitle = new JMenuItem(bundle.getString("observation"));
            seType = SchemaElementConstants.OBSERVATION;
        } else if (se instanceof IObserver || createType == SchemaElementConstants.OBSERVER) {
            menuTitle = new JMenuItem(bundle.getString("observer"));
            seType = SchemaElementConstants.OBSERVER;
        } else if (se instanceof ISite || createType == SchemaElementConstants.SITE) {
            menuTitle = new JMenuItem(bundle.getString("site"));
            seType = SchemaElementConstants.SITE;
        } else if (se instanceof ISession || createType == SchemaElementConstants.SESSION) {
            menuTitle = new JMenuItem(bundle.getString("session"));
            seType = SchemaElementConstants.SESSION;
        } else if (se instanceof IScope || createType == SchemaElementConstants.SCOPE) {
            menuTitle = new JMenuItem(bundle.getString("scope"));
            seType = SchemaElementConstants.SCOPE;
        } else if (se instanceof IEyepiece || createType == SchemaElementConstants.EYEPIECE) {
            menuTitle = new JMenuItem(bundle.getString("eyepiece"));
            seType = SchemaElementConstants.EYEPIECE;
        } else if (se instanceof IFilter || createType == SchemaElementConstants.FILTER) {
            menuTitle = new JMenuItem(bundle.getString("filter"));
            seType = SchemaElementConstants.FILTER;
        } else if (se instanceof ILens || createType == SchemaElementConstants.LENS) {
            if (createType == SchemaElementConstants.LENS) { // se object is NULL! (Root lens element clicked)
                menuTitle = new JMenuItem(bundle.getString("lens"));
            } else { // se object must not be NULL!
                if (((ILens) se).getFactor() > 1) {
                    menuTitle = new JMenuItem(bundle.getString("lens.barlow"));
                } else if (((ILens) se).getFactor() < 1) {
                    menuTitle = new JMenuItem(bundle.getString("lens.sharpley"));
                } else {
                    menuTitle = new JMenuItem(bundle.getString("lens"));
                }
            }
            seType = SchemaElementConstants.LENS;
        } else if (se instanceof IImager || createType == SchemaElementConstants.IMAGER) {
            menuTitle = new JMenuItem(bundle.getString("imager"));
            seType = SchemaElementConstants.IMAGER;
        } else if (se instanceof ITarget || createType == SchemaElementConstants.TARGET) {
            menuTitle = new JMenuItem(bundle.getString("target"));
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
                && createType != SchemaElementConstants.NONE) {
            this.createType = createType;

            this.create = new JMenuItem(bundle.getString("create"));
            this.create.addActionListener(this);
            popupMenu.add(this.create);
            entries++;
        }
        if ((operation & PopupMenuHandler.EDIT) == PopupMenuHandler.EDIT) {
            this.edit = new JMenuItem(bundle.getString("edit"));
            this.edit.addActionListener(this);
            popupMenu.add(this.edit);
            entries++;
        }
        if ((operation & PopupMenuHandler.DELETE) == PopupMenuHandler.DELETE) {
            this.delete = new JMenuItem(bundle.getString("delete"));
            this.delete.addActionListener(this);
            popupMenu.add(this.delete);
            entries++;
        }
        if (((operation & PopupMenuHandler.CREATE_HTML) == PopupMenuHandler.CREATE_HTML)
                && ((operation & PopupMenuHandler.CREATE_XML) == PopupMenuHandler.CREATE_XML)) {
            JMenu export = new JMenu(bundle.getString("menu.export"));

            // Add HTML export
            this.createHTML = new JMenuItem(bundle.getString("menu.htmlExport"));
            this.createHTML.addActionListener(this);
            export.add(this.createHTML);

            // Add OAL export
            this.createXML = new JMenuItem(bundle.getString("menu.oalExport"));
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
                this.createHTML = new JMenuItem(bundle.getString("menu.htmlExport"));
                this.createHTML.addActionListener(this);
                popupMenu.add(this.createHTML);
                entries++;
            }
            if (((operation & PopupMenuHandler.CREATE_XML) == PopupMenuHandler.CREATE_XML)
            /*
             * && ( (element instanceof IObservation) || (element instanceof ISession) )
             */
            ) {
                this.createXML = new JMenuItem(bundle.getString("menu.oalExport"));
                this.createXML.addActionListener(this);
                popupMenu.add(this.createXML);
                entries++;
            }
        }
        if (((operation & PopupMenuHandler.CREATE_NEW_OBSERVATION) == PopupMenuHandler.CREATE_NEW_OBSERVATION)) {
            this.createNewObservation = new JMenuItem(bundle.getString("menu.newObservation"));
            this.createNewObservation.addActionListener(this);
            popupMenu.add(this.createNewObservation);
            entries++;
        }

        // Extensions
        if (((operation & PopupMenuHandler.EXTENSIONS) == PopupMenuHandler.EXTENSIONS) && (extensions != null)
                && (extensions.length > 0)) {

            SchemaElementConstants[] schemaElementTypes = null;
            int contained = -1;
            boolean found = false;
            for (PopupMenuExtension extension : extensions) {
                schemaElementTypes = extension.getSchemaElementTypes();
                // Check if schemaElementType is requested by the PopupMenuExtension
                contained = Arrays.binarySearch(schemaElementTypes, seType);
                if (contained >= 0) { // Found
                    // Add Menu separator only once
                    if (!found) {
                        popupMenu.addSeparator();
                    }
                    found = true;

                    popupMenu.add(extension.getMenu());
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
                    ObservationDialog dialog = new ObservationDialog(this.observationManager, this.model,
                            this.textManager, (IObservation) this.element);
                    // Create new observation
                    while (dialog == null || dialog.isCreateAdditionalObservation()) {
                        dialog = new ObservationDialog(this.observationManager, this.model, this.textManager, null);
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
                    SessionDialog dialog = new SessionDialog(this.observationManager, this.model,
                            (ISession) this.element);
                } else if (element instanceof IObserver) {
                    ObserverDialog dialog = new ObserverDialog(this.observationManager, (IObserver) this.element);
                } else if (element instanceof ILens) {
                    LensDialog dialog = new LensDialog(this.observationManager, (ILens) this.element);
                }
            } else if (source.equals(this.create)) {
                switch (this.createType) {
                case EYEPIECE: {
                    EyepieceDialog dialog = new EyepieceDialog(this.observationManager, null);
                    this.observationManager.update(dialog.getEyepiece());
                    break;
                }
                case SCOPE: {
                    ScopeDialog dialog = new ScopeDialog(this.observationManager, null);
                    this.observationManager.update(dialog.getScope());
                    break;
                }
                case OBSERVATION: {
                    ObservationDialog dialog = null;
                    while (dialog == null || dialog.isCreateAdditionalObservation()) {
                        dialog = new ObservationDialog(this.observationManager, this.model, this.textManager, null);
                        this.observationManager.update(dialog.getObservation());
                    }
                    break;
                }
                case IMAGER: {
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
                case SITE: {
                    SiteDialog dialog = new SiteDialog(this.observationManager, null);
                    this.observationManager.update(dialog.getSite());
                    break;
                }
                case SESSION: {
                    SessionDialog dialog = new SessionDialog(this.observationManager, this.model, null);
                    this.observationManager.update(dialog.getSession());
                    break;
                }
                case OBSERVER: {
                    ObserverDialog dialog = new ObserverDialog(this.observationManager, null);
                    this.observationManager.update(dialog.getObserver());
                    break;
                }
                case FILTER: {
                    FilterDialog dialog = new FilterDialog(this.observationManager, null);
                    this.observationManager.update(dialog.getFilter());
                    break;
                }
                case TARGET: {
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
                case LENS: {
                    LensDialog dialog = new LensDialog(this.observationManager, null);
                    this.observationManager.update(dialog.getLens());
                    break;
                }
                default:
                    break;
                }
            } else if (source.equals(this.delete)) {
                this.observationManager.deleteSchemaElement(element);
            } else if (source.equals(this.createHTML)) {
                this.observationManager.getHtmlHelper().createHTMLForSchemaElement(element);
            } else if (source.equals(this.createXML)) {
                this.observationManager.getHtmlHelper().createXMLForSchemaElement(element);
            } else if (source.equals(this.createNewObservation)) {
                ObservationDialog dialog = null;
                while (dialog == null || dialog.isCreateAdditionalObservation()) {
                    dialog = new ObservationDialog(this.observationManager, this.model, this.textManager, null,
                            element);
                    this.observationManager.update(dialog.getObservation());
                }
            }
        }
    }

}
