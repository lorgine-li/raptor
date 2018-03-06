package com.ppdai.framework.raptor.codegen.core.swagger.swagger3object;

/**
 * Created by zhangyicong on 18-3-1.
 * https://swagger.io/specification/#serverObject
 */
public class SwaggerServerObject {
    private String url;
    private String description;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
