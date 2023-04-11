package utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogUtilities {

    public static Logger log = Logger.getGlobal();

    /**
     * Sets up a logging file with a SimpleFormatter and adds it to the logger's handlers.
     *
     * This method is typically called during initialization of the logger.
     */
    static {
        try {
            SimpleFormatter formatter = new SimpleFormatter();
            FileHandler file = new FileHandler("src/test/resources/files/Record.log");
            file.setFormatter(formatter);
            log.addHandler(file);
        }
        catch (IOException ignored) {}
    }

    /**
     * returns log level from a string (java.util.logging.Level)
     *
     * @param logLevel desired log level
     * @return returns log level
     */
    public java.util.logging.Level getLevel(String logLevel){
        return java.util.logging.Level.parse(Objects.requireNonNull(Arrays.stream(java.util.logging.Level.class.getFields()).filter(field -> {
            field.setAccessible(true);
            String fieldName = field.getName();
            return fieldName.equalsIgnoreCase(logLevel);
        }).findAny().orElse(null)).getName());
    }

    /**
     * Constructs a new instance of the LogUtilities class.
     *
     * This constructor is typically used to create a new instance of the LogUtilities class.
     */
    public LogUtilities() {}
}
