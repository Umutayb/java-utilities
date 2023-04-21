package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * A utility class for loading properties from a file.
 */
public class PropertyUtility {

    /**
     * The Properties object containing the loaded properties.
     */
    public static final Properties properties = new Properties();

    private static FileUtilities fileUtilities = new FileUtilities();

    /**
     * Loads the properties from the specified file path.
     * If the file does not exist, it will be created.
     *
     * @param path the file path to load the properties from
     * @throws RuntimeException if there is an error while loading the properties
     */
    public static void loadProperties(String path){
        try {
            properties.load(new FileReader(path));
        } catch (FileNotFoundException notFoundException){
            try {
                fileUtilities.createIfAbsent(path);
                properties.load(new FileReader(path));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    protected static String getProperty(String key){
        return properties.getProperty(key);
    }
}