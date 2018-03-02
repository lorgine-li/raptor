package com.ppdai.framework.raptor.codegen.core.swagger.template;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.DescriptorProtos;
import com.ppdai.framework.raptor.codegen.core.swagger.container.EnumContainer;
import com.ppdai.framework.raptor.codegen.core.swagger.container.MessageContainer;
import com.ppdai.framework.raptor.codegen.core.swagger.container.MetaContainer;
import com.ppdai.framework.raptor.codegen.core.swagger.container.ServiceContainer;
import com.ppdai.framework.raptor.codegen.core.swagger.swagger2object.*;
import com.ppdai.framework.raptor.codegen.core.swagger.tool.ContainerUtil;
import com.ppdai.framework.raptor.codegen.core.swagger.tool.TypeFormatUtil;
import com.ppdai.framework.raptor.codegen.core.swagger.type.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by zhangyicong on 18-2-27.
 */
public class Swagger2Template implements SwaggerTemplate {

    private static final Logger logger = LoggerFactory.getLogger(SwaggerTemplate.class);

    private final ObjectMapper mapper;

    public Swagger2Template() {
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
                swaggerSchemaObject.setRef("#/definitions/" +
                        messageContainer.findMessageTypeByFQPN(methodType.getInputType()).getName());
                swaggerParameterObject.setSchema(swaggerSchemaObject);

                // set responses
                Map<String, SwaggerResponseObject> responses = new LinkedHashMap<>();
                SwaggerResponseObject swaggerResponseObject = new SwaggerResponseObject();
                swaggerResponseObject.setDescription("successful operation");
                // set responses schema
                SwaggerSchemaObject reponseSchema = new SwaggerSchemaObject();
                swaggerResponseObject.setSchema(reponseSchema);
                reponseSchema.setRef("#/definitions/" +
                        messageContainer.findMessageTypeByFQPN(methodType.getOutputType()).getName());

                responses.put("200", swaggerResponseObject);
                swaggerOperationObject.setResponses(responses);

                swaggerObject.getPaths().put("/raptor/" + serviceType.getFQPN() + "/" + methodType.getName(),
                        swaggerPathItemObject);
            }
        }
    }

    private void addEnum2Definitions(SwaggerObject swaggerObject, EnumType enumType) {
        SwaggerSchemaObject swaggerSchemaObject = new SwaggerSchemaObject();
        swaggerObject.getDefinitions().put(enumType.getName(), swaggerSchemaObject);

        swaggerSchemaObject.setType("string");
        swaggerSchemaObject.setSwaggerEnum(new ArrayList<>(enumType.getValues()));
    }

    private void addType2Definitions(SwaggerObject swaggerObject, MessageType messageType) {
        SwaggerSchemaObject swaggerSchemaObject = new SwaggerSchemaObject();
        swaggerObject.getDefinitions().put(messageType.getName(), swaggerSchemaObject);

        swaggerSchemaObject.setType("object");

        Map<String, Object> properties = new LinkedHashMap<>();
        swaggerSchemaObject.setProperties(properties);

        for (FieldType fieldType : messageType.getFieldTypeList()) {
            Map<String, Object> typeSchema = TypeFormatUtil.formatTypeSwagger2(fieldType);
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
    public String applyTemplate(DescriptorProtos.FileDescriptorProto fdp,
                                MetaContainer metaContainer,
                                String apiVersion) throws JsonProcessingException {
        // 从pb中提取enum
        EnumContainer enumContainer = ContainerUtil.getEnums(fdp, metaContainer);
        // 从pb中提取message
        MessageContainer messageContainer = ContainerUtil.getMessages(fdp, metaContainer);
        // 从pb中提取service
        ServiceContainer serviceContainer = ContainerUtil.getServiceContainer(fdp);

        SwaggerObject swaggerObject = new SwaggerObject("2.0",
                Arrays.asList("http"),
                Arrays.asList("application/json"),
                Arrays.asList("application/json"),
                new LinkedHashMap<>(),
                new LinkedHashMap<>(),
                new SwaggerInfoObject(fdp.getName(), apiVersion));

        // render path
        renderServices(swaggerObject, serviceContainer, messageContainer);
        // render type and enum
        renderDefinitions(swaggerObject, messageContainer, enumContainer);

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(swaggerObject);
    }
}