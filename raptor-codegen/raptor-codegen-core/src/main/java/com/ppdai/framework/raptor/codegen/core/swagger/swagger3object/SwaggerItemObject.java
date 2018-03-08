package com.ppdai.framework.raptor.codegen.core.swagger.swagger3object;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by zhangyicong on 18-2-27.
 * core part of schema, which is common to itemsObject and schemaObject.
 * http://swagger.io/specification/#itemsObject
 */
public class SwaggerItemObject {
    private String type;
    private String format;
    @JsonProperty("$ref")
    private String ref;
    @JsonProperty("enum")
    private List<String> swaggerEnum;
    @JsonProperty("default")
    private String swaggerDefault;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public List<String> getSwaggerEnum() {
        return swaggerEnum;
    }

    public void setSwaggerEnum(List<String> swaggerEnum) {
        this.swaggerEnum = swaggerEnum;
    }

    public String getSwaggerDefault() {
        return swaggerDefault;
    }

    public void setSwaggerDefault(String swaggerDefault) {
        this.swaggerDefault = swaggerDefault;
    }
}
