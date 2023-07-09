package utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
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
        }
    }
}
