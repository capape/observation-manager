/* ====================================================================
 * /panel/FilterPanel.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.panel;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

import de.lehmannet.om.Filter;
import de.lehmannet.om.IFilter;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.OMLabel;

public class FilterPanel extends AbstractPanel implements ItemListener {		
	
  private static final long serialVersionUID = 4294491718036332590L;

  private IFilter filter = null;
	
	private JTextField model = new JTextField();
	private JComponent type = null;       // Will be JTextField or JComboBox
	private JComponent colorType = null;  // Will be JTextField or JComboBox
	private JTextField wratten = new JTextField();
	private JTextField schott = new JTextField();
	private JTextField vendor = new JTextField();
	
	private OMLabel LcolorType = null;
	
    public FilterPanel(IFilter filter, 
    				   boolean editable) {

		super(editable);
		
		this.filter = filter;
		
		this.createPanel();
		
		if( editable ) {
			this.fillBoxes();	
		}				       
		
		if( filter != null ) {
			this.loadSchemaElement();
		}                
	
	}  	
	
    
	// ------------
	// ItemListener -----------------------------------------------------------
	// ------------
	
	public void itemStateChanged(ItemEvent e) {
		    
		Object source = e.getSource();
		if( source.equals(this.type) ) {			
	        if( e.getStateChange() == ItemEvent.SELECTED ) {  // If type color gets selected enabled additional boxes and fields
	        	BoxItem typeItem = (BoxItem)((JComboBox)this.type).getSelectedItem();
	        	if(   (typeItem != null)
	        	   && (typeItem.getKey().equals(Filter.FILTER_TYPE_COLOR))
				   ) {
	        		this.colorType.setEnabled(true);
	        		this.wratten.setEnabled(true);
	        		this.wratten.setEditable(true);
	        		this.schott.setEnabled(true);
	        		this.schott.setEditable(true);

	        		// Remove the empty item in color box
	    			BoxItem emptyItem = new BoxItem(BoxItem.EMPTY_ITEM);  
	    			((JComboBox)this.colorType).removeItem(emptyItem);    	        		
	        		
	        		LcolorType.setFont(new Font("sansserif",Font.BOLD, 12));
	        		
	        		return;
	        	} 
	        }
		}
		
		// In all other cases make sure colorType and additional fields are disabled
		this.colorType.setEnabled(false);
		
		// Make sure there exists (only) one empty item...and select it
		BoxItem emptyItem = new BoxItem(BoxItem.EMPTY_ITEM);  
		((JComboBox)this.colorType).removeItem(emptyItem); 		
		((JComboBox)this.colorType).addItem(emptyItem);
		((JComboBox)this.colorType).setSelectedItem(emptyItem);
		
		this.wratten.setEnabled(false);
		this.wratten.setText("");
		this.schott.setEnabled(false);
		this.schott.setText("");
		LcolorType.setFont(new Font("sansserif",Font.ITALIC + Font.BOLD, 12));
		
	}    
    
    
	public ISchemaElement createSchemaElement() {
			                    
		// Check mandatory fields
		String modelName = this.model.getText();
		if(   (modelName == null)
           || ("".equals(modelName))				
		   ) {
			super.createWarning(AbstractPanel.bundle.getString("panel.filter.warning.noModel"));
			return null;
		}
		
		String t = this.getType();
		if( t == null ) {
			super.createWarning(AbstractPanel.bundle.getString("panel.filter.warning.noType"));
			return null;			
		}
		
		this.filter = new Filter(modelName, t);
						
    	// Add optional fields
		String ct = this.getColorType();
		if( ct != null ) {
			this.filter.setColor(ct);
		}
		
		this.filter.setWratten(this.wratten.getText());
		this.filter.setSchott(this.schott.getText());
		this.filter.setVendor(this.vendor.getText());
    	
    	return this.filter;									
		
	}

	public ISchemaElement getSchemaElement() {

		return this.filter;
		
	}

	public ISchemaElement updateSchemaElement() {
		
    	if( this.filter == null ) {
    		return null;
    	}
    	    	
		// Check mandatory fields
		String modelName = this.model.getText();
		if(   (modelName == null)
           || ("".equals(modelName))				
		   ) {
			super.createWarning(AbstractPanel.bundle.getString("panel.filter.warning.noModel"));
			return null;
		}
		
		String t = this.getType();
		if( t == null ) {
			super.createWarning(AbstractPanel.bundle.getString("panel.filter.warning.noType"));
			return null;			
		}
		
		this.filter.setModel(modelName);
		this.filter.setType(t);
						
    	// Add optional fields
		String ct = this.getColorType();
		if( ct != null ) {
			this.filter.setColor(ct);
		}
		
		this.filter.setWratten(this.wratten.getText());
		this.filter.setSchott(this.schott.getText());	
		this.filter.setVendor(this.vendor.getText());
    	
    	return this.filter;
		
	}
	
	public static String getI18Ntype(String type) {
		
		return BoxItem.getI18NString(type);
		
	}

	public static String getI18Ncolor(String type) {
		
		return BoxItem.getI18NString(type);
		
	}	
	
	private String getType() {
		
		Object t = null;
		if( super.isEditable() ) {
			t = ((JComboBox)this.type).getSelectedItem();
			BoxItem bi = (BoxItem)t;
			if( bi.isEmptyItem() ) {
				return null;
			} else {
				return bi.getKey();
			}			
		} else {
			t = ((JTextField)this.type).getText();
			return (String)t;
		}		
		
	}
	
	private String getColorType() {
		
		Object t = null;
		if( super.isEditable() ) {
			t = ((JComboBox)this.colorType).getSelectedItem();
			BoxItem bi = (BoxItem)t;
			if( bi.isEmptyItem() ) {
				return null;
			} else {
				return bi.getKey();
			}
		} else {
			t = ((JTextField)this.colorType).getText();
			return (String)t;
		}		
		
	}	
    
    private void loadSchemaElement() {        
       
    	// Set mandatory
    	
        this.model.setText(this.filter.getModel());
        this.model.setEditable(super.isEditable());

        if( this.filter.getVendor() != null ) {
        	this.vendor.setText(this.filter.getVendor());	
        }
        this.vendor.setEditable(super.isEditable());        
        
        if( super.isEditable() ) {
            ((JComboBox)this.type).setSelectedItem(new BoxItem(this.filter.getType()));
            this.type.setEnabled(super.isEditable());            
        } else {
        	((JTextField)this.type).setText(BoxItem.getI18NString(this.filter.getType()));
        	((JTextField)this.type).setEditable(super.isEditable());
        }                                  
        
        if( this.filter.getColor() != null ) {          
            if( super.isEditable() ) {
            	this.colorType.setEnabled(super.isEditable());            
            } else {
            	((JTextField)this.colorType).setEditable(super.isEditable());
            }          	        	
        	
            if( super.isEditable() ) {
                ((JComboBox)this.colorType).setSelectedItem(new BoxItem(this.filter.getColor()));                
            } else {
            	((JTextField)this.colorType).setText(BoxItem.getI18NString(this.filter.getColor()));            	
            }                         
            
            if(   (this.filter.getWratten() != null)
               && !("".equals(this.filter.getWratten())) 
			   ) {
            	this.wratten.setText(this.filter.getWratten());            
            }
            this.wratten.setEditable(super.isEditable());
            
            if(   (this.filter.getSchott() != null)
               && !("".equals(this.filter.getSchott())) 
			   ) {
            	this.schott.setText(this.filter.getSchott());            	
            }            
            this.schott.setEditable(super.isEditable());
            
        }        
        
    }	
	
    private void createPanel() {
        
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;        
        this.setLayout(gridbag);
        
        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 1, 1, 5, 1);
        OMLabel LmodelName = new OMLabel(AbstractPanel.bundle.getString("panel.filter.label.model"), true);        
        LmodelName.setToolTipText(AbstractPanel.bundle.getString("panel.filter.tooltip.model"));
        gridbag.setConstraints(LmodelName, constraints);
        this.add(LmodelName);
        ConstraintsBuilder.buildConstraints(constraints, 1, 0, 1, 1, 45, 1);
        this.model.setToolTipText(AbstractPanel.bundle.getString("panel.filter.tooltip.model"));
        gridbag.setConstraints(this.model, constraints);                
        this.add(this.model);  		
        
        ConstraintsBuilder.buildConstraints(constraints, 2, 0, 1, 1, 5, 1);
        OMLabel Lvendor = new OMLabel(AbstractPanel.bundle.getString("panel.filter.label.vendor"), false);        
        Lvendor.setToolTipText(AbstractPanel.bundle.getString("panel.filter.tooltip.vendor"));
        gridbag.setConstraints(Lvendor, constraints);
        this.add(Lvendor);
        ConstraintsBuilder.buildConstraints(constraints, 3, 0, 1, 1, 45, 1);
        this.vendor.setToolTipText(AbstractPanel.bundle.getString("panel.filter.tooltip.vendor"));
        gridbag.setConstraints(this.vendor, constraints);                
        this.add(this.vendor);          
		
        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 1, 1, 5, 1);
        OMLabel Ltype = new OMLabel(AbstractPanel.bundle.getString("panel.filter.label.type"), true);        
        Ltype.setToolTipText(AbstractPanel.bundle.getString("panel.filter.tooltip.type"));
        gridbag.setConstraints(Ltype, constraints);
        this.add(Ltype);
        ConstraintsBuilder.buildConstraints(constraints, 1, 1, 1, 1, 45, 1);
        if( super.isEditable() ) {
        	this.type = new JComboBox();
            ((JComboBox)this.type).addItemListener(this);                   	
        } else {
        	this.type = new JTextField();
        }       
        this.type.setToolTipText(AbstractPanel.bundle.getString("panel.filter.tooltip.type"));
        gridbag.setConstraints(this.type, constraints);                
        this.add(this.type);
        
        ConstraintsBuilder.buildConstraints(constraints, 2, 1, 1, 1, 5, 1);
        this.LcolorType = new OMLabel(AbstractPanel.bundle.getString("panel.filter.label.colorType"), false);        
        LcolorType.setToolTipText(AbstractPanel.bundle.getString("panel.filter.tooltip.colorType"));
        gridbag.setConstraints(LcolorType, constraints);
        this.add(LcolorType);
        ConstraintsBuilder.buildConstraints(constraints, 3, 1, 1, 1, 45, 1);
        if( super.isEditable() ) {
        	this.colorType = new JComboBox();
            this.colorType.setEnabled(false);  // Will be activated if type Color gets selected                
        } else {
        	this.colorType = new JTextField();
        	((JTextField)this.colorType).setEditable(false);
        }  
        this.colorType.setToolTipText(AbstractPanel.bundle.getString("panel.filter.tooltip.colorType"));
        gridbag.setConstraints(this.colorType, constraints);                
        this.add(this.colorType);       
        
        ConstraintsBuilder.buildConstraints(constraints, 0, 2, 1, 1, 5, 1);
        OMLabel Lwratten = new OMLabel(AbstractPanel.bundle.getString("panel.filter.label.wratten"), false);        
        Lwratten.setToolTipText(AbstractPanel.bundle.getString("panel.filter.tooltip.wratten"));
        gridbag.setConstraints(Lwratten, constraints);
        this.add(Lwratten);
        ConstraintsBuilder.buildConstraints(constraints, 1, 2, 1, 1, 45, 1);
        this.wratten.setToolTipText(AbstractPanel.bundle.getString("panel.filter.tooltip.wratten"));
        gridbag.setConstraints(this.wratten, constraints);
        this.wratten.setEditable(false);  
        this.add(this.wratten);           
        
        ConstraintsBuilder.buildConstraints(constraints, 2, 2, 1, 1, 5, 1);
        OMLabel Lschott = new OMLabel(AbstractPanel.bundle.getString("panel.filter.label.schott"), false);        
        Lschott.setToolTipText(AbstractPanel.bundle.getString("panel.filter.tooltip.schott"));
        gridbag.setConstraints(Lschott, constraints);       
        this.add(Lschott);
        ConstraintsBuilder.buildConstraints(constraints, 3, 2, 1, 1, 45, 1);
        this.schott.setToolTipText(AbstractPanel.bundle.getString("panel.filter.tooltip.schott"));
        gridbag.setConstraints(this.schott, constraints);
        this.schott.setEditable(false);
        this.add(this.schott);      
        
        ConstraintsBuilder.buildConstraints(constraints, 0, 3, 4, 1, 100, 90);
        constraints.fill = GridBagConstraints.BOTH;        
        JLabel Lfill = new JLabel("");        
        gridbag.setConstraints(Lfill, constraints);
        this.add(Lfill); 
        
    }
	
    private void fillBoxes() {
    	
    	// Fill type box
    	
    	JComboBox t = (JComboBox)this.type;
    	
    	// Add empty value only on creation
    	BoxItem typeEmptyItem = new BoxItem(BoxItem.EMPTY_ITEM);
    	if(   (super.isEditable())
    	   && (this.filter == null)
    	   ) {    		
    		t.addItem(typeEmptyItem);	
    	}    	
    	    	
    	t.addItem(new BoxItem(IFilter.FILTER_TYPE_NEUTRAL));
    	t.addItem(new BoxItem(IFilter.FILTER_TYPE_COLOR));
    	t.addItem(new BoxItem(IFilter.FILTER_TYPE_BROADBAND));    	    	
    	t.addItem(new BoxItem(IFilter.FILTER_TYPE_HALPHA));
    	t.addItem(new BoxItem(IFilter.FILTER_TYPE_HBETA));
    	t.addItem(new BoxItem(IFilter.FILTER_TYPE_OIII));
    	t.addItem(new BoxItem(IFilter.FILTER_TYPE_NARROWBAND));    	  	       	
    	t.addItem(new BoxItem(IFilter.FILTER_TYPE_SOLAR));
    	t.addItem(new BoxItem(IFilter.FILTER_TYPE_CORRECTIVE));
    	t.addItem(new BoxItem(IFilter.FILTER_TYPE_OTHER));
    	    	
    	t.setSelectedItem(typeEmptyItem);   // Try to...if empty item was set
    	
    	
    	// Fill colortype box
    	
    	JComboBox c = (JComboBox)this.colorType;
    	
    	// Add empty value only on creation    	
    	BoxItem colorEmptyItem = new BoxItem(BoxItem.EMPTY_ITEM);
    	if(   (super.isEditable())
    	   && (this.filter == null)
    	   ) {    		
    		c.addItem(colorEmptyItem);    	  	
    	}      	    	  	    	    
    	
    	c.addItem(new BoxItem(IFilter.FILTER_COLOR_LIGHTRED));
    	c.addItem(new BoxItem(IFilter.FILTER_COLOR_RED));
    	c.addItem(new BoxItem(IFilter.FILTER_COLOR_DEEPRED));
    	c.addItem(new BoxItem(IFilter.FILTER_COLOR_ORANGE));
    	c.addItem(new BoxItem(IFilter.FILTER_COLOR_LIGHTYELLOW));
    	c.addItem(new BoxItem(IFilter.FILTER_COLOR_YELLOW));
    	c.addItem(new BoxItem(IFilter.FILTER_COLOR_DEEPYELLOW));
    	c.addItem(new BoxItem(IFilter.FILTER_COLOR_YELLOWGREEN));
    	c.addItem(new BoxItem(IFilter.FILTER_COLOR_LIGHTGREEN));
    	c.addItem(new BoxItem(IFilter.FILTER_COLOR_GREEN));
    	c.addItem(new BoxItem(IFilter.FILTER_COLOR_MEDIUMBLUE));
    	c.addItem(new BoxItem(IFilter.FILTER_COLOR_PALEBLUE));      	
    	c.addItem(new BoxItem(IFilter.FILTER_COLOR_BLUE));
    	c.addItem(new BoxItem(IFilter.FILTER_COLOR_DEEPBLUE));    	    	
    	c.addItem(new BoxItem(IFilter.FILTER_COLOR_VIOLET));
    	    	
    	c.setSelectedItem(colorEmptyItem);    	// Try to...if empty item was set
    	
    }
    
}

// Helper class for I18N of type and color
class BoxItem {
	
	public static final String EMPTY_ITEM = "----";
	
	private final PropertyResourceBundle bundle = (PropertyResourceBundle)ResourceBundle.getBundle("ObservationManager", Locale.getDefault());
	
	private String key = null;
	
	public BoxItem(String key) {
		
		this.key = key;
		
	}
	
	public String toString() {
				
		return BoxItem.getI18NString(this.key);		
		
	}
	
	public boolean equals(Object o) {
		
		if( o instanceof BoxItem ) {
			BoxItem bi = (BoxItem)o;
			if( this.key.equals(bi.getKey()) ) {
				return true;
			}
		}
		
		return false;
		
	}
	
	public String getKey() {
		
		return this.key;
		
	}
	
	public boolean isEmptyItem() {
		
		if( key.equals(BoxItem.EMPTY_ITEM) ) {
			return true;
		}
		
		return false;
		
	}
	
	public static String getI18NString(String s) {
		
		if( s.equals(BoxItem.EMPTY_ITEM) ) {
			return BoxItem.EMPTY_ITEM;
		}
		
		BoxItem dummy = new BoxItem("");
		
		if( s.equals(IFilter.FILTER_TYPE_BROADBAND) ) {
			return dummy.bundle.getString("filter.type.broadBand");
		} else if( s.equals(IFilter.FILTER_TYPE_CORRECTIVE) ) {
			return dummy.bundle.getString("filter.type.corrective");
		} else if( s.equals(IFilter.FILTER_TYPE_HALPHA) ) {
			return dummy.bundle.getString("filter.type.Halpha");
		} else if( s.equals(IFilter.FILTER_TYPE_HBETA) ) {
			return dummy.bundle.getString("filter.type.Hbeta");
		} else if( s.equals(IFilter.FILTER_TYPE_NARROWBAND) ) {
			return dummy.bundle.getString("filter.type.narrowBand");
		} else if( s.equals(IFilter.FILTER_TYPE_NEUTRAL) ) {
			return dummy.bundle.getString("filter.type.neutral");
		} else if( s.equals(IFilter.FILTER_TYPE_OIII) ) {
			return dummy.bundle.getString("filter.type.OIII");
		} else if( s.equals(IFilter.FILTER_TYPE_SOLAR) ) {
			return dummy.bundle.getString("filter.type.solar");			
		} else if( s.equals(IFilter.FILTER_TYPE_OTHER) ) {
			return dummy.bundle.getString("filter.type.other");
		} else if( s.equals(IFilter.FILTER_TYPE_COLOR) ) {
			return dummy.bundle.getString("filter.type.color");
		} else if( s.equals(IFilter.FILTER_COLOR_BLUE) ) {
			return dummy.bundle.getString("filter.color.blue");
		} else if( s.equals(IFilter.FILTER_COLOR_DEEPBLUE) ) {
			return dummy.bundle.getString("filter.color.deepBlue");
		} else if( s.equals(IFilter.FILTER_COLOR_DEEPRED) ) {
			return dummy.bundle.getString("filter.color.deepRed");
		} else if( s.equals(IFilter.FILTER_COLOR_DEEPYELLOW) ) {
			return dummy.bundle.getString("filter.color.deepYellow");
		} else if( s.equals(IFilter.FILTER_COLOR_GREEN) ) {
			return dummy.bundle.getString("filter.color.green");
		} else if( s.equals(IFilter.FILTER_COLOR_LIGHTGREEN) ) {
			return dummy.bundle.getString("filter.color.lightGreen");
		} else if( s.equals(IFilter.FILTER_COLOR_LIGHTRED) ) {
			return dummy.bundle.getString("filter.color.lightRed");
		} else if( s.equals(IFilter.FILTER_COLOR_LIGHTYELLOW) ) {
			return dummy.bundle.getString("filter.color.lightYellow");
		} else if( s.equals(IFilter.FILTER_COLOR_MEDIUMBLUE) ) {
			return dummy.bundle.getString("filter.color.mediumBlue");
		} else if( s.equals(IFilter.FILTER_COLOR_ORANGE) ) {
			return dummy.bundle.getString("filter.color.orange");
		} else if( s.equals(IFilter.FILTER_COLOR_PALEBLUE) ) {
			return dummy.bundle.getString("filter.color.paleBlue");
		} else if( s.equals(IFilter.FILTER_COLOR_RED) ) {
			return dummy.bundle.getString("filter.color.red");
		} else if( s.equals(IFilter.FILTER_COLOR_VIOLET) ) {
			return dummy.bundle.getString("filter.color.violet");
		} else if( s.equals(IFilter.FILTER_COLOR_YELLOW) ) {
			return dummy.bundle.getString("filter.color.yellow");
		} else if( s.equals(IFilter.FILTER_COLOR_YELLOWGREEN) ) {
			return dummy.bundle.getString("filter.color.yellow-green");
		}		
		
		return "Filter string not found";
		
	}
	
}
