package utils;

import api_assured.exceptions.JavaUtilitiesException;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Assert;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

public class ReflectionUtilities {

    static Printer log = new Printer(ReflectionUtilities.class);
    ObjectMapper mapper = new ObjectMapper();

    public ReflectionUtilities(){
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.NONE);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    /**
     * Compares two objects and throws an AssertionError if they are not equal.
     * This method is useful for testing purposes.
     *
     * @param expected the expected object
     * @param actual the actual object
     * @param exceptions the list of exceptions to ignore during comparison
     * @throws AssertionError if the objects are not equal
     */
    public <T> void compareObjects(T expected, T actual, String... exceptions){
        try {
            String expectedString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(expected);
            String actualString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(actual);
            JsonObject expectedJson = JsonParser.parseString(expectedString).getAsJsonObject();
            JsonObject actualJson = JsonParser.parseString(actualString).getAsJsonObject();
            compareJson(expectedJson, actualJson, exceptions);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Compares two JSON objects and throws an assertion error if they do not match.
     *
     * @param expectedJson The expected JSON object.
     * @param actualJson The actual JSON object.
     * @param exceptions Optional field names to exclude from comparison.
     *
     * @throws AssertionError if the JSON objects do not match.
     */
    public void compareJson(JsonObject expectedJson, JsonObject actualJson, String... exceptions){
        Set<String> keySet = expectedJson.keySet().stream().filter(key -> !Arrays.asList(exceptions).contains(key)).collect(Collectors.toSet());
        for (String fieldName:keySet) {
            if (Arrays.stream(exceptions).noneMatch(exception -> exception.equals(fieldName))){
                boolean isObject = expectedJson.get(fieldName).isJsonObject();
                boolean isArray = expectedJson.get(fieldName).isJsonArray();
                boolean isPrimitive = expectedJson.get(fieldName).isJsonPrimitive();

                if (isObject){
                    compareJson(
                            expectedJson.get(fieldName).getAsJsonObject(),
                            actualJson.get(fieldName).getAsJsonObject(),
                            exceptions
                    );
                }
                else if (isArray){
                    compareJsonArray(
                            expectedJson.get(fieldName).getAsJsonArray(),
                            actualJson.get(fieldName).getAsJsonArray(),
                            exceptions
                    );
                }
                else if (isPrimitive)
                    Assert.assertEquals("Values of the '" + fieldName + "' fields do not match!",
                            expectedJson.get(fieldName),
                            actualJson.get(fieldName)
                    );
                else throw new RuntimeException("Could not determine field (" + expectedJson.get(fieldName) + ") type!");
            }
            log.success("Match: " + fieldName + " -> " + actualJson.get(fieldName));
        }
    }

    /**
     * Compares two JSON arrays and throws an Assertion Error if they are not identical.
     *
     * @param expectedJson the expected JSON array
     * @param actualJson the actual JSON array to be compared with the expected JSON array
     * @param exceptions optional list of JSON object keys to be excluded from the comparison
     * @throws AssertionError if the arrays are not identical
     */
    public void compareJsonArray(JsonArray expectedJson, JsonArray actualJson, String... exceptions){
        log.info("Comparing json arrays...");
        for (int index = 0; index <= expectedJson.size() - 1; index++) {
            if (expectedJson.get(index).isJsonObject()){
                compareJson(
                        expectedJson.get(index).getAsJsonObject(),
                        actualJson.get(index).getAsJsonObject(),
                        exceptions
                );
            }
            else if (expectedJson.get(index).isJsonArray()){
                compareJsonArray(
                        expectedJson.get(index).getAsJsonArray(),
                        actualJson.get(index).getAsJsonArray(),
                        exceptions
                );
            }
            else
                Assert.assertEquals("Array elements do not match!",
                        expectedJson.get(index),
                        actualJson.get(index)
                );
        }
        log.success("Json arrays are identical!");
    }

    /**
     * Compares two objects and returns a boolean indicating whether they match.
     * This method is useful for testing purposes.
     *
     * @param expected the expected object
     * @param actual the actual object
     * @param exceptions the list of exceptions to ignore during comparison
     * @return true if the objects match, false otherwise
     */
    public boolean objectsMatch(Object expected, Object actual, String... exceptions){
        try {
            String expectedString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(expected);
            String actualString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(actual);
            JsonObject expectedJson = JsonParser.parseString(expectedString).getAsJsonObject();
            JsonObject actualJson = JsonParser.parseString(actualString).getAsJsonObject();
            compareJson(expectedJson, actualJson, exceptions);
        }
        catch (AssertionError | JsonProcessingException error){
            log.warning(error.getMessage());
            return false;
        }
        log.success("All fields match!");
        return true;
    }

    /**
     * Returns a Map of all accessible methods of the given object.
     *
     * @param object the object whose methods are to be retrieved
     * @return a Map of method names to Method objects
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
     * Returns a Method that represents the method with the specified name in the given object's class.
     *
     * @param methodName the name of the method to retrieve
     * @param object the object whose class contains the method to retrieve
     * @return the Method object that represents the method with the specified name
     * @throws NoSuchMethodException if no method with the specified name could be located in the given object's class
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
     * This method retrieves the value of a field with a given field name and input class.
     * @param fieldName The name of the field to retrieve the value from.
     * @param inputClass The input class that contains the field to retrieve the value from.
     * @return The value of the field with the given field name and input class.
     * @throws RuntimeException If the field cannot be accessed or does not exist.
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
     * This method retrieves the value of a field with a given field name and input object instance.
     * @param fieldName The name of the field to retrieve the value from.
     * @param inputObject The input object that contains the field to retrieve the value from.
     * @return The value of the field with the given field name and input object.
     * @throws RuntimeException If the field cannot be accessed or does not exist.
     */
    public Object getField(String fieldName, Object inputObject){
        try {
            Field field = inputObject.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(inputObject);
        }
        catch (IllegalAccessException | NoSuchFieldException exception) {throw new RuntimeException(exception);}
    }

    /**
     * This method retrieves all the fields and their values in a given input class.
     * @param inputClass The input class to retrieve the fields and their values from.
     * @return A map containing the names and values of all the fields in the input class.
     * @throws RuntimeException If any of the fields cannot be accessed.
     */
    public <T> Map<String, Object> getFields(Class<T> inputClass){
        Map<String, Object> fieldMap = new HashMap<>();
        try {
            for (Field field:inputClass.getDeclaredFields()) {
                field.setAccessible(true);
                fieldMap.put(field.getName(),field.get(inputClass));
            }
        }
        catch (IllegalAccessException exception) {throw new RuntimeException(exception);}
        return fieldMap;
    }

    /**
     * This method retrieves all the fields and their values in a given input object instance.
     * @param inputObject The input object instance to retrieve the fields and their values from.
     * @return A map containing the names and values of all the fields in the input class.
     * @throws RuntimeException If any of the fields cannot be accessed.
     */
    public Map<String, Object> getFields(Object inputObject){
        Map<String,Object> fieldMap = new HashMap<>();
        try {
            for (Field field:inputObject.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                fieldMap.put(field.getName(), field.get(inputObject));
            }
        }
        catch (IllegalAccessException exception) {throw new RuntimeException(exception);}
        return fieldMap;
    }

    /**
     * Prints the values of all fields of the given object.
     *
     * @param object the object whose fields are to be printed
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
            log.important("\nFields: " + output);
        }
        catch (IllegalAccessException e) {throw new RuntimeException(e);}
    }

    /**
     * Prints the values of all getter methods of the given object.
     *
     * @param object the object whose getter methods are to be printed
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
            log.important("\nFields: " + output);
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
    public JsonArray getJsonArray(Field field, boolean primitive, String... exceptions) throws ClassNotFoundException, NoSuchFieldException {
        JsonArray array = new JsonArray();
        if (!primitive){
            List<JsonObject> list = List.of(
                    getJsonObject(
                             Class.forName(
                                     ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0].getTypeName()
                             ),
                            new JsonObject(),
                            exceptions
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
     * Returns a JsonObject representation of a given class instance, based on the provided JsonObject and
     * optional list of field exceptions.
     *
     * @param clazz The class of the object to be created.
     * @param json The JsonObject to be used as the basis for the object.
     * @param exceptions An optional list of field names to be excluded from the object creation.
     * @return A JsonObject representation of the class instance.
     * @throws NoSuchFieldException If one of the provided field exceptions does not exist in the class.
     * @throws ClassNotFoundException If the provided class name cannot be found.
     */
    public <T> JsonObject getJsonObject(Class<T> clazz, JsonObject json, String... exceptions) throws NoSuchFieldException, ClassNotFoundException {
        List<Field> fields = List.of(clazz.getDeclaredFields());

        for (Field field:fields) {
            field.setAccessible(true);
            if (Arrays.stream(exceptions).noneMatch(exception -> exception.equals(field.getName()))){
                boolean isMember = field.getType().isMemberClass();
                boolean isList = isOfType(field, "List");
                if (isMember)
                    json.add(field.getName(), getJsonObject(field.getType(), new JsonObject(), exceptions));
                else if (isList)
                    json.add(field.getName(), getJsonArray(field, isPrimitive(field)));
                else
                    json.addProperty(field.getName(), field.getType().getName());
            }

        }
        return json;
    }

    /**
     * Returns a JsonObject representation of a given class instance, based on the provided JsonObject and
     * optional list of field exceptions.
     *
     * @param object The class of the object to be created.
     * @param json The JsonObject to be used as the basis for the object.
     * @param exceptions An optional list of field names to be excluded from the object creation.
     * @return A JsonObject representation of the class instance.
     * @throws NoSuchFieldException If one of the provided field exceptions does not exist in the class.
     * @throws ClassNotFoundException If the provided class name cannot be found.
     */
    public <T> JsonObject getJsonFromObject(T object, JsonObject json, String... exceptions) throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
        List<Field> fields = List.of(object.getClass().getDeclaredFields());

        for (Field field:fields) {
            field.setAccessible(true);
            if (Arrays.stream(exceptions).noneMatch(exception -> exception.equals(field.getName()))){
                boolean isMember = field.getType().isMemberClass();
                boolean isList = isOfType(field, "List");

                if (isMember)
                    json.add(field.getName(), getJsonFromObject(field, new JsonObject(), exceptions));
                else if (isList)
                    json.add(field.getName(), getJsonArray(field, isPrimitive(field)));
                else
                    json.add(field.getName(), (JsonElement) field.get(object));
            }
        }
        return json;
    }

    /**
     * Verifies type of given field
     * @param field target field
     * @param expectedType expected field type
     * @return true or false
     */
    public boolean isOfType(Field field, String expectedType){
        return field.getType().getTypeName().contains(expectedType);
    }

    /**
     * Acquires the type of given field
     * @param field target field
     * @return field type
     */
    public String getTypeName(Field field) {
        ParameterizedType type = (ParameterizedType) field.getGenericType();
        return type.getActualTypeArguments()[0].getTypeName();
    }

    /**
     * Checks a given field type is primitive
     * @param field target field
     * @return a boolean
     */
    public boolean isPrimitive(Field field){
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
    public <T> boolean isMemberList(Class<T> clazz, Field field){
        List<Field> fields = List.of(clazz.getFields());
        return fields.stream().anyMatch(
                subField -> subField.getGenericType().getTypeName().equals(field.getGenericType().getTypeName())
        );
    }
}
