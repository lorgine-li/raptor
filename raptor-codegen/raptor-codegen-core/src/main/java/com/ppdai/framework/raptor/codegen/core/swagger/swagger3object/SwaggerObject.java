package com.ppdai.framework.raptor.codegen.core.swagger.swagger3object;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangyicong on 18-2-27.
 * https://swagger.io/specification/#openapi-object-17
 */
public class SwaggerObject {
    private String openapi;
    private SwaggerInfoObject info;
    private List<SwaggerServerObject> servers;
    @JsonIgnore
    private List<String> consumes;
    @JsonIgnore
    private List<String> produces;
    private Map<String, SwaggerPathItemObject> paths;
    private SwaggerComponentsObject components;
    private SwaggerExternalDocumentationObject externalDocs;

    public SwaggerObject(String openapi,
                         List<String> consumes,
                         List<String> produces,
                         SwaggerInfoObject info) {

        this.openapi = openapi;
        this.consumes = consumes;
        this.produces = produces;
        this.paths = new LinkedHashMap<>();

        SwaggerComponentsObject swaggerComponentsObject = new SwaggerComponentsObject();
        swaggerComponentsObject.setSchemas(new LinkedHashMap<>());

        this.components = swaggerComponentsObject;
        this.info = info;
    }

    public String getOpenapi() {
        return openapi;
    }

    public void setOpenapi(String openapi) {
        this.openapi = openapi;
    }

    public SwaggerInfoObject getInfo() {
        return info;
    }

    public void setInfo(SwaggerInfoObject info) {
        this.info = info;
    }

    public List<SwaggerServerObject> getServers() {
        return servers;
    }

    public void setServers(List<SwaggerServerObject> servers) {
        this.servers = servers;
    }

    public List<String> getConsumes() {
        return consumes;
    }

    public void setConsumes(List<String> consumes) {
        this.consumes = consumes;
    }

    public List<String> getProduces() {
        return produces;
    }

    public void setProduces(List<String> produces) {
        this.produces = produces;
    }

    public Map<String, SwaggerPathItemObject> getPaths() {
        return paths;
    }

    public void setPaths(Map<String, SwaggerPathItemObject> paths) {
        this.paths = paths;
    }

    public SwaggerComponentsObject getComponents() {
        return components;
    }

    public void setComponents(SwaggerComponentsObject components) {
        this.components = components;
    }

    public SwaggerExternalDocumentationObject getExternalDocs() {
        return externalDocs;
    }

    public void setExternalDocs(SwaggerExternalDocumentationObject externalDocs) {
        this.externalDocs = externalDocs;
    }
}
