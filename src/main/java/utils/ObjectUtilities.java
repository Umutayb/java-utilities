package utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ObjectUtilities {

    private final Printer log = new Printer(ObjectUtilities.class);

    public <T> Object getFieldValue(String fieldName, Class<T> inputClass) {
        try {
            Field field = inputClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(inputClass);
        }
        catch (Exception e) {log.new Error(e.fillInStackTrace());}
        return null;
    }

    public Map<String,Object> getFields(Object inputClass){
        Map<String,Object> fieldMap = new HashMap<>();
        try {
            for (Field field:inputClass.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                fieldMap.put(field.getName(),field.get(inputClass));
            }
        }
        catch (IllegalAccessException e) {throw new RuntimeException(e);}
        return fieldMap;
    }
}
