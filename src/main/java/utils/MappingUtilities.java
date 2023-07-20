package utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import static utils.MappingUtilities.Json.mapper;

public class MappingUtilities {

    /**
     * Converts a Json string representation of an object into a Java object of the specified type.
     *
     * @param <T> The type of the object.
     * @param objectString The JSON string representation of the object.
     * @param type The Class object of the type T.
     * @return The Java object of type T converted from the JSON string.
     * @throws RuntimeException If a JSON processing error occurs.
     */
    public static <T> T stringToObject(String objectString, Class<T> type) {
        try {return mapper.readValue(objectString, type);}
        catch (JsonProcessingException e) {throw new RuntimeException(e);}
    }

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
        }
    }
}
