package utils;

import org.junit.Assert;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectUtilities {

    static Printer log = new Printer(ObjectUtilities.class);

    /**
     * Compares two given objects matches, not throws an exception if not
     * @param expected expected object
     * @param actual actual object
     * @param exceptions exceptions
     */
    public void compareObjects(Object expected, Object actual, String... exceptions){

        Map<String, Object> expectedMap = getFields(expected);
        Map<String, Object> actualMap = getFields(actual);

        for (String fieldName:expectedMap.keySet()) {
            if (Arrays.stream(exceptions).noneMatch(exception -> exception.equals(fieldName))){
                Assert.assertEquals("Values of the '" + fieldName + "' fields do not match!",
                        expectedMap.get(fieldName),
                        actualMap.get(fieldName)
                );
                log.new Success("Match: " + fieldName + " -> " + expectedMap.get(fieldName));
            }
        }
    }

    /**
     * Compares two given objects matches, throws an exception if not
     * @param expected expected object
     * @param actual actual object
     * @param exceptions exceptions
     */
    public boolean objectsMatch(Object expected, Object actual, String... exceptions){

        Map<String, Object> expectedMap = getFields(expected);
        Map<String, Object> actualMap = getFields(actual);

        try {
            for (String fieldName:expectedMap.keySet()) {
                if (Arrays.stream(exceptions).noneMatch(exception -> exception.equals(fieldName))){
                    Assert.assertEquals("Values of the '" + fieldName + "' fields do not match!",
                            expectedMap.get(fieldName),
                            actualMap.get(fieldName)
                    );
                    log.new Success("Match: " + fieldName + " -> " + expectedMap.get(fieldName));
                }
            }
        }
        catch (AssertionError error){
            log.new Warning(error.getMessage());
            return false;
        }

        log.new Success("All fields match!");

        return true;
    }

    /**
     *
     * @param object
     * @return
     */
    public Map<String, Method> getMethods(Object object){
        Map<String, Method> methodMap = new HashMap<>();
        for (Method method: object.getClass().getDeclaredMethods()) {
            method.setAccessible(true);
            methodMap.put(method.getName(),method);
        }
        return methodMap;
    }

    /**
     *
     * @param methodName
     * @param object
     * @return
     * @throws NoSuchMethodException
     */
    public Method getMethod(String methodName, Object object) throws NoSuchMethodException {
        for (Method method: object.getClass().getDeclaredMethods()) {
            method.setAccessible(true);
            if (method.getName().equals(methodName)) return method;
        }
        throw new NoSuchMethodException (
                "No method named " + methodName + " could be located in class called" + object.getClass().getName()
        );
    }

    /**
     * Acquires a specified field value from a given class
     * @param fieldName field name
     * @param inputClass target class
     * @return returns the field value
     * @param <T> -
     */
    public <T> Object getFieldValue(String fieldName, Class<T> inputClass){
        try {
            Field field = inputClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(inputClass);
        }
        catch (IllegalAccessException | NoSuchFieldException exception) {throw new RuntimeException(exception);}
    }

    /**
     *
     * @param fieldName
     * @param inputClass
     * @return
     */
    public Object getField(String fieldName, Object inputClass){
        try {
            Field field = inputClass.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(inputClass);
        }
        catch (IllegalAccessException | NoSuchFieldException exception) {throw new RuntimeException(exception);}
    }

    /**
     *
     * @param inputClass
     * @return
     */
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

    /**
     *
     * @param object
     */
    public void printObjectFields(Object object){
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

    /**
     *
     * @param object
     */
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
