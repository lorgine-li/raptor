package com.ppdai.framework.raptor.codegen.core.swagger.swagger2object;

import java.util.List;
import java.util.Map;

/**
 * Created by zhangyicong on 18-2-27.
 * http://swagger.io/specification/#operationObject
 */
public class SwaggerOperationObject {
    private String summary;
    private String description;
    private String operationId;
    private List<String> consumes;
    private List<String> produces;
    private Map<String, SwaggerResponseObject> responses;
    private List<SwaggerParameterObject> parameters;
    private List<String> tags;
    private boolean deprecated;
    private SwaggerExternalDocumentationObject externalDocs;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
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

    public Map<String, SwaggerResponseObject> getResponses() {
        return responses;
    }

    public void setResponses(Map<String, SwaggerResponseObject> responses) {
        this.responses = responses;
    }

    public List<SwaggerParameterObject> getParameters() {
        return parameters;
    }

    public void setParameters(List<SwaggerParameterObject> parameters) {
        this.parameters = parameters;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public SwaggerExternalDocumentationObject getExternalDocs() {
        return externalDocs;
    }

    public void setExternalDocs(SwaggerExternalDocumentationObject externalDocs) {
        this.externalDocs = externalDocs;
    }
}
