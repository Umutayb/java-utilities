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

    public class Important { public Important(Object text){report(PURPLE, text);}}

    public class Info { public Info(Object text) {report(GRAY, text);}}

    public class Success { public Success(Object text){report(GREEN, text);}}

    public class Warning { public Warning(Object text){report(YELLOW, text);}}

    public class Error { public Error(Object text){report(RED,text);}}

    public void report(String color, Object text){
        if (Boolean.parseBoolean(properties.getProperty("enableLogging")))
            LogUtilities.log.info((String) text);
        else
            log.info(color + text + RESET);
    }
}
