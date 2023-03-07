package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {

    private final Printer log = new Printer(StringUtilities.class);

    private Properties properties;

    /**
     * Reads a given property
     * @param propertyFileName property directory
     */
    public PropertiesReader(String propertyFileName){
        try {
            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream(propertyFileName);
            this.properties = new Properties();
            this.properties.load(inputStream);
        }
        catch (IOException exception) {log.new Error(exception.getMessage(), exception);}
    }

    public String getProperty(String propertyName) {
        return this.properties.getProperty(propertyName);
    }
}
