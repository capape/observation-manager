/* ====================================================================
 * /container/ImageContainer.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.container;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Scrollable;

import de.lehmannet.om.IObservation;
import de.lehmannet.om.ui.dialog.FITSImageDialog;
import de.lehmannet.om.ui.dialog.ImageDialog;
import de.lehmannet.om.ui.navigation.ObservationManager;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.RelativPath;

public class ImageContainer extends Container implements MouseListener, Scrollable {

  private final PropertyResourceBundle bundle = (PropertyResourceBundle)ResourceBundle.getBundle("ObservationManager", Locale.getDefault());
  
  public static final int THUMBNAIL_SIZE_WIDTH = 96;
  public static final int THUMBNAIL_SIZE_HEIGHT = 96;

  // Use a static image as thumbnail for fits files
  public static final String THUMBNAIL_NAME_FITS = "fits.png";

  private ObservationManager om = null;

  private boolean editable = false;

  private GridBagLayout layout = new GridBagLayout();

  private int numberOfImages = 0;

  public ImageContainer(List files, ObservationManager om, boolean editable) {

    this.om = om;
    this.editable = editable;

    this.createContainer();

    this.addImages(files);

  }

  public void addImages(List images) {

		if(   (images == null)
		   || (images.size() <= 0)
		   ) {
				return;	
		}		
		 
		Image image = null;
		Image thumb = null;
		MyImageLabel label = null;
		String path = null;
		GridBagConstraints c = new GridBagConstraints();
		int i=0;
		for(; i < images.size(); i++) {

			// The given list can contain File or String objects
			if( images.get(i) instanceof String ) {
				path = (String)images.get(i);
				if( path.startsWith("." + File.separator) ) {   // Path is relative
				    path = this.om.getXmlCache().getXMLPathForSchemaElement(this.om.getSelectedTableElement()) + File.separator + path;  
				}
			} else {
				path = ((File)images.get(i)).getAbsolutePath();
				if( path.startsWith("." + File.separator) ) {   // Path is relative
            path = this.om.getXmlCache().getXMLPathForSchemaElement(this.om.getSelectedTableElement()) + File.separator + path;  
				}
			}
			
			if( !((path.endsWith(".fits")) || (path.endsWith(".fit")) || (path.endsWith(".fts")) ) ) {
				image = Toolkit.getDefaultToolkit().getImage(path);		
				
			    thumb = image.getScaledInstance(ImageContainer.THUMBNAIL_SIZE_WIDTH,
			                                    ImageContainer.THUMBNAIL_SIZE_HEIGHT,
										        Image.SCALE_FAST);				
			} else {
				thumb = Toolkit.getDefaultToolkit().getImage(this.om.getInstallDir() + File.separator + "images" + File.separator + ImageContainer.THUMBNAIL_NAME_FITS);
			}
								
		    // Only save thumbnail image, to avoid OutOfMemory exceptions
		    label = new MyImageLabel(new ImageIcon(thumb), path);
		       		    		    
		    label.addMouseListener(this);		        
		    		    
		    ConstraintsBuilder.buildConstraints(c, i + this.numberOfImages, 0, 1, 1, 1, 1);
		    this.layout.setConstraints(label, c);
		    		   		   
		    super.add(label);
		    
		    if( this.editable ) {
		    	JLabel delete = new JLabel(this.bundle.getString("imageContainer.deleteImage"));
		    	
		    	delete.setForeground(Color.BLUE);		    		    	
		    	
			    delete.addMouseListener(this);
		    	
			    // Add DeleteButton to ImageLabel
		    	label.addDeleteButton(delete);		    
		    	
		    	ConstraintsBuilder.buildConstraints(c, i + this.numberOfImages , 1, 1, 1, 1, 1);
			    this.layout.setConstraints(delete, c);		      
		      
		    	
			    super.add(delete);
		    }		    
			
		}	
		
		this.numberOfImages += i;
		
	}

  public List getImages(String homeDir) {

    // No images were set
    if (super.getComponents() == null) {
      return new ArrayList();
    }

    // Find out whether images path should be returned relative or absolute
    boolean relativePath = Boolean.valueOf(
        this.om.getConfiguration().getConfig(ObservationManager.CONFIG_IMAGESDIR_RELATIVE)).booleanValue();
    if ((homeDir == null) || ("".equals(homeDir.trim()))) {
      relativePath = false;
    }

    // Add each image individually
    Component[] comps = super.getComponents();
    ArrayList result = new ArrayList(comps.length);
    for (int i = 0; i < comps.length; i++) {
      MyImageLabel l = null;
      if (comps[i] instanceof MyImageLabel) {
        l = (MyImageLabel) comps[i];
        if (relativePath) {
          result.add(RelativPath.getRelativePath(new File(homeDir), new File(l.getPath()))); // Store path relative
        } else {
          result.add(l.getPath()); // Store path absolute
        }
      }
    }

    return result;

  }

  private void createContainer() {

    super.setLayout(this.layout);

  }

  // -------------
  // MouseListener ----------------------------------------------------------
  // -------------

  public void mouseClicked(MouseEvent e) {

    if ((e.getClickCount() == 2) && (e.getButton() == MouseEvent.BUTTON1)) {
      if (e.getSource() instanceof MyImageLabel) {
        MyImageLabel l = (MyImageLabel) e.getSource();
        if (!((l.getPath().endsWith(".fits")) || (l.getPath().endsWith(".fit")) || (l.getPath().endsWith(".fts")))) {
          new ImageDialog(l.getImage(), this.om);
        } else {
          new FITSImageDialog(this.om, new File(l.getPath()));
        }
      } else {
        JLabel b = (JLabel) e.getSource();
        Component[] comps = super.getComponents();
        for (int i = 0; i < comps.length; i++) {
          MyImageLabel l = null;
          if (comps[i] instanceof MyImageLabel) {
            l = (MyImageLabel) comps[i];
            if (l.getDeleteButton().equals(b)) {
              super.remove(l);
              super.remove(b);
              super.repaint();
            }
          }
        }
      }
    }

  }

  public void mouseEntered(MouseEvent e) {
    // do nothing
  }

  public void mouseExited(MouseEvent e) {
    // do nothing
  }

  public void mousePressed(MouseEvent e) {
    // do nothing
  }

  public void mouseReleased(MouseEvent e) {
    // do nothing
  }

  // ----------
  // Scrollable -------------------------------------------------------------
  // ----------

  public Dimension getPreferredScrollableViewportSize() {

    return new Dimension(this.getWidth(), this.getHeight());

  }

  public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
    return 1;
  }

  public boolean getScrollableTracksViewportHeight() {

    return false;

  }

  public boolean getScrollableTracksViewportWidth() {

    return false;

  }

  public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {

    return 1;

  }

}

class MyImageLabel extends JLabel {

  private String path = null;
  private JLabel deleteButton = null;

  public MyImageLabel(ImageIcon thumb, String path) {

    super(thumb);

    this.path = path;

  }

  public String getPath() {

    return this.path;

  }

  public Image getImage() {

    return Toolkit.getDefaultToolkit().getImage(this.path);

  }

  public void addDeleteButton(JLabel button) {

    this.deleteButton = button;

  }

  public JLabel getDeleteButton() {

    return this.deleteButton;

  }

}