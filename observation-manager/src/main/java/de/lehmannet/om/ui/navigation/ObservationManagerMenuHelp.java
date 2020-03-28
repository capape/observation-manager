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

public final class ObservationManagerMenuHelp {

    private final Logger LOGGER = LoggerFactory.getLogger(ObservationManagerMenuHelp.class);

    private final XMLFileLoader xmlCache;
    private final Configuration configuration;
    private final ObservationManager observationManager;
    

    public ObservationManagerMenuHelp(        
        Configuration configuration,
        XMLFileLoader xmlCache,
        ObservationManager om) {
       
        // Load configuration
        this.configuration = configuration; 
        this.xmlCache = xmlCache;
        this.observationManager = om;
 
    }
}