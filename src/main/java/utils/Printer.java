package utils;

import resources.Colors;
import java.util.logging.Level;
import java.util.logging.Logger;
import static utils.FileUtilities.properties;

public class Printer extends Colors {

    private final Logger log;

    public <T> Printer(Class<T> className){log = Logger.getLogger(className.getName());}

    public class Important { public Important(Object text){log(Level.INFO, PURPLE, text);}}

    public class Info { public Info(Object text) {log(Level.INFO, GRAY, text);}}

    public class Success { public Success(Object text){log(Level.INFO, GREEN, text);}}

    public class Warning { public Warning(Object text){log(Level.WARNING, YELLOW, text);}}

    public class Error { public Error(Object text, Exception exception){log(Level.SEVERE, RED, text, exception);}}

    private void log(Level level, String color, Object text){
        text = (color + text + RESET);
        if (Boolean.parseBoolean(properties.getProperty("save-logs", "false")))
            LogUtilities.log.info(text.toString());
        else log.logp(level, log.getName(), getMethod(), text.toString());
    }

    private void log(Level level, String color, Object text, Exception exception){
        text = (color + text + RESET);
        if (Boolean.parseBoolean(properties.getProperty("save-logs", "false")))
            LogUtilities.log.info(text.toString());
        else log.logp(level, log.getName(), getMethod(), text.toString(), exception);
    }

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
