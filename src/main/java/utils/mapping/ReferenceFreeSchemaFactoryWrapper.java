package utils.mapping;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import com.fasterxml.jackson.module.jsonSchema.factories.VisitorContext;

public class ReferenceFreeSchemaFactoryWrapper extends SchemaFactoryWrapper {
    public ReferenceFreeSchemaFactoryWrapper() {
        super();
        this.setVisitorContext(new CustomVisitorContext());
    }

    private static class CustomVisitorContext extends VisitorContext {
        @Override
        public String addSeenSchemaUri(JavaType seenSchema) {
            return null; // Return null prevents adding $ref URIs
        }
    }
}