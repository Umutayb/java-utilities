package utils;

import resources.Colors;

import java.util.Properties;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Printer extends Colors {

    public static Properties properties = PropertyUtility.properties;

    private final Logger log;

    public <T> Printer(Class<T> className){log = Logger.getLogger(className.getName());}

    public class Plain { public Plain(CharSequence output) {log(Level.INFO, String.valueOf(output));}}

    public class Important {
        StringJoiner colorCode = new StringJoiner("", PURPLE, RESET);

        public Important(CharSequence output){log(Level.INFO, String.valueOf(colorCode.add(output)));}
    }

    public class Info { public Info(CharSequence output) {
        StringJoiner colorCode = new StringJoiner("", GRAY, RESET);

        log(Level.INFO, String.valueOf(colorCode.add(output)));}
    }

    public class Success {
        StringJoiner colorCode = new StringJoiner("", GREEN, RESET);
        public Success(CharSequence output){log(Level.INFO, String.valueOf(colorCode.add(output)));}
    }

    public class Warning {
        StringJoiner colorCode = new StringJoiner("", YELLOW, RESET);
        public Warning(CharSequence output){log(Level.WARNING, String.valueOf(colorCode.add(output)));}
    }

    public class Error {
        StringJoiner colorCode = new StringJoiner("", RED, RESET);
        public Error(CharSequence output, Exception exception){log(String.valueOf(colorCode.add(output)), exception);}
    }

    private void log(Level level, String output){
        if (Boolean.parseBoolean(properties.getProperty("save-logs", "false")))
            LogUtilities.log.info(output);
        else log.logp(level, log.getName(), getMethod(), output);
    }

    private void log(String output, Exception exception){
        if (Boolean.parseBoolean(properties.getProperty("save-logs", "false")))
            LogUtilities.log.info(output);
        else log.logp(Level.SEVERE, log.getName(), getMethod(), output, exception);
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
