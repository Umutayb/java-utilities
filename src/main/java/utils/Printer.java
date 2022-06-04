package utils;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import java.io.File;
import java.io.FileReader;
import java.util.Properties;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import resources.Colors;

public class Printer extends Colors {

    private final Log log;
    private final Logger logger = Logger.getGlobal();
    private final Properties properties = new Properties();

    public <T> Printer(Class<T> className){
        log = LogFactory.getLog(className);
        SimpleFormatter formatter = new SimpleFormatter();
        try {
            properties.load(new FileReader("src/test/resources/test.properties"));
            FileUtilities fileUtils = new FileUtilities();
            fileUtils.createIfAbsent("src/test/resources/files/Record.log");
            FileHandler file = new FileHandler("target/Record.log");
            file.setFormatter(formatter);
            logger.addHandler(file);
        }
        catch (IOException e) {e.printStackTrace();}
    }

    public class Important { public Important(Object text){report(PURPLE, text);}}

    public class Info { public Info(Object text) {report(GRAY, text);}}

    public class Success { public Success(Object text){report(GREEN, text);}}

    public class Warning { public Warning(Object text){report(YELLOW, text);}}

    public class Error { public Error(Object text){report(RED,text);}}

    public void report(String color, Object text){
        if (Boolean.parseBoolean(properties.getProperty("enableLogging")))
            logger.info((String) text);
        else
            log.info(color + text + RESET);
    }
}
