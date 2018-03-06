package com.ppdai.framework.raptor.codegen.core.swagger.swagger3object;

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
    private Map<String, SwaggerResponseObject> responses;
    private SwaggerRequestBodyObject requestBody;
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

    public Map<String, SwaggerResponseObject> getResponses() {
        return responses;
    }

    public void setResponses(Map<String, SwaggerResponseObject> responses) {
        this.responses = responses;
    }

    public SwaggerRequestBodyObject getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(SwaggerRequestBodyObject requestBody) {
        this.requestBody = requestBody;
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
