package com.ppdai.framework.raptor.codegen.core.swagger.swagger2object;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.Map;

/**
 * Created by zhangyicong on 18-2-27.
 * http://swagger.io/specification/#swaggerObject
 */
public class SwaggerObject {
    private String swagger;
    private SwaggerInfoObject info;
    private String baseBath;
    private List<String> schemes;
    @JsonIgnore
    private List<String> consumes;
    @JsonIgnore
    private List<String> produces;
    private Map<String, SwaggerPathItemObject> paths;
    private Map<String, SwaggerSchemaObject> definitions;
    private SwaggerExternalDocumentationObject externalDocs;

    public SwaggerObject(String swagger,
                         List<String> schemes,
                         List<String> consumes,
                         List<String> produces,
                         Map<String, SwaggerPathItemObject> paths,
                         Map<String, SwaggerSchemaObject> definitions,
                         SwaggerInfoObject info) {

        this.swagger = swagger;
        this.schemes = schemes;
        this.consumes = consumes;
        this.produces = produces;
        this.paths = paths;
        this.definitions = definitions;
        this.info = info;
    }

    public String getSwagger() {
        return swagger;
    }

    public void setSwagger(String swagger) {
        this.swagger = swagger;
    }

    public SwaggerInfoObject getInfo() {
        return info;
    }

    public void setInfo(SwaggerInfoObject info) {
        this.info = info;
    }

    public String getBaseBath() {
        return baseBath;
    }

    public void setBaseBath(String baseBath) {
        this.baseBath = baseBath;
    }

    public List<String> getSchemes() {
        return schemes;
    }

    public void setSchemes(List<String> schemes) {
        this.schemes = schemes;
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

    public Map<String, SwaggerSchemaObject> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(Map<String, SwaggerSchemaObject> definitions) {
        this.definitions = definitions;
    }

    public SwaggerExternalDocumentationObject getExternalDocs() {
        return externalDocs;
    }

    public void setExternalDocs(SwaggerExternalDocumentationObject externalDocs) {
        this.externalDocs = externalDocs;
    }
}
