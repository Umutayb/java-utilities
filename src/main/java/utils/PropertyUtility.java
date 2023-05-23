package utils;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * A utility class for loading properties from a file.
 */
public class PropertyUtility {

    private static final Printer log = new Printer(PropertyUtility.class);

    /**
     * The Properties object containing the loaded properties.
     */
    public static final Properties properties = new Properties();

    protected static String getProperty(String key){
        return properties.getProperty(key);
    }

    /**
     * No args constructor.
     */
    public PropertyUtility(){}

    /**
     Initializes a PropertyUtility object by loading properties from the specified property file.
     The property file should be present in the classpath.
     @param propertyFileName The name of the property file to load.
     */
    public PropertyUtility(String propertyFileName){
            try {
                InputStream inputStream = getClass().getClassLoader()
                        .getResourceAsStream(propertyFileName);
                properties.load(inputStream);
            }
            catch (Exception ignored) {
                log.warning(propertyFileName + " file not found!");
            }
    }

    /**
     * Loads the properties from the specified file path.
     * If the file does not exist, it will be created.
     *
     * @param path the file path to load the properties from
     * @throws RuntimeException if there is an error while loading the properties
     */
    public static void loadProperties(String path){
        try {properties.load(new FileReader(path));}
        catch (IOException ioException){log.error(ioException.getMessage(), ioException);}
    }

    public Properties getProperties(){return properties;}
}