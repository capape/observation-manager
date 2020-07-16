package de.lehmannet.om.ui.util;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.SplashScreen;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

public class SplashScreenWithText {

    static SplashScreen splash;
    static Graphics2D splashGraphics; 
//    static Rectangle2D.Double splashTextArea; 
//    static Rectangle2D.Double splashProgressArea; 
    static Font font = new Font ("Helvetica", Font.BOLD, 9);

    private static final int TEXT_POSITION_X = 250;
    private static final int TEXT_POSITION_Y = 200;
    private static final int TEXT_HEIGHT_BOX = 20;

    private static final int VERSION_TEXT_POSITION_X = 250;
    private static final int VERSION_TEXT_POSITION_Y = 180;
    private static final int VERSION_TEXT_HEIGHT_BOX = 20;

    private static final long MILLISECONDS_TO_WAIT=0L;


    public static void showSplash() {

        splash = SplashScreen.getSplashScreen();
        if (splash != null && splash.isVisible()) {

            splashGraphics = splash.createGraphics();
            splashGraphics.setColor(Color.BLACK);      
            splashGraphics.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

      
            splashGraphics.setFont(font);
          
            splash.update();

            waitForNextMessage();
        }
    }

    public static void updateText(String text) {
        splash = SplashScreen.getSplashScreen();

        if (splash != null && splash.isVisible()) {

            resetGraphicText(TEXT_POSITION_X, TEXT_POSITION_Y, TEXT_HEIGHT_BOX);
            splashGraphics.drawString(text, TEXT_POSITION_X, TEXT_POSITION_Y);

            splash.update();

        }
    }   

    public static void updateTextVersion(String text) {
        splash = SplashScreen.getSplashScreen();

        if (splash != null && splash.isVisible()) {

            resetGraphicText(VERSION_TEXT_POSITION_X, VERSION_TEXT_POSITION_Y, VERSION_TEXT_HEIGHT_BOX);
            splashGraphics.drawString(text, VERSION_TEXT_POSITION_X, VERSION_TEXT_POSITION_Y);

            splash.update();

        }
    }   


    private static void resetGraphicText(int x, int y, int height) {
        
        splash = SplashScreen.getSplashScreen();
        if (splash != null && splash.isVisible()) {
            splashGraphics = splash.createGraphics();
        }
        splashGraphics.setBackground(Color.WHITE);                    
        splashGraphics.clearRect(x, y - (height / 2), Double.valueOf(splash.getSize().getWidth()).intValue() - x, height);        
        splashGraphics.setColor(Color.BLACK);            
        splashGraphics.setFont(font);
        waitForNextMessage();
    }


    private static void waitForNextMessage() {
        try {
            Thread.sleep(MILLISECONDS_TO_WAIT);
        } catch(InterruptedException e)  {

        }
    }

}