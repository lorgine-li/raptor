package com.ppdai.framework.raptor.codegen.core.swagger.swagger3object;

import java.util.Map;

/**
 * Created by zhangyicong on 18-3-1.
 * https://swagger.io/specification/#requestBodyObject
 */
public class SwaggerRequestBodyObject {
    private String description;
    private Map<String, SwaggerMediaTypeObject> content;
    private boolean required;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, SwaggerMediaTypeObject> getContent() {
        return content;
    }

    public void setContent(Map<String, SwaggerMediaTypeObject> content) {
        this.content = content;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}
