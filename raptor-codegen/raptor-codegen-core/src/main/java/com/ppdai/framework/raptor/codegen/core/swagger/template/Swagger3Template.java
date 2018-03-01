package com.ppdai.framework.raptor.codegen.core.swagger.template;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.DescriptorProtos;
import com.ppdai.framework.raptor.codegen.core.swagger.container.EnumContainer;
import com.ppdai.framework.raptor.codegen.core.swagger.container.MessageContainer;
import com.ppdai.framework.raptor.codegen.core.swagger.container.ServiceContainer;
import com.ppdai.framework.raptor.codegen.core.swagger.swagger3object.*;
import com.ppdai.framework.raptor.codegen.core.swagger.tool.ContainerUtil;
import com.ppdai.framework.raptor.codegen.core.swagger.tool.TypeFormatUtil;
import com.ppdai.framework.raptor.codegen.core.swagger.type.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by zhangyicong on 18-2-27.
 */
public class Swagger3Template implements SwaggerTemplate {

    private static final Logger logger = LoggerFactory.getLogger(Swagger3Template.class);

    private final ObjectMapper mapper;

    public Swagger3Template() {
        this.mapper = new ObjectMapper();
        this.mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /**
     * 生成swagger service定义
     * @param swaggerObject
     * @param serviceContainer
     */
    private void renderServices(SwaggerObject swaggerObject,
                               ServiceContainer serviceContainer,
                               MessageContainer messageContainer) {

        for (ServiceType serviceType : serviceContainer.getServiceTypeList()) {
            for (MethodType methodType : serviceType.getMethodTypeList()) {
                SwaggerPathItemObject swaggerPathItemObject = new SwaggerPathItemObject();

                // set post operation
                SwaggerOperationObject swaggerOperationObject = new SwaggerOperationObject();
                swaggerPathItemObject.setPost(swaggerOperationObject);
                swaggerOperationObject.setDescription("");// todo
                swaggerOperationObject.setSummary("");// todo
                swaggerOperationObject.setOperationId(methodType.getName());

                // set requestBody
                SwaggerRequestBodyObject swaggerRequestBodyObject = new SwaggerRequestBodyObject();
                swaggerRequestBodyObject.setDescription(""); // todo
                swaggerRequestBodyObject.setRequired(true);

                // set requestBody content schema
                SwaggerSchemaObject swaggerSchemaObject = new SwaggerSchemaObject();
                swaggerSchemaObject.setRef("#/components/schemas/" +
                        messageContainer.findMessageTypeByFQPN(methodType.getInputType()).getName());

                Map<String, SwaggerMediaTypeObject> requestContent = new HashMap<>();
                SwaggerMediaTypeObject requestMediaType = new SwaggerMediaTypeObject();
                requestMediaType.setSchema(swaggerSchemaObject);
                for (String contentType: swaggerObject.getConsumes()) {
                    requestContent.put(contentType, requestMediaType);
                }

                swaggerRequestBodyObject.setContent(requestContent);
                swaggerOperationObject.setRequestBody(swaggerRequestBodyObject);

                // set responses
                Map<String, SwaggerResponseObject> responses = new LinkedHashMap<>();
                SwaggerResponseObject swaggerResponseObject = new SwaggerResponseObject();
                swaggerResponseObject.setDescription("successful operation");
                // set responses content schema
                SwaggerSchemaObject reponseSchema = new SwaggerSchemaObject();
                reponseSchema.setRef("#/components/schemas/" +
                        messageContainer.findMessageTypeByFQPN(methodType.getOutputType()).getName());

                Map<String, SwaggerMediaTypeObject> responseContent = new HashMap<>();
                SwaggerMediaTypeObject responseMediaType = new SwaggerMediaTypeObject();
                responseMediaType.setSchema(reponseSchema);
                for (String contentType: swaggerObject.getConsumes()) {
                    responseContent.put(contentType, responseMediaType);
                }

                swaggerResponseObject.setContent(responseContent);

                responses.put("200", swaggerResponseObject);

                swaggerOperationObject.setResponses(responses);

                swaggerObject.getPaths().put("/raptor/" + serviceType.getFQPN() + "/" + methodType.getName(),
                        swaggerPathItemObject);
            }
        }
    }

    private void addEnum2Definitions(SwaggerObject swaggerObject, EnumType enumType) {
        SwaggerSchemaObject swaggerSchemaObject = new SwaggerSchemaObject();
        swaggerObject.getComponents().getSchemas().put(enumType.getName(), swaggerSchemaObject);

        swaggerSchemaObject.setType("string");
        swaggerSchemaObject.setSwaggerEnum(new ArrayList<>(enumType.getValues()));
    }

    private void addType2Definitions(SwaggerObject swaggerObject, MessageType messageType) {
        SwaggerSchemaObject swaggerSchemaObject = new SwaggerSchemaObject();
        swaggerObject.getComponents().getSchemas().put(messageType.getName(), swaggerSchemaObject);

        swaggerSchemaObject.setType("object");

        Map<String, Object> properties = new LinkedHashMap<>();
        swaggerSchemaObject.setProperties(properties);

        for (FieldType fieldType : messageType.getFieldTypeList()) {
            Map<String, Object> typeSchema = TypeFormatUtil.formatTypeSwagger3(fieldType);
            properties.put(fieldType.getName(), typeSchema);
        }
    }

    /**
     * 生成swagger type定义
     * @param swaggerObject
     * @param messageContainer
     * @param enumContainer
     */
    private void renderDefinitions(SwaggerObject swaggerObject,
                                   MessageContainer messageContainer,
                                   EnumContainer enumContainer) {
        for (MessageType messageType : messageContainer.getMessageTypeList()) {
            addType2Definitions(swaggerObject, messageType);
        }

        for (EnumType enumType : enumContainer.getEnumTypeList()) {
            addEnum2Definitions(swaggerObject, enumType);
        }
    }

    /**
     * proto转swagger
     * @param fdp
     * @param apiVersion
     * @return
     */
    public String applyTemplate(DescriptorProtos.FileDescriptorProto fdp, String apiVersion) throws JsonProcessingException {
        // 从pb中提取enum
        EnumContainer enumContainer = ContainerUtil.getEnumContainer(fdp);
        // 从pb中提取message
        MessageContainer messageContainer = ContainerUtil.getMessageContainer(fdp);
        // 从pb中提取service
        ServiceContainer serviceContainer = ContainerUtil.getServiceContainer(fdp);

        SwaggerObject swaggerObject = new SwaggerObject("3.0.0",
                Arrays.asList("application/json"),
                Arrays.asList("application/json"),
                new SwaggerInfoObject(fdp.getName(), apiVersion));

        // render path
        renderServices(swaggerObject, serviceContainer, messageContainer);
        // render type and enum
        renderDefinitions(swaggerObject, messageContainer, enumContainer);

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(swaggerObject);
    }
}
