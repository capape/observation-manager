package de.lehmannet.om.ui.util;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerConfig {

    private final static Logger LOGGER_AWT = Logger.getLogger("java.awt");
    private final static Logger LOGGER_SWING = Logger.getLogger("java.swing");
    private final static Logger LOGGER_SUN_AWT = Logger.getLogger("sun.awt");

    public static void initLogs() {
            
            LOGGER_AWT.setLevel(Level.FINE);
            LOGGER_SWING.setLevel(Level.FINE);
            LOGGER_SUN_AWT.setLevel(Level.INFO);
       
       
            // Los handler (manejadores) indican a donde mandar la salida ya sea consola o archivo
            // En este caso ConsoleHandler envia los logs a la consola
            Handler consoleHandler = new ConsoleHandler();
            LOGGER_AWT.addHandler(consoleHandler);
            LOGGER_SWING.addHandler(consoleHandler);
            LOGGER_SUN_AWT.addHandler(consoleHandler);

            // Indicamos a partir de que nivel deseamos mostrar los logs, podemos especificar un nivel en especifico
            // para ignorar informacion que no necesitemos
            consoleHandler.setLevel(Level.ALL);


    }
}