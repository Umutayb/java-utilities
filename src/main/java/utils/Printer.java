package utils;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import java.io.FileReader;
import java.util.Properties;
import java.io.IOException;
import resources.Colors;

public class Printer extends Colors {

    private final Log log;
    private final Properties properties = new Properties();

    public <T> Printer(Class<T> className){
        log = LogFactory.getLog(className);
        try {properties.load(new FileReader("src/test/resources/test.properties"));}
        catch (IOException e) {e.printStackTrace();}
    }

    public class Important { public Important(Object text){log(PURPLE, text);}}

    public class Info { public Info(Object text) {log(GRAY, text);}}

    public class Success { public Success(Object text){log(GREEN, text);}}

    public class Warning { public Warning(Object text){log(YELLOW, text);}}

    public class Error { public Error(Object text){log(RED,text);}}

    public void log(String color, Object text){
        if (Boolean.parseBoolean(properties.getProperty("enableLogging")))
            LogUtilities.log.info(text.toString());
        else
            log.info(color + text + RESET);
    }
}
