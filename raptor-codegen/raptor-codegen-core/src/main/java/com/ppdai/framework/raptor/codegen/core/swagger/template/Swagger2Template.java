package com.ppdai.framework.raptor.codegen.core.swagger.template;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.DescriptorProtos;
import com.ppdai.framework.raptor.codegen.core.swagger.container.MetaContainer;
import com.ppdai.framework.raptor.codegen.core.swagger.container.ServiceContainer;
import com.ppdai.framework.raptor.codegen.core.swagger.tool.ContainerUtil;
import com.ppdai.framework.raptor.codegen.core.swagger.tool.TypeFormatUtil;
import com.ppdai.framework.raptor.codegen.core.swagger.type.*;
import com.ppdai.framework.raptor.codegen.core.utils.CommonUtils;
import io.swagger.models.*;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static io.swagger.models.Scheme.HTTP;

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
     *
     * @param swagger
     * @param serviceContainer
     */
    private Set<MessageType> renderServices(Swagger swagger,
                                            ServiceContainer serviceContainer,
                                            MetaContainer metaContainer,
                                            String basePackage) {

        Set<MessageType> dependMessage = new LinkedHashSet<>();

        for (ServiceType serviceType : serviceContainer.getServiceTypeList()) {
            for (MethodType methodType : serviceType.getMethodTypeList()) {
                Path path = new Path();
                Operation operation = new Operation();
                path.setPost(operation);
                operation.setSummary(methodType.getLeadingComments());
                operation.setDescription("");// todo
                operation.setOperationId(methodType.getName());

                // set consumes
                operation.setConsumes(swagger.getConsumes());
                // set produces
                operation.setProduces(swagger.getProduces());


                // set parameters
                List<Parameter> parameters = new ArrayList<>();
                operation.setParameters(parameters);
                BodyParameter parameter = new BodyParameter();
                parameters.add(parameter);
                parameter.setDescription("");// todo
                parameter.setIn("body");
                parameter.setName("body");
                parameter.setRequired(true);

                // set schema
                RefModel schema = new RefModel();
                MessageType inputMessage = metaContainer.findMessageTypeByFQPN(methodType.getInputType(), basePackage);
                dependMessage.add(inputMessage);
                schema.set$ref("#/definitions/" +
                        getRefName(inputMessage, basePackage));
                parameter.setSchema(schema);

                // set responses
                Map<String, Response> responses = new LinkedHashMap<>();
                Response response = new Response();
                response.setDescription("successful operation");

                // set responses schema
                RefProperty reponseSchema = new RefProperty();
                response.setSchema(reponseSchema);
                MessageType outputMessage = metaContainer.findMessageTypeByFQPN(methodType.getOutputType(), basePackage);
                dependMessage.add(outputMessage);
                reponseSchema.set$ref("#/definitions/" +
                        getRefName(outputMessage, basePackage));

                responses.put("200", response);
                operation.setResponses(responses);
                swagger.path("/raptor/" + serviceType.getFQPN() + "/" + methodType.getName(),
                        path);
            }
        }
        return dependMessage;
    }

    private String getRefName(EnumType inputEnum, String basePackage) {
        String packageName = CommonUtils.getPackageNameFromFQPN(inputEnum.getFQPN());
        if (basePackage.equals(packageName)) {
            return inputEnum.getName();
        } else {
            return inputEnum.getFQPN();
        }
    }

    private String getRefName(MessageType inputMessage, String basePackage) {
        String packageName = CommonUtils.getPackageNameFromFQPN(inputMessage.getFQPN());
        if (basePackage.equals(packageName)) {
            return inputMessage.getName();
        } else {
            return inputMessage.getFQPN();
        }
    }

    private void addEnum2Definitions(Swagger swagger, EnumType enumType, String basePackage) {
        ModelImpl model = new ModelImpl();
        swagger.getDefinitions().put(getRefName(enumType, basePackage), model);

        model.setType("string");
        model.setEnum(new ArrayList<>(enumType.getValues()));
    }

    private void addType2Definitions(Swagger swagger, MessageType messageType, String basePackage) {
        Model model = new ModelImpl();
        swagger.getDefinitions().put(getRefName(messageType, basePackage), model);

        LinkedHashMap<String, Property> properties = new LinkedHashMap<>();
        for (FieldType fieldType : messageType.getFieldTypeList()) {
            Property property = TypeFormatUtil.formatTypeSwagger2(fieldType, basePackage);
            properties.put(fieldType.getName(), property);
        }
        model.setProperties(properties);
    }

    /**
     * 生成swagger type定义
     *
     * @param swagger
     * @param messageTypes
     * @param enumTypes
     */
    private void renderDefinitions(Swagger swagger,
                                   Set<MessageType> messageTypes,
                                   Set<EnumType> enumTypes,
                                   String basePackage) {
        for (MessageType messageType : messageTypes) {
            addType2Definitions(swagger, messageType, basePackage);
        }
        for (EnumType enumType : enumTypes) {
            addEnum2Definitions(swagger, enumType, basePackage);
        }
    }

    /**
     * proto转swagger
     *
     * @param fdp
     * @param apiVersion
     * @return
     */
    public String applyTemplate(DescriptorProtos.FileDescriptorProto fdp,
                                MetaContainer metaContainer,
                                String apiVersion) throws JsonProcessingException {
        // 从pb中提取service
        ServiceContainer serviceContainer = ContainerUtil.getServiceContainer(fdp);

        Swagger swagger = new Swagger().scheme(HTTP)
                .consumes(Collections.singletonList("application/json"))
                .produces(Collections.singletonList("application/json"))
                .paths(new LinkedHashMap<>())
                .info(new Info().version(apiVersion).title(fdp.getName()));
        swagger.setDefinitions(new LinkedHashMap<>());

        String basePackage = fdp.getPackage();
        // render path
        Set<MessageType> messageTypes = renderServices(swagger, serviceContainer, metaContainer, basePackage);
        // render type and enum
        TreeSet<EnumType> nestedEnumTypes = new TreeSet<>();
        TreeSet<MessageType> nestedMessageTypes = new TreeSet<>();

        collectNestType(metaContainer, messageTypes, nestedEnumTypes, nestedMessageTypes, basePackage);

        renderDefinitions(swagger, nestedMessageTypes, nestedEnumTypes, basePackage);

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(swagger);
    }

    private void collectNestType(MetaContainer metaContainer, Set<MessageType> messageTypes, Set<EnumType> nestEnumTypes, Set<MessageType> nestedMessageTypes, String basePackage) {
        List<MessageType> messageTypeList = new ArrayList<>(messageTypes);
        ListIterator<MessageType> listIterator = messageTypeList.listIterator();
        for (int i = 0; i < messageTypeList.size(); i++) {
            MessageType next = messageTypeList.get(i);
            for (FieldType fieldType : next.getFieldTypeList()) {
                if (StringUtils.isNotBlank(fieldType.getTypeName()) && !CommonUtils.isProtoBufType(fieldType.getTypeName())) {
                    MessageType nestedMessageType = metaContainer.findMessageTypeByFQPN(fieldType.getFQPN(), basePackage);
                    if (!messageTypeList.contains(nestedMessageType) && Objects.nonNull(nestedMessageType)) {
                        listIterator.add(nestedMessageType);
                    }

                    EnumType nestEnumType = metaContainer.findEnumTypeByFQPN(fieldType.getFQPN(), basePackage);
                    if (!messageTypeList.contains(nestedMessageType) && Objects.nonNull(nestEnumType)) {
                        nestEnumTypes.add(nestEnumType);
                    }
                }
            }
        }
        nestedMessageTypes.addAll(messageTypeList);
    }
}