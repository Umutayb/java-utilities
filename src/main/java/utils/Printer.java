package utils;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import resources.Colors;
import static utils.FileUtilities.properties;

public class Printer extends Colors {

    private final Log log;

    public <T> Printer(Class<T> className){log = LogFactory.getLog(className);}

    public class Important { public Important(Object text){log(PURPLE, text);}}

    public class Info { public Info(Object text) {log(GRAY, text);}}

    public class Success { public Success(Object text){log(GREEN, text);}}

    public class Warning { public Warning(Object text){log(YELLOW, text);}}

    public class Error { public Error(Object text){log(RED,text);}}

    public void log(String color, Object text){
        if (Boolean.parseBoolean(properties.getProperty("save-logs")))
            LogUtilities.log.info(text.toString());
        else
            log.info(color + text + RESET);
    }
}
