package de.lehmannet.om.ui.navigation;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import de.lehmannet.om.ui.dialog.UnavailableEquipmentDialog;
import de.lehmannet.om.ui.image.ImageResolver;
import de.lehmannet.om.ui.util.ExtenableSchemaElementSelector;
import de.lehmannet.om.ui.util.IConfiguration;
import de.lehmannet.om.ui.util.XMLFileLoader;
import de.lehmannet.om.util.SchemaElementConstants;

public final class ObservationManagerMenuData {

    private final Logger LOGGER = LoggerFactory.getLogger(ObservationManagerMenuData.class);

    private final XMLFileLoader xmlCache;
    private final IConfiguration configuration;
    private final ImageResolver imageResolver;
    private final ObservationManager observationManager;
    private final JMenu menu;
    

    public ObservationManagerMenuData(        
        IConfiguration configuration,
        XMLFileLoader xmlCache,
        ImageResolver imageResolver,
        ObservationManager om) {
       
        // Load configuration
        this.configuration = configuration; 
        this.xmlCache = xmlCache;
        this.observationManager = om;
        this.imageResolver = imageResolver;

        this.menu = this.createMenuDataItems();
 
    }

    public JMenu getMenu() {
        return this.menu;
    }

    private JMenu createMenuDataItems() {

        final int menuKeyModifier = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        // ----- Data Menu
        final JMenu dataMenu = new JMenu(ObservationManager.bundle.getString("menu.data"));
        dataMenu.setMnemonic('d');
        

        JMenuItem createObservation = new JMenuItem(ObservationManager.bundle.getString("menu.createObservation"),
                new ImageIcon(this.imageResolver.getImageURL("observation_l.png").orElse(null), ""));
        createObservation.setMnemonic('o');
        createObservation.addActionListener(new CreateObservationListener());
        createObservation.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, menuKeyModifier));
        dataMenu.add(createObservation);

        // Seperate Observation from the rest
        dataMenu.addSeparator();

        JMenuItem createObserver = new JMenuItem(ObservationManager.bundle.getString("menu.createObserver"),
                new ImageIcon(this.imageResolver.getImageURL("observer_l.png").orElse(null), ""));
        createObserver.setMnemonic('v');
        createObserver.addActionListener(new CreateObserverListener());
        dataMenu.add(createObserver);

        JMenuItem createSite = new JMenuItem(ObservationManager.bundle.getString("menu.createSite"),
                new ImageIcon(this.imageResolver.getImageURL("site_l.png").orElse(null), ""));
        createSite.setMnemonic('l');
        createSite.addActionListener(new CreateSiteListener());
        dataMenu.add(createSite);

        JMenuItem createScope = new JMenuItem(ObservationManager.bundle.getString("menu.createScope"),
                new ImageIcon(this.imageResolver.getImageURL("scope_l.png").orElse(null), ""));
        createScope.setMnemonic('s');
        createScope.addActionListener(new CreateScopeListener());
        dataMenu.add(createScope);

        JMenuItem createEyepiece = new JMenuItem(ObservationManager.bundle.getString("menu.createEyepiece"),
                new ImageIcon(this.imageResolver.getImageURL("eyepiece_l.png").orElse(null), ""));
        createEyepiece.setMnemonic('e');
        createEyepiece.addActionListener(new CreateEyepieceListener());
        dataMenu.add(createEyepiece);

        JMenuItem createLens = new JMenuItem(ObservationManager.bundle.getString("menu.createLens"),
                new ImageIcon(this.imageResolver.getImageURL("lens_l.png").orElse(null), ""));
        createLens.setMnemonic('o');
        createLens.addActionListener(new CreateLensListener());
        dataMenu.add(createLens);

        JMenuItem createFilter = new JMenuItem(ObservationManager.bundle.getString("menu.createFilter"),
                new ImageIcon(this.imageResolver.getImageURL("filter_l.png").orElse(null), ""));
        createFilter.setMnemonic('f');
        createFilter.addActionListener(new CreateFilterListener());
        dataMenu.add(createFilter);

        JMenuItem createImager = new JMenuItem(ObservationManager.bundle.getString("menu.createImager"),
                new ImageIcon(this.imageResolver.getImageURL("imager_l.png").orElse(null), ""));
        createImager.setMnemonic('i');
        createImager.addActionListener(new CreateImagerListener());
        dataMenu.add(createImager);

        JMenuItem createTarget = new JMenuItem(ObservationManager.bundle.getString("menu.createTarget"),
                new ImageIcon(this.imageResolver.getImageURL("target_l.png").orElse(null), ""));
        createTarget.setMnemonic('t');
        createTarget.addActionListener(new CreateTargetListener());
        dataMenu.add(createTarget);

        JMenuItem createSession = new JMenuItem(ObservationManager.bundle.getString("menu.createSession"),
                new ImageIcon(this.imageResolver.getImageURL("session_l.png").orElse(null), ""));
        createSession.setMnemonic('n');
        createSession.addActionListener(new CreateSessionListener());
        dataMenu.add(createSession);

        // Seperate Availability from the rest
        dataMenu.addSeparator();

        JMenuItem equipmentAvailability = new JMenuItem(ObservationManager.bundle.getString("menu.equipmentAvailability"),
                new ImageIcon(this.imageResolver.getImageURL("equipment.png").orElse(null), ""));
        equipmentAvailability.setMnemonic('a');
        equipmentAvailability.addActionListener(new CreatEquipmentListener());
        dataMenu.add(equipmentAvailability);

        return dataMenu;
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

    class CreateObservationListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            ObservationManagerMenuData.this.createNewObservation();

        }

    }

    class CreateObserverListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            ObservationManagerMenuData.this.createNewObserver();
        }

    }
    class CreateSiteListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            ObservationManagerMenuData.this.createNewSite();

        }

    }
    class CreateScopeListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            ObservationManagerMenuData.this.createNewScope();

        }

    }
    class CreateEyepieceListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            ObservationManagerMenuData.this.createNewEyepiece();
        }

    }
    class CreateImagerListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            ObservationManagerMenuData.this.createNewImager();

        }

    }
    class CreateFilterListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            ObservationManagerMenuData.this.createNewFilter();
        }

    }
    class CreateLensListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            ObservationManagerMenuData.this.createNewLens();

        }

    }
    class CreateTargetListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            ObservationManagerMenuData.this.createNewTarget();

        }

    }
    class CreateSessionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            ObservationManagerMenuData.this.createNewSession();

        }

    }
    class CreatEquipmentListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            final UnavailableEquipmentDialog uqd = new UnavailableEquipmentDialog(ObservationManagerMenuData.this.observationManager, 
            ObservationManagerMenuData.this.imageResolver);
            ObservationManagerMenuData.this.observationManager.setChanged(uqd.changedElements());

        }

    }
}
