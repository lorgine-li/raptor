package com.ppdai.framework.raptor.codegen.core.swagger;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.DescriptorProtos;
import com.ppdai.framework.raptor.codegen.core.swagger.container.MessageContainer;
import com.ppdai.framework.raptor.codegen.core.swagger.container.ServiceContainer;
import com.ppdai.framework.raptor.codegen.core.swagger.swaggerobject.*;
import com.ppdai.framework.raptor.codegen.core.swagger.type.FieldType;
import com.ppdai.framework.raptor.codegen.core.swagger.type.MessageType;
import com.ppdai.framework.raptor.codegen.core.swagger.type.MethodType;
import com.ppdai.framework.raptor.codegen.core.swagger.type.ServiceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by zhangyicong on 18-2-27.
 */
public class SwaggerTemplate {

    private static final Logger logger = LoggerFactory.getLogger(SwaggerTemplate.class);

    private MessageContainer getMessageContainer(DescriptorProtos.FileDescriptorProto fdp) {
        MessageContainer messageContainer = new MessageContainer(fdp.getPackage());

        for (DescriptorProtos.DescriptorProto dp : fdp.getMessageTypeList()) {
            messageContainer.addMessageProto(dp);
        }

        return messageContainer;
    }

    private ServiceContainer getServiceContainer(DescriptorProtos.FileDescriptorProto fdp) {
        ServiceContainer serviceContainer = new ServiceContainer(fdp.getPackage());

        for (DescriptorProtos.ServiceDescriptorProto sdp : fdp.getServiceList()) {
            serviceContainer.addServiceProto(sdp);
        }

        return serviceContainer;
    }

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

                // set consumes
                swaggerOperationObject.setConsumes(swaggerObject.getConsumes());
                // set produces
                swaggerOperationObject.setProduces(swaggerObject.getProduces());

                // set parameters
                List<SwaggerParameterObject> parameters = new ArrayList<>();
                swaggerOperationObject.setParameters(parameters);
                SwaggerParameterObject swaggerParameterObject = new SwaggerParameterObject();
                parameters.add(swaggerParameterObject);

                swaggerParameterObject.setDescription("");// todo
                swaggerParameterObject.setIn("body");
                swaggerParameterObject.setName("body");
                swaggerParameterObject.setRequired(true);

                // set schema
                SwaggerSchemaObject swaggerSchemaObject = new SwaggerSchemaObject();
                swaggerSchemaObject.setRef("#/definitions/" + messageContainer.findMessageTypeByFQPN(methodType.getInputType()).getName());
                swaggerParameterObject.setSchema(swaggerSchemaObject);

                // set responses
                Map<String, SwaggerResponseObject> responses = new LinkedHashMap<>();
                SwaggerResponseObject swaggerResponseObject = new SwaggerResponseObject();
                swaggerResponseObject.setDescription("successful operation");
                // set responses schema
                SwaggerSchemaObject reponseSchema = new SwaggerSchemaObject();
                swaggerResponseObject.setSchema(reponseSchema);
                reponseSchema.setRef("#/definitions/" + messageContainer.findMessageTypeByFQPN(methodType.getOutputType()).getName());

                responses.put("200", swaggerResponseObject);
                swaggerOperationObject.setResponses(responses);

                swaggerObject.getPaths().put("/raptor/" + serviceType.getFQPN() + "/" + methodType.getName(),
                        swaggerPathItemObject);
            }
        }
    }

    private void renderDefinitions(SwaggerObject swaggerObject, MessageContainer messageContainer) {
        for (MessageType messageType : messageContainer.getMessageTypeList()) {

            SwaggerSchemaObject swaggerSchemaObject = new SwaggerSchemaObject();
            swaggerObject.getDefinitions().put(messageType.getName(), swaggerSchemaObject);

            swaggerSchemaObject.setType("object");

            Map<String, Object> properties = new LinkedHashMap<>();
            swaggerSchemaObject.setProperties(properties);

            for (FieldType fieldType : messageType.getFieldTypeList()) {
                String type = "", format = "";
                Map<String, String> typeSchema = new HashMap<>(2);

                switch (fieldType.getType()) {
                    case TYPE_BYTES:
                        type = "string";
                        format = "binary";
                        break;
                    case TYPE_INT32:
                    case TYPE_SINT32:
                        type = "integer";
                        format = "int32";
                        break;
                    case TYPE_INT64:
                    case TYPE_SINT64:
                        type = "integer";
                        format = "int64";
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
                        format = "";
                        break;
                    case TYPE_STRING:
                        type = "string";
                        format = "";
                        break;
                }

                typeSchema.put("type", type);
                typeSchema.put("format", format);

                properties.put(fieldType.getName(), typeSchema);
            }
        }
    }

    public String applyTemplate(DescriptorProtos.FileDescriptorProto fdp) {
        MessageContainer messageContainer = getMessageContainer(fdp);
        ServiceContainer serviceContainer = getServiceContainer(fdp);

        SwaggerObject swaggerObject = new SwaggerObject("2.0",
                Arrays.asList("http", "https"),
                Arrays.asList("application/json"),
                Arrays.asList("application/json"),
                new LinkedHashMap<>(),
                new LinkedHashMap<>(),
                new SwaggerInfoObject(fdp.getName(), "version not set"));

        renderServices(swaggerObject, serviceContainer, messageContainer);
        renderDefinitions(swaggerObject, messageContainer);

        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            logger.info("Swagger API: {}", mapper.writeValueAsString(swaggerObject));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
