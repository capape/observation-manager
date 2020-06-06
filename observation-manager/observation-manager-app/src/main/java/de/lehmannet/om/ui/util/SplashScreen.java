package de.lehmannet.om.ui.util;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.io.File;
import java.net.URL;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;

import javax.swing.JFrame;

import de.lehmannet.om.ui.image.ImageResolver;

public class SplashScreen extends JFrame implements Runnable {

    private static final String SPLASH_PNG = "splash.png";
    private static final long serialVersionUID = 1L;
    private Image image;
    private final ImageResolver imageResolver;

    public SplashScreen(ImageResolver resolver) {

        this.imageResolver = resolver;
        this.init();

    }

    private void init() {

        this.imageResolver.getImageURL(SPLASH_PNG).ifPresent(

                imageFile -> {
                    this.image = Toolkit.getDefaultToolkit().getImage(imageFile);

                    MediaTracker mt = new MediaTracker(this);
                    mt.addImage(this.image, 0);
                    try {
                        mt.waitForAll();
                    } catch (InterruptedException ie) {
                        // Interrupted while loading image
                        System.err.println("Interrupted while loading SplashScreen");
                    }
                });
    }

    @Override
    public void run() {

        this.setSize(this.image.getWidth(null), this.image.getHeight(null));

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();

        int x = (width / 2) - (this.image.getWidth(null) / 2);
        int y = (height / 2) - (this.image.getHeight(null) / 2);
        this.setLocation(x, y);
        this.setUndecorated(true);
        this.setVisible(true);

        long now = System.currentTimeMillis();
        long then = now + 3 * 1000; // Show screen at least 2 seconds
        while (now < then) {
            try {
                Thread.currentThread();
                Thread.sleep(3 * 1000);
            } catch (InterruptedException ie) {
                now = then; // Break loop
                System.err.println("SplashScreen interrupted");
            }
            now = System.currentTimeMillis();
        }

        this.setVisible(false);
        this.dispose();

    }

    @Override
    public void paint(Graphics g) {

        g.drawImage(this.image, 0, 0, this);

    }

}