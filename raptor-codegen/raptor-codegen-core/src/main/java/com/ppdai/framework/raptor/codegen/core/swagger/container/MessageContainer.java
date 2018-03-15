package com.ppdai.framework.raptor.codegen.core.swagger.container;

import com.google.protobuf.DescriptorProtos;
import com.ppdai.framework.raptor.codegen.core.constant.ProtobufConstant;
import com.ppdai.framework.raptor.codegen.core.swagger.type.FieldType;
import com.ppdai.framework.raptor.codegen.core.swagger.type.MessageType;
import org.codehaus.plexus.util.StringUtils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by zhangyicong on 18-2-27.
 * 存放从proto文件中提取的message类型
 */
public class MessageContainer {

    private Map<String, MessageType> messageTypeMap = new LinkedHashMap<>();


    public void addMessageProto(String packageName, String className, String parent, DescriptorProtos.DescriptorProto descriptorProto) {
        MessageType messageType = new MessageType();
        messageType.setName((parent != null ? parent + ProtobufConstant.PACKAGE_SEPARATOR : "") + descriptorProto.getName());
        // TODO: 2018/3/6 packageName 为空的话会多一个点
        messageType.setFQPN(packageName + ProtobufConstant.PACKAGE_SEPARATOR + messageType.getName());
        messageType.setFQCN(StringUtils.join(new String[]{packageName, className, messageType.getName()}, ProtobufConstant.PACKAGE_SEPARATOR));
        messageType.setClassName(className);
        Map<String, FieldType> fieldTypeMap = new LinkedHashMap<>();
        messageType.setFields(fieldTypeMap);
        messageType.setPackageName(packageName);

        for (DescriptorProtos.FieldDescriptorProto ffdp : descriptorProto.getFieldList()) {
            FieldType fieldType = new FieldType();
            fieldType.setName(ffdp.getName());
            fieldType.setType(ffdp.getType());
            fieldType.setLabel(ffdp.getLabel());
            String FQPN = ffdp.getTypeName().replaceAll("^\\.", "");
            fieldType.setTypeName(FQPN.replace(packageName + ".", ""));
            fieldType.setFQPN(FQPN);
            fieldType.setMessage(messageType.getFQPN());
            fieldTypeMap.put(fieldType.getName(), fieldType);
        }

        messageTypeMap.put(messageType.getFQPN(), messageType);
    }

    public MessageType findMessageTypeByFQPN(String FQPN) {
        return messageTypeMap.get(FQPN);
    }

    public Collection<MessageType> getMessageTypeList() {
        return messageTypeMap.values();
    }
}
