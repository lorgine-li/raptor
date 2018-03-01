package com.ppdai.framework.raptor.codegen.core.swagger.swagger2object;

import java.util.Map;

/**
 * Created by zhangyicong on 18-2-27.
 * http://swagger.io/specification/#schemaObject
 */
public class SwaggerSchemaObject extends SwaggerItemObject {
    private Map<String, Object> properties;
    private SwaggerSchemaObject additionalProperties;
    private String description;
    private String title;
    private SwaggerExternalDocumentationObject externalDocs;

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public SwaggerSchemaObject getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(SwaggerSchemaObject additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public SwaggerExternalDocumentationObject getExternalDocs() {
        return externalDocs;
    }

    public void setExternalDocs(SwaggerExternalDocumentationObject externalDocs) {
        this.externalDocs = externalDocs;
    }
}
