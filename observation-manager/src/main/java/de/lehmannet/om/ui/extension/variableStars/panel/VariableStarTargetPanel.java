/* ====================================================================
 * /extension/variableStars/panel/VariableStarTargetPanel.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */


package de.lehmannet.om.ui.extension.variableStars.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import de.lehmannet.om.IObserver;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.ITarget;
import de.lehmannet.om.extension.variableStars.TargetVariableStar;
import de.lehmannet.om.ui.container.TargetStarContainer;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.panel.AbstractPanel;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.OMLabel;
import de.lehmannet.om.util.FloatUtil;


public class VariableStarTargetPanel extends AbstractPanel {

  private static final long serialVersionUID = -7456755701627150427L;

  private final PropertyResourceBundle bundle = (PropertyResourceBundle)ResourceBundle.getBundle("de.lehmannet.om.ui.extension.variableStars.VariableStar", Locale.getDefault());	
	
	private ObservationManager observationManager = null;
	private TargetVariableStar target = null;
	
	private TargetStarContainer targetContainer = null;
	
	private JTextField type = null;
	private JTextField maxApparentMag = null;
	private JTextField period = null;
	
    public VariableStarTargetPanel(ObservationManager om, 
    		                       ITarget target, 
    		                       Boolean editable) throws IllegalArgumentException {
	
		super(editable);
		
		if(    (target != null)
		   && !(target instanceof TargetVariableStar) 
		   ) {
			throw new IllegalArgumentException("Passed ITarget must derive from de.lehmannet.om.extension.variableStars.TargetVariableStar\n");
		}
		
		this.target = (TargetVariableStar)target;
		this.observationManager = om;
		
		this.createPanel();        
		
		if( this.target != null ) {
			this.loadSchemaElement();
		}		   
	
	}  	
	
    public ISchemaElement getSchemaElement() {
    	
    	return this.target;
    	
    }          
    
    public ISchemaElement updateSchemaElement() {
    	
    	if( this.target == null ) {
    		return null;
    	}
    	    	
		ITarget t = this.targetContainer.updateTarget();
		if( t == null ) {
			return null;
		} else {
			this.target = (TargetVariableStar)t;
		}  

		// Optional parameters
		
		String type = this.type.getText().trim();	
		if(   (type != null)
		   && !("".equals(type))
		   ) {
			this.target.setType(type);
		}	
		
		String magnitude = this.maxApparentMag.getText().trim();		
		if(   (magnitude != null)
		   && !("".equals(magnitude))
		   ) {
			float mag = FloatUtil.parseFloat(magnitude);
			if( mag > this.target.getMagnitudeApparent() ) {  // Greater value means less brightness
				super.createWarning(this.bundle.getString("panel.variableStarTarget.warning.maxSmallerMin"));
				return null;
			}
			this.target.setMaxMagnitudeApparent(mag);
		}
		
		String periodString = this.period.getText().trim();		
		if(   (periodString != null)
		   && !("".equals(periodString))
		   ) {
			float p = FloatUtil.parseFloat(periodString);
			this.target.setPeriod(p);
		}		
		
		return this.target;
		
	}
	
	public ISchemaElement createSchemaElement() {

		String name = this.targetContainer.getName();
		String datasource = this.targetContainer.getDatasource();
		IObserver observer = this.targetContainer.getObserver();		
		
		// Make sure only datasource or observer is set
		if( !this.targetContainer.checkOrigin(datasource, observer) ) {
			return null;
		}
				
		if( observer != null ) {
			this.target = new TargetVariableStar(name, observer);	
		} else {
			this.target = new TargetVariableStar(name, datasource);	
		}
		this.targetContainer.setTarget(this.target);  // Otherwise updateSchemaElement will fail
		
		// Set all other fields
		ITarget t = (ITarget)this.updateSchemaElement();
		if( t == null ) {
			return null;
		} else {
			this.target = (TargetVariableStar)t;
		}
		
		return this.target;		
		
	}		
	
	private void loadSchemaElement() {
			
		// Container data will be loaded while construction of container in
		// createPanel method		
		
		String type = this.target.getType();
		if( type != null ) {
			this.type.setText(type);
		} else {
			this.type.setText("");
		}
		this.type.setEditable(super.isEditable());
		
		float maxAppMag = this.target.getMaxApparentMag();
		if( !Float.isNaN(maxAppMag) ) {
			this.maxApparentMag.setText("" + maxAppMag);
		} else {
			this.maxApparentMag.setText("");
		}
		this.maxApparentMag.setEditable(super.isEditable());		
		
		float period = this.target.getPeriod();
		if( !Float.isNaN(period) ) {
			this.period.setText("" + period);
		} else {
			this.period.setText("");
		}
		this.period.setEditable(super.isEditable());			
		
	}
	
