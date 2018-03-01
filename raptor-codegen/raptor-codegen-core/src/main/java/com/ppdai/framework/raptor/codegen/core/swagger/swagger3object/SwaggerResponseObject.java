package com.ppdai.framework.raptor.codegen.core.swagger.swagger3object;

import java.util.Map;

/**
 * Created by zhangyicong on 18-2-27.
 * http://swagger.io/specification/#responseObject
 */
public class SwaggerResponseObject {
    private String description;
    private Map<String, SwaggerMediaTypeObject> content;

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
}
