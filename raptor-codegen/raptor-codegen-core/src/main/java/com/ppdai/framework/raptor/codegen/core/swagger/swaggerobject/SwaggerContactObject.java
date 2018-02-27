package com.ppdai.framework.raptor.codegen.core.swagger.swaggerobject;

/**
 * Created by zhangyicong on 18-2-27.
 * http://swagger.io/specification/#contactObject
 */
public class SwaggerContactObject {
    private String name;
    private String url;
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
