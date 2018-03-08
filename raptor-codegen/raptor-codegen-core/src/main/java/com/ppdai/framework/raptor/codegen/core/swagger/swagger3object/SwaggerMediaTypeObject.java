package com.ppdai.framework.raptor.codegen.core.swagger.swagger3object;

/**
 * Created by zhangyicong on 18-3-1.
 * https://swagger.io/specification/#mediaTypeObject
 */
public class SwaggerMediaTypeObject {
    private SwaggerSchemaObject schema;

    public SwaggerSchemaObject getSchema() {
        return schema;
    }

    public void setSchema(SwaggerSchemaObject schema) {
        this.schema = schema;
    }
}
