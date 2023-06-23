/*
 * ====================================================================
 * /container/ImageContainer.java
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
import java.net.URL;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Scrollable;

import org.apache.commons.lang3.StringUtils;

import de.lehmannet.om.model.ObservationManagerModel;
import de.lehmannet.om.ui.dialog.FITSImageDialog;
import de.lehmannet.om.ui.dialog.ImageDialog;
import de.lehmannet.om.ui.image.ImageResolver;
import de.lehmannet.om.ui.util.ConfigKey;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.IConfiguration;
import de.lehmannet.om.ui.util.LocaleToolsFactory;
import de.lehmannet.om.ui.util.RelativPath;

public class ImageContainer extends Container implements MouseListener, Scrollable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private final ResourceBundle bundle = LocaleToolsFactory.appInstance().getBundle("ObservationManager",
            Locale.getDefault());

    private static final int THUMBNAIL_SIZE_WIDTH = 96;
    private static final int THUMBNAIL_SIZE_HEIGHT = 96;

    // Use a static image as thumbnail for fits files
    private static final String THUMBNAIL_NAME_FITS = "fits.png";

    private boolean editable = false;

    private final GridBagLayout layout = new GridBagLayout();

    private int numberOfImages = 0;
    private final ImageResolver imageResolver;
    private final ObservationManagerModel model;
    private final IConfiguration configuration;
    private final JFrame om;

    public ImageContainer(List<File> files, JFrame om, IConfiguration configuration, ObservationManagerModel model,
            boolean editable, ImageResolver resolver) {

        this.configuration = configuration;
        this.model = model;
        this.imageResolver = resolver;
        this.editable = editable;
        this.om = om;

        this.createContainer();

        this.addImages(files);

    }

    public void addImagesFromPath(List<String> images) {

        this.addImages(this.model.getFilesFromPath(images));

    }

    public void addImages(List<File> images) {

        if ((images == null) || (images.size() <= 0)) {
            return;
        }

        Image image = null;
        Image thumb = null;
        MyImageLabel label = null;
        String path = null;
        GridBagConstraints c = new GridBagConstraints();
        int i = 0;
        for (; i < images.size(); i++) {

            path = ((File) images.get(i)).getAbsolutePath();
            if (path.startsWith("." + File.separator)) { // Path is relative
                path = this.model.getXMLPathForSchemaElement(this.model.getSelectedElement()) + File.separator + path;
            }

            if (!((path.endsWith(".fits")) || (path.endsWith(".fit")) || (path.endsWith(".fts")))) {
                image = Toolkit.getDefaultToolkit().getImage(path);

                thumb = image.getScaledInstance(ImageContainer.THUMBNAIL_SIZE_WIDTH,
                        ImageContainer.THUMBNAIL_SIZE_HEIGHT, Image.SCALE_FAST);
            } else {
                URL urlThumbFits = this.imageResolver.getImageURL(ImageContainer.THUMBNAIL_NAME_FITS).orElse(null);
                thumb = Toolkit.getDefaultToolkit().getImage(urlThumbFits);
            }

            // Only save thumbnail image, to avoid OutOfMemory exceptions
            label = new MyImageLabel(new ImageIcon(thumb), path);

            label.addMouseListener(this);

            ConstraintsBuilder.buildConstraints(c, i + this.numberOfImages, 0, 1, 1, 1, 1);
            this.layout.setConstraints(label, c);

            this.add(label);

            if (this.editable) {
                String labelText = this.bundle.getString("imageContainer.deleteImage");
                JLabel delete = new JLabel(labelText);

                delete.setForeground(Color.BLUE);

                delete.addMouseListener(this);

                // Add DeleteButton to ImageLabel
                label.addDeleteButton(delete);

                ConstraintsBuilder.buildConstraints(c, i + this.numberOfImages, 1, 1, 1, 1, 1);
                this.layout.setConstraints(delete, c);

                this.add(delete);
            }

        }

        this.numberOfImages += i;

    }

    public List<String> getImages(String homeDir) {

        // No images were set
        if (this.getComponents() == null) {
            return new ArrayList<String>();
        }

        // Find out whether images path should be returned relative or absolute
        boolean relativePath = Boolean.parseBoolean(this.configuration.getConfig(ConfigKey.CONFIG_IMAGESDIR_RELATIVE));
        if (StringUtils.isBlank(homeDir)) {
            relativePath = false;
        }

        // Add each image individually
        Component[] comps = this.getComponents();
        List<String> result = new ArrayList<>(comps.length);
        for (Component comp : comps) {
            MyImageLabel l = null;
            if (comp instanceof MyImageLabel) {
                l = (MyImageLabel) comp;
                if (relativePath) {
                    result.add(RelativPath.getRelativePath(FileSystems.getDefault().getPath(homeDir).toFile(),
                            FileSystems.getDefault().getPath(l.getPath()).toFile()));
                    // relative
                } else {
                    result.add(l.getPath()); // Store path absolute
                }
            }
        }

        return result;

    }

    private void createContainer() {

        this.setLayout(this.layout);

    }

    // -------------
    // MouseListener ----------------------------------------------------------
    // -------------

    @Override
    public void mouseClicked(MouseEvent e) {

        if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
            if (e.getSource() instanceof MyImageLabel) {
                MyImageLabel l = (MyImageLabel) e.getSource();
                if (!(l.getPath().endsWith(".fits")
                    || l.getPath().endsWith(".fit")
                    || l.getPath().endsWith(".fts"))) {
                    new ImageDialog(l.getImage(), this.om);
                } else {
                    new FITSImageDialog(this.om, FileSystems.getDefault().getPath(l.getPath()).toFile());
                }
            } else {
                JLabel b = (JLabel) e.getSource();
                Component[] comps = this.getComponents();
                for (Component comp : comps) {
                    MyImageLabel l = null;
                    if (comp instanceof MyImageLabel) {
                        l = (MyImageLabel) comp;
                        if (l.getDeleteButton().equals(b)) {
                            this.remove(l);
                            this.remove(b);
                            this.repaint();
                        }
                    }
                }
            }
        }

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // do nothing
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // do nothing
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // do nothing
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // do nothing
    }

    // ----------
    // Scrollable -------------------------------------------------------------
    // ----------

    @Override
    public Dimension getPreferredScrollableViewportSize() {

        return new Dimension(this.getWidth(), this.getHeight());

    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 1;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {

        return false;

    }

    @Override
    public boolean getScrollableTracksViewportWidth() {

        return false;

    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {

        return 1;

    }

}

class MyImageLabel extends JLabel {

    /**
     *
     */
    private static final long serialVersionUID = -1770949789891300L;
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