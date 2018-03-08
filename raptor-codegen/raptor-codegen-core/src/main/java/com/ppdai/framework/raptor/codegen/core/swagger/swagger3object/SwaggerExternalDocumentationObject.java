package com.ppdai.framework.raptor.codegen.core.swagger.swagger3object;

/**
 * Created by zhangyicong on 18-2-27.
 * http://swagger.io/specification/#externalDocumentationObject
 */
public class SwaggerExternalDocumentationObject {
    private String description;
    private String url;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
