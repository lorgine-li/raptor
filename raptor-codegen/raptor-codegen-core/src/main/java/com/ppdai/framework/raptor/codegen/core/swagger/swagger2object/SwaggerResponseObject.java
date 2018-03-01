package com.ppdai.framework.raptor.codegen.core.swagger.swagger2object;

/**
 * Created by zhangyicong on 18-2-27.
 * http://swagger.io/specification/#responseObject
 */
public class SwaggerResponseObject {
    private String description;
    private SwaggerSchemaObject schema;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SwaggerSchemaObject getSchema() {
        return schema;
    }

    public void setSchema(SwaggerSchemaObject schema) {
        this.schema = schema;
    }
}
