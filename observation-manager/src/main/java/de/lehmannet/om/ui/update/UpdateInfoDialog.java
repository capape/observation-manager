/* ====================================================================
 * /dialog/UpdateInfoDialog
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.update;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import de.lehmannet.om.ui.dialog.OMDialog;
import de.lehmannet.om.ui.dialog.ProgressDialog;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.Worker;

public class UpdateInfoDialog extends OMDialog implements ActionListener {

	private static final long serialVersionUID = -6681965343558223755L;

	final PropertyResourceBundle bundle = (PropertyResourceBundle)ResourceBundle.getBundle("ObservationManager", Locale.getDefault());	
	
	private JButton close = new JButton(this.bundle.getString("dialog.button.cancel"));
	private JButton download = new JButton(this.bundle.getString("updateInfo.button.download"));
	private JTable infoTable = null;
	
	private ObservationManager om = null;
	private List updateEntries = null;
	
	public UpdateInfoDialog(ObservationManager om, List updateEntryList) {

		super(om);
		
		this.om = om;
		this.updateEntries = updateEntryList;
		
		super.setTitle(this.bundle.getString("updateInfo.title"));
		super.setSize(UpdateInfoDialog.serialVersionUID, 390, 180);
		super.setModal(true);
		super.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		super.setLocationRelativeTo(om);				
					
		this.initDialog();	
		
		this.setVisible(true);
	//	this.pack();

	}		
	
	// --------------
	// ActionListener ---------------------------------------------------------
	// --------------	
	
	public void actionPerformed(ActionEvent e) {

		Object source = e.getSource();
		if( source instanceof JButton ) {
			if( source.equals(this.close) ) {								
				this.dispose();
			} else if( source.equals(this.download) ) {							
				List downloadList = ((UpdateTableModel)this.infoTable.getModel()).getSelected();				
				if(    (downloadList != null)
				   && !(downloadList.isEmpty())
				   ) {
					
					// ---------- Where to save the files?
			        JFileChooser chooser = new JFileChooser();
			        chooser.setMultiSelectionEnabled(false);
			        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			        int returnVal = chooser.showOpenDialog(this);
			        File directory = null;
			        if( returnVal == JFileChooser.APPROVE_OPTION ) {
			        	directory = chooser.getSelectedFile();
			           
			           // Download the selected files
			           boolean result = this.downloadFiles(downloadList, directory);
			           
			           if( result ) {
			        	   this.om.createInfo(this.bundle.getString("updateInfo.download.success"));
			        	   this.dispose();
			           } else {
			        	   this.om.createWarning(this.bundle.getString("updateInfo.download.error"));
			           }
			           
			        } else {
			        	return;
			        }			        
				}							
			}
		}
		
	}	
	
	private void initDialog() {
		
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();        
        super.getContentPane().setLayout(gridbag);
        
        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 2, 5, 20, 98);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.BOTH;
        this.infoTable = new JTable(new UpdateTableModel(this.updateEntries, this.download));
        this.infoTable.setRowSelectionAllowed(false);
        this.infoTable.setDefaultRenderer(String.class,
        								  new TableCellRenderer() {
												public Component getTableCellRendererComponent(JTable table,
																			                   Object value,
																			                   boolean isSelected,
																			                   boolean hasFocus,
																			                   int row,
																			                   int column) 	{
													
													DefaultTableCellRenderer cr = new DefaultTableCellRenderer();
													
													if( (column == 2) || (column == 3) ) {
														cr.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
													}
													
													cr.setText(value.toString());
																																												
													return cr;
									        	}
									       }       		
        								   ); 
        			
        
        
        
     /*   TableColumn col0 = this.infoTable.getColumnModel().getColumn(0);
        TableColumn col1 = this.infoTable.getColumnModel().getColumn(1);
        col0.setPreferredWidth(preferredWidth)((int)(col0.getWidth() + col1.getWidth() / 1.5));*/
        
        JScrollPane scrollPane = new JScrollPane(this.infoTable);
        gridbag.setConstraints(scrollPane, constraints);
        super.getContentPane().add(scrollPane);	
		          
        ConstraintsBuilder.buildConstraints(constraints, 0, 5, 1, 1, 20, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
		this.download.addActionListener(this);
		gridbag.setConstraints(this.download, constraints);
		super.getContentPane().add(this.download);		
		
        ConstraintsBuilder.buildConstraints(constraints, 1, 5, 1, 1, 50, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
		this.close.addActionListener(this);
		gridbag.setConstraints(this.close, constraints);
		super.getContentPane().add(this.close);					
		
	}
	
    private boolean downloadFiles(List updateEntries, File directory) {
    	
    	DownloadTask downloadTask = new DownloadTask(updateEntries, directory);

    	new ProgressDialog(this.om, 
    					   this.bundle.getString("updateInfo.downloadProgress.title"),
    					   this.bundle.getString("updateInfo.downloadProgress.information"),
    					   downloadTask);
    	    	
    	if( downloadTask.getReturnType() == Worker.RETURN_TYPE_OK ) {
    		return true;
    	} else {
    		return false;
    	}    	
    	
    }
	
}

class DownloadTask implements Worker {
	
	private File targetDir = null;
	private List updateEntries = null;
	
	private byte returnValue = Worker.RETURN_TYPE_OK;
	
	public DownloadTask(List updateEntries, File targetDirectory) {
		
		this.updateEntries = updateEntries;
		this.targetDir = targetDirectory;
		
	}
	
	public String getReturnMessage() {

		return null;
		
	}

