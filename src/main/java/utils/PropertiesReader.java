package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {

    private final Printer log = new Printer(StringUtilities.class);

    private Properties properties;

    /**
     * Constructs a new instance of the PropertiesReader class with the specified property file name.
     *
     * @param propertyFileName the name of the property file to read
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

    /**
     * Returns the value of the specified property.
     *
     * @param propertyName the name of the property to retrieve the value for
     * @return the value of the property with the specified name as a String
     */
    public String getProperty(String propertyName) {
        return this.properties.getProperty(propertyName);
    }
}
