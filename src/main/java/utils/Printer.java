package utils;

import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.TakesScreenshot;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.apache.commons.logging.Log;
import java.util.Properties;
import java.io.IOException;
import java.io.FileReader;
import resources.Colors;
import org.junit.Assert;
import java.io.File;

public class Printer extends Colors {

    private final Log log;

    NumericUtilities numeric = new NumericUtilities();

    public <T> Printer(Class<T> className){log = LogFactory.getLog(className);}

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
        try {properties.load(new FileReader("src/test/resources/test.properties"));}
        catch (IOException e) {e.printStackTrace();}
        if (Boolean.parseBoolean(properties.getProperty("enableLogging")))
            log.info(text);
        else
            System.out.println(text);
    }
}
