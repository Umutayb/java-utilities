package utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;

/**
 * Utility class for working with JSON mappings and generating JSON schemas.
 * Provides methods for serializing and deserializing Java objects to and from JSON.
 * It also includes functionality to generate JSON schema representations.
 */
public class MappingUtilities {

    /**
     * Utility class for handling JSON serialization and deserialization.
     * Configures an {@link ObjectMapper} for serializing and deserializing Java objects to and from JSON.
     */
    public static class Json {

        // An ObjectMapper configured for JSON operations
        public static ObjectMapper mapper = new ObjectMapper();

        static {
            // Configure ObjectMapper settings
            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            mapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
            mapper.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
            mapper.setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.NONE);
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            mapper.configure(SerializationFeature.CLOSE_CLOSEABLE, false);
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
        }

        /**
         * Converts the given object into a JSON string representation with pretty-print formatting.
         *
         * @param <T>   The type of the object to be converted to JSON.
         * @param body  The object to be converted to JSON.
         * @return      The JSON string representation of the given object in pretty-printed format.
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
         * @throws JsonProcessingException If a JSON processing error occurs.
         */
        public static <T> String getJsonString(T body) throws JsonProcessingException {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(body);
        }

        /**
         * Converts a JSON string representation of an object into a Java object of the specified type.
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

        /**
         * Utility class for generating JSON schema representations.
         */
        public static class Schema {

            /**
             * Recursively sets the ID of all nested JSON schemas to null.
             * This method traverses through object schemas and array schemas, setting their ID properties to null.
             *
             * @param schema The root JsonSchema object to be processed.
            /**
             * Recursively sets the ID of all nested JSON schemas to null.
             * This method traverses through object schemas and array schemas, setting their ID properties to null.
             *
             * @param schema The root JsonSchema object to be processed.
             * @return The input JsonSchema with all nested schemas' IDs set to null.
             */
            public static JsonSchema setIdNull(JsonSchema schema) {
                if (schema.isObjectSchema()) {
                    for (String propertyKey : schema.asObjectSchema().getProperties().keySet()) {
                        JsonSchema childSchema = schema.asObjectSchema().getProperties().get(propertyKey);
                        if (childSchema.isObjectSchema())
                            setIdNull(childSchema);

                        if (childSchema.isArraySchema())
                            setIdNull(childSchema.asArraySchema().getItems().asSingleItems().getSchema());

                        schema.setId(null);
                        schema.set$ref(null);
                    }
                }
                return schema;
            }

            /**
             * Generates a JSON schema for the given class, with the option to specify required fields.
             * This method uses the Jackson library to generate the schema and customize it based on the provided required fields.
             * It sets the ID of the schema and its nested schemas to null and adds the required fields to the schema's "required" property.
             *
             * @param clazz The class for which the JSON schema should be generated.
             * @param requiredFields A varargs array of field names that should be marked as "required" in the schema.
             * @return A JsonNode representing the generated schema, or null if an exception occurs during generation.
             */
            public static JsonNode getJsonNodeFor(Class<?> clazz, String... requiredFields) {
                JsonSchema schema = generateSchema(clazz);
                assert schema != null;
                schema.setId(null);
                return addRequiredFields(schema, requiredFields);
            }

            /**
             * Generates a JSON schema for the given class, with the option to specify required fields.
             * This method uses the Jackson library to generate the schema and customize it based on the provided required fields.
             * It sets the ID of the schema and its nested schemas to null and adds the required fields to the schema's "required" property.
             *
             * @param clazz The class for which the JSON schema should be generated.
             * @return A JsonNode representing the generated schema, or null if an exception occurs during generation.
             */
            public static JsonSchema generateSchema(Class<?> clazz) {
                try {
                    JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(new ObjectMapper());
                    return setIdNull(schemaGen.generateSchema(clazz));
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            /**
             * Adds the specified required fields to the "required" property of a JSON schema.
             * This method adds each field name from the provided array to the "required" property of the schema.
             *
             * @param schema         The JSON schema to modify.
             * @param requiredFields An array of field names that should be marked as required in the schema.
             * @return
             */
            private static JsonNode addRequiredFields(JsonSchema schema, String[] requiredFields) {
                JsonNode schemaNode = mapper.valueToTree(schema);

                if (!(schemaNode instanceof ObjectNode root)) {
                    throw new IllegalArgumentException("Schema must be an ObjectNode");
                }

                ArrayNode required = root.withArray("required");

                for (String fieldName : requiredFields) {
                    required.add(fieldName);
                }

                // Convert the modified JsonNode back to JsonSchema
                return mapper.valueToTree(root);
            }

        }
    }
}

