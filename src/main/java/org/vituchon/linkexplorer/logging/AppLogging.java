/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vituchon.linkexplorer.logging;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

/**
 *
 * @author Administrador
 */
public final class AppLogging {

    private static final Logger LOGGER = Logger.getLogger(AppLogging.class.getName());

    public static void init() {
        //LogManager logManager = LogManager.getLogManager();
        //loadConfiguration(logManager,"logger.properties");
        setup();
    }

    private static void loadConfiguration(LogManager logManager, String resourceRelativePath) {
        try {
            InputStream is = AppLogging.class.getResourceAsStream(resourceRelativePath);
            logManager.readConfiguration(is);

        } catch (IOException | SecurityException e) {
            LOGGER.log(Level.WARNING, "Couldn't load and apply logging configuration properly...", e);
        }
    }

    private static void setup() {
        AppFormatter appFormater = new AppFormatter();
        Handler fh = null;
        try {
            fh = new FileHandler("linkExplorer.log");
            fh.setFormatter(appFormater);
        } catch (IOException | SecurityException ex) {
            LOGGER.log(Level.SEVERE, "Can not set file hanlder", ex);
        }
        Handler ch = new ConsoleHandler();
        ch.setFormatter(appFormater);
        
        Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        for(Handler handler : handlers) {
            rootLogger.removeHandler(handler);
        }
        rootLogger.addHandler(ch);
        if (fh != null) {
            rootLogger.addHandler(fh);
        }
    }

    private static class AppFormatter extends Formatter {

        private static final DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");

        @Override
        public String format(LogRecord logRecord) {
            StringBuilder b = new StringBuilder();
            b.append(df.format(new Date(logRecord.getMillis())));
            b.append(" [").append(logRecord.getLevel()).append("] - ");
            b.append(logRecord.getLoggerName()).append("#").append(logRecord.getSourceMethodName());
            b.append(" : ");
            b.append(formatMessage(logRecord));
            b.append(System.lineSeparator());
            if (logRecord.getThrown() != null) {
                StringWriter sw = new StringWriter();
                try (PrintWriter pw = new PrintWriter(sw)) {
                    pw.println();
                    logRecord.getThrown().printStackTrace(pw);
                }
                b.append("\n").append(sw.toString());
            }
            return b.toString();
        }
    }
}
