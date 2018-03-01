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
            String type = "", format = "UNKNOWN", ref = null;
            Map<String, Object> typeSchema = new HashMap<>(2);

            switch (fieldType.getType()) {
                case TYPE_BYTES:
                    type = "string";
                    format = "byte";
                    break;
                case TYPE_INT32:
                case TYPE_SINT32:
                case TYPE_SFIXED32:
                    type = "integer";
                    format = "int32";
                    break;
                case TYPE_UINT32:
                    type = "integer";
                    format = "int64";
                    break;
                case TYPE_FIXED32:
                    type = "integer";
                    format = "int64";
                    break;
                case TYPE_INT64:
                case TYPE_SINT64:
                case TYPE_SFIXED64:
                    type = "integer";
                    format = "int64";
                    break;
                case TYPE_UINT64:
                case TYPE_FIXED64:
                    type = "string";
                    format = "uint64";
                    break;
                case TYPE_FLOAT:
                    type = "number";
                    format = "float";
                    break;
                case TYPE_DOUBLE:
                    type = "number";
                    format = "double";
                    break;
                case TYPE_BOOL:
                    type = "boolean";
                    format = "boolean";
                    break;
                case TYPE_STRING:
                    type = "string";
                    format = "";
                    break;
                case TYPE_ENUM:
                    ref = "#/components/schemas/" + fieldType.getTypeName();
                    break;
                case TYPE_MESSAGE:
                case TYPE_GROUP:
                    ref = "#/components/schemas/" + fieldType.getTypeName();
                    break;
            }

            if (ref == null) { // primitive type
                typeSchema.put("type", type);
                typeSchema.put("format", format);
            } else { // complex type
                typeSchema.put("$ref", ref);
            }

            switch (fieldType.getLabel()) {
                case LABEL_REPEATED:
                    Map<String, Object> subTypeSchema = new HashMap<>(typeSchema);
                    typeSchema.clear();
                    typeSchema.put("type", "array");
                    typeSchema.put("items", subTypeSchema);
                    break;
            }

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