	public byte getReturnType() {

		return this.returnValue;
		
	}

	public void run() {
		
        // Loop over files to download
        ListIterator iterator = this.updateEntries.listIterator();
        UpdateEntry currentEntry = null;
        while( iterator.hasNext() ) {
        	currentEntry = (UpdateEntry)iterator.next();
        	
        	try {
        		
    			HttpURLConnection conn = (HttpURLConnection)currentEntry.getDownloadURL().openConnection();
    			conn.setRequestProperty("User-Agent", "Observation Manager Update Client");
    			if ( conn.getResponseCode() != HttpURLConnection.HTTP_OK) {		// HTTP connection error
    				System.err.println("No download possible from: " + currentEntry.getDownloadURL() + "\nHTTP Response was: " + conn.getResponseMessage());
    				conn.disconnect();	
    				this.returnValue = Worker.RETURN_TYPE_ERROR;
    			} else {														// Download file
    				
    				// Get download filename
    				String path = currentEntry.getDownloadURL().getPath();			// Get rid of query parameters
    				String filename = path.substring(path.lastIndexOf('/') + 1);	// Get filename
    				
    				BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
    				FileOutputStream fos = new FileOutputStream(this.targetDir.getAbsolutePath() + File.separator + filename);
    			    byte buf[] = new byte[1024];
    			    int len;
    			    while( (len = bis.read(buf)) > 0 ) {
    			    	fos.write(buf, 0, len);	
    			    }
    			    
    			    // Close streams and connections
    			    bis.close();
    			    fos.flush();
    			    fos.close();
    			    conn.disconnect();			    
    			}	
    			
        	} catch(IOException ioe) {
        		System.err.println("Error while downloading file: " + currentEntry.getDownloadURL() + "\nNested exception was: " + ioe);
        	}
        	
        }	
        
        this.returnValue = Worker.RETURN_TYPE_OK;
		
	}
	
}


class UpdateTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 3059700226953902438L;

	private PropertyResourceBundle bundle = (PropertyResourceBundle)ResourceBundle.getBundle("ObservationManager", Locale.getDefault());

	private List updateEntries = null;
	private boolean[] checkBoxes = null;
	private JButton download = null;
	private int activeCounter = 0;
	
	public UpdateTableModel(List updateEntries, JButton download) {
		
		this.updateEntries = updateEntries;
		
		// Initialize checkboxes
		this.checkBoxes = new boolean[this.updateEntries.size()];
		for(int i=0; i < this.checkBoxes.length; i++) {
			this.checkBoxes[i] = true;
		}
		
		this.activeCounter = this.updateEntries.size();
		this.download = download;
		
	}
	
	public int getColumnCount() {

		return 4;
		
	}

	public int getRowCount() {
		
		if(   (this.updateEntries == null)
		   || (this.updateEntries.isEmpty())
		   ) {
			return 5;
		}

		return this.updateEntries.size();
		
	}
	
	public void setValueAt(Object o, int row, int column){
		
		if( column == 0 ) {
			if( o instanceof Boolean ) {		
				
				// If all entries are deselected, deactivate Download Button
				if( this.checkBoxes[row] ) {
					this.activeCounter--;
				} else {
					this.activeCounter++;
				}
				
				if( this.activeCounter == 0 ) {
					this.download.setEnabled(false);
				} else {
					this.download.setEnabled(true);
				}
				
				this.checkBoxes[row] = !this.checkBoxes[row];
				super.fireTableDataChanged();
			}				
		}
		
	}	

	public Object getValueAt(int rowIndex, int columnIndex) {

		switch( columnIndex ) {
			case 0: {
				return new Boolean(this.checkBoxes[rowIndex]);
			}
			case 1: {
				return "" + ((UpdateEntry)this.updateEntries.get(rowIndex)).getName();
			}			
			case 2: {
				return "" + ((UpdateEntry)this.updateEntries.get(rowIndex)).getOldVersion();
			}
			case 3: {
				return "" + ((UpdateEntry)this.updateEntries.get(rowIndex)).getNewVersion();
			}			
		}
		
		return "";
		
	}
	
    public Class getColumnClass(int columnIndex) {
    	
        Class c = null;

        switch (columnIndex) {
	        case 0 : {
	            		c = Boolean.class;
	            		break; 
	         		 }        
            default : {            	
                        c = String.class;
                        break; 
                     }
        }        
        
        return c;    	
    	
    }	

    public boolean isCellEditable(int rowIndex, int columnIndex) {
    	
    	if( columnIndex == 0 ) {
    		return true;
    	}
    	
    	return false;
    	
    }
    
    public String getColumnName(int column) {

        String name = "";

        switch (column) {
            case 0 : {
                        name = this.bundle.getString("updateInfo.column.download");
                        break; 
                     }
            case 1 : {
                        name = this.bundle.getString("updateInfo.column.name");
                        break; 
                     }
            case 2 : {
		                name = this.bundle.getString("updateInfo.column.oldVersion");
		                break; 
		             }
            case 3 : {
		                name = this.bundle.getString("updateInfo.column.newVersion");
		                break; 
		             }            
        }        
        
        return name;
     
    } 
    
    public List getSelected() {

    	ArrayList result = new ArrayList(this.checkBoxes.length);
    	boolean currentValue = false;
    	for(int i=0; i < this.checkBoxes.length; i++) {
    		currentValue = ((Boolean)this.getValueAt(i, 0)).booleanValue();
    		if( currentValue ) {
    			result.add(this.updateEntries.get(i));
    		}
    	}
    	
    	return result;
    	
    }

}
