package de.lehmannet.om.ui.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.SplashScreen;
import java.io.IOException;
import java.net.URL;

public class SplashScreenWithText {

    private SplashScreen splash;
    private Graphics2D splashGraphics;
    private final Font font = new Font("Helvetica", Font.BOLD, 9);

    private final int TEXT_POSITION_X = 250;
    private final int TEXT_POSITION_Y = 200;
    private final int TEXT_HEIGHT_BOX = 20;

    private final int VERSION_TEXT_POSITION_X = 250;
    private final int VERSION_TEXT_POSITION_Y = 180;
    private final int VERSION_TEXT_HEIGHT_BOX = 20;

    private final long millisecondsToWait;
    private final boolean nightMode;
    private final URL image;

    private SplashScreenWithText(Builder builder) {
        this.nightMode = builder.nightMode;
        this.image = builder.image;
        this.millisecondsToWait = builder.millisecondsToWait;

    }

    public void showSplash() {

        if (nightMode) {
            return;
        }

        splash = SplashScreen.getSplashScreen();
        if (image != null) {
            try {
                splash.setImageURL(image);
            } catch (NullPointerException | IllegalStateException | IOException e) {                
                return;
            }
        }

        if (splash != null && splash.isVisible()) {

            splashGraphics = splash.createGraphics();
            splashGraphics.setColor(Color.BLACK);
            splashGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

            splashGraphics.setFont(font);

            splash.update();

            waitForNextMessage();
        }
    }

    public  void updateText(String text) {
        
        if (nightMode) {
            return;
        }

        splash = SplashScreen.getSplashScreen();

        if (splash != null && splash.isVisible()) {

            resetGraphicText(TEXT_POSITION_X, TEXT_POSITION_Y, TEXT_HEIGHT_BOX);
            splashGraphics.drawString(text, TEXT_POSITION_X, TEXT_POSITION_Y);

            splash.update();

        }
    }

    

    public  void updateTextVersion(String text) {


        if (nightMode) {
            return;
        }

        splash = SplashScreen.getSplashScreen();

        if (splash != null && splash.isVisible()) {

            resetGraphicText(VERSION_TEXT_POSITION_X, VERSION_TEXT_POSITION_Y, VERSION_TEXT_HEIGHT_BOX);
            splashGraphics.drawString(text, VERSION_TEXT_POSITION_X, VERSION_TEXT_POSITION_Y);

            splash.update();

        }
    }

    private  void resetGraphicText(int x, int y, int height) {

        splash = SplashScreen.getSplashScreen();
        if (splash != null && splash.isVisible()) {
            splashGraphics = splash.createGraphics();
        }
        splashGraphics.setBackground(Color.WHITE);
        splashGraphics.clearRect(x, y - (height / 2), Double.valueOf(splash.getSize().getWidth()).intValue() - x,
                height);
        splashGraphics.setColor(Color.BLACK);
        splashGraphics.setFont(font);
        waitForNextMessage();
    }

    private  void waitForNextMessage() {
        try {
            Thread.sleep(millisecondsToWait);
        } catch (InterruptedException e) {

        }
    }


    public static class Builder {

        private boolean nightMode;
        private URL image;
        private long millisecondsToWait = 500L;

        public Builder(boolean nightMode) {
            this.nightMode = nightMode;
        }

        public Builder image(URL image) {
            this.image = image;
            return this;
        }

        public Builder millisecondsToWait(long value) {
            if (value > millisecondsToWait) {
                this.millisecondsToWait = value;
            }
            return this;
        }

        public SplashScreenWithText build() {
           return new SplashScreenWithText(this);
        }
    }
}