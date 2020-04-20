/* ====================================================================
 * /dialog/ImageDialog.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */
package de.lehmannet.om.ui.dialog;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import de.lehmannet.om.ui.navigation.ObservationManager;

public class ImageDialog extends JDialog implements ActionListener, KeyListener, MouseWheelListener {

    private static final long serialVersionUID = -3798904199589986801L;

    private final PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
            .getBundle("ObservationManager", Locale.getDefault());

    private Image origImage = null; // Won't be resized/changed at all
    private Image image = null; // Will be used for display
    private JLabel imageLabel = null;

    public ImageDialog(Image image, ObservationManager om) {

        super(om, true);

        this.image = image;
        this.origImage = image;

        // Wait on image to be loaded
        MediaTracker tracker = new MediaTracker(this);
        tracker.addImage(this.image, 1);
        while (!tracker.checkID(1, true)) {
            try {
                Thread.sleep(15);
            } catch (InterruptedException ie) {
                // Can't do much here, just wait...
            }
        }

        // Get the size of the screen and calculate max size of this popup
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int width = image.getWidth(this) + 40; // Add pixels for scrollbars
        int height = image.getHeight(this) + 50;
        if (width > dim.getWidth()) {
            width = (int) dim.getWidth();
        }
        if (height > (dim.getHeight() - 20)) {
            height = (int) dim.getHeight() - 20;
        }
        this.setSize(width, height);

        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(om);

        this.addKeyListener(this);

        this.initDialog();

        this.setTitle(this.bundle.getString("dialog.image.title"));

        this.setVisible(true);

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

        int notches = e.getWheelRotation();
        if (notches < 0) {
            this.zoomIn();
        } else {
            this.zoomOut();
        }

    }

    @Override
    public void keyPressed(KeyEvent e) {

        if (e.getKeyChar() == '+') {
            this.zoomIn();
        } else if (e.getKeyChar() == '-') {
            this.zoomOut();
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {

        // Do nothing

    }

    @Override
    public void keyTyped(KeyEvent e) {

        // Do nothing

    }

    private void zoomIn() {

        this.image = this.origImage.getScaledInstance(this.image.getWidth(this) << 1, this.image.getHeight(this) << 1,
                Image.SCALE_DEFAULT);
        ImageIcon icon = new ImageIcon(this.image);
        this.imageLabel.setIcon(icon);
        imageLabel.setPreferredSize(new Dimension(image.getWidth(this), image.getHeight(this)));
        this.update(this.getGraphics());

    }

    private void zoomOut() {

        this.image = this.origImage.getScaledInstance(this.image.getWidth(this) >> 1, this.image.getHeight(this) >> 1,
                Image.SCALE_DEFAULT);
        ImageIcon icon = new ImageIcon(this.image);
        this.imageLabel.setIcon(icon);
        imageLabel.setPreferredSize(new Dimension(image.getWidth(this), image.getHeight(this)));
        this.update(this.getGraphics());

    }

    private void initDialog() {

        // Make sure default image size fits into the screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        if (((dim.width - 40) < this.origImage.getWidth(this))
                || ((dim.height - 50) < this.origImage.getHeight(this))) {
            int w = this.origImage.getWidth(this);
            int h = this.origImage.getHeight(this);
            do {
                w = w >> 1;
                h = h >> 1;
            } while ((w > dim.width) || (h > dim.height));

            this.image = this.origImage.getScaledInstance(w, h, Image.SCALE_DEFAULT);
        } else {
            this.image = this.origImage;
        }

        // Maybe not the best way to scroll/show an image, but the easiest
        ImageIcon icon = new ImageIcon(this.image);
        this.imageLabel = new JLabel(icon);
        this.imageLabel.addMouseWheelListener(this);

        JScrollPane scroll = new JScrollPane(imageLabel);
        imageLabel.setPreferredSize(new Dimension(image.getWidth(this), image.getHeight(this)));

        this.getContentPane().add(scroll);

        // Add Menu for close operation
        JMenuBar menuBar = new JMenuBar();

        // ----- File Menu
        JMenu windowMenu = new JMenu(this.bundle.getString("dialog.image.menu.window"));
        windowMenu.setMnemonic('w');
        menuBar.add(windowMenu);

        JMenuItem close = new JMenuItem(this.bundle.getString("dialog.image.menu.window.close"));
        close.setMnemonic('c');
        close.setActionCommand("c");
        close.addActionListener(this);
        windowMenu.add(close);

        // ----- Zoom Menu
        JMenu zoomMenu = new JMenu(this.bundle.getString("dialog.image.menu.zoom"));
        windowMenu.setMnemonic('z');
        menuBar.add(zoomMenu);

        JMenuItem zoomIn = new JMenuItem(this.bundle.getString("dialog.image.menu.zoom.in") + " (+)");
        zoomIn.setMnemonic('i');
        zoomIn.setActionCommand("+");
        zoomIn.addActionListener(this);
        zoomMenu.add(zoomIn);

        JMenuItem zoomOut = new JMenuItem(this.bundle.getString("dialog.image.menu.zoom.out") + " (-)");
        zoomOut.setMnemonic('o');
        zoomOut.setActionCommand("-");
        zoomOut.addActionListener(this);
        zoomMenu.add(zoomOut);

        this.setJMenuBar(menuBar);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() instanceof JMenuItem) {
            if ("-".equals(e.getActionCommand())) {
                this.zoomOut();
            } else if ("+".equals(e.getActionCommand())) {
                this.zoomIn();
            } else if ("c".equals(e.getActionCommand())) {
                this.dispose();
            }
        }

    }

}
