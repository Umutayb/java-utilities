package utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ObjectUtilities {

    private final Printer log = new Printer(ObjectUtilities.class);

    public Object getFieldValue(String fieldName, Object inputClass){
        try {
            Field field = inputClass.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(inputClass);
        }
        catch (IllegalAccessException | NoSuchFieldException exception) {throw new RuntimeException(exception);}
    }

    public Map<String,Object> getFields(Object inputClass){
        Map<String,Object> fieldMap = new HashMap<>();
        try {
            for (Field field:inputClass.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                fieldMap.put(field.getName(),field.get(inputClass));
            }
        }
        catch (IllegalAccessException exception) {throw new RuntimeException(exception);}
        return fieldMap;
    }
}
