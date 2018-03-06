package com.ppdai.framework.raptor.codegen.core.swagger.swagger3object;

import java.util.Map;

/**
 * Created by zhangyicong on 18-3-1.
 * https://swagger.io/specification/#componentsObject
 */
public class SwaggerComponentsObject {
    private Map<String, SwaggerSchemaObject> schemas;

    public Map<String, SwaggerSchemaObject> getSchemas() {
        return schemas;
    }

    public void setSchemas(Map<String, SwaggerSchemaObject> schemas) {
        this.schemas = schemas;
    }
}
