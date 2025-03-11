package utils;

import properties.PropertyUtilities;

import java.util.logging.Level;
import java.util.logging.Logger;

import static utils.StringUtilities.Color.*;
import static utils.StringUtilities.highlighted;

/**
 * The Printer class is used to log messages with different levels of importance and formatting.
 * It uses a Logger to log the messages and includes options to save the logs in a file.
 * The class also includes utility methods for highlighting text with different colors.
 */
public class Printer {

    /**
     * The Logger instance used for logging messages.
     */
    private final Logger log;

    /**
     * Constructs a new Printer instance with the provided class name.
     *
     * @param className The name of the class to associate with the Logger.
     */
    public <T> Printer(Class<T> className){
        log = Logger.getLogger(className.getName());
    }

    public void plain(CharSequence output){
        log(Level.INFO, String.valueOf(output));
    }

    /**
     * Logs a message at the INFO level with the given output using the default logger and highlights the message with the specified color.
     *
     * @param output the message to be logged
     */
    public void info(CharSequence output){
        log(Level.INFO, highlighted(GRAY, output));
    }

    /**
     * Logs an important message at the INFO level with the given output using the default logger and highlights the message with the specified color.
     *
     * @param output the message to be logged
     */
    public void important(CharSequence output){
        log(Level.INFO, highlighted(PURPLE, output));
    }

    /**
     * Logs a success message at the INFO level with the given output using the default logger and highlights the message with the specified color.
     *
     * @param output the message to be logged
     */
    public void success(CharSequence output){
        log(Level.INFO, highlighted(GREEN, output));
    }

    /**
     * Logs a warning message at the WARNING level with the given output using the default logger and highlights the message with the specified color.
     *
     * @param output the message to be logged
     */
    public void warning(CharSequence output){
        log(Level.WARNING, highlighted(YELLOW, output));
    }

    /**
     * Logs an error message at the ERROR level with the given output and exception using the default logger and highlights the message with the specified color.
     *
     * @param output the message to be logged
     * @param exception the exception to be logged
     */
    public void error(CharSequence output, Exception exception){
        log(highlighted(RED, output), exception);
    }

    /**
     * Logs a message with the provided level and output.
     * If the "save-logs" property is set to true, the message will also be saved in a log file.
     *
     * @param level The level of the message (e.g. INFO, WARNING, SEVERE).
     * @param output The message to log.
     */
    private void log(Level level, String output){
        if (Boolean.parseBoolean(PropertyUtilities.getProperty("save-logs", "false")))
            LogUtilities.log.info(output);
        else log.logp(level, log.getName(), getMethod(), output);
    }

    /**
     * Logs a message with the SEVERE level, highlighted output, and an exception.
     * If the "save-logs" property is set to true, the message will also be saved in a log file.
     *
     * @param output The message to log.
     * @param exception The exception to include in the log.
     */
    private void log(String output, Exception exception){
        if (Boolean.parseBoolean(PropertyUtilities.getProperty("save-logs", "false")))
            LogUtilities.log.info(output);
        else log.logp(Level.SEVERE, log.getName(), getMethod(), output, exception);
    }

    /**
     * Returns the name of the method that called the log method.
     *
     * @return The name of the calling method.
     */
    private String getMethod(){
        Throwable dummyException = new Throwable();
        StackTraceElement[] stackTrace = dummyException.getStackTrace();
        // LOGGING-132: use the provided logger name instead of the class name
        String method = stackTrace[0].getMethodName();
        // Caller will be the third element
        if( stackTrace.length > 2 ) {
            StackTraceElement caller = stackTrace[3];
            method = caller.getMethodName();
        }
        return method;
    }
}
