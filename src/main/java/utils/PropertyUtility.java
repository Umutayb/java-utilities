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
    private static Properties properties = new Properties();

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
        properties = getProperties(propertyFileName);
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

    /**
     * Sets the current static properties to the provided Properties object.
     * If a properties object already exists, it will be replaced.
     *
     * @param properties The new Properties object to be set.
     */
    public static void setProperties(Properties properties){
        PropertyUtility.properties = properties;
    }

    /**
     * Retrieves the value of a specific property from the static properties.
     *
     * @param key The key of the property to retrieve.
     * @return The value of the property, or null if the property is not found.
     */
    public static String getProperty(String key){
        return properties.getProperty(key);
    }

    /**
     * Retrieves the value of a specific property from the static properties.
     * If the property is not found, it will return the provided default value.
     *
     * @param key The key of the property to retrieve.
     * @param defaultValue The value to return if the property is not found.
     * @return The value of the property, or the default value if the property is not found.
     */
    public static String getProperty(String key, String defaultValue) {
        String val = getProperty(key);
        return (val == null) ? defaultValue : val;
    }

    /**
     * Retrieves the properties from the specified property file.
     * The property file should be present in the classpath.
     *
     * @param propertyFileName The name of the property file to retrieve the properties from.
     * @return The Properties object containing the properties loaded from the file.
     */
    public Properties getProperties(String propertyFileName){
        Properties properties = new Properties();
        try {
            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream(propertyFileName);
            properties.load(inputStream);
        }
        catch (Exception ignored) {
            log.warning(propertyFileName + " file not found!");
        }
        return properties;
    }

    /**
     * Retrieves the static properties from the PropertyUtility.java class.
     *
     * @return The Properties object containing the properties loaded from the file.
     */
    public static Properties getProperties(){
        return properties;
    }

    /**
     * Adds the contents of one or more Properties objects to the current thread's
     * local properties. If a property already exists, its value will be overwritten
     * by the new value.
     *
     * @param properties The Properties object to be added. This is a varargs parameter,
     * therefore it is possible to pass multiple Properties objects separated by commas.
     */
    public static void addProperties(Properties... properties) {
        for (Properties property : properties)
            PropertyUtility.properties.putAll(property);
    }
}