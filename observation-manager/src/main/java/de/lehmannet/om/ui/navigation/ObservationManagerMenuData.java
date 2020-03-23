package de.lehmannet.om.ui.navigation;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Date;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import javax.swing.filechooser.FileFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.lehmannet.om.IEyepiece;
import de.lehmannet.om.IFilter;
import de.lehmannet.om.IImager;
import de.lehmannet.om.ILens;
import de.lehmannet.om.IObservation;
import de.lehmannet.om.IObserver;
import de.lehmannet.om.IScope;
import de.lehmannet.om.ISession;
import de.lehmannet.om.ISite;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.ui.dialog.EyepieceDialog;
import de.lehmannet.om.ui.dialog.FilterDialog;
import de.lehmannet.om.ui.dialog.IImagerDialog;
import de.lehmannet.om.ui.dialog.ITargetDialog;
import de.lehmannet.om.ui.dialog.LensDialog;
import de.lehmannet.om.ui.dialog.NewDocumentDialog;
import de.lehmannet.om.ui.dialog.ObservationDialog;
import de.lehmannet.om.ui.dialog.ObserverDialog;
import de.lehmannet.om.ui.dialog.ProgressDialog;
import de.lehmannet.om.ui.dialog.ScopeDialog;
import de.lehmannet.om.ui.dialog.SessionDialog;
import de.lehmannet.om.ui.dialog.SiteDialog;
import de.lehmannet.om.ui.navigation.observation.utils.SystemInfo;
import de.lehmannet.om.ui.util.Configuration;
import de.lehmannet.om.ui.util.ExtenableSchemaElementSelector;
import de.lehmannet.om.ui.util.Worker;
import de.lehmannet.om.ui.util.XMLFileLoader;
import de.lehmannet.om.util.SchemaElementConstants;

public final class ObservationManagerMenuData {

    private final Logger LOGGER = LoggerFactory.getLogger(ObservationManagerMenuData.class);

    private final XMLFileLoader xmlCache;
    private final Configuration configuration;
    private final ObservationManager observationManager;
    

    public ObservationManagerMenuData(        
        Configuration configuration,
        XMLFileLoader xmlCache,
        ObservationManager om) {
       
        // Load configuration
        this.configuration = configuration; 
        this.xmlCache = xmlCache;
        this.observationManager = om;
 
    }

    public void createNewObservation() {

        ObservationDialog dialog = null;
        while (dialog == null || dialog.isCreateAdditionalObservation()) {
            dialog = new ObservationDialog(this.observationManager, null);
            this.xmlCache.addSchemaElement(dialog.getObservation());
            this.observationManager.updateLeft(); // Refreshes tree (without that, the new element
                               // won't appear on UI)
            this.observationManager.updateUI(dialog.getObservation()); // Sets selection in tree
                                                    // (and table) on new
                                                    // element
        }

    }

    public void createNewObserver() {

        ObserverDialog dialog = new ObserverDialog(this.observationManager, null);
        this.xmlCache.addSchemaElement(dialog.getObserver());
        this.observationManager.updateLeft(); // Refreshes tree (without that, the new element
                           // won't appear on UI)
        this.observationManager.updateUI(dialog.getObserver()); // Sets selection in tree (and
                                             // table) on new element

    }

    public void createNewSession() {

        SessionDialog dialog = new SessionDialog(this.observationManager, null);
        this.xmlCache.addSchemaElement(dialog.getSession());
        this.observationManager.updateLeft(); // Refreshes tree (without that, the new element
                           // won't appear on UI)
        this.observationManager.updateUI(dialog.getSession()); // Sets selection in tree (and
                                            // table) on new element

    }

    public void createNewSite() {

        SiteDialog dialog = new SiteDialog(this.observationManager, null);
        this.xmlCache.addSchemaElement(dialog.getSite());
        this.observationManager.updateLeft(); // Refreshes tree (without that, the new element
                           // won't appear on UI)
        this.observationManager.updateUI(dialog.getSite()); // Sets selection in tree (and table)
                                         // on new element

    }

    public void createNewScope() {

        ScopeDialog dialog = new ScopeDialog(this.observationManager, null);
        this.xmlCache.addSchemaElement(dialog.getScope());
        this.observationManager.updateLeft(); // Refreshes tree (without that, the new element
                           // won't appear on UI)
        this.observationManager.updateUI(dialog.getScope()); // Sets selection in tree (and table)
                                          // on new element

    }

    public void createNewEyepiece() {

        EyepieceDialog dialog = new EyepieceDialog(this.observationManager, null);
        this.xmlCache.addSchemaElement(dialog.getEyepiece());
        this.observationManager.updateLeft(); // Refreshes tree (without that, the new element
                           // won't appear on UI)
        this.observationManager.updateUI(dialog.getEyepiece()); // Sets selection in tree (and
                                             // table) on new element

    }

    public void createNewImager() {

        ExtenableSchemaElementSelector is = new ExtenableSchemaElementSelector(this.observationManager,
         this.observationManager.getExtensionLoader().getSchemaUILoader(),
                SchemaElementConstants.IMAGER);
        if (is.getResult()) {
            // Get Imager Dialog
            IImagerDialog imagerDialog = (IImagerDialog) is.getDialog();
            this.xmlCache.addSchemaElement(imagerDialog.getImager());
            this.observationManager.updateLeft(); // Refreshes tree (without that, the new element
                               // won't appear on UI)
            this.observationManager.updateUI(imagerDialog.getImager()); // Sets selection in tree
                                                     // (and table) on new
                                                     // element
        }

    }

    public void createNewFilter() {

        FilterDialog dialog = new FilterDialog(this.observationManager, null);
        this.xmlCache.addSchemaElement(dialog.getFilter());
        this.observationManager.updateLeft(); // Refreshes tree (without that, the new element
                           // won't appear on UI)
        this.observationManager.updateUI(dialog.getFilter()); // Sets selection in tree (and table)
                                           // on new element

    }

    public void createNewTarget() {

        ExtenableSchemaElementSelector ts = new ExtenableSchemaElementSelector(this.observationManager,
         this.observationManager.getExtensionLoader().getSchemaUILoader(),
                SchemaElementConstants.TARGET);
        if (ts.getResult()) {
            // Get TargetContainer
            ITargetDialog targetDialog = (ITargetDialog) ts.getDialog();
            this.xmlCache.addSchemaElement(targetDialog.getTarget());
            this.observationManager.updateLeft(); // Refreshes tree (without that, the new element
                               // won't appear on UI)
            this.observationManager.updateUI(targetDialog.getTarget()); // Sets selection in tree
                                                     // (and table) on new
                                                     // element
        }

    }

    public void createNewLens() {

        LensDialog dialog = new LensDialog(this.observationManager, null);
        this.xmlCache.addSchemaElement(dialog.getLens());
        this.observationManager.updateLeft(); // Refreshes tree (without that, the new element
                           // won't appear on UI)
        this.observationManager.updateUI(dialog.getLens()); // Sets selection in tree (and table)
                                         // on new element

    }
}
