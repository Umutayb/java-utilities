package utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectUtilities {

    static Printer log = new Printer(ObjectUtilities.class);

    public <T> Object getFieldValue(String fieldName, Class<T> inputClass){
        try {
            Field field = inputClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(inputClass);
        }
        catch (IllegalAccessException | NoSuchFieldException exception) {throw new RuntimeException(exception);}
    }

    public Object getField(String fieldName, Object inputClass){
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

    public static void printObjectFields(Object object){
        List<Field> fields = List.of(object.getClass().getDeclaredFields());
        StringBuilder output = new StringBuilder();
        try {
            for (Field field:fields){
                field.setAccessible(true);
                String fieldName = new StringUtilities().firstLetterCapped(field.getName());
                output.append("\n").append(fieldName).append(" : ").append(field.get(object));
            }
            log.new Important("\nFields: " + output);
        }
        catch (IllegalAccessException e) {throw new RuntimeException(e);}
    }

    public void printModelGetterValues(Object object){
        Method[] methods = object.getClass().getDeclaredMethods();
        StringBuilder output = new StringBuilder();
        try {
            for (Method method:methods)
                if (method.getName().contains("get")){
                    String fieldName = new StringUtilities().firstLetterCapped(method.getName().replaceAll("get", ""));
                    output.append("\n").append(fieldName).append(" : ").append(method.invoke(object));
                }
            log.new Important("\nFields: " + output);
        }
        catch (InvocationTargetException | IllegalAccessException e) {throw new RuntimeException(e);}
    }
}
