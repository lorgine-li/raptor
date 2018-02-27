package com.ppdai.framework.raptor.codegen.core.swagger.swaggerobject;

/**
 * Created by zhangyicong on 18-2-27.
 * http://swagger.io/specification/#pathItemObject
 */
public class SwaggerPathItemObject {
    private SwaggerOperationObject get;
    private SwaggerOperationObject delete;
    private SwaggerOperationObject post;
    private SwaggerOperationObject put;
    private SwaggerOperationObject patch;

    public SwaggerOperationObject getGet() {
        return get;
    }

    public void setGet(SwaggerOperationObject get) {
        this.get = get;
    }

    public SwaggerOperationObject getDelete() {
        return delete;
    }

    public void setDelete(SwaggerOperationObject delete) {
        this.delete = delete;
    }

    public SwaggerOperationObject getPost() {
        return post;
    }

    public void setPost(SwaggerOperationObject post) {
        this.post = post;
    }

    public SwaggerOperationObject getPut() {
        return put;
    }

    public void setPut(SwaggerOperationObject put) {
        this.put = put;
    }

    public SwaggerOperationObject getPatch() {
        return patch;
    }

    public void setPatch(SwaggerOperationObject patch) {
        this.patch = patch;
    }
}
