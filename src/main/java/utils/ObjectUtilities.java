package utils;

import java.lang.reflect.Field;

public class ObjectUtilities {

    Printer log = new Printer(ObjectUtilities.class);

    public <T> Object getFieldValue(String fieldName, Class<T> inputClass) {
        try {
            Field field = inputClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(inputClass);
        }
        catch (Exception e) {log.new Error(e.fillInStackTrace());}
        return null;
    }
}
