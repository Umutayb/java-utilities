package utils.reflection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Assert;
import utils.MappingUtilities;
import utils.Printer;
import utils.StringUtilities;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReflectionUtilities {

    static Printer log = new Printer(ReflectionUtilities.class);
    static ObjectMapper mapper = MappingUtilities.Json.mapper;

    /**
     * Iteratively invokes a specified method or conditional function until a condition is met
     * or a timeout is reached.
     *
     * @param timeoutInSeconds The time limit (in seconds) for the iteration.
     * @param parent           The class containing the method to be invoked. (Deprecated: Use {@link ReflectionUtilities#iterativeConditionalInvocation(int, ConditionalFunction)})
     * @param methodName       The name of the method to invoke. (Deprecated: Use {@link ReflectionUtilities#iterativeConditionalInvocation(int, ConditionalFunction)})
     * @param args             The arguments to pass to the method when invoked. (Deprecated: Use {@link ReflectionUtilities#iterativeConditionalInvocation(int, ConditionalFunction)}
     * @param <T>              The type of the parent class.
     * @return True if the condition is met within the specified timeout; otherwise, false.
     * @throws RuntimeException if an exception occurs during method invocation.
     * @deprecated Since version 1.9.7, use {@link ReflectionUtilities#iterativeConditionalInvocation(int, ConditionalFunction)} instead
     */
    @Deprecated(since = "1.9.7")
    public static <T> boolean iterativeConditionalInvocation(
            int timeoutInSeconds,
            Class<T> parent,
            String methodName,
            Object... args) {
        boolean condition;
        long startingTime = System.currentTimeMillis();
        int interval = (int) Math.pow(timeoutInSeconds, 0.5);
        log.info("Iterating at " + interval + " second intervals.");
        try {
            do {
                Method method = getMethod(methodName, parent);
                method.setAccessible(true);
                condition = Boolean.parseBoolean(String.valueOf(method.invoke(parent, args)));
                if (condition) break;
                TimeUnit.SECONDS.sleep(interval);
            }
            while (!((System.currentTimeMillis() - startingTime) / 1000 > timeoutInSeconds));
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException |
                 InterruptedException exception) {
            throw new RuntimeException(exception);
        }
        return condition;
    }

    /**
     * Iteratively invokes a specified method on a class and checks a condition until the condition is met
     * or a timeout is reached.
     * <p>
     * Use this method when you have a specific {@link ConditionalFunction} that encapsulates the desired
     * condition-checking logic, and you want to repeatedly execute it until the condition is met
     * or a specified timeout is reached.
     * <p>
     * Example usage:
     * <pre>{@code
     *     class Test1 {
     *         public static void main(String[] args) {
     *             int a = 2;
     *             int b = 1;
     *             int timeout = 30;
     *             iterativeConditionalInvocation(timeout, 5, () -> {return a - b > 0;});
     *         }
     *     }
     *
     *     //OR:
     *     class Test2 {
     *         public static void main(String[] args) {
     *             int a = 2;
     *             int b = 1;
     *             int timeout = 30;
     *             iterativeConditionalInvocation(timeout, 5, () -> conditionalMethod(a, b));
     *         }
     *
     *         public static boolean conditionalMethod(int a, int b) {
     *             return a - b < 0;
     *         }
     *     }
     * }
     * }</pre>
     *
     * @param timeoutInSeconds The time limit (in seconds) for the iteration.
     * @return True if the condition is met within the specified timeout; otherwise, false.
     * @throws RuntimeException if an exception occurs during method invocation.
     */
    public static boolean iterativeConditionalInvocation(
            int timeoutInSeconds,
            int repeats,
            ConditionalFunction conditionalFunction
    ) {
        boolean condition;
        long startingTime = System.currentTimeMillis();
        int interval = timeoutInSeconds / repeats;
        log.info("Iterating at " + interval + " second intervals.");
        try {
            do {
                condition = conditionalFunction.execute();
                if (condition) break;
                TimeUnit.SECONDS.sleep(interval);
            }
            while (!((System.currentTimeMillis() - startingTime) / 1000 > timeoutInSeconds));
        } catch (InterruptedException exception) {
            throw new RuntimeException(exception);
        }
        return condition;
    }


    /**
     * Iteratively invokes a specified method on a class and checks a condition until the condition is met
     * or a timeout is reached.
     *
     * Use this method when you have a specific {@link ConditionalFunction} that encapsulates the desired
     * condition-checking logic, and you want to repeatedly execute it until the condition is met
     * or a specified timeout is reached.
     *
     * Example usage:
     * <pre>{@code
     *     class Test1 {
     *         public static void main(String[] args) {
     *             int a = 2;
     *             int b = 1;
     *             int timeout = 30;
     *             iterativeConditionalInvocation(timeout, () -> {return a - b > 0;});
     *         }
     *     }
     *
     *     //OR:
     *     class Test2 {
     *         public static void main(String[] args) {
     *             int a = 2;
     *             int b = 1;
     *             int timeout = 30;
     *             iterativeConditionalInvocation(timeout, () -> conditionalMethod(a, b));
     *         }
     *
     *         public static boolean conditionalMethod(int a, int b) {
     *             return a - b < 0;
     *         }
     *     }
     * }
     * }</pre>
     *
     *
     * @param timeoutInSeconds The time limit (in seconds) for the iteration.
     * @return True if the condition is met within the specified timeout; otherwise, false.
     * @throws RuntimeException if an exception occurs during method invocation.
     */
    public static boolean iterativeConditionalInvocation(
            int timeoutInSeconds,
            ConditionalFunction conditionalFunction
    ) {
        return iterativeConditionalInvocation(
                timeoutInSeconds,
                (int) Math.pow(timeoutInSeconds, 0.5),
                conditionalFunction
        );
    }

    /**
     * Compares two objects and throws an AssertionError if they are not equal.
     * This method is useful for testing purposes.
     *
     * @param expected   the expected object
     * @param actual     the actual object
     * @param exceptions the list of exceptions to ignore during comparison
     * @throws AssertionError if the objects are not equal
     */
    public static <T> void compareObjects(T expected, T actual, String... exceptions) {
        try {
            String expectedString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(expected);
            String actualString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(actual);
            JsonObject expectedJson = JsonParser.parseString(expectedString).getAsJsonObject();
            JsonObject actualJson = JsonParser.parseString(actualString).getAsJsonObject();
            compareJson(expectedJson, actualJson, exceptions);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Compares two JSON objects and throws an assertion error if they do not match.
     *
     * @param expectedJson The expected JSON object.
     * @param actualJson   The actual JSON object.
     * @param exceptions   Optional field names to exclude from comparison.
     * @throws AssertionError if the JSON objects do not match.
     */
    public static void compareJson(JsonObject expectedJson, JsonObject actualJson, String... exceptions) {
        Set<String> keySet = expectedJson.keySet().stream().filter(key -> !Arrays.asList(exceptions).contains(key))
                .collect(Collectors.toSet());
        for (String fieldName : keySet) {
            boolean skip = Arrays.asList(exceptions).contains(fieldName);
            boolean isNull = expectedJson.get(fieldName) == null;
            boolean valueNull = expectedJson.get(fieldName).toString().equals("null");
            boolean arrayIsEmpty = expectedJson.get(fieldName).toString().equals("[]");
            boolean isObject = expectedJson.get(fieldName).isJsonObject();
            boolean isArray = expectedJson.get(fieldName).isJsonArray();
            boolean isPrimitive = expectedJson.get(fieldName).isJsonPrimitive();
            if (!skip) {
                if (!isNull && !valueNull && !arrayIsEmpty) {
                    if (isObject) {
                        compareJson(
                                expectedJson.get(fieldName).getAsJsonObject(),
                                actualJson.get(fieldName).getAsJsonObject(),
                                exceptions
                        );
                    } else if (isArray) {
                        compareJsonArray(
                                expectedJson.get(fieldName).getAsJsonArray(),
                                actualJson.get(fieldName).getAsJsonArray(),
                                exceptions
                        );
                    } else if (isPrimitive)
                        Assert.assertEquals("Values of the '" + fieldName + "' fields do not match!",
                                expectedJson.get(fieldName),
                                actualJson.get(fieldName)
                        );
                    else
                        throw new RuntimeException("Could not determine field (" + expectedJson.get(fieldName) + ") type!");
                    log.success("Match: " + fieldName + " -> " + actualJson.get(fieldName));
                } else {
                    Assert.assertEquals("Values of the '" + fieldName + "' fields do not match!",
                            expectedJson.get(fieldName),
                            actualJson.get(fieldName)
                    );
                    log.success("Match: " + fieldName + " -> " + actualJson.get(fieldName));
                }
            }
        }
    }

    /**
     * Compares two JSON arrays and throws an Assertion Error if they are not identical.
     *
     * @param expectedJson the expected JSON array
     * @param actualJson   the actual JSON array to be compared with the expected JSON array
     * @param exceptions   optional list of JSON object keys to be excluded from the comparison
     * @throws AssertionError if the arrays are not identical
     */
    public static void compareJsonArray(JsonArray expectedJson, JsonArray actualJson, String... exceptions) {
        log.info("Comparing json arrays...");
        for (int index = 0; index <= expectedJson.size() - 1; index++) {
            if (expectedJson.get(index).isJsonObject()) {
                compareJson(
                        expectedJson.get(index).getAsJsonObject(),
                        actualJson.get(index).getAsJsonObject(),
                        exceptions
                );
            } else if (expectedJson.get(index).isJsonArray()) {
                compareJsonArray(
                        expectedJson.get(index).getAsJsonArray(),
                        actualJson.get(index).getAsJsonArray(),
                        exceptions
                );
            } else
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
     * @param expected   the expected object
     * @param actual     the actual object
     * @param exceptions the list of exceptions to ignore during comparison
     * @return true if the objects match, false otherwise
     */
    public static boolean objectsMatch(Object expected, Object actual, String... exceptions) {
        try {
            String expectedString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(expected);
            String actualString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(actual);
            JsonObject expectedJson = JsonParser.parseString(expectedString).getAsJsonObject();
            JsonObject actualJson = JsonParser.parseString(actualString).getAsJsonObject();
            compareJson(expectedJson, actualJson, exceptions);
        } catch (AssertionError | JsonProcessingException error) {
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
    public static Map<String, Method> getMethods(Object object) {
        Map<String, Method> methodMap = new HashMap<>();
        for (Method method : object.getClass().getDeclaredMethods()) {
            method.setAccessible(true);
            methodMap.put(method.getName(), method);
        }
        return methodMap;
    }

    /**
     * Returns a Method that represents the method with the specified name in the given object's class.
     *
     * @param methodName the name of the method to retrieve
     * @param object     the object whose class contains the method to retrieve
     * @return the Method object that represents the method with the specified name
     * @throws NoSuchMethodException if no method with the specified name could be located in the given object's class
     */
    public static <T> Method getMethod(String methodName, Class<T> object) throws NoSuchMethodException {
        for (Method method : object.getDeclaredMethods()) {
            method.setAccessible(true);
            if (method.getName().equals(methodName)) return method;
        }
        throw new NoSuchMethodException(
                "No method named " + methodName + " could be located in class called" + object.getName()
        );
    }

    /**
     * This method retrieves the value of a field with a given field name and input class.
     *
     * @param fieldName  The name of the field to retrieve the value from.
     * @param inputClass The input class that contains the field to retrieve the value from.
     * @return The value of the field with the given field name and input class.
     * @throws RuntimeException If the field cannot be accessed or does not exist.
     */
    public static <T> Object getFieldValue(String fieldName, Class<T> inputClass) {
        try {
            Field field = inputClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(inputClass);
        } catch (IllegalAccessException | NoSuchFieldException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * This method retrieves the value of a field with a given field name and input object instance.
     *
     * @param fieldName   The name of the field to retrieve the value from.
     * @param inputObject The input object that contains the field to retrieve the value from.
     * @return The value of the field with the given field name and input object.
     * @throws RuntimeException If the field cannot be accessed or does not exist.
     */
    public static Object getField(String fieldName, Object inputObject) {
        try {
            Field field = inputObject.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(inputObject);
        } catch (IllegalAccessException | NoSuchFieldException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * This method set the value to a field of object with a given field name.
     *
     * @param object     The object in which field the value is set.
     * @param fieldName  The name of the field to set the value.
     * @param fieldValue The value to set the field of object.
     * @throws RuntimeException If the field cannot be accessed or does not exist.
     */
    public static void setObjectField(Object object, String fieldName, Object fieldValue) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, fieldValue);
        } catch (IllegalAccessException | NoSuchFieldException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * This method retrieves all the fields and their values in a given input class.
     *
     * @param inputClass The input class to retrieve the fields and their values from.
     * @return A map containing the names and values of all the fields in the input class.
     * @throws RuntimeException If any of the fields cannot be accessed.
     */
    public static <T> Map<String, Object> getFields(Class<T> inputClass) {
        Map<String, Object> fieldMap = new HashMap<>();
        try {
            for (Field field : inputClass.getDeclaredFields()) {
                field.setAccessible(true);
                fieldMap.put(field.getName(), field.get(inputClass));
            }
        } catch (IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }
        return fieldMap;
    }

    /**
     * This method retrieves all the fields and their values in a given input object instance.
     *
     * @param inputObject The input object instance to retrieve the fields and their values from.
     * @return A map containing the names and values of all the fields in the input class.
     * @throws RuntimeException If any of the fields cannot be accessed.
     */
    public static Map<String, Object> getFields(Object inputObject) {
        Map<String, Object> fieldMap = new HashMap<>();
        try {
            for (Field field : inputObject.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                fieldMap.put(field.getName(), field.get(inputObject));
            }
        } catch (IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }
        return fieldMap;
    }

    /**
     * Prints the values of all fields of the given object.
     *
     * @param object the object whose fields are to be printed
     */
    public static void printObjectFields(Object object) {
        List<Field> fields = List.of(object.getClass().getDeclaredFields());
        StringBuilder output = new StringBuilder();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = StringUtilities.firstLetterCapped(field.getName());
                output.append("\n").append(fieldName).append(" : ").append(field.get(object));
            }
            log.important("\nFields: " + output);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Prints the values of all getter methods of the given object.
     *
     * @param object the object whose getter methods are to be printed
     */
    public static void printModelGetterValues(Object object) {
        Method[] methods = object.getClass().getDeclaredMethods();
        StringBuilder output = new StringBuilder();
        try {
            for (Method method : methods)
                if (method.getName().contains("get")) {
                    String fieldName = StringUtilities.firstLetterCapped(method.getName().replaceAll("get", ""));
                    output.append("\n").append(fieldName).append(" : ").append(method.invoke(object));
                }
            log.important("\nFields: " + output);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Acquires a json array from a given field
     *
     * @param field target field
     * @return generated array
     * @throws ClassNotFoundException throws if class not found
     * @throws NoSuchFieldException   throws if file not found
     */
    public static JsonArray getJsonArray(Field field, boolean primitive, String... exceptions) throws ClassNotFoundException, NoSuchFieldException {
        JsonArray array = new JsonArray();
        if (!primitive) {
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
        } else {
            List<String> list = List.of(getTypeName(field));
            for (String jsonObject : list) array.add(jsonObject);
        }
        return array;
    }

    /**
     * Returns a JsonObject representation of a given class instance, based on the provided JsonObject and
     * optional list of field exceptions.
     *
     * @param clazz      The class of the object to be created.
     * @param json       The JsonObject to be used as the basis for the object.
     * @param exceptions An optional list of field names to be excluded from the object creation.
     * @return A JsonObject representation of the class instance.
     * @throws NoSuchFieldException   If one of the provided field exceptions does not exist in the class.
     * @throws ClassNotFoundException If the provided class name cannot be found.
     */
    public static <T> JsonObject getJsonObject(Class<T> clazz, JsonObject json, String... exceptions) throws NoSuchFieldException, ClassNotFoundException {
        List<Field> fields = List.of(clazz.getDeclaredFields());

        for (Field field : fields) {
            field.setAccessible(true);
            if (Arrays.stream(exceptions).noneMatch(exception -> exception.equals(field.getName()))) {
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
     * @param object     The class of the object to be created.
     * @param json       The JsonObject to be used as the basis for the object.
     * @param exceptions An optional list of field names to be excluded from the object creation.
     * @return A JsonObject representation of the class instance.
     * @throws NoSuchFieldException   If one of the provided field exceptions does not exist in the class.
     * @throws ClassNotFoundException If the provided class name cannot be found.
     */
    public static <T> JsonObject getJsonFromObject(T object, JsonObject json, String... exceptions) throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
        List<Field> fields = List.of(object.getClass().getDeclaredFields());

        for (Field field : fields) {
            field.setAccessible(true);
            if (Arrays.stream(exceptions).noneMatch(exception -> exception.equals(field.getName()))) {
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
     *
     * @param field        target field
     * @param expectedType expected field type
     * @return true or false
     */
    public static boolean isOfType(Field field, String expectedType) {
        return field.getType().getTypeName().contains(expectedType);
    }

    /**
     * Acquires the type of given field
     *
     * @param field target field
     * @return field type
     */
    public static String getTypeName(Field field) {
        ParameterizedType type = (ParameterizedType) field.getGenericType();
        return type.getActualTypeArguments()[0].getTypeName();
    }

    /**
     * Checks a given field type is primitive
     *
     * @param field target field
     * @return a boolean
     */
    public static boolean isPrimitive(Field field) {
        return switch (getTypeName(field)) {
            case "java.lang.Integer",
                    "java.lang.Boolean",
                    "java.lang.Char",
                    "java.lang.Double",
                    "java.lang.Long",
                    "java.lang.Short",
                    "java.lang.Byte",
                    "java.lang.String" -> true;
            default -> false;
        };
    }

    /**
     * Verifies a given list as a member of a class
     *
     * @param clazz target class
     * @param field target field
     * @param <T>   type of the given class
     * @return a boolean
     */
    public static <T> boolean isMemberList(Class<T> clazz, Field field) {
        List<Field> fields = List.of(clazz.getFields());
        return fields.stream().anyMatch(
                subField -> subField.getGenericType().getTypeName().equals(field.getGenericType().getTypeName())
        );
    }

    /**
     * Retrieves the name of the method that called the current method.
     *
     * @return A string representing the name of the calling method.
     */
    public static String getPreviousMethodName() {
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        return stackTrace.length > 2 ? stackTrace[2].getMethodName() : null;
    }
}
