package utils;

import com.google.gson.JsonObject;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class for various data conversion operations.
 *
 * <p>
 * This class provides static methods for converting data between different formats.
 * </p>
 */
public class Conversion {

    /**
     * Converts a JsonObject to a Map of String key-value pairs.
     *
     * <p>
     * This method takes a JsonObject as input and converts it into a Map where each key-value pair in the JsonObject
     * is represented in the Map. The keys in the Map correspond to the keys in the JsonObject, and the values are
     * the string representations of the corresponding values in the JsonObject.
     * </p>
     *
     * @param json The JsonObject to be converted to a Map.
     * @return A Map representing the key-value pairs from the input JsonObject.
     */
    public static Map<String, String> mapFromJson(JsonObject json) {
        return json.keySet()
                .stream()
                .collect(Collectors.toMap(key -> key, key -> json.get(key).toString()));
    }

    /**
     * Converts a string representation of key-value pairs to a Map.
     *
     * <p>
     * This method takes a string representation of key-value pairs and converts it into a Map.
     * The input string should be in a format compatible with the {@code StringUtilities.str2Map} method.
     * </p>
     *
     * @param inputString The string representation of key-value pairs to be converted to a Map.
     * @return A Map representing the key-value pairs parsed from the input string.
     * @see StringUtilities#str2Map(String)
     */
    public static Map<String, String> mapFromString(String inputString){
        return StringUtilities.str2Map(inputString);
    }
}
