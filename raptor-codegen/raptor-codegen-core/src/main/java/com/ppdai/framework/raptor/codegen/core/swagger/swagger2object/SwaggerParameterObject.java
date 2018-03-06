package com.ppdai.framework.raptor.codegen.core.swagger.swagger2object;

import java.util.List;

/**
 * Created by zhangyicong on 18-2-27.
 * http://swagger.io/specification/#parameterObject
 */
public class SwaggerParameterObject {
    private String name;
    private String description;
    private String in;
    private boolean required;
    private String type;
    private String format;
    private SwaggerItemObject items;
    private List<String> swaggerEnum;
    private String swaggerDefault;
    private SwaggerSchemaObject schema;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIn() {
        return in;
    }

    public void setIn(String in) {
        this.in = in;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

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

    public SwaggerItemObject getItems() {
        return items;
    }

    public void setItems(SwaggerItemObject items) {
        this.items = items;
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

    public SwaggerSchemaObject getSchema() {
        return schema;
    }

    public void setSchema(SwaggerSchemaObject schema) {
        this.schema = schema;
    }
}
