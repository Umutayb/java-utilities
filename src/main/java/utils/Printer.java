package utils;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import java.util.Properties;
import java.io.IOException;
import java.io.FileReader;
import resources.Colors;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Printer extends Colors {

    private final Log log;

    private final Logger logger;
    private final SimpleFormatter formatter = new SimpleFormatter();
    private final FileUtilities fileUtils = new FileUtilities();

    public <T> Printer(Class<T> className){
        log = LogFactory.getLog(className);
        logger = Logger.getLogger(className.getName());
    }

    public class Important {
        public Important(Object text){report(PURPLE + text + RESET);}
    }

    public class Info {
        public Info(Object text) {report(GRAY + text + RESET);}
    }

    public class Success {
        public Success(Object text){report(GREEN + text + RESET);}
    }

    public class Warning {
        public Warning(Object text){report(YELLOW + text + RESET);}
    }

    public class Error {
        public Error(Object text){report(RED + text + RESET);}
    }

    public void report(Object text){
        Properties properties = new Properties();
        try {
            properties.load(new FileReader("src/test/resources/test.properties"));
            FileHandler file = new FileHandler("target/Record.log");
            logger.addHandler(file);
            file.setFormatter(formatter);
            logger.info((String) text);
        }
        catch (IOException e) {e.printStackTrace();}
        if (Boolean.parseBoolean(properties.getProperty("enableLogging")))
            log.info(text);
        else
            System.out.println(text);
    }
}
