package utils;

import api_assured.exceptions.JavaUtilitiesException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.Assert;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReflectionUtilities {

    static Printer log = new Printer(ReflectionUtilities.class);

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

    public Map<String, Method> getMethods(Object object){
        Map<String, Method> methodMap = new HashMap<>();
        for (Method method: object.getClass().getDeclaredMethods()) {
            method.setAccessible(true);
            methodMap.put(method.getName(),method);
        }
        return methodMap;
    }

    public Method getMethod(String methodName, Object object) throws NoSuchMethodException {
        for (Method method: object.getClass().getDeclaredMethods()) {
            method.setAccessible(true);
            if (method.getName().equals(methodName)) return method;
        }
        throw new NoSuchMethodException (
                "No method named " + methodName + " could be located in class called" + object.getClass().getName()
        );
    }

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

    /**
     * Acquires a json array from a given field
     * @param field target field
     * @return generated array
     * @throws ClassNotFoundException throws if class not found
     * @throws NoSuchFieldException throws if file not found
     */
    private JsonArray getJsonArray(Field field, boolean primitive) throws ClassNotFoundException, NoSuchFieldException {
        JsonArray array = new JsonArray();
        if (!primitive){
            List<JsonObject> list = List.of(
                    getJsonObject(Class.forName(
                                    ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0].getTypeName()
                            ),
                            new JsonObject()
                    )
            );
            for (JsonObject jsonObject : list) array.add(jsonObject);
        }
        else {
            List<String> list = List.of(getTypeName(field));
            for (String jsonObject : list) array.add(jsonObject);
        }
        return array;
    }

    /**
     * Generates a json from a given class
     * @param clazz given class
     * @param json target json
     * @return generated json
     * @param <T> type of the given class
     * @throws NoSuchFieldException throws if file not found
     * @throws ClassNotFoundException throws if json process is interrupted
     * @throws JavaUtilitiesException throws if unable to access class fields
     */
    private <T> JsonObject getJsonObject(Class<T> clazz, JsonObject json) throws NoSuchFieldException, ClassNotFoundException {
        List<Field> fields = List.of(clazz.getFields());
        if (fields.size() == 0) throw new JavaUtilitiesException("Please make sure fields of " + clazz.getSimpleName() + " class are set to public.");
        for (Field field:fields) {
            boolean isMember = field.getType().isMemberClass();
            boolean isList = isOfType(field, "List");

            if (!isList && !isMember)
                json.addProperty(field.getName(), field.getType().getName());
            else if (!isList)
                json.add(field.getName(), getJsonObject(clazz.getField(field.getName()).getType(), new JsonObject()));
            else
                json.add(field.getName(), getJsonArray(field, isPrimitive(field)));

        }
        return json;
    }

    /**
     * Verifies type of given field
     * @param field target field
     * @param expectedType expected field type
     * @return true or false
     */
    private boolean isOfType(Field field, String expectedType){
        return field.getType().getTypeName().contains(expectedType);
    }

    /**
     * Acquires the type of given field
     * @param field target field
     * @return field type
     */
    private String getTypeName(Field field) {
        ParameterizedType type = (ParameterizedType) field.getGenericType();
        return type.getActualTypeArguments()[0].getTypeName();
    }

    /**
     * Checks a given field type is primitive
     * @param field target field
     * @return a boolean
     */
    private boolean isPrimitive(Field field){
        return switch (getTypeName(field)) {
            case "java.lang.Integer",
                    "java.lang.Boolean",
                    "java.lang.Char",
                    "java.lang.Double",
                    "java.lang.Long",
                    "java.lang.Short",
                    "java.lang.Byte",
                    "java.lang.String"
                    -> true;
            default -> false;
        };
    }

    /**
     * Verifies a given list as a member of a class
     * @param clazz target class
     * @param field target field
     * @return a boolean
     * @param <T> type of the given class
     */
    private <T> boolean isMemberList(Class<T> clazz, Field field){
        List<Field> fields = List.of(clazz.getFields());
        return fields.stream().anyMatch(
                subField -> subField.getGenericType().getTypeName().equals(field.getGenericType().getTypeName())
        );
    }
}
