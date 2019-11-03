package de.lehmannet.om.ui.util;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.io.File;

import javax.swing.JFrame;

public class SplashScreen extends JFrame implements Runnable {

	private Image image = null;

	public SplashScreen(String installPath) {

		String imageFile = installPath + File.separatorChar + "images"
				+ File.separatorChar + "splash.png";

		this.image = Toolkit.getDefaultToolkit().getImage(imageFile);

		MediaTracker mt = new MediaTracker(this);
		mt.addImage(this.image, 0);
		try {
			mt.waitForAll();
		} catch (InterruptedException ie) {
			// Interrupted while loading image
			System.err.println("Interrupted while loading SplashScreen");
		}

	}

	public void run() {

		this.setSize(this.image.getWidth(null), this.image.getHeight(null));

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		this.setLocation((screenSize.width / 2)
				- (this.image.getWidth(null) / 2), (screenSize.height / 2)
				- (this.image.getHeight(null) / 2));

		this.setUndecorated(true);
		this.setVisible(true);

		long now = System.currentTimeMillis();
		long then = now + 3 * 1000; // Show screen at least 2 seconds
		while (now < then) {
			try {
				Thread.currentThread().sleep(3 * 1000);
			} catch (InterruptedException ie) {
				now = then; // Break loop
				System.err.println("SplashScreen interrupted");
			}
			now = System.currentTimeMillis();
		}

		this.setVisible(false);
		this.dispose();

	}

	public void paint(Graphics g) {

		g.drawImage(this.image, 0, 0, this);

	}

}