	private void createPanel() {	
		
		GridBagLayout gridbag = new GridBagLayout();
	    GridBagConstraints constraints = new GridBagConstraints();
	    constraints.anchor = GridBagConstraints.WEST;
	    constraints.fill = GridBagConstraints.HORIZONTAL; 
	    this.setLayout(gridbag);
	
        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 5, 1, 45, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.targetContainer = new TargetStarContainer(this.observationManager, this.target, super.isEditable());        
        gridbag.setConstraints(this.targetContainer, constraints);
        this.add(this.targetContainer);   	    
	    // Change labels in container        
        this.targetContainer.LmagnitudeApp.setText(this.bundle.getString("panel.variableStarTarget.label.minApparentMag"));
        this.targetContainer.LmagnitudeApp.setToolTipText(this.bundle.getString("panel.variableStarTarget.tooltip.minApparentMag"));
        this.targetContainer.magnitudeApparent.setToolTipText(this.bundle.getString("panel.variableStarTarget.tooltip.minApparentMag"));
        
        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 5, 1, 100, 1);
        JSeparator seperator1 = new JSeparator(JSeparator.HORIZONTAL);
        gridbag.setConstraints(seperator1, constraints);
        this.add(seperator1);             
    
	    ConstraintsBuilder.buildConstraints(constraints, 0, 2, 1, 1, 1, 1);       
	    OMLabel LmaxAppMag = new OMLabel(this.bundle.getString("panel.variableStarTarget.label.maxApparentMag"), false);
		LmaxAppMag.setToolTipText(this.bundle.getString("panel.variableStarTarget.tooltip.maxApparentMag"));
	    gridbag.setConstraints(LmaxAppMag, constraints);
	    this.add(LmaxAppMag);           
	    ConstraintsBuilder.buildConstraints(constraints, 1, 2, 1, 1, 15, 1);
	    this.maxApparentMag = new JTextField();
	    this.maxApparentMag.setToolTipText(this.bundle.getString("panel.variableStarTarget.tooltip.maxApparentMag"));
	    gridbag.setConstraints(this.maxApparentMag, constraints);
	    this.add(this.maxApparentMag);	    	    
	    
	    // Dummy for filling space
	    ConstraintsBuilder.buildConstraints(constraints, 2, 2, 3, 1, 50, 1);
	    constraints.fill = GridBagConstraints.HORIZONTAL;
	    JLabel Ldummy = new JLabel("");
        gridbag.setConstraints(Ldummy, constraints);
        this.add(Ldummy);	 	    
	    	
	    ConstraintsBuilder.buildConstraints(constraints, 0, 3, 1, 1, 1, 1);       
	    OMLabel Ltype = new OMLabel(this.bundle.getString("panel.variableStarTarget.label.type"), false);
		Ltype.setToolTipText(this.bundle.getString("panel.variableStarTarget.tooltip.type"));
	    gridbag.setConstraints(Ltype, constraints);
	    this.add(Ltype);           
	    ConstraintsBuilder.buildConstraints(constraints, 1, 3, 1, 1, 15, 1);
	    this.type = new JTextField();
	    this.type.setToolTipText(this.bundle.getString("panel.variableStarTarget.tooltip.type"));
	    gridbag.setConstraints(this.type, constraints);
	    this.add(this.type);	    
	    
	    ConstraintsBuilder.buildConstraints(constraints, 2, 3, 1, 1, 1, 1);       
	    OMLabel Lperiod = new OMLabel(this.bundle.getString("panel.variableStarTarget.label.period"), JLabel.RIGHT, false);
		Lperiod.setToolTipText(this.bundle.getString("panel.variableStarTarget.tooltip.period"));
	    gridbag.setConstraints(Lperiod, constraints);
	    this.add(Lperiod);           
	    ConstraintsBuilder.buildConstraints(constraints, 3, 3, 1, 1, 1, 1);
	    this.period = new JTextField(5);
	    this.period.setToolTipText(this.bundle.getString("panel.variableStarTarget.tooltip.period"));
	    gridbag.setConstraints(this.period, constraints);
	    this.add(this.period);	 
	    ConstraintsBuilder.buildConstraints(constraints, 4, 3, 1, 1, 1, 1);       
	    OMLabel Ldays = new OMLabel(this.bundle.getString("panel.variableStarTarget.label.period.days"), false);
		Ldays.setToolTipText(this.bundle.getString("panel.variableStarTarget.tooltip.period"));
	    gridbag.setConstraints(Ldays, constraints);
	    this.add(Ldays);           	    
 	    
	    
        ConstraintsBuilder.buildConstraints(constraints, 0, 4, 5, 1, 5, 90);
        constraints.fill = GridBagConstraints.BOTH;        
        JLabel Lfill = new JLabel("");        
        gridbag.setConstraints(Lfill, constraints);
        this.add(Lfill);  	       
        
	}

}
