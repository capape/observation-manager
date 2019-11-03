/* ====================================================================
 * /dialog/TableElementsDialog.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import de.lehmannet.om.IEyepiece;
import de.lehmannet.om.IFilter;
import de.lehmannet.om.IFinding;
import de.lehmannet.om.IImager;
import de.lehmannet.om.IObservation;
import de.lehmannet.om.IObserver;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.IScope;
import de.lehmannet.om.ISession;
import de.lehmannet.om.ISite;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.navigation.tableModel.AbstractSchemaTableModel;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.ui.util.ConstraintsBuilder;

public class TableElementsDialog extends AbstractDialog {

	private static final long serialVersionUID = -666228606403887834L;

	public TableElementsDialog(ObservationManager om, List schemaElements) {
		
		super(om, new TableElementsPanel(schemaElements, om), true);
		
		super.setTitle(AbstractDialog.bundle.getString("dialog.tableElements.title"));
		super.positive.setText(AbstractDialog.bundle.getString("dialog.button.ok"));
		super.setModal(false);
		
		super.setSize(TableElementsDialog.serialVersionUID, 450, 280);
		super.setVisible(true);			
		
	}	
	
}

class TableElementsPanel extends AbstractPanel {
	
    private JTable table = new JTable();
    private AbstractSchemaTableModel model = null;
    private JScrollPane scrollTable = null;
    private ObservationManager om = null;
	
	public TableElementsPanel(List schemaElements, ObservationManager om) {
		
		super(true);			
		
		this.om = om;
						
		ISchemaElement[] se = (ISchemaElement[])schemaElements.toArray(new ISchemaElement[] {});
		this.model = new SimpleSchemaElementModel(se);
		this.table.setModel(this.model);
        ListSelectionModel lsm = this.table.getSelectionModel();        
        lsm.addListSelectionListener(new ListSelectionListener() {
                                                                    public void valueChanged(ListSelectionEvent e) {
                                                                        //Ignore extra messages.
                                                                        if( e.getValueIsAdjusting() ) 
                                                                            return;
                                                        
                                                                        ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                                                                        if (lsm.isSelectionEmpty()) {
                                                                            //no rows are selected                                                                            
                                                                        } else {
                                                                            int selectedRow = lsm.getMinSelectionIndex();
                                                                            ISchemaElement se = TableElementsPanel.this.model.getSchemaElement(selectedRow);
                                                                            if( se != null ) {  
                                                                            	TableElementsPanel.this.om.update(se);                                                                            	
                                                                            }
                                                                        }
                                                                    }
                                                                  }
                                      );		
        
		// Set column size
        this.setColumnSize();   
        
        this.scrollTable = new JScrollPane(this.table);	
		
		this.createPanel();        
        
	}
	
	public ISchemaElement createSchemaElement() {
		
		return null;
		
	}
	
	public ISchemaElement getSchemaElement() {
		
		return null;
		
	}
	
	public ISchemaElement updateSchemaElement() {
		
		return null;
		
	}
	
    private void setColumnSize() {    	       
        
        if( table.getColumnModel().getColumnCount() <= 1 ) {
        	return;   // No settings necessary
        }
        
        this.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        TableColumn col = table.getColumnModel().getColumn(0);
        col.setPreferredWidth(((AbstractSchemaTableModel)this.table.getModel()).getColumnSize(0));
        
        col = table.getColumnModel().getColumn(1);
        col.setPreferredWidth(((AbstractSchemaTableModel)this.table.getModel()).getColumnSize(1));
        
        this.table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);    	
    	
    }
    
    private void createPanel() {
        
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        this.setLayout(gridbag);        
		
        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 1, 1, 5, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;        
        JLabel Lelements = new JLabel(AbstractDialog.bundle.getString("dialog.tableElements.label.elements"));        
        gridbag.setConstraints(Lelements, constraints);
        this.add(Lelements);
        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 1, 5, 45, 10);
        constraints.fill = GridBagConstraints.BOTH;
        gridbag.setConstraints(this.scrollTable, constraints);
        this.scrollTable.setMinimumSize(new Dimension(200, 200));
        this.add(this.scrollTable);      
        
        ConstraintsBuilder.buildConstraints(constraints, 0, 6, 1, 1, 50, 89);
        constraints.fill = GridBagConstraints.BOTH;        
        JLabel Lfill = new JLabel("");        
        gridbag.setConstraints(Lfill, constraints);
        this.add(Lfill);          
        
    }       	
	
}

class SimpleSchemaElementModel extends AbstractSchemaTableModel {
	
	private static final String MODEL_ID = "SimpleSE";
	
	private final PropertyResourceBundle bundle = (PropertyResourceBundle)ResourceBundle.getBundle("ObservationManager", Locale.getDefault());
	
	public SimpleSchemaElementModel(ISchemaElement[] elements) {

		super.elements = elements;
		
	}	
	
	public int getColumnCount() {
		
		return 1;
		
	}

    public String getID() {
    	
        return SimpleSchemaElementModel.MODEL_ID;
        
    }	
	
	public int getRowCount() {

        if( super.elements == null ) {
            return 5;
        }
        return super.elements.length;
		
	}

	public Object getValueAt(int rowIndex, int columnIndex) {

        String value = "";
        
        if( super.elements == null ) {
            return value;
        }
        
        ISchemaElement element = (ISchemaElement)super.elements[rowIndex];
        
        if( element instanceof IEyepiece ) {
        	value = this.bundle.getString("eyepiece");
        } else if( element instanceof IObservation ) {
        	value = this.bundle.getString("observation");
        } else if( element instanceof IFilter ) {
        	value = this.bundle.getString("filter");
        } else if( element instanceof IFinding ) {
        	value = this.bundle.getString("finding");
        } else if( element instanceof IImager ) {
        	value = this.bundle.getString("imager");
        } else if( element instanceof IObserver ) {
        	value = this.bundle.getString("observer");
        } else if( element instanceof IScope ) {
        	value = this.bundle.getString("scope");
        } else if( element instanceof ISession ) {
        	value = this.bundle.getString("session");
        } else if( element instanceof ISite ) {
        	value = this.bundle.getString("site");
        } else if( element instanceof ITarget ) {
        	value = this.bundle.getString("target");
        }
                                
        switch (columnIndex) {
            case 0 : {
                        value = value + " " + element.getDisplayName();
                        break; 
                     }                    
        }      
        
		return value;
		
	}
	
    public String getColumnName(int column) {

        String name = "";

        switch (column) {
            case 0 : {
                        name = this.bundle.getString("table.header.tableElements.element");
                        break; 
                     }                  
        }        
        
        return name;
     
    }	
	
}
