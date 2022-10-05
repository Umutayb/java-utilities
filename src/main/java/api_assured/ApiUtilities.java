package api_assured;

import utils.FileUtilities;
import utils.Printer;

import java.util.Properties;

public abstract class ApiUtilities extends Caller {
    public static Properties properties;
    Printer log = new Printer(ApiUtilities.class);

    public ApiUtilities(){properties = FileUtilities.properties;}
}
