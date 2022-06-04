package utils;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogUtilities {

    public static Logger log = Logger.getGlobal();
    private static final FileHandler file;

    static {
        try {
            SimpleFormatter formatter = new SimpleFormatter();
            file = new FileHandler("src/test/resources/files/Record.log");
            FileUtilities fileUtils = new FileUtilities();
            fileUtils.createIfAbsent("src/test/resources/files/Record.log");
            file.setFormatter(formatter);
            log.addHandler(file);
        }
        catch (IOException e) {throw new RuntimeException(e);}
    }

    public LogUtilities() throws IOException {}
}
