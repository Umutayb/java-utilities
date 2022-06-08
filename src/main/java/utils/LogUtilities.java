package utils;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogUtilities {

    public static Logger log = Logger.getGlobal();

    static {
        try {
            SimpleFormatter formatter = new SimpleFormatter();
            FileHandler file = new FileHandler("src/test/resources/files/Record.log");
            file.setFormatter(formatter);
            log.addHandler(file);
        }
        catch (IOException ignored) {}
    }

    public LogUtilities() {}
}
