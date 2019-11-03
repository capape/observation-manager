/* ====================================================================
 * /container/TimeContainer
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */


package de.lehmannet.om.ui.container;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JTextField;

import de.lehmannet.om.ui.util.ConstraintsBuilder;

public class TimeContainer extends Container implements FocusListener {

	private static final long serialVersionUID = 2217864566103147417L;

	final PropertyResourceBundle bundle = (PropertyResourceBundle)ResourceBundle.getBundle("ObservationManager", Locale.getDefault());	
	
	private JTextField h = new JTextField(2);
	private JTextField m = new JTextField(2);
	private JTextField s = new JTextField(2);
	
	private int hour = 0;
	private int min = 0;
	private int sec = 0;
	
	private boolean editable = false;
	
	public TimeContainer(int hour, 
			             int min, 
			             int sec,
			             boolean editable) {
					
		this.setTime(hour, min, sec);
		this.editable = editable;
		
		this.createContainer();
				
	}
	
	
	
	public void focusGained(FocusEvent e) {

		Component c = e.getComponent();
		if( c.equals(this.h) ) {
			this.h.selectAll();
		} else if( c.equals(this.m) ) {
			this.m.selectAll();
		} else if( c.equals(this.s) ) {
			this.s.selectAll();
		}
		
	}
	
	public void focusLost(FocusEvent e) {

		Component c = e.getComponent();
		if( c.equals(this.h) ) {
			int h = this.getHour();
			if( h == -1 ) {
				this.h.setText("00");
			}
		} else if( c.equals(this.m) ) {
			int m = this.getMinutes();
			if( m == -1 ) {
				this.m.setText("00");
			}
		} else if( c.equals(this.s) ) {
			int s = this.getSeconds();
			if( s == -1 ) {
				this.s.setText("00");
			}
		}
		
	}


	public int getHour() {
		
		String sHour = this.h.getText();
		try {
			this.hour = Integer.parseInt(sHour);	
		} catch (NumberFormatException nfe) {
			return -1;
		}
		
		if(   (this.hour > 23)
		   || (this.hour < 0)
		   ) {
			return -1;
		}
		
		return this.hour;
		
	}

	public int getMinutes() {
		
		String sMin = this.m.getText();
		try {
			this.min = Integer.parseInt(sMin);	
		} catch (NumberFormatException nfe) {
			return -1;
		}
		
		if(   (this.min > 59)
		   || (this.min < 0)
		   ) {
			return -1;
		}
		
		return this.min;
		
	}
	
	public int getSeconds() {
		
		String sSec = this.s.getText();
		try {
			this.sec = Integer.parseInt(sSec);	
		} catch (NumberFormatException nfe) {
			return -1;
		}
		
		if(   (this.sec > 59)
		   || (this.sec < 0)
		   ) {
			return -1;
		}
		
		return this.sec;
		
	}	
	
	public boolean checkTime() {
		
		if(   (this.getHour() != -1)
		   && (this.getMinutes() != -1)
		   && (this.getSeconds() != -1)
		   ) {
			return true;
		}
		
		return false;
		
	}
	
	public void setTime(int hour, int min, int sec) {
		
		if(   (hour < 24)
		   || (hour >= 0)
		   ) {
			this.hour = hour;
			this.h.setText(this.formatValue(this.hour));
		}
		
		if(   (min < 60)
		   || (min >= 0)
		   ) {
			this.min = min;
			this.m.setText(this.formatValue(this.min));
		}
		
		if(   (sec < 6)
		   || (sec >= 0)
		   ) {
			this.sec = sec;
			this.s.setText(this.formatValue(this.sec));
		}					
		
	}
	
	public void setEditable(boolean editable) {
		
		this.editable = editable;
		
		this.h.setEditable(this.editable);
		this.m.setEditable(this.editable);
		this.s.setEditable(this.editable);
		
	}
	
    public void setToolTipText(String text) {
    	
    	this.h.setToolTipText(text);    	
    	this.m.setToolTipText(text);    	
    	this.s.setToolTipText(text);
    	
    }	
	
    private void createContainer() {
        
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        super.setLayout(gridbag);
        
        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 1, 1, 10, 1);
        JLabel Ltime = new JLabel(this.bundle.getString("time.label.time"), JLabel.RIGHT);
        gridbag.setConstraints(Ltime, constraints);                
        super.add(Ltime);        

        ConstraintsBuilder.buildConstraints(constraints, 1, 0, 1, 1, 20, 1);
        this.h.setEditable(this.editable);
        this.h.addFocusListener(this);
        gridbag.setConstraints(this.h, constraints);                
        super.add(this.h);             
        
        ConstraintsBuilder.buildConstraints(constraints, 2, 0, 1, 1, 5, 1);
        JLabel LhmDelimiter = new JLabel(":");
        gridbag.setConstraints(LhmDelimiter, constraints);                
        super.add(LhmDelimiter);        

        ConstraintsBuilder.buildConstraints(constraints, 3, 0, 1, 1, 20, 1);
        this.m.setEditable(this.editable);
        this.m.addFocusListener(this);
        gridbag.setConstraints(this.m, constraints);                
        super.add(this.m);
        
        ConstraintsBuilder.buildConstraints(constraints, 4, 0, 1, 1, 5, 1);
        JLabel LmsDelimiter = new JLabel(":");
        gridbag.setConstraints(LmsDelimiter, constraints);                
        super.add(LmsDelimiter);        

        ConstraintsBuilder.buildConstraints(constraints, 5, 0, 1, 1, 20, 1);
        this.s.setEditable(this.editable);
        this.s.addFocusListener(this);
        gridbag.setConstraints(this.s, constraints);                
        super.add(this.s);        

   /*     ConstraintsBuilder.buildConstraints(constraints, 6, 0, 2, 1, 95, 1);
        JLabel dummy = new JLabel("");
        gridbag.setConstraints(dummy, constraints);                
        super.add(dummy);     */
        
    }
    
    private String formatValue(int value) {
    	
    	if( value < 10 ) {
    		return "0" + value;
    	}
    	
    	return "" + value;
    	
    }
	
}
