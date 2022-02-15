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

    public void captureScreen(String specName, RemoteWebDriver driver) {
        try {
             new Info("Capturing page");

            String name = specName+"#"+numeric.randomNumber(1,10000)+".jpg";
            File sourceFile = new File("Screenshots");
            File fileDestination  = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(fileDestination, new File(sourceFile, name));

            new Info("Screenshot saved as; "+name+" at the \"Screenshots\" file.");

        }catch (Exception gamma){
            Assert.fail(YELLOW+"Could not capture screen"+RED+"\n\t"+gamma+RESET);
            driver.quit();
        }
    }
}
