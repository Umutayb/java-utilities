package utils;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static utils.StringUtilities.Color.*;

public class Printer {

    StringUtilities strUtils = new StringUtilities();

    public static Properties properties = PropertyUtility.properties;

    private final Logger log;

    public <T> Printer(Class<T> className){log = Logger.getLogger(className.getName());}

    public class Plain { public Plain(CharSequence output) {log(Level.INFO, String.valueOf(output));}}

    public class Important {
        public Important(CharSequence output){log(Level.INFO, strUtils.highlighted(PURPLE, output));}
    }

    public class Info {
        public Info(CharSequence output) {log(Level.INFO, strUtils.highlighted(GRAY, output));}
    }

    public class Success {
        public Success(CharSequence output){log(Level.INFO, strUtils.highlighted(GREEN, output));}
    }

    public class Warning {
        public Warning(CharSequence output){log(Level.WARNING, strUtils.highlighted(YELLOW, output));}
    }

    public class Error {
        public Error(CharSequence output, Exception exception){log(strUtils.highlighted(RED, output), exception);}
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
