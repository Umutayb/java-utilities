package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PropertyUtility {
    public static final Properties properties = new Properties();

    /**
     * Loads given properties
     * @param path property directory
     */
    public static void loadProperties(String path){
        try {properties.load(new FileReader(path));}
        catch (FileNotFoundException notFoundException){
            try {
                new File(path).createNewFile();
                properties.load(new FileReader(path));
            }
            catch (IOException e) {throw new RuntimeException(e);}
        }
        catch (Exception exception) {exception.printStackTrace();}
    }
}
