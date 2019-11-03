/* ====================================================================
 * /extension/variableStars/ExtensionLoader.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.extension.variableStars;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.NoSuchElementException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import de.lehmannet.om.IObservation;
import de.lehmannet.om.IObserver;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.extension.variableStars.FindingVariableStar;
import de.lehmannet.om.extension.variableStars.TargetVariableStar;
import de.lehmannet.om.extension.variableStars.export.AAVSOVisualSerializer;
import de.lehmannet.om.ui.catalog.ICatalog;
import de.lehmannet.om.ui.comparator.ObservationComparator;
import de.lehmannet.om.ui.dialog.SchemaElementSelectorPopup;
import de.lehmannet.om.ui.extension.AbstractExtension;
import de.lehmannet.om.ui.extension.PopupMenuExtension;
import de.lehmannet.om.ui.extension.variableStars.catalog.GCVS4Catalog;
import de.lehmannet.om.ui.extension.variableStars.dialog.VariableStarChartDialog;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.navigation.tableModel.ExtendedSchemaTableModel;
import de.lehmannet.om.ui.preferences.PreferencesPanel;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.DatePicker;
import de.lehmannet.om.util.SchemaElementConstants;

public class VariableStarsExtension extends AbstractExtension implements ActionListener {
	
	private static final String NAME = "Variable Stars";
	private static final float VERSION = 0.92f;
	private static URL UPDATE_URL = null;
	static {
		try {
			VariableStarsExtension.UPDATE_URL = new URL("http://observation.sourceforge.net/extension/variableStars/update");
		} catch( MalformedURLException m_url ) {
			// Do nothing
		}
	}		
	
	private PropertyResourceBundle typeBundle = (PropertyResourceBundle)ResourceBundle.getBundle("de.lehmannet.om.ui.extension.variableStars.oalVariableStarTargetDisplayNames", Locale.getDefault());		
	private PropertyResourceBundle uiBundle = (PropertyResourceBundle)ResourceBundle.getBundle("de.lehmannet.om.ui.extension.variableStars.VariableStar", Locale.getDefault());
	
	private ObservationManager om = null;
	
	private JMenu menu = null;
	private JMenuItem exportAAVSO = null;
	private JMenuItem showChart = null;
	
	public VariableStarsExtension(ObservationManager om) {
		
		this.om = om;
		
		super.OAL_EXTENSION_FILE = "./openastronomylog21/extensions/ext_VariableStars.xsd";
		
		this.initFindingPanels();
		this.initTargetPanels();
		this.initTargetDialogs();		
		
	}
	
	public void actionPerformed(ActionEvent e) {
		
        if( e.getSource() instanceof JMenuItem ) {  // Should always be the case
            JMenuItem source = (JMenuItem)e.getSource();
            if( source.equals(this.exportAAVSO) ) {
            	
            	// Get preselected observations
            	IObservation[] allObservations = this.om.getXmlCache().getObservations();
            	if( allObservations.length == 0 ) {
            		this.om.createInfo(this.uiBundle.getString("info.noObservationsFound"));
            		return;
            	}            	
            	ArrayList preselectedObservations = new ArrayList();
            	for(int i=0; i < allObservations.length; i++) {
            		// Only the variable star observations are of interest
            		if( TargetVariableStar.XML_XSI_TYPE_VALUE.equals(allObservations[i].getTarget().getXSIType()) ) {
            			// @todo: This works only with one result!
            			if( !((FindingVariableStar)allObservations[i].getResults().get(0)).isAlreadyExportedToAAVSO() ) {
            				preselectedObservations.add(allObservations[i]);
            			}
            		}
            	}
            	
            	// Create popup for variable star observations
    			SchemaElementSelectorPopup popup = new SchemaElementSelectorPopup(this.om, this.uiBundle.getString("popup.exportAAVSO.selectObservations"), TargetVariableStar.XML_XSI_TYPE_VALUE, preselectedObservations, true, SchemaElementConstants.OBSERVATION);
    			List variableStarObservations = popup.getAllSelectedElements();
    			if(   (variableStarObservations == null)
    			   || (variableStarObservations.isEmpty())	
    			   ) {
    				return;
    			}
            	
            	AAVSOVisualSerializer aavsoExport = new AAVSOVisualSerializer("Observation Manager - " + ObservationManager.VERSION, variableStarObservations);
            	
                // Create export file path
            	String[] files = this.om.getXmlCache().getAllOpenedFiles();
            	if(   (files == null)
            	   || (files.length == 0)
            	   ) {   // There is data (otherwise we wouldn't have come here), but data's not saved
            		this.om.createInfo(this.uiBundle.getString("error.noXMLFileOpen"));
            		return;
            	}    	

            	Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
                om.setCursor(hourglassCursor);
            	
            	// @todo This works only with ONE file opened
            	File xmlFile = new File(files[0]);
            	String exportFileName = xmlFile.getName();
            	exportFileName = exportFileName.substring(0, exportFileName.indexOf('.'));
            	exportFileName = xmlFile.getParent() + File.separatorChar + exportFileName + "_aavso.txt";
            	File aavsoFile = new File(exportFileName);               	
            	int i=2;
            	while( aavsoFile.exists() ) {  // Check if file exists...
            		exportFileName = exportFileName.substring(0, exportFileName.lastIndexOf("_aavso"));
           			exportFileName = exportFileName + "_aavso(" + i + ").txt";	
            		i++;
            		aavsoFile = new File(exportFileName);
            	}                
            	
            	// Do the actual export
            	int exportCounter = 0;
            	try {
            		exportCounter = aavsoExport.serialize(new BufferedOutputStream(new FileOutputStream(aavsoFile)));
            	} catch( FileNotFoundException fnfe ) {
            		this.om.createInfo(this.uiBundle.getString("error.aavsoExportFileNotFound"));
            		System.err.println(fnfe);
            		
                    Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
                    om.setCursor(defaultCursor);   
                    return;
            	} catch( Exception ex ) {
            		this.om.createInfo(this.uiBundle.getString("error.aavsoExportNotOK"));
            		System.err.println(ex);
            		
                    Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
                    om.setCursor(defaultCursor);
                    return;
            	}
            	
            	// Set the om status to changed, as findings have been exported (which changes their status)
            	this.om.setChanged(true);
            	
                Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
                om.setCursor(defaultCursor);            	
            	
            	if( exportCounter != variableStarObservations.size() ) {  // Not all observations were exported
            		this.om.createInfo(exportCounter + " " + this.uiBundle.getString("info.aavsoExport") + "\n" +  aavsoFile + "\n" + this.uiBundle.getString("info.aavsoExportCheckLog"));
            	} else {  // All observations exported
            		this.om.createInfo(exportCounter + " " + this.uiBundle.getString("info.aavsoExport") + "\n" + aavsoFile);	
            	}    
            	
            	if( exportCounter == 0 ) {	// Nothing exported, so delete file (file only contains aavso header)
            		aavsoFile.delete();
            	}
            	
            } else if( source.equals(this.showChart) ) {
           	
            	// Create popup with variableStars
            	VariableStarSelectorPopup popup = null;
            	IObservation[] observations = null;
            	boolean quitLoop = false;
            	do {
            		try {
            			popup = new VariableStarSelectorPopup(this.om);
            		} catch(IllegalArgumentException iae) {	// No variable star observation found            			
            			return;
            		}
	            	if( popup.getAllSelectedObservations() != null) {
	            	   if( popup.getAllSelectedObservations().length > 0 ) {	
	            		   observations = popup.getAllSelectedObservations();
	            		   
	            		   if(   (observations != null)  // No observations for star
		            		  && (observations.length <= 0)
		            		  ) {
	            			   		this.om.createWarning(this.uiBundle.getString("popup.selectVariableStar.warning.noObservations"));
	            			   		continue;
		            	   } else {
		            		   quitLoop = true;
		            	   }
	            	   } else { // No Star selected
	            			this.om.createWarning(this.uiBundle.getString("popup.selectVariableStar.warning.noStarSelected"));
	            	   }
	            	} else {
	            		return;		// User pressed cancel
	            	}
            	} while( !quitLoop );   // Exit loop by pressing cancel            	
            	
            	// Show color selection
            	ColorSelectionDialog colorDialog = new ColorSelectionDialog(this.om, observations);
            	Map colorMap = colorDialog.getColorMap();
            	
            	// Show chart
            	if( colorMap != null ) {
            		new VariableStarChartDialog(this.om, observations, colorMap);
            	}
            }
        }
		
	}
	
	public String getName() {
		
		return VariableStarsExtension.NAME;
		
	}
	
	public URL getUpdateInformationURL() {
		
		return VariableStarsExtension.UPDATE_URL;
		
	}	
	
	public float getVersion() {
		
		return VariableStarsExtension.VERSION;
		
	}		
	
	public void reloadLanguage() {
		
		this.typeBundle = (PropertyResourceBundle)ResourceBundle.getBundle("de.lehmannet.om.ui.extension.variableStars.oalVariableStarTargetDisplayNames", Locale.getDefault());		
		this.uiBundle = (PropertyResourceBundle)ResourceBundle.getBundle("de.lehmannet.om.ui.extension.variableStars.VariableStar", Locale.getDefault());
		
	}		
	
	public JMenu getMenu() {
		
		this.menu = new JMenu(this.uiBundle.getString("menu.main"));
		
        this.exportAAVSO = new JMenuItem(this.uiBundle.getString("menu.aavsoExport"));
        exportAAVSO.setMnemonic('e');
        exportAAVSO.addActionListener(this);        
        this.menu.add(exportAAVSO);

        this.showChart = new JMenuItem(this.uiBundle.getString("menu.showChart"));
        showChart.setMnemonic('c');
        showChart.addActionListener(this);        
        this.menu.add(showChart);        
        
        return menu;
        
	}
	
	public PreferencesPanel getPreferencesPanel() {
		
		return new VariableStarsPreferences(this.om.getConfiguration());
		
	}
	
	public ICatalog[] getCatalogs(File catalogDir) {
		
		ICatalog gcvs = new GCVS4Catalog(catalogDir.getAbsoluteFile(), this.om);
		
		return new ICatalog[] { gcvs };
		
	}
	
	public Set getSupportedXSITypes(int schemaElementConstant) {
		
		Set result = null;
		if( SchemaElementConstants.TARGET == schemaElementConstant ) {			
			result = this.getSupportedTargetXSITypes();
		} else if( SchemaElementConstants.FINDING == schemaElementConstant ) {
			result = this.getSupportedFindingXSITypes();
		}
		
		return result;
		
	}	
	
	private Set getSupportedTargetXSITypes() {
		
		HashSet result = new HashSet();
		result.add(TargetVariableStar.XML_XSI_TYPE_VALUE);
		
		return result;
		
	}
	
	private Set getSupportedFindingXSITypes() {
		
		HashSet result = new HashSet();
		result.add(FindingVariableStar.XML_XSI_TYPE_VALUE);
		
		return result;
		
	}
	
	public String getDisplayNameForXSIType(String xsiType) {
		
		try {
			return this.typeBundle.getString(xsiType);	
		} catch( MissingResourceException mre ) {	// XSIType not found
			return null;
		}
		
	}
	
	public boolean isCreationAllowed(String xsiType) {
		
		// All elements are allowed for creation of new instances 
		return true;		
		
	}

	private void initFindingPanels() {

		HashMap findingPanels = new HashMap();
		
		findingPanels.put(FindingVariableStar.XML_XSI_TYPE_VALUE, "de.lehmannet.om.ui.extension.variableStars.panel.VariableStarFindingPanel");
		findingPanels.put(TargetVariableStar.XML_XSI_TYPE_VALUE, "de.lehmannet.om.ui.extension.variableStars.panel.VariableStarFindingPanel");
		
		super.panels.put(new Integer(SchemaElementConstants.FINDING), findingPanels);
		
	}
	
	private void initTargetPanels() {
		
		HashMap targetPanels = new HashMap();
		
		targetPanels.put(TargetVariableStar.XML_XSI_TYPE_VALUE, "de.lehmannet.om.ui.extension.variableStars.panel.VariableStarTargetPanel");
		
		super.panels.put(new Integer(SchemaElementConstants.TARGET), targetPanels);
		
	}
	
	private void initTargetDialogs() {
				
		HashMap targetDialogs = new HashMap();
		
		targetDialogs.put(TargetVariableStar.XML_XSI_TYPE_VALUE, "de.lehmannet.om.ui.extension.variableStars.dialog.VariableStarTargetDialog");		

		super.dialogs.put(new Integer(SchemaElementConstants.TARGET), targetDialogs);
		
	}	
	
	public PopupMenuExtension getPopupMenu() { 
		
		return null;
		
	}
	
}

class ColorSelectionDialog extends JDialog implements ActionListener {
	
	private final PropertyResourceBundle bundle = (PropertyResourceBundle)ResourceBundle.getBundle("de.lehmannet.om.ui.extension.variableStars.VariableStar", Locale.getDefault());
	
	private IObservation[] observations = null;
	
	private ObservationManager om = null;
	private JTable table = null;
	
	private JButton cancel = null;
	private JButton ok = null;
	
	private Map result = null;
	
	public ColorSelectionDialog(ObservationManager om, IObservation[] observations) {
	
		super(om);
		
		this.observations = observations;
		
		this.om = om;
		super.setTitle(this.bundle.getString("popup.observerColor.title"));
		super.setModal(true);
						
		super.setSize(550, 200);
		super.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		super.setLocationRelativeTo(om);		
		
		this.initDialog();
		
		super.setVisible(true);
		
	}
	
	public void actionPerformed(ActionEvent e) {

		if( this.cancel.equals(e.getSource()) ) {   // Cancel pressed
			this.result = null;			
		} else {									// OK pressed
			this.result = this.createMap();
		}
		
		super.dispose();
		
	}

	public void initDialog() {
		
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();        
        super.getContentPane().setLayout(gridbag);
                
	    ConstraintsBuilder.buildConstraints(constraints, 0, 0, 2, 1, 100, 100);
	    constraints.fill = GridBagConstraints.BOTH;
	    Color defaultColor = null;
	    if( this.om.isNightVisionEnabled() ) {
	    	defaultColor = Color.DARK_GRAY; 
	    } else {
	    	defaultColor = Color.RED;
	    }	    
	    this.table = new JTable(new ObserverColorTableModel(this.getObservers(), defaultColor));
	    this.table.setToolTipText(this.bundle.getString("popup.observerColor.tooltip.table"));
	    this.table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
	    this.table.setDefaultEditor(Color.class, new ColorEditor());
        this.table.setDefaultRenderer(Color.class,
                					  new TableCellRenderer() {
												public Component getTableCellRendererComponent(JTable table,
																			                   Object value,
																			                   boolean isSelected,
																			                   boolean hasFocus,
																			                   int row,
																			                   int column) 	{
													
													DefaultTableCellRenderer cr = new DefaultTableCellRenderer();	        															
													if( value != null ) {					        		
										        		cr.setBackground((Color)value); 																
													} else {
														cr.setText(ColorSelectionDialog.this.bundle.getString("popup.observerColor.noColorSelection"));
													}      															
													
													return cr;
									        	}
				                           }
                					   );
	    JScrollPane scrollPane = new JScrollPane(this.table);
	    gridbag.setConstraints(scrollPane, constraints);                
	    this.getContentPane().add(scrollPane);  	 	      
	    
	    ConstraintsBuilder.buildConstraints(constraints, 0, 1, 1, 1, 50, 5);
	    constraints.fill = GridBagConstraints.HORIZONTAL;	    
	    this.ok = new JButton(this.bundle.getString("popup.observerColor.button.ok"));
	    this.ok.addActionListener(this);
	    gridbag.setConstraints(this.ok, constraints);
	    this.getContentPane().add(ok);

	    ConstraintsBuilder.buildConstraints(constraints, 1, 1, 1, 1, 50, 5);
	    constraints.fill = GridBagConstraints.HORIZONTAL;
	    this.cancel = new JButton(this.bundle.getString("popup.observerColor.button.cancel"));
	    this.cancel.addActionListener(this);
	    gridbag.setConstraints(this.cancel, constraints);
	    this.getContentPane().add(cancel);	    
	    
	}	
	
	public Map getColorMap() {
		
		return this.result;
		
	}
	
	private Map createMap() {
			
		ObserverColorTableModel model = (ObserverColorTableModel)this.table.getModel();
		Map map = model.getResult();
		
		return map;
		
	}
	
	private IObserver[] getObservers() {
		
		// Make sure we only show the observers, which contributed a observation
		ArrayList list = new ArrayList();
		for(int i=0; i < this.observations.length; i++) {
			if( !list.contains(this.observations[i].getObserver()) ) {
				// Make sure the default observer is the top entry 
				if( this.observations[i].getObserver().getDisplayName().equals(this.om.getConfiguration().getConfig(ObservationManager.CONFIG_DEFAULT_OBSERVER)) ) {
					list.add(0, this.observations[i].getObserver());   
				} else {
					list.add(this.observations[i].getObserver());	
				}				
			}
		}
		
		return (IObserver[])list.toArray(new IObserver[] {});
		
	}
	
}


class ObserverColorTableModel extends AbstractTableModel {
	
	private final PropertyResourceBundle bundle = (PropertyResourceBundle)ResourceBundle.getBundle("de.lehmannet.om.ui.extension.variableStars.VariableStar", Locale.getDefault());
	
	private IObserver[] observers = null;
	private Color[] colors = null;
	
	public ObserverColorTableModel(IObserver[] observers, Color defaultColor) {
		
		this.observers = observers;
		this.colors = new Color[observers.length];
		
		// The first observer (default observer) gets automatically a Color assigned
		this.colors[0] = defaultColor;
		
	}
	
	public int getColumnCount() {

		return 2;
		
	}

	public int getRowCount() {
			
		return this.observers.length;
		
	}
	
	public Object getValueAt(int rowIndex, int columnIndex) {			
		
		switch( columnIndex ) {
			case 0: {
						String value = this.observers[rowIndex].getDisplayName();
						return value;
					}
			case 1: {
						return this.colors[rowIndex];
					}
		}
		
		return "";
		
	}
	
    public void setValueAt(Object value, int row, int col) {    

    	// Make sure both lists are always the same size
    	if( col == 0 ) {
    		this.observers[row] = (IObserver)value;
    	} else {	
    		this.colors[row] = (Color)value;
    		if( Color.white.equals(this.colors[row]) ) {		// White color is treated as no color selected
    			this.colors[row] = null;
    		}
    	}    	
    	
    	fireTableCellUpdated(row, col);
        
    }	
    
    public Class getColumnClass(int columnIndex) {
    	
        Class c = null;

        switch (columnIndex) {
	        case 0 : {
	            		c = String.class;
	            		break; 
	         		 }        
            case 1 : {            	
                        c = Color.class;
                        break; 
                     }
        }        
        
        return c;    	
    	
    }
    
    public String getColumnName(int column) {

        String name = "";

        switch (column) {
            case 0 : {
                        name = this.bundle.getString("popup.observerColor.column0");
                        break; 
                     }
            case 1 : {
                        name = this.bundle.getString("popup.observerColor.column1");
                        break; 
                     }
        }        
        
        return name;
     
    } 
	
	public boolean isCellEditable(int rowIndex, int columnIndex) {

		if( columnIndex == 1 ) {
			return true;
		}
		
		return false;
		
	}
	
	public Map getResult() {
		
		Map map = new HashMap();
		
		for(int i=0; i < this.observers.length; i++) {
			if( this.colors[i] != null ) {
				map.put(this.observers[i], this.colors[i]);	
			}								
		}
		
		// All observers were unselected
		if( map.size() == 0 ) {
			map = null;
		}
		
		return map;
		
	}
	
}


class ColorEditor extends AbstractCellEditor implements TableCellEditor,
                                                        ActionListener {
	
	private final PropertyResourceBundle bundle = (PropertyResourceBundle)ResourceBundle.getBundle("de.lehmannet.om.ui.extension.variableStars.VariableStar", Locale.getDefault());
	
	private Color currentColor;
	private JButton button;
	private JColorChooser colorChooser;
	private JDialog dialog;

	private String EDIT = "edit";

	public ColorEditor() {
		
		this.button = new JButton();
		this.button.setActionCommand(EDIT);
		this.button.addActionListener(this);
		this.button.setBorderPainted(false);

		//Set up the dialog that the button brings up.
		this.colorChooser = new JColorChooser();
		this.dialog = JColorChooser.createDialog(button, this.bundle.getString("popup.observerColor.colorEditor.title"), true, //modal
			                                 	 colorChooser, this, // OK button handler
				                                 null); // no CANCEL button handler		

	}

	public void actionPerformed(ActionEvent e) {
		
		if (EDIT.equals(e.getActionCommand())) {
			//The user has clicked the cell, so bring up the dialog.
			this.button.setBackground(currentColor);
			this.colorChooser.setColor(currentColor);
			this.dialog.setVisible(true);

			fireEditingStopped(); //Make the renderer reappear.

		} else { //User pressed dialog's "OK" button.
			currentColor = colorChooser.getColor();
		}
		
	}
	

	//Implement the one CellEditor method that AbstractCellEditor doesn't.
	public Object getCellEditorValue() {
		
		return currentColor;
		
	}

	//Implement the one method defined by TableCellEditor.
	public Component getTableCellEditorComponent(JTable table,
			                                     Object value, 
			                                     boolean isSelected, 
			                                     int row, 
			                                     int column) {
		
		if( value != null ) {
			currentColor = (Color)value;
			this.button.setBackground(currentColor);
			this.button.setText("");
		} else {
			currentColor = null;
			this.button.setText(this.bundle.getString("popup.observerColor.noColorSelection"));
			this.button.setBackground(Color.LIGHT_GRAY);
		}

		return this.button;
		
	}
	
}

class VariableStarSelectorPopup extends JDialog implements ActionListener, TableModelListener {

	private JButton ok = null;
	private JButton cancel = null;
	
    private JTextField beginField = null;
	private Calendar beginDate = null;
	private JButton beginPicker = null; 	
    private JTextField endField = null;
	private Calendar endDate = null;
	private JButton endPicker = null;
	
	private ObservationManager om = null;
	
	private PropertyResourceBundle uiBundle = (PropertyResourceBundle)ResourceBundle.getBundle("de.lehmannet.om.ui.extension.variableStars.VariableStar", Locale.getDefault());
	
	private ExtendedSchemaTableModel tableModel = null;
	
	public VariableStarSelectorPopup(ObservationManager om) throws IllegalArgumentException, NoSuchElementException {   // See SchemaElementConstants
		
		super(om, true);					
		
		this.om = om;
		
		super.setTitle(this.uiBundle.getString("popup.selectVariableStar.title"));
		super.setSize(500, 250);
		super.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		super.setLocationRelativeTo(null);				
		
		ITarget[] elements = om.getXmlCache().getTargets();	
		
		this.tableModel = new ExtendedSchemaTableModel(elements, SchemaElementConstants.TARGET, TargetVariableStar.XML_XSI_TYPE_VALUE, false, null);
		
		// Check if there're variable star observations at all. If not, show popup and return...
		Object o = this.tableModel.getValueAt(0, 0);
		if( o == null
		   || ( o instanceof String
			   && "".equals((String)o)
			   )	
		   ) {
			om.createInfo(this.uiBundle.getString("popup.selectVariableStar.info.noVariableStarObservations"));
			throw new IllegalArgumentException("No Variable Star Observation found.");			
		}
		
		this.initDialog();
		
		super.setVisible(true);	
	
	}
	
    public void tableChanged(TableModelEvent e) {
    	
        ExtendedSchemaTableModel model = (ExtendedSchemaTableModel)e.getSource();
        int row = model.getSelectedRow();
                
		// Make sure to reset fields, as otherwise we won't find all observation ins getAllSelectedObservations
		this.beginDate = null;
		this.endDate = null;
		
		// Also clear UI
		this.beginField.setText("");
		this.endField.setText("");        
        
        Object o = (Object)model.getValueAt(row, 0);              
        if( o instanceof Boolean ) {
        	if( ((Boolean)o).booleanValue() ) {   // If checkbox marked
        		
        		IObservation[] observations = this.getAllSelectedObservations();
        		
        		if(   (observations == null)
        		   || (observations.length == 0)
        		   ) {	
        			return;
        		}
        		
        		
        		// Get observations in a sorted way (newest observation at the beginning)
        		ObservationComparator comparator = new ObservationComparator(true); 
        		TreeSet set = new TreeSet(comparator);
        		set.addAll(Arrays.asList(observations));
        		
        		this.beginDate = ((IObservation)set.first()).getBegin();
        		this.endDate = ((IObservation)set.last()).getBegin();
        		
        		this.beginField.setText(this.formatDate(this.beginDate));
        		this.endField.setText(this.formatDate(this.endDate));        		
        	}
        }
        
    }

	
	public void actionPerformed(ActionEvent e) {

		Object source = e.getSource();
		if( source instanceof JButton ) {
			JButton sourceButton = (JButton)source;
			if( sourceButton.equals(this.ok) ) {
				super.dispose();
			} else if( sourceButton.equals(this.cancel) ) {				
				super.dispose();
				this.tableModel = null;   // Set TableModel = null to indicate canceled UI 
			} else if( sourceButton.equals(this.beginPicker) ) {
				DatePicker dp = null;
				if( this.beginDate != null ) {
					dp = new DatePicker(this.om, this.uiBundle.getString("popup.selectVariableStar.start.datePicker.title"), this.beginDate);	
				} else {
					dp = new DatePicker(this.om, this.uiBundle.getString("popup.selectVariableStar.start.datePicker.title"));
				}			
				
				// Make sure selected date is in observation period
        		IObservation[] observations = this.getAllSelectedObservations();        		
        		if(   (observations != null)
        		   && (observations.length > 0)
        		   ) {
            		// Get observations in a sorted way
            		ObservationComparator comparator = new ObservationComparator(true);
            		TreeSet set = new TreeSet(comparator);
            		set.addAll(Arrays.asList(observations));
            		
            		Calendar first = ((IObservation)set.first()).getBegin();
            		Calendar last = ((IObservation)set.last()).getBegin();
            		
            		if(   (dp.getDate().before(first))
            		   || (dp.getDate().after(last))
            		   ) {
            			this.om.createWarning(this.uiBundle.getString("popup.selectVariableStar.begin.datePicker.outOfScope"));
            			return;
            		}
        		}      		
        		
        		// Set selected date
				this.beginDate = dp.getDate();				
				this.beginField.setText(dp.getDateString());					
			} else if( sourceButton.equals(this.endPicker) ) {
				DatePicker dp = null;
				if( this.endDate != null ) {
					dp = new DatePicker(this.om, this.uiBundle.getString("popup.selectVariableStar.end.datePicker.title"), this.endDate);
				} else if(this.beginDate != null) {  // Try to initialize endDate Picker with startdate					
					dp = new DatePicker(this.om, this.uiBundle.getString("popup.selectVariableStar.end.datePicker.title"), this.beginDate);
				} else {
					dp = new DatePicker(this.om, this.uiBundle.getString("popup.selectVariableStar.end.datePicker.title"));
				}
				
				// Make sure selected date is in observation period
        		IObservation[] observations = this.getAllSelectedObservations();        		
        		if(   (observations != null)
        		   && (observations.length > 0)
        		   ) {
            		// Get observations in a sorted way
            		ObservationComparator comparator = new ObservationComparator();
            		TreeSet set = new TreeSet(comparator);
            		set.addAll(Arrays.asList(observations));
            		
            		Calendar first = ((IObservation)set.first()).getBegin();
            		Calendar last = ((IObservation)set.last()).getBegin();
            		
            		if(   (dp.getDate().before(first))
            		   || (dp.getDate().after(last))
            		   ) {
            			this.om.createWarning(this.uiBundle.getString("popup.selectVariableStar.end.datePicker.outOfScope"));
            			return;
            		}
        		}      		
        		
        		// Set selected date				
				this.endDate = dp.getDate();
				this.endField.setText(dp.getDateString());				
			}
		} 
		
	}
	
	public IObservation[] getAllSelectedObservations() {
		
		if( this.tableModel == null ) {
			return null;
		}
		
		List selectedStars = this.tableModel.getAllSelectedElements();
		if(   (selectedStars == null)
		   || (selectedStars.isEmpty())
		   ) {
			return new IObservation[] {};
		}
		ITarget selectedStar = (ITarget)selectedStars.get(0);		
		IObservation[] observations = this.om.getXmlCache().getObservations(selectedStar);
		
		// Filter by start/end date
		ArrayList result = new ArrayList();
		for(int i=0; i < observations.length; i++) {
			if(   (observations[i].getBegin().before(this.beginDate))
			   || (observations[i].getBegin().after(this.endDate))
			   ) {
				continue;   // Observation not in selected time period
			} else {
				result.add(observations[i]);
			}
		}
		
		return (IObservation[])result.toArray(new IObservation[] {});
		
	}

	private void initDialog() {
		
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        super.getContentPane().setLayout(gridbag);
        
        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 6, 1, 90, 90);
        constraints.fill = GridBagConstraints.BOTH;      
        JTable table = new JTable(this.tableModel);
        table.setEnabled(true);
        table.setEditingColumn(1);
        table.setRowSelectionAllowed(true);        
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setDoubleBuffered(true);     
        table.getModel().addTableModelListener(this);
        table.setToolTipText(this.uiBundle.getString("popup.selectVariableStar.table.tooltip"));       
        JScrollPane scrollPane = new JScrollPane(table);
        gridbag.setConstraints(scrollPane, constraints);
        super.getContentPane().add(scrollPane);
        
        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 1, 1, 2, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        JLabel beginLabel = new JLabel(this.uiBundle.getString("popup.selectVariableStar.label.beginDate"));
        beginLabel.setToolTipText(this.uiBundle.getString("popup.selectVariableStar.tooltip.beginDate"));
        gridbag.setConstraints(beginLabel, constraints);
        super.getContentPane().add(beginLabel); 
        this.beginField = new JTextField();
        this.beginField.setEditable(false);
        this.beginField.setToolTipText(this.uiBundle.getString("popup.selectVariableStar.label.beginDate"));
        ConstraintsBuilder.buildConstraints(constraints, 1, 1, 1, 1, 10, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(beginField, constraints);
        super.getContentPane().add(beginField); 
        this.beginPicker = new JButton("...");
        this.beginPicker.addActionListener(this);
        this.beginPicker.setToolTipText(this.uiBundle.getString("popup.selectVariableStar.button.beginDate"));
        ConstraintsBuilder.buildConstraints(constraints, 2, 1, 1, 1, 2, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(this.beginPicker, constraints);
        super.getContentPane().add(this.beginPicker);         
        
        ConstraintsBuilder.buildConstraints(constraints, 3, 1, 1, 1, 2, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        JLabel endLabel = new JLabel(this.uiBundle.getString("popup.selectVariableStar.label.endDate"));
        endLabel.setToolTipText(this.uiBundle.getString("popup.selectVariableStar.tooltip.endDate"));
        gridbag.setConstraints(endLabel, constraints);
        super.getContentPane().add(endLabel); 
        this.endField = new JTextField();
        this.endField.setEditable(false);
        this.endField.setToolTipText(this.uiBundle.getString("popup.selectVariableStar.label.endDate"));
        ConstraintsBuilder.buildConstraints(constraints, 4, 1, 1, 1, 10, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(endField, constraints);
        super.getContentPane().add(endField); 
        this.endPicker = new JButton("...");
        this.endPicker.addActionListener(this);
        this.endPicker.setToolTipText(this.uiBundle.getString("popup.selectVariableStar.button.endDate"));
        ConstraintsBuilder.buildConstraints(constraints, 5, 1, 1, 1, 2, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(this.endPicker, constraints);
        super.getContentPane().add(this.endPicker);         

        ConstraintsBuilder.buildConstraints(constraints, 0, 2, 3, 1, 5, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;        
        this.ok = new JButton(this.uiBundle.getString("dialog.button.ok"));        
        this.ok.addActionListener(this);
        gridbag.setConstraints(this.ok, constraints);
        super.getContentPane().add(this.ok);        
		
        ConstraintsBuilder.buildConstraints(constraints, 3, 2, 3, 1, 5, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;        
        this.cancel = new JButton(this.uiBundle.getString("dialog.button.cancel"));
        this.cancel.addActionListener(this);
        gridbag.setConstraints(this.cancel, constraints);
        super.getContentPane().add(this.cancel);         
        
	}
	
	private String formatDate(Calendar cal) {
		
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());	
        
        format.setCalendar(cal);
        return format.format(cal.getTime());
		
	}
	
}