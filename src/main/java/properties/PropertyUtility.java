package properties;

import utils.Printer;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static java.util.Objects.requireNonNull;
import static properties.Constants.UTILITY_PROPERTIES_FILE_NAME;

/**
 * A utility class for loading properties from a file.
 */
public abstract class PropertyUtility {

    private static final Printer log = new Printer(PropertyUtility.class);

    /**
     * The default Properties object containing the loaded default properties.
     */
    public static final Properties properties = new Properties();

    /**
     * Loads the default properties from the default property file.
     *
     * @return The Properties object containing the loaded default properties.
     */
    public static Properties loadPropertyFile(String propertyFileName) {
        try (InputStream inputStream = PropertyUtility.class.getResourceAsStream("/" + propertyFileName)) {
            if (inputStream != null)
                properties.load(inputStream);
            else
                log.warning(propertyFileName + " file not found!");
        }
        catch (IOException | NullPointerException e) {
            log.error(propertyFileName + " could not be loaded", e);
        }
        return properties;
    }

    /**
     * Retrieves the value of a specific property from the default properties.
     *
     * @param key The key of the property to retrieve.
     * @return The value of the property, or null if the property is not found.
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Retrieves the value of a specific property from the default properties.
     * If the property is not found, it will return the provided default value.
     *
     * @param key           The key of the property to retrieve.
     * @param defaultValue  The value to return if the property is not found.
     * @return The value of the property, or the default value if the property is not found.
     */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Retrieves properties from the default property file.
     *
     * @return A Map containing properties loaded from the default property file.
     */
    public static Map<String, String> fromPropertyFile() {
        return fromPropertyFile(UTILITY_PROPERTIES_FILE_NAME);
    }

    /**
     * Loads properties from a specified file and creates a Map with key-value pairs.
     * If an IOException occurs while attempting to load properties from the file,
     * it falls back to an alternative method of loading the properties.
     *
     * @param propertyFile The path to the property file to be loaded.
     * @return A Map containing key-value pairs from the loaded properties file.
     * @throws IllegalArgumentException If the provided propertyFile is null or empty.
     * @throws RuntimeException If an error occurs during the loading of properties.
     *                          This can be an IOException or any other runtime exception.
     *                          The specific exception details are logged for further investigation.
     * @see UtilityPropertiesMap#create(Properties) UtilityPropertiesMap.create(Properties)
     */
    public static Map<String, String> fromPropertyFile(String propertyFile) {
        try {
            loadPropertiesFromPath(propertyFile);
        } catch (IOException e) {
            // If IOException occurs, fall back to an alternative method
            loadPropertyFile(propertyFile);
        }
        return UtilityPropertiesMap.create(properties);
    }

    /**
     * Retrieves properties from one or more property files and returns them as a merged Map.
     *
     * @param propertyNames An array of property file names or paths to be loaded and merged.
     * @return A Map containing key-value pairs from the loaded properties files.
     * @throws IllegalArgumentException If the provided array of property names is null or empty.
     * @throws RuntimeException If an error occurs during the loading of properties.
     *                          This can be an IOException or any other runtime exception.
     *                          The specific exception details are logged for further investigation.
     * @see #fromPropertyFile(String) fromPropertyFile(String)
     */
    public static Map<String, String> getProperties(String... propertyNames) {
        Map<String, String> properties = new HashMap<>();
        for (String propertyName : propertyNames) {
            properties.putAll(fromPropertyFile(propertyName));
        }
        return properties;
    }

    /**
     * Retrieves PropertyUtility properties.
     *
     * @return A Map containing properties retrieved from the environment.
     */
    public static Properties getProperties(){
        return properties;
    }

    /**
     * Retrieves properties from the environment.
     *
     * @return A Map containing properties retrieved from the environment.
     */
    public static Map<String, String> fromEnvironment() {
        return new UtilityPropertiesMap(System.getenv());
    }

    /**
     * Retrieves properties from system properties.
     *
     * @return A Map containing properties retrieved from system properties.
     */
    public static Map<String, String> fromSystemProperties() {
        Properties systemProperties = System.getProperties();
        return UtilityPropertiesMap.create(systemProperties);
    }

    /**
     * Loads properties from a specified property file.
     *
     */
    public static void loadPropertiesFromPath(String path) throws IOException {
        properties.load(new FileReader(path));
    }
}
