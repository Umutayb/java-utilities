package utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class MappingUtilities {

    /**
     * Visibility settings are applied to allow the mapper to access and serialize/deserialize all fields, while ignoring getters, setters, and creators.
     * The FAIL_ON_EMPTY_BEANS serialization feature is disabled, which prevents the mapper from throwing an exception when encountering empty beans (objects without any properties).
     */
    public static class Json {
        public static ObjectMapper mapper = new ObjectMapper();

        static {
            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            mapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
            mapper.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
            mapper.setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.NONE);
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            mapper.configure(SerializationFeature.CLOSE_CLOSEABLE, false);
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
        }

        /**
         * Converts the given object into its JSON string representation in a pretty-printed format.
         *
         * @param <T>   The type of the object to be converted to JSON.
         * @param body  The object to be converted to JSON.
         * @return      The JSON string representation of the given object in a pretty-printed format.
         */
        public static <T> String getJsonStringFor(T body)  {
            return mapper.valueToTree(body).toPrettyString();
        }

        /**
         * Converts the given object into its JSON string representation in a pretty-printed format.
         *
         * @param <T>   The type of the object to be converted to JSON.
         * @param body  The object to be converted to JSON.
         * @return      The JSON string representation of the given object in a pretty-printed format.
         */
        public static <T> String getJsonString(T body) throws JsonProcessingException {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(body);
        }

        /**
         * Converts a Json string representation of an object into a Java object of the specified type.
         *
         * @param <T> The type of the object.
         * @param jsonString The JSON string representation of the object.
         * @param model The Class object of the type T.
         * @return The Java object of type T converted from the JSON string.
         * @throws JsonProcessingException If a JSON processing error occurs.
         */
        public static <T> T fromJsonString(String jsonString, Class<T> model) throws JsonProcessingException {
            return MappingUtilities.Json.mapper.readValue(jsonString, model);
        }
    }
}
