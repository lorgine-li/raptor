package com.ppdai.framework.raptor.codegen.core.swagger.tool;

/**
 * Created by zhangyicong on 18-3-1.
 */
public class TypeFormat {
    private String type;
    private String format;
    private String ref;
    private Object additionalProperties;

    public TypeFormat() {

    }
    public TypeFormat(String type, String format, String ref, Object additionalProperties) {
        this.type = type;
        this.format = format;
        this.ref = ref;
        this.additionalProperties = additionalProperties;
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

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public Object getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(Object additionalProperties) {
        this.additionalProperties = additionalProperties;
    }
}
