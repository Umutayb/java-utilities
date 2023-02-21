package api_assured;

import utils.*;

import java.util.Properties;

public abstract class ApiUtilities extends Caller {
    public static Properties properties = new Properties();
    public StringUtilities strUtils = new StringUtilities();
    public ObjectUtilities objectUtils = new ObjectUtilities();
    public NumericUtilities numUtils = new NumericUtilities();
    public Printer log = new Printer(this.getClass());

    public ApiUtilities(){properties = PropertyUtility.properties;}
}
